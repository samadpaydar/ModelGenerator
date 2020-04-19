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
public class UsecaseDiagram {
    private Usecase[] usecases;
    private SoftwareCase softwareCase;

    public UsecaseDiagram(int count) {
        usecases = new Usecase[count];
    }

    public void setUsecase(int i, Usecase usecase) {
        usecases[i] = usecase;
    }

    public Usecase getUsecase(int i) {
        return usecases[i];
    }

    public String toString() {
        return Arrays.toString(usecases);
    }

    public int getUsecaseCount() {
        return usecases.length;
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
    
}
