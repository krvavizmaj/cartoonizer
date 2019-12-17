package mk.arsov.cartoonizer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fdog")
public class EdgeDetectionConfiguration {

    /**
     * Number of iterations. For each iteration, the previously calculated edge
     * image is superimposed to the original image and the process is run again,
     * the etf remains the same.
     */
    private int iterations;

    /** Determines the length of the line segments, S. */
    private double sigmaM;

    /** Controls the size of the center interval. */
    private double sigmaC;

    /** Controls the size of the surrounding interval. */
    private double sigmaS;

    /** Controls the level of noise detected. */
    private double ro;

    /**
     * Length of the line segments in the gradient direction, on one side of the
     * center pixel.
     */
    private int t;

    /** Length of line segments in one direction. */
    private int s;

    /** Threshold level for the final edge detection decision. */
    private double tau;

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public double getSigmaM() {
        return sigmaM;
    }

    public void setSigmaM(double sigmaM) {
        this.sigmaM = sigmaM;
    }

    public double getSigmaC() {
        return sigmaC;
    }

    public void setSigmaC(double sigmaC) {
        this.sigmaC = sigmaC;
    }

    public double getSigmaS() {
        return sigmaS;
    }

    public void setSigmaS(double sigmaS) {
        this.sigmaS = sigmaS;
    }

    public double getRo() {
        return ro;
    }

    public void setRo(double ro) {
        this.ro = ro;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public double getTau() {
        return tau;
    }

    public void setTau(double tau) {
        this.tau = tau;
    }
}
