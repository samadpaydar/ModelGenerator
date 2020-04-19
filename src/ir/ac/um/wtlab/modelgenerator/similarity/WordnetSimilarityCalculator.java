/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.similarity;

import edu.sussex.nlp.jws.*;
import java.util.*;
import ir.ac.um.wtlab.modelgenerator.util.*;
import ir.ac.um.wtlab.modelgenerator.stemmer.*;
import ir.ac.um.wtlab.modelgenerator.db.DBManager;
//import uk.ac.shef.wit.simmetrics.similaritymetrics.*;

/**
 *
 * @author Home
 */
public class WordnetSimilarityCalculator {

    private final static String WORDNET_DIRECTORY = "./wordnet";
    private final static JWS ws = new JWS(WORDNET_DIRECTORY, "3.0");
    private final static Lin lin = ws.getLin();
    private final static WuAndPalmer wup = ws.getWuAndPalmer();//WUP GIVES HEAP SPACE ERROR
    private final static LeacockAndChodorow lac = ws.getLeacockAndChodorow(); //GIVES STACKOVERFLOW ERROR
    private final static AdaptedLesk lesk = ws.getAdaptedLesk();//VERY SLOW
    private final static JiangAndConrath jac = ws.getJiangAndConrath();
    private final static Path path = ws.getPath();//GIVES STACKOVERFLOW ERROR
    private final static HirstAndStOnge has = ws.getHirstAndStOnge();// vERYY SLOW
    private final static String[] SENSES = {"n", "v", "a", "r"}; //n = noun  v = verb  a = adjective  r = adverb
    private static SimilarityCache usecaseCache = new SimilarityCache(SimilarityCache.USECASE_SIMILARITY_CACHE);
    private static SimilarityCache wordCache = new SimilarityCache(SimilarityCache.WORD_SIMILARITY_CACHE);
    private static SimilarityCache hotUsecaseCache = new SimilarityCache(SimilarityCache.HOT_USECASE_SIMILARITY_CACHE);
    private static SimilarityCache hotWordCache = new SimilarityCache(SimilarityCache.HOT_WORD_SIMILARITY_CACHE);
    private final static double MAXIMUM_ALLOWED_SIMILARITY_FOR_DIFFERING_NAMES = 1.0;
    private final static double MAXIMUM_ALLOWED_SIMILARITY_FOR_DIFFERING_VERBS = 1.0;
//    private static Levenshtein leven = new Levenshtein();
//    public final static double LEVENSHTEIN_SIMILARITY_WEIGHT = 0.2;

    public static void saveResultsInDB() {
        DBManager.saveWordSimilarityCache(hotWordCache);
        DBManager.saveUsecaseSimilarityCache(hotUsecaseCache);
    }

    /**
     * this method bounds the similarity of two differing words to a specified
     * threshold It does not allow a big similarity for non-identical words
     *
     * @param similarity
     * @param word1
     * @param word2
     * @param sense
     * @return
     */
    private static double boundSimilarity(double similarity, String word1, String word2, String sense) {
        double bound = 1.0;
        if (sense.equalsIgnoreCase("n")) {
            bound = MAXIMUM_ALLOWED_SIMILARITY_FOR_DIFFERING_NAMES;
        } else if (sense.equalsIgnoreCase("v")) {
            bound = MAXIMUM_ALLOWED_SIMILARITY_FOR_DIFFERING_VERBS;
        }
        if (!word1.equalsIgnoreCase(word2)) {
            if (similarity > bound) {
                similarity = bound;
            }
        }
        return similarity;
    }

