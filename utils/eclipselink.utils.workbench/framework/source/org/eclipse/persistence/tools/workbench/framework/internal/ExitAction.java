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
package org.eclipse.persistence.tools.workbench.framework.internal;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * exit the application;
 * always enabled
 */
final class ExitAction
    extends AbstractFrameworkAction
{
    /** we need access to the node manager's internal api */
    private FrameworkNodeManager nodeManager;

    ExitAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
        super(context);
        this.nodeManager = nodeManager;
    }

    protected void initialize() {
        this.initializeTextAndMnemonic("EXIT_ACTION");
        this.initializeIcon("EXIT");
        this.initializeToolTipText("EXIT_ACTION.TOOL_TIP");
    }

    public void execute() {
        nodeManager.exit(this.getWorkbenchContext());
    }

}
