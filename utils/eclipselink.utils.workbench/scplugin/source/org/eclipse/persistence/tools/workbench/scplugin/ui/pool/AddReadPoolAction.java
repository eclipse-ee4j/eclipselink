/*
 * @(#)AddNewPoolAction.java
 *
 * Copyright 2004 by Oracle Corporation,
 * 500 Oracle Parkway, Redwood Shores, California, 94065, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Oracle Corporation.
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