    public static double getStemSimilarity(String word1, String word2, String sense) {
        if (word1.equalsIgnoreCase(word2)) {
            return 1.0;
        }
        List<String> stems1 = WNStemmer.stem(word1);
        if (stems1.size() > 0) {
            word1 = stems1.get(0);
        }
        List<String> stems2 = WNStemmer.stem(word2);
        if (stems2.size() > 0) {
            word2 = stems2.get(0);
        }
        double similarity = wordCache.getPairSimilarity(word1, word2, sense);
        if (similarity == -1) {
            try {
                double linSimilarity = lin.max(word1, word2, sense);
                //double wupSimilarity = wup.max(word1, word2, sense);
                //double jacSimilarity = jac.max(word1, word2, sense);
                //            double pathSimilarity = path.max(word1, word2, sense);
                //System.out.println("LIN similarity of " + word1 + " , " + word2 + " SENSE: " + sense + " : " + linSimilarity);
                //linSimilarity = boundSimilarity(linSimilarity, word1, word2, sense);

                /*
                 * double wupSimilarity = wup.max(word1, word2, sense);
                 * //System.out.println("WUP similarity of " + word1 + " , " +
                 * word2 + " SENSE: " + sense + " : " + wupSimilarity);
                 * wupSimilarity = boundSimilarity(wupSimilarity, word1, word2,
                 * sense);
                 *
                 * // double lacSimilarity = lac.max(word1, word2, sense); //
                 * double leskSimilarity = lesk.max(word1, word2, sense); //
                 * double jacSimilarity = jac.max(word1, word2, sense); double
                 * pathSimilarity = 0.0; try { pathSimilarity = path.max(word1,
                 * word2, sense); // System.out.println("PATH similarity of " +
                 * word1 + " , " + word2 + " SENSE: " + sense + " : " +
                 * pathSimilarity); } catch (Exception e) { e.printStackTrace();
                 * } pathSimilarity = boundSimilarity(pathSimilarity, word1,
                 * word2, sense); // double hasSimilarity = has.max(word1,
                 * word2, sense); // if (sense.equalsIgnoreCase("v")) { //
                 * String temp = word1 + "_" + word2; // temp += ", " +
                 * linSimilarity + ", " + wupSimilarity + ", " + pathSimilarity;
                 * // System.out.println(temp); // } int nonZeroCount = 0; if
                 * (linSimilarity > 0) { nonZeroCount++; } if (wupSimilarity >
                 * 0) { nonZeroCount++; } if (pathSimilarity > 0) {
                 * nonZeroCount++; } if (nonZeroCount > 0) { similarity =
                 * (linSimilarity + wupSimilarity + pathSimilarity) /
                 * nonZeroCount; } else { similarity = 0.0; }
                 */

                //similarity = (linSimilarity + wupSimilarity) / 2;
                similarity = linSimilarity;
                //DBManager.addWordSimilarity(word1, word2, similarity, sense);
                wordCache.add(new Pair(word1, word2, similarity, sense));
                hotWordCache.add(new Pair(word1, word2, similarity, sense));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return similarity;
    }

    private static boolean isOk(int index, int matchIndex, int[] matchIndexes) {
        for (int i = 0; i < index; i++) {
            if (matchIndexes[i] == matchIndex) {
                return false;
            }
        }
        return true;
    }

    /*
     * private static double computeSimilarity(ArrayList<String> words1,
     * ArrayList<String> words2, int[] matchIndexes) { Logger.log("computing
     * similarity between wordList '" + words1 + "' and '" + words2 + " with
     * matchIndexes: " + Arrays.toString(matchIndexes)); int length1 =
     * words1.size(); int length2 = words2.size(); double result = 0; if
     * (length1 <= length2) { for (int i = 0; i < matchIndexes.length; i++) {
     * String word1 = words1.get(i); String word2 = words2.get(matchIndexes[i]);
     * Logger.log("\tcomputing similarity between word '" + word1 + "' and '" +
     * word2 + "'"); double max = -1; for (String sense : SENSES) { double
     * similarity = 0; try { similarity = getStemSimilarity(word1, word2,
     * sense); } catch (Exception e) { e.printStackTrace(); } if (similarity >
     * max) { max = similarity; } } Logger.log("\t" + String.valueOf(max));
     * result += max; } } else { for (int i = 0; i < matchIndexes.length; i++) {
     * String word1 = words1.get(matchIndexes[i]); String word2 = words2.get(i);
     * Logger.log("\tcomputing similarity between word '" + word1 + "' and '" +
     * word2 + "'"); double max = -1; for (String sense : SENSES) { double
     * similarity = 0; try { similarity = getStemSimilarity(word1, word2,
     * sense); } catch (Exception e) { e.printStackTrace(); } if (similarity >
     * max) { max = similarity; } } Logger.log("\t" + String.valueOf(max));
     * result += max; } } Logger.log("similarity: " + result); return result; }
     */
    /*
     * private static double assignMatch(int index, int[] matchIndexes, int[]
     * bestMatchIndexes, double bestMatchSimilatity, ArrayList<String> words1,
     * ArrayList<String> words2) { int length1 = words1.size(); int length2 =
     * words2.size(); for (int i = 0; i < length2; i++) { if (isOk(index, i,
     * matchIndexes)) { matchIndexes[index] = i; if (index == length1 - 1) {
     * double similarity = computeSimilarity(words1, words2, matchIndexes); if
     * (similarity > bestMatchSimilatity) { bestMatchSimilatity = similarity;
     * for (int j = 0; j < matchIndexes.length; j++) { bestMatchIndexes[j] =
     * matchIndexes[j]; } } } else { bestMatchSimilatity = assignMatch(index +
     * 1, matchIndexes, bestMatchIndexes, bestMatchSimilatity, words1, words2);
     * } } } return bestMatchSimilatity; }
     */
    public static double computeActionNameSimilarity(String actionName1, String actionName2) {
        //subject to modification
        double similarity = computeUsecaseNameSimilarity(actionName1, actionName2);
        return similarity;
    }

    public static double computeActorNameSimilarity(String actorName1, String actorName2) {
        //subject to modification
        double similarity = computeUsecaseNameSimilarity(actorName1, actorName2);
        return similarity;
    }

    public static void main(String[] args) {
        //   System.out.println(computeUsecaseNameSimilarity("input", "CancelBookSellCorrectInput"));
        //    System.out.println(WordnetSimilarityCalculator.computeUsecaseNameSimilarity("register", "Create"));
        //     System.out.println(WordnetSimilarityCalculator.computeUsecaseNameSimilarity("register User", "Create Account"));

        System.out.println(lin.max("news", "content", "n"));
        System.out.println(lin.max("news", "Index", "n"));
    }

    public static double computeUsecaseNameSimilarity(String usecaseName1, String usecaseName2) {
        Logger.log("computing similarity between usecase '" + usecaseName1 + "' and usecase '" + usecaseName2 + "'");
        double similarity = usecaseCache.getPairSimilarity(usecaseName1, usecaseName2);
        if (similarity == -1) {
            similarity = hotUsecaseCache.getPairSimilarity(usecaseName1, usecaseName2);
        }
        if (similarity != -1) {
            Logger.log("similarity: " + similarity + "\t\t cache hit");
            return similarity;
        }
        String originalUsecaseName1 = usecaseName1;
        String originalUsecaseName2 = usecaseName2;

        usecaseName1 = Utils.prepare(usecaseName1);
        usecaseName2 = Utils.prepare(usecaseName2);

        ArrayList<String> words1 = Utils.split(usecaseName1);
        ArrayList<String> words2 = Utils.split(usecaseName2);

        if (words1.size() < words2.size()) {
            ArrayList<String> temp = words1;
            words1 = words2;
            words2 = temp;
        }

        double similaritySum = 0.0;
        for (int i = 0; i < words1.size(); i++) {
            String word1 = words1.get(i);
            double maxSimilarity = -1;
            for (int j = 0; j < words2.size(); j++) {
                String word2 = words2.get(j);
                similarity = getSimilarity(word1, word2);
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                }
            }
            similaritySum += maxSimilarity;
        }
        double result = similaritySum / words1.size();
        usecaseCache.add(new Pair(originalUsecaseName1, originalUsecaseName2, result));
        hotUsecaseCache.add(new Pair(originalUsecaseName1, originalUsecaseName2, result));
        Logger.log("similarity: " + result);
        return result;
    }

    public static double getSimilarity(String word1, String word2) {
        double maxSimilarity = -1;
        for (String sense : SENSES) {
            double similarity = getStemSimilarity(word1, word2, sense);
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
            }
        }
        return maxSimilarity;
    }
}
