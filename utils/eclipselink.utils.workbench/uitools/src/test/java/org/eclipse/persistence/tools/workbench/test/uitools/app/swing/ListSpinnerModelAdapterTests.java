/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListSpinnerModelAdapter;


public class ListSpinnerModelAdapterTests extends TestCase {
    private PropertyValueModel valueHolder;
    private SpinnerModel spinnerModelAdapter;
    boolean eventFired;
    private static final String[] VALUE_LIST = {"red", "green", "blue"};
    private static final String DEFAULT_VALUE = VALUE_LIST[0];

    public static Test suite() {
        return new TestSuite(ListSpinnerModelAdapterTests.class);
    }

    public ListSpinnerModelAdapterTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.valueHolder = new SimplePropertyValueModel(DEFAULT_VALUE);
        this.spinnerModelAdapter = new ListSpinnerModelAdapter(this.valueHolder, VALUE_LIST);
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testSetValueSpinnerModel() throws Exception {
        this.eventFired = false;
        this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ListSpinnerModelAdapterTests.this.eventFired = true;
            }
        });
        assertEquals(DEFAULT_VALUE, this.valueHolder.getValue());
        this.spinnerModelAdapter.setValue(VALUE_LIST[2]);
        assertTrue(this.eventFired);
        assertEquals(VALUE_LIST[2], this.valueHolder.getValue());
    }

    public void testSetValueValueHolder() throws Exception {
        this.eventFired = false;
        this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ListSpinnerModelAdapterTests.this.eventFired = true;
            }
        });
        assertEquals(DEFAULT_VALUE, this.spinnerModelAdapter.getValue());
        this.valueHolder.setValue(VALUE_LIST[2]);
        assertTrue(this.eventFired);
        assertEquals(VALUE_LIST[2], this.spinnerModelAdapter.getValue());
    }

    public void testDefaultValue() throws Exception {
        this.eventFired = false;
        this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ListSpinnerModelAdapterTests.this.eventFired = true;
            }
        });
        assertEquals(DEFAULT_VALUE, this.spinnerModelAdapter.getValue());

        this.valueHolder.setValue(VALUE_LIST[2]);
        assertTrue(this.eventFired);
        assertEquals(VALUE_LIST[2], this.spinnerModelAdapter.getValue());

        this.eventFired = false;
        this.valueHolder.setValue(null);
        assertTrue(this.eventFired);
        assertEquals(VALUE_LIST[0], this.spinnerModelAdapter.getValue());
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
        assertEquals(0, ((ListSpinnerModelAdapter) adapter).getChangeListeners().length);
    }

    private void verifyHasListeners(Object adapter) throws Exception {
        assertFalse(((ListSpinnerModelAdapter) adapter).getChangeListeners().length == 0);
    }


private class TestChangeListener implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
        fail("unexpected event");
    }
}

}
