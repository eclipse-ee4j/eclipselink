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

import java.math.BigDecimal;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.OracleDBPlatformHelper;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;


public class ServerBrokerTestModel extends TestModel {
    public Session oldSession;

    public static final String QUERY_NAME = "localNumbers";

    public ServerBrokerTestModel() {
        setDescription("This model tests reading/writing/deleting using the session broker with the employee demo.");
    }

    /**
     * Add a named query to a session Broker
     */
    public void addQuery1(SessionBroker broker) {
        // Add a predefined query with argument for employee with first name Bob.
        ExpressionBuilder builder = new ExpressionBuilder();
        ReadAllQuery query = new ReadAllQuery(Employee.class, builder);

        Expression exp = builder.get("id").equal(builder.getParameter("ID"));
        query.setSelectionCriteria(exp.and(builder.get("firstName").equal("Bob")));

        query.addArgument("ID", BigDecimal.class);
        broker.addQuery(QUERY_NAME, query);
    }

    /**
     * Add a named query to a session Broker
     */
    public void addQuery2(SessionBroker broker) {
        // Add a predefined query without argument for retrieving numbers with 613 area code.
        ExpressionBuilder builder = new ExpressionBuilder();
        ReadAllQuery query = new ReadAllQuery(PhoneNumber.class, builder);
        query.setSelectionCriteria(builder.get("type").equal("work"));

        broker.addQuery(QUERY_NAME, query);
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getSessionBrokerClientQueryTestSuite());
        //cr 2923
        addTest(new SessionBrokerPlatformTest());
        addTest(new ReadOnlyClassesInSessionBrokerTest());       
        addTest(new VerifyClientBrokerCreationTest());
        
        addTest(new SessionBrokerCustomFunctionTest(SessionBrokerCustomFunctionTest.READALLQUERY_TEST));
        addTest(new SessionBrokerCustomFunctionTest(SessionBrokerCustomFunctionTest.UPDATEALLQUERY_TEST));
        addTest(new SessionBrokerCustomFunctionTest(SessionBrokerCustomFunctionTest.DELETEALLQUERY_TEST));
    }

    /**
     * Build the session broker, this assume two data-sources Broker1 and Broker2 (JConnect).
     */
    public SessionBroker buildBroker() {
        createTables();

        SessionBroker broker = new SessionBroker();

        addQuery1(broker);
        addQuery2(broker);

        ServerSession ssession1 = new ServerSession(getLogin1());
        ServerSession ssession2 = new ServerSession(getLogin2());

        ssession1.addDescriptors(new EmployeeProject1());
        ssession2.addDescriptors(new EmployeeProject2());

        broker.registerSession("broker1", ssession1);
        broker.registerSession("broker2", ssession2);

        broker.setLog(oldSession.getLog());
        broker.setLogLevel(oldSession.getLogLevel());

        broker.login();

        // Set session for join table.
        ((ManyToManyMapping)ssession1.getDescriptor(Employee.class).getObjectBuilder().getMappingForAttributeName("projects")).setSessionName("broker2");
        // Disable delete verify.
        ((OneToOneMapping)ssession1.getDescriptor(Employee.class).getObjectBuilder().getMappingForAttributeName("address")).setShouldVerifyDelete(false);

        SessionBroker clientBroker = broker.acquireClientSessionBroker();
        new org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem().populate(clientBroker);

        return clientBroker;
    }

    /**
     * Create the tables on both databases.
     */
    public void createTables() {
        SessionBroker broker = new SessionBroker();

        DatabaseSession session1 = new DatabaseSessionImpl(getLogin1());
        DatabaseSession session2 = new DatabaseSessionImpl(getLogin2());

        session1.addDescriptors(new EmployeeProject1());
        session2.addDescriptors(new EmployeeProject2());

        broker.registerSession("broker1", session1);
        broker.registerSession("broker2", session2);

        broker.setLog(oldSession.getLog());
        broker.setLogLevel(oldSession.getLogLevel());

        broker.login();

        org.eclipse.persistence.testing.models.employee.relational.EmployeeTableCreator tables = 
            new org.eclipse.persistence.testing.models.employee.relational.EmployeeTableCreator();
        tables.replaceTables(session1);
        tables.replaceTables(session2);
        tables.dropConstraints(session1);
        tables.dropConstraints(session2);
        new SchemaManager(session1).createSequences();
        new SchemaManager(session2).createSequences();

        broker.logout();
    }

    public static DatabaseLogin getLogin1() {
        //Oracle 11.1
        DatabaseLogin login = new DatabaseLogin();
        try {
            login.usePlatform(OracleDBPlatformHelper.getInstance().getOracle9Platform());
        } catch (Exception e) {
        }
        login.useOracleThinJDBCDriver();
        login.setDatabaseURL("ottvm028.ca.oracle.com:1521:toplink");
        login.setUserName("QA7");
        login.setPassword("password");
        login.useNativeSequencing();
        login.getDefaultSequence().setPreallocationSize(1);

        return login;
    }

    public static DatabaseLogin getLogin2() {
        //Oracle 11.1
        DatabaseLogin login = new DatabaseLogin();
        try {
            login.usePlatform(OracleDBPlatformHelper.getInstance().getOracle9Platform());
        } catch (Exception e) {
        }
        login.useOracleThinJDBCDriver();
        login.setDatabaseURL("ottvm028.ca.oracle.com:1521:toplink");
        login.setUserName("QA8");
        login.setPassword("password");
        login.useNativeSequencing();
        login.getDefaultSequence().setPreallocationSize(1);

        return login;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("BrokerReadAllTestSuite");
        suite.setDescription("This suite tests read all sql.");

        PopulationManager manager = PopulationManager.getDefaultManager();

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

    public static TestSuite getSessionBrokerClientQueryTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("SessionBrokerClientQueryTestSuite");
        suite.setDescription("This suite tests queries.");

        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new SessionBrokerClientQueryTestCase());

        return suite;
    }

    /**
     * Replace the executor with the old session.
     */
    public void reset() {
        try {
            if (getExecutor().getSession() instanceof SessionBroker) {
                ((DatabaseSession)((ClientSession)((SessionBroker)getExecutor().getSession()).getSessionForName("broker1")).getParent()).logout();
                ((DatabaseSession)((ClientSession)((SessionBroker)getExecutor().getSession()).getSessionForName("broker2")).getParent()).logout();
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
