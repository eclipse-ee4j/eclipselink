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

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.DataReadQuery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryExceptionTest {

    private static final String QUERY_SQL = "SELECT query_value FROM query_table";

    // QueryException test. Verify that missing session does not expose query SQL.
    @Test
    public void testMessageWithoutSession() {
        QueryException exception = createExceptionWithoutSession(new DataReadQuery(QUERY_SQL));

        String message = assertDoesNotThrow(exception::getMessage);
        assertMessageDoesNotContainSql(message);
    }

    // QueryException test. Verify that a missing session does not expose query SQL through a wrapper.
    @Test
    public void testWrappedMessageWithoutSession() {
        RuntimeException wrapped = assertDoesNotThrow(
                () -> new RuntimeException(createExceptionWithoutSession(new DataReadQuery(QUERY_SQL))));

        assertMessageDoesNotContainSql(wrapped.getMessage());
    }

    // QueryException test. Verify that SQL logging OFF hides query SQL.
    @Test
    public void testMessageWithSqlLoggingOff() {
        DatabaseSessionImpl session = createSession(SessionLog.OFF);
        QueryException exception = executeFailedQuery(session, new QueryExceptionThrowingQuery(QUERY_SQL));

        assertMessageDoesNotContainSql(exception.getMessage());
    }

    // QueryException test. Verify that a failed query can be prepared again normally.
    @Test
    public void testFormattingAfterRepreparation() {
        DatabaseSessionImpl session = createSession(SessionLog.OFF);
        DataReadQuery query = new QueryExceptionThrowingQuery(QUERY_SQL);

        QueryException exception = executeFailedQuery(session, query);
        assertMessageDoesNotContainSql(exception.getMessage());

        query.setIsPrepared(false);
        assertDoesNotThrow(() -> query.prepareCall(session, null));
        assertTrue(query.isPrepared());
        assertTrue(query.toString().contains(QUERY_SQL));
    }

    // QueryException test. Verify that a failed query retains normal formatting after deserialization.
    @Test
    public void testFormattingAfterDeserialization() throws IOException, ClassNotFoundException {
        DatabaseSessionImpl session = createSession(SessionLog.OFF);
        DataReadQuery query = new QueryExceptionThrowingQuery(QUERY_SQL);

        QueryException exception = executeFailedQuery(session, query);
        assertMessageDoesNotContainSql(exception.getMessage());

        DataReadQuery deserializedQuery = deserialize(serialize(query));
        assertTrue(deserializedQuery.toString().contains(QUERY_SQL));
    }

    // QueryException test. Verify that SQL logging FINE includes query SQL.
    @Test
    public void testMessageWithSqlLoggingFine() {
        String message = createExceptionWithSqlLogging(SessionLog.FINE).getMessage();

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

    private QueryException createExceptionWithoutSession(DataReadQuery query) {
        return QueryException.invalidQuery(query);
    }

    private QueryException createExceptionWithSqlLogging(int sqlLogLevel) {
        QueryException exception = createExceptionWithoutSession(new DataReadQuery(QUERY_SQL));
        exception.setSession(createSession(sqlLogLevel));
        return exception;
    }

    private DatabaseSessionImpl createSession(int sqlLogLevel) {
        DatabaseSessionImpl session = new DatabaseSessionImpl();
        DefaultSessionLog log = new DefaultSessionLog();
        log.setLevel(sqlLogLevel, SessionLog.SQL);
        session.setSessionLog(log);
        return session;
    }

    private QueryException executeFailedQuery(DatabaseSessionImpl session, DataReadQuery query) {
        return assertThrows(QueryException.class, () -> session.executeQuery(query, null, 0));
    }

    private void assertMessageDoesNotContainSql(String message) {
        assertFalse(message.contains(QUERY_SQL));
    }

    private static class QueryExceptionThrowingQuery extends DataReadQuery {
        private QueryExceptionThrowingQuery(String sql) {
            super(sql);
        }

        @Override
        public Object execute(AbstractSession session, AbstractRecord translationRow) {
            throw QueryException.invalidQuery(this);
        }
    }
}