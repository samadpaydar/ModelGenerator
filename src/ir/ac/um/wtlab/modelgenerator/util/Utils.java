/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.util;

import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author Farshad
 */
public abstract class Utils {

    public static String escape(String query) {
        query = query.replace("<", "%3C");
        query = query.replace(":", "%3A");
        query = query.replace("/", "%2F");
        query = query.replace(">", "%3E");
        query = query.replace("?", "%3F");
        query = query.replace(" ", "%20");
        query = query.replace("\"", "%22");
        query = query.replace("#", "%23");
        return query;
    }

    public static String correctURL(String url) {
        final String str1 = "http://wtlab.um.ac.ir/uml.owl#";
        final String str2 = "http://localhost:8080/openrdf-workbench/repositories/UWE_Samples_910531/explore?resource=uwe2rdf%3A";
        return url.replace(str1, str2);
    }

    public static String makeFirstLetterUppercase(String str) {
        if(str == null) return null;
        return (str.charAt(0) + "").toUpperCase() + str.substring(1);
    }
    
    /**
     * this method is used to break the usecase name to a sentence
     * for instance if the usecase name is 'DeleteAccount', it is 
     * converted to 'Delete Account'
     * @param str
     * @return 
     */
    public static String prepare(String str) {
        ArrayList<String> words = split(str);
        String result = "";
        int i = 0;
        for (; i < words.size() - 1; i++) {
            result += words.get(i) + " ";
        }
        if (i < words.size()) {
            result += words.get(i);
        }
        return result;
    }
    
    public static ArrayList<String> split(String name) {
        name = name.trim();
        ArrayList<String> result = new ArrayList<String>();
        String word = "";
        boolean prevCharIsLowerCase = false;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            //if (ch == ' ') {
            if (!Character.isLetter(ch)) {
                if (word.length() > 0) {
                    result.add(word);
                    word = "";
                }
            } else if (Character.isUpperCase(ch) && (i > 0 && prevCharIsLowerCase)) {
                if (word.length() > 0) {
                    result.add(word);
                }
                word = "" + ch;
            } else {
                word += ch;
            }
            prevCharIsLowerCase = Character.isLowerCase(ch);
        }
        if (word.length() > 0) {
            result.add(word);
        }
        return result;
    }

    public static String[] removeStopWords(String[] words) {
        int stopWordsCount = 0;
        boolean[] isStopWord = new boolean[words.length];
        for (int i = 0; i < words.length; i++) {
            if (stopWord(words[i])) {
                isStopWord[i] = true;
                stopWordsCount++;
            }
        }
        String[] result = new String[words.length - stopWordsCount];
        for (int i = 0, j = 0; i < words.length; i++) {
            if (!isStopWord[i]) {
                result[j] = words[i];
                j++;
            }
        }
        return result;
    }

    public static ArrayList<String> removeStopWords(ArrayList<String> words) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (stopWord(word)) {
                System.out.println("STOP_WORD: " + word);
            } else {
                result.add(word);
            }
        }
        return result;
    }
    
    public static boolean stopWord(String word) {
        final String[] STOP_WORDS = {
            "a", "about", "above", "across", "after", "again", "against", "all",
            "almost", "alone", "along", "already", "also", "although", "always",
            "among", "an", "and", "another", "any", "anybody", "anyone", "anything",
            "anywhere", "are", "area", "areas", "around", "as", "ask", "asked",
            "asking", "asks", "at", "away", "b", "back", "backed", "backing",
            "backs", "be", "because", "became", "become", "becomes", "been",
            "before", "began", "behind", "being", "beings", "best", "better",
            "between", "big", "both", "but", "by", "c", "came", "can", "cannot",
            "case", "cases", "certain", "certainly", "clear", "clearly", "come",
            "could", "d", "did", "differ", "different", "differently", "do", "does", "done", "down", "downed", "downing", "downs", "during", "e", "each", "early", "either", "end", "ended", "ending", "ends", "enough", "even", "evenly", "ever", "every", "everybody", "everyone", "everything", "everywhere", "f", "face", "faces", "fact", "facts", "far", "felt", "few", "find", "finds", "first", "for", "four", "from", "full", "fully", "further", "furthered", "furthering", "furthers", "g", "gave", "general", "generally", "get", "gets", "give", "given", "gives", "go", "going", "good", "goods", "got", "great", "greater", "greatest", "group", "grouped", "grouping", "groups", "h", "had", "has", "have", "having", "he", "her", "herself", "here", "high", "higher", "highest", "him", "himself", "his", "how", "however", "i", "if", "important", "in", "interest", "interested", "interesting", "interests", "into", "is", "it", "its", "itself", "j", "just", "k", "keep", "keeps", "kind", "knew", "know", "known", "knows", "l", "large", "largely", "last", "later", "latest", "least", "less", "let", "lets", "like", "likely", "long", "longer", "longest", "m", "made", "make", "making", "man", "many", "may", "me", "member", "members", "men", "might", "more", "most", "mostly", "mr", "mrs", "much", "must", "my", "myself", "n", "necessary", "need", "needed", "needing", "needs", "never", "new", "newer", "newest", "next", "no", "non", "not", "nobody", "noone", "nothing", "now", "nowhere", "number", "numbered", "numbering", "numbers", "o", "of", "off", "often", "old", "older", "oldest", "on", "once", "one", "only", "open", "opened", "opening", "opens", "or", "order", "ordered", "ordering", "orders", "other", "others", "our", "out", "over", "p", "part", "parted", "parting", "parts", "per", "perhaps", "place", "places", "point", "pointed", "pointing", "points", "possible", "present", "presented", "presenting", "presents", "problem", "problems", "put", "puts", "q", "quite", "r", "rather", "really", "right", "room", "rooms", "s", "said", "same", "saw", "say", "says", "second", "seconds", "see", "seem", "seemed", "seeming", "seems", "sees", "several", "shall", "she", "should", "show", "showed", "showing", "shows", "side", "sides", "since", "small", "smaller", "smallest", "so", "some", "somebody", "someone", "something", "somewhere", "state", "states", "still", "such", "sure", "t", "take", "taken", "than", "that", "the", "their", "them", "then", "there", "therefore", "these", "they", "thing", "things", "think", "thinks", "this", "those", "though", "thought", "thoughts", "three", "through", "thus", "to", "today", "together", "too", "took", "toward", "turn", "turned", "turning", "turns", "two", "u", "under", "until", "up", "upon", "us", "use", "uses", "used", "v", "very", "w", "want", "wanted", "wanting", "wants", "was", "way", "ways", "we", "well", "wells", "went", "were", "what", "when", "where", "whether", "which", "while", "who", "whole", "whose", "why", "will", "with", "within", "without", "work", "worked", "working", "works", "would", "x", "y", "year", "years", "yet", "you", "young", "younger", "youngest", "your", "yours", "z"
        };
        int index = Arrays.binarySearch(STOP_WORDS, word.toLowerCase());
        return index >= 0;
    }

    

    
    
}
