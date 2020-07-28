/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.sessions.Project;

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
 * @since EclipseLink 1.2
 */
public class ExpressionImpl<X> extends SelectionImpl<X> implements Expression<X>, InternalExpression{
    protected Metamodel metamodel;
    protected boolean isLiteral;
    protected Object literal;

    protected ExpressionImpl(Metamodel metamodel, Class<X> javaType, org.eclipse.persistence.expressions.Expression expressionNode){
        super(javaType, expressionNode);
        this.metamodel = metamodel;
    }

    public ExpressionImpl(Metamodel metamodel, Class<X> javaType, org.eclipse.persistence.expressions.Expression expressionNode, Object value){
        super(javaType, expressionNode);
        this.metamodel = metamodel;
        this.literal = value;
        this.isLiteral = true;
    }

    public <T> Expression<T> as(Class<T> type) {
        Project project = ((MetamodelImpl)metamodel).getProject();
        if (project != null){
            ClassDescriptor descriptor = project.getClassDescriptor(javaType);
            if (descriptor != null && descriptor.hasInheritance()){
                descriptor = descriptor.getInheritancePolicy().getSubclassDescriptor(type);
                if (descriptor != null){
                    return buildExpressionForAs(type);
                }
            }
        }
        return (Expression<T>) this;
    }

    protected <T> Expression<T> buildExpressionForAs(Class<T> type) {
        return (Expression<T>) this;
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
        if (values != null) {
            if (values.length == 1 && ((InternalExpression) values[0]).isParameter()
                    && Collection.class.isAssignableFrom(((ParameterExpressionImpl) values[0]).getJavaType())) {
                // bug 349477 - Collection from Expression<Collection> was lost during compilation
                // and if we know that Collection is there we should help the runtime
                // and route the execution to the right method
                return in((Expression<Collection<?>>) values[0]);
            }
            List list = new ArrayList();
            list.add(this);
            if (values.length == 1 && ((InternalExpression) values[0]).isSubquery()) {
                list.add(values[0]);
                return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(((SubQueryImpl) values[0]).subQuery), list, "in");
            } else {
                List<Object> inValues = new ArrayList<Object>();
                for (Expression exp : values) {
                    if (!((InternalExpression) exp).isLiteral() && !((InternalExpression) exp).isParameter()) {
                        Object[] params = new Object[]{exp};
                        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("CRITERIA_NON_LITERAL_PASSED_TO_IN",params));
                    } else {
                        list.add(exp);
                        inValues.add(((InternalSelection)exp).getCurrentNode());
                    }
                }

                return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(inValues), list, "in");
            }
        }
        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NULL_PASSED_TO_EXPRESSION_IN"));
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
        return new InImpl(this.metamodel, this, values, list);
    }
    /**
     * Apply a predicate to test whether the expression is a member
     * of the collection.
     * @param values expression corresponding to collection
     * @return predicate testing for membership
     */
    public Predicate in(Expression<Collection<?>> values) {
        List list = new ArrayList();
        list.add(values);
        list.add(this);
        return new InImpl(metamodel, this, (ExpressionImpl)values, list);
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

    public boolean isSubquery(){
        return false;
    }

    public boolean isCompoundExpression(){
        return false;
    }
    public boolean isExpression(){
        return true;
    }

    public boolean isJunction(){
        return false;
    }

    public boolean isLiteral(){
        return this.isLiteral;
    }
    public boolean isParameter(){
        return false;
    }
    public void findRootAndParameters(CommonAbstractCriteriaImpl criteriaQuery){
        //no-op because an expression will have no root
    }

}
