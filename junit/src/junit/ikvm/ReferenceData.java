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
package junit.ikvm;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

import javax.imageio.ImageIO;

import junit.framework.Assert;
import static junit.framework.Assert.fail;

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
    public void assertEquals(String key, BufferedImage img) throws Exception{
        if(key == null){
            fail("Key is null.");
        }
        if(IKVM){
            BufferedImage expected = ImageIO.read(new File(file.getParent(), key + ".png"));
            File file_ikvm = new File(file.getParent(), key + "_ikvm.png");
            file_ikvm.delete();
            if(expected == null){
                fail("No Reference value for key:" + key + NO_DATA_MSG);
                return;
            }
            try{
                Assert.assertEquals(key + " width", expected.getWidth(), img.getWidth());
                Assert.assertEquals(key + " height", expected.getHeight(), img.getHeight());
                for(int x = 0; x < expected.getWidth(); x++){
                    for(int y = 0; y < expected.getHeight(); y++){
                        Assert.assertEquals(key + " pixel " + x + "," + y, expected.getRGB(x, y), img.getRGB(x, y));
                    }
                }
            }catch(Error ex){
                // save the IKVM result for better compare the differences
                ImageIO.write(img, "png", file_ikvm);
                throw ex;
            }
        }else{
            ImageIO.write(img, "png", new File(file.getParent(), key + ".png"));
        }
    }

}
