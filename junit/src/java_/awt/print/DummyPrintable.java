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
package java_.awt.print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import static org.junit.Assert.*;


public class DummyPrintable implements Printable{

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException{
        assertNotNull( "Graphics", g );
        assertNotNull( "PageFormat", pageFormat );
        assertTrue( "Graphics2D", g instanceof Graphics2D );
        g.drawString("Any String", 100, 100);
        return pageIndex < 2 ? PAGE_EXISTS : NO_SUCH_PAGE;
    }

}
