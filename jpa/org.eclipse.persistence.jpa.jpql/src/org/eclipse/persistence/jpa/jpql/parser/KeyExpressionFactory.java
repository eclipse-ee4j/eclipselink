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

/**
 * This {@link KeyExpressionFactory} creates a new {@link KeyExpression} when the portion of the
 * query to parse starts with <b>KEY</b>.
 *
 * @see KeyExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class KeyExpressionFactory extends GeneralIdentificationExpressionFactory {

    /**
     * The unique identifier of this {@link KeyExpressionFactory}.
     */
    public static final String ID = Expression.KEY;

    /**
     * Creates a new <code>KeyExpressionFactory</code>.
     */
    public KeyExpressionFactory() {
        super(ID, Expression.KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractExpression buildExpression(AbstractExpression parent) {
        return new KeyExpression(parent);
    }
}
