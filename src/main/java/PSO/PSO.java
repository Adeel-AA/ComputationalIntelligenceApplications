/**
 * @author Adeel Ahmed 159141566
 */

package PSO;

import main.java.PricingHelpers.PricingProblem;

import java.util.*;

// Run PSO in this class
public class PSO {

    private Random random;
    private PricingProblem pricingProblem;
    private Swarm swarm;

    public PSO(int numberOfParticles, int numberOfItems) {
        pricingProblem = PricingProblem.courseworkInstance();
        swarm = new Swarm(pricingProblem, numberOfParticles, numberOfItems);
    }


    public static void main(String[] args) {
        ArrayList<Double> highestRevenue = new ArrayList<>();
        ArrayList<Double> initialRevenue = new ArrayList<>();
        // Setup parameters for algorithm
        int iterations = 100;
        int timesAlgorithmRan = 100;
        int population = 50;
        int numberOfItems = 20;

        // Generations based termination condition
        for (int i = 0; i < timesAlgorithmRan; i++) {
            // Setup algorithm with population and initialise swarm
            PSO pso = new PSO(population, numberOfItems);
            Swarm swarm = pso.swarm;

            double initial = swarm.getPricingProblem().evaluate(swarm.getGlobalBestPosition());
            // Start the algorithm given a number of iterations
            swarm.findBest(iterations);

            double finalRevenue = swarm.getPricingProblem().evaluate(swarm.getGlobalBestPosition());

            System.out.println("Initial Revenue " + initial);
            System.out.println("Best Revenue: " + finalRevenue);

            highestRevenue.add((Double) Collections.max(swarm.getFittest()));
            initialRevenue.add((Double) Collections.min(swarm.getFittest()));


        }

        System.out.println("Initial Revenue: " + Collections.min(initialRevenue));
        System.out.println("Swarm size of: " + population);
        System.out.println("Number of iterations: " + iterations);
        System.out.println("Number of times algorithm was run: " + timesAlgorithmRan);
        System.out.println("The highest revenue from the total number of runs was: " + Collections.max(highestRevenue));
        System.out.println("The lowest revenue from the total number of runs was: " + Collections.min(highestRevenue));


    }
}
