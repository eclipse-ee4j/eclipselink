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
package org.eclipse.persistence.testing.tests.eis.xmlfile;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.eis.adapters.xmlfile.*;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.*;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;

/**
 * Simple connect test. This tests connecting TopLink EIS to the XML file connector.
 */
public class ConnectTest extends AutoVerifyTestCase {
    public ConnectTest() {
        setName("ConnectTest");
        setDescription("This tests connecting TopLink EIS to the XML file connector.");
    }

    public void test() throws Exception {
        testDatabaseSession();
        testServerSession();
    }

    public void testDatabaseSession() throws Exception {
        EISLogin login = new EISLogin(new XMLFilePlatform());

        XMLFileConnectionFactory connectionFactory = new XMLFileConnectionFactory();

        EISConnectionSpec eisSpec = new EISConnectionSpec();
        eisSpec.setConnectionFactory(connectionFactory);
        eisSpec.setConnectionSpec(new XMLFileConnectionSpec());
        login.setConnectionSpec(eisSpec);
        DatabaseSession session = new Project(login).createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());
        session.login();
        session.logout();
    }

    public void testServerSession() throws Exception {
        EISLogin login = new EISLogin(new XMLFilePlatform());
        login.setConnectionSpec(new XMLFileEISConnectionSpec());
        login.setProperty("directory", "./");
        Server session = new Project(login).createServerSession();
        session.setSessionLog(getSession().getSessionLog());
        session.login();
        session.logout();
    }
}
