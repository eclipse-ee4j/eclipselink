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
import java.util.Iterator;
import java.util.List;

import javax.swing.JList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListCollectionValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;


public class ListCollectionValueModelAdapterTests extends TestCase {
	CollectionValueModel adapter;
	private ListValueModel wrappedListHolder;
	private List wrappedList;

	public static Test suite() {
		return new TestSuite(ListCollectionValueModelAdapterTests.class);
	}
	
	public ListCollectionValueModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.wrappedList = new ArrayList();
		this.wrappedListHolder = new SimpleListValueModel(this.wrappedList);
		this.adapter = new ListCollectionValueModelAdapter(this.wrappedListHolder);
	}

	private Collection wrappedCollection() {
		return CollectionTools.collection(this.wrappedList.iterator());
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetValue() {
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, new TestListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				// override failure
			}
		});
		this.wrappedListHolder.addItem(0, "foo");
		this.wrappedListHolder.addItem(1, "bar");
		this.wrappedListHolder.addItem(2, "baz");
		Collection adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(3, adapterCollection.size());
		assertEquals(this.wrappedCollection(), adapterCollection);
	}

	public void testStaleValue() {
		CollectionChangeListener listener = new TestListener() {
			public void itemsAdded(CollectionChangeEvent e) {/* OK */}
		};
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, listener);
		this.wrappedListHolder.addItem(0, "foo");
		this.wrappedListHolder.addItem(1, "bar");
		this.wrappedListHolder.addItem(2, "baz");
		Collection adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(3, adapterCollection.size());
		assertEquals(this.wrappedCollection(), adapterCollection);

		this.adapter.removeCollectionChangeListener(ValueModel.VALUE, listener);
		adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(0, adapterCollection.size());
		assertEquals(new HashBag(), adapterCollection);

		this.adapter.addCollectionChangeListener(ValueModel.VALUE, listener);
		adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(3, adapterCollection.size());
		assertEquals(this.wrappedCollection(), adapterCollection);
	}

	public void testAddItem() {
		Bag synchCollection = new SynchronizedBag(this.adapter);
		List synchList = new SynchronizedList(this.wrappedListHolder);
		this.wrappedListHolder.addItem(0, "foo");
		assertTrue(this.wrappedList.contains("foo"));
		this.wrappedListHolder.addItem(1, "bar");
		this.wrappedListHolder.addItem(2, "baz");
		this.wrappedListHolder.addItem(3, "joo");
		this.wrappedListHolder.addItem(4, "jar");
		this.wrappedListHolder.addItem(5, "jaz");
		assertEquals(6, this.wrappedList.size());

		Collection adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(this.wrappedCollection(), adapterCollection);
		assertEquals(this.wrappedCollection(), CollectionTools.collection(synchList.iterator()));
		assertEquals(this.wrappedCollection(), synchCollection);
	}

	public void testRemoveItem() {
		Bag synchCollection = new SynchronizedBag(this.adapter);
		List synchList = new SynchronizedList(this.wrappedListHolder);
		this.wrappedListHolder.addItem(0, "foo");
		this.wrappedListHolder.addItem(1, "bar");
		this.wrappedListHolder.addItem(2, "baz");
		this.wrappedListHolder.addItem(3, "joo");
		this.wrappedListHolder.addItem(4, "jar");
		this.wrappedListHolder.addItem(5, "jaz");
		assertEquals("jaz", this.wrappedListHolder.removeItem(5));
		assertFalse(this.wrappedList.contains("jaz"));
		assertEquals("foo", this.wrappedListHolder.removeItem(0));
		assertFalse(this.wrappedList.contains("foo"));
		assertEquals(4, this.wrappedList.size());

		Collection adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(this.wrappedCollection(), adapterCollection);
		assertEquals(this.wrappedCollection(), CollectionTools.collection(synchList.iterator()));
		assertEquals(this.wrappedCollection(), synchCollection);
	}

	public void testListSynch() {
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, new TestListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				// override failure
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				// override failure
			}
		});
		this.wrappedListHolder.addItem(0, "foo");
		this.wrappedListHolder.addItem(1, "bar");
		this.wrappedListHolder.addItem(2, "baz");
		this.wrappedListHolder.addItem(3, "joo");
		this.wrappedListHolder.addItem(4, "jar");
		this.wrappedListHolder.addItem(5, "jaz");
		this.wrappedListHolder.removeItem(5);
		assertFalse(this.wrappedList.contains("jaz"));
		this.wrappedListHolder.removeItem(0);
		assertFalse(this.wrappedList.contains("foo"));
		assertEquals(4, this.wrappedList.size());

		Collection adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(this.wrappedCollection(), adapterCollection);
	}

	public void testReplaceItem() {
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, new TestListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				// override failure
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				// override failure
			}
		});
		this.wrappedListHolder.addItem(0, "foo");
		this.wrappedListHolder.addItem(1, "bar");
		this.wrappedListHolder.addItem(2, "baz");
		Collection adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(3, adapterCollection.size());
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, new TestListener() {
			public void itemsRemoved(CollectionChangeEvent e) {
				assertEquals("foo", e.items().next());
				assertFalse(CollectionTools.contains((Iterator) ListCollectionValueModelAdapterTests.this.adapter.getValue(), "joo"));
				assertEquals(2, ListCollectionValueModelAdapterTests.this.adapter.size());
			}
			public void itemsAdded(CollectionChangeEvent e) {
				assertEquals("joo", e.items().next());
				assertEquals(3, ListCollectionValueModelAdapterTests.this.adapter.size());
			}
		});
		this.wrappedListHolder.replaceItem(0, "joo");
		adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(3, adapterCollection.size());
		assertEquals(this.wrappedCollection(), adapterCollection);
	}

	public void testHasListeners() {
		assertFalse(((AbstractModel) this.adapter).hasAnyCollectionChangeListeners(ValueModel.VALUE));
		SynchronizedBag synchCollection = new SynchronizedBag(this.adapter);
		assertTrue(((AbstractModel) this.adapter).hasAnyCollectionChangeListeners(ValueModel.VALUE));
		this.adapter.removeCollectionChangeListener(ValueModel.VALUE, synchCollection);
		assertFalse(((AbstractModel) this.adapter).hasAnyCollectionChangeListeners(ValueModel.VALUE));
		this.adapter.addCollectionChangeListener(synchCollection);
		assertTrue(((AbstractModel) this.adapter).hasAnyCollectionChangeListeners(ValueModel.VALUE));
		this.adapter.removeCollectionChangeListener(synchCollection);
		assertFalse(((AbstractModel) this.adapter).hasAnyCollectionChangeListeners(ValueModel.VALUE));
	}
	
	public void testListChangedToEmpty() {
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, new TestListener() {
			public void itemsAdded(CollectionChangeEvent e) {/* OK */}
			public void itemsRemoved(CollectionChangeEvent e) {/* OK */}
		});
		this.wrappedListHolder.addItem(0, "foo");
		this.wrappedListHolder.addItem(1, "bar");
		this.wrappedListHolder.addItem(2, "baz");
		JList jList = new JList(new ListModelAdapter(this.adapter));
		((SimpleListValueModel) this.wrappedListHolder).setValue(new ArrayList());
		assertEquals(0, jList.getModel().getSize());
	}
	
	public void testCollectionChangedFromEmpty() {
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, new TestListener() {
			public void itemsAdded(CollectionChangeEvent e) {/* OK */}
			public void itemsRemoved(CollectionChangeEvent e) {/* OK */}
		});
		JList jList = new JList(new ListModelAdapter(this.adapter));
		
		ArrayList list = new ArrayList();
		list.add("foo");
		list.add("bar");
		((SimpleListValueModel) this.wrappedListHolder).setValue(list);
		assertEquals(2, jList.getModel().getSize());
	}
	
	public void testCollectionChangedFromEmptyToEmpty() {
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, new TestListener() {
			public void itemsAdded(CollectionChangeEvent e) {/* OK */}
			public void itemsRemoved(CollectionChangeEvent e) {/* OK */}
		});
		JList jList = new JList(new ListModelAdapter(this.adapter));
		
		ArrayList list = new ArrayList();
		((SimpleListValueModel) this.wrappedListHolder).setValue(list);
		assertEquals(0, jList.getModel().getSize());
	}


// ********** inner class **********

private class TestListener implements CollectionChangeListener {
	public void collectionChanged(CollectionChangeEvent e) {
		fail("unexpected event");
	}
	public void itemsAdded(CollectionChangeEvent e) {
		fail("unexpected event");
	}
	public void itemsRemoved(CollectionChangeEvent e) {
		fail("unexpected event");
	}
}

}
