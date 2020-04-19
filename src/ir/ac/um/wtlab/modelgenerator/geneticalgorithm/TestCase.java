/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.geneticalgorithm;

/**
 *
 * @author Home
 */
public class TestCase {

    private String inputSCName;
    private String inputUCName;
    private String[][] expectedResults;

    public TestCase(String inputSCName, String inputUCName, String[][] expectedResults) {
        setInputSCName(inputSCName);
        setInputUCName(inputUCName);
        setExpectedResults(expectedResults);
    }

    /**
     * @return the inputSCName
     */
    public String getInputSCName() {
        return inputSCName;
    }

    /**
     * @param inputSCName the inputSCName to set
     */
    public void setInputSCName(String inputSCName) {
        this.inputSCName = inputSCName;
    }

    /**
     * @return the inputUCName
     */
    public String getInputUCName() {
        return inputUCName;
    }

    /**
     * @param inputUCName the inputUCName to set
     */
    public void setInputUCName(String inputUCName) {
        this.inputUCName = inputUCName;
    }

    /**
     * @return the expectedResults
     */
    public String[][] getExpectedResults() {
        return expectedResults;
    }

    /**
     * @param expectedResults the expectedResults to set
     */
    public void setExpectedResults(String[][] expectedResults) {
        this.expectedResults = expectedResults;
    }

    public String toString() {
        String result = "inputSC: " + inputSCName + " inputUC: " + inputUCName;
        result += "\nexpectedResults: ";
        for (String[] expectedResult : expectedResults) {
            result += "[" + expectedResult[0] + ", " + expectedResult[1] + "]";
        }
        result += "\n";
        return result;
    }
}
