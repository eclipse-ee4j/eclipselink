/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.eis.xmlfile;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.eis.adapters.xmlfile.*;
import org.eclipse.persistence.sessions.factories.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test TopLink EIS with the an emulated xml file adapter.
 */
public class XMLFileTestModel extends TestModel {
    protected Session oldSession;

    public XMLFileTestModel() {
        super();
        setDescription("Test TopLink EIS with an emulated xml file adapter.");
    }

    public void addTests() {
        TestSuite directSuite = new TestSuite();
        directSuite.setName("DirectTestSuite");
        directSuite.addTest(new DirectConnectTest());
        directSuite.addTest(new DirectInteractionTest());
        addTest(directSuite);

        TestSuite toplinkSuite = new TestSuite();
        toplinkSuite.setName("TopLinkTestSuite");
        toplinkSuite.addTest(new ConnectTest());
        toplinkSuite.addTest(new ReadWriteTest());
        toplinkSuite.addTest(new UnitOfWorkTest());
        addTest(toplinkSuite);
		addTest(new EmployeeBasicTestModel());
		addTest(new EmployeeNSBasicTestModel());
        
    }

    public void setup() {
        oldSession = getSession();
        DatabaseSession session = XMLProjectReader.read("org/eclipse/persistence/testing/models/order/eis/xmlfile/order-project.xml", getClass().getClassLoader()).createDatabaseSession();
        EISLogin login = new EISLogin(new XMLFilePlatform());
        login.setConnectionSpec(new XMLFileEISConnectionSpec());
        login.setProperty("directory", "./");
        session.setLogin(login);
        session.setSessionLog(getSession().getSessionLog());
        session.login();
        getExecutor().setSession(session);

    }

    public void reset() {
        getDatabaseSession().logout();
        getExecutor().setSession(oldSession);
    }
}
