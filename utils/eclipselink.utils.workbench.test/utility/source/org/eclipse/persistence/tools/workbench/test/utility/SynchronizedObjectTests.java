/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.utility;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.SynchronizedObject;

public class SynchronizedObjectTests extends TestCase {
	private volatile SynchronizedObject so;
	private volatile boolean exCaught;
	private volatile boolean timeoutOccurred;
	volatile Object value = new Object();
	private volatile long startTime;
	private volatile long endTime;
	private volatile Object soValue;


	public static Test suite() {
		return new TestSuite(SynchronizedObjectTests.class);
	}

	public SynchronizedObjectTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.so = new SynchronizedObject();
		this.exCaught = false;
		this.timeoutOccurred = false;
		this.startTime = 0;
		this.endTime = 0;
		this.soValue = null;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testAccessors() throws Exception {
		this.so.setValue(null);
		assertNull(this.so.getValue());
		assertFalse(this.so.isNotNull());
		assertTrue(this.so.isNull());

		this.so.setValue(this.value);
		assertEquals(this.value, this.so.getValue());
		assertTrue(this.so.isNotNull());
		assertFalse(this.so.isNull());

		this.so.setNull();
		assertNull(this.so.getValue());
		assertFalse(this.so.isNotNull());
		assertTrue(this.so.isNull());

		assertSame(this.so, this.so.getMutex());
	}

	public void testEquals() throws Exception {
		this.so.setValue(null);
		SynchronizedObject so2 = new SynchronizedObject(null);
		assertEquals(this.so, so2);

		this.so.setValue(this.value);
		assertFalse(this.so.equals(so2));

		so2.setValue(this.value);
		assertEquals(this.so, so2);
	}

	public void testHashCode() {
		this.so.setValue(this.value);
		assertEquals(this.value.hashCode(), this.so.hashCode());

		this.so.setValue(null);
		assertEquals(0, this.so.hashCode());
	}

	/**
	 * t2 will wait indefinitely until t1 sets the value to null
	 */
	public void testWaitUntilNull() throws Exception {
		this.verifyWaitUntilNull(0);
		// no timeout occurs...
		assertFalse(this.timeoutOccurred);
		// ...and the value should be set to null by t2
		assertNull(this.so.getValue());
		// make a reasonable guess about how long t2 took
		long time = this.elapsedTime();
		assertTrue("t2 finished a bit early (expected value should be > 150): " + time, time > 150);
	}

	/**
	 * t2 will time out waiting for t1 to set the value to null
	 */
	public void testWaitUntilNullTimeout() throws Exception {
		this.verifyWaitUntilNull(20);
		// timeout occurs...
		assertTrue(this.timeoutOccurred);
		// ...and the value will eventually be set to null by t1
		assertNull(this.so.getValue());
		// make a reasonable guess about how long t2 took
		long time = this.elapsedTime();
		assertTrue("t2 finished a bit late (expected value should be < 150): " + time, time < 150);
	}

	private void verifyWaitUntilNull(long t2Timeout) throws Exception {
		this.executeThreads(this.buildSetNullCommand(), this.buildWaitUntilNullCommand(t2Timeout));
	}

	/**
	 * t2 will wait indefinitely until t1 sets the value to null;
	 * then t2 will set the value to an object
	 */
	public void testWaitToSetValue() throws Exception {
		this.verifyWaitToSetValue(0);
		// no timeout occurs...
		assertFalse(this.timeoutOccurred);
		// ...and the value should be set to an object by t2
		assertTrue(this.so.isNotNull());
		// make a reasonable guess about how long t2 took
		long time = this.elapsedTime();
		assertTrue("t2 finished a bit early (expected value should be > 150): " + time, time > 150);
	}

	/**
	 * t2 will time out waiting for t1 to set the value to null
	 */
	public void testWaitToSetValueTimeout() throws Exception {
		this.verifyWaitToSetValue(20);
		// timeout occurs...
		assertTrue(this.timeoutOccurred);
		// ...and the value will eventually be set to null by t1
		assertTrue(this.so.isNull());
		// make a reasonable guess about how long t2 took
		long time = this.elapsedTime();
		assertTrue("t2 finished a bit late (expected value should be < 150): " + time, time < 150);
	}

