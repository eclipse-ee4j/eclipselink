/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;


public class NumberSpinnerModelAdapterTests extends TestCase {
    private PropertyValueModel valueHolder;
    private SpinnerModel spinnerModelAdapter;
    boolean eventFired;

    public static Test suite() {
        return new TestSuite(NumberSpinnerModelAdapterTests.class);
    }

    public NumberSpinnerModelAdapterTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.valueHolder = new SimplePropertyValueModel(Integer.valueOf(0));
        this.spinnerModelAdapter = new NumberSpinnerModelAdapter(this.valueHolder, -33, 33, 1);
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testSetValueSpinnerModel() throws Exception {
        this.eventFired = false;
        this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
            public void stateChanged(ChangeEvent e) {
                NumberSpinnerModelAdapterTests.this.eventFired = true;
            }
        });
        this.spinnerModelAdapter.setValue(Integer.valueOf(5));
        assertTrue(this.eventFired);
        assertEquals(Integer.valueOf(5), this.valueHolder.getValue());
    }

    public void testSetValueValueHolder() throws Exception {
        this.eventFired = false;
        this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
            public void stateChanged(ChangeEvent e) {
                NumberSpinnerModelAdapterTests.this.eventFired = true;
            }
        });
        assertEquals(Integer.valueOf(0), this.spinnerModelAdapter.getValue());
        this.valueHolder.setValue(Integer.valueOf(7));
        assertTrue(this.eventFired);
        assertEquals(Integer.valueOf(7), this.spinnerModelAdapter.getValue());
    }

    public void testDefaultValue() throws Exception {
        this.eventFired = false;
        this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
            public void stateChanged(ChangeEvent e) {
                NumberSpinnerModelAdapterTests.this.eventFired = true;
            }
        });
        assertEquals(Integer.valueOf(0), this.spinnerModelAdapter.getValue());
        this.valueHolder.setValue(null);
        assertTrue(this.eventFired);
        assertEquals(Integer.valueOf(-33), this.spinnerModelAdapter.getValue());
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
        assertEquals(0, ((NumberSpinnerModelAdapter) adapter).getChangeListeners().length);
    }

    private void verifyHasListeners(Object adapter) throws Exception {
        assertFalse(((NumberSpinnerModelAdapter) adapter).getChangeListeners().length == 0);
    }

    public void testNullInitialValue() {
        this.valueHolder = new SimplePropertyValueModel();
        this.spinnerModelAdapter = new NumberSpinnerModelAdapter(this.valueHolder, Integer.valueOf(-33), Integer.valueOf(33), Integer.valueOf(1), Integer.valueOf(0));

        this.eventFired = false;
        this.spinnerModelAdapter.addChangeListener(new TestChangeListener() {
            public void stateChanged(ChangeEvent e) {
                NumberSpinnerModelAdapterTests.this.eventFired = true;
            }
        });
        assertEquals(Integer.valueOf(0), this.spinnerModelAdapter.getValue());
        this.valueHolder.setValue(Integer.valueOf(7));
        assertTrue(this.eventFired);
        assertEquals(Integer.valueOf(7), this.spinnerModelAdapter.getValue());
    }


    // ********** inner class **********
    private class TestChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            fail("unexpected event");
        }
    }

}
