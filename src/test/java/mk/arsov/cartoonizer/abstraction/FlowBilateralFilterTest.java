package mk.arsov.cartoonizer.abstraction;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import mk.arsov.cartoonizer.lineintegralconvolution.LineConvolutionCalculator;
import mk.arsov.cartoonizer.lineintegralconvolution.SobelGradient;
import mk.arsov.cartoonizer.util.ImageUtils;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for FlowBilateralFilter class.
 */
public class FlowBilateralFilterTest {

    private SobelGradient sobelGradient;
    private LineConvolutionCalculator lineConvolutionCalculator;
    private FlowBilateralFilter flowBilateralFilter;
    private EdgeTangentFlow edgeTangentFlow;

    @Before
    public void setUp() {
        this.sobelGradient = new SobelGradient();
        this.lineConvolutionCalculator = new LineConvolutionCalculator();
        this.flowBilateralFilter = new FlowBilateralFilter(lineConvolutionCalculator);
        this.edgeTangentFlow = new EdgeTangentFlow();
    }

    /**
     * Test calculateCex() method.
     */
    @Test
    public void testCalculateCex() {

        PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/slika.bmp");
        BufferedImage grayscaleImage = ImageUtils.toGrayscale(sourceImage.getAsBufferedImage());
        BufferedImage blurredImage = ImageUtils.blur(grayscaleImage, 9, 1);
        final Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(blurredImage);
        final Point2D.Double[][] etfVectors = edgeTangentFlow.calculate(tangentVectors, 3, 5);

        BufferedImage resultImage = flowBilateralFilter.calculateCex(sourceImage.getAsBufferedImage(), etfVectors, 10, 1.1, 2, 50);

        ImageUtils.saveImage(resultImage, "target/slika_fbl.png");
    }

    /**
     * Test calculateCgx() method.
     */
    @Test
    public void testCalculateCgx() {

        PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/slika.bmp");
        BufferedImage grayscaleImage = ImageUtils.toGrayscale(sourceImage.getAsBufferedImage());
        BufferedImage blurredImage = ImageUtils.blur(grayscaleImage, 9, 1);
        final Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(blurredImage);
        final Point2D.Double[][] etfVectors = edgeTangentFlow.calculate(tangentVectors, 3, 5);

        BufferedImage resultImage = flowBilateralFilter.calculateCgx(sourceImage.getAsBufferedImage(), etfVectors, 4, 2, 10);

        ImageUtils.saveImage(resultImage, "target/slika_fbl.png");
    }

    /**
     * Test full filter.
     */
    @Test
    public void testFlowBilateralFilter() {

        PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/vlatko_big.jpg");
        BufferedImage grayscaleImage = ImageUtils.toGrayscale(sourceImage.getAsBufferedImage());
        BufferedImage blurredImage = ImageUtils.blur(grayscaleImage, 9, 1);
        final Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(blurredImage);
        final Point2D.Double[][] etfVectors = edgeTangentFlow.calculate(tangentVectors, 3, 5);

        BufferedImage resultImage = flowBilateralFilter.calculateCex(sourceImage.getAsBufferedImage(), etfVectors, 10, 1.1, 2, 50);
        resultImage = flowBilateralFilter.calculateCgx(resultImage, etfVectors, 4, 2, 10);

        resultImage = flowBilateralFilter.calculateCex(sourceImage.getAsBufferedImage(), etfVectors, 10, 1.1, 2, 50);
        resultImage = flowBilateralFilter.calculateCgx(resultImage, etfVectors, 4, 2, 10);

        resultImage = flowBilateralFilter.calculateCex(sourceImage.getAsBufferedImage(), etfVectors, 10, 1.1, 2, 50);
        resultImage = flowBilateralFilter.calculateCgx(resultImage, etfVectors, 4, 2, 10);

        ImageUtils.saveImage(resultImage, "target/vlatko_big_fbl.png");
    }
}
