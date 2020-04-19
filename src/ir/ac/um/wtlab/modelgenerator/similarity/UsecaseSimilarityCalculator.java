/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.similarity;

import ir.ac.um.wtlab.modelgenerator.ModelGenerator;
import ir.ac.um.wtlab.modelgenerator.Actor;
import ir.ac.um.wtlab.modelgenerator.Usecase;
import ir.ac.um.wtlab.modelgenerator.*;
import ir.ac.um.wtlab.modelgenerator.util.Utils;
import java.util.*;

/**
 *
 * @author Home
 */
public class UsecaseSimilarityCalculator {

    private ModelGenerator modelGenerator;
    private static double semanticSimilarityWeight = 1.0;
    private static double relationalSimilarityWeight = 1.0;
    private static double contextSimilarityWeight = 1.0;
    private static double behaviorSimilarityWeight = 1.0;
    private static double conceptSimilarityWeight = 1.0;
    private static double subjectSimilarityWeight = 1.0;
    private static double extendedSimilarityWeight = 1.0;
    private static double extenderSimilarityWeight = 1.0;
    private static double includedSimilarityWeight = 1.0;
    private static double includerSimilarityWeight = 1.0;
    private static double generalSimilarityWeight = 1.0;
    private static double specificSimilarityWeight = 1.0;
    private static double actorSimilarityWeight = 1.0;
    private final static double MIN_SEMANTIC_SIMILARITY = 0.4;
    private double semanticSimilarity;
    private double relationalSimilarity;
    private double behaviorSimilarity;
    private double conceptSimilarity;
    private double subjectSimilarity;
    private double extendedSimilarity;
    private double extenderSimilarity;
    private double includedSimilarity;
    private double includerSimilarity;
    private double generalSimilarity;
    private double specificSimilarity;
    private double actorSimilarity;

    public UsecaseSimilarityCalculator(ModelGenerator modelGenerator) {
        this.modelGenerator = modelGenerator;
    }

    public double similarity(Usecase uc1, Usecase uc2) {
        double temp = behaviorSimilarity(uc1, uc2);
        setBehaviorSimilarity(temp);

        temp = conceptSimilarity(uc1, uc2, false);

        /*
         * temp = conceptSimilarity(uc1, uc2, true); if (temp != temp1) {
         * System.out.println("ConceptSimilarity of " + uc1.getName() + " " +
         * uc2.getName() + " : " + temp1); System.out.println("ConceptSimilarity
         * of " + uc1.getName() + " " + uc2.getName() + " By StopWords Removal:"
         * + temp);
        }
         */
        setConceptSimilarity(temp);

        temp = subjectSimilarity(uc1, uc2, false);

        /*
         * temp = subjectSimilarity(uc1, uc2, true); if (temp1 != temp) {
         * System.out.println("SubjectSimilarity of " +
         * uc1.getSoftwareCase().getName() + " " +
         * uc2.getSoftwareCase().getName() + " : " + temp1);
         * System.out.println("SubjectSimilarity of " +
         * uc1.getSoftwareCase().getName() + " " +
         * uc2.getSoftwareCase().getName() + " By StopWords Removal : " + temp);
        }
         */
        setSubjectSimilarity(temp);

        int nonZeroRelationalSimilarityCount = 0;
        temp = extendedSimilarity(uc1, uc2);
        if (temp > 0) {
            nonZeroRelationalSimilarityCount++;
        }
        setExtendedSimilarity(temp);

        temp = extenderSimilarity(uc1, uc2);
        if (temp > 0) {
            nonZeroRelationalSimilarityCount++;
        }
        setExtenderSimilarity(temp);

        temp = includedSimilarity(uc1, uc2);
        if (temp > 0) {
            nonZeroRelationalSimilarityCount++;
        }
        setIncludedSimilarity(temp);

        temp = includerSimilarity(uc1, uc2);
        if (temp > 0) {
            nonZeroRelationalSimilarityCount++;
        }
        setIncluderSimilarity(temp);

        temp = generalSimilarity(uc1, uc2);
        if (temp > 0) {
            nonZeroRelationalSimilarityCount++;
        }
        setGeneralSimilarity(temp);

        temp = specificSimilarity(uc1, uc2);
        if (temp > 0) {
            nonZeroRelationalSimilarityCount++;
        }
        setSpecificSimilarity(temp);

        temp = actorSimilarity(uc1, uc2);
        if (temp > 0) {
            nonZeroRelationalSimilarityCount++;
        }
        setActorSimilarity(temp);

        setSemanticSimilarity(
                (behaviorSimilarityWeight * behaviorSimilarity)
                + (conceptSimilarityWeight * conceptSimilarity)
                + (getSubjectSimilarityWeight() * subjectSimilarity));
        if (nonZeroRelationalSimilarityCount > 0) {
            setRelationalSimilarity(
                    ((extendedSimilarityWeight * extendedSimilarity)
                    + (extenderSimilarityWeight * extenderSimilarity)
                    + (generalSimilarityWeight * generalSimilarity)
                    + (specificSimilarityWeight * specificSimilarity)
                    + (includedSimilarityWeight * includedSimilarity)
                    + (includerSimilarityWeight * includerSimilarity)
                    + (actorSimilarityWeight * actorSimilarity)) / 7);
        } else {
            setRelationalSimilarity(0);
        }

        return (semanticSimilarityWeight * getSemanticSimilarity())
                + (relationalSimilarityWeight * getRelationalSimilarity());
    }

