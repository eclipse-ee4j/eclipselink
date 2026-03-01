/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.expressions;

import org.eclipse.persistence.expressions.Expression;

import java.util.List;

/**
 * Used to iterate an expression tree, through inner subclasses.
 * @param <T> result type
 */
public abstract class ExpressionIterator<T> {

    /** Allow the iteration to build a result. */
    protected T result;

    /** Some iterations require a statement. */
    protected SQLSelectStatement statement;

    /** Some iterations require a more general parameter. */
    protected Object parameter;

    /**
     * Block constructor comment.
     */
    protected ExpressionIterator() {
        super();
    }

    public T getResult() {
        return result;
    }

    public SQLSelectStatement getStatement() {
        return statement;
    }

    /**
     * Answers if this expression has already been visited.
     * For a faster iteration override to insure expressions are only
     * visited/processed once.
     */
    public boolean hasAlreadyVisited(Expression expression) {
        return false;
    }

    /**
     * INTERNAL:
     * This method must be defined by subclasses to implement the logic of the iteration.
     */
    public abstract void iterate(Expression expression);

    /**
     * INTERNAL:
     */
    public void iterateOn(List<Expression> expressions) {
        for (Expression expression : expressions) {
            iterate(expression);
        }
    }

    /**
     * INTERNAL:
     * Return the call.
     */
    public void iterateOn(Expression expression) {
        expression.iterateOn(this);
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setStatement(SQLSelectStatement statement) {
        this.statement = statement;
    }

    /**
     * Normally an Iterator will not go into the where clause of
     * an SQLSubSelectExpression.  I.e. when aliasing the parent statement
     * is aliased before the subselects may even be normalized.  An iterator to
     * alias the SubSelect must be run later.
     */
    public boolean shouldIterateOverSubSelects() {
        return false;
    }
}
