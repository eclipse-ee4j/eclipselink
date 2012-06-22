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
package org.eclipse.persistence.testing.tests.eis.aq;

import javax.jms.*;
import oracle.jms.*;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * Test conecting directly through the AQ JMS driver. Requires AQ installed on this database and an
 * aquser defined.
 */
public class JMSDirectConnectTest extends TestCase {
    public JMSDirectConnectTest() {
        this.setDescription("Test conecting directly through the AQ JMS driver");
    }

    public void test() throws Exception {
        TopicConnection topicConnection = connectTopic();
        topicConnection.close();
        QueueConnection queueConnection = connectQueue();
        queueConnection.close();
    }

    public TopicConnection connectTopic() throws Exception {
        String url = (String)getSession().getDatasourceLogin().getProperty("url");
        TopicConnectionFactory connectionFactory = AQjmsFactory.getTopicConnectionFactory(url, null);

        TopicConnection connection = connectionFactory.createTopicConnection("aquser", "aquser");
        TopicSession session = connection.createTopicSession(true, Session.CLIENT_ACKNOWLEDGE);
        connection.start();

        session.close();

        return connection;
    }

    public QueueConnection connectQueue() throws Exception {
        String url = (String)getSession().getDatasourceLogin().getProperty("url");
        QueueConnectionFactory connectionFactory = AQjmsFactory.getQueueConnectionFactory(url, null);

        QueueConnection connection = connectionFactory.createQueueConnection("aquser", "aquser");
        QueueSession session = connection.createQueueSession(true, Session.CLIENT_ACKNOWLEDGE);
        connection.start();

        session.close();

        return connection;
    }
}
