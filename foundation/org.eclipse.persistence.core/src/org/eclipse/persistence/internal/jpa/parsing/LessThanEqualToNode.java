/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.expressions.Expression;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a '&lt;=' in EJBQL
 * <p><b>Responsibilities</b>:
 * <ul>
 * <li> Generate the correct expression for a '&lt;=' in EJBQL</li>
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class LessThanEqualToNode extends BinaryOperatorNode {

    /**
     * LessThanEqualToNode constructor comment.
     */
    public LessThanEqualToNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate the current node and calculates its type.
     */
    @Override
    public void validate(ParseTreeContext context) {
        super.validate(context);
        TypeHelper typeHelper = context.getTypeHelper();
        setType(typeHelper.getBooleanType());
    }

    /**
     * INTERNAL
     * Resolve the expression. The steps are:
     * 1. Set the expressionBuilder for the left and right nodes
     * 2. Generate the expression for the left node
     * 3. Add the .lessThanEqualTo to the where clause returned from step 2
     * 4. Generate the expression for the right side and use it as the parameter for the .lessThanEqualTo()
     * 5. Return the completed where clause to the caller
     */
    @Override
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = getLeft().generateExpression(context);
        whereClause = whereClause.lessThanEqual(getRight().generateExpression(context));
        return whereClause;
    }

    /**
     * INTERNAL
     * Get the string representation of this node.
     */
    @Override
    public String getAsString() {
        return left.getAsString() + " <= " + right.getAsString();
    }
}
