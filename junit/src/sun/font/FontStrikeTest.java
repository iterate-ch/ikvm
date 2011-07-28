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
package sun.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.lang.reflect.Method;

import junit.ikvm.ReferenceData;

import org.junit.*;

public class FontStrikeTest{

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


    private FontStrike getFontStrike(Font font, FontRenderContext frc) throws Exception{
        Method getFont2D = font.getClass().getDeclaredMethod( "getFont2D" );
        getFont2D.setAccessible( true );
        Font2D font2D = (Font2D)getFont2D.invoke( font );
        return font2D.getStrike(font, frc);
    }

    @Test
    public void getCharMetrics() throws Exception {
        Font font = new Font( "Dialog", 0, 12 );
        FontRenderContext frc = new FontRenderContext( null, false, false );
        FontStrike strike = getFontStrike( font, frc );
        Method method = strike.getClass().getDeclaredMethod( "getCharMetrics", Character.TYPE );
        method.setAccessible( true );
        Point2D.Float value = (Point2D.Float)method.invoke( strike, 'a' );
        reference.assertEquals( "getCharMetrics", value );
    }
    
    @Test
    public void getCodePointAdvance() throws Exception {
        Font font = new Font( "Dialog", 0, 12 );
        FontRenderContext frc = new FontRenderContext( null, false, false );
        FontStrike strike = getFontStrike( font, frc );
        Method method = strike.getClass().getDeclaredMethod( "getCodePointAdvance", Integer.TYPE );
        method.setAccessible( true );
        Float value = (Float)method.invoke( strike, 'a' );
        reference.assertEquals( "getCodePointAdvance", value );
    }
    
    @Test
    public void getGlyphMetrics() throws Exception{
        Font font = new Font("Dialog", 0, 12);
        FontRenderContext frc = new FontRenderContext(null, false, false);
        FontStrike strike = getFontStrike( font,  frc);
        Method method = strike.getClass().getDeclaredMethod("getGlyphMetrics", Integer.TYPE );
        method.setAccessible(true);
        Point2D.Float value = (Point2D.Float)method.invoke(strike, 'a');
        reference.assertEquals("getGlyphMetrics", value);
    }
}
