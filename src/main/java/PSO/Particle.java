/**
 * @author Adeel Ahmed 159141566
 */

package PSO;

import main.java.PricingHelpers.PricingProblem;

import java.util.Random;

//Class which stores a single particle, each particle holding its own
//position, velocity and personal best position.
public class Particle {

    private PricingProblem pricingProblem;

    private double[] currentPosition;
    private double[] currentVelocity;
    private double[] personalBestPosition;
    // Most effective initial coefficients
    private final double initialInertialCoefficient = 0.9;
    private final double finalInertialCoefficient = 0.4;
    // Cognitive and social are the same as each other
    private final double cognitiveCoefficient = Math.log(2) + 0.5;
    private final double socialCoefficient = Math.log(2) + 0.5;

    public Particle(PricingProblem pricingProblem, double[] currentPosition, double[] currentVelocity) {

        this.pricingProblem = pricingProblem;
        this.currentPosition = currentPosition;
        this.personalBestPosition = this.currentPosition;
        this.currentVelocity = currentVelocity;
    }

    public void updateVelocity(double[] globalBestPosition, int iteration, int maximumIterations) {

        Random random = new Random();

        double[] newVelocity = new double[globalBestPosition.length];

        // Working out the linear decreasing inertia
        double inertialCoefficient = (initialInertialCoefficient -
                finalInertialCoefficient) * (maximumIterations - iteration) /
                maximumIterations + finalInertialCoefficient;

        double randomVector1 = random.nextDouble();
        double randomVector2 = random.nextDouble();

        // Calculation to work out new velocity based on pBest, gBest and coefficients
        for (int i = 0; i < globalBestPosition.length; i++) {
            double velocity = (inertialCoefficient * getCurrentVelocity()[i]) +
                    (cognitiveCoefficient * randomVector1 * (getPersonalBestPosition()[i] - getCurrentPosition()[i])) +
                    (socialCoefficient * randomVector2 * (globalBestPosition[i] - getCurrentPosition()[i]));

            newVelocity[i] = velocity;
        }

        setCurrentVelocity(newVelocity);

    }

    public void setCurrentPosition(double[] currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setCurrentVelocity(double[] currentVelocity) {
        this.currentVelocity = currentVelocity;
    }

    public void setPersonalBestPosition(double[] personalBestPosition) {
        this.personalBestPosition = personalBestPosition;
    }

    public double[] getCurrentPosition() {
        return currentPosition.clone();
    }

    public double[] getCurrentVelocity() {
        return currentVelocity.clone();
    }

    public double[] getPersonalBestPosition() {
        return personalBestPosition.clone();
    }
}
