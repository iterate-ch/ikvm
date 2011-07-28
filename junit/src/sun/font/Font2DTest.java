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
package sun.font;

import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Method;

import junit.ikvm.ReferenceData;

import org.junit.*;

public class Font2DTest{

    private static ReferenceData reference;

    private static Font font;

    private static Font2D font2D;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        reference = new ReferenceData();
        font = new Font("Arial", 0, 12);
        Method getFont2D = font.getClass().getDeclaredMethod( "getFont2D" );
        getFont2D.setAccessible( true );
        font2D = (Font2D)getFont2D.invoke( font );
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        if(reference != null){
            reference.save();
            reference = null;
            font = null;
            font2D = null;
        }
    }


    @Test
    public void getFontMetrics_1() throws Exception{
        float[] result = new float[4];
        font2D.getFontMetrics(font, new FontRenderContext(null, false, false), result);
        for(int i = 0; i < result.length; i++){
            reference.assertEquals("getFontMetrics_1." + i, result[i]);
        }
    }


    @Test
    public void getFontMetrics_2() throws Exception{
        float[] result = new float[8];
        font2D.getFontMetrics(font, new AffineTransform(), RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON, result);
        for(int i = 0; i < result.length; i++){
            reference.assertEquals("getFontMetrics_2." + i, result[i]);
        }
    }
}
