/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *
 *     03/19/2009-2.0 Michael O'Brien  
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 *         Stub interface without implementation only.
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.criteria;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.persistence.Parameter;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

// 266912: This class has been stubbed directly from the QueryBuilder interface for compilation only
// No implementation has occurred here yet
public class QueryBuilderImpl implements javax.persistence.criteria.QueryBuilder {

    public <N extends Number> Expression<N> abs(Expression<N> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> Expression<Y> all(Subquery<Y> subquery) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate and(Expression<Boolean> x, Expression<Boolean> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate and(Predicate... restrictions) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> Expression<Y> any(Subquery<Y> subquery) {
        // TODO Auto-generated method stub
        return null;
    }

    public Order asc(Expression<?> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<Double> avg(Expression<N> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y extends Comparable<Y>> Predicate between(Expression<? extends Y> v,
            Expression<? extends Y> x, Expression<? extends Y> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y extends Comparable<Y>> Predicate between(Expression<? extends Y> v, Y x, Y y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> Expression<Y> coalesce(Expression<? extends Y> x, Expression<? extends Y> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> Expression<Y> coalesce(Expression<? extends Y> x, Y y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> Coalesce<T> coalesce() {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> concat(Expression<String> x, Expression<String> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> concat(Expression<String> x, String y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> concat(String x, Expression<String> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate conjunction() {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Long> count(Expression<?> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Long> countDistinct(Expression<?> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public CriteriaQuery create() {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Date> currentDate() {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Time> currentTime() {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Timestamp> currentTimestamp() {
        // TODO Auto-generated method stub
        return null;
    }

    public Order desc(Expression<?> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> diff(Expression<? extends N> x,
            Expression<? extends N> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> diff(Expression<? extends N> x, N y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> diff(N x, Expression<? extends N> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate disjunction() {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate equal(Expression<?> x, Expression<?> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate equal(Expression<?> x, Object y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate exists(Subquery<?> subquery) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> Expression<T> function(String name, Class<T> type, Expression<?>... args) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate ge(Expression<? extends Number> x, Expression<? extends Number> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate ge(Expression<? extends Number> x, Number y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y extends Comparable<Y>> Predicate greaterThan(Expression<? extends Y> x,
            Expression<? extends Y> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y extends Comparable<Y>> Predicate greaterThan(Expression<? extends Y> x, Y y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y extends Comparable<Y>> Predicate greaterThanOrEqualTo(
            Expression<? extends Y> x, Expression<? extends Y> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y extends Comparable<Y>> Predicate greaterThanOrEqualTo(
            Expression<? extends Y> x, Y y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <X extends Comparable<X>> Expression<X> greatest(Expression<X> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate gt(Expression<? extends Number> x, Expression<? extends Number> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate gt(Expression<? extends Number> x, Number y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> In<T> in(Expression<? extends T> expression) {
        // TODO Auto-generated method stub
        return null;
    }

    public <C extends Collection<?>> Predicate isEmpty(Expression<C> collection) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate isFalse(Expression<Boolean> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public <E, C extends Collection<E>> Predicate isMember(E elem,
            Expression<C> collection) {
        // TODO Auto-generated method stub
        return null;
    }

    public <E, C extends Collection<E>> Predicate isMember(Expression<E> elem,
            Expression<C> collection) {
        // TODO Auto-generated method stub
        return null;
    }

    public <C extends Collection<?>> Predicate isNotEmpty(Expression<C> collection) {
        // TODO Auto-generated method stub
        return null;
    }

    public <E, C extends Collection<E>> Predicate isNotMember(E elem,
            Expression<C> collection) {
        // TODO Auto-generated method stub
        return null;
    }

    public <E, C extends Collection<E>> Predicate isNotMember(Expression<E> elem,
            Expression<C> collection) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate isTrue(Expression<Boolean> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public <K, M extends Map<K, ?>> Expression<Set<K>> keys(M map) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate le(Expression<? extends Number> x, Expression<? extends Number> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate le(Expression<? extends Number> x, Number y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <X extends Comparable<X>> Expression<X> least(Expression<X> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Integer> length(Expression<String> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y extends Comparable<Y>> Predicate lessThan(Expression<? extends Y> x,
            Expression<? extends Y> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y extends Comparable<Y>> Predicate lessThan(Expression<? extends Y> x, Y y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y extends Comparable<Y>> Predicate lessThanOrEqualTo(
            Expression<? extends Y> x, Expression<? extends Y> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y extends Comparable<Y>> Predicate lessThanOrEqualTo(
            Expression<? extends Y> x, Y y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate like(Expression<String> x, Expression<String> pattern) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate like(Expression<String> x, Expression<String> pattern,
            Expression<Character> escapeChar) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate like(Expression<String> x, Expression<String> pattern,
            char escapeChar) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate like(Expression<String> x, String pattern) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate like(Expression<String> x, String pattern,
            Expression<Character> escapeChar) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate like(Expression<String> x, String pattern, char escapeChar) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> Expression<T> literal(T value) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Integer> locate(Expression<String> x, Expression<String> pattern) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Integer> locate(Expression<String> x, Expression<String> pattern,
            Expression<Integer> from) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Integer> locate(Expression<String> x, String pattern) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Integer> locate(Expression<String> x, String pattern, int from) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> lower(Expression<String> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate lt(Expression<? extends Number> x, Expression<? extends Number> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate lt(Expression<? extends Number> x, Number y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> max(Expression<N> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> min(Expression<N> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Integer> mod(Expression<Integer> x, Expression<Integer> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Integer> mod(Expression<Integer> x, Integer y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Integer> mod(Integer x, Expression<Integer> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> neg(Expression<N> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate not(Expression<Boolean> restriction) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate notEqual(Expression<?> x, Expression<?> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate notEqual(Expression<?> x, Object y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate notLike(Expression<String> x, Expression<String> pattern) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate notLike(Expression<String> x, Expression<String> pattern,
            Expression<Character> escapeChar) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate notLike(Expression<String> x, Expression<String> pattern,
            char escapeChar) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate notLike(Expression<String> x, String pattern) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate notLike(Expression<String> x, String pattern,
            Expression<Character> escapeChar) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate notLike(Expression<String> x, String pattern, char escapeChar) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> Expression<Y> nullif(Expression<Y> x, Expression<?> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> Expression<Y> nullif(Expression<Y> x, Y y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate or(Expression<Boolean> x, Expression<Boolean> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Predicate or(Predicate... restrictions) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> Parameter<T> parameter(Class<T> paramClass) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> Parameter<T> parameter(Class<T> paramClass, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> prod(Expression<? extends N> x,
            Expression<? extends N> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> prod(Expression<? extends N> x, N y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> prod(N x, Expression<? extends N> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Number> quot(Expression<? extends Number> x,
            Expression<? extends Number> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Number> quot(Expression<? extends Number> x, Number y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Number> quot(Number x, Expression<? extends Number> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> Selection<Y> select(Class<Y> result, Selection<?>... selections) {
        // TODO Auto-generated method stub
        return null;
    }

    public <C, R> SimpleCase<C, R> selectCase(Expression<? extends C> expression) {
        // TODO Auto-generated method stub
        return null;
    }

    public <R> Case<R> selectCase() {
        // TODO Auto-generated method stub
        return null;
    }

    public <C extends Collection<?>> Expression<Integer> size(C collection) {
        // TODO Auto-generated method stub
        return null;
    }

    public <C extends Collection<?>> Expression<Integer> size(Expression<C> collection) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> Expression<Y> some(Subquery<Y> subquery) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Double> sqrt(Expression<? extends Number> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> substring(Expression<String> x, Expression<Integer> from) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> substring(Expression<String> x, int from) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> substring(Expression<String> x, Expression<Integer> from,
            Expression<Integer> len) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> substring(Expression<String> x, int from, int len) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> sum(Expression<N> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> sum(Expression<? extends N> x,
            Expression<? extends N> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> sum(Expression<? extends N> x, N y) {
        // TODO Auto-generated method stub
        return null;
    }

    public <N extends Number> Expression<N> sum(N x, Expression<? extends N> y) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<BigDecimal> toBigDecimal(Expression<? extends Number> number) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<BigInteger> toBigInteger(Expression<? extends Number> number) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Double> toDouble(Expression<? extends Number> number) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Float> toFloat(Expression<? extends Number> number) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Integer> toInteger(Expression<? extends Number> number) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<Long> toLong(Expression<? extends Number> number) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> toString(Expression<Character> character) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> trim(Expression<String> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> trim(Trimspec ts, Expression<String> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> trim(Expression<Character> t, Expression<String> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> trim(Trimspec ts, Expression<Character> t,
            Expression<String> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> trim(char t, Expression<String> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> trim(Trimspec ts, char t, Expression<String> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public Expression<String> upper(Expression<String> x) {
        // TODO Auto-generated method stub
        return null;
    }

    public <V, M extends Map<?, V>> Expression<Collection<V>> values(M map) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> Selection<Y> construct(Class<Y> result, Selection<?>... selections) {
        // TODO Auto-generated method stub
        return null;
    }
}
