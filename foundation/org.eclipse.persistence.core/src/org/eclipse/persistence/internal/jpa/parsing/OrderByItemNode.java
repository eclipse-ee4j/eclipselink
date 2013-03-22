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
    private Object orderByItem = null;
    private Node orderNode = null;

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        if (orderNode == null){
            if (orderByItem instanceof Node){
                orderNode = (Node)orderByItem;
            } else {
                orderNode = context.pathForVariable((String)orderByItem);
                if (orderNode == null){
                    throw JPQLException.nonExistantOrderByAlias(
                            context.getQueryInfo(), getLine(), getColumn(), 
                            (String)orderByItem);
                }
            }
        }
        if (orderNode != null) {
            orderNode.validate(context);
            Object type = orderNode.getType();
            setType(type);
            if (!typeHelper.isOrderableType(type)) {
                throw JPQLException.expectedOrderableOrderByItem(
                    context.getQueryInfo(), orderNode.getLine(), orderNode.getColumn(), 
                    orderNode.getAsString(), typeHelper.getTypeName(type));
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
        Expression orderByExpression = null;
        if (orderByItem instanceof Node){
            orderNode = (Node)orderByItem;
            orderByExpression = orderNode.generateExpression(context);
        } else {
            orderByExpression = context.expressionFor((String)orderByItem);
            if (orderByExpression == null){
                return null;
            }
        }
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

    public Object getOrderByItem() {
        return orderByItem;
    }

    public void setDirection(SortDirectionNode direction) {
        this.direction = direction;
    }

    public void setOrderByItem(Object orderByItem) {
        this.orderByItem = orderByItem;
    }
}
