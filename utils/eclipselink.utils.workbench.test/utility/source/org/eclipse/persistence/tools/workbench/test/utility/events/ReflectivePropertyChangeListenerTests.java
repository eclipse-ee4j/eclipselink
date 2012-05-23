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
package org.eclipse.persistence.tools.workbench.test.utility.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.events.ReflectiveChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;

public class ReflectivePropertyChangeListenerTests extends TestCase {
	
	public static Test suite() {
		return new TestSuite(ReflectivePropertyChangeListenerTests.class);
	}
	
	public ReflectivePropertyChangeListenerTests(String name) {
		super(name);
	}

	public void testZeroArgument() {
		TestModel testModel = new TestModel(7);
		Target target = new Target(testModel, TestModel.VALUE_PROPERTY, 7, 99);
		testModel.addPropertyChangeListener(ReflectiveChangeListener.buildPropertyChangeListener(target, "propertyChangedZeroArgument"));
		testModel.setValue(99);
		assertTrue(target.zeroArgumentFlag);
		assertFalse(target.singleArgumentFlag);
	}

	public void testZeroArgumentNamedProperty() {
		TestModel testModel = new TestModel(7);
		Target target = new Target(testModel, TestModel.VALUE_PROPERTY, 7, 99);
		testModel.addPropertyChangeListener(TestModel.VALUE_PROPERTY, ReflectiveChangeListener.buildPropertyChangeListener(target, "propertyChangedZeroArgument"));
		testModel.setValue(99);
		assertTrue(target.zeroArgumentFlag);
		assertFalse(target.singleArgumentFlag);
	}

	public void testSingleArgument() {
		TestModel testModel = new TestModel(7);
		Target target = new Target(testModel, TestModel.VALUE_PROPERTY, 7, 99);
		testModel.addPropertyChangeListener(ReflectiveChangeListener.buildPropertyChangeListener(target, "propertyChangedSingleArgument"));
		testModel.setValue(99);
		assertFalse(target.zeroArgumentFlag);
		assertTrue(target.singleArgumentFlag);
	}

	/**
	 * test method that has more general method parameter type
	 */
	public void testSingleArgument2() throws Exception {
		TestModel testModel = new TestModel(7);
		Target target = new Target(testModel, TestModel.VALUE_PROPERTY, 7, 99);
		Method method = ClassTools.method(target, "propertyChangedSingleArgument2", new Class[] {Object.class});
		testModel.addPropertyChangeListener(ReflectiveChangeListener.buildPropertyChangeListener(target, method));
		testModel.setValue(99);
		assertFalse(target.zeroArgumentFlag);
		assertTrue(target.singleArgumentFlag);
	}

	public void testSingleArgumentNamedProperty() {
		TestModel testModel = new TestModel(7);
		Target target = new Target(testModel, TestModel.VALUE_PROPERTY, 7, 99);
		testModel.addPropertyChangeListener(TestModel.VALUE_PROPERTY, ReflectiveChangeListener.buildPropertyChangeListener(target, "propertyChangedSingleArgument"));
		testModel.setValue(99);
		assertFalse(target.zeroArgumentFlag);
		assertTrue(target.singleArgumentFlag);
	}

	/**
	 * test method that has more general method parameter type
	 */
	public void testSingleArgumentNamedProperty2() throws Exception {
		TestModel testModel = new TestModel(7);
		Target target = new Target(testModel, TestModel.VALUE_PROPERTY, 7, 99);
		Method method = ClassTools.method(target, "propertyChangedSingleArgument2", new Class[] {Object.class});
		testModel.addPropertyChangeListener(TestModel.VALUE_PROPERTY, ReflectiveChangeListener.buildPropertyChangeListener(target, method));
		testModel.setValue(99);
		assertFalse(target.zeroArgumentFlag);
		assertTrue(target.singleArgumentFlag);
	}

	public void testListenerMismatch() {
		TestModel testModel = new TestModel(7);
		Target target = new Target(testModel, TestModel.VALUE_PROPERTY, 7, 99);
		// build a PROPERTY change listener and hack it so we
		// can add it as a STATE change listener
		Object listener = ReflectiveChangeListener.buildPropertyChangeListener(target, "propertyChangedSingleArgument");
		testModel.addStateChangeListener((StateChangeListener) listener);

		boolean exCaught = false;
		try {
			testModel.setValue(99);
			fail("listener mismatch: " + listener);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testBogusDoubleArgument1() {
		TestModel testModel = new TestModel(7);
		Target target = new Target(testModel, TestModel.VALUE_PROPERTY, 7, 99);
		boolean exCaught = false;
		try {
			PropertyChangeListener listener = ReflectiveChangeListener.buildPropertyChangeListener(target, "stateChangedDoubleArgument");
			fail("bogus listener: " + listener);
		} catch (RuntimeException ex) {
			if (ex.getCause().getClass() == NoSuchMethodException.class) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}

	public void testBogusDoubleArgument2() throws Exception {
		TestModel testModel = new TestModel(7);
		Target target = new Target(testModel, TestModel.VALUE_PROPERTY, 7, 99);
		Method method = ClassTools.method(target, "propertyChangedDoubleArgument", new Class[] {PropertyChangeEvent.class, Object.class});
		boolean exCaught = false;
		try {
			PropertyChangeListener listener = ReflectiveChangeListener.buildPropertyChangeListener(target, method);
			fail("bogus listener: " + listener);
		} catch (RuntimeException ex) {
			if (ex.getMessage().equals(method.toString())) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}


	private class TestModel extends AbstractModel {
		private int value = 0;
			public static final String VALUE_PROPERTY = "value";
		TestModel(int value) {
			super();
			this.value = value;
		}
		void setValue(int value) {
			int old = this.value;
			this.value = value;
			this.firePropertyChanged(VALUE_PROPERTY, old, value);
			if (old != value) {
				this.fireStateChanged();
			}
		}
	}

	private class Target {
		TestModel testModel;
		String propertyName;
		Object oldValue;
		Object newValue;
		boolean zeroArgumentFlag = false;
		boolean singleArgumentFlag = false;
		Target(TestModel testModel, String propertyName, int oldValue, int newValue) {
			super();
			this.testModel = testModel;
			this.propertyName = propertyName;
			this.oldValue = new Integer(oldValue);
			this.newValue = new Integer(newValue);
		}
		void propertyChangedZeroArgument() {
			this.zeroArgumentFlag = true;
		}
		void propertyChangedSingleArgument(PropertyChangeEvent e) {
			this.singleArgumentFlag = true;
			assertSame(this.testModel, e.getSource());
			assertEquals(this.propertyName, e.getPropertyName());
			assertEquals(this.oldValue, e.getOldValue());
			assertEquals(this.newValue, e.getNewValue());
		}
		void propertyChangedSingleArgument2(Object o) {
			PropertyChangeEvent e = (PropertyChangeEvent) o;
			this.singleArgumentFlag = true;
			assertSame(this.testModel, e.getSource());
			assertEquals(this.propertyName, e.getPropertyName());
			assertEquals(this.oldValue, e.getOldValue());
			assertEquals(this.newValue, e.getNewValue());
		}
		void propertyChangedDoubleArgument(PropertyChangeEvent e, Object o) {
			fail("bogus event: " + e);
		}
	}

}
