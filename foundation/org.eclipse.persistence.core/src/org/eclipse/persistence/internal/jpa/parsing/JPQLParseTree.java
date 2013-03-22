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

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL
 * <p><b>Purpose</b>: This represents an EJBQL parse tre
 * <p><b>Responsibilities</b>:<ul>
 * <li> Maintain the context for the expression generation
 * <li> Build an initial expression
 * <li> Return a reference class for the expression
 * <li> Maintain the root node for the query
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class JPQLParseTree extends ParseTree {

    /**
     * EJBQLParseTree constructor comment.
     */
    public JPQLParseTree() {
        super();
    }

    /**
     * INTERNAL
     * Build the context to be used when generating the expression from the parse tree
     */
    public GenerationContext buildContext(ReadQuery readQuery, AbstractSession session) {
        GenerationContext contextForGeneration = super.buildContext(readQuery, session);

        contextForGeneration.setBaseQueryClass(readQuery.getReferenceClass());
        return contextForGeneration;
    }

    /**
     * INTERNAL
     * Build the context to be used when generating the expression from the
     * subquery parse tree.
     */
    private GenerationContext buildSubqueryContext(ReadQuery readQuery, GenerationContext outer) {
        GenerationContext context = new SelectGenerationContext(outer, this);
        context.setBaseQueryClass(readQuery.getReferenceClass());
        return context;
    }

    /**
     * Add all of the relevant query settings from an EJBQLParseTree to the given
     * database query.
     * @param query The query to populate
     * @param outer the GenerationContext of the outer EJBQL query.
     * @return the GenerationContext for the subquery
     */
    public GenerationContext populateSubquery(ObjectLevelReadQuery readQuery, GenerationContext outer) {
        GenerationContext innerContext = buildSubqueryContext(readQuery, outer);
        populateReadQueryInternal(readQuery, innerContext);
        return innerContext;
    }
    
    /**
     * Add all of the relevant query settings from an EJBQLParseTree to the given
     * database query.
     * @param query The query to populate
     * @param session The session to use to information such as descriptors.
     */
    public void populateQuery(DatabaseQuery query, AbstractSession session) {
        if (query.isObjectLevelReadQuery()) {
            ObjectLevelReadQuery objectQuery = (ObjectLevelReadQuery)query;
            GenerationContext generationContext = buildContext(objectQuery, session);
            populateReadQueryInternal(objectQuery, generationContext);
        } else if (query.isUpdateAllQuery()) {
            UpdateAllQuery updateQuery = (UpdateAllQuery)query;
            GenerationContext generationContext = buildContext(updateQuery, session);
            populateModifyQueryInternal(updateQuery, generationContext);
            addUpdatesToQuery(updateQuery, generationContext);
        } else if (query.isDeleteAllQuery()) {
            DeleteAllQuery deleteQuery = (DeleteAllQuery)query;
            GenerationContext generationContext = buildContext(deleteQuery, session);
            populateModifyQueryInternal(deleteQuery, generationContext);
        }
    }

    private void populateReadQueryInternal(ObjectLevelReadQuery objectQuery, 
                                           GenerationContext generationContext) {
        // Get the reference class if it does not exist.  This is done
        // for dynamic queries in EJBQL 3.0
        if (objectQuery.getReferenceClass() == null) {
            // Adjust the reference class if necessary
            adjustReferenceClassForQuery(objectQuery, generationContext);
        }

        // Initialize the base expression in the generation context
        initBaseExpression(objectQuery, generationContext);
        
        // Validate parse tree
        validate(generationContext.getSession(), getClassLoader());

        // Apply the query node to the query (this will be a SelectNode for a read query)
        applyQueryNodeToQuery(objectQuery, generationContext);
        
        // Verify the SELECT is valid (valid alias, etc)
        verifySelect(objectQuery, generationContext);
        
        // This is what it's all about...
        setSelectionCriteriaForQuery(objectQuery, generationContext);
        
        // Add any ordering
        addOrderingToQuery(objectQuery, generationContext);

        // Add any grouping
        addGroupingToQuery(objectQuery, generationContext);
        
        // Add having
        addHavingToQuery(objectQuery, generationContext);
        
        // Add non fetch joined variables
        addNonFetchJoinAttributes(objectQuery, generationContext);
    }

    private void populateModifyQueryInternal(ModifyAllQuery query, 
                                             GenerationContext generationContext) {
        if (query.getReferenceClass() == null) {
            // Adjust the reference class if necessary
            adjustReferenceClassForQuery(query, generationContext);
        }
        query.setSession(generationContext.getSession());            

        // Initialize the base expression in the generation context
        initBaseExpression(query, generationContext);

        // Validate parse tree
        validate(generationContext.getSession(), getClassLoader());

        // Apply the query node to the query
        applyQueryNodeToQuery(query, generationContext);
        
        setSelectionCriteriaForQuery(query, generationContext);
    }

}
