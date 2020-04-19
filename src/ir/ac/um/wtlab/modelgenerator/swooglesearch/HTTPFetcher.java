/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.swooglesearch;

import java.io.*;
import java.net.*;
import java.util.Scanner;


/**
 *
 * @author UM
 */
public class HTTPFetcher {

    private static final String END_OF_INPUT = "\\Z";
    
    public void fetch(URL url, String resultFileName) {
        String result = null;
        URLConnection connection = null;
        PrintWriter resultFile = null;
        try {
            connection = url.openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter(END_OF_INPUT);
            result = scanner.next();
            resultFile = new PrintWriter(new BufferedWriter(new FileWriter(resultFileName)));
            resultFile.print(result);
            resultFile.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

