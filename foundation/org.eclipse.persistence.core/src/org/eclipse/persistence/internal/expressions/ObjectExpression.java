/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.mappings.querykeys.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadQuery;

/**
 * Superclass for any object type expressions.
 */
public abstract class ObjectExpression extends DataExpression {
    protected transient ClassDescriptor descriptor;
    public List<Expression> derivedExpressions;

    /** indicates whether subclasses should be joined */
    protected boolean shouldUseOuterJoinForMultitableInheritance;

    /** Is this query key to be resolved using an outer join or not. Does not apply to attributes. */
    protected boolean shouldUseOuterJoin;

    /** Allow an expression node to be cast to a subclass or implementation class. */
    protected Class castClass;

    /** Defines that this expression has been joined to the source expression. */
    protected Expression joinSource;

    /** Allow for an ON clause to be specified on a join condition. */
    protected Expression onClause;

    /** Used to track the index of the OuterJoinExpressionHolder that might be associated to this expression */
    private Integer outerJoinExpIndex = null;

    /** Allow hasBeenAliased to be marked independently from the existence of the tableAliases collection. */
    protected boolean hasBeenAliased = false;

    public ObjectExpression() {
        this.shouldUseOuterJoin = false;
    }

    /**
     * Return an expression that allows you to treat its base as if it were a subclass of the class returned by the base
     * This can only be called on an ExpressionBuilder, the result of expression.get(String), expression.getAllowingNull(String),
     * the result of expression.anyOf("String") or the result of expression.anyOfAllowingNull("String")
     *
     * downcast uses Expression.type() internally to guarantee the results are of the specified class.
     * <p>Example:
     * <pre><blockquote>
     *     Expression: employee.get("project").as(LargeProject.class).get("budget").equal(1000)
     *     Java: ((LargeProject)employee.getProjects().get(0)).getBudget() == 1000
     *     SQL: LPROJ.PROJ_ID (+)= PROJ.PROJ_ID AND L_PROJ.BUDGET = 1000 AND PROJ.TYPE = "L"
     * </blockquote></pre>
     */
    @Override
    public Expression treat(Class castClass){
        //to be used on expressionBuilders
        QueryKeyExpression clonedExpression = new TreatAsExpression(castClass, this);
        clonedExpression.shouldQueryToManyRelationship = false;
        clonedExpression.hasQueryKey = false;
        clonedExpression.hasMapping = false;

        this.addDerivedExpression(clonedExpression);
        return clonedExpression;
    }

    /**
     * INTERNAL:
     * Return if the expression is equal to the other.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    @Override
    public boolean equals(Object expression) {
        if (this == expression) {
            return true;
        }
        if (!super.equals(expression)) {
            return false;
        }
        if ((this.onClause != null) || (((ObjectExpression)expression).onClause != null)) {
            return false;
        }
        return this.shouldUseOuterJoin == ((ObjectExpression)expression).shouldUseOuterJoin;
    }

    /**
     * INTERNAL:
     * Add the expression as a derived child of this expression.
     * i.e. e.get("name"), "name" is a derived child of "e".
     */
    public synchronized void addDerivedExpression(Expression addThis) {
        if (this.derivedExpressions == null) {
            this.derivedExpressions = new ArrayList();
        }
        this.derivedExpressions.add(addThis);
    }

    /**
     * INTERNAL:
     * Return the expression to join the main table of this node to any auxiliary tables.
     */
    public Expression additionalExpressionCriteria() {
        if (getDescriptor() == null) {
            return null;
        }

        Expression criteria = getDescriptor().getQueryManager().getAdditionalJoinExpression();
        if(getSession().getPlatform().shouldPrintOuterJoinInWhereClause()) {
            if(isUsingOuterJoinForMultitableInheritance()) {
                Expression childrenCriteria = getDescriptor().getInheritancePolicy().getChildrenJoinExpression();
                childrenCriteria = getBaseExpression().twist(childrenCriteria, this);
                childrenCriteria.convertToUseOuterJoin();
                if(criteria == null) {
                    criteria = childrenCriteria;
                } else {
                    criteria = criteria.and(childrenCriteria);
                }
            }
        }

        return criteria;
    }

