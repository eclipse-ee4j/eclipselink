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
//     04/01/2016-2.7 Tomas Kraus
//       - 490677: Database connection properties made configurable in test.properties
package org.eclipse.persistence.testing.tests.eis.aq;

import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueReceiver;
import jakarta.jms.QueueSender;
import jakarta.jms.QueueSession;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import jakarta.jms.TopicConnection;
import jakarta.jms.TopicPublisher;
import jakarta.jms.TopicSession;
import jakarta.jms.TopicSubscriber;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.junit.LogTestExecution;
import org.eclipse.persistence.testing.tests.nosql.ModelHelper;
import org.eclipse.persistence.testing.tests.nosql.NoSQLProperties;
import org.eclipse.persistence.testing.tests.nosql.SessionHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import oracle.jakarta.jms.AQjmsSession;

/**
 * Test direct interactions through the AQ JMS driver. Requires AQ installed on this database.
 */
public class JMSDirectInteractionTest extends JMSDirectConnectTest {

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /** Relational database session. Required for model initialization. */
    DatabaseSession rDBSession;

    /**
     * Creates an instance of the test class.
     */
    public JMSDirectInteractionTest() {
    }

    /**
     * Set up this test suite.
     */
    @Before
    public void setUp() {
        // Use temporary relational database session to initialize the model.
        final Project project = SessionHelper.createModelProject(SessionHelper.createDatabaseLogin(), AQTestSuite.class);
        rDBSession = SessionHelper.createDatabaseSession(project);
    }

    /**
     * Clean up this test suite.
     */
    @After
    public void tearDown() {
        rDBSession.logout();
    }

    /**
     * Test AQ JMS driver direct interactions using {@link QueueConnection}.
     */
    @Test
    public void testQueue() throws Exception {
        ModelHelper.setupOrderQueue(rDBSession);
        QueueConnection connection = null;
        QueueSession session = null;
        try {
            connection = connectQueue();
            session = connection.createQueueSession(true, Session.CLIENT_ACKNOWLEDGE);
            Queue queue = ((AQjmsSession)session).getQueue(NoSQLProperties.getDBUserName(), "order_queue");
            LOG.log(SessionLog.FINEST, queue.toString());
            QueueReceiver receiver = session.createReceiver(queue);
            LOG.log(SessionLog.FINEST, receiver.toString());
            QueueSender sender = session.createSender(queue);
            LOG.log(SessionLog.FINEST, sender.toString());
            TextMessage message = session.createTextMessage();
            LOG.log(SessionLog.FINEST, message.toString());
            message.setText("the message");
            message.setJMSCorrelationID("1234");
            sender.send(queue, message);
            session.commit();
            message = (TextMessage)receiver.receiveNoWait();
            LOG.log(SessionLog.FINEST, message != null ? message.toString() : null);
            LOG.log(SessionLog.FINEST, message != null ? message.getText() : null);
            session.commit();
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
            ModelHelper.resetOrderQueue(rDBSession);
        }
   }

    /**
     * Test AQ JMS driver direct interactions using {@link TopicConnection}.
     */
    @Test
    public void testTopic() throws Exception {
        ModelHelper.setupOrderTopic(rDBSession);
        TopicSession session = null;
        TopicConnection connection = null;
        try {
            connection = connectTopic();
            session = connection.createTopicSession(true, Session.CLIENT_ACKNOWLEDGE);
            Topic topic = ((AQjmsSession)session).getTopic(NoSQLProperties.getDBUserName(), "order_topic");
            LOG.log(SessionLog.FINEST, topic.toString());
            TopicSubscriber subscriber = session.createSubscriber(topic);
            LOG.log(SessionLog.FINEST, subscriber.toString());
            TopicPublisher publisher = session.createPublisher(topic);
            LOG.log(SessionLog.FINEST, publisher.toString());
            TextMessage message = session.createTextMessage();
            LOG.log(SessionLog.FINEST, message.toString());
            message.setText("the message");
            message.setJMSCorrelationID("1234");
            publisher.publish(topic, message);
            session.commit();
            message = (TextMessage)subscriber.receiveNoWait();
            LOG.log(SessionLog.FINEST, message != null ? message.toString() : null);
            LOG.log(SessionLog.FINEST, message != null ? message.getText() : null);
            session.commit();
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
            ModelHelper.resetOrderTopic(rDBSession);
        }
    }

}
