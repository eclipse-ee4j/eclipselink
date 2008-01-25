/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing.dialog;


import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.AddNewSessionAction;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.SessionCreationDialog;


public class AddNewSessionActionUITest extends AddNewSessionAction  {

	public AddNewSessionActionUITest( WorkbenchContext context) {
		super( context);
	}

	public void execute() {
		ApplicationNode selectedNode;

		selectedNode = this.firstProjectNode();
		
		this.execute( selectedNode);
	}
	
	protected void execute( ApplicationNode selectedNode) {
		
		TopLinkSessionsAdapter sessions = (TopLinkSessionsAdapter) selectedNode.getValue();
		SessionCreationDialog dialog = new SessionCreationDialog( getWorkbenchContext(), sessions.getAllSessionsNames());
		dialog.show();
		if( dialog.wasConfirmed()) {
			SessionAdapter newSession = dialog.addNewSessionTo(( TopLinkSessionsAdapter)selectedNode.getValue());
			
		}
	}
	
	private ApplicationNode firstProjectNode()
	{
		return nodeManager().projectNodesFor( null)[0];
	}
}