    /**
     * INTERNAL:
     * Used in case outer joins should be printed in FROM clause.
     * Each of the additional tables mapped to expressions that joins it.
     */
    public Map additionalExpressionCriteriaMap() {
        if (getDescriptor() == null) {
            return null;
        }

        HashMap tablesJoinExpressions = null;
        if(isUsingOuterJoinForMultitableInheritance()) {
            tablesJoinExpressions = new HashMap();
            List childrenTables = getDescriptor().getInheritancePolicy().getChildrenTables();
            for( int i=0; i < childrenTables.size(); i++) {
                DatabaseTable table = (DatabaseTable)childrenTables.get(i);
                Expression joinExpression = getDescriptor().getInheritancePolicy().getChildrenTablesJoinExpressions().get(table);
                if (getBaseExpression() != null){
                    joinExpression = getBaseExpression().twist(joinExpression, this);
                } else {
                    joinExpression = twist(joinExpression, this);
                }
                tablesJoinExpressions.put(table, joinExpression);
            }
        }

        return tablesJoinExpressions;
    }

    /**
     * INTERNAL:
     * Assign aliases to any tables which I own. Start with t(initialValue),
     * and return the new value of  the counter , i.e. if initialValue is one
     * and I have tables ADDRESS and EMPLOYEE I will assign them t1 and t2 respectively, and return 3.
     */
    public int assignTableAliasesStartingAt(int initialValue) {
        //This differs from the Expression implementation only in that it must set the hasBeenAliased flag when done.
        int returnVal = super.assignTableAliasesStartingAt(initialValue);
        this.hasBeenAliased = true;
        return returnVal;
    }

    /**
     * PUBLIC:
     * Return an expression representing traversal of a 1:many or many:many relationship.
     * This allows you to query whether any of the "many" side of the relationship satisfies the remaining criteria.
     * <p>Example:
     * <pre><blockquote>
     *     Expression: employee.anyOf("managedEmployees").get("firstName").equal("Bob")
     *     Java: no direct equivalent
     *     SQL: SELECT DISTINCT ... WHERE (t2.MGR_ID = t1.ID) AND (t2.F_NAME = 'Bob')
     * </pre></blockquote>
     * @parameter shouldJoinBeIndependent indicates whether a new expression should be created.
     */
    @Override
    public Expression anyOf(String attributeName, boolean shouldJoinBeIndependent) {
        QueryKeyExpression queryKey;
        if (shouldJoinBeIndependent) {
            queryKey = newDerivedExpressionNamed(attributeName);
        } else {
            queryKey = derivedExpressionNamed(attributeName);
        }
        queryKey.doQueryToManyRelationship();
        return queryKey;
    }

    /**
     * ADVANCED:
     * Return an expression representing traversal of a 1:many or many:many relationship.
     * This allows you to query whether any of the "many" side of the relationship satisfies the remaining criteria.
     * <p>Example:
     * <pre><blockquote>
     *     Expression: employee.anyOf("managedEmployees").get("firstName").equal("Bob")
     *     Java: no direct equivalent
     *     SQL: SELECT DISTINCT ... WHERE (t2.MGR_ID (+) = t1.ID) AND (t2.F_NAME = 'Bob')
     * </pre></blockquote>
     * @parameter shouldJoinBeIndependent indicates whether a new expression should be created.
     */
    @Override
    public Expression anyOfAllowingNone(String attributeName, boolean shouldJoinBeIndependent) {
        QueryKeyExpression queryKey;
        if (shouldJoinBeIndependent) {
            queryKey = newDerivedExpressionNamed(attributeName);
        } else {
            queryKey = derivedExpressionNamed(attributeName);
        }
        queryKey.doUseOuterJoin();
        queryKey.doQueryToManyRelationship();
        return queryKey;
    }

    /**
     * INTERNAL
     * Return true if it uses a cast class and query is downcasting. It will
     * look into inheritance hierarchy of the root descriptor.
     */
    public boolean isDowncast(ClassDescriptor rootDescriptor, AbstractSession session) {
        if (castClass == null){
            return false;
        }

        if (rootDescriptor.getJavaClass() == castClass){
            return false;
        }

        ClassDescriptor castDescriptor = session.getClassDescriptor(castClass);

        if (castDescriptor == null){
            throw QueryException.couldNotFindCastDescriptor(castClass, getBaseExpression());
        }
        if (castDescriptor.getInheritancePolicy() == null){
            throw QueryException.castMustUseInheritance(getBaseExpression());
        }
        ClassDescriptor parentDescriptor = castDescriptor.getInheritancePolicy().getParentDescriptor();
        while (parentDescriptor != null){
            if (parentDescriptor == rootDescriptor){
                return true;
            }
            parentDescriptor = parentDescriptor.getInheritancePolicy().getParentDescriptor();
        }

        throw QueryException.couldNotFindCastDescriptor(castClass, getBaseExpression());
    }

