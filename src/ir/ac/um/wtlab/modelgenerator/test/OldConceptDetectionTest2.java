/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.test;

import java.io.*;
import ir.ac.um.wtlab.modelgenerator.ModelGenerator;
import java.util.ArrayList;

/**
 * The goal of this test is to identify what percent of the use cases have more than 1 concept
 * @author Home
 */
public class OldConceptDetectionTest2 {

    public void run(ModelGenerator modelGenerator) {
        try {
            BufferedReader file = new BufferedReader(new FileReader("usecase names.txt"));
            String line = null;
            while (true) {
                line = file.readLine();
                if (line == null) {
                    file.close();
                    break;
                }
                ArrayList<String> concepts = modelGenerator.getWordsTaggedAsNameWithoutSalt(line);
                
                System.out.print(line);
                for(String behavior: concepts) {
                    System.out.print(", " + behavior);
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
