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

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;

/**
 * INTERNAL:
 * UpdateNode is a ModifyNode that represents an UpdateAllQuery
 */
public class UpdateNode extends ModifyNode {

    public boolean isUpdateNode() {
        return true;
    }

    /**
     * INTERNAL
     * Returns a DatabaseQuery instance representing the owning
     * ParseTree. This implementation returns a UpdateAllQuery instance.
     */ 
    public DatabaseQuery createDatabaseQuery(ParseTreeContext context) {
        UpdateAllQuery query = new UpdateAllQuery();
        query.setShouldDeferExecutionInUOW(false);
        return query;
    }
}
