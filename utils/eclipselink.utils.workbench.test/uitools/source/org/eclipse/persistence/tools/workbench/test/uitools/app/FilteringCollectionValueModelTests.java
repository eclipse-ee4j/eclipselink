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

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


public class FilteringCollectionValueModelTests extends TestCase {
	private CollectionValueModel collectionHolder;
	CollectionChangeEvent addEvent;
	CollectionChangeEvent removeEvent;
	CollectionChangeEvent collectionChangedEvent;

	private CollectionValueModel filteredCollectionHolder;
	CollectionChangeEvent filteredAddEvent;
	CollectionChangeEvent filteredRemoveEvent;
	CollectionChangeEvent filteredCollectionChangedEvent;

	public static Test suite() {
		return new TestSuite(FilteringCollectionValueModelTests.class);
	}

	public FilteringCollectionValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.collectionHolder = new SimpleCollectionValueModel(buildCollection());
		this.filteredCollectionHolder = new FilteringCollectionValueModel(this.collectionHolder, this.buildFilter());
	}

	private Collection buildCollection() {
		Collection collection = new Vector();
		collection.add("foo");
		return collection;
	}

	private Filter buildFilter() {
		return new Filter() {
			public boolean accept(Object o) {
				return ((String) o).startsWith("b");
			}
		};
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetValue() {
		// add a listener to "activate" the wrapper
		this.filteredCollectionHolder.addCollectionChangeListener(ValueModel.VALUE, this.buildFilteredListener());

		assertEquals("foo", ((Iterator) this.collectionHolder.getValue()).next());
		assertFalse(((Iterator) this.filteredCollectionHolder.getValue()).hasNext());

		this.collectionHolder.addItem("bar");
		Iterator collectionHolderValue = (Iterator) this.collectionHolder.getValue();
		assertEquals("foo", collectionHolderValue.next());
		assertEquals("bar", collectionHolderValue.next());
		assertTrue(((Iterator) this.filteredCollectionHolder.getValue()).hasNext());
		assertEquals("bar", ((Iterator) this.filteredCollectionHolder.getValue()).next());

		this.collectionHolder.removeItem("bar");
		assertEquals("foo", ((Iterator) this.collectionHolder.getValue()).next());
		assertFalse(((Iterator) this.filteredCollectionHolder.getValue()).hasNext());

		this.collectionHolder.removeItem("foo");
		assertFalse(((Iterator) this.collectionHolder.getValue()).hasNext());
		assertFalse(((Iterator) this.filteredCollectionHolder.getValue()).hasNext());

		this.collectionHolder.addItem("foo");
		assertEquals("foo", ((Iterator) this.collectionHolder.getValue()).next());
		assertFalse(((Iterator) this.filteredCollectionHolder.getValue()).hasNext());
	}

	public void testSetValue() {
		// add a listener to "activate" the wrapper
		this.filteredCollectionHolder.addCollectionChangeListener(ValueModel.VALUE, this.buildFilteredListener());

		Collection newCollection = new Vector();
		newCollection.add("fox");
		newCollection.add("baz");
		
		((SimpleCollectionValueModel) this.collectionHolder).setValue(newCollection);

		Iterator collectionValues = (Iterator) this.collectionHolder.getValue();
		assertEquals("fox", collectionValues.next());
		assertEquals("baz", collectionValues.next());
		Iterator filteredCollectionValues = (Iterator) this.filteredCollectionHolder.getValue();
		assertEquals("baz", filteredCollectionValues.next());
		assertFalse(filteredCollectionValues.hasNext());
	}		

	public void testLazyListening() {
		assertTrue(((AbstractModel) this.collectionHolder).hasNoCollectionChangeListeners(ValueModel.VALUE));
		CollectionChangeListener listener = this.buildFilteredListener();
		this.filteredCollectionHolder.addCollectionChangeListener(listener);
		assertTrue(((AbstractModel) this.collectionHolder).hasAnyCollectionChangeListeners(ValueModel.VALUE));
		this.filteredCollectionHolder.removeCollectionChangeListener(listener);
		assertTrue(((AbstractModel) this.collectionHolder).hasNoCollectionChangeListeners(ValueModel.VALUE));

		this.filteredCollectionHolder.addCollectionChangeListener(ValueModel.VALUE, listener);
		assertTrue(((AbstractModel) this.collectionHolder).hasAnyCollectionChangeListeners(ValueModel.VALUE));
		this.filteredCollectionHolder.removeCollectionChangeListener(ValueModel.VALUE, listener);
		assertTrue(((AbstractModel) this.collectionHolder).hasNoCollectionChangeListeners(ValueModel.VALUE));
	}

	public void testCollectionChange1() {
		this.collectionHolder.addCollectionChangeListener(this.buildListener());
		this.filteredCollectionHolder.addCollectionChangeListener(this.buildFilteredListener());
		this.verifyCollectionChanges();
	}

	public void testCollectionChange2() {
		this.collectionHolder.addCollectionChangeListener(ValueModel.VALUE, this.buildListener());
		this.filteredCollectionHolder.addCollectionChangeListener(ValueModel.VALUE, this.buildFilteredListener());
		this.verifyCollectionChanges();
	}

	private void clearEvents() {
		this.addEvent = null;
		this.removeEvent = null;
		this.collectionChangedEvent = null;
		this.filteredAddEvent = null;
		this.filteredRemoveEvent = null;
		this.filteredCollectionChangedEvent = null;
	}

	private void verifyCollectionChanges() {
		clearEvents();
		this.collectionHolder.addItem("bar");
		Collection tempCollection = new Vector();
		tempCollection.add("bar");
		this.verifyEvent(this.addEvent, this.collectionHolder, tempCollection);
		this.verifyEvent(this.filteredAddEvent, this.filteredCollectionHolder, tempCollection);
		
		clearEvents();
		this.collectionHolder.removeItem("foo");
		tempCollection.remove("bar");
		tempCollection.add("foo");
		this.verifyEvent(this.removeEvent, this.collectionHolder, tempCollection);
		assertNull(this.filteredRemoveEvent);


		clearEvents();
		this.collectionHolder.removeItem("bar");
		tempCollection.add("bar");
		tempCollection.remove("foo");
		this.verifyEvent(this.removeEvent, this.collectionHolder, tempCollection);
		this.verifyEvent(this.filteredRemoveEvent, this.filteredCollectionHolder, tempCollection);


		clearEvents();
		this.collectionHolder.addItem("foo");
		tempCollection.remove("bar");
		tempCollection.add("foo");
		this.verifyEvent(this.addEvent, this.collectionHolder, tempCollection);
		assertNull(this.filteredAddEvent);


		clearEvents();
		Collection newCollection = new Vector();
		newCollection.add("fox");
		newCollection.add("baz");
		
		((SimpleCollectionValueModel) this.collectionHolder).setValue(newCollection);

		this.verifyEvent(this.collectionChangedEvent, this.collectionHolder, new Vector());
		
		tempCollection.remove("foo");
		tempCollection.add("baz");
		this.verifyEvent(this.filteredCollectionChangedEvent, this.filteredCollectionHolder, new Vector());
		
	}

	private CollectionChangeListener buildListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				FilteringCollectionValueModelTests.this.addEvent = e;
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				FilteringCollectionValueModelTests.this.removeEvent = e;
			}

			public void collectionChanged(CollectionChangeEvent e) {
				FilteringCollectionValueModelTests.this.collectionChangedEvent = e;

			}
		};
	}

	private CollectionChangeListener buildFilteredListener() {
		return new CollectionChangeListener() {
			public void itemsAdded(CollectionChangeEvent e) {
				FilteringCollectionValueModelTests.this.filteredAddEvent = e;
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				FilteringCollectionValueModelTests.this.filteredRemoveEvent = e;
			}

			public void collectionChanged(CollectionChangeEvent e) {
				FilteringCollectionValueModelTests.this.filteredCollectionChangedEvent = e;

			}
		};
	}

	private void verifyEvent(CollectionChangeEvent event, Object source, Object items) {
		assertEquals(source, event.getSource());
		assertEquals(ValueModel.VALUE, event.getCollectionName());
		assertEquals(items, CollectionTools.vector(event.items()));
	}

	public void testRemoveFilteredItem() {
		// build collection with TestItems
		this.collectionHolder = new SimpleCollectionValueModel(this.buildCollection2());
		this.filteredCollectionHolder = new FilteringCollectionValueModel(this.collectionHolder, this.buildFilter2());
		// add a listener to "activate" the wrapper
		this.filteredCollectionHolder.addCollectionChangeListener(ValueModel.VALUE, this.buildFilteredListener());

		assertEquals(0, this.filteredCollectionHolder.size());

		this.collectionHolder.addItem(new TestItem("bar"));
		assertEquals(1, this.filteredCollectionHolder.size());

		TestItem baz = new TestItem("baz");
		this.collectionHolder.addItem(baz);
		assertEquals(2, this.filteredCollectionHolder.size());
		// before removing it, change the item so that it is filtered
		baz.name = "jaz";
		this.collectionHolder.removeItem(baz);
		// this would fail because the item was not removed from
		// the filtered collection cache... but we've fixed it now
		assertEquals(1, this.filteredCollectionHolder.size());
	}

	private Collection buildCollection2() {
		Collection collection = new Vector();
		collection.add(new TestItem("foo"));
		return collection;
	}

	private Filter buildFilter2() {
		return new Filter() {
			public boolean accept(Object o) {
				return ((TestItem) o).name.startsWith("b");
			}
		};
	}


private class TestItem {
	String name;
	TestItem(String name) {
		super();
		this.name = name;
	}
}

}
