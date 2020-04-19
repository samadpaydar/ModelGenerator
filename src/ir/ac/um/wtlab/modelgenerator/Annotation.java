/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator;

/**
 *
 * @author Home
 */
public class Annotation {
    public final static int PRIORITY_LEVEL_0 = 0;
    public final static int PRIORITY_LEVEL_1 = 1;
    public final static int PRIORITY_LEVEL_2 = 2;
    private String activityDiagramURI;
    private String owner; //the element that ownes the annotation
    private String label;
    private String annotatedWord;
    private String target;
    private int type;
    private int priority;
    // a word in a label of an element is annotated to 
    // 1: a class in the class diagram
    // 2: an attribute of a class from the class diagram
    // 3: a concept in the name of the activity diagram
/*    public final static int CLASS_ANNOTATION_TYPE = 1;
    public final static int INDIRECT_CLASS_ANNOTATION_TYPE = 2;
    
    public final static int ATTRIBUTE_ANNOTATION_TYPE = 3;
    public final static int ACTOR_ANNOTATION_TYPE = 4;
    public final static int ACTIVITY_DIAGRAM_NAME_CONCEPT_ANNOTATION_TYPE = 3;
  */  
    public final static int ANNOTATION_TYPE_1 = 1;
    public final static int ANNOTATION_TYPE_2 = 2;
    public final static int ANNOTATION_TYPE_3 = 3;
    public final static int ANNOTATION_TYPE_4 = 4;
    public final static int ANNOTATION_TYPE_5 = 5;
    public final static int ANNOTATION_TYPE_6 = 6;
    public final static int ANNOTATION_TYPE_7 = 7;
    public final static int OTHER_ANNOTATION_TYPE = 8;
    
    
    
    public Annotation(String activityDiagramURI, int type, String label, String owner, String annotatedWord, String target, int priority) {
        setActivityDiagramURI(activityDiagramURI);
        setType(type);
        setLabel(label);
        setOwner(owner);
        setAnnotatedWord(annotatedWord);
        setTarget(target);
        setPriority(priority);
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the annotatedWord
     */
    public String getAnnotatedWord() {
        return annotatedWord;
    }

    /**
     * @param annotatedWord the annotatedWord to set
     */
    public void setAnnotatedWord(String annotatedWord) {
        this.annotatedWord = annotatedWord;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(256);
        builder.append('<').append(activityDiagramURI).append("> uwe2rdf:hasAnnotation [\n");
        builder.append(" rdf:type uwe2rdf:Annotation ;\n");
        builder.append(" uwe2rdf:owner <").append(owner).append("> ;\n");
        builder.append(" uwe2rdf:annotationType ").append(type).append(" ;\n");
        builder.append(" uwe2rdf:priorityLevel ").append(priority).append(" ;\n");
        builder.append(" uwe2rdf:label \"").append(label).append("\" ;\n");
        builder.append(" uwe2rdf:annotatedWord \"").append(annotatedWord).append("\" ;\n");
        builder.append(" uwe2rdf:target <").append(target).append(">  ] .\n");
        return builder.toString();
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
}
