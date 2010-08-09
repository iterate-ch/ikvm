/*
  Copyright (C) 2010 Volker Berlin (i-net software)

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

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class AbstractButtonTest<T extends AbstractButton>{

    protected static ReferenceData reference;


    protected abstract T createButton();


    @Test
    public void getDisabledIcon() throws Exception{
        AbstractButton button = createButton();
        button.setIcon(new ImageIcon(getClass().getResource("icon.gif")));
        Icon icon = button.getDisabledIcon();
        BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        icon.paintIcon(button, g, 0, 0);
        reference.assertEquals("getDisabledIcon", img);
    }
}