	private void verifyWaitToSetValue(long t2Timeout) throws Exception {
		this.executeThreads(this.buildSetNullCommand(), this.buildWaitToSetValueCommand(t2Timeout));
	}

	/**
	 * t2 will wait until t1 is finished "initializing" the value;
	 * then t2 will get the newly-initialized value ("foo")
	 */
	public void testExecute() throws Exception {
		this.so.setValue(null);
		Runnable r1 = this.buildRunnable(this.buildInitializeValueCommand(), this.so, 0);
		// give t1 a head start of 100 ms
		Runnable r2 = this.buildRunnable(this.buildGetValueCommand(), this.so, 100);
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		t1.start();
		t2.start();
		while (t1.isAlive() || t2.isAlive()) {
			Thread.sleep(50);
		}
		assertFalse(this.exCaught);
		assertEquals("foo", this.so.getValue());
		assertEquals("foo", this.soValue);
		// make a reasonable guess about how long t2 took
		long time = this.elapsedTime();
		assertTrue("t2 finished a bit early (expected value should be > 100): " + time, time > 300);
	}

	private void executeThreads(Command t1Command, Command t2Command) throws Exception {
		this.so.setValue(this.value);
		Runnable r1 = this.buildRunnable(t1Command, this.so, 200);
		Runnable r2 = this.buildRunnable(t2Command, this.so, 0);
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		t1.start();
		t2.start();
		while (t1.isAlive() || t2.isAlive()) {
			Thread.sleep(50);
		}
		assertFalse(this.exCaught);
	}

	private Command buildSetNullCommand() {
		return new Command() {
			public void execute(SynchronizedObject sObject) {
				sObject.setNull();
			}
		};
	}

	private Command buildWaitUntilNullCommand(final long timeout) {
		return new Command() {
			public void execute(SynchronizedObject sObject) throws Exception {
				SynchronizedObjectTests.this.setStartTime(System.currentTimeMillis());
				SynchronizedObjectTests.this.setTimeoutOccurred( ! sObject.waitUntilNull(timeout));
				SynchronizedObjectTests.this.setEndTime(System.currentTimeMillis());
			}
		};
	}

	private Command buildWaitToSetValueCommand(final long timeout) {
		return new Command() {
			public void execute(SynchronizedObject sObject) throws Exception {
				SynchronizedObjectTests.this.setStartTime(System.currentTimeMillis());
				SynchronizedObjectTests.this.setTimeoutOccurred( ! sObject.waitToSetValue(SynchronizedObjectTests.this.value, timeout));
				SynchronizedObjectTests.this.setEndTime(System.currentTimeMillis());
			}
		};
	}

	private Command buildInitializeValueCommand() {
		return new Command() {
			public void execute(final SynchronizedObject sObject) throws Exception {
				sObject.execute(
					new org.eclipse.persistence.tools.workbench.utility.Command() {
						public void execute() {
							// pretend to perform some long initialization process
							try {
								Thread.sleep(500);
							} catch (Exception ex) {
								SynchronizedObjectTests.this.setExCaught(true);
							}
							sObject.setValue("foo");
						}
					}
				);
			}
		};
	}

	private Command buildGetValueCommand() {
		return new Command() {
			public void execute(SynchronizedObject sObject) throws Exception {
				SynchronizedObjectTests.this.setStartTime(System.currentTimeMillis());
				SynchronizedObjectTests.this.setSOValue(sObject.getValue());
				SynchronizedObjectTests.this.setEndTime(System.currentTimeMillis());
			}
		};
	}

	private Runnable buildRunnable(final Command command, final SynchronizedObject sObject, final long sleep) {
		return new Runnable() {
			public void run() {
				try {
					if (sleep != 0) {
						Thread.sleep(sleep);
					}
					command.execute(sObject);
				} catch (Exception ex) {
					SynchronizedObjectTests.this.setExCaught(true);
				}
			}
		};
	}

	void setExCaught(boolean exCaught) {
		this.exCaught = exCaught;
	}

	void setTimeoutOccurred(boolean timeoutOccurred) {
		this.timeoutOccurred = timeoutOccurred;
	}

	void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	private long elapsedTime() {
		return this.endTime - this.startTime;
	}

	void setSOValue(Object soValue) {
		this.soValue = soValue;
	}


	// ********** Command interface **********

	private interface Command {
		void execute(SynchronizedObject so) throws Exception;
	}

}
