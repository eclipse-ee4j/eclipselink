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

    /** Stores the joined attributes added through the query */
    protected List<Expression> joinedAttributeExpressions;
    
    /** Stores the joined attributes as specified in the descriptor */
    protected List<Expression> joinedMappingExpressions;
    
    /** PERF: Cache the local joined attribute names. */
    protected List<String> joinedAttributes;
    
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
    
    public void addJoinedAttribute(String attributeExpression) {
        this.getJoinedAttributes().add(attributeExpression);
    }

    public void addJoinedAttributeExpression(Expression attributeExpression) {
        if(!getJoinedAttributeExpressions().contains(attributeExpression)) {
            int baseExpressionIndex = -1;
            boolean sameBase = false;
            if((attributeExpression instanceof BaseExpression)) {
                Expression baseExpression = ((BaseExpression)attributeExpression).getBaseExpression();
                if(baseExpression != null && !baseExpression.isExpressionBuilder()) {
                    addJoinedAttributeExpression(baseExpression);
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
                getJoinedAttributeExpressions().add(attributeExpression);
                if (!sameBase) {
                    lastJoinedAttributeBaseExpression = attributeExpression;
                }
            } else {
                //Add attributeExpression at baseExpressionIndex + 1.
                getJoinedAttributeExpressions().add(baseExpressionIndex+1, attributeExpression);
            }
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
            joinManager.joinedAttributes = new ArrayList<String>(this.joinedAttributes);
        }
        if (this.joinedMappingIndexes != null) {
            joinManager.joinedMappingIndexes = new HashMap<DatabaseMapping, Object>(this.joinedMappingIndexes);
        }
        if (this.joinedMappingQueries != null) {
            joinManager.joinedMappingQueries = new HashMap<DatabaseMapping, ObjectLevelReadQuery>(this.joinedMappingQueries);
        }
        if(this.orderByExpressions != null) {
            joinManager.orderByExpressions = new ArrayList<Expression>(this.orderByExpressions);
        }
        if(this.additionalFieldExpressions != null) {
            joinManager.additionalFieldExpressions = new ArrayList<Expression>(this.additionalFieldExpressions);
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
            
            // PERF: Cache join attribute names.
            ObjectExpression baseExpression = objectExpression;
            while (!baseExpression.getBaseExpression().isExpressionBuilder()) {
                baseExpression = (ObjectExpression)baseExpression.getBaseExpression();
            }
            this.addJoinedAttribute(baseExpression.getName());
            
            // Ignore nested
            if ((objectExpression.getBaseExpression() == objectExpression.getBuilder()) && objectExpression.getMapping().isForeignReferenceMapping()) {
                ForeignReferenceMapping mapping = (ForeignReferenceMapping)objectExpression.getMapping();

                // A nested query must be built to pass to the descriptor that looks like the real query execution would.
                ObjectLevelReadQuery nestedQuery = mapping.prepareNestedJoins(this, readQuery, session);
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
            this.joinedAttributes = new ArrayList<String>(getJoinedAttributeExpressions().size() + getJoinedMappingExpressions().size());
            setJoinedMappingQueries_(new HashMap(getJoinedAttributeExpressions().size() + getJoinedMappingExpressions().size()));
            computeNestedQueriesForJoinedExpressions(getJoinedAttributeExpressions(), session, (ObjectLevelReadQuery)this.baseQuery);
            computeNestedQueriesForJoinedExpressions(getJoinedMappingExpressions(), session, (ObjectLevelReadQuery)this.baseQuery);
        }
    }

    /**
     * This method is used when computing the indexes for joined mappings.
     * It iterates through a list of join expressions and adds an index that represents where the
     * fields represented by that expression will appear in the row returned by a read query.
     */
    protected int computeIndexesForJoinedExpressions(List joinedExpressions, int currentIndex, AbstractSession session) {
        for (int index = 0; index < joinedExpressions.size(); index++) {
            ObjectExpression objectExpression = (ObjectExpression)joinedExpressions.get(index);
            DatabaseMapping mapping = objectExpression.getMapping();
            // Ignore nested
            if ((objectExpression.getBaseExpression() == objectExpression.getBuilder()) && (mapping != null) && mapping.isForeignReferenceMapping()) {
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
    public List<String> getJoinedAttributes() {
        if (this.joinedAttributes == null){
            this.joinedAttributes = new ArrayList<String>();
        }
        return this.joinedAttributes;
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
    public boolean isAttributeJoined(ClassDescriptor mappingDescriptor, String attributeName) {
        // Since aggregates share the same query as their parent, must avoid the aggregate thinking
        // the parents mappings is for it, (queries only share if the aggregate was not joined).
        if (mappingDescriptor.isAggregateDescriptor() && (mappingDescriptor != getDescriptor())) {
            return false;
        }
        if (this.hasJoinedAttributes()) {
            return this.joinedAttributes.contains(attributeName);
        }
        return isAttributeExpressionJoined(attributeName) || isAttributeMappingJoined(attributeName);
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
    protected boolean isAttributeExpressionJoined(String attributeName) {
        return isAttributeNameInJoinedExpressionList(attributeName, getJoinedAttributeExpressions());
    }

    /**
     * Return whether the given attribute is joined as a result of a join on a mapping
     */
    protected boolean isAttributeMappingJoined(String attributeName) {
        return isAttributeNameInJoinedExpressionList(attributeName, getJoinedMappingExpressions());
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
        BaseExpression currentExpression = (BaseExpression)expression; 
        ArrayList<Expression> expressionBaseList = new ArrayList();
        do {
            expressionBaseList.add(currentExpression);
            currentExpression = (BaseExpression)currentExpression.getBaseExpression();
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
        for (int index = 0; index < getJoinedAttributeExpressions().size(); index++) {
            Expression expression = getJoinedAttributeExpressions().get(index);
            if(expression.isObjectExpression()) {
                ((ObjectExpression)expression).setShouldUseOuterJoinForMultitableInheritance(true);
            }
            prepareJoinExpression(expression, session);
        }
        for (int index = 0; index < getJoinedMappingExpressions().size(); index++) {
            Expression expression = getJoinedMappingExpressions().get(index);
            if(expression.isObjectExpression()) {
                ((ObjectExpression)expression).setShouldUseOuterJoinForMultitableInheritance(true);
            }
            prepareJoinExpression(expression, session);
        }
    }

    /**
     * Validate and prepare the join expression.
     */
    protected void prepareJoinExpression(Expression expression, AbstractSession session) {
        // Must be query key expression.
        if (!expression.isQueryKeyExpression()) {
            throw QueryException.mappingForExpressionDoesNotSupportJoining(expression);
        }
        QueryKeyExpression objectExpression = (QueryKeyExpression)expression;

        // Expression may not have been initialized.
        objectExpression.getBuilder().setSession(session.getRootSession(null));
        if (objectExpression.getBuilder().getQueryClass() == null){
            objectExpression.getBuilder().setQueryClass(this.descriptor.getJavaClass());
        }
        // Can only join relationships.
        if ((objectExpression.getMapping() == null) || (!objectExpression.getMapping().isJoiningSupported())) {
            throw QueryException.mappingForExpressionDoesNotSupportJoining(objectExpression);
        }

        // Search if any of the expression traverse a 1-m.
        ObjectExpression baseExpression = objectExpression;
        while (!baseExpression.isExpressionBuilder()) {
            if (((QueryKeyExpression)baseExpression).shouldQueryToManyRelationship()) {
                setIsToManyJoinQuery(true);
            }
            if (baseExpression.shouldUseOuterJoin()) {
                setIsOuterJoinedAttributeQuery(true);
            }
            baseExpression = (ObjectExpression)baseExpression.getBaseExpression();
        }
    }

    /**
     * This method collects the Joined Mappings from the descriptor and initializes them.
     * Excludes the mapping that are not in the passed mappingsAllowedToJoin set (if it's not null). 
     */
    public void processJoinedMappings() {
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
                        addJoinedMapping(mapping);
                    }
                }
            } else {
                for (int i = 0; i < mappingJoinedAttributes.size(); i++) {
                    ForeignReferenceMapping mapping = (ForeignReferenceMapping) mappingJoinedAttributes.get(i);
                    String attributeName = mapping.getAttributeName();
                    if (!isAttributeExpressionJoined(attributeName)) {
                        if(fetchGroupAttributes == null || fetchGroupAttributes.contains(attributeName)) {
                            addJoinedMapping(mapping);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Add the mapping join fetch.
     */
    public void addJoinedMapping(ForeignReferenceMapping mapping) {
        if (mapping.isCollectionMapping()) {
            Expression joinMappingExpression = null;
            if (mapping.isInnerJoinFetched()) {
                joinMappingExpression = getBaseExpressionBuilder().anyOf(mapping.getAttributeName());
            } else if (mapping.isOuterJoinFetched()) {
                joinMappingExpression = getBaseExpressionBuilder().anyOfAllowingNone(mapping.getAttributeName());
            }
            if (joinMappingExpression != null) {
                addJoinedMappingExpression(joinMappingExpression);
            }
        } else {
            if (mapping.isInnerJoinFetched()) {
                addJoinedMappingExpression(getBaseExpressionBuilder().get(mapping.getAttributeName()));
            } else if (mapping.isOuterJoinFetched()) {
                addJoinedMappingExpression(getBaseExpressionBuilder().getAllowingNull(mapping.getAttributeName()));
            }            
        }
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
