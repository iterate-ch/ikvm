/*
  Copyright (C) 2009 - 2012 Volker Berlin (i-net software)
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
package java_.awt.font;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.BufferedImage;

import junit.ikvm.ReferenceData;

import org.junit.*;


public class TextLayoutTest{

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


    @Test
    public void getLogicalHighlightShape() throws Exception{
        String text = "any Text";
        Font font = new Font("Arial", 0, 12);
        FontRenderContext frc = new FontRenderContext(null, false, false);
        TextLayout layout = new TextLayout(text, font, frc);
        Shape highlight = layout.getLogicalHighlightShape(0, text.length());
        Rectangle bounds = highlight.getBounds();
		reference.assertEquals("getLogicalHighlightShape.x", bounds.x);
		reference.assertEquals("getLogicalHighlightShape.y", bounds.y);
		reference.assertEquals("getLogicalHighlightShape.width", bounds.width, 2 );
		reference.assertEquals("getLogicalHighlightShape.height", bounds.height);
    }
    
    @Test
    public void testDrawConsistency() throws InterruptedException{
    	String text = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";
    	BufferedImage im = new BufferedImage(500, 50, BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g = (Graphics2D) im.getGraphics();
    	Font font = new Font("Arial", 0, 12);
    	g.setFont(font);
    	FontRenderContext frc = g.getFontRenderContext();
    	TextLayout layout = new TextLayout(text, font, frc);
    	// calculated size
        Shape highlight = layout.getLogicalHighlightShape(0, text.length());
        int calculated = highlight.getBounds().width;
        // determine size of the drawing
        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, im.getWidth(), im.getHeight() );
        g.setColor( Color.BLACK );
        g.drawString(text, 10, 30 );
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
        for( int x = 0; x<im.getWidth(); x++ ){
        	for( int y = 0; y < im.getHeight(); y++ ){
        		if( im.getRGB(x, y) != Color.WHITE.getRGB() ){
        			if( x < start ){
        				start = x;
        			}
        			if( x > end ){
        				end = x;
        			}
        		}
        	}
        }
        int byDrawing = end - start;
        reference.assertEquals("widthByTextLayout", calculated );
        reference.assertEquals("widthByDrawing", byDrawing );
        Assert.assertTrue( Math.abs( byDrawing - calculated ) <= 1 );
    }
    
    @Test
	public void getAdvance() {
		Font font = new Font("Arial", 0, 10);
		FontRenderContext frc = new FontRenderContext(null, false, false);
		TextLayout layout = new TextLayout("some LTR chars-שּׁבֿוּשּׂﬠהּﭏהּצ", font, frc); //RTL and LTR characters. This will be split internally
		reference.assertEquals("getAdvance", layout.getAdvance(), 1 );
	}
}
