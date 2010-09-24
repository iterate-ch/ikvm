/*
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
package java_.awt;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Button;
import java.awt.Point;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ButtonPeer;

import junit.framework.Assert;
import junit.ikvm.ReferenceData;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ButtonTest {

	protected static ReferenceData reference;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        reference = new ReferenceData();
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        if(reference != null){
            reference.save();
        }
        reference = null;
    }
	
	@Test
	public void create(){
		// check label peer creation
		Frame f = new Frame();		
		Button l = new Button("label");
		f.add( l );
		f.addNotify();
		l.addNotify(); // creates the peer
		reference.assertEquals("ButtonPeerCreated", l.getPeer() instanceof ButtonPeer );
		Assert.assertTrue( "Button has ButtonPeer", l.getPeer() instanceof ButtonPeer );
	}
	
	@Test
	public void getLocation() throws Exception{
		// basically check ComponentPeer.reshape
		Frame f = new Frame();	
		Button l = new Button("label");
		f.add( l );
		try{
			f.setVisible( true );
			f.pack();
			Point location = l.getLocation();
			reference.assertEquals("getLocation", location );
		} finally {
			f.setVisible(false);
			f.dispose();
		}
	}
	
}
