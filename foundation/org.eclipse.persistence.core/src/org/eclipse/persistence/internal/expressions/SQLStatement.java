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
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: Mirror SQL behavior.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Mirror SQL behavior.
 * <li> Print as SQL string.
 * </ul>
 *    @author Dorin Sandu
 *    @since TOPLink/Java 1.0
 */
public abstract class SQLStatement implements Serializable, Cloneable {
    protected Expression whereClause;
    protected ExpressionBuilder builder;
    protected AbstractRecord translationRow;
    protected String hintString;

    /**
     * Return SQL call for the statement, through generating the SQL string.
     */
    public abstract DatabaseCall buildCall(AbstractSession session);

    /**
     * Clone the Statement
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public ExpressionBuilder getBuilder() {
        return builder;
    }

    public ExpressionBuilder getExpressionBuilder() {
        return builder;
    }

    /**
     * INTERNAL:
     * Return the Hint String for the statement
     */
    public String getHintString() {
        return hintString;
    }

    /**
     * INTERNAL:
     * Return the row for translation
     */
    public AbstractRecord getTranslationRow() {
        return translationRow;
    }

    public Expression getWhereClause() {
        return whereClause;
    }

    /**
     * INTERNAL:
     * Set the Hint String for the statement
     */
    public void setHintString(String newHintString) {
        hintString = newHintString;
    }

    protected void setBuilder(ExpressionBuilder aBuilder) {
        builder = aBuilder;
    }

    /**
     * INTERNAL:
     * Set the row for translation
     */
    public void setTranslationRow(AbstractRecord theRow) {
        translationRow = theRow;
    }

    public void setWhereClause(Expression expression) {
        whereClause = expression;
        if (expression != null) {
            builder = expression.getBuilder();
        }
    }

    /**
     * Try to print the SQL.
     */
    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write(Helper.getShortClassName(getClass()));
        writer.write("(");

        try {
            DatabaseCall call = buildCall(new DatabaseSessionImpl(new org.eclipse.persistence.sessions.DatabaseLogin()));
            writer.write(call.getSQLString());
        } catch (Exception exception) {
        }
        writer.write(")");

        return writer.toString();
    }
}
