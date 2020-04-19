/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.uwe2rdf;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import java.io.*;
import java.util.*;

/**
 *
 * @author Home
 */
public class SAXHandler extends DefaultHandler {

    private PrintStream output;
    private ArrayList<String> pendingSubjectPredicates;
    private ArrayList<String> pendingObjects;
    private IdManager idManager;
    private String softwareCaseQualifiedName;
    private final String PROJECT_ID;
    private boolean waitingForGuardBody = false;
    //private boolean waitingForGroupNodes = false;
    private Stack<String> packagedElementsStack;

    public SAXHandler(String projectId, File outputFile) throws Exception {
        super();
        outputFile.createNewFile();
        PROJECT_ID = projectId;
        idManager = new IdManager(projectId);
        output = new PrintStream(outputFile);
        pendingSubjectPredicates = new ArrayList<String>();
        pendingObjects = new ArrayList<String>();
        packagedElementsStack = new Stack<String>();
    }

    public void createPrefixes() {
        for (int i = 0; i < Constants.PREFIXES.length; i++) {
            output.println("@prefix\t" + Constants.PREFIXES[i] + ":\t<" + Constants.NAMESPACES[i] + ">\t.");
        }
        output.println();
    }

    public void startDocument() {
        System.out.println("\tparsing started.");
        softwareCaseQualifiedName = idManager.getSoftwareCaseId();
        output.println(softwareCaseQualifiedName + " a " + Constants.UWE2RDF_SOFTWARE_CASE + " ;");
        output.println("\t" + Constants.UWE2RDF_NAME + " \"" + PROJECT_ID + "\" .");
    }

    public void endDocument() {
        ArrayList<String> allQualifiedIds = idManager.getAllIds();
        /**
         * this loop is started from 1. because the first Id is related to the software case
         * and it does not require a triple
         * if this loop begins from 0, it results in a useless triple:
         * uwe2rdf:AddressBook_obj_1 uwe2rdf:hasSoftwareCase uwe2rdf:AddressBook_obj_1 .
         */
        for (int i = 1; i < allQualifiedIds.size() - 1; i++) {
            String qualifiedSubject = allQualifiedIds.get(i);
            output.println(qualifiedSubject + " " + Constants.UWE2RDF_HAS_SOFTWARE_CASE + " " + softwareCaseQualifiedName + " .");
        }
        System.out.println("\tparsing finished.");
    }

    public void startElement(String uri, String localName, String qname, Attributes attributes) {
        if (qname.equalsIgnoreCase(Constants.UML_MODEL)) {
            startProcessUMLModel(attributes);
        } else if (qname.equalsIgnoreCase(Constants.PACKAGED_ELEMENT)) {
            startProcessPackagedElement(attributes);
        } else if (qname.equalsIgnoreCase(Constants.NESTED_CLASSIFIER)) {
            startProcessNestedClassifier(attributes);
        } else if (qname.equalsIgnoreCase(Constants.OWNED_DIAGRAM)) {
            startProcessOwnedDiagram(attributes);
        } else if (qname.equalsIgnoreCase(Constants.OWNED_ATTRIBUTE)) {
            startProcessOwnedAttribute(attributes);
        } else if (qname.equalsIgnoreCase(Constants.TYPE)) {
            startProcessType(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.LOWER_VALUE)) {
            startProcessLowerValue(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.UPPER_VALUE)) {
            startProcessUpperValue(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.MEMBER_END)) {
            startProcessMemberEnd(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.OWNED_END)) {
            startProcessOwnedEnd(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.OWNED_BEHAVIOR)) {
            startProcessOwnedBehavior(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.NODE)) {
            startProcessNode(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.OUTGOING)) {
            startProcessOutgoing(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.INCOMING)) {
            startProcessIncoming(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.EDGE)) {
            startProcessEdge(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.WEIGHT)) {
            startProcessWeight(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.ARGUMENT)) {
            startProcessArgument(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.RESULT)) {
            startProcessResult(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.EXTEND)) {
            startProcessExtend(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.INCLUDE)) {
            startProcessInclude(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.GENERALIZATION)) {
            startProcessGeneralization(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.GUARD)) {
            startProcessGuard(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.BODY)) {
            startProcessBody(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.GROUP)) {
            startProcessGroup(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.INTERRUPTING_EDGE)) {
            startProcessInterruptingEdge(uri, localName, qname, attributes);
        } else if (qname.equalsIgnoreCase(Constants.INTERRUPTIBLE_REGION)) {
            startProcessInterruptibleRegion(uri, localName, qname, attributes);
        }
    }

