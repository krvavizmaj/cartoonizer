package mk.arsov.cartoonizer.abstraction;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;

import javax.annotation.Resource;

import mk.arsov.cartoonizer.lic.LineConvolutionCalculator;
import mk.arsov.cartoonizer.lic.SobelGradient;
import mk.arsov.cartoonizer.util.FlowUtils;
import mk.arsov.cartoonizer.util.ImageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Edge detection calculation based on flow decision of Gausians.
 */
@Repository
public class EdgeDetection {

  /** Logger. */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /** SobelGradient. */
  @Resource
  private SobelGradient sobelGradient;

  /** EdgeTangentFlow. */
  @Resource
  private EdgeTangentFlow edgeTangentFlow;

  /** LineConvolutionCalculator. */
  @Resource
  private LineConvolutionCalculator lineConvolutionCalculator;

  /** ImageUtils. */
  @Resource
  private ImageUtils imageUtils;
  
  /** FlowUtils. */
  @Resource
  private FlowUtils flowUtils; 

  /**
   * Number of iterations. For each iteration, the previously calculated edge
   * image is superimposed to the original image and the process is run again,
   * the etf remains the same.
   */
  @Value("${fdog.iterations}")
  private int iterations;

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
  private int lineSegmentsLength;

  /** Threshold level for the final edge detection decision. */
  @Value("${fdog.tau}")
  private double tau;

  /**
   * DefaultConstructor.
   */
  public EdgeDetection() {

  }

  /**
   * Calculate edge pixels.
   * 
   * @param sourceImage the image on which edge detection is applied
   * @return image showing only the edges from the source image
   */
  public BufferedImage calculate(BufferedImage sourceImage) {
    logger.info("Running edge detection");
    BufferedImage blurredImage = imageUtils.blur(sourceImage);
    BufferedImage edgeImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(),
        BufferedImage.TYPE_3BYTE_BGR);

    final Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(blurredImage);
    final Point2D.Double[][] etfVectors = edgeTangentFlow.calculate(tangentVectors);

    for (int iteration = 0; iteration < iterations; iteration++) {
      logger.info("Edge detection iteration {}/{}", iteration, iterations);

      final double[][] immediateIntegralValues = calculateHgxIntegral(sourceImage, etfVectors);
      final double[][] integralValues = calculateHexIntegral(etfVectors, immediateIntegralValues);

      for (int i = 0; i < integralValues.length; i++) {
        for (int j = 0; j < integralValues[0].length; j++) {
          final int edgeValue = thresholdedValue(integralValues[i][j]);
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
      sourceImage = imageUtils.blur(sourceImage);
    }

    return edgeImage;
  }

  /**
   * Calculate Hg(x) integral for each point in the image.
   * 
   * @param sourceImage source image
   * @param tangentVectors array of tangent vectors for each point in the image
   * @return array with values of integral Hg(x) for each point in the image
   */
  protected double[][] calculateHgxIntegral(BufferedImage sourceImage,
      Point2D.Double[][] tangentVectors) {
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
        points = flowUtils.calculateGradientPoints(x, gradientVector, sourceImage.getWidth(),
            sourceImage.getHeight(), t);

        integralValue = 0;
        for (int k = 0; k < points.size(); k++) {
          final Point2D.Double point = points.get(k);
          final int xCoord = (int) point.getX();
          final int yCoord = (int) point.getY();

          int parameter = k % 2 == 0 ? -((k + 1) / 2) : ((k + 1) / 2);
          integralValue += raster.getSample(xCoord, yCoord, 0)
              * flowUtils.calculateDog(parameter, sigmaC, sigmaS, ro);
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
   * @param immediateIntegralValues the matrix with the immediate integral
   * values for each point
   * @return values of the final integral for each point in the image
   */
  protected double[][] calculateHexIntegral(Point2D.Double[][] etfVectors,
      double[][] immediateIntegralValues) {
    lineConvolutionCalculator.setKernelLength(lineSegmentsLength);

    // final values
    double[][] integralValues = new double[etfVectors.length][etfVectors[0].length];

    double integralValue = 0;
    for (int i = 0; i < etfVectors.length; i++) {
      for (int j = 0; j < etfVectors[0].length; j++) {
        final Point2D.Double centerPoint = new Point2D.Double(j, i);
        ArrayList<Point2D.Double> lineSegmentPoints = lineConvolutionCalculator.getLineSegmentPoints(
            etfVectors, centerPoint);

        // calculate the final integral
        integralValue = 0;

        for (int k = 0; k < lineSegmentPoints.size(); k++) {
          final Point2D.Double point = lineSegmentPoints.get(k);

          // the index of the point in the line segment (-S, S)
          int parameter = k % 2 == 0 ? -((k + 1) / 2) : ((k + 1) / 2);

          integralValue += flowUtils.calculateGausian(parameter, sigmaM)
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
   * @return thresholded value x
   */
  protected int thresholdedValue(double x) {
    if ((x < 0) && (Math.tanh(x) + 1 < tau)) {
      return 0;
    } else {
      return 1;
    }
  }

  /**
   * Gets sigmaC.
   * 
   * @return the sigmaC
   */
  public double getSigmaC() {
    return sigmaC;
  }

  /**
   * Sets sigmaC.
   * 
   * @param sigmaC the sigmaC to set
   */
  public void setSigmaC(double sigmaC) {
    this.sigmaC = sigmaC;
  }

  /**
   * Gets sigmaS.
   * 
   * @return the sigmaS
   */
  public double getSigmaS() {
    return sigmaS;
  }

  /**
   * Sets sigmaS.
   * 
   * @param sigmaS the sigmaS to set
   */
  public void setSigmaS(double sigmaS) {
    this.sigmaS = sigmaS;
  }

  /**
   * Gets sigmaM.
   * 
   * @return the sigmaM
   */
  public double getSigmaM() {
    return sigmaM;
  }

  /**
   * Sets sigmaM.
   * 
   * @param sigmaM the sigmaM to set
   */
  public void setSigmaM(double sigmaM) {
    this.sigmaM = sigmaM;
  }

  /**
   * Gets ro.
   * 
   * @return the ro
   */
  public double getRo() {
    return ro;
  }

  /**
   * Sets ro.
   * 
   * @param ro the ro to set
   */
  public void setRo(double ro) {
    this.ro = ro;
  }

  /**
   * Gets t.
   * 
   * @return the t
   */
  public int getT() {
    return t;
  }

  /**
   * Sets t.
   * 
   * @param t the t to set
   */
  public void setT(int t) {
    this.t = t;
  }

  /**
   * Gets tau.
   * 
   * @return the tau
   */
  public double getTau() {
    return tau;
  }

  /**
   * Sets tau.
   * 
   * @param tau the tau to set
   */
  public void setTau(double tau) {
    this.tau = tau;
  }
}
