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
import java.util.Iterator;

import javax.swing.JList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyCollectionValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;


public class PropertyCollectionValueModelAdapterTests extends TestCase {
	private CollectionValueModel adapter;
	private PropertyValueModel wrappedValueHolder;

	public static Test suite() {
		return new TestSuite(PropertyCollectionValueModelAdapterTests.class);
	}
	
	public PropertyCollectionValueModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.wrappedValueHolder = new SimplePropertyValueModel();
		this.adapter = new PropertyCollectionValueModelAdapter(this.wrappedValueHolder);
	}

	private Collection wrappedCollection() {
		return CollectionTools.collection(new SingleElementIterator(this.wrappedValueHolder.getValue()));
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetValue() {
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, new TestListener() {
			public void itemsAdded(CollectionChangeEvent e) {/* OK */}
		});
		this.wrappedValueHolder.setValue("foo");
		Collection adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(1, adapterCollection.size());
		assertEquals(this.wrappedCollection(), adapterCollection);
		assertEquals("foo", adapterCollection.iterator().next());
	}

	public void testStaleValue() {
		CollectionChangeListener listener = new TestListener() {
			public void itemsAdded(CollectionChangeEvent e) {/* OK */}
		};
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, listener);
		this.wrappedValueHolder.setValue("foo");
		Collection adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(1, adapterCollection.size());
		assertEquals(this.wrappedCollection(), adapterCollection);
		assertEquals("foo", adapterCollection.iterator().next());

		this.adapter.removeCollectionChangeListener(ValueModel.VALUE, listener);
		adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(0, adapterCollection.size());
		assertEquals(new HashBag(), adapterCollection);

		this.adapter.addCollectionChangeListener(ValueModel.VALUE, listener);
		adapterCollection = CollectionTools.collection((Iterator) this.adapter.getValue());
		assertEquals(1, adapterCollection.size());
		assertEquals(this.wrappedCollection(), adapterCollection);
		assertEquals("foo", adapterCollection.iterator().next());
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
		this.wrappedValueHolder.setValue("foo");
		JList jList = new JList(new ListModelAdapter(this.adapter));
		this.wrappedValueHolder.setValue(null);
		assertEquals(0, jList.getModel().getSize());
	}

	public void testCollectionChangedFromEmpty() {
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, new TestListener() {
			public void itemsAdded(CollectionChangeEvent e) {/* OK */}
		});
		JList jList = new JList(new ListModelAdapter(this.adapter));
		
		this.wrappedValueHolder.setValue("foo");
		assertEquals(1, jList.getModel().getSize());
	}
	
	public void testCollectionChangedFromEmptyToEmpty() {
		this.adapter.addCollectionChangeListener(ValueModel.VALUE, new TestListener());
		JList jList = new JList(new ListModelAdapter(this.adapter));
		
		this.wrappedValueHolder.setValue(null);
		assertEquals(0, jList.getModel().getSize());
	}


	// ********** member class **********
	
	private static class TestListener implements CollectionChangeListener {
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
