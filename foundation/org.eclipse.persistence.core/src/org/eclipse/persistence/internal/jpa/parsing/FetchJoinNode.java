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

import org.eclipse.persistence.exceptions.JPQLException;
/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a FETCH JOIN declaration as part of the FROM
 * clause: FETCH JOIN o.customer.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Manage the path node and the outer join flag of the FETCH JOIN clause. 
 * </ul>
 */
public class FetchJoinNode extends Node {

    private Node path;
    private boolean outerJoin;

    /** */
    public Node getPath() {
        return path;
    }
    
    /** */
    public void setPath(Node node) {
        path = node;
    }
    
    /** */
    public boolean isOuterJoin() {
        return outerJoin;
    }

    /** */
    public void setOuterJoin(boolean outerJoin) {
        this.outerJoin = outerJoin;
    }

    /** 
     * INTERNAL 
     * Check the path child node for an unqualified field access and if so,
     * replace it by a qualified field access.
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        if (path != null) {
            path = path.qualifyAttributeAccess(context);
        }
        return this;
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        if (path != null) {
            path.validate(context);
            setType(path.getType());

            // join of embedded attribute is not supported.
            if (path.isDotNode()) {
                TypeHelper typeHelper = context.getTypeHelper();
                VariableNode left = (VariableNode)path.getLeft();
                AttributeNode right = (AttributeNode)path.getRight();
                if ((left != null) && (right != null)) {
                    if (typeHelper.isEmbeddedAttribute(left.getType(), right.getAttributeName()))
                        throw JPQLException.unsupportJoinArgument(
                            context.getQueryInfo(), getLine(), getColumn(), 
                            "Fetch Join", getType().toString());
                }
            }
        }
    }
}
