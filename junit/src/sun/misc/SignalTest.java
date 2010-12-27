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
package sun.misc;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author Volker Berlin
 */
public class SignalTest{

    private static ReferenceData reference;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        reference = new ReferenceData();
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        if(reference != null){
            reference.save();
            reference = null;
        }
    }


    @Test
    public void signalBREAK(){
        testSignal("BREAK");
    }


    @Test
    public void signalSEGV(){
        testSignal("SEGV");
    }


    @Test
    public void signalILL(){
        testSignal("ILL");
    }


    @Test
    public void signalFPE(){
        testSignal("FPE");
    }


    @Test
    public void signalABRT(){
        testSignal("ABRT");
    }


    @Test
    public void signalINT(){
        testSignal("INT");
    }


    @Test
    public void signalTERM(){
        testSignal("TERM");
    }


    @Test
    public void signalBUS(){
        testSignal("BUS");
    }


    @Test
    public void signalCPU(){
        testSignal("CPU");
    }


    @Test
    public void signalFSZ(){
        testSignal("FSZ");
    }


    @Test
    public void signalHUP(){
        testSignal("HUP");
    }


    @Test
    public void signalUSR1(){
        testSignal("USR1");
    }


    @Test
    public void signalQUIT(){
        testSignal("QUIT");
    }


    @Test
    public void signalTRAP(){
        testSignal("TRAP");
    }


    @Test
    public void signalPIPE(){
        testSignal("PIPE");
    }


    private void testSignal(final String name){
        try{
            Signal signal = new Signal(name);
            assertEquals(name, signal.getName());
            reference.assertEquals(name, signal.getNumber());
            final StringBuffer handleResult = new StringBuffer();
            SignalHandler handler = new SignalHandler(){

                @Override
                public void handle(Signal arg0){
                    synchronized(handleResult){
                        handleResult.setLength(0);
                        handleResult.append(name);
                        handleResult.notifyAll();
                    }
                }
            };
            try{
                SignalHandler oldHandler = Signal.handle(signal, handler);
                synchronized(handleResult){
                    Signal.raise(signal);
                    handleResult.wait(100);
                }
                Signal.handle(signal, oldHandler);
                assertEquals(name, handleResult.toString());
                reference.assertEquals(name + "_handle", "true");
            }catch(Exception ex){
                reference.assertEquals(name + "_handle", ex.getClass().getName());
            }
        }catch(IllegalArgumentException ex){
            // Signal does not exist on this platform
            reference.assertEquals(name, -1);
        }
    }
}
