/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.swooglesearch;

import java.io.IOException;
import org.apache.xerces.parsers.*;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import java.util.ArrayList;

/**
 *
 * @author Farshad
 */
public class SWDExtractor {

    private XMLReader parser;
    private ArrayList<String> swdList;

    public SWDExtractor() {
        try {
            parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        } catch (SAXException e) {
            System.out.println("Error in creating SAX parser");
            return;
        }
        parser.setContentHandler(new SearchResultContentHandler());
    }

    public ArrayList<String> extractSWDs(String file) throws SAXException, IOException {
        swdList = new ArrayList<String>();
        parser.parse(file);
        return swdList;
    }

    private class SearchResultContentHandler extends DefaultHandler {

        public void startElement(String uri, String localName, String qname, Attributes attributes) {
            if (qname.equalsIgnoreCase("wob:SemanticWebDocument")) {
                String swdUri = attributes.getValue("rdf:about");
                swdList.add(swdUri);
            }
        }
    }
}

