/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import ir.ac.um.wtlab.modelgenerator.similarity.*;
import ir.ac.um.wtlab.modelgenerator.util.*;
import java.util.ArrayList;

/**
 *
 * @author Home
 */
public class DBManager {

    private static Connection dbConnection;
    private final static String DB_URI = "jdbc:mysql://localhost:3306/modelgenerator";
    private final static String DB_USERNAME = "root";
    private final static String DB_PASSWORD = "root";

    private DBManager() {
    }

    private static void connect() {
        try {
            if (dbConnection == null) {
                Class.forName("com.mysql.jdbc.Driver");
                dbConnection = DriverManager.getConnection(DB_URI, DB_USERNAME, DB_PASSWORD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addWordSimilarity(String word1, String word2, double similarity, String sense) {
        connect();
        try {
            //      long start = System.currentTimeMillis();
            Statement stmt = dbConnection.createStatement();
            String query = "INSERT INTO wordSimilarities (text1, text2, sense, similarity) "
                    + " VALUES('" + scape(word1) + "', '" + scape(word2) +"', '" +sense + "', " +
                    similarity+")";
            stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getWordSimilarity(String text1, String text2, String sense) {
        connect();
        double similarity = -1;
        try {
            //      long start = System.currentTimeMillis();
            Statement stmt = dbConnection.createStatement();
            String query = "SELECT similarity FROM wordSimilarities WHERE text1='" + text1 + ""
                    + "' AND text2='" + text2 + "' AND sense='" + sense + "'";
            ResultSet results = stmt.executeQuery(query);
            //        int i = 0;
            if (results.next()) {
                similarity = results.getDouble("similarity");
            }
            //          long end = System.currentTimeMillis();
//            Logger.log(i + " similarity was loaded from db in " + (end - start) + " ms similarity: " +  similarity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return similarity;
    }

    public static void loadWordSimilarityCache(SimilarityCache cache) {
        connect();
        try {
            long start = System.currentTimeMillis();
            Statement stmt = dbConnection.createStatement();
            String query = "SELECT * FROM wordSimilarities";
            ResultSet results = stmt.executeQuery(query);
            int i = 0;
            while (results.next()) {
                String text1 = results.getString("text1");
                String text2 = results.getString("text2");
                String sense = results.getString("sense");
                double similarity = results.getDouble("similarity");
                Pair pair = new Pair(text1, text2, similarity, sense);
                cache.add(pair);
                i++;
            }
            long end = System.currentTimeMillis();
            Logger.log(i + " word similarity pairs was loaded from db to cache");
            Logger.log(" took: " + (end - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveWordSimilarityCache(SimilarityCache cache) {
        connect();
        String query = "";
        try {
            long start = System.currentTimeMillis();
            Statement stmt = dbConnection.createStatement();
            ArrayList<Pair> pairs = cache.getPairs();
            int i = 0;
            for (; i < pairs.size(); i++) {
                Pair pair = pairs.get(i);
                query = "INSERT INTO wordSimilarities(text1, text2, similarity, sense) VALUES "
                        + "('" + scape(pair.getText1()) + "', '" + scape(pair.getText2()) + "', " + pair.getSimilarity()
                        + ", '" + pair.getSense() + "')";
                stmt.executeUpdate(query);
            }
            long end = System.currentTimeMillis();
            Logger.log(i + " word similarity pairs was added from cache to database");
            Logger.log(" took: " + (end - start) + " ms");
        } catch (Exception e) {
            System.out.println(query);
            e.printStackTrace();
        }
    }

    private static String scape(String text) {
        if (text == null) {
            return text;
        }
        text = text.replace("\"", "\\\"");
        text = text.replace("'", "\\'");
        text = text.replace('\n', ' ');
        return text;
    }

    public static void loadUsecaseSimilarityCache(SimilarityCache cache) {
        connect();
        try {
            long start = System.currentTimeMillis();
            Statement stmt = dbConnection.createStatement();
            String query = "SELECT * FROM usecaseSimilarities";
            ResultSet results = stmt.executeQuery(query);
            int i = 0;
            while (results.next()) {
                String text1 = results.getString("text1");
                String text2 = results.getString("text2");
                String sense = results.getString("sense");
                double similarity = results.getDouble("similarity");
                Pair pair = new Pair(text1, text2, similarity, sense);
                cache.add(pair);
                i++;
            }
            long end = System.currentTimeMillis();
            Logger.log(i + " usecase similarity pairs was loaded from db to cache");
            Logger.log(" took: " + (end - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveUsecaseSimilarityCache(SimilarityCache cache) {
        connect();
        try {
            long start = System.currentTimeMillis();
            Statement stmt = dbConnection.createStatement();
            ArrayList<Pair> pairs = cache.getPairs();
            int i = 0;
            for (; i < pairs.size(); i++) {
                Pair pair = pairs.get(i);
                String query = "INSERT INTO usecaseSimilarities(text1, text2, similarity, sense) VALUES "
                        + "('" + scape(pair.getText1()) + "', '" + scape(pair.getText2()) + "', " + pair.getSimilarity()
                        + ", '" + pair.getSense() + "')";
                stmt.executeUpdate(query);
            }
            long end = System.currentTimeMillis();
            Logger.log(i + " usecase similarity pairs was added from cache to database");
            Logger.log(" took: " + (end - start) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getOntologyCount() {
        connect();
        int result = 0;
        try {
            Statement stmt = dbConnection.createStatement();
            String query = "SELECT count(*) FROM ontologies";
            ResultSet results = stmt.executeQuery(query);
            if (results.next()) {
                result = results.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public static int getOntologyId(String uri) {
        connect();
        int result = -1;
        try {
            Statement stmt = dbConnection.createStatement();
            String query = "SELECT id FROM ontologies WHERE uri=\"" + uri + "\"";
            ResultSet results = stmt.executeQuery(query);
            if (results.next()) {
                result = results.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int addOntology(String uri) {
        connect();
        try {
            Statement stmt = dbConnection.createStatement();
            String query = "INSERT INTO ontologies(uri) VALUES('"
                    + uri + "')";
            stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getOntologyCount();
    }
}
