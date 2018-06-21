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
package org.eclipse.persistence.tools.workbench.platformsplugin.ui.type;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;


/**
 * delete all the selected nodes, after user confirmation
 */
final class DeleteDatabaseTypeAction extends AbstractFrameworkAction {

    public DeleteDatabaseTypeAction(WorkbenchContext context) {
        super(context);
    }

    protected void initialize() {
        this.initializeTextAndMnemonic("DELETE_DATABASE_TYPE");
        // no accelerator
        this.initializeIcon("DELETE_DATABASE_TYPE");
        this.initializeToolTipText("DELETE_DATABASE_TYPE.TOOL_TIP");
    }

    protected void execute() {
        int response = JOptionPane.showConfirmDialog(
                        this.currentWindow(),
                        this.confirmMessage(),
                        this.confirmTitle(),
                        JOptionPane.YES_NO_OPTION
        );
        if (response == JOptionPane.YES_OPTION) {
            super.execute();
        }
    }

    protected void execute(ApplicationNode selectedNode) {
        DatabaseType databaseType = ((DatabaseTypeNode) selectedNode).getDatabaseType();
        databaseType.getPlatform().removeDatabaseType(databaseType);
    }

    private String confirmMessage() {
        return this.resourceRepository().getString("DELETE_DATABASE_TYPE_DIALOG_MESSAGE");
    }

    private String confirmTitle() {
        return this.resourceRepository().getString("DELETE_DATABASE_TYPE_DIALOG_TITLE");
    }

}
