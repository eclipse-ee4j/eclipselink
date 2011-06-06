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

import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.SCAbstractPanelTest;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.EISLoginAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


public class EisLoginPaneTest extends AbstractLoginPaneTest
{
	public EisLoginPaneTest(SCAbstractPanelTest parentTest,
									PropertyValueModel nodeHolder,
									EISLoginAdapter selection)
	{
		super(parentTest, nodeHolder, selection);
	}

	protected void _testComponentEntryConnectionFactoryURL() throws Exception
	{
		simulateMnemonic("CONNECTION_EIS_CONNECTION_FACTORY_URL_FIELD");
		simulateTextInput("MyConnectionFactoryURL");

		EISLoginAdapter login = (EISLoginAdapter) getSelection();
		assertEquals("MyConnectionFactoryURL", login.getConnectionFactoryURL());
	}

	protected void _testComponentEntryConnectionSpecificationClass() throws Exception
	{
		simulateMnemonic("CONNECTION_EIS_CONNECTION_SPEC_CLASS_NAME_FIELD");
		simulateTextInput("MyConnectionSpec");

		EISLoginAdapter login = (EISLoginAdapter) getSelection();
		assertEquals("MyConnectionSpec", login.getConnectionSpecClassName());
	}

	protected void _testFocusTransferConnectionFactoryURL() throws Exception
	{
		testFocusTransferByMnemonic("CONNECTION_EIS_CONNECTION_FACTORY_URL_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected void _testFocusTransferConnectionSpecificationClass() throws Exception
	{
		testFocusTransferByMnemonic("CONNECTION_EIS_CONNECTION_SPEC_CLASS_NAME_FIELD", COMPONENT_TEXT_FIELD);
	}

	protected JComponent buildPane() throws Exception
	{
		throw new IllegalAccessException();
	}

	protected String windowTitle()
	{
		return "EIS Login Pane";
	}
}
