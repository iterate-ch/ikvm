/*
  Copyright (C) 2012 Volker Berlin (i-net software)

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
package java_.lang.management;
import java.lang.management.*;

import org.junit.Test;

import static org.junit.Assert.*;

public class ThreadInfoTest {

	@Test
	public void getThreadInfoUnknownID() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		ThreadInfo info = bean.getThreadInfo(12345678901234567L, Integer.MAX_VALUE);
		assertEquals(null, info);
	}
	
	@Test
	public void getThreadInfoCurrent() {
		Thread thread = Thread.currentThread();
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		ThreadInfo info = bean.getThreadInfo(thread.getId(), Integer.MAX_VALUE);
		assertNotNull(info);
		assertEquals(thread.getId(), info.getThreadId());
		assertEquals(thread.getName(), info.getThreadName());
		assertEquals(thread.getState(), info.getThreadState());

		int maxDepth = info.getStackTrace().length - 1;
		assertTrue( maxDepth >= 0 );
		ThreadInfo info2 = bean.getThreadInfo(thread.getId(), maxDepth);
		assertEquals(maxDepth, info2.getStackTrace().length);
	}
	
	@Test
	public void getThreadInfoOtherThread() throws Exception {
		Thread thread = new Thread() {
			public void run() {
				synchronized (ThreadInfoTest.this) {

				}
			}
		};
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		ThreadInfo info = bean.getThreadInfo(thread.getId(), Integer.MAX_VALUE);
		assertNull(info);

		synchronized (this) {
			thread.start();
			for (int i = 0; i < 100; i++) {
				if (thread.getState() == Thread.State.BLOCKED) {
					break;
				}
				Thread.sleep(1);
			}
			assertEquals(thread.getState(), Thread.State.BLOCKED);

			info = bean.getThreadInfo(thread.getId(), Integer.MAX_VALUE);
			
			assertEquals(thread.getName(), info.getThreadName());
			assertEquals(thread.getName(), info.getThreadName());
			assertEquals(thread.getState(), info.getThreadState());
			assertTrue(info.getStackTrace().length > 0);
		}
	}
}
