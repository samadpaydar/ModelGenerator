/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.stemmer;

import edu.mit.jwi.morph.*;
import edu.mit.jwi.*;
import java.net.*;
import java.util.List;

/**
 *
 * @author Home
 */
public class WNStemmer {

    private static WordnetStemmer stemmer;

    private WNStemmer() {
    }

    private static void initStemmer() {
        try {
            final String path = ".\\wordnet\\3.0\\dict";
            URL url = new URL("file", null, path);
            IDictionary dict = new Dictionary(url);
            dict.open();
            stemmer = new WordnetStemmer(dict);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> stem(String word) {
        if (stemmer == null) {
            initStemmer();
        }
        List<String> result = stemmer.findStems(word);
        if(result.size() == 0) {
        //System.out.println("******* WN STEMMER could not stem " + word);
        }
        /*PorterStemmer porter = new PorterStemmer();
        String result2 = porter.stem(word);
        System.out.println("PORTER STEMMER: " + word + " : " + result2);
        */
        return result;
    }
    
    public static void main(String[] args) {
        WNStemmer stemmer = new WNStemmer();
        System.out.println(stemmer.stem("keyboard"));
    }
}
