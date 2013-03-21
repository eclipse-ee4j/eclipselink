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

import java.io.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * <b>Purpose:</b>Wraps an immutable value for a past time.
 * A session, query, or expression can be as of a past time.
 * <p>
 * For Oracle 9R2 Flasback corresponds to the sub clause which appears between
 * the table and alias name in the FROM clause:
 * <code>SELECT ... FROM EMPLOYEE AS OF TIMESTAMP (value) t0, ...</code>
 * <p>For generic historical schema support, a special criteria can be added to
 * the where clause for each table in a select:
 * <code>((t0.ROW_START <= value) AND ((t0.END IS NULL) OR (t1.END > value)))</code>
 * <p><b>Responsibilities:<b>
 * <ul>
 * <li>By default AsOfClause is a timestamp.  To specify a system change number use AsOfSCNClause.
 * <li>For Oracle 9R2 Flashback prints the correct AS OF clause before the alias name in the FROM clause.
 * <li>Read-only: the wrapped value can not change.
 * </ul>
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 * @author Stephen McRitchie
 * @see org.eclipse.persistence.expressions.Expression#asOf(AsOfClause)
 * @see org.eclipse.persistence.queries.ObjectLevelReadQuery#asOf(AsOfClause)
 * @see org.eclipse.persistence.sessions.Session#acquireSessionAsOf(AsOfClause)
 * @see HistoryPolicy
 */
public class AsOfClause implements Serializable {
    public static final AsOfClause NO_CLAUSE = new AsOfClause((Expression)null);
    private Object value;

    protected AsOfClause() {
    }
    
    public AsOfClause(java.util.Date date) {
        this.value = date;
    }

    public AsOfClause(java.sql.Timestamp timestamp) {
        this.value = timestamp;
    }

    public AsOfClause(java.util.Calendar calendar) {
        this.value = calendar;
    }

    public AsOfClause(long time) {
        this.value = Long.valueOf(time);
    }

    public AsOfClause(Long time) {
        this.value = time;
    }

    protected AsOfClause(Number number) {
        this.value = number;
    }

    public AsOfClause(Expression expression) {
        this.value = expression;
    }

    protected AsOfClause(AsOfClause wrappedValue) {
        this.value = wrappedValue;
    }
    
    /**
     * INTERNAL:
     * Return if the as of is equal to the other.
     * Equality of asOf clauses is complex (with subclasses),
     * so only use identity.
     */
    public boolean equals(Object object) {
        return this == object;
    }
    
    /**
     * INTERNAL:
     * Prints the as of clause for an expression inside of the FROM clause.
     */
    public void printSQL(ExpressionSQLPrinter printer) {
        printer.printString("AS OF TIMESTAMP (");
        Object value = getValue();
        if (value instanceof Expression) {
            // Sort of an implementation of native sql.
            // Print AS OF TIMESTAMP (SYSDATE - 1000*60*10) not AS OF ('SYSDATE - 1000*60*10').
            if ((value instanceof ConstantExpression) && (((ConstantExpression)value).getValue() instanceof String)) {
                printer.printString((String)((ConstantExpression)value).getValue());
            } else {
                printer.printExpression((Expression)value);
            }
        } else {
            ConversionManager converter = ConversionManager.getDefaultManager();
            value = converter.convertObject(value, ClassConstants.TIMESTAMP);
            printer.printPrimitive(value);
        }
        printer.printString(")");
    }

    /**
     * PUBLIC:
     * The past time represented by the receiver.  Either a timestamp, a system
     * change number, or an Expression.
     */
    public Object getValue() {
        return value;
    }

    /**
     * PUBLIC:
     * Indicates that <code>value</code> is a system change number or an expression
     * evaluating to one.
     * <p>In Oracle the value will have to be printed using the syntax <code>AS OF SCN(value)</code>
     * instead of <code>AS OF TIMESTAMP(value)</code>.
     * @see AsOfSCNClause
     */
    public boolean isAsOfSCNClause() {
        return false;
    }

    /**
     * PUBLIC:
     * Answers if this is a UniversalAsOfClause, one to be applied
     * to the entire selection criteria.
     * <p>Used when a query is made as of a past time.
     */
    public boolean isUniversal() {
        return false;
    }

    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write(Helper.getShortClassName(getClass()));
        writer.write("(");
        writer.write(String.valueOf(getValue()));
        writer.write(")");
        return writer.toString();
    }
}
