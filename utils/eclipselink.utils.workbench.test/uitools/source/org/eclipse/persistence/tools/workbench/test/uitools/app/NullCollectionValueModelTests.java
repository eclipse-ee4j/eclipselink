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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.NullCollectionValueModel;


public class NullCollectionValueModelTests extends TestCase {
	private CollectionValueModel collectionHolder;

	public static Test suite() {
		return new TestSuite(NullCollectionValueModelTests.class);
	}
	
	public NullCollectionValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.collectionHolder = NullCollectionValueModel.instance();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testAddItem() {
		boolean exCaught = false;
		try {
			this.collectionHolder.addItem("foo");
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testAddItems() {
		boolean exCaught = false;
		Collection items = new ArrayList();
		items.add("foo");
		items.add("bar");
		items.add("baz");
		try {
			this.collectionHolder.addItems(items);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testRemoveItem() {
		boolean exCaught = false;
		try {
			this.collectionHolder.removeItem("foo");
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testRemoveItems() {
		boolean exCaught = false;
		Collection items = new ArrayList();
		items.add("foo");
		items.add("bar");
		items.add("baz");
		try {
			this.collectionHolder.removeItems(items);
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testSize() {
		assertEquals(0, this.collectionHolder.size());
	}

	public void testGetValue() {
		assertFalse(((Iterator) this.collectionHolder.getValue()).hasNext());
	}

}
