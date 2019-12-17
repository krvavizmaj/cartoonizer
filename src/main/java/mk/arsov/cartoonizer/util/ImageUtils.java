package mk.arsov.cartoonizer.util;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some image manipulation methods.
 */
public class ImageUtils {
  
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    private ImageUtils() {

    }

    /**
     * Create circular fluid dynamics vector field.
     *
     * @param width the width of the vector field
     * @param height the height of the vector field
     * @return array containing vector for each pixel
     */
    public static Point2D.Double[][] createCircularVectorField(final int width, final int height) {

        Point2D.Double[][] vectorField = new Point2D.Double[height][width];

        int centerRow = height / 2;
        int centerCol = width / 2;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final double x = j - centerCol;
                final double y = -(i - centerRow);

                final double tangentMagnitude = Math.sqrt(x * x + y * y);
                vectorField[i][j] = new Point2D.Double(-y / tangentMagnitude, x / tangentMagnitude);
            }
        }

        return vectorField;
    }

    /**
     * Converts a colored BufferedImage into a grayscale BufferedImage.
     *
     * @param source the source image.
     * @return grayscaled image of the original image
     */
    public static BufferedImage toGrayscale(final BufferedImage source) {

        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        if (source.getColorModel().getNumComponents() < 3) {
            return source;
        }

        Raster raster = source.getData();
        for (int i = 0; i < source.getHeight(); i++) {
            for (int j = 0; j < source.getWidth(); j++) {
                final int value = (int) Math.round(raster.getSample(j, i, 0) * 0.3
                    + raster.getSample(j, i, 1) * 0.59 + raster.getSample(j, i, 2) * 0.11);
                result.setRGB(j, i, value * 256 * 256 + value * 256 + value);
            }
        }

        return result;
    }

    /**
     * Gaussian blur the source image.
     *
     * @param sourceImage the image to be blured
     * @param blurKernelRadius radius for the blur operation
     * @param blurSigma
     * @return blurred image
     */
    public static BufferedImage blur(final BufferedImage sourceImage, final int blurKernelRadius, final double blurSigma) {

        logger.info("Calculating blurred image from the input image");
        BufferedImage blurredImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        // calculate kernel
        int kernelSize = blurKernelRadius * 2 + 1;
        double[][] kernel = new double[kernelSize][kernelSize];
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                final int x = j - blurKernelRadius;
                final int y = i - blurKernelRadius;

                kernel[i][j] = 1 / (2 * Math.PI * blurSigma * blurSigma) * Math.pow(Math.E, -(x * x + y * y) / (2 * blurSigma * blurSigma));
            }
        }

        // calculate the blurred image
        Raster raster = sourceImage.getData();
        for (int i = 0; i < sourceImage.getHeight(); i++) {
            for (int j = 0; j < sourceImage.getWidth(); j++) {
                double sum = 0;
                int points = 0;

                for (int y = i - blurKernelRadius; y <= i + blurKernelRadius; y++) {
                    for (int x = j - blurKernelRadius; x <= j + blurKernelRadius; x++) {
                        if ((x >= 0) && (y >= 0) && (x < sourceImage.getWidth()) && (y < sourceImage.getHeight())) {
                            sum += raster.getSample(x, y, 0) * kernel[y - i + blurKernelRadius][x - j + blurKernelRadius];
                            points++;
                        }
                    }
                }

                final int value = (int) (sum);
                blurredImage.setRGB(j, i, value * 256 * 256 + value * 256 + value);
            }
        }

        return blurredImage;
    }

    /**
     * Transform and rgb value into CIEL*ab space.
     *
     * @param rgb the rgb value
     * @return array containing the L*ab values
     */
    public static double[] rgbToLab(final double[] rgb) {

        double[] xyz = rgbToXYZ(rgb);
        double[] lab = xyzToLab(xyz);

        return lab;
    }

    /**
     * Saves the given image on disk with the given file name.
     *
     * @param image the image to be saved to disk
     * @param fileName the name of the image file
     */
    public static void saveImage(final BufferedImage image, final String fileName) {
        logger.info("Saving image {}", fileName);
        try {
            ImageIO.write(image, "PNG", new File(fileName));
        } catch (IOException e) {
            logger.info("Cannot save image to file {}", fileName);
        }
    }

    /**
     * Transform an rgb value into CIE XYZ space.
     *
     * @param rgb the rgb value
     * @return an array with the XYZ values
     */
    protected static double[] rgbToXYZ(final double[] rgb) {
        double newR = rgb[0] / 255.0D;
        double newG = rgb[1] / 255.0D;
        double newB = rgb[2] / 255.0D;

        if (newR > 0.04045) {
            newR = Math.pow(((newR + 0.055) / 1.055), 2.4);
        } else {
            newR = newR / 12.92;
        }

        if (newG > 0.04045) {
            newG = Math.pow(((newG + 0.055) / 1.055), 2.4);
        } else {
            newG = newG / 12.92;
        }

        if (newB > 0.04045) {
            newB = Math.pow(((newB + 0.055) / 1.055), 2.4);
        } else {
            newB = newB / 12.92;
        }

        newR = newR * 100;
        newG = newG * 100;
        newB = newB * 100;

        double[] xyz = new double[3];
        xyz[0] = newR * 0.4124 + newG * 0.3576 + newB * 0.1805;
        xyz[1] = newR * 0.2126 + newG * 0.7152 + newB * 0.0722;
        xyz[2] = newR * 0.0193 + newG * 0.1192 + newB * 0.9505;

        return xyz;
    }

    /**
     * Transform an xyz value into CIEL*ab space.
     *
     * @param xyz the xyz value
     * @return array representing the Lab values
     */
    protected static double[] xyzToLab(final double[] xyz) {
        double newX = xyz[0] / 95.047;
        double newY = xyz[1] / 100.0;
        double newZ = xyz[2] / 108.883;

        if (newX > 0.008856) {
            newX = Math.pow(newX, (double) (1.0 / 3.0));
        } else {
            newX = (7.787 * newX) + (double) (16.0 / 116.0);
        }
        if (newY > 0.008856) {
            newY = Math.pow(newY, (double) (1.0 / 3.0));
        } else {
            newY = (7.787 * newY) + (double) (16.0 / 116.0);
        }
        if (newZ > 0.008856) {
            newZ = Math.pow(newZ, (double) (1.0 / 3.0));
        } else {
            newZ = (7.787 * newZ) + (double) (16.0 / 116.0);
        }

        double[] lab = new double[3];
        lab[0] = (116 * newY) - 16;
        lab[1] = 500 * (newX - newY);
        lab[2] = 200 * (newY - newZ);

        return lab;
    }
}
