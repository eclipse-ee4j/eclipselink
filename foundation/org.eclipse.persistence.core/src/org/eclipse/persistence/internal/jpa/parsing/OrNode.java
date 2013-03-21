/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

import java.util.*;

import org.eclipse.persistence.expressions.*;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an OR
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an OR
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class OrNode extends LogicalOperatorNode {

    private Set leftOuterScopeVariables = null;
    private Set rightOuterScopeVariables = null;

    /**
     * Return a new OrNode.
     */
    public OrNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        Set saved = context.getOuterScopeVariables();
        if (left != null) {
            context.resetOuterScopeVariables();
            left.validate(context);
            leftOuterScopeVariables = context.getOuterScopeVariables();
        }
        if (right != null) {
            context.resetOuterScopeVariables();
            right.validate(context);
            rightOuterScopeVariables = context.getOuterScopeVariables();
        }
        context.resetOuterScopeVariables(saved);
        if ((left != null) && (right != null)) {
            left.validateParameter(context, right.getType());
            right.validateParameter(context, left.getType());
        }
        
        TypeHelper typeHelper = context.getTypeHelper();
        setType(typeHelper.getBooleanType());
    }

    /**
     * INTERNAL
     * Return a EclipseLink expression by 'OR'ing the expressions from the left and right nodes
     */
    public Expression generateExpression(GenerationContext context) {
        // Get the left expression
        Expression leftExpr = getLeft().generateExpression(context);
        leftExpr = appendOuterScopeVariableJoins(
            leftExpr, leftOuterScopeVariables, context);

        Expression rightExpr = getRight().generateExpression(context);
        rightExpr = appendOuterScopeVariableJoins(
            rightExpr, rightOuterScopeVariables, context);
        
        // Or it with whatever the right expression is
        return leftExpr.or(rightExpr);
    }

    /**
     * INTERNAL 
     */
    private Expression appendOuterScopeVariableJoins(
        Expression expr, Set outerScopeVariables, GenerationContext context) {
        if ((outerScopeVariables == null) || outerScopeVariables.isEmpty()) {
            // no outer scope variables => nothing to be done
            return expr;
        }
        Expression joins = context.joinVariables(outerScopeVariables);
        return appendExpression(expr, joins);
    }
    
}
