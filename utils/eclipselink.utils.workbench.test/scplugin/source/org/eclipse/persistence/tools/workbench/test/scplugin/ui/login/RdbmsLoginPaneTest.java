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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.login;

import java.awt.event.KeyEvent;
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.SCAbstractPanelTest;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


public class RdbmsLoginPaneTest extends AbstractLoginPaneTest
{
	public RdbmsLoginPaneTest(SCAbstractPanelTest parentTest,
									  PropertyValueModel nodeHolder,
									  DatabaseLoginAdapter selection)
	{
		super(parentTest, nodeHolder, selection);
	}

	protected void _testComponentEntryDatabaseDriver() throws Exception
	{
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) getSelection();
		login.setDatabaseDriverAsDriverManager();
		login.setDriverClassName("something");
		login.setConnectionURL("something");

		simulateMnemonic("CONNECTION_RDBMS_DATABASE_DRIVER_COMBO_BOX");
		simulateKey(KeyEvent.VK_DOWN);

		assertNull(login.getDriverClassName());
		assertNull(login.getConnectionURL());
	}

	protected void _testComponentEntryDataSourceName() throws Exception
	{
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) getSelection();
		login.setDatabaseDriverAsDataSource();

		simulateMnemonic("CONNECTION_RDBMS_DATA_SOURCE_FIELD");
		simulateTextInput("My Data Source");

		assertEquals("My Data Source", login.getDataSourceName());
	}

	protected void _testComponentEntryDriverClass() throws Exception
	{
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) getSelection();
		login.setDatabaseDriverAsDriverManager();

		simulateMnemonic("CONNECTION_RDBMS_DRIVER_CLASS_COMBO_BOX");
		simulateComboBoxTextInput("oracle.jdbc.driver.OracleDriver");

		assertEquals("oracle.jdbc.driver.OracleDriver", login.getDriverClassName());
	}

	protected void _testComponentEntryDriverURL() throws Exception
	{
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) getSelection();
		login.setDatabaseDriverAsDriverManager();

		simulateMnemonic("CONNECTION_RDBMS_DRIVER_URL_COMBO_BOX");
		simulateComboBoxTextInput("jdbc:oracle:thin:_tltest-2k:1521:anuj");

		assertEquals("jdbc:oracle:thin:_tltest-2k:1521:anuj", login.getConnectionURL());
	}

	protected void _testFocusTransferDatabaseDriver() throws Exception
	{
		testFocusTransferByMnemonic("CONNECTION_RDBMS_DATABASE_DRIVER_COMBO_BOX", COMPONENT_COMBO_BOX);
	}

	protected void _testFocusTransferDataSourceName() throws Exception
	{
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) getSelection();
		login.setDatabaseDriverAsDataSource();

		testFocusTransferByMnemonic("CONNECTION_RDBMS_DATA_SOURCE_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferDriverClass() throws Exception
	{
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) getSelection();
		login.setDatabaseDriverAsDriverManager();

		testFocusTransferByMnemonic("CONNECTION_RDBMS_DRIVER_CLASS_COMBO_BOX", COMPONENT_COMBO_BOX);
	}

	protected void _testFocusTransferDriverURL() throws Exception
	{
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) getSelection();
		login.setDatabaseDriverAsDriverManager();

		testFocusTransferByMnemonic("CONNECTION_RDBMS_DRIVER_URL_COMBO_BOX", COMPONENT_COMBO_BOX);
	}

	protected JComponent buildPane() throws Exception
	{
		throw new IllegalAccessException();
	}

	protected String windowTitle()
	{
		return "RDBMS Login Pane";
	}
}
