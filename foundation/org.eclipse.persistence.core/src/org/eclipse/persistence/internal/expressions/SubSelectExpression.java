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

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.queries.*;

/**
 * This is used to support subselects.
 * The subselect represents a mostly independent (has own expression builder) query using a report query.
 * Subselects can be used for, in (single column), exists (empty or non-empty), comparisons (single value).
 */
public class SubSelectExpression extends BaseExpression {
    protected boolean hasBeenNormalized;
    
    protected ReportQuery subQuery;

    protected String attribute;
    protected Class returnType;
    protected Expression criteriaBase;

    public SubSelectExpression() {
        super();
        subQuery = new ReportQuery();
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
    @Override
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
    @Override
    public String descriptionOfNodeType() {
        return "SubSelect";
    }

    public ReportQuery getSubQuery() {
        initializeCountSubQuery();
        return subQuery;
    }

    /**
     * INTERNAL:
     * This method creates a report query that counts the number of values in baseExpression.anyOf(attribute)
     * 
     * For most queries, a ReportQuery will be created that does a simple count using an anonymous query.  In the case of
     * a DirectCollectionMapping, the ReportQuery will use the baseExpression to create a join to the table
     * containing the Direct fields and count based on that join.
     */
    protected void initializeCountSubQuery(){
        if (criteriaBase != null && (subQuery.getItems() == null || subQuery.getItems().isEmpty())){
            if (baseExpression.getSession() != null && ((ObjectExpression)baseExpression).getDescriptor() != null){
                Class sourceClass = ((ObjectExpression)baseExpression).getDescriptor().getJavaClass();
                ClassDescriptor descriptor = baseExpression.getSession().getDescriptor(sourceClass);
                if (descriptor != null){
                    DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
                    if (mapping != null && mapping.isDirectCollectionMapping()){
                        subQuery.setExpressionBuilder(baseExpression.getBuilder());
                        subQuery.setReferenceClass(sourceClass);
                        subQuery.addCount(attribute, subQuery.getExpressionBuilder().anyOf(attribute), returnType);
                        return;
                    }
                }
             }
            // Use an anonymous subquery that will get its reference class
            // set during SubSelectExpression.normalize.
             subQuery.addCount("COUNT", subQuery.getExpressionBuilder(), returnType);
             if (attribute != null){
                 subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().equal(criteriaBase.anyOf(attribute)));
             } else {
                 subQuery.setSelectionCriteria(subQuery.getExpressionBuilder().equal(criteriaBase));
             }
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isSubSelectExpression() {
        return true;
    }
    
    /**
     * INTERNAL:
     * For iterating using an inner class
     */
    @Override
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
    @Override
    public Expression normalize(ExpressionNormalizer normalizer) {
        if (this.hasBeenNormalized) {
            return this;
        }
        //has no effect but validateNode is here for consistency
        validateNode();
        // Defer normalization of this expression until later.
        normalizer.addSubSelectExpression(this);
        normalizer.getStatement().setRequiresAliases(true);
        return this;
    }

    /**
     * INTERNAL:
     * Normalize this expression now that the parent statement has been normalized.
     * For CR#4223
     */
    public Expression normalizeSubSelect(ExpressionNormalizer normalizer, Map clonedExpressions) {
        if (this.hasBeenNormalized) {
            return this;
        }
        this.hasBeenNormalized = true;
        normalizer.getStatement().setRequiresAliases(true);
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
                    ClassDescriptor descriptor = ((QueryKeyExpression)rightChild).getDescriptor();
                    // descriptor will be null here for query key expressions
                    if (descriptor ==null){
                        descriptor = ((ObjectExpression)((QueryKeyExpression)rightChild).getBaseExpression()).getDescriptor();
                    }
                    subQuery.setReferenceClass(descriptor.getJavaClass());
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
    @Override
    protected void postCopyIn(Map alreadyDone) {
        initializeCountSubQuery();
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
                    clonedQuery.setExpressionBuilder((ExpressionBuilder)clonedQuery.getExpressionBuilder().copiedVersionFrom(alreadyDone));
                }
            } else if (clonedQuery.getExpressionBuilder() != null) {
                // Must clone the expression builder.
                clonedQuery.setExpressionBuilder((ExpressionBuilder)clonedQuery.getExpressionBuilder().copiedVersionFrom(alreadyDone));
            }
            // Must also clone report items, group by, having, order by.
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
    @Override
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
    @Override
    public Expression rebuildOn(Expression newBase) {
        // TODO: This should be using rebuildOn not twist, but needs to pass the oldBase to rebuildOn
        // and only the oldBase should be replaced with the new base.
        // Further the twist, rebuildOn, and copy are doing essentially the same thing
        // and should be merged into a single method instead of having three methods doing the same thing 3 different ways
        // and all having various different bugs in them.
        SubSelectExpression subSelect = (SubSelectExpression) shallowClone();
        
        // Rebuild base expression
        subSelect.setBaseExpression(getBaseExpression().rebuildOn(newBase));
        
        // Clone report query
        ReportQuery reportQuery = (ReportQuery) getSubQuery().clone();
        
        // Twist report items
        List<ReportItem> newItems = new ArrayList<ReportItem>(getSubQuery().getItems().size());
        
        for (ReportItem item : reportQuery.getItems()) {
            newItems.add(new ReportItem(item.getName(), item.getAttributeExpression().twistedForBaseAndContext(newBase, getBuilder(), getBaseExpression())));
        }

        reportQuery.setItems(newItems);
        
        // Twist group by expressions
        if (reportQuery.hasGroupByExpressions()) {
            List<Expression> groupByExpressions = new ArrayList<Expression>(reportQuery.getGroupByExpressions().size());
            for (Expression groupByExpression : reportQuery.getGroupByExpressions()) {
                groupByExpressions.add(groupByExpression.twistedForBaseAndContext(newBase, getBuilder(), getBaseExpression()));
            }
            
            reportQuery.setGroupByExpressions(groupByExpressions);
        }
        
        // Twist order by expressions
        if (reportQuery.hasOrderByExpressions()) {
            List<Expression> orderByExpressions = new ArrayList<Expression>(reportQuery.getOrderByExpressions().size());
            for (Expression orderByExpression : reportQuery.getOrderByExpressions()) {
                orderByExpressions.add(orderByExpression.twistedForBaseAndContext(newBase, getBuilder(), getBaseExpression()));
            }
            
            reportQuery.setOrderByExpressions(orderByExpressions);
        }
        
        // Twist union expressions
        if (reportQuery.hasUnionExpressions()) {
            List<Expression> unionExpressions = new ArrayList<Expression>(reportQuery.getUnionExpressions().size());
            for (Expression unionExpression : reportQuery.getUnionExpressions()) {
                unionExpressions.add(unionExpression.twistedForBaseAndContext(newBase, getBuilder(), getBaseExpression()));
            }
            
            reportQuery.setUnionExpressions(unionExpressions);
        }
        
        // Twist selection criteria
        if (reportQuery.getSelectionCriteria() != null) {
            reportQuery.setSelectionCriteria(reportQuery.getSelectionCriteria().twistedForBaseAndContext(newBase, getBuilder(), getBaseExpression()));
        }
        
        // Set the cloned report query
        subSelect.setSubQuery(reportQuery);
        
        return subSelect;
    }

    /**
     * INTERNAL:
     * Search the tree for any expressions (like SubSelectExpressions) that have been
     * built using a builder that is not attached to the query.  This happens in case of an Exists
     * call using a new ExpressionBuilder().  This builder needs to be replaced with one from the query.
     */
    @Override
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
    
    @Override
    public Expression twistedForBaseAndContext(Expression newBase, Expression context, Expression oldBase) {
        SubSelectExpression subSelect = (SubSelectExpression) shallowClone();
        
        // Twist base expression
        subSelect.setBaseExpression(subSelect.getBaseExpression().twistedForBaseAndContext(newBase, context, oldBase));
        
        // Clone report query
        ReportQuery reportQuery = (ReportQuery) getSubQuery().clone();
        
        // Twist report items
        List<ReportItem> newItems = new ArrayList<ReportItem>(getSubQuery().getItems().size());
        
        for (ReportItem item : getSubQuery().getItems()) {
            newItems.add(new ReportItem(item.getName(), item.getAttributeExpression().twistedForBaseAndContext(newBase, context, getBaseExpression())));
        }

        reportQuery.setItems(newItems);
        
        // Twist group by expressions
        if (getSubQuery().hasGroupByExpressions()) {
            List<Expression> groupByExpressions = new ArrayList<Expression>(getSubQuery().getGroupByExpressions().size());
            for (Expression groupByExpression : getSubQuery().getGroupByExpressions()) {
                groupByExpressions.add(groupByExpression.twistedForBaseAndContext(newBase, context, getBaseExpression()));
            }
            
            reportQuery.setGroupByExpressions(groupByExpressions);
        }
        
        // Twist order by expressions
        if (getSubQuery().hasOrderByExpressions()) {
            List<Expression> orderByExpressions = new ArrayList<Expression>(getSubQuery().getOrderByExpressions().size());
            for (Expression orderByExpression : getSubQuery().getOrderByExpressions()) {
                orderByExpressions.add(orderByExpression.twistedForBaseAndContext(newBase, context, getBaseExpression()));
            }
            
            reportQuery.setOrderByExpressions(orderByExpressions);
        }
        
        // Twist union expressions
        if (getSubQuery().hasUnionExpressions()) {
            List<Expression> unionByExpressions = new ArrayList<Expression>(getSubQuery().getUnionExpressions().size());
            for (Expression unionExpression : getSubQuery().getUnionExpressions()) {
                unionByExpressions.add(unionExpression.twistedForBaseAndContext(newBase, context, getBaseExpression()));
            }
            
            reportQuery.setUnionExpressions(unionByExpressions);
        }
        
        // Twist selection criteria
        if (getSubQuery().getSelectionCriteria() != null) {
            reportQuery.setSelectionCriteria(getSubQuery().getSelectionCriteria().twistedForBaseAndContext(newBase, context, getBaseExpression()));
        }
        
        // Set the clones report query
        subSelect.setSubQuery(reportQuery);
        
        return subSelect;
    }
    
    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    @Override
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write(String.valueOf(getSubQuery()));
    }

    /**
     * INTERNAL:
     * Used in SQL printing.
     */
    @Override
    public void writeSubexpressionsTo(BufferedWriter writer, int indent) throws IOException {        
        if (getSubQuery().getSelectionCriteria() != null) {
            getSubQuery().getSelectionCriteria().toString(writer, indent);
        }
    }

    /**
     * INTERNAL: called from SQLSelectStatement.writeFieldsFromExpression(...)
     * This allows a sub query in the select clause.
     */
    @Override
    public void writeFields(ExpressionSQLPrinter printer, Vector newFields, SQLSelectStatement statement) {
        //print ", " before each selected field except the first one
        if (printer.isFirstElementPrinted()) {
            printer.printString(", ");
        } else {
            printer.setIsFirstElementPrinted(true);
        }

        // This field is complex so any name can be used.
        DatabaseField field = new DatabaseField("*");
        field.setSqlType(DatabaseField.NULL_SQL_TYPE);
        newFields.add(field);

        printSQL(printer);
    }
    
    /**
     * INTERNAL:
     * This factory method is used to build a subselect that will do a count.
     * It will count the number of items in baseExpression.anyOf(attribute).
     */
    public static SubSelectExpression createSubSelectExpressionForCount(Expression outerQueryBaseExpression, Expression outerQueryCriteria, String attribute, Class returnType){
        SubSelectExpression expression = new SubSelectExpression();
        expression.setBaseExpression(outerQueryBaseExpression);
        expression.attribute = attribute;
        expression.criteriaBase = outerQueryCriteria;
        if (returnType != null){
            expression.returnType = returnType;
        }
        return expression;
    }
}
