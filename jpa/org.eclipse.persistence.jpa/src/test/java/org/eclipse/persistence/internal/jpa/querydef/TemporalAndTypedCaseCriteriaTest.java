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

package org.eclipse.persistence.internal.jpa.querydef;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder.Case;
import jakarta.persistence.criteria.CriteriaBuilder.SimpleCase;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.LocalDateField;
import jakarta.persistence.criteria.LocalTimeField;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.TemporalExpression;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.DB2Platform;
import org.eclipse.persistence.platform.database.DB2ZPlatform;
import org.eclipse.persistence.platform.database.DB2MainframePlatform;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.eclipse.persistence.platform.database.JavaDBPlatform;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.MariaDBPlatform;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.LocalDateTimeField;

import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemporalAndTypedCaseCriteriaTest {
    private final CriteriaBuilderImpl builder = new CriteriaBuilderImpl(new MetamodelImpl(new DatabaseSessionImpl(new Project(new DatabaseLogin()))));

    @SuppressWarnings("unchecked")
    private static <T> T roundTrip(T value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (T) input.readObject();
        }
    }

    @Test
    void subqueryCopyOwnsNonFetchJoinAndOnClauseWithTheExistingIdentityMap() {
        ExpressionBuilder outer = new ExpressionBuilder(Object.class);
        ExpressionBuilder inner = new ExpressionBuilder(Object.class);
        var parameter = inner.getParameter("onName", String.class);
        var join = (org.eclipse.persistence.internal.expressions.QueryKeyExpression) inner.get("parent");
        var on = join.get("text").equal(parameter).and(join.get("id").equal(outer.get("id")));
        inner.join(join, on);
        var report = new org.eclipse.persistence.queries.ReportQuery(Object.class, inner);
        report.addAttribute("id", inner.get("id"));
        report.setSelectionCriteria(inner.get("id").equal(2));
        report.addNonFetchJoinedAttribute(join);
        report.addNonFetchJoinedAttribute(join);
        assertSame(inner, join.getBuilder());
        assertSame(inner, report.getExpressionBuilder());
        var original = new org.eclipse.persistence.internal.expressions.SubSelectExpression(report, outer);
        java.util.Map<org.eclipse.persistence.expressions.Expression, org.eclipse.persistence.expressions.Expression> copies = new java.util.IdentityHashMap<>();
        var copied = (org.eclipse.persistence.internal.expressions.SubSelectExpression) original.copiedVersionFrom(copies);
        var copiedJoin = (org.eclipse.persistence.internal.expressions.QueryKeyExpression)
                copied.getSubQuery().getNonFetchJoinAttributeExpressions().getFirst();
        assertNotSame(join, copiedJoin);
        assertSame(copies.get(join), copiedJoin);
        assertNotSame(report.getNonFetchJoinAttributeExpressions(), copied.getSubQuery().getNonFetchJoinAttributeExpressions());
        assertSame(copiedJoin, copied.getSubQuery().getNonFetchJoinAttributeExpressions().get(1));
        assertSame(copies.get(outer), copied.getBaseExpression());
        assertSame(copies.get(outer.get("id")), ((org.eclipse.persistence.internal.expressions.CompoundExpression)
                ((org.eclipse.persistence.internal.expressions.CompoundExpression) copiedJoin.getOnClause()).getSecondChild()).getSecondChild());
        assertSame(copied.getSubQuery().getExpressionBuilder(), copiedJoin.getBuilder());
        assertNotSame(on, copiedJoin.getOnClause());
        assertSame(copies.get(on), copiedJoin.getOnClause());
        assertSame(inner, join.getBuilder());
        assertSame(on, join.getOnClause());
        assertSame(join, report.getNonFetchJoinAttributeExpressions().getFirst());
        var noJoin = new org.eclipse.persistence.queries.ReportQuery(Object.class, inner);
        var noJoinCopy = (org.eclipse.persistence.internal.expressions.SubSelectExpression)
                new org.eclipse.persistence.internal.expressions.SubSelectExpression(noJoin, outer).clone();
        assertTrue(noJoinCopy.getSubQuery().getNonFetchJoinAttributeExpressions().isEmpty());
        var call = new org.eclipse.persistence.queries.ReportQuery();
        call.setSQLString("SELECT 1");
        var callCopy = (org.eclipse.persistence.internal.expressions.SubSelectExpression)
                new org.eclipse.persistence.internal.expressions.SubSelectExpression(call, outer).clone();
        assertTrue(callCopy.getSubQuery().isCallQuery());
        assertTrue(callCopy.getSubQuery().getNonFetchJoinAttributeExpressions().isEmpty());
    }

    @Test
    void numericCompositionsFollowMutableCasesWithoutChangingFixedResults() throws Exception {
        CriteriaBuilderImpl cb = new CriteriaBuilderImpl(null);
        Case<Number> value = cb.selectCase(Number.class).when(cb.literal(false), 1);
        value.otherwise(2);
        value.alias("source");
        ParameterExpression<Integer> parameter = cb.parameter(Integer.class, "number");
        List<Expression<?>> expressions = List.of(cb.sum(value, parameter), cb.sum(value, 1), cb.sum(1, value),
                cb.prod(value, parameter), cb.prod(value, 2), cb.prod(2, value), cb.diff(value, parameter),
                cb.diff(value, 1), cb.diff(5, value), cb.quot(value, parameter), cb.quot(value, 2), cb.quot(5, value),
                cb.abs(value), cb.neg(value), cb.abs(cb.neg(cb.sum(value, 1))), cb.coalesce(value, parameter),
                cb.coalesce(value, 1), cb.<Number>coalesce().value(value).value(parameter),
                cb.nullif(value, parameter), cb.nullif(value, (Number) null));
        Expression<Integer> fixed = value.cast(Integer.class);
        for (int index = 0; index < expressions.size(); index++) {
            assertEquals(index == 11 ? Number.class : Integer.class, expressions.get(index).getJavaType());
        }
        Object[] restored = roundTrip(new Object[]{value, parameter, expressions});
        value.otherwise(2.5d);
        for (Expression<?> expression : expressions) {
            assertEquals(Double.class, expression.getJavaType());
            assertEquals(Double.class, ((FunctionExpression) ((InternalSelection) expression).getCurrentNode()).getResultType());
        }
        assertEquals(Integer.class, fixed.getJavaType());
        assertEquals("source", value.getAlias());
        assertEquals(Long.class, cb.sumAsLong(parameter).getJavaType());
        assertEquals(Double.class, cb.sumAsDouble(cb.literal(1f)).getJavaType());
        assertEquals(Long.class, cb.count(value).getJavaType());
        assertEquals(Byte.class, cb.neg(cb.literal((byte) 1)).getJavaType());
        assertEquals(Short.class, cb.abs(cb.literal((short) 1)).getJavaType());
        assertEquals(Double.class, cb.sum(cb.literal(new BigDecimal("1.25")), cb.literal(2d)).getJavaType());
        assertEquals(Float.class, cb.prod(cb.literal(new BigInteger("2")), cb.literal(2f)).getJavaType());
        Expression<Integer> original = cb.literal(7);
        var originalNode = ((InternalSelection) original).getCurrentNode();
        Expression<?> typedNull = cb.nullif(original, cb.nullLiteral(Double.class));
        assertEquals(Double.class, typedNull.getJavaType());
        assertNotSame(originalNode, ((InternalSelection) typedNull).getCurrentNode());
        assertSame(originalNode, ((InternalSelection) original).getCurrentNode());
        assertEquals(Integer.class, original.getJavaType());
        @SuppressWarnings("unchecked")
        Case<Number> restoredCase = (Case<Number>) restored[0];
        @SuppressWarnings("unchecked")
        List<Expression<?>> restoredExpressions = (List<Expression<?>>) restored[2];
        restoredCase.otherwise(3.5d);
        for (Expression<?> expression : restoredExpressions) {
            assertEquals(Double.class, expression.getJavaType());
        }
        assertEquals(Set.of(restored[1]), cb.createTupleQuery().multiselect(restoredExpressions.toArray(Expression<?>[]::new)).getParameters());
        assertEquals("source", restoredCase.getAlias());
        Case<Number> unknown = cb.selectCase(Number.class).when(cb.literal(false), (Number) null);
        unknown.otherwise((Number) null);
        Expression<Number> quotient = cb.quot(unknown, cb.literal(2));
        assertEquals(Number.class, quotient.getJavaType());
        unknown.otherwise(3);
        assertEquals(Number.class, quotient.getJavaType());
        assertEquals(Number.class, ((FunctionExpression) ((InternalSelection) quotient).getCurrentNode()).getResultType());
    }

    @Test
    void temporalOperationsKeepDerivedSourcesAndMutableCaseSelectorsIndependent() {
        ParameterExpression<Integer> selector = builder.parameter(Integer.class, "selector");
        var selectorNode = (org.eclipse.persistence.internal.expressions.ParameterExpression)
                ((InternalSelection) selector).getCurrentNode();
        var originalBase = selectorNode.getLocalBase();
        SimpleCase<Integer, LocalTime> choice = builder.selectCase(selector, LocalTime.class);
        choice.when(1, LocalTime.NOON).otherwise(LocalTime.MIDNIGHT);
        Expression<LocalTime> derived = builder.coalesce(choice, builder.literal(LocalTime.NOON));
        ParameterExpression<LocalTime> value = builder.parameter(LocalTime.class, "clock");
        var valueNode = (org.eclipse.persistence.internal.expressions.ParameterExpression)
                ((InternalSelection) value).getCurrentNode();
        var valueBase = valueNode.getLocalBase();
        for (Expression<?> operation : List.of(builder.equal(derived, derived), builder.lessThan(derived, choice),
                builder.lessThan(choice, derived), builder.nullif(derived, choice), builder.nullif(choice, derived),
                builder.between(derived, choice, value), builder.equal(value, choice))) {
            assertSame(originalBase, selectorNode.getLocalBase());
            assertSame(valueBase, valueNode.getLocalBase());
            assertNotNull(((InternalSelection) operation).getCurrentNode().getBuilder());
        }
        choice.when(2, LocalTime.of(13, 0));
        FunctionExpression coalesce = (FunctionExpression) ((InternalSelection) derived).getCurrentNode();
        FunctionExpression context = (FunctionExpression) coalesce.getChildren().getFirst();
        assertSame(((InternalSelection) choice).getCurrentNode(), context.getChildren().getFirst());
        assertSame(originalBase, selectorNode.getLocalBase());
    }

    @Test
    void temporalLiteralRetainsTypeAndRejectsNull() {
        LocalDateTime value = LocalDateTime.of(2021, 1, 1, 12, 34, 56, 125000000);
        assertEquals(LocalDateTime.class, builder.temporalLiteral(value).getJavaType());
        assertThrows(IllegalArgumentException.class, () -> builder.temporalLiteral(null));
    }

    @Test
    void extractUsesFieldResultType() {
        Expression<LocalDateTime> value = builder.literal(LocalDateTime.of(2021, 1, 1, 12, 34));
        assertEquals(Integer.class, builder.extract(LocalDateTimeField.YEAR, value).getJavaType());
        assertEquals(Double.class, builder.extract(LocalDateTimeField.SECOND, value).getJavaType());
    }

    @Test
    void allTypedCaseFactoriesRetainContextForNullArms() {
        Expression<Integer> value = builder.literal(1);
        assertEquals(String.class, value.selectCase(String.class).when(1, (String) null).otherwise("else").getJavaType());
        assertEquals(String.class, builder.selectCase(value, String.class).when(1, "then").otherwise((String) null).getJavaType());
        assertEquals(String.class, builder.selectCase(String.class).when(builder.equal(value, 1), "then").otherwise((String) null).getJavaType());
    }

    @Test
    void allExtractFieldsCarryIndependentCoreResultTypes() {
        Expression<LocalDateTime> source = builder.literal(LocalDateTime.of(2021, 1, 1, 12, 34));
        source.alias("stamp");
        Object node = ((InternalSelection) source).getCurrentNode();
        for (LocalDateTimeField<Integer> field : List.of(LocalDateTimeField.YEAR, LocalDateTimeField.QUARTER,
                LocalDateTimeField.MONTH, LocalDateTimeField.WEEK, LocalDateTimeField.DAY,
                LocalDateTimeField.HOUR, LocalDateTimeField.MINUTE)) {
            Expression<Integer> result = builder.extract(field, source);
            assertEquals(Integer.class, result.getJavaType());
            FunctionExpression function = (FunctionExpression) ((InternalSelection) result).getCurrentNode();
            assertEquals(Integer.class, function.getResultType());
            assertEquals(Integer.class, ((FunctionExpression) function.clone()).getResultType());
            assertEquals(Integer.class, ((FunctionExpression) function.rebuildOn(new ExpressionBuilder())).getResultType());
        }
        assertEquals(LocalDate.class, builder.extract(LocalDateTimeField.DATE, source).getJavaType());
        assertEquals(LocalTime.class, builder.extract(LocalDateTimeField.TIME, source).getJavaType());
        assertEquals(Integer.class, builder.temporalLiteral(LocalDate.of(2021, 1, 1)).extract(LocalDateField.YEAR).getJavaType());
        assertEquals(Double.class, builder.temporalLiteral(LocalTime.NOON).extract(LocalTimeField.SECOND).getJavaType());
        assertSame(node, ((InternalSelection) source).getCurrentNode());
        assertEquals(LocalDateTime.class, source.getJavaType());
        assertEquals("stamp", source.getAlias());
    }

    @Test
    void temporalChainsKeepParameterIdentityAndSourceAliases() {
        ParameterExpression<LocalDate> parameter = builder.parameter(LocalDate.class, "date");
        parameter.alias("source");
        TemporalExpression<LocalDate> temporal = new TemporalExpressionImpl<>((ExpressionImpl<LocalDate>) parameter);
        Object node = ((InternalSelection) parameter).getCurrentNode();
        for (TemporalExpression<LocalDate> result : List.of(temporal.coalesce((LocalDate) null),
                temporal.nullif((LocalDate) null), temporal.coalesce(builder.nullLiteral(LocalDate.class)),
                temporal.nullif(builder.nullLiteral(LocalDate.class)))) {
            assertNotSame(temporal, result);
            assertEquals(LocalDate.class, result.getJavaType());
            CriteriaQuery<Integer> query = builder.createQuery(Integer.class).select(result.extract(LocalDateField.YEAR).plus(1));
            assertEquals(Set.of(parameter), query.getParameters());
            assertSame(parameter, query.getParameters().iterator().next());
            result.alias("derived");
            assertEquals("source", parameter.getAlias());
        }
        assertEquals(LocalDate.class, temporal.min().getJavaType());
        assertEquals(LocalDate.class, temporal.max().getJavaType());
        assertSame(node, ((InternalSelection) parameter).getCurrentNode());
        assertTrue(((InternalExpression) builder.temporalLiteral(LocalDate.of(2021, 1, 1))).isLiteral());
    }

    @Test
    void typedNumericCasesPromoteAllStandardTypesAndPrimitiveOperands() {
        List<Number> values = List.of((byte) 1, (short) 2, 3, 4L, BigInteger.TEN, BigDecimal.ONE, 5F, 6D);
        List<Class<?>> types = List.of(Integer.class, Integer.class, Integer.class, Long.class,
                BigInteger.class, BigDecimal.class, Float.class, Double.class);
        Case<Number> expression = builder.selectCase(Number.class);
        for (int i = 0; i < values.size(); i++) {
            expression.when(builder.literal(true), values.get(i));
            assertEquals(types.get(i), expression.getJavaType());
        }
        assertEquals(Double.class, expression.otherwise((Number) null).getJavaType());
        for (Class<?> primitive : List.of(double.class, float.class, long.class)) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            ParameterExpression<? extends Number> parameter = builder.parameter((Class) primitive, "value");
            Expression<Number> result = builder.selectCase(Number.class).when(builder.literal(true), parameter).otherwise(1);
            assertEquals(primitive == double.class ? Double.class : primitive == float.class ? Float.class : Long.class,
                    result.getJavaType());
        }
    }

    @Test
    void contextualNullsAndExplicitTypedNullsHaveDifferentInferenceEvidence() {
        Case<Number> expression = builder.selectCase(Number.class).when(builder.literal(true), (Number) null);
        assertEquals(Number.class, expression.getJavaType());
        expression.when(builder.literal(false), 1);
        assertEquals(Integer.class, expression.getJavaType());
        assertEquals(Integer.class, expression.otherwise((Number) null).getJavaType());
        assertEquals(Double.class, expression.otherwise(builder.nullLiteral(Double.class)).getJavaType());
        assertEquals(Long.class, expression.otherwise(builder.literal(2L)).getJavaType());
        assertEquals(Integer.class, expression.otherwise((Number) null).getJavaType());
    }

    @Test
    void allSimpleCaseOverloadsPreserveParametersAndElseReplacement() {
        ParameterExpression<Integer> selector = builder.parameter(Integer.class, "selector");
        ParameterExpression<Integer> condition = builder.parameter(Integer.class, "condition");
        ParameterExpression<String> result = builder.parameter(String.class, "result");
        ParameterExpression<String> replaced = builder.parameter(String.class, "replaced");
        SimpleCase<Integer, String> expression = selector.selectCase(String.class);
        assertSame(selector, expression.getExpression());
        expression.when(1, "one").when(2, result).when(condition, "three").when(builder.literal(4), result);
        expression.otherwise(replaced);
        expression.otherwise("last");
        CriteriaQuery<String> query = builder.createQuery(String.class).select(expression);
        assertEquals(Set.of(selector, condition, result), query.getParameters());
        assertEquals(String.class, expression.getJavaType());
    }

    @Test
    void nestedCasesRecomputeTypesWithoutChangingOperands() {
        Case<Number> inner = builder.selectCase(Number.class).when(builder.literal(true), 1);
        Case<Number> outer = builder.selectCase(Number.class).when(builder.literal(true), inner);
        assertEquals(Integer.class, outer.getJavaType());
        inner.otherwise(2.5d);
        assertEquals(Double.class, outer.getJavaType());
        assertEquals(Double.class, ((FunctionExpression) ((InternalSelection) outer).getCurrentNode()).getResultType());
        assertEquals(Double.class, inner.getJavaType());
        Case<Object> polymorphic = builder.selectCase(Object.class).when(builder.literal(true), "text");
        assertEquals(String.class, polymorphic.getJavaType());
        polymorphic.otherwise(new StringBuilder("builder"));
        assertEquals(Object.class, polymorphic.getJavaType());
        polymorphic.otherwise((Object) null);
        assertEquals(String.class, polymorphic.getJavaType());
    }

    @Test
    void caseConstructionPreservesReusableSelectorAndResultAncestry() {
        Expression<Integer> literal = builder.literal(1);
        ParameterExpression<Integer> parameter = builder.parameter(Integer.class, "selector");
        Expression<Integer> function = builder.sum(parameter, 1);
        Expression<String> mapped = builder.fromExpression(new ExpressionBuilder().get("text"), String.class);
        for (Expression<Integer> selector : List.of(literal, parameter, function)) {
            org.eclipse.persistence.expressions.Expression node = ((InternalSelection) selector).getCurrentNode();
            Object originalBuilder = node.getBuilder();
            Expression<String> result = selector.selectCase(String.class).when(builder.literal(1), mapped).otherwise("other");
            assertSame(originalBuilder, node.getBuilder());
            assertSame(node, ((InternalSelection) selector).getCurrentNode());
            assertEquals(String.class, result.getJavaType());
            ((InternalSelection) result).getCurrentNode().clone().rebuildOn(new ExpressionBuilder()).getBuilder();
        }
        ParameterExpression<Boolean> condition = builder.parameter(Boolean.class, "condition");
        Object conditionBuilder = ((InternalSelection) condition).getCurrentNode().getBuilder();
        Expression<String> searched = builder.selectCase(String.class).when(condition, mapped).otherwise("other");
        assertSame(conditionBuilder, ((InternalSelection) condition).getCurrentNode().getBuilder());
        ((InternalSelection) searched).getCurrentNode().getBuilder();
        assertEquals(Set.of(condition), builder.createQuery(String.class).select(searched).getParameters());
    }

    @Test
    void inheritedPlatformsKeepIsoWeekSqlAndRepeatedOperandOrder() {
        for (DatabasePlatform platform : List.of(new H2Platform(), new DerbyPlatform(), new JavaDBPlatform(),
                new DB2Platform(), new DB2ZPlatform(), new DB2MainframePlatform(), new MySQLPlatform(), new MariaDBPlatform())) {
            DatabaseLogin login = new DatabaseLogin();
            login.setPlatform(platform);
            DatabaseSessionImpl session = new DatabaseSessionImpl(new Project(login));
            ExpressionBuilder base = new ExpressionBuilder();
            base.setSession(session);
            org.eclipse.persistence.expressions.Expression value = base.getParameter("date", LocalDate.class);
            FunctionExpression extract = (FunctionExpression) value.extract("week");
            extract.setResultType(Integer.class);
            SQLSelectStatement statement = new SQLSelectStatement();
            statement.setBuilder(base);
            statement.addField(extract);
            statement.addTable(new DatabaseTable("ITEM"));
            statement.computeTablesFromTables();
            DatabaseCall call = statement.buildCall(session, null);
            String sql = call.getSQLString();
            if (platform instanceof DerbyPlatform) {
                assertFalse(sql.contains("TIMESTAMPDIFF"));
                assertTrue(sql.contains("MOD(YEAR(") || sql.contains("MOD((YEAR("));
                assertTrue(call.getParameters().size() > 1);
                assertEquals(call.getParameters().size(), sql.chars().filter(character -> character == '?').count());
            } else {
                assertTrue(sql.contains(platform instanceof H2Platform ? "ISO_WEEK"
                        : platform instanceof MySQLPlatform ? ", 3)" : "WEEK_ISO"));
                assertEquals(1, call.getParameters().size());
            }
            assertSame(base, value.getBuilder());
        }
    }

    @Test
    void immutableTemporalContextSurvivesCoreLifecycle() throws Exception {
        FunctionExpression node = (FunctionExpression) ((InternalSelection) builder.temporalLiteral(
                LocalDateTime.of(2021, 1, 1, 12, 34, 56, 123456789))).getCurrentNode();
        ExpressionOperator operator = node.getOperator();
        ExpressionOperator clone = operator.clone();
        assertNotSame(operator, clone);
        assertEquals(operator, clone);
        assertEquals(operator.hashCode(), clone.hashCode());
        operator.copyTo(clone);
        assertThrows(IllegalArgumentException.class, () -> operator.copyTo(new ExpressionOperator()));
        FunctionExpression date = (FunctionExpression) ((InternalSelection) builder.temporalLiteral(LocalDate.of(2021, 1, 1))).getCurrentNode();
        assertNotEquals(operator, date.getOperator());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(node);
        }
        FunctionExpression restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (FunctionExpression) input.readObject();
        }
        ExpressionBuilder base = new ExpressionBuilder();
        for (FunctionExpression copy : List.of((FunctionExpression) node.clone(),
                (FunctionExpression) node.rebuildOn(base),
                (FunctionExpression) node.twistedForBaseAndContext(base, base, node.getBuilder()), restored)) {
            assertEquals(LocalDateTime.class, copy.getResultType());
            assertEquals(operator, copy.getOperator());
            java.io.StringWriter java = new java.io.StringWriter();
            copy.printJava(new org.eclipse.persistence.internal.expressions.ExpressionJavaPrinter("builder", java, new H2Platform()));
            assertFalse(java.toString().isEmpty());
        }
    }

    @Test
    void outerExtractAndTypedCaseRetainTypesWhenTwisted() {
        TemporalExpression<LocalDateTime> temporal = builder.temporalLiteral(LocalDateTime.of(2021, 1, 1, 12, 34, 56, 125000000));
        List<Expression<?>> expressions = List.of(temporal.extract(LocalDateTimeField.YEAR),
                temporal.extract(LocalDateTimeField.SECOND),
                builder.selectCase(String.class).when(builder.literal(true), "yes").otherwise("no"),
                builder.selectCase(builder.literal(1), Number.class).when(1, 2).otherwise(3.5d));
        assertAll(expressions.stream().map(expression -> () -> {
            Class<?> expectedType = expression.getJavaType();
            FunctionExpression node = (FunctionExpression) ((InternalSelection) expression).getCurrentNode();
            List<org.eclipse.persistence.expressions.Expression> originalChildren = List.copyOf(node.getChildren());
            ExpressionBuilder base = new ExpressionBuilder();
            FunctionExpression twisted = (FunctionExpression) node.twistedForBaseAndContext(base, base, node.getBuilder());
            assertNotSame(node, twisted);
            assertEquals(expectedType, node.getResultType(), "Original " + expectedType);
            assertEquals(expectedType, twisted.getResultType(), "Twisted " + expectedType);
            for (int index = 0; index < originalChildren.size(); index++) {
                assertSame(originalChildren.get(index), node.getChildren().get(index));
            }
        }));
        ExpressionBuilder base = new ExpressionBuilder();
        FunctionExpression untyped = (FunctionExpression) base.value("unchanged").toUpperCase();
        assertNull(untyped.getResultType());
        assertNull(((FunctionExpression) untyped.twistedForBaseAndContext(base, base, base)).getResultType());
        FunctionExpression empty = new FunctionExpression();
        empty.setResultType(LocalTime.class);
        FunctionExpression cloned = (FunctionExpression) empty.twistedForBaseAndContext(base, base, base);
        assertNotSame(empty, cloned);
        assertEquals(LocalTime.class, cloned.getResultType());
    }

    @Test
    void legacyFactoriesKeepTheirExistingTypeInference() {
        Expression<String> simple = builder.<Integer, String>selectCase(builder.literal(1)).when(1, "one").otherwise("other");
        Expression<Long> searched = builder.<Long>selectCase().when(builder.literal(true), 1L).otherwise(2L);
        assertEquals(String.class, simple.getJavaType());
        assertEquals(Long.class, searched.getJavaType());
        assertInstanceOf(FunctionExpressionImpl.class, simple);
    }
}
