package mk.arsov.cartoonizer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "blur")
public class BlurConfiguration {

    /** Sigma value for the gaussian blur. */
    private double sigma;

    /** Radius of the kernel for the blur operator. */
    private int kernelRadius;

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public int getKernelRadius() {
        return kernelRadius;
    }

    public void setKernelRadius(int kernelRadius) {
        this.kernelRadius = kernelRadius;
    }
}
