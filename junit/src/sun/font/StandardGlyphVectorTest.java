/*
  Copyright (C) 2009, 2010, 2013 Volker Berlin (i-net software)

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
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

public class StandardGlyphVectorTest {

    private static ReferenceData reference;

    private static int           FONT_SIZE = 36;

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

    private StandardGlyphVector create( String text, boolean antialias, boolean useFractional ) {
        Font font = new Font( "Arial", 0, FONT_SIZE );
        FontRenderContext frc = new FontRenderContext( null, antialias, useFractional );
        return new StandardGlyphVector( font, text, frc );
    }

    @Ignore
    @Test
    public void getGlyphInfo_Fixed() throws Exception {
        StandardGlyphVector sgv = create( "any Text", false, false );
        float[] info = sgv.getGlyphInfo();
        
        reference.assertEquals( "getGlyphInfo_Fixed.length", info.length );
        for( int i = 0; i < info.length; i++ ) {
            float value = info[i];
            float delta = i % 8 >= 4 ? Math.abs( value / 100 ) : 0;
            reference.assertEquals( "getGlyphInfo_Fixed " + i, value, delta );
        }
    }

    @Test
    public void getGlyphInfo_Fractional() throws Exception {
        StandardGlyphVector sgv = create( "any Text", false, true );
        float[] info = sgv.getGlyphInfo();

        reference.assertEquals( "getGlyphInfo_Fractional.length", info.length );
        for( int i = 0; i < info.length; i++ ) {
            float value = info[i];
            float delta = i % 8 >= 4 ? Math.abs( value / 100 ) : 0;
            reference.assertEquals( "getGlyphInfo_Fractional " + i, value, delta );
        }
    }

    @Ignore
    @Test
    public void getLogicalBounds_Fixed() throws Exception {
        StandardGlyphVector sgv = create( "any Text", false, false );
        Rectangle2D.Float bounds = (Rectangle2D.Float)sgv.getLogicalBounds();
        reference.assertEquals( "getLogicalBounds_Fixed", bounds );
    }

    @Test
    public void getLogicalBounds_Fractional() throws Exception {
        StandardGlyphVector sgv = create( "any Text", false, true );
        Rectangle2D.Float bounds = (Rectangle2D.Float)sgv.getLogicalBounds();
        reference.assertEquals( "getLogicalBounds_Fractional", bounds );

        sgv = create( "any large Text with many fffffffffffffffffffffffffffffff's and some spaces    ", false, true );
        bounds = (Rectangle2D.Float)sgv.getLogicalBounds();
        reference.assertEquals( "getLogicalBounds_Fractional large", bounds );
    }

    @Ignore
    @Test
    public void getLogicalBounds_Antialias() throws Exception {
        StandardGlyphVector sgv = create( "any Text", true, false );
        Rectangle2D.Float bounds = (Rectangle2D.Float)sgv.getLogicalBounds();
        reference.assertEquals( "getLogicalBounds_Antialias", bounds );
    }

    @Test
    public void getLogicalBounds_Antialias_Fractional() throws Exception {
        StandardGlyphVector sgv = create( "any Text", true, true );
        Rectangle2D.Float bounds = (Rectangle2D.Float)sgv.getLogicalBounds();
        reference.assertEquals( "getLogicalBounds_Antialias_Fractional", bounds );

        sgv = create( "any large Text with many fffffffffffffffffffffffffffffff's and some spaces    ", true, true );
        bounds = (Rectangle2D.Float)sgv.getLogicalBounds();
        reference.assertEquals( "getLogicalBounds_Antialias_Fractional large", bounds );
    }

    @Ignore
    @Test
    public void getGlyphLogicalBounds_Fixed() throws Exception {
        StandardGlyphVector sgv = create( "any Text", false, false );
        for( int i = 0; i < sgv.getNumGlyphs(); i++ ) {
            Shape shape = sgv.getGlyphLogicalBounds( i );
            Rectangle2D.Float bounds = (Rectangle2D.Float)shape.getBounds2D();
            reference.assertEquals( "getGlyphLogicalBounds_Fixed " + i, bounds );
        }
    }

    @Test
    public void getGlyphLogicalBounds_Fractional() throws Exception {
        StandardGlyphVector sgv = create( "any Text", false, true );
        for( int i = 0; i < sgv.getNumGlyphs(); i++ ) {
            Shape shape = sgv.getGlyphLogicalBounds( i );
            Rectangle2D.Float bounds = (Rectangle2D.Float)shape.getBounds2D();
            reference.assertEquals( "getGlyphLogicalBounds_Fractional " + i, bounds );
        }
    }

    @Ignore
    @Test
    public void getVisualBounds_Fixed() throws Exception {
        StandardGlyphVector sgv = create( "any Text", false, false );
        Rectangle2D.Float bounds = (Rectangle2D.Float)sgv.getVisualBounds();

        reference.assertEquals( "getVisualBounds_Fixed.x", bounds.x, bounds.x / 100 );
        reference.assertEquals( "getVisualBounds_Fixed.y", bounds.y, -bounds.y / 100 );
        reference.assertEquals( "getVisualBounds_Fixed.width", bounds.width, bounds.width / 100 );
        reference.assertEquals( "getVisualBounds_Fixed.height", bounds.height, bounds.height / 100 );
    }

    @Test
    public void getVisualBounds_Fractional() throws Exception {
        StandardGlyphVector sgv = create( "any Text", false, true );
        Rectangle2D.Float bounds = (Rectangle2D.Float)sgv.getVisualBounds();

        reference.assertEquals( "getVisualBounds_Fractional.x", bounds.x, bounds.x / 100 );
        reference.assertEquals( "getVisualBounds_Fractional.y", bounds.y, -bounds.y / 100 );
        reference.assertEquals( "getVisualBounds_Fractional.width", bounds.width, bounds.width / 100 );
        reference.assertEquals( "getVisualBounds_Fractional.height", bounds.height, bounds.height / 100 );
    }

    @Test
    public void getGlyphVisualBounds_Fixed() throws Exception {
        StandardGlyphVector sgv = create( "any Text", false, true );
        for( int i = 0; i < sgv.getNumGlyphs(); i++ ) {
            Shape shape = sgv.getGlyphVisualBounds( i );
            Rectangle2D.Float bounds = (Rectangle2D.Float)shape.getBounds2D();
            reference.assertEquals( "getGlyphVisualBounds_Fixed.x " + i, bounds.x, bounds.x / 100 );
            reference.assertEquals( "getGlyphVisualBounds_Fixed.y " + i, bounds.y, -bounds.y / 100 );
            reference.assertEquals( "getGlyphVisualBounds_Fixed.width " + i, bounds.width, bounds.width / 100 );
            reference.assertEquals( "getGlyphVisualBounds_Fixed.height " + i, bounds.height, bounds.height / 100 );
        }
    }

    @Test
    public void getGlyphVisualBounds_Fractional() throws Exception {
        StandardGlyphVector sgv = create( "any Text", false, true );
        for( int i = 0; i < sgv.getNumGlyphs(); i++ ) {
            Shape shape = sgv.getGlyphVisualBounds( i );
            Rectangle2D.Float bounds = (Rectangle2D.Float)shape.getBounds2D();
            reference.assertEquals( "getGlyphVisualBounds_Fractional.x " + i, bounds.x, bounds.x / 100 );
            reference.assertEquals( "getGlyphVisualBounds_Fractional.y " + i, bounds.y, -bounds.y / 100 );
            reference.assertEquals( "getGlyphVisualBounds_Fractional.width " + i, bounds.width, bounds.width / 100 );
            reference.assertEquals( "getGlyphVisualBounds_Fractional.height " + i, bounds.height, bounds.height / 100 );
        }
    }

    @Test
    public void getOutline() throws Exception {
        StandardGlyphVector sgv = create( " ", false, true );
        GeneralPath shape = (GeneralPath)sgv.getOutline( 0, FONT_SIZE );
        assertTrue( "empty shape", shape.getPathIterator( null ).isDone() );

        sgv = create( "some Text", false, true );
        shape = (GeneralPath)sgv.getOutline( 0, FONT_SIZE );
        BufferedImage img = new BufferedImage( FONT_SIZE * 5, FONT_SIZE + 10, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, img.getWidth(), img.getHeight() );
        g.setColor( Color.BLUE.darker() );
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.fill( shape );
        g.dispose();
        reference.assertEquals( "getOutline_img", img, 0.17, true );
    }

    @Test
    public void getGlyphOutline() throws Exception {
        StandardGlyphVector sgv = create( " ", false, true );
        GeneralPath shape = (GeneralPath)sgv.getGlyphOutline( 0 );
        assertTrue( "empty shape getGlyphOutline", shape.getPathIterator( null ).isDone() );

        sgv = create( "some Text", false, true );
        shape = (GeneralPath)sgv.getGlyphOutline( 5, 0, FONT_SIZE );
        BufferedImage img = new BufferedImage( FONT_SIZE * 5, FONT_SIZE + 10, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, img.getWidth(), img.getHeight() );
        g.setColor( Color.BLUE.darker() );
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.fill( shape );
        g.dispose();
        reference.assertEquals( "getGlyphOutline_img", img, 0.17, true );
    }

    @Ignore
    @Test
    public void getGlyphPosition_Fixed() {
        StandardGlyphVector sgv = create( "my Text", false, false );
        for( int i = 0; i <= sgv.getNumGlyphs(); i++ ) {
            Point2D.Float point = (Point2D.Float)sgv.getGlyphPosition( i );
            reference.assertEquals( "getGlyphPosition_Fixed " + i, point );
            try {
                sgv.getGlyphPosition( sgv.getNumGlyphs() + 1 );
                fail( "ArrayIndexOutOfBoundsException expected" );
            } catch( ArrayIndexOutOfBoundsException e ) {
                // expected
            }
        }
    }

    @Test
    public void getGlyphPosition_Fractional() {
        StandardGlyphVector sgv = create( "my Text", false, true );
        for( int i = 0; i <= sgv.getNumGlyphs(); i++ ) {
            Point2D.Float point = (Point2D.Float)sgv.getGlyphPosition( i );
            reference.assertEquals( "getGlyphPosition_Fractional " + i, point );
        }
        try {
            sgv.getGlyphPosition( sgv.getNumGlyphs() + 1 );
            fail( "ArrayIndexOutOfBoundsException expected" );
        } catch( ArrayIndexOutOfBoundsException e ) {
            // expected
        }
    }

    @Ignore
    @Test
    public void getGlyphPositions_Fixed() {
        StandardGlyphVector sgv = create( "bla bla", false, false );
        float[] positions = sgv.getGlyphPositions( null );
        reference.assertEquals( "getGlyphPositions_Fixed", positions );
    }

    @Test
    public void getGlyphPositions_Fractional() {
        StandardGlyphVector sgv = create( "bla bla", false, true );
        float[] positions = sgv.getGlyphPositions( null );
        reference.assertEquals( "getGlyphPositions_Fractional", positions );
    }

    @Ignore
    @Test
    public void getGlyphPositions_sub_Fixed() {
        StandardGlyphVector sgv = create( "bla bla", false, false );
        float[] positions = sgv.getGlyphPositions( 1, 3, null );
        reference.assertEquals( "getGlyphPositions_sub_Fixed", positions );
    }

    @Test
    public void getGlyphPositions_sub_Fractional() {
        StandardGlyphVector sgv = create( "bla bla", false, true );
        float[] positions = sgv.getGlyphPositions( 1, 3, null );
        reference.assertEquals( "getGlyphPositions_sub_Fractional", positions );
    }

    @Ignore
    @Test
    public void getGlyphMetrics_Fixed() {
        StandardGlyphVector sgv = create( "xyz asd", false, false );
        for( int i = 0; i < sgv.getNumGlyphs(); i++ ) {
            GlyphMetrics metrics = sgv.getGlyphMetrics( i );
                reference.assertEquals( "getGlyphMetrics_Fixed " + i + " advance", metrics.getAdvance() );
                reference.assertEquals( "getGlyphMetrics_Fixed " + i + " type", metrics.getType() );
                Rectangle2D.Float bounds = (Rectangle2D.Float)metrics.getBounds2D();
                reference.assertEquals( "getGlyphMetrics_Fixed " + i + " bounds.x", bounds.x, bounds.x/100 );
                reference.assertEquals( "getGlyphMetrics_Fixed " + i + " bounds.y", bounds.y, -bounds.y/100 );
                reference.assertEquals( "getGlyphMetrics_Fixed " + i + " bounds.width", bounds.width, bounds.width/100 );
                reference.assertEquals( "getGlyphMetrics_Fixed " + i + " bounds.height", bounds.height, bounds.height/100 );
        }
        try {
            sgv.getGlyphMetrics( sgv.getNumGlyphs() );
            fail( "IndexOutOfBoundsException expected" );
        } catch( IndexOutOfBoundsException e ) {
            // expected
        }
    }

    @Test
    public void getGlyphMetrics_Fractional() {
        StandardGlyphVector sgv = create( "xyz asd", false, true );
        for( int i = 0; i < sgv.getNumGlyphs(); i++ ) {
            GlyphMetrics metrics = sgv.getGlyphMetrics( i );
                reference.assertEquals( "getGlyphMetrics_Fractional " + i + " advance", metrics.getAdvance() );
                reference.assertEquals( "getGlyphMetrics_Fractional " + i + " type", metrics.getType() );
                Rectangle2D.Float bounds = (Rectangle2D.Float)metrics.getBounds2D();
                reference.assertEquals( "getGlyphMetrics_Fractional " + i + " bounds.x", bounds.x, bounds.x/100 );
                reference.assertEquals( "getGlyphMetrics_Fractional " + i + " bounds.y", bounds.y, -bounds.y/100 );
                reference.assertEquals( "getGlyphMetrics_Fractional " + i + " bounds.width", bounds.width, bounds.width/100 );
                reference.assertEquals( "getGlyphMetrics_Fractional " + i + " bounds.height", bounds.height, bounds.height/100 );
        }
        try {
            sgv.getGlyphMetrics( sgv.getNumGlyphs() );
            fail( "IndexOutOfBoundsException expected" );
        } catch( IndexOutOfBoundsException e ) {
            // expected
        }
    }
}
