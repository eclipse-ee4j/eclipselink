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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;

import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;


/**
 * This button remains depressed if the action indicates it is selected.
 */
public class ToggleToolBarButton
    extends JButton
{
    private PropertyChangeListener actionListener;


    public ToggleToolBarButton() {
        super();
        this.actionListener = this.buildActionListener();
    }

    public ToggleToolBarButton(ToggleFrameworkAction action) {
        this();
        this.setAction(action);
    }

    private PropertyChangeListener buildActionListener() {
        return new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName() == ToggleFrameworkAction.TOGGLE_STATE_PROPERTY) {
                    ToggleToolBarButton.this.setSelected(((Boolean) e.getNewValue()).booleanValue());
                }
            }
        };
    }

    /**
     * @see javax.swing.AbstractButton#setAction(javax.swing.Action)
     */
    public void setAction(Action a) {
        ToggleFrameworkAction oldTFA = (ToggleFrameworkAction) this.getAction();
        ToggleFrameworkAction newTFA = (ToggleFrameworkAction) a;
        if (oldTFA != null) {
            oldTFA.removePropertyChangeListener(this.actionListener);
        }
        if (newTFA == null) {
            this.setSelected(false);
        } else {
            newTFA.addPropertyChangeListener(this.actionListener);
            this.setSelected(newTFA.isSelected());
        }
        super.setAction(a);
    }

}
