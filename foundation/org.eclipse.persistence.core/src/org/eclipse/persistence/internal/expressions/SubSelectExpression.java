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

import java.io.*;
import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.queries.*;

/**
 * This is used to support subselects.
 * The subselect represents a mostly independent (has own expression builder) query using a report query.
 * Subselects can be used for, in (single column), exists (empty or non-empty), comparisons (single value).
 */
public class SubSelectExpression extends BaseExpression {
    protected ReportQuery subQuery;

    public SubSelectExpression() {
        super();
    }

    public SubSelectExpression(ReportQuery query, Expression baseExpression) {
        super(baseExpression);
        this.subQuery = query;
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
        // Equality cannot easily be determined for sub-select expressions.
        return false;
    }
    
    /**
     * INTERNAL:
     * Used in debug printing of this node.
     */
    public String descriptionOfNodeType() {
        return "SubSelect";
    }

    public ReportQuery getSubQuery() {
        return subQuery;
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

        // For Flashback: It is now possible to create iterators that will span
        // the entire expression, even the where clause embedded in a subQuery.
        if (iterator.shouldIterateOverSubSelects()) {
            if (getSubQuery().getSelectionCriteria() != null) {
                getSubQuery().getSelectionCriteria().iterateOn(iterator);
            } else {
                getSubQuery().getExpressionBuilder().iterateOn(iterator);
            }
        }
    }

    /**
     * INTERNAL:
     * The subquery must be normalized with the knowledge of the outer statement for outer references and correct aliasing.
     * For CR#4223 it will now be normalized after the outer statement is, rather than
     * somewhere in the middle of the outer statement's normalize.
     */
    public Expression normalize(ExpressionNormalizer normalizer) {
        //has no effect but validateNode is here for consistency
        validateNode();
        // Defer normalization of this expression until later.
        normalizer.addSubSelectExpression(this);
        normalizer.getStatement().setRequiresAliases(true);
        return this;
    }

    /**
     * INTERNAL:
     * Normalize this expression now that the parent statment has been normalized.
     * For CR#4223
     */
    public Expression normalizeSubSelect(ExpressionNormalizer normalizer, Map clonedExpressions) {
        // Anonymous subqueries: The following is to support sub-queries created
        // on the fly by OSQL Expressions isEmpty(), isNotEmpty(), size().
        if (!getSubQuery().isCallQuery() && (getSubQuery().getReferenceClass() == null)) {
            ReportQuery subQuery = getSubQuery();
            Expression criteria = subQuery.getSelectionCriteria();

            // The criteria should be of form builder.equal(exp), where exp belongs
            // to the parent statement and has already been normalized, hence it
            // knows its reference class.
            if (criteria instanceof LogicalExpression) {
                criteria = ((LogicalExpression)criteria).getFirstChild();
            }
            if (criteria instanceof RelationExpression) {
                Expression rightChild = ((RelationExpression)criteria).getSecondChild();
                if (rightChild instanceof QueryKeyExpression) {
                    subQuery.setReferenceClass(((QueryKeyExpression)rightChild).getDescriptor().getJavaClass());
                }
            }
        }

        //has no effect but validateNode is here for consistency
        validateNode();
        getSubQuery().prepareSubSelect(normalizer.getSession(), null, clonedExpressions);
        if (!getSubQuery().isCallQuery()) {
            SQLSelectStatement statement = (SQLSelectStatement)((StatementQueryMechanism)getSubQuery().getQueryMechanism()).getSQLStatement();

            // setRequiresAliases was already set for parent statement.
            statement.setRequiresAliases(true);
            statement.setParentStatement(normalizer.getStatement());
            statement.normalize(normalizer.getSession(), getSubQuery().getDescriptor(), clonedExpressions);
        }
        return this;
    }

