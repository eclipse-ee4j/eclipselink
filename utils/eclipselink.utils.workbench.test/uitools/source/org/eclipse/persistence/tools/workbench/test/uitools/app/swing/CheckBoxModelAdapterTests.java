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
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import javax.swing.ButtonModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


public class CheckBoxModelAdapterTests extends TestCase {
	private PropertyValueModel booleanHolder;
	private ButtonModel buttonModelAdapter;
	boolean eventFired;

	public static Test suite() {
		return new TestSuite(CheckBoxModelAdapterTests.class);
	}
	
	public CheckBoxModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.booleanHolder = new SimplePropertyValueModel(Boolean.TRUE);
		this.buttonModelAdapter = new CheckBoxModelAdapter(this.booleanHolder);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSetSelected() throws Exception {
		this.eventFired = false;
		this.buttonModelAdapter.addChangeListener(new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				CheckBoxModelAdapterTests.this.eventFired = true;
			}
		});
		this.buttonModelAdapter.setSelected(false);
		assertTrue(this.eventFired);
		assertEquals(Boolean.FALSE, this.booleanHolder.getValue());
	}

	public void testSetValue() throws Exception {
		this.eventFired = false;
		this.buttonModelAdapter.addChangeListener(new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				CheckBoxModelAdapterTests.this.eventFired = true;
			}
		});
		assertTrue(this.buttonModelAdapter.isSelected());
		this.booleanHolder.setValue(Boolean.FALSE);
		assertTrue(this.eventFired);
		assertFalse(this.buttonModelAdapter.isSelected());
	}

	public void testDefaultValue() throws Exception {
		this.eventFired = false;
		this.buttonModelAdapter.addChangeListener(new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				CheckBoxModelAdapterTests.this.eventFired = true;
			}
		});
		assertTrue(this.buttonModelAdapter.isSelected());
		this.booleanHolder.setValue(null);
		assertTrue(this.eventFired);
		assertFalse(this.buttonModelAdapter.isSelected());

		this.eventFired = false;
		this.booleanHolder.setValue(Boolean.FALSE);
		assertFalse(this.eventFired);
		assertFalse(this.buttonModelAdapter.isSelected());
	}

	public void testHasListeners() throws Exception {
		SimplePropertyValueModel localBooleanHolder = (SimplePropertyValueModel) this.booleanHolder;
		assertFalse(localBooleanHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(this.buttonModelAdapter);

		ChangeListener listener = new TestChangeListener();
		this.buttonModelAdapter.addChangeListener(listener);
		assertTrue(localBooleanHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasListeners(this.buttonModelAdapter);

		this.buttonModelAdapter.removeChangeListener(listener);
		assertFalse(localBooleanHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(this.buttonModelAdapter);
	}

	private void verifyHasNoListeners(Object model) throws Exception {
		EventListenerList listenerList = (EventListenerList) ClassTools.getFieldValue(model, "listenerList");
		assertEquals(0, listenerList.getListenerList().length);
	}

	private void verifyHasListeners(Object model) throws Exception {
		EventListenerList listenerList = (EventListenerList) ClassTools.getFieldValue(model, "listenerList");
		assertFalse(listenerList.getListenerList().length == 0);
	}


private class TestChangeListener implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
		fail("unexpected event");
	}
}

}
