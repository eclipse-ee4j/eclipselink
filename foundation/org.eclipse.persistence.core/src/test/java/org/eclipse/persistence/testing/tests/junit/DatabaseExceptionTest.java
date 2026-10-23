/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseExceptionTest {

    private static final String CALL_SQL = "SELECT call_value FROM call_table";
    private static final String QUERY_SQL = "SELECT query_value FROM query_table";
    private static final String DATABASE_ERROR_REASON = "deferred batch preparation failed";
    private static final String SQL_STATE = "42000";
    private static final int VENDOR_CODE = 1234;

    // DatabaseException test. Verify that missing session does not hide the database error.
    @Test
    public void testMessageWithoutSession() {
        DataReadQuery query = new DataReadQuery(QUERY_SQL);
        DatabaseException exception = createExceptionWithoutSession(query);

        String message = assertDoesNotThrow(exception::getMessage);
        assertTrue(message.contains(DATABASE_ERROR_REASON));
        assertTrue(message.contains(Integer.toString(VENDOR_CODE)));
        assertMessageDoesNotContainSql(message);
    }

    // DatabaseException test. Verify that a missing session does not break wrapped exceptions.
    @Test
    public void testWrappedMessageWithoutSession() {
        RuntimeException wrapped = assertDoesNotThrow(
                () -> new RuntimeException(createExceptionWithoutSession(new DataReadQuery(QUERY_SQL))));

        assertTrue(wrapped.getMessage().contains(DATABASE_ERROR_REASON));
    }

    // DatabaseException test. Verify that SQL logging OFF hides both call and query SQL.
    @Test
    public void testMessageWithSqlLoggingOff() {
        DatabaseSessionImpl session = createSession(SessionLog.OFF);
        DatabaseException exception = executeFailedQuery(session, new DatabaseExceptionThrowingQuery(QUERY_SQL));

        assertMessageDoesNotContainSql(exception.getMessage());
    }

    // DatabaseException test. Verify that a failed query can be prepared again normally.
    @Test
    public void testFormattingAfterRepreparation() {
        DatabaseSessionImpl session = createSession(SessionLog.OFF);
        DataReadQuery query = new DatabaseExceptionThrowingQuery(QUERY_SQL);

        DatabaseException exception = executeFailedQuery(session, query);
        assertMessageDoesNotContainSql(exception.getMessage());

        query.setIsPrepared(false);
        assertDoesNotThrow(() -> query.prepareCall(session, null));
        assertTrue(query.isPrepared());
        assertTrue(query.toString().contains(QUERY_SQL));
    }

    // DatabaseException test. Verify that a failed query retains normal formatting after deserialization.
    @Test
    public void testFormattingAfterDeserialization() throws IOException, ClassNotFoundException {
        DatabaseSessionImpl session = createSession(SessionLog.OFF);
        DataReadQuery query = new DatabaseExceptionThrowingQuery(QUERY_SQL);

        DatabaseException exception = executeFailedQuery(session, query);
        assertMessageDoesNotContainSql(exception.getMessage());

        DataReadQuery deserializedQuery = deserialize(serialize(query));
        assertTrue(deserializedQuery.toString().contains(QUERY_SQL));
    }

    // DatabaseException test. Verify that SQL logging FINE includes both call and query SQL.
    @Test
    public void testMessageWithSqlLoggingFine() {
        String message = createExceptionWithSqlLogging(SessionLog.FINE).getMessage();

        assertTrue(message.contains(CALL_SQL));
        assertTrue(message.contains(QUERY_SQL));
    }

    private byte[] serialize(DataReadQuery query) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(query);
        }
        return bytes.toByteArray();
    }

    private DataReadQuery deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (DataReadQuery) input.readObject();
        }
    }

    private DatabaseException createExceptionWithoutSession(DataReadQuery query) {
        DatabaseException exception = DatabaseException.sqlException(
                new SQLException(DATABASE_ERROR_REASON, SQL_STATE, VENDOR_CODE),
                new SQLCall(CALL_SQL), null, null, false);
        exception.setQuery(query);
        return exception;
    }

    private DatabaseException createExceptionWithSqlLogging(int sqlLogLevel) {
        DatabaseSessionImpl session = createSession(sqlLogLevel);

        DatabaseException exception = DatabaseException.sqlException(
            new SQLException(DATABASE_ERROR_REASON, SQL_STATE, VENDOR_CODE),
                new SQLCall(CALL_SQL), null, session, false);
        exception.setQuery(new DataReadQuery(QUERY_SQL));
        return exception;
    }

    private DatabaseSessionImpl createSession(int sqlLogLevel) {
        DatabaseSessionImpl session = new DatabaseSessionImpl();
        DefaultSessionLog log = new DefaultSessionLog();
        log.setLevel(sqlLogLevel, SessionLog.SQL);
        session.setSessionLog(log);
        return session;
    }

    private DatabaseException executeFailedQuery(DatabaseSessionImpl session, DataReadQuery query) {
        return assertThrows(DatabaseException.class, () -> session.executeQuery(query, null, 0));
    }

    private void assertMessageDoesNotContainSql(String message) {
        assertFalse(message.contains(CALL_SQL));
        assertFalse(message.contains(QUERY_SQL));
    }

    private static class DatabaseExceptionThrowingQuery extends DataReadQuery {
        private DatabaseExceptionThrowingQuery(String sql) {
            super(sql);
        }

        @Override
        public Object execute(AbstractSession session, AbstractRecord translationRow) {
            throw DatabaseException.sqlException(
                    new SQLException(DATABASE_ERROR_REASON, SQL_STATE, VENDOR_CODE),
                    new SQLCall(CALL_SQL), null, null, false);
        }
    }
}