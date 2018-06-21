/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.pool;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.SCAbstractPanelTest;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ReadConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ServerSessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.PoolNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.SessionNode;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;


/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public abstract class AbstractReadPoolPanelTest extends SCAbstractPanelTest
{
    public AbstractReadPoolPanelTest(String name)
    {
        super(name);
    }

    protected PropertyValueModel buildNodeHolder(ApplicationNode projectNode)
    {
        ServerSessionAdapter session = (ServerSessionAdapter) getTopLinkSessions().sessionNamed("SC-ServerSessionTest");
        SessionNode sessionNode = (SessionNode) retrieveNode(projectNode, session);
        PoolNode poolNode = (PoolNode) retrieveNode(sessionNode, session.getReadConnectionPool());
        return new SimplePropertyValueModel(poolNode);
    }

    protected SCAdapter buildSelection()
    {
        ServerSessionAdapter session = (ServerSessionAdapter) getTopLinkSessions().sessionNamed("SC-ServerSessionTest");
        return session.getReadConnectionPool();
    }

    protected void clearModel()
    {
    }

    protected final ReadConnectionPoolAdapter getConnectionPool()
    {
        return (ReadConnectionPoolAdapter) getSelection();
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
