/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator;

import ir.ac.um.wtlab.modelgenerator.similarity.UsecaseSimilarityCalculator;
import ir.ac.um.wtlab.modelgenerator.util.Utils;
import java.util.*;
import ir.ac.um.wtlab.modelgenerator.similarity.WordnetSimilarityCalculator;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Home
 */
public class ActivityDiagramAdapter {

    private ModelGenerator modelGenerator;
    private String inputSoftwareCaseURI;
    private String adapteeSoftwareCaseURI;
    private String inputUsecaseURI;
    private String adapteeActivityDiagramURI;
    private ArrayList<String> adapteeActivityDiagramConcepts;
    private ArrayList<String> inputUsecaseConcepts;
    private String adapteeActivityDiagramName;
    private String inputUsecaseName;
    private String inputSoftwareCaseName;
    private String adapteeSoftwareCaseName;
    private JTextPane textPane;
    private StyledDocument doc;

    public ActivityDiagramAdapter(ModelGenerator modelGenerator, JTextPane textPane) {
        this.modelGenerator = modelGenerator;
        this.textPane = textPane;
        this.doc = textPane.getStyledDocument();
        addStylesToDocument();
    }

    public void adapt(String inputSoftwareCaseURI, String inputUsecaseURI, String adapteeSoftwareCaseURI, String adapteeActivityDiagramURI) {
        this.inputSoftwareCaseURI = inputSoftwareCaseURI;
        this.inputUsecaseURI = inputUsecaseURI;
        this.adapteeSoftwareCaseURI = adapteeSoftwareCaseURI;
        this.adapteeActivityDiagramURI = adapteeActivityDiagramURI;
        this.adapteeActivityDiagramName = modelGenerator.getElementName(adapteeActivityDiagramURI);
        this.inputUsecaseName = modelGenerator.getElementName(inputUsecaseURI);
        this.adapteeActivityDiagramConcepts = modelGenerator.getWordsTaggedAsNameWithSalt(adapteeActivityDiagramName);
        this.inputUsecaseConcepts = modelGenerator.getWordsTaggedAsNameWithSalt(inputUsecaseName);
        this.inputSoftwareCaseName = modelGenerator.getSoftwareCase(inputUsecaseURI).getName();
        this.adapteeSoftwareCaseName = modelGenerator.getSoftwareCase(adapteeActivityDiagramURI).getName();

        log("Adaptation started for adapting activity diagram ", "regular");
        log(adapteeActivityDiagramName, "blue");
        log(" from software case ", "regular");
        log(adapteeSoftwareCaseName, "blue");
        log("\nto create activity diagram for use case ", "regular");
        log(inputUsecaseName + "\n", "blue");
        log("Adaptation started.\n", "regular");
        adaptAnnotations();
        System.out.println("Adaptation completed");
        log("Adaptation finished.\n", "regular");
    }

    private void adaptAnnotations() {
        ArrayList<Annotation> annotations = modelGenerator.getActivityDiagramAnnotations(adapteeActivityDiagramURI);

        int index = 0;;
        log("------------------------\n", "regular");
        log("-------------  MODIFICATIONS  -----------\n", "regular");
        for (Annotation annotation : annotations) {
            index++;
            System.out.println("------------------------------------------");

            System.out.println(index + " ) " + annotation);
            switch (annotation.getType()) {
                case 1:
                    adaptClassAnnotation(annotation);
                    break;
                case 2:
                    adaptAttributeAnnotation(annotation);
                    break;
                case 3:
                    adaptIndirectClassAnnotation(annotation);
                    break;
                case 4:
                    adaptIndirectAttributeAnnotation(annotation);
                    break;
                case 5:
                    adaptActorAnnotation(annotation);
                    break;
            }
            System.out.println("------------------------------------------");

        }
        log("------------------------\n", "regular");
    }

