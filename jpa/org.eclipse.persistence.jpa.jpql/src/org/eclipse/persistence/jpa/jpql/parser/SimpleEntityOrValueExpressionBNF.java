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
 * The query BNF for a simple entity or value expression.
 *
 * <div><b>BNF:</b> <code>simple_entity_or_value_expression ::= identification_variable | input_parameter | literal</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class SimpleEntityOrValueExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "simple_entity_or_value_expression";

    /**
     * Creates a new <code>SimpleEntityOrValueExpressionBNF</code>.
     */
    public SimpleEntityOrValueExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();

        registerChild(IdentificationVariableBNF.ID);
        registerChild(InputParameterBNF.ID);
    }
}
