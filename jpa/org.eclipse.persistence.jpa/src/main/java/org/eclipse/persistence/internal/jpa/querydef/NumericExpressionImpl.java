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

import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.NumericExpression;
import jakarta.persistence.criteria.Predicate;

import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.helper.ConversionManager;

/**
 * Numeric Criteria operations with specialized return types.
 */
public class NumericExpressionImpl<N extends Number & Comparable<N>>
        extends ComparableExpressionImpl<N> implements NumericExpression<N> {

    @Serial
    private static final long serialVersionUID = 1L;

    NumericExpressionImpl(ExpressionImpl<N> delegate) {
        super(delegate);
    }

    NumericExpressionImpl(ExpressionImpl<?> delegate, Class<? extends N> javaType) {
        super(delegate, javaType);
    }

    private static <T extends Number & Comparable<T>> NumericExpression<T> numeric(Expression<T> expression) {
        return new NumericExpressionImpl<>((ExpressionImpl<T>) expression);
    }

    private <T extends Number & Comparable<T>> NumericExpression<T> typed(Expression<? extends Number> expression, Class<T> type) {
        return new NumericExpressionImpl<>(parenthesized(expression, type));
    }

    /**
     * Give a numeric operand independent result typing without changing its SQL
     * value. A parenthesized function keeps the original mapped path intact while
     * preventing its mapping from overriding a typecast or narrowing a different
     * numeric operand to the mapped Java type.
     */
    private <T extends Number> ExpressionImpl<T> parenthesized(Expression<? extends Number> expression, Class<T> type) {
        FunctionExpression node = (FunctionExpression) currentNode(expression).getFunction("");
        node.setResultType(type);
        ExpressionImpl<T> result = new FunctionExpressionImpl<>(metamodel, type, node, List.of(expression), "numeric");
        result.alias(expression.getAlias());
        return result;
    }

    private Expression<? extends Number> comparisonOperand(Expression<? extends Number> expression) {
        return ConversionManager.getObjectClass(getJavaType()).equals(ConversionManager.getObjectClass(expression.getJavaType()))
                ? expression : parenthesized(expression, expression.getJavaType());
    }

    @SuppressWarnings("unchecked")
    private Expression<Number> number(Number value) {
        return createLiteral(value, metamodel, value == null ? Number.class : (Class<Number>) value.getClass());
    }

    @Override
    public Predicate gt(Expression<? extends Number> y) {
        return builder().gt(this, comparisonOperand(y));
    }

    @Override
    public Predicate gt(Number y) {
        return gt(number(y));
    }

    @Override
    public Predicate ge(Expression<? extends Number> y) {
        return builder().ge(this, comparisonOperand(y));
    }

    @Override
    public Predicate ge(Number y) {
        return ge(number(y));
    }

    @Override
    public Predicate lt(Expression<? extends Number> y) {
        return builder().lt(this, comparisonOperand(y));
    }

    @Override
    public Predicate lt(Number y) {
        return lt(number(y));
    }

    @Override
    public Predicate le(Expression<? extends Number> y) {
        return builder().le(this, comparisonOperand(y));
    }

    @Override
    public Predicate le(Number y) {
        return le(number(y));
    }

    @Override
    public NumericExpression<Integer> sign() {
        return numeric(builder().sign(this));
    }

    @Override
    public NumericExpression<N> negated() {
        return numeric(builder().neg(this));
    }

    @Override
    public NumericExpression<N> abs() {
        return numeric(builder().abs(this));
    }

    @Override
    public NumericExpression<N> ceiling() {
        return numeric(builder().ceiling(this));
    }

    @Override
    public NumericExpression<N> floor() {
        return numeric(builder().floor(this));
    }

    @Override
    public NumericExpression<Double> sqrt() {
        return numeric(builder().sqrt(this));
    }

    @Override
    public NumericExpression<Double> exp() {
        return numeric(builder().exp(this));
    }

    @Override
    public NumericExpression<Double> ln() {
        return numeric(builder().ln(this));
    }

    @Override
    public NumericExpression<Double> avg() {
        return numeric(builder().avg(this));
    }

    @Override
    public NumericExpression<N> sum() {
        return numeric(builder().sum(this));
    }

    @Override
    public NumericExpression<N> max() {
        return numeric(builder().max(this));
    }

    @Override
    public NumericExpression<N> min() {
        return numeric(builder().min(this));
    }

    @Override
    public NumericExpression<N> plus(Expression<? extends N> y) {
        return numeric(builder().sum(this, y));
    }

    @Override
    public NumericExpression<N> plus(N y) {
        return numeric(builder().sum(this, value(y)));
    }

    @Override
    public NumericExpression<N> times(Expression<? extends N> y) {
        return numeric(builder().prod(this, y));
    }

    @Override
    public NumericExpression<N> times(N y) {
        return numeric(builder().prod(this, value(y)));
    }

    @Override
    public NumericExpression<N> minus(Expression<? extends N> y) {
        return numeric(builder().diff(this, y));
    }

    @Override
    public NumericExpression<N> minus(N y) {
        return numeric(builder().diff(this, value(y)));
    }

    @Override
    public NumericExpression<N> dividedBy(Expression<? extends N> y) {
        return new NumericExpressionImpl<>((ExpressionImpl<?>) builder().quot(this, y), getJavaType());
    }

    @Override
    public NumericExpression<N> dividedBy(N y) {
        return new NumericExpressionImpl<>((ExpressionImpl<?>) builder().quot(this, value(y)), getJavaType());
    }

    @Override
    public NumericExpression<N> subtractedFrom(N y) {
        return numeric(builder().diff(value(y), this));
    }

    @Override
    public NumericExpression<N> dividedInto(N y) {
        return new NumericExpressionImpl<>((ExpressionImpl<?>) builder().quot(value(y), this), getJavaType());
    }

    @Override
    public NumericExpression<Double> power(Expression<? extends Number> y) {
        return numeric(builder().power(this, y));
    }

    @Override
    public NumericExpression<Double> power(Number y) {
        return numeric(builder().power(this, number(y)));
    }

    @Override
    public NumericExpression<N> round(Integer n) {
        return numeric(builder().round(this, n));
    }

    @Override
    public NumericExpression<Long> sumAsLong() {
        return typed(builder().sum(this), Long.class);
    }

    @Override
    public NumericExpression<Double> sumAsDouble() {
        return typed(builder().sum(this), Double.class);
    }

    @Override
    public NumericExpression<Long> toLong() {
        return typed(this, Long.class);
    }

    @Override
    public NumericExpression<Integer> toInteger() {
        return typed(this, Integer.class);
    }

    @Override
    public NumericExpression<Float> toFloat() {
        return typed(this, Float.class);
    }

    @Override
    public NumericExpression<Double> toDouble() {
        return typed(this, Double.class);
    }

    @Override
    public NumericExpression<BigDecimal> toBigDecimal() {
        return typed(this, BigDecimal.class);
    }

    @Override
    public NumericExpression<BigInteger> toBigInteger() {
        return typed(this, BigInteger.class);
    }

    @Override
    public NumericExpression<N> coalesce(Expression<? extends N> y) {
        return numeric(builder().coalesce(this, y));
    }

    @Override
    public NumericExpression<N> coalesce(N y) {
        return numeric(builder().coalesce(this, value(y)));
    }

    @Override
    public NumericExpression<N> nullif(Expression<? extends N> y) {
        return numeric(builder().nullif(this, y));
    }

    @Override
    public NumericExpression<N> nullif(N y) {
        return numeric(builder().nullif(this, value(y)));
    }
}
