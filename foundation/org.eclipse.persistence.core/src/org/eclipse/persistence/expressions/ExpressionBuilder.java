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
package org.eclipse.persistence.expressions;

import java.util.*;
import java.io.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadQuery;

/**
 * <P>
 * <B>Purpose</B>: Allow for instances of expression to be created. Expressions are Java object-level representations of SQL "where" clauses.
 * The expressions attempt to mirror Java code as closely as possible.</p>
 *
 * <P>
 *
 * <B>Example</B>:
 * <PRE><BLOCKQUOTE>
 *        ExpressionBuilder employee = new ExpressionBuilder();
 *        employee.get("firstName").equal("Bob").and(employee.get("lastName").equal("Smith"))
 *
 *        >> equivalent Java code: (employee.getFirstName().equals("Bob")) && (employee.getLastName().equals("Smith"))
 *
 *        >> equivalent SQL: (F_NAME = 'Bob') AND (L_NAME = 'Smith')
 * </BLOCKQUOTE></PRE>
 *
 * @see Expression
 */
public class ExpressionBuilder extends ObjectExpression {
    protected transient AbstractSession session;
    protected Class queryClass;
    protected SQLSelectStatement statement;
    protected DatabaseTable viewTable;
    protected DatabaseTable aliasedViewTable;

    protected boolean wasQueryClassSetInternally = true;
    
    protected boolean wasAdditionJoinCriteriaUsed = false;

    /**
     * PUBLIC:
     * Create a new ExpressionBuilder.
     */
    public ExpressionBuilder() {
        super();
    }

    /**
     * ADVANCED:
     * Create a new ExpressionBuilder representing instances of the argument class.
     * This can be used for the purpose of parallel expressions.
     * This is a type of query that searches on the relationship between to un-related objects.
     */
    public ExpressionBuilder(Class queryClass) {
        super();
        this.queryClass = queryClass;
        this.wasQueryClassSetInternally = false;
    }

    /**
     * INTERNAL:
     * Return if the expression is equal to the other.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    public boolean equals(Object expression) {
        if (this == expression) {
            return true;
        }
        // Return false for parallel expressions, as equality is unknown.
        return super.equals(expression) && ((getQueryClass() == null) && ((ExpressionBuilder)expression).getQueryClass() == null);
    }
    
    /**
     * INTERNAL: Find the alias for a given table. Handle the special case where we are bogus
     * and it should be aliased against our derived tables instead.
     */
    public DatabaseTable aliasForTable(DatabaseTable table) {
        if (hasViewTable()) {
            return getAliasedViewTable();
        }

        if (doesNotRepresentAnObjectInTheQuery()) {
            for (Expression expression : this.derivedTables) {
                DatabaseTable result = expression.aliasForTable(table);
                if (result != null) {
                    return result;
                }
            }
        } else {
            return super.aliasForTable(table);
        }
        return null; // No alias found in the derived tables
    }