    private double semanticSimilarity(Usecase uc1, Usecase uc2) {
        setBehaviorSimilarity(behaviorSimilarity(uc1, uc2));
        setConceptSimilarity(conceptSimilarity(uc1, uc2, false));
        double similarity = ((behaviorSimilarityWeight * getBehaviorSimilarity())
                + (conceptSimilarityWeight * getConceptSimilarity())) / 2.0;
        return similarity;
    }

    private double extendedSimilarity(Usecase uc1, Usecase uc2) {
        ArrayList<Usecase> extendedUsecases1 = modelGenerator.findExtendedUsecases(uc1);
        ArrayList<Usecase> extendedUsecases2 = modelGenerator.findExtendedUsecases(uc2);
        UsecaseSimilarityCalculator simCalculator = new UsecaseSimilarityCalculator(modelGenerator);
        double result = simCalculator.semanticSimilarity(extendedUsecases1, extendedUsecases2);
        return result;
    }

    private double extenderSimilarity(Usecase uc1, Usecase uc2) {
        ArrayList<Usecase> extenderUsecases1 = modelGenerator.findExtenderUsecases(uc1);
        ArrayList<Usecase> extenderUsecases2 = modelGenerator.findExtenderUsecases(uc2);
        UsecaseSimilarityCalculator simCalculator = new UsecaseSimilarityCalculator(modelGenerator);
        double result = simCalculator.semanticSimilarity(extenderUsecases1, extenderUsecases2);
        return result;
    }

    private double includedSimilarity(Usecase uc1, Usecase uc2) {
        ArrayList<Usecase> includedUsecases1 = modelGenerator.findIncludedUsecases(uc1);
        ArrayList<Usecase> includedUsecases2 = modelGenerator.findIncludedUsecases(uc2);
        UsecaseSimilarityCalculator simCalculator = new UsecaseSimilarityCalculator(modelGenerator);
        double result = simCalculator.semanticSimilarity(includedUsecases1, includedUsecases2);
        return result;
    }

    private double includerSimilarity(Usecase uc1, Usecase uc2) {
        ArrayList<Usecase> includerUsecases1 = modelGenerator.findIncluderUsecases(uc1);
        ArrayList<Usecase> includerUsecases2 = modelGenerator.findIncluderUsecases(uc2);
        UsecaseSimilarityCalculator simCalculator = new UsecaseSimilarityCalculator(modelGenerator);
        double result = simCalculator.semanticSimilarity(includerUsecases1, includerUsecases2);
        return result;
    }

