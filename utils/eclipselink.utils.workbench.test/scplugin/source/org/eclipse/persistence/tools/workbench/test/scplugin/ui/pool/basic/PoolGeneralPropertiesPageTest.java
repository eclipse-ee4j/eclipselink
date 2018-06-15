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