    /**
     * INTERNAL:
     * Assign aliases to any tables which I own. Start with t(initialValue),
     * and return the new value of  the counter , i.e. if initialValue is one
     * and I have tables ADDRESS and EMPLOYEE I will assign them t1 and t2 respectively, and return 3.
     */
    public int assignTableAliasesStartingAt(int initialValue) {
        if (hasBeenAliased()) {
            return initialValue;
        }

        if (doesNotRepresentAnObjectInTheQuery()) {
            return initialValue;
        }

        // This block should be removed I think.
        // The only reason to clone might be to
        // preserve the qualifier, but aliases need
        // qualifiers?  That seems strange.
        // Also this will break AsOf queries.  By
        // inference if has view table the AliasTableLookup
        // will contain one table, and that will be the
        // table of the view...
        if (hasViewTable()) {
            DatabaseTable aliased = viewTable.clone();
            String alias = "t" + initialValue;
            aliased.setName(alias);
            assignAlias(alias, viewTable);
            aliasedViewTable = aliased;
            return initialValue + 1;
        }
        return super.assignTableAliasesStartingAt(initialValue);
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    public String descriptionOfNodeType() {
        return "Base";
    }

    /**
     * INTERNAL:
     * There are cases (which we might want to eliminate?) where the expression builder
     * doesn't actually correspond to an object to be read. Mostly this is the case where
     * it's a data query in terms of tables, and the builder is only there to provide a base.
     * It might be better to make tables able to serve as their own base, but it's very nice
     * to have a known unique, shared base. In the meantime, this
     * is a special case to make sure the builder doesn't get tables assigned.
     */
    public boolean doesNotRepresentAnObjectInTheQuery() {
        return (hasDerivedTables() && !hasDerivedFields() && !hasDerivedExpressions());
    }

    /**
     * INTERNAL:
     */
    public DatabaseTable getAliasedViewTable() {
        return aliasedViewTable;

    }

    /**
     * INTERNAL:
     * Return the expression builder which is the ultimate base of this expression, or
     * null if there isn't one (shouldn't happen if we start from a root)
     */
    public ExpressionBuilder getBuilder() {
        return this;
    }

    /**
     * INTERNAL:
     * Only usable after the session and class have been set. Return the
     * descriptor for the class this node represents.
     */
    public ClassDescriptor getDescriptor() {
        if (descriptor == null) {
            if (getQueryClass() == null) {
                return null;
            } else {
                if (getSession() == null) {
                    throw QueryException.noExpressionBuilderFound(this);
                }
                descriptor = getSession().getDescriptor(getQueryClass());
                descriptor = convertToCastDescriptor(descriptor, getSession());
            }
        }
        return descriptor;

    }

    /**
     * INTERNAL:
     */
    public Class getQueryClass() {
        return queryClass;
    }

    /**
     * INTERNAL:
     */
    public AbstractSession getSession() {
        return session;
    }

    /**
     * INTERNAL:
     * Return the statement that expression is for.
     * This is used for the context in subselects.
     */
    public SQLSelectStatement getStatement() {
        return statement;
    }

    /**
     * INTERNAL:
     */
    public DatabaseTable getViewTable() {
        return viewTable;
    }

    /**
     * INTERNAL:
     */
    public boolean hasViewTable() {
        return viewTable != null;
    }

    /**
     * INTERNAL:
     */
    public boolean isExpressionBuilder() {
        return true;
    }

    /**
     * INTERNAL:
     * Normalize the expression into a printable structure.
     * Any joins must be added to form a new root.
     */
    @Override
    public Expression normalize(ExpressionNormalizer normalizer) {
        if (hasBeenNormalized()) {
            return this;
        } else {
            setHasBeenNormalized(true);
        }
        
        // Normalize the ON clause if present.  Need to use rebuild, not twist as parameters are real parameters.
        if (this.onClause != null) {
            this.onClause = this.onClause.normalize(normalizer);
            if (shouldUseOuterJoin() || (!getSession().getPlatform().shouldPrintInnerJoinInWhereClause())) {
                normalizer.getStatement().getOuterJoinExpressions().add(this);
                normalizer.getStatement().getOuterJoinedMappingCriteria().add(null);
                normalizer.getStatement().getOuterJoinedAdditionalJoinCriteria().add(null);
                normalizer.getStatement().getDescriptorsForMultitableInheritanceOnly().add(null);
                if ((getDescriptor() != null) && (getDescriptor().getHistoryPolicy() != null)) {
                    Expression historyCriteria = getDescriptor().getHistoryPolicy().additionalHistoryExpression(this, this);
                    if (historyCriteria != null) {
                        normalizer.addAdditionalExpression(historyCriteria);
                    }
                }
            } else {
                normalizer.addAdditionalExpression(this.onClause);                
            }
        }

        // This is required for parallel selects,
        // the session must be set and the additional join expression added.
        if (this.queryClass != null) {
            Expression criteria = null;

            setSession(normalizer.getSession().getRootSession(null));
            // The descriptor must be defined at this point.
            if (getDescriptor() == null) {
                throw QueryException.noExpressionBuilderFound(this);
            }
            if (!this.wasAdditionJoinCriteriaUsed) {
                criteria = getDescriptor().getQueryManager().getAdditionalJoinExpression();
                if (criteria != null) {
                    criteria = twist(criteria, this);
                }
            }

            if (isUsingOuterJoinForMultitableInheritance() && getSession().getPlatform().shouldPrintOuterJoinInWhereClause()) {
                Expression childrenCriteria = getDescriptor().getInheritancePolicy().getChildrenJoinExpression();
                childrenCriteria = twist(childrenCriteria, this);
                childrenCriteria.convertToUseOuterJoin();
                if(criteria == null) {
                    criteria = childrenCriteria;
                } else {
                    criteria = criteria.and(childrenCriteria);
                }
            }
            if (isUsingOuterJoinForMultitableInheritance() && (!getSession().getPlatform().shouldPrintOuterJoinInWhereClause())) {
                normalizer.getStatement().getOuterJoinExpressions().add(null);
                normalizer.getStatement().getOuterJoinedMappingCriteria().add(null);
                normalizer.getStatement().getOuterJoinedAdditionalJoinCriteria().add(additionalExpressionCriteriaMap());
                normalizer.getStatement().getDescriptorsForMultitableInheritanceOnly().add(this.getDescriptor());
                // fall through to the main case
            }
            normalizer.addAdditionalExpression(criteria);


        }
        setStatement(normalizer.getStatement());
        if (getAsOfClause() == null) {
            asOf(AsOfClause.NO_CLAUSE);
        }
        if ((getDescriptor() != null) && (getDescriptor().getHistoryPolicy() != null)) {
            Expression temporalCriteria = getDescriptor().getHistoryPolicy().additionalHistoryExpression(this, this);
            normalizer.addAdditionalExpression(temporalCriteria);
        }

        ReadQuery query = normalizer.getStatement().getQuery();
        // Record any class used in a join to invlaidate query results cache.
        if ((query != null) && query.shouldCacheQueryResults()) {
            if (this.queryClass != null) {
                query.getQueryResultsCachePolicy().getInvalidationClasses().add(this.queryClass);
            }
        }

        return super.normalize(normalizer);
    }

    /**
     * INTERNAL:
     * Print java
     */
    public void printJava(ExpressionJavaPrinter printer) {
        printer.printString(printer.getBuilderString());        
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     * This assumes that the original expression has only a single builder.
     */
    public Expression rebuildOn(Expression newBase) {
        return newBase;
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
    
    /**
     * INTERNAL:
     * Override Expression.registerIn to check if the new base expression
     * has already been provided for the clone.
     * @see org.eclipse.persistence.expressions.Expression#cloneUsing(Expression)
     * @bug  2637484 INVALID QUERY KEY EXCEPTION THROWN USING BATCH READS AND PARALLEL EXPRESSIONS
     */
    protected Expression registerIn(Map alreadyDone) {
        // Here do a special check to see if this a cloneUsing(newBase) call.
        Object value = alreadyDone.get(alreadyDone);
        if ((value == null) || (value == alreadyDone)) {
            // This is a normal cloning operation.
            return super.registerIn(alreadyDone);
        }
        ObjectExpression copy = (ObjectExpression)value;

        // copy is actually the newBase of a cloneUsing.
        alreadyDone.put(alreadyDone, alreadyDone);
        alreadyDone.put(this, copy);
        // Now need to copy over the derived expressions, etc.
        if (this.derivedExpressions != null) {
            if (copy.derivedExpressions == null) {
                copy.derivedExpressions = copyDerivedExpressions(alreadyDone);
            } else {
                copy.derivedExpressions.addAll(copyDerivedExpressions(alreadyDone));
            }
        }

        // Do the same for these protected fields.
        copy.postCopyIn(alreadyDone, this.derivedFields, this.derivedTables);
        return copy;
    }

    /**
     * INTERNAL:
     * Set the class which this node represents.
     */
    public void setQueryClass(Class queryClass) {
        this.queryClass = queryClass;
        this.descriptor = null;
    }

    /**
     * INTERNAL:
     * Set the class and descriptor which this node represents.
     */
    public void setQueryClassAndDescriptor(Class queryClass, ClassDescriptor descriptor) {
        this.queryClass = queryClass;
        this.descriptor = convertToCastDescriptor(descriptor, session);
    }

    /**
     * INTERNAL:
     * Set the session in which we expect this expression to be translated.
     */
    public void setSession(AbstractSession session) {
        this.session = session;
    }

    /**
     * INTERNAL:
     * Set the statement that expression is for.
     * This is used for the context in subselects.
     */
    public void setStatement(SQLSelectStatement statement) {
        this.statement = statement;
    }

    /**
     * INTERNAL:
     * This expression represents something read through a view table.
     */
    public void setViewTable(DatabaseTable theTable) {
        viewTable = theTable;

    }

    /**
     * INTERNAL:
     * If the additional Join Criteria for the class this builder represents has
     * been added to the statement then mark this as true.  This will prevent
     * TopLink from adding it again at normalization
     */
    public void setWasAdditionJoinCriteriaUsed(boolean joinCriteriaUsed){
        this.wasAdditionJoinCriteriaUsed = joinCriteriaUsed;
    }
    
    /**
     * INTERNAL:
     * Rebuild myself against the base, with the values of parameters supplied by the context
     * expression. This is used for transforming a standalone expression (e.g. the join criteria of a mapping)
     * into part of some larger expression. You normally would not call this directly, instead calling twist
     * See the comment there for more details"
     * @param newBase
     * @param context
     * @return
     */
    @Override
    public Expression twistedForBaseAndContext(Expression newBase, Expression context, Expression oldBase) {
        // TODO: this is wrong, it should only return the newBase if equal to the old base
        // since twist needs to copy as it twists it needs a map to keep track of new bases
        return newBase;
    }

    /**
     * INTERNAL:
     * The expression builder represent the entire object, just return it.
     */
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        return object;
    }

    /**
     * INTERNAL:
     * If the additional Join Criteria for the class this builder represents has
     * been added to the statement this method will return true;
     */
    public boolean wasAdditionJoinCriteriaUsed(){
        return this.wasAdditionJoinCriteriaUsed;
    }
    
    /**
     * INTERNAL:
     * Returns true if TopLink set the query class as opposed to the customer.  This
     * is important in determining if this Expression should be treated as a parallel
     * expression during normalization
     */
    public boolean wasQueryClassSetInternally(){
        return this.wasQueryClassSetInternally;
    }
    
    /**
     * INTERNAL:
     * Lookup the descriptor for this item by traversing its expression recursively.
     */
    @Override
    public ClassDescriptor getLeafDescriptor(DatabaseQuery query, ClassDescriptor rootDescriptor, AbstractSession session) {
        // The base case
        // The following special case is where there is a parallel builder
        // which has a different reference class as the primary builder.
        Class queryClass = getQueryClass();
        if ((queryClass != null) && ((query == null) || (queryClass != query.getReferenceClass()))) {
            return session.getDescriptor(queryClass);
        }
        return rootDescriptor;
    }
    
    /**
     * INTERNAL:
     * For debug printing purposes.
     */
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        String className;
        if (getQueryClass() == null) {
            className = "QUERY OBJECT";
        } else {
            className = getQueryClass().getName();
        }
        writer.write(className + tableAliasesDescription());
    }
}
