/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator;

import java.util.ArrayList;
import ir.ac.um.wtlab.modelgenerator.util.Utils;

/**
 *
 * @author Farshad
 */
public class ActivityDiagramAnnotator {

    private ModelGenerator modelGenerator;
    public static final int MAX_N_GRAM = 4;
    private String softwareCaseURI;
    private String activityDiagramURI;
    private String activityDiagramName;

    public ActivityDiagramAnnotator(ModelGenerator modelGenerator) {
        this.modelGenerator = modelGenerator;
    }

    public void annotateActivityDiagram(String softwareCaseURI, String activityDiagramURI) {
        if(activityDiagramURI.contains("AddressBook_obj_480")) {
            System.out.println("*********");
        }
        setSoftwareCaseURI(softwareCaseURI);
        setActivityDiagramURI(activityDiagramURI);
        setActivityDiagramName(modelGenerator.getElementName(activityDiagramURI));
        ArrayList<UMLClass> classes = modelGenerator.listClassesFromContentModel(softwareCaseURI);
        ArrayList<UMLClass> tempClasses = modelGenerator.listClassesFromOtherPackages(softwareCaseURI);
        classes.addAll(tempClasses);

        ArrayList<Actor> actors = modelGenerator.getAssociatedActors(activityDiagramURI);

        ArrayList<String[]> labeledElements = new ArrayList<String[]>();
        String[] firstElement = new String[]{activityDiagramURI, getActivityDiagramName()};
        labeledElements.add(firstElement);
        ArrayList<String[]> tempElements = modelGenerator.getActivityDiagramLabeledElements(activityDiagramURI);
        labeledElements.addAll(tempElements);

        for (String[] element : labeledElements) {
            String elementURI = element[0];
            String elementLabel = element[1];
            //System.out.println("***** " + elementLabel + " *****");
            annotateLabel(elementURI, elementLabel, classes, actors);
        }
    }

    private void annotateLabel(String elementURI, String label, ArrayList<UMLClass> classes, ArrayList<Actor> actors) {
        final String originalLabel = label;
        label = Utils.prepare(label);
        String[] labelWords = label.split(" ");
        boolean annotated = false;

        ArrayList<Annotation> annotationsList = new ArrayList<Annotation>();
        for (int i = 0/*(firstWordIsVerb? 1 : 0)*/; i < labelWords.length; i++) {
            //ArrayList<Annotation> annotationsList = new ArrayList<Annotation>();
            int bound = (labelWords.length - i >= MAX_N_GRAM) ? MAX_N_GRAM : labelWords.length - i;
            for (int j = bound; j > 0; j--) {
                String nGram = "";
                for (int k = 0; (k < j && ((i + k) < labelWords.length)); k++) {
                    nGram += labelWords[i + k];
                }
                ArrayList<Annotation> annotations = annotateNGram(nGram, classes, actors);
                for (Annotation annotation : annotations) {
                    annotation.setLabel(originalLabel);
                    annotation.setOwner(elementURI);
                    annotationsList.add(annotation);
                }
                /*                if (annotations.size() > 0) {
                //annotationsList = removeExtraAnnotations(annotationsList);
                annotationsList = resolveAnnotations(annotationsList);
                printAnnotations(annotationsList);
                annotated = true;
                break; //not consider other N-grams
                }*/
            }
        }
        if (annotationsList.size() > 1) {
            //annotationsList = removeExtraAnnotations(annotationsList);
            //System.out.println("BEFORE RESOLUTION: annotation count is " + annotationsList.size());
            //printAnnotations(annotationsList);
            annotationsList = resolveAnnotations(annotationsList);
            //System.out.println("AFTER RESOLUTION: annotation count is " + annotationsList.size());
        }
        if (annotationsList.size() > 0) {
            annotated = true;
            printAnnotations(annotationsList);
        }
        if (!annotated) {
            String typeURI = modelGenerator.getCentralBufferNodeType(elementURI);
            if (typeURI != null) {
                int priorityLevel = Annotation.PRIORITY_LEVEL_1;
                Annotation annotation = new Annotation(activityDiagramURI,
                        Annotation.OTHER_ANNOTATION_TYPE,
                        originalLabel, elementURI, originalLabel, typeURI, priorityLevel);
                System.out.println(annotation);
            }
        }

    }

