/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ir.ac.um.wtlab.modelgenerator.uwe2rdf;

/**
 *
 * @author Home
 */
public class Constants {
    
    public static final String MD_ELEMENT = "mdElement";
    public static final String ELEMENT_CLASS = "elementClass";
    public static final String DIAGRAM = "Diagram";
    public static final String LOWER_VALUE = "lowerValue";
    public static final String UPPER_VALUE = "upperValue";
    public static final String VALUE = "value";
    public static final String WEIGHT = "weight";
    public static final String ARGUMENT = "argument";

    public static final String UML_MODEL = "uml:Model";

    public static final String EXTEND = "extend";
    public static final String INCLUDE = "include";
    public static final String GENERALIZATION = "generalization";
    public static final String PACKAGED_ELEMENT = "packagedElement";
    public static final String NESTED_CLASSIFIER = "nestedClassifier";
    public static final String OWNED_DIAGRAM = "ownedDiagram";
    public static final String OWNED_ATTRIBUTE = "ownedAttribute";
    public static final String OWNED_BEHAVIOR = "ownedBehavior";
    public static final String MEMBER_END = "memberEnd";
    public static final String OWNED_END = "ownedEnd";
    public static final String OUTGOING = "outgoing";
    public static final String INCOMING = "incoming";
    public static final String SOURCE = "source";
    public static final String TARGET = "target";
    public static final String INTERRUPTS = "interrupts";

    public static final String XMI_ID = "xmi:id";
    public static final String XMI_TYPE = "xmi:type";
    public static final String XMI_IDREF = "xmi:idref";
    public static final String REGION_AS_INPUT = "regionAsInput";
    public static final String REGION_AS_OUTPUT = "regionAsOutput";
    public static final String SIGNAL = "signal";
    

    public static final String UML_DIAGRAM = "uml:Diagram";
    public static final String UML_ACTIVITY = "uml:Activity";
    public static final String UML_CONTROL_FLOW = "uml:ControlFlow";
    public static final String UML_OBJECT_FLOW = "uml:ObjectFlow";
    public static final String UML_INPUT_PIN = "uml:InputPin";
    public static final String UML_OUTPUT_PIN = "uml:OutputPin";
    public static final String UML_EXPANSION_NODE = "uml:ExpansionNode";
    public static final String NAME = "name";
    public static final String EXTENDED_CASE = "extendedCase";
    public static final String ADDITION = "addition";
    public static final String GENERAL = "general";
    
    public static final String TYPE = "type";
    public static final String ASSOCIATION = "association";
    public static final String HREF = "href";
    public static final String NODE = "node";
    public static final String EDGE = "edge";
    public static final String GUARD = "guard";
    public static final String BODY = "body";
    public static final String GROUP = "group";
    public static final String INTERRUPTING_EDGE = "interruptingEdge";
    public static final String INTERRUPTIBLE_REGION = "inInterruptibleRegion";
    
    public static final String RESULT = "result";

    public static final String VISIBILITY = "visibility";

    public static final String UML_PRIMITIVE_TYPES_NAMESPACE = "http://www.omg.org/spec/UML/20110701/PrimitiveTypes.xmi#";
    public static final String UML_PRIMITIVE_TYPES_PREFIX = "umlpt";
    public static final String UML_NAMESPACE = "http://www.omg.org/spec/UML/20110701";
    public static final String UML_PREFIX = "uml";
    
    public static final String OBJECT = "obj";
    
    public static final String RDF_PREFIX = "rdf";
    public static final String RDFS_PREFIX = "rdfs";
    public static final String UWE2RDF_PREFIX = "uwe2rdf";
    public static final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String UWE2RDF_NAMESPACE = "http://wtlab.um.ac.ir/uml.owl#";
    public static final String[] PREFIXES = {RDF_PREFIX, RDFS_PREFIX, UWE2RDF_PREFIX, 
        UML_PRIMITIVE_TYPES_PREFIX, UML_PREFIX };
    public static final String[] NAMESPACES = {RDF_NAMESPACE, RDFS_NAMESPACE, UWE2RDF_NAMESPACE, 
        UML_PRIMITIVE_TYPES_NAMESPACE, UML_NAMESPACE };
    
    
    public static final String UWE2RDF_ID = UWE2RDF_PREFIX + ":id";
    public static final String UWE2RDF_ID_REF = UWE2RDF_PREFIX + ":idref";
    public static final String UWE2RDF_NAME = UWE2RDF_PREFIX + ":name";
    public static final String UWE2RDF_TYPE = UWE2RDF_PREFIX + ":type";
    public static final String UWE2RDF_ASSOCIATION = UWE2RDF_PREFIX + ":association";
    
    public static final String UWE2RDF_VALUE = UWE2RDF_PREFIX + ":value";
    public static final String UWE2RDF_LOWER_VALUE = UWE2RDF_PREFIX + ":LowerValue";
    public static final String UWE2RDF_MEMBER_END = UWE2RDF_PREFIX + ":MemberEnd";
    public static final String UWE2RDF_NODE = UWE2RDF_PREFIX + ":Node";
    public static final String UWE2RDF_INTERRUPTING_EDGE = UWE2RDF_PREFIX + ":InterruptingEdge";
    public static final String UWE2RDF_INTERRUPTIBLE_ACTIVITY_REGION = UWE2RDF_PREFIX + ":InterruptibleActivityRegion";
    
