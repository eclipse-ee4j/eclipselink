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
package org.eclipse.persistence.tools.workbench.test.utility.events;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.events.ReflectiveChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;

public class ReflectiveStateChangeListenerTests extends TestCase {
	
	public static Test suite() {
		return new TestSuite(ReflectiveStateChangeListenerTests.class);
	}
	
	public ReflectiveStateChangeListenerTests(String name) {
		super(name);
	}

	public void testZeroArgument() {
		TestModel testModel = new TestModel();
		Target target = new Target(testModel);
		testModel.addStateChangeListener(ReflectiveChangeListener.buildStateChangeListener(target, "stateChangedZeroArgument"));
		testModel.changeState();
		assertTrue(target.zeroArgumentFlag);
		assertFalse(target.singleArgumentFlag);
	}

	public void testSingleArgument() {
		TestModel testModel = new TestModel();
		Target target = new Target(testModel);
		testModel.addStateChangeListener(ReflectiveChangeListener.buildStateChangeListener(target, "stateChangedSingleArgument"));
		testModel.changeState();
		assertFalse(target.zeroArgumentFlag);
		assertTrue(target.singleArgumentFlag);
	}

	/**
	 * test method that has more general method parameter type
	 */
	public void testSingleArgument2() throws Exception {
		TestModel testModel = new TestModel();
		Target target = new Target(testModel);
		Method method = ClassTools.method(target, "stateChangedSingleArgument2", new Class[] {Object.class});
		testModel.addStateChangeListener(ReflectiveChangeListener.buildStateChangeListener(target, method));
		testModel.changeState();
		assertFalse(target.zeroArgumentFlag);
		assertTrue(target.singleArgumentFlag);
	}

	public void testListenerMismatch() {
		TestModel testModel = new TestModel();
		Target target = new Target(testModel);
		// build a STATE change listener and hack it so we
		// can add it as a PROPERTY change listener
		Object listener = ReflectiveChangeListener.buildStateChangeListener(target, "stateChangedSingleArgument");
		testModel.addPropertyChangeListener((PropertyChangeListener) listener);

		boolean exCaught = false;
		try {
			testModel.changeProperty();
			fail("listener mismatch: " + listener);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}

	public void testBogusDoubleArgument1() {
		TestModel testModel = new TestModel();
		Target target = new Target(testModel);
		boolean exCaught = false;
		try {
			StateChangeListener listener = ReflectiveChangeListener.buildStateChangeListener(target, "stateChangedDoubleArgument");
			fail("bogus listener: " + listener);
		} catch (RuntimeException ex) {
			if (ex.getCause().getClass() == NoSuchMethodException.class) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}

	public void testBogusDoubleArgument2() throws Exception {
		TestModel testModel = new TestModel();
		Target target = new Target(testModel);
		Method method = ClassTools.method(target, "stateChangedDoubleArgument", new Class[] {StateChangeEvent.class, Object.class});
		boolean exCaught = false;
		try {
			StateChangeListener listener = ReflectiveChangeListener.buildStateChangeListener(target, method);
			fail("bogus listener: " + listener);
		} catch (RuntimeException ex) {
			if (ex.getMessage().equals(method.toString())) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}


	private class TestModel extends AbstractModel {
		void changeState() {
			this.fireStateChanged();
		}
		void changeProperty() {
			this.firePropertyChanged("value", 55, 42);
		}
	}

	private class Target {
		TestModel testModel;
		boolean zeroArgumentFlag = false;
		boolean singleArgumentFlag = false;
		Target(TestModel testModel) {
			super();
			this.testModel = testModel;
		}
		void stateChangedZeroArgument() {
			this.zeroArgumentFlag = true;
		}
		void stateChangedSingleArgument(StateChangeEvent e) {
			this.singleArgumentFlag = true;
			assertSame(this.testModel, e.getSource());
		}
		void stateChangedSingleArgument2(Object e) {
			this.singleArgumentFlag = true;
			assertSame(this.testModel, ((StateChangeEvent) e).getSource());
		}
		void stateChangedDoubleArgument(StateChangeEvent e, Object o) {
			fail("bogus event: " + e);
		}
	}

}
