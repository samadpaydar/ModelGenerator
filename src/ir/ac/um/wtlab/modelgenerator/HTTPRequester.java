/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ir.ac.um.wtlab.modelgenerator;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 *
 * @author UM
 */

public class HTTPRequester {
    private HTTPRequester() {
    }
    
    public static String fetch(URL url) {
        String result = null;
        try {
            URLConnection connection = url.openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter(Constants.END_OF_INPUT);
            result = scanner.next();
            connection = null;
            scanner = null;
        } catch (Exception ex) {
            System.out.println("EXCEPTION IN FETCHING URL: " + url);
            ex.printStackTrace();
        }
        return result;
    }
}
