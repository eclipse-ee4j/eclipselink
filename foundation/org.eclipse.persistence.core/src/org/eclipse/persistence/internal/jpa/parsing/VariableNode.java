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

import java.util.*;

// TopLink imports
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: The node that represents typed variables, local variables, remote variables and TYPE constants
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class VariableNode extends Node implements AliasableNode {

    /** */
    private String variableName;
    
    /** */
    private String canonicalName;
    
    /** if this represents a type constant, this value will be populated by validate **/
    private Object classConstant = null;

    /**
     * VariableNode constructor comment.
     */
    public VariableNode() {
        super();
    }

    public VariableNode(String newVariableName) {
        setVariableName(newVariableName);
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String newVariableName) {
        variableName = newVariableName;
        canonicalName = IdentificationVariableDeclNode.calculateCanonicalName(newVariableName);
    }

    /** */
    public String getCanonicalVariableName() {
        return canonicalName;
    }

    /**
     * INTERNAL
     * Is this node a VariableNode
     */
    public boolean isVariableNode() {
        return true;
    }

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext generationContext) {
        String name = getCanonicalVariableName();
        if (theQuery instanceof ReportQuery) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            Expression expression = generationContext.expressionFor(name);
            if (expression == null) {
                expression = generateExpression(generationContext);
            }
            addAttributeWithFetchJoins(reportQuery, expression, generationContext);
        } else {
            addFetchJoins(theQuery, generationContext);
        }
    }

    /**
     * INTERNAL
     * Add the variable as ReportQuery item. The method checks for any JOIN
     * FETCH nodes of the current variable and adds them as part of the
     * ReportQuery item.
     */
    private void addAttributeWithFetchJoins(ReportQuery reportQuery, 
                                            Expression expression, 
                                            GenerationContext context) {
        String name = getCanonicalVariableName();
        List fetchJoinNodes = context.getParseTreeContext().getFetchJoins(name);
        if (fetchJoinNodes == null) {
            reportQuery.addAttribute(name, expression);
        } else {
            List fetchJoinExprs = new ArrayList(fetchJoinNodes.size());
            for (Iterator i = fetchJoinNodes.iterator(); i.hasNext(); ) {
                Node node = (Node)i.next();
                fetchJoinExprs.add(node.generateExpression(context));
            }
            reportQuery.addItem(name, expression, fetchJoinExprs);
        }
    }

    /**
     * INTERNAL
     * Check for any JOIN FETCH nodes of the current variable and add them as
     * joined attributes. This method is called in case of a non ReportQuery
     * instance.
     */
    private void addFetchJoins(ObjectLevelReadQuery theQuery, 
                               GenerationContext context) {
        String name = getCanonicalVariableName();
        List fetchJoinNodes = context.getParseTreeContext().getFetchJoins(name);
        if (fetchJoinNodes != null) {
            for (Iterator i = fetchJoinNodes.iterator(); i.hasNext(); ) {
                Node node = (Node)i.next();
                theQuery.addJoinedAttribute(node.generateExpression(context));
            }
        }
    }

    /** 
     * INTERNAL 
     * This node represent an unqualified field access in the case the method
     * is called and the variableName is not defined as identification variable.
     * The method returns a DotNode representing a qualified field access with
     * the base variable as left child node. The right child node is an
     * AttributeNode using the variableName as field name.
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        return context.isVariable(variableName) ? this :
            (Node)context.getNodeFactory().newQualifiedAttribute(
                getLine(), getColumn(), context.getBaseVariable(), variableName);
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        classConstant = typeHelper.resolveSchema(variableName);
        if (classConstant != null){
            setType(Class.class);
            return;
        }
        String name = getCanonicalVariableName();
        if (context.isRangeVariable(name)) {
            String schema = context.schemaForVariable(name);
            setType(typeHelper.resolveSchema(schema));
        } else {
            Node path = context.pathForVariable(name);
            if (path == null) {
                throw JPQLException.aliasResolutionException(
                    context.getQueryInfo(), getLine(), getColumn(), name);
            } else {
                setType(path.getType());
            }
        }
        context.usedVariable(name);
        if (context.isDeclaredInOuterScope(name)) {
            context.registerOuterScopeVariable(name);
        }
    }

    public Expression generateBaseBuilderExpression(GenerationContext context) {
        //create builder, and add it, and answer it
        //BUG 3106877: Need to create builder using the actual class (if using parallel expressions)
        return new ExpressionBuilder(this.resolveClass(context));
    }

    public Expression generateExpression(GenerationContext generationContext) {
        Expression myExpression = null;
        String name = getCanonicalVariableName();
        
        //is there a cached Expression?
        myExpression = generationContext.expressionFor(name);
        if (myExpression != null) {
            return myExpression;
        }

        //Either I have an alias type, or I'm an IN declaration
        if (classConstant != null){
            myExpression = new ConstantExpression(classConstant, generationContext.getBaseExpression());
        } else if (generationContext.getParseTreeContext().isRangeVariable(name)) {
            myExpression = generateBaseBuilderExpression(generationContext);
        } else {
            myExpression = generateExpressionForAlias(generationContext);
        }

        generationContext.addExpression(myExpression, name);
        return myExpression;
    }

    public Expression generateExpressionForAlias(GenerationContext context) {
        // BUG 3105651: Verify if we need to resolve this alias, or just use
        // an empty ExpressionBuilder. See OrderByItemNode.generateExpression()
        // for more details
        if (context.getParseTree().getQueryNode().isSelectNode() && context.shouldCheckSelectNodeBeforeResolving() && (((SelectNode)context.getParseTree().getQueryNode()).isSelected(this.getCanonicalVariableName()))) {
            return new ExpressionBuilder();
        }

        Node nodeForAlias = getNodeForAlias(context);

        //assume that if there is no node available for the given variable, then
        //there must be an alias mismatch. Assume they know their attribute names better
        //than their alias names. - JGL
        if (nodeForAlias == null) {
            throw JPQLException.aliasResolutionException(
                context.getParseTreeContext().getQueryInfo(), 
                getLine(), getColumn(), getVariableName());
        }

        //create builder, and answer it
        return nodeForAlias.generateExpression(context);
    }

    public Node getNodeForAlias(GenerationContext context) {
        //Node node = context.getParseTreeContext().nodeForIdentifier(getCanonicalVariableName());
        //return node != null ? ((IdentificationVariableDeclNode)node).getPath() : null;
        return context.getParseTreeContext().pathForVariable(getCanonicalVariableName());
    }

    /**
     * isAlias: Answer true if this variable represents an alias in the FROM clause.
     * i.e. "FROM Employee emp" declares "emp" as an alias
     */
    public boolean isAlias(GenerationContext context) {
        return isAlias(context.getParseTreeContext());
    }

    public boolean isAlias(ParseTreeContext context) {
        String classNameForAlias = context.schemaForVariable(getCanonicalVariableName());
        return classNameForAlias != null;
    }

    /**
     * resolveClass: Answer the class which corresponds to my variableName. This is the class for
     * an alias, where the variableName is registered to an alias.
     */
    public Class resolveClass(GenerationContext generationContext) {
        Class clazz = null;
        String name = getCanonicalVariableName();
        ParseTreeContext context = generationContext.getParseTreeContext();
        if (context.isRangeVariable(name)) {
            String schema = context.schemaForVariable(name);
            clazz = context.classForSchemaName(schema, generationContext);
        } else {
            DotNode path = (DotNode)context.pathForVariable(name);
            if (path == null) {
                throw JPQLException.aliasResolutionException(
                    context.getQueryInfo(), getLine(), getColumn(), name);
            } else {
                clazz = path.resolveClass(generationContext);
            }
        }
        return clazz;
    }

    public String toString(int indent) {
        StringBuffer buffer = new StringBuffer();
        toStringIndent(indent, buffer);
        buffer.append(toStringDisplayName() + "[" + getVariableName() + "]");
        return buffer.toString();
    }

    /**
     * INTERNAL
     * Get the string representation of this node.
     */
    public String getAsString() {
        return getVariableName();
    }
    
    public Object getTypeForMapKey(ParseTreeContext context){
        String name = getCanonicalVariableName();
        if (context.isRangeVariable(name)) {
            throw JPQLException.variableCannotHaveMapKey(context.getQueryInfo(), getLine(), getColumn(), name);
        } else {
            DotNode path = (DotNode)context.pathForVariable(name);
            if (path == null) {
                throw JPQLException.aliasResolutionException(
                    context.getQueryInfo(), getLine(), getColumn(), name);
            } else {
                return path.getTypeForMapKey(context);
            }
        }
    }

    
    public boolean isAliasableNode(){
        return true;
    }
}
