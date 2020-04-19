/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.uwe2rdf;

import java.io.*;
import java.util.*;

/**
 *
 * @author Home
 */
public class FileMerger {

    private final String directory;

    public FileMerger(String directory) {
        this.directory = directory;
    }

    public File mergeFiles() {
        try {
            Calendar now2 = GregorianCalendar.getInstance();
            String timeStr = Integer.toString(now2.get(Calendar.YEAR))
                    + "-" + Integer.toString(now2.get(Calendar.MONTH) + 1)
                    + "-" + Integer.toString(now2.get(Calendar.DAY_OF_MONTH))
                    + " " + Integer.toString(now2.get(Calendar.HOUR_OF_DAY))
                    + "_" + Integer.toString(now2.get(Calendar.MINUTE))
                    + "_" + Integer.toString(now2.get(Calendar.SECOND));
            File outputFile = new File("models." + timeStr + ".n3");
            PrintStream output = new PrintStream(outputFile);
            processDirectory(new File(directory), output);
            output.close();
            return outputFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processDirectory(File directory, PrintStream output) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                processDirectory(file, output);
            } else {
                processFile(file, output);
            }
        }
    }

    private void processFile(File file, PrintStream output) {
        try {
            if(! file.getName().toLowerCase().endsWith(".inf.n3")) {
                return;
            }
            BufferedReader input = new BufferedReader(new FileReader(file));
            while (true) {
                String line = input.readLine();
                if (line == null) {
                    break;
                }
                output.println(line);
            }
            input.close();
            System.out.println("ADDED: " + file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
