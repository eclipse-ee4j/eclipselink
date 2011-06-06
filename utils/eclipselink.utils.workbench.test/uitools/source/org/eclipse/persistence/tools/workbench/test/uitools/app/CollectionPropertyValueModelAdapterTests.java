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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public class CollectionPropertyValueModelAdapterTests extends TestCase {
	private PropertyValueModel adapter;
	private CollectionValueModel wrappedCollectionHolder;
	PropertyChangeEvent event;

	public static Test suite() {
		return new TestSuite(CollectionPropertyValueModelAdapterTests.class);
	}
	
	public CollectionPropertyValueModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.wrappedCollectionHolder = new SimpleCollectionValueModel();
		this.adapter = new LocalAdapter(this.wrappedCollectionHolder, "666");
		this.event = null;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	private boolean booleanValue() {
		return ((Boolean) this.adapter.getValue()).booleanValue();
	}

	private Collection wrappedCollection() {
		return CollectionTools.collection((Iterator) this.wrappedCollectionHolder.getValue());
	}

	public void testGetValue() {
		this.adapter.addPropertyChangeListener(ValueModel.VALUE, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {/* OK */}
		});
		assertFalse(this.booleanValue());
		assertFalse(this.wrappedCollection().contains("666"));

		this.wrappedCollectionHolder.addItem("111");
		assertFalse(this.booleanValue());

		this.wrappedCollectionHolder.addItem("222");
		assertFalse(this.booleanValue());

		this.wrappedCollectionHolder.addItem("666");
		assertTrue(this.booleanValue());
		assertTrue(this.wrappedCollection().contains("666"));

		this.wrappedCollectionHolder.removeItem("666");
		assertFalse(this.booleanValue());
		assertFalse(this.wrappedCollection().contains("666"));

		this.wrappedCollectionHolder.addItem("666");
		assertTrue(this.booleanValue());
		assertTrue(this.wrappedCollection().contains("666"));

		((SimpleCollectionValueModel) this.wrappedCollectionHolder).clear();
		assertFalse(this.booleanValue());
		assertFalse(this.wrappedCollection().contains("666"));
	}

	public void testSetValue() {
		this.adapter.addPropertyChangeListener(ValueModel.VALUE, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {/* OK */}
		});
		assertFalse(this.booleanValue());
		assertFalse(this.wrappedCollection().contains("666"));

		this.adapter.setValue(Boolean.TRUE);
		assertTrue(this.booleanValue());
		assertTrue(this.wrappedCollection().contains("666"));

		this.adapter.setValue(Boolean.FALSE);
		assertFalse(this.booleanValue());
		assertFalse(this.wrappedCollection().contains("666"));
	}

	public void testEventFiring() {
		this.adapter.addPropertyChangeListener(ValueModel.VALUE, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				CollectionPropertyValueModelAdapterTests.this.event = e;
			}
		});
		assertNull(this.event);

		this.wrappedCollectionHolder.addItem("111");
		assertNull(this.event);

		this.wrappedCollectionHolder.addItem("222");
		assertNull(this.event);

		this.wrappedCollectionHolder.addItem("666");
		this.verifyEvent(false, true);

		this.wrappedCollectionHolder.removeItem("666");
		this.verifyEvent(true, false);

		this.wrappedCollectionHolder.addItem("666");
		this.verifyEvent(false, true);

		((SimpleCollectionValueModel) this.wrappedCollectionHolder).clear();
		this.verifyEvent(true, false);
	}

	private void verifyEvent(boolean oldValue, boolean newValue) {
		assertEquals(this.adapter, this.event.getSource());
		assertEquals(Boolean.valueOf(oldValue), this.event.getOldValue());
		assertEquals(Boolean.valueOf(newValue), this.event.getNewValue());
		this.event = null;
	}

	public void testStaleValue() {
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {/* OK */}
		};
		this.adapter.addPropertyChangeListener(ValueModel.VALUE, listener);
		this.wrappedCollectionHolder.addItem("666");
		assertTrue(this.booleanValue());
		assertTrue(this.wrappedCollection().contains("666"));

		this.adapter.removePropertyChangeListener(ValueModel.VALUE, listener);
		assertFalse(this.booleanValue());
		assertTrue(this.wrappedCollection().contains("666"));

		this.adapter.addPropertyChangeListener(ValueModel.VALUE, listener);
		assertTrue(this.booleanValue());
		assertTrue(this.wrappedCollection().contains("666"));
	}

	public void testHasListeners() {
		assertFalse(((AbstractModel) this.adapter).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertFalse(((AbstractModel) this.wrappedCollectionHolder).hasAnyCollectionChangeListeners(ValueModel.VALUE));

		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {/* OK */}
		};
		this.adapter.addPropertyChangeListener(ValueModel.VALUE, listener);
		assertTrue(((AbstractModel) this.adapter).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.wrappedCollectionHolder).hasAnyCollectionChangeListeners(ValueModel.VALUE));

		this.adapter.removePropertyChangeListener(ValueModel.VALUE, listener);
		assertFalse(((AbstractModel) this.adapter).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertFalse(((AbstractModel) this.wrappedCollectionHolder).hasAnyCollectionChangeListeners(ValueModel.VALUE));

		this.adapter.addPropertyChangeListener(listener);
		assertTrue(((AbstractModel) this.adapter).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(((AbstractModel) this.wrappedCollectionHolder).hasAnyCollectionChangeListeners(ValueModel.VALUE));

		this.adapter.removePropertyChangeListener(listener);
		assertFalse(((AbstractModel) this.adapter).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertFalse(((AbstractModel) this.wrappedCollectionHolder).hasAnyCollectionChangeListeners(ValueModel.VALUE));
	}


	// ********** member class **********

	/**
	 * the value is true if the wrapped collection contains the specified item,
	 * otherwise the value is false
	 */
	private static class LocalAdapter extends CollectionPropertyValueModelAdapter {
		private Object item;

		LocalAdapter(CollectionValueModel collectionHolder, Object item) {
			super(collectionHolder);
			this.item = item;
		}

		// ********** CollectionPropertyValueModelAdapter implementation **********
		/**
		 * always return a Boolean
		 */
		public Object getValue() {
			Object result = super.getValue();
			return (result == null) ? Boolean.FALSE : result;
		}
		public void setValue(Object value) {
			if (this.booleanValue()) {
				if ( ! this.booleanValueOf(value)) {
					// the value is changing from true to false
					this.collectionHolder.removeItem(this.item);
				}
			} else {
				if (this.booleanValueOf(value)) {
					// the value is changing from false to true
					this.collectionHolder.addItem(this.item);
				}
			}
		}
		protected Object buildValue() {
			return Boolean.valueOf(CollectionTools.contains((Iterator) this.collectionHolder.getValue(), this.item));
		}

		// ********** internal methods **********
		private boolean booleanValue() {
			return this.booleanValueOf(this.value);
		}
		private boolean booleanValueOf(Object b) {
			return (b == null) ? false : ((Boolean) b).booleanValue();
		}

	}

}
