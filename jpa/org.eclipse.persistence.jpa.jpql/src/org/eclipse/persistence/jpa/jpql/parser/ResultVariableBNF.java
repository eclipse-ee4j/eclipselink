/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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

/**
 * The query BNF for a result variable expression.
 *
 * @version 2.5.1
 * @since 2.3
 * @author Pascal Filion
 */
public final class ResultVariableBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule, which is the same as {@link InternalSelectExpressionBNF#ID}
     * in order to override it.
     */
    public static final String ID = InternalSelectExpressionBNF.ID;

    /**
     * Creates a new <code>ResultVariableBNF</code>.
     */
    public ResultVariableBNF() {
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
        registerChild(SelectExpressionBNF.ID);

        // The ResultVariable's BNF is this one so it needs to be registered as a child of itself.
        // This is required for validation to work properly. Basically, when checking if the select
        // expression is valid, it checks its BNF with the non-compound children of this one
        registerChild(ResultVariableBNF.ID);
    }
}
