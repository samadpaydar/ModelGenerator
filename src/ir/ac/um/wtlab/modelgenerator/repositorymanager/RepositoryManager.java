/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.repositorymanager;

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
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import ir.ac.um.wtlab.modelgenerator.*;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author UM
 */
public class RepositoryManager {

    private final static String REPOSITORY_PATH = "repository";

    private RepositoryManager() {
    }

    public static Repository connectToRepository(String repositoryName) {
        File dataDir = new File(REPOSITORY_PATH + File.separator + repositoryName);
        Logger.log("Connecting to repository " + repositoryName);
        try {
            Repository modelRepository = new SailRepository(new NativeStore(dataDir));
            modelRepository.initialize();
            return modelRepository;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Repository createRepository(String repositoryName) throws Exception {
        File dataDir = new File(REPOSITORY_PATH + File.separator + repositoryName);
        //empty directory
        if (dataDir.exists()) {
            boolean deleted = dataDir.delete();
            if (deleted) {
                dataDir.mkdir();
            } else {
                throw new RepositoryException("Can not delete existing repository: " + dataDir.getName());
            }
        }

        Logger.log("Creating RDF repository " + repositoryName);
        Repository modelRepository = new SailRepository(new NativeStore(dataDir));
        modelRepository.initialize();
        return modelRepository;
    }

    public static void addRDF(RepositoryConnection connection, File rdfFile, RDFFormat format) throws Exception {
        String baseURI = "http://example.org/example/local";
        Logger.log("Adding RDF file " + rdfFile.getName() + " to the repository");
        long start = System.currentTimeMillis();
        connection.add(rdfFile, baseURI, format);
        long end = System.currentTimeMillis();
        System.out.println("File: " + rdfFile.getAbsolutePath());
        System.out.println("Added to Repository in " + (end - start) + " ms");
    }

    public static TupleQueryResult executeQuery(RepositoryConnection connection, String queryString) {
        final String PREFIXES = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
                + " PREFIX uwe2rdf:<http://wtlab.um.ac.ir/uml.owl#>"
                + " PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + " PREFIX owl:<http://www.w3.org/2002/07/owl#>"
                + " PREFIX umlpt:<http://www.omg.org/spec/UML/20110701/PrimitiveTypes.xmi#>"
                + " PREFIX uml:<http://www.omg.org/spec/UML/20110701> "
                + " PREFIX dc:<http://purl.org/dc/elements/1.1/>";
        try {
            TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, PREFIXES + queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean executeBooleanQuery(RepositoryConnection connection, String queryString) {
        final String PREFIXES = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
                + " PREFIX uwe2rdf:<http://wtlab.um.ac.ir/uml.owl#>"
                + " PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + " PREFIX owl:<http://www.w3.org/2002/07/owl#>"
                + " PREFIX umlpt:<http://www.omg.org/spec/UML/20110701/PrimitiveTypes.xmi#>"
                + " PREFIX uml:<http://www.omg.org/spec/UML/20110701> ";
        try {
            BooleanQuery booleanQuery = connection.prepareBooleanQuery(QueryLanguage.SPARQL, PREFIXES + queryString);
            boolean result = booleanQuery.evaluate();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void closeConnection(RepositoryConnection connection) throws Exception {
        if (connection.isOpen()) {
            System.out.println("connection was open");
            connection.commit();
            connection.close();
        }
    }

    public static void main(String[] args) {
        try {
            Repository repository = RepositoryManager.createRepository("UWE");
            RepositoryConnection connection = repository.getConnection();
            RepositoryManager.addRDF(connection, new File("G:\\Projects\\My Thesis\\ModelGenerator\\models.2014-1-6 19_1_20.n3"), RDFFormat.N3);
            RepositoryManager.addRDF(connection, new File("G:\\Projects\\My Thesis\\ModelGenerator\\Annotations 921030.n3"), RDFFormat.N3);
            RepositoryManager.closeConnection(connection);

 /*           repository = RepositoryManager.createRepository("ontologies");
            connection = repository.getConnection();
            File root = new File("G:\\Projects & Courses\\Papers\\A Semantic Web Enabled Approach for Automatic Adaptation of Activity Diagrams to New Use Cases\\Evaluation\\Semantic Web Maturity\\SWOOGLE Ontology Search Results");
            addFiles(connection, root);
            RepositoryManager.closeConnection(connection);
   */         
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
}

class RepositoryException extends RuntimeException {

    public RepositoryException(String message) {
        super(message);
    }
}