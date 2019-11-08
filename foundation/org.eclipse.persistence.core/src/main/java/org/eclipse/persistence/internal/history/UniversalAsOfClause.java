/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.history;

import org.eclipse.persistence.history.AsOfClause;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;

/**
 * INTERNAL:
 * <b>Purpose:</b> Represents a query level AS OF TIMESTAMP/SCN Oracle SQL clause.
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Will be applied to the entire selection criteria, even if
 * it is only set on the Query's expression builder.</li>
 * <li>Prints the AS OF clause before the alias name in the FROM clause.</li>
 * </ul>
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 * @author Stephen McRitchie
 */
public class UniversalAsOfClause extends AsOfClause {
    public UniversalAsOfClause(AsOfClause value) {
        super(value);
    }

    /**
     * INTERNAL:
     * Prints the as of clause for an expression inside of the FROM clause.
     */
    @Override
    public void printSQL(ExpressionSQLPrinter printer) {
        ((AsOfClause)super.getValue()).printSQL(printer);
    }

    /**
     * INTERNAL:
     * Gets the actual as of clause represented by <code>this</code>.
     */
    public AsOfClause getAsOfClause() {
        return (AsOfClause)super.getValue();
    }

    @Override
    public Object getValue() {
        return getAsOfClause().getValue();
    }

    @Override
    public boolean isUniversal() {
        return true;
    }

    public String printString() {
        return "Universal" + getAsOfClause().toString();
    }
}
