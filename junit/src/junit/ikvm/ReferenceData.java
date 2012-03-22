/*
  Copyright (C) 2009 - 2012 Volker Berlin (i-net software)

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
package junit.ikvm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Assert;

import static org.junit.Assert.fail;

/**
 * RefererenceData to compare. With Sun Java it will save the data. With IKVM it will read the data.
 */
public class ReferenceData{

    private static final boolean IKVM;
    static{
        String referenceProp = System.getProperty( "reference" );
        IKVM = referenceProp == null ? 
                System.getProperty( "java.vm.name" ).equals( "IKVM.NET" ) :
                    !Boolean.parseBoolean( referenceProp );
    }

    private static final String NO_DATA_MSG = " Please run the test first with a Sun Java VM to create reference data for your system.";

    private final Map<String, Serializable> data;

    private final File file;


    /**
     * Create ReferenceData for the calling class. The class is reading from stacktrace. All keys must be unique for the
     * calling class. It should be called in a method which is marked with @BeforeClass. Typical this method is named
     * setUpBeforeClass().
     * 
     * @throws Exception
     *             If it run with IKVM and the data can not be read.
     */
    public ReferenceData() throws Exception{
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String name = ste.getClassName();
        String path = "references/" + name.replace('.', '/');
        file = new File(path).getAbsoluteFile();
        if(IKVM){
            if(!file.exists()){
                fail(NO_DATA_MSG);
            }
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (Map<String, Serializable>)ois.readObject();
            fis.close();
        }else{
            data = Collections.synchronizedMap( new HashMap<String, Serializable>() );  // synchronized for multi thread tests
        }
    }


    /**
     * Save the data if it is a Sun VM. It should be called in a method which is marked with @AfterClass. Typical this
     * method is named tearDownAfterClass().
     * 
     * @throws Exception
     *             If it run with Sun VM and can not save this reference data.
     */
    public void save() throws Exception{
        if(!IKVM){
            file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.flush();
            oos.close();
        }
    }


    public static boolean isIkvm(){
        return IKVM;
    }

    
	/**
	 * Get a saved object from serialized data cache.
	 * 
	 * @param key
	 *            The key in the reference data. It must be unique for the class
	 *            that create this ReferenceData
	 * @return the value if exists
	 */
	public Object get(String key) {
		Object expected = data.get(key);
		if (expected == null && !data.containsKey(key)) {
			fail("No Reference value for key:" + key + NO_DATA_MSG);
		}
		return expected;
	}
	

    /**
     * Asserts that two objects are equal which come from Sun VM and IKVM run.
     * 
     * @param key
     *            The key in the reference data. It must be unique for the class that create this ReferenceData
     * @param value
     *            the value will be saved in Sun VM and compared in IKVM. It must be Serializable that it can be saved
     *            and loaded on hard disk.
     */
    public void assertEquals(String key, Serializable value){
        if(key == null){
            fail("Key is null.");
        }
        if(IKVM){
            Object expected = get(key);
            if(expected instanceof float[]){
                Assert.assertArrayEquals(key, (float[])expected, (float[])value, 0.0F);
                return;
            }
            Assert.assertEquals(key, expected, value);
        }else{
            data.put(key, value);
        }
    }


    /**
     * Asserts that two objects are equal which come from Sun VM and IKVM run.
     * 
     * @param key
     *            The key in the reference data. It must be unique for the class that create this ReferenceData
     * @param value
     *            the value will be saved in Sun VM and compared in IKVM. It must be Serializable that it can be saved
     *            and loaded on hard disk.
     * @param delta
     *            The maximum difference
     */
    public void assertEquals( String key, float value, float delta ) {
        if( key == null ) {
            fail( "Key is null." );
        }
        if( IKVM ) {
            Float expected = (Float)get( key );
            Assert.assertEquals( key, (float)expected, value, delta );
        } else {
            data.put( key, value );
        }
    }
    
    /**
     * Asserts that two objects are equal which come from Sun VM and IKVM run.
     * 
     * @param key
     *            The key in the reference data. It must be unique for the class that create this ReferenceData
     * @param value
     *            the value will be saved in Sun VM and compared in IKVM. It must be Serializable that it can be saved
     *            and loaded on hard disk.
     * @param delta
     *            The maximum difference
     */
    public void assertEquals( String key, double value, double delta ) {
        if( key == null ) {
            fail( "Key is null." );
        }
        if( IKVM ) {
        	Double expected = (Double)get( key );
            Assert.assertEquals( key, (double)expected, value, delta );
        } else {
            data.put( key, value );
        }
    }
    
    /**
     * Asserts that two objects are equal which come from Sun VM and IKVM run.
     * 
     * @param key
     *            The key in the reference data. It must be unique for the class that create this ReferenceData
     * @param values
     *            the values will be saved in Sun VM and compared in IKVM. It must be Serializable that it can be saved
     *            and loaded on hard disk.
     * @param delta
     *            The maximum difference
     */
    public void assertEquals( String key, float[] values, float delta ) {
        if( key == null ) {
            fail( "Key is null." );
        }
        if( IKVM ) {
            float[] expected = (float[])get( key );
            Assert.assertArrayEquals( key, expected, values, delta );
        } else {
            data.put( key, values );
        }
    }
    
