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

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.action.ToggleFrameworkAction;

/**
 * Implementing class defines how a group of toolbar items should be formed. Additional
 * convenience methods are provided as well.
 *
 * @see org.eclipse.persistence.tools.workbench.framework.app.ButtonGroupDescription
 * @version 10.1.3
 */
public class ToolBarButtonGroupDescription extends ButtonGroupDescription
{

    public ToolBarButtonGroupDescription()
    {
        super();
    }

    /**
     * Adds a <code>ToolBarButtonDescription</code> for the given
     * <code>FrameworkAction</code>.
     */
    public void add(FrameworkAction action)
    {
        add(new ToolBarButtonDescription(action));
    }

    /**
     * Adds a <code>SelectableToolBarButtonDescription</code> for the
     * given <code>SelectableFrameworkAction</code>.
     */
    public void add(ToggleFrameworkAction action)
    {
        add(new ToggleToolBarButtonDescription(action));
    }
}
