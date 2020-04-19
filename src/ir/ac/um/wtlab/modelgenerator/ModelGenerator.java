/* To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator;

import com.jspell.domain.JSpellErrorInfo;
import ir.ac.um.wtlab.modelgenerator.lod.*;
import org.openrdf.rio.RDFFormat;
import org.openrdf.repository.RepositoryConnection;
import ir.ac.um.wtlab.modelgenerator.repositorymanager.RepositoryManager;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import edu.stanford.nlp.ling.TaggedWord;
import javax.swing.text.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import ir.ac.um.wtlab.modelgenerator.util.Logger;
import ir.ac.um.wtlab.modelgenerator.util.Utils;
import ir.ac.um.wtlab.modelgenerator.stanfordpostagger.*;
import ir.ac.um.wtlab.modelgenerator.similarity.*;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import ir.ac.um.wtlab.modelgenerator.swooglesearch.*;
import java.awt.Color;
import org.openrdf.model.Value;
import ir.ac.um.wtlab.modelgenerator.util.SpellChecker;
import ir.ac.um.wtlab.modelgenerator.util.Utils;

/**
 *
 * @author Home
 */
public class ModelGenerator {

    private String salt = "I want to ";
    private SoftwareCase inputSC;
    private SoftwareCase[] currentSCs;
    private String inputUsecaseName;
    private RepositoryConnection modelRepositoryConnection;
    private RepositoryConnection ontologyRepositoryConnection;
    private RepositoryConnection cycRepositoryConnection;
    private StyledDocument doc;

    public ModelGenerator(RepositoryConnection modelRepositoryConnection, RepositoryConnection ontologyRepositoryConnection, RepositoryConnection cycRepositoryConnection) {
        setModelRepositoryConnection(modelRepositoryConnection);
        setOntologyRepositoryConnection(ontologyRepositoryConnection);
        setCycRepositoryConnection(cycRepositoryConnection);
    }

