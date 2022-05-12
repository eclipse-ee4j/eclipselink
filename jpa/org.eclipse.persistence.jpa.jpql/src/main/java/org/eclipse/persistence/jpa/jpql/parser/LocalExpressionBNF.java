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
//       - Issue 317: Implement LOCAL DATE, LOCAL TIME and LOCAL DATETIME.
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The query BNF for local date/time expression.
 * <br>
 * Jakarta Persistence 3.1:
 * <div><b>BNF:</b> <code>
 * functions_returning_datetime ::= CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP |
 *                                  LOCAL local_datetime_type |
 *                                  extract_datetime_par
 *
 * local_datetime_type ::= DATE |   ..... matches Java java.time.LocalDate
 *                         TIME |   ..... matches Java java.time.LocalTime
 *                         DATETIME ..... matches Java java.time.LocalDateTime
 * </code></div>
 */
public class LocalExpressionBNF extends JPQLQueryBNF {

    /**
     * The unique identifier of this BNF rule.
     */
    public static final String ID = "local_expression";

    /**
     * Creates a new <code>LocalExpressionBNF</code>.
     */
    public LocalExpressionBNF() {
        super(ID);
    }

    @Override
    protected void initialize() {
        super.initialize();
        registerExpressionFactory(LocalExpressionFactory.ID);
        registerChild(LocalDateTypeBNF.ID);
    }

}
