/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.platform.database;

import java.io.StringWriter;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.ArgumentListFunctionExpression;
import org.eclipse.persistence.internal.expressions.ClassTypeExpression;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.FieldExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.expressions.TreatAsExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DerbyPlatformTest {

    @Test
    public void testPlainDatePreservesTimeAndMilliseconds() {
        Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
        Object converted = new DerbyPlatform().convertToDatabaseType(value);
        assertInstanceOf(Timestamp.class, converted);
        assertEquals(value.getTime(), ((Timestamp) converted).getTime());
    }

    @Test
    public void testMappingConversionDoesNotRequireConnectedAccessor() {
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(new DerbyPlatform()));
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Date.class);
        DirectToFieldMapping mapping = new DirectToFieldMapping();
        mapping.setDescriptor(descriptor);
        mapping.setFieldName("SCOPED.CREATED_AT");
        mapping.setFieldClassification(Date.class);
        Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());

        assertSame(value, mapping.getFieldValue(value, session));
        assertEquals(DatabaseField.NULL_SQL_TYPE, mapping.getField().getSqlType());
    }

    @Test
    public void testMappedTemporalLiteralsPreserveSourceExpressionsAcrossRepeatedRendering() {
        Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
        for (Class<?> type : List.of(java.sql.Date.class, Time.class, Timestamp.class)) {
            for (boolean reversed : List.of(false, true)) {
                Fixture fixture = new Fixture(new DerbyPlatform(), type);
                ConstantExpression literal = new ConstantExpression(value, fixture.builder);
                ArgumentListFunctionExpression expression = fixture.coalesce(literal, reversed);
                List<Expression> children = expression.getChildren();
                List<Expression> original = new ArrayList<>(children);
                for (int execution = 0; execution < 2; execution++) {
                    String sql = fixture.render(expression);
                    String expected = type == java.sql.Date.class ? "{d '2026-04-13'}"
                            : type == Time.class ? "{t '17:57:41'}" : "{ts '2026-04-13 17:57:41.123'}";
                    assertEquals(reversed ? "COALESCE(" + expected + ", TEMPORAL_VALUE)"
                            : "COALESCE(TEMPORAL_VALUE, " + expected + ")", sql);
                    assertSame(children, expression.getChildren());
                    assertEquals(original.size(), children.size());
                    for (int index = 0; index < original.size(); index++) {
                        assertSame(original.get(index), children.get(index));
                    }
                    assertSame(value, literal.getValue());
                    assertSame(fixture.builder, literal.getLocalBase());
                    assertEquals(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime(), value.getTime());
                }
                assertEquals(2, fixture.mapping.conversions);
            }
        }
    }

    @Test
    public void testMutatingConverterDoesNotChangeInferredTemporalLiteral() {
        long millis = Timestamp.valueOf("2026-04-13 17:57:41.123").getTime();
        for (Date value : List.of(new Date(millis), new java.sql.Date(millis), new Time(millis),
                Timestamp.valueOf("2026-04-13 17:57:41.123456789"))) {
            for (boolean missingBase : List.of(false, true)) {
                for (boolean reversed : List.of(false, true)) {
                    Fixture fixture = new Fixture(new DerbyPlatform(), Timestamp.class);
                    NormalizingConverter converter = new NormalizingConverter();
                    fixture.mapping.setConverter(converter);
                    Expression base = missingBase ? null : fixture.builder;
                    ConstantExpression literal = new ConstantExpression(value, base);
                    ArgumentListFunctionExpression expression = fixture.coalesce(literal, reversed);
                    List<Expression> children = expression.getChildren();
                    List<Expression> originalChildren = new ArrayList<>(children);
                    int nanos = value instanceof Timestamp timestamp ? timestamp.getNanos() : 0;
                    for (int execution = 0; execution < 2; execution++) {
                        String timestamp = "{ts '2026-04-13 17:57:41.0'}";
                        assertEquals(reversed ? "COALESCE(" + timestamp + ", TEMPORAL_VALUE)"
                                : "COALESCE(TEMPORAL_VALUE, " + timestamp + ")", fixture.render(expression));
                        assertNotSame(value, converter.receivedValue);
                        assertEquals(value.getClass(), converter.receivedValue.getClass());
                        assertEquals(millis, converter.receivedMillis);
                        assertEquals(nanos, converter.receivedNanos);
                        assertEquals(execution + 1, fixture.mapping.conversions);
                        assertSame(value, literal.getValue());
                        assertSame(base, literal.getLocalBase());
                        assertEquals(millis, value.getTime());
                        if (value instanceof Timestamp originalTimestamp) {
                            assertEquals(nanos, originalTimestamp.getNanos());
                        }
                        assertSame(children, expression.getChildren());
                        assertEquals(originalChildren.size(), children.size());
                        for (int index = 0; index < originalChildren.size(); index++) {
                            assertSame(originalChildren.get(index), children.get(index));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testExistingMappedLiteralBaseIsNotConvertedTwice() {
        Fixture fixture = new Fixture(new DerbyPlatform(), java.sql.Date.class);
        Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
        ConstantExpression literal = new ConstantExpression(value, fixture.attribute);

        assertEquals("COALESCE(TEMPORAL_VALUE, {d '2026-04-13'})", fixture.render(fixture.coalesce(literal, false)));
        assertEquals(1, fixture.mapping.conversions);
        assertSame(value, literal.getValue());
        assertSame(fixture.attribute, literal.getLocalBase());
    }

    @Test
    public void testTimestampNanosecondsArePreserved() {
        Fixture fixture = new Fixture(new DerbyPlatform(), Timestamp.class);
        Timestamp value = Timestamp.valueOf("2026-04-13 17:57:41.123456789");
        ConstantExpression literal = new ConstantExpression(value, fixture.builder);

        assertSame(value, fixture.session.getPlatform().convertToDatabaseType(value));
        assertEquals("COALESCE(TEMPORAL_VALUE, {ts '2026-04-13 17:57:41.123456789'})",
                fixture.render(fixture.coalesce(literal, false)));
        assertSame(value, literal.getValue());
        assertEquals(123456789, value.getNanos());
    }

    @Test
    public void testOtherPlatformsDoNotUseMappedTemporalConversion() {
        Fixture fixture = new Fixture(new H2Platform(), java.sql.Date.class);
        Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
        String sql = fixture.render(fixture.coalesce(new ConstantExpression(value, fixture.builder), false));

        assertTrue(sql.contains("17:57:41.123"), sql);
        assertEquals(0, fixture.mapping.conversions);
    }

    @Test
    public void testOtherOperatorsDoNotUseMappedTemporalConversion() {
        Fixture fixture = new Fixture(new DerbyPlatform(), java.sql.Date.class);
        Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
        ArgumentListFunctionExpression expression = new ArgumentListFunctionExpression();
        expression.setOperator(ExpressionOperator.caseStatement());
        expression.addChild(fixture.attribute);
        expression.addChild(new ConstantExpression(1, fixture.builder));
        expression.addChild(new ConstantExpression(value, fixture.builder));
        expression.addChild(new ConstantExpression(null, fixture.builder));

        assertTrue(fixture.render(expression).contains("{ts '2026-04-13 17:57:41.123'}"));
        assertEquals(0, fixture.mapping.conversions);
    }

    @Test
    public void testUnmappedDateAndNonDateConstantsRemainUnchanged() {
        Fixture fixture = new Fixture(new DerbyPlatform(), java.sql.Date.class);
        Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
        ArgumentListFunctionExpression unmapped = fixture.builder.coalesce();
        unmapped.addChild(new ConstantExpression(value, fixture.builder));
        unmapped.addChild(new ConstantExpression(null, fixture.builder));
        assertEquals("COALESCE({ts '2026-04-13 17:57:41.123'}, NULL)", fixture.render(unmapped));
        assertEquals("COALESCE(TEMPORAL_VALUE, NULL)",
                fixture.render(fixture.coalesce(new ConstantExpression(null, fixture.builder), false)));
        assertEquals("COALESCE(TEMPORAL_VALUE, 'unchanged')",
                fixture.render(fixture.coalesce(new ConstantExpression("unchanged", fixture.builder), false)));
        assertEquals(0, fixture.mapping.conversions);
    }

    @Test
    public void testExplicitMappedBaseRetainsItsConverter() {
        for (boolean incremental : List.of(false, true)) {
            Fixture selected = new Fixture(new DerbyPlatform(), Timestamp.class);
            Fixture context = new Fixture(new DerbyPlatform(), Timestamp.class);
            context.mapping.offsetMillis = 86_400_000L;
            Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
            ArgumentListFunctionExpression expression;
            if (incremental) {
                expression = selected.builder.coalesce();
                expression.addChild(selected.attribute);
                expression.addChild(context.attribute.value(value));
            } else {
                expression = context.attribute.coalesce(List.of(selected.attribute, value));
            }
            assertEquals("COALESCE(TEMPORAL_VALUE, {ts '2026-04-14 17:57:41.123'})", selected.render(expression));
            assertEquals(0, selected.mapping.conversions);
            assertEquals(1, context.mapping.conversions);
            assertEquals(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime(), value.getTime());
        }
    }

    @Test
    public void testExplicitFieldUnmappedAttributeAndFunctionBasesRemainAuthoritative() {
        Fixture fixture = new Fixture(new DerbyPlatform(), Timestamp.class);
        fixture.mapping.offsetMillis = 86_400_000L;
        DatabaseField field = new DatabaseField("OTHER_TEMPORAL_VALUE");
        field.setType(Timestamp.class);
        FieldExpression fieldBase = new FieldExpression(field, fixture.builder);
        QueryKeyExpression unmappedBase = new QueryKeyExpression("unmapped", fixture.builder) {
            @Override
            public DatabaseMapping getMapping() {
                return null;
            }
        };
        FunctionExpression functionBase = new FunctionExpression();
        functionBase.setBaseExpression(fixture.builder);
        for (Expression base : List.of(fieldBase, unmappedBase, functionBase)) {
            Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
            ConstantExpression literal = new ConstantExpression(value, base);
            assertEquals("COALESCE(TEMPORAL_VALUE, {ts '2026-04-13 17:57:41.123'})",
                    fixture.render(fixture.coalesce(literal, false)));
            assertEquals(0, fixture.mapping.conversions);
            assertSame(base, literal.getLocalBase());
            assertSame(value, literal.getValue());
        }
    }

    @Test
    public void testCustomBaseConversionRemainsAuthoritative() {
        Fixture fixture = new Fixture(new DerbyPlatform(), Timestamp.class);
        fixture.mapping.offsetMillis = 86_400_000L;
        ExpressionBuilder context = new ExpressionBuilder() {
            @Override
            public Object getFieldValue(Object value, AbstractSession session) {
                return new Timestamp(((Date) value).getTime() + 172_800_000L);
            }
        };
        context.setSession(fixture.session);
        FunctionExpression custom = new FunctionExpression() {
            @Override
            public Object getFieldValue(Object value, AbstractSession session) {
                return new Timestamp(((Date) value).getTime() + 172_800_000L);
            }
        };
        custom.setBaseExpression(fixture.builder);
        for (Expression base : List.of(context, custom)) {
            Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
            ConstantExpression literal = new ConstantExpression(value, base);
            assertEquals("COALESCE(TEMPORAL_VALUE, {ts '2026-04-15 17:57:41.123'})",
                    fixture.render(fixture.coalesce(literal, false)));
            assertEquals(0, fixture.mapping.conversions);
            assertSame(base, literal.getLocalBase());
            assertSame(value, literal.getValue());
        }
    }

    @Test
    public void testDelegatedConversionRemainsAuthoritative() {
        Fixture selected = new Fixture(new DerbyPlatform(), Timestamp.class);
        Fixture context = new Fixture(new DerbyPlatform(), Timestamp.class);
        context.mapping.offsetMillis = 86_400_000L;
        TreatAsExpression delegated = new TreatAsExpression(Date.class, context.attribute);
        Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
        ConstantExpression literal = new ConstantExpression(value, delegated);
        assertEquals("COALESCE(TEMPORAL_VALUE, {ts '2026-04-14 17:57:41.123'})",
                selected.render(selected.coalesce(literal, false)));
        assertEquals(0, selected.mapping.conversions);
        assertEquals(1, context.mapping.conversions);
        assertSame(delegated, literal.getLocalBase());
    }

    @Test
    public void testExplicitInvalidConversionStillThrows() {
        Fixture fixture = new Fixture(new DerbyPlatform(), Timestamp.class);
        ConstantExpression literal = new ConstantExpression(new Date(), new ClassTypeExpression(fixture.builder));
        assertThrows(QueryException.class, () -> fixture.render(fixture.coalesce(literal, false)));
        assertEquals(0, fixture.mapping.conversions);
    }

    @Test
    public void testMissingLiteralBaseInfersMappedTemporalType() {
        Fixture fixture = new Fixture(new DerbyPlatform(), java.sql.Date.class);
        Date value = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
        ConstantExpression literal = new ConstantExpression(value, null);
        assertEquals("COALESCE(TEMPORAL_VALUE, {d '2026-04-13'})", fixture.render(fixture.coalesce(literal, false)));
        assertEquals(1, fixture.mapping.conversions);
        assertSame(value, literal.getValue());
        assertNull(literal.getLocalBase());
    }

    private static final class Fixture {
        final DatabaseSessionImpl session;
        final ExpressionBuilder builder = new ExpressionBuilder();
        final CountingMapping mapping = new CountingMapping();
        final QueryKeyExpression attribute;

        Fixture(DatabasePlatform platform, Class<?> fieldType) {
            platform.setShouldBindAllParameters(false);
            platform.setShouldBindLiterals(false);
            session = new DatabaseSessionImpl(new DatabaseLogin(platform));
            builder.setSession(session);
            RelationalDescriptor descriptor = new RelationalDescriptor();
            descriptor.setJavaClass(Date.class);
            mapping.setDescriptor(descriptor);
            mapping.setFieldName("TEMPORAL_VALUE");
            mapping.setAttributeClassification(Date.class);
            mapping.setFieldClassification(fieldType);
            attribute = new QueryKeyExpression("value", builder) {
                @Override
                public DatabaseMapping getMapping() {
                    return Fixture.this.mapping;
                }

                @Override
                public Object getFieldValue(Object value, AbstractSession context) {
                    return Fixture.this.mapping.getFieldValue(value, context);
                }

                @Override
                public void printSQL(ExpressionSQLPrinter printer) {
                    printer.printString("TEMPORAL_VALUE");
                }
            };
        }

        ArgumentListFunctionExpression coalesce(ConstantExpression literal, boolean reversed) {
            ArgumentListFunctionExpression expression = builder.coalesce();
            expression.addChild(reversed ? literal : attribute);
            expression.addChild(reversed ? attribute : literal);
            return expression;
        }

        String render(ArgumentListFunctionExpression expression) {
            StringWriter sql = new StringWriter();
            ExpressionSQLPrinter printer = new ExpressionSQLPrinter(session, null, new SQLCall(), false, null);
            printer.setWriter(sql);
            expression.printSQL(printer);
            return sql.toString();
        }
    }

    private static final class NormalizingConverter implements Converter {
        Date receivedValue;
        long receivedMillis;
        int receivedNanos;

        @Override
        public Object convertObjectValueToDataValue(Object value, Session session) {
            receivedValue = (Date) value;
            receivedMillis = receivedValue.getTime();
            receivedNanos = receivedValue instanceof Timestamp timestamp ? timestamp.getNanos() : 0;
            receivedValue.setTime(receivedMillis / 1000 * 1000);
            return new Timestamp(receivedValue.getTime());
        }

        @Override
        public Object convertDataValueToObjectValue(Object value, Session session) {
            return value;
        }

        @Override
        public boolean isMutable() {
            return true;
        }

        @Override
        public void initialize(DatabaseMapping mapping, Session session) {
        }
    }

    private static final class CountingMapping extends DirectToFieldMapping {
        int conversions;
        long offsetMillis;

        @Override
        public Object getFieldValue(Object value, AbstractSession session) {
            conversions++;
            if (offsetMillis != 0 && value instanceof Date date) {
                value = new Date(date.getTime() + offsetMillis);
            }
            return super.getFieldValue(value, session);
        }
    }
}
