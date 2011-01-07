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

import java.util.Date;

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
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DateSpinnerModelAdapter;


public class DateSpinnerModelAdapterTests extends TestCase {
	private PropertyValueModel valueHolder;
	private SpinnerModel spinnerModelAdapter;
	boolean eventFired;

	public static Test suite() {
		return new TestSuite(DateSpinnerModelAdapterTests.class);
	}
	
	public DateSpinnerModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.valueHolder = new SimplePropertyValueModel(new Date());
		this.spinnerModelAdapter = new DateSpinnerModelAdapter(this.valueHolder);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSetValueSpinnerModel() throws Exception {
		this.eventFired = false;
		this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				DateSpinnerModelAdapterTests.this.eventFired = true;
			}
		});
		Date newDate = new Date();
		newDate.setTime(777777);
		this.spinnerModelAdapter.setValue(newDate);
		assertTrue(this.eventFired);
		assertEquals(777777, ((Date) this.valueHolder.getValue()).getTime());
	}

	public void testSetValueValueHolder() throws Exception {
		this.eventFired = false;
		this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				DateSpinnerModelAdapterTests.this.eventFired = true;
			}
		});
		Date newDate = new Date();
		newDate.setTime(777777);
		this.valueHolder.setValue(newDate);
		assertTrue(this.eventFired);
		assertEquals(777777, ((Date) this.spinnerModelAdapter.getValue()).getTime());
	}

	public void testDefaultValue() throws Exception {
		Date newDate = new Date();
		newDate.setTime(777777);
		this.valueHolder.setValue(newDate);
		this.eventFired = false;
		this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				DateSpinnerModelAdapterTests.this.eventFired = true;
			}
		});
		assertEquals(777777, ((Date) this.spinnerModelAdapter.getValue()).getTime());
		this.valueHolder.setValue(null);
		assertTrue(this.eventFired);
		assertFalse(((Date) this.spinnerModelAdapter.getValue()).getTime() == 777777);
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

	private void verifyHasNoListeners(SpinnerModel adapter) throws Exception {
		assertEquals(0, ((DateSpinnerModelAdapter) adapter).getChangeListeners().length);
	}

	private void verifyHasListeners(Object adapter) throws Exception {
		assertFalse(((DateSpinnerModelAdapter) adapter).getChangeListeners().length == 0);
	}

	public void testNullInitialValue() {
		Date today = new Date();
		this.valueHolder = new SimplePropertyValueModel();
		this.spinnerModelAdapter = new DateSpinnerModelAdapter(this.valueHolder, today);

		this.eventFired = false;
		this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				DateSpinnerModelAdapterTests.this.eventFired = true;
			}
		});
		assertEquals(today, this.spinnerModelAdapter.getValue());

		Date newDate = new Date();
		newDate.setTime(777777);
		this.valueHolder.setValue(newDate);

		assertTrue(this.eventFired);
		assertEquals(777777, ((Date) this.spinnerModelAdapter.getValue()).getTime());
	}


	// ********** inner class **********
	private class TestChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			fail("unexpected event");
		}
	}

}
