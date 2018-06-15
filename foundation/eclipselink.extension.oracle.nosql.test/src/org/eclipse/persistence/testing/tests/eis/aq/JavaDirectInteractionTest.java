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

import oracle.AQ.AQDequeueOption;
import oracle.AQ.AQDriverManager;
import oracle.AQ.AQEnqueueOption;
import oracle.AQ.AQMessage;
import oracle.AQ.AQQueue;
import oracle.AQ.AQRawPayload;
import oracle.AQ.AQSession;

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
            final Class<?> driver = Class.forName("oracle.AQ.AQOracleDriver");
            assertNotNull("Driver class oracle.AQ.AQOracleDriver was not found", driver);
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
            Class.forName("oracle.AQ.AQOracleDriver");
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
