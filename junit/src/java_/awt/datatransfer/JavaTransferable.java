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
package java_.awt.datatransfer;

import java.awt.datatransfer.*;



public class JavaTransferable implements Transferable{

    public final static DataFlavor DATA_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+";class=java.lang.Object", null);
    private final Object data;
    
    public JavaTransferable(Object data){
        this.data = data;
    }
    
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException{
        if(DATA_FLAVOR.equals(flavor)){
            return data;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }


    @Override
    public DataFlavor[] getTransferDataFlavors(){
        return new DataFlavor[]{DATA_FLAVOR};
    }


    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor){
        return DATA_FLAVOR.equals(flavor);
    }

}
