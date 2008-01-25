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

import java.util.Collection;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.RenameDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public class RenamePoolAction extends AbstractEnablableFrameworkAction {

	public RenamePoolAction( WorkbenchContext context) {
		super( context);
	}

	protected void initialize() {
		super.initialize();
		this.initializeText( "RENAME_POOL");
		this.initializeMnemonic( "RENAME_POOL");
		// no accelerator
		this.initializeIcon( "RENAME");
		this.initializeToolTipText( "RENAME_POOL.TOOL_TIP");
	}

	protected void execute( ApplicationNode selectedNode) {

		ConnectionPoolAdapter pool = ( ConnectionPoolAdapter)selectedNode.getValue();
		ServerSessionAdapter session = ( ServerSessionAdapter)pool.getParent().getParent();
		SimplePropertyValueModel stringHolder = new SimplePropertyValueModel();
		stringHolder.setValue( pool.getName());
		Collection existingNames = CollectionTools.collection( session.poolNames());
		existingNames.add( ConnectionPoolAdapter.READ_CONNECTION_POOL_NAME);

		RenameDialog dialog = new RenameDialog( getWorkbenchContext(), stringHolder, existingNames);
		dialog.show();
		if( dialog.wasConfirmed()) {

			navigatorSelectionModel().pushExpansionState();
			pool.setName(( String)stringHolder.getValue());
			navigatorSelectionModel().popAndRestoreExpansionState();
				
			(( AbstractApplicationNode)selectedNode.getProjectRoot()).selectDescendantNodeForValue( pool, navigatorSelectionModel());
		}
	}

	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		
		ConnectionPoolAdapter pool = (ConnectionPoolAdapter) selectedNode.getValue();

		return !pool.isWriteConnectionPool() &&
				 !pool.isReadConnectionPool()  &&
				 !pool.isSequenceConnectionPool();
	}
}