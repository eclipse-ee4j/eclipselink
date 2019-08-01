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
//     IBM - Bug 537795: CASE THEN and ELSE scalar expression Constants should not be casted to CASE operand type
package org.eclipse.persistence.internal.expressions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.transform.LikePatternTransformation;

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
        this.value = newValue;
        this.localBase = baseExpression;
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
        ConstantExpression expression = (ConstantExpression) object;
        return ((this.value == expression.getValue()) || ((this.value != null) && this.value.equals(expression.getValue())));
    }

    /**
     * INTERNAL:
     * Compute a consistent hash-code for the expression.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    @Override
    public int computeHashCode() {
        int hashCode = super.computeHashCode();
        if (this.value != null) {
            hashCode = hashCode + this.value.hashCode();
        }
        return hashCode;
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    @Override
    public String descriptionOfNodeType() {
        return "Constant";
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

    public Expression getLocalBase() {
        return this.localBase;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean isConstantExpression() {
        return true;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isValueExpression() {
        return true;
    }

    /**
     * INTERNAL:
     * Normalize collection of values if they are expressions.
     * or collection of collection expressions.
     */
    @Override
    public Expression normalize(ExpressionNormalizer normalizer) {
        super.normalize(normalizer);
        if (this.value == null)
            return this;

        if (this.value instanceof Collection) {
            normalizeValueList(normalizer, (Collection)this.value);
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
    @Override
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        if(this.localBase != null) {
            this.localBase = this.localBase.copiedVersionFrom(alreadyDone);
        }
    }

    /**
     * INTERNAL:
     * Print SQL onto the stream, using the ExpressionPrinter for context
     */
    @Override
    public void printSQL(ExpressionSQLPrinter printer) {
        Object value = this.value;
        if(this.localBase != null) {
            value = this.localBase.getFieldValue(value, getSession());
        }
        if(value == null) {
            printer.printNull(this);
        } else {
            printer.printPrimitive(value);
        }
    }

    // Bug# 545940 - Register delayed transformaion for JPQL LIKE expression
    // TODO: Transformation setup should be better done in caller method as lambda
    /**
     * INTERNAL:
     * Print SQL and apply transformation on value to be printed
     * @param printer target SQL expression printer
     * @param transform SQL parameters delayed transformation
     * @param arg transformation method expression argument
     */
    @Override
    public void printSQL(ExpressionSQLPrinter printer, LikePatternTransformation.Function transform, Expression arg) {
        if (transform == null || arg == null || value == null) {
            printSQL(printer);
        } else {
            if (arg.isConstantExpression()) {
                String argValue = ((ConstantExpression)arg).getValue().toString();
                String valueToPrint = transform.transform((String)value, argValue);
                printer.printPrimitive(valueToPrint);
            } else {
                // Index of next parameter value is determined from List size before adding it.
                List<Object> parameters = printer.getCall().getParameters();
                int index = parameters != null ? parameters.size() : 0;
                printSQL(printer);
                printer.getCall().addTransformation(
                        new LikePatternTransformation(
                                this, index, arg, printer.getPlatform()::escapeLikePattern));
            }
        }
    }

    /**
     * INTERNAL:
     * Print java for project class generation
     */
    @Override
    public void printJava(ExpressionJavaPrinter printer) {
        printer.printJava(this.value);
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    @Override
    public Expression rebuildOn(Expression newBase) {
        Expression result = (ConstantExpression)clone();

        Expression localBase = null;
        if(this.localBase != null) {
            localBase = this.localBase.rebuildOn(newBase);
        }
        result.setLocalBase(localBase);

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
        this.localBase = e;
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
        return this;
    }

    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for valueable expressions.
     */
    @Override
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        // PERF: direct-access.
        if(this.localBase != null) {
            return this.localBase.getFieldValue(this.value, session);
        }
        return this.value;
    }

    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    @Override
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write(String.valueOf(this.value));
    }

    /**
     * INTERNAL:
     * Append the constant value into the printer
     */
    @Override
    public void writeFields(ExpressionSQLPrinter printer, List<DatabaseField> newFields, SQLSelectStatement statement) {
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
        newFields.add(new DatabaseField("*"));
        printSQL(printer);
    }
}
