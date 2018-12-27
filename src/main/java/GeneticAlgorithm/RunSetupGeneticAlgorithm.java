/**
 * @author Adeel Ahmed 159141566
 */

package GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Collections;

public class RunSetupGeneticAlgorithm {
    private GeneticAlgorithm geneticAlgorithm;

    public RunSetupGeneticAlgorithm(int populationSize, int numberOfItems, double mutationProbability, double recombinationProbability) {
        geneticAlgorithm = new GeneticAlgorithm(populationSize, numberOfItems, mutationProbability, recombinationProbability);
    }

    public static void main(String[] args) {
        ArrayList<Double> highestRevenue = new ArrayList<>();
        ArrayList<Double> initialRevenue = new ArrayList<>();

        // Setup parameters for algorithm
        int iterations = 100;
        int timesAlgorithmRan = 100;
        int population = 50;
        int numberOfItems = 20;
        double mutationProbability = 0.7; // 70% probability
        double recombinationProbability = 1.0; // 100% probability

        // Generations based termination condition
        for (int i = 0; i < timesAlgorithmRan; i++) {

            RunSetupGeneticAlgorithm runSetupGeneticAlgorithm = new RunSetupGeneticAlgorithm(population,
                    numberOfItems, mutationProbability, recombinationProbability);
            GeneticAlgorithm geneticAlgorithm = runSetupGeneticAlgorithm.geneticAlgorithm;

            // Gets initial revenue
            double initial = geneticAlgorithm.getGlobalBestRevenue();

            // Evolves each iteration
            for (int j = 0; j < iterations; j++) {
                geneticAlgorithm.evolutionaryAlgorithm(geneticAlgorithm.getPopulation());
            }

            System.out.println("Initial best revenue: " + initial);
            System.out.println("Best revenue: " + geneticAlgorithm.getGlobalBestRevenue());

            highestRevenue.add((Double) Collections.max(geneticAlgorithm.getFittestArray()));
            initialRevenue.add((Double) Collections.min(geneticAlgorithm.getFittestArray()));

        }
        System.out.println("Initial Revenue: " + Collections.min(initialRevenue));
        System.out.println("Population size of: " + population);
        System.out.println("Number of iterations: " + iterations);
        System.out.println("Number of times algorithm was run: " + timesAlgorithmRan);
        System.out.println("The highest revenue from the total number of runs was: " + Collections.max(highestRevenue));
        System.out.println("The lowest revenue from the total number of runs was: " + Collections.min(highestRevenue));


    }
}