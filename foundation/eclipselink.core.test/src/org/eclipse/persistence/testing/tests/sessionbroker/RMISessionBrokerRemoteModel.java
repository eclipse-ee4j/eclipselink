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

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.remote.rmi.RMIConnection;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;


public class RMISessionBrokerRemoteModel extends org.eclipse.persistence.testing.tests.remote.RemoteModel {
    public Session oldSession;

    /**
     * RMIRemoteModel constructor comment.
     */
    public RMISessionBrokerRemoteModel() {
        super();
    }

    public void addRequiredSystems() {
        //do nothing	
    }

    public void addTests() {
        addTest(getBasicReadTestSuite());
        addTest(getFeatureTestSuite());
        addTest(getSequencingTestSuite());
    }

    protected SessionBroker buildClientSessionBrokerAndPopulate() {
        SessionBroker sb = Server.buildServerBroker();
        sb.setLogLevel(getSession().getLogLevel());
        sb.setLog(getSession().getLog());
        sb.login();

        SessionBroker cb = sb.acquireClientSessionBroker();
        try {
            new org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem().populate(cb);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cb.getIdentityMapAccessor().initializeAllIdentityMaps();

        return cb;

    }

    public static RMIConnection createConnection() {
        RMISessionBrokerServerManager serverManager = null;

        // Set the client security manager
        try {
            System.setSecurityManager(new RMISecurityManager());
        } catch (Exception exception) {
            System.out.println("Security violation " + exception.toString());
        }

        // Get the remote factory object from the Registry
        try {
            serverManager = (RMISessionBrokerServerManager)Naming.lookup("SERVER-BROKER-MANAGER");
        } catch (Exception exception) {
            throw new TestProblemException(exception.toString());
        }

        RMIConnection rmiConnection = null;
        try {
            rmiConnection = new RMIConnection(serverManager.createRemoteSessionController());
        } catch (RemoteException exception) {
            System.out.println("Error in invocation " + exception.toString());
        }

        return rmiConnection;
    }

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
        new SchemaManager(session1).createSequences();
        new SchemaManager(session2).createSequences();

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

    public static TestSuite getFeatureTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("FeatureTestSuite");
        suite.setDescription("This suite tests the features on the remote model.");

        suite.addTest(new org.eclipse.persistence.testing.tests.queries.CursoredStreamTest(Employee.class, 
                                                                                  new org.eclipse.persistence.expressions.ExpressionBuilder().get("firstName").equal("Bob")));
        suite.addTest(new org.eclipse.persistence.testing.tests.queries.PredefinedQueryReadObjectTest(org.eclipse.persistence.tools.schemaframework.PopulationManager.getDefaultManager().getObject(Employee.class, 
                                                                                                                                                                                  "0001")));
        suite.addTest(new org.eclipse.persistence.testing.tests.queries.PredefinedInQueryReadAllTest(Employee.class, 1));
        return suite;
    }

    public static TestSuite getSequencingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("SequencingTestSuite");
        suite.setDescription("This suite tests the sequencing setting on the remote model.");

        suite.addTest(new SequnceSettingOnRemoteTest());
        return suite;
    }

    public void reset() {
        try {
            if (getExecutor().getSession() instanceof SessionBroker) {
                (((ClientSession)((SessionBroker)getExecutor().getSession()).getSessionForName("broker1")).getParent()).logout();
                (((ClientSession)((SessionBroker)getExecutor().getSession()).getSessionForName("broker2")).getParent()).logout();
            }
        } finally {
            getExecutor().setSession(oldSession);
        }
    }

    public void setup() {

        createTables();
        oldSession = getSession();
        Session broker = buildClientSessionBrokerAndPopulate();
        org.eclipse.persistence.testing.tests.remote.RemoteModel.setServerSession(broker);
        RMIServerManagerController.start(broker);
        RMIConnection connection = createConnection();
        Session remoteSession = connection.createRemoteSession();

        remoteSession.setLogLevel(oldSession.getLogLevel());
        remoteSession.setLog(oldSession.getLog());

        getExecutor().setSession(remoteSession);
    }
}
