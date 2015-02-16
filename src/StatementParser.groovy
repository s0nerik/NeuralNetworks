class StatementParser {

    def methods = [
            "&": { x1, x2 ->
                x1 && x2
            },
            "|": { x1, x2 ->
                x1 || x2
            },
            "->": { x1, x2 ->
                !x1 || x2
            }
    ]

//    static def inpString = "X1 & (X2 -> X3) & X4"

    static def inpString = "X1 & X2 | X3"

    String solveFunction(String s) {
        def methodKeys = methods.keySet()

        def anyMethod = "[[${methodKeys.join(']|[')}]]"

        def method = s.find {
            it in methodKeys
        } as String

        def args = s.findAll("\\d${anyMethod}\\d")
        println "Args: ${args}"
        args[0]
//        methods[method](args[0].toBoolean(), args[1].toBoolean()) ? "1" : "0"
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

    def parse(String s) {
        s = s.replaceAll " ", ""

        println s

        def inputNames = s.findAll("X\\d+") as Set
        def inputsSize = inputNames.size()

        println inputsSize

        def inputs = generateInputs(inputsSize)
        println inputs
        inputs.each { inp ->
            String y = new String(s)
            inputNames.eachWithIndex { inpName, j ->
                y = y.replaceAll(inpName, "${inp[j]}")
            }
            println y
            println solveFunction(y)
        }

        def arrSize = inputsSize + 1

//        methods.each {
//            println it("0", "0")
//        }

    }

    static void main(String... args) {
        def parser = new StatementParser()
        parser.parse(inpString)

    }

}