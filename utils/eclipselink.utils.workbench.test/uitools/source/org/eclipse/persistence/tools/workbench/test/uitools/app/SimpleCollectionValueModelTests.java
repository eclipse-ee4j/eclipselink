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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;


public class SimpleCollectionValueModelTests extends TestCase {
	private CollectionValueModel bagHolder;
	CollectionChangeEvent bagEvent;
	String bagEventType;

	private CollectionValueModel setHolder;
	CollectionChangeEvent setEvent;
	String setEventType;

	private static final String ADD = "add";
	private static final String REMOVE = "remove";
	private static final String CHANGE = "change";

	public static Test suite() {
		return new TestSuite(SimpleCollectionValueModelTests.class);
	}
	
	public SimpleCollectionValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.bagHolder = new SimpleCollectionValueModel(this.buildBag());
		this.setHolder = new SimpleCollectionValueModel(this.buildSet());
	}

	private Bag buildBag() {
		Bag result = new HashBag();
		this.addItemsTo(result);
		return result;
	}

	private Set buildSet() {
		Set result = new HashSet();
		this.addItemsTo(result);
		return result;
	}

	private void addItemsTo(Collection c) {
		c.add("foo");
		c.add("bar");
		c.add("baz");
	}

	private Bag buildAddItems() {
		Bag result = new HashBag();
		result.add("joo");
		result.add("jar");
		result.add("jaz");
		return result;
	}

	private Bag buildRemoveItems() {
		Bag result = new HashBag();
		result.add("foo");
		result.add("baz");
		return result;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetValue() {
		assertEquals(this.buildBag(), CollectionTools.bag((Iterator) this.bagHolder.getValue()));
		assertEquals(this.buildSet(), CollectionTools.set((Iterator) this.setHolder.getValue()));
	}

	public void testSize() {
		assertEquals(this.buildBag().size(), CollectionTools.size((Iterator) this.bagHolder.getValue()));
		assertEquals(this.buildSet().size(), CollectionTools.size((Iterator) this.setHolder.getValue()));
	}

	private boolean bagHolderContains(Object item) {
		return CollectionTools.contains((Iterator) this.bagHolder.getValue(), item);
	}

	private boolean setHolderContains(Object item) {
		return CollectionTools.contains((Iterator) this.setHolder.getValue(), item);
	}

	private boolean bagHolderContainsAll(Collection items) {
		return CollectionTools.containsAll((Iterator) this.bagHolder.getValue(), items);
	}

	private boolean setHolderContainsAll(Collection items) {
		return CollectionTools.containsAll((Iterator) this.setHolder.getValue(), items);
	}

	private boolean bagHolderContainsAny(Collection items) {
		Bag bag = CollectionTools.bag((Iterator) this.bagHolder.getValue());
		for (Iterator stream = items.iterator(); stream.hasNext(); ) {
			if (bag.contains(stream.next())) {
				return true;
			}
		}
		return false;
	}

	private boolean setHolderContainsAny(Collection items) {
		Set set = CollectionTools.set((Iterator) this.setHolder.getValue());
		for (Iterator stream = items.iterator(); stream.hasNext(); ) {
			if (set.contains(stream.next())) {
				return true;
			}
		}
		return false;
	}

	public void testAddItem() {
		assertFalse(this.bagHolderContains("joo"));
		this.bagHolder.addItem("joo");
		assertTrue(this.bagHolderContains("joo"));

		assertFalse(this.bagHolderContains(null));
		this.bagHolder.addItem(null);
		assertTrue(this.bagHolderContains(null));

		assertFalse(this.setHolderContains("joo"));
		this.setHolder.addItem("joo");
		assertTrue(this.setHolderContains("joo"));

		assertFalse(this.setHolderContains(null));
		this.setHolder.addItem(null);
		assertTrue(this.setHolderContains(null));
	}

	public void testAddItems() {
		assertFalse(this.bagHolderContainsAny(this.buildAddItems()));
		this.bagHolder.addItems(this.buildAddItems());
		assertTrue(this.bagHolderContainsAll(this.buildAddItems()));

		assertFalse(this.setHolderContainsAny(this.buildAddItems()));
		this.setHolder.addItems(this.buildAddItems());
		assertTrue(this.setHolderContainsAll(this.buildAddItems()));
	}

	public void testRemoveItem() {
		assertTrue(this.bagHolderContains("bar"));
		this.bagHolder.removeItem("bar");
		assertFalse(this.bagHolderContains("bar"));

		this.bagHolder.addItem(null);
		assertTrue(this.bagHolderContains(null));
		this.bagHolder.removeItem(null);
		assertFalse(this.bagHolderContains(null));

		assertTrue(this.setHolderContains("bar"));
		this.setHolder.removeItem("bar");
		assertFalse(this.setHolderContains("bar"));

		this.setHolder.addItem(null);
		assertTrue(this.setHolderContains(null));
		this.setHolder.removeItem(null);
		assertFalse(this.setHolderContains(null));
	}

	public void testRemoveItems() {
		assertTrue(this.bagHolderContainsAll(this.buildRemoveItems()));
		this.bagHolder.removeItems(this.buildRemoveItems());
		assertFalse(this.bagHolderContainsAny(this.buildRemoveItems()));

		assertTrue(this.setHolderContainsAll(this.buildRemoveItems()));
		this.setHolder.removeItems(this.buildRemoveItems());
		assertFalse(this.setHolderContainsAny(this.buildRemoveItems()));
	}

	public void testSetValue() {
		assertTrue(this.bagHolderContains("bar"));
		assertFalse(this.bagHolderContains("jar"));
		((SimpleCollectionValueModel) this.bagHolder).setValue(this.buildAddItems());
		assertFalse(this.bagHolderContains("bar"));
		assertTrue(this.bagHolderContains("jar"));

		this.bagHolder.addItem(null);
		assertTrue(this.bagHolderContains(null));
		this.bagHolder.removeItem(null);
		assertFalse(this.bagHolderContains(null));

		((SimpleCollectionValueModel) this.bagHolder).setValue(null);
		assertFalse(this.bagHolderContains("jar"));

		assertTrue(this.setHolderContains("bar"));
		assertFalse(this.setHolderContains("jar"));
		((SimpleCollectionValueModel) this.setHolder).setValue(this.buildAddItems());
		assertFalse(this.setHolderContains("bar"));
		assertTrue(this.setHolderContains("jar"));

		this.setHolder.addItem(null);
		assertTrue(this.setHolderContains(null));
		this.setHolder.removeItem(null);
		assertFalse(this.setHolderContains(null));

		((SimpleCollectionValueModel) this.setHolder).setValue(null);
		assertFalse(this.setHolderContains("jar"));
	}

	public void testCollectionChange1() {
		this.bagHolder.addCollectionChangeListener(this.buildBagListener());
		this.verifyBagChange();

		this.setHolder.addCollectionChangeListener(this.buildSetListener());
		this.verifySetChange();
	}

	public void testCollectionChange2() {
		this.bagHolder.addCollectionChangeListener(ValueModel.VALUE, this.buildBagListener());
		this.verifyBagChange();

		this.setHolder.addCollectionChangeListener(ValueModel.VALUE, this.buildSetListener());
		this.verifySetChange();
	}

	private void verifyBagChange() {
		this.bagEvent = null;
		this.bagEventType = null;
		this.bagHolder.addItem("foo");
		this.verifyBagEvent(ADD, "foo");

		this.bagEvent = null;
		this.bagEventType = null;
		this.bagHolder.addItem("foo");
		this.verifyBagEvent(ADD, "foo");

		this.bagEvent = null;
		this.bagEventType = null;
		this.bagHolder.addItem("joo");
		this.verifyBagEvent(ADD, "joo");

		this.bagEvent = null;
		this.bagEventType = null;
		this.bagHolder.addItem(null);
		this.verifyBagEvent(ADD, null);

		this.bagEvent = null;
		this.bagEventType = null;
		this.bagHolder.addItem(null);
		this.verifyBagEvent(ADD, null);

		this.bagEvent = null;
		this.bagEventType = null;
		this.bagHolder.removeItem("joo");
		this.verifyBagEvent(REMOVE, "joo");

		this.bagEvent = null;
		this.bagEventType = null;
		this.bagHolder.removeItem(null);
		this.verifyBagEvent(REMOVE, null);

		this.bagEvent = null;
		this.bagEventType = null;
		((SimpleCollectionValueModel) this.bagHolder).setValue(this.buildBag());
		this.verifyBagEvent(CHANGE);

		this.bagEvent = null;
		this.bagEventType = null;
		this.bagHolder.addItems(this.buildBag());
		this.verifyBagEvent(ADD);
		assertEquals(this.buildBag(), CollectionTools.bag(this.bagEvent.items()));
	}

	private void verifySetChange() {
		this.setEvent = null;
		this.setEventType = null;
		this.setHolder.addItem("foo");
		assertNull(this.setEvent);
		assertNull(this.setEventType);

		this.setEvent = null;
		this.setEventType = null;
		this.setHolder.addItem("joo");
		this.verifySetEvent(ADD, "joo");

		this.setEvent = null;
		this.setEventType = null;
		this.setHolder.addItem("joo");
		assertNull(this.setEvent);
		assertNull(this.setEventType);

		this.setEvent = null;
		this.setEventType = null;
		this.setHolder.addItem(null);
		this.verifySetEvent(ADD, null);

		this.setEvent = null;
		this.setEventType = null;
		this.setHolder.addItem(null);
		assertNull(this.setEvent);
		assertNull(this.setEventType);

		this.setEvent = null;
		this.setEventType = null;
		this.setHolder.removeItem("joo");
		this.verifySetEvent(REMOVE, "joo");

		this.setEvent = null;
		this.setEventType = null;
		this.setHolder.removeItem("joo");
		assertNull(this.setEvent);
		assertNull(this.setEventType);

		this.setEvent = null;
		this.setEventType = null;
		this.setHolder.removeItem(null);
		this.verifySetEvent(REMOVE, null);

		this.setEvent = null;
		this.setEventType = null;
		((SimpleCollectionValueModel) this.setHolder).setValue(this.buildSet());
		this.verifySetEvent(CHANGE);

		this.setEvent = null;
		this.setEventType = null;
		this.setHolder.addItems(this.buildSet());
		assertNull(this.setEvent);
		assertNull(this.setEventType);
	}

	private CollectionChangeListener buildBagListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				SimpleCollectionValueModelTests.this.bagEventType = ADD;
				SimpleCollectionValueModelTests.this.bagEvent = e;
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				SimpleCollectionValueModelTests.this.bagEventType = REMOVE;
				SimpleCollectionValueModelTests.this.bagEvent = e;
			}
			public void collectionChanged(CollectionChangeEvent e) {
				SimpleCollectionValueModelTests.this.bagEventType = CHANGE;
				SimpleCollectionValueModelTests.this.bagEvent = e;
			}
		};
	}

	private CollectionChangeListener buildSetListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				SimpleCollectionValueModelTests.this.setEventType = ADD;
				SimpleCollectionValueModelTests.this.setEvent = e;
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				SimpleCollectionValueModelTests.this.setEventType = REMOVE;
				SimpleCollectionValueModelTests.this.setEvent = e;
			}
			public void collectionChanged(CollectionChangeEvent e) {
				SimpleCollectionValueModelTests.this.setEventType = CHANGE;
				SimpleCollectionValueModelTests.this.setEvent = e;
			}
		};
	}

	private void verifyBagEvent(String eventType) {
		assertEquals(eventType, this.bagEventType);
		assertEquals(this.bagHolder, this.bagEvent.getSource());
		assertEquals(ValueModel.VALUE, this.bagEvent.getCollectionName());
	}

	private void verifyBagEvent(String eventType, Object item) {
		this.verifyBagEvent(eventType);
		assertEquals(item, this.bagEvent.items().next());
	}

	private void verifySetEvent(String eventType) {
		assertEquals(eventType, this.setEventType);
		assertEquals(this.setHolder, this.setEvent.getSource());
		assertEquals(ValueModel.VALUE, this.setEvent.getCollectionName());
	}

	private void verifySetEvent(String eventType, Object item) {
		this.verifySetEvent(eventType);
		assertEquals(item, this.setEvent.items().next());
	}

}
