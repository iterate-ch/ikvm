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
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * To run this compatibility test suite you need to call first the follow line to create the reference data: <br>
 *      java -cp junit-4.7.jar;. org.junit.runner.JUnitCore AllTests
 * <p> and then call: <br>
 *      ikvm -cp junit-4.7.jar;. org.junit.runner.JUnitCore AllTests
 *      
 * <p> Of course you can also run this in an IDE like Eclipse. If you want run this compatibility test suite
 * with any other Java VM as the Java SE and the IKVM then you can use the command line parameter
 * -Dreference=true and reference=false to mark the one Java VM as reference VM and the other as to test Java VM.  
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	cacar.CompareAvailableClassesAndResources.class,
	com.sun.management.AllTests.class,
    java_.awt.AllTests.class,
    java_.awt.datatransfer.AllTests.class,
    java_.awt.color.AllTests.class,
    java_.awt.font.AllTests.class,
    java_.awt.image.AllTests.class,
    java_.awt.print.AllTests.class,
    java_.lang.management.ThreadInfoTest.class,
    java_.net.AllTests.class,
    java_.text.AllTests.class,
    java_.util.prefs.AllTests.class,
    javax.imageio.ImageIOTest.class,
    javax.print.AllTests.class,
    javax.swing.AllTests.class,
    sun.awt.shell.AllTests.class,
    sun.font.AllTests.class,
    sun.misc.AllTests.class,
})
public class AllTests{
    //Nothing
}
