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
import java.util.Objects;

import jakarta.persistence.criteria.CriteriaBuilder.Trimspec;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.NumericExpression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.TextExpression;

/**
 * Text Criteria operations with specialized return types.
 */
public class TextExpressionImpl extends ComparableExpressionImpl<String> implements TextExpression {

    @Serial
    private static final long serialVersionUID = 1L;

    TextExpressionImpl(ExpressionImpl<String> delegate) {
        super(delegate);
    }

    private TextExpression text(Expression<String> expression) {
        return new TextExpressionImpl((ExpressionImpl<String>) expression);
    }

    private NumericExpression<Integer> numeric(Expression<Integer> expression) {
        return new NumericExpressionImpl<>((ExpressionImpl<Integer>) expression);
    }

    @Override
    public Predicate like(Expression<String> pattern) {
        return builder().like(this, pattern);
    }

    @Override
    public Predicate like(Expression<String> pattern, Expression<Character> escapeChar) {
        return builder().like(this, pattern, escapeChar);
    }

    @Override
    public Predicate like(Expression<String> pattern, char escapeChar) {
        return builder().like(this, pattern, escapeChar);
    }

    @Override
    public Predicate like(String pattern) {
        return builder().like(this, pattern);
    }

    @Override
    public Predicate like(String pattern, Expression<Character> escapeChar) {
        return builder().like(this, pattern, escapeChar);
    }

    @Override
    public Predicate like(String pattern, char escapeChar) {
        return builder().like(this, pattern, escapeChar);
    }

    @Override
    public Predicate notLike(Expression<String> pattern) {
        return builder().notLike(this, pattern);
    }

    @Override
    public Predicate notLike(Expression<String> pattern, Expression<Character> escapeChar) {
        return builder().notLike(this, pattern, escapeChar);
    }

    @Override
    public Predicate notLike(Expression<String> pattern, char escapeChar) {
        return builder().notLike(this, pattern, escapeChar);
    }

    @Override
    public Predicate notLike(String pattern) {
        return builder().notLike(this, pattern);
    }

    @Override
    public Predicate notLike(String pattern, Expression<Character> escapeChar) {
        return builder().notLike(this, pattern, escapeChar);
    }

    @Override
    public Predicate notLike(String pattern, char escapeChar) {
        return builder().notLike(this, pattern, escapeChar);
    }

    @Override
    public Predicate contains(String substring) {
        Objects.requireNonNull(substring);
        return substring.isEmpty() ? builder().ge(length(), 0) : builder().gt(builder().locate(this, substring), 0);
    }

    @Override
    public Predicate notContains(String substring) {
        return builder().not(contains(substring));
    }

    @Override
    public Predicate startsWith(String prefix) {
        Objects.requireNonNull(prefix);
        return prefix.isEmpty() ? builder().ge(length(), 0) : builder().equal(builder().locate(this, prefix), 1);
    }

    @Override
    public Predicate notStartsWith(String prefix) {
        return builder().not(startsWith(prefix));
    }

    @Override
    public Predicate endsWith(String suffix) {
        Objects.requireNonNull(suffix);
        if (suffix.isEmpty()) {
            return builder().ge(length(), 0);
        }
        // Let the database count supplementary characters. The marker also keeps
        // trailing spaces visible to length functions such as SQL Server LEN.
        Expression<Integer> suffixLength = builder().diff(builder().length(value(suffix + "x")), 1);
        // Compute the final possible match position without applying RIGHT to a
        // source shorter than the suffix. Some databases reject that substring,
        // while others return null and give an incorrect negated predicate.
        // Multiplication retains SQL null semantics when CONCAT(null, 'x') = 'x'.
        Expression<Integer> sourceLength = builder().sum(
                builder().diff(builder().length(builder().concat(this, "x")), 1),
                builder().prod(length(), 0));
        Expression<Integer> start = builder().sum(builder().diff(sourceLength, suffixLength), 1);
        Expression<Integer> safeStart = builder().<Integer>selectCase()
                .when(builder().ge(start, 1), start).otherwise(1);
        // Compare the positional difference with a value to avoid making either
        // shared literal-based expression the conversion context of the other.
        return builder().and(builder().ge(start, 1),
                builder().equal(builder().diff(builder().locate(this, value(suffix), safeStart), start), 0));
    }

