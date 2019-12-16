/*
 * @(#) $CVSHeader:  $
 *
 * Copyright (C) 2011 by Netcetera AG.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of
 * Netcetera AG, Switzerland.  The program(s) may be used and/or copied
 * only with the written permission of Netcetera AG or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the program(s) have been supplied.
 *
 * @(#) $Id: codetemplates.xml,v 1.5 2004/06/29 12:49:49 hagger Exp $
 */
package mk.arsov.cartoonizer.abstraction;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mk.arsov.cartoonizer.lic.LineConvolutionCalculator;
import mk.arsov.cartoonizer.util.FlowUtils;
import mk.arsov.cartoonizer.util.ImageUtils;

/**
 * Class for calculation the flow-based bilateral filter.
 * Kang, Lee, Chui - Flow-Based Image Abstraction, 2009, chapter 4
 */
public class FlowBilateralFilter {
  
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LineConvolutionCalculator lineConvolutionCalculator;

    public FlowBilateralFilter(final LineConvolutionCalculator lineConvolutionCalculator) {
        this.lineConvolutionCalculator = lineConvolutionCalculator;
    }

    /**
     * Calculate the Ce(x) function.
     *
     * @param sourceImage the source image
     * @param etfVectors the edge tangent flow vectors
     * @return the image with the edge bilateral filter applied
     */
    public BufferedImage calculateCex(final BufferedImage sourceImage, final Point2D.Double[][] etfVectors,
          final int licKernelLength, final double licRoundoff, final double sigmaE, final double re) {
        logger.info("Running Ce(x) filter");

        Raster raster = sourceImage.getData();

        // resulting image
        BufferedImage result =
            new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        double tRed = 0;
        double tGreen = 0;
        double tBlue = 0;
        for (int i = 0; i < etfVectors.length; i++) {
            for (int j = 0; j < etfVectors[0].length; j++) {
                tRed = 0;
                tGreen = 0;
                tBlue = 0;

                // rgb values in the center pixel
                final Point2D.Double centerPoint = new Point2D.Double(j, i);
                double[] centerRGBArray = {raster.getSample(j, i, 0), raster.getSample(j, i, 1), raster.getSample(j, i, 2)};

                ArrayList<Point2D.Double> lineSegmentPoints = lineConvolutionCalculator.getLineSegmentPoints(
                    etfVectors, centerPoint, licKernelLength, licRoundoff);

                double totalWeight = 0;
                for (int k = 0; k < lineSegmentPoints.size(); k++) {
                    final Point2D.Double point = lineSegmentPoints.get(k);
                    double[] otherRGBArray = {raster.getSample((int) point.getX(), (int) point.getY(), 0),
                        raster.getSample((int) point.getX(), (int) point.getY(), 1),
                        raster.getSample((int) point.getX(), (int) point.getY(), 2)};

                    // the index of the point in the line segment (-S, S)
                    int parameter = k % 2 == 0 ? -((k + 1) / 2) : ((k + 1) / 2);
                    double weight = FlowUtils.calculateGausian(parameter, sigmaE)
                        * FlowUtils.calculateGausian(
                            calculateColorSpaceDistance(centerRGBArray, otherRGBArray), re);

                    totalWeight += weight;
                    tRed += raster.getSample((int) point.getX(), (int) point.getY(), 0) * weight;
                    tGreen += raster.getSample((int) point.getX(), (int) point.getY(), 1) * weight;
                    tBlue += raster.getSample((int) point.getX(), (int) point.getY(), 2) * weight;
                }

                tRed /= totalWeight;
                tGreen /= totalWeight;
                tBlue /= totalWeight;
                result.setRGB(j, i, (int) tRed * 256 * 256 + (int) tGreen * 256 + (int) tBlue);
            }
        }

        return result;
    }

    /**
     * Calculate the Cg(x) function.
     *
     * @param sourceImage the source image
     * @param etfVectors the edge tangent flow vectors
     * @return the image with the gradient direction bilateral filter applied
     */
    public BufferedImage calculateCgx(final BufferedImage sourceImage, final Point2D.Double[][] etfVectors,
            final int gradientSegmentLength, final double sigmaG, final double rg) {
        logger.info("Running Cg(x) filter");

        Raster raster = sourceImage.getData();

        // resulting image
        BufferedImage result =
            new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        double tRed = 0;
        double tGreen = 0;
        double tBlue = 0;
        for (int i = 0; i < etfVectors.length; i++) {
            for (int j = 0; j < etfVectors[0].length; j++) {
                tRed = 0;
                tGreen = 0;
                tBlue = 0;

                // rgb values in the center pixel
                final Point2D.Double centerPoint = new Point2D.Double(j, i);
                double[] centerRGBArray = {raster.getSample(j, i, 0), raster.getSample(j, i, 1), raster.getSample(j, i, 2)};

                ArrayList<Point2D.Double> gredientPoints = FlowUtils.calculateGradientPoints(
                    centerPoint, etfVectors[i][j], sourceImage.getWidth(), sourceImage.getHeight(), gradientSegmentLength);

                double totalWeight = 0;
                for (int k = 0; k < gredientPoints.size(); k++) {
                    final Point2D.Double point = gredientPoints.get(k);
                    double[] otherRGBArray = {raster.getSample((int) point.getX(), (int) point.getY(), 0),
                        raster.getSample((int) point.getX(), (int) point.getY(), 1),
                        raster.getSample((int) point.getX(), (int) point.getY(), 2)};

                    // the index of the point in the line segment (-T, T)
                    int parameter = k % 2 == 0 ? -((k + 1) / 2) : ((k + 1) / 2);
                    double weight = FlowUtils.calculateGausian(parameter, sigmaG)
                        * FlowUtils.calculateGausian(
                            calculateColorSpaceDistance(centerRGBArray, otherRGBArray), rg);

                    totalWeight += weight;
                    tRed += raster.getSample((int) point.getX(), (int) point.getY(), 0) * weight;
                    tGreen += raster.getSample((int) point.getX(), (int) point.getY(), 1) * weight;
                    tBlue += raster.getSample((int) point.getX(), (int) point.getY(), 2) * weight;
                }

                tRed /= totalWeight;
                tGreen /= totalWeight;
                tBlue /= totalWeight;
                result.setRGB(j, i, (int) tRed * 256 * 256 + (int) tGreen * 256 + (int) tBlue);
            }
        }

        return result;
    }

    /**
     * Calculate the color space distance between two points.
     *
     * @param rgb1 the rgb values in one pixel
     * @param rgb2 the rgb values in the other pixel
     * @return the distance in the color space between the two points
     */
    protected double calculateColorSpaceDistance(double[] rgb1, double[] rgb2) {

        double[] lab1 = ImageUtils.rgbToLab(rgb1);
        double[] lab2 = ImageUtils.rgbToLab(rgb2);

        // calculate the distance
        double distance = Math.sqrt((lab1[0] - lab2[0]) * (lab1[0] - lab2[0])
            + (lab1[1] - lab2[1]) * (lab1[1] - lab2[1])
            + (lab1[2] - lab2[2]) * (lab1[2] - lab2[2]));

        // normalize the distance
        // the value now is between 0 and 100, and 100 being most different colors, 0 the same color
        // the value instead should be from 0 to 1 where 1 is the same color and 0 is most different
    //    return (100 - distance) / 100;
        return distance;
    }
}
