/**
 * @author Adeel Ahmed 159141566
 */

package GeneticAlgorithm;

import main.java.PricingHelpers.PricingProblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GeneticAlgorithm {

    private PricingProblem pricingProblem;
    private double[] bestPrices;
    private double globalBestRevenue;
    private ArrayList<double[]> population;
    private int tournamentSize;
    private double mutationProbability;
    private double recombinationProbability;

    private ArrayList<Double> fittest;

    public GeneticAlgorithm(int populationSize, int numberOfItems, double mutationProbability, double recombinationProbability) {
        pricingProblem = PricingProblem.courseworkInstance();

        population = new ArrayList<>();
        this.mutationProbability = mutationProbability;
        this.recombinationProbability = recombinationProbability;
        tournamentSize = populationSize / 2;

        // Initiates prices of valid solutions and adds them to population
        for (int i = 0; i < populationSize; i++) {
            double[] validPrices = getValidSolution(numberOfItems);
            population.add(validPrices);
        }

        fittest = new ArrayList<>();

        setFittest();
    }

    // Get a valid solution based on the bounds
    public double[] getValidSolution(int numberOfItems) {
        Random random = new Random();
        double[][] bounds = pricingProblem.bounds();

        double[] solution = new double[numberOfItems];

        while (!pricingProblem.is_valid(solution)) {
            solution = new double[numberOfItems];

            for (int i = 0; i < solution.length; i++) {
                double rangeMin = bounds[i][0];
                double rangeMax = bounds[i][1];
                solution[i] = rangeMin + (rangeMax + rangeMin) * random.nextDouble();
            }

        }
        return solution;

    }

    // Best revenue set of prices are set as initial fittest individuals
    public void setFittest() {
        // Sets first from population as best prices and revenue
        bestPrices = getPopulation().get(0);
        globalBestRevenue = pricingProblem.evaluate(getPopulation().get(0));

        for (double[] prices : getPopulation()) {
            double revenue = pricingProblem.evaluate(prices);

            if (revenue > globalBestRevenue) {
                bestPrices = prices;
                globalBestRevenue = revenue;
            }
        }
        fittest.add(pricingProblem.evaluate(bestPrices));
    }


    public double[] getTwoOptNeighbourhood(double[] prices) {

        double[] newPrices = prices.clone();

        ArrayList<double[]> twoOptNeighbourhood = new ArrayList<>();
        twoOptNeighbourhood.add(newPrices);

        for (int i = 0; i < newPrices.length - 1; i++) {

            for (int j = i + 1; j < newPrices.length; j++) {

                double[] reverseNewPrices = newPrices.clone();

                // Iterates over prices in reverse order
                for (int k = 0; k < reverseNewPrices.length / 2; k++) {
                    double temp = reverseNewPrices[k];
                    reverseNewPrices[i] = reverseNewPrices[reverseNewPrices.length - k - 1];
                    reverseNewPrices[reverseNewPrices.length - k - 1] = temp;
                }

                if (!twoOptNeighbourhood.contains(newPrices) && !twoOptNeighbourhood.contains(reverseNewPrices)) {
                    twoOptNeighbourhood.add(newPrices);
                }
                // Passes the values which are to be swapped to the method which does the swapping
                newPrices = performTwoOptNeighbourhoodSwap(i, j, prices);
            }

        }

        double[] fittestPrices = prices.clone();

        for (int i = 0; i < twoOptNeighbourhood.size(); i++) {
            double fittestRevenue = pricingProblem.evaluate(fittestPrices);
            double currentRevenue = pricingProblem.evaluate(twoOptNeighbourhood.get(i));

            // Evaluates current revenue to the new two opt neighbourhood
            // If they're better then set them as the fittest
            if (currentRevenue > fittestRevenue) {
                fittestPrices = twoOptNeighbourhood.get(i);
            }
        }
        return fittestPrices;
    }

    // Does the swapping of the positions
    public double[] performTwoOptNeighbourhoodSwap(int i, int j, double[] prices) {
        double[] pricesCopy = prices.clone();

        double temp = pricesCopy[i];
        pricesCopy[i] = pricesCopy[j];
        pricesCopy[j] = temp;

        return pricesCopy;
    }

    // Takes the population and adds best solution to new population
    // then gets parents and combines until new population is filled.
    // If the individuals are fitter then it updates and proceeds on
    // until termination condition is met.
    public void evolutionaryAlgorithm(ArrayList<double[]> population) {
        ArrayList<double[]> newPopulation = new ArrayList<>();

        double[] bestPricesCopy = getBestPrices().clone();

        newPopulation.add(bestPricesCopy);


        for (int i = newPopulation.size(); i < population.size(); i++) {

            ArrayList<double[]> parents = tournamentSelection(population);
            // Sets first and second parent from previous population once tournament selects the fittest
            double[] firstParent = parents.get(0);
            double[] secondParent = parents.get(1);

            double[] child = new double[firstParent.length];
            // Will run depending on what the recombination probability is set to
            if (Math.random() <= recombinationProbability) {
                // Using order 1 crossover, can chose midpoint crossover if you want
                child = order1Recombination(firstParent, secondParent);
//                child = midpointRecombination(firstParent, secondParent);
            }

            double[] mutant = child;
            // Will run depending on what the mutation probability is set to
            if (Math.random() <= mutationProbability) {
                mutant = twoOptSwapMutation(child);
            }

            double costOfMutation = pricingProblem.evaluate(mutant);

            if (costOfMutation > getGlobalBestRevenue()) {
                setBestPrices(mutant);
                setGlobalBestRevenue(costOfMutation);

                System.out.println("Found a new best total revenue of " + getGlobalBestRevenue());

            }
            newPopulation.add(mutant);

        }
        fittest.add(pricingProblem.evaluate(getBestPrices()));
        this.population = newPopulation;
    }


    // The two fittest individuals are returned to be parents
    public ArrayList<double[]> tournamentSelection(ArrayList<double[]> population) {

        ArrayList<double[]> populationCopy = (ArrayList<double[]>) population.clone();

        Collections.shuffle(populationCopy);

        ArrayList<double[]> parents = new ArrayList<>(2);
        parents.add(populationCopy.get(0));
        parents.add(populationCopy.get(1));

        for (int i = 2; i < tournamentSize; i++) {
            double[] currentPrices = populationCopy.get(i);

            for (int j = 0; j < parents.size(); j++) {
                double bestPricesRevenue = pricingProblem.evaluate(parents.get(j));
                double currentPricesRevenue = pricingProblem.evaluate(currentPrices);

                if (currentPricesRevenue > bestPricesRevenue) {
                    parents.set(j, currentPrices);
                    break;
                }
            }
        }
        return parents;
    }

    // Selects a start and end point at random then copies that piece over
    // from first parent and merges with the remaining pieces from second parent to create child
    public double[] order1Recombination(double[] firstParent, double[] secondParent) {
        Random random = new Random();

        // setup the random start and end point
        int firstNumber = (int) (Math.random() * firstParent.length);
        int secondNumber = (int) (Math.random() * firstParent.length);
        int startPoint = Math.min(firstNumber, secondNumber);
        int endPoint = Math.max(firstNumber, secondNumber);

        double[] child = new double[firstParent.length];
        // Copy from first parent
        for (int i = startPoint; i <= endPoint; i++) {
            child[i] = firstParent[i];
        }
        // Copy from second parent
        for (int i = 0; i < startPoint; i++) {
            if (child[i] == 0.0) {
                child[i] = secondParent[i];
            }
        }

        for (int i = endPoint; i < firstParent.length; i++) {
            if (child[i] == 0.0) {
                child[i] = secondParent[i];
            }
        }
        return child;
    }

    // Alternative recombination crossover algorithm
    public double[] midpointRecombination(double[] firstParent, double[] secondParent) {
        Random random = new Random();
        double[] child = new double[firstParent.length];

        // Gets the mid point of each element and create a new child from it
        for (int i = 0; i < firstParent.length; i++) {
            double midPoint = (firstParent[i] + secondParent[i]) / 2;
            child[i] = midPoint;
        }
        return child;

    }

    public double[] twoOptSwapMutation(double[] prices) {
        return getTwoOptNeighbourhood(prices);
    }

    public ArrayList<double[]> getPopulation() {
        return population;
    }

    public double[] getBestPrices() {
        return bestPrices;
    }

    public void setBestPrices(double[] bestPrices) {
        this.bestPrices = bestPrices;
    }

    public double getGlobalBestRevenue() {
        return globalBestRevenue;
    }

    public ArrayList getFittestArray() {
        return fittest;
    }

    public void setGlobalBestRevenue(double globalBestRevenue) {
        this.globalBestRevenue = globalBestRevenue;
    }
}
