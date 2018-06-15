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

import java.util.Collection;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;


/**
 * Implementing class defines how a group of menu items should be formed. Additional
 * convenience methods are provided as well.
 *
 * @see org.eclipse.persistence.tools.workbench.framework.app.ButtonGroupDescription
 * @version 10.1.3
 */
public class MenuGroupDescription extends ButtonGroupDescription
{
    public MenuGroupDescription()
    {
        super();
    }

    /**
     * Overridden from the parent class to handle sub-menu merging. If a sub menu
     * has been merged and there are no sub-components, the sub-menu be removed.
     */
    protected void postUpdateOnActions(ComponentDescription desc, Collection frameworkActions)
    {
        // check to see if it is a sub-menu
        if (desc instanceof GroupContainerDescription)
        {
            if (!((GroupContainerDescription)desc).hasComponents())
            {
                remove(desc);
            }
        }
        else
        {
            super.postUpdateOnActions(desc, frameworkActions);
        }
    }

    /**
     * Convenience method for creating and adding a <code>MenuItemDescription</code>
     * for a <code>FrameworkAction</code>.
     */
    public void add(FrameworkAction action)
    {
        add(new MenuItemDescription(action));
    }

    /**
     * Convenience method for creating and adding a <code>SelectableMenuItemDescription</code>
     * for a <code>ToggleFrameworkAction</code>.
     */
    public void add(ToggleFrameworkAction action)
    {
        add(new ToggleMenuItemDescription(action));
    }
}
