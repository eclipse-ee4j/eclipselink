/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This expression represents the database specific data type, which may include size and scale.
 *
 * <div><b>BNF:</b> <code>database_type ::= data_type_literal [( [numeric_literal [, numeric_literal]] )]</code></div>
 *
 * <div><b>BNF:</b> <code>data_type_literal ::= [CHAR, VARCHAR, NUMERIC, INTEGER, DATE, TIME, TIMESTAMP, etc]</code></div>
 * <p>
 * Example: <code>CASE(e.name AS VARCHAR(20))</code>
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public final class DatabaseType extends AbstractDoubleEncapsulatedExpression {

    /**
     * Creates a new <code>DatabaseType</code>.
     *
     * @param parent The parent of this expression
     * @param databaseType The database type
     */
    public DatabaseType(AbstractExpression parent, String databaseType) {
        super(parent, databaseType);
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
    public JPQLQueryBNF getQueryBNF() {
        return getQueryBNF(DatabaseTypeQueryBNF.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isSecondExpressionOptional() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parameterExpressionBNF(int index) {
        return NumericLiteralBNF.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldParseRightParenthesis(WordParser wordParser, boolean tolerant) {
        // If the database type uses parenthesis, then this expression will own the right
        // parenthesis,otherwise its parent expression should own it
        return hasLeftParenthesis() || hasFirstExpression() || hasComma() || hasSecondExpression();
    }
}
