package mk.arsov.cartoonizer.abstraction;

import java.awt.geom.Point2D;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculate edge tangent flow.
 * See "Kang, Lee, Chui - Flow-Based Image Abstraction, 2009", chapter 2.2
 */
public class EdgeTangentFlow {
  
    /** Logger. */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Calculate the edge tangent flow.
     *
     * @param tangentVectorField the vector field used to calculate ETF
     * @param numberOfIterations
     * @param etfKernelRadius
     * @return vector field containing the ETF vectors.
     */
    public Point2D.Double[][] calculate(final Point2D.Double[][] tangentVectorField,
            final int numberOfIterations, final int etfKernelRadius) {
        long etfStartTime = System.currentTimeMillis();

        /** Gradient vector field. */
        final double[][] normalizedGradientMagnitude = new double[tangentVectorField.length][tangentVectorField[0].length];
        final Point2D.Double[][] normalizedTangentVectors =
            new Point2D.Double[tangentVectorField.length][tangentVectorField[0].length];

        // calculate normalized gradient magnitude field and normalized tangent vectors
        double gradientMagnitudeSum = 0;
        for (int i = 0; i < tangentVectorField.length; i++) {
            for (int j = 0; j < tangentVectorField[0].length; j++) {
                normalizedGradientMagnitude[i][j] = Math.sqrt(tangentVectorField[i][j].getX() * tangentVectorField[i][j].getX()
                    + tangentVectorField[i][j].getY() * tangentVectorField[i][j].getY());

                if (normalizedGradientMagnitude[i][j] != 0) {
                    normalizedTangentVectors[i][j] =
                        new Point2D.Double(tangentVectorField[i][j].getX() / normalizedGradientMagnitude[i][j],
                          tangentVectorField[i][j].getY() / normalizedGradientMagnitude[i][j]);
                } else {
                  normalizedTangentVectors[i][j] =
                      new Point2D.Double(tangentVectorField[i][j].getX(), tangentVectorField[i][j].getY());
                }

                gradientMagnitudeSum += normalizedGradientMagnitude[i][j];
            }
        }

        // normalize gradient magnitude
        for (int i = 0; i < tangentVectorField.length; i++) {
            for (int j = 0; j < tangentVectorField[0].length; j++) {
                normalizedGradientMagnitude[i][j] = normalizedGradientMagnitude[i][j] / gradientMagnitudeSum;
            }
        }

        double[][] summedVectorX;
        double[][] summedVectorY;
        double magnitude = 1;

        // calculate ETF, in several iterations
        for (int k = 0; k < numberOfIterations; k++) {
            logger.info("ETF iteration {}", k);
            summedVectorX = new double[tangentVectorField.length][tangentVectorField[0].length];
            summedVectorY = new double[tangentVectorField.length][tangentVectorField[0].length];

            // for each pixel
            for (int i = 0; i < tangentVectorField.length; i++) {
                for (int j = 0; j < tangentVectorField[0].length; j++) {

                    // for each pixel in the kernel
                    for (int y = i - etfKernelRadius; y < i + etfKernelRadius + 1; y++) {
                        for (int x = j - etfKernelRadius; x < j + etfKernelRadius + 1; x++) {

                            if ((x >= 0) && (y >= 0) && (x < tangentVectorField[0].length) && (y < tangentVectorField.length)) {
                                // fi function is ommited, see comment for wd() method
                                magnitude =
                  //                  fi(normalizedTangentVectors[i][j], normalizedTangentVectors[y][x])
                                    ws(j, i, x, y, etfKernelRadius)
                                    * wm(normalizedGradientMagnitude, j, i, x, y)
                                    * wd(normalizedTangentVectors[i][j], normalizedTangentVectors[y][x]);

                                summedVectorX[i][j] += normalizedTangentVectors[y][x].getX() * magnitude;
                                summedVectorY[i][j] += normalizedTangentVectors[y][x].getY() * magnitude;
                            }

                        }
                    }

                    // normalize the etf vector
                    double vectorMagnitude =
                        Math.sqrt(summedVectorX[i][j] * summedVectorX[i][j] + summedVectorY[i][j] * summedVectorY[i][j]);
                    if (vectorMagnitude != 0) {
                        summedVectorX[i][j] /= vectorMagnitude;
                        summedVectorY[i][j] /= vectorMagnitude;
                    }
                }
            }

            // replace normalizedTangentVectorField with etfVectorField for next iteration
            for (int ii = 0; ii < tangentVectorField.length; ii++) {
                for (int jj = 0; jj < tangentVectorField[0].length; jj++) {
                    normalizedTangentVectors[ii][jj] = new Point2D.Double(summedVectorX[ii][jj], summedVectorY[ii][jj]);
                }
            }

        }

        logger.info("ETF calculation finished in {} ms.", (System.currentTimeMillis() - etfStartTime));
        return normalizedTangentVectors;
    }

    /**
     * Calculates "fi" function from ETF construction, which is actualy a dot product between two vectors.
     *
     * @param x the center pixel .
     * @param y the kernel pixel.
     * @return value of "fi" function.
     */
    public int fi(Point2D.Double x, Point2D.Double y) {
        double dotProduct = x.getX() * y.getX() + x.getY() * y.getY();

        if (dotProduct > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Calculate "ws" function from ETF construction.
     * Ws is a spatial weight function, which is basically a box filter with the given kernel radius.
     *
     * @param centerX the x coordinate of the center pixel
     * @param centerY the y coordinate of the center pixel
     * @param kernelX the x coordinate of the kernel pixel
     * @param kernelY the y coordinate of the kernel pixel
     * @param kernelRadius the radius of the ETF kernel
     * @return value of "ws" function
     */
    public int ws(int centerX, int centerY, int kernelX, int kernelY, int kernelRadius) {
        double distance = Math.sqrt((kernelX - centerX) * (kernelX - centerX) + (kernelY - centerY) * (kernelY - centerY));

        if (distance < kernelRadius) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Calculate "wd" function from ETF construction.
     * This calculation is not exactly as in the document. The reason is
     * that using absolute value for this value multiplied by the fi
     * function is the same as only using this value as is (without abs.).
     * This cuts about 10% of the time for ETF calculation.
     *
     * @param x the center pixel
     * @param y the kernel pixel
     * @return value of "wd" function
     */
    public double wd(Point2D.Double x, Point2D.Double y) {
       return x.getX() * y.getX() + x.getY() * y.getY();
    }

    /**
     * Calculate "wm" function from ETF construction.
     * Wm is the magnitude weight function.
     *
     * @param gradientField normalized gradient values
     * @param centerX the x coordinate of the center pixel
     * @param centerY the y coordinate of the center pixel
     * @param kernelX the x coordinate of the kernel pixel
     * @param kernelY the y coordinate of the kernel pixel
     * @return value of "wm" function.
     */
    public double wm(double[][] gradientField, int centerX, int centerY, int kernelX, int kernelY) {
        return (gradientField[kernelY][kernelX] - gradientField[centerY][centerX] + 1) / 2.0D;
    }
}
