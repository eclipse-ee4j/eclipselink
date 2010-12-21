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

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.mappings.querykeys.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Superclass for any object type expressions.
 */
public abstract class ObjectExpression extends DataExpression {
    transient public ClassDescriptor descriptor;
    public Vector derivedExpressions;

    /** indicates whether subclasses should be joined */
    protected boolean shouldUseOuterJoinForMultitableInheritance;

    /** Is this query key to be resolved using an outer join or not. Does not apply to attributes. */
    protected boolean shouldUseOuterJoin;

    protected Class castClass = null;

    public ObjectExpression() {
        this.shouldUseOuterJoin = false;
    }
    
    /**
     * ADVANCED:
     * Return an expression that allows you to treat its base as if it were a subclass of the class returned by the base
     * This can only be called on an ExpressionBuilder, the result of expression.get(String), expression.getAllowingNull(String),
     * the result of expression.anyOf("String") or the result of expression.anyOfAllowingNull("String")
     * 
     * downcast does not guarantee the results of the downcast will be of the specified class and should be used in conjunction
     * with a Expression.type()
     * <p>Example:
     * <pre><blockquote>
     *     TopLink: employee.get("project").as(LargeProject.class).get("budget").equal(1000)
     *     Java: ((LargeProject)employee.getProjects().get(0)).getBudget() == 1000
     *     SQL: LPROJ.PROJ_ID (+)= PROJ.PROJ_ID AND L_PROJ.BUDGET = 1000
     * </blockquote></pre>
     */
    public Expression as(Class castClass){
        setCastClass(castClass);
        return this;
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
        return super.equals(expression) && (shouldUseOuterJoin() == ((ObjectExpression)expression).shouldUseOuterJoin());
    }

