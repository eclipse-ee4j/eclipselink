/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * The query BNF for the first two parameters of the <code><b>LOCATE</b></code> expression.
 * <br>
 * JPA 1.0, 2.0:
 * <div><b>BNF:</b> <code>expression ::= LOCATE(string_primary, string_primary [, simple_arithmetic_expression])</code></div>
 * <br>
 * JPA 2.1:
 * <div><b>BNF:</b> <code>expression ::= LOCATE(string_expression, string_expression [, arithmetic_expression])</code></div>
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InternalLocateStringExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "locate_item";

    /**
     * Creates a new <code>InternalLocateStringExpressionBNF</code>.
     */
    public InternalLocateStringExpressionBNF() {
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
