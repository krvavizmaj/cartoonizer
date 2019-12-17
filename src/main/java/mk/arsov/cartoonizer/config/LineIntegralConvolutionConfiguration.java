package mk.arsov.cartoonizer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lic")
public class LineIntegralConvolutionConfiguration {

    /** Kernel length. */
    private int kernelLength;

    /** Roundoff. */
    private double roundoff;

    public int getKernelLength() {
        return kernelLength;
    }

    public void setKernelLength(int kernelLength) {
        this.kernelLength = kernelLength;
    }

    public double getRoundoff() {
        return roundoff;
    }

    public void setRoundoff(double roundoff) {
        this.roundoff = roundoff;
    }
}
