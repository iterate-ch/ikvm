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

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.Rectangle2D;

import junit.ikvm.ReferenceData;

import org.junit.*;

public class StandardGlyphVectorTest{

    private static ReferenceData reference;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        reference = new ReferenceData();
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        if(reference != null){
            reference.save();
            reference = null;
        }
    }

    
    private StandardGlyphVector create(String text, boolean useFractional){
        Font font = new Font("Arial", 0, 12);
        FontRenderContext frc = new FontRenderContext(null, false, useFractional);
        return new StandardGlyphVector( font, text, frc );
    }

    @Test
    public void getGlyphInfo() throws Exception{
        StandardGlyphVector sgv = create( "any Text", false );
        float[] info = sgv.getGlyphInfo();
        reference.assertEquals("getGlyphInfo", info);
    }
    
    
    @Test
    public void getLogicalBounds_Fixed() throws Exception{
        StandardGlyphVector sgv = create( "any Text", false );
        Rectangle2D.Float bounds = (Rectangle2D.Float)sgv.getLogicalBounds();
        reference.assertEquals("getLogicalBounds_Fixed", bounds);
    }
    
    
    @Test
    public void getLogicalBounds_Fractional() throws Exception{
        StandardGlyphVector sgv = create( "any Text", true );
        Rectangle2D.Float bounds = (Rectangle2D.Float)sgv.getLogicalBounds();
        reference.assertEquals("getLogicalBounds_Fractional", bounds);
    }
}
