/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
