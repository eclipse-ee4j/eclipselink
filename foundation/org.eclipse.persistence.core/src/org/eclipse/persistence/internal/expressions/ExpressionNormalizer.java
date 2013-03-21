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
package org.eclipse.persistence.internal.expressions;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import java.util.*;

/**
 * This is used during the normalization process to allow for a single main expression traversal.
 */
public class ExpressionNormalizer {

    /** A new root expression can be made from joins being added to the original expression. */
    protected Expression additionalExpression;

    /** The statement being normalized. */
    protected SQLSelectStatement statement;

    /** Subselect expressions found in the course of normalization. */
    protected List<SubSelectExpression> subSelectExpressions;

    /** The session being normalized in. */
    protected AbstractSession session;
    
    /** Used to maintain identity of cloned expressions. */
    protected Map<Expression, Expression> clonedExpressions;

    public ExpressionNormalizer(SQLSelectStatement statement) {
        this.statement = statement;
    }

    public Map<Expression, Expression> getClonedExpressions() {
        return clonedExpressions;
    }

    public void setClonedExpressions(Map<Expression, Expression> clonedExpressions) {
        this.clonedExpressions = clonedExpressions;
    }

    public void addAdditionalExpression(Expression theExpression) {
        // This change puts a null check into every call, but is printing additional
        // expressions in a meaningful order worth it?
        additionalExpression = (additionalExpression == null) ? theExpression : additionalExpression.and(theExpression);
    }

    /**
     * INTERNAL:
     * Remember this subselect so that it can be normalized after the enclosing
     * select statement is.
     */
    public void addSubSelectExpression(SubSelectExpression subSelectExpression) {
        if (this.subSelectExpressions == null) {
            this.subSelectExpressions = new ArrayList(4);
        }
        if (!this.subSelectExpressions.contains(subSelectExpression)) {
            this.subSelectExpressions.add(subSelectExpression);
        }
    }

    public Expression getAdditionalExpression() {
        return additionalExpression;
    }

    public AbstractSession getSession() {
        return session;
    }

    public SQLSelectStatement getStatement() {
        return statement;
    }

    /**
     * INTERNAL:
     * Were subselect expressions found while normalizing the selection criteria?
     * Assumes underlying collection is initialized on first add.
     */
    public boolean encounteredSubSelectExpressions() {
        return (subSelectExpressions != null);
    }

    /**
     * INTERNAL:
     * Normalize all subselect expressions found in the course of normalizing the
     * enclosing query.
     * This method allows one to completely normalize the parent statement first
     * (which should treat its sub selects as black boxes), and then normalize the
     * subselects (which require full knowledge of the enclosing statement).
     * This should make things clearer too,
     * Assumes encounteredSubSelectExpressions() true.
     * For CR#4223.
     */
    public void normalizeSubSelects(Map clonedExpressions) {
        for (SubSelectExpression next : this.subSelectExpressions) {
            next.normalizeSubSelect(this, clonedExpressions);
        }
    }

    public void setAdditionalExpression(Expression additionalExpression) {
        this.additionalExpression = additionalExpression;
    }

    public void setSession(AbstractSession session) {
        this.session = session;
    }

    public void setStatement(SQLSelectStatement statement) {
        this.statement = statement;
    }
}
