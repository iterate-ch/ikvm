/*
 * Created on 28.10.2009
 */
package java_.awt.datatransfer;

import java.awt.datatransfer.*;
import java.io.IOException;



/**
 * @author Volker Berlin
 */
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
