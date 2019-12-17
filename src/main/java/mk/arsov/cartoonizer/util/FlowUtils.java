package mk.arsov.cartoonizer.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods used in edge detection and the bilateral filters.
 */
public class FlowUtils {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FlowUtils.class);

    private FlowUtils() {

    }

    /**
     * Calculate points in line parallel to the gradient in point x.
     *
     * @param x location of center point in line segment
     * @param gradientVector gradient vector in point x
     * @param imageWidth width of the image, for boundary calculation
     * @param imageHeight height of the image, for boundary calculation
     * @param t number of points in one direction
     * @return list of 2t+1 points in direction of gradient vector
     */
    public static ArrayList<Point2D.Double> calculateGradientPoints(final Point2D.Double x,
            final Point2D.Double gradientVector,
            final int imageWidth,
            final int imageHeight,
            final int t) {

        final ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
        // add the center point as first point
        points.add(x);

        // find points in one direction
        List<Point2D.Double> pointsRight = findPointsInVectorDirection(x, gradientVector, t);

        // the points in the other direction are the same points only symmetrical
        // relative to the center point
        // skip the first point as it's the center point and it's already added
        for (int i = 0; i < pointsRight.size(); i++) {
            final Point2D.Double point = pointsRight.get(i);
            final double newX = x.getX() - (point.getX() - x.getX());
            final double newY = x.getY() - (point.getY() - x.getY());

            // check if the point considered or the symetrical point is out of the
            // image bounds
            // if it is then don't put any more points
            if ((point.getX() >= imageWidth) || (point.getX() < 0) || (point.getY() >= imageHeight)
                    || (point.getY() < 0) || (newX >= imageWidth) || (newX < 0) || (newY >= imageHeight)
                    || (newY < 0)) {
                break;
            }

            // add the original point and the symetrical point
            points.add(point);
            points.add(new Point2D.Double(newX, newY));
        }

        return points;
    }

    /**
     * Calculate points in direction of given vector from a starting point. From
     * the starting point the next point is obtained by adding the gradient vector
     * multiplied by some value delta to the current point.
     *
     * @param centerPoint starting point
     * @param vector the vector in whom's direction the points should be calculated
     * @param numberOfPoints number of points to find
     * @return list of points in the direction of the given vector from the starting point
     */
    public static List<Point2D.Double> findPointsInVectorDirection(final Point2D.Double centerPoint,
            final Point2D.Double vector,
            final int numberOfPoints) {

        List<Point2D.Double> points = new ArrayList<Point2D.Double>();

        // check for zero length vector
        if ((vector.getX() == 0) && (vector.getY() == 0)) {
            logger.debug("Zero length gradient vector at location ({}, {})", centerPoint.getX(),
                centerPoint.getY());
            return points;
        }

        // the current point the line is at
        Point2D.Double currentPoint = new Point2D.Double(centerPoint.getX() + 0.5,
            centerPoint.getY() + 0.5);

        Point2D.Double otherPoint = new Point2D.Double(currentPoint.getX(), currentPoint.getY());
        while (points.size() < numberOfPoints) {
            // if the other point is still in the same pixel, add the vector
            while (((int) otherPoint.getX() == (int) currentPoint.getX())
                  && ((int) otherPoint.getY() == (int) currentPoint.getY())) {
                otherPoint.setLocation(otherPoint.getX() + vector.getX(), otherPoint.getY() + vector.getY());
            }

            points.add(new Point2D.Double((int) otherPoint.getX(), (int) otherPoint.getY()));
            currentPoint.setLocation(otherPoint.getX(), otherPoint.getY());
        }

        return points;
    }

    /**
     * Calculate difference of gausians function.
     *
     * @param x parameter for function
     * @param sigmaC controls the size of the center interval
     * @param sigmaS controls the size of the surrounding interval (sigmaS ~ 1.6 * sigmaC)
     * @param ro controls the level of noise detected (tipically ranges in [0.97 - 1.0])
     * @return value of difference of gausians function
     */
    public static double calculateDog(final double x, final double sigmaC, final double sigmaS, final double ro) {
        double gausianC = calculateGausian(x, sigmaC);
        double gausianS = calculateGausian(x, sigmaS);

        return gausianC - ro * gausianS;
    }

    /**
     * Calculates the gausian function for given point x, and sigma value.
     *
     * @param x variable in gausian function
     * @param sigma sigma value in gausian function
     * @return value of gausian function for value x
     */
    public static double calculateGausian(final double x, final double sigma) {
        if (sigma == 0) {
            return 0;
        } else {
            return (1 / (sigma * Math.sqrt(2 * Math.PI))) * Math.pow(Math.E, (-x * x / (2 * sigma * sigma)));
        }
    }
}
