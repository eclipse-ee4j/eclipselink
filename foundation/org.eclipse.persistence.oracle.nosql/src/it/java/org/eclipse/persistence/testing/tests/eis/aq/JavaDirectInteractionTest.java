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

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.LogTestExecution;
import org.eclipse.persistence.testing.tests.nosql.ModelHelper;
import org.eclipse.persistence.testing.tests.nosql.NoSQLProperties;
import org.eclipse.persistence.testing.tests.nosql.SessionHelper;
import org.junit.Rule;
import org.junit.Test;

import oracle.jakarta.AQ.AQDequeueOption;
import oracle.jakarta.AQ.AQDriverManager;
import oracle.jakarta.AQ.AQEnqueueOption;
import oracle.jakarta.AQ.AQMessage;
import oracle.jakarta.AQ.AQQueue;
import oracle.jakarta.AQ.AQRawPayload;
import oracle.jakarta.AQ.AQSession;

/**
 * Test direct interactions through the AQ Java driver. Requires AQ installed on this database.
 */
public class JavaDirectInteractionTest {

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /**
     * Creates an instance of the test class.
     */
    public JavaDirectInteractionTest() {
    }

    /**
     * Attempts to connect to the database using build properties.
     * @return Database connection.
     * @throws SQLException when database connection problem occurs.
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                NoSQLProperties.getDBURL(), NoSQLProperties.getDBUserName(), NoSQLProperties.getDBPassword());
    }

    /**
     * Test direct connection using AQ Java driver.
     */
    @Test
    public void testDirectConnect() throws Exception {
        Connection connection = null;
        AQSession session = null;
        try {
            final Class<?> driver = Class.forName("oracle.jakarta.AQ.AQOracleDriver");
            assertNotNull("Driver class oracle.jakarta.AQ.AQOracleDriver was not found", driver);
            connection = getConnection();
            connection.setAutoCommit(false);
            session = AQDriverManager.createAQSession(connection);
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
     * Test AQ Java driver direct interactions.
     */
    @Test
    public void testDirectInteraction() throws Exception {
        // Only this test uses "raw_order_queue" in this class so relational database session is local.
        final DatabaseSession rDBSession = SessionHelper.createDatabaseSession(
                SessionHelper.createModelProject(SessionHelper.createDatabaseLogin(), JavaDirectInteractionTest.class));
        ModelHelper.setupRawOrderQueue(rDBSession);
        Connection connection = null;
        AQSession session = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            Class.forName("oracle.jakarta.AQ.AQOracleDriver");
            session = AQDriverManager.createAQSession(connection);
            AQQueue queue = session.getQueue(NoSQLProperties.getDBUserName(), "raw_order_queue");
            LOG.log(SessionLog.FINEST, queue.toString());
            AQMessage message = queue.createMessage();
            LOG.log(SessionLog.FINEST, message.toString());
            AQRawPayload payload = message.getRawPayload();
            byte[] bytes = "hello".getBytes();
            payload.setStream(bytes, bytes.length);
            AQEnqueueOption enqueueOption = new AQEnqueueOption();
            queue.enqueue(enqueueOption, message);
            connection.commit();
            AQDequeueOption dequeueOption = new AQDequeueOption();
            message = queue.dequeue(dequeueOption);
            LOG.log(SessionLog.FINEST, message != null ? message.toString() : null);
            LOG.log(SessionLog.FINEST, message != null ? new String(message.getRawPayload().getBytes()) : null);
            connection.commit();
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
            ModelHelper.resetRawOrderQueue(rDBSession);
            rDBSession.logout();
        }
    }

}
