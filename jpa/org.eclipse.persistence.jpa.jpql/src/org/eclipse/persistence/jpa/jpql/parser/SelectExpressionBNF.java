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
 * The query BNF for a select expression.
 * <p>
 * JPA 1.0:
 * <div><b>BNF:</b> <code>select_expression ::= single_valued_path_expression |
 *                                                     aggregate_expression |
 *                                                     identification_variable |
 *                                                     OBJECT(identification_variable) |
 *                                                     constructor_expression</code></div>
 * <p>
 * JPA 2.0:
 * <div><b>BNF:</b> <code>select_expression ::= single_valued_path_expression |
 *                                                     scalar_expression |
 *                                                     aggregate_expression |
 *                                                     identification_variable |
 *                                                     OBJECT(identification_variable) |
 *                                                     constructor_expression</code></div>
 * <p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class SelectExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "select_expression";

    /**
     * Creates a new <code>SelectExpressionBNF</code>.
     */
    public SelectExpressionBNF() {
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
        registerChild(AggregateExpressionBNF.ID);
        registerChild(SingleValuedPathExpressionBNF.ID);
        registerChild(IdentificationVariableBNF.ID);
        registerChild(ObjectExpressionBNF.ID);
        registerChild(ConstructorExpressionBNF.ID);
    }
}
