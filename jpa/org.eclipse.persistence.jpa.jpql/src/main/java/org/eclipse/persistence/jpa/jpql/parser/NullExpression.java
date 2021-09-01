/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * A <code>null</code> {@link Expression} is used instead of a true <code>null</code>, which allows
 * operations to be performed without doing a <code>null</code> check first.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class NullExpression extends AbstractExpression {

    /**
     * Creates a new <code>NullExpression</code>.
     *
     * @param parent The parent of this <code>NullExpression</code>
     */
    public NullExpression(AbstractExpression parent) {
        super(parent);
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
    }

    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {
        return getParent().findQueryBNF(expression);
    }

    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getParent().getQueryBNF();
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {
        throw new IllegalAccessError("This method shouln't be invoked, WordParser has " + wordParser);
    }

    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {
    }
}
