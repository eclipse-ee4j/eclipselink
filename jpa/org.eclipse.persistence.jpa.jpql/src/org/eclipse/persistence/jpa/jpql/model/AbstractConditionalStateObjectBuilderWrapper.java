/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.model;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression.Specification;

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
	public IConditionalExpressionStateObjectBuilder abs(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.abs(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder add(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.add(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder all(SimpleSelectStatementStateObject subquery) {
		return delegate.all(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder and(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.and(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder any(SimpleSelectStatementStateObject subquery) {
		return delegate.any(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder avg(String path) {
		return delegate.avg(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder avgDistinct(String path) {
		return delegate.avgDistinct(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder between(IConditionalExpressionStateObjectBuilder lowerBoundExpression,
	                                              IConditionalExpressionStateObjectBuilder upperBoundExpression) {

		return delegate.between(lowerBoundExpression, upperBoundExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder collectionPath(String path) {
		return delegate.collectionPath(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() {
		delegate.commit();
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder concat(IConditionalExpressionStateObjectBuilder parameter1,
	                                             IConditionalExpressionStateObjectBuilder parameter2) {

		return delegate.concat(parameter1, parameter2);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder count(String path) {
		return delegate.count(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder countDistinct(String path) {
		return delegate.countDistinct(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder currentDate() {
		return delegate.currentDate();
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder currentTime() {
		return delegate.currentTime();
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder currentTimestamp() {
		return delegate.currentTimestamp();
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder date(String jdbcDate) {
		return delegate.date(jdbcDate);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder different(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.different(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder different(Number number) {
		return delegate.different(number);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder different(String literal) {
		return delegate.different(literal);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder divide(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.divide(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder entityType(String entityTypeName) {
		return delegate.entityType(entityTypeName);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder equal(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.equal(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder equal(Number number) {
		return delegate.equal(number);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder equal(String literal) {
		return delegate.equal(literal);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder exists(SimpleSelectStatementStateObject subquery) {
		return delegate.exists(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder FALSE() {
		return delegate.FALSE();
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder greaterThan(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.greaterThan(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder greaterThan(Number number) {
		return delegate.greaterThan(number);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder greaterThan(String literal) {
		return delegate.greaterThan(literal);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder greaterThanOrEqual(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.greaterThanOrEqual(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder greaterThanOrEqual(Number number) {
		return delegate.greaterThanOrEqual(number);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder greaterThanOrEqual(String literal) {
		return delegate.greaterThanOrEqual(literal);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder in(IConditionalExpressionStateObjectBuilder... inItems) {
		return delegate.in(inItems);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder in(SimpleSelectStatementStateObject subquery) {
		return delegate.in(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder in(String... inItems) {
		return delegate.in(inItems);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder index(String variable) {
		return delegate.index(variable);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder isEmpty(String path) {
		return delegate.isEmpty(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder isNotEmpty(String path) {
		return delegate.isNotEmpty(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder isNotNull(String path) {
		return delegate.isNotNull(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder isNull(String path) {
		return delegate.isNull(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder length(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.length(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder like(IConditionalExpressionStateObjectBuilder patternValue) {
		return delegate.like(patternValue);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder like(IConditionalExpressionStateObjectBuilder patternValue,
	                                           String escapeCharacter) {

		return delegate.like(patternValue, escapeCharacter);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder like(String patternValue) {
		return delegate.like(patternValue);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder locate(IConditionalExpressionStateObjectBuilder parameter1,
	                                             IConditionalExpressionStateObjectBuilder parameter2) {

		return delegate.locate(parameter1, parameter2);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder locate(IConditionalExpressionStateObjectBuilder parameter1,
	                                             IConditionalExpressionStateObjectBuilder parameter2, IConditionalExpressionStateObjectBuilder parameter3) {

		return delegate.locate(parameter1, parameter2);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder lower(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.lower(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder lowerThan(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.lowerThan(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder lowerThan(Number number) {
		return delegate.lowerThan(number);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder lowerThan(String literal) {
		return delegate.lowerThan(literal);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder lowerThanOrEqual(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.lowerThanOrEqual(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder lowerThanOrEqual(Number number) {
		return delegate.lowerThanOrEqual(number);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder lowerThanOrEqual(String literal) {
		return delegate.lowerThanOrEqual(literal);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder max(String path) {
		return delegate.max(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder maxDistinct(String path) {
		return delegate.maxDistinct(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder member(String path) {
		return delegate.member(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder memberOf(String path) {
		return delegate.memberOf(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder min(String path) {
		return delegate.min(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder minDistinct(String path) {
		return delegate.minDistinct(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder minus(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.minus(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder mod(IConditionalExpressionStateObjectBuilder parameter1,
	                                          IConditionalExpressionStateObjectBuilder parameter2) {

		return delegate.mod(parameter1, parameter2);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder multiply(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.multiply(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder notBetween(IConditionalExpressionStateObjectBuilder lowerBoundExpression,
	                                                 IConditionalExpressionStateObjectBuilder upperBoundExpression) {

		return delegate.notBetween(lowerBoundExpression, upperBoundExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder notExists(SimpleSelectStatementStateObject subquery) {
		return delegate.notExists(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder notIn(IConditionalExpressionStateObjectBuilder... inItems) {
		return delegate.notIn(inItems);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder notIn(SimpleSelectStatementStateObject subquery) {
		return delegate.notIn(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder notIn(String... inItems) {
		return delegate.notIn(inItems);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder notLike(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.notLike(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder notLike(IConditionalExpressionStateObjectBuilder builder,
	                                              String escapeCharacter) {

		return delegate.notLike(builder, escapeCharacter);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder notLike(String patternValue) {
		return delegate.notLike(patternValue);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder notMember(String path) {
		return delegate.notMember(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder notMemberOf(String path) {
		return delegate.notMemberOf(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder NULL() {
		return delegate.NULL();
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder numeric(Number numeric) {
		return delegate.numeric(numeric);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder or(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.or(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder parameter(String parameter) {
		return delegate.parameter(parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder path(String path) {
		return delegate.path(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder plus(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.plus(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder size(String path) {
		return delegate.size(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder some(SimpleSelectStatementStateObject subquery) {
		return delegate.some(subquery);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder sqrt(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.sqrt(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder string(String literal) {
		return delegate.string(literal);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder sub(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.sub(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder sub(StateObject stateObject) {
		return delegate.sub(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder substring(IConditionalExpressionStateObjectBuilder parameter1, IConditionalExpressionStateObjectBuilder parameter2, IConditionalExpressionStateObjectBuilder parameter3) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder subtract(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.subtract(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder sum(String path) {
		return delegate.sum(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder sumDistinct(String path) {
		return delegate.sumDistinct(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder trim(Specification specification,
	                                           IConditionalExpressionStateObjectBuilder builder) {

		return delegate.trim(specification, builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder trim(Specification specification,
	                                           String trimCharacter,
	                                           IConditionalExpressionStateObjectBuilder builder) {

		return delegate.trim(specification, trimCharacter, builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder TRUE() {
		return delegate.TRUE();
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder type(String path) {
		return delegate.type(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder upper(IConditionalExpressionStateObjectBuilder builder) {
		return delegate.upper(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConditionalExpressionStateObjectBuilder variable(String variable) {
		return delegate.variable(variable);
	}
}