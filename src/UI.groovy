import groovy.beans.Bindable
import groovy.swing.SwingBuilder
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent

class UI {

    class InputsModel {
        @Bindable String learningRate = "0.115"
        @Bindable String maxEpochs = "10000"
        @Bindable String hiddenLayers = "2"
    }

    def inputsModel = new InputsModel()

    JPanel chartPanelContainer

    def chart

    private void refreshChart() {
        chartPanelContainer?.removeAll();
        chartPanelContainer?.revalidate(); // This removes the old chart
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanelContainer?.setPreferredSize(new Dimension(1024, 720));
        chartPanelContainer?.add(chartPanel);
        chartPanelContainer?.repaint(); // This method makes the new chart appear
    }

    def replaceChart(def dataset) {
        def labels = ["Error chart", "Epoch", "Count"]
        def options = [true, false, false]
        chart = ChartFactory.createLineChart(*labels, dataset, PlotOrientation.VERTICAL, *options)
        refreshChart()
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
                preferredSize: new Dimension(1024, 768),
                defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE) {

            panel {
                tableLayout {
                    tr {
                        td {
                            panel(id: 'controls', preferredSize: new Dimension(1024, 48)) {

                                label(text:"Learning rate:")
                                textField(
                                        preferredSize: new Dimension(200, 24),
                                        text: bind('learningRate', source: inputsModel, mutual: true),
                                        action: action(name: 'changeLearningRate', closure: {
                                            ActionEvent e -> println "Learning rate: ${e.actionCommand}"
                                        })
                                )

                                label(text:"Max epochs:")
                                textField(
                                        preferredSize: new Dimension(200, 24),
                                        text: bind('maxEpochs', source: inputsModel, mutual: true),
                                        action: action(name: 'changeLearningRate', closure: {
                                            ActionEvent e -> println "Learning rate: ${e.actionCommand}"
                                        })
                                )

                                label(text:"Hidden layers:")
                                textField(
                                        preferredSize: new Dimension(200, 24),
                                        text: bind('hiddenLayers', source: inputsModel, mutual: true),
                                        action: action(name: 'changeLearningRate', closure: {
                                            ActionEvent e -> println "Learning rate: ${e.actionCommand}"
                                        })
                                )

                                button(preferredSize: new Dimension(64, 24), action: action(name: 'Run', closure: {
                                    def statement = new File('input.txt').readLines().find {!it.startsWith("#")}
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
//                                    println "basic"
                                }))
                            }
                        }
                    }

                    tr {
                        td {
                            chartPanelContainer = panel(id: 'canvas', preferredSize: new Dimension(1024, 720)) {
                                widget(new ChartPanel(chart))
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
