/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
    private IConditionalExpressionStateObjectBuilder delegate;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder abs(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.abs(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder add(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.add(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder all(SimpleSelectStatementStateObject subquery) {
        return delegate.all(subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder and(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.and(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder any(SimpleSelectStatementStateObject subquery) {
        return delegate.any(subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder avg(String path) {
        return delegate.avg(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder avgDistinct(String path) {
        return delegate.avgDistinct(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder between(IConditionalExpressionStateObjectBuilder lowerBoundExpression,
                                                  IConditionalExpressionStateObjectBuilder upperBoundExpression) {

        return delegate.between(lowerBoundExpression, upperBoundExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder collectionPath(String path) {
        return delegate.collectionPath(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() {
        delegate.commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder count(String path) {
        return delegate.count(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder countDistinct(String path) {
        return delegate.countDistinct(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder currentDate() {
        return delegate.currentDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder currentTime() {
        return delegate.currentTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder currentTimestamp() {
        return delegate.currentTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder date(String jdbcDate) {
        return delegate.date(jdbcDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder different(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.different(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder different(Number number) {
        return delegate.different(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder different(String literal) {
        return delegate.different(literal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder divide(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.divide(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder entityType(String entityTypeName) {
        return delegate.entityType(entityTypeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder equal(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.equal(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder equal(Number number) {
        return delegate.equal(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder equal(String literal) {
        return delegate.equal(literal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder exists(SimpleSelectStatementStateObject subquery) {
        return delegate.exists(subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder FALSE() {
        return delegate.FALSE();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder greaterThan(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.greaterThan(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder greaterThan(Number number) {
        return delegate.greaterThan(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder greaterThan(String literal) {
        return delegate.greaterThan(literal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder greaterThanOrEqual(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.greaterThanOrEqual(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder greaterThanOrEqual(Number number) {
        return delegate.greaterThanOrEqual(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder greaterThanOrEqual(String literal) {
        return delegate.greaterThanOrEqual(literal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder in(IConditionalExpressionStateObjectBuilder... inItems) {
        return delegate.in(inItems);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder in(SimpleSelectStatementStateObject subquery) {
        return delegate.in(subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder in(String... inItems) {
        return delegate.in(inItems);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder index(String variable) {
        return delegate.index(variable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder isEmpty(String path) {
        return delegate.isEmpty(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder isNotEmpty(String path) {
        return delegate.isNotEmpty(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder isNotNull(String path) {
        return delegate.isNotNull(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder isNull(String path) {
        return delegate.isNull(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder length(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.length(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder like(IConditionalExpressionStateObjectBuilder patternValue) {
        return delegate.like(patternValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder like(IConditionalExpressionStateObjectBuilder patternValue,
                                               String escapeCharacter) {

        return delegate.like(patternValue, escapeCharacter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder like(String patternValue) {
        return delegate.like(patternValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder locate(IConditionalExpressionStateObjectBuilder parameter1,
                                                 IConditionalExpressionStateObjectBuilder parameter2) {

        return delegate.locate(parameter1, parameter2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder locate(IConditionalExpressionStateObjectBuilder parameter1,
                                                 IConditionalExpressionStateObjectBuilder parameter2, IConditionalExpressionStateObjectBuilder parameter3) {

        return delegate.locate(parameter1, parameter2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder lower(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.lower(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder lowerThan(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.lowerThan(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder lowerThan(Number number) {
        return delegate.lowerThan(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder lowerThan(String literal) {
        return delegate.lowerThan(literal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder lowerThanOrEqual(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.lowerThanOrEqual(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder lowerThanOrEqual(Number number) {
        return delegate.lowerThanOrEqual(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder lowerThanOrEqual(String literal) {
        return delegate.lowerThanOrEqual(literal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder max(String path) {
        return delegate.max(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder maxDistinct(String path) {
        return delegate.maxDistinct(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder member(String path) {
        return delegate.member(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder memberOf(String path) {
        return delegate.memberOf(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder min(String path) {
        return delegate.min(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder minDistinct(String path) {
        return delegate.minDistinct(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder minus(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.minus(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder mod(IConditionalExpressionStateObjectBuilder parameter1,
                                              IConditionalExpressionStateObjectBuilder parameter2) {

        return delegate.mod(parameter1, parameter2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder multiply(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.multiply(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder notBetween(IConditionalExpressionStateObjectBuilder lowerBoundExpression,
                                                     IConditionalExpressionStateObjectBuilder upperBoundExpression) {

        return delegate.notBetween(lowerBoundExpression, upperBoundExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder notExists(SimpleSelectStatementStateObject subquery) {
        return delegate.notExists(subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder notIn(IConditionalExpressionStateObjectBuilder... inItems) {
        return delegate.notIn(inItems);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder notIn(SimpleSelectStatementStateObject subquery) {
        return delegate.notIn(subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder notIn(String... inItems) {
        return delegate.notIn(inItems);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder notLike(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.notLike(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder notLike(IConditionalExpressionStateObjectBuilder builder,
                                                  String escapeCharacter) {

        return delegate.notLike(builder, escapeCharacter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder notLike(String patternValue) {
        return delegate.notLike(patternValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder notMember(String path) {
        return delegate.notMember(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder notMemberOf(String path) {
        return delegate.notMemberOf(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder NULL() {
        return delegate.NULL();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder numeric(Number numeric) {
        return delegate.numeric(numeric);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder or(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.or(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder parameter(String parameter) {
        return delegate.parameter(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder path(String path) {
        return delegate.path(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder plus(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.plus(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder size(String path) {
        return delegate.size(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder some(SimpleSelectStatementStateObject subquery) {
        return delegate.some(subquery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder sqrt(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.sqrt(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder string(String literal) {
        return delegate.string(literal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder sub(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.sub(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder sub(StateObject stateObject) {
        return delegate.sub(stateObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder substring(IConditionalExpressionStateObjectBuilder parameter1, IConditionalExpressionStateObjectBuilder parameter2, IConditionalExpressionStateObjectBuilder parameter3) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder subtract(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.subtract(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder sum(String path) {
        return delegate.sum(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder sumDistinct(String path) {
        return delegate.sumDistinct(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder trim(Specification specification,
                                               IConditionalExpressionStateObjectBuilder builder) {

        return delegate.trim(specification, builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder trim(Specification specification,
                                               String trimCharacter,
                                               IConditionalExpressionStateObjectBuilder builder) {

        return delegate.trim(specification, trimCharacter, builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder TRUE() {
        return delegate.TRUE();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder type(String path) {
        return delegate.type(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder upper(IConditionalExpressionStateObjectBuilder builder) {
        return delegate.upper(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConditionalExpressionStateObjectBuilder variable(String variable) {
        return delegate.variable(variable);
    }
}
