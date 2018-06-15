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
 * The query BNF for a constructor item expression.
 *
 * <div><b>BNF:</b> <code>constructor_item ::= single_valued_path_expression |
 *                                                    scalar_expression |
 *                                                    aggregate_expression |
 *                                                    identification_variable</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class ConstructorItemBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "constructor_item";

    /**
     * Creates a new <code>ConstructorItemBNF</code>.
     */
    public ConstructorItemBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setHandleCollection(true);
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(LiteralExpressionFactory.ID);
        registerChild(SingleValuedPathExpressionBNF.ID);
        registerChild(ScalarExpressionBNF.ID);
        registerChild(AggregateExpressionBNF.ID);
        registerChild(IdentificationVariableBNF.ID);
    }
}
