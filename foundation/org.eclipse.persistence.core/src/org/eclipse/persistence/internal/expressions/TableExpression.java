/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.expressions.*;

public class TableExpression extends DataExpression {
    protected DatabaseTable table;

    /**
     * TableExpression constructor comment.
     */
    public TableExpression() {
        super();
    }

    /**
     * TableExpression constructor comment.
     */
    public TableExpression(DatabaseTable aTable) {
        super();
        table = aTable;
    }
    
    /**
     * INTERNAL:
     * Alias a particular table within this node
     */
    protected void assignAlias(DatabaseTable alias, DatabaseTable table) {
        if (baseExpression.isQueryKeyExpression()){
            QueryKeyExpression qkExpression = ((QueryKeyExpression)baseExpression);
            if (qkExpression.getTableAliases() != null && qkExpression.getTableAliases().keyAtValue(table) != null ){
                return;
            }
        }
        super.assignAlias(alias, table);
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
        TableExpression expression = (TableExpression) object;
        return ((getTable() == expression.getTable()) || ((getTable() != null) && getTable().equals(expression.getTable())));
    }
        
    /**
     * INTERNAL:
     * Compute a consistent hash-code for the expression.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    public int computeHashCode() {
        int hashCode = super.computeHashCode();
        if (getTable() != null) {
            hashCode = hashCode + getTable().hashCode();
        }
        return hashCode;
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    public String descriptionOfNodeType() {
        return "Table";
    }

    /**
     * INTERNAL:
     * Fully-qualify the databaseField if the table is known.
     * CR 3791
     */
    public Expression getField(String fieldName) {
        // we need to check for full table qualification
        DatabaseField field = new DatabaseField(fieldName);
        if (!field.hasTableName()) {
            field.setTable(getTable());
        }
        return getField(field);
    }

    /**
     * INTERNAL:
     */
    public Vector getOwnedTables() {
        Vector result = new Vector(1);
        result.addElement(getTable());
        return result;
    }

    public DatabaseTable getTable() {
        return table;
    }

    public boolean isTableExpression() {
        return true;
    }

    /**
     * INTERNAL:
     * Normalize the expression into a printable structure.
     * Any joins must be added to form a new root.
     */
    public Expression normalize(ExpressionNormalizer normalizer) {
		//Bug4736461  Only setTableQualifier if getDatasourceLogin().getTableQualifier() is an empty string to make the window much smaller when
		//DatabaseTable.qualifiedName is reset
        if (getTable().getTableQualifier().length() == 0 && (normalizer.getSession().getDatasourceLogin().getTableQualifier().length() != 0)) {
            getTable().setTableQualifier(normalizer.getSession().getDatasourceLogin().getTableQualifier());
        }
        return super.normalize(normalizer);
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    public Expression rebuildOn(Expression newBase) {
        Expression newLocalBase = getBaseExpression().rebuildOn(newBase);
        return newLocalBase.getTable(getTable());

    }

    /**
     * INTERNAL:
     * Added for temporal querying.
     */
    public void setTable(DatabaseTable table) {
        this.table = table;
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
        Expression twistedBase = getBaseExpression().twistedForBaseAndContext(newBase, context, oldBase);
        return twistedBase.getTable(getTable());

    }

    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write(getTable().toString());
        writer.write(tableAliasesDescription());
    }
}
