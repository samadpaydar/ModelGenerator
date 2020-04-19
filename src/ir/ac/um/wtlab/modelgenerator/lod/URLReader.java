/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.lod;

import java.io.*;
import java.net.*;

/**
 *
 * @author Home
 */
class URLReader {

    public String fetchURL(URL url) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));
        StringBuffer buffer = new StringBuffer();
        String line;
        while (true) {
            line = in.readLine();
            if (line == null) {
                break;
            } else {
                buffer.append(line);
            }
        }
        in.close();
        return buffer.toString();
    }
}
