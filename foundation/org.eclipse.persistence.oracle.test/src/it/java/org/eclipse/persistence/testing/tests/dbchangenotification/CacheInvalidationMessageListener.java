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

import org.eclipse.persistence.sessions.Session;

public class CacheInvalidationMessageListener extends CacheInvalidationHandler implements jakarta.jms.MessageListener {

    public CacheInvalidationMessageListener(Session session, jakarta.jms.Connection connection) {
        super(session, connection);
    }

    public CacheInvalidationMessageListener(Session session, jakarta.jms.Connection connection, long timeToWait) {
        super(session, connection, timeToWait);
    }

    @Override
    public void onMessage(jakarta.jms.Message message) {
        try {
            invalidator.invalidateObject(session, message);
            messageCount++;
            checkToStop(true);
        } catch (Exception ex) {
            addException(ex);
        }
    }

    @Override
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
            } catch (jakarta.jms.JMSException jmsException) {
            }
        }
    }
}
