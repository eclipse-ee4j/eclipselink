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


/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a Sort Direction for an
 * Order By Item
 * <p><b>Responsibilities</b>:<ul>
 * <li> Apply itself to a query correctly
 *
 * This node represents either an ASC or DESC encountered on the input stream
 * e.g SELECT ... FROM ... WHERE ... ORDER BY emp.salary ASC
 * </ul>
 *    @author Jon Driscoll
 *    @since TopLink 5.0
 */
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionOperator;

public class SortDirectionNode extends Node {
    private int sortDirection = ExpressionOperator.Ascending;

    /**
     * INTERNAL
     * Return the parent expression unmodified
     */
    public Expression addToExpression(Expression parentExpression, GenerationContext context) {
        return parentExpression.getFunction(getSortDirection());
    }

    public void useAscending() {
        setSortDirection(ExpressionOperator.Ascending);
    }

    public void useDescending() {
        setSortDirection(ExpressionOperator.Descending);
    }

    // Accessors
    public int getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(int sortDirection) {
        this.sortDirection = sortDirection;
    }
}
