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
package org.eclipse.persistence.internal.expressions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Field expressions represent a field of a table.
 * Their base is either a table or object expression.
 * This is used for mapping queries and to allow queries on non-mapped field or tables.
 */
public class FieldExpression extends DataExpression {
    protected DatabaseField field;
    protected transient DatabaseField aliasedField;

    /**
     * FieldExpression constructor comment.
     */
    public FieldExpression() {
        super();
    }

    /**
     * FieldExpression constructor comment.
     */
    public FieldExpression(DatabaseField newField) {
        super();
        field = newField;
    }

    /**
     * FieldExpression constructor comment.
     */
    public FieldExpression(DatabaseField newField, Expression myBase) {
        super();
        field = newField;
        baseExpression = myBase;
    }

    /**
     * INTERNAL:
     * Return if the expression is equal to the other.
     * This is used to allow dynamic expression's SQL to be cached.
     * This must be over written by each subclass.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!super.equals(object)) {
            return false;
        }
        FieldExpression expression = (FieldExpression)object;
        return ((getField() == expression.getField()) || ((getField() != null) && getField().equals(expression.getField())));
    }

    /**
     * INTERNAL:
     * Compute a consistent hash-code for the expression.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    @Override
    public int computeHashCode() {
        int hashCode = super.computeHashCode();
        if (getField() != null) {
            hashCode = hashCode + getField().hashCode();
        }
        return hashCode;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void clearAliases() {
        super.clearAliases();
        aliasedField = null;
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    @Override
    public String descriptionOfNodeType() {
        return "Field";
    }

    /**
     * INTERNAL:
     * Return the field appropriately aliased
     */
    @Override
    public DatabaseField getAliasedField() {
        if (aliasedField == null) {
            initializeAliasedField();
        }
        return aliasedField;

    }

    /**
     * Return the alias for our table
     */
    private DatabaseTable getAliasedTable() {
        DataExpression base = (DataExpression)getBaseExpression();
        DatabaseField field = getField();
        if (!field.hasTableName()) {
            if (base.getDescriptor() != null){
                field = base.getDescriptor().buildField(field);
            }
        }

        DatabaseTable alias = base.aliasForTable(field.getTable());
        if (alias == null) {
            return field.getTable();
        } else {
            return alias;
        }
    }

    /**
     * INTERNAL:
     * If there are any fields associated with this expression, return them
     */
    @Override
    public DatabaseField getClonedField() {
        return getField().clone();
    }

    /**
     * INTERNAL:
     * If there are any fields associated with this expression, return them
     */
    public Vector getClonedFields() {
        Vector result = new Vector(1);
        result.addElement(getField().clone());
        return result;
    }

    /**
     * INTERNAL:
     */
    @Override
    public DatabaseField getField() {
        return field;
    }

    /**
     * INTERNAL:
     * Return all the fields
     */
    @Override
    public Vector getFields() {
        Vector result = new Vector(1);
        result.addElement(getField());
        return result;
    }

    /**
     * INTERNAL:
     * Provide for conversion of the passed object's value, based on the referenced field value.
     */
    @Override
    public Object getFieldValue(Object value, AbstractSession session) {
        // Bug 418705
        if (getField() != null && value != null && (value.getClass() != getField().getType()) && !(value instanceof Collection) && !(value instanceof Enum)) {
            try {
                return session.getDatasourcePlatform().convertObject(value, getField().getType());
            } catch (ConversionException c) {}
        }
        return super.getFieldValue(value, session);
    }

