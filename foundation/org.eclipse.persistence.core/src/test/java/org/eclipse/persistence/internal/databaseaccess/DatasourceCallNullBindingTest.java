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

package org.eclipse.persistence.internal.databaseaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DatasourceCallNullBindingTest {

    private final DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(new PostgreSQLPlatform()));

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void preservesJavaTypeOfScalarNullBesideCollection(boolean nullFirst) {
        DatabaseField id = new DatabaseField("id");
        id.setType(Long.class);

        assertScalarNullPreserved(id, nullFirst);

        assertEquals(Long.class, id.getType());
        assertEquals(DatabaseField.NULL_SQL_TYPE, id.getSqlType());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void preservesExplicitJdbcTypeOfScalarNullBesideCollection(boolean nullFirst) {
        DatabaseField id = new DatabaseField("id");
        id.setType(Number.class);
        id.setSqlType(Types.BIGINT);

        assertScalarNullPreserved(id, nullFirst);

        assertEquals(Number.class, id.getType());
        assertEquals(Types.BIGINT, id.getSqlType());
    }

    private void assertScalarNullPreserved(DatabaseField id, boolean nullFirst) {
        DatabaseRecord row = new DatabaseRecord();
        row.put(id, null);
        List<Integer> statuses = List.of(0, 1);
        SQLCall call = new SQLCall(nullFirst
                ? "SELECT ID FROM CHILD WHERE ID = ? AND STATUS IN ?"
                : "SELECT ID FROM CHILD WHERE STATUS IN ? AND ID = ?");
        call.setParameters(nullFirst ? List.of(id, statuses) : List.of(statuses, id));

        call.translateQueryStringForParameterizedIN(row, null, session);

        // Expanding the collection must not discard the adjacent scalar null's type.
        int nullIndex = nullFirst ? 0 : 2;
        assertSame(id, call.getParameters().get(nullIndex));
        assertEquals(nullFirst ? List.of(id, 0, 1) : List.of(0, 1, id), call.getParameters());
        assertEquals(Types.BIGINT, session.getPlatform().getJDBCTypeForSetNull(
                (DatabaseField) call.getParameters().get(nullIndex)));
        assertEquals(nullFirst
                ? "SELECT ID FROM CHILD WHERE ID = (?) AND STATUS IN (?,?)"
                : "SELECT ID FROM CHILD WHERE STATUS IN (?,?) AND ID = (?)", call.getSQLString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"IN", "NOT IN"})
    void preservesNullCollectionParenthesesAndFollowingParameter(String operator) {
        DatabaseField statuses = new DatabaseField("statuses");
        statuses.setType(Collection.class);
        DatabaseRecord row = new DatabaseRecord();
        row.put(statuses, null);
        SQLCall call = new SQLCall("SELECT ID FROM CHILD WHERE STATUS " + operator + " ? AND ID = ?");
        call.setParameters(List.of(statuses, 1L));

        call.translateQueryStringForParameterizedIN(row, null, session);

        // A null collection still needs a parenthesized placeholder after expansion.
        assertEquals("SELECT ID FROM CHILD WHERE STATUS " + operator + " (?) AND ID = ?", call.getSQLString());
        assertEquals(List.of(statuses, 1L), call.getParameters());
        assertSame(statuses, call.getParameters().get(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"IN", "NOT IN"})
    void preservesEmptyCollectionExpansionAndFollowingParameter(String operator) {
        SQLCall call = new SQLCall("SELECT ID FROM CHILD WHERE STATUS " + operator + " ? AND ID = ?");
        call.setParameters(List.of(List.of(), 1L));

        call.translateQueryStringForParameterizedIN(new DatabaseRecord(), null, session);

        // Empty collections retain their existing single-null-placeholder behavior.
        assertEquals("SELECT ID FROM CHILD WHERE STATUS " + operator + " (?) AND ID = ?", call.getSQLString());
        assertEquals(Arrays.asList(null, 1L), call.getParameters());
    }
}
