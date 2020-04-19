/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.test;

import java.io.*;
import java.net.*;
import java.util.TimerTask;
import java.util.Timer;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Home
 */
public class SwoogleSearch {

    public static void main(String[] args) {
        /*        try {
        String[] keywords = {"Person", "Movie", "Article", "Ticket", "Patient", "Date"};
        long sum = 0L;
        for (int i = 0; i < 10; i++) {
        for (String keyword : keywords) {
        long start = System.currentTimeMillis();
        String query = "http://swoogle.umbc.edu/index.php?option=com_frontpage&service=search&queryType=search_swd_ontology&searchString=" + keyword + "&searchStart=1";
        URL url = new URL(query);
        File file = new File("c:\\swoogle.txt");
        FileUtils.copyURLToFile(url, file);
        long end = System.currentTimeMillis();
        long time = end - start;
        sum += time;
        System.out.println("Time: " + time + " ms");
        }
        }
        double average = sum / 10 / keywords.length;
        System.out.println("AVG: average" + average);
        } catch (Exception e) {
        e.printStackTrace();
        }*/
        try {
            Timer timer = new Timer();
            String[] urls = {
//                "http://swoogle.umbc.edu/index.php?option=com_swoogle_service&service=cache&view=raw&url=http%3A%2F%2Fxmlns.com%2Ffoaf%2F0.1%2Findex.rdf",
//                "http://swoogle.umbc.edu/index.php?option=com_swoogle_service&service=cache&view=raw&url=http%3A%2F%2Frdfs.org%2Fsioc%2Fns",
//                "http://swoogle.umbc.edu/index.php?option=com_swoogle_service&service=cache&view=raw&url=http%3A%2F%2Finferenceweb.stanford.edu%2F2004%2F07%2Fiw.owl",
//                "http://swoogle.umbc.edu/index.php?option=com_swoogle_service&service=cache&view=raw&url=http%3A%2F%2Fswrc.ontoware.org%2Fontology",
//                "http://swoogle.umbc.edu/index.php?option=com_swoogle_service&service=cache&view=raw&url=http%3A%2F%2Fwww.w3.org%2F2000%2F10%2Fswap%2Fpim%2Fcontact",
//                "http://swoogle.umbc.edu/index.php?option=com_swoogle_service&service=cache&view=raw&url=http%3A%2F%2Fswrc.ontoware.org%2Fontology%2Fportal"
            
            "http://lod.openlinksw.com/sparql?default-graph-uri=&query=SELECT+DISTINCT++*+WHERE+{%0D%0A{%0D%0A%3Fclass+rdf%3Atype+owl%3AClass+.%0D%0AFILTER%28regex%28str%28%3Fclass%29%2C+%22%23Movie%24%22%2C+%22i%22%29%29+.%0D%0AOPTIONAL+{%0D%0A%3Fattribute+rdfs%3Adomain+%3Fclass+.%0D%0A}%0D%0A}++UNION++{%0D%0A%3Fclass+rdf%3Atype+owl%3AClass+.%0D%0A%3Fclass+rdfs%3Alabel+%3Flabel+.%0D%0AFILTER%28regex%28str%28%3Flabel%29%2C+%22^Movie%24%22%2C+%22i%22%29%29++.%0D%0AOPTIONAL+{%0D%0A%3Fattribute+rdfs%3Adomain+%3Fclass+.%0D%0A}%0D%0A}%0D%0A}+LIMIT+300%0D%0A&format=text%2Fhtml&CXML_redir_for_subjs=121&CXML_redir_for_hrefs=&timeout=30000&debug=on",
            "http://lod.openlinksw.com/sparql?default-graph-uri=&query=SELECT+DISTINCT++*+WHERE+{%0D%0A{%0D%0A%3Fclass+rdf%3Atype+owl%3AClass+.%0D%0AFILTER%28regex%28str%28%3Fclass%29%2C+%22%23Movie%24%22%2C+%22i%22%29%29+.%0D%0AOPTIONAL+{%0D%0A%3Fattribute+rdfs%3Adomain+%3Fclass+.%0D%0A}%0D%0A}++UNION++{%0D%0A%3Fclass+rdf%3Atype+owl%3AClass+.%0D%0A%3Fclass+rdfs%3Alabel+%3Flabel+.%0D%0AFILTER%28regex%28str%28%3Flabel%29%2C+%22^Movie%24%22%2C+%22i%22%29%29++.%0D%0AOPTIONAL+{%0D%0A%3Fattribute+rdfs%3Adomain+%3Fclass+.%0D%0A}%0D%0A}%0D%0A}+LIMIT+300%0D%0A&format=text%2Fhtml&CXML_redir_for_subjs=121&CXML_redir_for_hrefs=&timeout=30000&debug=on",
            "http://lod.openlinksw.com/sparql?default-graph-uri=&query=SELECT+DISTINCT++*+WHERE+{%0D%0A{%0D%0A%3Fclass+rdf%3Atype+owl%3AClass+.%0D%0AFILTER%28regex%28str%28%3Fclass%29%2C+%22%23Movie%24%22%2C+%22i%22%29%29+.%0D%0AOPTIONAL+{%0D%0A%3Fattribute+rdfs%3Adomain+%3Fclass+.%0D%0A}%0D%0A}++UNION++{%0D%0A%3Fclass+rdf%3Atype+owl%3AClass+.%0D%0A%3Fclass+rdfs%3Alabel+%3Flabel+.%0D%0AFILTER%28regex%28str%28%3Flabel%29%2C+%22^Movie%24%22%2C+%22i%22%29%29++.%0D%0AOPTIONAL+{%0D%0A%3Fattribute+rdfs%3Adomain+%3Fclass+.%0D%0A}%0D%0A}%0D%0A}+LIMIT+300%0D%0A&format=text%2Fhtml&CXML_redir_for_subjs=121&CXML_redir_for_hrefs=&timeout=30000&debug=on",
            "http://lod.openlinksw.com/sparql?default-graph-uri=&query=SELECT+DISTINCT++*+WHERE+{%0D%0A{%0D%0A%3Fclass+rdf%3Atype+owl%3AClass+.%0D%0AFILTER%28regex%28str%28%3Fclass%29%2C+%22%23Movie%24%22%2C+%22i%22%29%29+.%0D%0AOPTIONAL+{%0D%0A%3Fattribute+rdfs%3Adomain+%3Fclass+.%0D%0A}%0D%0A}++UNION++{%0D%0A%3Fclass+rdf%3Atype+owl%3AClass+.%0D%0A%3Fclass+rdfs%3Alabel+%3Flabel+.%0D%0AFILTER%28regex%28str%28%3Flabel%29%2C+%22^Movie%24%22%2C+%22i%22%29%29++.%0D%0AOPTIONAL+{%0D%0A%3Fattribute+rdfs%3Adomain+%3Fclass+.%0D%0A}%0D%0A}%0D%0A}+LIMIT+300%0D%0A&format=text%2Fhtml&CXML_redir_for_subjs=121&CXML_redir_for_hrefs=&timeout=30000&debug=on",
            
            };
            for (int i = 0; i < urls.length; i++) {
                timer.schedule(new Downloader(i + 1, new URL(urls[i])), 1 * 5000, 5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Downloader extends TimerTask {

    private URL url;
    private int id;
    private boolean downloaded = false;

    public Downloader(int id, URL url) {
        this.id = id;
        this.url = url;
        System.out.println("Downloader "+id + " created.");
    }

    public void run() {
        if (downloaded) {
            return;
        }
        long start = System.currentTimeMillis();
        System.out.println(id + " Start: " + start);
        File file = new File("c:\\" + id + ".rdf");
        try {
            FileUtils.copyURLToFile(url, file);
            downloaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println(id + " Finish: " + end);
        System.out.println(id + " took: " + (end-start) + " ms");
    }
}