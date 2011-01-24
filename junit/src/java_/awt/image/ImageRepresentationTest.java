/*
  Copyright (C) 2011 Karsten Heinrich (i-net software)

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
package java_.awt.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

import javax.swing.JPanel;

import junit.ikvm.ReferenceData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sun.awt.image.ToolkitImage;

public class ImageRepresentationTest {

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
    
	@Test
	public void test8bit() throws Exception{
		byte[] color = new byte[]{0,(byte)255};
		ColorModel cm = new IndexColorModel(1, 2, color, color, color);
		byte[] pixels = new byte[]{0,1,0,1,0,1,0,1,0};
		checkImage( cm, pixels, "test1bit");
	}
	
	private int[] createBitmap( int size, ColorModel model ){
		int[] data = new int[size*size];
		for( int y=0; y<size; y++){
			for( int x=0; x<size; x++ ){
				float[] channels = new float[4];
				channels[0] = (float)y / (float)(size-1);
				channels[1] = (float)x / (float)(size-1);
				channels[3] = (float)(y+x) / (float)(size*2);
				data[y*size + x] = model.getDataElement(channels, 0);
			}
		}
		return data;
	}
	
	@Test
	public void test24bit888() throws Exception{
		ColorModel cm = new DirectColorModel( 24, 255 << 16, 255 << 8, 255 );
		int[] pixels = createBitmap( 9, cm );		
		checkImage( cm, pixels, "test24bit888");
	}
	
	@Test
	public void test16bit555() throws Exception{
		ColorModel cm = new DirectColorModel( 16, 31 << 10, 31 << 5, 31 );
		int[] pixels = createBitmap( 9, cm );		
		checkImage( cm, pixels, "test16bit555");
	}
	
	@Test
	public void test16bit5551() throws Exception{
		ColorModel cm = new DirectColorModel( 16, 31 << 10, 31 << 5, 31, 1 << 15 );
		int[] pixels = createBitmap( 9, cm );		
		checkImage( cm, pixels, "test16bit5551");
	}
	
	@Test
	public void test16bit565() throws Exception{
		ColorModel cm = new DirectColorModel( 16, 31 << 11, 63 << 5, 31 );
		int[] pixels = createBitmap( 9, cm );		
		checkImage( cm, pixels, "test16bit565");
	}
	
	private void checkImage( ColorModel cm, byte[] pixels, String name ) throws Exception{
		int size = (int) Math.sqrt(pixels.length);
		MemoryImageSource source = new MemoryImageSource(size, size, cm, pixels, 0, size);
		checkMemorySource(source, name);
	}
	
	private void checkImage( ColorModel cm, int[] pixels, String name ) throws Exception{
		int size = (int) Math.sqrt(pixels.length);
		MemoryImageSource source = new MemoryImageSource(size, size, cm, pixels, 0, size);
		checkMemorySource(source, name);
	}


	private void checkMemorySource(MemoryImageSource source, String name) throws Exception {
		ToolkitImage image = (ToolkitImage) Toolkit.getDefaultToolkit().createImage(source);
		
		MediaTracker mt = new MediaTracker( new JPanel() );
        mt.addImage(image, 0);
        try {
            mt.waitForAll();
        }
        catch(InterruptedException ex) {
            ex.printStackTrace();
        }
		
		BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = copy.getGraphics();
		g.setColor(Color.RED);
		int size = copy.getWidth();
		g.fillRect(0, 0, size, copy.getHeight());
		g.drawImage(image, 0, 0, null);
		
		
		reference.assertEquals(name, copy, 0, false);
	}
}
