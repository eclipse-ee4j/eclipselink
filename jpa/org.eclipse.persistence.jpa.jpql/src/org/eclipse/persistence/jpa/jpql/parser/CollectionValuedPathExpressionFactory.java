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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class CollectionValuedPathExpressionFactory extends AbstractLiteralExpressionFactory {

    /**
     * The unique identifier of this {@link CollectionValuedPathExpressionFactory}.
     */
    public static final String ID = "collection-valued-path";

    /**
     * Creates a new <code>CollectionValuedPathExpressionFactory</code>.
     */
    public CollectionValuedPathExpressionFactory() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent,
                                                 WordParser wordParser,
                                                 String word,
                                                 AbstractExpression expression,
                                                 boolean tolerant) {

        expression = new IdentificationVariable(parent, word);
        expression.parse(wordParser, tolerant);
        return expression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCollection() {
        return true;
    }
}
