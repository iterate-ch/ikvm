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
package javax.print;


import java.util.Arrays;

import javax.print.attribute.Attribute;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

public class PrintServiceTest{

    private static ReferenceData reference;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        reference = new ReferenceData(PrintServiceTest.class);
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        if(reference != null){
            reference.save();
        }
    }

    
    @Test
    public void createPrintJob(){
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob job = service.createPrintJob();
        assertNotNull( job );
    }
    
    
    @Test
    public void getAttributes(){
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        Attribute[] attrs = service.getAttributes().toArray();
        String[] strings = new String[attrs.length];
        for(int i = 0; i < strings.length; i++){
            strings[i] = attrs[i].toString();
        }
        Arrays.sort(strings);
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < strings.length; i++){
            builder.append(strings[i]);
            builder.append(",");
        }
        reference.assertEquals("getAttributes", builder.toString() );
    }
    
    
    @Test
    public void isDocFlavorSupported(){
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        reference.assertEquals("DocFlavorPAGEABLE", service.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE) );
        reference.assertEquals("DocFlavorPRINTABLE", service.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE) );
    }
}
