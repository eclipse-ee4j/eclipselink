/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.util.*;
import java.io.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.history.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.querykeys.*;

/**
 * Superclass for all expression that have a context.
 * i.e. a base expression.
 */
public abstract class DataExpression extends BaseExpression {
    protected Vector derivedTables;
    protected Vector derivedFields;
    protected boolean hasBeenNormalized = false;
    protected TableAliasLookup tableAliases;
    protected AsOfClause asOfClause;

    /**
     * DataExpression constructor comment.
     */
    public DataExpression() {
        super();
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
        DataExpression expression = (DataExpression) object;
        return ((getBaseExpression() == expression.getBaseExpression()) || ((getBaseExpression() != null) && getBaseExpression().equals(expression.getBaseExpression())))
            && ((getAsOfClause() == expression.getAsOfClause()) || ((getAsOfClause() != null) && getAsOfClause().equals(expression.getAsOfClause())));
    }
    
    public void addDerivedField(Expression addThis) {
        if (derivedFields == null) {
            derivedFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(4);
        }
        derivedFields.addElement(addThis);
    }

    public void addDerivedTable(Expression addThis) {
        if (derivedTables == null) {
            derivedTables = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(4);
        }
        derivedTables.addElement(addThis);
    }

    public Expression asOf(AsOfClause clause) {
        asOfClause = clause;
        return this;
    }

    /**
     * INTERNAL:
     * Find the alias for a given table
     */
    public DatabaseTable aliasForTable(DatabaseTable table) {
        if (tableAliases == null) {
            if (getBaseExpression() == null) {
                return null;
            }
            return getBaseExpression().aliasForTable(table);
        }

        return tableAliases.keyAtValue(table);
    }

    /**
     * INTERNAL:
     * Alias a particular table within this node
     */
    protected void assignAlias(String name, DatabaseTable table) {
        if (!getBuilder().getSession().getProject().hasGenericHistorySupport()) {
            assignAlias(new DecoratedDatabaseTable(name, getAsOfClause()), table);
        } else {
            assignAlias(new DatabaseTable(name), table);
        }
    }

    /**
     * INTERNAL:
     * Alias a particular table within this node
     */
    protected void assignAlias(DatabaseTable alias, DatabaseTable table) {
        if (tableAliases == null) {
            tableAliases = new TableAliasLookup();
        }
        tableAliases.put(alias, table);
    }

    /**
     * INTERNAL:
     */
    public void clearAliases() {
        tableAliases = null;
    }

    public Vector copyCollection(Vector in, Map alreadyDone) {
        if (in == null) {
            return null;
        }
        Vector result = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(in.size());
        for (Enumeration e = in.elements(); e.hasMoreElements();) {
            Expression exp = (Expression)e.nextElement();
            result.addElement(exp.copiedVersionFrom(alreadyDone));
        }
        return result;
    }

    /**
     * INTERNAL:
     */
    public Expression existingDerivedField(DatabaseField field) {
        if (derivedFields == null) {
            return null;
        }
        for (Enumeration e = derivedFields.elements(); e.hasMoreElements();) {
            FieldExpression exp = (FieldExpression)e.nextElement();
            if (exp.getField().equals(field)) {
                return exp;
            }
        }
        return null;

    }

    /**
     * INTERNAL:
     */
    public Expression existingDerivedTable(DatabaseTable table) {
        if (derivedTables == null) {
            return null;
        }
        for (Enumeration e = derivedTables.elements(); e.hasMoreElements();) {
            TableExpression exp = (TableExpression)e.nextElement();
            if (exp.getTable().equals(table)) {
                return exp;
            }
        }
        return null;

    }

    /**
     * INTERNAL:
     * Return the field appropriately aliased
     */
    public DatabaseField getAliasedField() {
        return null;

    }

    public AsOfClause getAsOfClause() {
        return asOfClause;
    }

    public ClassDescriptor getDescriptor() {
        return null;
    }

    /**
     * INTERNAL:
     */
    public DatabaseField getField() {
        return null;
    }

    public Expression getField(String fieldName) {
        DatabaseField field = new DatabaseField(fieldName);
        return getField(field);

    }

    public Expression getField(DatabaseField field) {
        Expression existing = existingDerivedField(field);
        if (existing != null) {
            return existing;
        }
        return newDerivedField(field);

    }

