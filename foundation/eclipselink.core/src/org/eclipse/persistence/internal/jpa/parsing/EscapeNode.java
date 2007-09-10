/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.expressions.*;

public class EscapeNode extends LogicalOperatorNode {

    public EscapeNode() {
    }

    /**
     * INTERNAL
     * Is this an escape node
     */
    public boolean isEscape() {
        return true;// Yes it is
    }

    /**
     * INTERNAL
     * Validate the current node and calculates its type.
     */
    public void validate(ParseTreeContext context) {
        
        TypeHelper typeHelper = context.getTypeHelper();
        if (left != null) {
            left.validate(context);
            left.validateParameter(context, typeHelper.getCharType());
        }
        
        setType(getType());
    }

    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = left.generateExpression(context);
        return whereClause;

    }
}
