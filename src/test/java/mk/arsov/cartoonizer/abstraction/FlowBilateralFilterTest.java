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

import javax.annotation.Resource;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import mk.arsov.cartoonizer.lic.SobelGradient;
import mk.arsov.cartoonizer.util.ImageUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for FlowBilateralFilter class.
 */
public class FlowBilateralFilterTest {

  /** FlowBilateralFilter. */
  @Resource
  private FlowBilateralFilter flowBilateralFilter;
  
  /** ImageUtils. */
  @Resource
  private ImageUtils imageUtils;
  
  /** SobelGradient. */
  @Resource
  private SobelGradient sobelGradient;
  
  /** EdgeTangentFlow. */
  @Resource
  private EdgeTangentFlow edgeTangentFlow;
  
  /**
   * Test calculateCex() method.
   */
  @Test
  public void testCalculateCex() {
    
    PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/slika.bmp");
    BufferedImage grayscaleImage = imageUtils.toGrayscale(sourceImage.getAsBufferedImage());
    BufferedImage blurredImage = imageUtils.blur(grayscaleImage);
    final Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(blurredImage);
    final Point2D.Double[][] etfVectors = edgeTangentFlow.calculate(tangentVectors);
    
    BufferedImage resultImage = flowBilateralFilter.calculateCex(sourceImage.getAsBufferedImage(), etfVectors);
    
    
    imageUtils.saveImage(resultImage, "target/slika_fbl.png");
  }
  
  /**
   * Test calculateCgx() method.
   */
  @Test
  public void testCalculateCgx() {
    
    PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/slika.bmp");
    BufferedImage grayscaleImage = imageUtils.toGrayscale(sourceImage.getAsBufferedImage());
    BufferedImage blurredImage = imageUtils.blur(grayscaleImage);
    final Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(blurredImage);
    final Point2D.Double[][] etfVectors = edgeTangentFlow.calculate(tangentVectors);
    
    BufferedImage resultImage = flowBilateralFilter.calculateCgx(sourceImage.getAsBufferedImage(), etfVectors);
    
    
    imageUtils.saveImage(resultImage, "target/slika_fbl.png");
  }
  
  /**
   * Test full filter.
   */
  @Test
  public void testFlowBilateralFilter() {
    
    PlanarImage sourceImage = JAI.create("fileload", "src/test/resources/images/vlatko_big.jpg");
    BufferedImage grayscaleImage = imageUtils.toGrayscale(sourceImage.getAsBufferedImage());
    BufferedImage blurredImage = imageUtils.blur(grayscaleImage);
    final Point2D.Double[][] tangentVectors = sobelGradient.calculateTangentVectorField(blurredImage);
    final Point2D.Double[][] etfVectors = edgeTangentFlow.calculate(tangentVectors);
   
    BufferedImage resultImage = flowBilateralFilter.calculateCex(sourceImage.getAsBufferedImage(), etfVectors);
    resultImage = flowBilateralFilter.calculateCgx(resultImage, etfVectors);
    
    resultImage = flowBilateralFilter.calculateCex(sourceImage.getAsBufferedImage(), etfVectors);
    resultImage = flowBilateralFilter.calculateCgx(resultImage, etfVectors);
    
    resultImage = flowBilateralFilter.calculateCex(sourceImage.getAsBufferedImage(), etfVectors);
    resultImage = flowBilateralFilter.calculateCgx(resultImage, etfVectors);
    
    imageUtils.saveImage(resultImage, "target/vlatko_big_fbl.png");
  }
  
}