    /**
     * The query must be cloned, and the sub-expression must be cloned using the same outer expression identity.
     */
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        ReportQuery clonedQuery = (ReportQuery)getSubQuery().clone();
        if (!clonedQuery.isCallQuery()) {
            if (clonedQuery.getSelectionCriteria() != null) {
                clonedQuery.setSelectionCriteria(getSubQuery().getSelectionCriteria().copiedVersionFrom(alreadyDone));
                // ensure the builder for the subquery is the same as the builder for the subquery's expression
                // for certain Subqueries (for instance batch queries for direct collections), when we get to this
                // point the builder for the clonedQuery will already be aliased.  Replacing the builder with
                // the builder for the query's new expression solves this issue.
                if (clonedQuery.getExpressionBuilder() != null) {
                    clonedQuery.setExpressionBuilder(clonedQuery.getSelectionCriteria().getBuilder());
                }
            } else if (clonedQuery.getExpressionBuilder() != null) {
                // Must clone the expression builder.
                clonedQuery.setExpressionBuilder((ExpressionBuilder)clonedQuery.getExpressionBuilder().copiedVersionFrom(alreadyDone));
            }
            // Must also clone report items.
            clonedQuery.copyReportItems(alreadyDone);
        }
        setSubQuery(clonedQuery);
    }

    /**
     * Print the sub query to the printer.
     */
    protected void printCustomSQL(ExpressionSQLPrinter printer) {
        /*
         * modified for bug#2658466.  This fix ensures that Custom SQL sub-queries are translated
         * and have variables substituted with values correctly.
         */
        SQLCall call = (SQLCall)getSubQuery().getCall();
        call.translateCustomQuery();
        printer.getCall().getParameters().addAll(call.getParameters());
        printer.getCall().getParameterTypes().addAll(call.getParameterTypes());
        printer.printString(call.getCallString());
    }

    /**
     * Print the sub query to the printer.
     */
    public void printSQL(ExpressionSQLPrinter printer) {
        ReportQuery query = getSubQuery();
        printer.printString("(");
        if (query.isCallQuery()) {
            printCustomSQL(printer);
        } else {
            SQLSelectStatement statement = (SQLSelectStatement)((ExpressionQueryMechanism)query.getQueryMechanism()).getSQLStatement();
            boolean isFirstElementPrinted = printer.isFirstElementPrinted();
            printer.setIsFirstElementPrinted(false);
            boolean requiresDistinct = printer.requiresDistinct();
            statement.printSQL(printer);
            printer.setIsFirstElementPrinted(isFirstElementPrinted);
            printer.setRequiresDistinct(requiresDistinct);
        }
        printer.printString(")");
    }

    /**
     * Should not rebuild as has its on expression builder.
     */
    public Expression rebuildOn(Expression newBase) {
        return this;
    }

    /**
     * INTERNAL:
     * Search the tree for any expressions (like SubSelectExpressions) that have been
     * built using a builder that is not attached to the query.  This happens in case of an Exists
     * call using a new ExpressionBuilder().  This builder needs to be replaced with one from the query.
     */
    public void resetPlaceHolderBuilder(ExpressionBuilder queryBuilder){
        if(this.baseExpression.isExpressionBuilder() && ((ExpressionBuilder)this.baseExpression).wasQueryClassSetInternally()){
            this.baseExpression = queryBuilder;
            if (this.builder != null){
                this.builder = queryBuilder;
            }
        }else{
            this.baseExpression.resetPlaceHolderBuilder(queryBuilder);
        }
    }

    public void setSubQuery(ReportQuery subQuery) {
        this.subQuery = subQuery;
    }

    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write(String.valueOf(getSubQuery()));
    }

    /**
     * INTERNAL:
     * Used in SQL printing.
     */
    public void writeSubexpressionsTo(BufferedWriter writer, int indent) throws IOException {
        if (getSubQuery().getSelectionCriteria() != null) {
            getSubQuery().getSelectionCriteria().toString(writer, indent);
        }
    }
}
