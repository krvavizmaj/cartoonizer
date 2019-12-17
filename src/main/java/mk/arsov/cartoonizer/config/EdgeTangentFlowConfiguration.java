package mk.arsov.cartoonizer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "etf")
public class EdgeTangentFlowConfiguration {

    /** The radius of the ETF kernel. */
    private int kernelRadius;

    /** Number of iterations. */
    private int numberOfIterations;

    public int getKernelRadius() {
        return kernelRadius;
    }

    public void setKernelRadius(int kernelRadius) {
        this.kernelRadius = kernelRadius;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    public void setNumberOfIterations(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
    }
}
