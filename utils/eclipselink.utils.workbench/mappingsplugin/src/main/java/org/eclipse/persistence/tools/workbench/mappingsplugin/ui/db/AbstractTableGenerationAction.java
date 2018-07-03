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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



abstract class AbstractTableGenerationAction extends AbstractEnablableFrameworkAction {

    protected AbstractTableGenerationAction(WorkbenchContext context) {
        super(context);
    }

    protected DatabaseNode databaseNode() {
        return (DatabaseNode) this.selectedNodes()[0].getParent();
    }

    protected MWDatabase database() {
        return this.databaseNode().getDatabase();
    }

    protected Collection selectedMWTables() {
        ApplicationNode[] nodes = this.selectedNodes();
        int len = nodes.length;
        Collection selectedTables = new ArrayList(len);
        for (int i = 0; i < len; i++) {
            selectedTables.add(((TableNode) nodes[i]).getTable());
        }
        return selectedTables;
    }

    protected Collection allMWTables() {
        return CollectionTools.collection(this.database().tables());
    }

    protected void checkDevelopmentLoginSpec(String dialogTitleKey) {
        if (this.database().getDevelopmentLoginSpec() == null) {
            JOptionPane.showMessageDialog(
                this.getWorkbenchContext().getCurrentWindow(),
                this.resourceRepository().getString("NO_LOGIN_DEFINED_DIALOG.message"),
                this.resourceRepository().getString(dialogTitleKey),
                JOptionPane.WARNING_MESSAGE
            );
            throw new IllegalStateException();
        }
    }

}
