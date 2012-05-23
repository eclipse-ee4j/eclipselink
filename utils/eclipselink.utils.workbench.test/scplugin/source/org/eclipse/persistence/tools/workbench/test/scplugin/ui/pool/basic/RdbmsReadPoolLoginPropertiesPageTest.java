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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool.basic;

import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool.AbstractReadPoolPanelTest;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ReadConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic.RdbmsReadPoolLoginPropertiesPage;


/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public class RdbmsReadPoolLoginPropertiesPageTest extends AbstractReadPoolPanelTest
{
	private boolean canContinue = true;

	public RdbmsReadPoolLoginPropertiesPageTest(String name)
	{
		super(name);
	}

	public static void main(String[] args) throws Exception
	{
		new RdbmsReadPoolLoginPropertiesPageTest(null).execute(args);
	}

	protected void _testComponentEnablerLoginPane() throws Exception
	{
		ReadConnectionPoolAdapter pool = getConnectionPool();
		boolean enabled = pool.usesNonTransactionalReadLogin();

		JComponent pane = retrieveChildComponent("CONNECTION_READ_LOGIN_PANE", getPane());
		assertNotNull(pane);
		assertTrue(checkChildrenEnableState(pane, enabled));

		simulateMnemonic("CONNECTION_READ_USE_NON_TRANSACTIONAL_READ_LOGIN_CHECK_BOX");
		boolean newEnabled = pool.usesNonTransactionalReadLogin();
		assertTrue(newEnabled != enabled);
		assertTrue(checkChildrenEnableState(pane, newEnabled));
	}

	protected void _testComponentEntryUseNonTransactionalReadLogin() throws Exception
	{
		ReadConnectionPoolAdapter pool = getConnectionPool();
		boolean enabled = pool.usesNonTransactionalReadLogin();

		simulateMnemonic("CONNECTION_READ_USE_NON_TRANSACTIONAL_READ_LOGIN_CHECK_BOX");

		assertTrue(enabled != pool.usesNonTransactionalReadLogin());
	}

	protected void _testFocusTransferUseNonTransactionalReadLogin() throws Exception
	{
		testFocusTransferByMnemonic("CONNECTION_READ_USE_NON_TRANSACTIONAL_READ_LOGIN_CHECK_BOX",
											 COMPONENT_CHECK_BOX);
	}

	protected JComponent buildPane() throws Exception
	{
		return buildPage(RdbmsReadPoolLoginPropertiesPage.class, getNodeHolder());
	}

	protected boolean canContinueTestingDuplicateMnemonic()
	{
		ReadConnectionPoolAdapter pool = getConnectionPool();
		DatabaseLoginAdapter login = (DatabaseLoginAdapter) pool.getLogin();

		if (login.databaseDriverIsDriverManager())
			login.setDatabaseDriverAsDataSource();
		else
			login.setDatabaseDriverAsDriverManager();

		boolean oldCanContinue = canContinue;
		canContinue = false;
		return oldCanContinue;
	}

	protected String windowTitle()
	{
		return "Read Connection Pool - General Tab Test";
	}
}
