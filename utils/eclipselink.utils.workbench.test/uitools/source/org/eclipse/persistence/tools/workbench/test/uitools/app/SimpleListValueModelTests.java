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
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;


public class SimpleListValueModelTests extends TestCase {
	private ListValueModel listHolder;
	ListChangeEvent event;
	String eventType;

	private static final String ADD = "add";
	private static final String REMOVE = "remove";
	private static final String REPLACE = "replace";
	private static final String CHANGE = "change";

	public static Test suite() {
		return new TestSuite(SimpleListValueModelTests.class);
	}
	
	public SimpleListValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.listHolder = new SimpleListValueModel(this.buildList());
	}

	private List buildList() {
		List result = new ArrayList();
		result.add("foo");
		result.add("bar");
		result.add("baz");
		return result;
	}

	private List buildAddList() {
		List result = new ArrayList();
		result.add("joo");
		result.add("jar");
		result.add("jaz");
		return result;
	}

	private List buildRemoveList() {
		List result = new ArrayList();
		result.add("foo");
		result.add("bar");
		return result;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetValue() {
		assertEquals(this.buildList(), CollectionTools.list((Iterator) this.listHolder.getValue()));
	}

	public void testSize() {
		assertEquals(this.buildList().size(), CollectionTools.size((Iterator) this.listHolder.getValue()));
	}

	private boolean listContains(Object item) {
		return CollectionTools.contains((Iterator) this.listHolder.getValue(), item);
	}

	private boolean listContainsAll(Collection items) {
		return CollectionTools.containsAll((Iterator) this.listHolder.getValue(), items);
	}

	private boolean listContainsAny(Collection items) {
		List list = CollectionTools.list((ListIterator) this.listHolder.getValue());
		for (Iterator stream = items.iterator(); stream.hasNext(); ) {
			if (list.contains(stream.next())) {
				return true;
			}
		}
		return false;
	}

	public void testAddItem() {
		assertFalse(this.listContains("joo"));
		this.listHolder.addItem(2, "joo");
		assertTrue(this.listContains("joo"));

		assertFalse(this.listContains(null));
		this.listHolder.addItem(0, null);
		assertTrue(this.listContains(null));
	}

	public void testAddItems() {
		assertFalse(this.listContainsAny(this.buildAddList()));
		this.listHolder.addItems(2, this.buildAddList());
		assertTrue(this.listContainsAll(this.buildAddList()));
	}

	public void testRemoveItem() {
		assertTrue(this.listContains("bar"));
		this.listHolder.removeItem(this.buildList().indexOf("bar"));
		assertFalse(this.listContains("bar"));

		this.listHolder.addItem(1, null);
		assertTrue(this.listContains(null));
		this.listHolder.removeItem(1);
		assertFalse(this.listContains(null));
	}

	public void testRemoveItems() {
		assertTrue(this.listContainsAll(this.buildRemoveList()));
		this.listHolder.removeItems(0, 2);
		assertFalse(this.listContainsAny(this.buildRemoveList()));
	}

	public void testSetValue() {
		List newList = new ArrayList();
		newList.add("joo");
		newList.add("jar");
		newList.add("jaz");

		assertTrue(this.listContains("bar"));
		assertFalse(this.listContains("jar"));
		((SimpleListValueModel) this.listHolder).setValue(newList);
		assertFalse(this.listContains("bar"));
		assertTrue(this.listContains("jar"));

		this.listHolder.addItem(1, null);
		assertTrue(this.listContains(null));
		this.listHolder.removeItem(1);
		assertFalse(this.listContains(null));

		((SimpleListValueModel) this.listHolder).setValue(null);
		assertFalse(this.listContains("jar"));
	}

	public void testListChange1() {
		this.listHolder.addListChangeListener(this.buildListener());
		this.verifyListChange();
	}

	public void testListChange2() {
		this.listHolder.addListChangeListener(ValueModel.VALUE, this.buildListener());
		this.verifyListChange();
	}

	private void verifyListChange() {
		this.event = null;
		this.eventType = null;
		this.listHolder.addItem(1, "joo");
		this.verifyEvent(ADD, 1, "joo");

		this.event = null;
		this.eventType = null;
		this.listHolder.addItem(1, null);
		this.verifyEvent(ADD, 1, null);

		this.event = null;
		this.eventType = null;
		this.listHolder.removeItem(1);
		this.verifyEvent(REMOVE, 1, null);

		this.event = null;
		this.eventType = null;
		this.listHolder.removeItem(1);
		this.verifyEvent(REMOVE, 1, "joo");

		this.event = null;
		this.eventType = null;
		((SimpleListValueModel) this.listHolder).setValue(this.buildList());
		this.verifyEvent(CHANGE);

		this.event = null;
		this.eventType = null;
		this.listHolder.addItems(0, this.buildList());
		this.verifyEvent(ADD);
		assertEquals(this.buildList(), CollectionTools.list(this.event.items()));

		this.event = null;
		this.eventType = null;
		this.listHolder.replaceItem(0, "joo");
		this.verifyEvent(REPLACE);
		assertFalse(CollectionTools.contains(this.event.items(), "foo"));
		assertTrue(CollectionTools.contains(this.event.items(), "joo"));
	}

	private ListChangeListener buildListener() {
		return new ListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {
				SimpleListValueModelTests.this.eventType = ADD;
				SimpleListValueModelTests.this.event = e;
			}
			public void itemsRemoved(ListChangeEvent e) {
				SimpleListValueModelTests.this.eventType = REMOVE;
				SimpleListValueModelTests.this.event = e;
			}
			public void itemsReplaced(ListChangeEvent e) {
				SimpleListValueModelTests.this.eventType = REPLACE;
				SimpleListValueModelTests.this.event = e;
			}
			public void listChanged(ListChangeEvent e) {
				SimpleListValueModelTests.this.eventType = CHANGE;
				SimpleListValueModelTests.this.event = e;
			}
		};
	}

	private void verifyEvent(String e) {
		assertEquals(e, this.eventType);
		assertEquals(this.listHolder, this.event.getSource());
		assertEquals(ValueModel.VALUE, this.event.getListName());
	}

	private void verifyEvent(String e, int index, Object item) {
		this.verifyEvent(e);
		assertEquals(index, this.event.getIndex());
		assertEquals(item, this.event.items().next());
	}

}
