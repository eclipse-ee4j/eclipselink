/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * Represents an alias to a item selected by a from clause sub-select.
 */
public class FromAliasExpression extends QueryKeyExpression {
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
     * Search the sub query for report item matching the alias name and returns its descriptor.
     */
    @Override
    public ClassDescriptor getContainingDescriptor() {
        if (this.containingDescriptor == null ) {
            ReportQuery subQuery = ((FromSubSelectExpression)getBaseExpression()).getSubSelect().getSubQuery();
            for (ReportItem item : subQuery.getItems()) {
                if (item.getName().equals(this.name)) {
                    if (item.getAttributeExpression().isQueryKeyExpression()) {
                        this.containingDescriptor = ((QueryKeyExpression)item.getAttributeExpression()).getContainingDescriptor();
                        return this.containingDescriptor;                        
                    }
                }
            }
            this.containingDescriptor = subQuery.getDescriptor();
        }
        return containingDescriptor;
    }
}
