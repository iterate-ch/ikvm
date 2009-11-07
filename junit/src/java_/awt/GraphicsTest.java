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


import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.*;

import junit.ikvm.ReferenceData;

import org.junit.*;



/**
 * @author Volker Berlin
 */
public class GraphicsTest{

    private static ReferenceData reference;

    private BufferedImage img;
    private Graphics g;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        reference = new ReferenceData(GraphicsTest.class);
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        if(reference != null){
            reference.save();
        }
    }
    
    
    @Before
    public void setUp() {
        img = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
        g = img.getGraphics();
    }
    
    
    @After
    public void tearDown (){
        g.dispose();
    }


    @Test
    public void copyArea() throws Exception{
        g.setColor(Color.RED);
        g.fillRect(0, 0, 5, 5);
        g.copyArea(1, 2, 5, 5, 3, 3);
        reference.assertEquals("copyArea", img);
    }
}
