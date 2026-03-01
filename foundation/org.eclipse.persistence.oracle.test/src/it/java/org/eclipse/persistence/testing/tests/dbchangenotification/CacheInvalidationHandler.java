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

import org.eclipse.persistence.sessions.Session;

public class CacheInvalidationHandler {
    Session session;
    jakarta.jms.Connection connection;
    long timeToWait;
    CacheInvalidator invalidator;

    Vector exceptions;
    int messageCount;
    boolean shouldStop;
    int numMessagesExpected = -1;
    long timeDead = -1;
    long timeLastMessageWasReceived;

    public CacheInvalidationHandler(Session session, jakarta.jms.Connection connection) {
        this(session, connection, 1000);
    }

    // each timeToWait the handler checks whether it shouldStop (should be positive)

    public CacheInvalidationHandler(Session session, jakarta.jms.Connection connection, long timeToWait) {
        this.session = session;
        this.connection = connection;
        this.timeToWait = timeToWait;
        invalidator = new CacheInvalidator(session);
    }

    protected void addException(Exception ex) {
        if (exceptions == null) {
            exceptions = new Vector();
        }
        exceptions.addElement(ex);
    }

    public Vector getExceptions() {
        return exceptions;
    }

    public int getMessageCount() {
        return messageCount;
    }

    String getName() {
        return session.getName();
    }

    // stops the handler as soon as possible

    public void askToStop() throws InterruptedException {
        askToStopAfter(0, 0);
    }

    // stops the handler when the expected number of messages has been received:
    //   messageCount >= numMessagesExpected
    // or if the last message was received a long time ago:
    //   currentTime - timeLastMessageWasReceived >= timeDead
    // - in this case something wrong probably has happened

    public void askToStopAfter(int numMessagesExpected, long timeDead) throws InterruptedException {
        this.numMessagesExpected = numMessagesExpected;
        this.timeDead = timeDead;
    }

    // this method is called at least every timeToWait:
    // if the message was received - with true;
    // otherwise - with false

    protected synchronized void checkToStop(boolean messageWasReceived) {
        if (numMessagesExpected < 0 && timeDead < 0) {
            return;
        }

        if (this.messageCount >= numMessagesExpected) {
            shouldStop = true;
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (timeLastMessageWasReceived == 0) {
            timeLastMessageWasReceived = currentTime;
            return;
        }
        if (currentTime - timeLastMessageWasReceived >= timeDead) {
            shouldStop = true;
            return;
        }
        if (messageWasReceived) {
            timeLastMessageWasReceived = currentTime;
        }
    }
}
