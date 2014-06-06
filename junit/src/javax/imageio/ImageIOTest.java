package javax.imageio;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.event.*;
import javax.imageio.stream.*;

import org.junit.*;

public class ImageIOTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void gif() throws Exception {
		testImage("red.gif");
	}

	@Test
	public void jpg() throws Exception {
		testImage("red.jpg");
	}

	@Test
	public void png() throws Exception {
		testImage("red.png");
	}

	private void testImage(String imageName) throws Exception {
		URL url = getClass().getResource(imageName);
		InputStream input = url.openStream();
		ImageInputStream stream = ImageIO.createImageInputStream(url.openStream());
		Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
		assertTrue("hasNext", readers.hasNext());
		
		ImageReader reader = readers.next();
		IIOReadListener listener = new IIOReadListener();
		reader.addIIOReadUpdateListener(listener);
		reader.addIIOReadProgressListener(listener);
		ImageReadParam param = reader.getDefaultReadParam();
		reader.setInput(stream, true, true);
		BufferedImage bi = reader.read(0, param);
		
		assertTrue("RGB", 0xFFFE0000 == bi.getRGB(0, 0) || 0xFFFF0000 == bi.getRGB(0, 0));
		assertEquals("width", 4, bi.getWidth());
		assertEquals("height", 4, bi.getHeight());
	}

	private static class IIOReadListener implements IIOReadUpdateListener,
			IIOReadProgressListener {
		private int sequence;

		@Override
		public void imageUpdate(ImageReader arg0, BufferedImage arg1, int arg2,
				int arg3, int arg4, int arg5, int arg6, int arg7, int[] arg8) {
			assertTrue("Wrong Event order:" + sequence, sequence == 4 || sequence == 3 || sequence == 2);
			sequence = 4;
		}

		@Override
		public void passComplete(ImageReader arg0, BufferedImage arg1) {
			assertTrue("Wrong Event order:" + sequence, sequence == 4
					|| sequence == 3);
			sequence = 5;
		}

		@Override
		public void passStarted(ImageReader arg0, BufferedImage arg1, int arg2,
				int arg3, int arg4, int arg5, int arg6, int arg7, int arg8,
				int[] arg9) {
			assertEquals("Wrong Event order", 1, sequence);
			sequence = 2;
		}

		@Override
		public void thumbnailPassComplete(ImageReader arg0, BufferedImage arg1) {
		}

		@Override
		public void thumbnailPassStarted(ImageReader arg0, BufferedImage arg1,
				int arg2, int arg3, int arg4, int arg5, int arg6, int arg7,
				int arg8, int[] arg9) {
		}

		@Override
		public void thumbnailUpdate(ImageReader arg0, BufferedImage arg1,
				int arg2, int arg3, int arg4, int arg5, int arg6, int arg7,
				int[] arg8) {
		}

		@Override
		public void imageComplete(ImageReader arg0) {
			assertTrue("Wrong Event order:" + sequence, sequence == 5 || sequence == 4);
			sequence = 6;
		}

		@Override
		public void imageProgress(ImageReader arg0, float arg1) {
			assertTrue("Wrong Event order:" + sequence, sequence == 1 || sequence == 2 || sequence == 4); // Java 8 does not start passStarted, passComplete for gif images
			sequence = 3;
		}

		@Override
		public void imageStarted(ImageReader arg0, int arg1) {
			assertEquals("Wrong Event order", 0, sequence);
			sequence = 1;
		}

		@Override
		public void readAborted(ImageReader arg0) {
		}

		@Override
		public void sequenceComplete(ImageReader arg0) {
		}

		@Override
		public void sequenceStarted(ImageReader arg0, int arg1) {
		}

		@Override
		public void thumbnailComplete(ImageReader arg0) {
		}

		@Override
		public void thumbnailProgress(ImageReader arg0, float arg1) {
		}

		@Override
		public void thumbnailStarted(ImageReader arg0, int arg1, int arg2) {
		}
	}
}
