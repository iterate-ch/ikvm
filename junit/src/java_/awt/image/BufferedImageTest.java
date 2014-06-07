/*
  Copyright (C) 2010-2013 Volker Berlin (i-net software)

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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.stream.ImageInputStream;

import junit.ikvm.ReferenceData;

import org.junit.*;

import static org.junit.Assert.*;



/**
 * @author Volker Berlin
 */
public class BufferedImageTest{

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
    public void constructor(){
        try{
            new BufferedImage(0, 100, BufferedImage.TYPE_INT_ARGB);
            fail("IllegalArgumentException should throw");
        }catch(IllegalArgumentException ex){
            // normal case
        }
        try{
            new BufferedImage(100, 0, BufferedImage.TYPE_INT_ARGB);
            fail("IllegalArgumentException should throw");
        }catch(IllegalArgumentException ex){
            // normal case
        }
    }
    
    @Test
	public void setRGB() throws Exception {
		// test parallel use of Graphics and setRGB
		BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.createGraphics();

		g.setColor(Color.RED);
		g.fillRect(0, 0, 10, 50);

		img.setRGB(0, 0, Color.BLACK.getRGB());

		g.setColor(Color.GREEN);
		g.fillRect(10, 0, 10, 50);

		int[] rgbs = new int[10 * 50];
		Arrays.fill(rgbs, Color.YELLOW.getRGB());
		img.setRGB(20, 0, 10, 50, rgbs, 0, 10);

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(30, 0, 10, 50);
		
		reference.assertEquals("setRGB", img);
	}
    
    @Test
	public void accessOnImageOnLoading() throws Exception {
		BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setPaint(new GradientPaint(0, 0, Color.RED, 100,100, Color.GREEN));
		g.fillRect(0, 0, 100, 100);
		g.dispose();

		accessOnImageOnLoading("png", img );
		accessOnImageOnLoading("gif", img );
		accessOnImageOnLoading("jpeg", img );
		
        BufferedImage imgRGB = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        g = imgRGB.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        accessOnImageOnLoading("bmp", imgRGB );

        BufferedImage imgBinary = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        g = imgBinary.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        accessOnImageOnLoading("wbmp", imgBinary );
    }
    
    private void accessOnImageOnLoading( String format, BufferedImage img ) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, format, baos);
		img = ImageIO.read( new ByteArrayInputStream(baos.toByteArray()) );
		
		InputStream sourceStream = new ByteArrayInputStream(baos.toByteArray());
		ImageInputStream stream = ImageIO.createImageInputStream(sourceStream);
		Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
		ImageReader reader = readers.next();

		ImageNotifier notifier = new ImageNotifier();
		reader.addIIOReadUpdateListener(notifier);
		reader.setInput(stream, true, true);
		ImageReadParam param = reader.getDefaultReadParam();
		BufferedImage bi = reader.read(0, param);
//		ImageIO.write(bi, format, new File("c:/temp/accessOnImageOnLoading1." + format) );
//		ImageIO.write(img, format, new File("c:/temp/accessOnImageOnLoading2." + format) );
		ReferenceData.assertEquals("accessOnImageOnLoading_" + format , img, bi, 0, false);
    }
    
    private class ImageNotifier implements IIOReadUpdateListener {

		@Override
		public void passStarted(ImageReader source, BufferedImage theImage, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY,
				int[] bands) {
		}

		@Override
		public void imageUpdate(ImageReader source, BufferedImage theImage, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {
			if (minY == 0) {
				Graphics g = theImage.getGraphics();
				g.setColor(Color.WHITE);
				g.fillRect(0, height, theImage.getWidth(), theImage.getHeight()-2*height);
				g.dispose();

				if( source.getClass().getSimpleName().equals("BMPImageReader") ) {
					// with Java 8 the BMPImageReader work directly with the byte array of the Java Raster
					// that we can not switch back to the raster before changes occur.
					// a call to getGraphics() switch to .NET Bitmap because we use .NET Graphics
					// the follow call of getRaster() switch back to the Java Raster
					theImage.getRaster();
				}
			}
		}

		@Override
		public void passComplete(ImageReader source, BufferedImage theImage) {
		}

		@Override
		public void thumbnailPassStarted(ImageReader source, BufferedImage theThumbnail, int pass, int minPass, int maxPass, int minX, int minY, int periodX,
				int periodY, int[] bands) {
		}

		@Override
		public void thumbnailUpdate(ImageReader source, BufferedImage theThumbnail, int minX, int minY, int width, int height, int periodX, int periodY,
				int[] bands) {
		}

		@Override
		public void thumbnailPassComplete(ImageReader source, BufferedImage theThumbnail) {
		}
    }
}
