/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator;

/**
 *
 * @author Home
 */
public class Actor {
    private String uri;
    private String name;
    
    public Actor(String uri, String name) {
        setURI(uri);
        setName(name);
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
    
    public String toString() {
        return "'" + name + "'";
    }

    /**
     * @return the uri
     */
    public String getURI() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setURI(String uri) {
        this.uri = uri;
    }
    
}
