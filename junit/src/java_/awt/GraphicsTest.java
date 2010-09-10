/*
  Copyright (C) 2009 Volker Berlin (i-net software)

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


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

import junit.ikvm.ReferenceData;

import org.junit.*;



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
        BufferedImage img = new BufferedImage( 100, 100, BufferedImage.TYPE_INT_ARGB );
        Graphics g = img.getGraphics();
        g.setColor( Color.RED );
        g.fillRect( 0, 0, 60, 60 );
        g.setColor( Color.BLUE );
        g.fillRect( 10, 10, 50, 50 );
        g.setColor( Color.GREEN );
        g.fillRect( 20, 20, 40, 40 );
        g.setColor( Color.CYAN );
        g.fillRect( 30, 30, 30, 30 );
        g.setColor( Color.YELLOW );
        g.fillRect( 40, 40, 20, 20 );
        g.setColor( Color.GRAY );
        g.fillRect( 50, 50, 10, 10 );
        g.dispose();
        reference.assertEquals("TestImage", img);
        return img;
    }

    @Test
    public void copyArea() throws Exception{
        BufferedImage img = createTestImage();
        Graphics g = img.getGraphics();
        g.copyArea( 10, 20, 50, 50, 30, 30 );
        reference.assertEquals( "copyArea", img );
        g.dispose();
    }
    
    @Test
    public void drawImageWithAffineTransform() throws Exception{
        BufferedImage img = createTestImage();
        Graphics2D g = (Graphics2D)img.getGraphics();
        AffineTransform transform = new AffineTransform(0.0, -1.0, 1.0, 0.0, 0.0, img.getWidth() );
        g.drawImage( createTestImage(), transform, null );
        reference.assertEquals( "drawImageWithAffineTransform", img );
        g.dispose();
    }
    
    @Test
    public void setComposite_Alpha_SRC_OVER() throws Exception {
        BufferedImage img = createTestImage();
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5F ) );
        g.setColor( Color.BLUE );
        g.fillRect( 30, 0, 40, 100 );
        reference.assertEquals( "setComposite_Alpha_SRC_OVER", img );
        g.dispose();
    }
}
