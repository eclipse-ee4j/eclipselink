/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.login.EisLoginPaneTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool.*;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.EISLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ReadConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.PoolNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic.EisReadPoolLoginPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.SessionNode;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;


public class EisReadPoolLoginPropertiesPageTest extends AbstractReadPoolPanelTest
{
	public EisReadPoolLoginPropertiesPageTest(String name)
	{
		super(name);
	}

	public static void main(String[] args) throws Exception
	{
		new EisReadPoolLoginPropertiesPageTest(null).execute(args);
	}

	public void _testComponentEnablerUseNonTransactionalReadLogin() throws Exception
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

	protected void _testSubPaneEisLoginPane() throws Throwable
	{
		ReadConnectionPoolAdapter pool = (ReadConnectionPoolAdapter) getSelection();
		pool.setUseNonTransactionalReadLogin(true);
		runTestCase(new EisLoginPaneTest(this, getNodeHolder(), (EISLoginAdapter) pool.getLogin()));
	}

	protected PropertyValueModel buildNodeHolder(ApplicationNode projectNode)
	{
		ServerSessionAdapter session = (ServerSessionAdapter) getTopLinkSessions().sessionNamed("SC-EisServerSessionTest");
		SessionNode sessionNode = (SessionNode) retrieveNode(projectNode, session);
		PoolNode poolNode = (PoolNode) retrieveNode(sessionNode, session.getReadConnectionPool());
		return new SimplePropertyValueModel(poolNode);
	}

	protected JComponent buildPane() throws Exception
	{
		return buildPage(EisReadPoolLoginPropertiesPage.class, getNodeHolder());
	}

	protected SCAdapter buildSelection()
	{
		ServerSessionAdapter session = (ServerSessionAdapter) getTopLinkSessions().sessionNamed("SC-EisServerSessionTest");
		return session.getReadConnectionPool();
	}

	protected String windowTitle()
	{
		return "Eis Read Connection Pool - Login Tab Test";
	}
}
