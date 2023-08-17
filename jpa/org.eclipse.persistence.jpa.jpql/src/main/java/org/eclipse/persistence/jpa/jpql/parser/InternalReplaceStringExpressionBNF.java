/*
 * Copyright (c) 2006, 2023 Oracle and/or its affiliates. All rights reserved.
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
 * The query BNF for the parameter of the <code><b>LENGTH</b></code> expression.
 * <br>
 * JPA 3.2:
 * <div><b>BNF</b> ::= REPLACE(string_expression, string_expression, string_expression)</div>
 *
 * @version 4.1
 * @since 4.1
 * @author Radek Felcman
 */
@SuppressWarnings("nls")
public final class InternalReplaceStringExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "replace_item";

    /**
     * Creates a new <code>InternalReplaceStringExpressionBNF</code>.
     */
    public InternalReplaceStringExpressionBNF() {
        super(ID);
    }

    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(LiteralExpressionFactory.ID);
        registerChild(StringPrimaryBNF.ID);
    }
}
