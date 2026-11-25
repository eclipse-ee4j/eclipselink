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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
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
import java.security.MessageDigest;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall.ParameterType;
import org.eclipse.persistence.internal.expressions.CompoundExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.expressions.SQLUpdateStatement;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.remote.Transporter;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.eclipse.persistence.platform.database.JavaDBPlatform;
import org.eclipse.persistence.platform.database.PostgreSQLPlatform;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Session;
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
    public void preservesUnknownConverterNullsWithoutBinding() throws SQLException {
        assertConverterNullBinding(0, 0);
        assertConverterNullBinding(3, 0);
    }

    @Test
    public void preservesUnknownConverterNullsWithPartialBinding() throws SQLException {
        assertConverterNullBinding(0, 1);
        assertConverterNullBinding(3, 1);
    }

    @Test
    public void preservesKnownConverterNullsAndBoundValues() throws SQLException {
        assertConverterNullBinding(0, 2);
        assertConverterNullBinding(3, 2);
        for (int metadata : new int[] {1, 2, 4}) {
            for (int mode = 0; mode < 3; mode++) {
                assertConverterNullBinding(metadata, mode);
            }
        }
    }

    private void assertConverterNullBinding(int metadata, int mode) throws SQLException {
        CapturingDerbyPlatform platform = new CapturingDerbyPlatform();
        platform.setShouldBindAllParameters(mode != 0);
        platform.setShouldBindPartialParameters(mode == 1);
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(ConvertedValue.class);
        descriptor.addTableName("ITEM");
        descriptor.addPrimaryKeyFieldName("ITEM.ID");
        descriptor.addDirectMapping("id", "ITEM.ID");
        DirectToFieldMapping mapping = new DirectToFieldMapping();
        mapping.setAttributeName("value");
        mapping.setFieldName("ITEM.V");
        NullConverter converter = new NullConverter(metadata);
        mapping.setConverter(converter);
        descriptor.addMapping(mapping);
        session.addDescriptor(descriptor);
        session.initializeDescriptors();
        DatabaseField mappedField = mapping.getField();
        DatabaseTable mappedTable = mappedField.getTable();
        Class<?> physicalType = metadata >= 3 ? Object.class : metadata == 1 ? Integer.class : null;
        int physicalSqlType = metadata == 2 || metadata == 4 ? Types.INTEGER : DatabaseField.NULL_SQL_TYPE;
        assertEquals(physicalType, mappedField.getType());
        assertEquals(physicalSqlType, mappedField.getSqlType());

        ExpressionBuilder builder = new ExpressionBuilder(ConvertedValue.class);
        builder.setSession(session);
        QueryKeyExpression attribute = (QueryKeyExpression) builder.get("value");
        assertSame(mapping, attribute.getMapping());
        assertSame(mappedField, attribute.getField());
        ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", String.class);
        parameter.setLocalBase(attribute);
        if (mode == 1) {
            parameter.setCanBind(false);
        }
        Class<?> parameterFieldType = parameter.getField().getType();
        DatabaseRecord changes = new DatabaseRecord();
        changes.put(mappedField, parameter);
        SQLUpdateStatement statement = new SQLUpdateStatement();
        statement.setTable(mappedTable);
        statement.setModifyRow(changes);
        statement.setWhereClause(builder.getField("STATUS").equal(builder.getParameter("tail", Integer.class)));
        DatabaseCall prepared = statement.buildCall(session);
        prepared.setQuery(new UpdateAllQuery());
        prepared.prepare(session);
        assertEquals(Arrays.asList(ParameterType.TRANSLATION, ParameterType.TRANSLATION),
                prepared.getParameterTypes());
        assertEquals(mode == 1 ? Arrays.asList(false, true) : Arrays.asList(null, null),
                prepared.getParameterBindings());

        for (String value : Arrays.asList("9", "NULL", null, "9", null)) {
            DatabaseField rowField = new DatabaseField("value");
            rowField.setType(String.class);
            rowField.setSqlType(Types.VARCHAR);
            DatabaseRecord row = new DatabaseRecord();
            row.put(rowField, value);
            row.put("tail", 77);
            platform.nullField = null;
            int conversions = converter.conversions;
            DatabaseCall execution = (DatabaseCall) prepared.clone();
            execution.translate(row, null, session);
            assertEquals(conversions + 1, converter.conversions);
            boolean nullValue = value == null || "NULL".equals(value);
            boolean unknownType = metadata == 0 || metadata == 3;
            String assignment = mode == 2 ? "?" : nullValue
                    ? (unknownType ? "NULL" : "CAST(NULL AS INTEGER)") : "9";
            assertEquals("UPDATE ITEM SET V = " + assignment + " WHERE (STATUS = "
                    + (mode == 0 ? "77" : "?") + ")", execution.getSQLString());
            assertEquals(mode, execution.getParameters().size());
            if (mode == 2) {
                assertEquals(nullValue ? rowField : Integer.valueOf(9), execution.getParameters().get(0));
            }
            if (mode != 0) {
                assertEquals(77, execution.getParameters().get(mode - 1));
            }
            if (nullValue && mode != 2 && !unknownType) {
                assertNotSame(mappedField, platform.nullField);
                assertSame(mappedTable, platform.nullField.getTable());
                platform.nullField.setType(Double.class);
                platform.nullField.setSqlType(Types.DOUBLE);
            } else {
                assertNull(platform.nullField);
            }
            assertSame(mappedField, mapping.getField());
            assertSame(mappedTable, mappedField.getTable());
            assertEquals("ITEM", mappedTable.getName());
            assertEquals("", mappedTable.getTableQualifier());
            assertEquals(physicalType, mappedField.getType());
            assertEquals(physicalSqlType, mappedField.getSqlType());
            assertEquals(String.class, rowField.getType());
            assertEquals(Types.VARCHAR, rowField.getSqlType());
            assertEquals(value, row.get(rowField));
            assertEquals(String.class, parameter.getType());
            assertEquals(parameterFieldType, parameter.getField().getType());
            assertEquals(DatabaseField.NULL_SQL_TYPE, parameter.getField().getSqlType());
        }
    }

    @Test
    public void preservesUnmappedNullFallbacks() throws SQLException {
        for (Class<?> type : Arrays.<Class<?>>asList(null, Object.class, Integer.class)) {
            for (boolean partial : new boolean[] {false, true}) {
                CapturingDerbyPlatform platform = new CapturingDerbyPlatform();
                platform.setShouldBindAllParameters(partial);
                platform.setShouldBindPartialParameters(partial);
                DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
                ExpressionBuilder builder = new ExpressionBuilder();
                builder.setSession(session);
                ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", type);
                assertSame(builder, parameter.getLocalBase());
                Class<?> fieldType = parameter.getField().getType();
                DatabaseTable table = parameter.getField().getTable();
                DatabaseCall prepared = prepareCall(session, builder, parameter);
                for (Integer value : Arrays.asList(null, 9, null)) {
                    DatabaseRecord row = new DatabaseRecord();
                    row.put("value", value);
                    DatabaseCall execution = (DatabaseCall) prepared.clone();
                    execution.translate(row, null, session);
                    String literal = value == null ? "CAST(NULL AS "
                            + (type == Integer.class ? "INTEGER" : "VARCHAR(1)") + ")" : "9";
                    assertEquals("SELECT " + literal + " FROM ITEM", execution.getSQLString());
                    assertTrue(execution.getParameters().isEmpty());
                    assertEquals(type, parameter.getType());
                    assertEquals(fieldType, parameter.getField().getType());
                    assertEquals(DatabaseField.NULL_SQL_TYPE, parameter.getField().getSqlType());
                    assertSame(table, parameter.getField().getTable());
                    assertEquals("", table.getName());
                }
            }
        }
    }

    public static class ConvertedValue {
        public Integer id;
        public String value;
    }

    private static class NullConverter implements Converter {
        private final int metadata;
        private int conversions;

        private NullConverter(int metadata) {
            this.metadata = metadata;
        }

        @Override
        public Object convertObjectValueToDataValue(Object value, Session session) {
            conversions++;
            return value == null || "NULL".equals(value) ? null : Integer.valueOf(value.toString());
        }

        @Override
        public Object convertDataValueToObjectValue(Object value, Session session) {
            return value == null ? null : value.toString();
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public void initialize(DatabaseMapping mapping, Session session) {
            if (metadata == 1) {
                ((DirectToFieldMapping) mapping).setFieldClassification(Integer.class);
            } else if (metadata >= 3) {
                ((DirectToFieldMapping) mapping).setFieldClassification(Object.class);
            }
            if (metadata == 2 || metadata == 4) {
                ((DirectToFieldMapping) mapping).setFieldType(Types.INTEGER);
            }
        }
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
        for (Class<?> type : Arrays.asList(Integer.class, Long.class, Double.class)) {
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
            assertEquals(Arrays.asList(77), execution.getParameters());
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
        assertEquals(Arrays.asList(77), execution.getParameters());

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
        row.put("ids", Arrays.asList(1, 2));
        row.put("tail", 77);
        execution = (DatabaseCall) membership.clone();
        execution.translate(row, null, executionSession);
        assertEquals("SELECT ID FROM ITEM WHERE ((ID IN (?,?)) AND (STATUS = ?))", execution.getSQLString());
        assertEquals(Arrays.asList(1, 2, 77), execution.getParameters());

        // A fixed expression list supplies parentheses before translation on this branch.
        statement.setWhereClause(builder.getField("ID").in(
                Arrays.asList(builder.getParameter("value", Integer.class))).and(tail));
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

        statement.setWhereClause(builder.getField("ID").in(Arrays.asList()).and(tail));
        membership = statement.buildCall(executionSession, null);
        membership.setQuery(new ReportQuery());
        membership.prepare(executionSession);
        row = new DatabaseRecord();
        row.put("tail", 77);
        execution = (DatabaseCall) membership.clone();
        execution.translate(row, null, executionSession);
        assertEquals("SELECT ID FROM ITEM WHERE ((ID IN (NULL)) AND (STATUS = ?))", execution.getSQLString());
        assertEquals(Arrays.asList(77), execution.getParameters());
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

        for (Object[] testCase : nullAssignmentCases()) {
            Class<?> type = (Class<?>) testCase[0];
            int sqlType = (Integer) testCase[1];
            String literal = "CAST(NULL AS " + testCase[2] + ")";
            assertNullLiteral(new DatabasePlatform(), type, sqlType, "NULL");
            assertNullLiteral(new DerbyPlatform(), type, sqlType, literal);
            assertNullLiteral(new JavaDBPlatform(), type, sqlType, literal);
            if (sqlType != DatabaseField.NULL_SQL_TYPE) {
                assertNullLiteral(new DerbyPlatform(), Object.class, sqlType, literal);
            }
        }
        assertNullLiteral(new DerbyPlatform(), Boolean.class, Types.BIT, "CAST(NULL AS SMALLINT)");
        assertNullLiteral(new DerbyPlatform(), java.sql.Blob.class, DatabaseField.NULL_SQL_TYPE,
                "CAST(NULL AS BLOB(1))");
        for (Object[] testCase : nativeDefinitionCases()) {
            Class<?> type = (Class<?>) testCase[0];
            int sqlType = (Integer) testCase[1];
            String literal = testCase[2] == null ? "NULL" : "CAST(NULL AS " + testCase[2] + ")";
            String definition = (String) testCase[4];
            assertNullLiteral(new DatabasePlatform(), type, sqlType, "NULL", definition);
            assertNullLiteral(new DerbyPlatform(), type, sqlType, literal, definition);
            assertNullLiteral(new JavaDBPlatform(), type, sqlType, literal, definition);
        }
    }

    private void assertNullLiteral(DatabasePlatform platform, Class<?> type, int sqlType, String literal) {
        assertNullLiteral(platform, type, sqlType, literal, "");
    }

    private void assertNullLiteral(DatabasePlatform platform, Class<?> type, int sqlType, String literal,
            String definition) {
        DatabaseField field = new DatabaseField("parameter_name");
        field.setType(type);
        field.setSqlType(sqlType);
        field.setColumnDefinition(definition);
        StringWriter writer = new StringWriter();
        assertEquals(0, platform.appendParameterInternal(new SQLCall(), writer, field));
        assertEquals(literal, writer.toString());
        assertEquals(type, field.getType());
        assertEquals(sqlType, field.getSqlType());
        assertEquals(definition, field.getColumnDefinition());
    }

    private Object[][] nullAssignmentCases() {
        return new Object[][] {
            {Boolean.class, Types.BOOLEAN, "BOOLEAN", Boolean.TRUE},
            {byte[].class, Types.BINARY, "CHAR(1) FOR BIT DATA", new byte[] {3, 4}},
            {byte[].class, Types.VARBINARY, "VARCHAR(1) FOR BIT DATA", new byte[] {3, 4}},
            {byte[].class, Types.LONGVARBINARY, "LONG VARCHAR FOR BIT DATA", new byte[] {3, 4}},
            {byte[].class, Types.BLOB, "BLOB(1)", new byte[] {3, 4}},
            {byte[].class, DatabaseField.NULL_SQL_TYPE, "BLOB(1)", new byte[] {3, 4}},
            {Byte[].class, DatabaseField.NULL_SQL_TYPE, "BLOB(1)", new Byte[] {3, 4}},
            {Boolean.class, DatabaseField.NULL_SQL_TYPE, "SMALLINT", Boolean.TRUE}
        };
    }

    private Object[][] nativeDefinitionCases() {
        return new Object[][] {
            {byte[].class, DatabaseField.NULL_SQL_TYPE, null, new byte[] {3, 4}, "VARCHAR(20) FOR BIT DATA"},
            {Boolean.class, DatabaseField.NULL_SQL_TYPE, null, Boolean.TRUE, "BOOLEAN"},
            {byte[].class, Types.VARBINARY, "VARCHAR(1) FOR BIT DATA", new byte[] {3, 4}, "VARCHAR(20) FOR BIT DATA"},
            {Boolean.class, Types.BOOLEAN, "BOOLEAN", Boolean.TRUE, "BOOLEAN"},
            {byte[].class, DatabaseField.NULL_SQL_TYPE, "BLOB(1)", new byte[] {3, 4}, null},
            {Boolean.class, DatabaseField.NULL_SQL_TYPE, "SMALLINT", Boolean.TRUE, null},
            {byte[].class, DatabaseField.NULL_SQL_TYPE, "BLOB(1)", new byte[] {3, 4}, ""},
            {Boolean.class, DatabaseField.NULL_SQL_TYPE, "SMALLINT", Boolean.TRUE, ""}
        };
    }

    @Test
    public void preservesDerbyNullAssignmentTypesAndBindings() throws SQLException {
        for (Object[] testCase : nullAssignmentCases()) {
            for (int mode = 0; mode < 3; mode++) {
                // Relational, parameter-local and translation-row metadata must retain JDBC provenance.
                for (int metadataSource = 0; metadataSource < 3; metadataSource++) {
                    try {
                        assertNullAssignment(testCase, mode, true, metadataSource);
                    } catch (AssertionError failure) {
                        throw new AssertionError("type=" + testCase[0] + ", sqlType=" + testCase[1]
                                + ", mode=" + mode + ", metadataSource=" + metadataSource, failure);
                    }
                }
                assertNullAssignment(testCase, mode, false, 0);
            }
        }
        for (Object[] testCase : nativeDefinitionCases()) {
            for (int mode = 0; mode < 3; mode++) {
                // Native DDL may leave JDBC metadata unknown until the first SELECT.
                assertNullAssignment(testCase, mode, true, 0);
                assertNullAssignment(testCase, mode, true, 1);
                assertNullAssignment(testCase, mode, false, 0);
            }
        }
    }

    private void assertNullAssignment(Object[] testCase, int mode, boolean expressionAssignment,
            int metadataSource) throws SQLException {
        Class<?> type = (Class<?>) testCase[0];
        int sqlType = (Integer) testCase[1];
        String definition = testCase.length > 4 ? (String) testCase[4] : "";
        boolean bindAll = mode != 0;
        boolean partial = mode == 1;
        DerbyPlatform platform = new DerbyPlatform();
        platform.setShouldBindAllParameters(bindAll);
        platform.setShouldBindPartialParameters(partial);
        assertEquals(partial, platform.shouldBindPartialParameters());
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.setSession(session);
        DatabaseField field = new DatabaseField("ASSIGN_VALUE");
        field.setType(type);
        field.setSqlType(sqlType);
        field.setColumnDefinition(definition);
        ParameterExpression parameter = (ParameterExpression) builder.getParameter("value", type);
        if (metadataSource == 0) {
            parameter.setLocalBase(builder.getField(field));
        } else if (metadataSource == 1) {
            parameter.getField().setType(type);
            parameter.getField().setSqlType(sqlType);
            parameter.getField().setColumnDefinition(definition);
        }
        if (partial) {
            parameter.setCanBind(false);
        }
        Class<?> parameterFieldType = parameter.getField().getType();
        int parameterSqlType = parameter.getField().getSqlType();
        String parameterDefinition = parameter.getField().getColumnDefinition();
        DatabaseRecord changes = new DatabaseRecord();
        changes.put(field, expressionAssignment ? parameter : null);
        SQLUpdateStatement statement = new SQLUpdateStatement();
        statement.setTable(new DatabaseTable("ITEM"));
        statement.setModifyRow(changes);
        statement.setWhereClause(builder.getField("STATUS").equal(builder.getParameter("tail", Integer.class)));
        DatabaseCall prepared = statement.buildCall(session);
        prepared.setQuery(new UpdateAllQuery());
        prepared.prepare(session);
        assertEquals(Arrays.asList(expressionAssignment ? ParameterType.TRANSLATION : ParameterType.MODIFY,
                ParameterType.TRANSLATION), prepared.getParameterTypes());
        assertEquals(Arrays.asList(expressionAssignment ? (partial ? Boolean.FALSE : null) : Boolean.TRUE,
                partial ? Boolean.TRUE : null), prepared.getParameterBindings());

        // Forced-inline nonnull binary values have an existing partial-binding limitation.
        // Their null/non-null/null lifecycle is covered by both supported binding modes.
        Collection<Object> values = partial && expressionAssignment && type.isArray()
                ? Arrays.asList(null, null) : Arrays.asList(null, testCase[3], null);
        for (Object value : values) {
            DatabaseField rowField = new DatabaseField("value");
            rowField.setType(type);
            rowField.setSqlType(sqlType);
            rowField.setColumnDefinition(definition);
            DatabaseRecord row = new DatabaseRecord();
            row.put(rowField, value);
            row.put("tail", 77);
            DatabaseRecord modifyRow = new DatabaseRecord();
            modifyRow.put(field, value);
            DatabaseCall execution = (DatabaseCall) prepared.clone();
            execution.translate(row, modifyRow, session);

            boolean inline = !bindAll || partial && expressionAssignment;
            boolean binaryValue = value != null && type.isArray();
            String assignment;
            if (!inline || binaryValue) {
                assignment = "?";
            } else if (value == null) {
                assignment = expressionAssignment && testCase[2] != null
                        ? "CAST(NULL AS " + testCase[2] + ")" : "NULL";
            } else {
                assignment = "1";
            }
            assertEquals("UPDATE ITEM SET ASSIGN_VALUE = " + assignment + " WHERE (STATUS = "
                    + (bindAll ? "?" : "77") + ")", execution.getSQLString());
            int assignmentParameters = !inline || binaryValue ? 1 : 0;
            assertEquals(assignmentParameters + (bindAll ? 1 : 0), execution.getParameters().size());
            if (assignmentParameters == 1) {
                Object boundValue = execution.getParameters().get(0);
                if (value == null) {
                    assertSame(expressionAssignment ? rowField : field, boundValue);
                    assertEquals(platform.getJDBCType(field),
                            platform.getJDBCTypeForSetNull((DatabaseField) boundValue));
                } else if (!bindAll && binaryValue) {
                    assertArrayEquals((byte[]) platform.convertToDatabaseType(value), (byte[]) boundValue);
                } else {
                    assertSame(value, boundValue);
                }
            }
            if (bindAll) {
                assertEquals(77, execution.getParameters().get(assignmentParameters));
            }
            assertEquals(type, field.getType());
            assertEquals(sqlType, field.getSqlType());
            assertEquals(definition, field.getColumnDefinition());
            assertEquals(type, rowField.getType());
            assertEquals(sqlType, rowField.getSqlType());
            assertEquals(definition, rowField.getColumnDefinition());
            assertEquals(type, parameter.getType());
            assertEquals(parameterFieldType, parameter.getField().getType());
            assertEquals(parameterSqlType, parameter.getField().getSqlType());
            assertEquals(parameterDefinition, parameter.getField().getColumnDefinition());
        }
    }

    @Test
    public void preservesBaselineSerializedParameterAndPlatformGraphs() throws Exception {
        assertEquals(-5914259908564788931L, ObjectStreamClass.lookup(ParameterExpression.class).getSerialVersionUID());
        assertEquals(-2048978264660806895L, ObjectStreamClass.lookup(DatabasePlatform.class).getSerialVersionUID());
        assertEquals(5941064965969966525L, ObjectStreamClass.lookup(DerbyPlatform.class).getSerialVersionUID());

        // Generated with the parameter/platform classes from 2.7 base 00e8344ecb5740e3b5cef9ddeaa88a0b8105ad94.
        // The fixture contains only production/JDK classes and synthetic parameter/query/platform values.
        Object[] payload;
        try (InputStream resource = DatasourceCallNullBindingTest.class.getResourceAsStream(
                "arithmetic-null-serialization-2.7-base.ser.base64")) {
            assertNotNull(resource);
            ByteArrayOutputStream serialized = new ByteArrayOutputStream();
            try (InputStream decoded = Base64.getMimeDecoder().wrap(resource)) {
                byte[] buffer = new byte[1024];
                int count;
                while ((count = decoded.read(buffer)) != -1) {
                    serialized.write(buffer, 0, count);
                }
            }
            byte[] fixture = serialized.toByteArray();
            StringBuilder checksum = new StringBuilder();
            for (byte value : MessageDigest.getInstance("SHA-256").digest(fixture)) {
                checksum.append(String.format("%02x", value & 0xff));
            }
            assertEquals("42b9fee3e244f3e4262a5bbc43c5af256d27d3192f0b0e4038a733c30e8bece1", checksum.toString());
            try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(fixture))) {
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
