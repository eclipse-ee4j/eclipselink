/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for a entity or value expression.
 *
 * <div><b>BNF:</b> <code>entity_or_value_expression ::= single_valued_object_path_expression |
 * state_field_path_expression | simple_entity_or_value_expression</code><p></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EntityOrValueExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "entity_or_value_expression";

    /**
     * Creates a new <code>EntityOrValueExpressionBNF</code>.
     */
    public EntityOrValueExpressionBNF() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();

        registerChild(SingleValuedObjectPathExpressionBNF.ID);
        registerChild(StateFieldPathExpressionBNF.ID);
        registerChild(SimpleEntityOrValueExpressionBNF.ID);
    }
}
