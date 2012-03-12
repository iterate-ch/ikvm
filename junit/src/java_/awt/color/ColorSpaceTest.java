/*
  Copyright (C) 2010 - 2012 Volker Berlin (i-net software)

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
package java_.awt.color;

import java.awt.Color;
import java.awt.color.*;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author Volker Berlin
 */
public class ColorSpaceTest {

    protected static ReferenceData reference;

    //The 2 ColorSpace does not work correctly because bad profile files (*.pf). With the original profile files it is better.
    private static int[] COLOR_SPACES = { ColorSpace.CS_sRGB, ColorSpace.CS_CIEXYZ, /*ColorSpace.CS_PYCC,*/ ColorSpace.CS_GRAY, /*ColorSpace.CS_LINEAR_RGB*/ };

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        reference = new ReferenceData();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if( reference != null ) {
            reference.save();
        }
        reference = null;
    }

    @Test
    public void convertRGB() {
        for( int colorspace : COLOR_SPACES ) {
            ColorSpace cs = ColorSpace.getInstance( colorspace );
            float[] rgb = { 0.9F, 0.5F, 0.2F };
            float[] color = cs.fromRGB( rgb );
            reference.assertEquals( "convertRGB.fromRGB model:" + colorspace, color, 0.02F );
            rgb = cs.toRGB( color );
            reference.assertEquals( "convertRGB.toRGB model:" + colorspace, rgb, 0.02F );
        }
    }

    @Test
    public void convertCIEXYZ() {
        for( int colorspace : COLOR_SPACES ) {
            ColorSpace cs = ColorSpace.getInstance( colorspace );
            float[] xyz = { 0.9F, 0.5F, 0.2F };
            float[] color = cs.fromCIEXYZ( xyz );
            reference.assertEquals( "convertCIEXYZ.fromCIEXYZ model:" + colorspace, color, 0.02F );
            xyz = cs.toCIEXYZ( color );
            reference.assertEquals( "convertCIEXYZ.toCIEXYZ model:" + colorspace, xyz, 0.02F );
        }
    }

    @Ignore("java.awt.color.CMMException: Not implemented")
    @Test
    public void customColorModel(){
        // Reloading a profile from the profile data
        ICC_Profile profile = ICC_Profile.getInstance(ColorSpace.CS_GRAY);
        profile = ICC_Profile.getInstance( profile.getData() );
        ICC_ColorSpace cs = new ICC_ColorSpace (profile);
        java.awt.image.ColorModel model = new java.awt.image.ComponentColorModel( cs, new int[]{1}, false, false, 1, 0 );
        reference.assertEquals( "customColorModel.getPixelSize", model.getPixelSize() );
    }
    
    @Test
    public void isCS_sRGB(){
        assertTrue( ColorSpace.getInstance( ColorSpace.CS_sRGB ).isCS_sRGB() );
        assertFalse( ColorSpace.getInstance( ColorSpace.CS_GRAY ).isCS_sRGB() );
    }
    
    @Test 
    public void newColor(){
        Color c = new Color( ColorSpace.getInstance( ColorSpace.CS_sRGB ), new float[] { 0.2f, 0.4f, 0.6f }, 0.8f );
        reference.assertEquals( "color.red", c.getRed(), 1 );
        reference.assertEquals( "color.blue", c.getBlue(), 1 );
        reference.assertEquals( "color.green", c.getGreen(), 1 );
        reference.assertEquals( "color.alpha", c.getAlpha() );
    }
}
