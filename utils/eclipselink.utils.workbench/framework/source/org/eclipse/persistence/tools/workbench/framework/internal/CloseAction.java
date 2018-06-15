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
package org.eclipse.persistence.tools.workbench.framework.internal;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * close the selected nodes;
 * built anew every time a node is selected, so it's always enabled;
 * this action is part of the SelectionMenu, while WorkbenchCloseAction is
 * part of the FileMenu and MainToolBar
 */
final class CloseAction
    extends AbstractFrameworkAction
{
    /** we need access to the node manager's internal api */
    private FrameworkNodeManager nodeManager;


    CloseAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
        super(context);
        this.nodeManager = nodeManager;
    }

    protected void initialize() {
        super.initialize();
        this.initializeTextAndMnemonic("file.close");
        this.initializeIcon("file.close");
        this.initializeToolTipText("file.close.toolTipText");
        this.initializeAccelerator("file.close.ACCELERATOR");
    }

    protected void execute() {
        this.nodeManager.close(this.selectedProjectNodes(), this.getWorkbenchContext());
    }

}
