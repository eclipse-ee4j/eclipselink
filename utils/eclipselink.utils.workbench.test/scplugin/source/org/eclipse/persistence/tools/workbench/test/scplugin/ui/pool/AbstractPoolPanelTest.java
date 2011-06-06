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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.SCAbstractPanelTest;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;


/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public abstract class AbstractPoolPanelTest extends SCAbstractPanelTest
{
	public AbstractPoolPanelTest(SCAbstractPanelTest parentTest)
	{
		super(parentTest);
	}

	public AbstractPoolPanelTest(String name)
	{
		super(name);
	}

	protected PropertyValueModel buildNodeHolder(ApplicationNode projectNode)
	{
		ServerSessionAdapter session = (ServerSessionAdapter) getTopLinkSessions().sessionNamed("SC-ServerSessionTest");
		ApplicationNode serverSessionNode = retrieveNode(projectNode, session);
		ApplicationNode poolNode = retrieveNode(serverSessionNode, session.poolNamed("MyConnectionPool"));
		return new SimplePropertyValueModel(poolNode);
	}

	protected SCAdapter buildSelection()
	{
		ServerSessionAdapter session = (ServerSessionAdapter) getTopLinkSessions().sessionNamed("SC-ServerSessionTest");
		return session.poolNamed("MyConnectionPool");
	}

	protected void clearModel()
	{
	}

	protected final ConnectionPoolAdapter getConnectionPool()
	{
		return (ConnectionPoolAdapter) getSelection();
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
}
