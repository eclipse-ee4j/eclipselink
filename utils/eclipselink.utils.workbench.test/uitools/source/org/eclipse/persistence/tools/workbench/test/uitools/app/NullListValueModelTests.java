/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.NullListValueModel;


public class NullListValueModelTests extends TestCase {
	private ListValueModel listHolder;

	public static Test suite() {
		return new TestSuite(NullListValueModelTests.class);
	}
	
	public NullListValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.listHolder = NullListValueModel.instance();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testAddItem() {
		boolean exCaught = false;
		try {
			this.listHolder.addItem(0, "foo");
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testAddItems() {
		boolean exCaught = false;
		List items = new ArrayList();
		items.add("foo");
		items.add("bar");
		items.add("baz");
		try {
			this.listHolder.addItems(0, items);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testRemoveItem() {
		boolean exCaught = false;
		try {
			this.listHolder.removeItem(0);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testRemoveItems() {
		boolean exCaught = false;
		try {
			this.listHolder.removeItems(0, 3);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testReplaceItem() {
		boolean exCaught = false;
		try {
			this.listHolder.replaceItem(0, "foo");
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testReplaceItems() {
		boolean exCaught = false;
		List items = new ArrayList();
		items.add("foo");
		items.add("bar");
		items.add("baz");
		try {
			this.listHolder.replaceItems(0, items);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testGetItem() {
		boolean exCaught = false;
		try {
			this.listHolder.getItem(0);
		} catch (IndexOutOfBoundsException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testSize() {
		assertEquals(0, this.listHolder.size());
	}

	public void testGetValue() {
		assertFalse(((ListIterator) this.listHolder.getValue()).hasNext());
	}

}
