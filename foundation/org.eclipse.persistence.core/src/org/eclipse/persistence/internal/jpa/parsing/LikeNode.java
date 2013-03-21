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

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a LIKE in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for a LIKE
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class LikeNode extends SimpleConditionalExpressionNode {
    private EscapeNode escape = null;

    /**
     * LikeNode constructor comment.
     */
    public LikeNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate the current node and calculates its type.
     */
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        if (left != null) {
            left.validate(context);
            left.validateParameter(context, typeHelper.getStringType());
        }
        if (right != null) {
            right.validate(context);
            right.validateParameter(context, typeHelper.getStringType());
        } 
        
        if (escape != null) {
            escape.validate(context);
        }
        
        setType(typeHelper.getBooleanType());
    }

    /**
     * INTERNAL
     * Return a EclipseLink expression for this node.
     */
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = getLeft().generateExpression(context);
        if (!hasEscape()) {
            whereClause = whereClause.like(getRight().generateExpression(context));
        } else {
            whereClause = whereClause.like(getRight().generateExpression(context), getEscapeNode().generateExpression(context));
        }
        return whereClause;
    }

    public boolean hasEscape() {
        return getEscapeNode() != null;
    }

    // Accessors
    public EscapeNode getEscapeNode() {
        return escape;
    }

    public void setEscapeNode(EscapeNode node) {
        escape = node;
    }
}
