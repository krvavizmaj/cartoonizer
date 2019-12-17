package mk.arsov.cartoonizer.util;

import java.awt.image.BufferedImage;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for ImageUtils class.
 */
public class ImageUtilsTest {

    /**
     * Test toGrayscale method.
     */
    @Test
    public void testToGrayscale() {
        PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/vlatko.jpg");

        BufferedImage grayscaleImage = ImageUtils.toGrayscale(sourceImage.getAsBufferedImage());
        ImageUtils.saveImage(grayscaleImage, "target/vlatko_gray.png");
    }

    /**
     * Test the blur() method.
     */
    @Test
    public void testBlur() {
        PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/vlatko_big.jpg");
        BufferedImage grayscale = ImageUtils.toGrayscale(sourceImage.getAsBufferedImage());

        BufferedImage blurredImage = ImageUtils.blur(grayscale, 9, 1);
        ImageUtils.saveImage(blurredImage, "target/vlatko_big_blured_1.png");
    }

    /**
     * Test the rgbToLab() method.
     */
    @Test
    public void testRgbToLab() {
        double[] rgb = {223, 39, 39};
        double[] lab1 = ImageUtils.rgbToLab(rgb);
        Assert.assertEquals(48.61776672052031, lab1[0], 0);
        Assert.assertEquals(67.57420018036825, lab1[1], 0);
        Assert.assertEquals(47.251546076395954, lab1[2], 0);

        rgb[0] = 0xcc;
        rgb[1] = 0x11;
        rgb[2] = 0x3d;
        double[] lab2 = ImageUtils.rgbToLab(rgb);
        Assert.assertEquals(43.61743426896632, lab2[0], 0);
        Assert.assertEquals(67.37251404455225, lab2[1], 0);
        Assert.assertEquals(28.112959746331732, lab2[2], 0);
    }
}
