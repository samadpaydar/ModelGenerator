/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ir.ac.um.wtlab.modelgenerator.swooglesearch;

/**
 *
 * @author Farshad
 */
public class Constants {
    public static final String SWOOGLE_URI = 
             "http://sparql.cs.umbc.edu:80/swoogle31/q?";
            
    public static final int SWOOGLE_RESULTS_PER_QUERY = 10;

    public static final int MAX_RESULT_COUNT = 100;
    public static final int MIN_RESULT_COUNT = 1;
    public static final int DEFAULT_RESULT_COUNT = 3;
    public static final int RESULT_COUNT_STEP = SWOOGLE_RESULTS_PER_QUERY;
    
    public static final String WORKING_DIRECTORY = "workingDirectory";
    public static final String RESULT_FILE_NAME = "result";
    public static final String ONTOLOGY_FILE_NAME = "ontology";
    
    public static final String[] BLACK_LIST = { "thing", "nothing" , "resource"};
}
