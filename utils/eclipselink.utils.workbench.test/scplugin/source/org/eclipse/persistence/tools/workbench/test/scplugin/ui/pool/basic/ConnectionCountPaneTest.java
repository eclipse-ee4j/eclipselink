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

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.SCAbstractPanelTest;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.PoolNode;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;

public class ConnectionCountPaneTest extends SCAbstractPanelTest
{
	private PoolNode poolNode;

	public ConnectionCountPaneTest(SCAbstractPanelTest parentTest, PoolNode poolNode)
	{
		super(parentTest);
		this.poolNode = poolNode;
	}

	protected void _testComponentEntryMaxConnections() throws Exception
	{
		makeSureExternalConnectionPoolingIsFalse();

		simulateMnemonic("CONNECTION_POOL_MAXIMUM_CONNECTIONS_SPINNER");
		simulateSpinnerInput(120);

		assertEquals(120, getConnectionPool().getMaxConnections());
	}

	protected void _testComponentEntryMinConnections() throws Exception
	{
		makeSureExternalConnectionPoolingIsFalse();

		simulateMnemonic("CONNECTION_POOL_MINIMUM_CONNECTIONS_SPINNER");
		simulateSpinnerInput(45);

		assertEquals(45, getConnectionPool().getMinConnections());
	}

	protected void _testFocusTransferMaxConnections() throws Exception
	{
		makeSureExternalConnectionPoolingIsFalse();

		testFocusTransferByMnemonic("CONNECTION_POOL_MINIMUM_CONNECTIONS_SPINNER", COMPONENT_SPINNER);
	}

	protected void _testFocusTransferMinConnections() throws Exception
	{
		makeSureExternalConnectionPoolingIsFalse();

		testFocusTransferByMnemonic("CONNECTION_POOL_MAXIMUM_CONNECTIONS_SPINNER", COMPONENT_SPINNER);
	}

	protected PropertyValueModel buildNodeHolder(ApplicationNode projectNode)
	{
		return new SimplePropertyValueModel(poolNode);
	}

	protected JComponent buildPane() throws Exception
	{
		throw new UnsupportedOperationException();
	}

	protected SCAdapter buildSelection()
	{
		return getConnectionPool();
	}

	protected void clearModel()
	{
	}

	private ConnectionPoolAdapter getConnectionPool()
	{
		return (ConnectionPoolAdapter) poolNode.getValue();
	}

	private void makeSureExternalConnectionPoolingIsFalse()
	{
		ConnectionPoolAdapter pool = getConnectionPool();
		DatabaseSessionAdapter session = (DatabaseSessionAdapter) pool.getParent().getParent();
		session.setExternalConnectionPooling(false);
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
		return "ConnectionCountPane Test";
	}
}
