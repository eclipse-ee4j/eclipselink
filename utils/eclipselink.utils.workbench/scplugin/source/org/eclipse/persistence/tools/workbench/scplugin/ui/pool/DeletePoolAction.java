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

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;


public class DeletePoolAction extends AbstractEnablableFrameworkAction {

	public DeletePoolAction( WorkbenchContext context) {
		super( context);
	}

	protected void initialize() {
		super.initialize();
		this.initializeText( "DELETE_CONNECTION_POOL");
		this.initializeMnemonic( "DELETE_CONNECTION_POOL");
		// no accelerator
		this.initializeIcon( "DELETE");
		this.initializeToolTipText( "DELETE_CONNECTION_POOL.TOOL_TIP");
	}

	protected void execute( ApplicationNode selectedNode) {
		ConnectionPoolAdapter pool = ( ConnectionPoolAdapter)selectedNode.getValue();
		String name = pool.getName();

		if( confirmDeletion( name)) {
			ServerSessionAdapter session = ( ServerSessionAdapter)selectedNode.getParent().getValue();
			if( pool.isWriteConnectionPool()) {
				session.removeWriteConnectionPool();
			}
			else if( pool.isSequenceConnectionPool()) {
				session.removeSequenceConnectionPool();
			}
			else {
				session.removeConnectionPoolNamed( name);
			}
		}

		navigatorSelectionModel().setSelectedNode(( ApplicationNode) selectedNode.getParent());
	}
	
	private boolean confirmDeletion( String name) {
		int option = JOptionPane.showConfirmDialog(getWorkbenchContext().getCurrentWindow(),
										resourceRepository().getString( "DELETE_CONNECTION_POOL_WARNING", name),
										resourceRepository().getString( "DELETE_CONNECTION_POOL_WARNING_TITLE"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.WARNING_MESSAGE);
										
		return ( option == JOptionPane.YES_OPTION);
	}
	
	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		ConnectionPoolAdapter pool = (ConnectionPoolAdapter) selectedNode.getValue();

		return !(pool.isReadConnectionPool());
	}
}