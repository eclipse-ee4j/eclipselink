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
 * INTERNAL:
 * EqualsAssignmentNode is implemented to distinguish nodes that hold updates in an update
 * query from other BinaryOperatorNodes
 */
public class EqualsAssignmentNode extends BinaryOperatorNode {

    /**
     * INTERNAL
     * Validate the current node and calculates its type.
     */
    public void validate(ParseTreeContext context) {
        // look for any field access that is not qualified with an variable
        super.validate(context);
        validateTarget(left, context);
    }

    /** */
    private void validateTarget(Node node, ParseTreeContext context) {
        if (node.isDotNode()) {
            TypeHelper typeHelper = context.getTypeHelper();
            Node path = node.getLeft();
            Object type = path.getType();
            AttributeNode attributeNode = (AttributeNode)node.getRight();
            String attribute = attributeNode.getAttributeName();
            if (typeHelper.isSingleValuedRelationship(type, attribute) || 
                typeHelper.isSimpleStateAttribute(type, attribute)) {
                validateNavigation(path, context);
            } else {
                throw JPQLException.invalidSetClauseTarget(
                    context.getQueryInfo(), attributeNode.getLine(), 
                    attributeNode.getColumn(), path.getAsString(), attribute);
            }
        }
    }

    /** */
    private void validateNavigation(Node qualifier, ParseTreeContext context) {
        if (qualifier.isDotNode()) {
            TypeHelper typeHelper = context.getTypeHelper();
            Node left = qualifier.getLeft();
            AttributeNode attributeNode = (AttributeNode)qualifier.getRight();
            String attribute = attributeNode.getAttributeName();
            Object type = left.getType();
            if (!typeHelper.isEmbeddedAttribute(type, attribute)) {
                throw JPQLException.invalidSetClauseNavigation(
                    context.getQueryInfo(), attributeNode.getLine(), 
                    attributeNode.getColumn(), qualifier.getAsString(), attribute);
            }
            validateNavigation(left, context);
        }
    }
    
}
