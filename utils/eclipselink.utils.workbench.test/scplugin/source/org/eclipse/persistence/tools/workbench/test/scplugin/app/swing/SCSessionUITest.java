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
package org.eclipse.persistence.tools.workbench.test.scplugin.app.swing;

import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;

/**
 * Based class for testing a Session property.
 * Sets the subject to the first session in the config file.
 */
public abstract class SCSessionUITest extends SCAbstractUITest {

    private SessionAdapter subject;
    private PropertyValueModel subjectHolder;

    protected SCSessionUITest() {
        super();
    }

    protected void setUp() {

        super.setUp();

        this.subject = getTopLinkSessions().sessionNamed( "SC-EmployeeTest");

        this.subjectHolder = new SimplePropertyValueModel( this.subject);
    }

    protected SessionAdapter subject() {
        return this.subject;
    }

    protected PropertyValueModel subjectHolder() {
        return this.subjectHolder;
    }

    protected void clearModel() {
        this.subjectHolder.setValue( null);
    }

    protected void restoreModel() {
        this.subjectHolder.setValue( subject());
    }

    protected void printModel() {
        System.out.println( subject());
    }

}
