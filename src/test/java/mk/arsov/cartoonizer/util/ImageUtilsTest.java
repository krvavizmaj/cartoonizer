package mk.arsov.cartoonizer.util;

import java.awt.image.BufferedImage;

import javax.annotation.Resource;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for ImageUtils class.
 */
public class ImageUtilsTest {

  /** ImageUtils. */
  @Resource
  private ImageUtils imageUtils;
  
  /**
   * Test toGrayscale method.
   */
  @Test
  public void testToGrayscale() {
    PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/vlatko.jpg");
    
    BufferedImage grayscaleImage = imageUtils.toGrayscale(sourceImage.getAsBufferedImage());
    imageUtils.saveImage(grayscaleImage, "target/vlatko_gray.png");
    
  }
  
  /**
   * Test the blur() method.
   */
  @Test
  public void testBlur() {
    PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/vlatko_big.jpg");
    BufferedImage grayscale = imageUtils.toGrayscale(sourceImage.getAsBufferedImage());
    
    BufferedImage blurredImage = imageUtils.blur(grayscale);
    imageUtils.saveImage(blurredImage, "target/vlatko_big_blured_1.png");
  }

  /**
   * Test the rgbToLab() method.
   */
  @Test
  public void testRgbToLab() {
    double[] rgb = {223, 39, 39};
    double[] lab1 = imageUtils.rgbToLab(rgb);
    System.out.println("x = {" + lab1[0] + ", " + lab1[1] + ", " + lab1[2] + "};");
    
    rgb[0] = 0xcc;
    rgb[1] = 0x11;
    rgb[2] = 0x3d;
    double[] lab2 = imageUtils.rgbToLab(rgb);
    System.out.println("y = {" + lab2[0] + ", " + lab2[1] + ", " + lab2[2] + "};");
    
  }
  
}
