/*
 * Copyright (c) 2013, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Gordon Yorke - Initial development
//     08/31/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serializable;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.Order;

/**
 * An object that defines an ordering over the query results.
 */
public class OrderImpl implements Order, Serializable{

    protected Expression expression;
    protected boolean isAscending;
    protected Nulls nullPrecedence;

    /**
     * Creates an instance of ordering over the query results definition.
     *
     * @param expression the query expression
     * @param isAscending whether ascending ordering is in effect
     * @param nullPrecedence the precedence of {@code null} values within query result sets
     */
    public OrderImpl(Expression expression, boolean isAscending, Nulls nullPrecedence) {
        this.expression = expression;
        this.isAscending = isAscending;
        this.nullPrecedence = nullPrecedence;
    }

    @Override
    public Expression<?> getExpression() {
        return this.expression;
    }

    @Override
    public boolean isAscending() {
        return this.isAscending;
    }

    @Override
    public Nulls getNullPrecedence() {
        return nullPrecedence;
    }

    @Override
    public Order reverse() {
        return new OrderImpl(expression, !isAscending, nullPrecedence);
    }

    public void findRootAndParameters(CommonAbstractCriteriaImpl query) {
        if(expression != null) {
            ((InternalSelection)expression).findRootAndParameters(query);
        }
    }

}