    public void addDerivedExpression(Expression addThis) {
        if (derivedExpressions == null) {
            derivedExpressions = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        }
        derivedExpressions.addElement(addThis);
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
     * PUBLIC:
     * Return an expression representing traversal of a 1:many or many:many relationship.
     * This allows you to query whether any of the "many" side of the relationship satisfies the remaining criteria.
     * <p>Example:
     * <pre><blockquote>
     *     TopLink: employee.anyOf("managedEmployees").get("firstName").equal("Bob")
     *     Java: no direct equivalent
     *     SQL: SELECT DISTINCT ... WHERE (t2.MGR_ID = t1.ID) AND (t2.F_NAME = 'Bob')
     * </pre></blockquote>
     */
    public Expression anyOf(String attributeName) {
        QueryKeyExpression queryKey = newDerivedExpressionNamed(attributeName);

        queryKey.doQueryToManyRelationship();
        return queryKey;

    }
    
    /**
     * ADVANCED:
     * Return an expression representing traversal of a 1:many or many:many relationship.
     * This allows you to query whether any of the "many" side of the relationship satisfies the remaining criteria.
     * <p>Example:
     * <pre><blockquote>
     *     TopLink: employee.anyOf("managedEmployees").get("firstName").equal("Bob")
     *     Java: no direct equivalent
     *     SQL: SELECT DISTINCT ... WHERE (t2.MGR_ID (+) = t1.ID) AND (t2.F_NAME = 'Bob')
     * </pre></blockquote>
     */
    public Expression anyOfAllowingNone(String attributeName) {
        QueryKeyExpression queryKey = newDerivedExpressionNamed(attributeName);
        queryKey.doUseOuterJoin();
        queryKey.doQueryToManyRelationship();
        return queryKey;

    }
    
    /**
     * INTERNAL
     * Return the descriptor which contains this query key, look in the inheritance hierarchy 
     * of rootDescriptor for the descriptor
     * @param rootDescriptor
     * @return
     */
    public ClassDescriptor convertToCastDescriptor(ClassDescriptor rootDescriptor, AbstractSession session) {
        if (castClass == null){
            return rootDescriptor;
        }
        
        if (rootDescriptor.getJavaClass() == castClass){
           this.descriptor = rootDescriptor;
            return rootDescriptor;
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
                this.descriptor = castDescriptor;
                return castDescriptor;
            }
            parentDescriptor = parentDescriptor.getInheritancePolicy().getParentDescriptor();
        }
        
        ClassDescriptor childDescriptor = rootDescriptor;
        while (childDescriptor != null){
            if (childDescriptor == castDescriptor){
                descriptor = rootDescriptor;
                return descriptor;
            }
            childDescriptor = childDescriptor.getInheritancePolicy().getParentDescriptor();
        }
        
        throw QueryException.couldNotFindCastDescriptor(castClass, getBaseExpression());
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

    protected void doNotUseOuterJoin() {
        shouldUseOuterJoin = false;
    }

    protected void doUseOuterJoin() {
        shouldUseOuterJoin = true;
    }

    public QueryKeyExpression existingDerivedExpressionNamed(String attributeName) {
        if (derivedExpressions == null) {
            return null;
        }
        for (Enumeration e = derivedExpressions.elements(); e.hasMoreElements();) {
            QueryKeyExpression exp = (QueryKeyExpression)e.nextElement();
            if (exp.getName().equals(attributeName)) {
                return exp;
            }
        }
        return null;
    }

    public Expression get(String attributeName, Vector arguments) {
        Expression operatorExpression = super.get(attributeName, arguments);
        if (operatorExpression != null) {
            return operatorExpression;
        }

        QueryKeyExpression result = derivedExpressionNamed(attributeName);
        result.doNotUseOuterJoin();
        return result;

    }

    public Expression getAllowingNull(String attributeName, Vector arguments) {
        ObjectExpression exp = existingDerivedExpressionNamed(attributeName);

        // The same (aliased) table cannot participate in a normal join and an outer join.
        // To help enforce this, if the node already exists 
        if (exp != null) {
            return exp;
        }
        exp = derivedExpressionNamed(attributeName);
        exp.doUseOuterJoin();
        return exp;

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
    public Expression type() {
        return new ClassTypeExpression(this);
    }

    public ClassDescriptor getDescriptor() {
        if (isAttribute()) {
            return null;
        }
        if (descriptor == null) {
            // Look first for query keys, then mappings. Ultimately we should have query keys
            // for everything and can dispense with the mapping part.
            ForeignReferenceQueryKey queryKey = (ForeignReferenceQueryKey)getQueryKeyOrNull();
            if (queryKey != null) {
                descriptor = getSession().getDescriptor(queryKey.getReferenceClass());
                return convertToCastDescriptor(descriptor, getSession());
            }
            if (getMapping() == null) {
                throw QueryException.invalidQueryKeyInExpression(this);
            }

            // We assume this is either a foreign reference or an aggregate mapping
            descriptor = getMapping().getReferenceDescriptor();
            if (getMapping().isVariableOneToOneMapping()) {
                throw QueryException.cannotQueryAcrossAVariableOneToOneMapping(getMapping(), descriptor);
            }
            convertToCastDescriptor(descriptor, getSession());
        }
        return descriptor;

    }

    /**
     * INTERNAL: Not to be confused with the public getField(String)
     * This returns a collection of all fields associated with this object. Really
     * only applies to query keys representing an object or to expression builders.
     */
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
    public Vector getOwnedTables() {
        ClassDescriptor descriptor = getDescriptor();
        Vector tables = null;
        if (descriptor == null) {
            List additionalTables = getAdditionalTables();
            if (additionalTables == null) {
                return null;
            } else {
                return new Vector(additionalTables);
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
            Helper.addAllUniqueToVector(tables, additionalTables);
            return tables;
        }
        return tables;
    }

    protected boolean hasDerivedExpressions() {
        return derivedExpressions != null;
    }

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
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        derivedExpressions = copyCollection(derivedExpressions, alreadyDone);
    }

    /**
     * INTERNAL:
     * The method was added to circumvent derivedFields and derivedTables being
     * protected.
     * @see org.eclipse.persistence.expressions.ExpressionBuilder#registerIn(Map alreadyDone)
     * @bug  2637484 INVALID QUERY KEY EXCEPTION THROWN USING BATCH READS AND PARALLEL EXPRESSIONS
     */
    public void postCopyIn(Map alreadyDone, Vector oldDerivedFields, Vector oldDerivedTables) {
        if (oldDerivedFields != null) {
            if (derivedFields == null) {
                derivedFields = copyCollection(oldDerivedFields, alreadyDone);
            } else {
                derivedFields.addAll(copyCollection(oldDerivedFields, alreadyDone));
            }
        }
        if (oldDerivedTables != null) {
            if (derivedTables == null) {
                derivedTables = copyCollection(oldDerivedTables, alreadyDone);
            } else {
                derivedTables.addAll(copyCollection(oldDerivedTables, alreadyDone));
            }
        }
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
}
