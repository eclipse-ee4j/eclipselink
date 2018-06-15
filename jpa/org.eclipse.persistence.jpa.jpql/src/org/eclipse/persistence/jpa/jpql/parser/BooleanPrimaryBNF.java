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
 * The query BNF for a boolean primary expression.
 * <p>
 * JPA 1.0:
 * <div><b>BNF:</b> <code>boolean_primary ::= state_field_path_expression |
 *                                                   boolean_literal |
 *                                                   input_parameter</code><p></div>
 *
 * JPA 2.0:
 * <div><b>BNF:</b> <code>boolean_primary ::= state_field_path_expression |
 *                                                   boolean_literal |
 *                                                   input_parameter |
 *                                                   case_expression</code><p></div>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class BooleanPrimaryBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "boolean_primary";

    /**
     * Creates a new <code>BooleanPrimaryBNF</code>.
     */
    public BooleanPrimaryBNF() {
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
        registerChild(StateFieldPathExpressionBNF.ID);
        registerChild(BooleanLiteralBNF.ID);
        registerChild(InputParameterBNF.ID);
    }
}
