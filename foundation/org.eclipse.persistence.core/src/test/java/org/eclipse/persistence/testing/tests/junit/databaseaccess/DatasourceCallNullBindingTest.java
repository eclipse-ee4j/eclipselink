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

package org.eclipse.persistence.testing.tests.junit.databaseaccess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
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
import org.junit.Test;

public class DatasourceCallNullBindingTest {

    @Test
    public void inlinesTypedNullsAndRebindsWithoutChangingMetadata() throws SQLException {
        for (boolean partial : new boolean[] {true, false}) {
            try {
                inlinesTypedNullsAndRebindsWithoutChangingMetadata(partial);
            } catch (AssertionError failure) {
                throw new AssertionError("partial=" + partial, failure);
            }
        }
    }

    private void inlinesTypedNullsAndRebindsWithoutChangingMetadata(boolean partial) throws SQLException {
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

    @Test
    public void inlineNullsUseRowMetadataWithoutMutatingFields() throws SQLException {
        for (boolean partial : new boolean[] {true, false}) {
            try {
                inlineNullsUseRowMetadataWithoutMutatingFields(partial);
            } catch (AssertionError failure) {
                throw new AssertionError("partial=" + partial, failure);
            }
        }
    }

    private void inlineNullsUseRowMetadataWithoutMutatingFields(boolean partial) throws SQLException {
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

    @Test
    public void inlineNullsPreferClonedRelationalFieldOverDomainType() throws SQLException {
        for (boolean partial : new boolean[] {true, false}) {
            try {
                inlineNullsPreferClonedRelationalFieldOverDomainType(partial);
            } catch (AssertionError failure) {
                throw new AssertionError("partial=" + partial, failure);
            }
        }
    }

    private void inlineNullsPreferClonedRelationalFieldOverDomainType(boolean partial) throws SQLException {
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

    @Test
    public void preservesBoundNullTypesAndFollowingValues() throws SQLException {
        for (boolean partial : new boolean[] {true, false}) {
            try {
                preservesBoundNullTypesAndFollowingValues(partial);
            } catch (AssertionError failure) {
                throw new AssertionError("partial=" + partial, failure);
            }
        }
    }

    private void preservesBoundNullTypesAndFollowingValues(boolean partial) throws SQLException {
        DerbyPlatform platform = new DerbyPlatform();
        platform.setShouldBindAllParameters(true);
        platform.setShouldBindPartialParameters(partial);
        assertEquals(partial, platform.shouldBindPartialParameters());
        DatabaseSessionImpl executionSession = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(executionSession);
        for (Class<?> type : List.of(Integer.class, Long.class, Double.class)) {
            ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", type);
            SQLSelectStatement statement = new SQLSelectStatement();
            statement.setBuilder(builder);
            statement.addField(builder.getField("ID"));
            statement.addTable(new DatabaseTable("ITEM"));
            statement.setWhereClause(builder.getField("VALUE").equal(parameter).and(
                    builder.getField("STATUS").equal(builder.getParameter("tail", Integer.class))));
            statement.computeTablesFromTables();
            DatabaseCall prepared = statement.buildCall(executionSession, null);
            prepared.setQuery(new ReportQuery());
            prepared.prepare(executionSession);
            assertEquals(partial ? Arrays.asList(true, true) : Arrays.asList(null, null),
                    prepared.getParameterBindings());
            DatabaseField rowField = new DatabaseField("value");
            rowField.setType(type);
            DatabaseRecord row = new DatabaseRecord();
            row.put(rowField, null);
            row.put("tail", 77);
            DatabaseCall execution = (DatabaseCall) prepared.clone();
            execution.translate(row, null, executionSession);
            assertEquals("SELECT ID FROM ITEM WHERE ((VALUE = ?) AND (STATUS = ?))", execution.getSQLString());
            assertEquals(2, execution.getParameters().size());
            assertSame(rowField, execution.getParameters().get(0));
            assertEquals(platform.getJDBCType(type), platform.getJDBCTypeForSetNull(
                    (DatabaseField) execution.getParameters().get(0)));
            assertEquals(77, execution.getParameters().get(1));
            assertEquals(type, parameter.getType());
        }

        if (partial) {
            ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", Integer.class);
            SQLSelectStatement statement = new SQLSelectStatement();
            statement.setBuilder(builder);
            // Derby inlines SELECT parameters while allowing this later WHERE parameter to bind.
            statement.addField(parameter);
            statement.addTable(new DatabaseTable("ITEM"));
            statement.setWhereClause(builder.getField("STATUS").equal(builder.getParameter("tail", Integer.class)));
            statement.computeTablesFromTables();
            DatabaseCall prepared = statement.buildCall(executionSession, null);
            prepared.setQuery(new ReportQuery());
            prepared.prepare(executionSession);
            assertEquals(Arrays.asList(false, true), prepared.getParameterBindings());
            DatabaseRecord row = new DatabaseRecord();
            row.put("value", null);
            row.put("tail", 77);
            DatabaseCall execution = (DatabaseCall) prepared.clone();
            execution.translate(row, null, executionSession);
            assertEquals("SELECT CAST(NULL AS INTEGER) FROM ITEM WHERE (STATUS = ?)", execution.getSQLString());
            assertEquals(List.of(77), execution.getParameters());
            assertEquals(Integer.class, parameter.getType());
            assertEquals(DatabaseField.NULL_SQL_TYPE, parameter.getField().getSqlType());
        }
    }

    @Test
    public void preservesFullBindingOptionalAndCollectionTranslation() throws SQLException {
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
        Expression tail = builder.getField("STATUS").equal(builder.getParameter("tail", Integer.class));
        statement.setWhereClause(builder.getField("ID").in(builder.getParameter("ids", Collection.class)).and(tail));
        statement.computeTablesFromTables();
        DatabaseCall membership = statement.buildCall(executionSession, null);
        membership.setQuery(new ReportQuery());
        membership.prepare(executionSession);
        row = new DatabaseRecord();
        row.put("ids", List.of(1, 2));
        row.put("tail", 77);
        execution = (DatabaseCall) membership.clone();
        execution.translate(row, null, executionSession);
        assertEquals("SELECT ID FROM ITEM WHERE ((ID IN (?,?)) AND (STATUS = ?))", execution.getSQLString());
        assertEquals(List.of(1, 2, 77), execution.getParameters());

        // A fixed expression list supplies parentheses before translation on this branch.
        statement.setWhereClause(builder.getField("ID").in(
                List.of(builder.getParameter("value", Integer.class))).and(tail));
        membership = statement.buildCall(executionSession, null);
        membership.setQuery(new ReportQuery());
        membership.prepare(executionSession);
        DatabaseField valueField = new DatabaseField("value");
        valueField.setType(Integer.class);
        for (Integer value : Arrays.asList(5, null)) {
            row = new DatabaseRecord();
            row.put(valueField, value);
            row.put("tail", 77);
            execution = (DatabaseCall) membership.clone();
            execution.translate(row, null, executionSession);
            assertEquals("SELECT ID FROM ITEM WHERE ((ID IN (?)) AND (STATUS = ?))", execution.getSQLString());
            assertEquals(Arrays.asList(value == null ? valueField : value, 77), execution.getParameters());
            if (value == null) {
                assertSame(valueField, execution.getParameters().get(0));
                assertEquals(Types.INTEGER, platform.getJDBCTypeForSetNull(valueField));
            }
        }

        statement.setWhereClause(builder.getField("ID").in(List.of()).and(tail));
        membership = statement.buildCall(executionSession, null);
        membership.setQuery(new ReportQuery());
        membership.prepare(executionSession);
        row = new DatabaseRecord();
        row.put("tail", 77);
        execution = (DatabaseCall) membership.clone();
        execution.translate(row, null, executionSession);
        assertEquals("SELECT ID FROM ITEM WHERE ((ID IN (NULL)) AND (STATUS = ?))", execution.getSQLString());
        assertEquals(List.of(77), execution.getParameters());
    }

    @Test
    public void rendersTypedNullCarrierAsNullInsteadOfFieldName() {
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
    public void preservesBaselineSerializedParameterAndPlatformGraphs() throws Exception {
        assertEquals(-73788244994662346L, ObjectStreamClass.lookup(ParameterExpression.class).getSerialVersionUID());
        assertEquals(1385141844905129190L, ObjectStreamClass.lookup(DatabasePlatform.class).getSerialVersionUID());
        assertEquals(3189761722676101748L, ObjectStreamClass.lookup(DerbyPlatform.class).getSerialVersionUID());

        // Generated with the parameter/platform classes from 4.0 base 1a17932f737c530aa57d9e7acf60f96ca05cd3f4.
        // The fixture contains only production/JDK classes and synthetic parameter/query/platform values.
        Object[] payload;
        try (InputStream resource = DatasourceCallNullBindingTest.class.getResourceAsStream(
                "arithmetic-null-serialization-4.0-base.ser")) {
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
            if (parameter instanceof DatabaseField) {
                nullField = (DatabaseField) parameter;
            }
            return super.appendParameterInternal(call, writer, parameter);
        }
    }
}
