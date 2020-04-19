/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.test;

import java.io.*;
import ir.ac.um.wtlab.modelgenerator.ModelGenerator;
import ir.ac.um.wtlab.modelgenerator.util.ExcelGenerator;
import ir.ac.um.wtlab.modelgenerator.util.Utils;
import java.util.ArrayList;
import java.net.URL;

/**
 *
 * @author Home
 */
public class ConceptDetectionTest {

    private final static String PATH = "G:\\Projects & Courses\\Papers\\A Semantic Web Enabled Approach to Reuse Functional Requirements Models in Web Engineering\\Submit to Automated Software Engineering\\Revise\\Evaluation";
    private ExcelGenerator excelGenerator;

    public void run(ModelGenerator modelGenerator) {
        String[] excelColumnNames = {"Use Case Name", "TruePositive", "FalsePositive", "FalseNegative",
            "Concept1", "Concept2", "Concept3", "Concept4",
            "Concept5", "Concept6", "Concept7", "Concept8",};
        excelGenerator = new ExcelGenerator(new File(PATH, "ConceptDetectionStopWordsRemoved.xls"), excelColumnNames);

        String[] salts = {
            null,
            "I want to ",
            "I want to do ",
            "Please ",
            "I ",
            "I do ",
            "Do you ",
            "Are you ",
            "Let’s "
        };

        for (String salt : salts) {
            System.out.println("***************************************");
            System.out.println("TEST with SALT: " + salt);
            test(modelGenerator, salt);
            System.out.println("***************************************");
        }
        excelGenerator.close();
    }

    private void test(ModelGenerator modelGenerator, String salt) {
        try {
            if (salt != null && !salt.equalsIgnoreCase("0")) {
                modelGenerator.setSalt(salt);
            }
            excelGenerator.goToNextSheet("Salt = " + salt);

            BufferedReader usecaseNamesFile = new BufferedReader(new FileReader(new File(PATH, "usecaseNames.txt")));
            BufferedReader goldenStandardFile = new BufferedReader(new FileReader(new File(PATH, "ConceptGoldenStandard.txt")));
            String line = null;
            String goldenStandardLine = null;
            while (true) {
                line = usecaseNamesFile.readLine();
                goldenStandardLine = goldenStandardFile.readLine();
                if (line == null) {
                    usecaseNamesFile.close();
                    goldenStandardFile.close();
                    break;
                }
                //spell checker


                ArrayList<String> concepts = null;
                if (salt == null) {
                    concepts = modelGenerator.getWordsTaggedAsNameWithoutSalt(line);
                } else {
                    modelGenerator.setSalt(salt);
                    concepts = modelGenerator.getWordsTaggedAsNameWithSalt(line);
                }

                String[] goldenStandardConcepts = goldenStandardLine.split(",");

                concepts = Utils.removeStopWords(concepts);
                goldenStandardConcepts = Utils.removeStopWords(goldenStandardConcepts);

                int truePositive = 0;
                int falsePositive = 0;
                int falseNegative = 0;
                for (String goldenStandardConcept : goldenStandardConcepts) {
                    goldenStandardConcept = goldenStandardConcept.trim();
                    if (goldenStandardConcept.length() == 0) {
                        continue;
                    }
                    boolean found = false;
                    for (String behavior : concepts) {
                        if (goldenStandardConcept.equalsIgnoreCase(behavior.trim())) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        truePositive++;
                    } else {
                        falseNegative++;
                    }
                }
                for (String concept : concepts) {
                    concept = concept.trim();
                    boolean found = false;
                    for (String goldenStandardConcept : goldenStandardConcepts) {
                        if (goldenStandardConcept.trim().equalsIgnoreCase(concept.trim())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        falsePositive++;
                    }
                }

                excelGenerator.append("Use Case Name", line);
                excelGenerator.append("TruePositive", Integer.toString(truePositive));
                excelGenerator.append("FalsePositive", Integer.toString(falsePositive));
                excelGenerator.append("FalseNegative", Integer.toString(falseNegative));
                for (int i = 0; i < concepts.size(); i++) {
                    String behavior = concepts.get(i);
                    excelGenerator.append("Concept" + (i + 1), behavior);
                }
                excelGenerator.goToNextRow();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
