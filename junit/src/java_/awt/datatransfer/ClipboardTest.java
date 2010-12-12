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
package java_.awt.datatransfer;


import java.awt.*;
import java.awt.datatransfer.*;

import junit.ikvm.ReferenceData;

import org.junit.*;

import static org.junit.Assert.*;


public class ClipboardTest{

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
    public void copyPasteJavaObject() throws Exception{
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Object copyData = new Object();
        Transferable data = new JavaTransferable( copyData );
        clipboard.setContents( data, null );
        
        
        Transferable transferable = clipboard.getContents(null);
        checkDataFlavorClass(transferable);
        assertTrue( transferable.isDataFlavorSupported( JavaTransferable.DATA_FLAVOR ) );
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        assertEquals(1, flavors.length);

        Object pasteData = transferable.getTransferData(JavaTransferable.DATA_FLAVOR);
        assertSame( copyData, pasteData );
    }
    
    @Test
    public void copyPasteString() throws Exception{
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String copyData = "Any Text";
        StringSelection data = new StringSelection("Any Text");
        clipboard.setContents(data, null);
        
        
        Transferable transferable = clipboard.getContents(null);
        checkDataFlavorClass(transferable);
        assertTrue(transferable.isDataFlavorSupported(DataFlavor.stringFlavor));
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        reference.assertEquals( "copyPasteString.flavor count", flavors.length );

        Object pasteData = transferable.getTransferData(DataFlavor.stringFlavor);
        assertEquals( copyData, pasteData );
        assertNotSame( copyData, pasteData );
    }
    
    /**
     * Check if the transfer data can be cast the class of the DataFlavor.
     * @param transferable a transferable from the clipboard
     * @throws Exception should never occur
     */
    private void checkDataFlavorClass(Transferable transferable) throws Exception{
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        for(DataFlavor dataFlavor : flavors){
            Class clazz = dataFlavor.getRepresentationClass();
            Object pasteData = transferable.getTransferData(dataFlavor);
            assertTrue(dataFlavor.toString(), clazz.isInstance(pasteData));
        }
    }
}
