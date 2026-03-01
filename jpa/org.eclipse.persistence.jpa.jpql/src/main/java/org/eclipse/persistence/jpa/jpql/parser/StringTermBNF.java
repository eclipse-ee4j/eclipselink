/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for a string term expression.
 *
 * <div><b>BNF:</b> <code>string_term ::= string_factor | string_term { || } string_factor</code></div>
 *
 * @version 4.1
 * @since 4.1
 * @author Radek Felcman
 */
@SuppressWarnings("nls")
public final class StringTermBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "string_term";

    /**
     * Creates a new <code>StringTermBNF</code>.
     */
    public StringTermBNF() {
        super(ID);
    }

    @Override
    protected void initialize() {
        super.initialize();
        setHandleAggregate(true);
        setFallbackBNFId(StringFactorBNF.ID);
        registerExpressionFactory(StringExpressionFactory.ID);
        registerChild(StringFactorBNF.ID);
    }
}
