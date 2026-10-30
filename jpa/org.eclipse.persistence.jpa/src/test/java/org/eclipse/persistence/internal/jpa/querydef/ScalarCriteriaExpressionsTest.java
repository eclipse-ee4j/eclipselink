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
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.persistence.criteria.ComparableExpression;
import jakarta.persistence.criteria.CriteriaBuilder.Trimspec;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.NumericExpression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.TextExpression;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScalarCriteriaExpressionsTest {

    private final CriteriaBuilderImpl builder = new CriteriaBuilderImpl(new MetamodelImpl(new DatabaseSessionImpl(new Project(new DatabaseLogin()))));

    @Test
    void literalsRejectNullAndRetainValueTypeAndLiteralCategory() {
        assertThrows(IllegalArgumentException.class, () -> builder.numericLiteral(null));
        assertThrows(IllegalArgumentException.class, () -> builder.stringLiteral(null));
        NumericExpression<Integer> number = builder.numericLiteral(13);
        TextExpression text = builder.stringLiteral("_%[literal]");
        assertEquals(Integer.class, number.getJavaType());
        assertEquals(String.class, text.getJavaType());
        assertTrue(((InternalExpression) number).isLiteral());
        assertTrue(((InternalExpression) text).isLiteral());
        assertEquals(13, ((ExpressionImpl<?>) number).literal);
        assertEquals("_%[literal]", ((ExpressionImpl<?>) text).literal);
        assertInstanceOf(NumericExpression.class, number.plus(2).abs());
        assertInstanceOf(TextExpression.class, text.upper().append("!"));
    }

    static Stream<Number> literalTypes() {
        return Stream.of((byte) 1, (short) 2, 3, 4L, 5.5F, 6.5D,
                BigInteger.valueOf(7), new BigDecimal("8.5"));
    }

    @ParameterizedTest
    @MethodSource("literalTypes")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void allStandardNumericLiteralTypesRemainSpecialized(Number value) {
        NumericExpression<?> expression = builder.numericLiteral((Number & Comparable) value);
        assertEquals(value.getClass(), expression.getJavaType());
        assertInstanceOf(NumericExpression.class, expression.abs());
        assertEquals(value, ((ExpressionImpl<?>) expression).literal);
    }

    @Test
    void conversionsAreIndependentSelectionsWithRequestedTypes() {
        NumericExpression<Integer> source = builder.numericLiteral(12);
        source.alias("source");
        org.eclipse.persistence.expressions.Expression originalNode = ((InternalSelection) source).getCurrentNode();
        ExpressionBuilder originalBuilder = originalNode.getBuilder();
        List<NumericExpression<?>> converted = List.of(source.toLong(), source.toInteger(), source.toFloat(),
                source.toDouble(), source.toBigDecimal(), source.toBigInteger());
        List<Class<?>> types = List.of(Long.class, Integer.class, Float.class, Double.class,
                BigDecimal.class, BigInteger.class);
        for (int i = 0; i < converted.size(); i++) {
            NumericExpression<?> result = converted.get(i);
            assertNotSame(source, result);
            assertEquals(types.get(i), result.getJavaType());
            assertEquals("source", result.getAlias());
            result.alias("converted" + i);
            assertEquals("source", source.getAlias());
            assertEquals(Integer.class, source.getJavaType());
            assertSame(originalNode, ((InternalSelection) source).getCurrentNode());
            assertSame(originalBuilder, originalNode.getBuilder());
            FunctionExpression node = assertInstanceOf(FunctionExpression.class, ((InternalSelection) result).getCurrentNode());
            assertNotSame(originalNode, node);
            assertSame(originalNode, node.getChildren().get(0));
            assertEquals(types.get(i), node.getResultType());
            FunctionExpression copy = assertInstanceOf(FunctionExpression.class, node.clone());
            assertEquals(types.get(i), copy.getResultType());
            ExpressionBuilder rebuiltBase = new ExpressionBuilder();
            FunctionExpression rebuilt = assertInstanceOf(FunctionExpression.class, node.rebuildOn(rebuiltBase));
            assertEquals(types.get(i), rebuilt.getResultType());
            assertSame(rebuiltBase, rebuilt.getBuilder());
            assertInstanceOf(NumericExpression.class, result.abs());
        }
    }

    @Test
    void heterogeneousComparisonsRetainOriginalParameterIdentityAndIndependentNumericType() {
        NumericExpression<Integer> number = builder.numericLiteral(6);
        ParameterExpression<Double> upper = builder.parameter(Double.class, "fractionalUpper");
        org.eclipse.persistence.expressions.Expression parameterNode = ((InternalSelection) upper).getCurrentNode();
        ExpressionBuilder parameterBuilder = parameterNode.getBuilder();
        CriteriaQuery<Object> query = builder.createQuery();
        Predicate comparison = number.lt(upper);
        query.where(comparison);
        Expression<?> operand = ((CompoundExpressionImpl) comparison).getChildExpressions().get(1);
        assertNotSame(upper, operand);
        assertEquals(Double.class, operand.getJavaType());
        assertEquals(Double.class, ((FunctionExpression) ((InternalSelection) operand).getCurrentNode()).getResultType());
        assertSame(upper, query.getParameters().iterator().next());
        assertSame(parameterNode, ((InternalSelection) upper).getCurrentNode());
        assertSame(parameterBuilder, parameterNode.getBuilder());
        assertEquals(Double.class, upper.getParameterType());
    }

    static Stream<Arguments> primitiveNumericComparisonTypes() {
        return Stream.of(new Class<?>[]{byte.class, Byte.class}, new Class<?>[]{short.class, Short.class},
                new Class<?>[]{int.class, Integer.class}, new Class<?>[]{long.class, Long.class},
                new Class<?>[]{float.class, Float.class}, new Class<?>[]{double.class, Double.class})
                .flatMap(pair -> Stream.of(Arguments.of(pair[0], pair[1]), Arguments.of(pair[1], pair[0]),
                        Arguments.of(pair[0], pair[0]), Arguments.of(pair[1], pair[1])));
    }

    @ParameterizedTest
    @MethodSource("primitiveNumericComparisonTypes")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void primitiveAndBoxedComparisonsRetainOriginalOperands(Class<?> sourceType, Class<?> operandType) {
        ParameterExpression source = builder.parameter(sourceType, "source");
        ParameterExpression operand = builder.parameter(operandType, "operand");
        source.alias("sourceAlias");
        org.eclipse.persistence.expressions.Expression sourceNode = ((InternalSelection) source).getCurrentNode();
        org.eclipse.persistence.expressions.Expression operandNode = ((InternalSelection) operand).getCurrentNode();
        NumericExpression number = new NumericExpressionImpl<>((ExpressionImpl) source);
        for (Predicate comparison : List.of(number.gt(operand), number.ge(operand), number.lt(operand), number.le(operand))) {
            assertSame(operand, ((CompoundExpressionImpl) comparison).getChildExpressions().get(1));
            CriteriaQuery<Object> query = builder.createQuery().where(comparison);
            assertEquals(2, query.getParameters().size());
            assertTrue(query.getParameters().stream().anyMatch(parameter -> parameter == source));
            assertTrue(query.getParameters().stream().anyMatch(parameter -> parameter == operand));
            assertSame(sourceNode, ((InternalSelection) source).getCurrentNode());
            assertSame(operandNode, ((InternalSelection) operand).getCurrentNode());
            assertEquals(sourceType, source.getJavaType());
            assertEquals(operandType, operand.getJavaType());
            assertEquals("sourceAlias", source.getAlias());
        }
    }

    @Test
    void fixedNumericResultsAndCovariantNullOperationsHaveTheirDeclaredTypes() {
        NumericExpression<BigDecimal> source = builder.numericLiteral(new BigDecimal("12.5"));
        assertEquals(Integer.class, source.sign().getJavaType());
        for (NumericExpression<?> expression : List.of(source.sqrt(), source.exp(), source.ln(),
                source.power(2), source.power(builder.literal(2)), source.avg(), source.sumAsDouble())) {
            assertEquals(Double.class, expression.getJavaType());
        }
        assertEquals(Long.class, source.sumAsLong().getJavaType());
        assertEquals(Long.class, source.count().getJavaType());
        assertEquals(Long.class, source.countDistinct().getJavaType());
        for (NumericExpression<?> expression : List.of(source.sum(), source.min(), source.max(),
                source.coalesce((BigDecimal) null), source.nullif((BigDecimal) null),
                source.coalesce(builder.nullLiteral(BigDecimal.class)),
                source.nullif(builder.nullLiteral(BigDecimal.class)))) {
            assertEquals(BigDecimal.class, expression.getJavaType());
        }
        TextExpression text = builder.stringLiteral("text");
        assertInstanceOf(TextExpression.class, text.coalesce((String) null));
        assertInstanceOf(TextExpression.class, text.nullif((String) null));
        assertInstanceOf(TextExpression.class, text.coalesce(builder.nullLiteral(String.class)));
        assertInstanceOf(TextExpression.class, text.nullif(builder.nullLiteral(String.class)));
    }

    @Test
    void ordinaryCoalescePreservesLiteralBuilderAndSupportsRepeatedSourceUse() {
        Expression<Integer> source = builder.literal(12);
        Object originalBuilder = ((InternalSelection) source).getCurrentNode().getBuilder();
        for (Expression<Integer> coalesce : List.of(builder.coalesce(source, builder.literal(3)),
                builder.coalesce(source, 4), builder.<Integer>coalesce().value(source).value(5))) {
            assertSame(originalBuilder, ((InternalSelection) source).getCurrentNode().getBuilder());
            assertSame(originalBuilder, ((InternalSelection) coalesce).getCurrentNode().getBuilder());
            assertEquals(Integer.class, builder.sum(source, 1).getJavaType());
        }
    }

    @Test
    void knownNullIdentitiesPreserveOriginalParametersAndIndependentAliases() {
        ParameterExpression<Integer> source = builder.parameter(Integer.class, "value");
        source.alias("original");
        Expression<Integer> absent = builder.nullLiteral(Integer.class);
        for (Expression<Integer> expression : List.of(builder.coalesce(source, absent),
                builder.coalesce(absent, source), builder.coalesce(source, (Integer) null),
                builder.nullif(source, absent), builder.nullif(source, (Integer) null),
                builder.<Integer>coalesce().value(absent).value(source).value(absent))) {
            assertNotSame(source, expression);
            CriteriaQuery<Integer> query = builder.createQuery(Integer.class).select(expression);
            assertEquals(1, query.getParameters().size());
            assertSame(source, query.getParameters().iterator().next());
            expression.alias("derived");
            assertEquals("original", source.getAlias());
            assertEquals(Integer.class, source.getParameterType());
            org.eclipse.persistence.expressions.Expression node = ((InternalSelection) expression).getCurrentNode();
            node.clone().rebuildOn(new ExpressionBuilder()).getBuilder();
        }
    }

    @Test
    void coalesceCopiesDerivedPathsFieldsAndTablesIntoReplacementContext() {
        for (String kind : List.of("path", "field", "table", "anyOf")) {
            for (boolean rebuild : List.of(false, true)) {
                ExpressionBuilder primary = new ExpressionBuilder(Object.class);
                org.eclipse.persistence.expressions.Expression operand = switch (kind) {
                    case "field" -> primary.getField("ITEM.VALUE");
                    case "table" -> primary.getTable("EXTRA").getField("VALUE");
                    case "anyOf" -> primary.anyOf("items").get("value");
                    default -> primary.get("child");
                };
                FunctionExpression original = (FunctionExpression) builder.toExpression(
                        builder.coalesce(builder.fromExpression(operand, Integer.class), builder.literal(0)));
                ExpressionBuilder replacement = new ExpressionBuilder();
                org.eclipse.persistence.expressions.Expression existingPath = replacement.get("existing");
                org.eclipse.persistence.expressions.Expression existingField = replacement.getField("OTHER.VALUE");
                org.eclipse.persistence.expressions.Expression existingTable = replacement.getTable("OTHER_TABLE");
                FunctionExpression copy = (FunctionExpression) (rebuild
                        ? original.rebuildOn(replacement) : original.cloneUsing(replacement));
                org.eclipse.persistence.expressions.Expression copiedOperand = switch (kind) {
                    case "field" -> replacement.getField("ITEM.VALUE");
                    case "table" -> replacement.getTable("EXTRA").getField("VALUE");
                    case "anyOf" -> replacement.anyOf("items", false).get("value");
                    default -> replacement.get("child");
                };
                assertSame(copiedOperand, copy.getChildren().get(0));
                assertSame(replacement, copy.getBuilder());
                assertSame(replacement, copiedOperand.getBuilder());
                assertNotSame(operand, copiedOperand);
                if (kind.equals("anyOf")) {
                    assertNotSame(copiedOperand, replacement.anyOf("items").get("value"));
                }
                assertSame(existingPath, replacement.get("existing"));
                assertSame(existingField, replacement.getField("OTHER.VALUE"));
                assertSame(existingTable, replacement.getTable("OTHER_TABLE"));
                assertSame(operand, original.getChildren().get(0));
                assertSame(primary, operand.getBuilder());
            }
        }
    }

    @Test
    void coalesceWithLeadingParameterAndCaseRetainsRootAndCopiesContextGraph() {
        ExpressionBuilder root = new ExpressionBuilder(Object.class);
        Expression<Integer> selector = builder.fromExpression(root.getField("ITEM.KIND"), Integer.class);
        ParameterExpression<String> leading = builder.parameter(String.class, "leading");
        ParameterExpression<String> branch = builder.parameter(String.class, "branch");
        ParameterExpression<String> trailing = builder.parameter(String.class, "trailing");
        Expression<String> selected = builder.<Integer, String>selectCase(selector)
                .when(1, branch).otherwise(builder.nullLiteral(String.class));
        Expression<String> expression = builder.<String>coalesce().value(leading).value(selected).value(trailing);
        FunctionExpression node = (FunctionExpression) ((InternalSelection) expression).getCurrentNode();
        node.setResultType(String.class);
        org.eclipse.persistence.internal.expressions.ParameterExpression originalLeading =
                (org.eclipse.persistence.internal.expressions.ParameterExpression) ((InternalSelection) leading).getCurrentNode();
        org.eclipse.persistence.expressions.Expression originalContext = originalLeading.getLocalBase();
        List<org.eclipse.persistence.expressions.Expression> originalChildren = List.copyOf(node.getChildren());
        assertSame(root, node.getBuilder());
        assertEquals(Set.of(leading, branch, trailing), builder.createQuery(String.class).select(expression).getParameters());
        for (ExpressionBuilder replacement : List.of(new ExpressionBuilder(), new ExpressionBuilder(Object.class))) {
            for (FunctionExpression input : List.of(node, (FunctionExpression) node.clone())) {
                for (org.eclipse.persistence.expressions.Expression result : List.of(input.rebuildOn(replacement),
                        input.cloneUsing(replacement))) {
                    FunctionExpression copy = assertInstanceOf(FunctionExpression.class, result);
                    assertSame(replacement, copy.getBuilder());
                    assertSame(replacement, copy.getChildren().get(1).getBuilder());
                    assertEquals(String.class, copy.getResultType());
                    assertNotSame(input.getOperator(), copy.getOperator());
                    assertEquals(3, copy.getChildren().size());
                    FunctionExpression cloned = (FunctionExpression) copy.clone();
                    assertNotSame(replacement, cloned.getBuilder());
                    assertSame(cloned.getBuilder(), cloned.getChildren().get(1).getBuilder());
                    DatabaseSessionImpl session = new DatabaseSessionImpl(new Project(new DatabaseLogin()));
                    replacement.setSession(session);
                    SQLSelectStatement statement = new SQLSelectStatement();
                    statement.setBuilder(replacement);
                    statement.addField(copy);
                    statement.addTable(new DatabaseTable("ITEM"));
                    statement.computeTablesFromTables();
                    DatabaseCall call = statement.buildCall(session, null);
                    assertTrue(call.getSQLString().startsWith("SELECT COALESCE("));
                    assertTrue(call.getSQLString().contains("CASE "));
                    assertTrue(call.getSQLString().endsWith(" FROM ITEM"));
                }
            }
        }
        assertEquals(originalChildren, node.getChildren());
        assertSame(originalContext, originalLeading.getLocalBase());
        assertSame(root, node.getBuilder());
    }

    @Test
    void coalesceCopiesPreserveIndependentRootOperands() {
        ExpressionBuilder first = new ExpressionBuilder(Object.class);
        ExpressionBuilder second = new ExpressionBuilder(Object.class);
        Expression<Integer> left = builder.abs(builder.fromExpression(first.getField("ITEM.LEFT_VALUE"), Integer.class));
        Expression<Integer> right = builder.abs(builder.fromExpression(second.getField("ITEM.RIGHT_VALUE"), Integer.class));
        Expression<Integer> expression = builder.<Integer>coalesce()
                .value(builder.parameter(Integer.class, "leading")).value(left).value(right);
        FunctionExpression source = (FunctionExpression) ((InternalSelection) expression).getCurrentNode();
        assertSame(first, source.getBuilder());
        ExpressionBuilder replacement = new ExpressionBuilder();
        for (org.eclipse.persistence.expressions.Expression result : List.of(source.rebuildOn(replacement),
                source.cloneUsing(replacement))) {
            FunctionExpression copy = (FunctionExpression) result;
            assertSame(replacement, copy.getBuilder());
            assertSame(replacement, copy.getChildren().get(1).getBuilder());
            assertNotSame(replacement, copy.getChildren().get(2).getBuilder());
            assertNotSame(second, copy.getChildren().get(2).getBuilder());
            FunctionExpression clone = (FunctionExpression) copy.clone();
            assertSame(clone.getBuilder(), clone.getChildren().get(1).getBuilder());
            assertNotSame(clone.getBuilder(), clone.getChildren().get(2).getBuilder());
        }
        assertSame(first, source.getBuilder());
        assertSame(first, ((InternalSelection) left).getCurrentNode().getBuilder());
        assertSame(second, ((InternalSelection) right).getCurrentNode().getBuilder());
    }

    @Test
    void rebuiltCoalesceRetainsSingleOperandSqlAndResultType() {
        Expression<Integer> absent = builder.nullLiteral(Integer.class);
        Expression<Integer> value = builder.literal(7);
        for (Expression<Integer> expression : List.of(builder.coalesce(absent, value),
                builder.coalesce(value, absent), builder.coalesce(value, (Integer) null),
                builder.<Integer>coalesce().value(absent).value(value).value(absent))) {
            FunctionExpression node = (FunctionExpression) ((InternalSelection) expression).getCurrentNode();
            node.setResultType(Integer.class);
            DatabaseSessionImpl session = new DatabaseSessionImpl(new Project(new DatabaseLogin()));
            ExpressionBuilder base = new ExpressionBuilder();
            base.setSession(session);
            FunctionExpression rebuilt = (FunctionExpression) node.clone().rebuildOn(base);
            assertEquals(Integer.class, rebuilt.getResultType());
            SQLSelectStatement statement = new SQLSelectStatement();
            statement.setBuilder(base);
            statement.addField(rebuilt);
            statement.addTable(new DatabaseTable("ITEM"));
            statement.computeTablesFromTables();
            DatabaseCall call = statement.buildCall(session, null);
            assertEquals("SELECT (?) FROM ITEM", call.getSQLString());
            assertEquals(List.of(7), call.getParameters());
            assertEquals(1, node.getChildren().size());
        }
    }

    @Test
    void twistedCoalesceRetainsSqlTypesBindingsAndOriginalOperands() {
        Expression<Integer> absent = builder.nullLiteral(Integer.class);
        Expression<Integer> value = builder.literal(7);
        value.alias("original");
        for (Expression<Integer> expression : List.of(builder.coalesce(absent, value),
                builder.coalesce(value, absent), builder.coalesce(value, (Integer) null),
                builder.<Integer>coalesce().value(absent).value(value).value(absent))) {
            FunctionExpression node = (FunctionExpression) ((InternalSelection) expression).getCurrentNode();
            node.setResultType(Integer.class);
            ConstantExpression originalChild = (ConstantExpression) node.getChildren().get(0);
            org.eclipse.persistence.expressions.Expression originalContext = originalChild.getLocalBase();
            List<Expression<?>> originalOperands = List.copyOf(((FunctionExpressionImpl<?>) expression).getChildExpressions());
            DatabaseSessionImpl session = new DatabaseSessionImpl(new Project(new DatabaseLogin()));
            ExpressionBuilder base = new ExpressionBuilder();
            base.setSession(session);
            for (FunctionExpression input : List.of(node, (FunctionExpression) node.clone())) {
                FunctionExpression twisted = (FunctionExpression) input.twistedForBaseAndContext(base, base, input.getBuilder());
                assertEquals(Integer.class, twisted.getResultType());
                SQLSelectStatement statement = new SQLSelectStatement();
                statement.setBuilder(base);
                statement.addField(twisted);
                statement.addTable(new DatabaseTable("ITEM"));
                statement.computeTablesFromTables();
                DatabaseCall call = statement.buildCall(session, null);
                assertEquals("SELECT (?) FROM ITEM", call.getSQLString());
                assertEquals(List.of(7), call.getParameters());
                assertEquals(1, node.getChildren().size());
                assertSame(originalChild, node.getChildren().get(0));
                assertSame(originalContext, originalChild.getLocalBase());
                assertEquals(7, originalChild.getValue());
                assertEquals(Integer.class, node.getResultType());
                assertEquals(originalOperands, ((FunctionExpressionImpl<?>) expression).getChildExpressions());
                assertEquals("original", value.getAlias());
            }
        }
    }

    @Test
    void allNullCoalesceRetainsOperandsAndCanAcceptAFurtherParameter() {
        Expression<Integer> first = builder.nullLiteral(Integer.class);
        Expression<Integer> second = builder.nullLiteral(Integer.class);
        Expression<Integer> binary = builder.coalesce(first, second);
        assertEquals(Integer.class, binary.getJavaType());
        assertEquals(List.of(first, second), ((FunctionExpressionImpl<?>) binary).getChildExpressions());
        assertTrue(builder.createQuery(Integer.class).select(binary).getParameters().isEmpty());
        jakarta.persistence.criteria.CriteriaBuilder.Coalesce<Integer> incremental =
                builder.<Integer>coalesce().value(first).value(second);
        assertEquals(List.of(first, second), ((FunctionExpressionImpl<?>) incremental).getChildExpressions());
        ParameterExpression<Integer> fallback = builder.parameter(Integer.class, "fallback");
        incremental.value(fallback);
        assertEquals(List.of(first, second, fallback), ((FunctionExpressionImpl<?>) incremental).getChildExpressions());
        assertSame(fallback, builder.createQuery(Integer.class).select(incremental).getParameters().iterator().next());
    }

    @Test
    void nullableNumericValuesAreSqlOperandsInsteadOfNullLiteralFactoryCalls() {
        NumericExpression<Integer> source = builder.numericLiteral(12);
        for (Expression<?> expression : List.of(source.plus((Integer) null), source.minus((Integer) null),
                source.times((Integer) null), source.dividedBy((Integer) null),
                source.subtractedFrom(null), source.dividedInto(null), source.power((Number) null),
                source.gt((Number) null), source.ge((Number) null), source.lt((Number) null),
                source.le((Number) null), source.greaterThan((Integer) null),
                source.greaterThanOrEqualTo((Integer) null), source.lessThan((Integer) null),
                source.lessThanOrEqualTo((Integer) null), source.between((Integer) null, null))) {
            assertInstanceOf(ExpressionImpl.class, expression);
        }
    }

    @Test
    void suffixPredicatesPreserveReusableLiteralAndParameterAncestry() {
        TextExpression literal = builder.stringLiteral("%_[x]\\middle\\[x]_%");
        ParameterExpression<String> parameter = builder.parameter(String.class, "suffix");
        TextExpression composed = literal.append(parameter);
        for (Predicate predicate : List.of(literal.endsWith("\\[x]_%"), literal.notEndsWith("[x]_"),
                composed.endsWith("_"), composed.notEndsWith("_"))) {
            org.eclipse.persistence.expressions.Expression node = ((InternalSelection) predicate).getCurrentNode();
            assertNotNull(node.clone().getBuilder());
            assertNotNull(node.clone().rebuildOn(new ExpressionBuilder()).getBuilder());
        }
        CriteriaQuery<Object> query = builder.createQuery();
        query.where(composed.endsWith("_"));
        assertEquals(Set.of(parameter), query.getParameters());
        assertSame(parameter, query.getParameters().iterator().next());
        assertNull(literal.getAlias());
        assertTrue(((ExpressionImpl<?>) literal).isLiteral());
        assertEquals("%_[x]\\middle\\[x]_%", ((ExpressionImpl<?>) literal).literal);
    }

    @Test
    void ordersPreserveDirectionAndNullPrecedenceWithoutMutatingTheExpression() {
        TextExpression text = builder.stringLiteral("text");
        assertTrue(text.asc().isAscending());
        assertFalse(text.desc().isAscending());
        assertEquals(Nulls.FIRST, text.asc(Nulls.FIRST).getNullPrecedence());
        assertEquals(Nulls.LAST, text.desc(Nulls.LAST).getNullPrecedence());
        assertSame(text, text.asc().getExpression());
        assertNull(text.getAlias());
    }

    static Stream<Arguments> nestedOperandCases() {
        CriteriaBuilderImpl builder = new CriteriaBuilderImpl(new MetamodelImpl(new DatabaseSessionImpl(new Project(new DatabaseLogin()))));
        NumericExpression<Integer> number = builder.numericLiteral(12);
        TextExpression text = builder.stringLiteral("text");
        ParameterExpression<Integer> offset = builder.parameter(Integer.class, "offset");
        ParameterExpression<Integer> bound = builder.parameter(Integer.class, "bound");
        ParameterExpression<String> pattern = builder.parameter(String.class, "pattern");
        ParameterExpression<String> replacement = builder.parameter(String.class, "replacement");
        ParameterExpression<Character> character = builder.parameter(Character.class, "character");
        return Stream.of(
                Arguments.of("numeric plus", number.plus(offset), Set.of(offset)),
                Arguments.of("numeric times", number.times(offset), Set.of(offset)),
                Arguments.of("numeric minus", number.minus(offset), Set.of(offset)),
                Arguments.of("numeric quotient", number.dividedBy(offset), Set.of(offset)),
                Arguments.of("numeric power", number.power(offset), Set.of(offset)),
                Arguments.of("numeric between", number.between(offset, bound), Set.of(offset, bound)),
                Arguments.of("value-first between", builder.between(12, offset, bound), Set.of(offset, bound)),
                Arguments.of("numeric coalesce", number.coalesce(offset), Set.of(offset)),
                Arguments.of("numeric nullif", number.nullif(offset), Set.of(offset)),
                Arguments.of("text append", text.append(pattern), Set.of(pattern)),
                Arguments.of("text prepend", text.prepend(pattern), Set.of(pattern)),
                Arguments.of("text substring", text.substring(offset, bound), Set.of(offset, bound)),
                Arguments.of("text suffix", text.substring(offset), Set.of(offset)),
                Arguments.of("text trim", text.trim(character), Set.of(character)),
                Arguments.of("text leading trim", text.trim(Trimspec.LEADING, character), Set.of(character)),
                Arguments.of("text trailing trim", text.trim(Trimspec.TRAILING, character), Set.of(character)),
                Arguments.of("text both trim", text.trim(Trimspec.BOTH, character), Set.of(character)),
                Arguments.of("text left", text.left(offset), Set.of(offset)),
                Arguments.of("text right", text.right(offset), Set.of(offset)),
                Arguments.of("replace two expressions", text.replace(pattern, replacement), Set.of(pattern, replacement)),
                Arguments.of("replace pattern", text.replace(pattern, "x"), Set.of(pattern)),
                Arguments.of("replace replacement", text.replace("x", replacement), Set.of(replacement)),
                Arguments.of("text locate", text.locate(pattern), Set.of(pattern)),
                Arguments.of("text locate offset", text.locate(pattern, offset), Set.of(pattern, offset)),
                Arguments.of("text like", text.like(pattern), Set.of(pattern)),
                Arguments.of("text escaped like", text.like(pattern, character), Set.of(pattern, character)),
                Arguments.of("text constant escaped like", text.like("%", character), Set.of(character)),
                Arguments.of("text not-like", text.notLike(pattern), Set.of(pattern)),
                Arguments.of("text escaped not-like", text.notLike(pattern, character), Set.of(pattern, character)),
                Arguments.of("text constant escaped not-like", text.notLike("%", character), Set.of(character)),
                Arguments.of("text coalesce", text.coalesce(pattern), Set.of(pattern)),
                Arguments.of("text nullif", text.nullif(pattern), Set.of(pattern)),
                Arguments.of("comparable max", text.coalesce(pattern).max(), Set.of(pattern)),
                Arguments.of("comparable min", text.coalesce(pattern).min(), Set.of(pattern)),
                Arguments.of("count", text.coalesce(pattern).count(), Set.of(pattern)),
                Arguments.of("distinct count", text.coalesce(pattern).countDistinct(), Set.of(pattern)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("nestedOperandCases")
    void queriesDiscoverTheOriginalParametersInEveryNestedOperand(String description,
            Expression<?> expression, Set<ParameterExpression<?>> expected) {
        CriteriaQuery<Object> query = builder.createQuery();
        query.select(expression);
        assertEquals(expected, query.getParameters(), description);
        for (ParameterExpression<?> parameter : expected) {
            assertSame(parameter, query.getParameters().stream()
                    .filter(actual -> actual.getName().equals(parameter.getName())).findFirst().orElseThrow());
        }
    }

    @Test
    void comparableParameterAdaptersDoNotPretendToBeParameterImplementations() {
        ParameterExpression<String> parameter = builder.parameter(String.class, "text");
        ComparableExpression<String> expression = new ComparableExpressionImpl<>((ExpressionImpl<String>) parameter);
        assertFalse(((InternalExpression) expression).isParameter());
        assertFalse(((InternalExpression) expression).isSubquery());
        assertTrue(((InternalExpression) expression).isCompoundExpression());
        assertInstanceOf(FunctionExpressionImpl.class, expression);
        CriteriaQuery<String> query = builder.createQuery(String.class).select(expression.max());
        query.orderBy(expression.asc(Nulls.FIRST));
        assertSame(parameter, query.getParameters().iterator().next());
    }

    @Test
    void serializingScalarExpressionsRetainsAliasesTypesParametersAndFurtherChaining() throws Exception {
        CriteriaBuilderImpl expressions = new CriteriaBuilderImpl(null);
        ParameterExpression<Integer> parameter = expressions.parameter(Integer.class, "increment");
        NumericExpression<Double> expression = expressions.numericLiteral(4).plus(parameter).toDouble();
        expression.alias("projected");
        NumericExpression<?> restored = roundTrip(expression);
        assertEquals(Double.class, restored.getJavaType());
        assertEquals("projected", restored.getAlias());
        CriteriaQuery<Object> query = builder.createQuery();
        ((InternalSelection) restored).findRootAndParameters((CommonAbstractCriteriaImpl<?>) query);
        assertEquals("increment", query.getParameters().iterator().next().getName());
        assertEquals(Integer.class, query.getParameters().iterator().next().getParameterType());
        Order order = roundTrip(expression.desc(Nulls.LAST));
        assertEquals(Nulls.LAST, order.getNullPrecedence());
        assertEquals(Double.class, restored.abs().getJavaType());
        TextExpression restoredText = roundTrip(expressions.stringLiteral("text").append("!"));
        assertInstanceOf(TextExpression.class, restoredText.lower());
        NumericExpression<Integer> restoredCoalesce = roundTrip(expressions.numericLiteral(4)
                .coalesce(expressions.nullLiteral(Integer.class)).nullif((Integer) null));
        assertEquals(Integer.class, restoredCoalesce.getJavaType());
        assertInstanceOf(NumericExpression.class, restoredCoalesce.plus(2));
    }

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
}
