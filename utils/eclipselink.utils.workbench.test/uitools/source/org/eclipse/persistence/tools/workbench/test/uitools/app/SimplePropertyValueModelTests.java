/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public class SimplePropertyValueModelTests extends TestCase {
    private PropertyValueModel objectHolder;
    PropertyChangeEvent event;

    public static Test suite() {
        return new TestSuite(SimplePropertyValueModelTests.class);
    }

    public SimplePropertyValueModelTests(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.objectHolder = new SimplePropertyValueModel("foo");
    }

    protected void tearDown() throws Exception {
        TestTools.clear(this);
        super.tearDown();
    }

    public void testGetValue() {
        assertEquals("foo", this.objectHolder.getValue());
    }

    public void testSetValue() {
        this.objectHolder.setValue("bar");
        assertEquals("bar", this.objectHolder.getValue());
        this.objectHolder.setValue(null);
        assertEquals(null, this.objectHolder.getValue());
        this.objectHolder.setValue("baz");
        assertEquals("baz", this.objectHolder.getValue());
    }

    public void testPropertyChange1() {
        this.objectHolder.addPropertyChangeListener(this.buildListener());
        this.verifyPropertyChange();
    }

    public void testPropertyChange2() {
        this.objectHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildListener());
        this.verifyPropertyChange();
    }

    private void verifyPropertyChange() {
        this.event = null;
        this.objectHolder.setValue("bar");
        this.verifyEvent("foo", "bar");

        this.event = null;
        this.objectHolder.setValue(null);
        this.verifyEvent("bar", null);

        this.event = null;
        this.objectHolder.setValue("baz");
        this.verifyEvent(null, "baz");
    }

    private PropertyChangeListener buildListener() {
        return new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                SimplePropertyValueModelTests.this.event = e;
            }
        };
    }

    private void verifyEvent(Object oldValue, Object newValue) {
        assertEquals(this.objectHolder, this.event.getSource());
        assertEquals(ValueModel.VALUE, this.event.getPropertyName());
        assertEquals(oldValue, this.event.getOldValue());
        assertEquals(newValue, this.event.getNewValue());
    }

}