    public void spellCheck() {
        SpellChecker spellChecker = new SpellChecker();
        try {
            String query = "SELECT DISTINCT ?element ?name WHERE { "
                    + " ?element uwe2rdf:name ?name ."
                    + " }";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            int total = 0;
            int erroneous = 0;
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String element = bindingSet.getValue("element").stringValue();
                String name = bindingSet.getValue("name").stringValue();
                total++;
                name = Utils.prepare(name);
                JSpellErrorInfo error = spellChecker.check(name);
                if (error != null) {
                    erroneous++;
                    System.out.println(erroneous + ". " + " element: " + element + " name: " + name);
                    spellChecker.print(error);
                }
            }
            System.out.println("There was " + erroneous + " errors in total of " + total);
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getUsecaseBehaviours(String ucName, boolean secondary) {
        ArrayList<String> behaviours = new ArrayList<String>();
        ucName = Utils.prepare(ucName);
        String originalUCName = ucName;
        ucName = ucName.toLowerCase();
        int prefixWordLength = 0;
        if (getSalt() != null) {
            ucName = getSalt() + ucName;
            prefixWordLength = getSalt().split(" ").length;
        }
        ArrayList<TaggedWord> words = StanfordPOSTagger.tag(ucName);
        for (int i = prefixWordLength; i < words.size(); i++) {
            TaggedWord word = words.get(i);
            String tag = word.tag();
            if (tag.startsWith("VB")) {
                behaviours.add(word.word());
            }
        }
        /*
         * I added the following block because of examples like 'Add Comment'
         * and 'Comment Movie' In such cases, it seems that the behavior is not
         * in the word which is tagged as verb, instead it is in the object of
         * this verb. In this example, it can be said that the behavior of the
         * first use case is in 'Comment' not in 'Add' My IDEA: remove the first
         * verb-tagged word and redo POS tagging and repeat the process with the
         * next verb-tagged word
         */
        String[] temp = originalUCName.split(" ");
        String secondaryUCName = "";
        if (temp.length > 1) {
            for (int i = 1; i < temp.length; i++) {
                secondaryUCName += temp[i] + " ";
            }
            ArrayList<String> secondaryBehaviours = getUsecaseBehaviours(secondaryUCName, true);
            for (String secondaryBehaviour : secondaryBehaviours) {
                boolean contains = false;
                for (String behaviour : behaviours) {
                    if (behaviour.equalsIgnoreCase(secondaryBehaviour)) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    behaviours.add(secondaryBehaviour);
                }
            }
        }

        /*
         * if (behaviours.isEmpty() && !secondary) {
         * behaviours.add(originalUCName.split(" ")[0]); }
         */
//        if (!secondary) {
//            System.out.println(">>> " + ucName);
//            System.out.println(behaviours);
//        }
        return behaviours;
    }

    public ArrayList<String> getUsecaseAllBehavioursWithSalt(String ucName) {
        ArrayList<String> behaviours = new ArrayList<String>();
        ucName = Utils.prepare(ucName);
        ucName = ucName.toLowerCase();
        int prefixWordLength = 0;

        if (getSalt() != null) {
            ucName = getSalt() + ucName;
            prefixWordLength = getSalt().split(" ").length;
        }
        ArrayList<TaggedWord> words = StanfordPOSTagger.tag(ucName);
        for (int i = prefixWordLength; i < words.size(); i++) {
            TaggedWord word = words.get(i);
            String tag = word.tag();
            if (tag.startsWith("VB")) {
                if (i < words.size() - 1) {
                    TaggedWord tempWord = words.get(i + 1);
                    String tempTag = tempWord.tag();
                    if (tempTag.equalsIgnoreCase("RP") && tempWord.word().equalsIgnoreCase("up")) {
                        behaviours.add(word.word() + " " + tempWord.word());
                        continue;
                    }
                }
                behaviours.add(word.word());
            }
        }
        return behaviours;
    }

    public ArrayList<String> getUsecaseAllBehavioursWithoutSalt(String ucName) {
        ArrayList<String> behaviours = new ArrayList<String>();
        ucName = Utils.prepare(ucName);
        ucName = ucName.toLowerCase();
        //ucName = Utils.makeFirstLetterUppercase(ucName);
        ArrayList<TaggedWord> words = StanfordPOSTagger.tag(ucName);
        for (int i = 0; i < words.size(); i++) {
            TaggedWord word = words.get(i);
            String tag = word.tag();
            if (tag.startsWith("VB")) {
                behaviours.add(word.word());
            }
        }
        return behaviours;
    }

    public ArrayList<String> getUsecaseFirstWordAsBehavior(String ucName) {
        ArrayList<String> behaviours = new ArrayList<String>();
        ucName = Utils.prepare(ucName);
        ucName = ucName.toLowerCase();
        //ucName = Utils.makeFirstLetterUppercase(ucName);
        ArrayList<TaggedWord> words = StanfordPOSTagger.tag(ucName);
        TaggedWord word = words.get(0);
        behaviours.add(word.word());
        return behaviours;
    }

    private Usecase findUsecase(String scName, String ucName) {
        Usecase uc = null;
        try {
            String query = "SELECT DISTINCT ?usecase WHERE { "
                    + " ?usecase uwe2rdf:hasSoftwareCase ?sc ."
                    + " ?sc uwe2rdf:name \"" + scName + "\" ."
                    + " ?usecase rdf:type uwe2rdf:UseCase . "
                    + " ?usecase uwe2rdf:name \"" + ucName + "\" "
                    + " }";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String usecaseURI = bindingSet.getValue("usecase").stringValue();
                uc = createUsecase(scName, usecaseURI, ucName);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uc;
    }

    public Object[][] calculateUsecaseSimilarities(String scName, String ucName) {
        Usecase uc = findUsecase(scName, ucName);
        if (uc == null) {
            throw new RuntimeException("UseCase not found. SC: " + scName + " UC: " + ucName);
        }
        ArrayList<Usecase> otherUCs = findUsecasesOfOtherSoftwareCases(uc);
        Object[][] result = new Object[otherUCs.size()][15];
        for (int i = 0; i < otherUCs.size(); i++) {
            Usecase otherUC = otherUCs.get(i);
            UsecaseSimilarityCalculator calculator = new UsecaseSimilarityCalculator(this);
            result[i][0] = otherUC.getSoftwareCase().getName();
            result[i][1] = otherUC.getName();
//            if (otherUC.getName().contains("Check PIN")) {
//            System.out.println("GGGGGG");
//            }
            //result[i][2] = calculator.similarity(uc, otherUC);
            result[i][2] = calculator.similarity(uc, otherUC);
            result[i][3] = calculator.getSemanticSimilarity();
            result[i][4] = calculator.getRelationalSimilarity();
            result[i][5] = calculator.getBehaviorSimilarity();
            result[i][6] = calculator.getConceptSimilarity();
            result[i][7] = calculator.getSubjectSimilarity();
            result[i][8] = calculator.getExtenderSimilarity();
            result[i][9] = calculator.getExtendedSimilarity();
            result[i][10] = calculator.getGeneralSimilarity();
            result[i][11] = calculator.getSpecificSimilarity();
            result[i][12] = calculator.getIncludedSimilarity();
            result[i][13] = calculator.getIncluderSimilarity();
            result[i][14] = calculator.getActorSimilarity();
        }
        sort(result);
        return result;
    }

    /**
     * computes similatiry of the use cases based on the metric introduced in
     * the following paper RETRIEVING USE CASE DIAGRAM WITH CASE-BASED REASONING
     * APPROACH, B. SRISURA, Dr. J. DAENGDEJ, Journal of Theoretical and Applied
     * Information Technology
     *
     * @param scName
     * @param ucName
     * @return
     */
    public Object[][] calculateUsecaseSimilaritiesByOtherMetric(String scName, String ucName) {
        Usecase uc = findUsecase(scName, ucName);
        ArrayList<Usecase> otherUCs = findUsecasesOfOtherSoftwareCases(uc);
        Object[][] result = new Object[otherUCs.size()][15];
        for (int i = 0; i < otherUCs.size(); i++) {
            Usecase otherUC = otherUCs.get(i);
            UsecaseSimilarityCalculator2 calculator = new UsecaseSimilarityCalculator2(this);
            result[i][0] = otherUC.getSoftwareCase().getName();
            result[i][1] = otherUC.getName();
            result[i][2] = calculator.similarity(uc, otherUC);
        }
        sort(result);
        return result;
    }

    public ArrayList<Usecase> findUsecasesOfOtherSoftwareCases(Usecase uc) {
        ArrayList<Usecase> result = new ArrayList<Usecase>();
        try {
            /*
             * String query = "SELECT DISTINCT ?scName ?usecase ?usecaseName
             * WHERE { " + " ?usecase rdf:type uwe2rdf:UseCase . " + " ?usecase
             * uwe2rdf:hasSoftwareCase ?sc ." + " ?sc uwe2rdf:name ?scName ." +
             * " ?usecase uwe2rdf:name ?usecaseName ." + " FILTER (str(?usecase)
             * != \"" + uc.getURI() + "\")" + " }";
             */ String query = "SELECT DISTINCT ?scName ?usecase ?usecaseName WHERE { "
                    + " ?usecase rdf:type uwe2rdf:UseCase . "
                    + " ?usecase uwe2rdf:hasSoftwareCase ?sc ."
                    + " ?sc uwe2rdf:name ?scName ."
                    + " ?usecase uwe2rdf:name ?usecaseName ."
                    + " FILTER (?scName != \"" + uc.getSoftwareCase().getName() + "\")"
                    + " }";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String usecaseURI = bindingSet.getValue("usecase").stringValue();
                String usecaseName = bindingSet.getValue("usecaseName").stringValue();
                String scName = bindingSet.getValue("scName").stringValue();
                uc = createUsecase(scName, usecaseURI, usecaseName);
                result.add(uc);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public long getNumberOfTriples() {
        long tripleCount = 0;
        try {
            String query = " SELECT ?sub ?pred ?obj WHERE {"
                    + " ?sub ?pred ?obj "
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                tripleCount++;
                String subject = bindingSet.getValue("sub").stringValue();
                String predicate = bindingSet.getValue("pred").stringValue();
                String object = bindingSet.getValue("obj").stringValue();
                System.out.println(tripleCount + ". " + subject + " " + predicate + " " + object);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripleCount;
    }

    public String getCentralBufferNodeType(String elementURI) {
        String result = null;
        try {
            String query = " SELECT ?type WHERE {"
                    + " <" + elementURI + "> rdf:type uwe2rdf:CentralBufferNode ."
                    + " <" + elementURI + "> uwe2rdf:type ?type "
                    + "}";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                result = bindingSet.getValue("type").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isInputPin(String elementURI) {
        try {
            String query = " SELECT ?type WHERE {"
                    + " <" + elementURI + "> rdf:type ?type "
                    + "}";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String type = bindingSet.getValue("type").stringValue();
                return type.contains("InputPin");
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getCorrespondingOutputPin(String inputPinURI) {
        String result = null;
        try {
            String query = " SELECT ?source WHERE {"
                    + " ?flow uwe2rdf:hasTarget <" + inputPinURI + "> . "
                    + " ?flow uwe2rdf:hasSource ?source ."
                    + " ?source rdf:type uwe2rdf:OutputPin"
                    + "}";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                result = bindingSet.getValue("source").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void step0() {
        //to annotate all activity diagrams
        ActivityDiagramAnnotator annotator = new ActivityDiagramAnnotator(this);
        try {
            String query = " SELECT ?softwareCase ?activity ?activityName WHERE {"
                    + " ?activity uwe2rdf:hasSoftwareCase ?softwareCase ."
                    + " ?activity rdf:type uwe2rdf:Activity ."
                    + " ?activity uwe2rdf:name ?activityName ."
                    + "}";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            String log = "";
            long sum = 0L;
            int count = 0;
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String scURI = bindingSet.getValue("softwareCase").stringValue();
                String acURI = bindingSet.getValue("activity").stringValue();
                String acName = bindingSet.getValue("activityName").stringValue();
                //if (acName.equalsIgnoreCase("CreateContact")) {
                long start = System.currentTimeMillis();
                annotator.annotateActivityDiagram(scURI, acURI);
                long end = System.currentTimeMillis();
                sum += (end - start);
                count++;
                log += "Annotation took:" + (end - start) + " ms for " + acName + " uri: " + acURI + "\n";
                //}
            }
            //System.out.println("Annotation Completed.");
            //System.out.println("log:");
            //System.out.println(log);
            double average = (sum * 1.0) / count;
            System.out.println("average activity diagram annotation time: " + average + " ms");
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isClassProperty(String uri) {
        try {
            String query = "ASK { <" + uri + "> rdf:type uwe2rdf:Property }";
            boolean result = RepositoryManager.executeBooleanQuery(getModelRepositoryConnection(), query);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<String> getPropertyOwnerClassURIs(String propertyURI) {
        ArrayList<String> classURIs = new ArrayList<String>();
        try {
            String query = " SELECT ?class WHERE {"
                    + " ?class uwe2rdf:containsAttribute <" + propertyURI + "> ."
                    + " ?class rdf:type uwe2rdf:Class ."
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String classURI = bindingSet.getValue("class").stringValue();
                classURIs.add(classURI);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classURIs;
    }

    public void step1(JTable table) {
        String[][] existingUsecasesInfo = getExistingUseCasesInfo();
        /*
         * to-do: I have considered only the first behvaiour of the input
         * usecase it is possible that other behaviours are better
         */
        String inputUCBehaviours = getUsecaseBehaviours(getInputUsecaseName(), false).get(0);
        Object[][] cellValues = new Object[existingUsecasesInfo.length][6];
        ActivityDiagramAnnotator detector = new ActivityDiagramAnnotator(this);
        for (int i = 0; i < existingUsecasesInfo.length; i++) {
            String scName = existingUsecasesInfo[i][0];
            String ucName = existingUsecasesInfo[i][1];
            ArrayList<String> ucBehaviours = getUsecaseBehaviours(ucName, false);
            String temp = "";
            double maxBehaviourSimilarity = 0;
            for (String behaviour : ucBehaviours) {
                double behaviourSimilarity = WordnetSimilarityCalculator.getStemSimilarity(behaviour, inputUCBehaviours, "v");
                if (behaviourSimilarity > maxBehaviourSimilarity) {
                    maxBehaviourSimilarity = behaviourSimilarity;
                }
                temp += behaviour + " - ";
            }

            String acURI = getActivityDiagramByUsecaseName(scName, ucName);

            Object[] row = {scName, ucName, acURI, new Double(0), temp, new Double(maxBehaviourSimilarity)};
            insert(cellValues, row, i);
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (int i = 0; i < cellValues.length; i++) {
            Object[] row = {new Integer(i + 1), cellValues[i][0],
                cellValues[i][1], cellValues[i][2], cellValues[i][3], cellValues[i][4], cellValues[i][5]
            };
            model.addRow(row);
        }
    }

    public ArrayList<String> getActorNameConcepts(Actor actor) {
        return getWordsTaggedAsNameWithSalt(actor.getName());
    }

    public ArrayList<String> getUsecaseNameConcepts(Usecase uc) {
        return getWordsTaggedAsNameWithSalt(uc.getName());
    }

    public ArrayList<String> getWordsTaggedAsNameWithSalt(String text) {
        ArrayList<String> result = new ArrayList<String>();
        text = Utils.prepare(text);
        text = text.toLowerCase();

        int prefixWordLength = 0;
        if (getSalt() != null) {
            text = getSalt() + text;
            prefixWordLength = getSalt().split(" ").length;
        }

        ArrayList<TaggedWord> words = StanfordPOSTagger.tag(text);
        for (int i = prefixWordLength; i < words.size(); i++) {
            TaggedWord word = words.get(i);
            String tag = word.tag();
            if (tag.startsWith("NN")) {// || tag.equalsIgnoreCase("JJ")) { //nouns and adjectives 
                /**
                 * I added adjectives because in use case 'view personal
                 * details', the personal is tagged as adjective, however it is
                 * important in determining the concept similarity
                 */
                result.add(word.word());
            }
        }
        return result;
    }

    public ArrayList<String> getWordsTaggedAsNameWithoutSalt(String text) {
        ArrayList<String> result = new ArrayList<String>();
        text = Utils.prepare(text);
        text = text.toLowerCase();

        ArrayList<TaggedWord> words = StanfordPOSTagger.tag(text);
        for (int i = 0; i < words.size(); i++) {
            TaggedWord word = words.get(i);
            String tag = word.tag();
            if (tag.startsWith("NN")) {// || tag.equalsIgnoreCase("JJ")) { //nouns and adjectives 
                /**
                 * I added adjectives because in use case 'view personal
                 * details', the personal is tagged as adjective, however it is
                 * important in determining the concept similarity
                 */
                result.add(word.word());
            }
        }
        return result;
    }

    public ArrayList<String> getWordsTaggedAsNameOrAdjective(String text) {
        ArrayList<String> result = new ArrayList<String>();
        text = Utils.prepare(text);
        text = text.toLowerCase();

        int prefixWordLength = 0;
        if (getSalt() != null) {
            text = getSalt() + text;
            prefixWordLength = getSalt().split(" ").length;
        }

        ArrayList<TaggedWord> words = StanfordPOSTagger.tag(text);
        for (int i = prefixWordLength; i < words.size(); i++) {
            TaggedWord word = words.get(i);
            String tag = word.tag();
            if (tag.startsWith("NN") || tag.equalsIgnoreCase("JJ")) { //nouns and adjectives 
                /**
                 * I added adjectives because in use case 'view personal
                 * details', the personal is tagged as adjective, however it is
                 * important in determining the concept similarity
                 */
                result.add(word.word());
            }
        }
        return result;
    }

    protected void addStylesToDocument() {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.BLUE);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);

    }

    private void log(String text, String style) {
        try {
            doc.insertString(doc.getLength(), text, doc.getStyle(style));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void step2(String inputUsecaseName, String inputSCName, String recomUsecaseName, String recomSCName, JTextPane textPane) {
        String inputSoftwareCaseURI = getSoftwareCaseURI(inputSCName);
        String adapteeSoftwareCaseURI = getSoftwareCaseURI(recomSCName);
        String inputUsecaseURI = getUsecaseURI(inputUsecaseName);
        String adapteeActivityDiagramURI = getActivityDiagramURI(recomUsecaseName);
        ActivityDiagramAdapter adapter = new ActivityDiagramAdapter(this, textPane);
        adapter.adapt(inputSoftwareCaseURI, inputUsecaseURI, adapteeSoftwareCaseURI, adapteeActivityDiagramURI);
        /*
         * String acName = recomUsecaseName; this.textPane = textPane; this.doc
         * = this.textPane.getStyledDocument(); addStylesToDocument(); try {
         * log("Adaptation process started.\n\nInput Usecase: ", "regular");
         * log(inputUsecaseName + "\n", "bold"); log("Input Activity Diagram: ",
         * "regular"); log(acName + "\n\n", "bold"); } catch (Exception e) {
         * e.printStackTrace(); } // ArrayList<UMLClass> classes1 =
         * resolve(acName, true); // ArrayList<UMLClass> classes2 =
         * resolve(inputUsecaseName, false); String activityDiagramURI =
         * getActivityDiagramByUsecaseName(recomSCName, recomUsecaseName); if
         * (activityDiagramURI != null) { adaptAnnotations(inputSCName,
         * activityDiagramURI, acName, inputUsecaseName); } else { log("No
         * activity diagram.\n\n", "regular"); } /* final int RESULT_COUNT = 3;
         * //check: if the concepts exist in the repository, do not search
         * sqoogle //if not, search swoogle OntologySearcher searcher = new
         * OntologySearcher(concepts, RESULT_COUNT); ArrayList<Integer>
         * ontologyIds = searcher.startSearch();
         */
    }

    private void logAnnotation(Annotation annotation) {
        log("Adapting annotation '", "regular");
        log(annotation.getLabel(), "bold");
        log("'\nannotatedWord:" + annotation.getAnnotatedWord(), "regular");
        log("\ttarget:" + annotation.getTarget(), "regular");
        log("\ttype:" + annotation.getType() + "\n", "regular");
    }

    public boolean hasAssociation(String classURI1, String classURI2) {
        boolean result = false;
        try {
            String query = " SELECT ?attrib WHERE {"
                    + " <" + classURI1 + "> rdf:type uwe2rdf:Class  ."
                    + " <" + classURI2 + "> rdf:type uwe2rdf:Class  ."
                    + " <" + classURI1 + "> uwe2rdf:containsAttribute ?attrib ."
                    + " ?attrib rdf:type uwe2rdf:Property ."
                    + " ?attrib uwe2rdf:type <" + classURI2 + "> ."
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String attrib = bindingSet.getValue("attrib").stringValue();
                result = true;
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public UMLAttribute createAttribute(String attributeURI) {
        UMLAttribute result = null;
        try {
            String query = " SELECT ?type ?visibility ?name WHERE {"
                    + " <" + attributeURI + "> uwe2rdf:type ?type ;"
                    + " uwe2rdf:visibility ?visibility ;"
                    + " uwe2rdf:name ?name ."
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String type = bindingSet.getValue("type").stringValue();
                String visibility = bindingSet.getValue("visibility").stringValue();
                String name = bindingSet.getValue("name").stringValue();
                result = new UMLAttribute(name, attributeURI);
                result.setType(type);
                result.setVisibility(visibility);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getAttributeTypeClassName(String attribute) {
        String result = null;
        try {
            String query = " SELECT ?className WHERE {"
                    + " <" + attribute + "> uwe2rdf:type ?class  ."
                    + " ?class rdf:type uwe2rdf:Class ."
                    + " ?class uwe2rdf:name ?className "
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                result = bindingSet.getValue("className").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getAttributeTypeClassURI(String attribute) {
        String result = null;
        try {
            String query = " SELECT ?class WHERE {"
                    + " <" + attribute + "> uwe2rdf:type ?class  ."
                    + " ?class rdf:type uwe2rdf:Class ."
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                result = bindingSet.getValue("class").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getAttributeOwnerClassName(String attribute) {
        String result = null;
        try {
            String query = " SELECT ?className WHERE {"
                    + " ?class uwe2rdf:containsAttribute <" + attribute + "> ."
                    + " ?class rdf:type uwe2rdf:Class ."
                    + " ?class uwe2rdf:name ?className "
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                result = bindingSet.getValue("className").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getAttributeOwnerClassURI(String attribute) {
        String result = null;
        try {
            String query = " SELECT ?class WHERE {"
                    + " ?class uwe2rdf:containsAttribute <" + attribute + "> ."
                    + " ?class rdf:type uwe2rdf:Class ."
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                result = bindingSet.getValue("class").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void sort(Object[][] values) {
        for (int i = 1; i < values.length; i++) {
            Object[] key = new Object[values[i].length];
            for (int j = 0; j < values[i].length; j++) {
                key[j] = values[i][j];
            }
            insert(values, key, i);
        }
    }

    private void insert(Object[][] cellValues, Object[] row, int i) {
        int j = i - 1;
        for (; j >= 0; j--) {
            double sim1 = ((Double) row[2]).doubleValue();
            double sim2 = ((Double) cellValues[j][2]).doubleValue();
            if (sim1 > sim2) {
                for (int k = 0; k < row.length; k++) {
                    cellValues[j + 1][k] = cellValues[j][k];
                }
            } else {
                break;
            }
        }
        for (int k = 0; k < row.length; k++) {
            cellValues[j + 1][k] = row[k];
        }
    }

    public ArrayList<Actor> findActors(String scName, String ucName) {
        ArrayList<Actor> result = new ArrayList<Actor>();
        String query = "";
        try {
            query = "SELECT ?actor ?actorName WHERE {"
                    + " ?actor rdf:type uwe2rdf:Actor ."
                    + "?actor uwe2rdf:hasSoftwareCase ?sc ."
                    + "?sc uwe2rdf:name \"" + scName + "\" ."
                    + "?actor uwe2rdf:name ?actorName . "
                    + "?actor uwe2rdf:usesUseCase ?uc ."
                    + "?uc uwe2rdf:name \"" + ucName + "\" ."
                    + "?uc rdf:type uwe2rdf:UseCase ."
                    + "?uc uwe2rdf:hasSoftwareCase ?sc "
                    + "}";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String actorURI = bindingSet.getValue("actor").stringValue();
                String actorName = bindingSet.getValue("actorName").stringValue();
                //System.out.println("ucName: " + usecase.getName() + " actor: " + actorName);
                result.add(new Actor(actorURI, actorName));
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("usecase " + usecase);
        //System.out.println("actors " + result);
        return result;
    }

    public ArrayList<Actor> findActors(Usecase usecase) {
        ArrayList<Actor> result = new ArrayList<Actor>();
        String query = "";
        try {
            query = "SELECT ?actor ?actorName WHERE {"
                    + " ?actor rdf:type uwe2rdf:Actor ."
                    + "?actor uwe2rdf:name ?actorName . "
                    + "?actor uwe2rdf:usesUseCase <" + usecase.getURI() + "> ."
                    + "}";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String actorURI = bindingSet.getValue("actor").stringValue();
                String actorName = bindingSet.getValue("actorName").stringValue();
                //System.out.println("ucName: " + usecase.getName() + " actor: " + actorName);
                result.add(new Actor(actorURI, actorName));
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("usecase " + usecase);
        //System.out.println("actors " + result);
        return result;
    }

    public ArrayList<UMLClass> getOtherContentModelClasses(SoftwareCase sc) {
        ArrayList<UMLClass> classes = new ArrayList<UMLClass>();
        try {
            String query = "select ?class ?className where { "
                    + "?model rdf:type uwe2rdf:Model . "
                    + "?model uwe2rdf:hasSoftwareCase ?sc . "
                    + "?sc uwe2rdf:name ?scName . "
                    + "?model uwe2rdf:name \"Content\" . "
                    + "?model uwe2rdf:containsPackagedElement ?class . "
                    + "?class uwe2rdf:name ?className . "
                    + "?class rdf:type uwe2rdf:Class . "
                    + "FILTER(?scName!=\"" + sc.getName() + "\")"
                    + "} ORDER BY ?className";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String url = bindingSet.getValue("class").stringValue();
                String name = bindingSet.getValue("className").stringValue();
                UMLClass umlClass = new UMLClass(name, url);
                ArrayList<UMLAttribute> attribtues = listClassAttributes(url);
                umlClass.addAttributes(attribtues);
                classes.add(umlClass);
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    private double getAnnotationScore(String acURI) {
        double result = -1;
        try {
            String query = "select ?score where { "
                    + "<" + acURI + "> uwe2rdf:hasAnnotationScore ?score"
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String temp = bindingSet.getValue("score").stringValue();
                result = Double.parseDouble(temp);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<UMLClass> findClassInOntologyRepository(String concept) {
        ArrayList<UMLClass> classes = new ArrayList<UMLClass>();
        try {
            String query = "select ?class ?className where { "
                    + "?class rdf:type ?classType . "
                    + "?class rdfs:label ?className . "
                    + " FILTER REGEX(str(?className), \"^" + concept + "$\", \"i\" ) "
                    + " FILTER (?classType = rdfs:Class || ?classType = owl:Class) "
                    + "} ORDER BY ?className";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getOntologyRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String url = bindingSet.getValue("class").stringValue();
                String name = bindingSet.getValue("className").stringValue();
                UMLClass umlClass = new UMLClass(name, url);
                ArrayList<UMLAttribute> attribtues = getClassAttributesFromOntologyRepository(url);
                umlClass.addAttributes(attribtues);
                classes.add(umlClass);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public ArrayList<UMLClass> findClassInOntologyRepository2(String concept) {
        ArrayList<UMLClass> classes = new ArrayList<UMLClass>();
        try {
            String query = "select ?class where { "
                    + "?class rdf:type ?classType . "
                    + " FILTER REGEX(str(?class), \"#" + concept + "$\", \"i\" ) "
                    + " FILTER (?classType = rdfs:Class || ?classType = owl:Class) "
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getOntologyRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String url = bindingSet.getValue("class").stringValue();
                String name = concept;
                UMLClass umlClass = new UMLClass(name, url);
                ArrayList<UMLAttribute> attribtues = getClassAttributesFromOntologyRepository(url);
                umlClass.addAttributes(attribtues);
                classes.add(umlClass);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public ArrayList<UMLClass> findClassInModelRepository(String concept) {
        ArrayList<UMLClass> classes = new ArrayList<UMLClass>();
        try {
            String query = "select ?class ?className where { "
                    + "?model rdf:type uwe2rdf:Model . "
                    + "?model uwe2rdf:name \"Content\" . "
                    + "?model uwe2rdf:containsPackagedElement ?class . "
                    + "?class uwe2rdf:name ?className . "
                    + "?class rdf:type uwe2rdf:Class "
                    + " FILTER regex(str(?className), \"" + concept + "\", \"i\" ) "
                    + "} ORDER BY ?className";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String url = bindingSet.getValue("class").stringValue();
                String name = bindingSet.getValue("className").stringValue();
                UMLClass umlClass = new UMLClass(name, url);
                ArrayList<UMLAttribute> attribtues = listClassAttributes(url);
                umlClass.addAttributes(attribtues);
                classes.add(umlClass);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public ArrayList<UMLClass> getAllClassesFromModelRepository() {
        ArrayList<UMLClass> classes = new ArrayList<UMLClass>();
        try {
            String query = "select ?class ?className where { "
                    + "?model rdf:type uwe2rdf:Model . "
                    + "?model uwe2rdf:name \"Content\" . "
                    + "?model uwe2rdf:containsPackagedElement ?class . "
                    + "?class uwe2rdf:name ?className . "
                    + "?class rdf:type uwe2rdf:Class "
                    + "} ORDER BY ?className";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String url = bindingSet.getValue("class").stringValue();
                String name = bindingSet.getValue("className").stringValue();
                UMLClass umlClass = new UMLClass(name, url);
                ArrayList<UMLAttribute> attribtues = listClassAttributes(url);
                umlClass.addAttributes(attribtues);
                classes.add(umlClass);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public ArrayList<UMLClass> findClassInModelRepository(String concept, String inputSoftwareCaseURI) {
        ArrayList<UMLClass> classes = new ArrayList<UMLClass>();
        try {
            String query = "select ?class ?className where { "
                    + "?model rdf:type uwe2rdf:Model . "
                    + "?model uwe2rdf:hasSoftwareCase ?sc ."
                    + "?model uwe2rdf:name \"Content\" . "
                    + "?model uwe2rdf:containsPackagedElement ?class . "
                    + "?class uwe2rdf:name ?className . "
                    + "?class rdf:type uwe2rdf:Class "
                    + " FILTER regex(str(?className), \"" + concept + "\", \"i\" ) ."
                    + " FILTER (str(?sc)!=\"" + inputSoftwareCaseURI + "\")"
                    + "} ORDER BY ?className";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String url = bindingSet.getValue("class").stringValue();
                String name = bindingSet.getValue("className").stringValue();
                UMLClass umlClass = new UMLClass(name, url);
                ArrayList<UMLAttribute> attribtues = listClassAttributes(url);
                umlClass.addAttributes(attribtues);
                classes.add(umlClass);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public ArrayList<Actor> getAssociatedActors(String adURI) {
        ArrayList<Actor> result = new ArrayList<Actor>();
        try {
            String query = "select ?actor ?actorName ?adName ?ucName where { "
                    + "<" + adURI + "> uwe2rdf:hasSoftwareCase ?sc ."
                    + "<" + adURI + "> uwe2rdf:name ?adName . "
                    + "?actor rdf:type uwe2rdf:Actor."
                    + "?actor uwe2rdf:hasSoftwareCase ?sc ."
                    + "?uc rdf:type uwe2rdf:UseCase ."
                    + "?uc uwe2rdf:hasSoftwareCase ?sc ."
                    + "?actor uwe2rdf:name ?actorName ."
                    + "?uc uwe2rdf:name ?ucName ."
                    + "?actor uwe2rdf:usesUseCase ?uc"
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String adName = bindingSet.getValue("adName").stringValue();
                String actorName = bindingSet.getValue("actorName").stringValue();
                String actorURI = bindingSet.getValue("actor").stringValue();
                String ucName = bindingSet.getValue("ucName").stringValue();
                if (adName.equalsIgnoreCase(ucName)) {
                    result.add(new Actor(actorURI, actorName));
                }
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<UMLClass> listClassesFromContentModel(String softwareCaseURI) {
        ArrayList<UMLClass> classes = new ArrayList<UMLClass>();
        try {
            String query = "select ?class ?className where { "
                    + "?model rdf:type uwe2rdf:Model . "
                    + "?model uwe2rdf:hasSoftwareCase <" + softwareCaseURI + "> . "
                    + "?model uwe2rdf:name \"Content\" . "
                    + "?model uwe2rdf:containsPackagedElement ?class . "
                    + "?class uwe2rdf:name ?className . "
                    + "?class rdf:type uwe2rdf:Class"
                    + "} ORDER BY ?className";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String uri = bindingSet.getValue("class").stringValue();
                String name = bindingSet.getValue("className").stringValue();
                UMLClass umlClass = new UMLClass(name, uri);
                ArrayList<UMLAttribute> attribtues = listClassAttributes(uri);
                umlClass.addAttributes(attribtues);
                classes.add(umlClass);
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    private boolean isValidPackage(String packageName) {
        final String[] KEYWORDS = {"user", "content", "activity"};
        for (String keyword : KEYWORDS) {
            if (packageName.toLowerCase().contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<UMLClass> listClassesFromOtherPackages(String softwareCaseURI) {
        ArrayList<UMLClass> classes = new ArrayList<UMLClass>();
        try {
            String query = "select ?class ?className ?packageName where { "
                    + "?package uwe2rdf:hasSoftwareCase <" + softwareCaseURI + "> . "
                    + "?package uwe2rdf:name ?packageName . "
                    + "?package uwe2rdf:containsPackagedElement ?class . "
                    + "?class uwe2rdf:name ?className . "
                    + "?class rdf:type uwe2rdf:Class"
                    + " FILTER(?packageName=\"ContentModel\" || ?packageName=\"UserModel\""
                    + " || ?packageName=\"Users\""
                    + ")"
                    + "} ORDER BY ?package ?className";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String packageName = bindingSet.getValue("packageName").stringValue();
                if (!isValidPackage(packageName)) {
                    continue;
                }
                String uri = bindingSet.getValue("class").stringValue();
                String name = bindingSet.getValue("className").stringValue();
                UMLClass umlClass = new UMLClass(name, uri);
                ArrayList<UMLAttribute> attribtues = listClassAttributes(uri);
                umlClass.addAttributes(attribtues);
                classes.add(umlClass);
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public ArrayList<UMLClass> getAllClasses(SoftwareCase sc) {
        ArrayList<UMLClass> classes = new ArrayList<UMLClass>();
        try {
            String query = "select ?class ?className where { "
                    + "?sc uwe2rdf:name \"" + sc.getName() + "\" . "
                    + "?class uwe2rdf:name ?className . "
                    + "?class rdf:type uwe2rdf:Class ."
                    + "?class uwe2rdf:hasSoftwareCase ?sc"
                    + "} ORDER BY ?className";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String url = bindingSet.getValue("class").stringValue();
                String name = bindingSet.getValue("className").stringValue();
                UMLClass umlClass = new UMLClass(name, url);
                ArrayList<UMLAttribute> attribtues = listClassAttributes(url);
                umlClass.addAttributes(attribtues);
                classes.add(umlClass);
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public ArrayList<UMLAttribute> getClassAttributesFromLOD(String className) {
        ArrayList<UMLAttribute> attributes = new ArrayList<UMLAttribute>();
        return attributes;
        //this is used to disabled semantic web facility for the purpose of evaluation
        /*
         * LODInterface lod = new LODInterface(); System.out.println("LOD SEARCH
         * FOR CLASS " + className); long start = System.currentTimeMillis();
         * ArrayList<String> classURIs = lod.getClassURIs(className); for
         * (String classURI : classURIs) { System.out.println(">> " + classURI);
         * ArrayList<UMLAttribute> tempAttributes =
         * lod.getClassAttributesURIs(classURI); for (UMLAttribute attr :
         * tempAttributes) { System.out.println("\t" + attr); }
         * attributes.addAll(tempAttributes); } long end =
         * System.currentTimeMillis(); System.out.println("TOOK " + (end -
         * start) + " ms"); return attributes;
         *
         */
    }

    public ArrayList<UMLAttribute> getClassAttributesFromOntologyRepository(String classURI) {
        ArrayList<UMLAttribute> result = new ArrayList<UMLAttribute>();
        try {
            String query1 = "select distinct ?attribute ?name ?type where { "
                    + " ?attribute rdfs:domain <" + classURI + ">  . "
                    + "optional {?attribute rdfs:label ?name } . "
                    + "optional {?attribute rdfs:range ?type } . "
                    + "}";
            String query2 = "select distinct ?attribute ?name ?type where { "
                    + " <" + classURI + "> rdfs:subClassOf ?super ."
                    + "?attribute rdfs:domain ?super  . "
                    + "optional {?attribute rdfs:label ?name } . "
                    + "optional {?attribute rdfs:range ?type } . "
                    + "}";
            String query3 = "select distinct ?attribute ?name ?type where { "
                    + " <" + classURI + "> rdfs:subClassOf ?super ."
                    + " super rdfs:subClassOf ?super2 ."
                    + "?attribute rdfs:domain ?super2  . "
                    + "optional {?attribute rdfs:label ?name } . "
                    + "optional {?attribute rdfs:range ?type } . "
                    + "}";
            String[] queries = {query1, query2, query3};
            for (String query : queries) {
                TupleQueryResult queryResult = RepositoryManager.executeQuery(getOntologyRepositoryConnection(), query);
                while (queryResult != null && queryResult.hasNext()) {
                    BindingSet bindingSet = queryResult.next();
                    String url = bindingSet.getValue("attribute").stringValue();
                    Value value = bindingSet.getValue("name");
                    Value type = bindingSet.getValue("type");
                    String typeURI = null;
                    if (type != null) {
                        typeURI = type.stringValue();
                    }
                    String name = null;
                    if (value != null) {
                        name = value.stringValue();
                    } else {
                        int index = url.lastIndexOf('#');
                        if (index != -1) {
                            name = url.substring(index + 1);
                        } else {
                            name = url;
                        }
                    }
                    UMLAttribute attribute = new UMLAttribute(name, url);
                    if (typeURI != null) {
                        attribute.setType(typeURI);
                    }

                    result.add(attribute);
                }
                queryResult.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<UMLAttribute> listClassAttributes(String classURI) {
        ArrayList<UMLAttribute> result = new ArrayList<UMLAttribute>();
        try {
            String query = "select ?attribute ?name ?type ?visibility where { "
                    + "<" + classURI + "> uwe2rdf:containsAttribute ?attribute . "
                    + "?attribute uwe2rdf:name ?name . "
                    + "optional {?attribute uwe2rdf:type ?type . "
                    + "?attribute uwe2rdf:visibility ?visibility } "
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String url = bindingSet.getValue("attribute").stringValue();
                String name = bindingSet.getValue("name").stringValue();
                Value temp = bindingSet.getValue("type");
                String type = null;
                if (temp != null) {
                    type = temp.stringValue();
                }
                temp = bindingSet.getValue("visibility");
                String visibility = null;
                if (temp != null) {
                    visibility = temp.stringValue();
                }
                UMLAttribute attribute = new UMLAttribute(name, url);
                attribute.setType(type);
                attribute.setVisibility(visibility);
                result.add(attribute);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isTypeOfCentralBuffer(String acURI, String classURI) {
        boolean result = false;
        try {
            String query = "select ?nodeName where { "
                    + "<" + acURI + "> uwe2rdf:containsNode ?node . "
                    + " ?node rdf:type uwe2rdf:CentralBufferNode . "
                    + " ?node uwe2rdf:type <" + classURI + ">"
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult != null && queryResult.hasNext()) {
                //              BindingSet bindingSet = queryResult.next();
//                String nodeName = bindingSet.getValue("nodeName").stringValue();
                result = true;
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isLabelInTheNameOfTheCentralBufferNode(String acURI, String label) {
        boolean result = false;
        try {
            String query = "select ?nodeName where { "
                    + "<" + acURI + "> uwe2rdf:containsNode ?node . "
                    + " ?node rdf:type uwe2rdf:CentralBufferNode . "
                    + " ?node uwe2rdf:name ?nodeName  "
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String nodeName = bindingSet.getValue("nodeName").stringValue();
                result = nodeName.equalsIgnoreCase(label);
                if (result) {
                    queryResult.close();
                    return result;
                }
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param acURI URI of the activity diagram
     * @param label a string from the labels of the activity diagram
     * @return
     */
    /*
     * public boolean isLabelInTheNameOfThecorrespondingActor(String acURI,
     * String label) { boolean result = false; try { String query = "select
     * ?acName ?actorName ?ucName where { " + "<" + acURI + ">
     * uwe2rdf:hasSoftwareCase ?sc ." + "<" + acURI + "> uwe2rdf:name ?acName .
     * " + "?actor rdf:type uwe2rdf:Actor." + "?actor uwe2rdf:hasSoftwareCase
     * ?sc ." + "?uc rdf:type uwe2rdf:UseCase ." + "?uc uwe2rdf:hasSoftwareCase
     * ?sc ." + "?actor uwe2rdf:name ?actorName ." + "?uc uwe2rdf:name ?ucName
     * ." + "?actor uwe2rdf:usesUseCase ?uc" + "}"; TupleQueryResult queryResult
     * = RepositoryManager.executeQuery(modelRepositoryConnection, query); if
     * (queryResult != null && queryResult.hasNext()) { BindingSet bindingSet =
     * queryResult.next(); String acName =
     * bindingSet.getValue("actorName").stringValue(); String actorName =
     * bindingSet.getValue("actorName").stringValue(); String ucName =
     * bindingSet.getValue("ucName").stringValue(); result =
     * (acName.equalsIgnoreCase(ucName) && actorName.equalsIgnoreCase(label));
     * if (result) { queryResult.close(); return result; } }
     * queryResult.close(); } catch (Exception e) { e.printStackTrace(); }
     * return result; }
     */
    public String getElementName(String elementURI) {
        String name = null;
        try {
            String query = "select ?name where { "
                    + "<" + elementURI + "> uwe2rdf:name ?name"
                    + "}";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult != null && queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                name = bindingSet.getValue("name").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public ArrayList<Usecase> findExtendedUsecases(Usecase usecase) {
        ArrayList<Usecase> result = new ArrayList<Usecase>();
        try {
            String query = " SELECT ?extended ?extendedName  WHERE { "
                    + " ?extended rdf:type uwe2rdf:UseCase ."
                    + " ?extended uwe2rdf:name ?extendedName ."
                    + " ?relation rdf:type uwe2rdf:Extend ."
                    + " ?relation uwe2rdf:extendedCase ?extended ."
                    + " ?relation uwe2rdf:extension <" + usecase.getURI() + ">  ."
                    + " } ";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String extendedURI = bindingSet.getValue("extended").stringValue();
                String extendedName = bindingSet.getValue("extendedName").stringValue();
                Usecase uc = new Usecase(extendedURI, extendedName);
                result.add(uc);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Usecase> findExtenderUsecases(Usecase usecase) {
        ArrayList<Usecase> result = new ArrayList<Usecase>();
        try {
            String query = " SELECT ?extender ?extenderName  WHERE { "
                    + " ?extender rdf:type uwe2rdf:UseCase . "
                    + " ?extender uwe2rdf:name ?extenderName . "
                    + " ?relation rdf:type uwe2rdf:Extend ."
                    + " ?relation uwe2rdf:extendedCase <" + usecase.getURI() + "> ."
                    + " ?relation uwe2rdf:extension ?extender ."
                    + " } ";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String extenderURI = bindingSet.getValue("extender").stringValue();
                String extenderName = bindingSet.getValue("extenderName").stringValue();
                Usecase uc = new Usecase(extenderURI, extenderName);
                result.add(uc);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Usecase> findIncludedUsecases(Usecase usecase) {
        ArrayList<Usecase> result = new ArrayList<Usecase>();
        try {
            String query = " SELECT ?included ?includedName WHERE { "
                    + " ?included rdf:type uwe2rdf:UseCase ."
                    + " ?included uwe2rdf:name ?includedName ."
                    + " ?relation rdf:type uwe2rdf:Include ."
                    + " ?relation uwe2rdf:addition ?included ."
                    + " ?relation uwe2rdf:includingCase <" + usecase.getURI() + ">  ."
                    + " } ";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String includedURI = bindingSet.getValue("included").stringValue();
                String includedName = bindingSet.getValue("includedName").stringValue();
                Usecase uc = new Usecase(includedURI, includedName);
                result.add(uc);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Usecase> findIncluderUsecases(Usecase usecase) {
        ArrayList<Usecase> result = new ArrayList<Usecase>();
        try {
            String query = " SELECT ?includer ?includerName WHERE { "
                    + " ?includer rdf:type uwe2rdf:UseCase . "
                    + " ?includer uwe2rdf:name ?includerName . "
                    + " ?relation rdf:type uwe2rdf:Include ."
                    + " ?relation uwe2rdf:includingCase ?includer ."
                    + " ?relation uwe2rdf:addition <" + usecase.getURI() + "> ."
                    + " } ";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String includerURI = bindingSet.getValue("includer").stringValue();
                String includerName = bindingSet.getValue("includerName").stringValue();
                //System.out.append("ucName: " + usecase.getName() + " includer: " + includerName);
                Usecase uc = new Usecase(includerURI, includerName);
                result.add(uc);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Usecase> findGeneralUsecases(Usecase usecase) {
        ArrayList<Usecase> result = new ArrayList<Usecase>();
        try {
            String query = " SELECT ?general ?generalName  WHERE { "
                    + " ?general rdf:type uwe2rdf:UseCase . "
                    + " ?general uwe2rdf:name ?generalName . "
                    + " ?relation rdf:type uwe2rdf:Generalization ."
                    + " ?relation uwe2rdf:hasGeneral ?general ."
                    + " ?relation uwe2rdf:hasSpecific <" + usecase.getURI() + "> ."
                    + " } ";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String generalURI = bindingSet.getValue("general").stringValue();
                String generalName = bindingSet.getValue("generalName").stringValue();
                Usecase uc = new Usecase(generalURI, generalName);
                result.add(uc);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Usecase> findSpecificUsecases(Usecase usecase) {
        ArrayList<Usecase> result = new ArrayList<Usecase>();
        try {
            String query = " SELECT ?specific ?specificName  WHERE { "
                    + " ?specific rdf:type uwe2rdf:UseCase ."
                    + " ?specific uwe2rdf:name ?specificName ."
                    + " ?specific uwe2rdf:hasSoftwareCase ?softwareCase ."
                    + " ?relation rdf:type uwe2rdf:Generalization ."
                    + " ?relation uwe2rdf:hasGeneral <" + usecase.getURI() + "> ."
                    + " ?relation uwe2rdf:hasSpecific ?specific ."
                    + " } ";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String specificURI = bindingSet.getValue("specific").stringValue();
                String specificName = bindingSet.getValue("specificName").stringValue();
                Usecase uc = new Usecase(specificURI, specificName);
                result.add(uc);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public SoftwareCase createSoftwareCase(String softwareCaseName) {
        SoftwareCase sc = new SoftwareCase(softwareCaseName);
        ArrayList<Usecase> usecases = getUsecases(sc);
        UsecaseDiagram ucd = new UsecaseDiagram(usecases.size());
        ucd.setSoftwareCase(sc);
        for (int i = 0; i < usecases.size(); i++) {
            usecases.get(i).setUsecaseDiagram(ucd);
            usecases.get(i).setSoftwareCase(sc);
            ucd.setUsecase(i, usecases.get(i));
        }
        sc.setUsecaseDiagram(ucd);
        return sc;
    }

    private double computeSimilarity(Actor[] actors1, Actor[] actors2) {
        double result = 0;
        for (int i = 0; i < actors1.length; i++) {
            double maxSimilarity = -1;
            Actor actor1 = actors1[i];
            for (int j = 0; j < actors2.length; j++) {
                Actor actor2 = actors2[j];
                double similarity = WordnetSimilarityCalculator.computeActorNameSimilarity(actor1.getName(), actor2.getName());
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                }
            }
            result += maxSimilarity;
        }
        return result;
    }

    private double computeSimilarity(String[] usecaseNames1, String[] usecaseNames2) {
        double result = 0;
        for (int i = 0; i < usecaseNames1.length; i++) {
            double maxSimilarity = -1;
            String usecaseName1 = usecaseNames1[i];
            for (int j = 0; j < usecaseNames2.length; j++) {
                String usecaseName2 = usecaseNames2[j];
                double similarity = WordnetSimilarityCalculator.computeActorNameSimilarity(usecaseName1, usecaseName2);
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                }
            }
            result += maxSimilarity;
        }
        return result;
    }

    /*
     * private double computeSimilarity(Usecase usecase1, Usecase usecase2) {
     * double textualSimilarity =
     * WordnetSimilarityCalculator.computeUsecaseNameSimilarity(usecase1.getName(),
     * usecase2.getName()); Actor[] actors1 = usecase1.getActors(); Actor[]
     * actors2 = usecase2.getActors(); double actorSimilarity =
     * computeSimilarity(actors1, actors2); String[] extendedUsecaseNames1 =
     * usecase1.getExtendedUsecaseNames(); String[] extendedUsecaseNames2 =
     * usecase2.getExtendedUsecaseNames(); String[] extenderUsecaseNames1 =
     * usecase1.getExtenderUsecaseNames(); String[] extenderUsecaseNames2 =
     * usecase2.getExtenderUsecaseNames(); double extendedsSimilarity =
     * computeSimilarity(extendedUsecaseNames1, extendedUsecaseNames2); double
     * extendersSimilarity = computeSimilarity(extenderUsecaseNames1,
     * extenderUsecaseNames2); String[] generalUsecaseNames1 =
     * usecase1.getGeneralUsecaseNames(); String[] generalUsecaseNames2 =
     * usecase2.getGeneralUsecaseNames(); String[] specificUsecaseNames1 =
     * usecase1.getSpecificUsecaseNames(); String[] specificUsecaseNames2 =
     * usecase2.getSpecificUsecaseNames(); double generalizationSimilarity =
     * computeSimilarity(generalUsecaseNames1, generalUsecaseNames2); double
     * specilizationSimilarity = computeSimilarity(specificUsecaseNames1,
     * specificUsecaseNames2); double similarity = textualSimilarity +
     * actorSimilarity + extendedsSimilarity + extendersSimilarity +
     * generalizationSimilarity + specilizationSimilarity; return similarity; }
     */

    /*
     * private UsecaseMatch getMostSimilar(Usecase inputUsecase) { Usecase
     * mostSimilar = null; double maxSimilarity = -1; for (SoftwareCase sc :
     * currentSCs) { for (int i = 0; i <
     * sc.getUsecaseDiagram().getUsecaseCount(); i++) { Usecase currentUC =
     * sc.getUsecaseDiagram().getUsecase(i); double similarity =
     * computeSimilarity(inputUsecase, currentUC); if (similarity >
     * maxSimilarity) { maxSimilarity = similarity; mostSimilar = currentUC; } }
     * } return new UsecaseMatch(inputUsecase, mostSimilar, maxSimilarity); }
     */
    /*
     * private void generateModels() { UsecaseMatch[] usecaseMatches = new
     * UsecaseMatch[inputSC.getUsecaseCount()]; for (int i = 0; i <
     * inputSC.getUsecaseCount(); i++) { Usecase inputUsecase =
     * inputSC.getUsecaseDiagram().getUsecase(i); usecaseMatches[i] =
     * getMostSimilar(inputUsecase); } double[] scSimilarities = new
     * double[currentSCs.length]; /* double maxSimilarity = -1; SoftwareCase
     * mostSimilar = null; for (int i = 0; i < currentSCs.length; i++) {
     * scSimilarities[i] = computeSimilarity(currentSCs[i]); if
     * (scSimilarities[i] > maxSimilarity) { maxSimilarity = scSimilarities[i];
     * mostSimilar = currentSCs[i]; } } printResults(usecaseMatches,
     * scSimilarities); }
     */
    private void printResults(UsecaseMatch[] usecaseMatches, double[] scSimilarities) {
        Arrays.sort(usecaseMatches);
        System.out.println("Matching Usecases");
        for (int i = 0; i < usecaseMatches.length; i++) {
            System.out.println((i + 1) + ". '" + usecaseMatches[i].getInputUsecase().getName() + "' from SC '"
                    + usecaseMatches[i].getInputUsecase().getSoftwareCase().getName() + "' =====> '"
                    + usecaseMatches[i].getMatchingUsecase().getName() + "' from SC '"
                    + usecaseMatches[i].getMatchingUsecase().getSoftwareCase().getName()
                    + "' similarity: " + usecaseMatches[i].getSimilarity());
        }
        /*
         * System.out.println("Matching Software Cases"); for (int i = 0; i <
         * scSimilarities.length; i++) { double maxSimilarity = -1; int maxIndex
         * = 0; for (int j = i; j < scSimilarities.length; j++) { if
         * (scSimilarities[j] > maxSimilarity) { maxSimilarity =
         * scSimilarities[j]; maxIndex = j; } } SoftwareCase sc = currentSCs[i];
         * currentSCs[i] = currentSCs[maxIndex]; currentSCs[maxIndex] = sc;
         * double temp = scSimilarities[i]; scSimilarities[i] =
         * scSimilarities[maxIndex]; scSimilarities[maxIndex] = temp; } try {
         * PrintStream htmlFile = new PrintStream("results.html");
         * htmlFile.println("<html><head></head><body>");
         * htmlFile.println("<table border=\"1\">"); for (int i = 0; i <
         * currentSCs.length; i++) { htmlFile.println("\t<tr>");
         * htmlFile.println("<td>" + inputSC.getName() + "</td><td>" +
         * currentSCs[i].getName() + "</td><td>" + scSimilarities[i] + "</td>");
         * htmlFile.println("\t</tr>"); htmlFile.println("\t<tr>");
         * htmlFile.println("<td><img src=\"images\\" + inputSC.getName() +
         * "\\ucd.jpg\"></td><td><img src=\"images\\" + currentSCs[i].getName()
         * + "\\ucd.jpg\"></td><td>" + scSimilarities[i] + "</td>");
         * htmlFile.println("\t</tr>"); } htmlFile.println("</table>");
         * htmlFile.println("</body>"); htmlFile.close(); } catch (Exception e)
         * { e.printStackTrace(); } System.out.println("Input SC: " +
         * inputSC.getName()); for (int i = 0; i < currentSCs.length; i++) {
         * System.out.println("\t" + (i + 1) + ". " + currentSCs[i].getName() +
         * " similarity: " + scSimilarities[i]);
         *
         * }
         */
    }

    private double computeSimilarity(SoftwareCase softwareCase) {
        double result = 0;
        for (int i = 0; i < inputSC.getUsecaseCount(); i++) {
            Usecase inputUC = inputSC.getUsecaseDiagram().getUsecase(i);
            double maxSimilarity = -1;
            for (int j = 0; j < softwareCase.getUsecaseCount(); j++) {
                Usecase uc = softwareCase.getUsecaseDiagram().getUsecase(j);
                double similarity = WordnetSimilarityCalculator.computeUsecaseNameSimilarity(inputUC.getName(), uc.getName());
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                }
            }
            result += maxSimilarity;
        }
        return result;
    }

    /*
     * public void generateModelForSoftwareCase(String softwareCaseName) { try {
     * Logger.log("generating model for software case '" + softwareCaseName +
     * "'"); inputSC = createSoftwareCase(softwareCaseName); ArrayList<String>
     * otherSoftwareCaseNames = getOtherSoftwareCaseNames(softwareCaseName);
     * currentSCs = new SoftwareCase[otherSoftwareCaseNames.size()]; for (int i
     * = 0; i < otherSoftwareCaseNames.size(); i++) { currentSCs[i] =
     * createSoftwareCase(otherSoftwareCaseNames.get(i)); } generateModels();
     * RepositoryManager.closeConnection(modelRepositoryConnection);
     * WordnetSimilarityCalculator.saveResultsInDB(); Logger.log("finished"); }
     * catch (Exception e) { e.printStackTrace(); } }
     *
     */
    private ArrayList<String> getOtherSoftwareCaseNames(String softwareCaseName) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            String query = "SELECT DISTINCT ?softwareCaseName  WHERE { "
                    + " ?softwareCase rdf:type uwe2rdf:softwareCase ."
                    + " ?softwareCase uwe2rdf:name ?softwareCaseName ."
                    + " FILTER (?softwareCaseName != \"" + softwareCaseName + "\")"
                    + " }";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String name = bindingSet.getValue("softwareCaseName").stringValue();
                result.add(name);
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> getAllSoftwareCaseNames() {
        ArrayList<String> result = new ArrayList<String>();
        try {
            String query = "SELECT DISTINCT ?softwareCaseName  WHERE { "
                    + " ?softwareCase rdf:type uwe2rdf:softwareCase ."
                    + " ?softwareCase uwe2rdf:name ?softwareCaseName "
                    + " } ORDER BY ?softwareCaseName";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String name = bindingSet.getValue("softwareCaseName").stringValue();
                result.add(name);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Usecase> getUsecases(SoftwareCase sc) {
        ArrayList<Usecase> result = new ArrayList<Usecase>();
        try {
            String query = "SELECT DISTINCT ?usecase ?usecaseName WHERE { "
                    + " ?usecase uwe2rdf:hasSoftwareCase ?sc ."
                    + " ?sc uwe2rdf:name \"" + sc.getName() + "\" ."
                    + " ?usecase rdf:type uwe2rdf:UseCase . "
                    + " ?usecase uwe2rdf:name ?usecaseName "
                    + " }";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String usecaseURI = bindingSet.getValue("usecase").stringValue();
                String usecaseName = bindingSet.getValue("usecaseName").stringValue();
                Usecase uc = createUsecase(sc.getName(), usecaseURI, usecaseName);
                result.add(uc);
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Actor> getActors(SoftwareCase sc) {
        ArrayList<Actor> result = new ArrayList<Actor>();
        try {
            String query = "SELECT DISTINCT ?actor ?actorName WHERE { "
                    + " ?actor uwe2rdf:hasSoftwareCase ?sc ."
                    + " ?sc uwe2rdf:name \"" + sc.getName() + "\" ."
                    + " ?actor rdf:type uwe2rdf:Actor . "
                    + " ?actor uwe2rdf:name ?actorName "
                    + " }";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String actorURI = bindingSet.getValue("actor").stringValue();
                String actorName = bindingSet.getValue("actorName").stringValue();
                Actor actor = new Actor(actorURI, actorName);
                result.add(actor);
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Usecase createUsecase(String scName, String usecaseURI, String usecaseName) {
        SoftwareCase sc = new SoftwareCase(scName);
        Usecase uc = new Usecase(usecaseURI, usecaseName);
        uc.setSoftwareCase(sc);
        ArrayList<Usecase> extenderUsecases = findExtenderUsecases(uc);
        uc.setExtenderUsecases(extenderUsecases);
        ArrayList<Usecase> extendedUsecases = findExtendedUsecases(uc);
        uc.setExtendedUsecases(extendedUsecases);
        ArrayList<Usecase> generalUsecases = findGeneralUsecases(uc);
        uc.setGeneralUsecases(generalUsecases);
        ArrayList<Usecase> specificUsecases = findSpecificUsecases(uc);
        uc.setSpecificUsecases(specificUsecases);
        ArrayList<Actor> actors = findActors(uc);
        Actor[] actorsArray = new Actor[actors.size()];
        for (int i = 0; i < actors.size(); i++) {
            actorsArray[i] = actors.get(i);
        }
        uc.setActors(actorsArray);
        return uc;
    }
    /*
     * public ArrayList<String> getUsecaseNames(String softwareCaseName) {
     * ArrayList<String> result = new ArrayList<String>(); try { String query =
     * "SELECT DISTINCT ?usecaseName WHERE { " + " ?usecase
     * uwe2rdf:hasSoftwareCase ?softwareCase ." + " ?softwareCase uwe2rdf:name
     * \"" + softwareCaseName + "\" ." + " ?usecase rdf:type uwe2rdf:UseCase . "
     * + " ?usecase uwe2rdf:name ?usecaseName " + " }";
     *
     * TupleQueryResult queryResult =
     * RepositoryManager.executeQuery(modelRepositoryConnection, query); while
     * (queryResult.hasNext()) { BindingSet bindingSet = queryResult.next();
     * String usecaseName = bindingSet.getValue("usecaseName").stringValue();
     * result.add(usecaseName); } queryResult.close();
     *
     * } catch (Exception e) { e.printStackTrace(); } return result; }
     */

    public ArrayList<Annotation> getActivityDiagramAnnotations(String activityDiagramURI) {
        ArrayList<Annotation> result = new ArrayList<Annotation>();
        try {
            String query = "SELECT ?annotatedWord ?target ?type ?label ?priority WHERE { "
                    + " <" + activityDiagramURI + "> uwe2rdf:hasAnnotation ?annotation ."
                    + " ?annotation uwe2rdf:annotatedWord ?annotatedWord ."
                    + " ?annotation uwe2rdf:label ?label ."
                    + " ?annotation uwe2rdf:target ?target ."
                    + " ?annotation uwe2rdf:annotationType ?type ."
                    + " ?annotation uwe2rdf:priorityLevel ?priority ."
                    + " }";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String owner = activityDiagramURI;
                String label = bindingSet.getValue("label").stringValue();
                String annotatedWord = bindingSet.getValue("annotatedWord").stringValue();
                String target = bindingSet.getValue("target").stringValue();
                int type = Integer.parseInt(bindingSet.getValue("type").stringValue());
                int priority = Integer.parseInt(bindingSet.getValue("priority").stringValue());
                result.add(new Annotation(activityDiagramURI, type, label, owner, annotatedWord, target, priority));
            }
            queryResult.close();

            query = "SELECT ?node ?annotatedWord ?target ?type ?label ?priority WHERE { "
                    + " <" + activityDiagramURI + "> uwe2rdf:containsNode ?node . "
                    + " ?node uwe2rdf:hasAnnotation ?annotation ."
                    + " ?annotation uwe2rdf:annotatedWord ?annotatedWord ."
                    + " ?annotation uwe2rdf:label ?label ."
                    + " ?annotation uwe2rdf:target ?target ."
                    + " ?annotation uwe2rdf:annotationType ?type ."
                    + " ?annotation uwe2rdf:priorityLevel ?priority ."
                    + " }";

            queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String owner = bindingSet.getValue("node").stringValue();
                String label = bindingSet.getValue("label").stringValue();
                String annotatedWord = bindingSet.getValue("annotatedWord").stringValue();
                String target = bindingSet.getValue("target").stringValue();
                int type = Integer.parseInt(bindingSet.getValue("type").stringValue());
                int priority = Integer.parseInt(bindingSet.getValue("priority").stringValue());
                result.add(new Annotation(activityDiagramURI, type, label, owner, annotatedWord, target, priority));
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getSoftwareCaseURI(String scName) {
        String result = null;
        try {
            String query = "SELECT ?sc WHERE { "
                    + " ?sc rdf:type uwe2rdf:softwareCase ."
                    + " ?sc uwe2rdf:name \"" + scName + "\""
                    + " }";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                result = bindingSet.getValue("sc").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getUsecaseURI(String ucName) {
        String result = null;
        try {
            String query = "SELECT ?uc WHERE { "
                    + " ?uc rdf:type uwe2rdf:UseCase ."
                    + " ?uc uwe2rdf:name \"" + ucName + "\""
                    + " }";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                result = bindingSet.getValue("uc").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getActivityDiagramURI(String adName) {
        String result = null;
        try {
            String query = "SELECT ?ad WHERE { "
                    + " ?ad rdf:type uwe2rdf:Activity . "
                    + " ?ad uwe2rdf:name \"" + adName + "\""
                    + " }";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                result = bindingSet.getValue("ad").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public SoftwareCase getSoftwareCase(String elementURI) {
        SoftwareCase result = null;
        try {
            String query = "SELECT ?name WHERE { "
                    + " <" + elementURI + "> uwe2rdf:hasSoftwareCase ?sc ."
                    + "  ?sc uwe2rdf:name ?name"
                    + " }";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String name = bindingSet.getValue("name").stringValue();
                result = new SoftwareCase(name);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<String[]> getActivityDiagramLabeledElements(String activityDiagram) {
        ArrayList<String[]> result = new ArrayList<String[]>();
//        if(activityDiagram.equalsIgnoreCase("uwe2rdf:SimpleMusicPortal_obj_162")) {
//            System.out.println("");
//        }
        try {
            String query1 = "SELECT ?node ?label  WHERE { "
                    + " <" + activityDiagram + "> uwe2rdf:containsNode ?node . "
                    + " ?node rdf:type uwe2rdf:InitialNode ."
                    + " ?node uwe2rdf:name ?label "
                    + " }";
            String query2 = "SELECT ?node ?label  WHERE { "
                    + " <" + activityDiagram + "> uwe2rdf:containsNode ?node . "
                    + " ?node rdf:type uwe2rdf:CallBehaviorAction ."
                    + " ?node uwe2rdf:name ?label "
                    + " }";
            String query3 = "SELECT ?node ?label  WHERE { "
                    + " <" + activityDiagram + "> uwe2rdf:containsEdge ?node . "
                    + " ?node uwe2rdf:hasGuard ?guard ."
                    + " ?guard uwe2rdf:hasBody ?label "
                    + " }";
            String query4 = "SELECT ?node ?label  WHERE { "
                    + " <" + activityDiagram + "> uwe2rdf:containsNode ?node . "
                    + " ?node rdf:type uwe2rdf:CentralBufferNode ."
                    + " ?node uwe2rdf:name ?label "
                    + " }";
            String query5 = "SELECT ?node ?label  WHERE { "
                    + " <" + activityDiagram + "> uwe2rdf:containsNode ?node . "
                    + " ?node rdf:type uwe2rdf:DecisionNode ."
                    + " ?node uwe2rdf:name ?label "
                    + " }";
            String query6 = "SELECT ?node ?label  WHERE { "
                    + " <" + activityDiagram + "> uwe2rdf:containsNode ?node . "
                    + " ?node rdf:type uwe2rdf:ActivityFinalNode ."
                    + " ?node uwe2rdf:name ?label "
                    + " }";

            String query7 = "SELECT ?node ?label  WHERE { "
                    + " <" + activityDiagram + "> uwe2rdf:containsNode ?node . "
                    + " ?node rdf:type uwe2rdf:InputPin ."
                    + " ?node uwe2rdf:name ?label "
                    + " }";
            String query8 = "SELECT ?node ?label  WHERE { "
                    + " <" + activityDiagram + "> uwe2rdf:containsNode ?node . "
                    + " ?node rdf:type uwe2rdf:OutputPin ."
                    + " ?node uwe2rdf:name ?label "
                    + " }";
//            String query9 = "SELECT ?node ?label  WHERE { "
//                    + " <" + activityDiagram + "> uwe2rdf:containsNode ?tempNode . "
//                    + "?tempNode uwe2rdf:hasArgument ?node ."
//                    + " ?node rdf:type uwe2rdf:InputPin ."
//                    + " ?node uwe2rdf:name ?label "
//                    + " }";

            //            String query6 = "SELECT ?node ?label  WHERE { "
//                    + " <" + activityDiagram + "> uwe2rdf:containsNode ?node . "
//                    + " ?node uwe2rdf:hasSource ?source . "
//                    + " ?source rdf:type uwe2rdf:OutputPin . "
//                    + " ?source uwe2rdf:name ?label "
//                    + " }";
//            String query8 = "SELECT ?node ?label  WHERE { "
//                    + " <" + activityDiagram + "> uwe2rdf:containsNode ?incoming . "
//                    + " ?incoming rdf:type uwe2rdf:ObjectFlow ."
//                    + " ?node rdf:type uwe2rdf:InputPin ."
//                    + " ?node uwe2rdf:hasIncoming ?incoming ."
//                    + " ?node uwe2rdf:name ?label "
//                    + " }";
//            String query9 = "SELECT ?node ?label  WHERE { "
//                    + " <" + activityDiagram + "> uwe2rdf:containsEdge ?flow . "
//                    + " ?flow rdf:type uwe2rdf:ObjectFlow ."
//                    + " ?node rdf:type uwe2rdf:InputPin ."
//                    + " ?flow uwe2rdf:hasTarget ?node ."
//                    + " ?node uwe2rdf:name ?label "
//                    + " }";
//            String query10 = "SELECT ?node ?label  WHERE { "
//                    + " <" + activityDiagram + "> uwe2rdf:containsEdge ?flow . "
//                    + " ?flow rdf:type uwe2rdf:ObjectFlow ."
//                    + " ?node rdf:type uwe2rdf:OutputPin ."
//                    + " ?flow uwe2rdf:hasSource ?node ."
//                    + " ?node uwe2rdf:name ?label "
//                    + " }";
//            String query10 = "SELECT ?node ?label  WHERE { "
//                    + " <" + activityDiagram + "> uwe2rdf:containsNode ?action . "
//                    + " ?action rdf:type uwe2rdf:CallBehaviorAction ."
//                    + " ?action uwe2rdf:hasResult ?node ."
//                    + " ?node rdf:type uwe2rdf:OutputPin ."
//                    + " ?node uwe2rdf:name ?label "
//                    + " }";
//
            String[] queries = {query1, query2, query3, query4, query5, query6,
                query7, query8};
            for (String query : queries) {
                TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
                //System.out.println(query);
                while (queryResult.hasNext()) {
                    BindingSet bindingSet = queryResult.next();
                    String uri = bindingSet.getValue("node").stringValue();
                    String label = bindingSet.getValue("label").stringValue();
                    if (activityDiagram.contains("SimpleMusicPortal_obj_162")) {
                        // System.out.println("uri: " + uri);
                        // System.out.println("label: " + label);
                    }
                    boolean exists = false;
                    for (String[] currentElement : result) {
                        if (currentElement[0].equalsIgnoreCase(uri)
                                && currentElement[1].equalsIgnoreCase(label)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        String[] element = new String[]{uri, label};
                        result.add(element);
                    }
                }
                queryResult.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result = removeExtraElements(result);
        return result;
    }

    private ArrayList<String[]> removeExtraElements(ArrayList<String[]> elements) {
        ArrayList<String[]> result = new ArrayList<String[]>();
        for (String[] element : elements) {
            String uri = element[0];
            if (isInputPin(uri)) {
                boolean isExtra = false;
                String correspondingOutputPin = getCorrespondingOutputPin(uri);
                if (correspondingOutputPin != null) {
                    for (String[] tempElement : elements) {
                        if (tempElement[0].equalsIgnoreCase(correspondingOutputPin)
                                && tempElement[1].equalsIgnoreCase(element[1])) {
                            isExtra = true;
                            break;
                        }
                    }
                }
                if (!isExtra) {
                    result.add(element);
                }
            } else {
                result.add(element);
            }
        }
        return result;
    }

    public ArrayList<String> getActivityDiagrams(String softwareCaseName) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            String query = "SELECT ?activity  WHERE { "
                    + " ?activity uwe2rdf:hasSoftwareCase ?softwareCase ."
                    + " ?softwareCase uwe2rdf:name \"" + softwareCaseName + "\" ."
                    + " ?activity rdf:type uwe2rdf:Activity "
                    + " }";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String activity = bindingSet.getValue("activity").stringValue();
                result.add(activity);
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getActivityDiagramByUsecaseName(String scName, String ucName) {
        String result = null;
        try {
            String query = "SELECT DISTINCT ?ac  WHERE { "
                    + " ?ac uwe2rdf:hasSoftwareCase ?softwareCase ."
                    + " ?softwareCase uwe2rdf:name \"" + scName + "\" ."
                    + " ?ac rdf:type uwe2rdf:Activity . "
                    + " ?ac uwe2rdf:name \"" + ucName + "\" "
                    + " }";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                result = bindingSet.getValue("ac").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    private ArrayList<String> getActorNames(String softwareCaseName) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            String query = "SELECT DISTINCT ?actorName  WHERE { "
                    + " ?actor uwe2rdf:hasSoftwareCase ?softwareCase ."
                    + " ?softwareCase uwe2rdf:name \"" + softwareCaseName + "\" ."
                    + " ?actor rdf:type uwe2rdf:Actor . "
                    + " ?actor uwe2rdf:name ?actorName "
                    + " }";

            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String usecaseName = bindingSet.getValue("actorName").stringValue();
                result.add(usecaseName);
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * private double computeSimilarityBetweenSoftwareCases(String
     * softwareCaseName1, String softwareCaseName2) { double similarity = 0.0;
     * ArrayList<String> usecaseNames1 = getUsecaseNames(softwareCaseName1);
     * ArrayList<String> actorNames1 = getActorNames(softwareCaseName1);
     * ArrayList<String> usecaseNames2 = getUsecaseNames(softwareCaseName2);
     * ArrayList<String> actorNames2 = getActorNames(softwareCaseName2); //for
     * increase speed during test, only use 4 first ArrayList<String> list1 =
     * new ArrayList<String>(); for (int i = 0; i < 4 && i <
     * usecaseNames1.size(); i++) { list1.add(usecaseNames1.get(i)); }
     *
     * ArrayList<String> list2 = new ArrayList<String>(); for (int i = 0; i < 4
     * && i < usecaseNames2.size(); i++) { list2.add(usecaseNames2.get(i)); }
     *
     * similarity = findBestMatch(list1, list2); return similarity; }
     */
    private double findBestMatch(ArrayList<String> usecaseNames1, ArrayList<String> usecaseNames2) {
        try {
            System.setErr(new PrintStream("err.log"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * usecaseNames1 = new ArrayList<String>(3); usecaseNames1.add("Create
         * Book"); usecaseNames1.add("Search Book"); usecaseNames1.add("Delete
         * Book"); usecaseNames2 = new ArrayList<String>(5);
         * usecaseNames2.add("Create Account"); usecaseNames2.add("Remove
         * Account"); usecaseNames2.add("Find Account");
         * usecaseNames2.add("Login"); usecaseNames2.add("Register");
         */
        Logger.log("Finding best match between usescaseList '" + usecaseNames1 + "' and '" + usecaseNames2 + "'");
        if (usecaseNames1.size() <= usecaseNames2.size()) {
            int[] matchIndexes = new int[usecaseNames1.size()];
            int[] bestMatchIndexes = new int[usecaseNames1.size()];
            double bestMatchSimilarity = -100;
            Arrays.fill(matchIndexes, -1);
            bestMatchSimilarity = assignMatch(0, matchIndexes, bestMatchIndexes, bestMatchSimilarity, usecaseNames1, usecaseNames2);
            Logger.log("best match is: " + Arrays.toString(bestMatchIndexes));
            Logger.log("best match Simialrity is: " + bestMatchSimilarity);
            return bestMatchSimilarity;
        } else {
            int[] matchIndexes = new int[usecaseNames2.size()];
            int[] bestMatchIndexes = new int[usecaseNames2.size()];
            double bestMatchSimilarity = -100;
            Arrays.fill(matchIndexes, -1);
            bestMatchSimilarity = assignMatch(0, matchIndexes, bestMatchIndexes, bestMatchSimilarity, usecaseNames2, usecaseNames1);
            Logger.log("best match is: " + Arrays.toString(bestMatchIndexes));
            Logger.log("best match Simialrity is: " + bestMatchSimilarity);
            return bestMatchSimilarity;
        }
    }

    private double assignMatch(int index, int[] matchIndexes, int[] bestMatchIndexes, double bestMatchSimilatity, ArrayList<String> usecaseNames1, ArrayList<String> usecaseNames2) {
        int length1 = usecaseNames1.size();
        int length2 = usecaseNames2.size();
        for (int i = 0; i < length2; i++) {
            if (isOk(index, i, matchIndexes)) {
                matchIndexes[index] = i;
                if (index == length1 - 1) {
                    double similarity = computeSimilarity(usecaseNames1, usecaseNames2, matchIndexes);
                    if (similarity > bestMatchSimilatity) {
                        bestMatchSimilatity = similarity;
                        for (int j = 0; j < matchIndexes.length; j++) {
                            bestMatchIndexes[j] = matchIndexes[j];
                        }
                    }
                } else {
                    bestMatchSimilatity = assignMatch(index + 1, matchIndexes, bestMatchIndexes, bestMatchSimilatity, usecaseNames1, usecaseNames2);
                }
            }
        }
        return bestMatchSimilatity;
    }

    private double computeSimilarity(ArrayList<String> usecaseNames1, ArrayList<String> usecaseNames2, int[] matchIndexes) {
        int length1 = usecaseNames1.size();
        int length2 = usecaseNames2.size();
        double result = 0;
        if (length1 <= length2) {
            for (int i = 0; i < matchIndexes.length; i++) {
                String usecaseName1 = usecaseNames1.get(i);
                String usecaseName2 = usecaseNames2.get(matchIndexes[i]);
                double similarity = WordnetSimilarityCalculator.computeUsecaseNameSimilarity(usecaseName1, usecaseName2);
                result += similarity;
            }
        } else {
            for (int i = 0; i < matchIndexes.length; i++) {
                String usecaseName1 = usecaseNames1.get(matchIndexes[i]);
                String usecaseName2 = usecaseNames2.get(i);
                double similarity = WordnetSimilarityCalculator.computeUsecaseNameSimilarity(usecaseName1, usecaseName2);
                result += similarity;
            }
        }

        return result;
    }

    private boolean isOk(int index, int matchIndex, int[] matchIndexes) {
        for (int i = 0; i < index; i++) {
            if (matchIndexes[i] == matchIndex) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> toUpperCase(ArrayList<String> words) {
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            word = word.toLowerCase();
            words.set(i, word);
        }
        return words;
    }

    /**
     * @return the inputUsecaseName
     */
    public String getInputUsecaseName() {
        return inputUsecaseName;
    }

    public static void main(String[] args) {
//        ModelGenerator f = new ModelGenerator(null, null);
//        ArrayList<String> concepts = f.getWordsTaggedAsNameWithSalt("I want to comment song.");
//        for (String concept : concepts) {
//            System.out.println(concept);
//        }
//        String[] keywords = {"user"};
        //      int RESULT_COUNT = 3;
        //    OntologySearcher searcher = new OntologySearcher(keywords, RESULT_COUNT);
        //  searcher.startSearch();
    }

    /**
     * @param inputUsecaseName the inputUsecaseName to set
     */
    public void setInputUsecaseName(String inputUsecaseName) {
        this.inputUsecaseName = inputUsecaseName;
    }

    public String[][] getExistingUseCasesInfo() {
        ArrayList<String> scNames = new ArrayList<String>();
        ArrayList<String> ucNames = new ArrayList<String>();
        try {
            String query = "SELECT ?scName ?ucName WHERE { "
                    + " ?uc rdf:type uwe2rdf:UseCase . "
                    + " ?uc uwe2rdf:hasSoftwareCase ?sc ."
                    + " ?sc uwe2rdf:name ?scName . "
                    + " ?uc uwe2rdf:name ?ucName "
                    + " } ORDER BY ?scName ?ucName";
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String scName = bindingSet.getValue("scName").stringValue();
                String ucName = bindingSet.getValue("ucName").stringValue();
                scNames.add(scName);
                ucNames.add(ucName);
            }
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        String[][] result = new String[ucNames.size()][2];
        for (int i = 0; i < ucNames.size(); i++) {
            result[i][0] = scNames.get(i);
            result[i][1] = ucNames.get(i);
        }
        return result;
    }

    public ArrayList<String> getAssociatedClassURIsFromClassDiagram(String classURI) {
        ArrayList<String> result = new ArrayList<String>();
        String query = " SELECT ?class2 WHERE {"
                + " ?class2 rdf:type uwe2rdf:Class  ."
                + " <" + classURI + "> uwe2rdf:containsAttribute ?attrib ."
                + " ?attrib rdf:type uwe2rdf:Property ."
                + " ?attrib uwe2rdf:type ?class2 ."
                + "}";
        try {
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String uri = bindingSet.getValue("class2").stringValue();
                result.add(uri);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> findAssociatedClassURIsFromModelRepository(String inputSoftwareCaseURI, String concept) {
        ArrayList<String> result = new ArrayList<String>();
        String query = " SELECT ?class2 WHERE {"
                + " {"
                + " ?class1 rdf:type uwe2rdf:Class  ."
                + " ?class2 rdf:type uwe2rdf:Class  ."
                + " ?class1 uwe2rdf:containsAttribute ?attrib ."
                + " ?attrib rdf:type uwe2rdf:Property ."
                + " ?attrib uwe2rdf:type ?class2 ."
                + " ?class1 uwe2rdf:name ?name1 ."
                + " ?class2 uwe2rdf:name ?name2 ."
                + " ?class1 uwe2rdf:hasSoftwareCase ?sc ."
                + " FILTER(regex(str(?name1), \"" + concept + "\", \"i\")) ."
                + " FILTER(str(?sc) != \"" + inputSoftwareCaseURI + "\") ."
                + " FILTER(str(?name1) != str(?name2))"
                + "} UNION {"
                + " ?class1 rdf:type uwe2rdf:Class  ."
                + " ?class2 rdf:type uwe2rdf:Class  ."
                + " ?class2 uwe2rdf:containsAttribute ?attrib ."
                + " ?attrib rdf:type uwe2rdf:Property ."
                + " ?attrib uwe2rdf:type ?class1 ."
                + " ?class1 uwe2rdf:name ?name1 ."
                + " ?class2 uwe2rdf:name ?name2 ."
                + " ?class1 uwe2rdf:hasSoftwareCase ?sc ."
                + " FILTER(regex(str(?name1), \"" + concept + "\", \"i\")) ."
                + " FILTER(str(?sc) != \"" + inputSoftwareCaseURI + "\") ."
                + " FILTER(str(?name1) != str(?name2))"
                + "}"
                + "}";

        /*
         * String query = "SELECT ?className2 WHERE { " + " ?property
         * rdfs:domain ?class1 . " + " ?property rdfs:range ?class2 . " + "
         * ?class1 uwe2rdf:hasSoftwareCase ?sc ." + " ?class2
         * uwe2rdf:hasSoftwareCase ?sc ." + " ?class1 uwe2rdf:name \"" + concept
         * + "\" . " + " ?class2 uwe2rdf:name ?className2 . " + "
         * FILTER(str(?sc) != \"" + inputSoftwareCaseURI + "\")" + " } ";
         */
        try {
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String uri = bindingSet.getValue("class2").stringValue();
                result.add(uri);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private ArrayList<String> findClassURIFromOntologyRepository(String concept) {
        ArrayList<String> result = new ArrayList<String>();
        String query = "SELECT DISTINCT ?class WHERE { "
                + " {"
                + "?class rdf:type owl:Class ."
                + "OPTIONAL {?class rdfs:label ?label } . "
                + " FILTER((bound(?label) && regex(str(?label), \"^" + concept + "$\", \"i\")) || "
                + " (regex(str(?class), \"#" + concept + "$\", \"i\")) ) "
                + " } UNION {"
                + "?class rdf:type owl:Class ."
                + "OPTIONAL {?class dc:title ?label } . "
                + " FILTER((bound(?label) && regex(str(?label), \"^" + concept + "$\", \"i\")) || "
                + " (regex(str(?class), \"#" + concept + "$\", \"i\")) ) "
                + "} UNION {"
                + "?class rdf:type rdfs:Class ."
                + "OPTIONAL {?class rdfs:label ?label } . "
                + " FILTER((bound(?label) && regex(str(?label), \"^" + concept + "$\", \"i\")) || "
                + " (regex(str(?class), \"#" + concept + "$\", \"i\")) ) "
                + " } UNION {"
                + "?class rdf:type rdfs:Class ."
                + "OPTIONAL {?class dc:title ?label } . "
                + " FILTER((bound(?label) && regex(str(?label), \"^" + concept + "$\", \"i\")) || "
                + " (regex(str(?class), \"#" + concept + "$\", \"i\")) ) "
                + "}"
                + "}";
        try {
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getOntologyRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String uri = bindingSet.getValue("class").stringValue();
                result.add(uri);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> findClassURIFromCycRepository(String concept) {
        ArrayList<String> result = new ArrayList<String>();
        String query = "SELECT ?class WHERE { "
                + " ?class rdf:type owl:Class ."
                + "OPTIONAL {?class rdfs:label ?label } . "
                + " FILTER((bound(?label) && regex(str(?label), \"^" + concept + "$\", \"i\")) || "
                + " (regex(str(?class), \"#" + concept + "$\", \"i\")) ) "
                + " } ";
        try {
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getCycRepositoryConnection(), query);
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                String uri = bindingSet.getValue("class").stringValue();
                result.add(uri);
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> findAssociatedClassURIsFromOntologyRepository(String concept) {
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> classURIs = findClassURIFromOntologyRepository(concept);
        for (String classURI : classURIs) {
            String query = "SELECT ?class2 WHERE { "
                    + " {"
                    + " ?property rdfs:domain <" + classURI + "> . "
                    + " ?property rdfs:range ?class2 . "
                    + " } UNION {"
                    + " ?property rdfs:range <" + classURI + "> . "
                    + " ?property rdfs:domain ?class2 . "
                    + " } UNION {"
                    + " <" + classURI + "> rdfs:subClassOf ?super ."
                    + " ?property rdfs:domain ?super . "
                    + " ?property rdfs:range ?class2 . "
                    + " } UNION {"
                    + " <" + classURI + "> rdfs:subClassOf ?super ."
                    + " ?property rdfs:range ?super . "
                    + " ?property rdfs:domain ?class2 . "
                    + " }"
                    + " }";
            try {
                TupleQueryResult queryResult = RepositoryManager.executeQuery(getOntologyRepositoryConnection(), query);
                while (queryResult.hasNext()) {
                    BindingSet bindingSet = queryResult.next();
                    String uri = bindingSet.getValue("class2").stringValue();
                    result.add(uri);
                }
                queryResult.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public ArrayList<String> findAssociatedClassURIsFromCycRepository(String concept) {
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> classURIs = findClassURIFromCycRepository(concept);
        for (String classURI : classURIs) {
            String query = "SELECT ?class2 WHERE { "
                    + " ?property rdfs:domain <" + classURI + "> . "
                    + " ?property rdfs:range ?class2 . "
                    + " } ";
            try {
                TupleQueryResult queryResult = RepositoryManager.executeQuery(getCycRepositoryConnection(), query);
                while (queryResult.hasNext()) {
                    BindingSet bindingSet = queryResult.next();
                    String uri = bindingSet.getValue("class2").stringValue();
                    result.add(uri);
                }
                queryResult.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public ArrayList<UMLAttribute> getClassAttributesFromCyc(String classURI) {
        ArrayList<UMLAttribute> attributes = new ArrayList<UMLAttribute>();
        String query1 = "SELECT ?attribute ?label WHERE { "
                + " ?attribute rdfs:domain <" + classURI + "> ."
                + " ?attribute rdfs:label ?label"
                + " } ";
        String query2 = "SELECT ?attribute ?label WHERE { "
                + " <" + classURI + "> rdfs:subClassOf ?superClass ."
                + " ?attribute rdfs:domain ?superClass ."
                + " ?attribute rdfs:label ?label"
                + " } ";
        String[] queries = {query1, query2};
        for (String query : queries) {
            try {
                TupleQueryResult queryResult = RepositoryManager.executeQuery(getCycRepositoryConnection(), query);
                while (queryResult.hasNext()) {
                    BindingSet bindingSet = queryResult.next();
                    String attributeURI = bindingSet.getValue("attribute").stringValue();
                    String attributeLabel = bindingSet.getValue("label").stringValue();
                    attributes.add(new UMLAttribute(attributeLabel, attributeURI));
                }
                queryResult.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return attributes;
    }

    public ArrayList<String> getSameAsURIsFromCyc(String uri) {
        ArrayList<String> uris = new ArrayList<String>();
        String query1 = "SELECT ?uri WHERE { "
                + " <" + uri + "> owl:sameAs ?uri . "
                + " } ";
        String query2 = "SELECT ?uri WHERE { "
                + " ?uri owl:sameAs <" + uri + "> . "
                + " } ";
        String[] queries = {query1, query2};
        for (String query : queries) {
            try {
                TupleQueryResult queryResult = RepositoryManager.executeQuery(getCycRepositoryConnection(), query);
                while (queryResult.hasNext()) {
                    BindingSet bindingSet = queryResult.next();
                    String sameAsURI = bindingSet.getValue("uri").stringValue();
                    uris.add(sameAsURI);
                }
                queryResult.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return uris;
    }

    public String getClassLabelFromOntologyRepository(String classURI) {
        String label = null;
        String query = "SELECT ?label WHERE { "
                + " <" + classURI + "> rdfs:label ?label . "
                + " } ";
        try {
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getOntologyRepositoryConnection(), query);
            if (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                label = bindingSet.getValue("label").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (label == null) {
            int index = classURI.lastIndexOf('#');
            label = classURI.substring(index + 1);
        }
        return label;
    }

    public String getClassLabelFromModelRepository(String classURI) {
        String label = null;
        String query = "SELECT ?label WHERE { "
                + " <" + classURI + "> uwe2rdf:name ?label . "
                + " } ";
        try {
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getModelRepositoryConnection(), query);
            if (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                label = bindingSet.getValue("label").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (label == null) {
            int index = classURI.lastIndexOf('#');
            label = classURI.substring(index + 1);
        }
        return label;
    }

    public String getClassLabelFromCycRepository(String classURI) {
        String label = null;
        String query = "SELECT ?label WHERE { "
                + " <" + classURI + "> rdfs:label ?label . "
                + " } ";
        try {
            TupleQueryResult queryResult = RepositoryManager.executeQuery(getCycRepositoryConnection(), query);
            if (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                label = bindingSet.getValue("label").stringValue();
            }
            queryResult.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (label == null) {
            int index = classURI.lastIndexOf('#');
            if (index != -1) {
                label = classURI.substring(index + 1);
            } else {
                index = classURI.lastIndexOf('/');
                label = classURI.substring(index + 1);
            }

        }
        return label;
    }

    public ArrayList<String> getClassLabelsFromCycRepository(ArrayList<String> classURIs) {
        ArrayList<String> result = new ArrayList<String>();
        for (String uri : classURIs) {
            result.add(getClassLabelFromCycRepository(uri));
        }
        return result;
    }

    public ArrayList<String> getClassLabelFromModelRepository(ArrayList<String> classURIs) {
        ArrayList<String> result = new ArrayList<String>();
        for (String uri : classURIs) {
            result.add(getClassLabelFromModelRepository(uri));
        }
        return result;
    }

    public ArrayList<String> getClassLabelFromOntologyRepository(ArrayList<String> classURIs) {
        ArrayList<String> result = new ArrayList<String>();
        for (String uri : classURIs) {
            result.add(getClassLabelFromOntologyRepository(uri));
        }
        return result;
    }

    public void findOntologiesFromWeb(String[] keywords) {
        OntologySearcher searcher = new OntologySearcher(getOntologyRepositoryConnection());
        searcher.startSearch(keywords, 10);
    }

    /**
     * @return the modelRepositoryConnection
     */
    public RepositoryConnection getModelRepositoryConnection() {
        return modelRepositoryConnection;
    }

    /**
     * @param modelRepositoryConnection the modelRepositoryConnection to set
     */
    public void setModelRepositoryConnection(RepositoryConnection modelRepositoryConnection) {
        this.modelRepositoryConnection = modelRepositoryConnection;
    }

    /**
     * @return the ontologyRepositoryConnection
     */
    public RepositoryConnection getOntologyRepositoryConnection() {
        return ontologyRepositoryConnection;
    }

    /**
     * @param ontologyRepositoryConnection the ontologyRepositoryConnection to
     * set
     */
    public void setOntologyRepositoryConnection(RepositoryConnection ontologyRepositoryConnection) {
        this.ontologyRepositoryConnection = ontologyRepositoryConnection;
    }

    /**
     * @return the cycRepositoryConnection
     */
    public RepositoryConnection getCycRepositoryConnection() {
        return cycRepositoryConnection;
    }

    /**
     * @param cycRepositoryConnection the cycRepositoryConnection to set
     */
    public void setCycRepositoryConnection(RepositoryConnection cycRepositoryConnection) {
        this.cycRepositoryConnection = cycRepositoryConnection;
    }

    /**
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * @param salt the salt to set
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }
}
