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
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * Object to be removed must implement Removable
 *
 */
public final class RemoveAction extends AbstractFrameworkAction {

    public RemoveAction(WorkbenchContext context) {
        this(context, "remove");
    }

    public RemoveAction(WorkbenchContext context, String iconKey) {
        super(context);
        initializeIcon(iconKey);
    }

    protected void initialize() {
        super.initialize();

        initializeTextAndMnemonic("REMOVE_ACTION");
        initializeToolTipText("REMOVE_ACTION.toolTipText");
    }

    protected void execute() {
        ApplicationNode[] selectedNodes = selectedNodes();
        if (selectedNodes.length == 1) {
            if (!confirmSingleItemRemoval((RemovableNode) selectedNodes[0])) {
                return;
            }
        }
        else {
            if (!confirmMultipleItemRemoval()) {
                return;
            }
        }

        for (int i = 0; i < selectedNodes.length; i ++) {
            ((RemovableNode) selectedNodes[i]).remove();
        }

    }

    protected boolean confirmSingleItemRemoval(RemovableNode removable) {
        int option = JOptionPane.showConfirmDialog(getWorkbenchContext().getCurrentWindow(),
                                      resourceRepository().getString("CONFIRM_REMOVE.message", removable.getName()),
                                      resourceRepository().getString("CONFIRM_REMOVE.title"),
                                      JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }

    protected boolean confirmMultipleItemRemoval() {
        int option = JOptionPane.showConfirmDialog(getWorkbenchContext().getCurrentWindow(),
                                      resourceRepository().getString("CONFIRM_MULTIPLE_REMOVE.message"),
                                      resourceRepository().getString("CONFIRM_REMOVE.title"),
                                      JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }

}
