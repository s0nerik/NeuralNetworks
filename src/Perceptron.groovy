import groovy.json.JsonSlurper

class Perceptron {

//    def patterns = new JsonSlurper().parse(new File("input.json")).patterns
    def patterns = new StatementParser().generateTable(new File('input.txt').getText())

    def studySpeed = 0.115
    def enters = (0..<(patterns[0].size() - 1)).collect { 0.0 }
    def weights = enters.collect { Math.random() * 0.2 + 0.1 }

    def MAX_EPOCHS = 100000

    def calculateExit(List<Integer> enters) {
        def exit = 0.0
        enters.eachWithIndex { enter, i ->
            exit += enter * weights[i]
        }
        return (exit >= 0.5)? 1 : 0
    }

    def study() {
        def epochs = 0
        while(true) {
            def lastWeights = weights.clone()
            def epochError = studyEpoch()
            epochs++
            if (lastWeights.equals(weights) || epochError == 0 || epochs > MAX_EPOCHS) break
        }
        epochs
    }

    def studyEpoch() {
        def globalError = 0.0
        patterns.each { List<Integer> pattern ->
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
        def epochsPassed = study()

        println "Epochs passed: ${epochsPassed}"
        println "Resuts:"
        println String.format("%10s %10s %10s", 'Expected', 'Got', 'Verdict')

        def expected = patterns.collect {it[-1]}
        patterns.each {
            def e = it[-1]
            def g = calculateExit(it[0..-2])
            def v = g == e
            println String.format("%10d %10d %10s", it[-1], calculateExit(it[0..-2]), v? "OK" : "NOT OK")
        }
    }

    static void main(String... args) {
        new Perceptron().test()
    }

}
