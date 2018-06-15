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
 * The query BNF for a general identification variable expression.
 *
 * <div><b>BNF:</b> <code>general_identification_variable ::= identification_variable |
 *                                                                   KEY(identification_variable) |
 *                                                                   VALUE(identification_variable)</code></div>
 * <p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class GeneralIdentificationVariableBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "general_identification_variable";

    /**
     * Creates a new <code>GeneralIdentificationVariableBNF</code>.
     */
    public GeneralIdentificationVariableBNF() {
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
        registerExpressionFactory(IdentificationVariableFactory.ID);
        registerExpressionFactory(KeyExpressionFactory.ID);
        registerExpressionFactory(ValueExpressionFactory.ID);
    }
}
