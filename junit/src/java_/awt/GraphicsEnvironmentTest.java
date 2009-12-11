package java_.awt;

import static org.junit.Assert.*;

import java.awt.*;
import java.util.Arrays;

import junit.ikvm.ReferenceData;

import org.junit.*;

public class GraphicsEnvironmentTest {

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
    public void getAllFonts(){
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	Font[] fonts = ge.getAllFonts();
    	String[] families = new String[fonts.length];
    	for(int i=0; i<families.length; i++){
    		families[i] = fonts[i].getFamily();
    	}
    	reference.assertEquals("getAllFonts", toString(families));
    }

    
    @Test
    public void getAvailableFontFamilyNames(){
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	String[] families = ge.getAvailableFontFamilyNames();
    	reference.assertEquals("getAvailableFontFamilyNames", toString(families));
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
        } else {
            return String.valueOf(obj);
        }
    }
}