    private void adaptActorAnnotation(Annotation annotation) {
        String annotatedWord = annotation.getAnnotatedWord();
        ArrayList<Actor> actors = modelGenerator.findActors(inputSoftwareCaseName, inputUsecaseName);
        for (Actor actor : actors) {
            String label1 = annotation.getLabel();
            String label2 = label1.replace(annotatedWord, actor.getName());
            System.out.println(">>" + label2);
            logLabelModification(label1, label2, annotatedWord, actor.getName());
        }
    }

    private void adaptAttributeAnnotation(Annotation annotation) {
        String annotatedWord = annotation.getAnnotatedWord();
        String target = annotation.getTarget();
        String className = modelGenerator.getAttributeOwnerClassName(target);
        String className2 = modelGenerator.getAttributeTypeClassName(target);
        boolean adapted = false;
        for (String acConcept : adapteeActivityDiagramConcepts) {
            if (className.equalsIgnoreCase(acConcept) || (className2 != null && className2.equalsIgnoreCase(acConcept))) {
                for (String ucConcept : inputUsecaseConcepts) {
                    ArrayList<UMLAttribute> equivalentAttributes = findEquivalentAttribute(target, ucConcept);
                    int i = 0;

                    for (UMLAttribute attribute : equivalentAttributes) {
                        i++;
                        if (i > 5) {
                            break;
                        }
                        String label1 = annotation.getLabel();
                        String label2 = label1.replace(annotatedWord, attribute.getName());
                        adapted = true;
                        System.out.println(label1 + " >>> " + label2);
                        logLabelModification(label1, label2, annotatedWord, attribute.getName());
                    }
                }
            }
        }
        if (!adapted) {
            adaptIndirectAttributeAnnotation(annotation);
        }
    }

    private void adaptClassAnnotation(Annotation annotation) {
        String annotatedWord = annotation.getAnnotatedWord();
        boolean adapted = false;
        for (int i = 0; i < adapteeActivityDiagramConcepts.size(); i++) {
            String word = "";
            int count = adapteeActivityDiagramConcepts.size();
            for (int j = 0; (j < ActivityDiagramAnnotator.MAX_N_GRAM && ((i + j) < count)); j++) {
                word += adapteeActivityDiagramConcepts.get(i + j);
                if (word.equalsIgnoreCase(annotatedWord)) {
                    for (String ucConcept : inputUsecaseConcepts) {
                        String label1 = annotation.getLabel();
                        String label2 = label1.replace(annotatedWord, Utils.makeFirstLetterUppercase(ucConcept));
                        adapted = true;
                        System.out.println(label1 + " --> " + label2);
                        logLabelModification(label1, label2, annotatedWord, Utils.makeFirstLetterUppercase(ucConcept));
                    }
                }
            }
        }
        if (!adapted) {
            adaptIndirectClassAnnotation(annotation);
        }
    }

    private void logLabelModification(String label1, String label2, String annotatedWord, String term) {
        int index = label1.indexOf(annotatedWord);
        String prefix = label1.substring(0, index);
        String postfix = label1.substring(index);
        log(prefix, "black");
        log(postfix, "green");
        log(" ---> ", "regular");
        index = label2.indexOf(term);
        prefix = label2.substring(0, index);
        postfix = label2.substring(index);
        log(prefix, "black");
        log(postfix, "green");
        log("\n", "regular");
    }

