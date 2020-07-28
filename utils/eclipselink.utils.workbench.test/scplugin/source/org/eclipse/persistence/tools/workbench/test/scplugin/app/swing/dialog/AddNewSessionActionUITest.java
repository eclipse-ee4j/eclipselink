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
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing.dialog;


import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.AddNewSessionAction;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.SessionCreationDialog;


public class AddNewSessionActionUITest extends AddNewSessionAction  {

    public AddNewSessionActionUITest( WorkbenchContext context) {
        super( context);
    }

    public void execute() {
        ApplicationNode selectedNode;

        selectedNode = this.firstProjectNode();

        this.execute( selectedNode);
    }

    protected void execute( ApplicationNode selectedNode) {

        TopLinkSessionsAdapter sessions = (TopLinkSessionsAdapter) selectedNode.getValue();
        SessionCreationDialog dialog = new SessionCreationDialog( getWorkbenchContext(), sessions.getAllSessionsNames());
        dialog.show();
        if( dialog.wasConfirmed()) {
            SessionAdapter newSession = dialog.addNewSessionTo(( TopLinkSessionsAdapter)selectedNode.getValue());

        }
    }

    private ApplicationNode firstProjectNode()
    {
        return nodeManager().projectNodesFor( null)[0];
    }
}
