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
