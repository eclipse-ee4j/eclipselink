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
 *     tware - initial implementation as part of JPA 2.0 RI
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an CASE statement in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an CASE in EJBQL
 * </ul>
 *    @author tware
 *    @since EclipseLink 1.2
 */
public class CaseNode extends Node implements AliasableNode {

    private List whenClauses = null;


    public CaseNode(){
        super();
    }
    
    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext generationContext) {
        if (theQuery instanceof ReportQuery) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            Expression expression = generateExpression(generationContext);
            reportQuery.addAttribute("Case", expression, (Class)getType());
        }
    }
    
    /**
     * INTERNAL
     * Generate the a new EclipseLink TableEntryExpression for this node.
     */
    public Expression generateExpression(GenerationContext context) {
        LinkedHashMap whenClauseMap = new LinkedHashMap(whenClauses.size());
        Iterator i = whenClauses.iterator();
        while (i.hasNext()){
            WhenThenNode clause = (WhenThenNode)i.next();
            whenClauseMap.put(clause.generateExpressionForWhen(context), clause.generateExpressionForThen(context));
        }
        
        Expression whereClause = null;
        if (getLeft() == null){
            whereClause = context.getBaseExpression().caseStatement(whenClauseMap, getRight().generateExpression(context));
        } else {
            whereClause = getLeft().generateExpression(context).caseStatement(whenClauseMap, getRight().generateExpression(context));
        }
        return whereClause;
    }
    
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        if (left != null){
            left.validate(context);
        }
        right.validate(context);
        Iterator i = whenClauses.iterator();
        Object type = null;
        while (i.hasNext()){
            Node node = ((Node)i.next());
            node.validate(context);
            if (type == null){
                type = node.getType();
            } else if (!type.equals(node.getType())){
                type = Object.class;
            }
        }
        if (getRight().getType() != type){
            type = typeHelper.getObjectType();
        }
        setType(type);
    }
    
    
    public List getWhenClauses() {
        return whenClauses;
    }

    public void setWhenClauses(List whenClauses) {
        this.whenClauses = whenClauses;
    }
    
    public boolean isAliasableNode(){
        return true;
    }
}
