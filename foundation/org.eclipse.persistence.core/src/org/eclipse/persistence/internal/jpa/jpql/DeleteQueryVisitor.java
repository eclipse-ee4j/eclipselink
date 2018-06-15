/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.queries.DeleteAllQuery;

/**
 * This builder is responsible to populate a {@link DeleteAllQuery} representing a <b>DELETE</b> query.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class DeleteQueryVisitor extends AbstractModifyAllQueryBuilder {

    /**
     * Creates a new <code>DeleteQueryBuilder</code>.
     *
     * @param queryContext The context used to query information about the application metadata and
     * cached information
     * @param query The {@link DeleteAllQuery} to populate by using this visitor to visit the parsed
     * tree representation of the JPQL query
     */
    DeleteQueryVisitor(JPQLQueryContext queryContext, DeleteAllQuery query) {
        super(queryContext, query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DeleteClause expression) {
        expression.getRangeVariableDeclaration().accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DeleteStatement expression) {

        expression.getDeleteClause().accept(this);

        if (expression.hasWhereClause()) {
            expression.getWhereClause().accept(this);
        }
    }
}
