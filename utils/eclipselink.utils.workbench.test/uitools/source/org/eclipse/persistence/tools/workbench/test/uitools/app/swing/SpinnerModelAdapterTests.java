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
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.SpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


public class SpinnerModelAdapterTests extends TestCase {
	private PropertyValueModel valueHolder;
	SpinnerModel spinnerModelAdapter;
	boolean eventFired;

	public static Test suite() {
		return new TestSuite(SpinnerModelAdapterTests.class);
	}
	
	public SpinnerModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.valueHolder = new SimplePropertyValueModel(new Integer(0));
		this.spinnerModelAdapter = new SpinnerModelAdapter(this.valueHolder);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSetValueSpinnerModel() throws Exception {
		this.eventFired = false;
		this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				SpinnerModelAdapterTests.this.eventFired = true;
				assertEquals(SpinnerModelAdapterTests.this.spinnerModelAdapter, e.getSource());
			}
		});
		this.spinnerModelAdapter.setValue(new Integer(5));
		assertTrue(this.eventFired);
		assertEquals(new Integer(5), this.valueHolder.getValue());
	}

	public void testSetValueValueHolder() throws Exception {
		this.eventFired = false;
		this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				SpinnerModelAdapterTests.this.eventFired = true;
				assertEquals(SpinnerModelAdapterTests.this.spinnerModelAdapter, e.getSource());
			}
		});
		assertEquals(new Integer(0), this.spinnerModelAdapter.getValue());
		this.valueHolder.setValue(new Integer(7));
		assertTrue(this.eventFired);
		assertEquals(new Integer(7), this.spinnerModelAdapter.getValue());
	}

	public void testHasListeners() throws Exception {
		SimplePropertyValueModel localValueHolder = (SimplePropertyValueModel) this.valueHolder;
		assertFalse(localValueHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(this.spinnerModelAdapter);

		ChangeListener listener = new TestChangeListener();
		this.spinnerModelAdapter.addChangeListener(listener);
		assertTrue(localValueHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasListeners(this.spinnerModelAdapter);

		this.spinnerModelAdapter.removeChangeListener(listener);
		assertFalse(localValueHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(this.spinnerModelAdapter);
	}

	private void verifyHasNoListeners(Object adapter) throws Exception {
		Object delegate = ClassTools.getFieldValue(adapter, "delegate");
		Object[] listeners = (Object[]) ClassTools.invokeMethod(delegate, "getChangeListeners");
		assertEquals(0, listeners.length);
	}

	private void verifyHasListeners(Object adapter) throws Exception {
		Object delegate = ClassTools.getFieldValue(adapter, "delegate");
		Object[] listeners = (Object[]) ClassTools.invokeMethod(delegate, "getChangeListeners");
		assertFalse(listeners.length == 0);
	}


private class TestChangeListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
		fail("unexpected event");
	}
}

}
