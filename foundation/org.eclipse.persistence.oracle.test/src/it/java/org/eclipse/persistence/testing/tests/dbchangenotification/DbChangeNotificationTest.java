/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.dbchangenotification;

import java.util.Vector;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;

public class DbChangeNotificationTest extends TestCase {

    int nMax = 2;
    int n; // n <= nMax

    Session session[] = new Session[nMax];

    Vector employees[] = new Vector[nMax];
    Vector smallProjects[] = new Vector[nMax];
    Vector largeProjects[] = new Vector[nMax];
    Vector phoneNumbers[] = new Vector[nMax];

    String aqUser;
    String aqPassword;
    String queueName;
    boolean useMultipleConsumers;
    boolean useListener;

    long timeToWait = 1000;
    long timeDead = 5000;
    CacheInvalidationHandler handler[] = new CacheInvalidationHandler[nMax];

    public DbChangeNotificationTest(String aqUser, String aqPassword, String queueName, boolean useMultipleConsumers, boolean useListener) {
        super();
        this.aqUser = aqUser;
        this.aqPassword = aqPassword;
        this.queueName = queueName;
        this.useMultipleConsumers = useMultipleConsumers;
        this.useListener = useListener;
        String uses;
        if (useListener) {
            uses = " listener";
        } else {
            uses = " thread";
        }
        String type;
        if (useMultipleConsumers) {
            n = nMax;
            type = " broadcast";
        } else {
            n = 1;
            type = " point to point";
        }
        setName(getName() + uses + type);
    }

    @Override
    protected void setup() throws Exception {
        jakarta.jms.TopicConnectionFactory topicConnectionFactory = null;
        jakarta.jms.QueueConnectionFactory queueConnectionFactory = null;
        if (useMultipleConsumers) {
            topicConnectionFactory = oracle.jakarta.jms.AQjmsFactory.getTopicConnectionFactory(getSession().getLogin().getConnectionString(), null);
        } else {
            queueConnectionFactory = oracle.jakarta.jms.AQjmsFactory.getQueueConnectionFactory(getSession().getLogin().getConnectionString(), null);
        }
        for (int i = 0; i < n; i++) {
            DatabaseLogin login = (DatabaseLogin)getSession().getLogin().clone();
            session[i] = new org.eclipse.persistence.sessions.Project(login).createDatabaseSession();
            ((DatabaseSession)session[i]).addDescriptors(new EmployeeProject());
            session[i].setSessionLog(getSession().getSessionLog());
            session[i].setLogLevel(getSession().getLogLevel());
            session[i].setName("session[" + i + "]");
            ((DatabaseSession)session[i]).login();
            employees[i] = session[i].readAllObjects(Employee.class);
            smallProjects[i] = session[i].readAllObjects(SmallProject.class);
            largeProjects[i] = session[i].readAllObjects(LargeProject.class);
            phoneNumbers[i] = session[i].readAllObjects(PhoneNumber.class);
            session[i].executeNonSelectingCall(new SQLCall("BEGIN NOTIFY_SET_APPID('" + session[i].getName() + "'); END;"));
            //        String selector = "(JMSXUserID IS NULL) OR (JMSXUserID <> " + "'" + session[i].getName() + "')";
            String selector = "(APP IS NULL) OR (APP <> " + "'" + session[i].getName() + "')";

            jakarta.jms.Connection jmsConnection;
            jakarta.jms.MessageConsumer messageConsumer;
            if (useMultipleConsumers) {
                jakarta.jms.TopicConnection topicConnection = topicConnectionFactory.createTopicConnection(aqUser, aqPassword);
                jmsConnection = topicConnection;
                jakarta.jms.TopicSession topicSession = topicConnection.createTopicSession(false, jakarta.jms.Session.AUTO_ACKNOWLEDGE);
                jakarta.jms.Topic topic = ((oracle.jakarta.jms.AQjmsSession)topicSession).getTopic(aqUser, queueName);
                messageConsumer = topicSession.createSubscriber(topic, selector, false);
            } else {
                jakarta.jms.QueueConnection queueConnection = queueConnectionFactory.createQueueConnection(aqUser, aqPassword);
                jmsConnection = queueConnection;
                jakarta.jms.QueueSession queueSession = queueConnection.createQueueSession(false, jakarta.jms.Session.AUTO_ACKNOWLEDGE);
                jakarta.jms.Queue queue = ((oracle.jakarta.jms.AQjmsSession)queueSession).getQueue(aqUser, queueName);
                messageConsumer = queueSession.createReceiver(queue, selector);
            }
            jmsConnection.start();
            if (useListener) {
                // every timeToWait listener.askToStopAfter() verifies whether it should stop
                CacheInvalidationMessageListener listener = new CacheInvalidationMessageListener(session[i], jmsConnection, timeToWait);
                messageConsumer.setMessageListener(listener);
                handler[i] = listener;
            } else {
                // runnable calls messageConsumer.receive(timeToWait) in  a loop, verifying whether it should stop
                CacheInvalidationRunnable runnable = new CacheInvalidationRunnable(session[i], jmsConnection, messageConsumer, timeToWait);
                new Thread(runnable).start();
                handler[i] = runnable;
            }
        }
        // enable sending notification messages - set appId to non-null
        getSession().executeNonSelectingCall(new SQLCall("BEGIN NOTIFY_SET_APPID('original session'); END;"));
        //    enableTriggers(true);
        Thread.sleep(1000);
    }

