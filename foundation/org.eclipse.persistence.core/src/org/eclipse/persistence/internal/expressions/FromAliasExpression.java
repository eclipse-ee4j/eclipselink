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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.querykeys.DirectQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * Represents an alias to a item selected by a from clause sub-select.
 */
public class FromAliasExpression extends QueryKeyExpression {
    protected ReportItem item;
    protected ClassDescriptor containingDescriptor;
    
    public FromAliasExpression() {
        super();
    }

    public FromAliasExpression(String name, Expression base) {
        super(name, base);
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    @Override
    public String descriptionOfNodeType() {
        return "From Alias";
    }

    /**
     * INTERNAL:
     * If a descriptor cannot be found, then return null.
     */
    @Override
    public QueryKey getQueryKeyOrNull() {
        if (!hasQueryKey) {
            return null;
        }
        if (getContainingDescriptor() == null) {
            // Assign an artificial query key to represent the alias.
            DirectQueryKey alias = new DirectQueryKey();
            alias.setField(new DatabaseField(this.name));
            ReportQuery subQuery = ((FromSubSelectExpression)getBaseExpression()).getSubSelect().getSubQuery();
            alias.setDescriptor(subQuery.getDescriptor());
            this.queryKey = alias;
            return alias;
        }
        return super.getQueryKeyOrNull();
    }

    
    /**
     * INTERNAL:
     * If the alias is for a query key item, then return its descriptor.
     * If it is for a function, then it has no descriptor.
     */
    @Override
    public ClassDescriptor getContainingDescriptor() {
        AbstractSession session = getBuilder().getSession();
        if (this.containingDescriptor == null) {
            Expression expression = getItem().getAttributeExpression();
            if (expression.isQueryKeyExpression()) {
                // Need to ensure expression has a session before getting its descriptor.
                if (expression.getBuilder().getSession() == null) {
                    expression.getBuilder().setSession(session);
                }
                this.containingDescriptor = ((QueryKeyExpression)expression).getContainingDescriptor();
                return this.containingDescriptor;
            }
            return null;
        }
        return containingDescriptor;
    }
    
    /**
     * INTERNAL:
     * Return the report item that this is an alias for.
     */
    public ReportItem getItem() {
        if (this.item == null) {
            ReportQuery subQuery = ((FromSubSelectExpression)getBaseExpression()).getSubSelect().getSubQuery();
            this.item = subQuery.getItem(this.name);
            if (this.item == null) {
                throw QueryException.invalidQueryKeyInExpression(this.name);
            }
        }
        return this.item;
    }
}