    private void adaptIndirectClassAnnotation(Annotation annotation) {
        String targetClassURI = annotation.getTarget();
        ArrayList<UMLClass> classes = modelGenerator.listClassesFromContentModel(adapteeSoftwareCaseURI);
        ArrayList<UMLClass> otherClasses = modelGenerator.listClassesFromOtherPackages(adapteeSoftwareCaseURI);
        classes.addAll(otherClasses);
        for (String concept : adapteeActivityDiagramConcepts) {
            for (UMLClass cls : classes) {
                if (cls.getName().equalsIgnoreCase(concept)) {
                    String ucClassURI = cls.getURL();
                    if (modelGenerator.hasAssociation(ucClassURI, targetClassURI)) {
                        //System.out.println("ASSOCIATION " + cls.getName() +" and " +  modelGenerator.getElementName(targetClassURI));
                        ArrayList<String> classLabels = findAssociatedClassLabels();
                        System.out.println("Associate Class Labels: " + classLabels);
                        double max = 0.0;
                        String bestCandidate = null;
                        for (String classLabel : classLabels) {
                            
                            //String name = modelGenerator.getClassLabelFromOntologyRepository(classURI);
//                            System.out.println("Candidate: " + classLabel);

                            //to do: consider also other concepts of the input use case
                            if (!inputUsecaseConcepts.isEmpty()) {
                                String ucConcept = inputUsecaseConcepts.get(0);
                                if(classLabel.equalsIgnoreCase(ucConcept)) {
                                    continue;
                                }
                                double similarity = computeScore(classLabel, ucConcept);
                                System.out.println("SIMILARITY " + classLabel + " " + ucConcept + " is " + similarity);
                                if (similarity > max) {
                                    max = similarity;
                                    bestCandidate = classLabel;
                                }
                            }
                            //  System.out.println("candidate: " + classLabel + " similarity: " + similarity);
                        }
                        String label1 = annotation.getLabel();
                        String annotatedWord = annotation.getAnnotatedWord();
                        String label2 = label1.replace(annotatedWord, Utils.makeFirstLetterUppercase(bestCandidate));
                        System.out.println(label1 + " ------> " + label2);
                        logLabelModification(label1, label2, annotatedWord, Utils.makeFirstLetterUppercase(bestCandidate));
                        //System.out.println("best candiate: " + bestCandidate + " similarity: " + max);
                    }
                }

            }
        }

    }

    private double computeScore(String label, String concept) {
        double similarity = 0.0;
        concept = Utils.prepare(concept);
        label = Utils.prepare(label);
        String[] conceptWords = concept.split(" ");
        String[] labelWords = label.split(" ");
        
        ArrayList<String> terms1 = new ArrayList<String>(conceptWords.length);
        for(String item: conceptWords) {
            terms1.add(item);
        }
        ArrayList<String> terms2 = new ArrayList<String>(labelWords.length);
        for(String item: labelWords) {
            terms2.add(item);
        }
        
        UsecaseSimilarityCalculator calculator = new UsecaseSimilarityCalculator(this.modelGenerator);
        similarity = calculator.getBestMatchSimilarity(terms1, terms2, "n");
        return similarity;
        /*
        double max = 0.0;
        int count = 0;
        for (String conceptWord : conceptWords) {
            for (String labelWord : labelWords) {
                if (!inSkipList(labelWord)) {

                    double temp = WordnetSimilarityCalculator.getSimilarity(conceptWord, labelWord);
                    if (temp > max) {
                        max = temp;
                    }
                    if (temp > 0) {
                        similarity += temp;
                        count++;
                    }
                }
            }
        }
//        System.out.println(label + "\tmax: " + max + "\tsimilarity: " + similarity);
        double result = similarity / count;
        //System.out.println(concept + " __ " + label + ": " + result);
        return result;// (conceptWords.length * labelWords.length);
        * 
        */
    }

    private static boolean inSkipList(String word) {
        String[] skipList = {"to", "or", "a", "an", "with", "by", "thing", "has", "on", "of"};
        for (String skipWord : skipList) {
            if (word.equalsIgnoreCase(skipWord)) {
                return true;
            }
        }
        return false;
    }

