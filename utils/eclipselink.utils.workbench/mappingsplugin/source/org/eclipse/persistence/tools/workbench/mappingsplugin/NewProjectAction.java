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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import java.awt.Cursor;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * prompt the user for a project name and add the project
 * node to the navigator tree
 */
final class NewProjectAction extends AbstractFrameworkAction {
    private MappingsPlugin plugin;

    NewProjectAction(MappingsPlugin plugin, WorkbenchContext context) {
        super(context);
        this.plugin = plugin;
    }

    protected void initialize() {
        this.initializeTextAndMnemonic("NEW_PROJECT_ACTION");
        this.initializeAccelerator("NEW_PROJECT_ACTION.accelerator");
        this.initializeIcon("PROJECT.NEW");
        this.initializeToolTipText("NEW_PROJECT_ACTION.toolTipText");
    }

    protected void execute() {
        ProjectCreationDialog dialog = new ProjectCreationDialog(this.getWorkbenchContext());
        dialog.show();
        if (dialog.wasCanceled()) {
            return;
        }
        this.currentWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        this.navigatorSelectionModel().pushExpansionState();

        ApplicationNode node = this.plugin.buildProjectNode(dialog.getProject(), this.getWorkbenchContext());
        this.nodeManager().addProjectNode(node);
        this.navigatorSelectionModel().setSelectedNode(node);
        this.navigatorSelectionModel().expandNode(node);

        this.navigatorSelectionModel().popAndRestoreExpansionState();
        this.currentWindow().setCursor(Cursor.getDefaultCursor());
    }
}