    /**
     * INTERNAL
     * Return true if treat was used on this expression
     */
    public boolean isTreatUsed() {
        if  (this.hasDerivedExpressions() )
            for (Expression exp: this.derivedExpressions) {
                if (exp.isTreatExpression()) {
                    return true;
            }
        }
        return false;
    }

    /**
     * INTERNAL
     * Return the descriptor which contains this query key, look in the inheritance hierarchy
     * of rootDescriptor for the descriptor.
     */
    public ClassDescriptor convertToCastDescriptor(ClassDescriptor rootDescriptor, AbstractSession session) {
        if (castClass == null || rootDescriptor == null || rootDescriptor.getJavaClass() == castClass) {
            return rootDescriptor;
        }

        ClassDescriptor castDescriptor = session.getClassDescriptor(castClass);

        if (castDescriptor == null){
            throw QueryException.couldNotFindCastDescriptor(castClass, getBaseExpression());
        }
        if (!castDescriptor.hasInheritance()){
            throw QueryException.castMustUseInheritance(getBaseExpression());
        }
        ClassDescriptor parentDescriptor = castDescriptor.getInheritancePolicy().getParentDescriptor();
        while (parentDescriptor != null){
            if (parentDescriptor == rootDescriptor){
                return castDescriptor;
            }
            parentDescriptor = parentDescriptor.getInheritancePolicy().getParentDescriptor();
        }

        ClassDescriptor childDescriptor = rootDescriptor;
        while (childDescriptor != null){
            if (childDescriptor == castDescriptor){
                return rootDescriptor;
            }
            childDescriptor = childDescriptor.getInheritancePolicy().getParentDescriptor();
        }

        throw QueryException.couldNotFindCastDescriptor(castClass, getBaseExpression());
    }

    public List<Expression> copyDerivedExpressions(Map alreadyDone) {
        if (this.derivedExpressions == null) {
            return null;
        }
        List<Expression> derivedExpressionsCopy;
        synchronized(this) {
            derivedExpressionsCopy = new ArrayList(this.derivedExpressions);
        }
        List<Expression> result = new ArrayList(derivedExpressionsCopy.size());
        for (Expression exp : derivedExpressionsCopy) {
            result.add(exp.copiedVersionFrom(alreadyDone));
        }
        return result;
    }

    public QueryKeyExpression derivedExpressionNamed(String attributeName) {
        QueryKeyExpression existing = existingDerivedExpressionNamed(attributeName);
        if (existing != null) {
            return existing;
        }
        return newDerivedExpressionNamed(attributeName);

    }

    public Expression derivedManualExpressionNamed(String attributeName, ClassDescriptor aDescriptor) {
        Expression existing = existingDerivedExpressionNamed(attributeName);
        if (existing != null) {
            return existing;
        }
        return newManualDerivedExpressionNamed(attributeName, aDescriptor);

    }

    public void doNotUseOuterJoin() {
        shouldUseOuterJoin = false;
    }

    public void doUseOuterJoin() {
        shouldUseOuterJoin = true;
    }

    public QueryKeyExpression existingDerivedExpressionNamed(String attributeName) {
        if (this.derivedExpressions == null) {
            return null;
        }
        List<Expression> derivedExpressionsCopy;
        synchronized(this) {
            derivedExpressionsCopy = new ArrayList(this.derivedExpressions);
        }
        for (Expression derivedExpression : derivedExpressionsCopy) {
            QueryKeyExpression exp = (QueryKeyExpression)derivedExpression;
            if (exp.getName().equals(attributeName)) {
                return exp;
            }
        }
        return null;
    }

    /**
     * Return the expression from the attribute dervied from this expression.
     */
    @Override
    public Expression get(String attributeName, boolean forceInnerJoin) {
        ObjectExpression result = derivedExpressionNamed(attributeName);
        if (forceInnerJoin) {
            result.doNotUseOuterJoin();
        }
        return result;
    }

    /**
     * Defines a join between this expression and the target expression based on the ON clause.
     */
    @Override
    public Expression leftJoin(Expression target, Expression onClause) {
        join(target, onClause);
        ((ObjectExpression)target).doUseOuterJoin();
        return this;
    }

