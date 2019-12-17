package mk.arsov.cartoonizer.lic;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Tests for SobelGrdient class.
 */
public class SobelGradientTest {

    private SobelGradient sobelGradient;

    public SobelGradientTest() {

    }

    @Before
    public void setUp() {
        this.sobelGradient = new SobelGradient();
    }

    /**
     * Test calculateGradientX() method.
     */
    @Test
    public void testCalculateGradientX() {
        int[][] imageData = {
            {0x44, 0x7b, 0x77, 0x67, 0x54},
            {0x41, 0x48, 0x83, 0x74, 0x63},
            {0x07, 0x21, 0x3f, 0x82, 0x77},
            {0x0f, 0x0b, 0x19, 0x45, 0x7c},
            {0x04, 0x0c, 0x12, 0x0b, 0x35},
        };

        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                image.setRGB(j, i, imageData[i][j] * 256 * 256 + imageData[i][j] * 256 + imageData[i][j]);
            }
        }

        double[][] gradientX = sobelGradient.calculateGradientX(image.getData());

        Assert.assertEquals(-33, gradientX[2][2], 0.00005);
        Assert.assertEquals(-35, gradientX[0][0], 0.00005);
        Assert.assertEquals(-6, gradientX[4][2], 0.00005);
    }

    /**
     * Test calculateGradientY() method.
     */
    @Test
    public void testCalculateGradientY() {
        int[][] imageData = {
            {0x44, 0x7b, 0x77, 0x67, 0x54},
            {0x41, 0x48, 0x83, 0x74, 0x63},
            {0x07, 0x21, 0x3f, 0x82, 0x77},
            {0x0f, 0x0b, 0x19, 0x45, 0x7c},
            {0x04, 0x0c, 0x12, 0x0b, 0x35},
        };

        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                image.setRGB(j, i, imageData[i][j] * 256 * 256 + imageData[i][j] * 256 + imageData[i][j]);
            }
        }

        double[][] gradientY = sobelGradient.calculateGradientY(image.getData());

        Assert.assertEquals(-36, gradientY[2][2], 0.00005);
        Assert.assertEquals(22, gradientY[0][0], 0.00005);
        Assert.assertEquals(-14, gradientY[4][2], 0.00005);
    }

    /**
     * Test calculateTangentVectorField.
     */
    @Test
    public void testCalculateTangentVectorField() {
        int[][] imageData = {
            {0x44, 0x7b, 0x77, 0x67, 0x54},
            {0x41, 0x48, 0x83, 0x74, 0x63},
            {0x07, 0x21, 0x3f, 0x82, 0x77},
            {0x0f, 0x0b, 0x19, 0x45, 0x7c},
            {0x04, 0x0c, 0x12, 0x0b, 0x35},
        };

        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                image.setRGB(j, i, imageData[i][j] * 256 * 256 + imageData[i][j] * 256 + imageData[i][j]);
            }
        }

        Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(image);

        Point2D.Double point = tangentVectors[2][2];
        Assert.assertEquals(-36, point.getX(), 0.05);
        Assert.assertEquals(-33, point.getY(), 0.05);

        point = tangentVectors[0][0];
        Assert.assertEquals(22, point.getX(), 0.05);
        Assert.assertEquals(-35, point.getY(), 0.05);

        point = tangentVectors[4][2];
        Assert.assertEquals(-14, point.getX(), 0.05);
        Assert.assertEquals(-6, point.getY(), 0.05);
    }

    /**
     * Test calculateTangentVectorField, read from image file and write vectors to file.
     * @throws FileNotFoundException
     */
    @Test
    @Ignore
    public void testCalculateTangentVectorFieldToFile() throws FileNotFoundException {

        // load image
        ClassPathResource testImageFileName = new ClassPathResource("slika1.bmp");
        PlanarImage planarImage = JAI.create("fileload", testImageFileName.getFilename());
    
        // calculate tangent vectors
        Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(planarImage.getAsBufferedImage());
    
        // write to file
        PrintWriter out = new PrintWriter("tangent_field.txt");
        for (int i = 0; i < tangentVectors.length; i++) {
            for (int j = 0; j < tangentVectors[0].length; j++) {
                out.print("(" + tangentVectors[i][j].getX() + ", " + tangentVectors[i][j].getY() + ");");
            }
            out.println();
        }
    
        out.flush();
        out.close();
    }
}
