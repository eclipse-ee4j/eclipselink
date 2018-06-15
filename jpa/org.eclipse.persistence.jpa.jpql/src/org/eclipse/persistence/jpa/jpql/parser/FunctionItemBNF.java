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
 * The query BNF for the parameters of a function expression.
 *
 * <div><b>BNF:</b> <code>func_item ::= literal |
 *                                             state_field_path_expression |
 *                                             input_parameter |
 *                                             scalar_expression</code><p></div>
 *
 * @version 2.5
 * @since 2.4
 * @author James Sutherland
 */
@SuppressWarnings("nls")
public final class FunctionItemBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this {@link FunctionItemBNF}.
     */
    public static final String ID = "function_item";

    /**
     * Creates a new <code>FunctionItemBNF</code>.
     */
    public FunctionItemBNF() {
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
        registerChild(LiteralBNF.ID);
        registerChild(StateFieldPathExpressionBNF.ID);
        registerChild(InputParameterBNF.ID);
        registerChild(ScalarExpressionBNF.ID);
    }
}
