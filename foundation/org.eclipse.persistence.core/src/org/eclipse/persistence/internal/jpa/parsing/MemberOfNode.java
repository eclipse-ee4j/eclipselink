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
 * <p><b>Purpose</b>: Represent the MEMBER-OF operator
 * <p><b>Responsibilities</b>:<ul>
 * <li> MEMBER OF is not supported.
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since since July 2003
 */
import org.eclipse.persistence.expressions.*;

public class MemberOfNode extends BinaryOperatorNode {
    private boolean notIndicated = false;

    //If we're dealing with a NOT, we store the expression for the left. 
    //When we get to the one-to-many on the right, it will handle the noneOf using 
    //the receiver stored in the context. 
    //(i.e. secondLastRightExpression.noneOf(lastRightVariable, leftExpression)
    private Expression leftExpression = null;

    /**
     * Return a new MemberOfNode
     */
    public MemberOfNode() {
        super();
    }

    /**
     * INTERNAL makeNodeOneToMany:
     * Traverse to the leaf on theNode and mark as one to many
     */
    public void makeNodeOneToMany(Node theNode) {
        Node currentNode = theNode;
        do {
            if (!currentNode.hasRight()) {
                ((AttributeNode)currentNode).setRequiresCollectionAttribute(true);
                return;
            }
            currentNode = currentNode.getRight();
        } while (true);
    }

    /**
     * INTERNAL
     * Validate node and calculates its type.
     */
    public void validate(ParseTreeContext context) {
        super.validate(context);
        Node left = getLeft();
        if (left.isVariableNode() && ((VariableNode)left).isAlias(context)) {
            context.usedVariable(((VariableNode)left).getCanonicalVariableName());
        }
        left.validateParameter(context, right.getType());
        TypeHelper typeHelper = context.getTypeHelper();
        setType(typeHelper.getBooleanType());
    }
    
    public Expression generateExpression(GenerationContext context) {
        // Need to make sure one of the node is marked as a one to many
        if (getRight().isParameterNode()) {
            makeNodeOneToMany(getLeft());
        } else {
            makeNodeOneToMany(getRight());
        }

        //Handle NOT. Store the expression for the left, let VariableNode handle it.
        if (notIndicated()) {
            Expression resultFromRight = null;
            context.setMemberOfNode(this);
            this.setLeftExpression(getLeft().generateExpression(context));
            resultFromRight = getRight().generateExpression(context);
            //clean up
            context.setMemberOfNode(null);
            this.setLeftExpression(null);
            return resultFromRight;
        } else {
            //otherwise, handle like normal anyOf()
            return getRight().generateExpression(context).equal(getLeft().generateExpression(context));
        }
    }

    /**
     * INTERNAL
     * Indicate if a NOT was found in the WHERE clause.
     * Examples:
     *        ...WHERE ... NOT MEMBER OF
     */
    public void indicateNot() {
        notIndicated = true;
    }

    public boolean notIndicated() {
        return notIndicated;
    }

    //set and get the leftExpression. This is for NOT MEMBER OF.
    public void setLeftExpression(Expression newLeftExpression) {
        leftExpression = newLeftExpression;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }
}
