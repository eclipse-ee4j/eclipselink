/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.expressions.*;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an ABS in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an ABS in EJBQL
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class AbsNode extends ArithmeticFunctionNode {
    public AbsNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    @Override
    public void validate(ParseTreeContext context) {
        if (left != null) {
            left.validate(context);
            setType(left.getType());
        }
    }

    /**
     * INTERNAL
     */
    @Override
    public void validateParameter(ParseTreeContext context, Object contextType) {
        // delegate to the argument node
        left.validateParameter(context, contextType);
    }

    /**
     * INTERNAL
     * Generate the EclipseLink expression for this node
     */
    @Override
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = getLeft().generateExpression(context);
        whereClause = ExpressionMath.abs(whereClause);
        return whereClause;
    }

}
