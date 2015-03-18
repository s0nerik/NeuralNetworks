import groovy.beans.Bindable
import groovy.swing.SwingBuilder
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset

import javax.swing.*
import javax.swing.filechooser.FileFilter
import javax.swing.table.TableModel
import java.awt.*
import java.awt.event.ActionEvent

class UI {

    class InputsModel {
        @Bindable
        String learningRate = "0.115"
        @Bindable
        String maxEpochs = "10000"
        @Bindable
        String hiddenLayers = "2"
    }

    def inputsModel = new InputsModel()

    JPanel chartPanelContainer
    TableModel inputTableModel
    JTable inputTable
    def inputTableData

    def chart

    private void refreshChart() {
        chartPanelContainer?.removeAll();
        chartPanelContainer?.revalidate(); // This removes the old chart
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanelContainer?.setPreferredSize(new Dimension(1024, 480));
        chartPanelContainer?.add(chartPanel);
        chartPanelContainer?.repaint(); // This method makes the new chart appear
    }

    def replaceChart(def dataset) {
        def labels = ["Error chart", "Epoch", "Count"]
        def options = [true, false, false]
        chart = ChartFactory.createLineChart(*labels, dataset, PlotOrientation.VERTICAL, *options)
        refreshChart()
    }

    def replaceInput() {
//        inputTableData = Collections.nCopies(500, 11..17)
//        inputTable.clear()
        inputTableData.removeAll()

//        inputTableModel.tableChanged
//        fireTableDataChanged
//
//        inputTableModel
//
//        inputTableData.tableModel(list: model) {
//            closureColumn(header: 'x1', read: { row -> return row[0] })
//            closureColumn(header: 'x2', read: { row -> return row[1] })
//            closureColumn(header: 'x3', read: { row -> return row[2] })
//            closureColumn(header: 'x4', read: { row -> return row[3] })
//            closureColumn(header: 'x5', read: { row -> return row[4] })
//            closureColumn(header: 'x6', read: { row -> return row[5] })
//            closureColumn(header: 'y', read: { row -> return row[6] })
//        }
    }

    def init() {
        def dataset = new DefaultCategoryDataset()
        200.times {
            dataset.addValue(it, "Error", it.toString())
        }

        replaceChart(dataset)
        def swing = new SwingBuilder()
        swing.frame(
                title: 'Neural Network',
                pack: true,
                show: true,
                preferredSize: new Dimension(1280, 768),
                defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE) {

            panel {
                tableLayout {
                    tr {
                        td(colspan: 2, rowfill: 2) {
                            panel(id: 'controls', preferredSize: new Dimension(1280, 48)) {

                                label(text: "Learning rate:")
                                textField(
                                        preferredSize: new Dimension(60, 24),
                                        text: bind('learningRate', source: inputsModel, mutual: true),
                                        action: action(name: 'changeLearningRate', closure: {
                                            ActionEvent e -> println "Learning rate: ${e.actionCommand}"
                                        })
                                )

                                label(text: "Max epochs:")
                                textField(
                                        preferredSize: new Dimension(60, 24),
                                        text: bind('maxEpochs', source: inputsModel, mutual: true),
                                        action: action(name: 'changeLearningRate', closure: {
                                            ActionEvent e -> println "Learning rate: ${e.actionCommand}"
                                        })
                                )

                                label(text: "Hidden layers:")
                                textField(
                                        preferredSize: new Dimension(60, 24),
                                        text: bind('hiddenLayers', source: inputsModel, mutual: true),
                                        action: action(name: 'changeLearningRate', closure: {
                                            ActionEvent e -> println "Learning rate: ${e.actionCommand}"
                                        })
                                )

                                button(preferredSize: new Dimension(128, 24), action: action(name: 'Load input', closure: {
                                    def openCsvDialog = fileChooser(
                                            dialogTitle: "Choose an excel file",
                                            fileSelectionMode: JFileChooser.FILES_ONLY,
                                            fileFilter: [getDescription: {-> "*.csv"}, accept:{file-> file ==~ /.*?\.csv/ || file.isDirectory() }] as FileFilter
                                    )

                                    switch(openCsvDialog.showOpenDialog()) {
                                        case JFileChooser.APPROVE_OPTION:
                                            File file = openCsvDialog.getSelectedFile();

                                            def path = openCsvDialog.getCurrentDirectory().getAbsolutePath();
                                            println "path="+path+"\nfile name="+file.toString();
                                            break;
                                        case JFileChooser.CANCEL_OPTION:
                                        case JFileChooser.ERROR_OPTION:
                                            break;
                                    }

                                    replaceInput()
                                }))

                                button(preferredSize: new Dimension(64, 24), action: action(name: 'Run', closure: {
                                    def statement = new File('input.txt').readLines().find { !it.startsWith("#") }
                                    def patterns = new StatementParser().generateTable(statement)

                                    Perceptron perceptron = new Perceptron(patterns,
                                            inputsModel.learningRate.toDouble(),
                                            inputsModel.maxEpochs.toInteger(),
                                            inputsModel.hiddenLayers.toInteger()
                                    )

                                    perceptron.test()

                                    def data = new DefaultCategoryDataset()
                                    200.times {
                                        data.addValue(1, "Error", it.toString())
                                    }

                                    replaceChart(data)
                                }))

                            }
                        }
                    }

                    tr {
                        td {
                            chartPanelContainer = panel(id: 'canvas', preferredSize: new Dimension(680, 480)) {
                                widget(new ChartPanel(chart))
                            }
                        }
                        td {
                            panel(preferredSize: new Dimension(500, 480)) {

                                boxLayout(axis: BoxLayout.Y_AXIS)
                                label text: "Input values"
                                scrollPane() {
                                    inputTable = table() {
                                        inputTableData = Collections.nCopies(500, 1..7)

                                        inputTableModel = tableModel(list: inputTableData, tableChanged: {
                                            println "Table changed"
                                        }) {
                                            closureColumn(header: 'x1', read: { row -> return row[0] })
                                            closureColumn(header: 'x2', read: { row -> return row[1] })
                                            closureColumn(header: 'x3', read: { row -> return row[2] })
                                            closureColumn(header: 'x4', read: { row -> return row[3] })
                                            closureColumn(header: 'x5', read: { row -> return row[4] })
                                            closureColumn(header: 'x6', read: { row -> return row[5] })
                                            closureColumn(header: 'y', read: { row -> return row[6] })
                                        }
                                    }
                                }

                                label text: "Output values"
                                scrollPane() {
                                    table() {
                                        def model = Collections.nCopies(5, 1..7)

                                        tableModel(list: model) {
                                            closureColumn(header: 'x1', read: { row -> return row[0] })
                                            closureColumn(header: 'x2', read: { row -> return row[1] })
                                            closureColumn(header: 'x3', read: { row -> return row[2] })
                                            closureColumn(header: 'x4', read: { row -> return row[3] })
                                            closureColumn(header: 'x5', read: { row -> return row[4] })
                                            closureColumn(header: 'x6', read: { row -> return row[5] })
                                            closureColumn(header: 'y', read: { row -> return row[6] })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    static void main(String... args) {
        new UI().init()
    }

}