    private void refineAnnotations(ArrayList<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            String targetURI = annotation.getTarget();
            if (modelGenerator.isClassProperty(targetURI)) {
                ArrayList<String> classURIs = modelGenerator.getPropertyOwnerClassURIs(targetURI);
                for (String classURI : classURIs) {
                    for (Annotation tempAnnotation : annotations) {
                        if (tempAnnotation.getTarget().equalsIgnoreCase(classURI)) {
                            //System.out.println("Changed from 2 to 0");
                            annotation.setPriority(Annotation.PRIORITY_LEVEL_0);
                            tempAnnotation.setPriority(Annotation.PRIORITY_LEVEL_0);
                        }
                    }
                }
            }
        }
    }

    private ArrayList<Annotation> resolveAnnotations(ArrayList<Annotation> annotations) {
        refineAnnotations(annotations);
        boolean[] mustBeDeleted = new boolean[annotations.size()];
        for (int i = 0; i < annotations.size(); i++) {
            Annotation annotation1 = annotations.get(i);
            for (int j = 0; j < annotations.size(); j++) {
                if (i != j) {
                    Annotation annotation2 = annotations.get(j);
                    if (annotation2.getAnnotatedWord().equalsIgnoreCase(annotation1.getAnnotatedWord())) {
                        if (annotation2.getPriority() < annotation1.getPriority()) {
                            mustBeDeleted[i] = true;
                        }
                    }
                }
            }
        }
        ArrayList<Annotation> result = new ArrayList<Annotation>();
        for (int i = 0; i < annotations.size(); i++) {
            if (!mustBeDeleted[i]) {
                result.add(annotations.get(i));
            }
        }

        //
        if (result.size() > 1) {
            mustBeDeleted = new boolean[result.size()];
            for (int i = 0; i < result.size(); i++) {
                Annotation annotation1 = result.get(i);
                for (int j = 0; j < result.size(); j++) {
                    if (i != j) {
                        Annotation annotation2 = result.get(j);
                        String str1 = annotation1.getAnnotatedWord().toLowerCase();
                        String str2 = annotation2.getAnnotatedWord().toLowerCase();
                        if (str2.contains(str1) && str2.length() > str1.length()) {
                            mustBeDeleted[i] = true;
                        }
                    }
                }
            }
            ArrayList<Annotation> finalResult = new ArrayList<Annotation>();
            for (int i = 0; i < result.size(); i++) {
                if (!mustBeDeleted[i]) {
                    finalResult.add(result.get(i));
                }
            }
            result = finalResult;
        }

        return result;
    }

    private void printAnnotations(ArrayList<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            System.out.println(annotation);
        }
    }

    private ArrayList<Annotation> annotateNGram(String nGram, ArrayList<UMLClass> classes, ArrayList<Actor> actors) {
        ArrayList<Annotation> result = new ArrayList<Annotation>();
        for (UMLClass umlClass : classes) {
            if (matches(umlClass.getName(), nGram)) {
                int priorityLevel = Annotation.PRIORITY_LEVEL_2;
                int type = Annotation.ANNOTATION_TYPE_6;
                if (activityDiagramName.toLowerCase().contains(nGram.toLowerCase())) {
                    priorityLevel = Annotation.PRIORITY_LEVEL_1;
                    type = Annotation.ANNOTATION_TYPE_1;
                } else if (isCentralBufferNode(nGram) || isTypeOfCentralBuffer(umlClass.getURL())) {
                    priorityLevel = Annotation.PRIORITY_LEVEL_1;
                    type = Annotation.ANNOTATION_TYPE_1;
                }

                ArrayList<String> associatedClassURIs = modelGenerator.getAssociatedClassURIsFromClassDiagram(umlClass.getURL());
                for (String associatedClassURI : associatedClassURIs) {
                    String associatedClassName = modelGenerator.getElementName(associatedClassURI);
                    if (activityDiagramName.toLowerCase().contains(associatedClassName.toLowerCase())) {
                        type = Annotation.ANNOTATION_TYPE_3;
                        break;
                    }
                }
                Annotation annotation = new Annotation(activityDiagramURI,
                        type, null, null, nGram, umlClass.getURL(), priorityLevel);
                result.add(annotation);
            }
            for (int i = 0; i < umlClass.getAttributeCount(); i++) {
                if (matches(umlClass.getAttribute(i).getName(), nGram)) {
                    int priorityLevel = Annotation.PRIORITY_LEVEL_2;
                    int type = Annotation.ANNOTATION_TYPE_7;
                    if (activityDiagramName.toLowerCase().contains(nGram.toLowerCase())
                            || activityDiagramName.toLowerCase().contains(umlClass.getName().toLowerCase())) {
                        priorityLevel = Annotation.PRIORITY_LEVEL_1;
                        type = Annotation.ANNOTATION_TYPE_2;
                    } else if (isCentralBufferNode(umlClass.getName()) || isTypeOfCentralBuffer(umlClass.getURL())) {
                        priorityLevel = Annotation.PRIORITY_LEVEL_1;
                        type = Annotation.ANNOTATION_TYPE_2;
                    }
                    ArrayList<String> associatedClassURIs = modelGenerator.getAssociatedClassURIsFromClassDiagram(umlClass.getURL());
                    for (String associatedClassURI : associatedClassURIs) {
                        String associatedClassName = modelGenerator.getElementName(associatedClassURI);
                        if (activityDiagramName.toLowerCase().contains(associatedClassName.toLowerCase())) {
                            type = Annotation.ANNOTATION_TYPE_4;
                            break;
                        }
                    }
                    Annotation annotation = new Annotation(activityDiagramURI,
                            type, null, null, nGram, umlClass.getAttribute(i).getURL(), priorityLevel);
                    result.add(annotation);
                }
            }
        }
        for (Actor actor : actors) {
            if (matches(actor.getName(), nGram)) {
                int priorityLevel = Annotation.PRIORITY_LEVEL_2;
                int type = Annotation.ANNOTATION_TYPE_5;
                if (activityDiagramName.toLowerCase().contains(actor.getName())) {
                    priorityLevel = Annotation.PRIORITY_LEVEL_1;
                }
//                new Annotation(type, label, owner, annotatedWord, target, priorityLevel)
                Annotation annotation = new Annotation(activityDiagramURI,
                        type,
                        null, null, nGram, actor.getURI(), priorityLevel);
                result.add(annotation);
            }
        }
        return result;
    }

    private boolean matches(String elementName, String nGram) {
        String guess1 = elementName;
        String guess2 = elementName + "s";
        String guess3 = elementName + "es";
        boolean ok = guess1.equalsIgnoreCase(nGram) || guess2.equalsIgnoreCase(nGram) || guess3.equalsIgnoreCase(nGram);
        if (!ok) {
            guess1 = nGram;
            guess2 = nGram + "s";
            guess3 = nGram + "es";
            ok = elementName.equalsIgnoreCase(guess1) || elementName.equalsIgnoreCase(guess2) || elementName.equalsIgnoreCase(guess3);
        }
        return ok;
    }

    private boolean isCentralBufferNode(String label) {
        return modelGenerator.isLabelInTheNameOfTheCentralBufferNode(activityDiagramURI, label);
    }

    private boolean isTypeOfCentralBuffer(String classURI) {
        return modelGenerator.isTypeOfCentralBuffer(activityDiagramURI, classURI);
    }

    /**
     * @return the activityDiagramURI
     */
    public String getActivityDiagramURI() {
        return activityDiagramURI;
    }

    /**
     * @param activityDiagramURI the activityDiagramURI to set
     */
    public void setActivityDiagramURI(String activityDiagramURI) {
        this.activityDiagramURI = activityDiagramURI;
    }

    /**
     * @return the softwareCaseURI
     */
    public String getSoftwareCaseURI() {
        return softwareCaseURI;
    }

    /**
     * @param softwareCaseURI the softwareCaseURI to set
     */
    public void setSoftwareCaseURI(String softwareCaseURI) {
        this.softwareCaseURI = softwareCaseURI;
    }

    /**
     * @return the modelGenerator
     */
    public ModelGenerator getModelGenerator() {
        return modelGenerator;
    }

    /**
     * @param modelGenerator the modelGenerator to set
     */
    public void setModelGenerator(ModelGenerator modelGenerator) {
        this.modelGenerator = modelGenerator;
    }

    /**
     * @return the activityDiagramName
     */
    public String getActivityDiagramName() {
        return activityDiagramName;
    }

    /**
     * @param activityDiagramName the activityDiagramName to set
     */
    public void setActivityDiagramName(String activityDiagramName) {
        this.activityDiagramName = activityDiagramName;
    }
}
