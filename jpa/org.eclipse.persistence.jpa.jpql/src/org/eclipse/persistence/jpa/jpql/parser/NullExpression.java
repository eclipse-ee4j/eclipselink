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

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF findQueryBNF(Expression expression) {
        return getParent().findQueryBNF(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return getParent().getQueryBNF();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNull() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {
        throw new IllegalAccessError("This method shouln't be invoked, WordParser has " + wordParser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {
    }
}
