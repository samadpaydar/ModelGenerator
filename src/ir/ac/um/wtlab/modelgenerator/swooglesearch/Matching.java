/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ir.ac.um.wtlab.modelgenerator.swooglesearch;

import com.hp.hpl.jena.ontology.*;

/**
 *
 * @author Farshad
 */
public class Matching {
    private OntClass ontClass;
    private String keyword;
    private String ontClassName;
    
    public Matching(OntClass ontClass, String keyword) {
        setOntClass(ontClass);
        setKeyword(keyword);
    }
    
    public void setOntClass(OntClass ontClass) {
        this.ontClass = ontClass;
        setOntClassName(ontClass.getLocalName());
    }
    
    public OntClass getOntClass() {
        return ontClass;
    }
    
    public void setOntClassName(String ontClassName) {
        this.ontClassName = ontClassName;
    }
    
    public String getOntClassName() {
        return ontClassName;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public String getKeyword() {
        return keyword;
    }

}
