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
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.querydef;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.internal.helper.ClassConstants;

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
public class PredicateImpl extends CompoundExpressionImpl implements Predicate {
    
    protected BooleanOperator booloperator;
    
    public <T> PredicateImpl (Metamodel metamodel, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> parentExpressions, BooleanOperator operator){
        super(metamodel, expressionNode, parentExpressions);
        this.booloperator = operator;
    }

    /**
     * Return the boolean operator for the predicate. If the predicate is
     * simple, this is AND.
     * 
     * @return boolean operator for the predicate
     */
    public BooleanOperator getOperator(){
        return this.booloperator;
    }

    /**
     * Has negation been applied to the predicate.
     * 
     * @return boolean indicating if the predicate has been negated
     */
    public boolean isNegated(){
        return this.booloperator == BooleanOperator.NOT;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(BooleanOperator operator) {
        this.booloperator = operator;
    }
    @Override
    public boolean isPredicate(){
        return true;
    }
    
    @Override
    public boolean isCompoundExpression(){
        return false;
    }

    @Override
    public boolean isExpression(){
        return false;
    }



}
