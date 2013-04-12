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
 *     05/5/2009-2.0 Guy Pelletier 
 *       - 248489: JPA 2.0 Pessimistic Locking/Lock Mode support
 *       - Allows the configuration of pessimistic locking from JPA entity manager
 *         functions (find, refresh, lock) and from individual query execution.
 *         A pessimistic lock can be issued with a lock timeout value as well, in
 *         which case, for those databases that support LOCK WAIT will cause
 *         a LockTimeoutException to be thrown if the query fails as a result of
 *         a timeout trying to acquire the lock. A PessimisticLockException is
 *         thrown otherwise.
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 *     09/21/2010-2.2 Frank Schwarz and ailitchev - Bug 325684 - QueryHints.BATCH combined with QueryHints.FETCH_GROUP_LOAD will cause NPE 
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.history.AsOfClause;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.expressions.FieldExpression;
import org.eclipse.persistence.internal.expressions.ForUpdateClause;
import org.eclipse.persistence.internal.expressions.ForUpdateOfClause;
import org.eclipse.persistence.internal.expressions.ObjectExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DeferredLockManager;
import org.eclipse.persistence.internal.helper.InvalidObject;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.history.UniversalAsOfClause;
import org.eclipse.persistence.internal.queries.DatabaseQueryMechanism;
import org.eclipse.persistence.internal.queries.ExpressionQueryMechanism;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.QueryByExampleMechanism;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * <p><b>Purpose</b>:
 * Abstract class for all read queries using objects.
 *
 * <p><b>Description</b>:
 * Contains common behavior for all read queries using objects.
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public abstract class ObjectLevelReadQuery extends ObjectBuildingQuery {
    /** Names of the possible lock mode types, JPA 1.0 and 2.0 */
    public static final String READ = "READ";
    public static final String WRITE = "WRITE";
    
    /** Names of the possible lock mode types, JPA 2.0 only */
    public static final String NONE = "NONE";
    public static final String PESSIMISTIC_ = "PESSIMISTIC_";
    public static final String PESSIMISTIC_READ = PESSIMISTIC_ + "READ";
    public static final String PESSIMISTIC_WRITE = PESSIMISTIC_ + "WRITE";
    public static final String PESSIMISTIC_FORCE_INCREMENT = PESSIMISTIC_ + "FORCE_INCREMENT";
    public static final String OPTIMISTIC = "OPTIMISTIC";
    public static final String OPTIMISTIC_FORCE_INCREMENT = "OPTIMISTIC_FORCE_INCREMENT";
    
    /** Provide a default builder so that it's easier to be consistent */
    protected ExpressionBuilder defaultBuilder;

    /** Allow for the cache usage to be specified to enable in-memory querying. */
    protected int cacheUsage;

    // Note: UseDescriptorSetting will result in CheckCacheByPrimaryKey for most cases
    // it simply allows the ClassDescriptor's disable cache hits to be used
    public static final int UseDescriptorSetting = -1;
    public static final int DoNotCheckCache = 0;
    public static final int CheckCacheByExactPrimaryKey = 1;
    public static final int CheckCacheByPrimaryKey = 2;
    public static final int CheckCacheThenDatabase = 3;
    public static final int CheckCacheOnly = 4;
    public static final int ConformResultsInUnitOfWork = 5;

    /**
     * Allow for additional fields to be selected, used for m-m batch reading.
     * Can contain DatabaseField or Expression.
     */
    protected List<Object> additionalFields;

    /** Allow for a complex result to be return including the rows and objects, used for m-m batch reading. */
    protected boolean shouldIncludeData;

    /** Allow a prePrepare stage to build the expression for EJBQL and QBE and resolve joining. */
    protected boolean isPrePrepared;

    /** Indicates if distinct should be used or not. */
    protected short distinctState;
    public static final short UNCOMPUTED_DISTINCT = 0;
    public static final short USE_DISTINCT = 1;
    public static final short DONT_USE_DISTINCT = 2;

    /**
     * Used to determine behavior of indirection in in-memory querying and conforming.
     */
    protected int inMemoryQueryIndirectionPolicy;

    /**
     * {@link FetchGroup} specified on this query. When set this FetchGroup will
     * override the {@link #fetchGroupName} and the use of the descriptor's
     * {@link FetchGroupManager#getDefaultFetchGroup()}
     */
    protected FetchGroup fetchGroup;

    /**
     * Name of {@link FetchGroup} stored in the {@link FetchGroupManager} of the
     * reference class' descriptor or any of its parent descriptors.
     */
    protected String fetchGroupName;

    /** Flag to turn on/off the use of the default fetch group. */
    protected boolean shouldUseDefaultFetchGroup = true;

    /** Specifies indirection that should be instantiated before returning result */
    protected LoadGroup loadGroup;
    
    /**
     * Stores the non fetchjoin attributes, these are joins that will be
     * represented in the where clause but not in the select.
     */
    protected List<Expression> nonFetchJoinAttributeExpressions;

    /**  Stores the partial attributes that have been added to this query */
    protected List<Expression> partialAttributeExpressions;
    
    /** Stores the helper object for dealing with joined attributes */
    protected JoinedAttributeManager joinedAttributeManager;
    
    /** Defines batch fetching configuration. */
    protected BatchFetchPolicy batchFetchPolicy;

    /** PERF: Caches locking policy isReferenceClassLocked setting. */
    protected Boolean isReferenceClassLocked;
    
    /** PERF: Allow queries to build directly from the database result-set. */
    protected boolean isResultSetOptimizedQuery = false;
    
    /** PERF: Allow queries to build while accessing the database result-set. Skips accessing result set non-pk fields in case the cached object is found. 
     If ResultSet optimization is used (isResultSetOptimizedQuery is set to true) then ResultSet Access optimization is ignored. */
    protected Boolean isResultSetAccessOptimizedQuery;
    
    /** If neither query specifies isResultSetOptimizedQuery nor session specifies shouldOptimizeResultSetAccess 
     * then this value is used to indicate whether optimization should be attempted 
     */
    public static boolean isResultSetAccessOptimizedQueryDefault = false;

    /** PERF: Indicates whether the query is actually using ResultSet optimization. If isResultSetOptimizedQuery==null set automatically before executing call. */
    protected transient Boolean usesResultSetAccessOptimization;
    
    /** PERF: Allow queries to be defined as read-only in unit of work execution. */
    protected boolean isReadOnly = false;
    
    /** Define if an outer join should be used to read subclasses. */
    protected Boolean shouldOuterJoinSubclasses;
    
    /** Allow concrete subclasses calls to be prepared and cached for inheritance queries. */
    protected Map<Class, DatabaseCall> concreteSubclassCalls;
    
    /** Allow concrete subclasses joined mapping indexes to be prepared and cached for inheritance queries. */
    protected Map<Class, Map<DatabaseMapping, Object>> concreteSubclassJoinedMappingIndexes;
    
    /** Used when specifying a lock mode for the query */
    protected String lockModeType;
    
    /**
     * waitTimeout has three possible setting: null, 0 and 1..N
     * null: use the session.getPessimisticLockTimeoutDefault() if available.
     * 0: issue a LOCK_NOWAIT
     * 1..N: use this value to set the WAIT clause.
     */
    protected Integer waitTimeout;
    
    /** Used for ordering support. */
    protected List<Expression> orderByExpressions;
    
    /** Indicates whether pessimistic lock should also be applied to relation tables (ManyToMany and OneToOne mappings),
     *  reference tables (DirectCollection and AggregateCollection mapping). 
     */
    protected boolean shouldExtendPessimisticLockScope;

    /**
     * Allow a query's results to be unioned (UNION, INTERSECT, EXCEPT) with another query results.
     */
    protected List<Expression> unionExpressions;

    /**
     * INTERNAL:
     * Initialize the state of the query
     */
    public ObjectLevelReadQuery() {
        this.shouldRefreshIdentityMapResult = false;
        this.distinctState = UNCOMPUTED_DISTINCT;
        this.cacheUsage = UseDescriptorSetting;
        this.shouldIncludeData = false;
        this.inMemoryQueryIndirectionPolicy = InMemoryQueryIndirectionPolicy.SHOULD_THROW_INDIRECTION_EXCEPTION;
        this.isCacheCheckComplete = false;
    }
    
    /**
     * PUBLIC:
     * Union the query results with the other query.
     */
    public void union(ReportQuery query) {
        addUnionExpression(getExpressionBuilder().union(query));
    }
    
    /**
     * PUBLIC:
     * Intersect the query results with the other query.
     */
    public void intersect(ReportQuery query) {
        addUnionExpression(getExpressionBuilder().intersect(query));
    }
    
    /**
     * PUBLIC:
     * Except the query results with the other query.
     */
    public void except(ReportQuery query) {
        addUnionExpression(getExpressionBuilder().except(query));
    }
    
    /**
     * PUBLIC:
     * Add the union expression to the query.
     * A union expression must be created with the query's expression builder
     * and one of union/unionAll/intersect/intersectAll/except/exceptAll with a subquery expression.
     */
    public void addUnionExpression(Expression union) {
        setIsPrepared(false);
        getUnionExpressions().add(union);
    }
    
    /**
     * Return any union expressions.
     */
    public List<Expression> getUnionExpressions() {
        if (unionExpressions == null) {
            unionExpressions = new ArrayList<Expression>();
        }
        return unionExpressions;
    }
    
    /**
     * INTERNAL:
     * Set any union expressions.
     */
    public void setUnionExpressions(List<Expression> unionExpressions) {
        this.unionExpressions = unionExpressions;
    }
    
    /**
     * PUBLIC:
     * Order the query results by the object's attribute or query key name.
     */
    public void addDescendingOrdering(String queryKeyName) {
        addOrdering(getExpressionBuilder().get(queryKeyName).descending());
    }

    /**
     * PUBLIC:
     * Add the ordering expression.  This allows for ordering across relationships or functions.
     * Example: readAllQuery.addOrdering(expBuilder.get("address").get("city").toUpperCase().descending())
     */
    public void addOrdering(Expression orderingExpression) {
        getOrderByExpressions().add(orderingExpression);
        //Bug2804042 Must un-prepare if prepared as the SQL may change.
        setIsPrepared(false);
        setShouldOuterJoinSubclasses(true);
    }

    /**
     * INTERNAL:
     * Return if the query is equal to the other.
     * This is used to allow dynamic expression query SQL to be cached.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if ((object == null) || (!getClass().equals(object.getClass())))  {
            return false;
        }
        ObjectLevelReadQuery query = (ObjectLevelReadQuery) object;
        // Only check expression queries for now.
        if ((!isExpressionQuery()) || (!isDefaultPropertiesQuery())) {
            return this == object;
        }
        if (!getExpressionBuilder().equals(query.getExpressionBuilder())) {
            return false;
        }
        if (this.distinctState != query.distinctState) {
            return false;
        }
        if (hasJoining()) {
            if (!query.hasJoining()) {
                return false;
            }
            List joinedAttributes = getJoinedAttributeManager().getJoinedAttributeExpressions();
            List otherJoinedAttributes = query.getJoinedAttributeManager().getJoinedAttributeExpressions();
            int size = joinedAttributes.size();
            if (size != otherJoinedAttributes.size()) {
                return false;
            }
            for (int index = 0; index < size; index++) {
                if (!joinedAttributes.get(index).equals(otherJoinedAttributes.get(index))) {
                    return false;
                }
            }
        } else if (query.hasJoining()) {
            return false;
        }
        if (hasOrderByExpressions()) {
            if (!query.hasOrderByExpressions()) {
                return false;
            }
            List orderBys = getOrderByExpressions();
            List otherOrderBys = query.getOrderByExpressions();
            int size = orderBys.size();
            if (size != otherOrderBys.size()) {
                return false;
            }
            for (int index = 0; index < size; index++) {
                if (!orderBys.get(index).equals(otherOrderBys.get(index))) {
                    return false;
                }
            }
        } else if (query.hasOrderByExpressions()) {
            return false;
        }
        if (! ((this.referenceClass == query.referenceClass) || ((this.referenceClass != null) && this.referenceClass.equals(query.referenceClass)))) {
            return false;
        }
        Expression selectionCriteria = getSelectionCriteria();
        Expression otherSelectionCriteria = query.getSelectionCriteria();
        return ((selectionCriteria == otherSelectionCriteria) || ((selectionCriteria != null) && selectionCriteria.equals(otherSelectionCriteria)));
    }
        
    /**
     * INTERNAL:
     * Compute a consistent hash-code for the expression.
     * This is used to allow dynamic expression's SQL to be cached.
     */
    @Override
    public int hashCode() {
        if (!isExpressionQuery()) {
            return super.hashCode();
        }
        int hashCode = 32;
        if (this.referenceClass != null) {
            hashCode = hashCode + this.referenceClass.hashCode();
        }
        Expression selectionCriteria = getSelectionCriteria();
        if (selectionCriteria != null) {
            hashCode = hashCode + selectionCriteria.hashCode();
        }
        return hashCode;
    }
    
    /**
     * PUBLIC:
     * Return if the query is read-only.
     * This allows queries executed against a UnitOfWork to be read-only.
     * This means the query will be executed against the Session,
     * and the resulting objects will not be tracked for changes.
     * The resulting objects are from the Session shared cache,
     * and must not be modified.
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }
        
    /**
     * PUBLIC:
     * Set the query to be read-only.
     * This allows queries executed against a UnitOfWork to be read-only.
     * This means the query will be executed against the Session,
     * and the resulting objects will not be tracked for changes.
     * The resulting objects are from the Session shared cache,
     * and must not be modified.
     */
    public void setIsReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }
    
    /**
     * PUBLIC:
     * Sets that this a pessimistic wait locking query.
     * <ul>
     * <li>ObjectBuildingQuery.LOCK: SELECT .... FOR UPDATE WAIT issued.
     * </ul>
     * <p>Fine Grained Locking: On execution the reference class and those of 
     * all joined attributes will be checked. If any of these have a 
     * PessimisticLockingPolicy set on their descriptor, they will be locked 
     * in a SELECT ... FOR UPDATE OF ... {NO WAIT}. Issues fewer locks
     * and avoids setting the lock mode on each query.
     * 
     * <p>Example:
     * <code>readAllQuery.setSelectionCriteria(employee.get("address").equal("Ottawa"));</code>
     * <ul>
     * <li>LOCK: all employees in Ottawa and all referenced Ottawa addresses 
     * will be locked and the lock will wait only the specified amount of time.
     * </ul>
     * @see org.eclipse.persistence.descriptors.PessimisticLockingPolicy
     */
    public void setWaitTimeout(Integer waitTimeout) {
        this.waitTimeout = waitTimeout;
        setIsPrePrepared(false);
        setIsPrepared(false);
        setWasDefaultLockMode(false);
    }
    
    /**
     * INTERNAL:
     * Clone the query
     */
    @Override
    public Object clone() {
        ObjectLevelReadQuery cloneQuery = (ObjectLevelReadQuery)super.clone();

        // Must also clone the joined expressions as always joined attribute will be added
        // don't use setters as this will trigger unprepare.
        if (this.joinedAttributeManager != null) {
            cloneQuery.joinedAttributeManager = this.joinedAttributeManager.clone();
            cloneQuery.joinedAttributeManager.setBaseQuery(cloneQuery);
        }
        if (this.batchFetchPolicy != null) {
            cloneQuery.batchFetchPolicy = this.batchFetchPolicy.clone();
        }
        if (this.nonFetchJoinAttributeExpressions != null){
            cloneQuery.nonFetchJoinAttributeExpressions = new ArrayList<Expression>(this.nonFetchJoinAttributeExpressions);
        }
        // Don't use setters as that will trigger unprepare
        if (this.orderByExpressions != null) {
            cloneQuery.orderByExpressions = new ArrayList<Expression>(this.orderByExpressions);
        }
        if (this.fetchGroup != null) {
            cloneQuery.fetchGroup = this.fetchGroup.clone();
            // don't clone immutable entityFetchGroup
        }
        cloneQuery.clearUsesResultSetAccessOptimization();
        return cloneQuery;
    }

    /**
     * INTERNAL:
     * Clone the query, including its selection criteria.
     * <p>
     * Normally selection criteria are not cloned here as they are cloned
     * later on during prepare.
     */
    @Override
    public Object deepClone() {
        ObjectLevelReadQuery clone = (ObjectLevelReadQuery)clone();
        if (getSelectionCriteria() != null) {
            clone.setSelectionCriteria((Expression)getSelectionCriteria().clone());
        } 
        if (defaultBuilder != null) {
            clone.defaultBuilder = (ExpressionBuilder)defaultBuilder.clone();
        }
        return clone;
    }

    /**
     * PUBLIC:
     * Set the query to lock, this will also turn refreshCache on.
     */
    public void acquireLocks() {
        setLockMode(LOCK);
        //Bug2804042 Must un-prepare if prepared as the SQL may change.
        setIsPrepared(false);
    }

    /**
     * PUBLIC:
     * Set the query to lock without waiting (blocking), this will also turn refreshCache on.
     */
    public void acquireLocksWithoutWaiting() {
        setLockMode(LOCK_NOWAIT);
        //Bug2804042 Must un-prepare if prepared as the SQL may change.
        setIsPrepared(false);
    }

    /**
     * INTERNAL:
     * Additional fields can be added to a query.  This is used in m-m batch reading to bring back the key from the join table.
     */
    public void addAdditionalField(DatabaseField field) {
        getAdditionalFields().add(field);
        // Bug2804042 Must un-prepare if prepared as the SQL may change.
        setIsPrepared(false);
    }

    /**
     * INTERNAL:
     * Additional fields can be added to a query.  This is used in m-m batch reading to bring back the key from the join table.
     */
    public void addAdditionalField(Expression fieldExpression) {
        getAdditionalFields().add(fieldExpression);
        // Bug2804042 Must un-prepare if prepared as the SQL may change.
        setIsPrepared(false);
    }

    /**
     * PUBLIC:
     * Specify the relationship attribute to be join fetched in this query.
     * The query will join the object(s) being read with the attribute,
     * this allows all of the data required for the object(s) to be read in a single query instead of (n) queries.
     * This should be used when the application knows that it requires the part for all of the objects being read.
     *
     * <p>Note: This cannot be used for objects where it is possible not to have a part,
     * as these objects will be omitted from the result set,
     * unless an outer join is used through passing and expression using "getAllowingNull".
     * To join fetch collection relationships use the addJoinedAttribute(Expression) using "anyOf" to "anyOfAllowingNone".
     *
     * <p>Example: query.addJoinedAttribute("address")
     *
     * @see #addJoinedAttribute(Expression)
     * @see ReadAllQuery#addBatchReadAttribute(Expression)
     */
    public void addJoinedAttribute(String attributeName) {
        addJoinedAttribute(getExpressionBuilder().get(attributeName));
    }

    /**
     * PUBLIC:
     * Specify the attribute to be join fetched in this query.
     * The query will join the object(s) being read with the specified attribute,
     * this allows all of the data required for the object(s) to be read in a single query instead of (n) queries.
     * This should be used when the application knows that it requires the part for all of the objects being read.
     *
     * <p>Note: This cannot be used for objects where it is possible not to have a part,
     * as these objects will be omitted from the result set,
     * unless an outer join is used through passing and expression using "getAllowingNull".
     *
     * <p>Example: 
     * The following will fetch along with Employee(s) "Jones" all projects they participate in
     * along with teamLeaders and their addresses, teamMembers and their phones.
     * 
     * query.setSelectionCriteria(query.getExpressionBuilder().get("lastName").equal("Jones"));
     * Expression projects = query.getExpressionBuilder().anyOf("projects");
     * query.addJoinedAttribute(projects);
     * Expression teamLeader = projects.get("teamLeader");
     * query.addJoinedAttribute(teamLeader);
     * Expression teamLeaderAddress = teamLeader.getAllowingNull("address");
     * query.addJoinedAttribute(teamLeaderAddress);
     * Expression teamMembers = projects.anyOf("teamMembers");
     * query.addJoinedAttribute(teamMembers);
     * Expression teamMembersPhones = teamMembers.anyOfAllowingNone("phoneNumbers");
     * query.addJoinedAttribute(teamMembersPhones);
     * 
     * Note that:
     * the order is essential: an expression should be added before any expression derived from it;
     * the object is built once - it won't be rebuilt if it to be read again as a joined attribute:
     * in the example the query won't get phones for "Jones" - 
     * even though they are among teamMembers (for whom phones are read).
     *
     */
    public void addJoinedAttribute(Expression attributeExpression) {
        getJoinedAttributeManager().addJoinedAttributeExpression(attributeExpression);
        // Bug2804042 Must un-prepare if prepared as the SQL may change.
        // Joined attributes are now calculated in prePrepare.
        setIsPrePrepared(false);
    }

    /**
     * PUBLIC:
     * Specify the relationship attribute to be join in this query.
     * This allows the query results to be filtered based on the relationship join.
     * The query will join the object(s) being read with the attribute.
     * The difference between this and a joined fetched attribute is that
     * it does not select the joined data nor populate the joined attribute,
     * it is only used to filter the query results.
     *
     * <p>Example: query.addNonFetchJoinedAttribute("address")
     *
     * @see #addNonFetchJoinedAttribute(Expression)
     */
    public void addNonFetchJoinedAttribute(String attributeName) {
        addNonFetchJoin(getExpressionBuilder().get(attributeName));
    }

    /**
     * PUBLIC:
     * Specify the relationship attribute to be join in this query.
     * This allows the query results to be filtered based on the relationship join.
     * The query will join the object(s) being read with the attribute.
     * The difference between this and a joined fetched attribute is that
     * it does not select the joined data nor populate the joined attribute,
     * it is only used to filter the query results.
     *
     * <p>Example: query.addNonFetchJoinedAttribute(query.getExpressionBuilder().get("teamLeader").get("address"))
     *
     * @see #addNonFetchJoinedAttribute(Expression)
     */
    public void addNonFetchJoinedAttribute(Expression attributeExpression) {
        addNonFetchJoin(attributeExpression);
    }

    /**
     * PUBLIC:
     * Specify the object expression to be joined in this query.
     * This allows the query results to be filtered based on the join to the object.
     * The object should define an on clause that defines the join condition.
     * This allows for two non-related objects to be joined.
     *
     * <p>Example: (select all employees that are a team leader)<p>
     * <pre>
     * ExpressionBuilder project = new ExpressionBuilder(Project.class);
     * ExpressionBuilder employee = new ExpressionBuilder(Employee.class);
     * ReadAllQuery query = new ReadAllQuery(Employee.class, employee);
     * query.addJoin(project.on(project.get("teamLeader").equal(employee)))
     * </pre>
     */
    public void addNonFetchJoin(Expression target) {
        getNonFetchJoinAttributeExpressions().add(target);
        
        // Bug 2804042 Must un-prepare if prepared as the SQL may change.
        // Joined attributes are now calculated in prePrepare.
        setIsPrePrepared(false);
    }

    /**
     * PUBLIC:
     * Specify that only a subset of the class' attributes be selected in this query.
     * <p>
     * This allows for the query to be optimized through selecting less data.
     * <p>
     * Partial objects will be returned from the query, where the unspecified attributes will be left <code>null</code>.
     * The primary key will always be selected to allow re-querying of the whole object.
     * <p>Note: Because the object is not fully initialized it cannot be cached, and cannot be edited.
     * <p>Note: You cannot have 2 partial attributes of the same type.  You also cannot
     * add a partial attribute which is of the same type as the class being queried.
     * <p><b>Example</b>: query.addPartialAttribute("firstName")
     * @see #addPartialAttribute(Expression)
     * @deprecated since EclipseLink 2.1, partial attributes replaced by fetch groups.
     * @see FetchGroup
     * Example:
     * FetchGroup fetchGroup = new FetchGroup();
     * fetchGroup.addAttribute("address.city"); 
     * query.setFetchGroup(fetchGroup);
     */
    public void addPartialAttribute(String attributeName) {
        addPartialAttribute(getExpressionBuilder().get(attributeName));
    }

    /**
     * INTERNAL:
     * The method adds to the passed input vector the
     * fields or expressions corresponding to the passed join expression.
     */
    protected void addSelectionFieldsForJoinedExpression(List fields, boolean isCustomSQL, Expression expression) {
        if(isCustomSQL) {
            // Expression may not have been initialized.
            ExpressionBuilder builder = expression.getBuilder();
            builder.setSession(getSession().getRootSession(null));
            builder.setQueryClass(getReferenceClass());
        }
        ForeignReferenceMapping mapping = (ForeignReferenceMapping)((QueryKeyExpression)expression).getMapping();
        ClassDescriptor referenceDescriptor = mapping.getReferenceDescriptor();

        // Add the fields defined by the nested fetch group - if it exists.
        if (referenceDescriptor != null && referenceDescriptor.hasFetchGroupManager()) {
            ObjectLevelReadQuery nestedQuery = getJoinedAttributeManager().getNestedJoinedMappingQuery(expression);
            FetchGroup nestedFetchGroup = nestedQuery.getExecutionFetchGroup();
            if(nestedFetchGroup != null) {
                List<DatabaseField> nestedFields = nestedQuery.getFetchGroupSelectionFields(mapping);
                for(DatabaseField field : nestedFields) {
                    fields.add(new FieldExpression(field, expression));
                }
                return;
            }
        }

        if(isCustomSQL) {
            if(referenceDescriptor != null) {
                fields.addAll(referenceDescriptor.getAllFields());
            } else {
                fields.add(expression);
            }
        } else {
            fields.add(expression);
        }
    }

    /**
     * ADVANCED: Sets the query to execute as of the past time.
     * Both the query execution and result will conform to the database as it
     * existed in the past.
     * <p>
     * Equivalent to query.getSelectionCriteria().asOf(pastTime) called
     * immediately before query execution.
     * <p>An as of clause at the query level will override any clauses set at the
     * expression level.  Useful in cases where the selection criteria is not known in
     * advance, such as for query by example or primary key (selection object), or
     * where you do not need to cache the result (report query).
     * <p>Ideally an as of clause at the session level is superior as query
     * results can then be cached.  You must set
     * {@link org.eclipse.persistence.queries.ObjectLevelReadQuery#setShouldMaintainCache setShouldMaintainCache(false)}
     * <p>To query all joined/batched attributes as of the same time set
     * this.{@link org.eclipse.persistence.queries.ObjectLevelReadQuery#cascadeAllParts cascadeAllParts()}.
     * @throws QueryException (at execution time) unless
     * <code>setShouldMaintainCache(false)</code> is set.  If some more recent
     * data were in the cache, this would be returned instead, and both the
     * cache and query result would become inconsistent.
     * @since OracleAS TopLink 10<i>g</i> (10.0.3)
     * @see #hasAsOfClause
     * @see org.eclipse.persistence.sessions.Session#acquireHistoricalSession(org.eclipse.persistence.history.AsOfClause)
     * @see org.eclipse.persistence.expressions.Expression#asOf(org.eclipse.persistence.history.AsOfClause)
     */
    public void setAsOfClause(AsOfClause pastTime) {
        getExpressionBuilder().asOf(new UniversalAsOfClause(pastTime));
        setIsPrepared(false);
    }

    /**
     * PUBLIC:
     * Specify that only a subset of the class' attributes be selected in this query.
     * <p>This allows for the query to be optimized through selecting less data.
     * <p>Partial objects will be returned from the query, where the unspecified attributes will be left <code>null</code>.
     * The primary key will always be selected to allow re-querying of the whole object.
     * <p>Note: Because the object is not fully initialized it cannot be cached, and cannot be edited.
     * <p>Note: You cannot have 2 partial attributes of the same type.  You also cannot
     * add a partial attribute which is of the same type as the class being queried.
     * <p><b>Example</b>: query.addPartialAttribute(query.getExpressionBuilder().get("address").get("city"))
     * @deprecated since EclipseLink 2.1, partial attributes replaced by fetch groups.
     * @see FetchGroup
     * Example:
     * FetchGroup fetchGroup = new FetchGroup();
     * fetchGroup.addAttribute("address.city"); 
     * query.setFetchGroup(fetchGroup);
     */
    public void addPartialAttribute(Expression attributeExpression) {
        getPartialAttributeExpressions().add(attributeExpression);
        //Bug2804042 Must un-prepare if prepared as the SQL may change.
        setIsPrepared(false);
    }

    /**
     * INTERNAL:
     * Used to build the object, and register it if in the context of a unit of work. 
     */
    @Override
    public Object buildObject(AbstractRecord row) {
        return this.descriptor.getObjectBuilder().buildObject(this, row);
    }

    /**
     * PUBLIC:
     * The cache will checked completely, if the object is not found null will be returned or an error if the query is too complex.
     * Queries can be configured to use the cache at several levels.
     * Other caching option are available.
     * @see #setCacheUsage(int)
     */
    public void checkCacheOnly() {
        setCacheUsage(CheckCacheOnly);
    }

    /**
     * INTERNAL:
     * Ensure that the descriptor has been set.
     */
    @Override
    public void checkDescriptor(AbstractSession session) throws QueryException {
        if (this.descriptor == null) {
            if (getReferenceClass() == null) {
                throw QueryException.referenceClassMissing(this);
            }
            ClassDescriptor referenceDescriptor = session.getDescriptor(getReferenceClass());
            if (referenceDescriptor == null) {
                throw QueryException.descriptorIsMissing(getReferenceClass(), this);
            }
            setDescriptor(referenceDescriptor);
        }
    }

    /**
     * INTERNAL:
     * Contains the body of the check early return call, implemented by subclasses.
     */
    protected abstract Object checkEarlyReturnLocal(AbstractSession session, AbstractRecord translationRow);

    /**
     * INTERNAL:
     * Check to see if this query already knows the return value without performing any further work.
     */
    @Override
    public Object checkEarlyReturn(AbstractSession session, AbstractRecord translationRow) {
        // For bug 3136413/2610803 building the selection criteria from an EJBQL string or
        // an example object is done just in time.
        // Also calls checkDescriptor here.
        //buildSelectionCriteria(session);
        checkPrePrepare(session);

        if (!session.isUnitOfWork()) {
            return checkEarlyReturnLocal(session, translationRow);
        }
        UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)session;

        // The cache check must happen on the UnitOfWork in these cases either
        // to access transient state or for pessimistic locking, as only the 
        // UOW knows which objects it has locked.
        Object result = null;
        // PERF: Avoid uow check for read-only.
        if (!this.descriptor.shouldBeReadOnly()) {
            result = checkEarlyReturnLocal(unitOfWork, translationRow);
        }
        if (result != null) {
            return result;
        }

        // PERF: If a locking query, or isolated always, then cache is ignored, so no point checking.
        // An error should be thrown on prepare is checkCacheOnly is used with these.
        if ((!unitOfWork.isNestedUnitOfWork()) && (this.descriptor.getCachePolicy().shouldIsolateObjectsInUnitOfWork()  || isLockQuery()) || unitOfWork.shouldForceReadFromDB(this, null)) {
            return null;
        }

        // follow the execution path in looking for the object.
        AbstractSession parentSession = unitOfWork.getParentIdentityMapSession(this);

        // assert parentSession != unitOfWork;
        result = checkEarlyReturn(parentSession, translationRow);

        if (result != null) {
            // Optimization: If find deleted object by exact primary key
            // treat this as cache hit but return null.  Bug 2782991.
            if (result == InvalidObject.instance) {
                return result;
            }
            Object clone = registerResultInUnitOfWork(result, unitOfWork, translationRow, false);
            if (shouldConformResultsInUnitOfWork() && unitOfWork.isObjectDeleted(clone)) {
                return InvalidObject.instance;
            }
            return clone;
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     * Check to see if this query needs to be prepare and prepare it.
     * The prepare is done on the original query to ensure that the work is not repeated.
     */
    @Override
    public void checkPrepare(AbstractSession session, AbstractRecord translationRow, boolean force) {
        // CR#3823735 For custom queries the prePrepare may not have been called yet.
        if (!this.isPrePrepared || (this.descriptor == null)) {
            checkPrePrepare(session);
        }
        super.checkPrepare(session, translationRow, force);
    }

    /**
     * INTERNAL:
     * ObjectLevelReadQueries now have an explicit pre-prepare stage, which
     * is for checking for pessimistic locking, and computing any joined
     * attributes declared on the descriptor.
     */
    public void checkPrePrepare(AbstractSession session) {
        try {
            // This query is first prepared for global common state, this must be synced.
            if (!this.isPrePrepared) {// Avoid the monitor is already prePrepare, must check again for concurrency.      
                synchronized (this) {
                    if (!isPrePrepared()) {
                        AbstractSession alreadySetSession = getSession();
                        setSession(session);// Session is required for some init stuff.
                        prePrepare();
                        setSession(alreadySetSession);
                        setIsPrePrepared(true);// MUST not set prepare until done as other thread may hit before finishing the prePrepare.
                    }
                }
            } else if (this.descriptor == null) {        
                // Must always check descriptor as transient for remote.
                checkDescriptor(session);
            }
        } catch (QueryException knownFailure) {
            // Set the query, as preprepare can be called directly.
            if (knownFailure.getQuery() == null) {
                knownFailure.setQuery(this);
                knownFailure.setSession(session);
            }
            throw knownFailure;
        }
    }

    /**
     * INTERNAL:
     * The reference class has been changed, need to reset the
     * descriptor. Null out the current descriptor and call
     * checkDescriptor
     * Added Feb 27, 2001 JED for EJBQL feature
     */
    public void changeDescriptor(AbstractSession theSession) {
        setDescriptor(null);
        checkDescriptor(theSession);
    }

    /**
     * INTERNAL:
     * Conforms and registers an individual result.  This instance could be one
     * of the elements returned from a read all query, the result of a Read Object
     * query, or an element read from a cursor.
     * <p>
     * A result needs to be registered before it can be conformed, so
     * registerIndividualResult is called here.
     * <p>
     * Conforming on a result from the database is lenient.  Since the object
     * matched the query on the database we assume it matches here unless we can
     * determine for sure that it was changed in this UnitOfWork not to conform.
     * @param result may be an original, or a raw database row
     * @param arguments the parameters this query was executed with
     * @param selectionCriteriaClone the expression to conform to.  If was a
     * selection object or key, null (which all conform to) is used
     * @param alreadyReturned a hashtable of objects already found by scanning
     * the UnitOfWork cache for conforming instances.  Prevents duplicates.
     * @param buildDirectlyFromRows whether result is an original or a raw database
     * row
     * @return a clone, or null if result does not conform.
     */
    protected Object conformIndividualResult(Object clone, UnitOfWorkImpl unitOfWork, AbstractRecord arguments, Expression selectionCriteriaClone, Map alreadyReturned) {
        if (this.descriptor.hasWrapperPolicy() && this.descriptor.getWrapperPolicy().isWrapped(clone)) {
            // The only time the clone could be wrapped is if we are not registering
            // results in the unitOfWork and we are ready to return a final
            // (unregistered) result now.  Any further processing may accidently
            // cause it to get registered.
            return clone;
        }
        //bug 4459976 in order to maintain backward compatibility on ordering
        // lets use the result as a guild for the final result not the hashtable
        // of found objects.
        if (unitOfWork.isObjectDeleted(clone) ) {
            return null;
        }
        // No need to conform if no expression, or primary key query.
        if (!isExpressionQuery() || (selectionCriteriaClone == null) || isPrimaryKeyQuery()) {
            if (alreadyReturned != null) {
                alreadyReturned.remove(clone);
            }
            return clone;
        }
        try {
            // pass in the policy to assume that the object conforms if indirection is not triggered.  This
            // is valid because the query returned the object and we should trust the query that the object
            // matches the selection criteria, and because the indirection is not triggered then the customer
            //has not changed the value.
            // bug 2637555
            // unless for bug 3568141 use the painstaking shouldTriggerIndirection if set
            int policy = getInMemoryQueryIndirectionPolicyState();
            if (policy != InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION) {
                policy = InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_CONFORMED;
            }
            if (selectionCriteriaClone.doesConform(clone, unitOfWork, arguments, policy)) {
                 if (alreadyReturned != null) {
                    alreadyReturned.remove(clone);
                }
               return clone;
            }
        } catch (QueryException exception) {
            // bug 3570561: mask all-pervasive valueholder exceptions while conforming
            if ((unitOfWork.getShouldThrowConformExceptions() == UnitOfWorkImpl.THROW_ALL_CONFORM_EXCEPTIONS) && (exception.getErrorCode() != QueryException.MUST_INSTANTIATE_VALUEHOLDERS)) {
                throw exception;
            }
            if (alreadyReturned != null) {
                alreadyReturned.remove(clone);
            }
            return clone;
        }
        return null;
    }

    /**
     * PUBLIC:
     * The cache will checked completely, if the object is not found the database will be queried,
     * and the database result will be verified with what is in the cache and/or unit of work including new objects.
     * This can lead to poor performance so it is recommended that only the database be queried in most cases.
     * Queries can be configured to use the cache at several levels.
     * Other caching option are available.
     * @see #setCacheUsage(int)
     */
    public void conformResultsInUnitOfWork() {
        setCacheUsage(ConformResultsInUnitOfWork);
    }

    /**
     * PUBLIC:
     * Set the query not to lock.
     */
    public void dontAcquireLocks() {
        setLockMode(NO_LOCK);
        //Bug2804042 Must un-prepare if prepared as the SQL may change.
        setIsPrepared(false);
    }

    /**
     * PUBLIC:
     * This can be used to explicitly disable the cache hit.
     * The cache hit may not be desired in some cases, such as
     * stored procedures that accept the primary key but do not query on it.
     */
    public void dontCheckCache() {
        setCacheUsage(DoNotCheckCache);
    }

    /**
     * PUBLIC:
     * When unset means perform read normally and dont do refresh.
     */
    public void dontRefreshIdentityMapResult() {
        setShouldRefreshIdentityMapResult(false);
    }

    /**
     * PUBLIC:
     * When unset means perform read normally and dont do refresh.
     */
    public void dontRefreshRemoteIdentityMapResult() {
        setShouldRefreshRemoteIdentityMapResult(false);
    }

    /**
     * ADVANCED:
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is used internally by EclipseLink for batch reading but may also be
     * used directly for advanced queries or report queries.
     */
    public void dontUseDistinct() {
        setDistinctState(DONT_USE_DISTINCT);
        //Bug2804042 Must un-prepare if prepared as the SQL may change.
        setIsPrepared(false);
    }

    /**
     * INTERNAL:
     * There is a very special case where a query may be a bean-level
     * pessimistic locking query.
     * <p>
     * If that is so, only queries executed inside of a UnitOfWork should
     * have a locking clause.  In the extremely rare case that we execute
     * a locking query outside of a UnitOfWork, must disable locking so that
     * we do not get a fetch out of sequence error.
     */
    public DatabaseQuery prepareOutsideUnitOfWork(AbstractSession session) {
        // Implementation is complicated because: if locking refresh will be
        // auto set to true preventing cache hit.
        // Must prepare this query from scratch if outside uow but locking
        // Must not reprepare this query as a NO_LOCK, but create a clone first
        // Must not cloneAndUnPrepare unless really have to
        if (isLockQuery(session) && getLockingClause().isForUpdateOfClause()) {
            ObjectLevelReadQuery clone = (ObjectLevelReadQuery)clone();
            clone.setIsExecutionClone(true);
            clone.dontAcquireLocks();
            clone.setIsPrepared(false);
            clone.checkPrePrepare(session);
            return clone;
        }
        return this;
    }

    /**
     * INTERNAL:
     * Execute the query. If there are objects in the cache  return the results
     * of the cache lookup.
     *
     * @param session - the session in which the receiver will be executed.
     * @exception  DatabaseException - an error has occurred on the database.
     * @exception  OptimisticLockException - an error has occurred using the optimistic lock feature.
     * @return An object, the result of executing the query.
     */
    @Override
    public Object execute(AbstractSession session, AbstractRecord translationRow) throws DatabaseException, OptimisticLockException {
        //Bug#2839852  Refreshing is not possible if the query uses checkCacheOnly.
        if (shouldRefreshIdentityMapResult() && shouldCheckCacheOnly()) {
            throw QueryException.refreshNotPossibleWithCheckCacheOnly(this);
        }
        
        return super.execute(session, translationRow);
    }

    /**
     * INTERNAL:
     * Executes the prepared query on the datastore.
     */
    @Override
    public Object executeDatabaseQuery() throws DatabaseException {
        // PERF: If the query has been set to optimize building its result
        // directly from the database result-set then follow an optimized path.
        if (this.isResultSetOptimizedQuery) {
            return executeObjectLevelReadQueryFromResultSet();
        }
        
        if (this.session.isUnitOfWork()) {
            UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)this.session;

            // Note if a nested unit of work this will recursively start a
            // transaction early on the parent also.
            if (isLockQuery()) {
                if ((!unitOfWork.getCommitManager().isActive()) && (!unitOfWork.wasTransactionBegunPrematurely())) {
                    unitOfWork.beginTransaction();
                    unitOfWork.setWasTransactionBegunPrematurely(true);
                }
            }
            if (unitOfWork.isNestedUnitOfWork()) {
                UnitOfWorkImpl nestedUnitOfWork = (UnitOfWorkImpl)this.session;
                setSession(nestedUnitOfWork.getParent());
                Object result = executeDatabaseQuery();
                setSession(nestedUnitOfWork);
                return registerResultInUnitOfWork(result, nestedUnitOfWork, getTranslationRow(), false);
            }
        }
        
        this.session.validateQuery(this);// this will update the query with any settings

        if (this.queryId == 0) {
            this.queryId = this.session.getNextQueryId();
        }

        Object result = executeObjectLevelReadQuery();
        if (result != null) {
            if (getLoadGroup() != null) {
                Object resultToLoad  = result;
                if (this.shouldIncludeData) {
                    resultToLoad = ((ComplexQueryResult)result).getResult();
                }
                session.load(resultToLoad, getLoadGroup());
            } else {
                FetchGroup executionFetchGroup = getExecutionFetchGroup(); 
                if (executionFetchGroup != null) {
                    LoadGroup lg = executionFetchGroup.toLoadGroupLoadOnly();
                    if (lg != null) {
                        Object resultToLoad  = result;
                        if (this.shouldIncludeData) {
                            resultToLoad = ((ComplexQueryResult)result).getResult();
                        }
                        session.load(resultToLoad, lg);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Executes the prepared query on the datastore.
     */
    protected abstract Object executeObjectLevelReadQuery() throws DatabaseException;
    
    /**
     * Executes the prepared query on the datastore.
     */
    protected abstract Object executeObjectLevelReadQueryFromResultSet() throws DatabaseException;

    /**
     * INTERNAL:
     * Execute the query in the unit of work.
     * This allows any pre-execute checks to be done for unit of work queries.
     */
    @Override
    public Object executeInUnitOfWork(UnitOfWorkImpl unitOfWork, AbstractRecord translationRow) throws DatabaseException, OptimisticLockException {        
        Object result = null;
        
        if (!shouldMaintainCache() || isReadOnly()) {
            result = unitOfWork.getParent().executeQuery(this, translationRow);
        } else {
            result = execute(unitOfWork, translationRow);
        }
            
        // If a lockModeType was set (from JPA) we need to check if we need
        // to perform a force update to the version field.
        if (lockModeType != null && result != null) {
            if (lockModeType.equals(READ) || lockModeType.equals(WRITE) || lockModeType.contains(OPTIMISTIC) || lockModeType.equals(PESSIMISTIC_FORCE_INCREMENT)) {
                boolean forceUpdateToVersionField = lockModeType.equals(WRITE) || lockModeType.equals(OPTIMISTIC_FORCE_INCREMENT) || lockModeType.equals(PESSIMISTIC_FORCE_INCREMENT);
                    
                if (result instanceof Collection) {
                    Iterator i = ((Collection) result).iterator();
                    while (i.hasNext()) {
                        Object obj = i.next();
                            
                        if (obj != null) {
                            // If it is a report query the result could be an array of objects. Must
                            // also deal with null results.
                            if (obj instanceof Object[]) {    
                                for (Object o : (Object[]) obj) {
                                    if (o != null) {
                                        unitOfWork.forceUpdateToVersionField(o, forceUpdateToVersionField);
                                    }
                                }
                            } else {
                                unitOfWork.forceUpdateToVersionField(obj, forceUpdateToVersionField);
                            }
                        }
                    }
                } else {
                    unitOfWork.forceUpdateToVersionField(result, forceUpdateToVersionField);
                }
            }
        }
        
        return result;
    }

    /**
     * INTERNAL:
     * Additional fields can be added to a query.  This is used in m-m batch reading to bring back the key from the join table.
     */
    public List<Object> getAdditionalFields() {
        if (this.additionalFields == null) {
            this.additionalFields = new ArrayList<Object>();
        }
        return this.additionalFields;
    }

    /**
     * ADVANCED:
     * Answers the past time this query is as of.
     * @return An immutable object representation of the past time.
     * <code>null</code> if no clause set, <code>AsOfClause.NO_CLAUSE</code> if
     * clause explicitly set to <code>null</code>.
     * @see org.eclipse.persistence.history.AsOfClause
     * @see #setAsOfClause(org.eclipse.persistence.history.AsOfClause)
     * @see #hasAsOfClause
     */
    public AsOfClause getAsOfClause() {
        return (defaultBuilder != null) ? defaultBuilder.getAsOfClause() : null;
    }

    /**
     * PUBLIC:
     * Return the cache usage.
     * By default only primary key read object queries will first check the cache before accessing the database.
     * Any query can be configure to query against the cache completely, by key or ignore the cache check.
     * <p>Valid values are:
     * <ul>
     * <li> DoNotCheckCache
     * <li> CheckCacheByExactPrimaryKey
     * <li> CheckCacheByPrimaryKey
     * <li> CheckCacheThenDatabase
     * <li> CheckCacheOnly
     * <li> ConformResultsInUnitOfWork
     * <li> UseDescriptorSetting
     * Note: UseDescriptorSetting functions like CheckCacheByPrimaryKey, except checks the appropriate descriptor's
     * shouldDisableCacheHits setting when querying on the cache.
     * </ul>
     */
    public int getCacheUsage() {
        return cacheUsage;
    }

    /**
     * ADVANCED:
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is used internally by EclipseLink for batch reading but may also be
     * used directly for advanced queries or report queries.
     */
    public short getDistinctState() {
        return distinctState;
    }

    /**
     * PUBLIC:
     * This method returns the current example object.  The "example" object is an actual domain object, provided
     * by the client, from which an expression is generated.
     * This expression is used for a query of all objects from the same class, that match the attribute values of
     * the "example" object.
     */
    public Object getExampleObject() {
        if (getQueryMechanism().isQueryByExampleMechanism()) {
            return ((QueryByExampleMechanism)getQueryMechanism()).getExampleObject();
        } else {
            return null;
        }
    }

    /**
     * REQUIRED:
     * Get the expression builder which should be used for this query.
     * This expression builder should be used to build all expressions used by this query.
     */
    public ExpressionBuilder getExpressionBuilder() {
        if (defaultBuilder == null) {
            initializeDefaultBuilder();
        }

        return defaultBuilder;
    }

    /**
     * INTERNAL
     * Sets the default expression builder for this query.
     */
    public void setExpressionBuilder(ExpressionBuilder builder) {
        this.defaultBuilder = builder;
    }

    /**
     * PUBLIC:
     * Returns the InMemoryQueryIndirectionPolicy for this query
     */
    public int getInMemoryQueryIndirectionPolicyState() {
        return this.inMemoryQueryIndirectionPolicy;
    }
    
    /**
     * PUBLIC:
     * Returns the InMemoryQueryIndirectionPolicy for this query
     */
    public InMemoryQueryIndirectionPolicy getInMemoryQueryIndirectionPolicy() {
        return new InMemoryQueryIndirectionPolicy(inMemoryQueryIndirectionPolicy, this);
    }

    /**
     * INTERNAL:
     * Return join manager responsible for managing all aspects of joining for the query.
     * Queries without joining should not have a joinedAttributeManager.
     */
    public JoinedAttributeManager getJoinedAttributeManager() {
        if (this.joinedAttributeManager == null) {
            this.joinedAttributeManager = new JoinedAttributeManager(getDescriptor(), getExpressionBuilder(), this);
        }
        return this.joinedAttributeManager;
    }
    
    /**
     * INTERNAL:
     * Set join manager responsible for managing all aspects of joining for the query.
     */
    public void setJoinedAttributeManager(JoinedAttributeManager joinedAttributeManager) {
        this.joinedAttributeManager = joinedAttributeManager;
    }
    
    /**
     * INTERNAL:
     * Return if any attributes are joined.
     * To avoid the initialization of the JoinedAttributeManager this should be first checked before accessing.
     */
    public boolean hasJoining() {
        return this.joinedAttributeManager != null;
    }

    /**
     * INTERNAL:
     * Convenience method for project mapping.
     */
    public List getJoinedAttributeExpressions() {
        return getJoinedAttributeManager().getJoinedAttributeExpressions();
    }
 
    /**
     * INTERNAL:
     * Convenience method for project mapping.
     */
    public void setJoinedAttributeExpressions(List expressions) {
        if (((expressions != null) && !expressions.isEmpty()) || hasJoining()) {
            getJoinedAttributeManager().setJoinedAttributeExpressions_(expressions);
        }
    }
            
    /**
     * INTERNAL:
     * Return the order expressions for the query.
     */
    public List<Expression> getOrderByExpressions() {
        if (orderByExpressions == null) {
            orderByExpressions = new ArrayList<Expression>();
        }
        return orderByExpressions;
    }

    /**
     * INTERNAL:
     * Set the order expressions for the query.
     */
    public void setOrderByExpressions(List<Expression> orderByExpressions) {
        this.orderByExpressions = orderByExpressions;
    }

    /**
     * INTERNAL:
     * The order bys are lazy initialized to conserve space.
     */
    public boolean hasOrderByExpressions() {
        return (orderByExpressions != null) && (!orderByExpressions.isEmpty());
    }

    /**
     * INTERNAL:
     * The unions are lazy initialized to conserve space.
     */
    public boolean hasUnionExpressions() {
        return (unionExpressions != null) && (!unionExpressions.isEmpty());
    }

    /**
     * PUBLIC:
     * Return if duplicate rows should be filter when using 1-m joining.
     */
    public boolean shouldFilterDuplicates() {
        if (hasJoining()) {
            return getJoinedAttributeManager().shouldFilterDuplicates();
        }
        return true;
    }
        
    /**
     * PUBLIC:
     * Set if duplicate rows should be filter when using 1-m joining.
     */
    public void setShouldFilterDuplicates(boolean shouldFilterDuplicates) {
        getJoinedAttributeManager().setShouldFilterDuplicates(shouldFilterDuplicates);
    }    

    /**
     * INTERNAL:
     * It is not exactly as simple as a query being either locking or not.
     * Any combination of the reference class object and joined attributes
     * may be locked.
     */
    public ForUpdateClause getLockingClause() {
        return lockingClause;
    }

    /**
     * INTERNAL:
     * Return the attributes that must be joined, but not fetched, that is,
     * do not trigger the value holder.
     */
    public List<Expression> getNonFetchJoinAttributeExpressions() {
        if (this.nonFetchJoinAttributeExpressions == null){
            this.nonFetchJoinAttributeExpressions = new ArrayList<Expression>();
        }
        return nonFetchJoinAttributeExpressions;
    }

    /**
     * INTERNAL:
     * Return the partial attributes to select.
     */
    public List<Expression> getPartialAttributeExpressions() {
        if (this.partialAttributeExpressions == null) {
            this.partialAttributeExpressions = new ArrayList<Expression>();
        }
        return this.partialAttributeExpressions;
    }

    /**
     * PUBLIC:
     * When using Query By Example, an instance of QueryByExamplePolicy is used to customize the query.
     * The policy is useful when special operations are to be used for comparisons (notEqual, lessThan,
     * greaterThan, like etc.), when a certain value is to be ignored, or when dealing with nulls.
     */
    public QueryByExamplePolicy getQueryByExamplePolicy() {
        if (getQueryMechanism().isQueryByExampleMechanism()) {
            return ((QueryByExampleMechanism)getQueryMechanism()).getQueryByExamplePolicy();
        } else {
            return null;
        }
    }

    /**
     * PUBLIC:
     * Return the reference class of the query.
     */
    @Override
    public Class getReferenceClass() {
        return referenceClass;
    }

    /**
     * INTERNAL:
     * Return the reference class of the query.
     */
    public String getReferenceClassName() {
        if ((referenceClassName == null) && (referenceClass != null)) {
            referenceClassName = referenceClass.getName();
        }
        return referenceClassName;
    }

    /**
     * PUBLIC:
     * Answers if the domain objects are to be read as of a past time.
     * @see #getAsOfClause
     */
    public boolean hasAsOfClause() {
        return ((defaultBuilder != null) && defaultBuilder.hasAsOfClause());
    }

    /**
     * INTERNAL:
     * Return the attributes that must be joined.
     */
    public boolean hasNonFetchJoinedAttributeExpressions() {
        return this.nonFetchJoinAttributeExpressions != null && !this.nonFetchJoinAttributeExpressions.isEmpty();
    }

    /**
     * INTERNAL:
     * Return if partial attributes.
     */
    public boolean hasPartialAttributeExpressions() {
        return (this.partialAttributeExpressions != null) && (!this.partialAttributeExpressions.isEmpty());
    }
    
    /**
     * INTERNAL:
     * Return if additional field.
     */
    public boolean hasAdditionalFields() {
        return (this.additionalFields != null) && (!this.additionalFields.isEmpty());
    }
    
    /**
     * INTERNAL:
     * Return the fields required in the select clause, for patial attribute reading.
     */
    public Vector getPartialAttributeSelectionFields(boolean isCustomSQL) {
        Vector localFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(getPartialAttributeExpressions().size());
        Vector foreignFields = null;
        
        //Add primary key and indicator fields.
        localFields.addAll(getDescriptor().getPrimaryKeyFields());
        if (getDescriptor().hasInheritance() && (getDescriptor().getInheritancePolicy().getClassIndicatorField() != null)) {
            localFields.addElement(getDescriptor().getInheritancePolicy().getClassIndicatorField());
        }

        //Add attribute fields
        for(Iterator it = getPartialAttributeExpressions().iterator();it.hasNext();){
            Expression expression = (Expression)it.next();
            if (expression.isQueryKeyExpression()) {
                ((QueryKeyExpression)expression).getBuilder().setSession(session.getRootSession(null));
                ((QueryKeyExpression)expression).getBuilder().setQueryClass(getDescriptor().getJavaClass());
                DatabaseMapping mapping = ((QueryKeyExpression)expression).getMapping();
                if (!((QueryKeyExpression)expression).getBaseExpression().isExpressionBuilder()) {
                    if(foreignFields==null){
                        foreignFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
                    }
                    if(!isCustomSQL){
                        foreignFields.add(expression);
                    }else{
                        foreignFields.addAll(expression.getFields());
                    }
                }else{
                    if (mapping == null) {
                        throw QueryException.specifiedPartialAttributeDoesNotExist(this, expression.getName(), descriptor.getJavaClass().getName());
                    }
                    if(mapping.isForeignReferenceMapping() ){
                        if(foreignFields==null){
                            foreignFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
                        }
                        if(!isCustomSQL){
                            foreignFields.add(expression);
                        }else{
                            foreignFields.addAll(expression.getFields());
                        }
                    }else{
                        localFields.addAll(expression.getFields());
                    }
                }
            } else {
                throw QueryException.expressionDoesNotSupportPartialAttributeReading(expression);
            }
        }
        //Build fields in same order as the fields of the descriptor to ensure field and join indexes match.
        Vector selectionFields = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        for (Iterator iterator = getDescriptor().getFields().iterator(); iterator.hasNext();) {
            DatabaseField field = (DatabaseField)iterator.next();
            if (localFields.contains(field)) {
                selectionFields.add(field);
            } else {
                selectionFields.add(null);
            }
        }
        //Combine fields list for source descriptor and target descriptor.
        if(foreignFields!=null){
            selectionFields.addAll(foreignFields);
        }
        return selectionFields;
    }
    
    /**
     * INTERNAL:
     * Return the set of fields required in the select clause, for fetch group reading.
     */
    public Set<DatabaseField> getFetchGroupNonNestedFieldsSet() {
        return getFetchGroupNonNestedFieldsSet(null);
    }
    
    /**
     * INTERNAL:
     * Return the set of fields required in the select clause, for fetch group reading.
     */
    public Set<DatabaseField> getFetchGroupNonNestedFieldsSet(DatabaseMapping nestedMapping) {
        Set fetchedFields = new HashSet(getExecutionFetchGroup().getAttributeNames().size());

        // Add required fields.
        fetchedFields.addAll(getDescriptor().getPrimaryKeyFields());
        if (getDescriptor().hasInheritance() && (getDescriptor().getInheritancePolicy().getClassIndicatorField() != null)) {
            fetchedFields.add(getDescriptor().getInheritancePolicy().getClassIndicatorField());
        }
        if (shouldMaintainCache() && getDescriptor().usesOptimisticLocking()) {
            DatabaseField lockField = getDescriptor().getOptimisticLockingPolicy().getWriteLockField();
            if (lockField != null) {
                fetchedFields.add(lockField);
            }
        }
        // Add specified fields.
        for (Iterator iterator = getExecutionFetchGroup().getAttributeNames().iterator(); iterator.hasNext();) {
            String attribute = (String)iterator.next();
            DatabaseMapping mapping = getDescriptor().getObjectBuilder().getMappingForAttributeName(attribute);
            if (mapping == null) {
                throw QueryException.fetchGroupAttributeNotMapped(attribute);
            }
            fetchedFields.addAll(mapping.getFields());
        }
        if ((nestedMapping != null) && nestedMapping.isCollectionMapping()) {
            List<DatabaseField> additionalFields = nestedMapping.getContainerPolicy().getAdditionalFieldsForJoin((CollectionMapping)nestedMapping);
            if(additionalFields != null) {
                fetchedFields.addAll(additionalFields);
            }
        }
        return fetchedFields;
    }
    
    /**
     * INTERNAL:
     * Return the fields required in the select clause, for fetch group reading.
     */
    public List<DatabaseField> getFetchGroupSelectionFields() {
        return getFetchGroupSelectionFields(null);
    }

    /**
     * INTERNAL:
     * Return the fields required in the select clause, for fetch group reading.
     * Top level (not nested) passes null instead of nestedMapping.
     */
    protected List<DatabaseField> getFetchGroupSelectionFields(DatabaseMapping nestedMapping) {
        Set<DatabaseField> fetchedFields = getFetchGroupNonNestedFieldsSet(nestedMapping);
        
        // Build field list in the same order as descriptor's fields so that the fields printed in the usual order in SELECT clause.
        List<DatabaseField> fields = new ArrayList(fetchedFields.size());
        for (Iterator iterator = getDescriptor().getFields().iterator(); iterator.hasNext();) {
            DatabaseField field = (DatabaseField)iterator.next();
            if (fetchedFields.contains(field)) {
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * INTERNAL:
     * The method adds to the passed input vector the
     * fields or expressions corresponding to the joins.
     */
    public void addJoinSelectionFields(Vector fields, boolean isCustomSQL) {
        // executiveFetchGroup is used for warnings only - always null if no warnings logged
        FetchGroup executionFetchGroup = null;
        if(session.shouldLog(SessionLog.WARNING, SessionLog.QUERY)) {
            executionFetchGroup = getExecutionFetchGroup();
        }
        for(Expression expression : getJoinedAttributeManager().getJoinedAttributeExpressions()) {
            addSelectionFieldsForJoinedExpression(fields, isCustomSQL, expression);
            // executiveFetchGroup is used for warnings only - always null if no warnings logged
            if(executionFetchGroup != null) {
                String nestedAttributeName = ((QueryKeyExpression)expression).getNestedAttributeName();
                if(nestedAttributeName != null) {
                    if(!executionFetchGroup.containsAttributeInternal(nestedAttributeName)) {
                        getSession().log(SessionLog.WARNING, SessionLog.QUERY, "query_has_joined_attribute_outside_fetch_group", new Object[]{toString(), nestedAttributeName});
                    }
                }
            }
        }
        for(Expression expression : getJoinedAttributeManager().getJoinedMappingExpressions()) {
            addSelectionFieldsForJoinedExpression(fields, isCustomSQL, expression);
        }
    }

    /**
     * INTERNAL:
     * Return the fields selected by the query.
     * This includes the partial or joined fields.
     * This is only used for custom SQL executions.
     */
    public Vector getSelectionFields() {
        if (hasPartialAttributeExpressions()) {
            return getPartialAttributeSelectionFields(true);
        }

        Vector fields = NonSynchronizedVector.newInstance();
        if (getExecutionFetchGroup() != null) {
            fields.addAll(getFetchGroupSelectionFields());
        } else {
            fields.addAll(getDescriptor().getAllFields());
        }
        // Add joined fields.
        if (hasJoining()) {
            addJoinSelectionFields(fields, true);
        }
        if (hasAdditionalFields()) {
            // Add additional fields, use for batch reading m-m.
            fields.addAll(getAdditionalFields());
        }
        return fields;
    }
 
 
    /**
     * PUBLIC:
     * Return the WAIT timeout value of pessimistic locking query.
     */
    public Integer getWaitTimeout() {
        return waitTimeout;
    }
    
    /**
     * Initialize the expression builder which should be used for this query. If
     * there is a where clause, use its expression builder, otherwise
     * generate one and cache it. This helps avoid unnecessary rebuilds.
     */
    protected void initializeDefaultBuilder() {
        DatabaseQueryMechanism mech = getQueryMechanism();
        if (mech.isExpressionQueryMechanism() && ((ExpressionQueryMechanism)mech).getExpressionBuilder() != null) {
            this.defaultBuilder = ((ExpressionQueryMechanism)mech).getExpressionBuilder();
            if (this.defaultBuilder.getQueryClass() != null && !this.defaultBuilder.getQueryClass().equals(this.referenceClass)){
                this.defaultBuilder = new ExpressionBuilder();
            }
            return;
        }
        this.defaultBuilder = new ExpressionBuilder();
    }

    /**
     * INTERNAL:
     * return true if this query has computed its distinct value already
     */
    public boolean isDistinctComputed() {
        return this.distinctState != UNCOMPUTED_DISTINCT;
    }

    /**
     * PUBLIC:
     * Answers if the query lock mode is known to be LOCK or LOCK_NOWAIT.
     *
     * In the case of DEFAULT_LOCK_MODE and the query reference class being a CMP entity bean,
     * at execution time LOCK, LOCK_NOWAIT, or NO_LOCK will be decided.
     * <p>
     * If a single joined attribute was configured for pessimistic locking then
     * this will return true (after first execution) as the SQL contained a
     * FOR UPDATE OF clause.
     */
    public boolean isLockQuery() {
        return (this.lockingClause != null) && (getLockMode() > NO_LOCK);
    }

    /**
     * ADVANCED:
     * Answers if this query will issue any pessimistic locks.
     * <p>
     * If the lock mode is not known (DEFAULT_LOCK_MODE / descriptor specified
     * fine-grained locking) the lock mode will be determined now, to be either
     * LOCK, LOCK_NOWAIT, or NO_LOCK.
     * @see #isLockQuery()
     */
    public boolean isLockQuery(org.eclipse.persistence.sessions.Session session) {
        checkPrePrepare((AbstractSession)session);
        return isLockQuery();
    }

    /**
     * PUBLIC:
     * Return if this is an object level read query.
     */
    @Override
    public boolean isObjectLevelReadQuery() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if partial attribute.
     */
    public boolean isPartialAttribute(String attributeName) {
        if (!hasPartialAttributeExpressions()) {
            return false;
        }
        List<Expression> partialAttributeExpressions = getPartialAttributeExpressions();
        int size = partialAttributeExpressions.size();
        for (int index = 0; index < size; index++) {
            QueryKeyExpression expression = (QueryKeyExpression)partialAttributeExpressions.get(index);
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
     * INTERNAL:
     * Indicates whether pessimistic lock should also be applied to relation tables (ManyToMany and OneToOne mappings),
     * reference tables (DirectCollection and AggregateCollection mapping). 
     */
    public boolean shouldExtendPessimisticLockScope() {
        return shouldExtendPessimisticLockScope;
    }
    
    /**
     * PUBLIC:
     * Queries prepare common stated in themselves.
     */
    protected boolean isPrePrepared() {
        return isPrePrepared;
    }

    /**
     * INTERNAL:
     * If changes are made to the query that affect the derived SQL or Call
     * parameters the query needs to be prepared again.
     * <p>
     * Automatically called internally.
     * <p>
     * The early phase of preparation is to check if this is a pessimistic
     * locking query.
     */
    public void setIsPrePrepared(boolean isPrePrepared) {
        // Only unprepare if was prepared to begin with, prevent unpreparing during prepare.
        if (this.isPrePrepared && !isPrePrepared) {
            setIsPrepared(false);
            if (hasJoining()) {
                getJoinedAttributeManager().reset();
            }
        }
        this.isPrePrepared = isPrePrepared;
    }
    
    /** 
     * INTERNAL:
     * Indicates whether pessimistic lock should also be applied to relation tables (ManyToMany and OneToOne mappings),
     * reference tables (DirectCollection and AggregateCollection mapping). 
     */
    public void setShouldExtendPessimisticLockScope(boolean isExtended) {
        shouldExtendPessimisticLockScope = isExtended;
    }
    
    /**
     * INTERNAL:
     * Clear cached flags when un-preparing.
     */
    public void setIsPrepared(boolean isPrepared) {
        super.setIsPrepared(isPrepared);
        if (!isPrepared) {
            this.isReferenceClassLocked = null;
            this.concreteSubclassCalls = null;
            this.concreteSubclassJoinedMappingIndexes = null;
        }
    }

    /**
     * INTERNAL:
     * Clear cached flags when un-preparing.
     * The method always keeps concrete subclass data (unlike setIsPrepared(false)).
     */
    protected void setIsPreparedKeepingSubclassData(boolean isPrepared) {
        super.setIsPrepared(isPrepared);
        if (!isPrepared) {
            this.isReferenceClassLocked = null;
        }
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    @Override
    protected void prepare() throws QueryException {
        super.prepare();
        prepareQuery();
        if (hasJoining()) {
            this.joinedAttributeManager.computeJoiningMappingQueries(session);
        }
        computeBatchReadMappingQueries();
        if (getLoadGroup() != null) {
            if (getLoadGroup().getIsConcurrent() == null) {
                getLoadGroup().setIsConcurrent(getSession().isConcurrent());
            }
        }
    }
    
    /**
     * INTERNAL:
     * Check if the query is cached and prepare from it.
     * Return true if the query was cached.
     */
    protected boolean prepareFromCachedQuery() {
        // PERF: Check if the equivalent expression query has already been prepared.
        // Only allow queries with default properties to be cached.
        boolean isCacheable = isExpressionQuery() && (!getQueryMechanism().isJPQLCallQueryMechanism()) && isDefaultPropertiesQuery() && (!getSession().isHistoricalSession());
        DatabaseQuery cachedQuery = null;
        if (isCacheable) {
            cachedQuery = this.descriptor.getQueryManager().getCachedExpressionQuery(this);
        } else {
            return false;
        }
        if ((cachedQuery != null) && cachedQuery.isPrepared()) {
            prepareFromQuery(cachedQuery);
            setIsPrepared(true);
            return true;
        }
        this.descriptor.getQueryManager().putCachedExpressionQuery(this);
        this.isExecutionClone = false;
        return false;
    }

    /**
     * INTERNAL:
     * Copy all setting from the query.
     * This is used to morph queries from one type to the other.
     * By default this calls prepareFromQuery, but additional properties may be required
     * to be copied as prepareFromQuery only copies properties that affect the SQL.
     */
    @Override
    public void copyFromQuery(DatabaseQuery query) {
        super.copyFromQuery(query);
        if (query.isObjectLevelReadQuery()) {
            ObjectLevelReadQuery readQuery = (ObjectLevelReadQuery)query;
            this.cacheUsage = readQuery.cacheUsage;
            this.isReadOnly = readQuery.isReadOnly;
            this.isResultSetOptimizedQuery = readQuery.isResultSetOptimizedQuery;
            this.shouldIncludeData = readQuery.shouldIncludeData;
            this.inMemoryQueryIndirectionPolicy = readQuery.inMemoryQueryIndirectionPolicy;
            this.lockModeType = readQuery.lockModeType;
            this.defaultBuilder = readQuery.defaultBuilder;
            this.distinctState = readQuery.distinctState;
        }
    }
    
    /**
     * INTERNAL:
     * Prepare the query from the prepared query.
     * This allows a dynamic query to prepare itself directly from a prepared query instance.
     * This is used in the EJBQL parse cache to allow preparsed queries to be used to prepare
     * dynamic queries.
     * This only copies over properties that are configured through EJBQL.
     */
    @Override
    public void prepareFromQuery(DatabaseQuery query) {
        super.prepareFromQuery(query);
        if (query.isObjectLevelReadQuery()) {
            ObjectLevelReadQuery objectQuery = (ObjectLevelReadQuery)query;
            this.referenceClass = objectQuery.referenceClass;
            this.distinctState = objectQuery.distinctState;
            if (objectQuery.hasJoining()) {
                JoinedAttributeManager thisManager = getJoinedAttributeManager();
                JoinedAttributeManager queryManager = objectQuery.getJoinedAttributeManager();
                thisManager.setJoinedAttributeExpressions_(queryManager.getJoinedAttributeExpressions());
                thisManager.setJoinedMappingExpressions_(queryManager.getJoinedMappingExpressions());
                thisManager.setJoinedMappingIndexes_(queryManager.getJoinedMappingIndexes_());
                thisManager.setJoinedMappingQueries_(queryManager.getJoinedMappingQueries_());
                thisManager.setOrderByExpressions_(queryManager.getOrderByExpressions_());
                thisManager.setAdditionalFieldExpressions_(queryManager.getAdditionalFieldExpressions_());
            }
            if (objectQuery.hasBatchReadAttributes()) {
                this.batchFetchPolicy = objectQuery.getBatchFetchPolicy().clone();
            }
            this.nonFetchJoinAttributeExpressions = objectQuery.nonFetchJoinAttributeExpressions;
            this.defaultBuilder = objectQuery.defaultBuilder;
            this.fetchGroup = objectQuery.fetchGroup;
            this.fetchGroupName = objectQuery.fetchGroupName;
            this.isReferenceClassLocked = objectQuery.isReferenceClassLocked;
            this.shouldOuterJoinSubclasses = objectQuery.shouldOuterJoinSubclasses;
            this.shouldUseDefaultFetchGroup = objectQuery.shouldUseDefaultFetchGroup;
            this.concreteSubclassCalls = objectQuery.concreteSubclassCalls;
            this.concreteSubclassJoinedMappingIndexes = objectQuery.concreteSubclassJoinedMappingIndexes;
            this.additionalFields = objectQuery.additionalFields;
            this.partialAttributeExpressions = objectQuery.partialAttributeExpressions;
            if (objectQuery.hasOrderByExpressions()) {
                this.orderByExpressions = objectQuery.orderByExpressions;
            }
            if (objectQuery.hasUnionExpressions()) {
                this.unionExpressions = objectQuery.unionExpressions;
            }
        }
    }
    
    /**
     * INTERNAL:
     * Add mandatory attributes to fetch group, create entityFetchGroup.
     */
    public void prepareFetchGroup() throws QueryException {
        FetchGroupManager fetchGroupManager = this.descriptor.getFetchGroupManager();
        if (fetchGroupManager != null) {
            if (this.fetchGroup == null) {
                if (this.fetchGroupName != null) {
                    this.fetchGroup = fetchGroupManager.getFetchGroup(this.fetchGroupName);
                } else if (this.shouldUseDefaultFetchGroup) {
                    this.fetchGroup = this.descriptor.getFetchGroupManager().getDefaultFetchGroup();
                }
            }
            if (this.fetchGroup != null) {
                if (hasPartialAttributeExpressions()) {
                    //fetch group does not work with partial attribute reading
                    throw QueryException.fetchGroupNotSupportOnPartialAttributeReading();
                }
                this.descriptor.getFetchGroupManager().prepareAndVerify(this.fetchGroup);
            }
        } else {
            // FetchGroupManager is null
            if ((this.fetchGroup != null && !this.fetchGroup.isValidated()) || this.fetchGroupName != null) {
                throw QueryException.fetchGroupValidOnlyIfFetchGroupManagerInDescriptor(getDescriptor().getJavaClassName(), getName());
            }
        }
    }
    
    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    protected void prePrepare() throws QueryException {
        // For bug 3136413/2610803 building the selection criteria from an EJBQL string or
        // an example object is done just in time.
        buildSelectionCriteria(this.session);
        checkDescriptor(this.session);

        // Validate and prepare join expressions.           
        if (hasJoining()) {
            this.joinedAttributeManager.prepareJoinExpressions(this.session);
        }

        prepareFetchGroup();

        // Add mapping joined attributes.
        if (getQueryMechanism().isExpressionQueryMechanism() && this.descriptor.getObjectBuilder().hasJoinedAttributes()) {
            getJoinedAttributeManager().processJoinedMappings(this.session);
            if (this.joinedAttributeManager.hasOrderByExpressions()) {
                for (Expression orderBy : this.joinedAttributeManager.getOrderByExpressions()) {
                    addOrdering(orderBy);
                }
            }
            if (this.joinedAttributeManager.hasAdditionalFieldExpressions()) {
                for (Expression additionalField : this.joinedAttributeManager.getAdditionalFieldExpressions()) {
                    addAdditionalField(additionalField);
                }
            }
        }

        if (this.lockModeType != null) {
            if (this.lockModeType.equals(NONE)) {
                setLockMode(ObjectBuildingQuery.NO_LOCK);
            } else if (this.lockModeType.contains(PESSIMISTIC_)) {
                // If no wait timeout was set from a query hint, grab the
                // default one from the session if one is available.
                Integer timeout = (this.waitTimeout == null) ? this.session.getPessimisticLockTimeoutDefault() : this.waitTimeout;                
                if (timeout == null) {
                    setLockMode(ObjectBuildingQuery.LOCK);
                } else {
                    if (timeout.intValue() == 0) {
                        setLockMode(ObjectBuildingQuery.LOCK_NOWAIT);    
                    } else {
                        this.lockingClause = ForUpdateClause.newInstance(timeout);    
                    }
                }
            }
        }

        // modify query for locking only if locking has not been configured
        if (isDefaultLock()) {
            setWasDefaultLockMode(true);
            ForUpdateOfClause lockingClause = null;
            if (hasJoining()) {
                lockingClause = getJoinedAttributeManager().setupLockingClauseForJoinedExpressions(lockingClause, getSession());
            }
            if (this.descriptor.hasPessimisticLockingPolicy()) {
                lockingClause = new ForUpdateOfClause();
                lockingClause.setLockMode(this.descriptor.getCMPPolicy().getPessimisticLockingPolicy().getLockingMode());
                lockingClause.addLockedExpression(getExpressionBuilder());
            }
            if (lockingClause != null) {
                this.lockingClause = lockingClause;
                // SPECJ: Locking not compatible with distinct for batch reading.
                dontUseDistinct();
            }
        } else if ((getLockMode() <= NO_LOCK) && (!descriptor.hasPessimisticLockingPolicy())) {
            setWasDefaultLockMode(true);            
        }
        setRequiresDeferredLocks(DeferredLockManager.SHOULD_USE_DEFERRED_LOCKS && (hasJoining() || (this.descriptor.shouldAcquireCascadedLocks())));

        if (hasJoining() && hasPartialAttributeExpressions()) {
            this.session.log(SessionLog.WARNING, SessionLog.QUERY, "query_has_both_join_attributes_and_partial_attributes", new Object[]{this, getName()});
        }
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    protected void prepareQuery() throws QueryException {
        if ((!shouldMaintainCache()) && shouldRefreshIdentityMapResult() && (!this.descriptor.isAggregateCollectionDescriptor())) {
            throw QueryException.refreshNotPossibleWithoutCache(this);
        }
        if (shouldMaintainCache() && hasPartialAttributeExpressions()) {
            throw QueryException.cannotCachePartialObjects(this);
        }

        if (this.descriptor.isAggregateDescriptor()) {
            // Not allowed
            throw QueryException.aggregateObjectCannotBeDeletedOrWritten(this.descriptor, this);
        }

        if (hasAsOfClause() && (getSession().getAsOfClause() == null)) {
            if (shouldMaintainCache()) {
                throw QueryException.historicalQueriesMustPreserveGlobalCache();
            } else if (!getSession().getPlatform().isOracle() && !getSession().getProject().hasGenericHistorySupport()) {
                throw QueryException.historicalQueriesOnlySupportedOnOracle();
            }
        }

        // Validate and prepare partial attribute expressions.
        if (hasPartialAttributeExpressions()) {
            for (int index = 0; index < getPartialAttributeExpressions().size(); index++) {
                Expression expression = getPartialAttributeExpressions().get(index);

                // Search if any of the expression traverse a 1-m.
                while (expression.isQueryKeyExpression() && (!expression.isExpressionBuilder())) {
                    if (((QueryKeyExpression)expression).shouldQueryToManyRelationship()) {
                    	getJoinedAttributeManager().setIsToManyJoinQuery(true);
                    }
                    expression = ((QueryKeyExpression)expression).getBaseExpression();
                }
            }
        }
        if (!shouldOuterJoinSubclasses()) {
            setShouldOuterJoinSubclasses(getMaxRows()>0 || getFirstResult()>0 || (this.descriptor != null && 
                    this.descriptor.hasInheritance() && this.descriptor.getInheritancePolicy().shouldOuterJoinSubclasses()) );
        }

        // Ensure the subclass call cache is initialized if a multiple table inheritance descriptor.
        // This must be initialized in the query before it is cloned, and never cloned.
        if ((!shouldOuterJoinSubclasses()) && this.descriptor.hasInheritance() && this.descriptor.getInheritancePolicy().requiresMultipleTableSubclassRead()) {
            getConcreteSubclassCalls();
            if (hasJoining()) {
                getConcreteSubclassJoinedMappingIndexes();
            }
        }
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    @Override
    protected void prepareForRemoteExecution() throws QueryException {
        super.prepareForRemoteExecution();
        checkPrePrepare(getSession());
        prepareQuery();
    }

    /**
     * PUBLIC:
     * Refresh the attributes of the object(s) resulting from the query.
     * If cascading is used the private parts of the objects will also be refreshed.
     */
    public void refreshIdentityMapResult() {
        setShouldRefreshIdentityMapResult(true);
    }

    /**
     * PUBLIC:
     * Refresh the attributes of the object(s) resulting from the query.
     * If cascading is used the private parts of the objects will also be refreshed.
     */
    public void refreshRemoteIdentityMapResult() {
        setShouldRefreshRemoteIdentityMapResult(true);
    }
    
    /**
     * INTERNAL:
     * All objects queried via a UnitOfWork get registered here.  If the query
     * went to the database.
     * <p>
     * Involves registering the query result individually and in totality, and
     * hence refreshing / conforming is done here.
     *
     * @param result may be collection (read all) or an object (read one),
     * or even a cursor.  If in transaction the shared cache will
     * be bypassed, meaning the result may not be originals from the parent
     * but raw database rows.
     * @param unitOfWork the unitOfWork the result is being registered in.
     * @param arguments the original arguments/parameters passed to the query
     * execution.  Used by conforming
     * @param buildDirectlyFromRows If in transaction must construct
     * a registered result from raw database rows.
     *
     * @return the final (conformed, refreshed, wrapped) UnitOfWork query result
     */
    public abstract Object registerResultInUnitOfWork(Object result, UnitOfWorkImpl unitOfWork, AbstractRecord arguments, boolean buildDirectlyFromRows);

    /**
     * ADVANCED:
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is used internally by TopLink for batch reading but may also be
     * used directly for advanced queries or report queries.
     */
    public void resetDistinct() {
        setDistinctState(UNCOMPUTED_DISTINCT);
        //Bug2804042 Must un-prepare if prepared as the SQL may change.
        setIsPrepared(false);
    }

    /**
     * INTERNAL:
     * Additional fields can be added to a query.  This is used in m-m batch reading to bring back the key from the join table.
     */
    public void setAdditionalFields(List<Object> additionalFields) {
        this.additionalFields = additionalFields;
    }

    /**
     * PUBLIC:
     * Return if the cache should be checked.
     */
    public boolean shouldCheckCache() {
        return this.cacheUsage != DoNotCheckCache;
    }

    /**
     * PUBLIC:
     * Set the cache usage.
     * By default only primary key read object queries will first check the cache before accessing the database.
     * Any query can be configure to query against the cache completely, by key or ignore the cache check.
     * <p>Valid values are:
     * <ul>
     * <li> DoNotCheckCache - The query does not check the cache but accesses the database, the cache will still be maintain.
     * <li> CheckCacheByExactPrimaryKey - If the query is exactly and only on the object's primary key the cache will be checked.
     * <li> CheckCacheByPrimaryKey - If the query contains the primary key and possible other values the cache will be checked.
     * <li> CheckCacheThenDatabase - The whole cache will be checked to see if there is any object matching the query, if not the database will be accessed.
     * <li> CheckCacheOnly - The whole cache will be checked to see if there is any object matching the query, if not null or an empty collection is returned.
     * <li> ConformResultsAgainstUnitOfWork - The results will be checked against the changes within the unit of work and object no longer matching or deleted will be remove, matching new objects will also be added.
     * <li> shouldCheckDescriptorForCacheUsage - This setting functions like CheckCacheByPrimaryKey, except checks the appropriate descriptor's
     * shouldDisableCacheHits setting when querying on the cache.
      * </ul>
     */
    public void setCacheUsage(int cacheUsage) {
        this.cacheUsage = cacheUsage;
    }

    /**
     * INTERNAL:
     * Set the descriptor for the query.
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        // If the descriptor changed must unprepare as the SQL may change.
        if (this.descriptor != descriptor) {
            setIsPreparedKeepingSubclassData(false);
            this.descriptor = descriptor;
        }
        if (this.fetchGroup != null){
            this.fetchGroup = getExecutionFetchGroup(descriptor);
        }
        if (this.joinedAttributeManager != null){
            this.joinedAttributeManager.setDescriptor(descriptor);
        }
    }

    /**
     * ADVANCED:
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is used internally by TopLink for batch reading but may also be
     * used directly for advanced queries or report queries.
     */
    public void setDistinctState(short distinctState) {
        this.distinctState = distinctState;
    }

    /**
     * PUBLIC:
     * Set the example object of the query to be the newExampleObject.
     * The example object is used for Query By Example.
     * When doing a Query By Example, an instance of the desired object is created, and the fields are filled with
     * the values that are required in the result set.  From these values the corresponding expression is built
     * by EclipseLink, and the query is executed, returning the set of results.
     * <p>If a query already has a selection criteria this criteria and the generated
     * query by example criteria will be conjuncted.
     * <p>Once a query is executed you must make an explicit call to setExampleObject
     * if the example object is changed, so the query will know to prepare itself again.
     * <p>There is a caution to setting both a selection criteria and an example object:
     * Only in this case if you set the example object again after execution you must then also reset the selection criteria.
     * (This is because after execution the original criteria and Query By Example criteria were fused together,
     * and the former cannot be easily recovered from the now invalid result).
     */
    public void setExampleObject(Object newExampleObject) {
        if (!getQueryMechanism().isQueryByExampleMechanism()) {
            setQueryMechanism(new QueryByExampleMechanism(this, getSelectionCriteria()));
            ((QueryByExampleMechanism)getQueryMechanism()).setExampleObject(newExampleObject);
            setIsPrepared(false);
        } else {
            ((QueryByExampleMechanism)getQueryMechanism()).setExampleObject(newExampleObject);

            // Potentially force the query to be prepared again.  If shouldPrepare() is false
            // must use a slightly inferior check.
            if (isPrepared() || (!shouldPrepare() && ((QueryByExampleMechanism)getQueryMechanism()).isParsed())) {
                ((QueryByExampleMechanism)getQueryMechanism()).setIsParsed(false);
                setSelectionCriteria(null);
            }
        }
        if (newExampleObject != null) {
            setReferenceClass(newExampleObject.getClass());
        }
    }

    /**
     * PUBLIC:
     * Set the InMemoryQueryIndirectionPolicy for this query.
     */
    public void setInMemoryQueryIndirectionPolicy(InMemoryQueryIndirectionPolicy inMemoryQueryIndirectionPolicy) {
        // Bug2862302 Backwards compatibility.  This makes sure 9.0.3 and any older version project xml don't break.
        if (inMemoryQueryIndirectionPolicy != null) {
            this.inMemoryQueryIndirectionPolicy = inMemoryQueryIndirectionPolicy.getPolicy();
            inMemoryQueryIndirectionPolicy.setQuery(this);
        }
    }
    
    /**
     * PUBLIC:
     * Set the InMemoryQueryIndirectionPolicy for this query.
     */
    public void setInMemoryQueryIndirectionPolicyState(int inMemoryQueryIndirectionPolicy) {
        this.inMemoryQueryIndirectionPolicy = inMemoryQueryIndirectionPolicy;
    }

    /**
     * PUBLIC:
     * Sets whether this is a pessimistically locking query.
     * <ul>
     * <li>ObjectBuildingQuery.LOCK: SELECT .... FOR UPDATE issued.
     * <li>ObjectBuildingQuery.LOCK_NOWAIT: SELECT .... FOR UPDATE NO WAIT issued.
     * <li>ObjectBuildingQuery.NO_LOCK: no pessimistic locking.
     * <li>ObjectBuildingQuery.DEFAULT_LOCK_MODE (default) and you have a CMP descriptor:
     * fine grained locking will occur.
     * </ul>
     * <p>Fine Grained Locking: On execution the reference class
     * and those of all joined attributes will be checked.  If any of these have a
     * PessimisticLockingPolicy set on their descriptor, they will be locked in a
     * SELECT ... FOR UPDATE OF ... {NO WAIT}.  Issues fewer locks
     * and avoids setting the lock mode on each query.
     * <p>Example:<code>readAllQuery.setSelectionCriteria(employee.get("address").equal("Ottawa"));</code>
     * <ul><li>LOCK: all employees in Ottawa and all referenced Ottawa addresses will be locked.
     * <li>DEFAULT_LOCK_MODE: if address is a joined attribute, and only address has a pessimistic
     * locking policy, only referenced Ottawa addresses will be locked.
     * </ul>
     * @see org.eclipse.persistence.descriptors.PessimisticLockingPolicy
     */
    public void setLockMode(short lockMode) {
        if ((lockMode == LOCK) || (lockMode == LOCK_NOWAIT)) {
            lockingClause = ForUpdateClause.newInstance(lockMode);
            setShouldRefreshIdentityMapResult(true);
        } else if (lockMode == NO_LOCK) {
            lockingClause = ForUpdateClause.newInstance(lockMode);
        } else {
            lockingClause = null;
            setIsPrePrepared(false);
        }
        setIsPrepared(false);
        setWasDefaultLockMode(false);
    }
    
    /**
     * INTERNAL:
     * returns the javax.persistence.LockModeType string value set on this query.
     */
    public String getLockModeType(){
        return this.lockModeType;
    }

    /**
     * INTERNAL:
     * Sets a javax.persistence.LockModeType to used with this queries execution. 
     * The valid types are:
     *  - WRITE
     *  - READ
     *  - OPTIMISTIC
     *  - OPTIMISTIC_FORCE_INCREMENT
     *  - PESSIMISTIC_READ
     *  - PESSIMISTIC_WRITE
     *  - PESSIMISTIC_FORCE_INCREMENT
     *  - NONE
     * Setting a null type will do nothing.
     * @return returns a failure flag indicating that we were UNABLE to set the 
     * lock mode because of validation. Callers to this method should check the 
     * return value and throw the necessary exception.
     */
    public boolean setLockModeType(String lockModeType, AbstractSession session) {
        if (lockModeType != null) {
            OptimisticLockingPolicy lockingPolicy = session.getDescriptor(getReferenceClass()).getOptimisticLockingPolicy();
        
            if (lockingPolicy == null || !(lockingPolicy instanceof VersionLockingPolicy)) {
                if (! lockModeType.equals(PESSIMISTIC_READ) && ! lockModeType.equals(PESSIMISTIC_WRITE) && ! lockModeType.equals(NONE)) {
                    // Any locking mode other than PESSIMISTIC and NONE needs a 
                    // version locking policy to be present, otherwise return a
                    // failure flag of true.
                    return true;
                }
            }
            
            this.lockModeType = lockModeType;
            setIsPrePrepared(false);
            setIsPrepared(false);
            setWasDefaultLockMode(false);
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Return the attributes that must be joined, but not fetched, that is,
     * do not trigger the value holder.
     */
    public void setNonFetchJoinAttributeExpressions(List<Expression> nonFetchJoinExpressions) {
        this.nonFetchJoinAttributeExpressions = nonFetchJoinExpressions;
    }

    /**
     * INTERNAL:
     * The locking clause contains a list of expressions representing which
     * objects are to be locked by the query.
     * <p>
     * Use for even finer grained control over what is and is not locked by
     * a particular query.
     */
    public void setLockingClause(ForUpdateClause clause) {
        if (clause.isForUpdateOfClause()) {
            this.lockingClause = clause;
            setIsPrePrepared(false);
        } else {
            setLockMode(clause.getLockMode());
        }
        setWasDefaultLockMode(false);
    }

    /**
     * INTERNAL:
     * Set the partial attributes to select.
     */
    public void setPartialAttributeExpressions(List<Expression> partialAttributeExpressions) {
        this.partialAttributeExpressions = partialAttributeExpressions;
    }

    public void setEJBQLString(String ejbqlString) {
        super.setEJBQLString(ejbqlString);
        setIsPrePrepared(false);
    }

    /**
     * PUBLIC:
     * The QueryByExamplePolicy, is a useful to customize the query when Query By Example is used.
     * The policy will control what attributes should, or should not be included in the query.
     * When dealing with nulls, using special operations (notEqual, lessThan, like, etc.)
     * for comparison, or choosing to include certain attributes at all times, it is useful to modify
     * the policy accordingly.
     * <p>Once a query is executed you must make an explicit call to setQueryByExamplePolicy
     * when changing the policy, so the query will know to prepare itself again.
     * <p>There is a caution to setting both a selection criteria and an example object:
     * If you set the policy after execution you must also reset the selection criteria.
     * (This is because after execution the original criteria and Query By Example criteria are fused together,
     * and the former cannot be easily recovered).
     */
    public void setQueryByExamplePolicy(QueryByExamplePolicy queryByExamplePolicy) {
        if (!getQueryMechanism().isQueryByExampleMechanism()) {
            setQueryMechanism(new QueryByExampleMechanism(this, getSelectionCriteria()));
            ((QueryByExampleMechanism)getQueryMechanism()).setQueryByExamplePolicy(queryByExamplePolicy);
            setIsPrepared(false);
        } else {
            ((QueryByExampleMechanism)getQueryMechanism()).setQueryByExamplePolicy(queryByExamplePolicy);
            // Must allow the query to be prepared again.
            if (isPrepared() || (!shouldPrepare() && ((QueryByExampleMechanism)getQueryMechanism()).isParsed())) {
                ((QueryByExampleMechanism)getQueryMechanism()).setIsParsed(false);
                setSelectionCriteria(null);
                // setIsPrepared(false) triggered by previous.
            }
        }
        setIsPrePrepared(false);
    }

    /**
     * REQUIRED:
     * Set the reference class for the query.
     */
    @Override
    public void setReferenceClass(Class aClass) {
        if (referenceClass != aClass) {
            setIsPreparedKeepingSubclassData(false);
        }
        referenceClass = aClass;
    }

    /**
     * INTERNAL:
     * Set the reference class for the query.
     */
    @Override
    public void setReferenceClassName(String aClass) {
        if (this.referenceClassName != aClass) {
            setIsPreparedKeepingSubclassData(false);
        }
        this.referenceClassName = aClass;
    }


    /**
     * PUBLIC:
     * Set the Expression/where clause of the query.
     * The expression should be defined using the query's ExpressionBuilder.
     */
    @Override
    public void setSelectionCriteria(Expression expression) {
        super.setSelectionCriteria(expression);
        if ((expression != null) && (this.defaultBuilder != null) && (this.defaultBuilder.getQueryClass() == null)){
            // For flashback: Must make sure expression and defaultBuilder always in sync.
            ExpressionBuilder newBuilder = expression.getBuilder();
            if (newBuilder != this.defaultBuilder) {
                if (hasAsOfClause() && getAsOfClause().isUniversal()) {
                    newBuilder.asOf(this.defaultBuilder.getAsOfClause());
                }
                this.defaultBuilder = newBuilder;
            }
        }
    }

    /**
     * INTERNAL:
     * Set if the rows for the result of the query should also be returned using a complex query result.
     * @see ComplexQueryResult
     */
    public void setShouldIncludeData(boolean shouldIncludeData) {
        this.shouldIncludeData = shouldIncludeData;
    }

    /**
     * PUBLIC:
     * Return if cache should be checked.
     */
    public boolean shouldCheckCacheOnly() {
        return this.cacheUsage == CheckCacheOnly;
    }

    /**
     * PUBLIC:
     * Return whether the descriptor's disableCacheHits setting should be checked prior
     * to querying the cache.
     */
    public boolean shouldCheckDescriptorForCacheUsage() {
        return this.cacheUsage == UseDescriptorSetting;
    }

    /**
     * PUBLIC:
     * Should the results will be checked against the changes within the unit of work and object no longer matching or deleted will be remove, matching new objects will also be added..
     */
    public boolean shouldConformResultsInUnitOfWork() {
        return this.cacheUsage == ConformResultsInUnitOfWork;
    }

    /**
     * INTERNAL:
     * return true if this query should use a distinct
     */
    public boolean shouldDistinctBeUsed() {
        return getDistinctState() == USE_DISTINCT;
    }

    /**
     * INTERNAL:
     * Return if the rows for the result of the query should also be returned using a complex query result.
     * @see ComplexQueryResult
     */
    public boolean shouldIncludeData() {
        return shouldIncludeData;
    }
        
    /**
     * PUBLIC:
     * Return if an outer join should be used to read subclasses.
     * By default a separate query is done for each subclass when querying for
     * a root or branch inheritance class that has subclasses that span multiple tables.
     */
    public boolean shouldOuterJoinSubclasses() {
        if (shouldOuterJoinSubclasses == null) {
            return false;
        }
        return shouldOuterJoinSubclasses.booleanValue();
    }
            
    /**
     * PUBLIC:
     * Set if an outer join should be used to read subclasses.
     * By default a separate query is done for each subclass when querying for
     * a root or branch inheritance class that has subclasses that span multiple tables.
     */
    public void setShouldOuterJoinSubclasses(boolean shouldOuterJoinSubclasses) {
        this.shouldOuterJoinSubclasses = Boolean.valueOf(shouldOuterJoinSubclasses);
        setIsPrepared(false);
    }

    /**
     * INTERNAL:
     * Return if this is a full object query, not partial nor fetch group.
     */
    public boolean shouldReadAllMappings() {
        return (!hasPartialAttributeExpressions()) && (this.fetchGroup == null);
    }
    
    /**
     * INTERNAL:
     * Check if the mapping is part of the partial attributes.
     */
    public boolean shouldReadMapping(DatabaseMapping mapping, FetchGroup fetchGroup) {
        String attrName = mapping.getAttributeName();

        // bug 3659145 TODO - What is this bug ref? dclarke modified this next
        // if block
        if(fetchGroup  != null) {
            return fetchGroup.containsAttributeInternal(mapping.getAttributeName());
        }

        return isPartialAttribute(attrName);
    }

    /**
     * ADVANCED:
     * If a distinct has been set the DISTINCT clause will be printed.
     * This is used internally by EclipseLink for batch reading but may also be
     * used directly for advanced queries or report queries.
     */
    public void useDistinct() {
        setDistinctState(USE_DISTINCT);
        //Bug2804042 Must un-prepare if prepared as the SQL may change.
        setIsPrepared(false);
    }

    /**
    * INTERNAL:
    * Helper method that checks if clone has been locked with uow.
    */
    public boolean isClonePessimisticLocked(Object clone, UnitOfWorkImpl uow) {
        return this.descriptor.hasPessimisticLockingPolicy() && uow.isPessimisticLocked(clone);
    }

    /**
     * INTERNAL:
     * Cache the locking policy isReferenceClassLocked check.
     */
    protected boolean isReferenceClassLocked() {
        if (isReferenceClassLocked == null) {
            isReferenceClassLocked = Boolean.valueOf(isLockQuery() && lockingClause.isReferenceClassLocked());
        }
        return isReferenceClassLocked.booleanValue();
    }
    
    /**
     * INTERNAL:
     * Helper method that records clone with uow if query is pessimistic locking.
     */
    public void recordCloneForPessimisticLocking(Object clone, UnitOfWorkImpl uow) {
        if (isReferenceClassLocked()) {
            uow.addPessimisticLockedClone(clone);
        }
    }

    /**
     * ADVANCED:
     * Return if the query should be optimized to build directly from the result set.
     * This optimization follows an optimized path and can only be used for,
     * singleton primary key, direct mapped, simple type, no inheritance, uow isolated objects.
     */
    public boolean isResultSetOptimizedQuery() {
        return this.isResultSetOptimizedQuery;
    }
    
    /**
     * ADVANCED:
     * Return if the query result set access should be optimized.
     */
    public Boolean isResultSetAccessOptimizedQuery() {
        return this.isResultSetAccessOptimizedQuery;
    }
    
    /**
     * INTERNAL:
     * Return if the query uses ResultSet optimization.
     * Note that to be accurate it's required to be set by prepareResultSetAccessOptimization or checkResultSetAccessOptimization method.
     * It's always returns the same value as this.isResultSetOptimizedQuery.booleanValue (if not null).
     * Note that in this case if optimization is incompatible with other query settings then exception is thrown.
     * Otherwise - if the session demand optimization and it is possible - optimizes (returns true),
     * otherwise false.
     */
    @Override
    public boolean usesResultSetAccessOptimization() {
        return this.usesResultSetAccessOptimization != null && this.usesResultSetAccessOptimization;
    }
    
    /**
     * INTERNAL:
     * Sets usesResultSetAccessOptimization based on isResultSetAccessOptimizedQuery, session default and 
     * query settings that could not be altered without re-preparing the query.
     * Called when the query is prepared or in case usesResultSetAccessOptimization hasn't been set yet.
     * Throws exception if isResultSetAccessOptimizedQuery==true cannot be accommodated because of a conflict with the query settings.
     * In case of isResultSetAccessOptimizedQuery hasn't been set and session default conflicting with the the query settings
     * the optimization is turned off.
     */
    protected void prepareResultSetAccessOptimization() {
        // if ResultSet optimization is used then ResultSet Access optimization is ignored. 
        if (this.isResultSetOptimizedQuery) {
            return;
        }
        if (this.isResultSetAccessOptimizedQuery != null) {
            this.usesResultSetAccessOptimization = this.isResultSetAccessOptimizedQuery;
            if (this.usesResultSetAccessOptimization) {
                if (!supportsResultSetAccessOptimizationOnPrepare() || !supportsResultSetAccessOptimizationOnExecute()) {
                    this.usesResultSetAccessOptimization = null;
                    throw QueryException.resultSetAccessOptimizationIsNotPossible(this);
                }
            }
        } else {
            if (getSession().shouldOptimizeResultSetAccess() && supportsResultSetAccessOptimizationOnPrepare() && supportsResultSetAccessOptimizationOnExecute()) {
                this.usesResultSetAccessOptimization = Boolean.TRUE;
            } else {
                this.usesResultSetAccessOptimization = Boolean.FALSE;
            }
        }
    }
    
    /**
     * INTERNAL:
     * Sets usesResultSetAccessOptimization each time when the query is executed.
     * Unless usesResultSetAccessOptimization hasn't been set yet  
     * checks only query settings that could be altered without re-preparing the query.
     * Throws exception if isResultSetAccessOptimizedQuery==true cannot be accommodated because of a conflict with the query settings.
     * In case of isResultSetAccessOptimizedQuery hasn't been set and session default conflicting with the the query settings
     * the optimization is turned off.
     */
    public void checkResultSetAccessOptimization() {
        if (this.usesResultSetAccessOptimization == null) {
            prepareResultSetAccessOptimization();
        } else {
            if (this.usesResultSetAccessOptimization.booleanValue() && !supportsResultSetAccessOptimizationOnExecute()) {
                if (this.isResultSetAccessOptimizedQuery == null) {
                    usesResultSetAccessOptimization = Boolean.FALSE;
                } else {
                    throw QueryException.resultSetAccessOptimizationIsNotPossible(this);                        
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public void clearUsesResultSetAccessOptimization() {
        this.usesResultSetAccessOptimization = null;
    }
    
    /**
     * ADVANCED:
     * Set if the query should be optimized to build directly from the result set.
     * This optimization follows an optimized path and can only be used for,
     * singleton primary key, direct mapped, simple type, no inheritance, uow isolated objects.
     */
    public void setIsResultSetOptimizedQuery(boolean isResultSetOptimizedQuery) {
        this.isResultSetOptimizedQuery = isResultSetOptimizedQuery;
    }
    
    /**
     * ADVANCED:
     * Set if the query should be optimized to build directly from the result set.
     */
    public void setIsResultSetAccessOptimizedQuery(boolean isResultSetAccessOptimizedQuery) {
        if (this.isResultSetAccessOptimizedQuery == null || this.isResultSetAccessOptimizedQuery.booleanValue() != isResultSetOptimizedQuery) {
            this.isResultSetAccessOptimizedQuery = isResultSetAccessOptimizedQuery;
            this.usesResultSetAccessOptimization = null;
        }
    }
    
    /**
     * ADVANCED:
     * Clear the flag set by setIsResultSetOptimizedQuery method, allow to use default set on the session instead.
     */
    public void clearIsResultSetOptimizedQuery(boolean isResultSetOptimizedQuery) {
        if (this.isResultSetAccessOptimizedQuery != null) {
            this.isResultSetAccessOptimizedQuery = null;
            this.usesResultSetAccessOptimization = null;
        }
    }
    
    /**
     * INTERNAL: Helper method to determine the default mode. If true and query has a pessimistic locking policy,
     * locking will be configured according to the pessimistic locking policy.
     */
    public boolean isDefaultLock() {
        return (this.lockingClause == null) || wasDefaultLockMode();
    }
    
    /**
     * INTERNAL:
     * Return true if the query uses default properties.
     * This is used to determine if this query is cacheable.
     * i.e. does not use any properties that may conflict with another query
     * with the same JPQL or selection criteria.
     */
    @Override
    public boolean isDefaultPropertiesQuery() {
        return super.isDefaultPropertiesQuery()
            && (!this.isResultSetOptimizedQuery)
            && (this.isResultSetAccessOptimizedQuery == null || this.isResultSetAccessOptimizedQuery.equals(isResultSetAccessOptimizedQueryDefault))
            && (isDefaultLock())
            && (!hasAdditionalFields())
            && (!hasPartialAttributeExpressions())
            && (!hasUnionExpressions())
            && (!hasNonFetchJoinedAttributeExpressions())
            && (this.fetchGroup == null)
            && (this.fetchGroupName == null)
            && (this.shouldUseDefaultFetchGroup);
    }
    
    /**
     * INTERNAL:
     * Checks to see if a builder has been set on the query.
     */
    public boolean hasDefaultBuilder(){
        return this.defaultBuilder != null;
    }

    /**
     * Return if a fetch group is set in the query.
     */
    public boolean hasFetchGroup() {
        return this.fetchGroup != null;
    }
    
    /**
     * Return the fetch group set in the query.
     * If a fetch group is not explicitly set in the query, default fetch group optionally defined in the descriptor
     * would be used, unless the user explicitly calls query.setShouldUseDefaultFetchGroup(false).
     * Note that the returned fetchGroup may be updated during preProcess.
     * @see #getFetchGroup(ClassDescriptor) for named and default FetchGroup
     *      lookup.
     */
    public FetchGroup getFetchGroup() {
        return this.fetchGroup;
    }

    /**
     * Return the load group set in the query.
     */
    public LoadGroup getLoadGroup() {
        return this.loadGroup;
    }

    /**
     * INTERNAL:
     * Returns FetchGroup that will be applied to the query.
     * Note that the returned fetchGroup may be updated during preProcess.
     */
    @Override
    public FetchGroup getExecutionFetchGroup() {
        return this.fetchGroup;
    }

    /**
     * INTERNAL:
     * Returns FetchGroup that will be applied to the query.
     * Note that the returned fetchGroup may be updated during preProcess.
     */
    @Override
    public FetchGroup getExecutionFetchGroup(ClassDescriptor descriptor) {
        if (this.fetchGroup != null && descriptor.hasInheritance()){
            return (FetchGroup) this.fetchGroup.findGroup(descriptor);
        }
        return this.fetchGroup;
    }

    /**
     * INTERNAL:
     * Indicates whether a FetchGroup will be applied to the query.
     */
    public boolean hasExecutionFetchGroup() {
        return getExecutionFetchGroup() != null;
    }

    /**
     * Set a dynamic (use case) fetch group to the query.
     */
    public void setFetchGroup(FetchGroup newFetchGroup) {
        this.fetchGroup = newFetchGroup;
        this.fetchGroupName = null;
        setIsPrePrepared(false);
    }

    /**
     * Set a descriptor-level pre-defined named fetch group  to the query.
     */
    public void setFetchGroupName(String groupName) {
        //nullify the fetch group reference as one query can only has one fetch group.
        this.fetchGroup = null;
        this.fetchGroupName = groupName;
        setIsPrePrepared(false);
    }

    /**
     * Set a load group to the query.
     */
    public void setLoadGroup(LoadGroup loadGroup) {
        this.loadGroup = loadGroup;
    }

    /**
     * Return the fetch group name set in the query.
     */
    public String getFetchGroupName() {
        return this.fetchGroupName;
    }

    /**
     * Return false if the query does not use the default fetch group defined in the descriptor level.
     */
    public boolean shouldUseDefaultFetchGroup() {
        return this.shouldUseDefaultFetchGroup;
    }

    /**
     * Set false if the user does not want to use the default fetch group defined in the descriptor level.
     */
    public void setShouldUseDefaultFetchGroup(boolean shouldUseDefaultFetchGroup) {
        this.shouldUseDefaultFetchGroup = shouldUseDefaultFetchGroup;
        this.fetchGroup = null;
        this.fetchGroupName = null;
        // Force prepare again so executeFetchGroup is calculated
        setIsPrePrepared(false);
    }
    
    /**
     * INTERNAL:
     * Return the cache of concrete subclass calls.
     * This allow concrete subclasses calls to be prepared and cached for inheritance queries.
     */
    public Map<Class, DatabaseCall> getConcreteSubclassCalls() {
        if (concreteSubclassCalls == null) {
            concreteSubclassCalls = new ConcurrentHashMap(8);
        }
        return concreteSubclassCalls;
    }

    /**
     * INTERNAL:
     * Return the cache of concrete subclass joined mapping indexes.
     * This allow concrete subclasses calls to be prepared and cached for inheritance queries.
     */
    public Map<Class, Map<DatabaseMapping, Object>> getConcreteSubclassJoinedMappingIndexes() {
        if (concreteSubclassJoinedMappingIndexes == null) {
            concreteSubclassJoinedMappingIndexes = new ConcurrentHashMap(8);
        }
        return concreteSubclassJoinedMappingIndexes;
    }
    

    /**
     * INTERNAL:
     * Return if the query is known to be by primary key.
     */
    public boolean isPrimaryKeyQuery() {
        return false;
    }

    /**
     * INTERNAL:
     * Extends pessimistic lock scope.
     */
    public void extendPessimisticLockScope() {
        if(!shouldExtendPessimisticLockScope || getDescriptor() == null) {
            return;
        }
        int size = getDescriptor().getMappings().size();
        boolean isExtended = false;
        boolean isFurtherExtensionRequired = false;
        for(int i=0; i < size; i++) {
            DatabaseMapping mapping = getDescriptor().getMappings().get(i);
            if(mapping.isForeignReferenceMapping()) {
                ForeignReferenceMapping frMapping = (ForeignReferenceMapping)mapping;
                if(frMapping.shouldExtendPessimisticLockScope()) {
                    if(frMapping.shouldExtendPessimisticLockScopeInSourceQuery()) {
                        frMapping.extendPessimisticLockScopeInSourceQuery(this);
                        isExtended = true;
                    } else {
                        isFurtherExtensionRequired = true;
                    }
                }
            }
        }
        if(isExtended) {
            useDistinct();
        }
        if(!isFurtherExtensionRequired) {
            shouldExtendPessimisticLockScope = false;
        }
    }
    
    /**
     * Return the batch fetch policy for configuring batch fetching.
     */
    public BatchFetchPolicy getBatchFetchPolicy() {
        if (batchFetchPolicy == null) {
            batchFetchPolicy = new BatchFetchPolicy();
        }
        return batchFetchPolicy;
    }
    
    /**
     * Set the batch fetch policy for configuring batch fetching.
     */
    public void setBatchFetchPolicy(BatchFetchPolicy batchFetchPolicy) {
        this.batchFetchPolicy = batchFetchPolicy;
    }

    /**
     * INTERNAL:
     * Return all attributes specified for batch reading.
     */
    public List<Expression> getBatchReadAttributeExpressions() {
        return getBatchFetchPolicy().getAttributeExpressions();
    }

    /**
     * INTERNAL:
     * Set all attributes specified for batch reading.
     */
    public void setBatchReadAttributeExpressions(List<Expression> attributeExpressions) {
        if ((this.batchFetchPolicy == null) && (attributeExpressions.isEmpty())) {
            return;
        }
        getBatchFetchPolicy().setAttributeExpressions(attributeExpressions);
    }

    /**
     * INTERNAL:
     * Return true is this query has batching
     */
    public boolean hasBatchReadAttributes() {
        return (this.batchFetchPolicy != null) && (this.batchFetchPolicy.hasAttributes());
    }
        
    /**
     * INTERNAL:
     * Return if the attribute is specified for batch reading.
     */
    public boolean isAttributeBatchRead(ClassDescriptor mappingDescriptor, String attributeName) {
        if (this.batchFetchPolicy == null) {
            return false;
        }
        // Since aggregates share the same query as their parent, must avoid the aggregate thinking
        // the parents mappings is for it, (queries only share if the aggregate was not joined).
        if (mappingDescriptor.isAggregateDescriptor() && (mappingDescriptor != this.descriptor)) {
            return false;
        }
        return this.batchFetchPolicy.isAttributeBatchRead(mappingDescriptor, attributeName);
    }

    /**
     * INTERNAL:
     * Used to optimize joining by pre-computing the nested join queries for the mappings.
     */
    public void computeBatchReadMappingQueries() {
        boolean initialized = false;
        if (getDescriptor().getObjectBuilder().hasBatchFetchedAttributes()) {
            // Only set the descriptor batched attributes if no batching has been set.
            // This avoid endless recursion if a recursive relationship is batched.
            // The batched mapping do not need to be processed up front,
            // this is just an optimization, and needed for IN batching.
            if (this.batchFetchPolicy == null) {
                this.batchFetchPolicy = new BatchFetchPolicy();
                if (getDescriptor().getObjectBuilder().hasInBatchFetchedAttribute()) {
                    this.batchFetchPolicy.setType(BatchFetchType.IN);
                }
                List<DatabaseMapping> batchedMappings = getDescriptor().getObjectBuilder().getBatchFetchedAttributes();
                this.batchFetchPolicy.setMappingQueries(new HashMap(batchedMappings.size()));
                initialized = true;
                int size = batchedMappings.size();
                for (int index = 0; index < size; index++) {
                    DatabaseMapping mapping = batchedMappings.get(index);
                    if ((mapping != null) && mapping.isForeignReferenceMapping()) {
                        // A nested query must be built to pass to the descriptor that looks like the real query execution would.
                        ReadQuery nestedQuery = ((ForeignReferenceMapping)mapping).prepareNestedBatchQuery(this);    
                        // Register the nested query to be used by the mapping for all the objects.
                        this.batchFetchPolicy.getMappingQueries().put(mapping, nestedQuery);
                    }
                }
                this.batchFetchPolicy.setBatchedMappings(getDescriptor().getObjectBuilder().getBatchFetchedAttributes());
            }
        }
        // Cannot prepare the batch queries if using inheritance, as child descriptors can have different mappings.
        if (hasBatchReadAttributes() && (!this.descriptor.hasInheritance())) {
            List<Expression> batchReadAttributeExpressions = getBatchReadAttributeExpressions();
            this.batchFetchPolicy.setAttributes(new ArrayList(batchReadAttributeExpressions.size()));
            if (!initialized) {
                this.batchFetchPolicy.setMappingQueries(new HashMap(batchReadAttributeExpressions.size()));
            }
            computeNestedQueriesForBatchReadExpressions(batchReadAttributeExpressions);
        }
    }

    /**
     * INTERNAL:
     * This method is used when computing the nested queries for batch read mappings.
     * It recurses computing the nested mapping queries.
     */
    protected void computeNestedQueriesForBatchReadExpressions(List<Expression> batchReadExpressions) {
        int size = batchReadExpressions.size();
        for (int index = 0; index < size; index++) {
            ObjectExpression objectExpression = (ObjectExpression)batchReadExpressions.get(index);

            // Expression may not have been initialized.
            ExpressionBuilder builder = objectExpression.getBuilder();
            builder.setSession(getSession().getRootSession(null));
            builder.setQueryClass(getReferenceClass());            
            
            // PERF: Cache join attribute names.
            ObjectExpression baseExpression = objectExpression;
            while (!baseExpression.getBaseExpression().isExpressionBuilder()) {
                baseExpression = (ObjectExpression)baseExpression.getBaseExpression();
            }
            this.batchFetchPolicy.getAttributes().add(baseExpression.getName());
            
            // Ignore nested
            if (objectExpression.getBaseExpression().isExpressionBuilder()) {
                DatabaseMapping mapping = objectExpression.getMapping();
                if ((mapping != null) && mapping.isForeignReferenceMapping()) {
                    // A nested query must be built to pass to the descriptor that looks like the real query execution would.
                    ReadQuery nestedQuery = ((ForeignReferenceMapping)mapping).prepareNestedBatchQuery(this);    
                    // Register the nested query to be used by the mapping for all the objects.
                    this.batchFetchPolicy.getMappingQueries().put(mapping, nestedQuery);
                }
            }
        }
    }

    /**
     * PUBLIC:
     * Specify the foreign-reference mapped attribute to be optimized in this query.
     * The query will execute normally, however when any of the batched parts is accessed,
     * the parts will all be read in a single query,
     * this allows all of the data required for the parts to be read in a single query instead of (n) queries.
     * This should be used when the application knows that it requires the part for all of the objects being read.
     * This can be used for one-to-one, one-to-many, many-to-many and direct collection mappings.
     *
     * The use of the expression allows for nested batch reading to be expressed.
     * <p>Example: query.addBatchReadAttribute("phoneNumbers")
     *
     * @see #addBatchReadAttribute(Expression)
     * @see #setBatchFetchType(BatchFetchType)
     * @see ObjectLevelReadQuery#addJoinedAttribute(String)
     */
    public void addBatchReadAttribute(String attributeName) {
        addBatchReadAttribute(getExpressionBuilder().get(attributeName));
    }

    /**
     * PUBLIC:
     * Specify the foreign-reference mapped attribute to be optimized in this query.
     * The query will execute normally, however when any of the batched parts is accessed,
     * the parts will all be read in a single query,
     * this allows all of the data required for the parts to be read in a single query instead of (n) queries.
     * This should be used when the application knows that it requires the part for all of the objects being read.
     * This can be used for one-to-one, one-to-many, many-to-many and direct collection mappings.
     *
     * The use of the expression allows for nested batch reading to be expressed.
     * <p>Example: query.addBatchReadAttribute(query.getExpressionBuilder().get("policies").get("claims"))
     *
     * @see #setBatchFetchType(BatchFetchType)
     * @see ObjectLevelReadQuery#addJoinedAttribute(String)
     */
    public void addBatchReadAttribute(Expression attributeExpression) {
        if (! getQueryMechanism().isExpressionQueryMechanism()){
            throw QueryException.batchReadingNotSupported(this);
        }
        getBatchReadAttributeExpressions().add(attributeExpression);
    }

    /**
     * PUBLIC:
     * Set the batch fetch type for the query.
     * This can be JOIN, EXISTS, or IN.
     * This defines the type of batch reading to use with the query.
     * The query must have defined batch read attributes to set its fetch type.
     * 
     * @see #addBatchReadAttribute(Expression)
     */
    public void setBatchFetchType(BatchFetchType type) {
        getBatchFetchPolicy().setType(type);
    }

    /**
     * PUBLIC:
     * Set the batch fetch size for the query.
     * This is only relevant for the IN batch fetch type.
     * This defines the max number of keys for the IN clause.
     * 
     * @see #setBatchFetchType(BatchFetchType)
     * @see #addBatchReadAttribute(Expression)
     */
    public void setBatchFetchSize(int size) {
        getBatchFetchPolicy().setSize(size);
    }

    /**
     * INTERNAL:
     * Return temporary map of batched objects.
     */
    public Map<Object, Object> getBatchObjects() {
        return getBatchFetchPolicy().getBatchObjects();
    }

    /**
     * INTERNAL:
     * Set temporary map of batched objects.
     */
    public void setBatchObjects(Map<Object, Object> batchObjects) {
        getBatchFetchPolicy().setBatchObjects(batchObjects);
    }

    @Override
    public String toString() {
        String str = super.toString();
        if(this.fetchGroup != null) {
            str += '\n' + this.fetchGroup.toString();
        } else if(this.fetchGroupName != null) {
            str += '\n' + "FetchGroup(" + this.fetchGroupName + ")";
        } else if(this.shouldUseDefaultFetchGroup) {
            if(this.descriptor != null && this.descriptor.hasFetchGroupManager()) {
                FetchGroup defaultFetchGroup = descriptor.getFetchGroupManager().getDefaultFetchGroup(); 
                if(defaultFetchGroup != null) {
                    str += '\n' + "Default " + defaultFetchGroup.toString();
                }
            }
        }
        return str;
    }
    
    /**
     * INTERNAL:
     * Indicates whether the query can use ResultSet optimization.
     * The method is called when the query is prepared, 
     * so it should refer only to the attributes that cannot be altered without re-preparing the query.
     * If the query is a clone and the original has been already prepared
     * this method will be called to set a (transient and therefore set to null) usesResultSetAccessOptimization attribute. 
     */
    public boolean supportsResultSetAccessOptimizationOnPrepare() {
        return getCall() != null && ((DatabaseCall)getCall()).getReturnsResultSet() && // must return ResultSet
            (!hasJoining() || !this.joinedAttributeManager.isToManyJoin()) && 
            (!this.descriptor.hasInheritance() || 
                    !this.descriptor.getInheritancePolicy().hasClassExtractor() &&  // ClassExtractor requires the whole row
                    (shouldOuterJoinSubclasses() || !this.descriptor.getInheritancePolicy().requiresMultipleTableSubclassRead() || this.descriptor.getInheritancePolicy().hasView())); // don't know how to handle select class type call - ResultSet optimization breaks it.
    }

    /**
     * INTERNAL:
     * Indicates whether the query can use ResultSet optimization.
     * Note that the session must be already set.
     * The method is called when the query is executed, 
     * so it should refer only to the attributes that can be altered without re-preparing the query.
     */
    public boolean supportsResultSetAccessOptimizationOnExecute() {
        return !getSession().isConcurrent();
    }
}
