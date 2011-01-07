/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.Vector;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractEnablableFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;


public class DeleteSessionAction extends AbstractEnablableFrameworkAction {

	public DeleteSessionAction( WorkbenchContext context) {
		super( context);
	}

	protected void initialize()
	{
		super.initialize();
		this.initializeText( "DELETE_SESSION");
		this.initializeMnemonic( "DELETE_SESSION");
		this.initializeToolTipText( "DELETE_SESSION.TOOL_TIP");
		this.initializeIcon( "DELETE");
	}

	protected void execute( ApplicationNode selectedNode) {

		SessionAdapter session = ( SessionAdapter)selectedNode.getValue();
		TopLinkSessionsAdapter topLinkSessions = ( TopLinkSessionsAdapter)session.getParent();

		if( !canRemoveSessions( topLinkSessions, session)) {
			return;
		}

		if( session.isManaged()) {
			DatabaseSessionAdapter dbSession = (DatabaseSessionAdapter) session;
			dbSession.getBroker().unManage(session.getName());
		}
		else {
			topLinkSessions.removeSession( session);
		}

		navigatorSelectionModel().setSelectedNode(( ApplicationNode) selectedNode.getParent());
	}

	/**
	 * Determines whether if the given collection of {@link SessionAdapter}s can
	 * be removed from the list.
	 *
	 * @param sessions The collection of {@link SessionAdapter}s that have been
	 * selected to be removed
	 * @return <code>true<code> if they can be removed; <code>false<code>
	 * otherwise
	 */
	protected boolean canRemoveSessions(TopLinkSessionsAdapter topLinkSessions,
													SessionAdapter session)
	{
		if (session.isManaged())
			return true;

		String name = topLinkSessions.getName();

		int confirmation = JOptionPane.showConfirmDialog
		(
			getWorkbenchContext().getCurrentWindow(),
			resourceRepository().getString("PROJECT_SESSIONS_PROMPT_REMOVE_MULTI", name),
			resourceRepository().getString("PROJECT_SESSIONS_PROMPT_REMOVE_MULTI_TITLE"),
			JOptionPane.YES_NO_OPTION
		);

		return (confirmation == JOptionPane.OK_OPTION);
	}

	public void execute() {
		super.execute();
	}

	protected void updateEnabledState() {

		ApplicationNode[] nodes = selectedNodes();
		Vector unmanagedSessions = new Vector();

		if( nodes.length > 0) {
			for( int index = 0; index < nodes.length; index++) {
				ApplicationNode node = nodes[index];
				SessionAdapter session = ( SessionAdapter)node.getValue();

				if ( session.isManaged()) {
					unmanagedSessions.add( session);
				}
			}
		}

		if(( unmanagedSessions.size() > 0) && (nodes.length == unmanagedSessions.size())) {
			this.initializeText( "UNMANAGED_SESSION");
			this.initializeMnemonic( "UNMANAGED_SESSION");
			this.initializeToolTipText( "UNMANAGED_SESSION.TOOL_TIP");
			this.initializeIcon( "REMOVE");
		}
		else {
			this.initializeText( "DELETE_SESSION");
			this.initializeMnemonic( "DELETE_SESSION");
			this.initializeToolTipText( "DELETE_SESSION.TOOL_TIP");
			this.initializeIcon( "DELETE");
		}
	}
	
	protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
		throw new UnsupportedOperationException();
	}
}
