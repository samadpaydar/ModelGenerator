/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.um.wtlab.modelgenerator.geneticalgorithm;

import java.io.PrintStream;
import java.io.*;
import java.util.Arrays;
import java.util.Random;
import ir.ac.um.wtlab.modelgenerator.ModelGenerator;
import ir.ac.um.wtlab.modelgenerator.similarity.UsecaseSimilarityCalculator;

/**
 *
 * @author Home
 */
public class GeneticAlgorithm {

    private final int POPULATION = 50;
    private Chromosome[] chromosomes;
    private Random randomGenerator;
    private ModelGenerator modelGenerator;
    public static double mutationProbability = 0.1;
    private TestCase[] testCases;

    public GeneticAlgorithm(ModelGenerator modelGenerator, TestCase[] testCases) {
        this.modelGenerator = modelGenerator;
        this.testCases = testCases;
        randomGenerator = new Random();
    }

    public void start(int generationCount) {
        try {
            long start = System.currentTimeMillis();
            System.setOut(new PrintStream("log_" + start + ".log"));
            //createInitialPopulation();
            loadInitialPopulation();
            for (int i = 1; i < generationCount; i++) {
                System.out.println("GENERATION #" + (i + 1));
                evaluatePopulation();
                System.out.println("----------- POPULATION " + (i + 1));
                int j = 0;
                for (Chromosome chromosome : chromosomes) {
                    System.out.println("chromosome[" + (++j) + "]: " + chromosome);
                }
                generateNextGeneration();
                updateMutationProbability(i);
            }
            System.out.println("Finished");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMutationProbability(int generation) {
        if (generation < 10) {
            mutationProbability = 0.2;
        } else if (generation >= 10 && generation < 15) {
            mutationProbability = 0.1;
        } else {
            mutationProbability = 0.05;
        }
    }

    /**
     * The next generation is created this way:
     * 1. first 50% of the best chromosomes of the current generation are selected
     * 2. The other 50% are created by cross_over, i.e. by randomly selecting two chromosomes and
     * crossing them over. The cross over is this way: a point is randomly selected and the two chromosomes
     * are crossed at that point and one side from each chromosome is selected.
     * 3. After 100% of the generation is created each gene of each chromosome is mutated by a very low probability
     */
    private void generateNextGeneration() {
        Chromosome[] newChromosomes = new Chromosome[POPULATION];
        int half = POPULATION / 2;
        for (int i = 0, j = POPULATION - 1; i < half; i++, j--) {
            newChromosomes[i] = chromosomes[j];
        }
        for (int i = half; i < POPULATION; i++) {
            int index1 = randomGenerator.nextInt(POPULATION);
            int index2 = randomGenerator.nextInt(POPULATION);
            Chromosome chromosome = crossOverChromosomes(index1, index2);
            newChromosomes[i] = chromosome;
        }
        chromosomes = newChromosomes;
        mutateChromosomes();
    }

    private void mutateChromosomes() {
        for (Chromosome chromosome : chromosomes) {
            for (int i = 0; i < Chromosome.LENGTH; i++) {
                double temp = randomGenerator.nextDouble();
                if (temp < mutationProbability) {
                    double newValue = randomGenerator.nextDouble();
                    System.out.println("mutated from " + chromosome.getGene(i) + " to " + newValue);
                    chromosome.setGene(i, newValue);
                }
            }
        }
    }

    private Chromosome crossOverChromosomes(int index1, int index2) {
        double[] genes = new double[Chromosome.LENGTH];
        int point = randomGenerator.nextInt(Chromosome.LENGTH);
        for (int i = 0; i < point; i++) {
            genes[i] = chromosomes[index1].getGene(i);
        }
        for (int i = point; i < Chromosome.LENGTH; i++) {
            genes[i] = chromosomes[index2].getGene(i);
        }
        return new Chromosome(genes);
    }

    private void createInitialPopulation() {
        chromosomes = new Chromosome[POPULATION];
        for (int i = 0; i < chromosomes.length; i++) {
            double[] genes = new double[Chromosome.LENGTH];
            for (int j = 0; j < genes.length; j++) {
                genes[j] = randomGenerator.nextDouble();
            }
            chromosomes[i] = new Chromosome(genes);
        }
    }

    private void loadInitialPopulation() {
        try {
            BufferedReader file = new BufferedReader(new FileReader("GA_chromosomes.txt"));
            chromosomes = new Chromosome[POPULATION];
            for (int i = 0; i < chromosomes.length; i++) {
                String line = file.readLine();
                int index1 = line.indexOf("weights:");
                int index2 = line.indexOf("fitness:");
                String weights = line.substring(index1 + "weights:".length(), index2);
                String[] values = weights.split(",");
                double[] genes = new double[Chromosome.LENGTH];
                for (int j = 0; j < genes.length; j++) {
                    genes[j] = Double.parseDouble(values[j].trim());
                }
                chromosomes[i] = new Chromosome(genes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void evaluatePopulation() {
        double sum = 0.0;
        int i = 0;
        for (Chromosome chromosome : chromosomes) {
            i++;
            //System.out.println("#" + i + ": " + chromosome);
            long start = System.currentTimeMillis();
            double fitness = evaluateChromosome(chromosome);
            long end = System.currentTimeMillis();
            //System.out.println("evaluating a single chromosome took " + (end - start) + " ms");
            sum += fitness;
            //System.out.println("fitness: " + chromosome.getFitnessValue());
        }
        /***
         * NOTE: this sort is ascending, i.e. from the worst chromosome to the best
         */
        Arrays.sort(chromosomes);
        double average = sum / POPULATION;
        System.out.println("average fitness: " + average);
        System.out.println("best fitness: " + chromosomes[chromosomes.length - 1].getFitnessValue());
        System.out.println("best chromosome: " + chromosomes[chromosomes.length - 1]);
    }

    private double evaluateChromosome(Chromosome chromosome) {
        UsecaseSimilarityCalculator.setSemanticSimilarityWeight(chromosome.getGene(0));
        UsecaseSimilarityCalculator.setRelationalSimilarityWeight(chromosome.getGene(1));
        UsecaseSimilarityCalculator.setBehaviorSimilarityWeight(chromosome.getGene(2));
        UsecaseSimilarityCalculator.setConceptSimilarityWeight(chromosome.getGene(3));
        UsecaseSimilarityCalculator.setSubjectSimilarityWeight(chromosome.getGene(4));

        UsecaseSimilarityCalculator.setExtendedSimilarityWeight(1.0);
        UsecaseSimilarityCalculator.setExtenderSimilarityWeight(1.0);
        UsecaseSimilarityCalculator.setGeneralSimilarityWeight(1.0);
        UsecaseSimilarityCalculator.setSpecificSimilarityWeight(1.0);
        UsecaseSimilarityCalculator.setIncludedSimilarityWeight(1.0);
        UsecaseSimilarityCalculator.setIncluderSimilarityWeight(1.0);
        UsecaseSimilarityCalculator.setActorSimilarityWeight(1.0);
        double sum = 0.0;
        for (TestCase testCase : testCases) {
            String inputSCName = testCase.getInputSCName();
            String inputUCName = testCase.getInputUCName();
            Object[][] result = modelGenerator.calculateUsecaseSimilarities(inputSCName, inputUCName);
            double fitness = computeFitness(result, testCase.getExpectedResults());
            sum += fitness;
        }
        double averageFitness = sum / testCases.length;
        chromosome.setFitnessValue(averageFitness);
        return averageFitness;
    }

    private double computeFitness(Object[][] results, String[][] expectedResults) {
        double fitness = 0;
        double precisionSum = 0.0;

        final int LEVEL = expectedResults.length;
        for (int level = 1; level <= LEVEL; level++) {
            int count = 0;
            int i;
            loop:
            for (i = 0; i < results.length; i++) {
                String scName = (String) results[i][0];
                String ucName = (String) results[i][1];
                for (int j = 0; j < expectedResults.length; j++) {
                    String scName2 = expectedResults[j][0];
                    String ucName2 = expectedResults[j][1];
                    if (scName.equalsIgnoreCase(scName2) && ucName.equalsIgnoreCase(ucName2)) {
                        count++;
                        if (count == level) {
                            break loop;
                        }
                    }
                }
            }
            double precision = ((double) level) / (i + 1);
            precisionSum += precision;
        }
        fitness = precisionSum / LEVEL;
        return fitness;
    }
}
