package mk.arsov.cartoonizer.lineintegralconvolution;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import mk.arsov.cartoonizer.util.ImageUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for LineConvolutionCalculator class.
 */
public class LineConvolutionCalculatorTest {

    private SobelGradient sobelGradient;

    private LineConvolutionCalculator lineConvolutionCalculator;

    @Before
    public void setUp() {
        this.sobelGradient = new SobelGradient();
        this.lineConvolutionCalculator = new LineConvolutionCalculator();
    }

    /**
     * Test method for {@link LineConvolutionCalculator#calculate(Point2D.Double[][], BufferedImage, int, double)}.
     *
     * @throws IOException IOException
     */
    @Test 
    public void testCalculate() throws IOException {
        // load image
        PlanarImage planarImage = JAI.create("fileload", "src/test/resources/images/test_lic_grayscale.png");
    
        int kernelLength = 4;
        double roundoff = 1.1;

        Point2D.Double[][] vectorField = sobelGradient.calculateTangentVectorField(planarImage.getAsBufferedImage());
        BufferedImage resultImage = lineConvolutionCalculator.calculate(vectorField, planarImage.getAsBufferedImage(), kernelLength, roundoff);

        Assert.assertEquals(172, ((resultImage.getRGB(3, 5) % 256) + 256) % 256);
    }

