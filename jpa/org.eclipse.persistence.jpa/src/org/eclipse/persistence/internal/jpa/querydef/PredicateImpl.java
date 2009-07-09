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

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
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
 * @since EclipseLink 2.0
 */
public class PredicateImpl extends ExpressionImpl<Boolean> implements Predicate {
    
    protected BooleanOperator operator;
    protected List<Expression<Boolean>> expressions;
    
    public <T> PredicateImpl (Metamodel metamodel, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<Boolean>> parentExpressions, BooleanOperator operator){
        super(metamodel, (Class<Boolean>)ClassConstants.BOOLEAN, expressionNode);
        this.expressions = new ArrayList<Expression<Boolean>>(parentExpressions);
        this.operator = operator;
    }

    /**
     * Return the boolean operator for the predicate. If the predicate is
     * simple, this is AND.
     * 
     * @return boolean operator for the predicate
     */
    public BooleanOperator getOperator(){
        return this.operator;
    }

    /**
     * Has negation been applied to the predicate.
     * 
     * @return boolean indicating if the predicate has been negated
     */
    public boolean isNegated(){
        return this.operator == BooleanOperator.NOT;
    }

    /**
     * Return the top-level conjuncts or disjuncts of the predicate.
     * 
     * @return list boolean expressions forming the predicate
     */
    public List<Expression<Boolean>> getExpressions(){
        return this.expressions;
    }

    /**
     * Apply negation to the predicate.
     * 
     * @return the negated predicate
     */
    public Predicate negate(){
        if (this.currentNode == null){
            return new PredicateImpl(this.metamodel, null, new ArrayList(), BooleanOperator.NOT);
        }
        List<Expression<Boolean>> list = new ArrayList<Expression<Boolean>>();
        list.add(this);
        return new PredicateImpl(this.metamodel, this.currentNode.not(), list, BooleanOperator.NOT);
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(BooleanOperator operator) {
        this.operator = operator;
    }
    @Override
    public boolean isPredicate(){
        return true;
    }


}
