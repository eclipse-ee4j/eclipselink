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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBoxMenuItem;

import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;


/**
 * @version 10.1.3
 */
public class ToggleMenuItem extends JCheckBoxMenuItem
{

    public ToggleMenuItem(ToggleFrameworkAction action)
    {
        super(action);
        initializeSelectionListener(action);
    }

    private void initializeSelectionListener(ToggleFrameworkAction action)
    {
        action.addPropertyChangeListener(new SelectionListener());
        setSelected(action.isSelected());
    }

    private class SelectionListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent event)
        {
            if (event.getPropertyName() == ToggleFrameworkAction.TOGGLE_STATE_PROPERTY)
            {
                setSelected(((Boolean)event.getNewValue()).booleanValue());
            }
        }
    }

}
