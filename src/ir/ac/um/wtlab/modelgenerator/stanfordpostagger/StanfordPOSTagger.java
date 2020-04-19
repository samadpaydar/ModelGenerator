/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.stanfordpostagger;

import java.util.ArrayList;
import java.util.List;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.StringReader;

/**
 *
 * @author Home
 */
public class StanfordPOSTagger {

    public final static String[] POS_TAGS = {
        "NN", "VB", "NNP", "NNS", "JJ", "IN", "PRP$", ":",
        "JJS", "NNPS", "TO", "VBD", "VBN", ",", "CD", "MD",
        "DT", ".", ".$$.", "VBZ", "VBG", "CC", "RB", "PRP",
        "RBR", "WDT", "VBP", "RP", "POS", "``", "EX", "''",
        "WP", "JJR", "WRB", "$", "WP$", "-LRB-", "-RRB-",
        "PDT", "RBS", "FW", "UH", "SYM", "LS", "#"
    };
    private static StanfordPOSTagger instance = new StanfordPOSTagger();
    private MaxentTagger tagger;

    private StanfordPOSTagger() {
        try {
            String file = "stanford-postagger-2011-12-22\\" + "models\\bidirectional-distsim-wsj-0-18.tagger";// MaxentTagger.DEFAULT_DISTRIBUTION_PATH;
            tagger = new MaxentTagger(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getTagCount() {
        return POS_TAGS.length;
    }

    public static int getTagIndex(String tag) {
        for (int i = 0; i < POS_TAGS.length; i++) {
            if (POS_TAGS[i].equalsIgnoreCase(tag)) {
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<TaggedWord> tag(String str) {
        List<List<HasWord>> sentences = instance.tagger.tokenizeText(new StringReader(str));
        /*NOTE: I (farshad) have assumed that the use case label are just one sentence
         * therefore instead of iterating over sentences, I just return the first sentence
         */
        List<HasWord> sentence = sentences.get(0);
        ArrayList<TaggedWord> tSentence = instance.tagger.tagSentence(sentence);
        return tSentence;
    }
    /*
    private void posTag(String ucName) {
    ucName = SimilarityCalculator.prepare(ucName);
    ucName = ucName.toLowerCase();
    int prefixWordLength = 0;
    if(prefix != null) {
    ucName = prefix + ucName;
    prefixWordLength = prefix.split(" ").length;
    }
    ArrayList<TaggedWord> words = StanfordPOSTagger.tag(ucName);
    boolean hasVerbTag = false;
    for(int i=prefixWordLength; i<words.size(); i++) {
    TaggedWord word = words.get(i);
    String tag = word.tag();
    if(tag.startsWith("VB")) {
    hasVerbTag = true;
    int index = i-prefixWordLength;
    if(index!=0) {
    ucNamesWithVerbsInOtherWords += ucName;
    }
    verbTagCount[index]++;
    }
    System.out.println("\t" + word.word() + " [" + tag + "] ");
    }
    if(hasVerbTag) {
    usecaseWithVerbTag++;
    }
    totalUsecaseCount++;
    }
     */

    public static void main(String[] args) {
        ArrayList<TaggedWord> words = StanfordPOSTagger.tag("I want to save customer information.");
        for (TaggedWord word : words) {
            System.out.println(word.word() + " " + word.tag() + " " + word.value());
        }
    }
}
