/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.history.AsOfClause;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.history.DecoratedDatabaseTable;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.querykeys.QueryKey;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Superclass for all expression that have a context.
 * i.e. a base expression.
 */
public abstract class DataExpression extends BaseExpression {
    protected List<Expression> derivedTables;
    protected List<Expression> derivedFields;
    protected boolean hasBeenNormalized;
    protected TableAliasLookup tableAliases;
    protected AsOfClause asOfClause;

    /**
     * DataExpression constructor comment.
     */
    protected DataExpression() {
        super();
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
        DataExpression expression = (DataExpression) object;
        return ((this.baseExpression == expression.getBaseExpression()) || ((this.baseExpression != null) && this.baseExpression.equals(expression.getBaseExpression())))
            && ((getAsOfClause() == expression.getAsOfClause()) || ((getAsOfClause() != null) && getAsOfClause().equals(expression.getAsOfClause())));
    }

    public void addDerivedField(Expression addThis) {
        if (derivedFields == null) {
            derivedFields = new ArrayList<>();
        }
        derivedFields.add(addThis);
    }

    public void addDerivedTable(Expression addThis) {
        if (derivedTables == null) {
            derivedTables = new ArrayList<>();
        }
        derivedTables.add(addThis);
    }

    @Override
    public Expression asOf(AsOfClause clause) {
        asOfClause = clause;
        return this;
    }

    /**
     * INTERNAL:
     * Find the alias for a given table
     */
    @Override
    public DatabaseTable aliasForTable(DatabaseTable table) {
        if (tableAliases == null) {
            if (this.baseExpression == null) {
                return null;
            }
            return this.baseExpression.aliasForTable(table);
        }

        return tableAliases.keyAtValue(table);
    }

    /**
     * INTERNAL:
     * Alias a particular table within this node
     */
    @Override
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

    public List<Expression> copyCollection(List<Expression> in, Map<Expression, Expression> alreadyDone) {
        if (in == null) {
            return null;
        }
        List<Expression> result = new ArrayList<>(in.size());
        for (Expression exp : in) {
            result.add(exp.copiedVersionFrom(alreadyDone));
        }
        return result;
    }

    /**
     * INTERNAL:
     */
    public Expression existingDerivedField(DatabaseField field) {
        if (this.derivedFields == null) {
            return null;
        }
        for (Expression exp : this.derivedFields) {
            if (((FieldExpression)exp).getField().equals(field)) {
                return exp;
            }
        }
        return null;

    }