    private double generalSimilarity(Usecase uc1, Usecase uc2) {
        ArrayList<Usecase> generalUsecases1 = modelGenerator.findGeneralUsecases(uc1);
        ArrayList<Usecase> generalUsecases2 = modelGenerator.findGeneralUsecases(uc2);
        UsecaseSimilarityCalculator simCalculator = new UsecaseSimilarityCalculator(modelGenerator);
        double result = simCalculator.semanticSimilarity(generalUsecases1, generalUsecases2);
        return result;
    }

    private double specificSimilarity(Usecase uc1, Usecase uc2) {
        ArrayList<Usecase> specificUsecases1 = modelGenerator.findSpecificUsecases(uc1);
        ArrayList<Usecase> specificUsecases2 = modelGenerator.findSpecificUsecases(uc2);
        UsecaseSimilarityCalculator simCalculator = new UsecaseSimilarityCalculator(modelGenerator);
        double result = simCalculator.semanticSimilarity(specificUsecases1, specificUsecases2);
        return result;
    }

    private double actorSimilarity(Usecase uc1, Usecase uc2) {
        ArrayList<Actor> actors1 = modelGenerator.findActors(uc1);
        ArrayList<Actor> actors2 = modelGenerator.findActors(uc2);
        UsecaseSimilarityCalculator simCalculator = new UsecaseSimilarityCalculator(modelGenerator);
        double result = simCalculator.actorSimilarity(actors1, actors2);
        return result;
    }

    private double contextSimilarity(Usecase uc1, Usecase uc2) {
//        uc1.gets
        return 0;
    }

    private double behaviorSimilarity(Usecase uc1, Usecase uc2) {
        ArrayList<String> behaviors1 = modelGenerator.getUsecaseAllBehavioursWithSalt(uc1.getName());
        ArrayList<String> behaviors2 = modelGenerator.getUsecaseAllBehavioursWithSalt(uc2.getName());

        double result = 0.0;
        double weight1 = 1.0;
        final double WEIGHT_LOSS_RATIO = 0.9;
        int i = 0;
        int count = behaviors1.size() * behaviors2.size();
        loop:
        for (String behavior1 : behaviors1) {
            double weight2 = 1.0;
            for (String behavior2 : behaviors2) {
                double similarity = WordnetSimilarityCalculator.getStemSimilarity(behavior1, behavior2, "v");
//                System.out.println("similarity " + behavior1 + " " + behavior2 + " : " + similarity);
                result += (weight1 * weight2 * similarity);
                i++;
                if (i == 1 && similarity > 0.999) {
                    /*
                     * the similarity of the main behaviours is almost 1
                     * therefore do not consider secondary behaviours, because
                     * it reduces the overall similarity
                     */
                    count = 1;
                    break loop;
                }
                weight2 *= WEIGHT_LOSS_RATIO;
            }
            weight1 *= WEIGHT_LOSS_RATIO;
        }

        if (count > 0) {
            result /= count;
        }
        return result;
    }

    private double conceptSimilarity(Usecase uc1, Usecase uc2, boolean removeStopWords) {
        ArrayList<String> concepts1 = modelGenerator.getUsecaseNameConcepts(uc1);
        ArrayList<String> concepts2 = modelGenerator.getUsecaseNameConcepts(uc2);
        if (removeStopWords) {
            concepts1 = Utils.removeStopWords(concepts1);
            concepts2 = Utils.removeStopWords(concepts2);
        }
        if (concepts1.isEmpty() || concepts2.isEmpty()) {
            return 0.0;
        }
        return getBestMatchSimilarity(concepts1, concepts2, "n");
    }

