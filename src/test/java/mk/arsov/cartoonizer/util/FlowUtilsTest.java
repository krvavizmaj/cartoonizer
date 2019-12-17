package mk.arsov.cartoonizer.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for FlowUtils class.
 */
public class FlowUtilsTest {

    @Test
    public void testCalculateGradientPoints1() {
        Point2D.Double x = new Point2D.Double(5, 5);
        Point2D.Double gradientVector = new Point2D.Double(0.5, -0.3);

        ArrayList<Point2D.Double> points = FlowUtils.calculateGradientPoints(x, gradientVector, 30, 30, 5);


        Assert.assertEquals(11, points.size());
        Assert.assertTrue((points.get(0).getX() == 5.0) && (points.get(0).getY() == 5.0));
        Assert.assertTrue((points.get(4).getX() == 4.0) && (points.get(4).getY() == 6.0));
        Assert.assertTrue((points.get(10).getX() == 2.0) && (points.get(10).getY() == 7.0));

    }

    @Test
    public void testCalculateGradientPoints2() {
        Point2D.Double x = new Point2D.Double(2, 1);
        Point2D.Double gradientVector = new Point2D.Double(0.5, -0.2);

        ArrayList<Point2D.Double> points = FlowUtils.calculateGradientPoints(x, gradientVector, 10, 10, 5);

        Assert.assertEquals(5, points.size());
        Assert.assertTrue((points.get(0).getX() == 2.0) && (points.get(0).getY() == 1.0));
        Assert.assertTrue((points.get(3).getX() == 4.0) && (points.get(3).getY() == 0.0));
        Assert.assertTrue((points.get(4).getX() == 0.0) && (points.get(4).getY() == 2.0));
    }

    @Test
    public void testFindPointsInVectorDirection1() {
        Point2D.Double centerPoint = new Point2D.Double(5, 5);
        Point2D.Double vector = new Point2D.Double(0.5, -0.3);

        List<Point2D.Double> points = FlowUtils.findPointsInVectorDirection(centerPoint, vector, 5);

        Assert.assertEquals(5, points.size());
        Assert.assertEquals(6, (int) points.get(0).getX());
        Assert.assertEquals(5, (int) points.get(0).getY());

        Assert.assertEquals(6, (int) points.get(1).getX());
        Assert.assertEquals(4, (int) points.get(1).getY());

        Assert.assertEquals(7, (int) points.get(2).getX());
        Assert.assertEquals(4, (int) points.get(2).getY());

        Assert.assertEquals(8, (int) points.get(3).getX());
        Assert.assertEquals(4, (int) points.get(3).getY());

        Assert.assertEquals(8, (int) points.get(4).getX());
        Assert.assertEquals(3, (int) points.get(4).getY());
    }

    @Test
    public void testFindPointsInVectorDirection2() {
        Point2D.Double centerPoint = new Point2D.Double(5, 5);
        Point2D.Double vector = new Point2D.Double(-0.3, 0.8);

        List<Point2D.Double> points = FlowUtils.findPointsInVectorDirection(centerPoint, vector, 5);

        Assert.assertEquals(5, points.size());
        Assert.assertEquals(5, (int) points.get(0).getX());
        Assert.assertEquals(6, (int) points.get(0).getY());

        Assert.assertEquals(4, (int) points.get(1).getX());
        Assert.assertEquals(7, (int) points.get(1).getY());

        Assert.assertEquals(4, (int) points.get(2).getX());
        Assert.assertEquals(8, (int) points.get(2).getY());

        Assert.assertEquals(4, (int) points.get(3).getX());
        Assert.assertEquals(9, (int) points.get(3).getY());

        Assert.assertEquals(3, (int) points.get(4).getX());
        Assert.assertEquals(10, (int) points.get(4).getY());
    }

    @Test
    public void testFindPointsInVectorDirection3() {
        Point2D.Double centerPoint = new Point2D.Double(5, 5);
        Point2D.Double vector = new Point2D.Double(0.5, 0.2);

        List<Point2D.Double> points = FlowUtils.findPointsInVectorDirection(centerPoint, vector, 5);

        Assert.assertEquals(5, points.size());
        Assert.assertEquals(6, (int) points.get(0).getX());
        Assert.assertEquals(5, (int) points.get(0).getY());

        Assert.assertEquals(7, (int) points.get(1).getX());
        Assert.assertEquals(6, (int) points.get(1).getY());

        Assert.assertEquals(8, (int) points.get(2).getX());
        Assert.assertEquals(6, (int) points.get(2).getY());

        Assert.assertEquals(9, (int) points.get(3).getX());
        Assert.assertEquals(6, (int) points.get(3).getY());

        Assert.assertEquals(9, (int) points.get(4).getX());
        Assert.assertEquals(7, (int) points.get(4).getY());
    }

    @Test
    public void testGausian() {
        double x = 100;
        double sigma = 2;
        Assert.assertEquals(0.0D, FlowUtils.calculateGausian(x, sigma), 0);

        x = 9;
        sigma = 2;
        Assert.assertEquals(0.00000799187, FlowUtils.calculateGausian(x, sigma), 0.00000000005);

        sigma = 0;
        Assert.assertEquals(0.0, FlowUtils.calculateGausian(x, sigma), 0);
    }

}
