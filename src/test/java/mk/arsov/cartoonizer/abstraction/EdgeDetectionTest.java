package mk.arsov.cartoonizer.abstraction;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import mk.arsov.cartoonizer.lic.LineConvolutionCalculator;
import mk.arsov.cartoonizer.lic.SobelGradient;
import mk.arsov.cartoonizer.util.ImageUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for EdgeDetection class.
 */
public class EdgeDetectionTest {

    private SobelGradient sobelGradient;
    private LineConvolutionCalculator lineConvolutionCalculator;
    private EdgeTangentFlow edgeTangentFlow;
    private EdgeDetection edgeDetection;

    @Before
    public void setUp() {
        this.sobelGradient = new SobelGradient();
        this.lineConvolutionCalculator = new LineConvolutionCalculator();
        this.edgeTangentFlow = new EdgeTangentFlow();
        this.edgeDetection = new EdgeDetection(lineConvolutionCalculator, edgeTangentFlow, sobelGradient);
    }

    /**
     * Test the calculateHexIntegral value.
     */
    @Test
    @Ignore
    public void testCalculateHexIntegral() {

    }

    /**
     * Test the calculation of Hg(x) values with a small image.
     */
    @Test
    public void testCalculateHgxIntegral() {
        PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/slika.bmp");
        BufferedImage grayscaleImage = ImageUtils.toGrayscale(sourceImage.getAsBufferedImage());

        Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(grayscaleImage);

        // normalize tangent vectors
        for (int i = 0; i < tangentVectors.length; i++) {
            for (int j = 0; j < tangentVectors[0].length; j++) {
                Point2D.Double vector = tangentVectors[i][j];
                double length = Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY());

                tangentVectors[i][j].setLocation((vector.getX() == 0 ? 0 : vector.getX() / length),
                        (vector.getY() == 0 ? 0 : vector.getY() / length));
            }
        }

        double[][] integralValues = edgeDetection.calculateHgxIntegral(sourceImage.getAsBufferedImage(), tangentVectors, 1, 1.6, 0.997, 4);

        BufferedImage image =
                new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < sourceImage.getHeight(); i++) {
            for (int j = 0; j < sourceImage.getWidth(); j++) {
                if (integralValues[i][j] >= -1) {
                    image.setRGB(j, i, 16777215);
                } else {
                    image.setRGB(j, i, 0);
                }
            }
        }

        ImageUtils.saveImage(image, "target/vlatko_edge.png");
    }

    /**
     * Test calculate() method.
     * */
    @Test
    public void testCalculate() {
        PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/cone.bmp");
        BufferedImage grayscaleImage = ImageUtils.toGrayscale(sourceImage.getAsBufferedImage());

        BufferedImage edgeImage = edgeDetection.calculate(grayscaleImage, 1, 3, 1, 1.6, 0.997, 4, 0.5,
                9, 1, 10, 1.1, 3, 5);

//        ParameterBlock pb = new ParameterBlock();
//        pb.addSource(sourceImage);
//        pb.addSource(edgeImage);
//
//        PlanarImage combinedImage = JAI.create("and", pb);
        ImageUtils.saveImage(edgeImage, "target/cone_edge_1_3_1_997_0.5.png");
    }
}
