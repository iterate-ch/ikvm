package java.lang;

// Note: stop() should take care not to stop a thread if it is
// executing code in this class.
final class VMThread
{
    private static final Object countLock = new Object();
    private static int nonDaemonCount;
    // used by inner class
    /*private*/ static Thread __tls_javaThread;
    private static Object __tls_cleanup;
    private cli.System.WeakReference nativeThreadReference;

    // Note: when this thread dies, this reference is *not* cleared
    volatile Thread thread;
    private volatile boolean running;
    private volatile boolean interruptableWait;
    private volatile boolean interruptPending;
    private VMThread firstJoinWaiter;
    private VMThread nextJoinWaiter;

    private VMThread(Thread thread)
    {
	this.thread = thread;
    }

    // used by inner class
    /*private*/ void run()
    {
	try
	{
	    try
	    {
		running = true;
		synchronized(thread)
		{
		    Throwable t = thread.stillborn;
		    if(t != null)
		    {
			thread.stillborn = null;
			throw t;
		    }
		}
		thread.run();
	    }
	    catch(Throwable t)
	    {
		try
		{
                    Thread.UncaughtExceptionHandler handler = thread.getUncaughtExceptionHandler();
                    if(handler == null)
                    {
                        handler = thread.group;
                    }
                    handler.uncaughtException(thread, t);
		}
		catch(Throwable ignore)
		{
		}
	    }
	}
	finally
	{
	    // Setting runnable to false is partial protection against stop
	    // being called while we're cleaning up. To be safe all code in
	    // VMThread should be unstoppable.
	    running = false;
	    cleanup();
	}
    }

    // notify Thread that it is dead, this method can safely be called multiple times
    // used by inner class
    /*private*/ synchronized void cleanup()
    {
	if(thread.vmThread != null)
	{
	    thread.die();
	    nativeThreadReference.set_Target(null);
	    if(!thread.daemon)
	    {
		synchronized(countLock)
		{
		    nonDaemonCount--;
		}
	    }
	}
    }

    private synchronized void addJoinWaiter(VMThread waiter)
    {
        if(waiter != this)
        {
            waiter.nextJoinWaiter = firstJoinWaiter;
            firstJoinWaiter = waiter;
        }
    }

    private synchronized void removeJoinWaiter(VMThread waiter)
    {
        if(waiter == this)
        {
            // we never link ourself
        }
        else if(firstJoinWaiter == waiter)
        {
            firstJoinWaiter = waiter.nextJoinWaiter;
            waiter.nextJoinWaiter = null;
        }
        else
        {
            VMThread prev = firstJoinWaiter;
            VMThread curr = prev.nextJoinWaiter;
            while(curr != null)
            {
                if(curr == waiter)
                {
                    prev.nextJoinWaiter = waiter.nextJoinWaiter;
                    waiter.nextJoinWaiter = null;
                    break;
                }
                prev = curr;
                curr = curr.nextJoinWaiter;
            }
        }
    }

    static void jniDetach()
    {
        // If this thread never called Thread.currentThread(), we don't need
        // to clean up, because the Java Thread object doesn't exist and nobody
        // can possibly be waiting on us.
        Thread javaThread = __tls_javaThread;
        if(javaThread != null)
        {
	    VMThread vmthread = javaThread.vmThread;
	    synchronized(vmthread)
	    {
	        vmthread.cleanup();
                __tls_javaThread = null;
                __tls_cleanup = null;
                VMThread joinWaiter = vmthread.firstJoinWaiter;
                while(joinWaiter != null)
                {
                    VMThread next = joinWaiter.nextJoinWaiter;
                    joinWaiter.interrupt();
                    joinWaiter = next;
                }
	    }
        }
    }

    static void jniWaitUntilLastThread()
    {
	if(!Thread.currentThread().isDaemon())
	{
	    synchronized(countLock)
	    {
		nonDaemonCount--;
	    }
	}
	for(;;)
	{
	    synchronized(countLock)
	    {
		if(nonDaemonCount == 0)
		{
		    return;
		}
	    }
	    try
	    {
		Thread.sleep(1);
	    }
	    catch(InterruptedException x)
	    {
	    }
	}
    }

    static void setThreadGroup(ThreadGroup group)
    {
        if(__tls_javaThread == null)
        {
            newThread(group);
        }
    }

    static void create(Thread thread, long stacksize)
    {
	VMThread vmThread = new VMThread(thread);
	vmThread.start(stacksize);
	thread.vmThread = vmThread;
    }

