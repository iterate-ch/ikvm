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
package java_.net;

import java.io.*;
import java.net.*;

import junit.ikvm.ReferenceData;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Volker Berlin
 */
public abstract class IPTest{

    protected static ReferenceData reference;
    
    private final static int PORT = 32119;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        if(reference != null){
            reference.save();
        }
        reference = null;
    }


    abstract String getLocalIP();


    abstract int getAddressLength();


    private InetAddress getInetAddress() throws Exception{
        InetAddress address = InetAddress.getByName(getLocalIP());
        byte[] bytes = address.getAddress();
        assertEquals("length", getAddressLength(), bytes.length);
        return address;
    }


    @Test
    public void tcp() throws Throwable{
        final Throwable[] throwable = new Throwable[1];

        InetAddress address = getInetAddress();

        final ServerSocket ss = new ServerSocket(PORT, 50, address);

        Thread thread = new Thread("Socket Accept"){

            @Override
            public void run(){
                try{
                    Socket socket = ss.accept();
                    OutputStream output = socket.getOutputStream();
                    output.write("Any Text".getBytes());
                    output.flush();
                    reference.assertEquals( "tcp.accept.remote.hostname", ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress() );
                    socket.close();
                }catch(Throwable ex){
                    ex.printStackTrace();
                    throwable[0] = ex;
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(1);

        Socket socket = new Socket(address, PORT);
        InputStream input = socket.getInputStream();
        byte[] buffer = new byte[1024];
        int count = input.read(buffer);
        assertEquals("Any Text", new String(buffer, 0, count));
        reference.assertEquals( "tcp.remote.hostname", ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress() );
        socket.close();
        ss.close();
        
        thread.join();
        if( throwable[0] != null ){
            throw throwable[0];
        }
   }


    @Test
    public void udp() throws Throwable{
        final InetAddress address = getInetAddress();

        byte[] buffer = "Any Text".getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,address, PORT);
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        
        buffer = new byte[256];
        packet = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(10);
        try{
            socket.receive(packet);
            fail( "SocketTimeoutException expected" );
        }catch(SocketTimeoutException ex){
            // expected
        }
    }

}
