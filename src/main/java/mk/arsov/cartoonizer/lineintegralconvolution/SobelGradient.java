package mk.arsov.cartoonizer.lineintegralconvolution;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculates Sobel operator.
 */
public class SobelGradient {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Sobel gradient X kernel. */
    static final float[][] GX = {
        {1.0F / 9.0F,  0.0F / 9.0F, -1.0F / 9.0F},
        {2.0F / 9.0F,  0.0F / 9.0F, -2.0F / 9.0F},
        {1.0F / 9.0F,  0.0F / 9.0F, -1.0F / 9.0F}};

    /** Sobel gradient Y kernel. */
    static final float[][] GY = {
        {-1.0F / 9.0F, -2.0F / 9.0F, -1.0F / 9.0F},
        {0.0F / 9.0F,  0.0F / 9.0F,  0.0F / 9.0F},
        {1.0F / 9.0F,  2.0F / 9.0F,  1.0F / 9.0F}};

    /**
     * Calculate tangent vector fields.
     * Tangent vectors are vectors normal to the gradient vectors. For each point
     * in the image, the tangent vector is a counterclockwise vector perpendicular to the
     * gradient vector at that point.
     *
     * @param sourceImage the input image.
     * @return vector field representing the tangents of the gradient vectors in each pixel.
     */
    public Point2D.Double[][] calculateTangentVectorField(final BufferedImage sourceImage) {
        long sobelStartTime = System.currentTimeMillis();

        // This gets rid of exception for not using native acceleration
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");

        // BufferedImage grayscaleImage = imageUtils.toGrayscale(sourceImage);
        BufferedImage grayscaleImage = sourceImage;

        // gradient x and y matrices, as defined in Sobel gradient algorithm
        double[][] gradientX = calculateGradientX(grayscaleImage.getData());
        double[][] gradientY = calculateGradientY(grayscaleImage.getData());

        // Compose tangent vector field.
        // Vector components are calculated in right-hand coordinate system whereas image coordinates are
        // given in left-hand coordinate system. Therefore the y coordinate from the gradient vector should
        // be inverted so we get the gradient vector in left-hand coordinate system.
        //
        // Also tangent vector is normal to gradient vector, so the vector should be rotated counterclockwise.
        // Counterclockwise normal vector to (x, y) is (-y, x), plus inversion of the y coordinate we get (y, x).
        Point2D.Double[][] tangentVectorField = new Point2D.Double[sourceImage.getHeight()][sourceImage.getWidth()];
        for (int i = 0; i < sourceImage.getHeight(); i++) {
            for (int j = 0; j < sourceImage.getWidth(); j++) {
                final Point2D.Double tangentVector = new Point2D.Double(gradientY[i][j], gradientX[i][j]);
                tangentVectorField[i][j] = tangentVector;
            }
        }

        logger.info("Sobel gradient calculation finished in {} seconds",
            (System.currentTimeMillis() - sobelStartTime) / 1000);
        return tangentVectorField;
    }

    /**
     * Calculate sobel gradient in x direction.
     *
     * @param grayscaleImage the input image in grayscale format
     * @return array of x values from sobel operator
     */
    protected double[][] calculateGradientX(final Raster grayscaleImage) {
        double[][] result = new double[grayscaleImage.getHeight()][grayscaleImage.getWidth()];

        // convolution, gradient x
        for (int i = 0; i < grayscaleImage.getHeight(); i++) {
            for (int j = 0; j < grayscaleImage.getWidth(); j++) {
                double sum = 0;

                for (int i1 = 0; i1 < 3; i1++) {
                    for (int j1 = 0; j1 < 3; j1++) {
                        final int y = i - (3 / 2) + i1;
                        final int x = j - (3 / 2) + j1;
                        if ((x >= 0) && (y >= 0) && (x < grayscaleImage.getWidth()) && (y < grayscaleImage.getHeight())) {
                            sum += grayscaleImage.getSample(x, y, 0) * GX[i1][j1];
                        }
                    }
                }

                result[i][j] = Math.round(sum);
            }
        }

        return result;
    }

    /**
     * Calculate sobel gradient in y direction.
     *
     * @param grayscaleImage the input image in grayscale mode
     * @return array of y values from sobel operator
     */
    protected double[][] calculateGradientY(final Raster grayscaleImage) {
        double[][] result = new double[grayscaleImage.getHeight()][grayscaleImage.getWidth()];

        // convolution, gradient y
        for (int i = 0; i < grayscaleImage.getHeight(); i++) {
            for (int j = 0; j < grayscaleImage.getWidth(); j++) {
                double sum = 0;

                for (int i1 = 0; i1 < 3; i1++) {
                    for (int j1 = 0; j1 < 3; j1++) {
                        final int y = i - (3 / 2) + i1;
                        final int x = j - (3 / 2) + j1;
                        if ((x >= 0) && (y >= 0) && (x < grayscaleImage.getWidth()) && (y < grayscaleImage.getHeight())) {
                            sum += grayscaleImage.getSample(x, y, 0) * GY[i1][j1];
                        }
                    }
                }

                result[i][j] = Math.round(sum);
            }
        }

        return result;
    }

}
