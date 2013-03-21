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
package org.eclipse.persistence.internal.queries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.BaseExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.expressions.ObjectExpression;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.expressions.ForUpdateOfClause;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.NonSynchronizedSubVector;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * <p><b>Purpose</b>:
 * A common class to be used by ObjectLevelReadQueries and ReportItems.  This
 * Class will be used to store Joined Attribute Expressions.  It will also
 * store the indexes for object construction.
 *
 * @author Gordon Yorke
 * @since EJB3.0 RI
 */

public class JoinedAttributeManager implements Cloneable, Serializable {
    
    /** Stores AggregateObjectMapping expressions used within local join expressions */
    protected transient List<DatabaseMapping> joinedAggregateMappings = new ArrayList(0);
    
    /** indexed list of mappings corresponding to */
    protected transient List<DatabaseMapping> joinedAttributeMappings = new ArrayList(0);

    /** Stores the joined attributes added through the query */
    protected List<Expression> joinedAttributeExpressions;
    
    /** Stores the joined attributes as specified in the descriptor */
    protected List<Expression> joinedMappingExpressions;
    
    /** PERF: Cache the local joined attribute expressions. */
    protected List<Expression> joinedAttributes;
    
    /** Used to determine if -m joining has been used. */
    protected boolean isToManyJoin = false;

    /** PERF: Used to avoid null checks for inner attribute joining. */
    protected boolean hasOuterJoinedAttribute = true;
    
    /** Used internally for joining. */
    protected transient Map<DatabaseMapping, Object> joinedMappingIndexes;

    /** Used internally for joining. */
    protected transient Map<DatabaseMapping, ObjectLevelReadQuery> joinedMappingQueries;
    
    /** PERF: Stores the cloned joinedMappingQueries. */
    protected transient Map<DatabaseMapping, ObjectLevelReadQuery> joinedMappingQueryClones;

    /** Stored all row results to -m joining. */
    protected transient List<AbstractRecord> dataResults;

    /** Stored all row results to -m joining by cache key. */
    protected transient Map<Object, List<AbstractRecord>> dataResultsByPrimaryKey;
    
    /** Stores the descriptor that these joins apply on */
    protected transient ClassDescriptor descriptor;
    
    /** Stores the base builder for resolving joined attributes by name. */
    protected ExpressionBuilder baseExpressionBuilder;
    
    /** Stores the last used base expression while adding joined attribute expression. */
    protected Expression lastJoinedAttributeBaseExpression;
    
    /** Stores the base query. */
    protected ObjectBuildingQuery baseQuery;
    
    /** Stores the result index of the parent, used for oneToMany joins. */
    protected int parentResultIndex;
        
    /** Determine if duplicate rows should be filter when using 1-m joining. */
    protected boolean shouldFilterDuplicates = true;
    
    //** Stores orderBy expressions of the joined CollectionMappings - in case the mapping has listFieldOrder */ 
    protected transient List<Expression> orderByExpressions;
    
    //** Stores additional field expressions of the joined CollectionMappings - in case the mapping has listFieldOrder */ 
    protected transient List<Expression> additionalFieldExpressions;
    
    public JoinedAttributeManager(){
    }
    
    public JoinedAttributeManager(ClassDescriptor descriptor, ExpressionBuilder baseBuilder, ObjectBuildingQuery baseQuery){
        this.descriptor = descriptor;
        this.baseQuery = baseQuery;
        this.baseExpressionBuilder = baseBuilder;
        this.parentResultIndex = 0;
    }
        
    /**
     * Return if duplicate rows should be filter when using 1-m joining.
     */
    public boolean shouldFilterDuplicates() {
        return shouldFilterDuplicates;
    }

    /**
     * Set if duplicate rows should be filter when using 1-m joining.
     */
    public void setShouldFilterDuplicates(boolean shouldFilterDuplicates) {
        this.shouldFilterDuplicates = shouldFilterDuplicates;
    }
    
    public void addJoinedAttribute(Expression attributeExpression) {
        this.getJoinedAttributes().add(attributeExpression);
    }

    public void addJoinedAttributeExpression(Expression attributeExpression) {
        if(!getJoinedAttributeExpressions().contains(attributeExpression)) {
            getJoinedAttributeExpressions().add(attributeExpression);
        }
    }
    
    /**
     * Add an attribute represented by the given attribute name to the list of joins for this query.
     * Note: Mapping level joins are represented separately from query level joins.
     */
    public void addJoinedMappingExpression(Expression mappingExpression) {
        getJoinedMappingExpressions().add(mappingExpression);
    }

    /**
     * Add an attribute represented by the given attribute name to the list of joins for this query.
     * Note: Mapping level joins are represented separately from query level joins.
     */
    public void addJoinedMapping(String attributeName) {
        addJoinedMappingExpression(this.baseExpressionBuilder.get(attributeName));
    }

