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
package org.eclipse.persistence.testing.tests.jms;

import javax.jms.*;

/** 
 * Dummy implementation for testing purposes.
 */
public class TopicSessionImpl implements TopicSession {
    public TopicSessionImpl() {
    }

    public ObjectMessage createObjectMessage() {
        return new ObjectMessageImpl();
    }

    // satisfy interface implementation

    public void unsubscribe(String str) {
    }

    public Topic createTopic(String topic) {
        return null;
    }

    public TemporaryTopic createTemporaryTopic() {
        return null;
    }

    public TopicSubscriber createSubscriber(Topic topic, String str, boolean bool) {
        return null;
    }

    public TopicSubscriber createSubscriber(Topic topic) {
        return null;
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String str, String str1, boolean bool) {
        return null;
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String str) {
        return null;
    }

    public TopicPublisher createPublisher(Topic topic) {
        return null;
    }

    public void setMessageListener(MessageListener msgListener) {
    }

    public void run() {
    }

    public void rollback() {
    }

    public void recover() {
    }

    public boolean getTransacted() {
        return false;
    }

    public MessageListener getMessageListener() {
        return null;
    }

    public TextMessage createTextMessage(String msg) {
        return null;
    }

    public TextMessage createTextMessage() {
        return null;
    }

    public StreamMessage createStreamMessage() {
        return null;
    }

    public ObjectMessage createObjectMessage(java.io.Serializable sio) {
        return null;
    }

    public Message createMessage() {
        return null;
    }

    public MapMessage createMapMessage() {
        return null;
    }

    public BytesMessage createBytesMessage() {
        return null;
    }

    public void commit() {
    }

    public int getAcknowledgeMode() {
        return 0;
    }

    public javax.jms.MessageProducer createProducer(javax.jms.Destination p1) {
        return null;
    }

    public javax.jms.MessageConsumer createConsumer(javax.jms.Destination p1) {
        return null;
    }

    public javax.jms.MessageConsumer createConsumer(javax.jms.Destination p1, java.lang.String p2) {
        return null;
    }

    public javax.jms.MessageConsumer createConsumer(javax.jms.Destination p1, java.lang.String p2, boolean p3) {
        return null;
    }

    public javax.jms.Queue createQueue(java.lang.String p1) {
        return null;
    }

    public javax.jms.QueueBrowser createBrowser(javax.jms.Queue p1) {
        return null;
    }

    public javax.jms.QueueBrowser createBrowser(javax.jms.Queue p1, java.lang.String p2) {
        return null;
    }

    public javax.jms.TemporaryQueue createTemporaryQueue() {
        return null;
    }

    public void close() {
    }
}
