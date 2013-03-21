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
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.*;

import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.foundation.AbstractColumnMapping;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.mappings.querykeys.*;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Represents expression on query keys or mappings.
 * This includes direct, relationships query keys and mappings.
 */
public class QueryKeyExpression extends ObjectExpression {

    /** The name of the query key. */
    protected String name;

    /** Cache the aliased field. Only applies to attributes. */
    protected DatabaseField aliasedField;

    /** Is this a query across a 1:many or many:many relationship. Does not apply to attributes. */
    protected boolean shouldQueryToManyRelationship;

    /** Cache the query key for performance. Store a boolean so we don't repeat the search if there isn't one. */
    transient protected QueryKey queryKey;
    protected boolean hasQueryKey;

    /** Cache the mapping for performance. Store a boolean so we don't repeat the search if there isn't one. */
    transient protected DatabaseMapping mapping;
    protected boolean hasMapping;
    
    /** PERF: Cache if the expression is an attribute expression. */
    protected Boolean isAttributeExpression;
    
    protected IndexExpression index;
    
    protected boolean isClonedForSubQuery = false;
    
    public QueryKeyExpression() {
        this.shouldQueryToManyRelationship = false;
        this.hasQueryKey = true;
        this.hasMapping = true;
    }

    public QueryKeyExpression(String aName, Expression base) {
        super();
        name = aName;
        baseExpression = base;
        shouldUseOuterJoin = false;
        shouldQueryToManyRelationship = false;
        hasQueryKey = true;
        hasMapping = true;
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
        QueryKeyExpression expression = (QueryKeyExpression) object;        
        // Return false for anyOf expressions, as equality is unknown.
        if (shouldQueryToManyRelationship() || expression.shouldQueryToManyRelationship()) {
            return false;
        }
        return ((getName() == expression.getName()) || ((getName() != null) && getName().equals(expression.getName())));
    }
        
    /**
     * INTERNAL:
     * Compute a consistent hash-code for the expression.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    @Override
    public int computeHashCode() {
        int hashCode = super.computeHashCode();
        if (getName() != null) {
            hashCode = hashCode + getName().hashCode();
        }
        return hashCode;
    }

    /**
     * INTERNAL:
     * Return the expression to join the main table of this node to any auxiliary tables.
     */
    @Override
    public Expression additionalExpressionCriteria() {
        if (getDescriptor() == null) {
            return null;
        }

        Expression criteria = getDescriptor().getQueryManager().getAdditionalJoinExpression();
        if (criteria != null) {
            criteria = this.baseExpression.twist(criteria, this);
            if (shouldUseOuterJoin() && getSession().getPlatform().shouldPrintOuterJoinInWhereClause()) {
                criteria.convertToUseOuterJoin();
            }
        }
        if(getSession().getPlatform().shouldPrintOuterJoinInWhereClause()) {
            if(isUsingOuterJoinForMultitableInheritance()) {
                Expression childrenCriteria = getDescriptor().getInheritancePolicy().getChildrenJoinExpression();
                childrenCriteria = this.baseExpression.twist(childrenCriteria, this);
                childrenCriteria.convertToUseOuterJoin();
                if(criteria == null) {
                    criteria = childrenCriteria;
                } else {
                    criteria = criteria.and(childrenCriteria);
                }
            }
        }
        if (getDescriptor().getHistoryPolicy() != null) {
            Expression historyCriteria = getDescriptor().getHistoryPolicy().additionalHistoryExpression(this, this);
            if (criteria != null) {
                criteria = criteria.and(historyCriteria);
            } else {
                criteria = historyCriteria;
            }
        }
        return criteria;
    }

    /**
     * INTERNAL:
     * Used in case outer joins should be printed in FROM clause.
     * Each of the additional tables mapped to expressions that joins it.
     */
    @Override
    public Map additionalExpressionCriteriaMap() {
        if (getDescriptor() == null) {
            return null;
        }

        HashMap tablesJoinExpressions = new HashMap();
        Vector tables = getDescriptor().getTables();
        // skip the main table - start with i=1
        int tablesSize = tables.size();
        if (shouldUseOuterJoin() || (!getSession().getPlatform().shouldPrintInnerJoinInWhereClause())) {
            for (int i=1; i < tablesSize; i++) {
                DatabaseTable table = (DatabaseTable)tables.elementAt(i);
                Expression joinExpression = getDescriptor().getQueryManager().getTablesJoinExpressions().get(table);
                joinExpression = this.baseExpression.twist(joinExpression, this);
                if (getDescriptor().getHistoryPolicy() != null) {
                    joinExpression = joinExpression.and(getDescriptor().getHistoryPolicy().additionalHistoryExpression(this, this, i));
                }
                tablesJoinExpressions.put(table, joinExpression);
            }
        }
        if (isUsingOuterJoinForMultitableInheritance()) {
            List childrenTables = getDescriptor().getInheritancePolicy().getChildrenTables();
            tablesSize = childrenTables.size();
            for (int i=0; i < tablesSize; i++) {
                DatabaseTable table = (DatabaseTable)childrenTables.get(i);
                Expression joinExpression = getDescriptor().getInheritancePolicy().getChildrenTablesJoinExpressions().get(table);
                joinExpression = this.baseExpression.twist(joinExpression, this);
                tablesJoinExpressions.put(table, joinExpression);
            }
        }
        
        return tablesJoinExpressions;
    }

