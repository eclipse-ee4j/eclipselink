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
 * An implementation of an {@link Expression} that wraps a string.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class DefaultStringExpression extends AbstractExpression {

    /**
     * The wrapped string value.
     */
    private final String value;

    /**
     * Creates a new <code>DefaultStringExpression</code>.
     *
     * @param owningExpression The {@link Expression} from where the text value comes
     * @param value The string value to wrap with this {@link Expression}
     */
    DefaultStringExpression(AbstractExpression owningExpression, String value) {
        super(owningExpression);
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(ExpressionVisitor visitor) {
        // This object should not be visited
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptChildren(ExpressionVisitor visitor) {
        // This object does not have children
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLQueryBNF getQueryBNF() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(WordParser wordParser, boolean tolerant) {
        // Nothing to parse
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populatePosition(QueryPosition queryPosition, int position) {
        queryPosition.setExpression(getParent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toParsedText(StringBuilder writer, boolean actual) {
        writer.append(value);
    }
}
