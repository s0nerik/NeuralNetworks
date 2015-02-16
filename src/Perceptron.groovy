import groovy.json.JsonSlurper

class Perceptron {

    def patterns = new JsonSlurper().parse(new File("input.json")).patterns

//    def patterns = [
////            // X1 & (X2->X3) & X4
////            [0, 0, 0, 0,  0],
////            [0, 0, 0, 1,  0],
////            [0, 0, 1, 0,  0],
////            [0, 0, 1, 1,  0],
////            [0, 1, 0, 0,  0],
////            [0, 1, 0, 1,  0],
////            [0, 1, 1, 0,  0],
////            [0, 1, 1, 1,  0],
////            [1, 0, 0, 0,  0],
////            [1, 0, 0, 1,  1],
////            [1, 0, 1, 0,  0],
////            [1, 0, 1, 1,  1],
////            [1, 1, 0, 0,  0],
////            [1, 1, 0, 1,  0],
////            [1, 1, 1, 0,  0],
////            [1, 1, 1, 1,  1]
//
//            // AND
////            [0, 0,  0],
////            [0, 1,  0],
////            [1, 0,  0],
////            [1, 1,  1]
//
//            // OR
////            [0, 0,  0],
////            [0, 1,  1],
////            [1, 0,  1],
////            [1, 1,  1]
//
////            // IMPLIFY
////            [0, 0,  1],
////            [0, 1,  1],
////            [1, 0,  0],
////            [1, 1,  1]
//
////            // XOR
////            [0, 0,  0],
////            [0, 1,  1],
////            [1, 0,  1],
////            [1, 1,  0]
//
////            // X1 && X2 || X3
////            [0, 0, 0,  0],
////            [0, 0, 1,  1],
////            [0, 1, 0,  0],
////            [0, 1, 1,  1],
////            [1, 0, 0,  0],
////            [1, 0, 1,  1],
////            [1, 1, 0,  1],
////            [1, 1, 1,  1]
//    ]

    def studySpeed = 0.15
    def enters = (0..<(patterns[0].size() - 1)).collect { 0.0 }
    def weights = enters.collect { Math.random() * 0.2 + 0.1 }

    def calculateExit(List enters) {
        def exit = 0.0
        enters.eachWithIndex { enter, i ->
            exit += enter * weights[i]
        }
        return (exit > 0.5)? 1.0 : 0.0
    }

    def study() {
        def epochResult = studyEpoch();
        if (epochResult > 0) study();
    }

    def studyEpoch() {
        def globalError = 0.0
        patterns.each { pattern ->
            enters = pattern[0..-2] // Don't include last element as it contains answer
            def exit = calculateExit(enters)
            def error = pattern[-1] - exit
            globalError += Math.abs(error)
            weights.eachWithIndex { weight, i ->
                weights[i] += studySpeed * error * enters[i]
            }
        }

        globalError
    }

    def test() {
        study()

        patterns.each { pattern ->
            println calculateExit(pattern[0..-2])
        }
    }

    static void main(String... args) {
        new Perceptron().test()
    }

}
