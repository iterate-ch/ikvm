/*
  Copyright (C) 2010 Volker Berlin (i-net software)

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
package java_.net;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;

import javax.swing.JPanel;

import junit.ikvm.ReferenceData;

import org.junit.*;



/**
 * @author Volker Berlin
 */
public class URLConnectionTest{

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
    public void getContent_gif() throws Exception{
        URL url = getClass().getResource("/javax/swing/icon.gif");
        getContent(url);
    }
    
    @Test
    public void getContent_gif2() throws Exception{
        URL url = getClass().getResource("/javax/imageio/red.gif");
        getContent(url);
    }
    
    @Test
    public void getContent_jpg() throws Exception{
        URL url = getClass().getResource("/javax/imageio/red.jpg");
        getContent(url);
    }
    
    @Test
    public void getContent_png() throws Exception{
        URL url = getClass().getResource("/javax/imageio/red.png");
        getContent(url);
    }
    

    private void getContent(URL url) throws Exception{
        String baseKey = url.getPath().replace('/', '.').replace(':', '_');
        URLConnection conn = url.openConnection();
        String contentType = conn.getContentType();
        reference.assertEquals(baseKey + ".getContent.type", contentType);
        java.awt.image.ImageProducer ip = (java.awt.image.ImageProducer)conn.getContent();
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        Image img = tk.createImage(ip);
        MediaTracker mt = new MediaTracker(new JPanel());
        mt.addImage(img, 0);
        mt.waitForAll();
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        reference.assertEquals(baseKey + ".getContent.width", width);
        reference.assertEquals(baseKey + ".getContent.height", height);
        BufferedImage bufimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        reference.assertEquals(baseKey + ".getContent.image", bufimg);
    }
}
