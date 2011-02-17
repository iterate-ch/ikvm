package javax.print;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.print.attribute.standard.PageRanges;

import junit.framework.Assert;
import junit.ikvm.ReferenceData;

import org.junit.Test;

import sun.print.Win32PrintJob;

public class PageRangeTest {

	private static class PNCWrapper{

		private Object instance ;
		private Class<?> pncClass;

		public PNCWrapper(PageRanges pageRanges, int copies, boolean collate) {
			try {
				pncClass = ClassLoader.getSystemClassLoader().loadClass( "sun.print.Win32PrintJob$PageNumberConverter");
				Constructor<?> ctr = pncClass.getConstructor(PageRanges.class, int.class, boolean.class );
				instance = ctr.newInstance( pageRanges, Integer.valueOf(copies), Boolean.valueOf(collate) );
			} catch (Throwable e) {
				if( ReferenceData.isIkvm() ){
					e.printStackTrace();
					Assert.fail("Could not instantiate class sun.print.Win32PrintJob.PageNumberConverter: " + e.getMessage() );
				}
			}
		}

		public int getPageForIndex(int i) {
			Method m;
			try {
				m = pncClass.getMethod("getPageForIndex", int.class );
				return ((Integer)m.invoke(instance, Integer.valueOf(i))).intValue();
			} catch (Throwable e) {
				e.printStackTrace();
				Assert.fail("Calling PageNumberConverter.getPageForIndex() failed: " + e.getMessage() );
			}
			return 0;
		}

		public boolean checkJobComplete(int index) {
			Method m;
			try {
				m = pncClass.getMethod("checkJobComplete", int.class );
				return ((Boolean)m.invoke(instance, Integer.valueOf(index))).booleanValue();
			} catch (Throwable e) {
				e.printStackTrace();
				Assert.fail("Calling PageNumberConverter.checkJobComplete() failed: " + e.getMessage() );
			}
			return false;
		}
		
	}
	
    private void test( int[][] ranges, int copies, boolean collate, String expected){
    	if( !ReferenceData.isIkvm() ){
            return;
    	}
		PNCWrapper pnc = new PNCWrapper( new PageRanges( ranges ), copies, collate );
        int index = 0;
        int page = 0;
        StringBuilder pages = new StringBuilder();
        while( page >= 0 ){
            page = pnc.getPageForIndex( index++ );
            if( page < 0 ){
                break;
            }
            if( pages.length() > 0 ){
                pages.append(',');                
            }
            pages.append( page+1 );
        }
        Assert.assertEquals( expected, pages.toString() );
    }
    
    @Test
    public void testTwoRanges(){
    	test( new int[][] {{1,3},{5,7}}, 1, true, "1,2,3,5,6,7" );
    }
    
    @Test
    public void testDuplicateRange(){
        test( new int[][] {{1,3},{1,3}}, 1, true, "1,2,3" );
    }
    
    @Test
    public void testMergeRange(){
        test( new int[][] {{1,3},{4,6}}, 1, true, "1,2,3,4,5,6" );
    }
    
    @Test
    public void testIntersectingRange(){
        test( new int[][] {{2,4},{1,6}}, 1, true, "1,2,3,4,5,6" );
    }
    
    @Test
    public void testSinglePages(){
        test( new int[][] {{5,5},{3,3},{4,4},{2,2}}, 1, true, "2,3,4,5" );
    }
    
    @Test
    public void testCopiesCollated(){
        test( new int[][] {{1,2},{4,5}}, 3, true, "1,2,4,5,1,2,4,5,1,2,4,5" );
    }
    
    @Test
    public void testCopiesUncollated(){
        test( new int[][] {{1,2},{4,5}}, 3, false, "1,2,4,5" );
    }
    
    private void testNoRange( int copies, boolean collated, int maxpages, String expected ){        
    	if( !ReferenceData.isIkvm() ){
            return;
    	}
    	PNCWrapper conv = new PNCWrapper( null, copies, collated );
        int index = 0;
        int page = 0;
        int maxPage = maxpages;
        StringBuilder pages = new StringBuilder();
        while( page >= 0 ){
            page = conv.getPageForIndex( index++ );
            if( index == maxPage ){
                if( conv.checkJobComplete( index ) ){
                    break;
                }
            }
            if( page < 0 ){
                break;
            }
            if( pages.length() > 0 ){
                pages.append(',');                
            }
            pages.append( page+1 );
        }
        Assert.assertEquals( expected, pages.toString() );
    }
    
    @Test
    public void testNoRange(){
        testNoRange( 1, true, 3, "1,2,3");
    }
    
    @Test
    public void testNoRangeCollatedCopies(){
        testNoRange( 3, true, 3, "1,2,3,1,2,3,1,2,3");
    }
    
    @Test
    public void testNoRangeUncollatedCopies(){
        testNoRange( 3, false, 3, "1,2,3"); // NOTE: copies is handled by the printer for uncollated
    }
}
