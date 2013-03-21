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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Used for wrapping constant values.
 */
public class ConstantExpression extends Expression {
    protected Object value;
    protected Expression localBase;

    public ConstantExpression() {
        super();
    }

    public ConstantExpression(Object newValue, Expression baseExpression) {
        super();
        value = newValue;
        localBase = baseExpression;
    }

    /**
     * INTERNAL:
     * Return if the expression is equal to the other.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!super.equals(object)) {
            return false;
        }
        ConstantExpression expression = (ConstantExpression) object;
        return ((getValue() == expression.getValue()) || ((getValue() != null) && getValue().equals(expression.getValue())));
    }
        
    /**
     * INTERNAL:
     * Compute a consistent hash-code for the expression.
     * This is used to allow dynamic expression's SQL to be cached.
     */
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
    public String descriptionOfNodeType() {
        return "Constant";
    }

    /**
     * Return the expression builder which is the ultimate base of this expression, or
     * null if there isn't one (shouldn't happen if we start from a root)
     */
    public ExpressionBuilder getBuilder() {
        if(this.localBase != null) {
            return this.localBase.getBuilder();
        } else {
            return null;
        }
    }

    public Expression getLocalBase() {
        return localBase;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isConstantExpression() {
        return true;
    }

    /**
     * INTERNAL:
     */
    public boolean isValueExpression() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Normalize collection of values if they are expressions.
     * or collection of collection expressions.
     */
    public Expression normalize(ExpressionNormalizer normalizer) {
        super.normalize(normalizer);
        if (value == null)
            return this;
        
        if (value instanceof Collection) {
            normalizeValueList(normalizer, (Collection)value);
        }
        return this;
    }
    
    private void normalizeValueList(ExpressionNormalizer normalizer, Collection valueCollection) {
        for (Object obj : valueCollection) {
            if (obj instanceof Collection) {
                normalizeValueList(normalizer, (Collection)obj);
            } else if (obj instanceof Expression) {
                ((Expression)obj).normalize(normalizer);
            }
        }
    }

    /**
     * INTERNAL:
     * Used for cloning.
     */
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        localBase = localBase.copiedVersionFrom(alreadyDone);
    }

    /**
     * INTERNAL:
     * Print SQL onto the stream, using the ExpressionPrinter for context
     */
    public void printSQL(ExpressionSQLPrinter printer) {
        Object value = getLocalBase().getFieldValue(getValue(), getSession());
        if(value == null) {
            printer.printNull(this);
        } else {
            printer.printPrimitive(value);
        }
    }

    /**
     * INTERNAL:
     * Print java for project class generation
     */
    public void printJava(ExpressionJavaPrinter printer) {
        printer.printJava(getValue());
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    public Expression rebuildOn(Expression newBase) {
        Expression result = (ConstantExpression)clone();
        result.setLocalBase(getLocalBase().rebuildOn(newBase));
        return result;
    }

    /**
     * INTERNAL:
     * Search the tree for any expressions (like SubSelectExpressions) that have been
     * built using a builder that is not attached to the query.  This happens in case of an Exists
     * call using a new ExpressionBuilder().  This builder needs to be replaced with one from the query.
     */
    public void resetPlaceHolderBuilder(ExpressionBuilder queryBuilder){
        return;
    }

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
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        // PERF: direct-access.
        return this.localBase.getFieldValue(this.value, session);
    }

    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write(String.valueOf(getValue()));
    }

    /**
     * INTERNAL:
     * Append the constant value into the printer
     */
    @Override
    public void writeFields(ExpressionSQLPrinter printer, Vector newFields, SQLSelectStatement statement) {
        if (printer.getPlatform().isDynamicSQLRequiredForFunctions()) {
            printer.getCall().setUsesBinding(false);
        }
        //print ", " before each selected field except the first one
        if (printer.isFirstElementPrinted()) {
            printer.printString(", ");
        } else {
            printer.setIsFirstElementPrinted(true);
        }

        // This field is a constant value, so any name can be used.
        newFields.addElement(new DatabaseField("*"));
        printSQL(printer);
    }
}
