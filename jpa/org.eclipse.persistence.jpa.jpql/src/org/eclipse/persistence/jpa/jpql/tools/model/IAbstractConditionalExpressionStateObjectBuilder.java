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

import org.eclipse.persistence.jpa.jpql.parser.TrimExpression.Specification;
import org.eclipse.persistence.jpa.jpql.tools.model.query.SimpleSelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface IAbstractConditionalExpressionStateObjectBuilder<T extends IScalarExpressionStateObjectBuilder<T>> extends IScalarExpressionStateObjectBuilder<T> {

    /**
     * Creates the expression <code><b>ALL(subquery)</b></code>.
     *
     * @param subquery The already constructed subquery
     * @return This builder
     */
    T all(SimpleSelectStatementStateObject subquery);

    /**
     * Creates the expression <code><b>x AND y</b></code>.
     *
     * @param builder The right side of the logical expression
     * @return This builder
     */
    T and(T builder);

    /**
     * Creates the expression <code><b>ANY(subquery)</b></code>.
     *
     * @param subquery The already constructed subquery
     * @return This builder
     */
    T any(SimpleSelectStatementStateObject subquery);

    /**
     * Creates the expression <code><b>x BETWEEN y AND z</b></code>.
     *
     * @param lowerBoundExpression The lower bound expression
     * @param upperBoundExpression The upper bound expression
     * @return This builder
     */
    T between(T lowerBoundExpression, T upperBoundExpression);

    /**
     * Creates a new collection-valued path expression.
     *
     * @param path The collection-valued path expression
     * @return This builder
     */
    T collectionPath(String path);

    T different(Number number);
    T different(String literal);
    T different(T builder);
    T equal(Number number);
    T equal(String literal);
    T equal(T builder);
    T exists(SimpleSelectStatementStateObject subquery);
    T FALSE();
    T greaterThan(Number number);
    T greaterThan(String literal);
    T greaterThan(T builder);
    T greaterThanOrEqual(Number number);
    T greaterThanOrEqual(String literal);
    T greaterThanOrEqual(T builder);
    T in(SimpleSelectStatementStateObject subquery);
    T in(String... inItems);
    T in(T... inItems);
    T isEmpty(String path);
    T isNotEmpty(String path);
    T isNotNull(String path);
    T isNull(String path);
    T like(String patternValue);
    T like(T patternValue);
    T like(T patternValue, String escapeCharacter);
    T lower(T builder);
    T lowerThan(Number number);
    T lowerThan(String literal);
    T lowerThan(T builder);
    T lowerThanOrEqual(Number number);
    T lowerThanOrEqual(String literal);
    T lowerThanOrEqual(T builder);
    T member(String path);
    T memberOf(String path);
    T notBetween(T lowerBoundExpression, T upperBoundExpression);
    T notExists(SimpleSelectStatementStateObject subquery);
    T notIn(SimpleSelectStatementStateObject subquery);
    T notIn(String... inItems);
    T notIn(T... inItems);
    T notLike(String patternValue);
    T notLike(T builder);
    T notLike(T builder, String escapeCharacter);
    T notMember(String path);
    T notMemberOf(String path);
    T NULL();
    T or(T builder);
    T some(SimpleSelectStatementStateObject subquery);
    T sub(StateObject stateObject);
    T substring(T parameter1, T parameter2, T parameter3);
    T trim(Specification specification, String trimCharacter, T builder);
    T trim(Specification specification, T builder);
    T TRUE();
    T upper(T builder);

    /**
     * Creates the expression representing an identification variable.
     *
     * @param variable The identification variable
     * @return This builder
     */
    T variable(String variable);
}
