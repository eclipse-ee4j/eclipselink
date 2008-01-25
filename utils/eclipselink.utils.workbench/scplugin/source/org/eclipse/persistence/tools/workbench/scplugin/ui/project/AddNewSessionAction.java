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


public class AddNewSessionAction extends AbstractFrameworkAction {

	private final boolean selectNode;

	public AddNewSessionAction( WorkbenchContext context) {
		this( context, true);
	}

	public AddNewSessionAction( WorkbenchContext context, boolean selectNode) {
		super( context);
		this.selectNode = selectNode;
	}

	protected void initialize() {
		super.initialize();
		this.initializeText( "ADD_SESSION");
		this.initializeMnemonic( "ADD_SESSION");
		// no accelerator
		this.initializeIcon( "ADD_SESSION");
		this.initializeToolTipText( "ADD_SESSION.TOOL_TIP");
	}

	protected void execute( ApplicationNode selectedNode) {

		TopLinkSessionsAdapter sessions = (TopLinkSessionsAdapter) selectedNode.getValue();
		SessionCreationDialog dialog = new SessionCreationDialog( getWorkbenchContext(), sessions.getAllSessionsNames());
		dialog.show();

		if( dialog.wasCanceled())
			return;

		navigatorSelectionModel().pushExpansionState();

		SessionAdapter newSession = dialog.addNewSessionTo(sessions);

		navigatorSelectionModel().popAndRestoreExpansionState();

		if( this.selectNode) {
			((AbstractApplicationNode) selectedNode.getProjectRoot()).selectDescendantNodeForValue( newSession, navigatorSelectionModel());
		}
	}

	/**
	 * make this public???
	 */
	public void execute() {
		super.execute();
	}
}