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
 * The query BNF for a function expression returning a date/time value.
 *
 * <div><b>BNF:</b> <code>functions_returning_datetime ::= CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP</code></div>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class FunctionsReturningDatetimeBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "functions_returning_datetime";

    /**
     * Creates a new <code>FunctionsReturningDatetimeBNF</code>.
     */
    public FunctionsReturningDatetimeBNF() {
        super(ID);
    }

    @Override
    protected void initialize() {
        super.initialize();
        registerExpressionFactory(DateTimeFactory.ID);
    }
}
