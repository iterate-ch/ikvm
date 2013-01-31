/*
  Copyright (C) 2009 - 2013 Volker Berlin (i-net software)

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
package java_.awt;

import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

public class GraphicsTest{

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
    }


    private BufferedImage createTestImage() throws Exception{
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 60, 60);
        g.setColor(Color.BLUE);
        g.fillRect(10, 10, 50, 50);
        g.setColor(Color.GREEN);
        g.fillRect(20, 20, 40, 40);
        g.setColor(Color.CYAN);
        g.fillRect(30, 30, 30, 30);
        g.setColor(Color.YELLOW);
        g.fillRect(40, 40, 20, 20);
        g.setColor(Color.GRAY);
        g.fillRect(50, 50, 10, 10);
        g.dispose();
        reference.assertEquals("TestImage", img);
        return img;
    }


    @Test
    public void copyArea() throws Exception{
        BufferedImage img = createTestImage();
        Graphics g = img.getGraphics();
        g.copyArea(10, 20, 50, 50, 30, 30);
        reference.assertEquals("copyArea", img);
        g.dispose();
    }


    @Test
    public void drawImageWithAffineTransform() throws Exception{
        BufferedImage img = createTestImage();
        Graphics2D g = (Graphics2D)img.getGraphics();
        AffineTransform transform = new AffineTransform(0.0, -1.0, 1.0, 0.0, 0.0, img.getWidth());
        g.drawImage(createTestImage(), transform, null);
        reference.assertEquals("drawImageWithAffineTransform", img);
        g.dispose();
    }


    @Test
    public void setComposite_Null() throws Exception{
        BufferedImage img = createTestImage();
        Graphics2D g = (Graphics2D)img.getGraphics();
        try{
            g.setComposite(null);
            fail("Exception expected");
        }catch(Exception e){
            reference.assertEquals("setComposite_Null", e.toString());
        }
    }


    @Test
    public void setComposite_Alpha_CLEAR_FirstCall() throws Exception{
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setComposite(AlphaComposite.Clear); // first call before setColor or setPaint
        g.drawImage(createTestImage(), 0, 0, null);
        g.fillRect(0, 0, 100, 100);
        reference.assertEquals("setComposite_Alpha_CLEAR_FirstCall", img, 0.005, false);
        g.dispose();
    }


    @Test
    public void setComposite_Alpha_CLEAR() throws Exception{
        setComposite("setComposite_Alpha_CLEAR", AlphaComposite.getInstance(AlphaComposite.CLEAR, 128F / 255F));
    }


    @Test
    public void setComposite_Alpha_SRC() throws Exception{
        setComposite("setComposite_Alpha_SRC", AlphaComposite.getInstance(AlphaComposite.SRC, 128F / 255F));
    }


    @Test
    public void setComposite_Alpha_SRC_OVER() throws Exception{
        setComposite("setComposite_Alpha_SRC_OVER", AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 128F / 255F));
    }


    @Test
    public void setComposite_Alpha_DST() throws Exception{
        setComposite("setComposite_Alpha_DST", AlphaComposite.getInstance(AlphaComposite.DST, 128F / 255F));
    }


    private void setComposite(String key, Composite composite) throws Exception{
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setColor(Color.RED);
        g.drawLine(0, 5, 100, 5); // line in the background

        g.setColor(Color.BLUE); // set the color before composite
        g.setComposite(composite);
        g.drawImage(createTestImage(), 0, 0, null);
        g.fillRect(30, 0, 40, 100);
        g.setColor(Color.GREEN);
        g.fillRect(0, 70, 100, 30);
        reference.assertEquals(key, img, 0.005, false);
        g.dispose();
    }


    @Test
    public void drawString() throws Exception{
        Font font = new Font("Arial", 0, 12);

        BufferedImage img = new BufferedImage(100, 25, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setFont(font);
        g.setBackground(Color.WHITE);
        g.setColor(Color.BLACK);
        g.clearRect(0, 0, img.getWidth(), img.getHeight() );
        try {
            g.drawString( (String)null, 10, 10 );
            fail("NullPointerException expected");
        } catch( NullPointerException npe ) {
            // expected;
        }
        g.drawString( "", 10, 20 );
        g.drawString( "unknown", 10, 20 );
        g.dispose();
        reference.assertEquals( "drawString_", img, 0.01, true );
        reference.assertEqualsMetrics("drawString", img);

    }

    @Ignore("completely wrong in IKVM")
    @Test
    public void drawGlyphVector() throws Exception{
        Font font = new Font("Arial", 0, 12);
        FontRenderContext frc = new FontRenderContext(null, true, true);
        GlyphVector gv = font.createGlyphVector(frc, "unknown");

        BufferedImage img = new BufferedImage(100, 25, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setBackground(Color.WHITE);
        g.setColor(Color.BLACK);
        g.clearRect(0, 0, img.getWidth(), img.getHeight() );
        g.drawGlyphVector(gv, 10, 20);
        g.dispose();
        reference.assertEquals( "drawGlyphVector_", img, 0.01, true );
        reference.assertEqualsMetrics("drawGlyphVector", img);
    }


    @Test
    public void setLinearGradientPaint() throws Exception{
        setLinearGradientPaint(CycleMethod.NO_CYCLE);
        setLinearGradientPaint(CycleMethod.REFLECT);
        setLinearGradientPaint(CycleMethod.REPEAT);
    }


    private void setLinearGradientPaint(CycleMethod cycle) throws Exception{
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setPaint( new LinearGradientPaint(0,0, 40, 80, new float[]{0.0F, 0.2F, 0.7F, 1.0F}, new Color[]{Color.GREEN, Color.RED, Color.YELLOW, Color.GREEN}, cycle ) );
        g.fillRect(0, 0, 100, 100);
        g.dispose();
        reference.assertEquals("setLinearGradientPaint " + cycle, img, 0.05, false);
    }


    @Test
    public void transform() throws Exception{
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setTransform(g.getTransform());
        g.scale(0.5, 0.5);
        g.translate(-10, -10);

        BufferedImage testImg = createTestImage();

        g.drawImage(testImg, 30, 30, null);
        AffineTransform tx = g.getTransform();
        g.scale(0.5, 0.5);
        g.setTransform(tx);

        g.drawImage(testImg, 130, 130, null);

        reference.assertEquals("transform", img, 0.05, false);
    }


    @Test
    public void setStroke() throws Exception{
        BufferedImage img = new BufferedImage( 100, 100, BufferedImage.TYPE_INT_ARGB );

        BasicStroke stroke1 = new BasicStroke(0.01F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{1,3}, 0);
        BasicStroke stroke2 = new BasicStroke(0.01F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{1,1}, 1);
        BasicStroke stroke3 = new BasicStroke(0.01F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{1,3}, 2);

        int x1 = 0;
        int x2 = 100;
        int y = 10;
        Graphics2D g = (Graphics2D)img.getGraphics();
        assertEquals(new BasicStroke(), g.getStroke());

        // draw a red zig zag line
        g.setColor(Color.RED);
        g.setStroke(stroke1);
        g.drawLine(x1, y, x2, y);
        y++;
        g.setStroke(stroke2);
        g.create().dispose(); // create and dispose the a Graphics, stoke should be valid
        g.drawLine(x1, y, x2, y);
        y++;
        g.setStroke(stroke3);
        Graphics2D g2 = (Graphics2D)g.create(); // test the stroke in a new Graphics
        g2.drawLine(x1, y, x2, y);
        g2.dispose();
        y++;

        // draw a hairline
        g.setStroke(new BasicStroke(0));
        g.setColor(Color.BLUE);
        g.drawLine(x1, y, x2, y);
        y++;

        y++;
        g.setColor(Color.GREEN);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10, new float[]{3, 6, 3}, 1));
        g.drawLine(x1, y, x2, y);
        y += 3;
        
        g.setColor(Color.MAGENTA);
        g.setStroke(stroke3);
        g.scale(2, 2);
        g.drawLine(x1, y, x2, y);
        
        g.dispose();
        reference.assertEquals("setStroke", img);
    }
    
    @Test
    public void clearRect() throws Exception {
        BufferedImage img = new BufferedImage( 20, 20, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)img.getGraphics();

        // clearRect should ignore color, composite, and paint
        g.setColor( new Color( 200, 200, 200, 50 ) );
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5f ) );
        g.setPaint( new GradientPaint( new Point2D.Float( 0, 0 ), new Color( 10, 10, 10 ), new Point2D.Float( 20, 20 ), new Color( 80, 80, 80 ), true ) );
        g.fillRect( 0, 0, 20, 20 );

        Color c = new Color( 5, 255, 6 ); // green
        g.setBackground( c );
        g.clearRect( 0, 0, 10, 10 ); // left top corner

        c = new Color( 255, 0, 0, 51 ); // red with alpha
        g.setBackground( c );
        g.clearRect( 10, 0, 10, 10 ); // right top corner

        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC, 0.5f ) );

        c = new Color( 0, 0, 255 ); // blue
        g.setBackground( c );
        g.clearRect( 0, 10, 10, 10 ); // left bottom corner

        c = new Color( 254, 254, 0, 52 ); // yellow with alpha
        g.setBackground( c );
        g.clearRect( 10, 10, 10, 10 ); // right bottom corner

        reference.assertEquals( "clearRect", img );
    }
    
    @Test
    public void radialGradientPaint() throws Exception {
    	radialGradientPaint( CycleMethod.REPEAT );
    	radialGradientPaint( CycleMethod.NO_CYCLE );
    	radialGradientPaint( CycleMethod.REFLECT );
    }
    
    private void radialGradientPaint( CycleMethod cycle ) throws Exception {
        BufferedImage img = new BufferedImage( 100, 100, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)img.getGraphics();
        
        Paint paint = new RadialGradientPaint( 30, 60, 40, new float[]{0.0F, 0.2F, 0.7F, 1.0F}, new Color[]{Color.GREEN, Color.RED, Color.YELLOW, Color.GREEN}, cycle );
        g.setPaint( paint );
        g.fillRect(0, 0, 200, 200);
        g.dispose();
        
        reference.assertEquals( "radialGradientPaint " + cycle, img, 0.05, false );
    }
    
    @Test
    public void texturePaint() throws Exception {   	
        BufferedImage img = new BufferedImage( 200, 200, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)img.getGraphics();
        
    	BufferedImage txtr = createTestImage();
    	TexturePaint paint = new TexturePaint(txtr, new Rectangle(10,20,50,50));
    	
        g.setPaint( paint );
        g.fillRect(0, 0, 200, 200);
        g.dispose();
        
        reference.assertEquals( "texturePaint", img, 0.05, false );
    }
    
    @Test
    public void custemGradientPaint() throws Exception {
        BufferedImage img = new BufferedImage( 100, 100, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)img.getGraphics();
        
        
        Paint paint = new Paint(){
        	private Paint p = new RadialGradientPaint( 30, 60, 40, new float[]{0.0F, 0.2F, 0.7F, 1.0F}, new Color[]{Color.GREEN, Color.RED, Color.YELLOW, Color.GREEN}, CycleMethod.REPEAT );

			@Override
			public int getTransparency() {
				return p.getTransparency();
			}

			@Override
			public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
				return p.createContext(cm, deviceBounds, userBounds, xform, hints);
			}
        	
        };
        		
        g.setPaint( paint );
        g.fillRect(0, 0, 200, 200);
        g.dispose();
        
        reference.assertEquals( "custemGradientPaint", img, 0.05, false );
    }
    
    @Test
    public void getFontRenderContext(){
        BufferedImage img = new BufferedImage( 20, 20, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)img.getGraphics();
        FontRenderContext frc = g.getFontRenderContext();
        assertFalse( "AntiAliased", frc.isAntiAliased() );
        assertFalse( "Fractional", frc.usesFractionalMetrics() );
        
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		frc = g.getFontRenderContext();
        assertFalse( "AntiAliased", frc.isAntiAliased() );
        assertTrue( "Fractional", frc.usesFractionalMetrics() );

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		frc = g.getFontRenderContext();
        assertTrue( "AntiAliased", frc.isAntiAliased() );
        assertTrue( "Fractional", frc.usesFractionalMetrics() );

		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		frc = g.getFontRenderContext();
        assertTrue( "AntiAliased", frc.isAntiAliased() );
        assertFalse( "Fractional", frc.usesFractionalMetrics() );

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		frc = g.getFontRenderContext();
        assertFalse( "AntiAliased", frc.isAntiAliased() );
        assertFalse( "Fractional", frc.usesFractionalMetrics() );
    }
    
    @Test
	public void constructorWidthRaster() throws Exception {
		BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setPaint(new GradientPaint(new Point2D.Float(0, 0), Color.GREEN, new Point2D.Float(20, 20), Color.RED, true));
		WritableRaster raster = img.getRaster();

		BufferedImage img2 = new BufferedImage(img.getColorModel(), raster, true, null);
		Graphics2D g2 = img2.createGraphics();
		reference.assertEquals("constructorWidthRaster", img2);
	}
}
