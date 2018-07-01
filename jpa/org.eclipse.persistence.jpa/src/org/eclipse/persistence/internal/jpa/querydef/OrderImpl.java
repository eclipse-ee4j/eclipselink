/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;

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

    @Override
    public Order reverse() {
        return new OrderImpl(this.expression, false);
    }

}
