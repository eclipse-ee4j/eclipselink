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


// Java imports
import java.util.Hashtable;
import java.util.Set;

// TopLink imports
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.expressions.Expression;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Maintain the generation context for an EJBQL query
 * <p><b>Responsibilities</b>:<ul>
 * <li> Maintain a table of expression builders and alias's
 * <li> Maintain the base query class
 * <li> Maintain a handle to the session
 * <li> Maintain a handle to the parse tree
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class GenerationContext {
    protected AbstractSession session;
    protected ParseTreeContext parseTreeContext;
    protected Class baseQueryClass;
    protected Expression baseExpression;
    protected Hashtable expressions;
    protected ParseTree parseTree;
    protected boolean isNotIndicatedInMemberOf = false;

    //If a NOT MEMBER OF is encountered, we need to store the MEMBER OF
    //so that the right side of the member of can use the stored expression
    //from the left
    protected MemberOfNode memberOfNode = null;

    public GenerationContext() {
        super();
        expressions = new Hashtable();
    }

    public GenerationContext(ParseTreeContext newContext, AbstractSession newSession, ParseTree newParseTree) {
        this();
        parseTreeContext = newContext;
        session = newSession;
        parseTree = newParseTree;
    }

    public void addExpression(Expression expression, String aliasName) {
        expressions.put(aliasName, expression);
    }

    public Expression expressionFor(String aliasName) {
        Expression exp = (Expression) expressions.get(aliasName);
        
        if (exp == null && (! expressions.isEmpty()) && isSelectGenerationContext()) {
            GenerationContext outerContext = ((SelectGenerationContext) this).getOuterContext();
            
            if (outerContext != null) {
                return outerContext.expressionFor(aliasName);
            }
        }
       
        return exp;
    }

    public Class getBaseQueryClass() {
        return baseQueryClass;
    }

    public ParseTreeContext getParseTreeContext() {
        return parseTreeContext;
    }

    public ParseTree getParseTree() {
        return parseTree;
    }

    public AbstractSession getSession() {
        return session;
    }

    public void setBaseQueryClass(java.lang.Class newBaseQueryClass) {
        baseQueryClass = newBaseQueryClass;
    }

    /** 
     * Caches the specified expression under the variable name for the base
     * query class.
     */
    public void setBaseExpression(String variable, Expression expr) {
        // Store the expression for faster access
        baseExpression = expr;
        
        // Store it into the cache
        addExpression(expr, variable);
    }

    /** */
    public Expression getBaseExpression() {
        return baseExpression;
    }

    public void setParseTree(ParseTree parseTree) {
        this.parseTree = parseTree;
    }

    public void setParseTreeContext(ParseTreeContext newParseTreeContext) {
        parseTreeContext = newParseTreeContext;
    }

    public void setSession(AbstractSession newSession) {
        session = newSession;
    }

    //Answer true if we need to use parallel expressions
    //This will be the case if a 1:1 is SELECTed in the EJBQL. 
    public boolean useParallelExpressions() {
        return false;
    }

    //Answer true if we want VariableNodes to check if they're
    //SELECTed first, to determine how to instantiate the ExpressionBuilder 
    public boolean shouldCheckSelectNodeBeforeResolving() {
        return false;
    }

    //Set and get the contained MemberOfNode. This is for handling NOT MEMBER OF.
    public void setMemberOfNode(MemberOfNode newMemberOfNode) {
        memberOfNode = newMemberOfNode;
    }

    public MemberOfNode getMemberOfNode() {
        return memberOfNode;
    }

    //Answer true if we have a MemberOfNode contained. This is for handling NOT MEMBER OF
    public boolean hasMemberOfNode() {
        return memberOfNode != null;
    }

    public boolean isSelectGenerationContext() {
        return false;
    }
    
    //Answer true if we should use outer joins in our get() (vs getAllowingNull())
    public boolean shouldUseOuterJoins() {
        return false;
    }

    /** */
    public Expression joinVariables(Set variables) {
        return null;
    }
}
