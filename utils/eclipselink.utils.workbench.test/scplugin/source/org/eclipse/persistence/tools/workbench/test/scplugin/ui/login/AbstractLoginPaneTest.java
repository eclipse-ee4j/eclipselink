/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.login;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.SCAbstractPanelTest;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


abstract class AbstractLoginPaneTest extends SCAbstractPanelTest
{
	private PropertyValueModel nodeHolder;
	private LoginAdapter selection;

	public AbstractLoginPaneTest(SCAbstractPanelTest parentTest,
										  PropertyValueModel nodeHolder,
										  LoginAdapter selection)
	{
		super(parentTest);

		this.nodeHolder = nodeHolder;
		this.selection  = selection;
	}

	protected void _testComponentEntryPassword() throws Exception
	{
		simulateMnemonic("CONNECTION_PASSWORD_FIELD");
		simulateTextInput("MyPassword");

		LoginAdapter login = (LoginAdapter) getSelection();
		assertEquals("MyPassword", login.getPassword());
	}

	protected void _testComponentEntryUserName() throws Exception
	{
		simulateMnemonic("CONNECTION_USER_NAME_FIELD");
		simulateTextInput("MyUsername");

		LoginAdapter login = (LoginAdapter) getSelection();
		assertEquals("MyUsername", login.getUserName());
	}

	protected void _testFocusTransferPassword() throws Exception
	{
		testFocusTransferByMnemonic("CONNECTION_PASSWORD_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferUserName() throws Exception
	{
		testFocusTransferByMnemonic("CONNECTION_USER_NAME_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected PropertyValueModel buildNodeHolder(ApplicationNode projectNode)
	{
		return this.nodeHolder;
	}

	protected SCAdapter buildSelection()
	{
		return this.selection;
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

	protected void tearDown() throws Exception
	{
		super.tearDown();

		this.nodeHolder = null;
		this.selection = null;
	}
}