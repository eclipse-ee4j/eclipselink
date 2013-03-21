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
package org.eclipse.persistence.internal.queries;

import java.util.*;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;

/**
 * <p><b>Purpose</b>:
 * Mechanism used for all queries by example.
 * <p>
 * <p><b>Responsibilities</b>:
 * Builds a selection criteria for this query given an example object and
 * a QueryByExample policy.  Works akin to EBQLMechanism which builds the
 * selection criteria from an EJBQL string.
 *
 * @author Stephen McRitchie
 * @since 9.0.3.5
 */
public class QueryByExampleMechanism extends ExpressionQueryMechanism {
    protected boolean isParsed = false;

    /** Used for Query By Example. */
    protected Object exampleObject;
    protected QueryByExamplePolicy queryByExamplePolicy;

    /**
     * Initialize the state of the query
     * @param query - owner of mechanism
     */
    public QueryByExampleMechanism(DatabaseQuery query) {
        super(query);
    }

    /**
     * Initialize the state of the query
     * @param query - owner of mechanism
     * @param expression - selection criteria
     */
    public QueryByExampleMechanism(DatabaseQuery query, Expression expression) {
        super(query, expression);
    }

    /**
     * INTERNAL:
     * In the case of EJBQL or query by example, an expression needs to be
     * generated. Build the required expression.
     */
    public void buildSelectionCriteria(AbstractSession session) {
        if (isParsed() || (getExampleObject() == null)) {
            return;
        }
        ObjectLevelReadQuery query = (ObjectLevelReadQuery)getQuery();

        query.checkDescriptor(session);

        QueryByExamplePolicy policy = getQueryByExamplePolicy();

        if (policy == null) {
            policy = new QueryByExamplePolicy();
        }

        if (query.getReferenceClass().isInstance(getExampleObject())) {
            Expression exampleExpression = query.getDescriptor().getObjectBuilder().buildExpressionFromExample(getExampleObject(), policy, query.getExpressionBuilder(), new IdentityHashMap(), session);

            if (getSelectionCriteria() != null) {
                setSelectionCriteria(getSelectionCriteria().and(exampleExpression));
            } else {
                setSelectionCriteria(exampleExpression);
            }
        } else {
            throw QueryException.exampleAndReferenceObjectClassMismatch(getExampleObject().getClass(), query.getReferenceClass(), query);
        }
        setIsParsed(true);
    }

    /**
     * PUBLIC:
     * This method returns the current example object.  The "example" object is an actual domain object, provided
     * by the client, from which an expression is generated.
     * This expression is used for a query of all objects from the same class, that match the attribute values of
     * the "example" object.
     */
    public Object getExampleObject() {
        return exampleObject;
    }

    /**
     * PUBLIC:
     * When using Query By Example, an instance of QueryByExamplePolicy is used to customize the query.
     * The policy is useful when special operations are to be used for comparisons (notEqual, lessThan,
     * greaterThan, like etc.), when a certain value is to be ignored, or when dealing with nulls.
     */
    public QueryByExamplePolicy getQueryByExamplePolicy() {
        return queryByExamplePolicy;
    }

    /**
     * INTERNAL:
     * Is this query Parsed
     */
    public boolean isParsed() {
        return isParsed;
    }

    /**
     * Return true if this is a query by example mechanism
     */
    public boolean isQueryByExampleMechanism() {
        return true;
    }

    /**
     * PUBLIC:
     * Set the example object of the query to be the newExampleObject.
     * The example object is used for Query By Example.
     * When doing a Query By Example, an instance of the desired object is created, and the fields are filled with
     * the values that are required in the result set.  From these values the corresponding expression is build
     * by TopLink, and the query is executed, returning the set of results.
     */
    public void setExampleObject(Object newExampleObject) {
        exampleObject = newExampleObject;
    }

    /**
     * INTERNAL:
     * Set the isParsed state
     */
    public void setIsParsed(boolean newIsParsed) {
        isParsed = newIsParsed;
    }

    /**
     * PUBLIC:
     * The QueryByExamplePolicy, is a useful to customize the query when Query By Example is used.
     * The pollicy will control what attributes should, or should not be included in the query.
     * When dealing with nulls, using specail operations (notEqual, lessThan, like, etc.)
     * for comparison, or chosing to include certain attributes at all times, it is useful to modify
     * the policy accordingly.
     */
    public void setQueryByExamplePolicy(QueryByExamplePolicy queryByExamplePolicy) {
        this.queryByExamplePolicy = queryByExamplePolicy;
    }
}