    synchronized String getName()
    {
	return thread.name;
    }

    synchronized void setName(String name)
    {
	thread.name = name;
    }

    synchronized void setPriority(int priority)
    {
	thread.priority = priority;
	nativeSetPriority(priority);
    }

    synchronized int getPriority()
    {
        return thread.priority;
    }

    boolean isDaemon()
    {
        return thread.daemon;
    }

    int countStackFrames()
    {
	return 0;
    }

    void join(long ms, int ns) throws InterruptedException
    {
	cli.System.Threading.Thread nativeThread = (cli.System.Threading.Thread)nativeThreadReference.get_Target();
	if(nativeThread == null)
	{
	    return;
	}
	try
	{
            VMThread current = currentThread().vmThread;
	    enterInterruptableWait();
	    try
	    {
                addJoinWaiter(current);
                if(thread.vmThread != null)
                {
		    if(ms == 0 && ns == 0)
		    {
		        nativeThread.Join();
		    }
		    else
		    {
		        // if nanoseconds are specified, round up to one millisecond
		        if(ns != 0)
		        {
			    nativeThread.Join(1);
		        }
		        for(long iter = ms / Integer.MAX_VALUE; iter != 0; iter--)
		        {
			    if(nativeThread.Join(Integer.MAX_VALUE))
                            {
                                break;
                            }
		        }
		        nativeThread.Join((int)(ms % Integer.MAX_VALUE));
		    }
                }
	    }
	    finally
	    {
                removeJoinWaiter(current);
                leaveInterruptableWait();
	    }
	}
	catch(InterruptedException x)
	{
	    // if native code detached the thread, we get interrupted
	    // to signal that the thread we were waiting for died
	    if(thread.vmThread != null)
	    {
		throw x;
	    }
	}
	// make sure the thread is marked as dead and removed from the thread group, before we
	// return from a successful join
	if(!nativeThread.get_IsAlive())
	{
	    cleanup();
	}
    }

    void stop(Throwable t)
    {
	// NOTE we assume that we own the lock on thread
	// (i.e. that Thread.stop() is synchronized)
	if(running)
	    nativeStop(t);
	else
	    thread.stillborn = t;
    }

    void start(long stacksize)
    {
	cli.System.Threading.ThreadStart starter = new cli.System.Threading.ThreadStart(
	    new cli.System.Threading.ThreadStart.Method()
	{
	    public void Invoke()
	    {
                __tls_javaThread = thread;
		run();
	    }
	});
	cli.System.Threading.Thread nativeThread = new cli.System.Threading.Thread(starter);
	nativeThreadReference = new cli.System.WeakReference(nativeThread);
	nativeThread.set_Name(thread.name);
	nativeThread.set_IsBackground(thread.daemon);
	nativeSetPriority(thread.priority);
	nativeThread.Start();
	if(!thread.daemon)
	{
	    synchronized(countLock)
	    {
		nonDaemonCount++;
	    }
	}
    }

    private static void enterInterruptableWait() throws InterruptedException
    {
        VMThread vmthread = currentThread().vmThread;
        synchronized(vmthread)
        {
            if(vmthread.interruptPending)
            {
                vmthread.interruptPending = false;
                throw new InterruptedException();
            }
            vmthread.interruptableWait = true;
        }
    }

    private static void leaveInterruptableWait() throws InterruptedException
    {
        cli.System.Threading.ThreadInterruptedException dotnetInterrupt = null;
        for(;;)
        {
            try
            {
                if(false) throw new cli.System.Threading.ThreadInterruptedException();
                VMThread vmthread = currentThread().vmThread;
                synchronized(vmthread)
                {
                    vmthread.interruptableWait = false;
                    if(vmthread.interruptPending)
                    {
                        vmthread.interruptPending = false;
                        throw new InterruptedException();
                    }
                }
                break;
            }
            catch(cli.System.Threading.ThreadInterruptedException x)
            {
                dotnetInterrupt = x;
            }
        }
        if(dotnetInterrupt != null)
        {
            VMClass.throwException(dotnetInterrupt);
        }
    }

    synchronized void interrupt()
    {
        interruptPending = true;
        if(interruptableWait)
        {
            cli.System.Threading.Thread nativeThread = (cli.System.Threading.Thread)nativeThreadReference.get_Target();
	    if(nativeThread != null)
	    {
	        nativeThread.Interrupt();
	    }
        }
    }

