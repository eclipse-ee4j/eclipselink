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

import jakarta.jms.*;

import org.eclipse.persistence.sessions.Session;

public class CacheInvalidationRunnable extends CacheInvalidationHandler implements Runnable {
    MessageConsumer messageConsumer;

    class Lock {
        boolean locked;

        synchronized void acquire() throws InterruptedException {
            while (locked) {
                wait();
            }
            locked = true;
        }

        synchronized void release() {
            locked = false;
            notifyAll();
        }
    }
    Lock lock;

    public CacheInvalidationRunnable(Session session, Connection connection, MessageConsumer messageConsumer) {
        super(session, connection);
        this.messageConsumer = messageConsumer;
    }

    public CacheInvalidationRunnable(Session session, Connection connection, MessageConsumer messageConsumer, long timeToWait) {
        super(session, connection, timeToWait);
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void run() {
        lock = new Lock();
        try {
            lock.acquire();
        } catch (InterruptedException ex) {
            addException(ex);
            shouldStop = true;
        }
        Message msg = null;
        do {
            try {
                msg = messageConsumer.receive(timeToWait);
                if (msg != null) {
                    invalidator.invalidateObject(session, msg);
                    messageCount++;
                }
                checkToStop(msg != null);
            } catch (Exception ex) {
                checkToStop(false);
                addException(ex);
            }
        } while (!shouldStop);
        lock.release();
        try {
            connection.close();
        } catch (JMSException jmsException) {
        }
    }

    @Override
    public void askToStopAfter(int numMessagesExpected, long timeDead) throws InterruptedException {
        super.askToStopAfter(numMessagesExpected, timeDead);
        try {
            lock.acquire();
        } finally {
            lock.release();
        }
    }
}
