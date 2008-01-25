/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.project;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;


public class RenameSessionAction extends AbstractFrameworkAction {

	public RenameSessionAction( WorkbenchContext context) {
		super( context);
	}

	protected void initialize() {
		this.initializeText( "RENAME_SESSION");
		this.initializeMnemonic( "RENAME_SESSION");
		// no accelerator
		this.initializeIcon( "RENAME");
		this.initializeToolTipText( "RENAME_SESSION.TOOL_TIP");
	}

	protected void execute( ApplicationNode selectedNode) {

		SessionAdapter session = ( SessionAdapter)selectedNode.getValue();
		TopLinkSessionsAdapter sessions = ( TopLinkSessionsAdapter)session.getParent();
		SimplePropertyValueModel stringHolder = new SimplePropertyValueModel();
		stringHolder.setValue( session.getName());

		RenameDialog dialog = new RenameDialog( getWorkbenchContext(), stringHolder, sessions.getAllSessionsNames());
		dialog.show();
		if( dialog.wasConfirmed()) {

			navigatorSelectionModel().pushExpansionState();
			session.setName(( String)stringHolder.getValue());
			navigatorSelectionModel().popAndRestoreExpansionState();
				
			((AbstractApplicationNode) selectedNode.getProjectRoot()).selectDescendantNodeForValue( session, navigatorSelectionModel());
		}
	}
}
