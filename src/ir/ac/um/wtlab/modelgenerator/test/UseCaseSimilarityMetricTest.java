/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.test;

import ir.ac.um.wtlab.modelgenerator.ModelGenerator;
import ir.ac.um.wtlab.modelgenerator.geneticalgorithm.TestCase;
import ir.ac.um.wtlab.modelgenerator.similarity.UsecaseSimilarityCalculator;
import ir.ac.um.wtlab.modelgenerator.util.ExcelGenerator;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

/**
 *
 * @author Home
 */
public class UseCaseSimilarityMetricTest {

    private TestCase[] testCases;
    private ModelGenerator modelGenerator;
    private ExcelGenerator excelGenerator;
    private final static String PATH = "G:\\Projects & Courses\\Papers\\A Semantic Web Enabled Approach to Reuse Functional Requirements Models in Web Engineering\\Submit to Automated Software Engineering\\Revise\\Evaluation";

    public UseCaseSimilarityMetricTest(ModelGenerator modelGenerator) {
        this.modelGenerator = modelGenerator;
        String[] columnNames = {"TestCase", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "precisionAtRecallLevel_0",
        "precisionAtRecallLevel_10",
        "precisionAtRecallLevel_20",
        "precisionAtRecallLevel_30",
        "precisionAtRecallLevel_40",
        "precisionAtRecallLevel_50",
        "precisionAtRecallLevel_60",
        "precisionAtRecallLevel_70",
        "precisionAtRecallLevel_80",
        "precisionAtRecallLevel_90",
        "precisionAtRecallLevel_100"
        };
        excelGenerator = new ExcelGenerator(new File(PATH, "usecaseTemp.xls"), columnNames);
    }

    private void initTestCases() {
        File xmlFile = new File(PATH, "usecaseSimilarityTestCases.xml");
        loadTestCases(xmlFile);
    }

