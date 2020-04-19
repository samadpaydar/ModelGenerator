/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.similarity;

/**
 *
 * @author Home
 */
public class Pair {

    private String text1;
    private String text2;
    private double similarity;
    private String sense;

    public Pair(String text1, String text2, double similarity) {
        this(text1, text2, similarity, null);
    }

    public Pair(String text1, String text2, double similarity, String sense) {
        setText1(text1);
        setText2(text2);
        setSimilarity(similarity);
        setSense(sense);
    }

    public double getSimilarity() {
        return similarity;
    }

    /**
     * @param similarity the similarity to set
     */
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    /**
     * @return the text1
     */
    public String getText1() {
        return text1;
    }

    /**
     * @param text1 the text1 to set
     */
    public void setText1(String text1) {
        this.text1 = text1;
    }

    /**
     * @return the text2
     */
    public String getText2() {
        return text2;
    }

    /**
     * @param text2 the text2 to set
     */
    public void setText2(String text2) {
        this.text2 = text2;
    }

    /**
     * @return the sense
     */
    public String getSense() {
        return sense;
    }

    /**
     * @param sense the sense to set
     */
    public void setSense(String sense) {
        this.sense = sense;
    }
    
    public String toString() {
        return "[" + text1 + ", " + text2 + ", similarity:" + similarity + "] ";
    }

}
