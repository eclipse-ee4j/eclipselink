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
package org.eclipse.persistence.history;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * <b>Purpose:</b>Wraps an immutable value for a past time, represented as a
 * database system change number.
 * <p>
 * This should be specified with an Oracle platform supporting flashback,
 * and the value will be written to the SQL FROM clause:
 * <p>
 * SELECT ... FROM EMPLOYEE AS OF SCN (value) t0, ...
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 * @author Stephen McRitchie
 * @see AsOfClause
 * @see org.eclipse.persistence.platform.database.OraclePlatform#getSystemChangeNumberQuery
 */
public class AsOfSCNClause extends AsOfClause {
    public AsOfSCNClause(Number systemChangeNumber) {
        super(systemChangeNumber);
    }

    public AsOfSCNClause(Long systemChangeNumber) {
        super(systemChangeNumber);
    }

    public AsOfSCNClause(long systemChangeNumber) {
        super(Long.valueOf(systemChangeNumber));
    }

    public AsOfSCNClause(Expression expression) {
        super(expression);
    }

    /**
     * INTERNAL:
     * Prints the as of clause for an expression inside of the FROM clause.
     */
    public void printSQL(ExpressionSQLPrinter printer) {
        printer.printString("AS OF SCN (");
        Object value = getValue();
        if (value instanceof Expression) {
            // Sort of an implementation of native sql.
            // Print AS OF SCN (1000L - 45L) not AS OF ('1000L - 45L').
            if ((value instanceof ConstantExpression) && (((ConstantExpression)value).getValue() instanceof String)) {
                printer.printString((String)((ConstantExpression)value).getValue());
            } else {
                printer.printExpression((Expression)value);
            }
        } else {
            ConversionManager converter = ConversionManager.getDefaultManager();
            value = converter.convertObject(value, ClassConstants.LONG);
            printer.printPrimitive(value);
        }
        printer.printString(")");
    }

    /**
     * PUBLIC:
     */
    public boolean isAsOfSCNClause() {
        return true;
    }
}
