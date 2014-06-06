/*
  Copyright (C) 2009 - 2012 Volker Berlin (i-net software)

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

import java.awt.print.*;
import java.io.File;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;

import junit.ikvm.ReferenceData;

import org.junit.*;

import sun.print.RasterPrinterJob;
import static org.junit.Assert.*;


public class PrinterJobTest{

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


    @Before
    public void setUp() throws Exception{
    }


    @After
    public void tearDown() throws Exception{
    }

    
    @Test
    public void getPrinterJob(){
        PrinterJob job = PrinterJob.getPrinterJob();
        assertNotNull(job);
    }

    
    @Test
    public void lookupPrintServices(){
        PrintService[] services = PrinterJob.lookupPrintServices();
        assertNotNull(services);
        reference.assertEquals("lookupPrintServicesCount", services.length );
    }

    
    @Test
    public void getPrintService(){
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintService service = job.getPrintService();
        reference.assertEquals("getPrintService.getName", service.getName() );
    }
    
    
    @Test
    public void defaultPage(){
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pageFormat = job.defaultPage();
        reference.assertEquals("defaultPage.width", pageFormat.getWidth(), 0.35 );
        reference.assertEquals("defaultPage.height", pageFormat.getHeight(), 0.25 );
        reference.assertEquals("defaultPage.ImageableX", pageFormat.getImageableX() );
        reference.assertEquals("defaultPage.ImageableY", pageFormat.getImageableY() );
        reference.assertEquals("defaultPage.getImageableWidth", pageFormat.getImageableWidth(), 0.35 );
        reference.assertEquals("defaultPage.getImageableHeight", pageFormat.getImageableHeight(), 0.25 );
        reference.assertEquals("defaultPage.Orientation", pageFormat.getOrientation() );
    }

    
    @Test
    public void print() throws Exception{
        PrinterJob job = PrinterJob.getPrinterJob();
        HashPrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
        File file = new File("temp.prn").getAbsoluteFile();
        file.delete();
        try{
            RasterPrinterJob.debugPrint = true;
            assertFalse("exist",file.exists());
            attrs.add(new Destination(file.toURI()));
            job.setPrintable(new DummyPrintable() );
            job.print(attrs);
            assertTrue("exist",file.exists());
        }finally{
            file.delete();
        }
    }
}
