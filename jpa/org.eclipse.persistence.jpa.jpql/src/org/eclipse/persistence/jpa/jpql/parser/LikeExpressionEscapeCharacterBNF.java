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
 * This BNF is used when parsing an invalid fragment or to extend the default grammar.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class LikeExpressionEscapeCharacterBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF.
     */
    public static final String ID = "like_escape";

    /**
     * Creates a new <code>LikeExpressionEscapeCharacterBNF</code>.
     */
    public LikeExpressionEscapeCharacterBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(LiteralExpressionFactory.ID);
    }
}
