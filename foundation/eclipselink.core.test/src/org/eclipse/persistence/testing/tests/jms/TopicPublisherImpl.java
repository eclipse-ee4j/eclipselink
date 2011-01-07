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
package org.eclipse.persistence.testing.tests.jms;

import javax.jms.*;

/** 
 * Dummy implementation for testing purposes.
 */
public class TopicPublisherImpl implements TopicPublisher {
    public TopicPublisherImpl() {
    }

    public void publish(Message msg) throws JMSException {
        if (((ObjectMessage)msg).getObject() == null) {
            throw new JMSException("");
        }
    }

    // satisfy interface implementation

    public void publish(Topic topic, Message msg) {
    }

    public void publish(Message msg, int i, int j, long l) {
    }

    public void publish(Topic topic, Message msg, int i, int j, long l) {
    }

    public Topic getTopic() {
        return null;
    }

    public void setTimeToLive(long time) {
    }

    public void setPriority(int p) {
    }

    public void setDisableMessageTimestamp(boolean b) {
    }

    public void setDisableMessageID(boolean b) {
    }

    public void setDeliveryMode(int mode) {
    }

    public long getTimeToLive() {
        return 1;
    }

    public int getPriority() {
        return 1;
    }

    public boolean getDisableMessageTimestamp() {
        return false;
    }

    public boolean getDisableMessageID() {
        return false;
    }

    public int getDeliveryMode() {
        return 1;
    }

    public Destination getDestination() {
        return null;
    }

    public void send(javax.jms.Message p1) {
    }

    public void send(javax.jms.Message p1, int p2, int p3, long p4) {
    }

    public void send(javax.jms.Destination p1, javax.jms.Message p2) {
    }

    public void send(javax.jms.Destination p1, javax.jms.Message p2, int p3, int p4, long p5) {
    }

    public void close() {
    }
}
