package mk.arsov.cartoonizer.config;

/**
 * Line integral convolution parameters (Used to visualize ETF field).
 */
public class LineIntegralConvolutionConfiguration {

    /**
     * Line integral convolution kernel length
     *
     * The kernel length defines the number of points in each line segment in one direction (L).
     * The total length of the line segment therefore would be 2*L+1 (+1 is for the for center pixel)
     * If the line segment length is too large, the calculated value for all pixels in the image would
     * be very close to each other. On the other hand if the line segment length is too small, then an
     * insufficient amount of filtering occurs.
     * Cobral, Leedom - "Imaging vector fiellds using line integral convolution", page 3;
     * */
    public static int KERNEL_LENGTH = 10;

    /**
     * Roundoff.
     *
     * In the implementation of the algorithm, each delta s(i) is multiplied by a small roundoff
     * term, to insure that entry into to adjacent cell occurs.
     */
    public static double ROUND_OFF = 1.1;
}
