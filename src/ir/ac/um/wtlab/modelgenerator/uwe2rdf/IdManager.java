/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.uwe2rdf;

import java.util.*;

/**
 *
 * @author Home
 */
public class IdManager {
    private Stack<String> idsStack = new Stack<String>();
    private TreeMap idElementMap = new TreeMap<String, String>();
    private int valueCount;
    private int virtualIdCount;
    private String projectId;
    public IdManager(String projectId) {
        this.projectId = projectId;
    }
    
    public void push(String id) {
        if(!idElementMap.containsKey(id)) {
            String value = projectId + "_" + Constants.OBJECT + "_" + (++valueCount);
            idElementMap.put(id, value);
        }
        idsStack.push(id);
    }
    
    public String getSoftwareCaseId() {
        return Constants.UWE2RDF_PREFIX + ":" + projectId + "_" + Constants.OBJECT + "_" + (++valueCount);
    }
    
    public String getEquivalentId(String id){
        Object value = idElementMap.get(id);
        if(value==null)
            return null;
        else 
            return Constants.UWE2RDF_PREFIX + ":" + value;
    }
    
    public ArrayList<String> getAllIds() {
        ArrayList<String> result = new ArrayList<String>(valueCount);
        for(int i=0; i<valueCount; i++) {
            result.add(Constants.UWE2RDF_PREFIX + ":" + projectId + "_" + Constants.OBJECT + "_" + (i+1));
        }
        return result;
    }
    
    public String pop() {
        return idsStack.pop();
    }
    
    public String peek() {
        if(!idsStack.isEmpty())
            return idsStack.peek();
        else return null;
    }
    
    public String createVirtualId() {
        return Constants.VIRTUAL + "_" + (++virtualIdCount);
    }
    
}
