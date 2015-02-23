class StatementParser {

    def solveFunction(String s) {
        Eval.me(s)
    }

    def generateInputs(int inputs) {
        def max = 2**inputs

        def inputList = []

        max.times {
            def binStr = String.format("%0${inputs}d", Integer.valueOf(Integer.toBinaryString(it)))
            def singleLine = []
            binStr.each { obj ->
                singleLine << (obj as int)
            }
            inputList << singleLine
        }

        inputList
    }

    def generateTable(String statement) {
        statement = statement.replaceAll " ", ""

        def inputNames = statement.findAll("X\\d+") as Set
        def inputsSize = inputNames.size()

        def inputs = generateInputs(inputsSize)

        def outputs = []

        inputs.each { inp ->
            String y = new String(statement)
            inputNames.eachWithIndex { inpName, j ->
                y = y.replaceAll(inpName, "${inp[j] as boolean}")
            }

            outputs << inp.clone() + (solveFunction(y)? 1 : 0)
        }

        outputs
    }

}