    @Override
    public Predicate notEndsWith(String suffix) {
        return builder().not(endsWith(suffix));
    }

    @Override
    public TextExpression append(Expression<String> y) {
        return text(builder().concat(this, y));
    }

    @Override
    public TextExpression append(String y) {
        return text(builder().concat(this, y));
    }

    @Override
    public TextExpression prepend(Expression<String> y) {
        return text(builder().concat(y, this));
    }

    @Override
    public TextExpression prepend(String y) {
        return text(builder().concat(y, this));
    }

    @Override
    public TextExpression substring(Expression<Integer> from) {
        return text(builder().substring(this, from));
    }

    @Override
    public TextExpression substring(int from) {
        return text(builder().substring(this, from));
    }

    @Override
    public TextExpression substring(Expression<Integer> from, Expression<Integer> len) {
        return text(builder().substring(this, from, len));
    }

    @Override
    public TextExpression substring(int from, int len) {
        return text(builder().substring(this, from, len));
    }

    @Override
    public TextExpression trim() {
        return text(builder().trim(this));
    }

    @Override
    public TextExpression trim(Trimspec ts) {
        return text(builder().trim(ts, this));
    }

    @Override
    public TextExpression trim(Expression<Character> t) {
        return text(builder().trim(t, this));
    }

    @Override
    public TextExpression trim(Trimspec ts, Expression<Character> t) {
        return text(builder().trim(ts, t, this));
    }

    @Override
    public TextExpression trim(char t) {
        return text(builder().trim(t, this));
    }

    @Override
    public TextExpression trim(Trimspec ts, char t) {
        return text(builder().trim(ts, t, this));
    }

    @Override
    public TextExpression lower() {
        return text(builder().lower(this));
    }

    @Override
    public TextExpression upper() {
        return text(builder().upper(this));
    }

    @Override
    public Expression<Integer> length() {
        return builder().length(this);
    }

    @Override
    public TextExpression left(int len) {
        return text(builder().left(this, len));
    }

    @Override
    public TextExpression left(Expression<Integer> len) {
        return text(builder().left(this, len));
    }

    @Override
    public TextExpression right(int len) {
        return text(builder().right(this, len));
    }

    @Override
    public TextExpression right(Expression<Integer> len) {
        return text(builder().right(this, len));
    }

    @Override
    public TextExpression replace(Expression<String> substring, Expression<String> replacement) {
        return text(builder().replace(this, substring, replacement));
    }

    @Override
    public TextExpression replace(String substring, Expression<String> replacement) {
        return text(builder().replace(this, substring, replacement));
    }

    @Override
    public TextExpression replace(Expression<String> substring, String replacement) {
        return text(builder().replace(this, substring, replacement));
    }

    @Override
    public TextExpression replace(String substring, String replacement) {
        return text(builder().replace(this, substring, replacement));
    }

    @Override
    public NumericExpression<Integer> locate(Expression<String> pattern) {
        return numeric(builder().locate(this, pattern));
    }

    @Override
    public NumericExpression<Integer> locate(String pattern) {
        return numeric(builder().locate(this, pattern));
    }

    @Override
    public NumericExpression<Integer> locate(Expression<String> pattern, Expression<Integer> from) {
        return numeric(builder().locate(this, pattern, from));
    }

    @Override
    public NumericExpression<Integer> locate(String pattern, int from) {
        return numeric(builder().locate(this, pattern, from));
    }

    @Override
    public TextExpression coalesce(Expression<? extends String> y) {
        return text(builder().coalesce(this, y));
    }

    @Override
    public TextExpression coalesce(String y) {
        return text(builder().coalesce(this, value(y)));
    }

    @Override
    public TextExpression nullif(Expression<? extends String> y) {
        return text(builder().nullif(this, y));
    }

    @Override
    public TextExpression nullif(String y) {
        return text(builder().nullif(this, value(y)));
    }
}
