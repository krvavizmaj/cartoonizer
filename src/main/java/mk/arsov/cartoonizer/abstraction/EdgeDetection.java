package mk.arsov.cartoonizer.abstraction;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;

import javax.inject.Inject;

import mk.arsov.cartoonizer.lic.LineConvolutionCalculator;
import mk.arsov.cartoonizer.lic.SobelGradient;
import mk.arsov.cartoonizer.util.FlowUtils;
import mk.arsov.cartoonizer.util.ImageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Edge detection calculation based on flow decision of Gausians.
 */
public class EdgeDetection {

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SobelGradient sobelGradient;
    private final EdgeTangentFlow edgeTangentFlow;
    private final LineConvolutionCalculator lineConvolutionCalculator;

    @Inject
    public EdgeDetection(final LineConvolutionCalculator lineConvolutionCalculator, final EdgeTangentFlow edgeTangentFlow,
            final SobelGradient sobelGradient) {
        this.lineConvolutionCalculator = lineConvolutionCalculator;
        this.edgeTangentFlow = edgeTangentFlow;
        this.sobelGradient = sobelGradient;
    }

    /**
     * Calculate edge pixels.
     *
     * @param sourceImage the image on which edge detection is applied
     * @return image showing only the edges from the source image
     */
    public BufferedImage calculate(BufferedImage sourceImage,
            final int iterations, final int blurKernelRadius, final double blurSigma,
            final double tau, final int etfIterations, final int etfKernelRadius,
            final double sigmaC, final double sigmaS, final double ro,
            final double sigmaM, final int licKernelLength, final double licRoundoff, final int t) {

        logger.info("Running edge detection");
        BufferedImage blurredImage = ImageUtils.blur(sourceImage, blurKernelRadius, blurSigma);
        BufferedImage edgeImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(),
            BufferedImage.TYPE_3BYTE_BGR);

        final Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(blurredImage);
        final Point2D.Double[][] etfVectors = edgeTangentFlow.calculate(tangentVectors, etfIterations, etfKernelRadius);

        for (int iteration = 0; iteration < iterations; iteration++) {
            logger.info("Edge detection iteration {}/{}", iteration, iterations);

            final double[][] immediateIntegralValues = this.calculateHgxIntegral(sourceImage, etfVectors, sigmaC, sigmaS, ro, t);
            final double[][] integralValues = this.calculateHexIntegral(etfVectors, immediateIntegralValues, sigmaM, licKernelLength, licRoundoff);

            for (int i = 0; i < integralValues.length; i++) {
                for (int j = 0; j < integralValues[0].length; j++) {
                    final int edgeValue = this.thresholdedValue(integralValues[i][j], tau);
                    edgeImage.setRGB(j, i, edgeValue * 16777215);
                }
            }

            // combine images
            final Raster edgeRaster = edgeImage.getData();
            for (int i = 0; i < edgeImage.getHeight(); i++) {
                for (int j = 0; j < edgeImage.getWidth(); j++) {
                    if (edgeRaster.getSample(j, i, 0) == 0) {
                        sourceImage.setRGB(j, i, 0);
                    }
                }
            }

            // blur image
            sourceImage = ImageUtils.blur(sourceImage, blurKernelRadius, blurSigma);
        }

        return edgeImage;
    }

    /**
     * Calculate Hg(x) integral for each point in the image.
     *
     * @param sourceImage source image
     * @param tangentVectors array of tangent vectors for each point in the image
     * @param sigmaC
     * @param sigmaS
     * @param ro
     * @return array with values of integral Hg(x) for each point in the image
     */
    protected double[][] calculateHgxIntegral(final BufferedImage sourceImage,
            final Point2D.Double[][] tangentVectors, final double sigmaC, final double sigmaS, final double ro, final int t) {
        logger.info("Starting calculation of Hg(x) values for image with size {}, {}",
            sourceImage.getWidth(), sourceImage.getHeight());
        double[][] result = new double[sourceImage.getHeight()][sourceImage.getWidth()];
        ArrayList<Point2D.Double> points;

        double integralValue = 0;

        Raster raster = sourceImage.getData();
        for (int i = 0; i < sourceImage.getHeight(); i++) {
            for (int j = 0; j < sourceImage.getWidth(); j++) {
                final Point2D.Double x = new Point2D.Double(j, i);
                final Point2D.Double gradientVector = new Point2D.Double(tangentVectors[i][j].getY(),
                    -tangentVectors[i][j].getX());
                points = FlowUtils.calculateGradientPoints(x, gradientVector, sourceImage.getWidth(),
                    sourceImage.getHeight(), t);

                integralValue = 0;
                for (int k = 0; k < points.size(); k++) {
                    final Point2D.Double point = points.get(k);
                    final int xCoord = (int) point.getX();
                    final int yCoord = (int) point.getY();

                    int parameter = k % 2 == 0 ? -((k + 1) / 2) : ((k + 1) / 2);
                    integralValue += raster.getSample(xCoord, yCoord, 0)
                        * FlowUtils.calculateDog(parameter, sigmaC, sigmaS, ro);
                }

                result[i][j] = integralValue;
            }
        }

        return result;
    }

    /**
     * Calculate He(x) integral from edge detection for the entire image.
     *
     * @param etfVectors etf vectors for each point in the image
     * @param immediateIntegralValues the matrix with the immediate integral values for each point
     * @param sigmaM
     * @param licKernelLength
     * @param licRoundoff
     * @return values of the final integral for each point in the image
     */
    protected double[][] calculateHexIntegral(final Point2D.Double[][] etfVectors,
            final double[][] immediateIntegralValues, final double sigmaM,
            final int licKernelLength, final double licRoundoff) {

        // final values
        double[][] integralValues = new double[etfVectors.length][etfVectors[0].length];

        double integralValue = 0;
        for (int i = 0; i < etfVectors.length; i++) {
            for (int j = 0; j < etfVectors[0].length; j++) {
                final Point2D.Double centerPoint = new Point2D.Double(j, i);
                ArrayList<Point2D.Double> lineSegmentPoints = lineConvolutionCalculator.getLineSegmentPoints(
                    etfVectors, centerPoint, licKernelLength, licRoundoff);

                // calculate the final integral
                integralValue = 0;

                for (int k = 0; k < lineSegmentPoints.size(); k++) {
                    final Point2D.Double point = lineSegmentPoints.get(k);

                    // the index of the point in the line segment (-S, S)
                    int parameter = k % 2 == 0 ? -((k + 1) / 2) : ((k + 1) / 2);

                    integralValue += FlowUtils.calculateGausian(parameter, sigmaM)
                        * immediateIntegralValues[(int) point.getY()][(int) point.getX()];
                }

                integralValues[i][j] = integralValue;
            }
        }

        return integralValues;
    }

    /**
     * Calculate thresholded value of the a pixel output value.
     *
     * @param x the value to be thresholded
     * @param tau
     * @return thresholded value x
     */
    protected int thresholdedValue(final double x, final double tau) {
        if ((x < 0) && (Math.tanh(x) + 1 < tau)) {
            return 0;
        } else {
            return 1;
        }
    }
}
