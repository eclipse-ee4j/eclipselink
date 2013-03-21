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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import java.util.*;

/**
 * INTERNAL:
 * This node holds a list of all the updates that will occur in an Update Query.
 * Slightly different from other nodes since holds more than two children in a list.
 */
public class SetNode extends MajorNode {
    private List assignmentNodes = null;

    public SetNode() {
        super();
        assignmentNodes = new Vector();
    }

    /**
     * Iterate through the updates in this query and build expressions for them.  Set the
     * built expressions on the query.
     */
    public void addUpdatesToQuery(UpdateAllQuery theQuery, GenerationContext context) {
        Iterator iterator = assignmentNodes.iterator();
        while (iterator.hasNext()) {
            EqualsAssignmentNode node = (EqualsAssignmentNode)iterator.next();
            Expression leftExpression = getExpressionForNode(node.getLeft(), theQuery.getReferenceClass(), context);
            Expression rightExpression = getExpressionForNode(node.getRight(), theQuery.getReferenceClass(), context);
            theQuery.addUpdate(leftExpression, rightExpression);
        }
    }

    /** 
     * INTERNAL 
     * Check the update item node for a path expression starting with a
     * unqualified field access and if so, replace it by a qualified field
     * access. 
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        for (Iterator i = assignmentNodes.iterator(); i.hasNext(); ) {
            Node item = (Node)i.next();
            item.qualifyAttributeAccess(context);
        }
        return this;
    }
    
    /**
     * INTERNAL
     * Validate node.
     */
    public void validate(ParseTreeContext context) {
        for (Iterator i = assignmentNodes.iterator(); i.hasNext(); ) {
            Node item = (Node)i.next();
            item.validate(context);
        }
    }

    /**
     * Create an expression to represent one of the nodes on a SetToNode.
     * We will assume that set_to nodes change elements that are direct mappings on the reference
     * class of the query.
     */
    protected Expression getExpressionForNode(Node node, Class referenceClass, GenerationContext context) {
        Expression expression = null;
        if (node.isAttributeNode()) {
            // look up a preexisting expression based on the reference class of the query.
            String classVariable = context.getParseTreeContext().getVariableNameForClass(referenceClass, context);
            expression = context.expressionFor(classVariable);
            if (expression == null) {
                expression = new ExpressionBuilder();
                context.addExpression(expression, classVariable);
            }
            expression = node.addToExpression(expression, context);
        } else {
            expression = node.generateExpression(context);
        }
        return expression;
    }

    /**
     * INTERNAL
     */
    public void setAssignmentNodes(List nodes) {
        assignmentNodes = nodes;
    }

}
