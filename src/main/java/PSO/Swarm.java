/**
 * @author Adeel Ahmed 159141566
 */

package PSO;

import main.java.PricingHelpers.PricingProblem;

import java.util.ArrayList;
import java.util.Random;

public class Swarm {

    private PricingProblem pricingProblem;
    private double[] globalBestPosition;
    private double globalBestRevenue;
    private ArrayList<Particle> particles;
    private int numberOfParticles;
    private int numberOfItems;

    private ArrayList<Double> fittest;

    public Swarm(PricingProblem pricingProblem, int numberOfParticles, int numberOfItems) {

        this.pricingProblem = pricingProblem;
        this.numberOfItems = numberOfItems;
        this.numberOfParticles = numberOfParticles;
        particles = new ArrayList<>(numberOfParticles);

        // Setup swarm with particles
        for (int i = 0; i < numberOfParticles; i++) {
            addParticleToSwarm();
        }
        fittest = new ArrayList<>();

        //Initialise global best position
        globalBestPosition(getParticles());

    }

    public void addParticleToSwarm() {
        double[] validSolution = getValidSolution(numberOfItems);
        double[] velocity = getVelocity(numberOfItems);
        Particle particle = new Particle(pricingProblem, validSolution, velocity);
        particles.add(particle);
    }

    // Setup velocity for each position to be half the difference between first and second solution
    public double[] getVelocity(int numberOfItems) {
        Random random = new Random();
        double[] firstSolution = getValidSolution(numberOfItems);
        double[] secondSolution = getValidSolution(numberOfItems);
        double[] velocity = new double[numberOfItems];

        for (int i = 0; i < velocity.length; i++) {
            double differenceBetween = firstSolution[i] - secondSolution[i];
            velocity[i] = differenceBetween / 2;
        }
        return velocity;
    }

    // Find best revenue from the initial swarm and set to the global best
    public void globalBestPosition(ArrayList<Particle> particles) {
        for (Particle particle : particles) {
            if (globalBestPosition == null) {
                setGlobalBestPosition(particle.getCurrentPosition());
            }

            double result = pricingProblem.evaluate(particle.getCurrentPosition());

            if (result > pricingProblem.evaluate(globalBestPosition)) {
                setGlobalBestPosition(particle.getCurrentPosition());
            }
        }
        fittest.add(getPricingProblem().evaluate(getGlobalBestPosition()));
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

    // Finds the best revenue from all of the particles and updates corresponding global and personal bests
    public void findBest(int iterations) {
        for (int i = 1; i <= iterations; i++) {

            for (Particle particle : getParticles()) {


                particle.updateVelocity(getGlobalBestPosition(), i, iterations);
                updatePosition(particle);

                double result = pricingProblem.evaluate(particle.getCurrentPosition());
                double personalBestResult = pricingProblem.evaluate(particle.getPersonalBestPosition());

                if (result > personalBestResult) {
                    particle.setPersonalBestPosition(particle.getCurrentPosition());
                }

                double globalBestResult = pricingProblem.evaluate(getGlobalBestPosition());

                if (personalBestResult > globalBestResult) {
                    setGlobalBestPosition(particle.getPersonalBestPosition());
                    setGlobalBestRevenue(personalBestResult);

                    System.out.println("Found a new best total revenue of " + globalBestRevenue);
                }
            }

            fittest.add(getPricingProblem().evaluate(getGlobalBestPosition()));
        }
    }

    // Updates position of particle using velocity and checks if valid then sets it as the new position
    public void updatePosition(Particle particle) {
        double[] newPosition = particle.getCurrentPosition();

        for (int i = 0; i < particle.getCurrentPosition().length; i++) {
            newPosition[i] = particle.getCurrentPosition()[i] + particle.getCurrentVelocity()[i];

            if (!pricingProblem.is_valid(newPosition)) {
                newPosition[i] = particle.getCurrentPosition()[i];
            }

        }
        particle.setCurrentPosition(newPosition);
    }

    public void setGlobalBestPosition(double[] solution) {
        globalBestPosition = solution;
    }

    public void setGlobalBestRevenue(double revenue) {
        globalBestRevenue = revenue;
    }

    public double[] getGlobalBestPosition() {
        return globalBestPosition;
    }

    public double getGlobalBestRevenue() {
        return globalBestRevenue;
    }

    public ArrayList<Particle> getParticles() {
        return particles;
    }

    public ArrayList getFittest() {
        return fittest;
    }

    public PricingProblem getPricingProblem() {
        return pricingProblem;
    }


}
