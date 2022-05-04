/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for Jakarta Persistence 2.2, 3.0 and 3.1
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for the parameters of the <code><b>POWER</b></code> expression.
 * <br>
 * Jakarta Persistence 3.1:
 * <div><b>BNF:</b> <code>expression ::= POWER(arithmetic_expression, arithmetic_expression)</code></div>
 */
public class InternalPowerExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "power_item";

    /**
     * Creates a new <code>InternalPowerExpressionBNF</code>.
     */
    public InternalPowerExpressionBNF() {
        super(ID);
    }

    @Override
    protected void initialize() {
        super.initialize();
        setFallbackBNFId(ID);
        setFallbackExpressionFactoryId(LiteralExpressionFactory.ID);
        registerChild(ArithmeticExpressionBNF.ID);
    }

}
