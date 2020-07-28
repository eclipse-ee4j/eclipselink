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
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.metamodel.Metamodel;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the Predicate interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: The predicate forms the least specific expression node.  Predicates
 * result in boolean expressions that are combined to form the final expression.
 * <p>
 *
 * @see javax.persistence.criteria Predicate
 *
 * @author gyorke
 * @since EclipseLink 1.2
 */
public class FunctionExpressionImpl<X> extends ExpressionImpl<X>{

    protected String operator;
    protected List expressions;

    protected <T> FunctionExpressionImpl (Metamodel metamodel, Class<X> resultClass, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions){
        super(metamodel, resultClass, expressionNode);
        if (compoundExpressions != null){
            this.expressions = compoundExpressions;
        }else{
            this.expressions = new ArrayList();
        }
    }

    public <T> FunctionExpressionImpl (Metamodel metamodel, Class<X> resultClass, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions, String operator){
        this(metamodel, resultClass, expressionNode, compoundExpressions);
        this.operator = operator;
    }

    /**
     * Return the top-level conjuncts or disjuncts of the predicate.
     *
     * @return list boolean expressions forming the predicate
     */
    public List<Expression<?>> getChildExpressions(){
        return expressions;
    }
    /**
     * @return the operator
     */
    public String getOperation() {
        return operator;
    }

    @Override
    public boolean isCompoundExpression(){
        return true;
    }

    @Override
    public boolean isExpression(){
        return false;
    }

    public void findRootAndParameters(CommonAbstractCriteriaImpl query){
        if (this.expressions != null){
            for (Object exp : this.expressions){
                ((InternalSelection)exp).findRootAndParameters(query);
            }
        }
    }
}
