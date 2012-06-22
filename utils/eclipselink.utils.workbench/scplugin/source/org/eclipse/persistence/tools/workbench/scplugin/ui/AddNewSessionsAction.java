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
package org.eclipse.persistence.tools.workbench.scplugin.ui;

import java.io.File;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsProperties;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public class AddNewSessionsAction extends AbstractFrameworkAction {
	
	private SCPlugin plugin;

	public AddNewSessionsAction( WorkbenchContext context, SCPlugin plugin) {

		super( context);
		this.plugin = plugin;
	}

	protected void initialize() {
		this.initializeText( "NEW_SESSIONS_CONFIGURATION");
		this.initializeMnemonic( "NEW_SESSIONS_CONFIGURATION");
		// no accelerator
		this.initializeIcon( "NEW_SESSIONS_CONFIGURATION");
		this.initializeToolTipText( "NEW_SESSIONS_CONFIGURATION.TOOL_TIP");
	}

	protected void execute() {

		navigatorSelectionModel().pushExpansionState();

		File sessionsFile = this.plugin.nextUntitledFile( getApplicationContext());
		ApplicationNode newProjectNode = buildTopLinkSessionsNode( sessionsFile);

		nodeManager().addProjectNode( newProjectNode);

		navigatorSelectionModel().setSelectedNode(newProjectNode);
		navigatorSelectionModel().popAndRestoreExpansionState();
	}

	private ApplicationNode buildTopLinkSessionsNode( File sessionsFile) {

		SCSessionsProperties properties = this.plugin.getSessionsProperties( getApplicationContext(), sessionsFile);
		TopLinkSessionsAdapter topLinkSessions = this.buildTopLinkSessions( properties);
		updateClasspath( topLinkSessions);

		return new ProjectNode( topLinkSessions, this.nodeManager().getRootNode(), this.plugin, this.getApplicationContext());
	}
	/**
	 * Builds the TopLinkSessions config model and returns its adapter.
	 */
	private TopLinkSessionsAdapter buildTopLinkSessions( SCSessionsProperties properties) {

		return new TopLinkSessionsAdapter( properties, preferences(), true);
	}

	private void updateClasspath( TopLinkSessionsAdapter topLinkSessions) {

		String classpath = preferences().get(SCPlugin.DEFAULT_CLASSPATH_PREFERENCE, null);
		if (classpath != null && !"".equals(classpath)) {
			String[] entries = classpath.split(System.getProperty("path.separator"));
			topLinkSessions.getClassRepository().addClasspathEntries(0, CollectionTools.list(entries));
		}
	}
}