    /**
     * Defines a join between this expression and the target expression based on the ON clause.
     */
    @Override
    public Expression join(Expression target, Expression onClause) {
        if (target instanceof ObjectExpression) {
            ((ObjectExpression)target).setJoinSource(this);
            ((ObjectExpression)target).setOnClause(onClause);
        } else {
            throw new IllegalArgumentException();
        }
        return this;
    }

    @Override
    public Expression getAllowingNull(String attributeName) {
        ObjectExpression exp = existingDerivedExpressionNamed(attributeName);

        // The same (aliased) table cannot participate in a normal join and an outer join.
        // To help enforce this, if the node already exists
        if (exp != null) {
            return exp;
        }
        ObjectExpression result = derivedExpressionNamed(attributeName);
        result.doUseOuterJoin();
        return result;
    }

    public Class getCastClass() {
        return castClass;
    }

    /**
     * PUBLIC:
     * Return an expression that wraps the inheritance type field in an expression.
     * <p>Example:
     * <pre><blockquote>
     *  builder.getClassForInheritance().equal(SmallProject.class);
     * </blockquote></pre>
     */
    @Override
    public Expression type() {
        return new ClassTypeExpression(this);
    }

    @Override
    public ClassDescriptor getDescriptor() {
        if (isAttribute()) {
            return null;
        }
        if (descriptor == null) {
            // Look first for query keys, then mappings. Ultimately we should have query keys
            // for everything and can dispense with the mapping part.
            ForeignReferenceQueryKey queryKey = (ForeignReferenceQueryKey)getQueryKeyOrNull();
            if (queryKey != null) {
                descriptor = convertToCastDescriptor(getSession().getDescriptor(queryKey.getReferenceClass()), getSession());
                return descriptor;
            }
            if (getMapping() == null) {
                throw QueryException.invalidQueryKeyInExpression(this);
            }

            // We assume this is either a foreign reference or an aggregate mapping
            descriptor = getMapping().getReferenceDescriptor();
            if (getMapping().isVariableOneToOneMapping()) {
                throw QueryException.cannotQueryAcrossAVariableOneToOneMapping(getMapping(), descriptor);
            }
            descriptor = convertToCastDescriptor(descriptor, getSession());
        }
        return descriptor;

    }

