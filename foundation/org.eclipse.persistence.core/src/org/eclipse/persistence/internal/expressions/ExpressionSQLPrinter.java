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
import java.util.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * <p><b>Purpose</b>: Expression SQL printer.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Print an expression in SQL format.
 * <li> Replaces FIELD types with field names from the descriptor.
 * <li> Replaces PARAMETER types with row or object values.
 * <li> Calls accessor to print primitive types.
 * </ul>
 *    @author Dorin Sandu
 *    @since TOPLink/Java 1.0
 */
public class ExpressionSQLPrinter {

    /**
     * Stores the current session. The session accessor
     * is used to print all the primitive types.
     */
    protected AbstractSession session;
    
    /**
     * Stores the current platform to access platform specific functions.
     */
    protected DatabasePlatform platform;
    
    /**
     * Stores the call being created.
     */
    protected SQLCall call;

    /**
     * Stores the row. Used to print PARAMETER nodes.
     */
    protected AbstractRecord translationRow;

    /**
     * Indicates whether fully qualified field names
     * (owner + table) should be used or not.
     */
    protected boolean shouldPrintQualifiedNames;

    // What we write on
    protected Writer writer;

    /** Used for distincts in functions. */
    protected boolean requiresDistinct;

    // Used in figuring out when to print a comma in the select line
    protected boolean isFirstElementPrinted;

    public ExpressionSQLPrinter(AbstractSession session, AbstractRecord translationRow, SQLCall call, boolean printQualifiedNames, ExpressionBuilder builder) {
        this.session = session;
        this.translationRow = translationRow;
        this.call = call;
        this.shouldPrintQualifiedNames = printQualifiedNames;
        // reference session's platform directly if builder or builder's descriptor is null
        if (builder == null || builder.getDescriptor() == null) {
            this.platform = getSession().getPlatform();
        } else {
            this.platform = (DatabasePlatform) getSession().getPlatform(builder.getDescriptor().getJavaClass());
        }
        this.requiresDistinct = false;
        isFirstElementPrinted = false;
    }

    /**
     * Return the call.
     */
    public SQLCall getCall() {
        return call;
    }

    /**
     * INTERNAL:
     * Return the database platform specific information.
     */
    public DatabasePlatform getPlatform() {
        return this.platform;
    }

    protected AbstractSession getSession() {
        return session;
    }

    /**
     * INTERNAL:
     * Return the row for translation
     */
    protected AbstractRecord getTranslationRow() {
        return translationRow;
    }

    public Writer getWriter() {
        return writer;
    }

    /**
     * INTERNAL:
     * Used in figuring out when to print a comma in the select clause
     */
    public boolean isFirstElementPrinted() {
        return isFirstElementPrinted;
    }

    public void printExpression(Expression expression) {
        translateExpression(expression);
    }

    public void printField(DatabaseField field) {
        if (field == null) {
            return;
        }

        try {
            // Print the field using either short or long notation i.e. owner + table name.
            if (shouldPrintQualifiedNames()) {
                getWriter().write(field.getQualifiedNameDelimited(platform));
            } else {
                getWriter().write(field.getNameDelimited(platform));
            }
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    public void printParameter(ParameterExpression expression) {
        try {
            getCall().appendTranslationParameter(getWriter(), expression, getPlatform(), getTranslationRow());

        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    public void printParameter(DatabaseField field) {
        getCall().appendTranslation(getWriter(), field);
    }

    public void printPrimitive(Object value) {
        if (value instanceof Collection) {
            printValuelist((Collection)value);
            return;
        }

        session.getPlatform().appendLiteralToCall(getCall(), getWriter(), value);
    }

    public void printNull(ConstantExpression nullValueExpression) {
        if(session.getPlatform().shouldBindLiterals()) {
            DatabaseField field = null;
            Expression localBase = nullValueExpression.getLocalBase();
            if(localBase.isFieldExpression()) {
                field = ((FieldExpression)localBase).getField();
            } else if(localBase.isQueryKeyExpression()) {
                field = ((QueryKeyExpression)localBase).getField();
            }
            session.getPlatform().appendLiteralToCall(getCall(), getWriter(), field);
        } else {
            session.getPlatform().appendLiteralToCall(getCall(), getWriter(), null);
        }
    }
    
    public void printString(String value) {
        try {
            getWriter().write(value);

        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    public void printValuelist(Collection values) {
        try {
            getWriter().write("(");
            Iterator valuesEnum = values.iterator();
            while (valuesEnum.hasNext()) {
                Object value = valuesEnum.next();
                // Support nested arrays for IN.
                if (value instanceof Collection) {
                    printValuelist((Collection)value);
                } else if (value instanceof Expression) {
                    ((Expression)value).printSQL(this);
                } else {
                    session.getPlatform().appendLiteralToCall(getCall(), getWriter(), value);
                }
                if (valuesEnum.hasNext()) {
                    getWriter().write(", ");
                }
            }
            getWriter().write(")");
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }
    
    /*
     * Same as printValuelist, but allows for collections containing expressions recursively
     */
    public void printList(Collection values) {
        try {
            getWriter().write("(");
            Iterator valuesEnum = values.iterator();
            while (valuesEnum.hasNext()) {
                Object value = valuesEnum.next();
                if (value instanceof Expression){
                    ((Expression)value).printSQL(this);
                }else{
                    session.getPlatform().appendLiteralToCall(getCall(), getWriter(), value);
                }
                if (valuesEnum.hasNext()) {
                    getWriter().write(", ");
                }
            }
            getWriter().write(")");
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    /**
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is required for batch reading.
     */
    public boolean requiresDistinct() {
        return requiresDistinct;
    }

    protected void setCall(SQLCall call) {
        this.call = call;
    }

    /**
     * INTERNAL:
     * Used in figuring out when to print a comma in the select clause
     */
    public void setIsFirstElementPrinted(boolean isFirstElementPrinted) {
        this.isFirstElementPrinted = isFirstElementPrinted;
    }

    /**
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is required for batch reading.
     */
    public void setRequiresDistinct(boolean requiresDistinct) {
        this.requiresDistinct = requiresDistinct;
    }

    protected void setSession(AbstractSession theSession) {
        session = theSession;
    }

    protected void setShouldPrintQualifiedNames(boolean shouldPrintQualifiedNames) {
        this.shouldPrintQualifiedNames = shouldPrintQualifiedNames;
    }

    /**
     * INTERNAL:
     * Set the row for translation
     */
    protected void setTranslationRow(AbstractRecord theRow) {
        translationRow = theRow;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public boolean shouldPrintParameterValues() {
        return getTranslationRow() != null;
    }

    protected boolean shouldPrintQualifiedNames() {
        return shouldPrintQualifiedNames;
    }

    /**
     * Translate an expression i.e. call the appropriate
     * translation method for the expression based on its
     * type. The translation method is then responsible
     * for translating the subexpressions.
     */
    protected void translateExpression(Expression theExpression) {
        theExpression.printSQL(this);
    }
}
