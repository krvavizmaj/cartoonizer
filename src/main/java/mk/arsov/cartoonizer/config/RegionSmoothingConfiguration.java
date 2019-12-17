package mk.arsov.cartoonizer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fbl")
public class RegionSmoothingConfiguration {

    /** Sigma e value. */
    private double sigmaE;

    /** Sigma value for the color space distance Gaussian. */
    private double re;

    /** Length of the kernel along the flow axis, in one direction. */
    private int s;

    /** Sigma g value. */
    private double sigmaG;

    /** Sigma value for the color space distance Gaussian. */
    private double rg;

    /** Length of the kernel along the gradient vector, in one direction. */
    private int t;

    public double getSigmaE() {
        return sigmaE;
    }

    public void setSigmaE(double sigmaE) {
        this.sigmaE = sigmaE;
    }

    public double getRe() {
        return re;
    }

    public void setRe(double re) {
        this.re = re;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public double getSigmaG() {
        return sigmaG;
    }

    public void setSigmaG(double sigmaG) {
        this.sigmaG = sigmaG;
    }

    public double getRg() {
        return rg;
    }

    public void setRg(double rg) {
        this.rg = rg;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }
}
