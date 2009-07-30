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
 * @since EclipseLink 2.0
 */
public class CompoundExpressionImpl extends ExpressionImpl<Boolean> implements Predicate {
    
    protected String operator;
    protected List expressions;
    
    public <T> CompoundExpressionImpl (Metamodel metamodel, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions){
        super(metamodel, (Class<Boolean>)ClassConstants.BOOLEAN, expressionNode);
        this.expressions = new ArrayList(compoundExpressions);
    }

    public <T> CompoundExpressionImpl (Metamodel metamodel, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions, String operator){
        this(metamodel, expressionNode, compoundExpressions);
        this.operator = operator;
    }
    /**
     * Return the boolean operator for the predicate. If the predicate is
     * simple, this is AND.
     * 
     * @return boolean operator for the predicate
     */
    public BooleanOperator getOperator(){
        return BooleanOperator.AND;
    }

    /**
     * Has negation been applied to the predicate.
     * 
     * @return boolean indicating if the predicate has been negated
     */
    public boolean isNegated(){
        return false;
    }

    /**
     * Return the top-level conjuncts or disjuncts of the predicate.
     * 
     * @return list boolean expressions forming the predicate
     */
    public List<Expression<Boolean>> getExpressions(){
        return new ArrayList();
    }

    /**
     * Apply negation to the predicate.
     * 
     * @return the negated predicate
     */
    public Predicate negate(){
        if (this.currentNode == null){
            return new PredicateImpl(this.metamodel, null, null, BooleanOperator.NOT);
        }
        List<Expression<?>> list = new ArrayList();
        list.add(this);
        return new PredicateImpl(this.metamodel, this.currentNode.not(), list, BooleanOperator.NOT);
    }

    @Override
    public boolean isCompoundExpression(){
        return true;
    }
    
    @Override
    public boolean isExpression(){
        return false;
    }

    
    protected void findRoot(Set<Root<?>> roots){
        if (this.expressions != null){
            for (Object exp : this.expressions){
                ((ExpressionImpl)exp).findRoot(roots);
            }   
        }
    }



}
