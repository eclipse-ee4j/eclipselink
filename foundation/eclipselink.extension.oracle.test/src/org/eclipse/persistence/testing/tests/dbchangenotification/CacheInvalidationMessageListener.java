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
package org.eclipse.persistence.testing.tests.dbchangenotification;

import org.eclipse.persistence.sessions.Session;

public class CacheInvalidationMessageListener extends CacheInvalidationHandler implements javax.jms.MessageListener {

    public CacheInvalidationMessageListener(Session session, javax.jms.Connection connection) {
        super(session, connection);
    }

    public CacheInvalidationMessageListener(Session session, javax.jms.Connection connection, long timeToWait) {
        super(session, connection, timeToWait);
    }

    public void onMessage(javax.jms.Message message) {
        try {
            invalidator.invalidateObject(session, message);
            messageCount++;
            checkToStop(true);
        } catch (Exception ex) {
            addException(ex);
        }
    }

    public void askToStopAfter(int numMessagesExpected, long timeDead) throws InterruptedException {
        try {
            super.askToStopAfter(numMessagesExpected, timeDead);
            while (!shouldStop) {
                Thread.sleep(timeToWait);
                checkToStop(false);
            }
        } finally {
            try {
                connection.close();
            } catch (javax.jms.JMSException jmsException) {
            }
        }
    }
}
