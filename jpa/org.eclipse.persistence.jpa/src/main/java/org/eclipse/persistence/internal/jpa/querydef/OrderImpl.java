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
//
package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serializable;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.Order;

public class OrderImpl implements Order, Serializable{

    protected Expression expression;
    protected boolean isAscending;

    public OrderImpl(Expression expression){
        this(expression, true);
    }

    public OrderImpl(Expression expression, boolean isAscending){
        this.expression = expression;
        this.isAscending = isAscending;
    }

    @Override
    public Expression<?> getExpression() {
        return this.expression;
    }

    @Override
    public boolean isAscending() {
        return this.isAscending;
    }

    // TODO-API-3.2
    @Override
    public Nulls getNullPrecedence() {
        throw new UnsupportedOperationException("Jakarta Persistence 3.2 API was not implemented yet");
    }

    @Override
    public Order reverse() {
        return new OrderImpl(this.expression, false);
    }

    public void findRootAndParameters(CommonAbstractCriteriaImpl query) {
        if(expression != null) {
            ((InternalSelection)expression).findRootAndParameters(query);
        }
    }

}
