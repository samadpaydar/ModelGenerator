/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator;

import java.util.*;

/**
 *
 * @author Home
 */
public class Usecase {

    private String uri;
    private String name;
    private UsecaseDiagram usecaseDiagram;
    private SoftwareCase softwareCase;
    private Actor[] actors;
    private ArrayList<Usecase> extenderUsecases;
    private ArrayList<Usecase> extendedUsecases;
    private ArrayList<Usecase> generalUsecases;
    private ArrayList<Usecase> specificUsecases;

    public Usecase(String uri, String name) {
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
        return "['" + getName() + "' + actors:{" + Arrays.toString(actors) + "}]";
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

    /**
     * @return the softwareCase
     */
    public SoftwareCase getSoftwareCase() {
        return softwareCase;
    }

    /**
     * @param softwareCase the softwareCase to set
     */
    public void setSoftwareCase(SoftwareCase softwareCase) {
        this.softwareCase = softwareCase;
    }

    /**
     * @return the actors
     */
    public Actor[] getActors() {
        return actors;
    }

    /**
     * @param actors the actors to set
     */
    public void setActors(Actor[] actors) {
        this.actors = actors;
    }

    /**
     * @return the extenderUsecaseNames
     */
    public ArrayList<Usecase> getExtenderUsecases() {
        return extenderUsecases;
    }

    /**
     * @param extenderUsecases the extenderUsecases to set
     */
    public void setExtenderUsecases(ArrayList<Usecase> extenderUsecases) {
        this.extenderUsecases = extenderUsecases;
    }

    /**
     * @return the extendedUsecases
     */
    public ArrayList<Usecase> getExtendedUsecases() {
        return extendedUsecases;
    }

    /**
     * @param extendedUsecases the extendedUsecases to set
     */
    public void setExtendedUsecases(ArrayList<Usecase> extendedUsecases) {
        this.extendedUsecases = extendedUsecases;
    }

    /**
     * @return the generalUsecases
     */
    public ArrayList<Usecase> getGeneralUsecases() {
        return generalUsecases;
    }

    /**
     * @param generalUsecases the generalUsecases to set
     */
    public void setGeneralUsecases(ArrayList<Usecase> generalUsecases) {
        this.generalUsecases = generalUsecases;
    }

    /**
     * @return the specificUsecases
     */
    public ArrayList<Usecase> getSpecificUsecases() {
        return specificUsecases;
    }

    /**
     * @param specificUsecases the specificUsecases to set
     */
    public void setSpecificUsecases(ArrayList<Usecase> specificUsecases) {
        this.specificUsecases = specificUsecases;
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
