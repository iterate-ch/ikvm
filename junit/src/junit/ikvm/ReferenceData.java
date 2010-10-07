/*
  Copyright (C) 2009, 2010 Volker Berlin (i-net software)

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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.junit.Assert;

import static org.junit.Assert.fail;

/**
 * RefererenceData to compare. With Sun Java it will save the data. With IKVM it will read the data.
 */
public class ReferenceData{

    private static final boolean IKVM = System.getProperty("java.vm.name").equals("IKVM.NET");

    private static final String NO_DATA_MSG = " Please run the test first with a Sun Java VM to create reference data for your system.";

    private final HashMap<String, Serializable> data;

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
            data = (HashMap<String, Serializable>)ois.readObject();
            fis.close();
        }else{
            data = new HashMap<String, Serializable>();
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
            Object expected = data.get(key);
            if(expected == null && !data.containsKey(key)){
                fail("No Reference value for key:" + key + NO_DATA_MSG);
            }
            if(expected instanceof float[]){
                Assert.assertArrayEquals(key, (float[])expected, (float[])value, 0.0F);
            }
            Assert.assertEquals(key, expected, value);
        }else{
            data.put(key, value);
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
        assertEquals( key, img, 0.0 );
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
     * @param delta
     *            The maximum difference between hue, saturation and brightness of every pixel
     */
    public void assertEquals( String key, BufferedImage img, double delta ) throws Exception {
        if(key == null){
            fail("Key is null.");
        }
        File imgFile = new File(file.getParent(), key + ".png");
        if(IKVM){
            if( imgFile.length() == 0 ){
                Assert.assertEquals( key, null, img );
            }
            BufferedImage expected = ImageIO.read( imgFile );
            File file_ikvm = new File(file.getParent(), key + "_ikvm.png");
            file_ikvm.delete();
            if(expected == null){
                fail("No Reference value for key:" + key + NO_DATA_MSG);
                return;
            }
            try{
                Assert.assertEquals(key + " width", expected.getWidth(), img.getWidth());
                Assert.assertEquals(key + " height", expected.getHeight(), img.getHeight());
                float[] hsbExpected = null;
                float[] hsbCurrent = null;
                for(int x = 0; x < expected.getWidth(); x++){
                    for( int y = 0; y < expected.getHeight(); y++ ) {
                        int rgbExpected = expected.getRGB( x, y );
                        int rgbCurrent = img.getRGB( x, y );
                        if( delta > 0 ) {
                            hsbExpected =
                                            Color.RGBtoHSB( (rgbExpected >> 16) & 0xFF, (rgbExpected >> 8) & 0xFF, (rgbExpected) & 0xFF, hsbExpected );
                            hsbCurrent =
                                            Color.RGBtoHSB( (rgbCurrent >> 16) & 0xFF, (rgbCurrent >> 8) & 0xFF, (rgbCurrent) & 0xFF, hsbCurrent );
                            try {
                                Assert.assertEquals( key + " pixel hue " + x + "," + y, hsbExpected[0], hsbCurrent[0], delta );
                            } catch( Error e ) {
                                try {
                                    float newCurrent = hsbCurrent[0] + hsbCurrent[0] < 0.5F ? 1 : -1;
                                    Assert.assertEquals( key + " pixel hue " + x + "," + y, hsbExpected[0], newCurrent, delta );
                                } catch( Exception e1 ) {
                                    // throw the first exception
                                    throw e;
                                }
                            }
                            Assert.assertEquals( key + " pixel saturation " + x + "," + y, hsbExpected[1], hsbCurrent[1], delta );
                            Assert.assertEquals( key + " pixel brightness " + x + "," + y, hsbExpected[2], hsbCurrent[2], delta );
                        } else {
                            Assert.assertEquals( key + " pixel " + x + "," + y, rgbExpected, rgbCurrent );
                        }
                    }
                }
            }catch(Error ex){
                // save the IKVM result for better compare the differences
                ImageIO.write(img, "png", file_ikvm);
                throw ex;
            }
        }else{
        	file.getParentFile().mkdirs();
        	if( img == null ){
        	    // create a empty file
        	    imgFile.delete();
        	    imgFile.createNewFile();
        	} else {
        	    ImageIO.write( img, "png", imgFile );
        	}
        }
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
