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

import java.util.Set;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a subquery.
 */
public class SubqueryNode extends Node {

    private JPQLParseTree subqueryParseTree;

    /** Set of names of variables declared in an outer scope and used in teh
     * subquery. */
    private Set outerVars;

    /**
     * Return a new SubqueryNode.
     */
    public SubqueryNode() {
        super();
    }

    /** */
    public ReportQuery getReportQuery(GenerationContext context) {
        ReportQuery innerQuery = new ReportQuery();
        GenerationContext innerContext = 
            subqueryParseTree.populateSubquery(innerQuery, context);
        Expression joins = innerContext.joinVariables(outerVars);
        if (joins != null) {
            Expression where = innerQuery.getSelectionCriteria();
            where = appendExpression(where, joins);
            innerQuery.setSelectionCriteria(where);
        }
        return innerQuery;
    }

    /** 
     * INTERNAL 
     * If called the subquery is part of the WHERE clause of an UPDATE or
     * DELETE statement that does not define an identification variable. 
     * The method checks the clauses of the subquery for unqualified fields
     * accesses. 
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        subqueryParseTree.getFromNode().qualifyAttributeAccess(context);
        subqueryParseTree.getQueryNode().qualifyAttributeAccess(context);
        if (subqueryParseTree.getWhereNode() != null) {
            subqueryParseTree.getWhereNode().qualifyAttributeAccess(context);
        }
        if (subqueryParseTree.getGroupByNode() != null) {
            subqueryParseTree.getGroupByNode().qualifyAttributeAccess(context);
        }
        if (subqueryParseTree.getHavingNode() != null) {
            subqueryParseTree.getHavingNode().qualifyAttributeAccess(context);
        }
        return this;
    }
    
    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        subqueryParseTree.validate(context);
        outerVars = context.getOuterScopeVariables();
        SelectNode selectNode = (SelectNode)subqueryParseTree.getQueryNode();
        // Get the select expression, subqueries only have one
        Node selectExpr = (Node)selectNode.getSelectExpressions().get(0);
        setType(selectExpr.getType());
    }

    /**
     * INTERNAL
     * Generate the EclipseLink expression for this node
     */
    public Expression generateExpression(GenerationContext context) {
        Expression base = context.getBaseExpression();
        ReportQuery innerQuery = getReportQuery(context);
        return base.subQuery(innerQuery);
    }
    
    /**
     * INTERNAL
     * Is this node a SubqueryNode
     */
    public boolean isSubqueryNode() {
        return true;
    }

    /** */
    public void setParseTree(JPQLParseTree parseTree) {
        this.subqueryParseTree = parseTree;
    }

    /** */
    public JPQLParseTree getParseTree() {
        return subqueryParseTree;
    }
    
}

