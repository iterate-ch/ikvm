/*
  Copyright (C) 2010 Karsten Heinrich (i-net software)
  Copyright (C) 2012 Volker Berlin (i-net software)

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
import java.util.ArrayList;
import java.util.Arrays;

import junit.ikvm.ReferenceData;

import org.junit.*;

public class GraphicsEnvironmentTest {

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
    
    @Ignore("Java seems to have duplicates in the list with bold and styles")
    @Test
    public void getAllFonts(){
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	Font[] fonts = ge.getAllFonts();
    	String[] families = new String[fonts.length];
    	for(int i=0; i<families.length; i++){
    		// use fontName instead of family, since there may be certain types of the same family (plain, bolt etc. )
    		families[i] = fonts[i].getFontName();
    	}
    	reference.assertEquals("getAllFonts", toString(families));
    }

    @Test
    public void getAvailableFontFamilyNames(){
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	String[] families = ge.getAvailableFontFamilyNames();
    	ArrayList<String> list = new ArrayList<String>( Arrays.asList(families) );
		list.remove("Lucida Sans Typewriter"); // a special font of the Sun Java VM
		list.remove("Lucida Bright");
		list.remove("Lucida Sans");
    	families = list.toArray(new String[list.size()]);
    	reference.assertEquals("getAvailableFontFamilyNames", toString(families));
    }

    
    private static String toString(Object obj){
        if(obj instanceof Object[]){
            Object[] data = (Object[])obj;
            String[] strings = new String[data.length];
            for(int i = 0; i < strings.length; i++){
                strings[i] = String.valueOf(data[i]);
            }
            Arrays.sort(strings);
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < strings.length; i++){
                builder.append(strings[i]);
                builder.append(",");
            }
            return builder.toString();
        } else {
            return String.valueOf(obj);
        }
    }
}
