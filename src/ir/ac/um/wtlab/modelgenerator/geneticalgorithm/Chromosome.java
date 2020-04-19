/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.geneticalgorithm;

import java.security.InvalidParameterException;

/**
 *
 * @author Home
 */
public class Chromosome implements Comparable {

    private double[] genes;
    public final static int LENGTH = 5;
    private double fitnessValue;

    public Chromosome(double[] genes) {
        this.genes = genes;
    }

    public double getGene(int index) {
        if (index >= 0 && index < genes.length) {
            return genes[index];
        } else {
            throw new InvalidParameterException("Gene index out of bounds: " + index + ", genes count: " + genes.length);
        }
    }
    public void setGene(int index, double value) {
        if (index >= 0 && index < genes.length) {
            genes[index] = value;
        } else {
            throw new InvalidParameterException("Gene index out of bounds: " + index + ", genes count: " + genes.length);
        }
    }

    /**
     * @return the fitnessValue
     */
    public double getFitnessValue() {
        return fitnessValue;
    }

    /**
     * @param fitnessValue the fitnessValue to set
     */
    public void setFitnessValue(double fitnessValue) {
        this.fitnessValue = fitnessValue;
    }

    public int compareTo(Object other) {
        if (other instanceof Chromosome) {
            Chromosome otherChromosome = (Chromosome) other;
            if (this.fitnessValue > otherChromosome.fitnessValue) {
                return +1;
            } else if (this.fitnessValue < otherChromosome.fitnessValue) {
                return -1;
            } else {
                return 0;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String toString() {
        String result = "[weights: ";
        for(double gene:genes) {
            result += gene + " , ";
        }
        result += " fitness: " + fitnessValue + "]";
        return result;
    }
}