    /**
     * Test method for {@link LineConvolutionCalculator#calculate(Point2D.Double[][], BufferedImage, int, double)}.
     *
     * @throws IOException IOException
     */
    @Test
    public void testCalculateCircularField() throws IOException {
        int kernelLength = 8;
        double roundoff = 1.1;

        int rows = 512;
        int cols = 512;
        Point2D.Double[][] vectorField = new Point2D.Double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // normal vector
                double x = j - (cols/2);
                double y = i - (rows/2);
                double m = Math.sqrt(x*x + y*y);
                vectorField[i][j] = new Point2D.Double(-y/m, x/m);
            }
        }
        BufferedImage resultImage = lineConvolutionCalculator.calculateOnWhiteNoise(vectorField, kernelLength, roundoff);
        ImageUtils.saveImage(resultImage, "target/circular_field_lic.png");
    }

    /**
     * Test getLineSegmentPoints.
     *
     * @throws IOException IOException
     */
    @Test
    public void testGetLineSegmentPoints() throws IOException {
        // load image
        PlanarImage planarImage = JAI.create("fileload", "src/test/resources/images/test_lic_grayscale.png");

        // calculate tangent vectors
        Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(planarImage.getAsBufferedImage());

        ArrayList<Point2D.Double> pointsInLine = lineConvolutionCalculator.getLineSegmentPoints(tangentVectors, new Point2D.Double(3, 5), 4, 1.1D);

        Assert.assertEquals(9, pointsInLine.size());
        Point2D.Double point = new Point2D.Double(3, 5);
        Assert.assertEquals(point, pointsInLine.get(0));
        point = new Point2D.Double(2, 5);
        Assert.assertEquals(point, pointsInLine.get(1));
        point = new Point2D.Double(4, 5);
        Assert.assertEquals(point, pointsInLine.get(2));
        point = new Point2D.Double(2, 6);
        Assert.assertEquals(point, pointsInLine.get(3));
        point = new Point2D.Double(5, 6);
        Assert.assertEquals(point, pointsInLine.get(4));
        point = new Point2D.Double(1, 6);
        Assert.assertEquals(point, pointsInLine.get(5));
        point = new Point2D.Double(5, 7);
        Assert.assertEquals(point, pointsInLine.get(6));
        point = new Point2D.Double(0, 6);
        Assert.assertEquals(point, pointsInLine.get(7));
        point = new Point2D.Double(4, 7);
        Assert.assertEquals(point, pointsInLine.get(8));

        pointsInLine = lineConvolutionCalculator.getLineSegmentPoints(tangentVectors, new Point2D.Double(3, 3), 4, 1.1D);

        Assert.assertEquals(9, pointsInLine.size());
        point = new Point2D.Double(3, 3);
        Assert.assertEquals(point, pointsInLine.get(0));
        point = new Point2D.Double(2, 3);
        Assert.assertEquals(point, pointsInLine.get(1));
        point = new Point2D.Double(4, 3);
        Assert.assertEquals(point, pointsInLine.get(2));
        point = new Point2D.Double(2, 4);
        Assert.assertEquals(point, pointsInLine.get(3));
        point = new Point2D.Double(5, 3);
        Assert.assertEquals(point, pointsInLine.get(4));
        point = new Point2D.Double(1, 4);
        Assert.assertEquals(point, pointsInLine.get(5));
        point = new Point2D.Double(6, 3);
        Assert.assertEquals(point, pointsInLine.get(6));
        point = new Point2D.Double(1, 5);
        Assert.assertEquals(point, pointsInLine.get(7));
        point = new Point2D.Double(7, 3);
        Assert.assertEquals(point, pointsInLine.get(8));

        pointsInLine = lineConvolutionCalculator.getLineSegmentPoints(tangentVectors, new Point2D.Double(7, 3), 4, 1.1D);

        Assert.assertEquals(9, pointsInLine.size());
        point = new Point2D.Double(7, 3);
        Assert.assertEquals(point, pointsInLine.get(0));
        point = new Point2D.Double(6, 3);
        Assert.assertEquals(point, pointsInLine.get(1));
        point = new Point2D.Double(8, 3);
        Assert.assertEquals(point, pointsInLine.get(2));
        point = new Point2D.Double(5, 3);
        Assert.assertEquals(point, pointsInLine.get(3));
        point = new Point2D.Double(8, 4);
        Assert.assertEquals(point, pointsInLine.get(4));
        point = new Point2D.Double(5, 2);
        Assert.assertEquals(point, pointsInLine.get(5));
        point = new Point2D.Double(8, 5);
        Assert.assertEquals(point, pointsInLine.get(6));
        point = new Point2D.Double(5, 1);
        Assert.assertEquals(point, pointsInLine.get(7));
        point = new Point2D.Double(7, 5);
        Assert.assertEquals(point, pointsInLine.get(8));
    }

    /**
     * Test shortestDistanceToEdge method.
     */
    @Test
    public void testShortestDistanceToEdge() {
        LineConvolutionCalculator lineConvolutionCalculator = new LineConvolutionCalculator();

        Point2D.Double vector = new Point2D.Double(1, 0.3);
        double x = 3.2;
        double y = 3.6;
        Assert.assertEquals(0.8352245, lineConvolutionCalculator.shortestDistanceToCellEdge(vector, x, y), 0.000005);

        vector = new Point2D.Double(0, 1);
        x = 2.6;
        y = 1.3;
        Assert.assertEquals(0.7, lineConvolutionCalculator.shortestDistanceToCellEdge(vector, x, y), 0.00005);

        vector = new Point2D.Double(-0.2, 1);
        x = 4.7;
        y = 2.3;
        Assert.assertEquals(0.713863, lineConvolutionCalculator.shortestDistanceToCellEdge(vector, x, y), 0.00005);

        vector = new Point2D.Double(-1, 0.6);
        x = 0.3;
        y = 0.3;
        Assert.assertEquals(0.349857, lineConvolutionCalculator.shortestDistanceToCellEdge(vector, x, y), 0.00005);

        vector = new Point2D.Double(-1, 0);
        x = 4.8;
        y = 2.4;
        Assert.assertEquals(0.8, lineConvolutionCalculator.shortestDistanceToCellEdge(vector, x, y), 0.00005);

        vector = new Point2D.Double(-1, -0.1);
        x = 4.1;
        y = 3.3;
        Assert.assertEquals(0.100499, lineConvolutionCalculator.shortestDistanceToCellEdge(vector, x, y), 0.00005);

        vector = new Point2D.Double(-0.3, -1);
        x = 3.6;
        y = 2.1;
        Assert.assertEquals(0.104403, lineConvolutionCalculator.shortestDistanceToCellEdge(vector, x, y), 0.00005);

        vector = new Point2D.Double(0, -1);
        x = 3.6;
        y = 2.1;
        Assert.assertEquals(0.1, lineConvolutionCalculator.shortestDistanceToCellEdge(vector, x, y), 0.00005);

        vector = new Point2D.Double(0.2, -1);
        x = 4.4;
        y = 5.8;
        Assert.assertEquals(0.815843, lineConvolutionCalculator.shortestDistanceToCellEdge(vector, x, y), 0.00005);

        vector = new Point2D.Double(1, -0.4);
        x = 2.6;
        y = 3.9;
        Assert.assertEquals(0.430813, lineConvolutionCalculator.shortestDistanceToCellEdge(vector, x, y), 0.00005);

        vector = new Point2D.Double(1, 0);
        x = 3.1;
        y = 2.8;
        Assert.assertEquals(0.9, lineConvolutionCalculator.shortestDistanceToCellEdge(vector, x, y), 0.00005);
    }

}