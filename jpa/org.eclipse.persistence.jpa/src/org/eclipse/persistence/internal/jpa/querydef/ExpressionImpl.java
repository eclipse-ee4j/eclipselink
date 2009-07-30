/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
 ******************************************************************************/package org.eclipse.persistence.internal.jpa.querydef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * <p>
 * <b>Purpose</b>: Represents an Expression in the Criteria API heirarchy.
 * <p>
 * <b>Description</b>: Expressions are expression nodes that can not be joined from 
 * and may or not be the result of a Path expression.
 * <p>
 * 
 * @see javax.persistence.criteria Expression
 * 
 * @author gyorke
 * @since EclipseLink 2.0
 */
public class ExpressionImpl<X> extends SelectionImpl<X> implements Expression<X> {
    protected Metamodel metamodel;
    protected boolean isLiteral;
    protected Object literal;
    
    public <T> ExpressionImpl(Metamodel metamodel, Class<X> javaType, org.eclipse.persistence.expressions.Expression expressionNode){
        super(javaType, expressionNode);
        this.metamodel = metamodel;
    }
    
    public <T> ExpressionImpl(Metamodel metamodel, Class<X> javaType, org.eclipse.persistence.expressions.Expression expressionNode, Object value){
        super(javaType, expressionNode);
        this.metamodel = metamodel;
        this.literal = value;
        this.isLiteral = true;
    }

    public <X> Expression<X> as(Class<X> type) {
        // TODO Auto-generated method stub
        return (Expression<X>) this;
    }
    
    public Predicate in(Object... values) {
        List list = new ArrayList();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(values), list, "in");
    }

    /**
     * Apply a predicate to test whether the expression is a member
     * of the argument list.
     * @param values
     * @return predicate testing for membership
     */
    public Predicate in(Expression<?>... values) {
        List list = new ArrayList();
        list.add(this);
        for (Expression exp: values){
            if (!((ExpressionImpl)exp).isLiteral()){
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("CRITERIA_NON_LITERAL_PASSED_TO_IN_TODO"));
            }
        }
        
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(values), list, "in");
    }

    /**
     * Apply a predicate to test whether the expression is a member
     * of the collection.
     * @param values collection
     * @return predicate testing for membership
     */
    public Predicate in(Collection<?> values) {
        List list = new ArrayList();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(values), list, "in");
    }
    /**
     * Apply a predicate to test whether the expression is a member
     * of the collection.
     * @param values expression corresponding to collection
     * @return predicate testing for membership
     */
    public Predicate in(Expression<Collection<?>> values) {
        List list = new ArrayList();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(((ExpressionImpl)values).getCurrentNode()), list, "in");
    }
    
    public Predicate isNotNull() {
        List list = new ArrayList();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.notNull(), list, "not null");
    }

    
    public Predicate isNull() {
        List list = new ArrayList();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.isNull(), list, "is null");
    }
    
    public boolean isPredicate(){
        return false;
    }

    public boolean isCompoundExpression(){
        return false;
    }
    public boolean isExpression(){
        return true;
    }
    
    public boolean isLiteral(){
        return this.isLiteral;
    }
    protected void findRoot(Set<Root<?>> roots){
        //no-op because an expression will have no root
    }
    
    protected Object getValue(){
        return this.getValue();
    }
}
