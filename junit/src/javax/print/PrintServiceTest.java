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
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.*;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

public class PrintServiceTest{

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
    public void createPrintJob(){
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob job = service.createPrintJob();
        assertNotNull( job );
    }
    
    
    @Test
    public void getAttribute(){
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        assertGetAttribute(service, ColorSupported.class);
        assertGetAttribute(service, PagesPerMinute.class);
        assertGetAttribute(service, PagesPerMinuteColor.class);
        assertGetAttribute(service, PDLOverrideSupported.class);
        assertGetAttribute(service, PrinterInfo.class);
        assertGetAttribute(service, PrinterIsAcceptingJobs.class);
        assertGetAttribute(service, PrinterLocation.class);
        assertGetAttribute(service, PrinterMakeAndModel.class);
        assertGetAttribute(service, PrinterMessageFromOperator.class);
        assertGetAttribute(service, PrinterMoreInfo.class);
        assertGetAttribute(service, PrinterMoreInfoManufacturer.class);
        assertGetAttribute(service, PrinterName.class);
        assertGetAttribute(service, PrinterState.class);
        assertGetAttribute(service, PrinterStateReasons.class);
        assertGetAttribute(service, PrinterURI.class);
        assertGetAttribute(service, QueuedJobCount.class);
    }


    private void assertGetAttribute(PrintService service, Class<? extends PrintServiceAttribute> category){
        reference.assertEquals(category.getName(), service.getAttribute(category));
    }
    
    
    @Test
    public void getAttributes(){
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        Attribute[] attrs = service.getAttributes().toArray();
        String[] strings = new String[attrs.length];
        for(int i = 0; i < strings.length; i++){
            strings[i] = attrs[i].getClass().getName() + ":" + attrs[i].toString();
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
    public void getDefaultAttributeValue(){
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        Class<?>[] categories = service.getSupportedAttributeCategories();
        for(int i = 0; i < categories.length; i++){
            Class clazz = categories[i];
            Object attribute = service.getDefaultAttributeValue(clazz);
            reference.assertEquals("DefaultAttribute-"+clazz.getName(), String.valueOf(attribute));
        }
    }
    
    
    @Test
    public void getName(){
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        reference.assertEquals("getName", service.getName() );
    }
    
    
    @Test
    public void getSupportedAttributeCategories(){
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        Class<?>[] categories = service.getSupportedAttributeCategories();
        String[] strings = new String[categories.length];
        for(int i = 0; i < strings.length; i++){
            strings[i] = categories[i].getName();
        }
        Arrays.sort(strings);
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < strings.length; i++){
            builder.append(strings[i]);
            builder.append(",");
        }
        reference.assertEquals("getSupportedAttributeCategories", builder.toString() );
    }
    
    @Ignore("javax.print.attribute.standard.MediaPrintableArea is not implemented")
    @Test
    public void getSupportedAttributeValues(){
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        Class<?>[] categories = service.getSupportedAttributeCategories();
        for(int i = 0; i < categories.length; i++){
            Class<? extends Attribute> category = (Class<? extends Attribute>)categories[i];
            Object obj = service.getSupportedAttributeValues(category, null, null);
            reference.assertEquals("SupportedValues-"+category, toString(obj));
        }
    }
    
    
    @Test
    public void isDocFlavorSupported(){
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        reference.assertEquals("DocFlavorPAGEABLE", service.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE) );
        reference.assertEquals("DocFlavorPRINTABLE", service.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE) );
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
