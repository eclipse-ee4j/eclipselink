/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Gordon Yorke - Initial development
 *
 ******************************************************************************/
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

    public Expression<?> getExpression() {
        return this.expression;
    }

    public boolean isAscending() {
        return this.isAscending;
    }

    public Order reverse() {
        return new OrderImpl(this.expression, false);
    }

}
