/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ir.ac.um.wtlab.modelgenerator;

/**
 *
 * @author Home
 */
public class SoftwareCase {
    private String name;
    private UsecaseDiagram usecaseDiagram;
    
    public SoftwareCase(String name) {
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

    /**
     * @return the usecaseDiagram
     */
    public UsecaseDiagram getUsecaseDiagram() {
        return usecaseDiagram;
    }

    /**
     * @param usecaseDiagram the usecaseDiagram to set
     */
    public void setUsecaseDiagram(UsecaseDiagram usecaseDiagram) {
        this.usecaseDiagram = usecaseDiagram;
    }

    public int getUsecaseCount() {
        return usecaseDiagram.getUsecaseCount();
    }

    public String toString() {
        String result = "----------------------\n";
        result += "\tSC{ " + name + " }";
        result += usecaseDiagram;
        return result;
    }

}
