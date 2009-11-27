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
