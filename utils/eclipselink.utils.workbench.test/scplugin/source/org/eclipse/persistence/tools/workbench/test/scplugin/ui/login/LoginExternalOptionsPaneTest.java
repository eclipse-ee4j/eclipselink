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

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public class LoginExternalOptionsPaneTest extends AbstractLoginPaneTest
{
	public LoginExternalOptionsPaneTest(SCAbstractPanelTest parentTest,
													PropertyValueModel nodeHolder,
													DatabaseLoginAdapter selection)
	{
		super(parentTest, nodeHolder, selection);
	}

	protected void _testComponentEntryExternalConnectionPooling() throws Exception
	{
		LoginAdapter login = (LoginAdapter) getSelection();
		DatabaseSessionAdapter session = (DatabaseSessionAdapter) login.getParent();
				boolean value = session.usesExternalConnectionPooling();

		simulateMnemonic("CONNECTION_EXTERNAL_CONNECTION_POOLING_CHECK_BOX");

		assertTrue(value != session.usesExternalConnectionPooling());
	}

	protected void _testFocusTransferExternalConnectionPooling() throws Exception
	{
		testFocusTransferByMnemonic("CONNECTION_EXTERNAL_CONNECTION_POOLING_CHECK_BOX", COMPONENT_RADIO_BUTTON);
	}

	protected JComponent buildPane() throws Exception
	{
		return null;
	}

	protected String windowTitle()
	{
		return "Login External Options Test";
	}
}
