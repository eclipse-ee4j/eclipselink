/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;


public class AddWritePoolAction extends AbstractEnablableFrameworkAction {

	public AddWritePoolAction( WorkbenchContext context) {
		super( context);
	}

	protected void initialize() {
		super.initialize();
		this.initializeText( "ADD_WRITE_CONNECTION_POOL");
		this.initializeMnemonic( "ADD_WRITE_CONNECTION_POOL");
		// no accelerator
		this.initializeIcon( "CONNECTION_POOL_WRITE");
		this.initializeToolTipText( "ADD_WRITE_CONNECTION_POOL.TOOL_TIP");
	}

	protected void execute( ApplicationNode selectedNode) {

		ServerSessionAdapter session = ( ServerSessionAdapter)selectedNode.getValue();

		navigatorSelectionModel().pushExpansionState();
		ConnectionPoolAdapter newPool = session.addWriteConnectionPool();

		navigatorSelectionModel().popAndRestoreExpansionState();

		(( AbstractApplicationNode)selectedNode.getProjectRoot()).selectDescendantNodeForValue( newPool, navigatorSelectionModel());
	}

	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		ServerSessionAdapter session = (ServerSessionAdapter) selectedNode.getValue();
		
		return !session.hasWritePool();
	}
	
	protected String[] enabledPropertyNames() {
		return new String[] {ServerSessionAdapter.WRITE_CONNECTION_POOL_PROPERTY};
	}
	
	private void promptUserToTurnOffExternalConnectionPooling()
	{
		
		JOptionPane.showMessageDialog(getWorkbenchContext().getCurrentWindow(), 
				resourceRepository().getString("EXTERNAL_CONNECTION_POOLING_ENABLED_WARNING_MESSAGE"));
	
	}

}
