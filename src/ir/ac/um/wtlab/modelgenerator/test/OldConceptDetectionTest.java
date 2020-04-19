/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.test;

import java.io.*;
import ir.ac.um.wtlab.modelgenerator.ModelGenerator;
import java.util.ArrayList;

/**
 *
 * @author Home
 */
public class OldConceptDetectionTest {

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
                ArrayList<String> behaviors = modelGenerator.getWordsTaggedAsNameWithoutSalt(line);
                
                System.out.print(line);
                for(String behavior: behaviors) {
                    System.out.print(", " + behavior);
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
