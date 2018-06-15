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
 * The query BNF for the items of a <b>COLUMN</b> expression.
 *
 * <div><b>BNF:</b> <code>column_expression ::= COLUMN('function' {, [single_valued_object_path_expression | identification_variable]}+)</code><p></div>
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InternalColumnExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "column_item";

    /**
     * Creates a new <code>InternalColumnExpressionBNF</code>.
     */
    public InternalColumnExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setHandleCollection(true); // For invalid queries
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(LiteralExpressionFactory.ID);
        registerChild(SingleValuedObjectPathExpressionBNF.ID);
        registerChild(StateFieldPathExpressionBNF.ID);
        registerChild(IdentificationVariableBNF.ID);
    }
}
