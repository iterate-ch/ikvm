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
package java_.util.prefs;

import java.util.Arrays;
import java.util.prefs.*;

import junit.ikvm.ReferenceData;

import org.junit.*;
import static org.junit.Assert.*;

public class PreferencesTest{

    protected static ReferenceData reference;

    private static final String PATH_NAME = "ikvm preferences test/sub folder";


    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        reference = new ReferenceData();
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception{
        if(reference != null){
            reference.save();
        }
    }


    @Test
    public void absolutePath(){
        Preferences user = Preferences.userRoot();
        Preferences node = user.node(PATH_NAME);
        reference.assertEquals("absolutePath", node.absolutePath());
    }


    @Test
    public void childrenNames() throws BackingStoreException{
        Preferences user = Preferences.userRoot();
        reference.assertEquals("childrenNames", toString(user.childrenNames()));
    }


    @Test
    public void getXXX(){
        Preferences user = Preferences.userRoot();
        assertEquals("def", user.get("A Key that not exist", "def"));
        assertArrayEquals("def".getBytes(), user.getByteArray("A Key that not exist", "def".getBytes()));
        assertEquals(true, user.getBoolean("A Key that not exist", true));
        assertEquals(false, user.getBoolean("A Key that not exist", false));
        assertEquals(25.5, user.getDouble("A Key that not exist", 25.5), 0.0);
        assertEquals(25.5F, user.getFloat("A Key that not exist", 25.5F), 0.0);
        assertEquals(-234, user.getInt("A Key that not exist", -234));
        assertEquals(1234567890L, user.getLong("A Key that not exist", 1234567890L));
    }


    @Test
    public void name(){
        Preferences user = Preferences.userRoot();
        Preferences node = user.node(PATH_NAME);
        reference.assertEquals("name", node.name());
    }

    @Test
    public void putXXX() throws BackingStoreException{
        Preferences user = Preferences.userRoot();
        Preferences node = user.node(PATH_NAME);

        node.put("key", "value");
        node.flush();
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
        Preferences user = Preferences.userRoot();
        user.remove("A Key that not exist");
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