    public double getBestMatchSimilarity(ArrayList<String> terms1, ArrayList<String> terms2, String sense) {
        double bestMatchSimilarity = -100;
        if (terms1.size() <= terms2.size()) {
            int[] matchIndexes = new int[terms1.size()];
            int[] bestMatchIndexes = new int[terms1.size()];
            Arrays.fill(matchIndexes, -1);
            bestMatchSimilarity = assignMatch(0, matchIndexes, bestMatchIndexes, bestMatchSimilarity, terms1, terms2, sense);
            int count = terms1.size();
            if (count > 0) {
                //bestMatchSimilarity /= count;
                bestMatchSimilarity = (2 * bestMatchSimilarity) / (terms1.size() + terms2.size());
            }
        } else {
            int[] matchIndexes = new int[terms2.size()];
            int[] bestMatchIndexes = new int[terms2.size()];
            Arrays.fill(matchIndexes, -1);
            bestMatchSimilarity = assignMatch(0, matchIndexes, bestMatchIndexes, bestMatchSimilarity, terms2, terms1, sense);
            int count = terms2.size();
            if (count > 0) {
                //bestMatchSimilarity /= count;
                bestMatchSimilarity = (2 * bestMatchSimilarity) / (terms1.size() + terms2.size());
            }
        }
        return bestMatchSimilarity;
//        double result = 0.0;
//        for (String term1 : terms1) {
//            for (String term2 : terms2) {
//                double similarity = WordnetSimilarityCalculator.getStemSimilarity(term1, term2, sense);
//                result += similarity;
//            }
//        }
//        int count = terms1.size() * terms2.size();
//        if (count > 0) {
//            result /= count;
//        }
//        return result;
    }

    private ArrayList<String> convertToArrayList(String[] items) {
        ArrayList<String> result = new ArrayList<String>(items.length);
        for (int i = 0; i < items.length; i++) {
            result.add(items[i]);
        }
        return result;
    }

    private double subjectSimilarity(Usecase uc1, Usecase uc2, boolean removeStopeWords) {
        String subject1 = modelGenerator.getSoftwareCase(uc1.getURI()).getName();
        String subject2 = modelGenerator.getSoftwareCase(uc2.getURI()).getName();
        subject1 = Utils.prepare(subject1);
        subject2 = Utils.prepare(subject2);
        String[] words1 = subject1.split(" ");
        String[] words2 = subject2.split(" ");

        ArrayList<String> terms1 = convertToArrayList(words1);
        ArrayList<String> terms2 = convertToArrayList(words2);
        if (removeStopeWords) {
            terms1 = Utils.removeStopWords(terms1);
            terms2 = Utils.removeStopWords(terms2);
        }
        if (terms1.isEmpty() || terms2.isEmpty()) {
            return 0.0;
        }

        return getBestMatchSimilarity(terms1, terms2, "n");
        /*
         * double result = 0.0; for (String word1 : words1) { for (String word2
         * : words2) { double similarity =
         * WordnetSimilarityCalculator.getStemSimilarity(word1, word2, "n");
         * result += similarity; } } int count = words1.length * words2.length;
         * if (count > 0) { result /= count; } // System.out.println("subject
         * similarity: " + subject1 + " " + subject2 + " is " + result); return
         * result;
         */
    }

    private double semanticSimilarity(ArrayList<Usecase> usecases1, ArrayList<Usecase> usecases2) {
        double result = 0.0;
        for (Usecase uc1 : usecases1) {
            for (Usecase uc2 : usecases2) {
                double similarity = semanticSimilarity(uc1, uc2);
                result += similarity;
            }
        }
        int count = usecases1.size() * usecases2.size();
        if (count > 0) {
            result /= count;
        }
        return result;
    }

    private double actorSimilarity(ArrayList<Actor> actors1, ArrayList<Actor> actors2) {
        double result = 0.0;
        for (Actor actor1 : actors1) {
            for (Actor actor2 : actors2) {
                double similarity = actorSimilarity(actor1, actor2);
                result += similarity;
            }
        }
        int count = actors1.size() * actors2.size();
        if (count > 0) {
            result /= count;
        }
        return result;
    }

