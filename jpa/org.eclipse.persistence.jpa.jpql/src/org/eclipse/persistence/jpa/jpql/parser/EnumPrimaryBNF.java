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
 * The query BNF for an enum primary expression.
 * <p>
 * JPA 1.0:
 * <div><b>BNF:</b> <code>enum_primary ::= state_field_path_expression |
 *                                                enum_literal |
 *                                                input_parameter</code><p></div>
 *
 * JPA 2.0:
 * <div><b>BNF:</b> <code>enum_primary ::= state_field_path_expression |
 *                                                enum_literal |
 *                                                input_parameter |
 *                                                case_expression</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EnumPrimaryBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "enum_primary";

    /**
     * Creates a new <code>EnumPrimaryBNF</code>.
     */
    public EnumPrimaryBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();

        registerChild(StateFieldPathExpressionBNF.ID);
        registerChild(EnumLiteralBNF.ID);
        registerChild(InputParameterBNF.ID);
    }
}
