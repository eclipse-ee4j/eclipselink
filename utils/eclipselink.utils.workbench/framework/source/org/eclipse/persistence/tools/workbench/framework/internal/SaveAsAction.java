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
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;

/**
 * loop through the selected "project" nodes and tell them each to save-as;
 * built anew every time a node is selected, so it's always enabled;
 * this action is part of the SelectionMenu, while WorkbenchSaveAsAction is
 * part of the FileMenu and MainToolBar
 */
final class SaveAsAction
    extends AbstractFrameworkAction
{
    /** we need access to the node manager's internal api */
    private FrameworkNodeManager nodeManager;


    SaveAsAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
        super(context);
        this.nodeManager = nodeManager;
    }

    protected void initialize() {
        super.initialize();
        this.initializeTextAndMnemonic("file.saveAs");
        this.initializeIcon("file.saveAs");
        this.initializeToolTipText("file.saveAs.toolTipText");
        this.initializeAccelerator("file.saveAs.ACCELERATOR");
    }

    protected void execute() {
        ApplicationNode[] projectNodes = this.selectedProjectNodes();
        for (int i = projectNodes.length; i-- > 0; ) {
            this.nodeManager.saveAs(projectNodes[i], getWorkbenchContext());
        }
    }

}
