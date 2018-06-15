/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.UpdateAllQuery;

/**
 * INTERNAL:
 * UpdateNode is a ModifyNode that represents an UpdateAllQuery
 */
public class UpdateNode extends ModifyNode {

    @Override
    public boolean isUpdateNode() {
        return true;
    }

    /**
     * INTERNAL
     * Returns a DatabaseQuery instance representing the owning
     * ParseTree. This implementation returns a UpdateAllQuery instance.
     */
    @Override
    public DatabaseQuery createDatabaseQuery(ParseTreeContext context) {
        UpdateAllQuery query = new UpdateAllQuery();
        query.setShouldDeferExecutionInUOW(false);
        return query;
    }
}
