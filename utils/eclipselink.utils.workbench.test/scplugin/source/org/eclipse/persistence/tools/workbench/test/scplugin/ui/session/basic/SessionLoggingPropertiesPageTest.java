/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.basic;

import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DefaultSessionLogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LogAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic.SessionLoggingPropertiesPage;

/**
 * @author Tran Le
 * @version 1.0a
 */
public class SessionLoggingPropertiesPageTest extends AbstractSessionPanelTest
{
	public SessionLoggingPropertiesPageTest(String name)
	{
		super(name);
	}

	protected void _testComponentEntry()
	{
		
	}

	protected JComponent buildPane() throws Exception
	{
		return buildPage(SessionLoggingPropertiesPage.class, getNodeHolder());
	}

	public static void main(String[] args) throws Exception
	{
		new SessionLoggingPropertiesPageTest("SessionLoggingPropertiesPageTest")
					.execute(args);
	}

	protected String windowTitle()
	{
		return "Testing Session Logging Page";
	}

	protected void printModel()
	{
		SessionAdapter session = (SessionAdapter) this.getSelectionHolder().getValue();
		LogAdapter log = session.getLog();
		String displayString = log.displayString();
		if (log instanceof DefaultSessionLogAdapter)
		{
			displayString += " | " + ((DefaultSessionLogAdapter) log).getLogLevel()
						+ " | " + ((DefaultSessionLogAdapter) log).getFileName();
		}
		System.out.println("subject.log( " + displayString + " )");
	}

	protected void resetProperty()
	{
		SessionAdapter session = ((SessionAdapter) this.getSelectionHolder().getValue());
		session.setDefaultLogging();
	}
}