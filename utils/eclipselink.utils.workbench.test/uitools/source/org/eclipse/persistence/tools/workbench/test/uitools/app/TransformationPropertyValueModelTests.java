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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.BidiTransformer;


public class TransformationPropertyValueModelTests extends TestCase {
	private PropertyValueModel objectHolder;
	PropertyChangeEvent event;

	private PropertyValueModel transformationObjectHolder;
	PropertyChangeEvent transformationEvent;

	public static Test suite() {
		return new TestSuite(TransformationPropertyValueModelTests.class);
	}
	
	public TransformationPropertyValueModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.objectHolder = new SimplePropertyValueModel("foo");
		this.transformationObjectHolder = new TransformationPropertyValueModel(this.objectHolder, this.buildTransformer());
	}

	private BidiTransformer buildTransformer() {
		return new BidiTransformer() {
			public Object transform(Object o) {
				return (o == null) ? null : ((String) o).toUpperCase();
			}
			public Object reverseTransform(Object o) {
				return (o == null) ? null : ((String) o).toLowerCase();
			}
		};
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testGetValue() {
		assertEquals("foo", this.objectHolder.getValue());
		assertEquals("FOO", this.transformationObjectHolder.getValue());

		this.objectHolder.setValue("bar");
		assertEquals("bar", this.objectHolder.getValue());
		assertEquals("BAR", this.transformationObjectHolder.getValue());

		this.objectHolder.setValue("baz");
		assertEquals("baz", this.objectHolder.getValue());
		assertEquals("BAZ", this.transformationObjectHolder.getValue());

		this.objectHolder.setValue(null);
		assertNull(this.objectHolder.getValue());
		assertNull(this.transformationObjectHolder.getValue());

		this.objectHolder.setValue("foo");
		assertEquals("foo", this.objectHolder.getValue());
		assertEquals("FOO", this.transformationObjectHolder.getValue());
	}

	public void testSetValue() {
		this.transformationObjectHolder.setValue("BAR");
		assertEquals("bar", this.objectHolder.getValue());
		assertEquals("BAR", this.transformationObjectHolder.getValue());

		this.transformationObjectHolder.setValue("Foo");
		assertEquals("foo", this.objectHolder.getValue());
		assertEquals("FOO", this.transformationObjectHolder.getValue());

		this.transformationObjectHolder.setValue(null);
		assertNull(this.objectHolder.getValue());
		assertNull(this.transformationObjectHolder.getValue());

		this.transformationObjectHolder.setValue("baz");
		assertEquals("baz", this.objectHolder.getValue());
		assertEquals("BAZ", this.transformationObjectHolder.getValue());
	}

	public void testLazyListening() {
		assertTrue(((AbstractModel) this.objectHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
		PropertyChangeListener listener = this.buildTransformationListener();
		this.transformationObjectHolder.addPropertyChangeListener(listener);
		assertTrue(((AbstractModel) this.objectHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.transformationObjectHolder.removePropertyChangeListener(listener);
		assertTrue(((AbstractModel) this.objectHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));

		this.transformationObjectHolder.addPropertyChangeListener(ValueModel.VALUE, listener);
		assertTrue(((AbstractModel) this.objectHolder).hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.transformationObjectHolder.removePropertyChangeListener(ValueModel.VALUE, listener);
		assertTrue(((AbstractModel) this.objectHolder).hasNoPropertyChangeListeners(ValueModel.VALUE));
	}

	public void testPropertyChange1() {
		this.objectHolder.addPropertyChangeListener(this.buildListener());
		this.transformationObjectHolder.addPropertyChangeListener(this.buildTransformationListener());
		this.verifyPropertyChanges();
	}

	public void testPropertyChange2() {
		this.objectHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildListener());
		this.transformationObjectHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildTransformationListener());
		this.verifyPropertyChanges();
	}

	private void verifyPropertyChanges() {
		this.event = null;
		this.transformationEvent = null;
		this.objectHolder.setValue("bar");
		this.verifyEvent(this.event, this.objectHolder, "foo", "bar");
		this.verifyEvent(this.transformationEvent, this.transformationObjectHolder, "FOO", "BAR");

		this.event = null;
		this.transformationEvent = null;
		this.objectHolder.setValue("baz");
		this.verifyEvent(this.event, this.objectHolder, "bar", "baz");
		this.verifyEvent(this.transformationEvent, this.transformationObjectHolder, "BAR", "BAZ");

		this.event = null;
		this.transformationEvent = null;
		this.objectHolder.setValue("Foo");
		this.verifyEvent(this.event, this.objectHolder, "baz", "Foo");
		this.verifyEvent(this.transformationEvent, this.transformationObjectHolder, "BAZ", "FOO");

		this.event = null;
		this.transformationEvent = null;
		this.objectHolder.setValue("FOO");
		this.verifyEvent(this.event, this.objectHolder, "Foo", "FOO");
		assertNull(this.transformationEvent);

		this.event = null;
		this.transformationEvent = null;
		this.objectHolder.setValue(null);
		this.verifyEvent(this.event, this.objectHolder, "FOO", null);
		this.verifyEvent(this.transformationEvent, this.transformationObjectHolder, "FOO", null);

		this.event = null;
		this.transformationEvent = null;
		this.objectHolder.setValue("bar");
		this.verifyEvent(this.event, this.objectHolder, null, "bar");
		this.verifyEvent(this.transformationEvent, this.transformationObjectHolder, null, "BAR");
	}

	private PropertyChangeListener buildListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				TransformationPropertyValueModelTests.this.event = e;
			}
		};
	}

	private PropertyChangeListener buildTransformationListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				TransformationPropertyValueModelTests.this.transformationEvent = e;
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