    @Override
    protected void test() throws Exception {
        int[] numMessagesExpected = new int[n];
        int numUpdated;

        // 1 ********************************************************
        // Original session: Begin transaction, update SmallProjects, rollback transaction
        // No change notification should be sent
        getAbstractSession().beginTransaction();
        SQLCall updateSmallProjectsCall = new SQLCall("UPDATE PROJECT SET DESCRIP = CONCAT('BLA ', DESCRIP) WHERE PROJ_TYPE = 'S'");
        getSession().executeNonSelectingCall(updateSmallProjectsCall);
        getAbstractSession().rollbackTransaction();

        // 2 ********************************************************
        // Original session updates LargeProjects
        // Change notification should be sent to both session[0] and session[1]
        SQLCall updateLargeProjectsCall = new SQLCall("UPDATE LPROJECT SET BUDGET = BUDGET + 10000");
        numUpdated = getSession().executeNonSelectingCall(updateLargeProjectsCall);
        for (int i = 0; i < n; i++) {
            numMessagesExpected[i] += numUpdated;
        }

        // 3 ********************************************************
        // session[0] updates Salaries
        // Change notification should be sent only to session[1]
        SQLCall updateSalariesCall = new SQLCall("UPDATE SALARY SET SALARY = SALARY + 10000");
        numUpdated = session[0].executeNonSelectingCall(updateSalariesCall);
        for (int i = 0; i < n; i++) {
            if (i != 0) {
                numMessagesExpected[i] += numUpdated;
            }
        }

        // 4 ********************************************************
        // session[1] updates PhoneNumbers
        // Change notification should be sent only to session[0]
        if (n > 1) {
            // add 1 to area code: 110 -> 111
            // Added 1000 to properly handled codes starting with 0: 095 -> 096 (not to 96)
            SQLCall updatePhoneNumbersCall = new SQLCall("UPDATE PHONE SET AREA_CODE = SUBSTR(TO_CHAR(1001+TO_NUMBER(AREA_CODE)),2)");
            numUpdated = session[1].executeNonSelectingCall(updatePhoneNumbersCall);
            for (int i = 0; i < n; i++) {
                if (i != 1) {
                    numMessagesExpected[i] += numUpdated;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            // stop either after expected number of messages has been received or
            // after timeDead has past since receiving the last message
            handler[i].askToStopAfter(numMessagesExpected[i], timeDead);
        }
    }

    @Override
    protected void verify() throws Exception {
        Thread.sleep(1000);
        // 1 ********************************************************
        // Original session: Begin transaction, update SmallProjects, rollback transaction
        // No change notification should be sent
        for (int i = 0; i < n; i++) {
            boolean shouldInvalidate = false;
            int numInvalidated = 0;
            for (int j = 0; j < smallProjects[i].size(); j++) {
                if (!session[i].getIdentityMapAccessor().isValid(smallProjects[i].elementAt(j))) {
                    numInvalidated++;
                }
            }
            verifyInvalidated(session[i].getName(), shouldInvalidate, "SmallProject", numInvalidated, smallProjects[i].size());
        }

        // 2 ********************************************************
        // Original session updates LargeProjects
        // Change notification should be sent to both session[0] and session[1]
        for (int i = 0; i < n; i++) {
            boolean shouldInvalidate = true;
            int numInvalidated = 0;
            for (int j = 0; j < largeProjects[i].size(); j++) {
                if (!session[i].getIdentityMapAccessor().isValid(largeProjects[i].elementAt(j))) {
                    numInvalidated++;
                }
            }
            verifyInvalidated(session[i].getName(), shouldInvalidate, "LargeProject", numInvalidated, largeProjects[i].size());
        }

        // 3 ********************************************************
        // session[0] updates Salaries
        // Change notification should be sent only to session[1]
        for (int i = 0; i < n; i++) {
            boolean shouldInvalidate = i == 1;
            int numInvalidated = 0;
            for (int j = 0; j < employees[i].size(); j++) {
                if (!session[i].getIdentityMapAccessor().isValid(employees[i].elementAt(j))) {
                    numInvalidated++;
                }
            }
            verifyInvalidated(session[i].getName(), shouldInvalidate, "Employee", numInvalidated, employees[i].size());
        }

        // 4 ********************************************************
        // session[1] updates PhoneNumbers
        // Change notification should be sent only to session[0]
        if (n > 1) {
            for (int i = 0; i < n; i++) {
                boolean shouldInvalidate = i == 0;
                int numInvalidated = 0;
                for (int j = 0; j < phoneNumbers[i].size(); j++) {
                    if (!session[i].getIdentityMapAccessor().isValid(phoneNumbers[i].elementAt(j))) {
                        numInvalidated++;
                    }
                }
                verifyInvalidated(session[i].getName(), shouldInvalidate, "PhoneNumber", numInvalidated, phoneNumbers[i].size());
            }
        }
    }

    protected void verifyInvalidated(String sessionName, boolean shouldInvalidate, String className, int numInvalidated, int numTotal) {
        if (shouldInvalidate) {
            if (numInvalidated == 0) {
                throw new TestErrorException(sessionName + " has not invalidated " + className);
            } else if (numInvalidated < numTotal) {
                throw new TestErrorException(sessionName + " has invalidated only " + numInvalidated + ' ' + className + ", should've invalidated " + numTotal);
            }
        } else {
            if (numInvalidated > 0) {
                throw new TestErrorException(sessionName + " has invalidated " + numInvalidated + ' ' + className + ", was not supposed to invalidate any");
            }
        }
    }

    @Override
    public void reset() throws Exception {
        // disable sending notification messages - set appId to null
        getSession().executeNonSelectingCall(new SQLCall("BEGIN NOTIFY_SET_APPID; END;"));
        //    enableTriggers(false);
        Thread.sleep(1000);

        for (int i = 0; i < n; i++) {
            ((DatabaseSession)session[i]).logout();
        }

        // reset original values into the db
        // 2 ********************************************************
        SQLCall updateLargeProjectsCall = new SQLCall("UPDATE LPROJECT SET BUDGET = BUDGET - 10000");
        getSession().executeNonSelectingCall(updateLargeProjectsCall);

        // 3 ********************************************************
        SQLCall updateSalariesCall = new SQLCall("UPDATE SALARY SET SALARY = SALARY - 10000");
        getSession().executeNonSelectingCall(updateSalariesCall);

        // 4 ********************************************************
        if (n > 1) {
            // subtract 1 from area code: 111 -> 110
            // Added 1000 to properly handled codes starting with 0: 096 -> 095 (not to 95)
            SQLCall updatePhoneNumbersCall = new SQLCall("UPDATE PHONE SET AREA_CODE = SUBSTR(TO_CHAR(999+TO_NUMBER(AREA_CODE)),2)");
            getSession().executeNonSelectingCall(updatePhoneNumbersCall);
        }
    }

    /*protected void enableTriggers(boolean enable) {
    String[] tableNames = {"PROJECT", "LPROJECT", "SALARY", "PHONE"};
    String action;
    if(enable) {
        action = "ENABLE";
    } else {
        action = "DISABLE";
    }
    getAbstractSession().beginTransaction();
    for(int i=0; i<tableNames.length; i++) {
        String triggerName = "NOTIFY_" + tableNames[i];
        getSession().executeNonSelectingCall(new SQLCall("ALTER TRIGGER "+triggerName+" "+action));
    }
    getAbstractSession().commitTransaction();
}*/
}
