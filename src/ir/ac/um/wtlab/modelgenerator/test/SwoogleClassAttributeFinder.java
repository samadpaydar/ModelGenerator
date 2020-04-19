/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.test;

import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.QueryLanguage;
import java.io.*;
import ir.ac.um.wtlab.modelgenerator.util.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import ir.ac.um.wtlab.modelgenerator.*;
import org.openrdf.sail.memory.MemoryStore;

import ir.ac.um.wtlab.modelgenerator.repositorymanager.*;

/**
 *
 * @author Home
 */
public class SwoogleClassAttributeFinder {

    public static void main(String[] args) {
        try {
            Repository repository = RepositoryManager.createRepository("SWOOGLE_ONTOLOGIES");
            RepositoryConnection connection = repository.getConnection();
            File root = new File("G:\\Projects & Courses\\Papers\\A Semantic Web Enabled Approach for Automatic Adaptation of Activity Diagrams to New Use Cases\\Evaluation\\Semantic Web Maturity\\SWOOGLE Ontology Search Results");
            addFiles(connection, root);
            findAttributes(connection);
            RepositoryManager.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addFiles(RepositoryConnection connection, File directory) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                addFiles(connection, file);
            } else {
                String fileName = file.getName().toLowerCase();

                try {
                    if (fileName.endsWith(".rdf") || fileName.endsWith(".owl")) {
                        RepositoryManager.addRDF(connection, file, RDFFormat.RDFXML);
                    } else if (fileName.endsWith(".n3")) {
                        RepositoryManager.addRDF(connection, file, RDFFormat.N3);
                    } else {
                        System.out.println("SKIPPED: " + file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    System.out.println("EXCEPTION: " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }

    }

    private static void findAttributes(RepositoryConnection connection) {
        String[] classNames = {"Acknowledgement",
            "Address",
            "AddressBook",
            "Album",
            "Article",
            "Artist",
            "Cast",
            "Contact",
            "Date",
            "DomainObject",
            "EMail",
            "Example",
            "ExternalReview",
            "Information",
            "Institution",
            "Journal",
            "MemorableQuote",
            "Movie",
            "News",
            "Organization",
            "Patient",
            "Performance",
            "Person",
            "Phone",
            "Picture",
            "Presentation",
            "Project",
            "Publication",
            "Publications",
            "Publisher",
            "RegisteredUser",
            "Room",
            "School",
            "Section",
            "Song",
            "Soundtrack",
            "TaggedEntry",
            "TeachingMaterial",
            "TextFile",
            "Theater",
            "Ticket",
            "Time",
            "Tool",
            "Trailer",
            "Tutorial",
            "URL",
            "UWE",
            "UserComment",
            "UserData",
            "UserRating",
            "WebEngineeringGroup"};

        for (String className : classNames) {
            try {
                System.out.println("-------   CLASS " + className + "  -------");
                ArrayList<String> attributeNames = new ArrayList<String>();
                String query = "SELECT DISTINCT ?attribute WHERE {"
                        + "{"
                        + " ?class rdf:type ?type . "
                        + " ?attribute rdfs:domain ?class ."
                        + " FILTER(regex(str(?class), \"#" + className + "$\", \"i\")) "
                        + "} UNION { "
                        + " ?class rdf:type ?type . "
                        + " ?attribute rdfs:domain ?class ."
                        + " ?class rdfs:label ?label . "
                        + " FILTER(regex(str(?label), \"^" + className + "$\", \"i\")) "
                        + "}"
                        + "}";
//                long start = System.currentTimeMillis();
                TupleQueryResult result = RepositoryManager.executeQuery(connection, query);
//                long end = System.currentTimeMillis();
//                System.out.println("Query took: " + (end - start) + " ms");
                int resultCount = 0;
                while (result != null && result.hasNext()) {
                    resultCount++;
                    BindingSet bindingSet = result.next();
                    String attributeURI = bindingSet.getValue("attribute").stringValue();
                    String attributeName = getAttributeName(attributeURI);
                    if (!attributeNames.contains(attributeName)) {
                        attributeNames.add(attributeName);
                    }

                    System.out.println("\t\t" + resultCount + ". " + attributeName + "\t" + attributeURI);
                }
                if (resultCount < 5) {
                    query = "SELECT DISTINCT ?attribute WHERE {"
                            + "{"
                            + " ?class rdf:type ?type . "
                            + " ?class rdfs:subClassOf ?super ."
                            + " ?attribute rdfs:domain ?super ."
                            + " FILTER(regex(str(?class), \"#" + className + "$\", \"i\")) "
                            + "} UNION {"
                            + " ?class rdf:type ?type . "
                            + " ?class rdfs:subClassOf ?super ."
                            + " ?attribute rdfs:domain ?super ."
                            + " ?class rdfs:label ?label . "
                            + " FILTER(regex(str(?label), \"^" + className + "$\", \"i\")) "
                            + "}"
                            + "}";
                    result = RepositoryManager.executeQuery(connection, query);
                    System.out.println("\t\t======= ATTRIBUTES FROM SUPERCLASS ========");
                    while (result != null && result.hasNext()) {
                        resultCount++;
                        BindingSet bindingSet = result.next();
                        String attributeURI = bindingSet.getValue("attribute").stringValue();
                        String attributeName = getAttributeName(attributeURI);
                        if (!attributeNames.contains(attributeName)) {
                            attributeNames.add(attributeName);
                        }
                        System.out.println("\t\t" + resultCount + ". " + attributeName + "\t" + attributeURI);
                    }
                }
                String[] temp1 = new String[attributeNames.size()];
                String[] temp = attributeNames.toArray(temp1);
                Arrays.sort(temp);
                String temp2 = "";
                if (temp.length > 0) {
                    for (String name : temp) {
                        temp2 += name + ", ";
                    }
                    System.out.println("ATTRIBUTE NAMES (" + temp.length + "): " +temp2);
                }
                result.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getAttributeName(String uri) {
        String result = null;
        int index = uri.lastIndexOf('#');
        if (index != -1) {
            result = uri.substring(index + 1);
        } else {
            index = uri.lastIndexOf('/');
            if (index != -1) {
                result = uri.substring(index + 1);
            }
        }
        return result;
    }

}