    public void endElement(String uri, String localName, String qname) {
        if (qname.equalsIgnoreCase(Constants.UML_MODEL)) {
            endProcessUMLModel();
        } else if (qname.equalsIgnoreCase(Constants.PACKAGED_ELEMENT)) {
            endProcessPackagedElement();
        } else if (qname.equalsIgnoreCase(Constants.NESTED_CLASSIFIER)) {
            endProcessNestedClassifier();
        } else if (qname.equalsIgnoreCase(Constants.OWNED_DIAGRAM)) {
            endProcessOwnedDiagram();
        } else if (qname.equalsIgnoreCase(Constants.OWNED_ATTRIBUTE)) {
            endProcessOwnedAttribute();
        } else if (qname.equalsIgnoreCase(Constants.TYPE)) {
            endProcessType();
        } else if (qname.equalsIgnoreCase(Constants.LOWER_VALUE)) {
            endProcessLowerValue();
        } else if (qname.equalsIgnoreCase(Constants.UPPER_VALUE)) {
            endProcessUpperValue();
        } else if (qname.equalsIgnoreCase(Constants.MEMBER_END)) {
            endProcessMemberEnd();
        } else if (qname.equalsIgnoreCase(Constants.OWNED_END)) {
            endProcessOwnedEnd();
        } else if (qname.equalsIgnoreCase(Constants.OWNED_BEHAVIOR)) {
            endProcessOwnedBehavior();
        } else if (qname.equalsIgnoreCase(Constants.NODE)) {
            endProcessNode();
        } else if (qname.equalsIgnoreCase(Constants.OUTGOING)) {
            endProcessOutgoing();
        } else if (qname.equalsIgnoreCase(Constants.INCOMING)) {
            endProcessIncoming();
        } else if (qname.equalsIgnoreCase(Constants.EDGE)) {
            endProcessEdge();
        } else if (qname.equalsIgnoreCase(Constants.WEIGHT)) {
            endProcessWeight();
        } else if (qname.equalsIgnoreCase(Constants.ARGUMENT)) {
            endProcessArgument();
        } else if (qname.equalsIgnoreCase(Constants.RESULT)) {
            endProcessResult();
        } else if (qname.equalsIgnoreCase(Constants.EXTEND)) {
            endProcessExtend();
        } else if (qname.equalsIgnoreCase(Constants.INCLUDE)) {
            endProcessInclude();
        } else if (qname.equalsIgnoreCase(Constants.GENERALIZATION)) {
            endProcessGeneralization();
        } else if (qname.equalsIgnoreCase(Constants.GUARD)) {
            endProcessGuard();
        } else if (qname.equalsIgnoreCase(Constants.BODY)) {
            endProcessBody();
        } else if (qname.equalsIgnoreCase(Constants.GROUP)) {
            endProcessGroup();
        } else if (qname.equalsIgnoreCase(Constants.INTERRUPTING_EDGE)) {
            endProcessInterruptingEdge();
        } else if (qname.equalsIgnoreCase(Constants.INTERRUPTIBLE_REGION)) {
            endProcessInterruptibleRegion();
        }
    }