    public static final String UWE2RDF_UPPER_VALUE = UWE2RDF_PREFIX + ":UpperValue";
    public static final String UWE2RDF_OUTGOING = UWE2RDF_PREFIX + ":Outgoing";
    public static final String UWE2RDF_INCOMING = UWE2RDF_PREFIX + ":Incoming";
    public static final String UWE2RDF_WEIGHT = UWE2RDF_PREFIX + ":Weight";
    public static final String UWE2RDF_VISIBILITY = UWE2RDF_PREFIX + ":visibility";
    
    public static final String UWE2RDF_CONTAINS_PACKAGED_ELEMENT = UWE2RDF_PREFIX + ":" + "containsPackagedElement";
    public static final String UWE2RDF_CONTAINS_NESTED_CLASSIFIER = UWE2RDF_PREFIX + ":" + "containsNestedClassifier";
    public static final String UWE2RDF_CONTAINS_DIAGRAM = UWE2RDF_PREFIX + ":" + "containsDiagram";
    public static final String UWE2RDF_CONTAINS_ATTRIBUTE = UWE2RDF_PREFIX + ":" + "containsAttribute";
    public static final String UWE2RDF_CONTAINS_BEHAVIOR = UWE2RDF_PREFIX + ":" + "containsBehavior";
    public static final String UWE2RDF_CONTAINS_NODE = UWE2RDF_PREFIX + ":" + "containsNode";
    public static final String UWE2RDF_CONTAINS_INTERRUPTING_EDGE = UWE2RDF_PREFIX + ":" + "containsInterruptingEdge";
    public static final String UWE2RDF_IS_IN_INTERRUPTIBLE_ACTIVITY_REGION = UWE2RDF_PREFIX + ":" + "isInInterruptibleActivityRegion";
    
    public static final String UWE2RDF_CONTAINS_EDGE = UWE2RDF_PREFIX + ":" + "containsEdge";
    public static final String UWE2RDF_HAS_LOWER_VALUE = UWE2RDF_PREFIX + ":" + "hasLowerValue";
    public static final String UWE2RDF_HAS_UPPER_VALUE = UWE2RDF_PREFIX + ":" + "hasUpperValue";
    public static final String UWE2RDF_HAS_MEMBER_END = UWE2RDF_PREFIX + ":" + "hasMemberEnd";
    public static final String UWE2RDF_HAS_OWNED_END = UWE2RDF_PREFIX + ":" + "hasOwnedEnd";
    public static final String UWE2RDF_HAS_OUTGOING = UWE2RDF_PREFIX + ":" + "hasOutgoing";
    public static final String UWE2RDF_HAS_INCOMING = UWE2RDF_PREFIX + ":" + "hasIncoming";
    public static final String UWE2RDF_HAS_SOURCE = UWE2RDF_PREFIX + ":hasSource";
    public static final String UWE2RDF_HAS_TARGET = UWE2RDF_PREFIX + ":hasTarget";
    public static final String UWE2RDF_INTERRUPTS = UWE2RDF_PREFIX + ":interrupts";
    public static final String UWE2RDF_HAS_WEIGHT = UWE2RDF_PREFIX + ":hasWeight";
    public static final String UWE2RDF_HAS_ARGUMENT = UWE2RDF_PREFIX + ":hasArgument";
    public static final String UWE2RDF_HAS_RESULT = UWE2RDF_PREFIX + ":hasResult";
    
    public static final String DUMMY_ID = "-1";
    public static final String VIRTUAL = "virtual";
    
    
    public static final String UWE2RDF_SOFTWARE_CASE = UWE2RDF_PREFIX + ":softwareCase";
    
    public static final String UWE2RDF_HAS_SOFTWARE_CASE = UWE2RDF_PREFIX + ":hasSoftwareCase";
    public static final String UWE2RDF_HAS_EXTENDER_USECASE = UWE2RDF_PREFIX + ":extension";
    public static final String UWE2RDF_HAS_EXTENDED_USECASE = UWE2RDF_PREFIX + ":extendedCase";
    public static final String UWE2RDF_HAS_INCLUDER_USECASE = UWE2RDF_PREFIX + ":includingCase";
    public static final String UWE2RDF_HAS_INCLUDED_USECASE = UWE2RDF_PREFIX + ":addition";
    public static final String UWE2RDF_HAS_SPECIFIC = UWE2RDF_PREFIX + ":hasSpecific";
    public static final String UWE2RDF_HAS_GUARD = UWE2RDF_PREFIX + ":hasGuard";
    public static final String UWE2RDF_HAS_BODY = UWE2RDF_PREFIX + ":hasBody";
    public static final String UWE2RDF_HAS_GENERAL = UWE2RDF_PREFIX + ":hasGeneral";
    public static final String UWE2RDF_HAS_REGION_AS_INPUT = UWE2RDF_PREFIX + ":hasRegionAsInput";
    public static final String UWE2RDF_HAS_REGION_AS_OUTPUT = UWE2RDF_PREFIX + ":hasRegionAsOutput";
    public static final String UWE2RDF_HAS_SIGNAL = UWE2RDF_PREFIX + ":hasSignal";
   
}