    private double actorSimilarity(Actor actor1, Actor actor2) {
        ArrayList<String> concepts1 = modelGenerator.getActorNameConcepts(actor1);
        ArrayList<String> concepts2 = modelGenerator.getActorNameConcepts(actor2);
        if (concepts1.isEmpty() || concepts2.isEmpty()) {
            return 0.0;
        }
        return getBestMatchSimilarity(concepts1, concepts2, "n");

        /*
         * double result = 0.0; for (String concept1 : concepts1) { for (String
         * concept2 : concepts2) { double similarity =
         * WordnetSimilarityCalculator.getStemSimilarity(concept1, concept2,
         * "n"); result += similarity; } } int count = concepts1.size() *
         * concepts2.size(); if (count > 0) { result /= count; }
         *
         * return result;
         */
    }

    public static void setFiveWeights(double[] weights) {
        setSemanticSimilarityWeight(weights[0]);
        setRelationalSimilarityWeight(weights[1]);
        setSubjectSimilarityWeight(weights[2]);
        setBehaviorSimilarityWeight(weights[3]);
        setConceptSimilarityWeight(weights[4]);

        setExtendedSimilarityWeight(1.0);
        setExtenderSimilarityWeight(1.0);
        setGeneralSimilarityWeight(1.0);
        setSpecificSimilarityWeight(1.0);
        setIncludedSimilarityWeight(1.0);
        setIncluderSimilarityWeight(1.0);
        setActorSimilarityWeight(1.0);
    }

    /**
     * @return the semanticSimilarity
     */
    public double getSemanticSimilarity() {
        return semanticSimilarity;
    }

    /**
     * @param semanticSimilarity the semanticSimilarity to set
     */
    public void setSemanticSimilarity(double semanticSimilarity) {
        this.semanticSimilarity = semanticSimilarity;
    }

    /**
     * @return the relationalSimilarity
     */
    public double getRelationalSimilarity() {
        return relationalSimilarity;
    }

    /**
     * @param relationalSimilarity the relationalSimilarity to set
     */
    public void setRelationalSimilarity(double relationalSimilarity) {
        this.relationalSimilarity = relationalSimilarity;
    }

    /**
     * @return the behaviorSimilarity
     */
    public double getBehaviorSimilarity() {
        return behaviorSimilarity;
    }

    /**
     * @param behaviorSimilarity the behaviorSimilarity to set
     */
    public void setBehaviorSimilarity(double behaviorSimilarity) {
        this.behaviorSimilarity = behaviorSimilarity;
    }

    /**
     * @return the conceptSimilarity
     */
    public double getConceptSimilarity() {
        return conceptSimilarity;
    }

    /**
     * @param conceptSimilarity the conceptSimilarity to set
     */
    public void setConceptSimilarity(double conceptSimilarity) {
        this.conceptSimilarity = conceptSimilarity;
    }

    /**
     * @return the extendedSimilarity
     */
    public double getExtendedSimilarity() {
        return extendedSimilarity;
    }

    /**
     * @param extendedSimilarity the extendedSimilarity to set
     */
    public void setExtendedSimilarity(double extendedSimilarity) {
        this.extendedSimilarity = extendedSimilarity;
    }

    /**
     * @return the extenderSimilarity
     */
    public double getExtenderSimilarity() {
        return extenderSimilarity;
    }

    /**
     * @param extenderSimilarity the extenderSimilarity to set
     */
    public void setExtenderSimilarity(double extenderSimilarity) {
        this.extenderSimilarity = extenderSimilarity;
    }

    /**
     * @return the generalSimilarity
     */
    public double getGeneralSimilarity() {
        return generalSimilarity;
    }

    /**
     * @param generalSimilarity the generalSimilarity to set
     */
    public void setGeneralSimilarity(double generalSimilarity) {
        this.generalSimilarity = generalSimilarity;
    }

    /**
     * @return the specificSimilarity
     */
    public double getSpecificSimilarity() {
        return specificSimilarity;
    }

    /**
     * @param specificSimilarity the specificSimilarity to set
     */
    public void setSpecificSimilarity(double specificSimilarity) {
        this.specificSimilarity = specificSimilarity;
    }

    /**
     * @return the semanticSimilarityWeight
     */
    public static double getSemanticSimilarityWeight() {
        return semanticSimilarityWeight;
    }