    private void startProcessUMLModel(Attributes attributes) {
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedSubject = idManager.getEquivalentId(id);
        String qualifiedObject = Constants.UWE2RDF_PREFIX + ":" + translate(Constants.UML_MODEL);
        output.println(qualifiedSubject + " a " + qualifiedObject + " ;");
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" .");
    }

    private void endProcessUMLModel() {
        idManager.pop();
    }

    private void startProcessPackagedElement(Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            /* 
             * push a dummy value, so the push/pop balance is preserved.
             * in start methods, id is pushed, and
             * in end methods, id is poped
             */
            idManager.push(Constants.DUMMY_ID);
            packagedElementsStack.push(Constants.DUMMY_ID);
            return;
        }
        /*
         * xmlType must be one of these values:
         * Constants.UML_MODEL, Constants.UML_CLASS, Constants.UML_ASSOCIATION,
         * Constants.UML_ACTOR, Constants.UML_USECASE
         */
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        packagedElementsStack.push(qualifiedChild);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        String name = attributes.getValue(Constants.NAME);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_CONTAINS_PACKAGED_ELEMENT + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        if (name != null) {
            output.println("\t" + Constants.UWE2RDF_NAME + " \"" + scape(name) + "\" ;");
        }
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" .");
    }

    private String scape(String text) {
        if (text == null) {
            return text;
        }
        text = text.replace("\"", "\\\"");
        text = text.replace('\n', ' ');
        return text;
    }

    private void endProcessPackagedElement() {
        packagedElementsStack.pop();
        idManager.pop();
    }

    private void startProcessNestedClassifier(Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        /*
         * xmlType must be Constants.UML_CLASS
         */
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        String name = attributes.getValue(Constants.NAME);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_CONTAINS_NESTED_CLASSIFIER + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        if (name != null) {
            output.println("\t" + Constants.UWE2RDF_NAME + " \"" + scape(name) + "\" ;");
        }
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" .");

    }

    private void endProcessNestedClassifier() {
        idManager.pop();
    }

    private void startProcessOwnedDiagram(Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        //xmiType must be Constants.UML_DIAGRAM
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        String name = attributes.getValue(Constants.NAME);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_CONTAINS_DIAGRAM + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        if (name != null) {
            output.println("\t" + Constants.UWE2RDF_NAME + " \"" + scape(name) + "\" ;");
        }
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" .");
    }

    private void endProcessOwnedDiagram() {
        idManager.pop();
    }

    private void startProcessOwnedAttribute(Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        //xmiType must be Constants.UML_PROPERTY
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        String name = attributes.getValue(Constants.NAME);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_CONTAINS_ATTRIBUTE + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" ;");
        if (name != null) {
            output.println("\t" + Constants.UWE2RDF_NAME + " \"" + scape(name) + "\" ;");
        }
        String attribType = attributes.getValue(Constants.TYPE);
        if (attribType != null) {
            String result = idManager.getEquivalentId(attribType);
            if (result == null) {
                pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_TYPE);
                pendingObjects.add(attribType);
            } else {
                output.println("\t" + Constants.UWE2RDF_TYPE + " " + result + " ;");
            }
        }
        output.println("\t" + Constants.UWE2RDF_VISIBILITY + " \"" + attributes.getValue(Constants.VISIBILITY) + "\" .");
    }

    private void endProcessOwnedAttribute() {
        idManager.pop();
    }

    private void startProcessType(String uri, String localName, String qname, Attributes attributes) {
        String parentId = idManager.peek();
        if (parentId == null) {
            return;
        }
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String href = attributes.getValue(Constants.HREF);
        String qualifiedType = "";
        if (href.startsWith(Constants.UML_PRIMITIVE_TYPES_NAMESPACE)) {
            int index = href.indexOf('#');
            qualifiedType = Constants.UML_PRIMITIVE_TYPES_PREFIX + ":" + href.substring(index + 1);
            output.println(qualifiedParent + " " + Constants.UWE2RDF_TYPE + " " + qualifiedType + " .");
        }
    }

    private void endProcessType() {
    }

    private void startProcessLowerValue(String uri, String localName, String qname, Attributes attributes) {
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_HAS_LOWER_VALUE + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + Constants.UWE2RDF_LOWER_VALUE + ";");
        String type = attributes.getValue(Constants.XMI_TYPE);
        if (type.startsWith(Constants.UML_PREFIX)) {
            output.println("\t" + Constants.UWE2RDF_TYPE + " " + type + " ;");
        }
        String value = attributes.getValue(Constants.VALUE);
        if (value != null) {
            output.println("\t" + Constants.UWE2RDF_VALUE + " \"" + value + "\" ;");
        }
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" .");
    }

    private void endProcessLowerValue() {
        idManager.pop();
    }

    private void startProcessUpperValue(String uri, String localName, String qname, Attributes attributes) {
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_HAS_UPPER_VALUE + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + Constants.UWE2RDF_UPPER_VALUE + ";");
        String type = attributes.getValue(Constants.XMI_TYPE);
        if (type.startsWith(Constants.UML_PREFIX)) {
            output.println("\t" + Constants.UWE2RDF_TYPE + " " + type + " ;");
        }
        String value = attributes.getValue(Constants.VALUE);
        if (value != null) {
            output.println("\t" + Constants.UWE2RDF_VALUE + " \"" + value + "\" ;");
        }
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" .");
    }

    private void endProcessUpperValue() {
        idManager.pop();
    }

    private void startProcessMemberEnd(String uri, String localName, String qname, Attributes attributes) {
        String parentId = idManager.peek();
        /*      String id = idManager.createVirtualId();
        if(id.contains("313")) {
        System.out.println("sdfsf");
        }
        idManager.push(id);*/
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String xmiIDRef = attributes.getValue(Constants.XMI_IDREF);
        String result = idManager.getEquivalentId(xmiIDRef);
        if (result == null) {
            pendingSubjectPredicates.add(qualifiedParent + " " + Constants.UWE2RDF_HAS_MEMBER_END);
            pendingObjects.add(xmiIDRef);
        } else {
            output.println(qualifiedParent + " " + Constants.UWE2RDF_HAS_MEMBER_END + " " + result + " .");
        }

    }

    private void endProcessMemberEnd() {
        // idManager.pop();
    }

    private void startProcessOwnedEnd(String uri, String localName, String qname, Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        //xmiType must be Constants.UML_PROPERTY
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_HAS_OWNED_END + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" ;");
        output.println("\t" + Constants.UWE2RDF_VISIBILITY + " \"" + attributes.getValue(Constants.VISIBILITY) + "\" .");
        String type = attributes.getValue(Constants.TYPE);
        String association = attributes.getValue(Constants.ASSOCIATION);

        String result = idManager.getEquivalentId(type);
        if (result == null) {
            pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_TYPE);
            pendingObjects.add(type);
        } else {
            output.println(qualifiedChild + " " + Constants.UWE2RDF_TYPE + " " + result + " .");
        }
        result = idManager.getEquivalentId(association);
        if (result == null) {
            pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_ASSOCIATION);
            pendingObjects.add(association);
        } else {
            output.println(qualifiedChild + " " + Constants.UWE2RDF_ASSOCIATION + " " + result + " .");
        }
    }

    private void endProcessOwnedEnd() {
        idManager.pop();
    }

    private void startProcessOwnedBehavior(String uri, String localName, String qname, Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        //xmiType must be Constants.UML_ACTIVITY
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        String name = attributes.getValue(Constants.NAME);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_CONTAINS_BEHAVIOR + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        if (name != null) {
            output.println("\t" + Constants.UWE2RDF_NAME + " \"" + scape(name) + "\" ;");
        }
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" .");
    }

    private void endProcessOwnedBehavior() {
        idManager.pop();
    }

    private void startProcessNode(String uri, String localName, String qname, Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        /*
         * xmiType must be one of the values: Constants.UML_INITIAL_NODE,
         * Constants.UML_CALL_BEHAVIOR_ACTION, Constants.UML_ACTIVITY_FINAL_NODE,
         * Constants.UML_CENTRAL_BUFFER_NODE
         */
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        String name = attributes.getValue(Constants.NAME);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_CONTAINS_NODE + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" ;");
        if (name != null) {
            output.println("\t" + Constants.UWE2RDF_NAME + " \"" + scape(name) + "\" ;");
        }
        output.println("\t" + Constants.UWE2RDF_VISIBILITY + " \"" + attributes.getValue(Constants.VISIBILITY) + "\" .");
        String regionId = attributes.getValue(Constants.REGION_AS_INPUT);
        if (regionId != null) {
            String result = idManager.getEquivalentId(regionId);
            if (result == null) {
                pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_HAS_REGION_AS_INPUT);
                pendingObjects.add(regionId);
            } else {
                output.println(qualifiedChild + " " + Constants.UWE2RDF_HAS_REGION_AS_INPUT + " " + result + " .");
            }
        }

        regionId = attributes.getValue(Constants.REGION_AS_OUTPUT);
        if (regionId != null) {
            String result = idManager.getEquivalentId(regionId);
            if (result == null) {
                pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_HAS_REGION_AS_OUTPUT);
                pendingObjects.add(regionId);
            } else {
                output.println(qualifiedChild + " " + Constants.UWE2RDF_HAS_REGION_AS_OUTPUT + " " + result + " .");
            }
        }

        String parentPackagedElement = packagedElementsStack.peek();
        output.println(parentPackagedElement + " " + Constants.UWE2RDF_CONTAINS_NODE + " " + qualifiedChild + " .");

        //for such cases: <node xmi:type='uml:SendSignalAction' xmi:id='_17_0_1beta...' visibility='public' signal='....'
        String signalId = attributes.getValue(Constants.SIGNAL);
        if (signalId != null) {
            String result = idManager.getEquivalentId(signalId);
            if (result == null) {
                pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_HAS_SIGNAL);
                pendingObjects.add(signalId);
            } else {
                output.println(qualifiedChild + " " + Constants.UWE2RDF_HAS_SIGNAL + " " + result + " .");
            }
        }

        String type = attributes.getValue(Constants.TYPE);
        if (type != null) {
            String result = idManager.getEquivalentId(type);
            if (result == null) {
                pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_TYPE);
                pendingObjects.add(type);
            } else {
                output.println(qualifiedChild + " " + Constants.UWE2RDF_TYPE + " " + result + " .");
            }
        }
    }

    private void endProcessNode() {
        idManager.pop();
    }

    private void startProcessEdge(String uri, String localName, String qname, Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        /*
         * xmiType must be one of the values: Constants.UML_CONTROL_FLOW,
         * Constants.UML_OBJECT_FLOW
         */
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        String name = attributes.getValue(Constants.NAME);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_CONTAINS_EDGE + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + qualifiedType + ";");


        String sourceId = attributes.getValue(Constants.SOURCE);
        String result = idManager.getEquivalentId(sourceId);
        if (result == null) {
            pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_HAS_SOURCE);
            pendingObjects.add(sourceId);
        } else {
            output.println("\t" + Constants.UWE2RDF_HAS_SOURCE + " " + result + " ;");
        }
        String targetId = attributes.getValue(Constants.TARGET);
        result = idManager.getEquivalentId(targetId);
        if (result == null) {
            pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_HAS_TARGET);
            pendingObjects.add(targetId);
        } else {
            output.println("\t" + Constants.UWE2RDF_HAS_TARGET + " " + result + " ;");
        }
        String interruptedRegionId = attributes.getValue(Constants.INTERRUPTS);
        if (interruptedRegionId != null) {
            result = idManager.getEquivalentId(interruptedRegionId);
            if (result == null) {
                pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_INTERRUPTS);
                pendingObjects.add(interruptedRegionId);
            } else {
                output.println("\t" + Constants.UWE2RDF_INTERRUPTS + " " + result + " ;");
            }
        }
        if (name != null) {
            output.println("\t" + Constants.UWE2RDF_NAME + " \"" + scape(name) + "\" ;");
        }
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" .");
        String parentPackagedElement = packagedElementsStack.peek();
        output.println(parentPackagedElement + " " + Constants.UWE2RDF_CONTAINS_NODE + " " + qualifiedChild + " .");
    }

    private void endProcessEdge() {
        idManager.pop();
    }

    private void startProcessOutgoing(String uri, String localName, String qname, Attributes attributes) {
        String parentId = idManager.peek();
        /*String id = idManager.createVirtualId();
        if(id.contains("313")) {
        System.out.println("sdfsf");
        }
        
        
        idManager.push(id);*/
        String qualifiedParent = idManager.getEquivalentId(parentId);
        //   String qualifiedChild = idManager.getEquivalentId(id);
        String xmiIDRef = attributes.getValue(Constants.XMI_IDREF);
        String result = idManager.getEquivalentId(xmiIDRef);
        if (result == null) {
            pendingSubjectPredicates.add(qualifiedParent + " " + Constants.UWE2RDF_HAS_OUTGOING);
            pendingObjects.add(xmiIDRef);
        } else {
            output.println(qualifiedParent + " " + Constants.UWE2RDF_HAS_OUTGOING + " " + result + " .");
        }
    }

    private void endProcessOutgoing() {
        // idManager.pop();
    }

    private void startProcessIncoming(String uri, String localName, String qname, Attributes attributes) {
        String parentId = idManager.peek();
        /*        String id = idManager.createVirtualId();
        if(id.contains("313")) {
        System.out.println("sdfsf");
        }
        
        idManager.push(id);*/
        String qualifiedParent = idManager.getEquivalentId(parentId);
        //String qualifiedChild = idManager.getEquivalentId(id);
        String xmiIDRef = attributes.getValue(Constants.XMI_IDREF);
        String result = idManager.getEquivalentId(xmiIDRef);
        if (result == null) {
            pendingSubjectPredicates.add(qualifiedParent + " " + Constants.UWE2RDF_HAS_INCOMING);
            pendingObjects.add(xmiIDRef);
        } else {
            output.println(qualifiedParent + " " + Constants.UWE2RDF_HAS_INCOMING + " " + result + " .");
        }
    }

    private void endProcessIncoming() {
        // idManager.pop();
    }

    private void startProcessWeight(String uri, String localName, String qname, Attributes attributes) {
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_HAS_WEIGHT + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + Constants.UWE2RDF_WEIGHT + ";");
        String type = attributes.getValue(Constants.XMI_TYPE);
        if (type.startsWith(Constants.UML_PREFIX)) {
            output.println("\t" + Constants.UWE2RDF_TYPE + " " + type + " ;");
        }
        String value = attributes.getValue(Constants.VALUE);
        if (value != null) {
            output.println("\t" + Constants.UWE2RDF_VALUE + " \"" + value + "\" ;");
        }
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" .");
    }

    private void endProcessWeight() {
        idManager.pop();
    }

    private void startProcessArgument(String uri, String localName, String qname, Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        /*
         * xmlType must be Constants.UML_INPUT_PIN
         */
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        String name = attributes.getValue(Constants.NAME);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_HAS_ARGUMENT + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" ;");
        output.println("\t" + Constants.UWE2RDF_NAME + " \"" + scape(name) + "\" ;");
        output.println("\t" + Constants.UWE2RDF_VISIBILITY + " \"" + attributes.getValue(Constants.VISIBILITY) + "\" .");
    }

    private void endProcessArgument() {
        idManager.pop();
    }

    private void startProcessResult(String uri, String localName, String qname, Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        /*
         * xmlType must be Constants.UML_OUTPUT_PIN
         */
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        String name = attributes.getValue(Constants.NAME);
        output.println(qualifiedParent + " " + Constants.UWE2RDF_HAS_RESULT + " " + qualifiedChild + " .");
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" ;");
        output.println("\t" + Constants.UWE2RDF_NAME + " \"" + scape(name) + "\" ;");
        output.println("\t" + Constants.UWE2RDF_VISIBILITY + " \"" + attributes.getValue(Constants.VISIBILITY) + "\" .");
    }

    private void endProcessResult() {
        idManager.pop();
    }

    private void startProcessExtend(String uri, String localName, String qname, Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        /*
         * xmlType must be Constants.UML_EXTEND uml:Extend
         */
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" ;");

        String extendedCaseId = attributes.getValue(Constants.EXTENDED_CASE);
        String qualifiedExtenderUsecase = qualifiedParent;
        output.println("\t" + Constants.UWE2RDF_HAS_EXTENDER_USECASE + " " + qualifiedExtenderUsecase + " ;");
        String qualifiedExtendedUsecase = idManager.getEquivalentId(extendedCaseId);
        if (qualifiedExtendedUsecase != null) {
            output.println("\t" + Constants.UWE2RDF_HAS_EXTENDED_USECASE + " " + qualifiedExtendedUsecase + " ;");
        } else {
            pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_HAS_EXTENDED_USECASE);
            pendingObjects.add(extendedCaseId);
        }
        output.println("\t" + Constants.UWE2RDF_VISIBILITY + " \"" + attributes.getValue(Constants.VISIBILITY) + "\" .");
    }

    private void endProcessExtend() {
        idManager.pop();
    }

    private void startProcessInclude(String uri, String localName, String qname, Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        /*
         * xmlType must be Constants.UML_INCLUDE uml:Include
         */
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" ;");

        String includedCaseId = attributes.getValue(Constants.ADDITION);
        String qualifiedIncluderUsecase = qualifiedParent;
        output.println("\t" + Constants.UWE2RDF_HAS_INCLUDER_USECASE + " " + qualifiedIncluderUsecase + " ;");
        String qualifiedIncludedUsecase = idManager.getEquivalentId(includedCaseId);
        if (qualifiedIncludedUsecase != null) {
            output.println("\t" + Constants.UWE2RDF_HAS_INCLUDED_USECASE + " " + qualifiedIncludedUsecase + " ;");
        } else {
            pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_HAS_INCLUDED_USECASE);
            pendingObjects.add(includedCaseId);
        }
        output.println("\t" + Constants.UWE2RDF_VISIBILITY + " \"" + attributes.getValue(Constants.VISIBILITY) + "\" .");
    }

    private void endProcessInclude() {
        idManager.pop();
    }

    private void startProcessGeneralization(String uri, String localName, String qname, Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        /*
         * xmlType must be Constants.UML_GENERALIZATION uml:Generalization'
         */
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" ;");

        String generalId = attributes.getValue(Constants.GENERAL);
        if (generalId == null) {
            output.println(".");
            return;
        }
        String qualifiedSpecific = qualifiedParent;
        String qualifiedGeneral = idManager.getEquivalentId(generalId);
        if (qualifiedGeneral != null) {
            output.println("\t" + Constants.UWE2RDF_HAS_GENERAL + " " + qualifiedGeneral + " ;");
        } else {
            pendingSubjectPredicates.add(qualifiedChild + " " + Constants.UWE2RDF_HAS_GENERAL);
            pendingObjects.add(generalId);
        }
        output.println("\t" + Constants.UWE2RDF_HAS_SPECIFIC + " " + qualifiedSpecific + " .");
    }

    private void endProcessGeneralization() {
        idManager.pop();
    }

    private void startProcessGuard(String uri, String localName, String qname, Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        /*
         * xmlType must be Constants.UML_OPAQUE_EXPRESSION uml:OpaqueExpression
         */
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" .");
        output.println(qualifiedParent + " " + Constants.UWE2RDF_HAS_GUARD + " " + qualifiedChild + ".");
    }

    private void endProcessGuard() {
        idManager.pop();
    }

    private void startProcessBody(String uri, String localName, String qname, Attributes attributes) {
        waitingForGuardBody = true;
    }

    private void endProcessBody() {
        waitingForGuardBody = false;
    }

    private void startProcessGroup(String uri, String localName, String qname, Attributes attributes) {
        String xmiType = attributes.getValue(Constants.XMI_TYPE);
        if (xmiType == null) {
            idManager.push(Constants.DUMMY_ID);
            return;
        }
        /*
         * xmlType must be Constants.uml_INTERRUPTIBLE_ACTIVITY_REGION
         */
        String parentId = idManager.peek();
        String id = attributes.getValue(Constants.XMI_ID);
        idManager.push(id);
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String qualifiedChild = idManager.getEquivalentId(id);
        String qualifiedType = Constants.UWE2RDF_PREFIX + ":" + translate(xmiType);
        output.println(qualifiedChild + " a " + qualifiedType + ";");
        output.println("\t" + Constants.UWE2RDF_ID + " \"" + id + "\" ;");
        output.println("\t" + Constants.UWE2RDF_VISIBILITY + " \"" + attributes.getValue(Constants.VISIBILITY) + "\" .");
        //waitingForGroupNodes = true;
        String parentPackagedElement = packagedElementsStack.peek();
        output.println(parentPackagedElement + " " + Constants.UWE2RDF_CONTAINS_NODE + " " + qualifiedChild + " .");

    }

    private void endProcessGroup() {
        idManager.pop();
        //waitingForGroupNodes = false;
    }

    private void startProcessInterruptingEdge(String uri, String localName, String qname, Attributes attributes) {
        String parentId = idManager.peek();
        /*   String id = idManager.createVirtualId();
        if(id.contains("313")) {
        System.out.println("sdfsf");
        }
        
        idManager.push(id);*/
        String qualifiedParent = idManager.getEquivalentId(parentId);
        String xmiIDRef = attributes.getValue(Constants.XMI_IDREF);
        String result = idManager.getEquivalentId(xmiIDRef);
        if (result == null) {
            pendingSubjectPredicates.add(qualifiedParent + " " + Constants.UWE2RDF_CONTAINS_INTERRUPTING_EDGE);
            pendingObjects.add(xmiIDRef);
        } else {
            output.println(qualifiedParent + " " + Constants.UWE2RDF_CONTAINS_INTERRUPTING_EDGE + " " + result + " .");
        }
    }

    private void endProcessInterruptingEdge() {
        //idManager.pop();
    }

    private void startProcessInterruptibleRegion(String uri, String localName, String qname, Attributes attributes) {
        String parentId = idManager.peek();
        /* String id = idManager.createVirtualId();
        if(id.contains("313")) {
        System.out.println("sdfsf");
        }
        
        idManager.push(id);*/
        String qualifiedParent = idManager.getEquivalentId(parentId);
        //  String qualifiedChild = idManager.getEquivalentId(id);

        String xmiIDRef = attributes.getValue(Constants.XMI_IDREF);
        String result = idManager.getEquivalentId(xmiIDRef);

        if (result == null) {
            pendingSubjectPredicates.add(qualifiedParent + " " + Constants.UWE2RDF_IS_IN_INTERRUPTIBLE_ACTIVITY_REGION);
            pendingObjects.add(xmiIDRef);
        } else {
            output.println(qualifiedParent + " " + Constants.UWE2RDF_IS_IN_INTERRUPTIBLE_ACTIVITY_REGION + " " + result + " .");
        }
    }

    private void endProcessInterruptibleRegion() {
        //idManager.pop();
    }

    public void characters(char[] ch, int start, int length) {
        String body = new String(ch, start, length);
        if (waitingForGuardBody) {
            String parentId = idManager.peek();
            String qualifiedParent = idManager.getEquivalentId(parentId);
            output.println(qualifiedParent + " " + Constants.UWE2RDF_HAS_BODY + " \"" + body + "\" .");
        }
    }

    private void flushPendingTriples() {
        for (int i = 0; i < pendingSubjectPredicates.size(); i++) {
            String subjectPredicate = pendingSubjectPredicates.get(i);
            String object = pendingObjects.get(i);
            object = idManager.getEquivalentId(object);
            if (object != null) {
                output.println(subjectPredicate + " " + object + " .");
            } else {
                System.out.println("\t\tERROR " + pendingObjects.get(i));
            }
        }
    }

    public void close() {
        flushPendingTriples();
        output.close();
    }

    private String translate(String term) {
        final String PREFIX = "uml:";
        if (term.startsWith("uml:")) {
            return term.substring(PREFIX.length());
        }
        return term.replace(':', '_');
    }
}