    /**
     * INTERNAL:
     * Find the alias for a given table
     */
    @Override
    public DatabaseTable aliasForTable(DatabaseTable table) {
        DatabaseMapping mapping = getMapping();
        if (isAttribute() || ((mapping != null) && (mapping.isAggregateObjectMapping() || mapping.isTransformationMapping()))) {
            return ((DataExpression)this.baseExpression).aliasForTable(table);
        }

        //"ref" and "structure" mappings, no table printed in the FROM clause, need to get the table alias form the parent table
        if ((mapping != null) && (mapping.isReferenceMapping() || mapping.isStructureMapping())) {
            DatabaseTable alias = this.baseExpression.aliasForTable(mapping.getDescriptor().getTables().firstElement());
            alias.setName(alias.getName() + "." + mapping.getField().getName());
            return alias;
        }
        
        // For direct-collection mappings the alias is store on the table expression.
        if ((mapping != null) && (mapping.isDirectCollectionMapping())) {
            if (tableAliases != null){
                DatabaseTable aliasedTable = tableAliases.keyAtValue(table);
                if (aliasedTable != null){
                    return aliasedTable;
                }
            }
            return getTable(table).aliasForTable(table);
        }

        return super.aliasForTable(table);
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
    @Override
    public Expression treat(Class castClass){
        QueryKeyExpression clonedExpression = new QueryKeyExpression(getName(), this.baseExpression);
        clonedExpression.shouldQueryToManyRelationship = this.shouldQueryToManyRelationship;
        clonedExpression.shouldUseOuterJoin = this.shouldUseOuterJoin;
        clonedExpression.hasQueryKey = this.hasQueryKey;
        clonedExpression.hasMapping = this.hasMapping;

        clonedExpression.setCastClass(castClass);
        return clonedExpression;
    }
    
    /**
     * INTERNAL:
     * Used for cloning.
     */
    @Override
    protected void postCopyIn(Map alreadyDone) {
        super.postCopyIn(alreadyDone);
        if (this.index != null) {
            this.index = (IndexExpression)this.index.copiedVersionFrom(alreadyDone);
        }
    }

    /**
     * INTERNAL:
     * Used for debug printing.
     */
    @Override
    public String descriptionOfNodeType() {
        return "Query Key";
    }

    /**
     * INTERNAL:
     */
    public void doQueryToManyRelationship() {
        shouldQueryToManyRelationship = true;
    }

    /**
     * INTERNAL:
     * Return any additional tables that belong to this expression
     * An example of how this method is used is to return any tables that belong to the map key
     * when this expression traverses a mapping that uses a Map
     */
    @Override
    public List<DatabaseTable> getAdditionalTables() {
        if (mapping != null && mapping.isCollectionMapping()){
            return ((CollectionMapping)mapping).getContainerPolicy().getAdditionalTablesForJoinQuery();
        }
        return null;
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
    protected DatabaseTable getAliasedTable() {
        DataExpression base = (DataExpression)this.baseExpression;

        DatabaseTable alias = base.aliasForTable(getField().getTable());
        if (alias == null) {
            return getField().getTable();
        } else {
            return alias;
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    public DatabaseField getField() {
        if (!isAttribute()) {
            return null;
        }
        QueryKey key = getQueryKeyOrNull();
        if ((key != null) && key.isDirectQueryKey()) {
            return ((DirectQueryKey)key).getField();
        }
        DatabaseMapping mapping = getMapping();
        if ((mapping == null) || mapping.getFields().isEmpty()) {
            return null;
        }
        return mapping.getFields().get(0);
    }

    /**
     * INTERNAL:
     * Return all the fields
     */
    @Override
    public Vector getFields() {
        if (isAttribute()) {
            Vector result = new Vector(1);
            DatabaseField field = getField();
            if (field != null) {
                result.addElement(field);
            }
            return result;
        } else {
            Vector result = new Vector();
            result.addAll(super.getFields());
            if ((this.mapping != null) && this.mapping.isCollectionMapping()){
                List<DatabaseField> fields = this.mapping.getContainerPolicy().getAdditionalFieldsForJoin((CollectionMapping)this.mapping);
                if (fields != null){
                    result.addAll(fields);
                }
            }
            return result;
        }
    }

    /**
     * INTERNAL:
     * Transform the object-level value into a database-level value
     */
    @Override
    public Object getFieldValue(Object objectValue, AbstractSession session) {
        DatabaseMapping mapping = getMapping();
        Object fieldValue = objectValue;
        if ((mapping != null) && (mapping.isAbstractDirectMapping() || mapping.isDirectCollectionMapping())) {
            // CR#3623207, check for IN Collection here not in mapping.
            if (objectValue instanceof Collection) {
                // This can actually be a collection for IN within expressions... however it would be better for expressions to handle this.
                Collection values = (Collection)objectValue;
                Vector fieldValues = new Vector(values.size());
                for (Iterator iterator = values.iterator(); iterator.hasNext();) {
                    Object value = iterator.next();
                    if (!(value instanceof Expression)){
                        value = getFieldValue(value, session);
                    }
                    fieldValues.add(value);
                }
                fieldValue = fieldValues;
            } else {
                if (mapping.isAbstractColumnMapping()) {
                    fieldValue = ((AbstractColumnMapping)mapping).getFieldValue(objectValue, session);
                } else if (mapping.isDirectCollectionMapping()) {
                    fieldValue = ((DirectCollectionMapping)mapping).getFieldValue(objectValue, session);                    
                }
            }
        } else if ((objectValue instanceof Collection) && (mapping.isForeignReferenceMapping())) {
            // Was an IN with a collection of objects, extract their ids.
            List ids = new ArrayList();
            for (Object object : ((Collection)objectValue)) {
                if ((mapping.getReferenceDescriptor() != null) && (mapping.getReferenceDescriptor().getJavaClass().isInstance(object))) {
                    Object id = mapping.getReferenceDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(object, session);
                    if (id instanceof CacheId) {
                        id = Arrays.asList(((CacheId)id).getPrimaryKey());
                    }
                    ids.add(id);
                } else {
                    ids.add(object);
                }
            }
            fieldValue = ids;
        }

        return fieldValue;
    }

    @Override
    public DatabaseMapping getMapping() {
        if (!hasMapping) {
            return null;
        }

        if (mapping == null) {
            mapping = super.getMapping();
            if (mapping == null) {
                hasMapping = false;
            }
        }
        return mapping;
    }

    public DatabaseMapping getMappingFromQueryKey() {
        QueryKey queryKey = getQueryKeyOrNull();
        if ((queryKey == null) || (!(queryKey instanceof DirectQueryKey))) {
            throw QueryException.cannotConformExpression();
        }
        mapping = queryKey.getDescriptor().getObjectBuilder().getMappingForField(((DirectQueryKey)queryKey).getField());
        if (mapping == null) {
            throw QueryException.cannotConformExpression();
        }
        return mapping;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * INTERNAL:
     * Returns nested attribute name or null
     */
    public String getNestedAttributeName() {
        if(getMapping() != null) {
            String attributeName = getMapping().getAttributeName();
            if(this.baseExpression.isExpressionBuilder()) {
                return attributeName;
            } else if (this.baseExpression.isQueryKeyExpression()) {
                String nestedAttributeName = ((QueryKeyExpression)this.baseExpression).getNestedAttributeName();
                if(nestedAttributeName == null) {
                    return null;
                } else {
                    return nestedAttributeName + '.' + attributeName;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public List<DatabaseTable> getOwnedTables() {
        if ((getMapping() != null) && getMapping().isNestedTableMapping()) {
            List<DatabaseTable> nestedTable = null;
            if (shouldQueryToManyRelationship()) {
                nestedTable = new ArrayList(super.getOwnedTables());
            } else {
                nestedTable = new ArrayList(1);
            }

            nestedTable.add(new NestedTable(this));
            return nestedTable;
        }
        if ((getMapping() != null) && (getMapping().isReferenceMapping() || getMapping().isStructureMapping())) {
            return null;
        }

        return super.getOwnedTables();

    }

    @Override
    public QueryKey getQueryKeyOrNull() {
        if (!hasQueryKey) {
            return null;
        }

        // Oct 19, 2000 JED
        // Added try/catch. This was throwing a NPE in the following case
        // expresssionBuilder.get("firstName").get("bob")
        //moved by Gordon Yorke to cover validate and normalize
        if (getContainingDescriptor() == null) {
            throw QueryException.invalidQueryKeyInExpression(getName());
        }
        if (queryKey == null) {
            queryKey = getContainingDescriptor().getQueryKeyNamed(getName());
            if (queryKey == null) {
                hasQueryKey = false;
            }
        }
        return queryKey;

    }

    /*
     * PUBLIC:
     * Index method could be applied to QueryKeyExpression corresponding to CollectionMapping
     * that has non-null listOrderField (the field holding the index values).
     * <p>Example:
     * <pre><blockquote>
     *    ReportQuery query = new ReportQuery();
     *    query.setReferenceClass(Employee.class);
     *    ExpressionBuilder builder = query.getExpressionBuilder();
     *    Expression firstNameJohn = builder.get("firstName").equal("John");
     *    Expression anyOfProjects = builder.anyOf("projects");
     *    Expression exp = firstNameJohn.and(anyOfProjects.index().between(2, 4));
     *    query.setSelectionCriteria(exp);
     *    query.addAttribute("projects", anyOfProjects);
     *       
     *    SELECT DISTINCT t0.PROJ_ID, t0.PROJ_TYPE, t0.DESCRIP, t0.PROJ_NAME, t0.LEADER_ID, t0.VERSION, t1.PROJ_ID, t1.BUDGET, t1.MILESTONE 
     *    FROM OL_PROJ_EMP t4, OL_SALARY t3, OL_EMPLOYEE t2, OL_LPROJECT t1, OL_PROJECT t0 
     *    WHERE ((((t2.F_NAME = 'John') AND (t4.PROJ_ORDER BETWEEN 2 AND 4)) AND (t3.OWNER_EMP_ID = t2.EMP_ID)) AND
     *    (((t4.EMP_ID = t2.EMP_ID) AND (t0.PROJ_ID = t4.PROJ_ID)) AND (t1.PROJ_ID (+) = t0.PROJ_ID)))
     * </blockquote></pre>
     */
    @Override
    public Expression index() {
        if(index == null) {
            index = new IndexExpression(this);
        }
        return index;
    }
    
    /**
     * INTERNAL:
     * Alias the database field for our current environment
     */
    protected void initializeAliasedField() {
        DatabaseField tempField = getField().clone();
        DatabaseTable aliasedTable = getAliasedTable();

        //  Put in a special check here so that if the aliasing does nothing we don't cache the
        // result because it's invalid. This saves us from caching premature data if e.g. debugging
        // causes us to print too early"
        //	if (aliasedTable.equals(getField().getTable())) {
        //		return;
        //	} else {
        aliasedField = tempField;
        aliasedField.setTable(aliasedTable);
        //	}
    }

    /**
     * INTERNAL:
     * Return if the expression is for a direct mapped attribute.
     */
    @Override
    public boolean isAttribute() {
        if (isAttributeExpression == null) {
            if (getSession() == null) {
                // We can't tell, so say no.
                return false;
            }
            QueryKey queryKey = getQueryKeyOrNull();
            if (queryKey != null) {
                isAttributeExpression = Boolean.valueOf(queryKey.isDirectQueryKey());
            } else {
                DatabaseMapping mapping = getMapping();
                if (mapping != null) {
                    if (mapping.isVariableOneToOneMapping()) {
                        throw QueryException.cannotQueryAcrossAVariableOneToOneMapping(mapping, mapping.getDescriptor());
                    } else {
                        isAttributeExpression = Boolean.valueOf(mapping.isDirectToFieldMapping());
                    }
                } else {
                    isAttributeExpression = Boolean.FALSE;
                }
            }
        }
        return isAttributeExpression.booleanValue();
    }

    @Override
    public boolean isQueryKeyExpression() {
        return true;
    }

    /*
     * INTERNAL:
     * If this query key represents a foreign reference answer the
     * base expression -> foreign reference join criteria.
     */
    public Expression mappingCriteria(Expression base) {
        Expression selectionCriteria;

        // First look for a query key, then a mapping
        if (getQueryKeyOrNull() == null) {
            if ((getMapping() == null) || (!getMapping().isForeignReferenceMapping())) {
                return null;
            } else {
                // The join criteria is now twisted by the mappings.
                selectionCriteria = ((ForeignReferenceMapping)getMapping()).getJoinCriteria(this, base);
            }
        } else {
            if (!getQueryKeyOrNull().isForeignReferenceQueryKey()) {
                return null;
            } else {
                selectionCriteria = ((ForeignReferenceQueryKey)getQueryKeyOrNull()).getJoinCriteria();
                selectionCriteria = this.baseExpression.twist(selectionCriteria, base);
            }
        }

        if (shouldUseOuterJoin() && getSession().getPlatform().shouldPrintOuterJoinInWhereClause()) {
            selectionCriteria = selectionCriteria.convertToUseOuterJoin();
        }

        return selectionCriteria;
    }

    /**
     * INTERNAL:
     * Normalize the expression into a printable structure.
     * Any joins must be added to form a new root.
     */
    @Override
    public Expression normalize(ExpressionNormalizer normalizer) {
        return normalize(normalizer, this, null);
    }
    
    /**
     * INTERNAL:
     * Check if new expression need to be created for sub queries and re-normalized.
     */
    protected Expression checkJoinForSubSelectWithParent(ExpressionNormalizer normalizer, Expression base, List<Expression> foreignKeyJoinPointer) {
        SQLSelectStatement statement = normalizer.getStatement();
        if(!isClonedForSubQuery && statement.isSubSelect() && statement.getParentStatement().getBuilder().equals(getBuilder())) {
            if (baseExpression.isQueryKeyExpression()) {
                QueryKeyExpression baseQueryKeyExpression = (QueryKeyExpression) baseExpression;
                DatabaseMapping mapping = baseQueryKeyExpression.getMapping();
                if (mapping != null && mapping.isOneToOneMapping()) {
                    if (statement.getOptimizedClonedExpressions().containsKey(this)) {
                        return statement.getOptimizedClonedExpressions().get(this);
                    }
                    
                    QueryKeyExpression clonedBaseExpression = null;
                    if (baseQueryKeyExpression.hasBeenNormalized()) {
                        // Call normalize again to get same expression.
                        clonedBaseExpression = (QueryKeyExpression) baseQueryKeyExpression.normalize(normalizer);
                    }
                    
                    if (clonedBaseExpression == null && baseQueryKeyExpression.getBaseExpression().isQueryKeyExpression()) {
                        DatabaseMapping basebaseExprMapping = ((QueryKeyExpression)baseQueryKeyExpression.getBaseExpression()).getMapping();
                        if (basebaseExprMapping != null && basebaseExprMapping.isOneToOneMapping()) {
                            // Let base expression normalization re-create base base expression and normalize it if needed.
                            clonedBaseExpression = (QueryKeyExpression) baseQueryKeyExpression.normalize(normalizer);
                        }
                    }
                    
                    if (clonedBaseExpression == null) {
                        // Clone base expression & normalize
                        clonedBaseExpression = new QueryKeyExpression(baseQueryKeyExpression.getName(), baseQueryKeyExpression.getBaseExpression());
                        clonedBaseExpression.shouldQueryToManyRelationship = baseQueryKeyExpression.shouldQueryToManyRelationship;
                        clonedBaseExpression.shouldUseOuterJoin = baseQueryKeyExpression.shouldUseOuterJoin;
                        clonedBaseExpression.hasQueryKey = baseQueryKeyExpression.hasQueryKey;
                        clonedBaseExpression.hasMapping = baseQueryKeyExpression.hasMapping;
                        clonedBaseExpression.isAttributeExpression = baseQueryKeyExpression.isAttributeExpression;
                        clonedBaseExpression.isClonedForSubQuery = true;
                        
                        clonedBaseExpression = (QueryKeyExpression) clonedBaseExpression.normalize(normalizer);
                        statement.addOptimizedClonedExpressions(baseQueryKeyExpression, clonedBaseExpression);
                    }
                    
                    // Clone expression, normalize & return.
                    QueryKeyExpression clonedExpression = new QueryKeyExpression(name, clonedBaseExpression);
                    clonedExpression.shouldQueryToManyRelationship = this.shouldQueryToManyRelationship;
                    clonedExpression.shouldUseOuterJoin = this.shouldUseOuterJoin;
                    clonedExpression.hasQueryKey = this.hasQueryKey;
                    clonedExpression.hasMapping = this.hasMapping;
                    clonedExpression.isAttributeExpression = this.isAttributeExpression;
                    clonedExpression.isClonedForSubQuery = true;
                    
                    if (base == this) {
                        clonedExpression = (QueryKeyExpression) clonedExpression.normalize(normalizer, clonedExpression, foreignKeyJoinPointer);
                    } else {
                        // Caller invoked overloaded method with different base, RelationExpression in this case.
                        clonedExpression = (QueryKeyExpression) clonedExpression.normalize(normalizer, base, foreignKeyJoinPointer);
                    }
                    statement.addOptimizedClonedExpressions(this, clonedExpression);
                    return clonedExpression;
                }
            }
        }
        
        return null;
    }

    /**
     * INTERNAL:
     * For CR#2456 if this is part of an objExp.equal(objExp), do not need to add
     * additional expressions to normalizer both times, and the foreign key join
     * replaces the equal expression.
     */
    public Expression normalize(ExpressionNormalizer normalizer, Expression base, List<Expression> foreignKeyJoinPointer) {
        if (this.hasBeenNormalized) {
            return this;
        }
        // Bug 397619 - It should only be normalized by parent.
        // If subselect & not normalized, always clone and normalize
        // if it has parent builder.
        Expression clonedExpression = checkJoinForSubSelectWithParent(normalizer, base, foreignKeyJoinPointer);
        if (clonedExpression != null) {
            return clonedExpression;
        }
        super.normalize(normalizer);
        DatabaseMapping mapping = getMapping();
        SQLSelectStatement statement = normalizer.getStatement();
        if ((mapping != null) && mapping.isDirectToXMLTypeMapping()) {
            statement.setRequiresAliases(true);
        }

        // Check if any joins need to be added.
        if (isAttribute()) {
            return this;
        }

        ReadQuery query = normalizer.getStatement().getQuery();
        // Record any class used in a join to invlaidate query results cache.
        if ((query != null) && query.shouldCacheQueryResults()) {
            if ((mapping != null) && (mapping.getReferenceDescriptor().getJavaClass() != null)) {
                query.getQueryResultsCachePolicy().getInvalidationClasses().add(mapping.getReferenceDescriptor().getJavaClass());
            } else {
                QueryKey queryKey = getQueryKeyOrNull();
                if ((queryKey != null) && queryKey.isForeignReferenceQueryKey()) {
                    query.getQueryResultsCachePolicy().getInvalidationClasses().add(((ForeignReferenceQueryKey)queryKey).getReferenceClass());
                }
            }
        }

        // If the mapping is 'ref' or 'structure', no join needed.
        if ((mapping != null) && (mapping.isReferenceMapping() || mapping.isStructureMapping())) {
            statement.setRequiresAliases(true);
            return this;
        }

        // Compute if a distinct is required during normalization.
        if (shouldQueryToManyRelationship() && (!statement.isDistinctComputed()) && (!statement.isAggregateSelect())) {
            statement.useDistinct();
        }

        // Turn off DISTINCT if nestedTableMapping is used (not supported by Oracle 8.1.5).
        if ((mapping != null) && mapping.isNestedTableMapping()) {
            // There are two types of nested tables, one used by clients, one used by mappings, do nothing in the mapping case.
            if (!shouldQueryToManyRelationship()) {
                return this;
            }
            statement.dontUseDistinct();
        }

        // Normalize the ON clause if present.  Need to use rebuild, not twist as parameters are real parameters.
        if (this.onClause != null) {
            this.onClause = this.onClause.normalize(normalizer);
        }
        
        Expression mappingExpression = mappingCriteria(base);
        if (mappingExpression != null) {
            mappingExpression = mappingExpression.normalize(normalizer);
        }
        if (mappingExpression != null) {
            // If the join was an outer join we must not add the join criteria to the where clause,
            // if the platform prints the join in the from clause.
            if (shouldUseOuterJoin() && (getSession().getPlatform().isInformixOuterJoin())) {
                statement.getOuterJoinExpressions().add(this);
                statement.getOuterJoinedMappingCriteria().add(mappingExpression);
                normalizer.addAdditionalExpression(mappingExpression.and(additionalExpressionCriteria()));
                return this;
            } else if ((shouldUseOuterJoin() && (!getSession().getPlatform().shouldPrintOuterJoinInWhereClause()))
                    || (!getSession().getPlatform().shouldPrintInnerJoinInWhereClause())) {
                statement.getOuterJoinExpressions().add(this);
                statement.getOuterJoinedMappingCriteria().add(mappingExpression);
                statement.getOuterJoinedAdditionalJoinCriteria().add(additionalExpressionCriteriaMap());
                statement.getDescriptorsForMultitableInheritanceOnly().add(null);
                if ((getDescriptor() != null) && (getDescriptor().getHistoryPolicy() != null)) {
                    Expression historyOnClause = getDescriptor().getHistoryPolicy().additionalHistoryExpression(this, this, 0); 
                    if (getOnClause() != null) {
                        setOnClause(getOnClause().and(historyOnClause));
                    } else {
                        setOnClause(historyOnClause);
                    }
                }
                return this;
            } else if (isUsingOuterJoinForMultitableInheritance() && (!getSession().getPlatform().shouldPrintOuterJoinInWhereClause())) {
                statement.getOuterJoinExpressions().add(null);
                statement.getOuterJoinedMappingCriteria().add(null);
                statement.getOuterJoinedAdditionalJoinCriteria().add(additionalExpressionCriteriaMap());
                statement.getDescriptorsForMultitableInheritanceOnly().add(mapping.getReferenceDescriptor());
                // fall through to the main case
            }
            
            // This must be added even if outer. Actually it should be converted to use a right outer join, but that gets complex
            // so we do not support this current which is a limitation in some cases.
            if (foreignKeyJoinPointer != null) {
                // If this expression is right side of an objExp.equal(objExp), one
                // need not add additionalExpressionCriteria twice.
                // Also the join will replace the original objExp.equal(objExp).
                // For CR#2456.
                foreignKeyJoinPointer.add(mappingExpression.and(this.onClause));
            } else {
                normalizer.addAdditionalExpression(mappingExpression.and(additionalExpressionCriteria()).and(this.onClause));
            }
        }
        // For bug 2900974 special code for DirectCollectionMappings moved to printSQL.
        return this;
    }

    /**
     * INTERNAL:
     * Print SQL onto the stream, using the ExpressionPrinter for context
     */
    @Override
    public void printSQL(ExpressionSQLPrinter printer) {
        if (isAttribute()) {
            printer.printField(getAliasedField());
        }

        // If the mapping is a direct collection then this falls into a gray area.
        // It must be treated as an attribute at this moment for it has a direct field.
        // However it is not an attribute in the sense that it also represents a foreign
        // reference and a mapping criteria has been added.
        // For bug 2900974 these are now handled as non-attributes during normalize but
        // as attributes when printing SQL.
        //
        if ((!isAttribute()) && (getMapping() != null) && getMapping().isDirectCollectionMapping()) {
            DirectCollectionMapping directCollectionMapping = (DirectCollectionMapping)getMapping();

            // The aliased table comes for free as it was a required part of the join criteria.
            TableExpression table = (TableExpression)getTable(directCollectionMapping.getReferenceTable());
            DatabaseTable aliasedTable = table.aliasForTable(table.getTable());
            DatabaseField aliasedField = directCollectionMapping.getDirectField().clone();
            aliasedField.setTable(aliasedTable);
            printer.printField(aliasedField);
        }

        if ((getMapping() != null) && getMapping().isNestedTableMapping()) {
            DatabaseTable tableAlias = aliasForTable(new NestedTable(this));
            printer.printString(tableAlias.getName());
        }
    }

    /**
     * INTERNAL:
     * Print java for project class generation
     */
    @Override
    public void printJava(ExpressionJavaPrinter printer) {
        this.baseExpression.printJava(printer);
        if (!shouldUseOuterJoin()) {
            if (!shouldQueryToManyRelationship()) {
                printer.printString(".get(");
            } else {
                printer.printString(".anyOf(");
            }
        } else {
            if (!shouldQueryToManyRelationship()) {
                printer.printString(".getAllowingNull(");
            } else {
                printer.printString(".anyOfAllowingNone(");
            }
        }
        printer.printString("\"" + getName() + "\")");
        if (castClass != null){
            printer.printString(".cast(" + castClass.getName() + ".class)");
        }
    }

    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    @Override
    public Expression rebuildOn(Expression newBase) {
        Expression newLocalBase = this.baseExpression.rebuildOn(newBase);
        QueryKeyExpression result = null;

        // For bug 3096634 rebuild outer joins correctly from the start.
        if (shouldUseOuterJoin) {
            result = (QueryKeyExpression)newLocalBase.getAllowingNull(getName());
        } else {
            result = (QueryKeyExpression)newLocalBase.get(getName());
        }
        if (shouldQueryToManyRelationship) {
            result.doQueryToManyRelationship();
        }
        result.setSelectIfOrderedBy(selectIfOrderedBy());
        if (castClass != null){
            result.setCastClass(castClass);
        }
        return result;
    }

    /**
     * INTERNAL:
     * A special version of rebuildOn where the newBase need not be a new
     * ExpressionBuilder but any expression.
     * <p>
     * For nested joined attributes, the joined attribute query must have
     * its joined attributes rebuilt relative to it.
     */
    public Expression rebuildOn(Expression oldBase, Expression newBase) {
        if (this == oldBase) {
            return newBase;
        }
        Expression newLocalBase = ((QueryKeyExpression)this.baseExpression).rebuildOn(oldBase, newBase);
        QueryKeyExpression result = null;

        // For bug 3096634 rebuild outer joins correctly from the start.
        if (shouldUseOuterJoin) {
            result = (QueryKeyExpression)newLocalBase.getAllowingNull(getName());
        } else {
            result = (QueryKeyExpression)newLocalBase.get(getName());
        }
        if (shouldQueryToManyRelationship) {
            result.doQueryToManyRelationship();
        }
        result.setSelectIfOrderedBy(selectIfOrderedBy());
        return result;
    }

    /**
     * Reset cached information here so that we can be sure we're accurate.
     */
    @Override
    protected void resetCache() {
        hasMapping = true;
        mapping = null;
        hasQueryKey = true;
        queryKey = null;
    }

    public boolean shouldQueryToManyRelationship() {
        return shouldQueryToManyRelationship;
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
        if (oldBase == null || this.baseExpression == oldBase) {
            Expression twistedBase = this.baseExpression.twistedForBaseAndContext(newBase, context, oldBase);
            QueryKeyExpression result = (QueryKeyExpression)twistedBase.get(getName());
            if (shouldUseOuterJoin) {
                result.doUseOuterJoin();
            }
            if (shouldQueryToManyRelationship) {
                result.doQueryToManyRelationship();
            }
            return result;
        }
        
        return this;
    }

    /**
     * Do any required validation for this node. Throw an exception if it's incorrect.
     */
    @Override
    public void validateNode() {
        QueryKey queryKey = getQueryKeyOrNull();
        DatabaseMapping mapping = getMapping();
        
        if ((queryKey == null) && (mapping == null)) {
            throw QueryException.invalidQueryKeyInExpression(getName());
        }


        Object theOneThatsNotNull = null;
        boolean qkIsToMany = false;
        if (queryKey != null) {
            theOneThatsNotNull = queryKey;
            qkIsToMany = queryKey.isManyToManyQueryKey() || queryKey.isOneToManyQueryKey();
        }
        boolean isNestedMapping = false;
        if (mapping != null) {
            // Bug 2847621 - Add Aggregate Collection to the list of valid items for outer join.
            if (this.shouldUseOuterJoin && (!(mapping.isOneToOneMapping() || mapping.isOneToManyMapping() || mapping.isManyToManyMapping() || mapping.isAggregateCollectionMapping() || mapping.isDirectCollectionMapping()))) {
                throw QueryException.outerJoinIsOnlyValidForOneToOneMappings(mapping);
            }
            qkIsToMany = mapping.isCollectionMapping();
            if (this.index != null) {
                if (qkIsToMany) {
                    CollectionMapping collectionMapping = (CollectionMapping)mapping;
                    if(collectionMapping.getListOrderField() != null) {
                        this.index.setField(collectionMapping.getListOrderField());
                        addDerivedField(this.index);
                    } else {
                        throw QueryException.indexRequiresCollectionMappingWithListOrderField(this, collectionMapping);
                    }
                } else {
                    throw QueryException.indexRequiresCollectionMappingWithListOrderField(this, mapping);
                }
            }
            isNestedMapping = mapping.isNestedTableMapping();
            theOneThatsNotNull = mapping;
        } else {
            if (this.index != null) {
                throw QueryException.indexRequiresCollectionMappingWithListOrderField(this, null);
            }
        }
        if ((!shouldQueryToManyRelationship()) && qkIsToMany && (!isNestedMapping)) {
            throw QueryException.invalidUseOfToManyQueryKeyInExpression(theOneThatsNotNull);
        }
        if (shouldQueryToManyRelationship() && !qkIsToMany) {
            throw QueryException.invalidUseOfAnyOfInExpression(theOneThatsNotNull);
        }
    }

    /**
     * INTERNAL:
     * Return the value for in memory comparison.
     * This is only valid for valueable expressions.
     */
    @Override
    public Object valueFromObject(Object object, AbstractSession session, AbstractRecord translationRow, int valueHolderPolicy, boolean isObjectUnregistered) {
        // The expression may be across a relationship, in which case it must be traversed.
        if ((!this.baseExpression.isExpressionBuilder()) && this.baseExpression.isQueryKeyExpression()) {
            object = this.baseExpression.valueFromObject(object, session, translationRow, valueHolderPolicy, isObjectUnregistered);

            // toDo: Null means the join filters out the row, returning null is not correct if an inner join,
            // outer/inner joins need to be fixed to filter correctly.
            if (object == null) {
                return null;
            }

            // If from an anyof the object will be a collection of values,
            // A new vector must union the object values and the values extracted from it.
            if (object instanceof Vector) {
                Vector comparisonVector = new Vector(((Vector)object).size() + 2);
                for (Enumeration valuesToIterate = ((Vector)object).elements();
                         valuesToIterate.hasMoreElements();) {
                    Object vectorObject = valuesToIterate.nextElement();
                    if (vectorObject == null) {
                        comparisonVector.addElement(vectorObject);
                    } else {
                        Object valueOrValues = valuesFromCollection(vectorObject, session, valueHolderPolicy, isObjectUnregistered);

                        // If a collection of values were extracted union them.
                        if (valueOrValues instanceof Vector) {
                            for (Enumeration nestedValuesToIterate = ((Vector)valueOrValues).elements();
                                     nestedValuesToIterate.hasMoreElements();) {
                                comparisonVector.addElement(nestedValuesToIterate.nextElement());
                            }
                        } else {
                            comparisonVector.addElement(valueOrValues);
                        }
                    }
                }
                return comparisonVector;
            }
        }
        return valuesFromCollection(object, session, valueHolderPolicy, isObjectUnregistered);
    }

    /**
     * INTERNAL
     * This method iterates through a collection and gets the values from the objects to conform in an in-memory query.
     */
    public Object valuesFromCollection(Object object, AbstractSession session, int valueHolderPolicy, boolean isObjectUnregistered) {
        // in case the mapping is null - this can happen if a query key is being used
        // In this case, check for the query key and find it's mapping.
        boolean readMappingFromQueryKey = false;
        if (getMapping() == null) {
            getMappingFromQueryKey();
            readMappingFromQueryKey = true;
        }

        // For bug 2780817 get the mapping directly from the object.  In EJB 2.0 
        // inheritance, each child must override mappings defined in an abstract 
        // class with its own.
        DatabaseMapping mapping = this.mapping;
        ClassDescriptor descriptor = mapping.getDescriptor();
        if (descriptor.hasInheritance() && (descriptor.getJavaClass() != object.getClass())) {
            descriptor = descriptor.getInheritancePolicy().getDescriptor(object.getClass());
            mapping = descriptor.getObjectBuilder().getMappingForAttributeName(mapping.getAttributeName());
        }

        //fetch group support
        if (descriptor.hasFetchGroupManager()) {
            FetchGroupManager fetchGroupManager = descriptor.getFetchGroupManager();
            if (fetchGroupManager.isPartialObject(object) && (!fetchGroupManager.isAttributeFetched(object, mapping.getAttributeName()))) {
                //the conforming attribute is not fetched, simply throw exception
                throw QueryException.cannotConformUnfetchedAttribute(mapping.getAttributeName());
            }
        }

        if (mapping.isAbstractColumnMapping()) {
            return ((AbstractColumnMapping)mapping).valueFromObject(object, mapping.getField(), session);
        } else if (mapping.isForeignReferenceMapping()) {
            //CR 3677 integration of a ValueHolderPolicy
            Object valueFromMapping = mapping.getAttributeValueFromObject(object);
            if (!((ForeignReferenceMapping)mapping).getIndirectionPolicy().objectIsInstantiated(valueFromMapping)) {
                if (valueHolderPolicy  != InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION) {
                    //If the client wishes us to trigger the indirection then we should do so,
                    //Other wise throw the exception
                    throw QueryException.mustInstantiateValueholders();// you should instantiate the valueholder for this to work
                }

                // maybe we should throw this exception from the start, to save time
            }
            Object valueToIterate = mapping.getRealAttributeValueFromObject(object, session);
            UnitOfWorkImpl uow = isObjectUnregistered ? (UnitOfWorkImpl)session : null;

            // First check that object in fact is unregistered.
            // toDo: ?? Why is this commented out? Why are we supporting the unregistered thing at all?
            // Does not seem to be any public API for this, nor every used internally?
            //if (isObjectUnregistered) {
            //	isObjectUnregistered = !uow.getCloneMapping().containsKey(object);
            //}
            if (mapping.isCollectionMapping() && (valueToIterate != null)) {
                // For bug 2766379 must use the correct version of vectorFor to
                // unwrap the result same time.
                valueToIterate = mapping.getContainerPolicy().vectorFor(valueToIterate, session);

                // toDo: If the value is empty, need to support correct inner/outer join filtering symantics.
                // For CR 2612601, try to partially replace the result with already
                // registered objects.
                if (isObjectUnregistered && (uow.getCloneMapping().get(object) == null)) {
                    Vector objectValues = (Vector)valueToIterate;
                    for (int i = 0; i < objectValues.size(); i++) {
                        Object original = objectValues.elementAt(i);
                        Object clone = uow.getIdentityMapAccessorInstance().getIdentityMapManager().getFromIdentityMap(original);
                        if (clone != null) {
                            objectValues.setElementAt(clone, i);
                        }
                    }
                }

                // For CR 2612601, conforming without registering, a query could be
                // bob.get("address").get("city").equal("Ottawa"); where the address
                // has been registered and modified in the UOW, but bob has not.  Thus
                // even though bob does not point to the modified address now, it will
                // as soon as it is registered, so should point to it here.
            } else if (isObjectUnregistered && (uow.getCloneMapping().get(object) == null)) {
                Object clone = uow.getIdentityMapAccessorInstance().getIdentityMapManager().getFromIdentityMap(valueToIterate);
                if (clone != null) {
                    valueToIterate = clone;
                }
            }
            return valueToIterate;
        } else if (mapping.isAggregateMapping()) {
            Object aggregateValue = mapping.getAttributeValueFromObject(object);
            // Bug 3995468 - if this query key is to a mapping in an aggregate object, get the object from actual mapping rather than the aggregate mapping
            while (readMappingFromQueryKey && mapping.isAggregateObjectMapping() && !((AggregateObjectMapping)mapping).getReferenceClass().equals(queryKey.getDescriptor().getJavaClass())) {
                mapping = mapping.getReferenceDescriptor().getObjectBuilder().getMappingForField(((DirectQueryKey)queryKey).getField());
                aggregateValue = mapping.getRealAttributeValueFromObject(aggregateValue, session);
            }
            return aggregateValue;
        } else {
            throw QueryException.cannotConformExpression();
        }
    }
    
    /**
     * INTERNAL:
     * Lookup the descriptor for this item by traversing its expression recursively.
     */
    @Override
    public ClassDescriptor getLeafDescriptor(DatabaseQuery query, ClassDescriptor rootDescriptor, AbstractSession session) {
        Expression baseExpression = getBaseExpression();
        ClassDescriptor baseDescriptor = baseExpression.getLeafDescriptor(query, rootDescriptor, session);
        
        if (isMapEntryExpression()) {
            // get the expression that owns the mapping for the table entry
            Expression owningExpression = ((QueryKeyExpression)baseExpression).getBaseExpression();
            ClassDescriptor owningDescriptor = owningExpression.getLeafDescriptor(query, rootDescriptor, session);
            
            // Get the mapping that owns the table
            CollectionMapping mapping = (CollectionMapping)owningDescriptor.getObjectBuilder().getMappingForAttributeName(baseExpression.getName());
            
            return ((MapContainerPolicy)mapping.getContainerPolicy()).getDescriptorForMapKey();
        }

        ClassDescriptor descriptor = null;
        String attributeName = getName();

        DatabaseMapping mapping = baseDescriptor.getObjectBuilder().getMappingForAttributeName(attributeName);

        if (mapping == null) {
            QueryKey queryKey = baseDescriptor.getQueryKeyNamed(attributeName);
            if (queryKey != null) {
                if (queryKey.isForeignReferenceQueryKey()) {
                    descriptor = session.getDescriptor(((ForeignReferenceQueryKey)queryKey).getReferenceClass());
                } else { // if (queryKey.isDirectQueryKey())
                    descriptor = queryKey.getDescriptor();
                }
            }
            if (descriptor == null) {
                throw QueryException.invalidExpressionForQueryItem(this, query);
            }
        } else if (mapping.isAggregateMapping()) {
            descriptor = ((AggregateMapping)mapping).getReferenceDescriptor();
        } else if (mapping.isForeignReferenceMapping()) {
            descriptor = ((ForeignReferenceMapping)mapping).getReferenceDescriptor();
        }
        return descriptor;
    }
    
    /**
     * INTERNAL:
     * Lookup the mapping for this item by traversing its expression recursively.
     * If an aggregate of foreign mapping is found it is traversed.
     */
    @Override
    public DatabaseMapping getLeafMapping(DatabaseQuery query, ClassDescriptor rootDescriptor, AbstractSession session) {
        if (isMapEntryExpression()){
            MapEntryExpression mapEntryExpression = (MapEntryExpression)this;
            
            // get the expression that we want the table entry for
            QueryKeyExpression baseExpression = (QueryKeyExpression)mapEntryExpression.getBaseExpression();
            
            // get the expression that owns the mapping for the table entry
            Expression owningExpression = baseExpression.getBaseExpression();
            ClassDescriptor owningDescriptor = owningExpression.getLeafDescriptor(query, rootDescriptor, session);
            
            // Get the mapping that owns the table
            CollectionMapping mapping = (CollectionMapping)owningDescriptor.getObjectBuilder().getMappingForAttributeName(baseExpression.getName());
            
            if (mapEntryExpression.shouldReturnMapEntry()) {
                return mapping;
            }
            if (mapping.getContainerPolicy().isMappedKeyMapPolicy()){
                MappedKeyMapContainerPolicy policy = (MappedKeyMapContainerPolicy)mapping.getContainerPolicy();
                return (DatabaseMapping)policy.getKeyMapping();
            }
            return mapping;
        }

        Expression baseExpression = getBaseExpression();

        ClassDescriptor descriptor = baseExpression.getLeafDescriptor(query, rootDescriptor, session);
        if (descriptor == null){
            return null;
        }
        return descriptor.getObjectBuilder().getMappingForAttributeName(getName());
    }
    
    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    @Override
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write(getName());
        if (castClass != null){
            writer.write(" (" + castClass.getName() + ") ");
        }
        writer.write(tableAliasesDescription());
    }
    
    /**
     * INTERNAL:
     * Indicates whether this expression corresponds to DirectCollection.
     */
    @Override
    public boolean isDirectCollection() {
        if(getMapping() != null) {
            return getMapping().isDirectCollectionMapping();
        } else {
            if(getQueryKeyOrNull() != null) {
                return this.queryKey.isDirectCollectionQueryKey();
            } else {
                return false;
            }
        }
    }

    /**
     * INTERNAL:
     * Indicates whether this expression corresponds to OneToOne.
     */
    public boolean isOneToOne() {
        if(getMapping() != null) {
            return getMapping().isOneToOneMapping();
        } else {
            if(getQueryKeyOrNull() != null) {
                return this.queryKey.isOneToOneQueryKey();
            } else {
                return false;
            }
        }
    }

    /**
     * INTERNAL:
     * Indicates whether this expression corresponds to OneToMany.
     */
    public boolean isOneToMany() {
        if(getMapping() != null) {
            return getMapping().isOneToManyMapping();
        } else {
            if(getQueryKeyOrNull() != null) {
                return this.queryKey.isOneToManyQueryKey();
            } else {
                return false;
            }
        }
    }

    /**
     * INTERNAL:
     * Indicates whether this expression corresponds to ManyToMany.
     */
    public boolean isManyToMany() {
        if(getMapping() != null) {
            return getMapping().isManyToManyMapping();
        } else {
            if(getQueryKeyOrNull() != null) {
                return this.queryKey.isManyToManyQueryKey();
            } else {
                return false;
            }
        }
    }

    /**
     * INTERNAL:
     * Return if the expression if for a map key mapping where the key is a OneToOne.
     */
    public boolean isMapKeyObjectRelationship() {
        if (getMapping() != null) {
            return getMapping().isCollectionMapping() && ((CollectionMapping)getMapping()).isMapKeyObjectRelationship();
        } else {
            return false;
        }
    }

    /**
     * INTERNAL:
     * Return if descriptor for the map key mapping where the key is a OneToOne.
     */
    public ClassDescriptor getMapKeyDescriptor() {
        return ((CollectionMapping)getMapping()).getContainerPolicy().getDescriptorForMapKey();
    }
    
    /**
     * Calculate the reference table for based on the various QueryKeyExpression
     * usages (join query keys, custom defined query keys, or query keys for
     * mappings).
     * 
     * Called from {@link SQLSelectStatement#appendFromClauseForOuterJoin}.
     * 
     * @return DatabaseTable
     */    
    public DatabaseTable getReferenceTable() {
        if(getMapping() != null) {
            if (getMapping().isDirectCollectionMapping()) {
                return ((DirectCollectionMapping)getMapping()).getReferenceTable();
            } else {
                return getMapping().getReferenceDescriptor().getTables().firstElement();
            }
        } else {
            return ((ForeignReferenceQueryKey)getQueryKeyOrNull()).getReferenceTable(getDescriptor());
        }
    }

    /**
     * Calculate the source table for based on the various QueryKeyExpression
     * usages (join query keys, custom defined query keys, or query keys for
     * mappings).
     * 
     * Called from {@link SQLSelectStatement#appendFromClauseForOuterJoin}.
     * 
     * @return DatabaseTable
     */    
    public DatabaseTable getSourceTable() {
        if (getBaseExpression().isExpressionBuilder() && getBuilder().hasViewTable()) {
            return getBuilder().getViewTable();
        }
        if (getMapping() != null) {
            // Grab the source table from the mapping not just the first table 
            // from the descriptor. In an joined inheritance hierarchy, the
            // fk used in the outer join may be from a subclasses's table.
            if (getMapping().isObjectReferenceMapping() && ((ObjectReferenceMapping) getMapping()).isForeignKeyRelationship()) {
                 return getMapping().getFields().firstElement().getTable();
            } else {
                return ((ObjectExpression)this.baseExpression).getDescriptor().getTables().firstElement();    
            }
        } else {
            return ((ForeignReferenceQueryKey)getQueryKeyOrNull()).getSourceTable();
        }
    }

    /**
     * Calculate the relation table for based on the various QueryKeyExpression
     * usages (join query keys, custom defined query keys, or query keys for
     * mappings).
     * 
     * Called from {@link SQLSelectStatement#appendFromClauseForOuterJoin}.
     * 
     * @return DatabaseTable
     */    
    public DatabaseTable getRelationTable() {
        if(getMapping() != null) {
            if(getMapping().isManyToManyMapping()) {
                return ((ManyToManyMapping)getMapping()).getRelationTable();
            } else if(getMapping().isOneToOneMapping()) {
                return ((OneToOneMapping)getMapping()).getRelationTable();
            }
        } else {
            if(getQueryKeyOrNull().isForeignReferenceQueryKey()) {
                return ((ForeignReferenceQueryKey)getQueryKeyOrNull()).getRelationTable(getDescriptor());
            }
        }
        return null;
    }
}
