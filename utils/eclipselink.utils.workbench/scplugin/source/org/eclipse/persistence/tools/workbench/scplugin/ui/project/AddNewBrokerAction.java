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
package org.eclipse.persistence.tools.workbench.scplugin.ui.project;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.NullServerPlatformAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatform;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionBrokerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public class AddNewBrokerAction extends AbstractFrameworkAction {

	private final boolean selectNode;

	public AddNewBrokerAction( WorkbenchContext context) {
		this( context, true);
	}

	public AddNewBrokerAction( WorkbenchContext context, boolean selectNode) {
		super( context);
		this.selectNode = selectNode;
	}

	protected void initialize() {
		super.initialize();
		this.initializeText( "ADD_BROKER");
		this.initializeMnemonic( "ADD_BROKER");
		// no accelerator
		this.initializeIcon( "ADD_BROKER");
		this.initializeToolTipText( "ADD_BROKER.TOOL_TIP");
	}

	private ObjectListSelectionModel buildSelectionModel(CollectionValueModel itemHolder) {

		ListModelAdapter adapter = new ListModelAdapter(itemHolder);
		return new ObjectListSelectionModel(adapter);
	}

	private CollectionValueModel buildSessionCollectionHolder(TopLinkSessionsAdapter topLinkSessions) {

		Collection sessions = CollectionTools.collection(topLinkSessions.databaseSessions());
		return new ReadOnlyCollectionValueModel(sessions);
	}

	private Collection buildSessionBrokerNames(TopLinkSessionsAdapter topLinkSessions) {
		return CollectionTools.collection(topLinkSessions.sessionBrokerNames());
	}

	public void execute() {
		super.execute();
	}

	protected void execute( ApplicationNode selectedNode) {

		TopLinkSessionsAdapter topLinkSessions = ( TopLinkSessionsAdapter) selectedNode.getValue();
		SimplePropertyValueModel stringHolder = new SimplePropertyValueModel();
		SimplePropertyValueModel serverPlatformHolder = new SimplePropertyValueModel();
		CollectionValueModel itemHolder = buildSessionCollectionHolder(topLinkSessions);
		ObjectListSelectionModel selectionModel = buildSelectionModel(itemHolder);
		Collection sessionBrokerNames = buildSessionBrokerNames(topLinkSessions);

		BrokerCreationDialog dialog = new BrokerCreationDialog(
			getWorkbenchContext(),
			itemHolder,
			selectionModel,
			stringHolder,
			serverPlatformHolder,
			sessionBrokerNames
		);

		dialog.show();

		if( dialog.wasCanceled())
			return;

		navigatorSelectionModel().pushExpansionState();

		// Server Platform
		ServerPlatform sp;

		if( dialog.usesServerPlatform()) {
			sp = new ServerPlatform(( String)serverPlatformHolder.getValue()); 
		}
		else {
			String serverClassName = NullServerPlatformAdapter.instance().getServerClassName();
			sp = new ServerPlatform( ClassTools.shortNameForClassNamed( serverClassName));
		}

		// Create the Session Broker
		SessionBrokerAdapter newBroker = topLinkSessions.addSessionBrokerNamed( (String) stringHolder.getValue(), sp);

		// All all the sessions to the broker
		Iterator iter = CollectionTools.iterator( selectionModel.getSelectedValues());

		while( iter.hasNext()) {
			SessionAdapter sessionAdapter = ( SessionAdapter)iter.next();
			newBroker.manage( sessionAdapter.getName());
		}

		navigatorSelectionModel().popAndRestoreExpansionState();

		if( this.selectNode) {
			(( AbstractApplicationNode)selectedNode.getProjectRoot()).selectDescendantNodeForValue( newBroker, navigatorSelectionModel());
		}
	}
}
