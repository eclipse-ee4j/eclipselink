/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.expressions.*;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a BETWEEN in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for a BETWEEN in EJBQL
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class BetweenNode extends SimpleConditionalExpressionNode {
    protected Node rightForBetween;
    protected Node rightForAnd;

    /**
     * BetweenNode constructor comment.
     */
    public BetweenNode() {
        super();
    }

    /**
     * INTERNAL
     * Check the child nodes for an unqualified field access and if there are
     * any, replace them by a qualified field access.
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        if (left != null) {
            left = left.qualifyAttributeAccess(context);
        }
        if (rightForBetween != null) {
            rightForBetween = rightForBetween.qualifyAttributeAccess(context);
        }
        if (rightForAnd != null) {
            rightForAnd = rightForAnd.qualifyAttributeAccess(context);
        }
        return this;
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        Object type = null;
        if (left != null) {
            left.validate(context);
            type = left.getType();
        }
        if (rightForBetween != null) {
            rightForBetween.validate(context);
            rightForBetween.validateParameter(context, type);
        }
        if (rightForAnd != null) {
            rightForAnd.validate(context);
            rightForAnd.validateParameter(context, type);
        }
        TypeHelper typeHelper = context.getTypeHelper();
        setType(typeHelper.getBooleanType());
    }

    /**
     * INTERNAL
     * Return a EclipseLink expression by 'BETWEEN' and 'AND'ing the expressions from the left,
     * rightForBetween and rightForAnd nodes
     */
    public Expression generateExpression(GenerationContext context) {
        // Get the left expression
        Expression whereClause = getLeft().generateExpression(context);

        // Between it with whatever the rightForBetween expression and rightForAnd expressions are
        whereClause = whereClause.between(getRightForBetween().generateExpression(context), getRightForAnd().generateExpression(context));

        // and return the expression...
        return whereClause;
    }

    public Node getRightForAnd() {
        return rightForAnd;
    }

    public Node getRightForBetween() {
        return rightForBetween;
    }

    public boolean hasRightForAnd() {
        return rightForAnd != null;
    }

    public boolean hasRightForBetween() {
        return rightForBetween != null;
    }

    public void setRightForAnd(Node newRightForAnd) {
        rightForAnd = newRightForAnd;
    }

    public void setRightForBetween(Node newRightForBetween) {
        rightForBetween = newRightForBetween;
    }
}
