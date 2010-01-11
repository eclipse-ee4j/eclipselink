/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
