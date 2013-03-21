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

import java.util.*;

import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.mappings.querykeys.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * This class represents a "query key" that isn't really there
 * in the descriptors. For example, I could use this to create
 * an 'employee' query key from an 'address' node even if addresses
 * don't know their employee. It's called manual, because I will
 * have to provide the relevant join criteria myself (normally based
 * on a reverse relationship. Motivated by batch reading.
 */
public class ManualQueryKeyExpression extends QueryKeyExpression {
    public ManualQueryKeyExpression() {
        super();
    }

    public ManualQueryKeyExpression(String impliedRelationshipName, Expression base) {
        super(impliedRelationshipName, base);
    }

    public ManualQueryKeyExpression(String impliedRelationshipName, Expression base, ClassDescriptor descriptor) {
        super(impliedRelationshipName, base);
        this.descriptor = descriptor;
    }
    
    /**
     * INTERNAL:
     * Return if the expression is equal to the other.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        // Equality cannot easily be determined for maunal expressions.
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for debug printing.
     */
    public String descriptionOfNodeType() {
        return "Manual Query Key";
    }

    /**
     * INTERNAL:
     * If we ever get in the circumstance of a manual query key
     * to an aggregate, then we can assume that the owner of that
     * aggregate isn't participating (and even if it is, we can't
     * know which node it is, so *DO* use the aggregate's parents tables
     */
    public List<DatabaseTable> getOwnedTables() {
        if (getDescriptor() == null) {
            return null;
        } else {
            if ((getDescriptor().getHistoryPolicy() != null) && (getAsOfClause().getValue() != null)) {
                return getDescriptor().getHistoryPolicy().getHistoricalTables();
            }
            return getDescriptor().getTables();
        }
    }

    public QueryKey getQueryKeyOrNull() {
        return null;
    }

    /**
     * INTERNAL:
     * We can never be an attribute, we're always a join
     */
    public boolean isAttribute() {
        return false;
    }

    @Override
    public Expression mappingCriteria(Expression base) {
        return null;
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    public Expression rebuildOn(Expression newBase) {
        ObjectExpression newLocalBase = (ObjectExpression)getBaseExpression().rebuildOn(newBase);
        return newLocalBase.getManualQueryKey(getName(), getDescriptor());
    }

    /**
     * INTERNAL:
     * Rebuild myself against the base, with the values of parameters supplied by the context
     * expression. This is used for transforming a standalone expression (e.g. the join criteria of a mapping)
     * into part of some larger expression. You normally would not call this directly, instead calling twist
     * See the comment there for more details"
     */
    @Override
    public Expression twistedForBaseAndContext(Expression newBase, Expression context, Expression oldBase) {
        ObjectExpression twistedBase = (ObjectExpression)getBaseExpression().twistedForBaseAndContext(newBase, context, oldBase);
        return twistedBase.getManualQueryKey(getName(), getDescriptor());

    }

    /**
     * Do any required validation for this node. Throw an exception if it's incorrect.
     */
    public void validateNode() {
        // Override super.validateNode() because those criteria don't apply to us
    }
}
