/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator;

/**
 *
 * @author Home
 */
public class UsecaseMatch implements Comparable{
    private Usecase inputUsecase;
    private Usecase matchingUsecase;
    private double similarity;
    
    public UsecaseMatch(Usecase inputUsecase, Usecase matchingUsecase, double similarity) {
        setInputUsecase(inputUsecase);
        setMatchingUsecase(matchingUsecase);
        setSimilarity(similarity);
    }

    public int compareTo(Object other) {
        if(other instanceof UsecaseMatch) {
            if(similarity >= ((UsecaseMatch)other).getSimilarity())
                return 1;
            else return -1;
                        
        } else throw new IllegalArgumentException();
    }
    /**
     * @return the matchingUsecase
     */
    public Usecase getMatchingUsecase() {
        return matchingUsecase;
    }

    /**
     * @param matchingUsecase the matchingUsecase to set
     */
    public void setMatchingUsecase(Usecase matchingUsecase) {
        this.matchingUsecase = matchingUsecase;
    }

    /**
     * @return the similarity
     */
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
     * @return the inputUsecase
     */
    public Usecase getInputUsecase() {
        return inputUsecase;
    }

    /**
     * @param inputUsecase the inputUsecase to set
     */
    public void setInputUsecase(Usecase inputUsecase) {
        this.inputUsecase = inputUsecase;
    }
}
