/*
  Copyright (C) 20014 Volker Berlin (i-net software)

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
package com.sun.management;

import java.lang.management.ManagementFactory;

import junit.ikvm.ReferenceData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;

public class OperatingSystemMXBeanTest {

    private static ReferenceData reference;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        reference = new ReferenceData();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if( reference != null ) {
            reference.save();
        }
    }

    @Test
    public void getTotalPhysicalMemorySize() {
        OperatingSystemMXBean bean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        reference.assertEquals( "TotalPhysicalMemorySize", bean.getTotalPhysicalMemorySize() );
    }

    @Test
    public void getFreePhysicalMemorySize() {
        OperatingSystemMXBean bean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        long free = bean.getFreePhysicalMemorySize();
        assertTrue( free > 0 );
        assertTrue( free < bean.getTotalPhysicalMemorySize() );
    }
}
