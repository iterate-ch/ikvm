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

import javax.print.attribute.AttributeSet;


public class DummyPrintServiceLookup extends PrintServiceLookup{

    @Override
    public PrintService getDefaultPrintService(){
        return null;
    }


    @Override
    public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] flavors, AttributeSet attributes){
        return new MultiDocPrintService[]{new DummyMultiDocPrintService()};
    }


    @Override
    public PrintService[] getPrintServices(DocFlavor flavor, AttributeSet attributes){
        return new PrintService[0];
    }


    @Override
    public PrintService[] getPrintServices(){
        return new PrintService[0];
    }

    private static class DummyMultiDocPrintService extends DummyPrintService implements MultiDocPrintService{

        @Override
        public MultiDocPrintJob createMultiDocPrintJob(){
            return null;
        }
        
    }
}
