/*
 * Copyright (c) 2008, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.workbench.scplugin.ui.pool;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;

public class AddReadPoolAction extends AbstractEnablableFrameworkAction {

    public AddReadPoolAction( WorkbenchContext context) {
        super( context);
    }

    protected void initialize() {
        super.initialize();
        this.initializeText( "ADD_READ_CONNECTION_POOL");
        this.initializeMnemonic( "ADD_READ_CONNECTION_POOL");
        // no accelerator
        this.initializeIcon( "CONNECTION_POOL_READ");
        this.initializeToolTipText( "ADD_READ_CONNECTION_POOL.TOOL_TIP");
    }

    protected void execute( ApplicationNode selectedNode) {

        ServerSessionAdapter session = ( ServerSessionAdapter)selectedNode.getValue();

        navigatorSelectionModel().pushExpansionState();
        ConnectionPoolAdapter newPool = session.addReadConnectionPool();

        navigatorSelectionModel().popAndRestoreExpansionState();

        (( AbstractApplicationNode)selectedNode.getProjectRoot()).selectDescendantNodeForValue( newPool, navigatorSelectionModel());
    }

    protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
        ServerSessionAdapter session = (ServerSessionAdapter) selectedNode.getValue();

        return !session.hasReadPool();
    }

    protected String[] enabledPropertyNames() {
        return new String[] {ServerSessionAdapter.READ_CONNECTION_POOL_PROPERTY};
    }

    private void promptUserToTurnOffExternalConnectionPooling()
    {

        JOptionPane.showMessageDialog(getWorkbenchContext().getCurrentWindow(),
                resourceRepository().getString("EXTERNAL_CONNECTION_POOLING_ENABLED_WARNING_MESSAGE"));

    }
}