    /**
     * Clones the Joined Attribute Manager.  Generally called from Query.clone().
     */
    public JoinedAttributeManager clone(){
        JoinedAttributeManager joinManager = null;
        try {
            joinManager = (JoinedAttributeManager)super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
        if (this.joinedAttributeExpressions != null) {
            joinManager.joinedAttributeExpressions = new ArrayList<Expression>(this.joinedAttributeExpressions);
        }
        if (this.joinedMappingExpressions != null) {
            joinManager.joinedMappingExpressions = new ArrayList<Expression>(this.joinedMappingExpressions);
        }
        if (this.joinedAttributes != null) {
            joinManager.joinedAttributes = new ArrayList<Expression>(this.joinedAttributes);
        }
        if (this.joinedMappingIndexes != null) {
            joinManager.joinedMappingIndexes = new HashMap<DatabaseMapping, Object>(this.joinedMappingIndexes);
        }
        if (this.joinedMappingQueries != null) {
            joinManager.joinedMappingQueries = new HashMap<DatabaseMapping, ObjectLevelReadQuery>(this.joinedMappingQueries);
        }
        if (this.orderByExpressions != null) {
            joinManager.orderByExpressions = new ArrayList<Expression>(this.orderByExpressions);
        }
        if (this.additionalFieldExpressions != null) {
            joinManager.additionalFieldExpressions = new ArrayList<Expression>(this.additionalFieldExpressions);
        }
        if (this.joinedAttributeMappings != null) {
            joinManager.joinedAttributeMappings = new ArrayList<DatabaseMapping>(this.joinedAttributeMappings);
        }
        if (this.joinedAggregateMappings !=null) {
            joinManager.joinedAggregateMappings = new ArrayList<DatabaseMapping>(this.joinedAggregateMappings);
        }
        return joinManager;
    }
    
    /**
     * Clear the joining state.  This is used to redefine a queries joins for nested joins.
     */
    public void clear(){
        this.joinedAttributeExpressions = null;
        this.joinedMappingExpressions = null;
        this.joinedAttributes = null;
        this.joinedMappingIndexes = null;
        this.isToManyJoin = false;
        this.hasOuterJoinedAttribute = false;
        this.joinedMappingQueries = null;
        this.joinedMappingQueryClones = null;
        this.orderByExpressions = null;
        this.additionalFieldExpressions = null;
        this.joinedAttributeMappings = null;
        this.joinedAggregateMappings = null;
    }

    /**
     * For joining the resulting rows include the field/values for many objects.
     * As some of the objects may have the same field names, these row partitions need to be calculated.
     * The indexes are stored in the query and used later when building the objects.
     */
    public int computeJoiningMappingIndexes(boolean includeAllSubclassFields, AbstractSession session, int offset) {
        if (!hasJoinedExpressions()) {
            return offset;
        }
        setJoinedMappingIndexes_(new HashMap(getJoinedAttributeExpressions().size() + getJoinedMappingExpressions().size()));
        int fieldIndex = 0;
        if (getBaseQuery().hasPartialAttributeExpressions()) {
            fieldIndex = getDescriptor().getPrimaryKeyFields().size(); // Query will select pks
            //next check for any partial attributes that are not joined attributes
            Iterator partialAttributes = ((ObjectLevelReadQuery)getBaseQuery()).getPartialAttributeExpressions().iterator();
            while(partialAttributes.hasNext()){
                Expression expression = (Expression)partialAttributes.next();
                if (expression.isQueryKeyExpression()){
                    if (!getJoinedMappingExpressions().contains(expression) && ! getJoinedAttributeExpressions().contains(expression)){
                        fieldIndex += ((QueryKeyExpression)expression).getFields().size();
                    }
                }
            }
        } else if (getBaseQuery().hasExecutionFetchGroup()) {
            fieldIndex = ((ObjectLevelReadQuery)getBaseQuery()).getFetchGroupNonNestedFieldsSet().size();
        } else {
            if (includeAllSubclassFields) {
                fieldIndex = getDescriptor().getAllFields().size();
            } else {
                fieldIndex = getDescriptor().getFields().size();
            }
        }
        fieldIndex += offset;
        fieldIndex = computeIndexesForJoinedExpressions(getJoinedAttributeExpressions(), fieldIndex, session);
        fieldIndex = computeIndexesForJoinedExpressions(getJoinedMappingExpressions(), fieldIndex, session);
        return fieldIndex;
    }

    /**
     * This method is used when computing the nested queries for joined mappings.
     * It recurses computing the nested mapping queries and their join indexes.
     */
    protected void computeNestedQueriesForJoinedExpressions(List joinedExpressions, AbstractSession session, ObjectLevelReadQuery readQuery) {
        for (int index = 0; index < joinedExpressions.size(); index++) {
            ObjectExpression objectExpression = (ObjectExpression)joinedExpressions.get(index);

            // Expression may not have been initialized.
            objectExpression.getBuilder().setSession(session.getRootSession(null));
            if (objectExpression.getBuilder().getQueryClass() == null){
                objectExpression.getBuilder().setQueryClass(descriptor.getJavaClass());
            }
            //get the first expression after the builder that is not an aggregate, and populate the aggregateMapping list if there are aggregates
            ObjectExpression baseExpression = getFirstNonAggregateExpressionAfterExpressionBuilder(objectExpression, getJoinedAggregateMappings());

            // PERF: Cache local join attribute Expression.
            this.addJoinedAttribute(baseExpression);
            
            DatabaseMapping mapping = baseExpression.getMapping();
            this.getJoinedAttributeMappings().add(mapping);
            
            // focus on the base expression.  Nested queries will handle nested expressions, and only need to be processed once
            if (mapping.isForeignReferenceMapping() && !getJoinedMappingQueries_().containsKey(mapping)) {
                // A nested query must be built to pass to the descriptor that looks like the real query execution would.
                ObjectLevelReadQuery nestedQuery = ((ForeignReferenceMapping)mapping).prepareNestedJoins(this, readQuery, session);
                if (nestedQuery != null) {
                    // Register the nested query to be used by the mapping for all the objects.
                    getJoinedMappingQueries_().put(mapping, nestedQuery);
                }
                if (mapping.isCollectionMapping()){
                    ((CollectionMapping)mapping).getContainerPolicy().addNestedJoinsQueriesForMapKey(this, readQuery, session);
                }
            }
        }
    }

    /**
     * Used to optimize joining by pre-computing the nested join queries for the mappings.
     */
    public void computeJoiningMappingQueries(AbstractSession session) {
        if (hasJoinedExpressions()) {
            this.joinedAttributeMappings = new ArrayList<DatabaseMapping>(getJoinedAttributeExpressions().size() + getJoinedMappingExpressions().size());
            this.joinedAttributes = new ArrayList<Expression>(getJoinedAttributeExpressions().size() + getJoinedMappingExpressions().size());
            setJoinedMappingQueries_(new HashMap(getJoinedAttributeExpressions().size() + getJoinedMappingExpressions().size()));
            computeNestedQueriesForJoinedExpressions(getJoinedAttributeExpressions(), session, (ObjectLevelReadQuery)this.baseQuery);
            computeNestedQueriesForJoinedExpressions(getJoinedMappingExpressions(), session, (ObjectLevelReadQuery)this.baseQuery);
        }
    }

    /**
     * This method is used when computing the indexes for joined mappings.
     * It iterates through a list of join expressions and adds an index that represents where the
     * fields represented by that expression will appear in the row returned by a read query.
     * computeNestedQueriesForJoinedExpressions must be already called.
     */
    protected int computeIndexesForJoinedExpressions(List joinedExpressions, int currentIndex, AbstractSession session) {
        for (int index = 0; index < joinedExpressions.size(); index++) {
            ObjectExpression objectExpression = (ObjectExpression)joinedExpressions.get(index);
            DatabaseMapping mapping = objectExpression.getMapping();
            // only store the index if this is the local expression to avoid it being added multiple times
            //This means the base local expression must be first on the list, followed by nested expressions
            ObjectExpression localExpression = getFirstNonAggregateExpressionAfterExpressionBuilder(objectExpression, new ArrayList(1));
            if ((localExpression == objectExpression) && (mapping != null) && mapping.isForeignReferenceMapping()) {
                getJoinedMappingIndexes_().put(mapping, Integer.valueOf(currentIndex));
            }
            ClassDescriptor descriptor = mapping.getReferenceDescriptor();
            int numberOfFields = 0;
            if (descriptor == null) {
                // Direct-collection mappings do not have descriptor.
                if (mapping.isDirectCollectionMapping()) {
                    numberOfFields = 1;
                }
            } else {
                ObjectLevelReadQuery nestedQuery = null;
                FetchGroup fetchGroup = null;
                if(descriptor.hasFetchGroupManager()) {
                    nestedQuery = getNestedJoinedMappingQuery(objectExpression);
                    fetchGroup = nestedQuery.getExecutionFetchGroup();
                }
                if(fetchGroup != null) {
                    numberOfFields = nestedQuery.getFetchGroupNonNestedFieldsSet(mapping).size();
                } else {
                    if (objectExpression.isQueryKeyExpression() && objectExpression.isUsingOuterJoinForMultitableInheritance()) {
                        numberOfFields = descriptor.getAllFields().size();
                    } else {
                        numberOfFields = descriptor.getFields().size();
                    }
                }
            }
            if (mapping.isCollectionMapping()){
                // map keys are indexed within the collection's row.  Therefore we use an offset from within the collections row
                numberOfFields += ((CollectionMapping)mapping).getContainerPolicy().updateJoinedMappingIndexesForMapKey(getJoinedMappingIndexes_(), numberOfFields);
            }
            currentIndex = currentIndex + numberOfFields;
        }
        return currentIndex;
    }

    /**
     * Get the list of additional field expressions.
     */
    public List<Expression> getAdditionalFieldExpressions() {
        if (this.additionalFieldExpressions == null){
            this.additionalFieldExpressions = new ArrayList<Expression>();
        }
        return additionalFieldExpressions;
    }

    /**
     * Get the list of additional field expressions.
     */
    public List<Expression> getAdditionalFieldExpressions_() {
        return additionalFieldExpressions;
    }

    /**
     * Returns the base expression builder for this query.
     */
    public ExpressionBuilder getBaseExpressionBuilder(){
        return this.baseExpressionBuilder;
    }
    
    /**
     * Returns the base query.
     */
    public ObjectBuildingQuery getBaseQuery(){
        return this.baseQuery;
    }
    
    /**
     * Return  all of the rows fetched by the query, used for 1-m joining.
     */
    public List<AbstractRecord> getDataResults_() {
        return dataResults;
    }

    public ClassDescriptor getDescriptor(){
        if (this.descriptor == null){
            this.descriptor = this.baseQuery.getDescriptor();
        }
        return this.descriptor;
    }
    
    /**
     * INTERNAL:
     * Parses an expression to return the first non-AggregateObjectMapping expression after the base ExpressionBuilder.
     * 
     * @param fullExpression
     * @param aggregateMappingsEncountered - collection of aggregateObjectMapping expressions encountered in the returned expression
     *  between the first expression and the ExpressionBuilder
     * @return first non-AggregateObjectMapping expression after the base ExpressionBuilder from the fullExpression
     */
    protected ObjectExpression getFirstNonAggregateExpressionAfterExpressionBuilder(ObjectExpression fullExpression, List aggregateMappingsEncountered){
        boolean done = false;
        ObjectExpression baseExpression = fullExpression;
        ObjectExpression prevExpression = fullExpression;
        while (!baseExpression.getBaseExpression().isExpressionBuilder()&& !done) {
            baseExpression = (ObjectExpression)baseExpression.getBaseExpression();
            while (!baseExpression.isExpressionBuilder() && baseExpression.getMapping().isAggregateObjectMapping()){
                aggregateMappingsEncountered.add(baseExpression.getMapping());
                baseExpression = (ObjectExpression)baseExpression.getBaseExpression();
            }
            if (baseExpression.isExpressionBuilder()){
                done = true;
                //use the one closest to the expression builder that wasn't an aggregate
                baseExpression = prevExpression;
            } else {
                prevExpression = baseExpression;
            }
        }
        return baseExpression;
    }

    /**
     * Return if there are additional field expressions.
     */
    public boolean hasAdditionalFieldExpressions() {
        return (this.additionalFieldExpressions != null) && (!this.additionalFieldExpressions.isEmpty());
    }

    /**
     * Set the list of additional field expressions.
     */
    public void setAdditionalFieldExpressions_(List<Expression> expressions) {
        this.additionalFieldExpressions = expressions;
    }

    /**
     * Return the attributes that must be joined.
     */
    public List<DatabaseMapping> getJoinedAggregateMappings() {
        if (this.joinedAggregateMappings == null){
            this.joinedAggregateMappings = new ArrayList<DatabaseMapping>();
        }
        return joinedAggregateMappings;
    }

    /**
     * Return the attributes that must be joined.
     */
    public List<Expression> getJoinedAttributeExpressions() {
        if (this.joinedAttributeExpressions == null){
            this.joinedAttributeExpressions = new ArrayList<Expression>();
        }
        return joinedAttributeExpressions;
    }

    /**
     * Return the attributes that must be joined.
     */
    public List<DatabaseMapping> getJoinedAttributeMappings() {
        if (this.joinedAttributeMappings == null){
            this.joinedAttributeMappings = new ArrayList<DatabaseMapping>();
        }
        return this.joinedAttributeMappings;
    }

    /**
     * Return the attributes that must be joined.
     */
    public List<Expression> getJoinedAttributes() {
        if (this.joinedAttributes == null){
            this.joinedAttributes = new ArrayList<Expression>();
        }
        return this.joinedAttributes;
    }

    /**
     * Get the list of expressions that represent elements that are joined because of their
     * mapping for this query.
     */
    public List<Expression> getJoinedMappingExpressions() {
        if (this.joinedMappingExpressions == null){
            this.joinedMappingExpressions = new ArrayList<Expression>();
        }
        return joinedMappingExpressions;
    }

    /**
     * Return the attributes that must be joined.
     */
    public boolean hasJoinedAttributeExpressions() {
        return (this.joinedAttributeExpressions != null) && (!this.joinedAttributeExpressions.isEmpty());
    }

    /**
     * This method checks both attribute expressions and mapping expressions and
     * determines if there are any joins to be made.
     */
    public boolean hasJoinedExpressions() {
        return hasJoinedAttributeExpressions() || hasJoinedMappingExpressions();
    }
    
    /**
     * Return the attributes that must be joined.
     */
    public boolean hasJoinedMappingExpressions() {
        return (this.joinedMappingExpressions != null) && (!this.joinedMappingExpressions.isEmpty());
    }

    /**
     * Return if any attributes are joined.  This is a convience method that 
     * is only valid after prepare.
     */
    public boolean hasJoinedAttributes() {
        return (this.joinedAttributes != null) && (!this.joinedAttributes.isEmpty());
    }

    /**
     * PERF: Return if the query uses any outer attribute joins, used to avoid null checks in building objects.
     */
    public boolean hasOuterJoinedAttributeQuery() {
        return this.hasOuterJoinedAttribute;
    }

    /**
     * Get the list of orderBy expressions.
     */
    public List<Expression> getOrderByExpressions() {
        if (this.orderByExpressions == null){
            this.orderByExpressions = new ArrayList<Expression>();
        }
        return orderByExpressions;
    }

    /**
     * Get the list of orderBy expressions.
     */
    public List<Expression> getOrderByExpressions_() {
        return orderByExpressions;
    }
    
    /**
     * INTERNAL: 
     *  Helper method to get the value from the clone for the expression passed in, triggering joins on
     *  all intermediate steps.  
     *  Example expression "emp.project.pk" with a clone Employee will trigger indirection and return
     *  the project pk value.  
     * @param session
     * @param clone
     * @param expression
     * @return
     */
    public Object getValueFromObjectForExpression(AbstractSession session, Object clone, ObjectExpression expression){
        if (!expression.isExpressionBuilder()){
            //can only operate over querykeys representing aggregate Objects.  Indirection should not be needed
            Object baseValue = this.getValueFromObjectForExpression(session, clone, (ObjectExpression)expression.getBaseExpression());
            if ( baseValue == null ) {
                return null;
            }
            DatabaseMapping mapping = expression.getMapping();
            Object attributeValue = mapping.getRealAttributeValueFromObject(baseValue, session);
            if (attributeValue != null) {
                if (mapping.isForeignReferenceMapping() && (((ForeignReferenceMapping)mapping).getIndirectionPolicy().usesTransparentIndirection())) {
                    //getRealAttributeValueFromObject does not trigger transparent indirection, but instantiateObject will (it calls size on it)
                    ((ForeignReferenceMapping)mapping).getIndirectionPolicy().instantiateObject(baseValue, attributeValue);
                }
            }
            return attributeValue;
            
        }
        return clone;
    }

    /**
     * Return if there are orderBy expressions.
     */
    public boolean hasOrderByExpressions() {
        return (this.orderByExpressions != null) && (!this.orderByExpressions.isEmpty());
    }

    /**
     * Set the list of orderBy expressions.
     */
    public void setOrderByExpressions_(List<Expression> expressions) {
        this.orderByExpressions = expressions;
    }

    /**
     * Return if the query uses any -m joins, and thus return duplicate/multiple rows.
     */
    public boolean isToManyJoin() {
        return this.isToManyJoin;
    }
    
    /**
     * Return if the attribute is specified for joining.
     */
    public boolean isAttributeJoined(ClassDescriptor mappingDescriptor, DatabaseMapping attributeMapping) {
        // Since aggregates share the same query as their parent, must avoid the aggregate thinking
        // the parents mappings is for it, (queries only share if the aggregate was not joined). 
        //This isn't taking into account inheritance - a query on a child may use/join parent level mappings
        if (this.hasJoinedAttributes()) {
            //if it has joined attributes, the other collections must also be set and so don't need to be checked
            if (attributeMapping.isAggregateMapping()){
                return this.getJoinedAggregateMappings().contains(attributeMapping);
            } else {
                return this.getJoinedAttributeMappings().contains(attributeMapping);
            }}
        return isAttributeExpressionJoined(attributeMapping) || isAttributeMappingJoined(attributeMapping);
    }

    /**
     * Iterate through a list of expressions searching for the given attribute name.
     * Return true if it is found, false otherwise.  Only use if the query was preprepared so that join expressions
     * were processed.  
     */
    protected boolean isMappingInJoinedExpressionList(DatabaseMapping attributeMapping, List joinedExpressionList) {
        for (Iterator joinEnum = joinedExpressionList.iterator(); joinEnum.hasNext();) {
            List aggregateMappings = new ArrayList();
            ObjectExpression expression = getFirstNonAggregateExpressionAfterExpressionBuilder((ObjectExpression)joinEnum.next(), aggregateMappings);
            if (attributeMapping.isAggregateObjectMapping() && aggregateMappings.contains(attributeMapping)) {
                return true;
            } else if (attributeMapping.equals(expression.getMapping())) {//expression may not have been processed yet     
                return true;
            }
        }
        return false;
    }
    

   /**
    * Iterate through a list of expressions searching for the given attribute name.
    * Return true if it is found, false otherwise.
    */
    protected boolean isAttributeNameInJoinedExpressionList(String attributeName, List joinedExpressionList) {
        for (Iterator joinEnum = joinedExpressionList.iterator(); joinEnum.hasNext();) {
            QueryKeyExpression expression = (QueryKeyExpression)joinEnum.next();
            while (!expression.getBaseExpression().isExpressionBuilder()) {
                expression = (QueryKeyExpression)expression.getBaseExpression();
            }
            if (expression.getName().equals(attributeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return if the attribute is specified for joining.
     */
    protected boolean isAttributeExpressionJoined(DatabaseMapping attributeMapping) {
        return isMappingInJoinedExpressionList(attributeMapping, getJoinedAttributeExpressions());
    }

    /**
     * Return whether the given attribute is joined as a result of a join on a mapping
     */
    protected boolean isAttributeMappingJoined(DatabaseMapping attributeMapping) {
        return isAttributeNameInJoinedExpressionList(attributeMapping.getAttributeName(), getJoinedMappingExpressions());
    }

    /**
     * Set the list of expressions that represent elements that are joined because of their
     * mapping for this query.
     */
    public void setJoinedAttributeExpressions_(List joinedExpressions) {
        this.joinedAttributeExpressions = joinedExpressions;
    }

    /**
     * Set the list of expressions that represent elements that are joined because of their
     * mapping for this query.
     */
    public void setJoinedMappingExpressions_(List joinedMappingExpressions) {
        this.joinedMappingExpressions = joinedMappingExpressions;
    }

    /**
     * Return the joined mapping indexes, used to compute mapping row partitions.
     */
    public Map<DatabaseMapping, Object> getJoinedMappingIndexes_() {
        return joinedMappingIndexes;
    }

    /**
     * Return the joined mapping queries, used optimize joining, only compute the nested queries once.
     */
    public Map<DatabaseMapping, ObjectLevelReadQuery> getJoinedMappingQueries_() {
        return joinedMappingQueries;
    }

    /**
     * INTERNAL:
     * Returns the nested query corresponding to the expression.
     * The passed expression should be either join mapping or joined attribute expression.
     */
    public ObjectLevelReadQuery getNestedJoinedMappingQuery(Expression expression) {
        // the first element of the list is the passed expression,
        // next one is its base, ...
        // the last one's base is ExpressionBuilder.
        ObjectExpression currentExpression = (ObjectExpression)expression; 
        ArrayList<Expression> expressionBaseList = new ArrayList();
        do {
            //skip aggregates since they do not have nested query objects added to JoinedMappingQueries, instead
            //reference mappings on aggregates are added to the parent's joinAttributeManager
            if (!currentExpression.getMapping().isAggregateObjectMapping()){
                expressionBaseList.add(currentExpression);
            }
            currentExpression = (ObjectExpression)currentExpression.getBaseExpression();
        } while(!currentExpression.isExpressionBuilder());
        
        // the last expression in the list is not nested - its mapping should have corresponding nestedQuery.
        DatabaseMapping currentMapping = ((QueryKeyExpression)expressionBaseList.get(expressionBaseList.size() - 1)).getMapping();
        ObjectLevelReadQuery nestedQuery = getJoinedMappingQueries_().get(currentMapping);
        
        // unless the passed expression was not nested, repeat moving up the list.
        // the last step is the passed expression (first on the list) getting nested query corresponding to its mapping.
        for(int i = expressionBaseList.size() - 2; i >= 0; i--) {
            currentMapping = ((QueryKeyExpression)expressionBaseList.get(i)).getMapping();
            nestedQuery = nestedQuery.getJoinedAttributeManager().getJoinedMappingQueries_().get(currentMapping);
        }
        return nestedQuery;
    }

    /**
     * Set the joined mapping queries, used optimize joining, only compute the nested queries once.
     */
    public void setJoinedMappingQueries_(Map joinedMappingQueries) {
        this.joinedMappingQueries = joinedMappingQueries;
    }

    /**
     * Set the joined mapping indexes, used to compute mapping row partitions.
     */
    public void setJoinedMappingIndexes_(Map joinedMappingIndexes) {
        this.joinedMappingIndexes = joinedMappingIndexes;
    }

    /**
     * PERF: Set if the query uses any outer attribute joins, used to avoid null checks in building objects.
     */
    protected void setIsOuterJoinedAttributeQuery(boolean isOuterJoinedAttribute) {
        this.hasOuterJoinedAttribute = isOuterJoinedAttribute;
    }
    
    /**
     * Set if the query uses any -m joins, and thus return duplicate/multiple rows.
     */
    public void setIsToManyJoinQuery(boolean isToManyJoin) {
        this.isToManyJoin = isToManyJoin;
    }
    

    /**
     * Validate and prepare join expressions.
     */
    public void prepareJoinExpressions(AbstractSession session) {
        // The prepareJoinExpression check for outer-joins to set this to true.
        setIsOuterJoinedAttributeQuery(false);
        Expression lastJoinedAttributeBaseExpression = null;
        List groupedExpressionList = new ArrayList(getJoinedAttributeExpressions().size());
        for (int index = 0; index < getJoinedAttributeExpressions().size(); index++) {
            Expression expression = getJoinedAttributeExpressions().get(index);
            expression = prepareJoinExpression(expression, session);
            //EL bug 307497: break base expressions out onto the list and sort/group expressions by base expression  
            lastJoinedAttributeBaseExpression = addExpressionAndBaseToGroupedList(expression, groupedExpressionList, lastJoinedAttributeBaseExpression);
        }
        //use the grouped list instead of the original
        this.setJoinedAttributeExpressions_(groupedExpressionList);
        for (int index = 0; index < getJoinedMappingExpressions().size(); index++) {
            Expression expression = getJoinedMappingExpressions().get(index);
            expression = prepareJoinExpression(expression, session);
            getJoinedMappingExpressions().set(index, expression);
        }
    }
    
    /**
     * adds expression and its base expressions recursively to the expressionList in groups, so that an expression is never listed before
     * its base expression
     * @param expression
     * @param expressionlist
     * @param lastJoinedAttributeBaseExpression
     * @return
     */
    protected Expression addExpressionAndBaseToGroupedList(Expression expression, List expressionlist, Expression lastJoinedAttributeBaseExpression){
        if(!expressionlist.contains(expression)) {
            int baseExpressionIndex = -1;
            boolean sameBase = false;//better than using instanceof BaseExpression.  If its not an objectExpression, it will get an exception in prepare anyway
            if((expression.isObjectExpression())) {
                Expression baseExpression = ((BaseExpression)expression).getBaseExpression();
                //filter out aggregate expressions between this and the next node.
                while (!baseExpression.isExpressionBuilder() && ((QueryKeyExpression)baseExpression).getMapping().isAggregateMapping()){
                    baseExpression = ((BaseExpression)baseExpression).getBaseExpression();
                }
                
                if(baseExpression != null && !baseExpression.isExpressionBuilder()) {
                    addExpressionAndBaseToGroupedList(baseExpression, expressionlist, lastJoinedAttributeBaseExpression);
                    // EL bug 307497
                    if (baseExpression != lastJoinedAttributeBaseExpression) {
                        baseExpressionIndex = getJoinedAttributeExpressions().indexOf(baseExpression);
                    } else {
                        sameBase = true;
                    }
                }
            }
            
            // EL bug 307497
            if (baseExpressionIndex == -1) {
                expressionlist.add(expression);
                if (!sameBase) {
                    lastJoinedAttributeBaseExpression = expression;
                }
            } else {
                //Add attributeExpression at baseExpressionIndex + 1.
                expressionlist.add(baseExpressionIndex+1, expression);
            }
        }
        return lastJoinedAttributeBaseExpression;
    }

    /**
     * Validate and prepare the join expression.
     */
    protected Expression prepareJoinExpression(Expression expression, AbstractSession session) {
        // Must be query key expression.
        if (!expression.isQueryKeyExpression()) {
            throw QueryException.mappingForExpressionDoesNotSupportJoining(expression);
        }
        QueryKeyExpression objectExpression = (QueryKeyExpression)expression;

        // Expression may not have been initialized.
        if (objectExpression.getBuilder().getQueryClass() == null) {
            objectExpression = (QueryKeyExpression)objectExpression.rebuildOn(this.baseExpressionBuilder);
            
            if (objectExpression.getBuilder().getQueryClass() == null) {
                objectExpression.getBuilder().setQueryClass(this.descriptor.getJavaClass());
            }
        }
        objectExpression.getBuilder().setSession(session.getRootSession(null));
        
        // Can only join relationships.
        if ((objectExpression.getMapping() == null) || (!objectExpression.getMapping().isJoiningSupported())) {
            throw QueryException.mappingForExpressionDoesNotSupportJoining(objectExpression);
        }

        // Search if any of the expression traverse a 1-m.
        ObjectExpression baseExpression = objectExpression;
        while (!baseExpression.isExpressionBuilder()) {
            //pulled from prepareJoinExpressions
            baseExpression.setShouldUseOuterJoinForMultitableInheritance(true);
            
            if (((QueryKeyExpression)baseExpression).shouldQueryToManyRelationship()) {
                setIsToManyJoinQuery(true);
            }
            if (baseExpression.shouldUseOuterJoin()) {
                setIsOuterJoinedAttributeQuery(true);
            }
            baseExpression = (ObjectExpression)baseExpression.getBaseExpression();
        }
        
        return objectExpression;
    }

    /**
     * This method collects the Joined Mappings from the descriptor and initializes them.
     * Excludes the mapping that are not in the passed mappingsAllowedToJoin set (if it's not null). 
     */
    public void processJoinedMappings(AbstractSession session) {
        Set<String> fetchGroupAttributes = null;
        FetchGroup fetchGroup = getBaseQuery().getExecutionFetchGroup();
        if(fetchGroup != null) {
            fetchGroupAttributes = fetchGroup.getAttributeNames();
        }
        ObjectBuilder objectBuilder = getDescriptor().getObjectBuilder();
        if (objectBuilder.hasJoinedAttributes()) {
            List mappingJoinedAttributes = objectBuilder.getJoinedAttributes();
            if (!hasJoinedAttributeExpressions()) {
                for (int i = 0; i < mappingJoinedAttributes.size(); i++) {
                    ForeignReferenceMapping mapping = (ForeignReferenceMapping) mappingJoinedAttributes.get(i);
                    if(fetchGroupAttributes == null || fetchGroupAttributes.contains(mapping.getAttributeName())) {
                        addAndPrepareJoinedMapping(mapping, session);
                    }
                }
            } else {
                for (int i = 0; i < mappingJoinedAttributes.size(); i++) {
                    ForeignReferenceMapping mapping = (ForeignReferenceMapping) mappingJoinedAttributes.get(i);
                    if (!isAttributeExpressionJoined(mapping)) {
                        if(fetchGroupAttributes == null || fetchGroupAttributes.contains(mapping.getAttributeName())) {
                            addAndPrepareJoinedMapping(mapping, session);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Add the mapping for join fetch, prepare and return the join expression being used.  
     */
    public Expression addAndPrepareJoinedMapping(ForeignReferenceMapping mapping, AbstractSession session) {
        Expression joinMappingExpression = null;
        if (mapping.isCollectionMapping()) {
            if (mapping.isInnerJoinFetched()) {
                joinMappingExpression = getBaseExpressionBuilder().anyOf(mapping.getAttributeName(), false);
            } else if (mapping.isOuterJoinFetched()) {
                joinMappingExpression = getBaseExpressionBuilder().anyOfAllowingNone(mapping.getAttributeName(), false);
            }
        } else {
            if (mapping.isInnerJoinFetched()) {
                joinMappingExpression = getBaseExpressionBuilder().get(mapping.getAttributeName());
            } else if (mapping.isOuterJoinFetched()) {
                joinMappingExpression = getBaseExpressionBuilder().getAllowingNull(mapping.getAttributeName());
            }
        }
        if (joinMappingExpression != null) {
            joinMappingExpression = prepareJoinExpression(joinMappingExpression, session);
            addJoinedMappingExpression(joinMappingExpression);
        }
        return joinMappingExpression;
    }
    
    /**
     * Reset the JoinedAttributeManager.  This will be called when the Query is re-prepared
     */
    public void reset(){
        this.joinedMappingExpressions = null;
        this.joinedAttributes = null;
        this.isToManyJoin = false;
        this.hasOuterJoinedAttribute = true;
        this.joinedMappingIndexes = null;
        this.joinedMappingQueries = null;
        this.dataResults = null;
        this.joinedAttributeMappings = null;
        this.joinedAggregateMappings = null;
    }
    
    /**
     * This method is called from within this package it is used when 
     * initializing a report Item
     */
    public void setBaseQuery(ObjectLevelReadQuery query){
        this.baseQuery = query;
    }
    
    /**
     * This method is called from within this package, it is used when
     * initializing a ReportItem
     */
    protected void setBaseExpressionBuilder(ExpressionBuilder builder){
        this.baseExpressionBuilder = builder;
    }
    
    /**
     * Return all of the rows fetched by the query by cache-key, used for 1-m joining.
     */
    public Map<Object, List<AbstractRecord>> getDataResultsByPrimaryKey() {
        return dataResultsByPrimaryKey;
    }
    
    /**
     * Set all of the rows fetched by the query by cache-key, used for 1-m joining.
     */
    protected void setDataResultsByPrimaryKey(Map<Object, List<AbstractRecord>> dataResultsByPrimaryKey) {
        this.dataResultsByPrimaryKey = dataResultsByPrimaryKey;
    }
    
    /**
     * Set all of the rows fetched by the query, used for 1-m joining.
     */
    public void setDataResults(List dataResults, AbstractSession session) {
        this.dataResults = dataResults;
        processDataResults(session);
    }
    
    /**
     * Process the data-results for joined data for a 1-m join.
     * This allows all the data to be processed once, instead of n times for each object.
     */
    protected void processDataResults(AbstractSession session) {
        this.dataResultsByPrimaryKey = new HashMap();
        int size = this.dataResults.size();
        Object firstKey = null;
        Object lastKey = null;
        List<AbstractRecord> childRows = null;
        ObjectBuilder builder = getDescriptor().getObjectBuilder();
        int parentIndex = getParentResultIndex();
        Vector trimedFields = null;
        for (int dataResultsIndex = 0; dataResultsIndex < size; dataResultsIndex++) {
            AbstractRecord row = this.dataResults.get(dataResultsIndex);
            AbstractRecord parentRow = row;
            // Must adjust for the parent index to ensure the correct pk is extracted.
            if (parentIndex > 0) {
                if (trimedFields == null) { // The fields are always the same, so only build once.
                    trimedFields = new NonSynchronizedSubVector(row.getFields(), parentIndex, row.size());
                }
                Vector trimedValues = new NonSynchronizedSubVector(row.getValues(), parentIndex, row.size());
                parentRow = new DatabaseRecord(trimedFields, trimedValues);
            }
            // Extract the primary key of the source object, to filter only the joined rows for that object.
            Object sourceKey = builder.extractPrimaryKeyFromRow(parentRow, session);
            // May be any outer-join so ignore null.
            if (sourceKey != null) {
                if (firstKey == null) {
                    firstKey = sourceKey;
                }
                if ((lastKey != null) && lastKey.equals(sourceKey)) {
                    childRows.add(row);
                    if (shouldFilterDuplicates()) {
                        // Also null out the row because it is a duplicate to avoid object building processing it.
                        this.dataResults.set(dataResultsIndex, null);
                    }
                } else {
                    childRows =  this.dataResultsByPrimaryKey.get(sourceKey);
                    if (childRows == null) {
                        childRows = new ArrayList();
                        this.dataResultsByPrimaryKey.put(sourceKey, childRows);
                    } else {
                        if (shouldFilterDuplicates()) {
                            // Also null out the row because it is a duplicate to avoid object building processing it.
                            this.dataResults.set(dataResultsIndex, null);
                        }
                    }
                    childRows.add(row);
                    lastKey = sourceKey;
                }
            }
        }
        // If pagination is used, the first and last rows may be missing their 1-m joined rows, so reject them from the results.
        // This will cause them to build normally by executing a query.
        if (this.isToManyJoin) {
            if ((lastKey != null) && (this.baseQuery.getMaxRows() > 0)) {
                this.dataResultsByPrimaryKey.remove(lastKey);
            }
            if ((firstKey != null) && (this.baseQuery.getFirstResult() > 0)) {
                this.dataResultsByPrimaryKey.remove(firstKey);
            }
        }
    }
    
    /**
     * Clear the data-results for joined data for a 1-m join.
     */
    public void clearDataResults() {
        this.dataResults = null;
        this.dataResultsByPrimaryKey = null;
    }
    
    /**
     * Process the data-results for joined data for a 1-m join.
     * This allows incremental processing for a cursor.
     */
    public AbstractRecord processDataResults(AbstractRecord row, Cursor cursor, boolean forward) {
        if (this.dataResultsByPrimaryKey == null) {
            this.dataResultsByPrimaryKey = new HashMap();
        }
        AbstractRecord parentRow = row;
        List<AbstractRecord> childRows = new ArrayList<AbstractRecord>();
        childRows.add(row);
        int parentIndex = getParentResultIndex();
        // Must adjust for the parent index to ensure the correct pk is extracted.
        Vector trimedFields = new NonSynchronizedSubVector(row.getFields(), parentIndex, row.size());
        if (parentIndex > 0) {
            Vector trimedValues = new NonSynchronizedSubVector(row.getValues(), parentIndex, row.size());
            parentRow = new DatabaseRecord(trimedFields, trimedValues);
        }
        ObjectBuilder builder = getDescriptor().getObjectBuilder();
        AbstractSession session = cursor.getExecutionSession();
        // Extract the primary key of the source object, to filter only the joined rows for that object.
        Object sourceKey = builder.extractPrimaryKeyFromRow(parentRow, session);
        AbstractRecord extraRow = null;
        while (true) {
            AbstractRecord nextRow = null;
            if (forward) {
                nextRow = cursor.getAccessor().cursorRetrieveNextRow(cursor.getFields(), cursor.getResultSet(), session);
            } else {
                nextRow = cursor.getAccessor().cursorRetrievePreviousRow(cursor.getFields(), cursor.getResultSet(), session);
            }
            if (nextRow == null) {
                break;
            }
            AbstractRecord nextParentRow = nextRow;
            if (parentIndex > 0) {
                Vector trimedValues = new NonSynchronizedSubVector(nextParentRow.getValues(), parentIndex, nextParentRow.size());
                nextParentRow = new DatabaseRecord(trimedFields, trimedValues);
            }
            // Extract the primary key of the source object, to filter only the joined rows for that object.
            Object nextKey = builder.extractPrimaryKeyFromRow(nextParentRow, session);
            if ((sourceKey != null) && sourceKey.equals(nextKey)) {
                childRows.add(nextRow);
            } else {
                extraRow = nextRow;
                break;
            }
        }
        this.dataResultsByPrimaryKey.put(sourceKey, childRows);
        return extraRow;
    }

    /**
     * Called to set the descriptor on a Join Managerwith in a ReportItem, durring
     * initialization, and durring DatabaseQuery.checkDescriptor.
     */
    public void setDescriptor(ClassDescriptor descriptor){
        this.descriptor = descriptor;
    }
    
    /**
     * Used for joining in conjunction with pessimistic locking.
     * Iterate through a list of joined expressions and ensure expression is set on the locking
     * clause for each expression that represents a pessimisically locked descriptor.
     */
    public ForUpdateOfClause setupLockingClauseForJoinedExpressions(ForUpdateOfClause lockingClause, AbstractSession session) {
        if (hasJoinedAttributeExpressions()){
            return setupLockingClauseForJoinedExpressions(getJoinedAttributeExpressions(), session,lockingClause);
        }
        if (hasJoinedMappingExpressions()){
            return setupLockingClauseForJoinedExpressions(getJoinedMappingExpressions(), session,lockingClause);
        }
        return lockingClause;
    }

    /**
     * Used for joining in conjunction with pessimistic locking.
     * Iterate through a list of joined expressions and ensure expression is set on the locking
     * clause for each expression that represents a pessimisically locked descriptor.
     */
    private ForUpdateOfClause setupLockingClauseForJoinedExpressions(List joinedExpressions, AbstractSession session, ForUpdateOfClause lockingClause) {
        // Must iterate over all of the joined attributes, just check
        // if any of them have pessimistic locking defined on the descriptor.
        for (Iterator e = joinedExpressions.iterator(); e.hasNext();) {
            Expression expression = (Expression)e.next();

            // Expression has not yet been validated.
            if (expression.isObjectExpression()) {
                ObjectExpression joinedAttribute = (ObjectExpression)expression;

                // Expression may not have been initialized.
                joinedAttribute.getBuilder().setSession(session.getRootSession(null));
                if (joinedAttribute.getBuilder().getQueryClass() == null){
                    joinedAttribute.getBuilder().setQueryClass(descriptor.getJavaClass());
                }
                
                ClassDescriptor nestedDescriptor = joinedAttribute.getDescriptor();

                // expression may not be valid, no descriptor, validation occurs later.
                if (nestedDescriptor == null) {
                    return lockingClause;
                }
                if (nestedDescriptor.hasPessimisticLockingPolicy()) {
                    if (lockingClause == null) {
                        lockingClause = new ForUpdateOfClause();
                        lockingClause.setLockMode(nestedDescriptor.getCMPPolicy().getPessimisticLockingPolicy().getLockingMode());
                    }
                    lockingClause.addLockedExpression(joinedAttribute);
                }
            }
        }
        return lockingClause;
    }

    public void setParentResultIndex(int parentsResultIndex) {
        this.parentResultIndex = parentsResultIndex;
    }

    public int getParentResultIndex() {
        return parentResultIndex;
    }

    public Map<DatabaseMapping, ObjectLevelReadQuery> getJoinedMappingQueryClones() {
        return joinedMappingQueryClones;
    }
    
    public void setJoinedMappingQueryClones(Map joinedMappingQueryClones) {
        this.joinedMappingQueryClones = joinedMappingQueryClones;
    }
}
