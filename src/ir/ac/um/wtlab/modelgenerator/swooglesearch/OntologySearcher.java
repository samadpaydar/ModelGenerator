/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.swooglesearch;

import ir.ac.um.wtlab.modelgenerator.db.DBManager;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import ir.ac.um.wtlab.modelgenerator.repositorymanager.RepositoryManager;

/**
 *
 * @author Farshad
 */
public class OntologySearcher {

    private String[] keywords;
    private int resultCount;
    private HTTPFetcher httpFetcher;
    private RepositoryConnection ontologyRepositoryConnection;

    public OntologySearcher(RepositoryConnection ontologyRepositoryConnection) {
        this.ontologyRepositoryConnection = ontologyRepositoryConnection;
        httpFetcher = new HTTPFetcher();
    }

    private void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public String[] getKeywords() {
        return keywords;
    }

    private void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void startSearch(String[] keywords, int resultCount) {
        return;
        //this is used to disabled semantic web facility for the purpose of evaluation
        
 /*       setKeywords(keywords);
        setResultCount(resultCount);
        searchInSwoogle();
        fetchAndStoreResultOntologies();
         * 
         */
    }

    /**
     * searches the keywords in swoogle and saves the result in an .XML file
     */
    private void searchInSwoogle() {
        return;
        //this is used to disabled semantic web facility for the purpose of evaluation
        /*String query = createQuery();
        String resultFileName = Constants.WORKING_DIRECTORY + File.separator
                + Constants.RESULT_FILE_NAME + ".xml";
        try {
            String temp = "";
            for (String keyword : keywords) {
                temp += keyword + ", ";
            }
            System.out.println("Searching SWOOGLE for keywords: " + temp);
            httpFetcher.fetch(new URL(query), resultFileName);
            System.out.println("Search completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * fetches the result ontologies from the XML file generated in the previous phase
     */
    private void fetchAndStoreResultOntologies() {
        SWDExtractor extractor = new SWDExtractor();
        ArrayList<String> resultOntologies = new ArrayList<String>();
        String resultFileName = Constants.WORKING_DIRECTORY + File.separator
                + Constants.RESULT_FILE_NAME + ".xml";
        try {
            ArrayList<String> temp = extractor.extractSWDs(resultFileName);
            resultOntologies.addAll(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < resultCount; i++) {
            String ontologyUri = resultOntologies.get(i);
            int ontologyId = DBManager.getOntologyId(ontologyUri);
            if (ontologyId != -1) {
                continue;
            }
            int id = DBManager.getOntologyCount() + 1;
            String path = Constants.WORKING_DIRECTORY + File.separator
                    + Constants.ONTOLOGY_FILE_NAME + File.separator + keywords[0] ;
            new File(path).mkdirs();
            String ontologyFileName = path + File.separator + id + ".xml";
            try {
                ontologyId = DBManager.addOntology(ontologyUri);
                System.out.println(i + " Fetching ontology: " + ontologyUri + " to file " + ontologyFileName);
                httpFetcher.fetch(new URL(ontologyUri), ontologyFileName);
                System.out.println("Fetched.");
                File file = new File(ontologyFileName);
                if (file.exists()) {
                    System.out.println("Adding to ontology repository file: " + file.getName());
                    RepositoryManager.addRDF(ontologyRepositoryConnection, file, RDFFormat.RDFXML);
                    System.out.println("Added.");
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String createQuery() {
        String searchString = keywords[0];
        for (int i = 1; i < keywords.length; i++) {
            searchString += "%20" + keywords[i];
        }
        return Constants.SWOOGLE_URI
                + "queryType=search_swd_ontology&searchString="
                + searchString + "&key=demo";
    }

    /*    public static void main(String[] args) {
    String[] keywords = {"Student", "Teacher", "Account", "Hospital", "User", "Patient", "Publication", "Personal Wiki",
    "Report", "Project", "Movie", "Ticket", "Inventory"};
    for (String keyword : keywords) {
    String[] terms = {keyword};
    OntologySearcher searcher = new OntologySearcher(terms, 10);
    ArrayList<Integer> ontologyIds = searcher.startSearch();
    System.out.println(Arrays.toString(terms));
    for (int i = 0; i < ontologyIds.size(); i++) {
    System.out.println("id: " + ontologyIds.get(i));
    }
    }
    }*/
}
