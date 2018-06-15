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
package org.eclipse.persistence.testing.models.weaving;

import java.beans.*;

public class TestingPropertyChangeListener implements PropertyChangeListener {

    boolean triggered = false;
    Object source;
    boolean sameSource = false;
    String attribute;
    boolean correctAttribute = false;
    Object oldValue;
    boolean correctOldValue = false;
    Object newValue;
    boolean correctNewValue = false;

    public TestingPropertyChangeListener(Object source, String attribute,
        Object oldValue, Object newValue) {
        reset(source, attribute, oldValue, newValue);
    }

    public boolean isTriggered() {
        return triggered;
    }

    public Object getSource() {
        return source;
    }
    public void setSource(Object source) {
        this.source = source;
    }
    public boolean isSameSource() {
        return sameSource;
    }

    public String getAttribute() {
        return attribute;
    }
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    public boolean isCorrectAttribute() {
        return correctAttribute;
    }

    public Object getOldValue() {
        return oldValue;
    }
    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }
    public boolean isCorrectOldValue() {
        return correctOldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }
    public boolean isCorrectNewValue() {
        return correctNewValue;
    }

    public void reset(Object source, String attribute, Object oldValue,
        Object newValue) {
        triggered = false;
        this.source = source;
        sameSource = false;
        this.attribute = attribute;
        correctAttribute = false;
        this.oldValue = oldValue;
        correctOldValue = false;
        this.newValue = newValue;
        correctNewValue = false;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        triggered = true;
        sameSource = (source == evt.getSource());
        correctAttribute = evt.getPropertyName().equals(attribute);
        if (oldValue == null) {
            correctOldValue = evt.getOldValue() == null;
        }
        else {
            correctOldValue = oldValue.equals(evt.getOldValue());
        }
        if (newValue == null) {
            correctNewValue = evt.getNewValue() == null;
        }
        else {
            correctNewValue = newValue.equals(evt.getNewValue());
        }
    }
}
