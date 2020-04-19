/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator;

import ir.ac.um.wtlab.modelgenerator.util.Utils;

/**
 *
 * @author Home
 */
public class UMLAttribute {

    private String name;
    private String url;
    private String type;
    private String visibility;

    public UMLAttribute(String name, String url) {
        setName(name);
        setURL(url);
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

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the visibility
     */
    public String getVisibilility() {
        return visibility;
    }

    /**
     * @param visibility the visibility to set
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String toString() {
        return "[" + name + " * " + type + " * " + url + "]";
    }
/*    public String toString() {
        StringBuilder buffer = new StringBuilder();
        String href = Utils.correctURL(url);
        buffer.append("<a href=\"").append(href).append("\">").append(name).append("</a>").append(":&nbsp;&nbsp;");
        String typeHTMLCode = getTypeHTMLCode();
        buffer.append(typeHTMLCode);
        return buffer.toString();
    }
*/
    private String getTypeHTMLCode() {
        final String str1 = "http://www.omg.org/spec/UML/20110701/PrimitiveTypes.xmi#";
        final String str2 = "http://wtlab.um.ac.ir/uml.owl#";
        if (type.startsWith(str1)) {
            return "<i><a href=\"" + type + "\">" + type.substring(str1.length()) + "</a></i>";
        } else if (type.startsWith(str2)) {
            String href = Utils.correctURL(type);
            String typeName = type.substring(str2.length());//ModelGenerator.getElementName(type);
            return "<i><a href=\"" + href + "\">" + typeName + "</a></i>";
        } else {
            return type;
        }
    }
}
