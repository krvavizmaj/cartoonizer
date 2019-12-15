/**
 * 
 */
package mk.arsov.cartoonizer.abstraction;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.annotation.Resource;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import junit.framework.Assert;
import junit.framework.TestCase;
import mk.arsov.cartoonizer.lic.LineConvolutionCalculator;
import mk.arsov.cartoonizer.lic.SobelGradient;
import mk.arsov.cartoonizer.util.ImageUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for EdgeTangentFlow class.
 */
public class EdgeTangentFlowTest extends TestCase {

  /** ImageUtils. */
  @Resource
  private ImageUtils imageUtils;
  
/** SobelGradient. */
  @Resource
private SobelGradient sobelGradient;

/** EdgeTangentFlow. */
  @Resource
private EdgeTangentFlow edgeTangentFlow;
  
  /** {@link LineConvolutionCalculator}. */
  @Resource
  private LineConvolutionCalculator lineConvolutionCalculator;

  /**
   * Test method for {@link EdgeTangentFlow#calculate(Point2D.Double[][])}.
   */
  @Test
  public void testCalculate() {
    String fileName = "src/test/resources/images/slika.bmp";
    PlanarImage image = JAI.create("fileload", fileName);
    BufferedImage bluredImage = imageUtils.blur(image.getAsBufferedImage());
    PlanarImage whiteNoiseImage = JAI.create("fileload", "src/test/resources/images/whitenoise.bmp");
    
    Point2D.Double[][] vectorField = sobelGradient.calculateTangentVectorField(bluredImage);
    edgeTangentFlow.setNumberOfIterations(1);
    edgeTangentFlow.setEtfKernelRadius(5);
    vectorField = edgeTangentFlow.calculate(vectorField);
    
    // display on white noise image
    lineConvolutionCalculator.setKernelLength(21);
    BufferedImage resultImage = 
      lineConvolutionCalculator.calculate(vectorField, whiteNoiseImage.getAsBufferedImage(), 21, 1.1);
    imageUtils.saveImage(resultImage, "target/slika_etf_1_5_blur.png");
  }
  
  /**
   * Test ETF fi function.
   */
  @Test
  public void testFi() {
    Point2D.Double x = new Point2D.Double(10, 20);
    Point2D.Double y = new Point2D.Double(5, 15);
    Assert.assertEquals(1, edgeTangentFlow.fi(x, y));
    
    y = new Point2D.Double(-5, -1);
    Assert.assertEquals(-1, edgeTangentFlow.fi(x, y));
  }
  
  /**
   * Test ws function.
   */
  @Test
  public void testWs() {
    int centerX = 10;
    int centerY = 0;
    Assert.assertEquals(0, edgeTangentFlow.ws(centerX, centerY, 15, 15, 5));
    
    centerX = 13;
    centerY = 13;
    Assert.assertEquals(1, edgeTangentFlow.ws(centerX, centerY, 15, 15, 5));
  }
  
}

