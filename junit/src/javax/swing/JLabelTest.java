/*
  Copyright (C) 2011 Volker Berlin (i-net software)

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
package javax.swing;


import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

public class JLabelTest{

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
    public void getPreferredSize(){
        String text = "a label text";
        JLabel label = new JLabel(text);
        Dimension size = label.getPreferredSize();
        
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(label.getFont());
        Rectangle2D bounds = fm.getStringBounds(text, label.getGraphics());
        
        // all metrics API must show the same value.
        assertEquals("getPreferredSize.Width 1", size.width, bounds.getWidth(), 0.0 );
        assertEquals("getPreferredSize.Width 2", size.width, fm.stringWidth(text) );
        
        GlyphVector gv = label.getFont().createGlyphVector(new FontRenderContext(null, false, false), text);
        assertEquals("getPreferredSize.Width 3", size.width, gv.getLogicalBounds().getWidth(), 0.0 );
    }


}
