/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

/**
 * INTERNAL
 * <p><b>Purpose</b>: This node represents an ORDER BY item
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for ORDER BY
 * </ul>
 *    @author Jon Driscoll
 *    @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.expressions.Expression;

public class OrderByItemNode extends Node {
    private SortDirectionNode direction = null;
    private Node orderByItem = null;

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        if (orderByItem != null) {
            orderByItem.validate(context);
            Object type = orderByItem.getType();
            setType(type);
            if (!typeHelper.isOrderableType(type)) {
                throw JPQLException.expectedOrderableOrderByItem(
                    context.getQueryInfo(), orderByItem.getLine(), orderByItem.getColumn(), 
                    orderByItem.getAsString(), typeHelper.getTypeName(type));
            }
        }
    }

    /** */
    public Expression generateExpression(GenerationContext context) {
        //BUG 3105651: Indicate to the VariableNodes in the subtree
        //that they should check the SelectNode before resolving.
        //If the variable involved is SELECTed, then we want an empty builder
        //instead (with an empty constructor).
        boolean oldCheckState = context.shouldCheckSelectNodeBeforeResolving();
        ((SelectGenerationContext)context).checkSelectNodeBeforeResolving(true);
        Expression orderByExpression = getOrderByItem().generateExpression(context);
        orderByExpression = getDirection().addToExpression(orderByExpression, context);
        ((SelectGenerationContext)context).checkSelectNodeBeforeResolving(oldCheckState);
        return orderByExpression;
    }

    public SortDirectionNode getDirection() {
        if (direction == null) {
            setDirection(new SortDirectionNode());
        }
        return direction;
    }

    public Node getOrderByItem() {
        return orderByItem;
    }

    public void setDirection(SortDirectionNode direction) {
        this.direction = direction;
    }

    public void setOrderByItem(Node orderByItem) {
        this.orderByItem = orderByItem;
    }
}
