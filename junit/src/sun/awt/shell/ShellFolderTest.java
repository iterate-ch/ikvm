/*
  Copyright (C) 2009 Volker Berlin (i-net software)
  Copyright (C) 2010 Karsten Heinrich (i-net software)

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
package sun.awt.shell;

import static sun.awt.shell.Win32ShellFolder2.DRIVES;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileSystemView;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.ikvm.ReferenceData;
import junit.textui.TestRunner;

import org.junit.*;

public class ShellFolderTest {

    private static ReferenceData reference;

    private static String originalLaF;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        reference = new ReferenceData();
        originalLaF = UIManager.getLookAndFeel().getClass().getName();
        String laf = UIManager.getSystemLookAndFeelClassName();
        UIManager.setLookAndFeel(laf);
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        if(reference != null){
            reference.save();
        }
        UIManager.setLookAndFeel(originalLaF);
    }


    @Test
    public void getRoots() throws Exception{
        File[] roots = (File[])ShellFolder.get("roots");
        reference.assertEquals("roots count", roots.length);
        reference.assertEquals("roots names", toString(roots));
        for(int i = 0; i < roots.length; i++){
            File file = roots[i];
            reference.assertEquals("roots list names " + file, toString(file.listFiles()));
        }
    }

    private String objectToString( Object obj ){
    	if( !obj.getClass().isArray() ){
    		return String.valueOf( obj );
    	} else {
    		Object[] array = (Object[])obj;
    		String[] toString = new String[array.length];
    		for( int i=0; i < array.length; i++ ){
    			toString[i] = String.valueOf( array[i] );
    		}
    		return Arrays.toString(toString);
    	}
    }
    

    @Test
    public void fileChooserDefaultFolder() throws Exception{
        String folder = objectToString(ShellFolder.get("fileChooserDefaultFolder"));
        reference.assertEquals("fileChooserDefaultFolder", folder);
    }


    @Test
    public void fileChooserComboBoxFolders() throws Exception{    	
    	// Desktop
    	// Computer
    	// Drives
    	// Network
    	// home
    	// public
    	ShellFolder desktop = (ShellFolder)((File[])ShellFolder.get("roots"))[0];
    	ShellFolder drives = (ShellFolder)((File[])ShellFolder.get("fileChooserShortcutPanelFolders"))[3];

    	ArrayList<File> folders = new ArrayList<File>();
        folders.add(desktop);
        // Add all second level folders
        File[] secondLevelFolders = drives.listFiles();
        Arrays.sort(secondLevelFolders);
        for (File secondLevelFolder : secondLevelFolders) {
        	ShellFolder folder = (ShellFolder) secondLevelFolder;
            if (!folder.isFileSystem() || folder.isDirectory()) {
                folders.add(folder);
                // Add third level for "My Computer"
                if (folder.equals(drives)) {
                    File[] thirdLevelFolders = folder.listFiles();
                    if (thirdLevelFolders != null) {
                        for (File thirdLevelFolder : thirdLevelFolders) {
                            folders.add(thirdLevelFolder);
                        }
                    }
                }
            }
        }
        String folder = objectToString(ShellFolder.get("fileChooserComboBoxFolders"));
        reference.assertEquals("fileChooserComboBoxFolders", folder);
    }


    @Test
    public void fileChooserShortcutPanelFolders() throws Exception{
        String folder = objectToString(ShellFolder.get("fileChooserShortcutPanelFolders"));
        reference.assertEquals("fileChooserShortcutPanelFolders", folder);
    }


    @Test
    public void fileChooserIcon_ListView() throws Exception{
        BufferedImage icon = (BufferedImage)ShellFolder.get("fileChooserIcon ListView");
        reference.assertEquals("fileChooserIcon ListView", icon);
    }


    @Test
    public void fileChooserIcon_DetailsView() throws Exception{
        BufferedImage icon = (BufferedImage)ShellFolder.get("fileChooserIcon DetailsView");
        reference.assertEquals("fileChooserIcon DetailsView", icon);
    }


    @Test
    public void fileChooserIcon_UpFolder() throws Exception{
        BufferedImage icon = (BufferedImage)ShellFolder.get("fileChooserIcon UpFolder");
        reference.assertEquals("fileChooserIcon UpFolder", icon);
    }


    @Test
    public void fileChooserIcon_NewFolder() throws Exception{
        BufferedImage icon = (BufferedImage)ShellFolder.get("fileChooserIcon NewFolder");
        reference.assertEquals("fileChooserIcon NewFolder", icon);
    }


    @Test
    public void fileChooserIcon_nn() throws Exception{
        for(int i = 0; i < 47; i++){
            String key = "fileChooserIcon " + i;
            BufferedImage icon = (BufferedImage)ShellFolder.get(key);
            reference.assertEquals(key, icon);
        }
    }


    @Test
    public void shell32Icon_nn() throws Exception{
        int i = 1;
        while(true){
            String key = "shell32Icon " + i++;
            BufferedImage icon = (BufferedImage)ShellFolder.get(key);
            if(icon == null){
                break;
            }
            reference.assertEquals(key, icon);
        }
        reference.assertEquals("shell32Icon count", i);
    }


    private static String toString(File[] files){
        String[] names = new String[files.length];
        for(int i = 0; i < files.length; i++){
            File file = files[i];
            names[i] = file.toString();
        }
        Arrays.sort(names);
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < names.length; i++){
            builder.append(names[i]);
            builder.append(",");
        }
        return builder.toString();
    }
    
    @Test
    public void isFileSystemRoot() throws Throwable{
    	// assert that the drives list is complete
    	Class<?> clazz = Class.forName("sun.awt.shell.Win32ShellFolder2");
    	Constructor ctr = (Constructor) clazz.getDeclaredConstructor( int.class );
    	ctr.setAccessible(true);
    	Object drives = ctr.newInstance(Integer.valueOf( Win32ShellFolder2.DRIVES ) );
    	Method[] methods = clazz.getDeclaredMethods();
    	Method m = clazz.getDeclaredMethod( "listFiles", boolean.class );
    	m.setAccessible(true);
    	File[] files = (File[]) m.invoke(drives, Boolean.TRUE );    	
    	for( int i = 0; i < files.length; i++ ){
    		reference.assertEquals( "drive#"+i, files[i].getPath() );
    	}
    	
    	// check that c:\ is identified as a fs-root - should work, if the drives list contains c:\
    	File f = new File("C:\\");
    	boolean result = new Win32ShellFolderManager2().isFileSystemRoot(f);
    	reference.assertEquals( "isFileSystemRoot", Boolean.valueOf( result ) );
    }
}
