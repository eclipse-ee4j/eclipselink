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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.login;

// JDK
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.basic.AbstractSessionPanelTest;

// Mapping Workbench
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.login.RdbmsConnectionPropertiesPage;

// Mapping Workbench Test

/**
 * Tests for {@link org.eclipse.persistence.tools.workbench.scplugin.ui.session.login.SequencingPropertiesPage}.
 *
 * @author Pascal Filion
 * @version 10.0.3
 */
public class RdbmsConnectionPropertiesPageTest extends AbstractSessionPanelTest
{
	public RdbmsConnectionPropertiesPageTest(String name)
	{
		super(name);
	}

	public static void main(String[] args) throws Exception
	{
		new RdbmsConnectionPropertiesPageTest("RdbmsConnectionPropertiesPageTest").execute(args);
	}

	protected void _testFocusTransferDatabaseDriver() throws Exception
	{
		testFocusTransferByMnemonic("CONNECTION_RDBMS_DATABASE_DRIVER_COMBO_BOX", COMPONENT_COMBO_BOX);
	}

	protected void _testFocusTransferDatabaseSourceURL() throws Exception
	{
		getLogin().setDatabaseDriverAsDataSource();

		testFocusTransferByMnemonic("CONNECTION_RDBMS_DATA_SOURCE_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferDriverClass() throws Exception
	{
		getLogin().setDatabaseDriverAsDriverManager();

		testFocusTransferByMnemonic("CONNECTION_RDBMS_DRIVER_CLASS_COMBO_BOX", COMPONENT_COMBO_BOX);
	}

	protected void _testFocusTransferDriverURL() throws Exception
	{
		getLogin().setDatabaseDriverAsDriverManager();

		testFocusTransferByMnemonic("CONNECTION_RDBMS_DRIVER_URL_COMBO_BOX", COMPONENT_COMBO_BOX);
	}

	protected void _testFocusTransferExternalConnectionPooling() throws Exception
	{
		testFocusTransferByMnemonic("CONNECTION_EXTERNAL_CONNECTION_POOLING_CHECK_BOX", COMPONENT_CHECK_BOX);
	}

	protected void _testFocusTransferPassword() throws Exception
	{
		testFocusTransferByMnemonic("CONNECTION_PASSWORD_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferUsername() throws Exception
	{
		testFocusTransferByMnemonic("CONNECTION_USER_NAME_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected JComponent buildPane() throws Exception
	{
		return buildPage(RdbmsConnectionPropertiesPage.class, getNodeHolder());
	}

	private DatabaseLoginAdapter getLogin()
	{
		DatabaseSessionAdapter session = (DatabaseSessionAdapter) getSession();
		return (DatabaseLoginAdapter) session.getLogin();
	}

	protected void printModel()
	{
	}

	protected void resetProperty()
	{
	}

	protected String windowTitle()
	{
		return "RDBMS Connection Page Test";
	}
}
