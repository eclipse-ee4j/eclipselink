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
 * The query BNF for a qualified identification variable expression.
 *
 * <div><b>BNF:</b> <code>qualified_identification_variable ::= KEY(identification_variable) |
 * VALUE(identification_variable) | ENTRY(identification_variable)</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class QualifiedIdentificationVariableBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "qualified_identification_variable";

    /**
     * Creates a new <code>QualifiedIdentificationVariableBNF</code>.
     */
    public QualifiedIdentificationVariableBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        registerExpressionFactory(KeyExpressionFactory.ID);
        registerExpressionFactory(ValueExpressionFactory.ID);
        registerExpressionFactory(EntryExpressionFactory.ID);
    }
}