    private void adaptIndirectAttributeAnnotation(Annotation annotation) {
        String target = annotation.getTarget();
        String classURI = modelGenerator.getAttributeOwnerClassURI(target);
        if (classURI == null) {
            classURI = modelGenerator.getAttributeTypeClassURI(target);
        }
        if (classURI == null) {
            return;
        }
        ArrayList<UMLClass> classes = modelGenerator.listClassesFromContentModel(adapteeSoftwareCaseURI);
        ArrayList<UMLClass> otherClasses = modelGenerator.listClassesFromOtherPackages(adapteeSoftwareCaseURI);
        classes.addAll(otherClasses);
        for (String concept : adapteeActivityDiagramConcepts) {
            for (UMLClass cls : classes) {
                if (cls.getName().equalsIgnoreCase(concept)) {
                    String ucClassURI = cls.getURL();
                    //if (modelGenerator.hasAssociation(ucClassURI, classURI)) {
                    //System.out.println("ASSOCIATION " + cls.getName() +" and " +  modelGenerator.getElementName(targetClassURI));
                    ArrayList<Element> classURIs = findAssociatedClassURIs();
                    /**
                     * farshad to do dar dastoore bala, classURI momken ast as
                     * ontologyrepository ya cycrepository bedast amade bashad,
                     * banabar in dar dastoor paiin ham bayad labele an ra as
                     * haman reopsitory begiram
                     */
                    for (Element element : classURIs) {
                        //find an attribute of class uri for adapting the attribute
                        String name = null;
                        switch (element.getRepository()) {
                            case 1:
                                name = modelGenerator.getClassLabelFromModelRepository(element.getUri());
                                break;
                            case 2:
                                name = modelGenerator.getClassLabelFromCycRepository(element.getUri());
                                break;
                            case 3:
                                name = modelGenerator.getClassLabelFromOntologyRepository(element.getUri());
                                break;
                        }

                        ArrayList<UMLAttribute> equivalentAttributes = findEquivalentAttribute(target, name);

                        double max = 0.0;
                        String bestCandidate = null;
                        for (UMLAttribute attribute : equivalentAttributes) {
                            //String name = modelGenerator.getClassLabelFromOntologyRepository(classURI);
//                            System.out.println("Candidate: " + classLabel);

                            double similarity = computeScore(attribute.getName(), annotation.getAnnotatedWord());
                            if (similarity > max) {
                                max = similarity;
                                bestCandidate = attribute.getName();
                            }
                            System.out.println("candidate: " + attribute.getName() + " similarity: " + similarity);
                        }
                        if (bestCandidate != null) {
                            String label1 = annotation.getLabel();
                            String annotatedWord = annotation.getAnnotatedWord();
                            String label2 = label1.replace(annotatedWord, Utils.makeFirstLetterUppercase(bestCandidate));
                            System.out.println(label1 + " ------> " + label2);
                            logLabelModification(label1, label2, annotatedWord, Utils.makeFirstLetterUppercase(bestCandidate));
                        }
                    }
                    //}
                }
            }
        }
    }

    private ArrayList<String> findAssociatedClassLabels() {
        ArrayList<String> result = new ArrayList<String>();
        for (String concept : inputUsecaseConcepts) {
            result = modelGenerator.findAssociatedClassURIsFromModelRepository(inputSoftwareCaseURI, concept);
            if (result.isEmpty()) {
//                result = modelGenerator.findAssociatedClassURIsFromCycRepository(concept);
                //              if (result.isEmpty()) {
                result = modelGenerator.findAssociatedClassURIsFromOntologyRepository(concept);
                if (result.isEmpty()) {
                    modelGenerator.findOntologiesFromWeb(new String[]{concept});
                    result = modelGenerator.findAssociatedClassURIsFromOntologyRepository(concept);
                }
                if (!result.isEmpty()) {
                    return modelGenerator.getClassLabelFromOntologyRepository(result);
                }
                //            } else {
                //            return modelGenerator.getClassLabelsFromCycRepository(result);
                //          }
            } else {
                return modelGenerator.getClassLabelFromModelRepository(result);
            }
        }

        return null;
    }

    private ArrayList<Element> findAssociatedClassURIs() {
        ArrayList<Element> result = new ArrayList<Element>();

        for (String concept : inputUsecaseConcepts) {
            int repository = 0;
            ArrayList<String> uris = modelGenerator.findAssociatedClassURIsFromModelRepository(inputSoftwareCaseURI, concept);
            if (uris.isEmpty()) {
                uris = modelGenerator.findAssociatedClassURIsFromCycRepository(concept);
                if (uris.isEmpty()) {
                    uris = modelGenerator.findAssociatedClassURIsFromOntologyRepository(concept);
                    if (uris.isEmpty()) {
                        modelGenerator.findOntologiesFromWeb(new String[]{concept});
                        uris = modelGenerator.findAssociatedClassURIsFromOntologyRepository(concept);
                    } else {
                        repository = 3;
                    }
                } else {
                    repository = 2;
                }
            } else {
                repository = 1;
            }
            for (String uri : uris) {
                result.add(new Element(uri, repository));
            }
        }

        return result;
    }

