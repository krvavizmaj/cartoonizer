package mk.arsov.cartoonizer.config;

/**
 * Edge detection parameters (Flow based difference of Gausians).
 */
public class EdgeDetectionConfiguration {

    /**
     * Number of iterations. For each iteration, the previously calculated edge
     * image is superimposed to the original image and the process is run again,
     * the etf remains the same.
     */
    public static int ITERATIONS = 3;

    /**
     * Determines the length of the line segments, S.
     * */
    public static double SIGMA_M = 3;

    /**
     * Controls the size of the center interval.
     * By selecting sigma.m, sigma.c is automatically obtained.
     */
    public static double SIGMA_C = 1;

    /**
     * Controls the size of the surrounding interval, usually 1.6*sigma.c
     * sigma.m determines the length of the gradient segments, T, in a way that
     * the value for the gaussian function for sigma.s and T is less than some value e (ex. e=0.001)
     */
    public static double SIGMA_S = 1.6;

    /**
     * Controls the level of noise detected, ranges in [0.97, 1.0].
     */
    public static double RO = 0.997;

    /**
     * Length of the line segments in the gradient direction, on one side of the center pixel.
     */
    public static int T = 4;

    /**
     * Length of line segments in one direction.
     */
    public static int S = 15;

    /**
     * Threshold level for the final edge detection decision.
     */
    public static double TAU = 0.5;
}
