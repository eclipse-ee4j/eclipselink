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
package org.eclipse.persistence.testing.tests.distributedservers.rcm.jms;

import java.util.Properties;

import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import javax.naming.Context;
import javax.naming.InitialContext;

import oracle.jms.AQjmsFactory;
import oracle.jms.AQjmsSession;

import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.jms.JMSTopicTransportManager;
import org.eclipse.persistence.testing.tests.distributedservers.rcm.broadcast.BroadcastSetupHelper;

public class JMSSetupHelper extends BroadcastSetupHelper {
    protected static JMSSetupHelper helper;

    public static JMSSetupHelper getHelper() {
        if (helper == null) {
            helper = new JMSSetupHelper();
        }
        return helper;
    }

    protected JMSSetupHelper() {
        super();
        factoryJndiName = JMSTopicTransportManager.DEFAULT_CONNECTION_FACTORY;
        topicJndiName = JMSTopicTransportManager.DEFAULT_TOPIC;
    }

    // If removeConnectionOnError is set,
    // local (listening) connection is removed in JMS case if subscriber.receive() throws exception;
    // however in Oc4jJGroups case the only (local and external) connection is not removed
    // unless message sending fails.

    public boolean isLocalConnectionRemovedOnListeningError() {
        return true;
    }

    // Returns errorCode of RemoteCommandManagerException thrown in case
    // creation of localConnection has failed.

    public int getRcmExceptionErrorCodeOnFailureToCreateLocalConnection() {
        return RemoteCommandManagerException.ERROR_CREATING_LOCAL_JMS_CONNECTION;
    }

    // JMSTopicTransportManager has separate connection for sending (external) and receiving (local) messages.
    // Oc4jJGroups uses a single connection for both sending and receiving messages.

    public boolean isLocalConnectionAlsoExternalConnection() {
        return false;
    }

    protected String user = "aquser";
    protected String password = "aquser";
    protected String connectionString;
    protected String queueName = "jms_test";
    protected String queueTableName = "jms_test_table";

    protected String newConnectionString;

    protected oracle.jdbc.pool.OracleDataSource oracleDataSource;

    public void setConnectionString(String newConnectionString) {
        this.newConnectionString = newConnectionString;
    }

    public void setConnectionStringFromSession(AbstractSession session) {
        this.newConnectionString = session.getLogin().getConnectionString();
    }

    protected void updateDbSettings() {
        connectionString = newConnectionString;
    }

    // returns array of two objects: the first is factory, the second is topic

    protected Object[] internalCreateFactory() throws Exception {
        updateDbSettings();
        if (oracleDataSource != null) {
            try {
                oracleDataSource.close();
            } catch (java.sql.SQLException ex) {
                // ignore
            }
        }
        createInDb();

        oracleDataSource = new oracle.jdbc.pool.OracleDataSource();
        oracleDataSource.setURL(connectionString);
        oracleDataSource.setUser(user);
        oracleDataSource.setPassword(password);

        TopicConnectionFactory topicConnectionFactory = AQjmsFactory.getTopicConnectionFactory(oracleDataSource);
        TopicConnection topicConnection = topicConnectionFactory.createTopicConnection();
        TopicSession topicSession = topicConnection.createTopicSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
        Topic topic = ((AQjmsSession)topicSession).getTopic(user, queueName);
        topicSession.close();
        topicConnection.close();

        return new Object[] { topicConnectionFactory, topic };
    }

    protected void internalStartFactory() throws Exception {
        startInDb();
    }

    protected void internalStopFactory() throws Exception {
        stopInDb();
    }

    protected void internalDestroyFactory() throws Exception {
        try {
            stopInDb();
            destroyInDb();
        } catch (java.sql.SQLException ex) {
        } finally {
            try {
                oracleDataSource.close();
            } catch (java.sql.SQLException ex) {
            } finally {
                oracleDataSource = null;
            }
        }
    }

