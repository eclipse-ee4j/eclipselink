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
 * The query BNF for literals, which is based on the listing defined in section 4.6.1 of the Java
 * Specification document for JPA 2.0.
 *
 * @version 2.5.2
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class LiteralBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this <code>LiteralBNF</code>.
     */
    public static final String ID = "literal";

    /**
     * Creates a new <code>LiteralBNF</code>.
     */
    public LiteralBNF() {
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
        registerChild(BooleanLiteralBNF.ID);
        registerChild(DateTimeTimestampLiteralBNF.ID);
        registerChild(EnumLiteralBNF.ID);
        registerChild(NumericLiteralBNF.ID);
        registerChild(StringLiteralBNF.ID);
    }
}
