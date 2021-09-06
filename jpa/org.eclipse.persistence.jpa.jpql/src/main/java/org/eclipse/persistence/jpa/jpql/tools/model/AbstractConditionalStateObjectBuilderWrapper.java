/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.model;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression.Specification;
import org.eclipse.persistence.jpa.jpql.tools.model.query.SimpleSelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;

/**
 * This builder wraps another builder and delegates the calls to it.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractConditionalStateObjectBuilderWrapper implements IConditionalExpressionStateObjectBuilder {

    /**
     * The delegate to receive the calls from this one.
     */
    private final IConditionalExpressionStateObjectBuilder delegate;

    /**
     * Creates a new <code>AbstractConditionalStateObjectBuilderWrapper</code>.
     *
     * @param delegate The delegate to receive the calls from this one
     */
    public AbstractConditionalStateObjectBuilderWrapper(IConditionalExpressionStateObjectBuilder delegate) {
        super();
        Assert.isNotNull(delegate, "The delegate builder cannot be null");
        this.delegate = delegate;
    }

    @Override
    public IConditionalExpressionStateObjectBuilder abs(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.abs(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder add(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.add(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder all(SimpleSelectStatementStateObject subquery) {
        return delegate.all(subquery);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder and(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.and(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder any(SimpleSelectStatementStateObject subquery) {
        return delegate.any(subquery);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder avg(String path) {
        return delegate.avg(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder avgDistinct(String path) {
        return delegate.avgDistinct(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder between(IConditionalExpressionStateObjectBuilder lowerBoundExpression,
                                                  IConditionalExpressionStateObjectBuilder upperBoundExpression) {

        return delegate.between(lowerBoundExpression, upperBoundExpression);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder collectionPath(String path) {
        return delegate.collectionPath(path);
    }

    @Override
    public void commit() {
        delegate.commit();
    }

    @Override
    public IConditionalExpressionStateObjectBuilder count(String path) {
        return delegate.count(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder countDistinct(String path) {
        return delegate.countDistinct(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder currentDate() {
        return delegate.currentDate();
    }

    @Override
    public IConditionalExpressionStateObjectBuilder currentTime() {
        return delegate.currentTime();
    }

    @Override
    public IConditionalExpressionStateObjectBuilder currentTimestamp() {
        return delegate.currentTimestamp();
    }

    @Override
    public IConditionalExpressionStateObjectBuilder date(String jdbcDate) {
        return delegate.date(jdbcDate);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder different(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.different(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder different(Number number) {
        return delegate.different(number);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder different(String literal) {
        return delegate.different(literal);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder divide(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.divide(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder entityType(String entityTypeName) {
        return delegate.entityType(entityTypeName);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder equal(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.equal(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder equal(Number number) {
        return delegate.equal(number);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder equal(String literal) {
        return delegate.equal(literal);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder exists(SimpleSelectStatementStateObject subquery) {
        return delegate.exists(subquery);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder FALSE() {
        return delegate.FALSE();
    }

    @Override
    public IConditionalExpressionStateObjectBuilder greaterThan(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.greaterThan(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder greaterThan(Number number) {
        return delegate.greaterThan(number);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder greaterThan(String literal) {
        return delegate.greaterThan(literal);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder greaterThanOrEqual(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.greaterThanOrEqual(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder greaterThanOrEqual(Number number) {
        return delegate.greaterThanOrEqual(number);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder greaterThanOrEqual(String literal) {
        return delegate.greaterThanOrEqual(literal);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder in(IConditionalExpressionStateObjectBuilder... inItems) {
        return delegate.in(inItems);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder in(SimpleSelectStatementStateObject subquery) {
        return delegate.in(subquery);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder in(String... inItems) {
        return delegate.in(inItems);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder index(String variable) {
        return delegate.index(variable);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder isEmpty(String path) {
        return delegate.isEmpty(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder isNotEmpty(String path) {
        return delegate.isNotEmpty(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder isNotNull(String path) {
        return delegate.isNotNull(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder isNull(String path) {
        return delegate.isNull(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder length(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.length(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder like(IConditionalExpressionStateObjectBuilder patternValue) {
        return delegate.like(patternValue);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder like(IConditionalExpressionStateObjectBuilder patternValue,
                                               String escapeCharacter) {

        return delegate.like(patternValue, escapeCharacter);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder like(String patternValue) {
        return delegate.like(patternValue);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder locate(IConditionalExpressionStateObjectBuilder parameter1,
                                                 IConditionalExpressionStateObjectBuilder parameter2) {

        return delegate.locate(parameter1, parameter2);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder locate(IConditionalExpressionStateObjectBuilder parameter1,
                                                 IConditionalExpressionStateObjectBuilder parameter2, IConditionalExpressionStateObjectBuilder parameter3) {

        return delegate.locate(parameter1, parameter2);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder lower(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.lower(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder lowerThan(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.lowerThan(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder lowerThan(Number number) {
        return delegate.lowerThan(number);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder lowerThan(String literal) {
        return delegate.lowerThan(literal);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder lowerThanOrEqual(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.lowerThanOrEqual(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder lowerThanOrEqual(Number number) {
        return delegate.lowerThanOrEqual(number);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder lowerThanOrEqual(String literal) {
        return delegate.lowerThanOrEqual(literal);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder max(String path) {
        return delegate.max(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder maxDistinct(String path) {
        return delegate.maxDistinct(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder member(String path) {
        return delegate.member(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder memberOf(String path) {
        return delegate.memberOf(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder min(String path) {
        return delegate.min(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder minDistinct(String path) {
        return delegate.minDistinct(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder minus(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.minus(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder mod(IConditionalExpressionStateObjectBuilder parameter1,
                                              IConditionalExpressionStateObjectBuilder parameter2) {

        return delegate.mod(parameter1, parameter2);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder multiply(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.multiply(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder notBetween(IConditionalExpressionStateObjectBuilder lowerBoundExpression,
                                                     IConditionalExpressionStateObjectBuilder upperBoundExpression) {

        return delegate.notBetween(lowerBoundExpression, upperBoundExpression);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder notExists(SimpleSelectStatementStateObject subquery) {
        return delegate.notExists(subquery);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder notIn(IConditionalExpressionStateObjectBuilder... inItems) {
        return delegate.notIn(inItems);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder notIn(SimpleSelectStatementStateObject subquery) {
        return delegate.notIn(subquery);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder notIn(String... inItems) {
        return delegate.notIn(inItems);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder notLike(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.notLike(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder notLike(IConditionalExpressionStateObjectBuilder builder,
                                                  String escapeCharacter) {

        return delegate.notLike(builder, escapeCharacter);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder notLike(String patternValue) {
        return delegate.notLike(patternValue);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder notMember(String path) {
        return delegate.notMember(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder notMemberOf(String path) {
        return delegate.notMemberOf(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder NULL() {
        return delegate.NULL();
    }

    @Override
    public IConditionalExpressionStateObjectBuilder numeric(Number numeric) {
        return delegate.numeric(numeric);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder or(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.or(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder parameter(String parameter) {
        return delegate.parameter(parameter);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder path(String path) {
        return delegate.path(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder plus(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.plus(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder size(String path) {
        return delegate.size(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder some(SimpleSelectStatementStateObject subquery) {
        return delegate.some(subquery);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder sqrt(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.sqrt(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder string(String literal) {
        return delegate.string(literal);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder sub(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.sub(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder sub(StateObject stateObject) {
        return delegate.sub(stateObject);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder substring(IConditionalExpressionStateObjectBuilder parameter1, IConditionalExpressionStateObjectBuilder parameter2, IConditionalExpressionStateObjectBuilder parameter3) {
        return null;
    }

    @Override
    public IConditionalExpressionStateObjectBuilder subtract(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.subtract(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder sum(String path) {
        return delegate.sum(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder sumDistinct(String path) {
        return delegate.sumDistinct(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder trim(Specification specification,
                                               IConditionalExpressionStateObjectBuilder builder) {

        return delegate.trim(specification, builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder trim(Specification specification,
                                               String trimCharacter,
                                               IConditionalExpressionStateObjectBuilder builder) {

        return delegate.trim(specification, trimCharacter, builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder TRUE() {
        return delegate.TRUE();
    }

    @Override
    public IConditionalExpressionStateObjectBuilder type(String path) {
        return delegate.type(path);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder upper(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.upper(builder);
    }

    @Override
    public IConditionalExpressionStateObjectBuilder variable(String variable) {
        return delegate.variable(variable);
    }
}
