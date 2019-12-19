package mk.arsov.cartoonizer.config;

/**
 * Region smoothing parameters (Flow based bilateral filter).
 */
public class RegionSmoothingConfiguration {

    /**
     * Determines the kernel size S for the Gaussian along the flow axis
     */
    public static double SIGMA_E = 2;

    /**
     * Sigma value for the Gaussian in the color space distance
     */
    public static double RE = 50;

    /**
     * length of the kernel for the Gaussian along the flow axis.
     */
    public static int S = 6;

    /**
     * Determines the kernel size T for the Gaussian along the gradient vector.
     */
    public static double SIGMA_G = 2;

    /**
     * Sigma value for the Gaussian in the color space distance.
     */
    public static double RG = 10;

    /**
     * length of the kernel for the Gaussian along the gradient vector.
     */
    public static int T = 6;
}
