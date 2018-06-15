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
//     04/01/2016-2.7 Tomas Kraus
//       - 490677: Database connection properties made configurable in test.properties
package org.eclipse.persistence.testing.tests.eis.aq;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;

import org.eclipse.persistence.testing.framework.junit.LogTestExecution;
import org.eclipse.persistence.testing.tests.nosql.NoSQLProperties;
import org.junit.Rule;
import org.junit.Test;

import oracle.jms.AQjmsFactory;

/**
 * Test connecting directly through the AQ JMS driver. Requires AQ installed on this database.
 */
public class JMSDirectConnectTest {

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /**
     * Creates an instance of direct connection tests.
     */
    public JMSDirectConnectTest() {
    }

    /**
     * Create database connection using {@link TopicConnection}.
     * @return {@link TopicConnection} instance.
     */
    static TopicConnection connectTopic() throws JMSException {
        final TopicConnectionFactory connectionFactory
                = AQjmsFactory.getTopicConnectionFactory(NoSQLProperties.getDBURL(), null);
        return connectionFactory.createTopicConnection(
                NoSQLProperties.getDBUserName(), NoSQLProperties.getDBPassword());
    }

    /**
     * Create database connection using {@link QueueConnection}.
     * @return {@link TopicConnection} instance.
     */
    static QueueConnection connectQueue() throws JMSException {
        final QueueConnectionFactory connectionFactory
                = AQjmsFactory.getQueueConnectionFactory(NoSQLProperties.getDBURL(), null);
        return connectionFactory.createQueueConnection(
                NoSQLProperties.getDBUserName(), NoSQLProperties.getDBPassword());
    }

    /**
     * Test direct connection using {@link TopicConnection}.
     */
    @Test
    public void testConnectTopic() throws Exception {
        TopicSession session = null;
        TopicConnection connection = null;
        try {
            connection = connectTopic();
            session = connection.createTopicSession(true, Session.CLIENT_ACKNOWLEDGE);
            connection.start();
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Test direct connection using {@link QueueConnection}.
     */
    @Test
    public void testConnectQueue() throws Exception {
        QueueConnection connection = null;
        QueueSession session = null;
        try {
            connection = connectQueue();
            session = connection.createQueueSession(true, Session.CLIENT_ACKNOWLEDGE);
            connection.start();
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

}