    protected void createTransportManager(RemoteCommandManager rcm) {
        JMSTopicTransportManager tm = new JMSTopicTransportManager(rcm);
        Properties props = new Properties();
        // should use the testing JNDI factory.
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, TEST_CONTEXT_FACTORY);
        // the testing JNDI doesn't require PROVIDER_URL property, but without it NPE is thrown
        props.setProperty(Context.PROVIDER_URL, "");
        tm.setRemoteContextProperties(props);
        // the testing JNDI doesn't require password, but without it NPE is thrown
        tm.setPassword("");
    }

    protected void createInDb() throws java.sql.SQLException {
        // just in case the table hasn't been already dropped
        try {
            destroyInDb();
        } catch (java.sql.SQLException ex) {
            // ignore
        }
        java.sql.Connection conn = java.sql.DriverManager.getConnection(connectionString, user, password);
        try {
            java.sql.Statement stmt = conn.createStatement();
            String createTable = "BEGIN DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => '" + queueTableName + "', multiple_consumers => true, queue_payload_type => 'SYS.AQ$_JMS_OBJECT_MESSAGE'); END;";
            stmt.executeUpdate(createTable);
            String createQueue = "BEGIN DBMS_AQADM.CREATE_QUEUE (queue_name => '" + queueName + "', queue_table => '" + queueTableName + "'); END;";
            stmt.executeUpdate(createQueue);
        } finally {
            conn.close();
        }
    }

    protected void startInDb() throws java.sql.SQLException {
        java.sql.Connection conn = java.sql.DriverManager.getConnection(connectionString, user, password);
        try {
            java.sql.Statement stmt = conn.createStatement();
            String startQueue = "BEGIN DBMS_AQADM.START_QUEUE (queue_name => '" + queueName + "'); END;";
            stmt.executeUpdate(startQueue);
        } finally {
            conn.close();
        }
    }

    protected void stopInDb() throws java.sql.SQLException {
        java.sql.Connection conn = java.sql.DriverManager.getConnection(connectionString, user, password);
        try {
            java.sql.Statement stmt = conn.createStatement();
            String stopQueue = "BEGIN DBMS_AQADM.STOP_QUEUE (queue_name => '" + queueName + "'); END;";
            stmt.executeUpdate(stopQueue);
        } finally {
            conn.close();
        }
    }

    protected void destroyInDb() throws java.sql.SQLException {
        java.sql.Connection conn = java.sql.DriverManager.getConnection(connectionString, user, password);
        try {
            java.sql.Statement stmt1 = conn.createStatement();
            String dropQueue = "BEGIN DBMS_AQADM.DROP_QUEUE (queue_name => '" + queueName + "'); END;";
            try {
                stmt1.executeUpdate(dropQueue);
            } catch (java.sql.SQLException ex) {
            }
            java.sql.Statement stmt2 = conn.createStatement();
            String dropTable = "BEGIN DBMS_AQADM.DROP_QUEUE_TABLE (queue_table => '" + queueTableName + "', force => TRUE); END;";
            stmt2.executeUpdate(dropTable);
        } finally {
            conn.close();
        }
    }

    // Sends an arbitrary message to speed up shut down of listening threads.

    protected void sendMessageToStopListenerThreads() throws Exception {
        Context context = new InitialContext(CONTEXT_PROPERTIES);
        TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)context.lookup(this.factoryJndiName);
        TopicConnection topicConnection = topicConnectionFactory.createTopicConnection();
        try {
            Topic topic = (Topic)context.lookup(this.topicJndiName);
            TopicSession session = topicConnection.createTopicSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            TopicPublisher publisher = session.createPublisher(topic);
            ObjectMessage objectMessage = session.createObjectMessage();
            publisher.publish(objectMessage);
        } finally {
            topicConnection.close();
        }
    }

    protected void createExternalConnection(AbstractSession session) {
        ((JMSTopicTransportManager)session.getCommandManager().getTransportManager()).createExternalConnection();
    }
}
