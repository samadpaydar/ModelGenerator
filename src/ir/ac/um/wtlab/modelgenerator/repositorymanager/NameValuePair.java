/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.repositorymanager;

/**
 *
 * @author Home
 */
class NameValuePair {
    private String name;
    private String value;
    
    public NameValuePair(String name, String value) {
        setName(name);
        setValue(value);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    public String toString() {
        return "[" + name + ":" + value + "]";
    }
    
    
}