    private void loadTestCases(File xmlFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("TestCase");
            int testCaseCount = nodeList.getLength();
            testCases = new TestCase[testCaseCount];
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node testCaseNode = nodeList.item(i);
                Element testCaseElement = (Element) testCaseNode;
                NodeList temp = testCaseElement.getElementsByTagName("inputSC");
                String inputSC = temp.item(0).getTextContent();
                temp = testCaseElement.getElementsByTagName("inputUC");
                String inputUC = temp.item(0).getTextContent();
                temp = testCaseElement.getElementsByTagName("expectedResult");
                String[][] expectedResults = new String[temp.getLength()][2];
                for (int j = 0; j < temp.getLength(); j++) {
                    Node tempNode = temp.item(j);
                    Element tempElement = (Element) tempNode;
                    NodeList tempNodeList2 = tempElement.getElementsByTagName("SC");
                    String sc = tempNodeList2.item(0).getTextContent();
                    tempNodeList2 = tempElement.getElementsByTagName("UC");
                    String uc = tempNodeList2.item(0).getTextContent();
                    expectedResults[j][0] = sc;
                    expectedResults[j][1] = uc;
                }
                testCases[i] = new TestCase(inputSC, inputUC, expectedResults);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void evaluateTestCases() {

        int i = 0;
        for (TestCase testCase : testCases) {
            i++;
            String inputSCName = testCase.getInputSCName();
            String inputUCName = testCase.getInputUCName();
            System.out.println("Test Case #" + i +" / " + testCases.length + "  SC: " + inputSCName + " , UC: " + inputUCName);
            Object[][] result = modelGenerator.calculateUsecaseSimilarities(inputSCName, inputUCName);
            result = removeDuplicates(result);
            int j = 0;
            for (Object[] resultItem : result) {
                j++;
                String str = j + " SC: " + resultItem[0] + " UC: " + resultItem[1] + " SIM: " + resultItem[2];
                System.out.println("\t" + str);
            }
            excelGenerator.append("TestCase", "SC: " + inputSCName + " UC: " + inputUCName);
            for (int k = 1; k <= 10; k++) {
                double precisionAtK = computePrecisionAtRankK(result, testCase.getExpectedResults(), k);
                long percent = Math.round(precisionAtK * 100);
                excelGenerator.append(Integer.toString(k), Long.toString(percent));
            }
            for (int r = 0; r <= 10; r++) {
                int level = r * 10;
                double precisoinAtLevel = computePrecisionAtRecallLevel(result, testCase.getExpectedResults(), level);
                long percent = Math.round(precisoinAtLevel * 100);
                excelGenerator.append("precisionAtRecallLevel_" + Integer.toString(level), Long.toString(percent));
            }
            excelGenerator.goToNextRow();
        }
    }

    private Object[][] removeDuplicates(Object[][] items) {
        boolean[] duplicate = new boolean[items.length];
        int duplicateCount = 0;
        for (int i = 1; i < items.length; i++) {
            if (items[i][0] == items[i - 1][0] && items[i][1] == items[i - 1][1]) {
                duplicate[i] = true;
                duplicateCount++;
            }
        }
        Object[][] result = new Object[items.length - duplicateCount][items[0].length];
        for (int i = 0, j = 0; i < items.length; i++) {
            if (!duplicate[i]) {
                for (int k = 0; k < items[i].length; k++) {
                    result[j][k] = items[i][k];
                }
                j++;
            }
        }
        return result;
    }

    private double computePrecisionAtRecallLevel(Object[][] results, String[][] expectedResults, int recallThreshold) {
        int truePositive = 0;
        double precisionAtLevel = 0.0;
        double recallAtLevel = 0.0;

        for (int i = 0; i < results.length; i++) {
            String scName = results[i][0].toString();
            String ucName = results[i][1].toString();
            boolean isTruePositive = false;
            for (int j = 0; j < expectedResults.length; j++) {
                String scName2 = expectedResults[j][0];
                String ucName2 = expectedResults[j][1];
                if (scName.equalsIgnoreCase(scName2) && ucName.equalsIgnoreCase(ucName2)) {
                    isTruePositive = true;
                    break;
                }
            }
            if (isTruePositive) {
                truePositive++;
                recallAtLevel = (1.0 * truePositive) / (expectedResults.length);
                int recallPercent = (int) (recallAtLevel * 100);
                if (recallPercent >= recallThreshold) {
                    precisionAtLevel = (1.0 * truePositive) / (i + 1);
                    break;
                }
            }
        }
        return precisionAtLevel;
    }

    private double computePrecisionAtRankK(Object[][] results, String[][] expectedResults, int k) {
        double matchedCount = 0;
        double precisionAtK = 0.0;

        for (int i = 0; i < k; i++) {
            String scName = results[i][0].toString();
            String ucName = results[i][1].toString();
            for (int j = 0; j < expectedResults.length; j++) {
                String scName2 = expectedResults[j][0];
                String ucName2 = expectedResults[j][1];
                if (scName.equalsIgnoreCase(scName2) && ucName.equalsIgnoreCase(ucName2)) {
                    matchedCount++;
                }
            }
        }
        precisionAtK = matchedCount / (1.0D * k);
        return precisionAtK;
    }

    private double computeFitness(Object[][] results, String[][] expectedResults) {
        double fitness = 0;
        double precisionSum = 0.0;

        final int LEVEL = expectedResults.length;
        for (int level = 1; level <= LEVEL; level++) {
            int count = 0;
            int i;
            loop:
            for (i = 0; i < results.length; i++) {
                String scName = (String) results[i][0];
                String ucName = (String) results[i][1];
                for (int j = 0; j < expectedResults.length; j++) {
                    String scName2 = expectedResults[j][0];
                    String ucName2 = expectedResults[j][1];
                    if (scName.equalsIgnoreCase(scName2) && ucName.equalsIgnoreCase(ucName2)) {
                        count++;
                        if (count == level) {
                            break loop;
                        }
                    }
                }
            }
            double precision = ((double) level) / (i + 1);
            precisionSum += precision;
        }
        fitness = precisionSum / LEVEL;
        return fitness;
    }

    public void run() {
        initTestCases();
        double[][] weightsSet = {
            {0.8, 0.2, 0.2, 0.9, 0.2},
            {0.8, 0.2, 0.3, 0.9, 0.2},
            {0.8, 0.2, 0.3, 1.0, 0.2},
            {0.8, 0.2, 0.3, 1.0, 0.1},
            {0.8, 0.2, 0.2, 1.0, 0.3},
            {0.8, 0.2, 0.2, 1.0, 0.2},
            {1.0, 1.0, 1.0, 1.0, 1.0}
        };

        int i = 0;
        for (double[] weights : weightsSet) {
            i++;
            excelGenerator.goToNextSheet("Sheet " + i);
            UsecaseSimilarityCalculator.setFiveWeights(weights);
            System.out.println("WEIGHTS " + i + " / " + weightsSet.length);
            System.out.println(Arrays.toString(weights));
            evaluateTestCases();
        }

        excelGenerator.close();
    }
}