    /**
     * @param semanticSimilarityWeight the semanticSimilarityWeight to set
     */
    public static void setSemanticSimilarityWeight(double weight) {
        semanticSimilarityWeight = weight;
    }

    /**
     * @return the relationalSimilarityWeight
     */
    public static double getRelationalSimilarityWeight() {
        return relationalSimilarityWeight;
    }

    /**
     * @param relationalSimilarityWeight the relationalSimilarityWeight to set
     */
    public static void setRelationalSimilarityWeight(double weight) {
        relationalSimilarityWeight = weight;
    }

    /**
     * @return the contextSimilarityWeight
     */
    public static double getContextSimilarityWeight() {
        return contextSimilarityWeight;
    }

    /**
     * @param contextSimilarityWeight the contextSimilarityWeight to set
     */
    public static void setContextSimilarityWeight(double weight) {
        contextSimilarityWeight = weight;
    }

    /**
     * @return the behaviorSimilarityWeight
     */
    public static double getBehaviorSimilarityWeight() {
        return behaviorSimilarityWeight;
    }

    /**
     * @param behaviorSimilarityWeight the behaviorSimilarityWeight to set
     */
    public static void setBehaviorSimilarityWeight(double weight) {
        behaviorSimilarityWeight = weight;
    }

    /**
     * @return the conceptSimilarityWeight
     */
    public static double getConceptSimilarityWeight() {
        return conceptSimilarityWeight;
    }

    /**
     * @param conceptSimilarityWeight the conceptSimilarityWeight to set
     */
    public static void setConceptSimilarityWeight(double weight) {
        conceptSimilarityWeight = weight;
    }

    /**
     * @return the extendedSimilarityWeight
     */
    public static double getExtendedSimilarityWeight() {
        return extendedSimilarityWeight;
    }

    /**
     * @param extendedSimilarityWeight the extendedSimilarityWeight to set
     */
    public static void setExtendedSimilarityWeight(double weight) {
        extendedSimilarityWeight = weight;
    }

    public static void setSubjectSimilarityWeight(double weight) {
        subjectSimilarityWeight = weight;
    }

    /**
     * @return the extenderSimilarityWeight
     */
    public static double getExtenderSimilarityWeight() {
        return extenderSimilarityWeight;
    }

    /**
     * @param extenderSimilarityWeight the extenderSimilarityWeight to set
     */
    public static void setExtenderSimilarityWeight(double weight) {
        extenderSimilarityWeight = weight;
    }

    /**
     * @return the generalSimilarityWeight
     */
    public static double getGeneralSimilarityWeight() {
        return generalSimilarityWeight;
    }

    /**
     * @param generalSimilarityWeight the generalSimilarityWeight to set
     */
    public static void setGeneralSimilarityWeight(double weight) {
        generalSimilarityWeight = weight;
    }

    /**
     * @return the specificSimilarityWeight
     */
    public static double getSpecificSimilarityWeight() {
        return specificSimilarityWeight;
    }

    /**
     * @param specificSimilarityWeight the specificSimilarityWeight to set
     */
    public static void setSpecificSimilarityWeight(double weight) {
        specificSimilarityWeight = weight;
    }

    /**
     * @return the includerSimilarityWeight
     */
    public static double getIncluderSimilarityWeight() {
        return includerSimilarityWeight;
    }

    /**
     * @param aIncluderSimilarityWeight the includerSimilarityWeight to set
     */
    public static void setIncluderSimilarityWeight(double weight) {
        includerSimilarityWeight = weight;
    }

    /**
     * @return the includedSimilarityWeight
     */
    public static double getIncludedSimilarityWeight() {
        return includedSimilarityWeight;
    }

    /**
     * @param aIncludedSimilarityWeight the includedSimilarityWeight to set
     */
    public static void setIncludedSimilarityWeight(double weight) {
        includedSimilarityWeight = weight;
    }

    /**
     * @return the includedSimilarity
     */
    public double getIncludedSimilarity() {
        return includedSimilarity;
    }

