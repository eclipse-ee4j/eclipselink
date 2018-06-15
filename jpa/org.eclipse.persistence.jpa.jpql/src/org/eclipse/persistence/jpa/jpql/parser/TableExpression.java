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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * Defines a table expression. This allow a non-mapped table to be used in a query. This is not part
 * of the JPA functional specification but is EclipseLink specific support.
 *
 * <div><b>BNF:</b> <code>table_expression ::= TABLE(string_literal)</code></div>
 *
 * @version 2.5
 * @since 2.4
 * @author James Sutherland
 */
public final class TableExpression extends AbstractSingleEncapsulatedExpression {

    /**
     * Creates a new <code>TableExpression</code>.
     *
     * @param parent The parent of this expression
     */
    public TableExpression(AbstractExpression parent) {
        super(parent, TABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        acceptUnknownVisitor(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncapsulatedExpressionQueryBNFId() {
        return StringLiteralBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(TableExpressionBNF.ID);
    }
}
