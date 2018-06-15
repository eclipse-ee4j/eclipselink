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
 * The query BNF for a result variable expression.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class SimpleResultVariableBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = InternalSimpleSelectExpressionBNF.ID;

    /**
     * Creates a new <code>SimpleResultVariableBNF</code>.
     */
    public SimpleResultVariableBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {

        super.initialize();

        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(ResultVariableFactory.ID);
        registerChild(SimpleSelectExpressionBNF.ID);

        // The ResultVariable's BNF needs to be registered as a child of this one. This is required
        // for validation to work properly. Basically, when checking if the select expression is
        // valid, it checks its BNF with the non-compound children of this one
        registerChild(ResultVariableBNF.ID);
    }
}
