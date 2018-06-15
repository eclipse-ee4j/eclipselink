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
 * The query BNF for a <code><b>TREAT</b></code> expression.
 *
 * <div><b>BNF:</b> <code>join_treat ::= TREAT(join_collection_valued_path_expression AS subtype)</code></div>
 *
 * <div><b>BNF:</b> <code>join_treat ::= TREAT(join_single_valued_path_expression AS subtype)</code></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class TreatExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this <code>TreatExpressionBNF</code>.
     */
    public static final String ID = "treat_expression";

    /**
     * Creates a new <code>TreatExpressionBNF</code>.
     */
    public TreatExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        registerExpressionFactory(TreatExpressionFactory.ID);
    }
}
