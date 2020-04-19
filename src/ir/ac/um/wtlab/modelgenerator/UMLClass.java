/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator;

import ir.ac.um.wtlab.modelgenerator.util.Utils;
import java.util.*;

/**
 *
 * @author Home
 */
public class UMLClass {

    private String name;
    private String url;
    private ArrayList<UMLAttribute> attributes;

    public UMLClass(String name, String url) {
        setName(name);
        setURL(url);
        attributes = new ArrayList<UMLAttribute>();
    }

    public void addAttributes(ArrayList<UMLAttribute> attributes) {
        this.attributes.addAll(attributes);
    }

    public void addAttribute(UMLAttribute attribute) {
        attributes.add(attribute);
    }
    
    public ArrayList<UMLAttribute> getAttributes() {
        return attributes;
    }

    public UMLAttribute getAttribute(int i) {
        return attributes.get(i);
    }

    public int getAttributeCount() {
        return attributes.size();
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
     * @return the url
     */
    public String getURL() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setURL(String url) {
        this.url = url;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        String href = Utils.correctURL(url);
        buffer.append("<b><a href=\"").append(href).append("\">").append(name).append("</a></b>");
        if (attributes != null && attributes.size() > 0) {
            buffer.append("<br />");
            buffer.append("<ol>attributes");
            for (UMLAttribute attribute : attributes) {
                buffer.append("<li>").append(attribute).append("</li>");
            }
            buffer.append("</ol>");
        }
        return buffer.toString();
    }

}
