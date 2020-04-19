/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator;

import ir.ac.um.wtlab.modelgenerator.util.Utils;
import java.io.PrintWriter;
import java.net.*;
import java.io.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import java.util.*;
import javax.xml.parsers.*;

/**
 *
 * @author Home
 */
public class RKBExplorerWordNetManager {

    private final static String WORDNET_SERVICE_URI = "http://wordnet.rkbexplorer.com/sparql/?query=";
    private SAXParser parser;

    public RKBExplorerWordNetManager() {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setValidating(true);
            parser = spf.newSAXParser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getHypernyms(String word) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            String query = "SELECT ?hypernymLabel WHERE { ?word rdfs:label \"" + word + "\" ."
                    + " ?word wordnet:hyponymOf ?hypernym . "
                    + " ?hypernym rdfs:label ?hypernymLabel } LIMIT 100";
            String response = executeQuery(query);
            File xmlFile = new File("response.xml");
            PrintWriter responseFile = new PrintWriter(xmlFile);
            responseFile.println(response);
            responseFile.close();
            XMLHandler xmlHandler = new XMLHandler();
            parser.parse(xmlFile, xmlHandler);
            result = xmlHandler.getResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String executeQuery(String query) {
        String prefix = getPrefix();
        query = Utils.escape(query);
        prefix = Utils.escape(prefix);
        String response = null;
        try {
            response = HTTPRequester.fetch(new URL(WORDNET_SERVICE_URI + prefix + query));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private String getPrefix() {
        final String[] NAMESPACES = {
            Constants.ID_NAMESPACE, Constants.RDF_NAMESPACE, Constants.RDFS_NAMESPACE,
            Constants.AKT_NAMESPACE, Constants.AKTS_NAMESPACE, Constants.OWL_NAMESPACE,
            Constants.WORDNET_NAMESPACE
        };

        final String[] NAMESPACES_PREFIXES = {
            Constants.ID_NAMESPACE_PREFIX, Constants.RDF_NAMESPACE_PREFIX,
            Constants.RDFS_NAMESPACE_PREFIX, Constants.AKT_NAMESPACE_PREFIX,
            Constants.AKTS_NAMESPACE_PREFIX, Constants.OWL_NAMESPACE_PREFIX,
            Constants.WORDNET_NAMESPACE_PREFIX
        };
        String result = "";
        for (int i = 0; i < NAMESPACES.length; i++) {
            result += "PREFIX " + NAMESPACES_PREFIXES[i] + ": <" + NAMESPACES[i] + "> ";
        }
        return result;
    }
}

class XMLHandler extends DefaultHandler {

    private ArrayList<String> results = new ArrayList<String>();
    private StringBuffer buffer;
    private final String LITERAL = "literal";
    
    public ArrayList<String> getResults() {
        return results;
    }

    public void startElement(String uri, String localName, String qname, Attributes attributes) {
        if (qname.equalsIgnoreCase(LITERAL)) {
            buffer = new StringBuffer();
        }
    }

    public void characters(char buf[], int offset, int len) {
        String str = new String(buf, offset, len);
        if (buffer != null) {
            buffer.append(str);
        }
    }

    public void endElement(String uri, String localName, String qname) {
        if (buffer != null) {
            results.add(buffer.toString());
        }
        buffer = null;
    }
}
