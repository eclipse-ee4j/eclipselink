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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.filters.BidiFilter;


public class FilteringPropertyValueModelTests extends TestCase {
	private PropertyValueModel objectHolder;
	PropertyChangeEvent event;

	private PropertyValueModel filteredObjectHolder;
	PropertyChangeEvent filteredEvent;
	
	public static Test suite() {
		return new TestSuite(FilteringPropertyValueModelTests.class);
	}
	
	public FilteringPropertyValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.objectHolder = new SimplePropertyValueModel("foo");
		this.filteredObjectHolder = new FilteringPropertyValueModel(this.objectHolder, this.buildFilter());
	}

	private BidiFilter buildFilter() {
		return new BidiFilter() {
			public boolean accept(Object o) {
				return (o != null) && ((String) o).startsWith("b");
			}
			public boolean reverseAccept(Object o) {
				return (o != null) && ((String) o).startsWith("b");
			}
		};
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetValue() {
		assertEquals("foo", this.objectHolder.getValue());
		assertNull(this.filteredObjectHolder.getValue());

		this.objectHolder.setValue("bar");
		assertEquals("bar", this.objectHolder.getValue());
		assertNotNull(this.filteredObjectHolder.getValue());
		assertEquals("bar", this.filteredObjectHolder.getValue());

		this.objectHolder.setValue("baz");
		assertEquals("baz", this.objectHolder.getValue());
		assertNotNull(this.filteredObjectHolder.getValue());
		assertEquals("baz", this.filteredObjectHolder.getValue());

		this.objectHolder.setValue(null);
		assertNull(this.objectHolder.getValue());
		assertNull(this.filteredObjectHolder.getValue());

		this.objectHolder.setValue("foo");
		assertEquals("foo", this.objectHolder.getValue());
		assertNull(this.filteredObjectHolder.getValue());
	}

	public void testSetValue() {
		this.filteredObjectHolder.setValue("bar");
		assertEquals("bar", this.objectHolder.getValue());
		assertEquals("bar", this.filteredObjectHolder.getValue());

		this.filteredObjectHolder.setValue("foo");
		assertEquals("bar", this.objectHolder.getValue());
		assertEquals("bar", this.filteredObjectHolder.getValue());

		this.filteredObjectHolder.setValue(null);
		assertEquals("bar", this.objectHolder.getValue());
		assertEquals("bar", this.filteredObjectHolder.getValue());

		this.filteredObjectHolder.setValue("baz");
		assertEquals("baz", this.objectHolder.getValue());
		assertEquals("baz", this.filteredObjectHolder.getValue());
	}

	public void testLazyListening() {
		assertTrue(((AbstractModel) this.objectHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
		PropertyChangeListener listener = this.buildFilteredListener();
		this.filteredObjectHolder.addPropertyChangeListener(listener);
		assertTrue(((AbstractModel) this.objectHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.filteredObjectHolder.removePropertyChangeListener(listener);
		assertTrue(((AbstractModel) this.objectHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));

		this.filteredObjectHolder.addPropertyChangeListener(ValueModel.VALUE, listener);
		assertTrue(((AbstractModel) this.objectHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.filteredObjectHolder.removePropertyChangeListener(ValueModel.VALUE, listener);
		assertTrue(((AbstractModel) this.objectHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
	}

	public void testPropertyChange1() {
		this.objectHolder.addPropertyChangeListener(this.buildListener());
		this.filteredObjectHolder.addPropertyChangeListener(this.buildFilteredListener());
		this.verifyPropertyChanges();
	}

	public void testPropertyChange2() {
		this.objectHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildListener());
		this.filteredObjectHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildFilteredListener());
		this.verifyPropertyChanges();
	}

	private void verifyPropertyChanges() {
		this.event = null;
		this.filteredEvent = null;
		this.objectHolder.setValue("bar");
		this.verifyEvent(this.event, this.objectHolder, "foo", "bar");
		this.verifyEvent(this.filteredEvent, this.filteredObjectHolder, null, "bar");

		this.event = null;
		this.filteredEvent = null;
		this.objectHolder.setValue("baz");
		this.verifyEvent(this.event, this.objectHolder, "bar", "baz");
		this.verifyEvent(this.filteredEvent, this.filteredObjectHolder, "bar", "baz");

		this.event = null;
		this.filteredEvent = null;
		this.objectHolder.setValue("foo");
		this.verifyEvent(this.event, this.objectHolder, "baz", "foo");
		this.verifyEvent(this.filteredEvent, this.filteredObjectHolder, "baz", null);

		this.event = null;
		this.filteredEvent = null;
		this.objectHolder.setValue("fop");
		this.verifyEvent(this.event, this.objectHolder, "foo", "fop");
		assertNull(this.filteredEvent);

		this.event = null;
		this.filteredEvent = null;
		this.objectHolder.setValue(null);
		this.verifyEvent(this.event, this.objectHolder, "fop", null);
		assertNull(this.filteredEvent);

		this.event = null;
		this.filteredEvent = null;
		this.objectHolder.setValue("bar");
		this.verifyEvent(this.event, this.objectHolder, null, "bar");
		this.verifyEvent(this.filteredEvent, this.filteredObjectHolder, null, "bar");
	}

	private PropertyChangeListener buildListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				FilteringPropertyValueModelTests.this.event = e;
			}
		};
	}

	private PropertyChangeListener buildFilteredListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				FilteringPropertyValueModelTests.this.filteredEvent = e;
			}
		};
	}

	private void verifyEvent(PropertyChangeEvent e, Object source, Object oldValue, Object newValue) {
		assertEquals(source, e.getSource());
		assertEquals(ValueModel.VALUE, e.getPropertyName());
		assertEquals(oldValue, e.getOldValue());
		assertEquals(newValue, e.getNewValue());
	}

}
