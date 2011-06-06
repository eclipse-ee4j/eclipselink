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

import javax.jms.*;
import oracle.jms.*;

/**
 * Test direct interactions through the AQ JMS driver. Requires AQ installed on this database and
 * an aquser defined.
 */
public class JMSDirectInteractionTest extends JMSDirectConnectTest {
    public JMSDirectInteractionTest() {
        this.setDescription("Test direct interactions through the AQ JMS driver.");
    }

    public void test() throws Exception {
        testQueue();
        testTopic();
    }

    public void testQueue() throws Exception {
        QueueConnection connection = connectQueue();
        QueueSession session = connection.createQueueSession(true, Session.CLIENT_ACKNOWLEDGE);

        Queue queue = ((AQjmsSession)session).getQueue("aquser", "order_queue");
        getSession().logMessage(queue.toString());
        QueueReceiver receiver = session.createReceiver(queue);
        getSession().logMessage(receiver.toString());
        QueueSender sender = session.createSender(queue);
        getSession().logMessage(sender.toString());

        TextMessage message = session.createTextMessage();
        getSession().logMessage(message.toString());
        message.setText("the message");
        message.setJMSCorrelationID("1234");
        sender.send(queue, message);
        session.commit();

        message = (TextMessage)receiver.receiveNoWait();
        getSession().logMessage(message.toString());
        getSession().logMessage(message.getText());
        session.commit();

        session.close();
        connection.close();
    }

    public void testTopic() throws Exception {
        TopicConnection connection = connectTopic();
        TopicSession session = connection.createTopicSession(true, Session.CLIENT_ACKNOWLEDGE);

        Topic topic = ((AQjmsSession)session).getTopic("aquser", "order_topic");
        getSession().logMessage(topic.toString());
        TopicSubscriber subscriber = session.createSubscriber(topic);
        getSession().logMessage(subscriber.toString());
        TopicPublisher publisher = session.createPublisher(topic);
        getSession().logMessage(publisher.toString());

        TextMessage message = session.createTextMessage();
        getSession().logMessage(message.toString());
        message.setText("the message");
        message.setJMSCorrelationID("1234");
        publisher.publish(topic, message);
        session.commit();

        message = (TextMessage)subscriber.receiveNoWait();
        getSession().logMessage(message.toString());
        getSession().logMessage(message.getText());
        session.commit();

        session.close();
        connection.close();
    }
}
