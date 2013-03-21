/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.history;

import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.expressions.*;

/**
 * INTERNAL:
 * <b>Purpose:</b> Represents a query level AS OF TIMESTAMP/SCN Oracle SQL clause.
 * <p><b>Responsibilities:<b>
 * <ul><li>Will be applied to the entire selection criteria, even if
 * it is only set on the Query's expression builder.
 * <li>Prints the AS OF clause before the alias name in the FROM clause.
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

    public Object getValue() {
        return getAsOfClause().getValue();
    }

    public boolean isUniversal() {
        return true;
    }

    public String printString() {
        return "Universal" + getAsOfClause().toString();
    }
}
