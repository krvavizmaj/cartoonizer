package mk.arsov.cartoonizer.util;

import org.springframework.beans.factory.annotation.Value;

public class Configuration {

    /** Sigma value for the gaussian blur. */
    @Value("${blur.sigma}")
    private double blurSigma;

    /** Radius of the kernel for the blur operator. */
    @Value("${blur.radius}")
    private int blurKernelRadius;

    /** Kernel length. */
    @Value("${lic.kernellength}")
    private int licKernelLength;

    /** Roundoff. */
    @Value("${lic.roundoff}")
    private double licRoundoff;

    /** The radius of the ETF kernel. */
    @Value("${etf.kernelradius}")
    private int etfKernelRadius;

    /** Number of iterations. */
    @Value("${etf.numberofiterations}")
    private int numberOfIterations;

    /**
     * Number of iterations. For each iteration, the previously calculated edge
     * image is superimposed to the original image and the process is run again,
     * the etf remains the same.
     */
    @Value("${fdog.iterations}")
    private int fdogIterations;

    /** Controls the size of the center interval. */
    @Value("${fdog.sigma.c}")
    private double sigmaC;

    /** Controls the size of the surrounding interval. */
    @Value("${fdog.sigma.s}")
    private double sigmaS;

    /** Determines the length of the line segments, S. */
    @Value("${fdog.sigma.m}")
    private double sigmaM;

    /** Controls the level of noise detected. */
    @Value("${fdog.ro}")
    private double ro;

    /**
     * Length of the line segments in the gradient direction, on one side of the
     * center pixel.
     */
    @Value("${fdog.t}")
    private int t;

    /** Length of line segments in one direction. */
    @Value("${fdog.s}")
    private int fdogLineSegmentsLength;

    /** Threshold level for the final edge detection decision. */
    @Value("${fdog.tau}")
    private double tau;

    /** Sigma e value. */
    @Value("${fbl.sigmae}")
    private double sigmaE;

    /** Sigma g value. */
    @Value("${fbl.sigmag}")
    private double sigmaG;

    /** Length of the kernel along the flow axis, in one direction. */
    @Value("${fbl.s}")
    private int fblLineSegmentsLength;

    /** Length of the kernel along the gradient vector, in one direction. */
    @Value("${fbl.t}")
    private int gradientSegmentLength;

    /** Sigma value for the color space distance Gaussian. */
    @Value("${fbl.re}")
    private double re;

    /** Sigma value for the color space distance Gaussian. */
    @Value("${fbl.rg}")
    private double rg;
}