    private ArrayList<UMLClass> resolveClassFromModelRepository(String inputUsecaseConcept) {
        ArrayList<UMLClass> classes = modelGenerator.findClassInModelRepository(inputUsecaseConcept, inputSoftwareCaseURI);
        return classes;
    }

    private ArrayList<UMLClass> resolveClassFromOntologyRepository(String concept) {
        ArrayList<UMLClass> classes = modelGenerator.findClassInOntologyRepository(concept);
        ArrayList<UMLClass> classes2 = modelGenerator.findClassInOntologyRepository2(concept);
        classes.addAll(classes2);
        return classes;
    }

    private ArrayList<UMLClass> resolveClassFromCycRepository(String concept) {
        ArrayList<String> uris = modelGenerator.findClassURIFromCycRepository(concept);
        ArrayList<String> temp = new ArrayList<String>();
        for (String uri : uris) {
            ArrayList<String> sameAsURIs = modelGenerator.getSameAsURIsFromCyc(uri);
            for (String sameAsURI : sameAsURIs) {
                if (!uris.contains(sameAsURI) && !temp.contains(sameAsURI)) {
                    temp.add(sameAsURI);
                }
            }
        }
        uris.addAll(temp);

        ArrayList<UMLClass> classes = new ArrayList<UMLClass>();
        ArrayList<String> classLabels = modelGenerator.getClassLabelsFromCycRepository(uris);
        for (int i = 0; i < classLabels.size(); i++) {
            //Farshad to do 
            UMLClass umlClass = new UMLClass(classLabels.get(i), uris.get(i));
            ArrayList<UMLAttribute> attribtues = modelGenerator.getClassAttributesFromCyc(uris.get(i));
            umlClass.addAttributes(attribtues);
            classes.add(umlClass);
        }
        return classes;
    }

    private ArrayList<UMLAttribute> findEquivalentAttribute(String attributeURI, String inputUsecaseConcept) {
        ArrayList<UMLAttribute> result = new ArrayList<UMLAttribute>();
        UMLAttribute sourceAttribute = modelGenerator.createAttribute(attributeURI);

        ArrayList<UMLAttribute> candidateAttributes = new ArrayList<UMLAttribute>();
        ArrayList<UMLClass> classes = resolveClassFromModelRepository(inputUsecaseConcept);
        ArrayList<UMLClass> classes2 = resolveClassFromOntologyRepository(inputUsecaseConcept);
        //ArrayList<UMLClass> classes3 = resolveClassFromCycRepository(inputUsecaseConcept);
        classes.addAll(classes2);
        for (UMLClass clas : classes) {
            candidateAttributes.addAll(clas.getAttributes());
        }
        candidateAttributes.addAll(modelGenerator.getClassAttributesFromLOD(inputUsecaseConcept));
        for (UMLAttribute attribute : candidateAttributes) {
            if (sourceAttribute == null || isSimilar(sourceAttribute, attribute)) {
                result = new ArrayList<UMLAttribute>();
                result.add(attribute);
                return result;
            } else if (sourceAttribute == null || hasSimilarType(sourceAttribute, attribute)) {
                result.add(attribute);
            } else if (sourceAttribute == null || isSimilar2(sourceAttribute, attribute)) {
                result.add(attribute);
            }
        }
        if (sourceAttribute == null) {
            return result;
        }
        //sort equivalent attributes based on their textual similarity
        /*
         * for (int i = 0; i < result.size(); i++) { for (int j = i + 1; j <
         * result.size(); j++) { UMLAttribute attribute1 = result.get(j - 1);
         * UMLAttribute attribute2 = result.get(j); String attribName1 =
         * attribute1.getName().toLowerCase(); String attribName2 =
         * attribute2.getName().toLowerCase(); String sourceAttribName =
         * sourceAttribute.getName().toLowerCase(); double similarity1 = 0.0; if
         * (sourceAttribName.contains(attribName1)) { similarity1 = 1.0; } else
         * { similarity1 = computeScore(attribName1, sourceAttribName); } double
         * similarity2 = 0.0; if (sourceAttribName.contains(attribName2)) {
         * similarity2 = 1.0; } else { similarity2 = computeScore(attribName2,
         * sourceAttribName); } if (similarity1 < similarity2) { String temp =
         * attribute1.getName(); attribute1.setName(attribute2.getName());
         * attribute2.setName(temp); temp = attribute1.getType();
         * attribute1.setType(attribute2.getType()); attribute2.setType(temp);
         * temp = attribute1.getURL(); attribute1.setURL(attribute2.getURL());
         * attribute2.setURL(temp); temp = attribute1.getVisibilility();
         * attribute1.setVisibility(attribute2.getVisibilility());
         * attribute2.setVisibility(temp); } } }
         */

        return result;
    }

