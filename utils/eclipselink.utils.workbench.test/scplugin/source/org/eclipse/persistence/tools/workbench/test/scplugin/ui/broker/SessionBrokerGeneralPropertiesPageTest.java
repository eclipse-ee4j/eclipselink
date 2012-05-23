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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.broker;

import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.SCAbstractPanelTest;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DataSource;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerPlatform;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionBrokerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.broker.SessionBrokerSessionsPropertiesPage;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public class SessionBrokerGeneralPropertiesPageTest extends SCAbstractPanelTest
{
	public SessionBrokerGeneralPropertiesPageTest(String name)
	{
		super(name);
	}

	public static void main(String[] args) throws Exception
	{
		new SessionBrokerGeneralPropertiesPageTest(null).execute(args);
	}

	protected DataSource buildDataSource( String platformName)
	{
		DatabasePlatform platform = DatabasePlatformRepository.getDefault().platformNamed( platformName);
		return new DataSource( platform);
	}

	protected ServerPlatform noServerPlatform() {

		return new ServerPlatform( "NoServerPlatform");
	}

	protected void _testComponentEnablerButtons() throws Exception
	{
		TopLinkSessionsAdapter sessions = getTopLinkSessions();
		SessionBrokerAdapter broker =  sessions.addSessionBrokerNamed("MySessionBroker", noServerPlatform());

		// Retrieve the buttons
		JButton addButton    = (JButton) retrieveChildComponent("PROJECT_SESSIONS_ADD_BUTTON",    getPane());
		JButton removeButton = (JButton) retrieveChildComponent("PROJECT_SESSIONS_REMOVE_BUTTON", getPane());
		JButton renameButton = (JButton) retrieveChildComponent("PROJECT_SESSIONS_RENAME_BUTTON", getPane());

		// Change the underlying model
		getNodeHolder().setValue(retrieveNode(getProjectNode(), broker));
		getSelectionHolder().setValue(broker);

		// Test the buttons
		assertTrue (addButton   .isEnabled());
		assertFalse(removeButton.isEnabled());
		assertFalse(renameButton.isEnabled());

		// Remove all the sessions first
		sessions.removeSessions(CollectionTools.collection(sessions.databaseSessions()));

		// Test the buttons
		assertFalse(addButton   .isEnabled());
		assertFalse(removeButton.isEnabled());
		assertFalse(renameButton.isEnabled());

		// Add an item in the list
		DatabaseSessionAdapter session = sessions.addDatabaseSessionNamed("MyDatabaseSession", noServerPlatform(), buildDataSource("Oracle10g"));

		// Test the buttons
		assertTrue (addButton   .isEnabled());
		assertFalse(removeButton.isEnabled());
		assertFalse(renameButton.isEnabled());

		broker.manage(session.getName());

		simulateMnemonic("PROJECT_SESSIONS_LIST");

		// This will select the first item in the list
		simulateKey(KeyEvent.VK_DOWN);

		// Test the buttons
		assertFalse(addButton   .isEnabled());
		assertTrue (removeButton.isEnabled());
		assertTrue (renameButton.isEnabled());

		// Remove the selected item
		broker.unManage(session.getName());

		// Test the buttons
		assertTrue (addButton   .isEnabled());
		assertFalse(removeButton.isEnabled());
		assertFalse(renameButton.isEnabled());
	}

	protected PropertyValueModel buildNodeHolder(ApplicationNode projectNode)
	{
		SessionBrokerAdapter broker = (SessionBrokerAdapter) getTopLinkSessions().sessionNamed("SC-SessionBrokerTest");
		return new SimplePropertyValueModel(retrieveNode(projectNode, broker));
	}

	protected JComponent buildPane() throws Exception
	{
		return buildPage(SessionBrokerSessionsPropertiesPage.class, getNodeHolder());
	}

	protected SCAdapter buildSelection()
	{
		return (SessionBrokerAdapter) getTopLinkSessions().sessionNamed("SC-SessionBrokerTest");
	}

	protected void clearModel()
	{
	}

	protected void printModel()
	{
	}

	protected void resetProperty()
	{
	}

	protected void restoreModel()
	{
	}

	protected String windowTitle()
	{
		return "SessionBroker - General Tab Test";
	}
}
