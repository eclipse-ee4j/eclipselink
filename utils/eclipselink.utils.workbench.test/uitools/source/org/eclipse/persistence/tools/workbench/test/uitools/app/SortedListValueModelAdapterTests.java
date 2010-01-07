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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.ReverseComparator;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;


public class SortedListValueModelAdapterTests extends TestCase {
	private SortedListValueModelAdapter adapter;
	private CollectionValueModel wrappedCollectionHolder;
	private Collection wrappedCollection;

	public static Test suite() {
		return new TestSuite(SortedListValueModelAdapterTests.class);
	}
	
	public SortedListValueModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.wrappedCollection = new HashBag();
		this.wrappedCollectionHolder = new SimpleCollectionValueModel(this.wrappedCollection);
		this.adapter = new SortedListValueModelAdapter(this.wrappedCollectionHolder);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	private void verifyList(Collection expected, ListValueModel actual) {
		this.verifyList(expected, actual, null);
	}

	private void verifyList(Collection expected, ListValueModel actual, Comparator comparator) {
		Collection sortedSet = new TreeSet(comparator);
		sortedSet.addAll(expected);
		List expectedList = new ArrayList(sortedSet);
		List actualList = CollectionTools.list((ListIterator) actual.getValue());
		assertEquals(expectedList, actualList);
	}

	public void testGetValue() {
		this.adapter.addListChangeListener(ValueModel.VALUE, new TestListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {/* OK */}
			public void itemsReplaced(ListChangeEvent e) {/* OK */}
		});
		this.wrappedCollectionHolder.addItem("foo");
		this.wrappedCollectionHolder.addItem("bar");
		this.wrappedCollectionHolder.addItem("baz");
		assertEquals(3, this.adapter.size());
		this.verifyList(this.wrappedCollection, this.adapter);
	}

	public void testAddItem() {
		List synchList = new SynchronizedList(this.adapter);
		Bag synchCollection = new SynchronizedBag(this.wrappedCollectionHolder);
		this.wrappedCollectionHolder.addItem("foo");
		assertTrue(this.wrappedCollection.contains("foo"));
		this.wrappedCollectionHolder.addItem("bar");
		this.wrappedCollectionHolder.addItem("baz");
		this.wrappedCollectionHolder.addItem("joo");
		this.wrappedCollectionHolder.addItem("jar");
		this.wrappedCollectionHolder.addItem("jaz");
		assertEquals(6, this.wrappedCollection.size());

		this.verifyList(this.wrappedCollection, this.adapter);
		assertEquals(this.wrappedCollection, CollectionTools.collection(synchList.iterator()));
		assertEquals(this.wrappedCollection, synchCollection);
	}

	public void testRemoveItem() {
		List synchList = new SynchronizedList(this.adapter);
		Bag synchCollection = new SynchronizedBag(this.wrappedCollectionHolder);
		this.wrappedCollectionHolder.addItem("foo");
		this.wrappedCollectionHolder.addItem("bar");
		this.wrappedCollectionHolder.addItem("baz");
		this.wrappedCollectionHolder.addItem("joo");
		this.wrappedCollectionHolder.addItem("jar");
		this.wrappedCollectionHolder.addItem("jaz");
		this.wrappedCollectionHolder.removeItem("jaz");
		assertFalse(this.wrappedCollection.contains("jaz"));
		this.wrappedCollectionHolder.removeItem("foo");
		assertFalse(this.wrappedCollection.contains("foo"));
		assertEquals(4, this.wrappedCollection.size());

		this.verifyList(this.wrappedCollection, this.adapter);
		assertEquals(this.wrappedCollection, CollectionTools.collection(synchList.iterator()));
		assertEquals(this.wrappedCollection, synchCollection);
	}

	public void testListSynch() {
		this.adapter.addListChangeListener(ValueModel.VALUE, new TestListChangeListener() {
			public void itemsAdded(ListChangeEvent e) {/* OK */}
			public void itemsRemoved(ListChangeEvent e) {/* OK */}
			public void itemsReplaced(ListChangeEvent e) {/* OK */}
		});
		this.wrappedCollectionHolder.addItem("foo");
		this.wrappedCollectionHolder.addItem("bar");
		this.wrappedCollectionHolder.addItem("baz");
		this.wrappedCollectionHolder.addItem("joo");
		this.wrappedCollectionHolder.addItem("jar");
		this.wrappedCollectionHolder.addItem("jaz");
		this.wrappedCollectionHolder.removeItem("jaz");
		assertFalse(this.wrappedCollection.contains("jaz"));
		this.wrappedCollectionHolder.removeItem("foo");
		assertFalse(this.wrappedCollection.contains("foo"));
		assertEquals(4, this.wrappedCollection.size());

		this.verifyList(this.wrappedCollection, this.adapter);
	}

	public void testSetComparator() {
		List synchList = new SynchronizedList(this.adapter);
		Bag synchCollection = new SynchronizedBag(this.wrappedCollectionHolder);
		this.wrappedCollectionHolder.addItem("foo");
		assertTrue(this.wrappedCollection.contains("foo"));
		this.wrappedCollectionHolder.addItem("bar");
		this.wrappedCollectionHolder.addItem("baz");
		this.wrappedCollectionHolder.addItem("joo");
		this.wrappedCollectionHolder.addItem("jar");
		this.wrappedCollectionHolder.addItem("jaz");
		assertEquals(6, this.wrappedCollection.size());

		this.verifyList(this.wrappedCollection, this.adapter);
		assertEquals(this.wrappedCollection, CollectionTools.collection(synchList.iterator()));
		assertEquals(this.wrappedCollection, synchCollection);

		this.adapter.setComparator(new ReverseComparator());
		this.verifyList(this.wrappedCollection, this.adapter, new ReverseComparator());
		assertEquals(this.wrappedCollection, CollectionTools.collection(synchList.iterator()));
		assertEquals(this.wrappedCollection, synchCollection);
	}

	public void testHasListeners() {
		assertFalse(((AbstractModel) this.adapter).hasAnyListChangeListeners(ValueModel.VALUE));
		SynchronizedList synchList = new SynchronizedList(this.adapter);
		assertTrue(((AbstractModel) this.adapter).hasAnyListChangeListeners(ValueModel.VALUE));
		this.adapter.removeListChangeListener(ValueModel.VALUE, synchList);
		assertFalse(((AbstractModel) this.adapter).hasAnyListChangeListeners(ValueModel.VALUE));
		this.adapter.addListChangeListener(synchList);
		assertTrue(((AbstractModel) this.adapter).hasAnyListChangeListeners(ValueModel.VALUE));
		this.adapter.removeListChangeListener(synchList);
		assertFalse(((AbstractModel) this.adapter).hasAnyListChangeListeners(ValueModel.VALUE));
	}

private class TestListChangeListener implements ListChangeListener {
	public void itemsAdded(ListChangeEvent e) {
		fail("unexpected event");
	}
	public void itemsRemoved(ListChangeEvent e) {
		fail("unexpected event");
	}
	public void itemsReplaced(ListChangeEvent e) {
		fail("unexpected event");
	}
	public void listChanged(ListChangeEvent e) {
		fail("unexpected event");
	}
}

}
