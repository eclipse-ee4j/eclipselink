/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool.basic;

import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.login.EisLoginPaneTest;
import org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool.AbstractPoolPanelTest;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.EISLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.PoolNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic.EisPoolLoginPropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.SessionNode;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;



/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public class EisPoolLoginPropertiesPageTest extends AbstractPoolPanelTest
{
    public EisPoolLoginPropertiesPageTest(String name)
    {
        super(name);
    }

    public static void main(String[] args) throws Exception
    {
        new EisPoolLoginPropertiesPageTest(null).execute(args);
    }

    protected void _testSubPaneEisLoginPaneTest() throws Throwable
    {
        ConnectionPoolAdapter pool = (ConnectionPoolAdapter) getSelection();
        runTestCase(new EisLoginPaneTest(this, getNodeHolder(), (EISLoginAdapter) pool.getLogin()));
    }

    protected PropertyValueModel buildNodeHolder(ApplicationNode projectNode)
    {
        ServerSessionAdapter session = (ServerSessionAdapter) getTopLinkSessions().sessionNamed("SC-EisServerSessionTest");
        SessionNode sessionNode = (SessionNode) retrieveNode(projectNode, session);
        PoolNode poolNode = (PoolNode) retrieveNode(sessionNode, session.poolNamed("MyConnectionPool"));
        return new SimplePropertyValueModel(poolNode);
    }

    protected JComponent buildPane() throws Exception
    {
        return buildPage(EisPoolLoginPropertiesPage.class, getNodeHolder());
    }

    protected SCAdapter buildSelection()
    {
        ServerSessionAdapter session = (ServerSessionAdapter) getTopLinkSessions().sessionNamed("SC-EisServerSessionTest");
        return session.poolNamed("MyConnectionPool");
    }

    protected String windowTitle()
    {
        return "Eis Connection Pool - Login Tab Test";
    }
}