    /**
     * Asserts that two images are equal which come from Sun VM and IKVM run.
     * 
     * @param key
     *            The key in the reference data. It must be unique for the class that create this ReferenceData
     * @param value
     *            the value will be saved in Sun VM and compared in IKVM. The value will be saved as png image. If a
     *            difference occur then a second png file will be saved which ended with _ikvm. This make it easer to
     *            see the image differences.
     */
    public void assertEquals( String key, BufferedImage img ) throws Exception {
        assertEquals( key, img, 0.0, false );
    }
    
    /**
     * Asserts that two images are equal which come from Sun VM and IKVM run.
     * 
     * @param key
     *            The key in the reference data. It must be unique for the class that create this ReferenceData
     * @param img
     *            the value will be saved in Sun VM and compared in IKVM. The value will be saved as png image. If a
     *            difference occur then a second png file will be saved which ended with _ikvm. This make it easer to
     *            see the image differences.
     * @param delta
     *            The maximum difference between hue, saturation and brightness of every pixel
     * @param useMediumValue
     *            if true then compare the medium value of 9 pixel instead of every pixel
     */
    public void assertEquals( String key, BufferedImage img, double delta, boolean useMediumValue ) throws Exception {
        if( key == null ) {
            fail( "Key is null." );
        }
        File imgFile = new File( file.getParent(), key + ".png" );
        if( IKVM ) {
        	if( !imgFile.exists() ){
        		fail("No Reference value for key:" + key + NO_DATA_MSG);
        	}
            if( imgFile.length() == 0 ) {
                Assert.assertEquals( key, null, img );
                return;
            }
            BufferedImage expected = ImageIO.read( imgFile );
            File file_ikvm = new File( file.getParent(), key + "_ikvm.png" );
            file_ikvm.delete();
            if( expected == null ) {
                fail( "No Reference value for key:" + key + NO_DATA_MSG );
                return;
            }
            try {
                assertEquals( key, expected, img , delta, useMediumValue );
            } catch( Error ex ) {
                // save the IKVM result for better compare the differences
                ImageIO.write( img, "png", file_ikvm );
                throw ex;
            }
        } else {
            file.getParentFile().mkdirs();
            if( img == null ) {
                // create a empty file
                imgFile.delete();
                imgFile.createNewFile();
            } else {
                ImageIO.write( img, "png", imgFile );
            }
        }
    }
    
    /**
     * Asserts that two images are equal.
     * 
     * @param baseMsg
     *            The base message in asserts
     * @param expected
     *            The expected image
     * @param img
     *            the value will be saved in Sun VM and compared in IKVM. The value will be saved as png image. If a
     *            difference occur then a second png file will be saved which ended with _ikvm. This make it easer to
     *            see the image differences.
     * @param delta
     *            The maximum difference between hue, saturation and brightness of every pixel
     * @param useMediumValue
     *            if true then compare the medium value of 9 pixel instead of every pixel
     */
    public static void assertEquals( String baseMsg, Image expected, Image img, double delta, boolean useMediumValue ) throws Exception {
        BufferedImage bExpected = toBufferedImage( expected );
        BufferedImage bImg = toBufferedImage( img );
        Assert.assertEquals( baseMsg + " width", bExpected.getWidth(), bImg.getWidth() );
        Assert.assertEquals( baseMsg + " height", bExpected.getHeight(), bImg.getHeight() );
        float[] hsbExpected = null;
        float[] hsbCurrent = null;
        for( int x = 0; x < bExpected.getWidth(); x++ ) {
            for( int y = 0; y < bExpected.getHeight(); y++ ) {
                int rgbExpected = bExpected.getRGB( x, y );
                int rgbCurrent = bImg.getRGB( x, y );
                String pixelName = baseMsg + " pixel " + x + "," + y;
                if( delta > 0 ) {
                    hsbExpected = rgbToHSB( rgbExpected, hsbExpected );
                    hsbCurrent = rgbToHSB( rgbCurrent, hsbCurrent );
                    try {
                        assertEqualsColor( pixelName, hsbExpected, hsbCurrent, delta );
                        Assert.assertEquals( pixelName + " alpha", ((rgbExpected >> 24) & 0xFF)/255.0, ((rgbCurrent >> 24) & 0xFF)/255.0, delta);
                    } catch( Error e ) {
                        if( useMediumValue ){
                            getMediumHSB( bExpected, x, y, hsbExpected );
                            getMediumHSB( bImg, x, y, hsbCurrent );
                            try {
                                assertEqualsColor( pixelName, hsbExpected, hsbCurrent, delta );
                            } catch( Error e1 ) {
                                // throw the first exception
                                throw e;
                            }
                        } else {
                            throw e;
                        }
                    }
                } else {
                    Assert.assertEquals( baseMsg + " pixel " + x + "," + y, rgbExpected, rgbCurrent );
                }
            }
        }     
    }
    
