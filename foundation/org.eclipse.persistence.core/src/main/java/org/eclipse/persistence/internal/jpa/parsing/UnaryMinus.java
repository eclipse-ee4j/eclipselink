/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.expressions.ConstantExpression;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an unary '-' in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an unary '-'
 * </ul>
 */
public class UnaryMinus extends Node {

    public UnaryMinus() {
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
     * Generate the expression.
     */
    @Override
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = new ConstantExpression(Integer.valueOf(0), new ExpressionBuilder());
        whereClause = ExpressionMath.subtract(whereClause, getLeft().generateExpression(context));
        return whereClause;
    }

}
