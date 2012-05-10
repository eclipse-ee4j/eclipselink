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

import org.eclipse.persistence.tools.workbench.utility.SynchronizedBoolean;

public class SynchronizedBooleanTests extends TestCase {
	private volatile SynchronizedBoolean sb;
	private volatile boolean exCaught;
	private volatile boolean timeoutOccurred;
	private volatile long startTime;
	private volatile long endTime;


	public static Test suite() {
		return new TestSuite(SynchronizedBooleanTests.class);
	}

	public SynchronizedBooleanTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.sb = new SynchronizedBoolean();
		this.exCaught = false;
		this.timeoutOccurred = false;
		this.startTime = 0;
		this.endTime = 0;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testAccessors() throws Exception {
		this.sb.setValue(false);
		assertFalse(this.sb.getValue());
		assertFalse(this.sb.isTrue());
		assertTrue(this.sb.isFalse());

		this.sb.setValue(true);
		assertTrue(this.sb.getValue());
		assertTrue(this.sb.isTrue());
		assertFalse(this.sb.isFalse());

		this.sb.setFalse();
		assertFalse(this.sb.getValue());
		assertFalse(this.sb.isTrue());
		assertTrue(this.sb.isFalse());

		this.sb.setTrue();
		assertTrue(this.sb.getValue());
		assertTrue(this.sb.isTrue());
		assertFalse(this.sb.isFalse());

		assertSame(this.sb, this.sb.getMutex());
	}

	public void testEquals() throws Exception {
		this.sb.setValue(false);
		SynchronizedBoolean sb2 = new SynchronizedBoolean(false);
		assertEquals(this.sb, sb2);

		this.sb.setValue(true);
		assertFalse(this.sb.equals(sb2));

		sb2.setValue(true);
		assertEquals(this.sb, sb2);
	}

	public void testHashCode() {
		this.sb.setValue(false);
		assertEquals(0, this.sb.hashCode());

		this.sb.setValue(true);
		assertEquals(1, this.sb.hashCode());
	}

	public void testWaitUntilTrue() throws Exception {
		this.verifyWaitUntilTrue(0);
		// no timeout occurs...
		assertFalse(this.timeoutOccurred);
		// ...and the value should be set to true by t2
		assertTrue(this.sb.getValue());
		// make a reasonable guess about how long t2 took
		assertTrue(this.elapsedTime() > 150);
	}

	public void testWaitUntilTrueTimeout() throws Exception {
		this.verifyWaitUntilTrue(20);
		// timeout occurs...
		assertTrue(this.timeoutOccurred);
		// ...and the value will eventually be set to true by t1
		assertTrue(this.sb.getValue());
		// make a reasonable guess about how long t2 took
		assertTrue(this.elapsedTime() < 150);
	}

	private void verifyWaitUntilTrue(long timeout) throws Exception {
		this.sb.setFalse();
		Runnable r1 = this.buildRunnable(this.buildSetTrueCommand(), this.sb, 200);
		Runnable r2 = this.buildRunnable(this.buildWaitUntilTrueCommand(timeout), this.sb, 0);
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		t1.start();
		t2.start();
		while (t1.isAlive() || t2.isAlive()) {
			Thread.sleep(50);
		}
		assertFalse(this.exCaught);
	}

	public void testWaitToSetFalse() throws Exception {
		this.verifyWaitToSetFalse(0);
		// no timeout occurs...
		assertFalse(this.timeoutOccurred);
		// ...and the value should be set to false by t2
		assertFalse(this.sb.getValue());
		// make a reasonable guess about how long t2 took
		assertTrue(this.elapsedTime() > 150);
	}

	public void testWaitToSetFalseTimeout() throws Exception {
		this.verifyWaitToSetFalse(20);
		// timeout occurs...
		assertTrue(this.timeoutOccurred);
		// ...and the value will eventually be set to true by t1
		assertTrue(this.sb.getValue());
		// make a reasonable guess about how long t2 took
		assertTrue(this.elapsedTime() < 150);
	}

	private void verifyWaitToSetFalse(long timeout) throws Exception {
		this.sb.setFalse();
		Runnable r1 = this.buildRunnable(this.buildSetTrueCommand(), this.sb, 200);
		Runnable r2 = this.buildRunnable(this.buildWaitToSetFalseCommand(timeout), this.sb, 0);
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		t1.start();
		t2.start();
		while (t1.isAlive() || t2.isAlive()) {
			Thread.sleep(50);
		}
		assertFalse(this.exCaught);
	}

	private Command buildSetTrueCommand() {
		return new Command() {
			public void execute(SynchronizedBoolean syncBool) {
				syncBool.setTrue();
			}
		};
	}

	private Command buildWaitUntilTrueCommand(final long timeout) {
		return new Command() {
			public void execute(SynchronizedBoolean syncBool) throws Exception {
				SynchronizedBooleanTests.this.setStartTime(System.currentTimeMillis());
				SynchronizedBooleanTests.this.setTimeoutOccurred( ! syncBool.waitUntilTrue(timeout));
				SynchronizedBooleanTests.this.setEndTime(System.currentTimeMillis());
			}
		};
	}

	private Command buildWaitToSetFalseCommand(final long timeout) {
		return new Command() {
			public void execute(SynchronizedBoolean syncBool) throws Exception {
				SynchronizedBooleanTests.this.setStartTime(System.currentTimeMillis());
				SynchronizedBooleanTests.this.setTimeoutOccurred( ! syncBool.waitToSetFalse(timeout));
				SynchronizedBooleanTests.this.setEndTime(System.currentTimeMillis());
			}
		};
	}

	private Runnable buildRunnable(final Command command, final SynchronizedBoolean syncBool, final long sleep) {
		return new Runnable() {
			public void run() {
				try {
					if (sleep != 0) {
						Thread.sleep(sleep);
					}
					command.execute(syncBool);
				} catch (Exception ex) {
					SynchronizedBooleanTests.this.setExCaught(true);
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

	long elapsedTime() {
		return this.endTime - this.startTime;
	}


	// ********** Command interface **********

	private interface Command {
		void execute(SynchronizedBoolean syncBool) throws Exception;
	}

}