    static boolean interrupted()
    {
        VMThread thread = currentThread().vmThread;
        synchronized(thread)
        {
            boolean state = thread.interruptPending;
            thread.interruptPending = false;
            return state;
        }
    }

    boolean isInterrupted()
    {
	return interruptPending;
    }

    void suspend()
    {
	cli.System.Threading.Thread nativeThread = (cli.System.Threading.Thread)nativeThreadReference.get_Target();
	if(nativeThread != null)
	{
            try
            {
                if(false) throw new cli.System.Threading.ThreadStateException();
                nativeThread.Suspend();
            }
            catch(cli.System.Threading.ThreadStateException x)
            {
            }
	}
    }

    void resume()
    {
	cli.System.Threading.Thread nativeThread = (cli.System.Threading.Thread)nativeThreadReference.get_Target();
	if(nativeThread != null)
	{
            try
            {
                if(false) throw new cli.System.Threading.ThreadStateException();
                nativeThread.Resume();
            }
            catch(cli.System.Threading.ThreadStateException x)
            {
            }
        }
    }

    void nativeSetPriority(int priority)
    {
	cli.System.Threading.Thread nativeThread = (cli.System.Threading.Thread)nativeThreadReference.get_Target();
	if(nativeThread != null)
	{
	    if(priority == Thread.MIN_PRIORITY)
	    {
		nativeThread.set_Priority(cli.System.Threading.ThreadPriority.wrap(cli.System.Threading.ThreadPriority.Lowest));
	    }
	    else if(priority > Thread.MIN_PRIORITY && priority < Thread.NORM_PRIORITY)
	    {
		nativeThread.set_Priority(cli.System.Threading.ThreadPriority.wrap(cli.System.Threading.ThreadPriority.BelowNormal));
	    }
	    else if(priority == Thread.NORM_PRIORITY)
	    {
		nativeThread.set_Priority(cli.System.Threading.ThreadPriority.wrap(cli.System.Threading.ThreadPriority.Normal));
	    }
	    else if(priority > Thread.NORM_PRIORITY && priority < Thread.MAX_PRIORITY)
	    {
		nativeThread.set_Priority(cli.System.Threading.ThreadPriority.wrap(cli.System.Threading.ThreadPriority.AboveNormal));
	    }
	    else if(priority == Thread.MAX_PRIORITY)
	    {
		nativeThread.set_Priority(cli.System.Threading.ThreadPriority.wrap(cli.System.Threading.ThreadPriority.Highest));
	    }
	}
    }

    void nativeStop(Throwable t)
    {
	// NOTE we allow ThreadDeath (and its subclasses) to be thrown on every thread, but any
	// other exception is ignored, except if we're throwing it on the current Thread. This
	// is done to allow exception handlers to be type specific, otherwise every exception
	// handler would have to catch ThreadAbortException and look inside it to see if it
	// contains the real exception that we wish to handle.
	// I hope we can get away with this behavior, because Thread.stop() is deprecated
	// anyway. Note that we do allow arbitrary exceptions to be thrown on the current
	// thread, since this is harmless (because they aren't wrapped) and also because it
	// provides some real value, because it is the only way you can throw arbitrary checked
	// exceptions from Java.
	if(currentThread().vmThread == this)
	{
	    VMClass.throwException(t);
	}
	else if(t instanceof ThreadDeath)
	{
	    cli.System.Threading.Thread nativeThread = (cli.System.Threading.Thread)nativeThreadReference.get_Target();
	    if(nativeThread != null)
	    {
                try
                {
                    if(false) throw new cli.System.Threading.ThreadStateException();
                    nativeThread.Abort(t);
                }
                catch(cli.System.Threading.ThreadStateException x)
                {
                    // .NET 2.0 throws a ThreadStateException if the target thread is currently suspended
                }
                try
                {
                    if(false) throw new cli.System.Threading.ThreadStateException();
                    int suspend = cli.System.Threading.ThreadState.Suspended | cli.System.Threading.ThreadState.SuspendRequested;
                    while((nativeThread.get_ThreadState().Value & suspend) != 0)
                    {
                        nativeThread.Resume();
                    }
                }
                catch(cli.System.Threading.ThreadStateException x)
                {
                }
	    }
	}
    }

    private static class CleanupHack
    {
	private Thread thread;

	CleanupHack(Thread thread)
	{
	    this.thread = thread;
	}

