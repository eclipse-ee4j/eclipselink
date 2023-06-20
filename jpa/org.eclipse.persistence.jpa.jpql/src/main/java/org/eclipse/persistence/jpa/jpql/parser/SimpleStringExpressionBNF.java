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
//
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for a simple string expression.
 *
 * <div><b>BNF:</b> <code>simple_string_expression ::= string_term | simple_string_expression { || } string_term</code></div>
 *
 * @version 4.1
 * @since 4.1
 * @author Radek Felcman
 */
@SuppressWarnings("nls")
public final class SimpleStringExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "simple_string_expression";

    /**
     * Creates a new <code>SimpleStringExpressionBNF</code>.
     */
    public SimpleStringExpressionBNF() {
        super(ID);
    }

    @Override
    protected void initialize() {
        super.initialize();
        setHandleAggregate(true);
        setFallbackBNFId(StringTermBNF.ID);
        registerExpressionFactory(StringExpressionFactory.ID);
        registerChild(StringTermBNF.ID);
    }
}
