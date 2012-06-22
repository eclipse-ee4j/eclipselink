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

import org.eclipse.persistence.tools.workbench.utility.Counter;

public class CounterTests extends TestCase {

	public static Test suite() {
		return new TestSuite(CounterTests.class);
	}
	
	public CounterTests(String name) {
		super(name);
	}

	public void testCtors() {
		Counter counter;
		counter = new Counter();
		assertEquals(0, counter.count());
		counter = new Counter(7);
		assertEquals(7, counter.count());
		counter = new Counter(-7);
		assertEquals(-7, counter.count());
	}

	public void testIncrement() {
		Counter counter;
		int count;
		counter = new Counter();
		assertEquals(0, counter.count());

		count = counter.increment(3);
		assertEquals(3, count);
		assertEquals(3, counter.count());

		count = counter.increment();
		assertEquals(4, count);
		assertEquals(4, counter.count());

		count = counter.increment(-7);
		assertEquals(-3, count);
		assertEquals(-3, counter.count());
	}

	public void testDecrement() {
		Counter counter;
		int count;
		counter = new Counter();
		assertEquals(0, counter.count());

		count = counter.decrement(3);
		assertEquals(-3, count);
		assertEquals(-3, counter.count());

		count = counter.decrement();
		assertEquals(-4, count);
		assertEquals(-4, counter.count());

		count = counter.decrement(-7);
		assertEquals(3, count);
		assertEquals(3, counter.count());
	}

	public void testClone() {
		Counter counter = new Counter(44);
		Counter counter2 = (Counter) counter.clone();
		assertEquals(44, counter2.count());
		assertEquals(counter, counter2);
		assertNotSame(counter, counter2);
	}

	public void testEquals() {
		Counter counter = new Counter(44);
		Counter counter2 = new Counter(44);
		assertEquals(counter, counter2);
		assertEquals(counter.hashCode(), counter2.hashCode());
	}

	public void testSerialization() throws Exception {
		Counter counter = new Counter(44);
		Counter counter2 = (Counter) TestTools.serialize(counter);
		assertEquals(44, counter2.count());
		assertEquals(counter, counter2);
		assertNotSame(counter, counter2);
	}

}