    public DatabaseMapping getMapping() {
        if (getBaseExpression() == null) {
            return null;
        }
        ClassDescriptor aDescriptor = ((DataExpression)getBaseExpression()).getDescriptor();
        if (aDescriptor == null) {
            return null;
        }
        return aDescriptor.getObjectBuilder().getMappingForAttributeName(getName());
    }
    
    public QueryKey getQueryKeyOrNull() {
        return null;
    }

    public Expression getTable(String tableName) {
        DatabaseTable table = new DatabaseTable(tableName);
        return getTable(table);
    }

    public Expression getTable(DatabaseTable table) {
        Expression existing = existingDerivedTable(table);
        if (existing != null) {
            return existing;
        }
        return newDerivedTable(table);

    }

    /**
     * INTERNAL:
     * Return the aliases used.  For CR#2456 must never lazily initialize as also used for Expression identity.
     */
    public TableAliasLookup getTableAliases() {
        return tableAliases;

    }

    /**
     * INTERNAL:
     * Did the normalizer already add additional joins to the where clause due to
     * this query key representing a foreign reference mapping?
     * This insures that join criteria (for any query key expression) is not
     * added twice.
     * <p>
     * New meaning: DataExpressions are often iterated on multiple times during
     * normalize, but Function/Relation expressions only once.  Adding a has
     * been normalized flag improves performance and is required in some
     * applications, such as have temporal query criteria been added.
     */
    public boolean hasBeenNormalized() {
        return hasBeenNormalized;
    }

    public boolean hasAsOfClause() {
        return ((getAsOfClause() != null) && (getAsOfClause().getValue() != null));
    }

    public boolean hasBeenAliased() {
        return ((tableAliases != null) && (tableAliases.size() != 0));

    }

    protected boolean hasDerivedFields() {
        return derivedFields != null;
    }

    protected boolean hasDerivedTables() {
        return derivedTables != null;
    }

    /**
     * INTERNAL:
     */
    public boolean isAttribute() {
        return false;
    }

    public boolean isDataExpression() {
        return true;
    }

    /**
     * INTERNAL:
     * For iterating using an inner class
     */
    public void iterateOn(ExpressionIterator iterator) {
        super.iterateOn(iterator);
        if (baseExpression != null) {
            baseExpression.iterateOn(iterator);
        }
    }

    public Expression mappingCriteria() {
        return null;
    }

    /**
     * INTERNAL:
     */
    public Expression newDerivedField(DatabaseField field) {
        FieldExpression result = new FieldExpression(field, this);
        addDerivedField(result);
        return result;

    }

    /**
     * INTERNAL:
     */
    public Expression newDerivedTable(DatabaseTable table) {
        TableExpression result = new TableExpression(table);
        result.setBaseExpression(this);
        addDerivedTable(result);
        return result;

    }

    /**
     * INTERNAL:
     * Normalize the expression into a printable structure.
     * Any joins must be added to form a new root.
     */
    public Expression normalize(ExpressionNormalizer normalizer) {
        if (getBaseExpression() != null) {
            // First normalize the base.
            setBaseExpression(getBaseExpression().normalize(normalizer));
            if (getAsOfClause() == null) {
                asOf(getBaseExpression().getAsOfClause());
            }
        }

        return super.normalize(normalizer);
    }

    /**
     * INTERNAL:
     * Used for cloning.
     */
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        clearAliases();
        derivedFields = copyCollection(derivedFields, alreadyDone);
        derivedTables = copyCollection(derivedTables, alreadyDone);
    }

    /**
     * INTERNAL:
     * Print SQL onto the stream, using the ExpressionPrinter for context
     */
    public void printSQL(ExpressionSQLPrinter printer) {
        printer.printField(getAliasedField());
    }

    public void setHasBeenNormalized(boolean value) {
        hasBeenNormalized = value;
    }

    /**
     * INTERNAL:
     * For CR#2456, Table identity involves having two tables sharing the same
     * aliasing table.
     */
    public void setTableAliases(TableAliasLookup tableAliases) {
        if (this.tableAliases == null) {
            this.tableAliases = tableAliases;
        }
    }

    public String tableAliasesDescription() {
        if (tableAliases == null) {
            return "";
        }
        return tableAliases.toString();
    }

    /**
     * Print the base for debuggin purposes.
     */
    public void writeSubexpressionsTo(BufferedWriter writer, int indent) throws IOException {
        if (getBaseExpression() != null) {
            getBaseExpression().toString(writer, indent);
        }
    }
}
