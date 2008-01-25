/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool;

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
}