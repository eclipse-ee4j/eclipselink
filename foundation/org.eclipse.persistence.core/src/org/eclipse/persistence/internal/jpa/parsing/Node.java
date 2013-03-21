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
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: This is the superclass for all Nodes.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Answer default answers for all method calls
 * <li> Delegate most responsibilities to the sub-classes
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class Node {
    private int line;
    private int column;
    protected Node left = null;
    protected Node right = null;
    private Object type;
    public boolean shouldGenerateExpression;
    protected String alias = null;
    
    /**
     * Return a new Node.
     */
    public Node() {
        super();
    }

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
    }

    /**
     * INTERNAL
     * Add my expression semantics to the parentExpression. Each subclass will add a different expression and
     * thus will need to override this method
     */
    public Expression addToExpression(Expression parentExpression, GenerationContext context) {
        return parentExpression;
    }

    /**
     * INTERNAL
     * Get the string representation of this node.
     * By default return toString()
     */
    public String getAsString() {
        return toString();
    }

    /** 
     * INTERNAL 
     * Check the child node for an unqualified field access and if so,
     * replace it by a qualified field access.
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        if (left != null) {
            left = left.qualifyAttributeAccess(context);
        }
        if (right != null) {
            right = right.qualifyAttributeAccess(context);
        }
        return this;
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        // Nothing to be validated here, but delegate to the child nodes.
        if (left != null) {
            left.validate(context);
        }
        if (right != null) {
            right.validate(context);
        }
    }

    /**
     * INTERNAL 
     */
    public void validateParameter(ParseTreeContext context, Object contextType) {
        // nothing to be done
    }

    /**
     * INTERNAL
     * Generate an expression for the node. Each subclass will generate a different expression and
     * thus will need to override this method
     */
    public Expression generateExpression(GenerationContext context) {
        return null;
    }

    /**
     * INTERNAL
     * Return the left node
     */
    public Node getLeft() {
        return left;
    }

    /**
     * INTERNAL
     * Return the right node
     */
    public Node getRight() {
        return right;
    }

    /**
     * INTERNAL
     * Does this node have a left
     */
    public boolean hasLeft() {
        return getLeft() != null;
    }

    /**
     * INTERNAL
     * Does this node have a right
     */
    public boolean hasRight() {
        return getRight() != null;
    }

    /**
     * INTERNAL
     * Is this node an Aggregate node
     */
    public boolean isAggregateNode() {
        return false;
    }
    
    /**
     * INTERNAL
     * Is this node a Dot node
     */
    public boolean isDotNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this a literal node
     */
    public boolean isLiteralNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this node a Multiply node
     */
    public boolean isMultiplyNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this node a Not node
     */
    public boolean isNotNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this a Parameter node
     */
    public boolean isParameterNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this node a Divide node
     */
    public boolean isDivideNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this node a Plus node
     */
    public boolean isPlusNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this node a MapKey node
     */
    public boolean isMapKeyNode() {
        return false;
    }
    
    /**
     * INTERNAL
     * Is this node a Minus node
     */
    public boolean isMinusNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this node a VariableNode
     */
    public boolean isVariableNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this node an AttributeNode
     */
    public boolean isAttributeNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this node a CountNode
     */
    public boolean isCountNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this node a ConstructorNode
     */
    public boolean isConstructorNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this node a SubqueryNode
     */
    public boolean isSubqueryNode() {
        return false;
    }

    /**
     * INTERNAL
     * Is this an escape node
     */
    public boolean isEscape() {
        return false;// no it is not
    }

    /**
     * resolveAttribute(): Answer the name of the attribute which is represented by the receiver.
     * Subclasses should override this.
     */
    public String resolveAttribute() {
        return "";
    }

    /**
     * resolveClass: Answer the class associated with the content of this node. Default is to return null.
     * Subclasses should override this.
     */
    public Class resolveClass(GenerationContext context) {
        return null;
    }
    
    /**
     * resolveClass: Answer the class associated with the content of this node. Default is to return null.
     * Subclasses should override this.
     */
    public Class resolveClass(GenerationContext context, Class ownerClass) {
        return null;
    }

    /**
     * resolveMapping: Answer the mapping associated with the contained nodes.
     * Subclasses should override this.
     */
    public DatabaseMapping resolveMapping(GenerationContext context) {
        return null;
    }

    /**
     * resolveMapping: Answer the mapping associated with the contained nodes. Use the provided
     * class as the context.
     * Subclasses should override this.
     */
    public DatabaseMapping resolveMapping(GenerationContext context, Class ownerClass) {
        return null;
    }

    /**
     * INTERNAL
     * Set the left node to the passed value
     */
    public void setLeft(Node newLeft) {
        left = newLeft;
    }

    /**
     * INTERNAL
     * Set the right for this node
     */
    public void setRight(Node newRight) {
        right = newRight;
    }

    public int getLine() {
        return line;
    }
    
    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }
    
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * INTERNAL
     * Return the type of this node.
     */
    public Object getType() {
        return type;
    }

    /**
     * INTERNAL
     * Set this node's type.
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * INTERNAL
     * Returns left.and(right) if both are defined.
     */
    public Expression appendExpression(Expression left, Expression right) {
        Expression expr = null;
        if (left == null) {
            expr = right;
        } else if (right == null) {
            expr = left;
        } else {
            expr = left.and(right);
        }
        return expr;
    }
    
    public String toString() {
        try {
            return toString(1);
        } catch (Throwable t) {
            return t.toString();
        }
    }

    public String toString(int indent) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(toStringDisplayName());
        buffer.append("\r\n");
        toStringIndent(indent, buffer);
        if (hasLeft()) {
            buffer.append("Left: " + getLeft().toString(indent + 1));
        } else {
            buffer.append("Left: null");
        }

        buffer.append("\r\n");
        toStringIndent(indent, buffer);
        if (hasRight()) {
            buffer.append("Right: " + getRight().toString(indent + 1));
        } else {
            buffer.append("Right: null");
        }
        return buffer.toString();
    }

    public String toStringDisplayName() {
        return getClass().toString().substring(getClass().toString().lastIndexOf('.') + 1, getClass().toString().length());
    }

    public void toStringIndent(int indent, StringBuffer buffer) {
        for (int i = 0; i < indent; i++) {
            buffer.append("  ");
        }
        ;
    }
    
    public String getAlias(){
        return this.alias;
    }
    
    public void setAlias(String alias){
        this.alias = alias;
    }
    
    public boolean isAliasableNode(){
        return false;
    }
}
