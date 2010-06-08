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

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool.AbstractPoolPanelTest;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.PoolNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic.PoolGeneralPropertiesPage;

/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public class PoolGeneralPropertiesPageTest extends AbstractPoolPanelTest
{
	public PoolGeneralPropertiesPageTest(String name)
	{
		super(name);
	}

	public static void main(String[] args) throws Exception
	{
		new PoolGeneralPropertiesPageTest(null).execute(args);
	}

	protected void _testComponentEnablerConnectionCountPane() throws Exception
	{
		ConnectionPoolAdapter pool = getConnectionPool();
		ServerSessionAdapter session = (ServerSessionAdapter) pool.getParent().getParent();
		boolean enabled = session.usesExternalConnectionPooling();

		JComponent pane = retrieveChildComponent("CONNECTION_POOL_CONNECTION_COUNT_PANE", getPane());
		assertNotNull(pane);
		assertTrue(checkChildrenEnableState(pane, !enabled));

		session.setExternalConnectionPooling(!enabled);
		assertTrue(checkChildrenEnableState(pane, enabled));
	}

	protected void _testSubPaneConnnectionCountPaneTest() throws Throwable
	{
		runTestCase(new ConnectionCountPaneTest(this, (PoolNode) getNodeHolder().getValue()));
	}

	protected JComponent buildPane() throws Exception
	{
		return buildPage(PoolGeneralPropertiesPage.class, getNodeHolder());
	}

	protected String windowTitle()
	{
		return "Connection Pool - General Tab Test";
	}
}
