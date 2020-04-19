/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.similarity;

import java.util.*;
import ir.ac.um.wtlab.modelgenerator.db.DBManager;

/**
 *
 * @author Home
 */
public class SimilarityCache {

    private ArrayList<Pair> pairs;
    public static int WORD_SIMILARITY_CACHE = 0;
    public static int HOT_WORD_SIMILARITY_CACHE = 1;
    public static int USECASE_SIMILARITY_CACHE = 2;
    public static int HOT_USECASE_SIMILARITY_CACHE = 3;
    private final int TYPE;

    public SimilarityCache(int type) {
        this.TYPE = type;
        pairs = new ArrayList<Pair>(100);
        if (type == WORD_SIMILARITY_CACHE) {
            DBManager.loadWordSimilarityCache(this);
        } else if (type == USECASE_SIMILARITY_CACHE) {
            DBManager.loadUsecaseSimilarityCache(this);
        } else {
            pairs = new ArrayList<Pair>();
        }
    }

    public ArrayList<Pair> getPairs() {
        return pairs;
    }

    public double getPairSimilarity(String text1, String text2) {
        for (int i = 0; i < pairs.size(); i++) {
            Pair pair = pairs.get(i);
            if ((pair.getText1().equalsIgnoreCase(text1) && pair.getText2().equalsIgnoreCase(text2))
                    || (pair.getText1().equalsIgnoreCase(text2) && pair.getText2().equalsIgnoreCase(text1))) {
                return pair.getSimilarity();
            }
        }
        return -1;
    }

    public double getPairSimilarity(String text1, String text2, String sense) {
//        double similarity = -1;
//        if (this.TYPE == SimilarityCache.WORD_SIMILARITY_CACHE) {
//            similarity = DBManager.getWordSimilarity(text1, text2, sense);
//        }
        for (int i = 0; i < pairs.size(); i++) {
            Pair pair = pairs.get(i);
            if ((pair.getText1().equalsIgnoreCase(text1) && pair.getText2().equalsIgnoreCase(text2) && pair.getSense().equalsIgnoreCase(sense))
                    || (pair.getText1().equalsIgnoreCase(text2) && pair.getText2().equalsIgnoreCase(text1) && pair.getSense().equalsIgnoreCase(sense))) {
                return pair.getSimilarity();
            }
        }
        return -1;
    }

    public void add(Pair pair) {
        pairs.add(pair);
    }
}
