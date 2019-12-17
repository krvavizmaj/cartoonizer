package mk.arsov.cartoonizer.abstraction;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import mk.arsov.cartoonizer.lic.LineConvolutionCalculator;
import mk.arsov.cartoonizer.lic.SobelGradient;
import mk.arsov.cartoonizer.util.ImageUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for EdgeTangentFlow class.
 */
public class EdgeTangentFlowTest {

    private SobelGradient sobelGradient;
    private LineConvolutionCalculator lineConvolutionCalculator;
    private EdgeTangentFlow edgeTangentFlow;

    @Before
    public void setUp() {
        this.sobelGradient = new SobelGradient();
        this.lineConvolutionCalculator = new LineConvolutionCalculator();
        this.edgeTangentFlow = new EdgeTangentFlow();
    }

    /**
     * Test method for {@link EdgeTangentFlow#calculate(Point2D.Double[][], int, int)}.
     */
    @Test
    public void testCalculate() {
        PlanarImage image = JAI.create("fileload", "src/test/resources/images/slika.bmp");
        BufferedImage bluredImage = ImageUtils.blur(image.getAsBufferedImage(), 9, 1);
        PlanarImage whiteNoiseImage = JAI.create("fileload", "src/test/resources/images/whitenoise.bmp");

        Point2D.Double[][] vectorField = sobelGradient.calculateTangentVectorField(bluredImage);
        vectorField = edgeTangentFlow.calculate(vectorField, 1, 5);

        // display on white noise image
        BufferedImage resultImage =
                lineConvolutionCalculator.calculate(vectorField, whiteNoiseImage.getAsBufferedImage(), 21, 1.1);
        ImageUtils.saveImage(resultImage, "target/slika_etf_1_5_blur.png");
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

