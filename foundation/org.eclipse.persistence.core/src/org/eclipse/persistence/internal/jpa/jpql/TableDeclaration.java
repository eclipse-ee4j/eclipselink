/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.LiteralType;
import org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * This {@link Declaration} uses a database table as the "root" object.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
final class TableDeclaration extends Declaration {

    /**
     * Creates a new <code>TableDeclaration</code>.
     *
     * @param queryContext The context used to query information about the application metadata and
     * cached information
     */
    TableDeclaration(JPQLQueryContext queryContext) {
        super(queryContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Expression buildQueryExpression() {
        TableVariableDeclaration declaration = (TableVariableDeclaration) getBaseExpression();
        String tableName = queryContext.literal(declaration.getTableExpression(), LiteralType.STRING_LITERAL);
        tableName = ExpressionTools.unquote(tableName);
        return queryContext.getBaseExpression().getTable(tableName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return Type.TABLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ClassDescriptor resolveDescriptor() {
        // A TableExpression does not resolve to a descriptor,
        // it maps directly to a database table
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    DatabaseMapping resolveMapping() {
        // A TableExpression does not resolve to a mapping,
        // it maps directly to a database table
        return null;
    }
}
