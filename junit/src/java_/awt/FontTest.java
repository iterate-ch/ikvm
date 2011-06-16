/*
  Copyright (C) 2009 - 2011 Volker Berlin (i-net software)

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
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;

import junit.ikvm.ReferenceData;

import static org.junit.Assert.*;
import org.junit.*;



public class FontTest{
    
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

    
    @Test
    public void getStringBounds_Fixed(){
        Font font = new Font("Arial", 0, 12);
        //not all characters has the same metrics on Java and .NET, cause is the mystic algorithms for the fixed string width in java.
        //follow character are different for the used font: m,s,x,y
        Rectangle2D bounds = font.getStringBounds("unknown", new FontRenderContext(null, false, false) );
        reference.assertEquals("getStringBounds", (Rectangle2D.Float)bounds);
    }


    @Test
    public void getStringBounds_Fractional(){
        Font font = new Font("Arial", 0, 12);
        Rectangle2D bounds = font.getStringBounds("any text", new FontRenderContext(null, false, true) );
        reference.assertEquals("getStringBounds_Fixed", (Rectangle2D.Float)bounds);
    }


    @Test
    public void createFontFromFile() throws Exception{
    	String windir = System.getenv("windir");
    	File file = new File(windir + "/fonts/Arial.ttf");
    	Font font = Font.createFont(Font.TRUETYPE_FONT, file);
    	testCreatedFont( font );
    }


    @Test
    public void createFontFromStream() throws Exception{
    	String windir = System.getenv("windir");
    	File file = new File(windir + "/fonts/Arial.ttf");
    	Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(file));
    	testCreatedFont( font );
    }
    
    private void testCreatedFont(Font font) throws Exception{
        assertEquals( 1, font.getSize());
        reference.assertEquals("testCreatedFont.bounds", (Rectangle2D.Float)font.getStringBounds("any text", new FontRenderContext(null, false, true) ) );
    }
    
    @Test
    public void getFamilyAndNameLogical(){
    	getFamilyAndName(Font.DIALOG);
    	getFamilyAndName(Font.DIALOG_INPUT);
    	getFamilyAndName(Font.MONOSPACED);
    	getFamilyAndName(Font.SERIF);
    	getFamilyAndName(Font.SANS_SERIF);
    }
    
    @Test
    public void getFamilyAndNameReal(){
    	Font font = new Font("Arial Bold", 0, 1);
    	assertEquals("Arial Bold", font.getName());
    	assertEquals("Arial", font.getFamily());
    	
    	font = new Font("Arial Black Standard", 0, 1);
    	assertEquals("Arial Black Standard", font.getName());
    	assertEquals("Arial Black", font.getFamily());
    	
    }
    
    private void getFamilyAndName(String name){
    	Font font = new Font(name, 0, 1);
    	assertEquals(name, font.getName());
    	assertEquals(name, font.getFamily());
    }
}