    /**
     * COnvert any Image to a BufferedImage
     * @param img base image
     * @return a BufferedImage
     */
    public static BufferedImage toBufferedImage( Image img ){
        if( img instanceof BufferedImage ){
            return (BufferedImage)img;
        }
        BufferedImage bufImg = new BufferedImage( img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB );
        Graphics g = bufImg.getGraphics();
        g.drawImage( img, 0, 0, null );
        g.dispose();
        return bufImg;
    }
    
    /**
     * Compare 2 HSB color values with a delta
     * 
     * @param key
     *            message for assert
     * @param hsbExpected
     *            expected value
     * @param hsbCurrent
     *            current value
     * @param delta
     *            the delta
     */
    public static void assertEqualsColor( String key, float[] hsbExpected, float[] hsbCurrent, double delta ) {
        try {
            Assert.assertEquals( key + " hue", hsbExpected[0], hsbCurrent[0], delta );
        } catch( Error e ) {
            try {
                float newCurrent = hsbCurrent[0] + hsbCurrent[0] < 0.5F ? 1 : -1;
                Assert.assertEquals( key + " hue", hsbExpected[0], newCurrent, delta );
            } catch( Error e1 ) {
                // throw the first exception
                throw e;
            }
        }
        Assert.assertEquals( key + " saturation", hsbExpected[1], hsbCurrent[1], delta );
        Assert.assertEquals( key + " brightness", hsbExpected[2], hsbCurrent[2], delta );
    }
    
    /**
     * Calculate the medium of 9 pixel
     * 
     * @param img
     *            the pixel source
     * @param x
     *            the x position of the middle
     * @param y
     *            the y position of the middle
     * @param hsb
     *            the array used to return the three HSB values, or null
     * @return the hsb value
     */
    private static float[] getMediumHSB( BufferedImage img, int x, int y, float[] hsb ) {
        int r = 0, g = 0, b = 0;
        int count = 0;

        int minX = Math.max( x - 1, 0 );
        int maxX = Math.min( x + 1, img.getWidth() - 1 );
        int minY = Math.max( 0, y - 1 );
        int maxY = Math.min( y + 1, img.getHeight() - 1 );
        for( int i = minX; i <= maxX; i++ ) {
            for( int j = minY; j <= maxY; j++ ) {
                int rgb = img.getRGB( i, j );
                r += (rgb >> 16) & 0xFF;
                g += (rgb >> 8) & 0xFF;
                b += (rgb >> 0) & 0xFF;
                count++;
            }
        }
        return Color.RGBtoHSB( r / count, g / count, b / count, hsb );
    }
    
    /**
     * Converts the rgb color to an equivalent set of values for hue, saturation, and brightness that are the three
     * components of the HSB model.
     * 
     * If the hsb argument is null, then a new array is allocated to return the result. Otherwise, the method returns
     * the array hsb, with the values put into that array.
     * 
     * @param rgb
     *            the rgb value
     * @param hsb
     *            the array used to return the three HSB values, or null
     * @return the the hsb value
     */
    private static float[] rgbToHSB( int rgb, float[] hsb ) {
        return Color.RGBtoHSB( (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb) & 0xFF, hsb );
    }
    
    /**
     * Asserts that the size of the draw object in the middle of the images are equal with which come from Sun VM and IKVM run.
     * @param key The key in the reference data. It must be unique for the class that create this ReferenceData
     * @param img A Image that contain some graphics object. The border pixel must be identical color.
     */
    public void assertEqualsMetrics( String key, BufferedImage img ) throws Exception{
        int color = img.getRGB(0, 0);
        int height = img.getHeight();
        int width = img.getWidth();
        
        int y1 = 0;       
        L: for(; y1 < height; y1++){
            for(int x = 0; x < width; x++){
                if(img.getRGB(x, y1) != color){
                    break L;
                }
            }
        }
        
        int y2 = height-1;       
        L: for(; y2 >=0; y2--){
            for(int x = 0; x < width; x++){
                if(img.getRGB(x, y2) != color){
                    break L;
                }
            }
        }
        
        int x1 = 0;       
        L: for(; x1 < width; x1++){
            for(int y = 0; y < height; y++){
                if(img.getRGB(x1, y) != color){
                    break L;
                }
            }
        }
        
        int x2 = width - 1;       
        L: for(; x2 >= 0; x2--){
            for(int y = 0; y < height; y++){
                if(img.getRGB(x2, y) != color){
                    break L;
                }
            }
        }

        Assert.assertTrue( "Object not found x", x1 <= x2 );
        Assert.assertTrue( "Object not found y", y1 <= y2 );
        assertEquals( key, new Rectangle( x1, y1, x2 - x1, y2 - y1 ) );
    }

}