    /**
     * INTERNAL:
     * Alias the database field for our current environment
     */
    private void initializeAliasedField() {
        DatabaseField tempField = getField().clone();
        DatabaseTable aliasedTable = getAliasedTable();

        //  Put in a special check here so that if the aliasing does nothing we don't cache the
        // result because it's invalid. This saves us from caching premature data if e.g. debugging
        // causes us to print too early"
        //    if (aliasedTable.equals(getField().getTable())) {
        //        return;
        //    } else {
        aliasedField = tempField;
        aliasedField.setTable(aliasedTable);
        //    }
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isAttribute() {
        return true;
    }

    @Override
    public boolean isFieldExpression() {
        return true;
    }

    /**
     * INTERNAL:
     * Normalize the expression into a printable structure.
     */
    @Override
    public Expression normalize(ExpressionNormalizer normalizer) {
        if (this.hasBeenNormalized) {
            return this;
        }
        Expression expression = super.normalize(normalizer);
        // to support custom types, print expressions derived from field expressions, table expressions and direct query keys with their aliases
        if (getBaseExpression() != null && getBaseExpression().isFieldExpression() || getBaseExpression().isTableExpression() ||
               (getBaseExpression().isQueryKeyExpression() && ((QueryKeyExpression)getBaseExpression()).isAttribute())){
            getBuilder().getStatement().setRequiresAliases(true);
        }
        return expression;
    }

    /**
     * INTERNAL:
     * Print SQL onto the stream, using the ExpressionPrinter for context
     */
    @Override
    public void printSQL(ExpressionSQLPrinter printer) {
        // to support custom types, print expressions derived from field expressions and direct query keys with their aliases
        // Note: This is also necessary for TableExpressions, but they are taken care of by their associated FieldExpression.
        if (getBaseExpression() != null &&  getBaseExpression().isFieldExpression() ||
                (getBaseExpression().isQueryKeyExpression() && ((QueryKeyExpression)getBaseExpression()).isAttribute())){
            getBaseExpression().printSQL(printer);
            printer.printString(".");
        }
        printer.printField(getAliasedField());
    }

    /**
     * INTERNAL:
     * Print java for project class generation
     */
    @Override
    public void printJava(ExpressionJavaPrinter printer) {
        getBaseExpression().printJava(printer);
        printer.printString(".getField(\"" + getField().getQualifiedName() + "\")");
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    @Override
    public Expression rebuildOn(Expression newBase) {
        DatabaseField field = getField();
        ClassDescriptor descriptor = null;
        // Check for possible table per class rebuild and translate fields to correct table.
        // TODO: JPA also allows for field to be renamed in subclasses, this needs to account for that (never has...).
        if (getBaseExpression().isExpressionBuilder() && newBase.isObjectExpression()) {
            if (newBase.getSession() != null) {
                descriptor = ((ObjectExpression)newBase).getDescriptor();
            }
            if ((descriptor != null) && descriptor.hasTablePerClassPolicy()) {
                field = field.clone();
                field.setTable(descriptor.getDefaultTable());
            }
        }
        FieldExpression expression = new FieldExpression(field, getBaseExpression().rebuildOn(newBase));
        expression.setSelectIfOrderedBy(selectIfOrderedBy());
        return expression;
    }

    /**
     * INTERNAL:
     * Set the field in the mapping.
     */
    public void setField(DatabaseField newField) {
        field = newField;
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
        Expression twistedBase = this.baseExpression.twistedForBaseAndContext(newBase, context, oldBase);
        DatabaseField field = getField();
        ClassDescriptor descriptor = null;
        // Check for possible table per class rebuild and translate fields to correct table.
        // TODO: JPA also allows for field to be renamed in subclasses, this needs to account for that (never has...).
        if (this.baseExpression.isExpressionBuilder() && newBase.isObjectExpression()
                && ((this.baseExpression == oldBase) || (oldBase == null))) {
            if (newBase.getSession() != null) {
                descriptor = ((ObjectExpression)newBase).getDescriptor();
            }
            if ((descriptor != null) && descriptor.hasTablePerClassPolicy()) {
                field = field.clone();
                field.setTable(descriptor.getDefaultTable());
            }
        }
        return twistedBase.getField(field);
    }

    /**
     * Do any required validation for this node. Throw an exception if it's incorrect.
     */
    @Override
    public void validateNode() {
        DataExpression base = (DataExpression)getBaseExpression();
        if (getField().getTable().hasName()) {
            List<DatabaseTable> tables = base.getOwnedTables();
            if ((tables != null) && (!tables.contains((getField().getTable())))) {
                throw QueryException.invalidTableForFieldInExpression(getField());
            }
        }
    }

    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for valueable expressions.
     */
    @Override
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        // Joins not supported.
        if (!getBaseExpression().isExpressionBuilder()) {
            throw QueryException.cannotConformExpression();
        }

        // For bug 2780817 get the mapping directly from the object.  In EJB 2.0
        // inheritance, each child must override mappings defined in an abstract
        // class with its own.
        DatabaseMapping mapping = session.getDescriptor(object.getClass()).getObjectBuilder().getMappingForField(getField());
        if (mapping == null) {
            throw QueryException.cannotConformExpression();
        }

        return mapping.valueFromObject(object, getField(), session);
    }

    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    @Override
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write(getField().toString());
    }

    /**
     * INTERNAL: called from SQLSelectStatement.writeFieldsFromExpression(...)
     */
    @Override
    public void writeFields(ExpressionSQLPrinter printer, List<DatabaseField> newFields, SQLSelectStatement statement) {
        DatabaseField field = getField();

        if (field != null) {
            newFields.add(field);
            writeField(printer, field, statement);
        }
    }

    @Override
    protected void writeField(ExpressionSQLPrinter printer, DatabaseField field, SQLSelectStatement statement) {
        if (this.field == field){
            //print ", " before each selected field except the first one
            if (printer.isFirstElementPrinted()) {
                printer.printString(", ");
            } else {
                printer.setIsFirstElementPrinted(true);
            }
            printSQL(printer);
        } else {
            super.writeField(printer, field, statement);
        }

    }

    /**
     * INTERNAL:
     * writes the field for fine-grained pessimistic locking.
     */
    protected void writeForUpdateOf(ExpressionSQLPrinter printer, SQLSelectStatement statement) {
        if (printer.getPlatform().shouldPrintAliasForUpdate()) {
            writeAlias(printer, field, statement);
        } else {
            writeField(printer, field, statement);
        }
    }
}
