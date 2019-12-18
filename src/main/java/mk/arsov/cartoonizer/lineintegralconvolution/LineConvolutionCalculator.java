package mk.arsov.cartoonizer.lineintegralconvolution;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;

/**
 * Creates line integral convolution representation, based on an image and a vector field.
 */
public class LineConvolutionCalculator {

    /**
     * Calculate the line integral convolution for the given image and vector field.
     *
     * @param tangentVectors the vector field for the line integral convolution
     * @param image the source image used for displaying purpose
     * @param kernelLength the lent of the line segments
     * @param roundoff parameter used in the LIC calculation
     * @return image with calculated line integral convolution according to the tangent vectors at each point
     */
    public BufferedImage calculate(final Point2D.Double[][] tangentVectors, final BufferedImage image,
            final int kernelLength, final double roundoff) {
        // resultant image
        BufferedImage targetImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Raster raster = image.getData();

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int sum = 0;

                // get line segment at point (x, y) = (j, i)
                ArrayList<Point2D.Double> lineSegmentPoints = getLineSegmentPoints(
                        tangentVectors, new Point2D.Double(j, i), kernelLength, roundoff);

                // calculate convolution along the line segment
                if ((Math.abs(tangentVectors[i][j].getX()) == 0) && (Math.abs(tangentVectors[i][j].getY()) == 0)) {
                    // if the LIC in this point is singularity, the output value is a mean value of the surrounding pixels.
                    int summedPixels = 0;
                    for (int i1 = 0; i1 < 3; i1++) {
                        for (int j1 = 0; j1 < 3; j1++) {
                            int col = j - 1 + j1;
                            int row = i - 1 + i1;
                            if ((col >= 0) && (row >= 0) && (col < image.getWidth()) && (row < image.getHeight())) {
                                sum += raster.getSample(col, row, 0);
                                summedPixels++;
                            }
                        }
                    }

                    sum = (int) Math.round(sum / summedPixels);
                } else {
                    // else calculate convolution along the line segment
                    for (int k = 0; k < lineSegmentPoints.size(); k++) {
                        sum += raster.getSample((int) lineSegmentPoints.get(k).getX(), (int) lineSegmentPoints.get(k).getY(), 0);
                    }
                    sum /= (int) Math.round(lineSegmentPoints.size());
                }

                // resulting grayscale pixel value
                targetImage.setRGB(j, i, sum * 256 * 256 + sum * 256 + sum);
            }
        }

        return targetImage;
    }

    /**
     * Calculate the line integral convolution for the given image and vector field.
     *
     * @param tangentVectors the vector field for the line integral convolution
     * @param kernelLength the lent of the line segments
     * @param roundoff parameter used in the LIC calculation
     * @return image with calculated line integral convolution according to the tangent vectors at each point
     */
    public BufferedImage calculateOnWhiteNoise(final Point2D.Double[][] tangentVectors, final int kernelLength, final double roundoff) {
        int imageHeight = tangentVectors.length;
        int imageWidth = tangentVectors[0].length;

        // white noise field
        int[][] whiteNoise = new int[imageHeight][imageWidth];
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                whiteNoise[i][j] = (int)(Math.random() * 256.0);
            }
        }

        // resultant image
        BufferedImage targetImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);

        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                int sum = 0;

                // get line segment at point (x, y) = (j, i)
                ArrayList<Point2D.Double> lineSegmentPoints = getLineSegmentPoints(
                        tangentVectors, new Point2D.Double(j, i), kernelLength, roundoff);

                // calculate convolution along the line segment
                if ((Math.abs(tangentVectors[i][j].getX()) == 0) && (Math.abs(tangentVectors[i][j].getY()) == 0)) {
                    // if the LIC in this point is singularity, the output value is a mean value of the surrounding pixels.
                    int summedPixels = 0;
                    for (int i1 = 0; i1 < 3; i1++) {
                        for (int j1 = 0; j1 < 3; j1++) {
                            int col = j - 1 + j1;
                            int row = i - 1 + i1;
                            if ((col >= 0) && (row >= 0) && (col < imageWidth) && (row < imageHeight)) {
                                sum += whiteNoise[row][col];
                                summedPixels++;
                            }
                        }
                    }

                    sum = (int) Math.round(sum / summedPixels);
                } else {
                    // else calculate convolution along the line segment
                    for (int k = 0; k < lineSegmentPoints.size(); k++) {
                        sum += whiteNoise[(int) lineSegmentPoints.get(k).getY()][(int) lineSegmentPoints.get(k).getY()];
                    }
                    sum /= (int) Math.round(lineSegmentPoints.size());
                }

                // resulting grayscale pixel value
                targetImage.setRGB(j, i, sum * 256 * 256 + sum * 256 + sum);
            }
        }

        return targetImage;
    }

    /**
     * Calculate points in line segment with center in point x, and length 2*kernelLength.
     *
     * @param tangentVectorsField array with tangent vector for each point
     * @param x center point of line segment
     * @param kernelLength the lent of the line segments
     * @param roundoff parameter used in the LIC calculation
     * @return list with points in the line segment
     */
    public ArrayList<Point2D.Double> getLineSegmentPoints(final Point2D.Double[][] tangentVectorsField, final Point2D.Double x,
            final int kernelLength, final double roundoff) {

        ArrayList<Point2D.Double> pointsInLine = new ArrayList<Point2D.Double>();
        ArrayList<Point2D.Double> rightPoints = new ArrayList<Point2D.Double>();
        ArrayList<Point2D.Double> leftPoints = new ArrayList<Point2D.Double>();
        pointsInLine.add(new Point2D.Double(x.getX() + 0.5, x.getY() + 0.5));

        Point2D.Double currentPoint = new Point2D.Double(x.getX() + 0.5, x.getY() + 0.5);
        Point2D.Double currentVector = new Point2D.Double();
        // find points in negative direction
        for (int k = 0; k < kernelLength; k++) {
            // index of current point
            final double px = currentPoint.getX();
            final double py = currentPoint.getY();

            // get vector on current location
            currentVector.setLocation(tangentVectorsField[(int) Math.floor(py)][(int) Math.floor(px)]);
            currentVector.setLocation(-currentVector.getX(), -currentVector.getY());

            // normalize tangent vector
            final double vectorMagnitude =
                Math.sqrt(currentVector.getX() * currentVector.getX() + currentVector.getY() * currentVector.getY());
            Point2D.Double unitVector;
            if (vectorMagnitude != 0) {
                unitVector = new Point2D.Double(currentVector.getX() / vectorMagnitude, currentVector.getY() / vectorMagnitude);
            } else {
                unitVector = new Point2D.Double(currentVector.getX(), currentVector.getY());
            }

            double deltaS = shortestDistanceToCellEdge(currentVector, px, py);
            deltaS *= roundoff;

            double newX = px + unitVector.getX() * deltaS;
            double newY = py + unitVector.getY() * deltaS;
            if ((newX >= 0) && (newX < tangentVectorsField[0].length) && (newY >= 0) && (newY < tangentVectorsField.length)) {
                rightPoints.add(new Point2D.Double(newX, newY));
                currentPoint.setLocation(newX, newY);
            }
        }

        currentPoint = new Point2D.Double(x.getX() + 0.5, x.getY() + 0.5);
        // find points in positive direction
        for (int k = 0; k < kernelLength; k++) {
            // index of current point
            final double px = currentPoint.getX();
            final double py = currentPoint.getY();

            // get vector on current location
            currentVector = tangentVectorsField[(int) Math.floor(py)][(int) Math.floor(px)];

            // normalize tangent vector
            final double vectorMagnitude = Math.sqrt(currentVector.getX() * currentVector.getX() + currentVector.getY() * currentVector.getY());
            Point2D.Double unitVector;
            if (vectorMagnitude != 0) {
                unitVector = new Point2D.Double(currentVector.getX() / vectorMagnitude, currentVector.getY() / vectorMagnitude);
            } else {
                unitVector = new Point2D.Double(currentVector.getX(), currentVector.getY());
            }

            double deltaS = shortestDistanceToCellEdge(currentVector, px, py);
            deltaS *= roundoff;

            double newX = px + unitVector.getX() * deltaS;
            double newY = py + unitVector.getY() * deltaS;
            if ((newX >= 0) && (newX < tangentVectorsField[0].length) && (newY >= 0) && (newY < tangentVectorsField.length)) {
                leftPoints.add(new Point2D.Double(newX, newY));
                currentPoint.setLocation(newX, newY);
            }
        }

        // add the right and left points to the points list in parallel, truncate to the smaller set
        final int pointsInSegment = Math.min(rightPoints.size(), leftPoints.size());
        for (int i = 0; i < pointsInSegment; i++) {
            pointsInLine.add(rightPoints.get(i));
            pointsInLine.add(leftPoints.get(i));
        }

        // turn to integer points
        for (Point2D.Double point : pointsInLine) {
            point.setLocation((int) point.getX(), (int) point.getY());
        }

        return pointsInLine;
    }

    /**
     * Calculate the shortest distance from point (x, y) to one of the four surrounding edges of the cell, along the vector direction.
     *
     * @param vector the vector at location (x, y).
     * @param x x coordinate of the point.
     * @param y y coordinate of the point.
     * @return the distance to the closest edge from point (x, y)
     */
    protected double shortestDistanceToCellEdge(final Point2D.Double vector, final double x, final double y) {

        double minValue = Double.MAX_VALUE;
        double distance = 0;

        // distance to top edge
        if (vector.getY() > 0) {
            double edgeValue = Math.ceil(y);

            double angle;
            if (vector.getX() == 0) {
                angle = Math.PI / 2.0D;
            } else {
                angle = Math.atan(vector.getY() / vector.getX());
            }

            if (Math.sin(angle) != 0) {
                distance = Math.abs((edgeValue - y) / Math.sin(angle));
            } else {
                distance = Double.MAX_VALUE;
            }
            if (distance < minValue) {
                minValue = distance;
            }
        }

        // distance to bottom edge
        if (vector.getY() < 0) {
            double edgeValue = Math.floor(y);

            double angle;
            if (vector.getX() == 0) {
                angle = -Math.PI / 2.0D;
            } else {
                angle = Math.atan(vector.getY() / vector.getX());
            }

            if (Math.sin(angle) != 0) {
                distance = Math.abs((edgeValue - y) / Math.sin(angle));
            } else {
                distance = Double.MAX_VALUE;
            }
            if (distance < minValue) {
               minValue = distance;
            }
        }

        // distance to left edge
        if (vector.getX() < 0) {
            double edgeValue = Math.floor(x);

            double angle;
            if (vector.getX() == 0) {
                angle = Math.PI / 2.0D;
            } else {
                angle = Math.atan(vector.getY() / vector.getX());
            }

            if (Math.cos(angle) != 0) {
                distance = Math.abs((edgeValue - x) / Math.cos(angle));
            } else {
                distance = Double.MAX_VALUE;
            }
            if (distance < minValue) {
                minValue = distance;
            }
        }

        // distance to right edge
        if (vector.getX() > 0) {
            double edgeValue = Math.ceil(x);

            double angle;
            if (vector.getX() == 0) {
                angle = Math.PI / 2.0D;
            } else {
                angle = Math.atan(vector.getY() / vector.getX());
            }

            if (Math.cos(angle) != 0) {
                distance = Math.abs((edgeValue - x) / Math.cos(angle));
            } else {
                distance = Double.MAX_VALUE;
            }
            if (distance < minValue) {
                minValue = distance;
            }
        }

        return minValue;
    }

}
