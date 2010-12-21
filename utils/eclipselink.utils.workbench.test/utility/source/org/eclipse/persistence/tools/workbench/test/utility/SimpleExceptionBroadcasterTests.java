/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.ExceptionListener;
import org.eclipse.persistence.tools.workbench.utility.SimpleExceptionBroadcaster;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

public class SimpleExceptionBroadcasterTests extends TestCase {
	private SimpleExceptionBroadcaster broadcaster;
	private int localListenerCount;
	private ExceptionListener[] localListeners;
	private boolean[] localListenersNotified;

	public static Test suite() {
		return new TestSuite(SimpleExceptionBroadcasterTests.class);
	}

	public SimpleExceptionBroadcasterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.broadcaster = new SimpleExceptionBroadcaster();
		this.localListenerCount = 5;
		this.localListeners = new ExceptionListener[this.localListenerCount];
		this.localListenersNotified = new boolean[this.localListenerCount];
		for (int i = 0; i < this.localListenerCount; i++) {
			this.localListeners[i] = new TestExceptionListener(i);
			this.localListenersNotified[i] = false;
		}
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testAddListener() throws Exception {
		assertEquals(0, this.broadcaster.getExceptionListeners().length);
		for (int i = 0; i < this.localListenerCount; i++) {
			ExceptionListener listener = this.localListeners[i];
			assertFalse(CollectionTools.contains(this.broadcaster.getExceptionListeners(), listener));
			this.broadcaster.addExceptionListener(listener);
			assertTrue(CollectionTools.contains(this.broadcaster.getExceptionListeners(), listener));
		}
		boolean exCaught = false;
		try {
			this.broadcaster.addExceptionListener(null);
		} catch (NullPointerException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testRemoveListener() throws Exception {
		assertEquals(0, this.broadcaster.getExceptionListeners().length);
		for (int i = 0; i < this.localListenerCount; i++) {
			this.broadcaster.addExceptionListener(this.localListeners[i]);
		}

		boolean exCaught = false;
		try {
			this.broadcaster.removeExceptionListener(null);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);

		exCaught = false;
		try {
			this.broadcaster.removeExceptionListener(new TestExceptionListener(77));
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);

		for (int i = 0; i < this.localListenerCount; i++) {
			ExceptionListener listener = this.localListeners[i];
			assertTrue(CollectionTools.contains(this.broadcaster.getExceptionListeners(), listener));
			this.broadcaster.removeExceptionListener(listener);
			assertFalse(CollectionTools.contains(this.broadcaster.getExceptionListeners(), listener));
		}
		assertEquals(0, this.broadcaster.getExceptionListeners().length);

		exCaught = false;
		try {
			this.broadcaster.removeExceptionListener(new TestExceptionListener(77));
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	// remove from middle and double-check state
	public void testRemoveListener2() throws Exception {
		assertEquals(0, this.broadcaster.getExceptionListeners().length);
		for (int i = 0; i < this.localListenerCount; i++) {
			this.broadcaster.addExceptionListener(this.localListeners[i]);
		}
		int removed = 2;
		this.broadcaster.removeExceptionListener(this.localListeners[removed]);
		for (int i = 0; i < this.localListenerCount; i++) {
			ExceptionListener listener = this.localListeners[i];
			if (i == removed) {
				assertFalse(CollectionTools.contains(this.broadcaster.getExceptionListeners(), listener));
			} else {
				assertTrue(CollectionTools.contains(this.broadcaster.getExceptionListeners(), listener));
			}
		}
	}

	public void testBroadcast() throws Exception {
		for (int i = 0; i < this.localListenerCount; i++) {
			this.broadcaster.addExceptionListener(this.localListeners[i]);
		}
		this.broadcaster.broadcast(Thread.currentThread(), new IllegalStateException());
		for (int i = 0; i < this.localListenerCount; i++) {
			assertTrue("listener not notified: " + i, this.localListenersNotified[i]);
		}
	}

	public void testHasListeners() throws Exception {
		assertFalse(this.broadcaster.hasExceptionListeners());
		for (int i = 0; i < this.localListenerCount; i++) {
			this.broadcaster.addExceptionListener(this.localListeners[i]);
		}
		assertTrue(this.broadcaster.hasExceptionListeners());
	}

	public void testHasNoListeners() throws Exception {
		assertTrue(this.broadcaster.hasNoExceptionListeners());
		for (int i = 0; i < this.localListenerCount; i++) {
			this.broadcaster.addExceptionListener(this.localListeners[i]);
		}
		assertFalse(this.broadcaster.hasNoExceptionListeners());
	}

	void localListenerNotified(int number) {
		this.localListenersNotified[number] = true;
	}


	private class TestExceptionListener implements ExceptionListener {
		private int number;
		TestExceptionListener(int number) {
			super();
			this.number = number;
		}
		public void exceptionThrown(Thread thread, Throwable exception) {
			SimpleExceptionBroadcasterTests.this.localListenerNotified(this.number);
		}
		public String toString() {
			return StringTools.buildToStringFor(this, new Integer(this.number));
		}
	}

}
