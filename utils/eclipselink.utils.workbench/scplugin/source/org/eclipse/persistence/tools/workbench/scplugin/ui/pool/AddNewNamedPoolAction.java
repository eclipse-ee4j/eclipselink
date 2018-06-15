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
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;


public class AddNewNamedPoolAction extends AbstractFrameworkAction {

    public AddNewNamedPoolAction( WorkbenchContext context) {
        super( context);
    }

    protected void initialize() {
        super.initialize();
        this.initializeText( "ADD_NAMED_CONNECTION_POOL");
        this.initializeMnemonic( "ADD_NAMED_CONNECTION_POOL");
        // no accelerator
        this.initializeIcon( "CONNECTION_POOL");
        this.initializeToolTipText( "ADD_NAMED_CONNECTION_POOL.TOOL_TIP");
    }

    protected void execute( ApplicationNode selectedNode) {

        ServerSessionAdapter session = ( ServerSessionAdapter)selectedNode.getValue();
        SimplePropertyValueModel stringHolder = new SimplePropertyValueModel();

        NamedConnectionPoolCreationDialog dialog = new NamedConnectionPoolCreationDialog
        (
                getWorkbenchContext(),
                stringHolder,
                session.poolNames()
        );

        dialog.show();

        if( dialog.wasConfirmed()) {

            navigatorSelectionModel().pushExpansionState();
            ConnectionPoolAdapter newPool = session.addConnectionPoolNamed( (String) stringHolder.getValue());

            navigatorSelectionModel().popAndRestoreExpansionState();

            (( AbstractApplicationNode)selectedNode.getProjectRoot()).selectDescendantNodeForValue( newPool, navigatorSelectionModel());
        }
    }

    public void execute() {
        super.execute();
    }

    private void promptUserToTurnOffExternalConnectionPooling()
    {

        JOptionPane.showMessageDialog(getWorkbenchContext().getCurrentWindow(),
                resourceRepository().getString("EXTERNAL_CONNECTION_POOLING_ENABLED_WARNING_MESSAGE"));

    }
}