    /**
     * INTERNAL:
     */
    public Expression existingDerivedTable(DatabaseTable table) {
        if (this.derivedTables == null) {
            return null;
        }
        for (Expression exp : this.derivedTables) {
            if (((TableExpression)exp).getTable().equals(table)) {
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

    @Override
    public AsOfClause getAsOfClause() {
        return asOfClause;
    }

    /**
     * INTERNAL:
     * Get persistence class descriptor of this expression.
     *
     * @return the persistence class descriptor
     */
    public ClassDescriptor getDescriptor() {
        return null;
    }

    /**
     * INTERNAL:
     */
    public DatabaseField getField() {
        return null;
    }

    @Override
    public Expression getField(String fieldName) {
        DatabaseField field = new DatabaseField(fieldName);
        return getField(field);
    }

    @Override
    public Expression getField(DatabaseField field) {
        Expression existing = existingDerivedField(field);
        if (existing != null) {
            return existing;
        }
        return newDerivedField(field);
    }

    /**
     * INTERNAL:
     * Return the descriptor which contains this query key.
     */
    public ClassDescriptor getContainingDescriptor() {
        return ((DataExpression)this.baseExpression).getDescriptor();
    }

    /**
     * INTERNAL:
     * Get {@link DatabaseMapping} for this {@link Expression}.
     * Aggregate mapping will not be resolved.
     *
     * @return the mapping for current expression or {@code null} when no mapping was found
     */
    public DatabaseMapping getMapping() {
        return getMapping(false);
    }

    // Package local only, should not be exposed as API.
    /**
     * Get {@link DatabaseMapping} for this {@link Expression}.
     *
     * @param resolveAggregate trigger resolution of aggregate mapping
     * @return the mapping for current expression or {@code null} when no mapping was found
     */
    DatabaseMapping getMapping(boolean resolveAggregate) {
        if (this.baseExpression == null) {
            return null;
        }
        ClassDescriptor descriptor = getContainingDescriptor();
        if (descriptor == null) {
            return null;
        }
        DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(getName());

        // Retrieve aggregate mapping when requested
        if (mapping == null && resolveAggregate && baseExpression.isObjectExpression()
                && ((ObjectExpression)baseExpression).derivedExpressions != null) {
            mapping = getAggregateMapping(getName());
        }

        return mapping;
    }

    // Retrieve the aggregate mapping.
    private DatabaseMapping getAggregateMapping(String name) {
        List<Expression> derivedExpressions = ((ObjectExpression)baseExpression).derivedExpressions;
        if (derivedExpressions.size() > 1) {
            Expression derivedExpression = derivedExpressions.get(derivedExpressions.size() - 2);
            if (derivedExpression.isObjectExpression()) {
                ObjectExpression objectExpression = (ObjectExpression) derivedExpression;
                DatabaseMapping parentMapping = null;
                if (objectExpression.baseExpression != null) {
                    ClassDescriptor parentDescriptor = ((DataExpression) this.baseExpression).getDescriptor();
                    parentMapping = parentDescriptor.getObjectBuilder().getMappingForAttributeName(objectExpression.getName());
                }
                if (parentMapping != null && parentMapping.isAggregateMapping()) {
                    ClassDescriptor parentDescriptor;
                    parentDescriptor = objectExpression.getDescriptor();
                    if (parentDescriptor != null && parentDescriptor.isAggregateDescriptor()) {
                        return parentDescriptor.getObjectBuilder().getMappingForAttributeName(name);
                    }
                }
            }
        }
        return null;
    }

    public QueryKey getQueryKeyOrNull() {
        return null;
    }

    @Override
    public Expression getTable(String tableName) {
        DatabaseTable table = new DatabaseTable(tableName);
        return getTable(table);
    }

    @Override
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
    @Override
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

    @Override
    public boolean hasAsOfClause() {
        return ((getAsOfClause() != null) && (getAsOfClause().getValue() != null));
    }

    @Override
    public boolean hasBeenAliased() {
        return ((tableAliases != null) && (!tableAliases.isEmpty()));

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

    // Just placeholder for overriding method in QueryKeyExpression
    boolean isAttribute(boolean resolveAggregate) {
        return false;
    }

    @Override
    public boolean isDataExpression() {
        return true;
    }

    /**
     * INTERNAL:
     * For iterating using an inner class
     */
    @Override
    public void iterateOn(ExpressionIterator<?> iterator) {
        super.iterateOn(iterator);
        if (baseExpression != null) {
            baseExpression.iterateOn(iterator);
        }
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
     * ADVANCED: Return an expression representing a sub-select in the from clause.
     * <p> Example:
     * <pre>
     *  builder.getAlias(builder.subQuery(reportQuery)).get("type").equal("S");
     * </pre>
     */
    @Override
    public Expression getAlias(Expression subSelect) {
        TableExpression result = new FromSubSelectExpression((SubSelectExpression)subSelect);
        result.setBaseExpression(this);
        return result;
    }

    /**
     * INTERNAL:
     * Normalize the expression into a printable structure.
     * Any joins must be added to form a new root.
     */
    @Override
    public Expression normalize(ExpressionNormalizer normalizer) {
        if (this.hasBeenNormalized) {
            return this;
        }
        this.hasBeenNormalized = true;
        if (this.baseExpression != null) {
            // First normalize the base.
            setBaseExpression(this.baseExpression.normalize(normalizer));
            if (getAsOfClause() == null) {
                asOf(this.baseExpression.getAsOfClause());
            }
        }

        return super.normalize(normalizer);
    }

    /**
     * INTERNAL:
     * Used for cloning.
     */
    @Override
    protected void postCopyIn(Map<Expression, Expression> alreadyDone) {
        super.postCopyIn(alreadyDone);
        clearAliases();
        this.derivedFields = copyCollection(this.derivedFields, alreadyDone);
        this.derivedTables = copyCollection(this.derivedTables, alreadyDone);
    }

    /**
     * INTERNAL:
     * Print SQL onto the stream, using the ExpressionPrinter for context
     */
    @Override
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
    @Override
    public void writeSubexpressionsTo(BufferedWriter writer, int indent) throws IOException {
        if (this.baseExpression != null) {
            this.baseExpression.toString(writer, indent);
        }
    }
}
