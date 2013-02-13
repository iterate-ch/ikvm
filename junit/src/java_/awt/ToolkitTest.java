/*
  Copyright (C) 2010, 2013 Volker Berlin (i-net software)

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
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.net.URL;

import javax.swing.JPanel;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author Volker Berlin
 */
public class ToolkitTest{

    protected static ReferenceData reference;


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

    @Test
    public void createImage_ImageProducer() throws Exception{
        ImageProducer prod = new ImageProducer(){

            @Override
            public void addConsumer(ImageConsumer ic){
            }

            @Override
            public boolean isConsumer(ImageConsumer ic){
                return false;
            }

            @Override
            public void removeConsumer(ImageConsumer ic){
           }

            @Override
            public void requestTopDownLeftRightResend(ImageConsumer ic){
            }

            @Override
            public void startProduction(ImageConsumer ic){
                ic.setDimensions( 20, 20);
                // negative offsets are ignored
                ic.setPixels(-1, -1, 8, 1, ColorModel.getRGBdefault(), new byte[100], 0, 10);
                ic.setPixels( 0, 0, -1, -1, ColorModel.getRGBdefault(), new byte[100], 0, 10);
                
                try{
                	// Data offset out of bounds.
                    ic.setPixels( 0, 0, 8, 1, ColorModel.getRGBdefault(), new byte[100], -1, 10);
                    fail("ArrayIndexOutOfBoundsException should occur");
                }catch(ArrayIndexOutOfBoundsException ex){
                    // expected
                }
                try{
                	// Data array is too short.
                    ic.setPixels(0, 0, 10, 10, ColorModel.getRGBdefault(), new byte[10], 0, 10);
                    fail("ArrayIndexOutOfBoundsException should occur");
                }catch(ArrayIndexOutOfBoundsException ex){
                    // expected
                }
                ic.imageComplete(ImageConsumer.STATICIMAGEDONE);
            }
            
        };
        Image img = Toolkit.getDefaultToolkit().createImage(prod);
        MediaTracker tracker = new MediaTracker(new JPanel());
        tracker.addImage(img, 0);
        tracker.waitForAll();
    }
    
    @Test
    public void createImage_URL() throws Exception {
        Image img = Toolkit.getDefaultToolkit().createImage( new URL( "file:." ) );
        assertEquals( -1, img.getWidth( null ) );
        ImageProducer src = img.getSource();
        assertNotNull( src );        
    }
    
    @Test
    public void createImage_String() throws Exception {
        Image img = Toolkit.getDefaultToolkit().createImage( "." );
        assertEquals( -1, img.getWidth( null ) );
        ImageProducer src = img.getSource();
        assertNotNull( src );        
    }
    
    @Test
    public void createImage_bytes() throws Exception {
        Image img = Toolkit.getDefaultToolkit().createImage( new byte[5] );
        assertEquals( -1, img.getWidth( null ) );
        ImageProducer src = img.getSource();
        assertNotNull( src );        
    }
}
