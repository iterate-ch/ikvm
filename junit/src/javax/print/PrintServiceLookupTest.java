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

import javax.print.DocFlavor.SERVICE_FORMATTED;
import javax.print.attribute.HashAttributeSet;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

public class PrintServiceLookupTest{

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
    public void lookupDefaultPrintService(){
        PrintService ps = PrintServiceLookup.lookupDefaultPrintService();
        String str = ps.toString();
        reference.assertEquals("defaultToString", str);
    }
    
    
    /**
     * Create a String representation that can be saved for compare.
     */
    private String toString(PrintService[] services){
        String[] names = new String[services.length];
        for(int i = 0; i < names.length; i++){
            names[i] = services[i].getName();
        }
        Arrays.sort(names); //Sorting because the order is unimportant
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < names.length; i++){
            builder.append(names[i]);
            builder.append(",");
        }
        return builder.toString();
    }
    
    
    @Test
    public void lookupPrintServices(){
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        assertNotNull( services );
        reference.assertEquals("lookupPrintServices1Count", services.length );
        reference.assertEquals("lookupPrintServices1Names", toString(services) );

        services = PrintServiceLookup.lookupPrintServices(SERVICE_FORMATTED.PAGEABLE, new HashAttributeSet());
        assertNotNull( services );
        reference.assertEquals("lookupPrintServices2Count", services.length );
        reference.assertEquals("lookupPrintServices2Names", toString(services) );

        services = PrintServiceLookup.lookupPrintServices(SERVICE_FORMATTED.PRINTABLE, new HashAttributeSet());
        assertNotNull( services );
        reference.assertEquals("lookupPrintServices3Count", services.length );
        reference.assertEquals("lookupPrintServices3Names", toString(services) );

        PrintService service = new DummyPrintService();
        PrintService[] oldServices = PrintServiceLookup.lookupPrintServices(null, null);
        boolean registered = PrintServiceLookup.registerService(service);
        assertTrue("registered 1", registered);
        registered = PrintServiceLookup.registerService(service);
        assertFalse("registered 2", registered);
        PrintService[] newServices = PrintServiceLookup.lookupPrintServices(null, null);
        assertEquals("Service count", oldServices.length + 1, newServices.length );
        
        registered = false;
        for(int i = 0; i < newServices.length; i++){
            if(newServices[i] == service){
                registered = true;
            }
        }
        assertTrue("registered 3", registered);
    }
    
    
    @Test
    public void lookupMultiDocPrintServices(){
        MultiDocPrintService[] services = PrintServiceLookup.lookupMultiDocPrintServices(null, null);
        assertNotNull( services );
        reference.assertEquals("lookupMultiDocPrintServicesCount", services.length );
        reference.assertEquals("lookupMultiDocPrintServicesNames", toString(services) );
        
        PrintServiceLookup.registerServiceProvider(new DummyPrintServiceLookup());
        
        MultiDocPrintService[] newServices = PrintServiceLookup.lookupMultiDocPrintServices(null, null);
        assertEquals("Service count", services.length + 1, newServices.length);
    }
}
