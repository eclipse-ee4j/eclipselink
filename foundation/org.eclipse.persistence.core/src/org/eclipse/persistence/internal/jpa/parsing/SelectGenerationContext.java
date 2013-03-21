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

import java.util.Iterator;
import java.util.Set;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 * An extension of GenerationContext the provides SELECT specific behavior.
 * Used when building the query features that are not usable in other types of queries
 */
public class SelectGenerationContext extends GenerationContext {
    //if a 1:1 is SELECTed in the EJBQL, then we need to use parallel expressions
    //with each ExpressionBuilder created using "new ExpressionBuilder(MyClass.class)"
    private boolean useParallelExpressions = false;

    //BUG 3105651: If a variable is SELECTed, and it's in an ORDER BY, then 
    //we want the ExpressionBuilder to be instantiated using an empty constructor
    private boolean shouldCheckSelectNodeBeforeResolving = false;

    //If a NOT MEMBER OF is encountered, we need to store the MEMBER OF
    //so that the right side of the member of can use the stored expression
    //from the left
    private MemberOfNode memberOfNode = null;

    //Do we want to use outer joins? get("address") vs getAllowingNull("address")
    private boolean shouldUseOuterJoins = false;

    //Outer SelectGenerationContext
    private GenerationContext outer = null;

    public SelectGenerationContext() {
        super();
    }

    /**
     * Constructor used to create the context for a subquery.
     */
    public SelectGenerationContext(GenerationContext outer, ParseTree newParseTree) {
        this(outer.getParseTreeContext(), outer.getSession(), newParseTree);
        this.outer = outer;
    }

    public SelectGenerationContext(ParseTreeContext newContext, AbstractSession newSession, ParseTree newParseTree) {
        super(newContext, newSession, newParseTree);

        //indicate if we want parallel expressions or not
        useParallelExpressions = this.computeUseParallelExpressions();
    }

    //Set and get the contained MemberOfNode. This is for handling NOT MEMBER OF.
    public void setMemberOfNode(MemberOfNode newMemberOfNode) {
        memberOfNode = newMemberOfNode;
    }

    public MemberOfNode getMemberOfNode() {
        return memberOfNode;
    }

    private boolean computeUseParallelExpressions() {
        boolean computedUseParallelExpressions;

        //use parallel expressions if I have a 1:1 selected, and the same class isn't
        //declared in the FROM
        computedUseParallelExpressions = ((SelectNode)this.parseTree.getQueryNode()).hasOneToOneSelected(this);
        //check if they've SELECTed a variable declared in the IN clause in the FROM,
        //or they've mapped more than one variable to the same type in the FROM
        computedUseParallelExpressions = computedUseParallelExpressions || ((SelectNode)this.parseTree.getQueryNode()).isVariableInINClauseSelected(this) || this.parseTree.getContext().hasMoreThanOneVariablePerType() || this.parseTree.getContext().hasMoreThanOneAliasInFrom();
        return computedUseParallelExpressions;
    }

    //Answer true if we need to use parallel expressions
    //This will be the case if a 1:1 is SELECTed in the EJBQL. 
    public boolean useParallelExpressions() {
        return useParallelExpressions;
    }

    //Indicate that we want VariableNodes to check if they're
    //SELECTed first, to determine how to instantiate the ExpressionBuilder 
    public void checkSelectNodeBeforeResolving(boolean shouldCheck) {
        shouldCheckSelectNodeBeforeResolving = shouldCheck;
    }

    //Answer true if we want VariableNodes to check if they're
    //SELECTed first, to determine how to instantiate the ExpressionBuilder 
    public boolean shouldCheckSelectNodeBeforeResolving() {
        return shouldCheckSelectNodeBeforeResolving;
    }

    //Answer true if we should use outer joins in our get() (vs getAllowingNull())
    public boolean shouldUseOuterJoins() {
        return shouldUseOuterJoins;
    }

    public void useOuterJoins() {
        shouldUseOuterJoins = true;
    }

    public void dontUseOuterJoins() {
        shouldUseOuterJoins = false;
    }

    //Answer true if we have a MemberOfNode contained. This is for handling NOT MEMBER OF
    public boolean hasMemberOfNode() {
        return memberOfNode != null;
    }
    
    public boolean isSelectGenerationContext() {
        return true;
    }

    /** */
    public GenerationContext getOuterContext() {
        return outer;
    }
    
    /** 
     * Iterate the set of variables declared in an outer scope and
     * connect the inner variable expression with the outer one.
     */
    public Expression joinVariables(Set variables) {        
        if ((outer == null) || (variables == null) || variables.isEmpty()) {
            // not an inner query or no variables to join
            return null;
        }
        Expression expr = null;
        for (Iterator i = variables.iterator(); i.hasNext(); ) {
            String name = (String)i.next();
            VariableNode var = new VariableNode(name);
            Expression innerExpr = var.generateExpression(this);
            Expression outerExpr = var.generateExpression(outer);
            
            // Join them only if they are not the same.
            if (innerExpr != outerExpr) {
                Expression join = innerExpr.equal(outerExpr);
                expr = var.appendExpression(expr, join);
            }
        }
        return expr;
        
    }
}
