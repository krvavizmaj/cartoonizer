package mk.arsov.cartoonizer.config;

/**
 * Gaussian blur parameters.
 */
public class BlurConfiguration {

    /**
     * Sigma value for the gaussian blur.
     * */
    public static double SIGMA = 1;

    /**
     * The kernel radius, the dimensions of the kernel will be: radius * 2 + 1
     */
    public static int KERNEL_RADIUS = 9;
}
