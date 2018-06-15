/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     dminsky - added writeFields API overriding behavior from Expression
package org.eclipse.persistence.internal.expressions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Used for wrapping literal values.
 */
public class LiteralExpression extends Expression {

    protected String value;
    protected Expression localBase;

    public LiteralExpression() {
        super();
    }

    public LiteralExpression(String newValue, Expression baseExpression) {
        super();
        value = newValue;
        localBase = baseExpression;
    }

    /**
     * INTERNAL:
     * Return if the expression is equal to the other.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!super.equals(object)) {
            return false;
        }
        LiteralExpression expression = (LiteralExpression) object;
        return ((getValue() == expression.getValue()) || ((getValue() != null) && getValue().equals(expression.getValue())));
    }

    /**
     * INTERNAL:
     * Compute a consistent hash-code for the expression.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    @Override
    public int computeHashCode() {
        int hashCode = super.computeHashCode();
        if (getValue() != null) {
            hashCode = hashCode + getValue().hashCode();
        }
        return hashCode;
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    @Override
    public String descriptionOfNodeType() {
        return "Literal";
    }

    /**
     * Return the expression builder which is the ultimate base of this expression, or
     * null if there isn't one (shouldn't happen if we start from a root)
     */
    @Override
    public ExpressionBuilder getBuilder() {
        if(this.localBase != null) {
            return this.localBase.getBuilder();
        } else {
            return null;
        }
    }

    protected Expression getLocalBase() {
        return localBase;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean isLiteralExpression() {
        return true;
    }

    /**
     * INTERNAL:
     * Used for cloning.
     */
    @Override
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        localBase = localBase.copiedVersionFrom(alreadyDone);
    }

    /**
     * INTERNAL:
     * Print SQL onto the stream, using the ExpressionPrinter for context
     */
    @Override
    public void printSQL(ExpressionSQLPrinter printer) {
        printer.printString(value);
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    @Override
    public Expression rebuildOn(Expression newBase) {
        Expression result = (LiteralExpression)clone();
        result.setLocalBase(getLocalBase().rebuildOn(newBase));
        return result;
    }

    /**
     * INTERNAL:
     * Search the tree for any expressions (like SubSelectExpressions) that have been
     * built using a builder that is not attached to the query.  This happens in case of an Exists
     * call using a new ExpressionBuilder().  This builder needs to be replaced with one from the query.
     */
    @Override
    public void resetPlaceHolderBuilder(ExpressionBuilder queryBuilder){
        return;
    }


    @Override
    public void setLocalBase(Expression e) {
        localBase = e;
    }

    /**
     * INTERNAL:
     * Rebuild myself against the base, with the values of parameters supplied by the context
     * expression. This is used for transforming a standalone expression (e.g. the join criteria of a mapping)
     * into part of some larger expression. You normally would not call this directly, instead calling twist
     * See the comment there for more details"
     */
    @Override
    public Expression twistedForBaseAndContext(Expression newBase, Expression context, Expression oldBase) {
        return (Expression)this.clone();
    }

    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for valueable expressions.
     */
    @Override
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        return getLocalBase().getFieldValue(getValue(), session);
    }

    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    @Override
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write(getValue());
    }

    /**
     * INTERNAL:
     * Append the literal value into the printer, accounting for the first element
     */
    @Override
    public void writeFields(ExpressionSQLPrinter printer, Vector newFields, SQLSelectStatement statement) {
        // print ", " before each selected field except the first one
        if (printer.isFirstElementPrinted()) {
            printer.printString(", ");
        } else {
            printer.setIsFirstElementPrinted(true);
        }

        newFields.addElement(new DatabaseField(getValue()));
        printSQL(printer);
    }

}
