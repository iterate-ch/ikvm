/*
  Copyright (C) 2010, 2011 Volker Berlin (i-net software)

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

import java.awt.*;
import java.awt.geom.Rectangle2D;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

public class FontMetricsTest {

    protected static ReferenceData reference;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        reference = new ReferenceData();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if( reference != null ) {
            reference.save();
            reference = null;
        }
    }

    @SuppressWarnings( "deprecation" )
    protected FontMetrics getFontMetrics() {
        Font font = new Font( "Arial", 0, 12 );
        return Toolkit.getDefaultToolkit().getFontMetrics( font );
    }

    @Test
    public void getStringBounds() throws Exception {
        FontMetrics fm = getFontMetrics();

        //not all characters has the same metrics on Java and .NET, cause is the mystic algorithms for the fixed string width in java.
        //follow character are different for the used font: m,s,x,y
        Rectangle2D.Float bounds = (Rectangle2D.Float)fm.getStringBounds( "bla abc", null );
        reference.assertEquals( "getStringBounds", bounds );
        reference.assertEquals( "getStringBounds long", (Rectangle2D.Float)fm.getStringBounds( "abc    iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii", null ) );
        reference.assertEquals( "getStringBounds empty", (Rectangle2D.Float)fm.getStringBounds( "", null ) );

        try {
            fm.getStringBounds( null, null );
            fail( "NullPointerException expected" );
        } catch( NullPointerException ex ) {
            // expected;
        }
    }

    @Test
    public void stringWidth() throws Exception {
        FontMetrics fm = getFontMetrics();
        int width = fm.stringWidth( "bla abc    iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" );
        reference.assertEquals( "stringWidth", width );
        reference.assertEquals( "stringWidth empty", fm.stringWidth( "" ) );
    }

}
