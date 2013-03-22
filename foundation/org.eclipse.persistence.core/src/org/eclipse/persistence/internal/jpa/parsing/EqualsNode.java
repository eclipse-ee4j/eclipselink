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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.JPQLException;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an '=' in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an '=' in EJBQL
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class EqualsNode extends BinaryOperatorNode {

    /**
     * EqualsNode constructor comment.
     */
    public EqualsNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate node and calculates its type.
     */
    public void validate(ParseTreeContext context) {
        super.validate(context);
        TypeHelper typeHelper = context.getTypeHelper();
        Object leftType = left.getType();
        Object rightType = right.getType();
        if (typeHelper.isEnumType(leftType) && !typeHelper.isEnumType(rightType)) {
            throw JPQLException.invalidEnumEqualExpression( 
                context.getQueryInfo(), getLine(), getColumn(), 
                typeHelper.getTypeName(leftType), typeHelper.getTypeName(rightType));
        } else if (typeHelper.isEnumType(rightType) && !typeHelper.isEnumType(leftType)) {
            throw JPQLException.invalidEnumEqualExpression( 
                context.getQueryInfo(), getLine(), getColumn(),
                typeHelper.getTypeName(rightType), typeHelper.getTypeName(leftType));
        }
                
        setType(typeHelper.getBooleanType());
    }

    /**
     * Resolve the expression. The steps are:
     * 1. Set the expressionBuilder for the left and right nodes
     * 2. Generate the expression for the left node
     * 3. Add the .equals to the where clause returned from step 2
     * 4. Generate the expression for the right side and use it as the parameter for the .equals()
     * 5. Return the completed where clause to the caller
     */
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = getLeft().generateExpression(context);
        whereClause = whereClause.equal(getRight().generateExpression(context));
        return whereClause;
    }

    /**
     * INTERNAL
     * Get the string representation of this node.
     */
    public String getAsString() {
        return left.getAsString() + " = " + right.getAsString();
    }
}
