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
 * The query BNF for a range variable declaration expression.
 * <p>
 * JPA:
 * <div><b>BNF:</b> <code>range_variable_declaration ::= entity_name [AS] identification_variable</code></div>
 * <p>
 * EclipseLink 2.4:
 * <div><b>BNF:</b> <code>range_variable_declaration ::= { range_variable_declaration | (subquery) } [AS] identification_variable</code></div>
 * <p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class RangeVariableDeclarationBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "range_variable_declaration";

    /**
     * Creates a new <code>SubselectIdentificationVariableDeclarationBNF</code>.
     */
    public RangeVariableDeclarationBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(RangeVariableDeclarationFactory.ID);
    }
}