    /**
     * INTERNAL: Not to be confused with the public getField(String)
     * This returns a collection of all fields associated with this object. Really
     * only applies to query keys representing an object or to expression builders.
     */
    @Override
    public Vector getFields() {
        if (getDescriptor() == null) {
            DatabaseMapping mapping = getMapping();
            if (mapping != null) {
                return mapping.getSelectFields();
            }
            return new NonSynchronizedVector(0);
        }
        if (descriptor.hasInheritance() && descriptor.getInheritancePolicy().shouldReadSubclasses()
                && (!descriptor.getInheritancePolicy().hasMultipleTableChild()) || shouldUseOuterJoinForMultitableInheritance()) {
            // return all fields because we can.
            return descriptor.getAllFields();
        } else {
            return descriptor.getFields();
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public List<DatabaseField> getSelectionFields(ReadQuery query) {
        if (getDescriptor() == null) {
            DatabaseMapping mapping = getMapping();
            if (mapping != null) {
                return mapping.getSelectFields();
            }
            return new ArrayList<DatabaseField>(0);
        }
        if (descriptor.hasInheritance() && descriptor.getInheritancePolicy().shouldReadSubclasses()
                && (!descriptor.getInheritancePolicy().hasMultipleTableChild()) || shouldUseOuterJoinForMultitableInheritance()) {
            // return all fields because we can.
            if (query != null && query.isObjectLevelReadQuery()) {
                return descriptor.getAllSelectionFields((ObjectLevelReadQuery)query);
            } else {
                return descriptor.getAllSelectionFields();
            }
        } else {
            if (query != null && query.isObjectLevelReadQuery()) {
                return descriptor.getSelectionFields((ObjectLevelReadQuery)query);
            } else {
                return descriptor.getSelectionFields();
            }
        }
    }

    /**
     * INTERNAL:
     * Returns the first field from each of the owned tables, used for
     * fine-grained pessimistic locking.
     */
    protected Vector getForUpdateOfFields() {
        Vector allFields = getFields();
        int expected = getTableAliases().size();
        Vector firstFields = new Vector(expected);
        DatabaseTable lastTable = null;
        DatabaseField field = null;
        int i = 0;

        // The following loop takes O(n*m) time.  n=# of fields. m=#tables.
        // However, in the m=1 case this will take one pass only.
        // Also assuming that fields are generally sorted by table, this will
        // take O(n) time.
        // An even faster way may be to go getDescriptor().getAdditionalPrimaryKeyFields.
        while ((i < allFields.size()) && (firstFields.size() < expected)) {
            field = (DatabaseField)allFields.elementAt(i++);
            if ((lastTable == null) || !field.getTable().equals(lastTable)) {
                lastTable = field.getTable();
                int j = 0;
                while (j < firstFields.size()) {
                    if (lastTable.equals(((DatabaseField)firstFields.elementAt(j)).getTable())) {
                        break;
                    }
                    j++;
                }
                if (j == firstFields.size()) {
                    firstFields.addElement(field);
                }
            }
        }
        return firstFields;
    }

    public Expression getManualQueryKey(String attributeName, ClassDescriptor aDescriptor) {
        return derivedManualExpressionNamed(attributeName, aDescriptor);
    }

    /**
     * Return any tables in addition to the descriptor's tables, such as the mappings join table.
     */
    public List<DatabaseTable> getAdditionalTables() {
        return null;
    }

    /**
     * Return any tables that are defined by this expression (and not its base).
     */
    @Override
    public List<DatabaseTable> getOwnedTables() {
        ClassDescriptor descriptor = getDescriptor();
        List<DatabaseTable> tables = null;
        if (descriptor == null) {
            List additionalTables = getAdditionalTables();
            if (additionalTables == null) {
                return null;
            } else {
                return new ArrayList(additionalTables);
            }
        } else if (descriptor.isAggregateDescriptor()) {
            return null;
        } else if ((descriptor.getHistoryPolicy() != null) && (getAsOfClause().getValue() != null)) {
            tables = descriptor.getHistoryPolicy().getHistoricalTables();
        } else if (isUsingOuterJoinForMultitableInheritance()) {
            tables = descriptor.getInheritancePolicy().getAllTables();
        } else {
            tables = descriptor.getTables();
        }
        List additionalTables = getAdditionalTables();
        if (additionalTables != null) {
            tables = new Vector(tables);
            Helper.addAllUniqueToList(tables, additionalTables);
            return tables;
        }
        return tables;
    }

    public boolean hasBeenAliased() {
        return hasBeenAliased;
    }

    /**
     * INTERNAL:
     */
    public void clearAliases() {
        hasBeenAliased = false;
        super.clearAliases();
    }

    protected boolean hasDerivedExpressions() {
        return derivedExpressions != null;
    }

    @Override
    public boolean isObjectExpression() {
        return true;
    }

    /**
     * INTERNAL:
     * indicates whether additional expressions for multitable inheritance should be used and are available
     */
    public boolean isUsingOuterJoinForMultitableInheritance() {
        return shouldUseOuterJoinForMultitableInheritance() &&
                getDescriptor() != null && getDescriptor().hasInheritance() &&
                getDescriptor().getInheritancePolicy().hasMultipleTableChild() &&
                getDescriptor().getInheritancePolicy().shouldReadSubclasses();
    }

    public QueryKeyExpression newDerivedExpressionNamed(String attributeName) {
        QueryKeyExpression result = new QueryKeyExpression(attributeName, this);
        addDerivedExpression(result);
        return result;

    }

    public Expression newManualDerivedExpressionNamed(String attributeName, ClassDescriptor aDescriptor) {
        QueryKeyExpression result = new ManualQueryKeyExpression(attributeName, this, aDescriptor);
        addDerivedExpression(result);
        return result;

    }

    /**
     * INTERNAL:
     * Used for cloning.
     */
    @Override
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        this.derivedExpressions = copyDerivedExpressions(alreadyDone);
        if (this.onClause != null) {
            this.onClause = this.onClause.copiedVersionFrom(alreadyDone);
        }
        if (this.joinSource != null) {
            this.joinSource = this.joinSource.copiedVersionFrom(alreadyDone);
        }
    }

    /**
     * Return null by default, only QueryKeyExpression can have a relation table.
     */
    public DatabaseTable getRelationTable() {
        return null;
    }

    /**
     * Return false by default, only possible for QueryKeyExpression.
     */
    public boolean isDirectCollection() {
        return false;
    }

    /**
     * INTERNAL:
     * The method was added to circumvent derivedFields and derivedTables being
     * protected.
     * @see org.eclipse.persistence.expressions.ExpressionBuilder#registerIn(Map alreadyDone)
     * @bug  2637484 INVALID QUERY KEY EXCEPTION THROWN USING BATCH READS AND PARALLEL EXPRESSIONS
     */
    public void postCopyIn(Map alreadyDone, List<Expression> oldDerivedFields, List<Expression> oldDerivedTables) {
        if (oldDerivedFields != null) {
            if (this.derivedFields == null) {
                this.derivedFields = copyCollection(oldDerivedFields, alreadyDone);
            } else {
                this.derivedFields.addAll(copyCollection(oldDerivedFields, alreadyDone));
            }
        }
        if (oldDerivedTables != null) {
            if (this.derivedTables == null) {
                this.derivedTables = copyCollection(oldDerivedTables, alreadyDone);
            } else {
                this.derivedTables.addAll(copyCollection(oldDerivedTables, alreadyDone));
            }
        }
    }

    public Expression getOnClause() {
        return onClause;
    }

    public void setOnClause(Expression onClause) {
        this.onClause = onClause;
    }

    public void setCastClass(Class castClass) {
        this.castClass = castClass;
    }

    /**
     * INTERNAL:
     * set the flag indicating whether subclasses should be joined
     */
    public void setShouldUseOuterJoinForMultitableInheritance(boolean shouldUseOuterJoinForMultitableInheritance) {
        this.shouldUseOuterJoinForMultitableInheritance = shouldUseOuterJoinForMultitableInheritance;
    }

    public boolean shouldUseOuterJoin() {
        return shouldUseOuterJoin;
    }

    public boolean shouldUseOuterJoinForMultitableInheritance() {
        return shouldUseOuterJoinForMultitableInheritance;
    }

    /**
     * INTERNAL:
     * writes the first field from each of the owned tables, used for
     * fine-grained pessimistic locking.
     */
    protected void writeForUpdateOfFields(ExpressionSQLPrinter printer, SQLSelectStatement statement) {
        for (Iterator iterator = getForUpdateOfFields().iterator(); iterator.hasNext();) {
            DatabaseField field = (DatabaseField)iterator.next();
            if (printer.getPlatform().shouldPrintAliasForUpdate()) {
                writeAlias(printer, field, statement);
            } else {
                writeField(printer, field, statement);
            }
        }
    }

    public Expression getJoinSource() {
        return joinSource;
    }

    public void setJoinSource(Expression joinSource) {
        this.joinSource = joinSource;
    }

    public Integer getOuterJoinExpIndex() {
        return outerJoinExpIndex;
    }

    public void setOuterJoinExpIndex(Integer outerJoinExpIndex) {
        this.outerJoinExpIndex = outerJoinExpIndex;
    }

    /**
     * INTERNAL:
     * Parses an expression to return the first non-AggregateObjectMapping expression after the base ExpressionBuilder.
     * This is used by joining and batch fetch to get the list of mappings that really need to be processed (non-aggregates).
     * @param aggregateMappingsEncountered - collection of aggregateObjectMapping expressions encountered in the returned expression
     *  between the first expression and the ExpressionBuilder
     * @return first non-AggregateObjectMapping expression after the base ExpressionBuilder from the fullExpression
     */
    public ObjectExpression getFirstNonAggregateExpressionAfterExpressionBuilder(List aggregateMappingsEncountered) {
        boolean done = false;
        ObjectExpression baseExpression = this;
        ObjectExpression prevExpression = this;
        while (!baseExpression.getBaseExpression().isExpressionBuilder() && !done) {
            baseExpression = (ObjectExpression)baseExpression.getBaseExpression();
            while (!baseExpression.isExpressionBuilder() && baseExpression.getMapping().isAggregateObjectMapping()) {
                aggregateMappingsEncountered.add(baseExpression.getMapping());
                baseExpression = (ObjectExpression)baseExpression.getBaseExpression();
            }
            if (baseExpression.isExpressionBuilder()) {
                done = true;
                //use the one closest to the expression builder that wasn't an aggregate
                baseExpression = prevExpression;
            } else {
                prevExpression = baseExpression;
            }
        }
        return baseExpression;
    }

}
