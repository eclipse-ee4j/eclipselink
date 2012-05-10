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
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


public class RadioButtonModelAdapterTests extends TestCase {
	private PropertyValueModel valueHolder;

	private ButtonModel redButtonModelAdapter;
	private ChangeListener redListener;
	boolean redEventFired;

	private ButtonModel greenButtonModelAdapter;
	private ChangeListener greenListener;
	boolean greenEventFired;

	private ButtonModel blueButtonModelAdapter;
	private ChangeListener blueListener;
	boolean blueEventFired;

//	private ButtonGroup buttonGroup;	// DO NOT use a ButtonGroup

	private static final String RED = "red";
	private static final String GREEN = "green";
	private static final String BLUE = "blue";

	public static Test suite() {
		return new TestSuite(RadioButtonModelAdapterTests.class);
	}
	
	public RadioButtonModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.valueHolder = new SimplePropertyValueModel(null);
//		buttonGroup = new ButtonGroup();

		this.redButtonModelAdapter = new RadioButtonModelAdapter(this.valueHolder, RED);
//		this.redButtonModelAdapter.setGroup(buttonGroup);
		this.redListener = new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				RadioButtonModelAdapterTests.this.redEventFired = true;
			}
		};

		this.greenButtonModelAdapter = new RadioButtonModelAdapter(this.valueHolder, GREEN);
//		this.greenButtonModelAdapter.setGroup(buttonGroup);
		this.greenListener = new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				RadioButtonModelAdapterTests.this.greenEventFired = true;
			}
		};

		this.blueButtonModelAdapter = new RadioButtonModelAdapter(this.valueHolder, BLUE);
//		this.blueButtonModelAdapter.setGroup(buttonGroup);
		this.blueListener = new TestChangeListener() {
			public void stateChanged(ChangeEvent e) {
				RadioButtonModelAdapterTests.this.blueEventFired = true;
			}
		};
		
		this.clearFlags();
	}

	private void listenToModelAdapters() {
		this.redButtonModelAdapter.addChangeListener(this.redListener);
		this.greenButtonModelAdapter.addChangeListener(this.greenListener);
		this.blueButtonModelAdapter.addChangeListener(this.blueListener);
	}

	private void clearFlags() {
		this.redEventFired = false;
		this.greenEventFired = false;
		this.blueEventFired = false;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSetSelected() throws Exception {
		this.listenToModelAdapters();

		this.greenButtonModelAdapter.setSelected(true);
		assertFalse(this.redEventFired);
		assertTrue(this.greenEventFired);
		assertFalse(this.blueEventFired);
		assertEquals(GREEN, this.valueHolder.getValue());

		this.clearFlags();
		this.blueButtonModelAdapter.setSelected(true);
		assertFalse(this.redEventFired);
		assertTrue(this.greenEventFired);
		assertTrue(this.blueEventFired);
		assertEquals(BLUE, this.valueHolder.getValue());

		this.clearFlags();
		this.redButtonModelAdapter.setSelected(true);
		assertTrue(this.redEventFired);
		assertFalse(this.greenEventFired);
		assertTrue(this.blueEventFired);
		assertEquals(RED, this.valueHolder.getValue());
	}

	public void testSetValue() throws Exception {
		this.listenToModelAdapters();

		this.greenButtonModelAdapter.setSelected(true);

		this.clearFlags();
		this.valueHolder.setValue(BLUE);
		assertFalse(this.redEventFired);
		assertTrue(this.greenEventFired);
		assertTrue(this.blueEventFired);
		assertFalse(this.redButtonModelAdapter.isSelected());
		assertFalse(this.greenButtonModelAdapter.isSelected());
		assertTrue(this.blueButtonModelAdapter.isSelected());

		this.clearFlags();
		this.valueHolder.setValue(RED);
		assertTrue(this.redEventFired);
		assertFalse(this.greenEventFired);
		assertTrue(this.blueEventFired);
		assertTrue(this.redButtonModelAdapter.isSelected());
		assertFalse(this.greenButtonModelAdapter.isSelected());
		assertFalse(this.blueButtonModelAdapter.isSelected());
	}

	public void testDefaultValue() throws Exception {
		this.listenToModelAdapters();

		this.valueHolder.setValue(GREEN);
		assertFalse(this.redButtonModelAdapter.isSelected());
		assertTrue(this.greenButtonModelAdapter.isSelected());
		assertFalse(this.blueButtonModelAdapter.isSelected());

		this.clearFlags();
		this.valueHolder.setValue(null);
		assertFalse(this.redEventFired);
		assertTrue(this.greenEventFired);
		assertFalse(this.blueEventFired);
		assertFalse(this.redButtonModelAdapter.isSelected());
		assertFalse(this.greenButtonModelAdapter.isSelected());
		assertFalse(this.blueButtonModelAdapter.isSelected());

		this.clearFlags();
		this.valueHolder.setValue(BLUE);
		assertFalse(this.redEventFired);
		assertFalse(this.greenEventFired);
		assertTrue(this.blueEventFired);
		assertFalse(this.redButtonModelAdapter.isSelected());
		assertFalse(this.greenButtonModelAdapter.isSelected());
		assertTrue(this.blueButtonModelAdapter.isSelected());
	}

	public void testHasListeners() throws Exception {
		SimplePropertyValueModel localValueHolder = (SimplePropertyValueModel) this.valueHolder;
		assertFalse(localValueHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(this.redButtonModelAdapter);
		this.verifyHasNoListeners(this.greenButtonModelAdapter);
		this.verifyHasNoListeners(this.blueButtonModelAdapter);

		ChangeListener listener = new TestChangeListener();
		this.redButtonModelAdapter.addChangeListener(listener);
		assertTrue(localValueHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasListeners(this.redButtonModelAdapter);
		this.verifyHasNoListeners(this.greenButtonModelAdapter);
		this.verifyHasNoListeners(this.blueButtonModelAdapter);

		this.redButtonModelAdapter.removeChangeListener(listener);
		assertFalse(localValueHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		this.verifyHasNoListeners(this.redButtonModelAdapter);
		this.verifyHasNoListeners(this.greenButtonModelAdapter);
		this.verifyHasNoListeners(this.blueButtonModelAdapter);
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
