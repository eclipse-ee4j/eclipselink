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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.session.basic;

import org.eclipse.persistence.tools.workbench.test.scplugin.ui.SCAbstractPanelTest;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;


/**
 * @author Tran Le
 * @version 1.0a
 */
public abstract class AbstractSessionPanelTest extends SCAbstractPanelTest {

    private SessionAdapter selection;

    public AbstractSessionPanelTest(String name) {

        super(name);
    }

    protected PropertyValueModel buildNodeHolder( ApplicationNode projectNode) {

        SCAdapter session = getTopLinkSessions().sessionNamed("SC-EmployeeTest");
        return new SimplePropertyValueModel(retrieveNode(projectNode, session));
    }

    protected SCAdapter buildSelection() {

        this.selection = getTopLinkSessions().sessionNamed( "SC-EmployeeTest");
        return this.selection;
    }

    protected void clearModel() {

        getSelectionHolder().setValue( null);
    }

    protected SessionAdapter getSession() {

        return (SessionAdapter) getSelection();
    }

    protected void printModel() {

        System.out.println( this.selection);
    }

    protected void resetProperty() {

        this.selection = getTopLinkSessions().sessionNamed( "SC-EmployeeTest");
        this.restoreModel();
    }

    protected void restoreModel() {

        getSelectionHolder().setValue( this.selection);
    }

    protected void tearDown() throws Exception {

        super.tearDown();
        this.selection = null;
    }
}
