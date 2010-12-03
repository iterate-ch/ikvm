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
package java_.util.prefs;

import java.util.Arrays;
import java.util.prefs.*;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

public class UserPreferencesTest{

    private static ReferenceData reference;
    
    private static Preferences rootPreferences;

    private static final String PATH_NAME = "/ikvm preferences test/sub folder";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        setReferenceData( new ReferenceData() );
        setRootPreferences( Preferences.userRoot() );
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        try {
            Preferences root = rootPreferences;
            Preferences node = root.node(PATH_NAME);
            node.parent().removeNode();
            reference.assertEquals( "tearDownAfterClass", "" );
        } catch( Exception ex ) {
            reference.assertEquals( "tearDownAfterClass", ex.toString() );
        }
        if(reference != null){
            reference.save();
            reference = null;
        }
    }
    
    protected static void setReferenceData( ReferenceData reference ) {
        UserPreferencesTest.reference = reference;
    }

    protected static void setRootPreferences( Preferences rootPreferences ) {
        UserPreferencesTest.rootPreferences = rootPreferences;
    }

    private Preferences getRoot(){
        return rootPreferences;
    }

    @Test
    public void absolutePath(){
        Preferences root = getRoot();
        Preferences node = root.node(PATH_NAME);
        reference.assertEquals("absolutePath", node.absolutePath());
    }


    @Test
    public void childrenNames() throws BackingStoreException{
        Preferences root = getRoot();
        reference.assertEquals("childrenNames", toString(root.childrenNames()));
    }


    @Test
    public void getXXX(){
        Preferences root = getRoot();
        assertEquals("def", root.get("A Key that not exist", "def"));
        assertArrayEquals("def".getBytes(), root.getByteArray("A Key that not exist", "def".getBytes()));
        assertEquals(true, root.getBoolean("A Key that not exist", true));
        assertEquals(false, root.getBoolean("A Key that not exist", false));
        assertEquals(25.5, root.getDouble("A Key that not exist", 25.5), 0.0);
        assertEquals(25.5F, root.getFloat("A Key that not exist", 25.5F), 0.0);
        assertEquals(-234, root.getInt("A Key that not exist", -234));
        assertEquals(1234567890L, root.getLong("A Key that not exist", 1234567890L));
    }


    @Test
    public void name(){
        Preferences root = getRoot();
        Preferences node = root.node(PATH_NAME);
        reference.assertEquals("name", node.name());
    }

    @Test
    public void putXXX() throws BackingStoreException{
        Preferences root = getRoot();
        Preferences node = root.node(PATH_NAME);
        node.put("key", "value");
        try {
            node.flush();
            reference.assertEquals( "putXXX.value", "" );
        } catch( Exception ex ) {
            reference.assertEquals( "putXXX.value", ex.toString() );
            return;
        }
        assertEquals("value", node.get("key", "default"));

        node.putBoolean("key", true);
        node.flush();
        assertEquals(true, node.getBoolean("key", false));

        node.putByteArray("key", "value".getBytes());
        node.flush();
        assertArrayEquals("value".getBytes(), node.getByteArray("key", "default".getBytes()));

        node.putDouble("key", 37.5);
        node.flush();
        assertEquals(37.5, node.getDouble("key", 1.5), 0.0);

        node.putFloat("key", -37.5F);
        node.flush();
        assertEquals(-37.5F, node.getFloat("key", 1.5F), 0.0);

        node.putInt("key", 54321);
        node.flush();
        assertEquals(54321, node.getInt("key", 333));

        node.putLong("key", 9876543210L);
        node.flush();
        assertEquals(9876543210L, node.getLong("key", 333));

        node.sync();
        reference.assertEquals("keys_before", toString(node.keys()));
        node.remove("key");
        reference.assertEquals("keys_after", toString(node.keys()));
    }


    @Test
    public void remove(){
        Preferences root = getRoot();
        try {
            root.remove("A Key that not exist");
            reference.assertEquals( "remove", "" );
        } catch( Exception ex ) {
            reference.assertEquals( "remove", ex.toString() );
            return;
        }
    }


    private static String toString(Object obj){
        if(obj instanceof Object[]){
            Object[] data = (Object[])obj;
            String[] strings = new String[data.length];
            for(int i = 0; i < strings.length; i++){
                strings[i] = String.valueOf(data[i]);
            }
            Arrays.sort(strings);
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < strings.length; i++){
                builder.append(strings[i]);
                builder.append(",");
            }
            return builder.toString();
        }else{
            return String.valueOf(obj);
        }
    }

}
