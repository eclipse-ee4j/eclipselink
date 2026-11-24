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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.CompoundExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.remote.Transporter;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.jupiter.api.Test;
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

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void inlinesTypedNullsAndRebindsWithoutChangingMetadata(boolean partial) throws SQLException {
        DerbyPlatform platform = new DerbyPlatform();
        platform.setShouldBindAllParameters(partial);
        platform.setShouldBindPartialParameters(partial);
        DatabaseSessionImpl executionSession = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(executionSession);
        Class<?>[] types = {Integer.class, Long.class, Double.class, Object.class, String.class};
        Object[] values = {42, 42L, 42.5, "text", "text"};
        String[] literals = {"42", "42", "42.5", "'text'", "'text'"};
        String[] casts = {"INTEGER", "BIGINT", "DOUBLE", "VARCHAR(1)", "VARCHAR(1)"};
        for (int index = 0; index < types.length; index++) {
            ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", types[index]);
            DatabaseCall prepared = prepareCall(executionSession, builder, parameter);
            for (Object value : Arrays.asList(null, values[index], null, values[index])) {
                DatabaseRecord row = new DatabaseRecord();
                row.put("value", value);
                DatabaseCall execution = (DatabaseCall) prepared.clone();
                execution.translate(row, null, executionSession);
                String literal = value == null ? "CAST(NULL AS " + casts[index] + ")" : literals[index];
                assertEquals("SELECT " + literal + " FROM ITEM", execution.getSQLString());
                assertTrue(execution.getParameters().isEmpty());
                assertEquals(types[index], parameter.getType());
                assertEquals(DatabaseField.NULL_SQL_TYPE, parameter.getField().getSqlType());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void inlineNullsUseRowMetadataWithoutMutatingFields(boolean partial) throws SQLException {
        CapturingDerbyPlatform platform = new CapturingDerbyPlatform();
        platform.setShouldBindAllParameters(partial);
        platform.setShouldBindPartialParameters(partial);
        DatabaseSessionImpl executionSession = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(executionSession);
        ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", Integer.class);
        parameter.getField().setType(Integer.class);
        parameter.getField().setSqlType(Types.INTEGER);
        DatabaseField rowField = new DatabaseField("value");
        rowField.setType(Long.class);
        rowField.setSqlType(Types.BIGINT);
        DatabaseRecord row = new DatabaseRecord();
        row.put(rowField, null);
        DatabaseCall execution = (DatabaseCall) prepareCall(executionSession, builder, parameter).clone();
        execution.translate(row, null, executionSession);
        assertEquals("SELECT CAST(NULL AS BIGINT) FROM ITEM", execution.getSQLString());
        assertNotSame(parameter.getField(), platform.nullField);
        assertNotSame(rowField, platform.nullField);
        platform.nullField.setType(Double.class);
        platform.nullField.setSqlType(Types.DOUBLE);
        assertEquals(Integer.class, parameter.getType());
        assertEquals(Integer.class, parameter.getField().getType());
        assertEquals(Types.INTEGER, parameter.getField().getSqlType());
        assertEquals(Long.class, rowField.getType());
        assertEquals(Types.BIGINT, rowField.getSqlType());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void inlineNullsPreferClonedRelationalFieldOverDomainType(boolean partial) throws SQLException {
        CapturingDerbyPlatform platform = new CapturingDerbyPlatform();
        platform.setShouldBindAllParameters(partial);
        platform.setShouldBindPartialParameters(partial);
        DatabaseSessionImpl executionSession = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(executionSession);
        DatabaseField relational = new DatabaseField("TEXT_VALUE");
        relational.setType(String.class);
        relational.setSqlType(Types.CHAR);
        ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", Integer.class);
        parameter.setLocalBase(builder.getField(relational));
        DatabaseField rowField = new DatabaseField("value");
        rowField.setType(Integer.class);
        DatabaseRecord row = new DatabaseRecord();
        row.put(rowField, null);
        DatabaseCall execution = (DatabaseCall) prepareCall(executionSession, builder, parameter).clone();
        execution.translate(row, null, executionSession);
        assertEquals("SELECT CAST(NULL AS CHAR(1)) FROM ITEM", execution.getSQLString());
        assertNotSame(relational, platform.nullField);
        platform.nullField.setType(Double.class);
        platform.nullField.setSqlType(Types.DOUBLE);
        assertEquals(String.class, relational.getType());
        assertEquals(Types.CHAR, relational.getSqlType());
        assertEquals(Integer.class, rowField.getType());
        assertEquals(DatabaseField.NULL_SQL_TYPE, rowField.getSqlType());
        assertEquals(Integer.class, parameter.getType());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void preservesBoundNullTypesAndFollowingValues(boolean partial) throws SQLException {
        PostgreSQLPlatform platform = new PostgreSQLPlatform();
        platform.setShouldBindAllParameters(true);
        platform.setShouldBindPartialParameters(partial);
        DatabaseSessionImpl executionSession = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(executionSession);
        for (Class<?> type : List.of(Integer.class, Long.class, Double.class)) {
            ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", type);
            DatabaseCall prepared = prepareCall(executionSession, builder, parameter,
                    builder.getParameter("tail", Integer.class));
            DatabaseField rowField = new DatabaseField("value");
            rowField.setType(type);
            DatabaseRecord row = new DatabaseRecord();
            row.put(rowField, null);
            row.put("tail", 77);
            DatabaseCall execution = (DatabaseCall) prepared.clone();
            execution.translate(row, null, executionSession);
            assertEquals("SELECT ?, ? FROM ITEM", execution.getSQLString());
            assertEquals(2, execution.getParameters().size());
            assertSame(rowField, execution.getParameters().get(0));
            assertEquals(platform.getJDBCType(type), platform.getJDBCTypeForSetNull(
                    (DatabaseField) execution.getParameters().get(0)));
            assertEquals(77, execution.getParameters().get(1));
            assertEquals(type, parameter.getType());
        }
    }

    @Test
    void preservesFullBindingOptionalAndCollectionTranslation() throws SQLException {
        PostgreSQLPlatform platform = new PostgreSQLPlatform();
        platform.setShouldBindAllParameters(true);
        platform.setShouldBindPartialParameters(false);
        DatabaseSessionImpl executionSession = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(executionSession);
        DatabaseCall optional = prepareCall(executionSession, builder, builder.getParameter("optional", Object.class),
                builder.getParameter("tail", Integer.class));
        optional.getQuery().getNullableArguments().add(new DatabaseField("optional"));
        DatabaseRecord row = new DatabaseRecord();
        row.put("optional", null);
        row.put("tail", 77);
        DatabaseCall execution = (DatabaseCall) optional.clone();
        execution.translate(row, null, executionSession);
        assertEquals(List.of(77), execution.getParameters());

        SQLSelectStatement statement = new SQLSelectStatement();
        statement.setBuilder(builder);
        statement.addField(builder.getField("ID"));
        statement.addTable(new DatabaseTable("ITEM"));
        statement.setWhereClause(builder.getField("ID").in(builder.getParameter("ids", Collection.class)));
        statement.computeTablesFromTables();
        DatabaseCall membership = statement.buildCall(executionSession, null);
        membership.setQuery(new ReportQuery());
        membership.prepare(executionSession);
        row = new DatabaseRecord();
        row.put("ids", List.of(1, 2));
        execution = (DatabaseCall) membership.clone();
        execution.translate(row, null, executionSession);
        assertTrue(execution.getSQLString().contains("IN (?,?)"));
        assertEquals(List.of(1, 2), execution.getParameters());
        row.put("ids", null);
        execution = (DatabaseCall) membership.clone();
        execution.translate(row, null, executionSession);
        assertTrue(execution.getSQLString().contains("IN (?)"));
        assertEquals(1, execution.getParameters().size());
    }

    @Test
    void rendersTypedNullCarrierAsNullInsteadOfFieldName() {
        DatabaseField field = new DatabaseField("parameter_name");
        field.setType(Integer.class);
        StringWriter writer = new StringWriter();
        assertEquals(0, new DatabasePlatform().appendParameterInternal(new SQLCall(), writer, field));
        assertEquals("NULL", writer.toString());
        writer = new StringWriter();
        assertEquals(0, new DerbyPlatform().appendParameterInternal(new SQLCall(), writer, field));
        assertEquals("CAST(NULL AS INTEGER)", writer.toString());
        assertEquals(Integer.class, field.getType());
        assertEquals(DatabaseField.NULL_SQL_TYPE, field.getSqlType());
    }

    @Test
    void preservesBaselineSerializedParameterAndPlatformGraphs() throws Exception {
        assertEquals(2557286785134171861L, ObjectStreamClass.lookup(ParameterExpression.class).getSerialVersionUID());
        assertEquals(6612575632706304494L, ObjectStreamClass.lookup(DatabasePlatform.class).getSerialVersionUID());
        assertEquals(1172253490549540445L, ObjectStreamClass.lookup(DerbyPlatform.class).getSerialVersionUID());

        // Generated with the parameter/platform classes from 5.0 base 9728d89de96024a0c87286037ca560048e66cb2d.
        // The fixture contains only production/JDK classes and synthetic parameter/query/platform values.
        Object[] payload;
        try (InputStream resource = DatasourceCallNullBindingTest.class.getResourceAsStream(
                "arithmetic-null-serialization-5.0-base.ser")) {
            assertNotNull(resource);
            try (ObjectInputStream input = new ObjectInputStream(resource)) {
                payload = (Object[]) input.readObject();
            }
        }
        assertSerializedPayload(payload);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(payload);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            assertSerializedPayload((Object[]) input.readObject());
        }
    }

    private void assertSerializedPayload(Object[] payload) {
        assertEquals(4, payload.length);
        assertEquals(ParameterExpression.class, payload[0].getClass());
        ParameterExpression parameter = (ParameterExpression) payload[0];
        assertEquals("compatValue", parameter.getField().getName());
        assertEquals(Integer.class, parameter.getType());
        assertEquals(Transporter.class, payload[1].getClass());
        Transporter transporter = (Transporter) payload[1];
        assertTrue(transporter.wasOperationSuccessful());
        assertEquals(ReadAllQuery.class, transporter.getObject().getClass());
        ReadAllQuery query = (ReadAllQuery) transporter.getObject();
        assertEquals("serializationCompatibility", query.getName());
        assertEquals(Object.class, query.getReferenceClass());
        assertSame(parameter, ((CompoundExpression) query.getSelectionCriteria()).getSecondChild());
        assertEquals(DatabasePlatform.class, payload[2].getClass());
        DatabasePlatform platform = (DatabasePlatform) payload[2];
        assertEquals("COMPAT", platform.getTableQualifier());
        assertTrue(platform.shouldBindAllParameters());
        assertFalse(platform.shouldBindPartialParameters());
        assertEquals(DerbyPlatform.class, payload[3].getClass());
        DerbyPlatform derby = (DerbyPlatform) payload[3];
        assertEquals("DERBY_COMPAT", derby.getTableQualifier());
        assertTrue(derby.shouldBindAllParameters());
        assertTrue(derby.shouldBindPartialParameters());
        assertFalse(derby.allowBindingForSelectClause());
    }

    private DatabaseCall prepareCall(DatabaseSessionImpl executionSession, ExpressionBuilder builder,
            Expression... expressions) throws SQLException {
        SQLSelectStatement statement = new SQLSelectStatement();
        statement.setBuilder(builder);
        for (Expression expression : expressions) {
            statement.addField(expression);
        }
        statement.addTable(new DatabaseTable("ITEM"));
        statement.computeTablesFromTables();
        DatabaseCall call = statement.buildCall(executionSession, null);
        call.setQuery(new ReportQuery());
        call.prepare(executionSession);
        return call;
    }

    private static class CapturingDerbyPlatform extends DerbyPlatform {
        private DatabaseField nullField;

        @Override
        public int appendParameterInternal(Call call, Writer writer, Object parameter) {
            if (parameter instanceof DatabaseField field) {
                nullField = field;
            }
            return super.appendParameterInternal(call, writer, parameter);
        }
    }
}
