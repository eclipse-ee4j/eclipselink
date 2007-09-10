/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteAllQuery;

/**
 * INTERNAL:
 * DeleteNode is a ModifyNode that represents an DeleteAllQuery
 */
public class DeleteNode extends ModifyNode {

    public boolean isDeleteNode() {
        return true;
    }

    /**
     * INTERNAL
     * Returns a DatabaseQuery instance representing the owning
     * ParseTree. This implementation returns a DeleteAllQuery instance.
     */ 
    public DatabaseQuery createDatabaseQuery(ParseTreeContext context) {
        DeleteAllQuery query = new DeleteAllQuery();
        query.setShouldDeferExecutionInUOW(false);
        return query;
    }
}