	protected void finalize()
	{
	    VMThread vmThread = thread.vmThread;
	    if(vmThread != null)
	    {
		vmThread.cleanup();
	    }
	}
    }

    // this method creates a new java.lang.Thread instance for threads that were started outside
    // of Java (either in .NET or in native code)
    private static Thread newThread(ThreadGroup group)
    {
        cli.System.Threading.Thread nativeThread = cli.System.Threading.Thread.get_CurrentThread();
        VMThread vmThread = new VMThread(null);
        vmThread.nativeThreadReference = new cli.System.WeakReference(nativeThread);
        vmThread.running = true;
        int priority = Thread.NORM_PRIORITY;
        switch(nativeThread.get_Priority().Value)
        {
            case cli.System.Threading.ThreadPriority.Lowest:
                priority = Thread.MIN_PRIORITY;
                break;
            case cli.System.Threading.ThreadPriority.BelowNormal:
                priority = 3;
                break;
            case cli.System.Threading.ThreadPriority.Normal:
                priority = Thread.NORM_PRIORITY;
                break;
            case cli.System.Threading.ThreadPriority.AboveNormal:
                priority = 7;
                break;
            case cli.System.Threading.ThreadPriority.Highest:
                priority = Thread.MAX_PRIORITY;
                break;
        }
        Thread javaThread = new Thread(vmThread, nativeThread.get_Name(), priority, nativeThread.get_IsBackground());
        if(!javaThread.daemon)
        {
            synchronized(countLock)
            {
                nonDaemonCount++;
            }
        }
        vmThread.thread = javaThread;
        __tls_javaThread = javaThread;
        __tls_cleanup = new CleanupHack(javaThread);
        javaThread.group = group;
        javaThread.group.addThread(javaThread);
        InheritableThreadLocal.newChildThread(javaThread);
        return javaThread;
    }

    static Thread currentThread()
    {
        Thread javaThread = __tls_javaThread;
	if(javaThread == null)
	{
            __tls_javaThread = javaThread = newThread(ThreadGroup.root);
	}
	return javaThread;
    }

    static void yield()
    {
	cli.System.Threading.Thread.Sleep(0);
    }

    static void sleep(long ms, int ns) throws InterruptedException
    {
	// NOTE sleep(0) doesn't trigger a pending interrupt on the Sun JDK,
	// so we duplicate that behavior.
	if(ms == 0 && ns == 0)
	{
            // TODO this bug was fixed in Mustang,
            // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6213203
	    yield();
	}
	else
	{
            enterInterruptableWait();
            try
            {
	        // if nanoseconds are specified, round up to one millisecond
	        if(ns != 0)
	        {
		    cli.System.Threading.Thread.Sleep(1);
	        }
	        for(long iter = ms / Integer.MAX_VALUE; iter != 0; iter--)
	        {
		    cli.System.Threading.Thread.Sleep(Integer.MAX_VALUE);
	        }
	        cli.System.Threading.Thread.Sleep((int)(ms % Integer.MAX_VALUE));
            }
            finally
            {
                leaveInterruptableWait();
            }
	}
    }

    static boolean holdsLock(Object obj)
    {
	if(obj == null)
	{
	    throw new NullPointerException();
	}
	try
	{
	    // The new 1.5 memory model explicitly allows spurious wake-ups from Object.wait,
	    // so we abuse Pulse to check if we own the monitor.
	    if(false) throw new IllegalMonitorStateException();
	    cli.System.Threading.Monitor.Pulse(obj);
	    return true;
	}
	catch(IllegalMonitorStateException x)
	{
	    return false;
	}
    }

    // this implements java.lang.Object.wait(long timeout, int nanos) (via map.xml)
    static void objectWait(Object o, long timeout, int nanos) throws InterruptedException
    {
        if(o == null)
        {
            throw new NullPointerException();
        }
        if(timeout < 0 || nanos < 0 || nanos > 999999)
        {
            throw new IllegalArgumentException("argument out of range");
        }
        enterInterruptableWait();
        try
        {
            if((timeout == 0 && nanos == 0) || timeout > 922337203685476L)
            {
                cli.System.Threading.Monitor.Wait(o);
            }
            else
            {
                cli.System.Threading.Monitor.Wait(o, new cli.System.TimeSpan(timeout * 10000 + (nanos + 99) / 100));
            }
        }
        finally
        {
            leaveInterruptableWait();
        }
    }
}
