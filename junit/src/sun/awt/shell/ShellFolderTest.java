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
package sun.awt.shell;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import javax.swing.UIManager;

import junit.ikvm.ReferenceData;

import org.junit.*;

public class ShellFolderTest{

    private static ReferenceData reference;

    private static String originalLaF;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        reference = new ReferenceData(ShellFolderTest.class);
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
            reference.assertEquals("roots list names", toString(file.listFiles()));
        }
    }


    @Test
    public void fileChooserDefaultFolder() throws Exception{
        String folder = String.valueOf(ShellFolder.get("fileChooserDefaultFolder"));
        reference.assertEquals("fileChooserDefaultFolder", folder);
    }


    @Test
    public void fileChooserComboBoxFolders() throws Exception{
        String folder = String.valueOf(ShellFolder.get("fileChooserComboBoxFolders"));
        reference.assertEquals("fileChooserComboBoxFolders", folder);
    }


    @Test
    public void fileChooserShortcutPanelFolders() throws Exception{
        String folder = String.valueOf(ShellFolder.get("fileChooserShortcutPanelFolders"));
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


    private String toString(File[] files){
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
}