    /**
     * @param includedSimilarity the includedSimilarity to set
     */
    public void setIncludedSimilarity(double includedSimilarity) {
        this.includedSimilarity = includedSimilarity;
    }

    /**
     * @return the includerSimilarity
     */
    public double getIncluderSimilarity() {
        return includerSimilarity;
    }

    /**
     * @param includerSimilarity the includerSimilarity to set
     */
    public void setIncluderSimilarity(double includerSimilarity) {
        this.includerSimilarity = includerSimilarity;
    }

    /**
     * @return the actorSimilarity
     */
    public double getActorSimilarity() {
        return actorSimilarity;
    }

    /**
     * @param actorSimilarity the actorSimilarity to set
     */
    public void setActorSimilarity(double actorSimilarity) {
        this.actorSimilarity = actorSimilarity;
    }

    /**
     * @return the actorSimilarityWeight
     */
    public static double getActorSimilarityWeight() {
        return actorSimilarityWeight;
    }

    /**
     * @param aActorSimilarityWeight the actorSimilarityWeight to set
     */
    public static void setActorSimilarityWeight(double weight) {
        actorSimilarityWeight = weight;
    }

    /**
     * @return the subjectSimilarity
     */
    public double getSubjectSimilarity() {
        return subjectSimilarity;
    }

    /**
     * @param subjectSimilarity the subjectSimilarity to set
     */
    public void setSubjectSimilarity(double subjectSimilarity) {
        this.subjectSimilarity = subjectSimilarity;
    }

    /**
     * @return the subjectSimilarityWeight
     */
    public static double getSubjectSimilarityWeight() {
        return subjectSimilarityWeight;
    }

    ///////////farshad
    private boolean isOk(int index, int matchIndex, int[] matchIndexes) {
        for (int i = 0; i < index; i++) {
            if (matchIndexes[i] == matchIndex) {
                return false;
            }
        }
        return true;
    }

    private double computeSimilarity(ArrayList<String> words1, ArrayList<String> words2, String sense, int[] matchIndexes) {
        int length1 = words1.size();
        int length2 = words2.size();
        double result = 0;
        if (length1 <= length2) {
            for (int i = 0; i < matchIndexes.length; i++) {
                String word1 = words1.get(i);
                String word2 = words2.get(matchIndexes[i]);
                double max = -1;
                double similarity = 0;
                try {
                    similarity = WordnetSimilarityCalculator.getStemSimilarity(word1, word2, sense);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (similarity > max) {
                    max = similarity;
                }
                result += max;
            }
        } else {
            for (int i = 0; i < matchIndexes.length; i++) {
                String word1 = words1.get(matchIndexes[i]);
                String word2 = words2.get(i);
                double max = -1;
                double similarity = 0;
                try {
                    similarity = WordnetSimilarityCalculator.getStemSimilarity(word1, word2, sense);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (similarity > max) {
                    max = similarity;
                }
                result += max;
            }
        }
        return result;
    }

    private double assignMatch(int index, int[] matchIndexes, int[] bestMatchIndexes, double bestMatchSimilatity, ArrayList<String> words1, ArrayList<String> words2, String sense) {
        int length1 = words1.size();
        int length2 = words2.size();
        for (int i = 0; i < length2; i++) {
            if (isOk(index, i, matchIndexes)) {
                matchIndexes[index] = i;
                if (index == length1 - 1) {
                    double similarity = computeSimilarity(words1, words2, sense, matchIndexes);
                    if (similarity > bestMatchSimilatity) {
                        bestMatchSimilatity = similarity;
                        for (int j = 0; j < matchIndexes.length; j++) {
                            bestMatchIndexes[j] = matchIndexes[j];
                        }
                    }
                } else {
                    bestMatchSimilatity = assignMatch(index + 1, matchIndexes, bestMatchIndexes, bestMatchSimilatity, words1, words2, sense);
                }
            }
        }
        return bestMatchSimilatity;
    }

    public static void main(String[] args) {
        UsecaseSimilarityCalculator s = new UsecaseSimilarityCalculator(null);
        
    }
}
