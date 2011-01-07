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
package org.eclipse.persistence.testing.tests.sessionbroker;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;

public class MultipleClientBrokersTestModel extends org.eclipse.persistence.testing.framework.TestModel {
    /**
     * MultipleClientBrokersTestModel constructor comment.
     */
    public MultipleClientBrokersTestModel() {
        super();
        setDescription("This suite tests the session broker with two or more client session brokers");

    }

    public void addTests() {
        addTest(getClientBrokerTestSuite());
    }

    /**
     * This method was created in VisualAge.
     */
    public void createTables() {
        SessionBroker broker = new SessionBroker();


        DatabaseSession session1 = new DatabaseSessionImpl(Server.getLogin1());
        DatabaseSession session2 = new DatabaseSessionImpl(Server.getLogin2());

        session1.addDescriptors(new EmployeeProject1());
        session2.addDescriptors(new EmployeeProject2());

        broker.registerSession("broker1", session1);
        broker.registerSession("broker2", session2);

        broker.setLogLevel(getSession().getLogLevel());
        broker.setLog(getSession().getLog());

        broker.login();

        // Set session for join table.
        ((ManyToManyMapping)session1.getDescriptor(Employee.class).getObjectBuilder().getMappingForAttributeName("projects")).setSessionName("broker2");
        // Disable delete verify.
        ((OneToOneMapping)session1.getDescriptor(Employee.class).getObjectBuilder().getMappingForAttributeName("address")).setShouldVerifyDelete(false);

        org.eclipse.persistence.testing.models.employee.relational.EmployeeTableCreator tables = 
            new org.eclipse.persistence.testing.models.employee.relational.EmployeeTableCreator();
        tables.replaceTables(session1);
        tables.replaceTables(session2);
        tables.dropConstraints(session1);
        tables.dropConstraints(session2);
        new org.eclipse.persistence.tools.schemaframework.SchemaManager(session1).createSequences();
        new org.eclipse.persistence.tools.schemaframework.SchemaManager(session2).createSequences();
        new EmployeeSystem().populate(broker);
        broker.logout();
    }

    public static TestSuite getBasicReadTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BasicReadTestSuite");
        suite.setDescription("This suite tests the reading of objects remotely.");

        suite.addTest(EmployeeBasicTestModel.getReadObjectTestSuite());
        suite.addTest(EmployeeBasicTestModel.getReadAllTestSuite());

        return suite;
    }

    public static TestSuite getClientBrokerTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName(" MultipleClientBrokerTestSuite");
        suite.addTest(new ClientBrokerTest());
        return suite;
    }

    /**
     * Because this changes the database it must put it back to a valid state.
     */
    public

    void reset() {
        getExecutor().removeConfigureSystem(new EmployeeSystem());
        getExecutor().getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * This method was created in VisualAge.
     */
    public void setup() {
        createTables();
    }
}
