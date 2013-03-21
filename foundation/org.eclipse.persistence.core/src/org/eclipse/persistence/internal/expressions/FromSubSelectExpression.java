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
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.queries.ReportItem;

/**
 * Allow a table expression to be created on a sub-select to define a sub-select in the from clause.
 */
public class FromSubSelectExpression extends TableExpression {
    /**  Allows a sub-select to be defined from clause. */
    protected SubSelectExpression subSelect;

    public FromSubSelectExpression() {
        super();
    }

    public FromSubSelectExpression(SubSelectExpression subSelect) {
        super();
        this.subSelect = subSelect;
        this.table = new SubSelectDatabaseTable(subSelect);
    }
    
    /**
     * INTERNAL:
     * Return if the expression is equal to the other.
     * This is used to allow dynamic expression's SQL to be cached.
     * From sub-selects are too complex to cache, so use identity.
     */
    @Override
    public boolean equals(Object object) {
        return this == object;
    }
        
    /**
     * INTERNAL:
     * Compute a consistent hash-code for the expression.
     */
    @Override
    public int computeHashCode() {
        return System.identityHashCode(this);
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    @Override
    public String descriptionOfNodeType() {
        return "FromSubSelect";
    }
    
    /**
     * This is used by sub-selects in the from clause to define a virtual table,
     * 'get' allows one of the sub-selected attributes to be aliased without using the field name.
     */
    public Expression get(String alias) {
        FromAliasExpression aliasExpression = new FromAliasExpression(alias, this);
        return aliasExpression;
    }

    /**
     * INTERNAL:
     * Normalize the expression into a printable structure.
     * Any joins must be added to form a new root.
     */
    @Override
    public Expression normalize(ExpressionNormalizer normalizer) {
        if (this.subSelect != null) {
            // Each item that is a function must have an alias defined for it.
            for (ReportItem item : this.subSelect.getSubQuery().getItems()) {
                if (!item.getAttributeExpression().isQueryKeyExpression()) {
                    item.setAttributeExpression(item.getAttributeExpression().as(item.getName()));
                }
            }
            // Need to force the sub-slect to normalize instead of deferring.
            this.subSelect.normalizeSubSelect(normalizer, normalizer.getClonedExpressions());
        }
        return super.normalize(normalizer);
    }
    
    /**
     * INTERNAL:
     * Also iterate over the sub-select if present.
     */
    @Override
    public void iterateOn(ExpressionIterator iterator) {
        super.iterateOn(iterator);
        if (this.subSelect != null) {
            this.subSelect.iterateOn(iterator);
        }
    }
    
    /**
     * INTERNAL:
     * Also copy over the sub-select if present.
     */
    @Override
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        if (this.subSelect != null) {
            this.subSelect = (SubSelectExpression)this.subSelect.copiedVersionFrom(alreadyDone);
            this.table = new SubSelectDatabaseTable(subSelect);
        }
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    @Override
    public Expression rebuildOn(Expression newBase) {
        Expression newLocalBase = getBaseExpression().rebuildOn(newBase);
        return newLocalBase.getAlias(this.subSelect.rebuildOn(newBase));
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
        return twistedBase.getAlias(this.subSelect.twistedForBaseAndContext(newBase, context, oldBase));
    }

    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    @Override
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        this.subSelect.writeDescriptionOn(writer);
        writer.write(tableAliasesDescription());
    }

    /**
     * INTERNAL:
     * Any table should use the sub-selects alias.
     */
    @Override
    public DatabaseTable aliasForTable(DatabaseTable table) {
        return super.aliasForTable(getTable());        
    }


    public SubSelectExpression getSubSelect() {
        return subSelect;
    }

    public void setSubSelect(SubSelectExpression subSelect) {
        this.subSelect = subSelect;
        this.table = new SubSelectDatabaseTable(subSelect);
    }
}
