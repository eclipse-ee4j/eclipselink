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
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.ComparableExpression;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;

/**
 * Comparable operations on an existing Criteria expression. The original
 * expression remains responsible for root and parameter discovery.
 */
public class ComparableExpressionImpl<C extends Comparable<? super C>>
        extends FunctionExpressionImpl<C> implements ComparableExpression<C> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ExpressionImpl<?> delegate;

    ComparableExpressionImpl(ExpressionImpl<C> delegate) {
        this(delegate, delegate.getJavaType());
    }

    protected ComparableExpressionImpl(ExpressionImpl<?> delegate, Class<? extends C> javaType) {
        super(delegate.metamodel, javaType, delegate.getCurrentNode(),
                delegate instanceof FunctionExpressionImpl<?> function
                        ? new ArrayList<>(function.getChildExpressions()) : List.of(delegate),
                delegate instanceof FunctionExpressionImpl<?> function ? function.getOperation() : null);
        this.delegate = delegate;
        this.alias = delegate.getAlias();
        this.isLiteral = delegate.isLiteral();
        this.literal = delegate.literal;
    }

    @Override
    public void findRootAndParameters(CommonAbstractCriteriaImpl<?> query) {
        delegate.findRootAndParameters(query);
    }

    protected CriteriaBuilderImpl builder() {
        return new CriteriaBuilderImpl(metamodel);
    }

    @SuppressWarnings("unchecked")
    protected Expression<C> value(C value) {
        return createLiteral(value, metamodel, (Class<C>) getJavaType());
    }

    private ComparableExpression<C> comparable(Expression<C> expression) {
        return new ComparableExpressionImpl<>((ExpressionImpl<C>) expression);
    }

    @Override
    public Predicate greaterThan(Expression<? extends C> y) {
        return builder().greaterThan(this, y);
    }

    @Override
    public Predicate greaterThan(C y) {
        return builder().greaterThan(this, value(y));
    }

    @Override
    public Predicate greaterThanOrEqualTo(Expression<? extends C> y) {
        return builder().greaterThanOrEqualTo(this, y);
    }

    @Override
    public Predicate greaterThanOrEqualTo(C y) {
        return builder().greaterThanOrEqualTo(this, value(y));
    }

    @Override
    public Predicate lessThan(Expression<? extends C> y) {
        return builder().lessThan(this, y);
    }

    @Override
    public Predicate lessThan(C y) {
        return builder().lessThan(this, value(y));
    }

    @Override
    public Predicate lessThanOrEqualTo(Expression<? extends C> y) {
        return builder().lessThanOrEqualTo(this, y);
    }

    @Override
    public Predicate lessThanOrEqualTo(C y) {
        return builder().lessThanOrEqualTo(this, value(y));
    }

    @Override
    public Predicate between(Expression<? extends C> x, Expression<? extends C> y) {
        return builder().between(this, x, y);
    }

    @Override
    public Predicate between(C x, C y) {
        return builder().between(this, value(x), value(y));
    }

    @Override
    public ComparableExpression<C> max() {
        return comparable(builder().greatest(this));
    }

    @Override
    public ComparableExpression<C> min() {
        return comparable(builder().least(this));
    }

    @Override
    public Order asc() {
        return builder().asc(this);
    }

    @Override
    public Order asc(Nulls nullPrecedence) {
        return builder().asc(this, nullPrecedence);
    }

    @Override
    public Order desc() {
        return builder().desc(this);
    }

    @Override
    public Order desc(Nulls nullPrecedence) {
        return builder().desc(this, nullPrecedence);
    }

    @Override
    public ComparableExpression<C> coalesce(Expression<? extends C> y) {
        return comparable(builder().coalesce(this, y));
    }

    @Override
    public ComparableExpression<C> coalesce(C y) {
        return comparable(builder().coalesce(this, value(y)));
    }

    @Override
    public ComparableExpression<C> nullif(Expression<? extends C> y) {
        return comparable(builder().nullif(this, y));
    }

    @Override
    public ComparableExpression<C> nullif(C y) {
        return comparable(builder().nullif(this, value(y)));
    }
}
