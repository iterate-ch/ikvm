/*
  Copyright (C) 2010 Volker Berlin (i-net software)

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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.InputStream;

import javax.swing.ImageIcon;



/**
 * @author Volker Berlin
 */
public enum SetClipboardContent{
    STRING,
    IMAGE,
    JAVA_OBJECT
    ;
    
    final static DataFlavor DATA_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class=java.lang.Object", null);
    /**
     * @param args
     */
    public static void main(String[] args){
        SetClipboardContent type = valueOf(args[0]);
        Object data = args[1];
        copyLocal( type, data );
        System.exit(0);
    }
    
    /**
     * Copy the data to the Clipboard from an external Java VM
     * @param type the type of data
     * @param data the data or the description of the data
     * @throws Exception if there any problems
     */
    static void copyExternal(SetClipboardContent type, String data) throws Exception{
        // build the process
        String jvm = new java.io.File(new java.io.File(System.getProperty("java.home"), "bin"), "java").getAbsolutePath();
        ProcessBuilder pb = new ProcessBuilder(jvm, "-cp", System.getProperty("java.class.path"), SetClipboardContent.class.getName(), type.toString(), data);
        pb.redirectErrorStream(true);
        
        // create a clipboard change listener
        final boolean[] result = new boolean[1];
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection( pb.toString() ), new ClipboardOwner(){
            @Override
            public void lostOwnership(Clipboard cb, Transferable contents){
                result[0] = true;
            }
        });
        
        // start the process and wait until finish
        Process process = pb.start();        
        InputStream input = process.getInputStream();
        while(true){
            int ch = input.read();
            if(ch == -1){
                break;
            }
        }
        process.waitFor();
        
        for(int i=0; i<1000; i++){
            Thread.sleep(1);
            if(result[0]){
                return;
            }
        }
        throw new Exception("External Clipboard was not set");
    }


    /**
     * Copy the data to the Clipboard in the current Java VM
     * @param type the type of data
     * @param data the data or the description of the data
     * @throws Exception if there any problems
     */
    static Transferable copyLocal( SetClipboardContent type, Object data ){
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable;
        switch(type){
            case STRING:
                transferable = new StringSelection( (String)data );
                break;
            case IMAGE:
                Image copyData = new ImageIcon(SetClipboardContent.class.getResource((String)data)).getImage();
                transferable = new GenericTransferable(copyData, new DataFlavor[]{DataFlavor.imageFlavor});
                break;
            case JAVA_OBJECT:
                transferable = new GenericTransferable(data, new DataFlavor[]{DATA_FLAVOR});
                break;
            default:
                throw new IllegalStateException(type.toString());

        }
        clipboard.setContents(transferable, null);
        return transferable;
    }
}
