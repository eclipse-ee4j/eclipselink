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
package org.eclipse.persistence.testing.tests.eis.aq;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.eis.adapters.aq.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.factories.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test TopLink EIS with the Oracle IP JCA drivers.
 */
public class AQTestModel extends TestModel {
    protected Session oldSession;

    public AQTestModel() {
        super();
        setDescription("Test TopLink EIS with the Oracle AQ drivers.");
    }

    public void addTests() {
        TestSuite jmsSuite = new TestSuite();
        jmsSuite.setName("JMSDirectTestSuite");
        jmsSuite.addTest(new JMSDirectConnectTest());
        jmsSuite.addTest(new JMSDirectInteractionTest());
        addTest(jmsSuite);

        TestSuite javaSuite = new TestSuite();
        javaSuite.setName("JavaDirectTestSuite");
        javaSuite.addTest(new JavaDirectConnectTest());
        javaSuite.addTest(new JavaDirectInteractionTest());
        addTest(javaSuite);

        TestSuite toplinkSuite = new TestSuite();
        toplinkSuite.setName("TopLinkTestSuite");
        toplinkSuite.addTest(new ConnectTest());
        toplinkSuite.addTest(new AuthenticationTest());
        toplinkSuite.addTest(new ReadWriteTest());
        toplinkSuite.addTest(new ReadTimeoutTest());
        toplinkSuite.addTest(new ReadUOWTest());
        addTest(toplinkSuite);
    }

    public void setup() {
        oldSession = getSession();
        DatabaseLogin login = (DatabaseLogin)oldSession.getLogin().clone();
        if (login.getConnector() instanceof JNDIConnector) {
            login.setConnector(new JNDIConnector("jdbc/OracleCoreAQ"));
        } else {
            login.setUserName("aquser");
            login.setPassword("aquser");
        }
        DatabaseSession session = new Project(login).createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());
        try {
            session.login();
        } catch (Exception exception) {
            throw new TestProblemException("Database needs to be setup for AQ, with the aquser", exception);
        }

        /** to setup aquser, need to execute,
            1 - login as sys (default password is password)
            - login as scott tiger
            connect sys/password@james as sysdba

            2 - might need to install aq procesures?
            - in sqlplus - @@<orahome>\ora92\rdbms\admin\catproc.sql

            3 - create a aq user (i.e aquser)
            grant connect, resource , aq_administrator_role to aquser identified by aquser
            grant execute on dbms_aq to aquser
            grant execute on dbms_aqadm to aquser
            grant execute on dbms_lock to aquser
            connect aquser/aquser
        */
        try {
            session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.STOP_QUEUE (queue_name => 'order_queue'); end;"));
        } catch (Exception notThere) {
        }
        try {
            session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.DROP_QUEUE (queue_name => 'order_queue'); end;"));
        } catch (Exception notThere) {
        }
        try {
            session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.DROP_QUEUE_TABLE (queue_table => 'order_queue_table'); end;"));
        } catch (Exception notThere) {
        }
        try {
            session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.STOP_QUEUE (queue_name => 'order_topic'); end;"));
        } catch (Exception notThere) {
        }
        try {
            session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.DROP_QUEUE (queue_name => 'order_topic'); end;"));
        } catch (Exception notThere) {
        }
        try {
            session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.DROP_QUEUE_TABLE (queue_table => 'order_topic_table'); end;"));
        } catch (Exception notThere) {
        }
        try {
            session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.STOP_QUEUE (queue_name => 'raw_order_queue'); end;"));
        } catch (Exception notThere) {
        }
        try {
            session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.DROP_QUEUE (queue_name => 'raw_order_queue'); end;"));
        } catch (Exception notThere) {
        }
        try {
            session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.DROP_QUEUE_TABLE (queue_table => 'raw_order_queue_table'); end;"));
        } catch (Exception notThere) {
        }

        session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'order_queue_table', multiple_consumers => FALSE, queue_payload_type => 'SYS.AQ$_JMS_TEXT_MESSAGE'); end;"));
        session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.CREATE_QUEUE (queue_name => 'order_queue', queue_table => 'order_queue_table'); end;"));
        session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.START_QUEUE (queue_name => 'order_queue'); end;"));

        session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'order_topic_table', multiple_consumers => TRUE, queue_payload_type => 'SYS.AQ$_JMS_TEXT_MESSAGE'); end;"));
        session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.CREATE_QUEUE (queue_name => 'order_topic', queue_table => 'order_topic_table'); end;"));
        session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.START_QUEUE (queue_name => 'order_topic'); end;"));

        session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'raw_order_queue_table', multiple_consumers => FALSE, queue_payload_type => 'RAW'); end;"));
        session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.CREATE_QUEUE (queue_name => 'raw_order_queue', queue_table => 'raw_order_queue_table'); end;"));
        session.executeNonSelectingCall(new SQLCall("begin DBMS_AQADM.START_QUEUE (queue_name => 'raw_order_queue'); end;"));

        session = XMLProjectReader.read("org/eclipse/persistence/testing/models/order/eis/aq/order-project.xml", getClass().getClassLoader()).createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());

        //String url = oldSession.getLogin().getConnectionString();
		String url;
		try{
			url = ((AbstractSession)getSession()).getAccessor().getConnection().getMetaData().getURL();
		}catch(java.sql.SQLException se){
			se.printStackTrace();
			throw new TestErrorException("There is SQLException");
		}
        EISLogin eisLogin = (EISLogin)session.getDatasourceLogin();
        eisLogin.setConnectionSpec(new AQEISConnectionSpec());
        eisLogin.setProperty(AQEISConnectionSpec.URL, url);
        eisLogin.setUserName("aquser");
        eisLogin.setPassword("aquser");
        session.login();

        getExecutor().setSession(session);
    }

    public void reset() {
        getDatabaseSession().logout();
        getExecutor().setSession(oldSession);
    }
}
