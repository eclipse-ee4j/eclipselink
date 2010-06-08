/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.InsertObjectTest;
import org.eclipse.persistence.testing.framework.OracleDBPlatformHelper;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;
import org.eclipse.persistence.testing.tests.employee.EmployeeDeleteTest;
import org.eclipse.persistence.testing.tests.employee.ProjectDeleteTest;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;


public class BrokerTestModel extends TestModel {
    public Session oldSession;

    public BrokerTestModel() {
        setDescription("This model tests reading/writing/deleting using the session broker with the employee demo.");
    }

    public void addTests() {
        addTest(getInitializationTestSuite());
        addTest(getReadObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getDeleteObjectTestSuite());
    }

    /**
     * Build the session broker, this assume two data-sources Broker1 and Broker2 (JConnect).
     */
    public SessionBroker buildBroker() {
        SessionBroker broker = new SessionBroker();

        DatabaseSession session1 = new DatabaseSessionImpl(getLogin1());
        DatabaseSession session2 = new DatabaseSessionImpl(getLogin2());

        session1.addDescriptors(new EmployeeProject1());

        session2.addDescriptors(new EmployeeProject2());

        broker.registerSession("broker1", session1);
        broker.registerSession("broker2", session2);

        broker.setSessionLog(oldSession.getSessionLog());

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
        new SchemaManager(session1).createSequences();
        new SchemaManager(session2).createSequences();

        new org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem().populate(broker);

        return broker;
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BrokerDeleteObjectTestSuite");
        suite.setDescription("This suite tests delete SQL.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new EmployeeDeleteTest(manager.getObject(Employee.class, "0001")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(Employee.class, "0002")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(LargeProject.class, "0001")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(SmallProject.class, "0002")));

        return suite;
    }

    public static TestSuite getInitializationTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BrokerInitializationTestSuite");
        suite.setDescription("This suite tests Session Broker initialization.");

        suite.addTest(new ExternalTransactionControllerInitializationTest());

        return suite;
    }


    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BrokerInsertObjectTestSuite");
        suite.setDescription("This suite tests insert SQL.");
        org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator system = 
            new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator();

        suite.addTest(new InsertObjectTest(system.basicEmployeeExample4()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample5()));
        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample5()));
        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample5()));

        return suite;
    }

    public static DatabaseLogin getLogin1() {
        //Oracle 11.1
        DatabaseLogin login = new DatabaseLogin();
        try {
            login.usePlatform(OracleDBPlatformHelper.getInstance().getOracle9Platform());
        } catch (Exception e) {
        }
        login.useOracleThinJDBCDriver();
        login.setDatabaseURL("tlsvrdb7.ca.oracle.com:1521:toplink");
        login.setUserName("QA7");
        login.setPassword("password");
        login.useNativeSequencing();
        login.getDefaultSequence().setPreallocationSize(1);

        return login;
    }

    public static DatabaseLogin getLogin2() {
        return ServerBrokerTestModel.getLogin2();
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BrokerReadAllTestSuite");
        suite.setDescription("This suite tests read all sql.");

        suite.addTest(new ReadAllTest(Employee.class, 12));
        suite.addTest(new ReadAllTest(org.eclipse.persistence.testing.models.employee.domain.Project.class, 15));
        suite.addTest(new ReadAllTest(LargeProject.class, 5));
        suite.addTest(new ReadAllTest(SmallProject.class, 10));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BrokerReadObjectTestSuite");
        suite.setDescription("This suite test read sql.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(Employee.class, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(Employee.class, "0004")));
        suite.addTest(new ReadObjectTest(manager.getObject(LargeProject.class, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(SmallProject.class, "0004")));

        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BrokerUpdateObjectTestSuite");
        suite.setDescription("This suite tests update sql.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(Employee.class, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(Employee.class, "0005")));
        suite.addTest(new WriteObjectTest(manager.getObject(SmallProject.class, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(LargeProject.class, "0005")));

        suite.addTest(new BatchWritingWithSessionBrokerTest());

        return suite;
    }

    /**
     * Replace the executor with the old session.
     */
    public void reset() {
        try {
            if (getExecutor().getSession() instanceof SessionBroker) {
                ((DatabaseSession)getExecutor().getSession()).logout();
            }
        } finally {
            getExecutor().setSession(oldSession);
        }
    }

    /**
     * Replace the executor with the broker session.
     */
    public void setup() {
        oldSession = getSession();
        getExecutor().setSession(buildBroker());
    }
}
