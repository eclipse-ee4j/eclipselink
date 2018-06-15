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
 * The query BNF for the <b>TYPE</b> expression.
 *
 * <div><b>BNF:</b> <code>entity_type_expression ::= type_discriminator |
 *                                                          entity_type_literal |
 *                                                          input_parameter</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EntityTypeExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "entity_type_expression";

    /**
     * Creates a new <code>EntityTypeExpressionBNF</code>.
     */
    public EntityTypeExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(EntityTypeLiteralBNF.ID);
        registerChild(TypeExpressionBNF.ID);
        registerChild(InputParameterBNF.ID);
        registerChild(EntityTypeLiteralBNF.ID);
    }
}