    private boolean isSimilar(UMLAttribute attribute1, UMLAttribute attribute2) {
        String attributeName1 = attribute1.getName();
        String attributeName2 = attribute2.getName();
        //    System.out.println("isSimilar " + attributeName1 + ", " + attributeName2);
        attributeName2 = attributeName2.replace('.', ' ');
        attributeName2 = attributeName2.replace('_', ' ');

        String str2 = Utils.prepare(attributeName2);
        ArrayList<String> words2 = Utils.split(str2);
        for (String word : words2) {
            if (word.equalsIgnoreCase(attributeName1)) {
                attribute2.setName(word);
                //          System.out.println("TRUE");
                return true;
            }
        }
//        System.out.println("FALSE");
        return false;
    }

    private boolean isSimilar2(UMLAttribute attribute1, UMLAttribute attribute2) {
        String attributeName1 = attribute1.getName();
        String attributeName2 = attribute2.getName();
        //    System.out.println("isSimilar " + attributeName1 + ", " + attributeName2);
        attributeName2 = attributeName2.replace('.', ' ');
        attributeName2 = attributeName2.replace('_', ' ');

        String str1 = Utils.prepare(attributeName1);
        ArrayList<String> words1 = Utils.split(str1);
        String str2 = Utils.prepare(attributeName2);
        ArrayList<String> words2 = Utils.split(str2);

        for (String word2 : words2) {
            for (String word1 : words1) {
                if (word2.equalsIgnoreCase(word1)) {
                    //attribute2.setName(word1);
                    //          System.out.println("TRUE");
                    return true;
                }
            }
        }
//        System.out.println("FALSE");
        return false;
    }

    private boolean hasSimilarType(UMLAttribute attribute1, UMLAttribute attribute2) {
        //      System.out.println("hasSimilarType");
        String type1 = attribute1.getType();
        if (type1 == null) {
            return true;
        }
        type1 = type1.substring(type1.lastIndexOf('#') + 1);
        String type2 = attribute2.getType();
        if (type2 == null) {
            return true;
        }
        type2 = type2.substring(type2.lastIndexOf('#') + 1);
        //    System.out.println("type1: " + type1 + ", type2 " + type2);
        return type1.equalsIgnoreCase(type2);
    }

    protected void addStylesToDocument() {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("green", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, new Color(34, 177, 76));

        s = doc.addStyle("blue", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.BLUE);

        s = doc.addStyle("black", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.BLACK);

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

    public static void main(String[] args) {
    }
}

class Element {

    private String uri;
    private int repository;

    public Element(String uri, int repository) {
        setUri(uri);
        setRepository(repository);
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the repository
     */
    public int getRepository() {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(int repository) {
        if (repository >= 1 && repository <= 3) {
            this.repository = repository;
        }
    }
}