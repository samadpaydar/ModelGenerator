/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.test;

import java.io.*;
import ir.ac.um.wtlab.modelgenerator.ModelGenerator;
import ir.ac.um.wtlab.modelgenerator.util.ExcelGenerator;
import java.util.ArrayList;
import java.net.URL;

/**
 *
 * @author Home
 */
public class BehaviorDetectionTest {

    private final static String PATH = "G:\\Projects & Courses\\Papers\\A Semantic Web Enabled Approach to Reuse Functional Requirements Models in Web Engineering\\Submit to Automated Software Engineering\\Revise\\Evaluation";
    private ExcelGenerator excelGenerator;

    public void run(ModelGenerator modelGenerator) {
        String[] excelColumnNames = {"Use Case Name", "TruePositive", "FalsePositive", "FalseNegative",
            "Behavior1", "Behavior2", "Behavior3", "Behavior4",
            "Behavior5", "Behavior6", "Behavior7", "Behavior8",};
        excelGenerator = new ExcelGenerator(new File(PATH, "BehaviorDetectionEvaluationResults.xls"), excelColumnNames);

        String[] salts = {
            "0",
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
            BufferedReader goldenStandardFile = new BufferedReader(new FileReader(new File(PATH, "BehaviorGoldenStandard.txt")));
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


                ArrayList<String> behaviors = null;
                if (salt == null) {
                    behaviors = modelGenerator.getUsecaseAllBehavioursWithoutSalt(line);
                } else if (salt.equalsIgnoreCase("0")) {
                    behaviors = modelGenerator.getUsecaseFirstWordAsBehavior(line);
                } else {
                    behaviors = modelGenerator.getUsecaseAllBehavioursWithSalt(line);
                }
                String[] goldenStandardBehaviors = goldenStandardLine.split(",");
                int truePositive = 0;
                int falsePositive = 0;
                int falseNegative = 0;
                for (String goldenStandardBehavior : goldenStandardBehaviors) {
                    goldenStandardBehavior = goldenStandardBehavior.trim();
                    if (goldenStandardBehavior.length() == 0) {
                        continue;
                    }

                    boolean found = false;
                    for (String behavior : behaviors) {
                        if (goldenStandardBehavior.equalsIgnoreCase(behavior.trim())) {
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
                for (String behavior : behaviors) {
                    behavior = behavior.trim();
                    boolean found = false;
                    for (String goldenStandardBehavior : goldenStandardBehaviors) {
                        if (goldenStandardBehavior.trim().equalsIgnoreCase(behavior.trim())) {
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
                for (int i = 0; i < behaviors.size(); i++) {
                    String behavior = behaviors.get(i);
                    excelGenerator.append("Behavior" + (i + 1), behavior);
                }
                excelGenerator.goToNextRow();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
