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
public class UsecaseSimilarityCalculator2 {

    private ModelGenerator modelGenerator;
    private final static double w1 = 0.6;
    private final static double w2 = 0.4;

    private final static double wr = 0.5;
    private final static double wn = 0.3;
    private final static double wm = 0.2;

    private final static double vrAssociation = 0.4;
    private final static double vrGeneralization = 0.1;
    private final static double vrInclude = 0.3;
    private final static double vrExtend = 0.2;

    private final static double vnSubject = 0.7;
    private final static double vnObject = 0.3;

    public UsecaseSimilarityCalculator2(ModelGenerator modelGenerator) {
        this.modelGenerator = modelGenerator;
    }

    public double similarity(Usecase uc1, Usecase uc2) {
        return ((w1 * similarityFromFirstDimension(uc1, uc2))
                + (w2 * similarityFromSecondDimension(uc1, uc2)) / (w1 + w2));
    }

    private double similarityFromFirstDimension(Usecase uc1, Usecase uc2) {
        ArrayList<String> words1 = getAssociatedWords(uc1);
        ArrayList<String> words2 = getAssociatedWords(uc2);

        double matchedCount = 0;
        for (String word2 : words2) {
            for (String word1 : words1) {
                if (word2.equalsIgnoreCase(word1)) {
                    matchedCount++;
                }
            }
        }
        double similarity = matchedCount / words1.size();
        return similarity;
    }

    private double similarityFromSecondDimension(Usecase uc1, Usecase uc2) {
        double similarity = 0;

        ArrayList<Usecase> usecases = modelGenerator.getUsecases(uc1.getSoftwareCase());
        ArrayList<Actor> actors = modelGenerator.getActors(uc1.getSoftwareCase());
        int ucCount = usecases.size();
        int actorCount = actors.size();
        final int n = ucCount + actorCount;
        int m = 0;

        double term1 = 0, term2 = 0, term3 = 0;
        for (Usecase usecase : usecases) {
            int extendedUsecasesCount = usecase.getExtendedUsecases().size();
            int extenderUsecasesCount = usecase.getExtendedUsecases().size();

            int generalUsecasesCount = usecase.getGeneralUsecases().size();
            int specificUsecasesCount = usecase.getSpecificUsecases().size();

            //int includedUseCasesCount = usecase.getIncludedUsecases().size();
            //int includerUseCasesCount = usecase.getIncluderUsecases().size();
            m += extendedUsecasesCount + extenderUsecasesCount + generalUsecasesCount + specificUsecasesCount;
            term1 += (extendedUsecasesCount * vrExtend)
                    + (extenderUsecasesCount * vrExtend)
                    + (generalUsecasesCount * vrGeneralization)
                    + (specificUsecasesCount * vrGeneralization);
            term2 += (vnSubject * (extendedUsecasesCount + generalUsecasesCount));
            term2 += (vnObject * (extenderUsecasesCount + specificUsecasesCount));
        }

        for (Actor actor : actors) {
        }
        similarity = ((term1 * wr) + (term2 * wn)) / (n * m); // + (term3 * wm)
        return similarity;
    }

    private ArrayList<String> getAssociatedWords(Usecase uc) {
        ArrayList<String> words = new ArrayList<String>();
        String scName = uc.getSoftwareCase().getName();
        scName = Utils.prepare(scName);
        ArrayList<String> scNameWords = Utils.split(scName);
        for (String word : scNameWords) {
            if (!words.contains(word)) {
                words.add(word);
            }
        }
        ArrayList<Usecase> usecases = modelGenerator.getUsecases(uc.getSoftwareCase());
        for (Usecase usecase : usecases) {
            String ucName = usecase.getName();
            ucName = Utils.prepare(ucName);
            ArrayList<String> ucNameWords = Utils.split(ucName);
            for (String word : ucNameWords) {
                if (!words.contains(word)) {
                    words.add(word);
                }
            }
        }

        ArrayList<Actor> actors = modelGenerator.getActors(uc.getSoftwareCase());
        for (Actor actor : actors) {
            String actorName = actor.getName();
            actorName = Utils.prepare(actorName);
            ArrayList<String> actorNameWords = Utils.split(actorName);
            for (String word : actorNameWords) {
                if (!words.contains(word)) {
                    words.add(word);
                }
            }
        }

        return words;
    }

}
