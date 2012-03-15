/*
  Copyright (C) 2010 Karsten Heinrich (i-net software)

  This software is provided 'as-is', without any express or implied
  warranty.  In no event will the authors be held liable for any damages
  arising from the use of this software.

  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely, subject to the following restrictions:

  1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
  2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
  3. This notice may not be removed or altered from any source distribution.

  Jeroen Frijters
  jeroen@frijters.net
  
*/
package javax.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import junit.ikvm.ReferenceData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ClipTest {

    private static ReferenceData reference;

	@BeforeClass
    public static void setUpBeforeClass() throws Exception{
        reference = new ReferenceData();
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        if(reference != null){
            reference.save();
        }
        reference = null;
    }
	
	private BufferedImage getImage( Shape clip, boolean setClip ){
		BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 100, 100);
		g.setColor(Color.RED);
		if( setClip ){
			g.setClip( clip );
		}
		Rectangle2D bounds = clip.getBounds2D();
		// draw centered into the area, this requires the height or width to be 1
		Point2D upperLeft = new Point2D.Double( bounds.getMinX() + 0.5d, bounds.getMinY() + 0.5d );
		Point2D lowerRigth = new Point2D.Double( bounds.getMaxX() - 0.5d, bounds.getMaxY() - 0.5d );
		double lineWidth = Math.min( bounds.getWidth(), bounds.getHeight() ) * 1.01f; // 1.01 is required to avoid a rounding error in javas drawPath
		BasicStroke stroke = new BasicStroke( (float)lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
		g.setStroke( stroke );
        Line2D.Double line = new Line2D.Double( upperLeft.getX(), upperLeft.getY(), lowerRigth.getX(), lowerRigth.getY() );
		g.draw( line );	
		return img;
	}
	
	@Ignore("1 Pixel move of the line. Can be a problem of the stroke mapping.")
	@Test
	public void testSmallHorizontal() throws Exception{
		BufferedImage img = getImage( new Rectangle(10, 50, 80, 1), false );
		reference.assertEqualsMetrics("testSmallHorizontal", img);
	}
	
	@Ignore("1 Pixel move of the line. Can be a problem of the stroke mapping.")
	@Test
	public void testSmallVertical() throws Exception{
		BufferedImage img = getImage( new Rectangle(50, 10, 1, 80), false );
		reference.assertEqualsMetrics("testSmallVertical", img);
	}
	
	@Ignore("1 Pixel move of the line. Can be a problem of the stroke mapping.")
	@Test
	public void testSmallHorizontalShape() throws Exception{
		Polygon shape = new Polygon();
		shape.addPoint(10, 50);
		shape.addPoint(90, 50);
		shape.addPoint(89, 51);
		shape.addPoint(11, 51);
		BufferedImage img = getImage( shape, false );
		reference.assertEqualsMetrics("testSmallHorizontalShape", img);
	}
	
	@Ignore("1 Pixel move of the line. Can be a problem of the stroke mapping.")
	@Test
	public void testSmallVerticalShape() throws Exception{
		Polygon shape = new Polygon();
		shape.addPoint(50, 10);
		shape.addPoint(50, 90);
		shape.addPoint(51, 89);
		shape.addPoint(51, 11);
		BufferedImage img = getImage( shape, false );
		reference.assertEqualsMetrics("testSmallVerticalShape", img);
	}
	
	@Ignore("1 Pixel move of the line. Can be a problem of the stroke mapping.")
	@Test
	public void testSmallHorizontalClipped() throws Exception{
		BufferedImage img = getImage( new Rectangle(10, 50, 80, 1), true );
		reference.assertEqualsMetrics("testSmallHorizontalClipped", img);
	}
	
	@Ignore("1 Pixel move of the line. Can be a problem of the stroke mapping.")
	@Test
	public void testSmallVerticalClipped() throws Exception{
		BufferedImage img = getImage( new Rectangle(50, 10, 1, 80), true );
		reference.assertEqualsMetrics("testSmallVerticalClipped", img);
	}
	
	@Ignore("1 Pixel move of the line. Can be a problem of the stroke mapping.")
	@Test
	public void testSmallHorizontalShapeClipped() throws Exception{
		Polygon shape = new Polygon();
		shape.addPoint(10, 50);
		shape.addPoint(90, 50);
		shape.addPoint(89, 51);
		shape.addPoint(11, 51);
		BufferedImage img = getImage( shape, true );
		reference.assertEqualsMetrics("testSmallHorizontalShapeClipped", img);
	}
	
	@Ignore("1 Pixel move of the line. Can be a problem of the stroke mapping.")
	@Test
	public void testSmallVerticalShapeClipped() throws Exception{
		Polygon shape = new Polygon();
		shape.addPoint(50, 10);
		shape.addPoint(50, 90);
		shape.addPoint(51, 89);
		shape.addPoint(51, 11);
		BufferedImage img = getImage( shape, true );
		reference.assertEqualsMetrics("testSmallVerticalShapeClipped", img);
	}
}
