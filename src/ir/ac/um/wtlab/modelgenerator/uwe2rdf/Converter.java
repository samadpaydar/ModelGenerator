/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.uwe2rdf;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.reasoner.rulesys.*;
import com.hp.hpl.jena.reasoner.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;

/**
 *
 * @author Home
 */
class Converter {

    private SAXParser parser;
    private String projectId;

    public Converter(String projectId) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setValidating(true);
            this.projectId = projectId;
            parser = spf.newSAXParser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void convert(File xmlFile) {
        try {
            String temp = xmlFile.getName().substring(0, xmlFile.getName().lastIndexOf("."));
            String outputFileName = temp + ".n3";
            File outputFile = new File(xmlFile.getParent(), outputFileName);
            SAXHandler handler = new SAXHandler(projectId, outputFile);
            handler.createPrefixes();
            parser.parse(xmlFile, handler);
            handler.close();
            System.out.println("Doing Inference");
            doInference(outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private void doInference(File rawN3File) {
        try {
            OntModel rawModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            System.out.println("raw size: " + rawModel.size());
            String uri = rawN3File.toURI().toString();
            rawModel.read(uri, "N3");
            List rules = Rule.rulesFromURL("file:uwe2rdf.rules");
            Reasoner reasoner = new GenericRuleReasoner(rules);
            InfModel inf = ModelFactory.createInfModel(reasoner, rawModel);
            
            System.out.println("inferred size: " + inf.size());
            String temp = rawN3File.getName().substring(0, rawN3File.getName().lastIndexOf("."));
            String outputFileName = temp + ".inf.n3";
            File outputFile = new File(rawN3File.getParent(), outputFileName);
            PrintStream file = new PrintStream(outputFile);
            inf.write(file, "N3");
            file.close();
         //   findCancelActivity(outputFile);
        } catch (Exception e) {
            System.out.println("EXCEPTION FOR FILE: " + rawN3File.getName());
            e.printStackTrace();
        }
    }
    
}
