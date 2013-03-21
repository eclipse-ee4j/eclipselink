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
 *     04/01/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 2)
 *     09/09/2011-2.3.1 Guy Pelletier 
 *       - 356197: Add new VPD type to MultitenantType
 *     01/06/2011-2.3 Guy Pelletier 
 *       - 371453: JPA Multi-Tenancy in Bidirectional OneToOne Relation throws ArrayIndexOutOfBoundsException
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import java.util.*;
import java.sql.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.queries.DatasourceCallQueryMechanism;
import org.eclipse.persistence.internal.sessions.remote.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ResultSetRecord;
import org.eclipse.persistence.internal.sessions.SimpleResultSetRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.tools.profiler.QueryMonitor;

/**
 * <p><b>Purpose</b>:
 * Concrete class for all read queries involving a single object.
 * <p>
 * <p><b>Responsibilities</b>:
 * Return a single object for the query.
 * Implements the inheritance feature when dealing with abstract descriptors.
 *
 * @author Yvon Lavoie
 * @since TOPLink/Java 1.0
 */
public class ReadObjectQuery extends ObjectLevelReadQuery {
    /** Object that can be used in place of a selection criteria. */
    protected transient Object selectionObject;

    /** Key that can be used in place of a selection criteria. */
    protected Object selectionId;

    /** Can be used to refresh a specific non-cached instance from the database. */
    protected boolean shouldLoadResultIntoSelectionObject = false;    
    
    /**
     * PUBLIC:
     * Return a new read object query.
     * A reference class must be specified before execution.
     * It is better to provide the class and expression builder on construction to esnure a single expression builder is used.
     * If no selection criteria is specified this will reads the first object found in the database.
     */
    public ReadObjectQuery() {
        super();
    }

    /**
     * PUBLIC:
     * Return a new read object query.
     * By default, the query has no selection criteria. Executing this query without
     * selection criteria will always result in a database access to read the first
     * instance of the specified Class found in the database. This is true no
     * matter how cache usage is configured and even if an instance of the
     * specified Class exists in the cache.
     * Executing a query with selection criteria allows you to avoid a database
     * access if the selected instance is in the cache. For this reason, you may whish to use a ReadObjectQuery constructor that takes selection criteria, such as: {@link #ReadObjectQuery(Class, Call)}, {@link #ReadObjectQuery(Class, Expression)}, {@link #ReadObjectQuery(Class, ExpressionBuilder)}, {@link #ReadObjectQuery(ExpressionBuilder)}, {@link #ReadObjectQuery(Object)}, or {@link #ReadObjectQuery(Object, QueryByExamplePolicy)}.
     */
    public ReadObjectQuery(Class classToRead) {
        this();
        this.referenceClass = classToRead;
    }

    /**
     * PUBLIC:
     * Return a new read object query for the class and the selection criteria.
     */
    public ReadObjectQuery(Class classToRead, Expression selectionCriteria) {
        this();
        this.referenceClass = classToRead;
        setSelectionCriteria(selectionCriteria);
    }

    /**
     * PUBLIC:
     * Return a new read object query for the class.
     * The expression builder must be used for all associated expressions used with the query.
     */
    public ReadObjectQuery(Class classToRead, ExpressionBuilder builder) {
        this();
        this.defaultBuilder = builder;
        this.referenceClass = classToRead;
    }

    /**
     * PUBLIC:
     * Return a new read object query.
     * The call represents a database interaction such as SQL, Stored Procedure.
     */
    public ReadObjectQuery(Class classToRead, Call call) {
        this();
        this.referenceClass = classToRead;
        setCall(call);
    }

    /**
     * PUBLIC:
     * Return a new read object query.
     * The call represents a database interaction such as SQL, Stored Procedure.
     */
    public ReadObjectQuery(Call call) {
        this();
        setCall(call);
    }

    /**
     * PUBLIC:
     * Return a query to read the object with the same primary key as the provided object.
     * Note: This is not a query by example object, only the primary key will be used for the selection criteria.
     */
    public ReadObjectQuery(Object objectToRead) {
        this();
        setSelectionObject(objectToRead);
    }

    /**
     * PUBLIC:
     * Return a query by example query to find an object matching the attributes of the example object.
     */
    public ReadObjectQuery(Object exampleObject, QueryByExamplePolicy policy) {
        this();
        setExampleObject(exampleObject);
        setQueryByExamplePolicy(policy);
    }

    /**
     * PUBLIC:
     * The expression builder should be provide on creation to ensure only one is used.
     */
    public ReadObjectQuery(ExpressionBuilder builder) {
        this();
        this.defaultBuilder = builder;
    }

    /**
     * INTERNAL:
     * <P> This method is called by the object builder when building an original.
     * It will cause the original to be cached in the query results if the query
     * is set to do so.
     */
    public void cacheResult(Object object) {
        Object cachableObject = object;
        if (object == null) {
            this.temporaryCachedQueryResults = InvalidObject.instance();
        } else {
            if (this.shouldUseWrapperPolicy) {
                cachableObject = this.session.wrapObject(object);
            }
            this.temporaryCachedQueryResults = cachableObject;
        }
    }


    /**
     * PUBLIC:
     * The cache will be checked only if the query contains exactly the primary key.
     * Queries can be configured to use the cache at several levels.
     * Other caching option are available.
     * @see #setCacheUsage(int)
     */
    public void checkCacheByExactPrimaryKey() {
        setCacheUsage(CheckCacheByExactPrimaryKey);
    }

    /**
     * PUBLIC:
     * This is the default, the cache will be checked only if the query contains the primary key.
     * Queries can be configured to use the cache at several levels.
     * Other caching option are available.
     * @see #setCacheUsage(int)
     */
    public void checkCacheByPrimaryKey() {
        setCacheUsage(CheckCacheByPrimaryKey);
    }

    /**
     * PUBLIC:
     * The cache will be checked completely, then if the object is not found or the query too complex the database will be queried.
     * Queries can be configured to use the cache at several levels.
     * Other caching option are available.
     * @see #setCacheUsage(int)
     */
    public void checkCacheThenDatabase() {
        setCacheUsage(CheckCacheThenDatabase);
    }

    /**
     * INTERNAL:
     * Ensure that the descriptor has been set.
     */
    public void checkDescriptor(AbstractSession session) throws QueryException {
        if (this.descriptor == null) {
            if (getReferenceClass() == null) {
                throw QueryException.referenceClassMissing(this);
            }
            ClassDescriptor referenceDescriptor;
            //Bug#3947714  In case getSelectionObject() is proxy            
            if (getSelectionObject() != null && session.getProject().hasProxyIndirection()) {
                referenceDescriptor = session.getDescriptor(getSelectionObject());            
            } else {
                referenceDescriptor = session.getDescriptor(getReferenceClass());                
            }
            if (referenceDescriptor == null) {
                throw QueryException.descriptorIsMissing(getReferenceClass(), this);
            }
            setDescriptor(referenceDescriptor);
        }
    }

    /**
     * INTERNAL:
     * The cache check is done before the prepare as a hit will not require the work to be done.
     */
    protected Object checkEarlyReturnLocal(AbstractSession session, AbstractRecord translationRow) {
        if (shouldCheckCache() && shouldMaintainCache() && (!shouldRefreshIdentityMapResult() && (!shouldRetrieveBypassCache()))
                && (!(session.isRemoteSession() && (shouldRefreshRemoteIdentityMapResult() || this.descriptor.shouldDisableCacheHitsOnRemote())))
                && (!(shouldCheckDescriptorForCacheUsage() && this.descriptor.shouldDisableCacheHits())) && (!this.descriptor.isDescriptorForInterface())) {
            Object cachedObject = getQueryMechanism().checkCacheForObject(translationRow, session);
            this.isCacheCheckComplete = true;

            // Optimization: If find deleted object by exact primary
            // key expression or selection object/key just abort.
            if (cachedObject == InvalidObject.instance) {
                return cachedObject;
            }
            if (cachedObject != null) {
                if (shouldLoadResultIntoSelectionObject()) {
                    ObjectBuilder builder = this.descriptor.getObjectBuilder();
                    builder.copyInto(cachedObject, getSelectionObject());
                    //put this object into the cache.  This may cause some loss of identity
                    session.getIdentityMapAccessorInstance().putInIdentityMap(getSelectionObject());
                    cachedObject = getSelectionObject();
                }

                // check locking.  If clone has not been locked, do not early return cached object
                if (isLockQuery() && (session.isUnitOfWork() && !((UnitOfWorkImpl)session).isPessimisticLocked(cachedObject))) {
                    return null;
                }
                if (QueryMonitor.shouldMonitor()) {
                    QueryMonitor.incrementReadObjectHits(this);
                }
                session.incrementProfile(SessionProfiler.CacheHits, this);
            } else {
                if (!session.isUnitOfWork()) {
                    if (QueryMonitor.shouldMonitor()) {
                        QueryMonitor.incrementReadObjectMisses(this);
                    }
                    session.incrementProfile(SessionProfiler.CacheMisses, this);
                }
            }
            if (shouldUseWrapperPolicy()) {
                cachedObject = this.descriptor.getObjectBuilder().wrapObject(cachedObject, session);
            }            
            return cachedObject;
        } else {
            if (!session.isUnitOfWork()) {
                if (QueryMonitor.shouldMonitor()) {
                    QueryMonitor.incrementReadObjectMisses(this);
                }
                session.incrementProfile(SessionProfiler.CacheMisses, this);
            }
            return null;
        }
    }

    /**
     * INTERNAL:
     * Check to see if a custom query should be used for this query.
     * This is done before the query is copied and prepared/executed.
     * null means there is none.
     */
    protected DatabaseQuery checkForCustomQuery(AbstractSession session, AbstractRecord translationRow) {
        if (this.descriptor == null) {
            checkDescriptor(session);
        }

        if (this.isCustomQueryUsed == null) {
            // Check if user defined a custom query in the query manager.
            if (!this.isUserDefined) {
                if (!isCallQuery()) {
                    DescriptorQueryManager descriptorQueryManager = this.descriptor.getQueryManager();
        
                    // By default all descriptors have a custom ("static") read-object query.
                    // This allows the read-object query and SQL to be prepare once.
                    if (descriptorQueryManager.hasReadObjectQuery()) {
                        // If the query require special SQL generation or execution do not use the static read object query.
                        // PERF: the read-object query should always be static to ensure no regeneration of SQL.
                        if ((!hasJoining() || !this.joinedAttributeManager.hasJoinedAttributeExpressions()) && (!hasPartialAttributeExpressions()) && (redirector == null) && !doNotRedirect && (!hasAsOfClause()) && (!hasNonDefaultFetchGroup())
                                && this.wasDefaultLockMode && (shouldBindAllParameters == null) && (this.hintString == null)) {
                            if ((this.selectionId != null) || (this.selectionObject != null)) {// Must be primary key.
                                this.isCustomQueryUsed = true;
                            } else {
                                Expression selectionCriteria = getSelectionCriteria();
                                if (selectionCriteria != null) {
                                    AbstractRecord primaryKeyRow = this.descriptor.getObjectBuilder().extractPrimaryKeyRowFromExpression(selectionCriteria, translationRow, session);                
                                    // Only execute the query if the selection criteria has the primary key fields set
                                    if (primaryKeyRow != null) {
                                        this.isCustomQueryUsed = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.isCustomQueryUsed == null) {
                this.isCustomQueryUsed = false;
            }
        }
        
        if (this.isCustomQueryUsed.booleanValue()) {
            ReadObjectQuery customQuery = this.descriptor.getQueryManager().getReadObjectQuery();
            if (this.accessors != null) {
                customQuery = (ReadObjectQuery) customQuery.clone();
                customQuery.setIsExecutionClone(true);
                customQuery.setAccessors(this.accessors);
            }
            return customQuery;
        } else {
            return null;
        }
    }

    /**
     * INTERNAL:
     * Conform the result in the UnitOfWork.
     */
    protected Object conformResult(Object result, UnitOfWorkImpl unitOfWork, AbstractRecord databaseRow, boolean buildDirectlyFromRows) {
        // Note that if the object does not conform even though other objects might exist on the database null is returned.
        // Note that new objects is checked before the read is executed so does not have to be re-checked.
        // Must unwrap as the built object is always wrapped.
        // Note the object is unwrapped on the parent which it belongs to, as we
        // do not want to trigger a registration just yet.
        Object clone = null;
        if (buildDirectlyFromRows) {
            clone = buildObject((AbstractRecord)result);
        } else {
            clone = registerIndividualResult(
                    this.descriptor.getObjectBuilder().unwrapObject(result, unitOfWork.getParent()),
                    null, unitOfWork, null, null);
        }
        Expression selectionCriteria = getSelectionCriteria();
        if ((selectionCriteria != null) && (this.selectionId == null) && (this.selectionObject == null)) {
            ExpressionBuilder builder = selectionCriteria.getBuilder();
            builder.setSession(unitOfWork.getRootSession(null));
            builder.setQueryClass(getReferenceClass());
        }

        clone = conformIndividualResult(clone, unitOfWork, databaseRow, selectionCriteria, null);
        if (clone == null) {
            return clone;
        }

        if (shouldUseWrapperPolicy()) {
            return this.descriptor.getObjectBuilder().wrapObject(clone, unitOfWork);
        } else {
            return clone;
        }
    }

    /**
     * PUBLIC:
     * Do not refesh/load into the selection object, this is the default.
     * This property allows for the selection object of the query to be refreshed or put into the TopLink cache.
     * By default on a read or refresh the object in the cache is refreshed and returned or a new object is built from the database,
     * in some cases such as EJB BMP it is desirable to refresh or load into the object passed into the read object query.
     * <p>Note: This forces the selection object into the cache a replaces any existing object that may already be there,
     * this is a strict violation of object identity and other objects can still be refering to the old object.
     */
    public void dontLoadResultIntoSelectionObject() {
        setShouldLoadResultIntoSelectionObject(false);
    }

    /**
     * INTERNAL:
     * Execute the query. If there are cached results return those.
     * This must override the super to support result caching.
     *
     * @param session the session in which the receiver will be executed.
     * @return An object or vector, the result of executing the query.
     * @exception DatabaseException - an error has occurred on the database
     */
    public Object execute(AbstractSession session, AbstractRecord row) throws DatabaseException {
        if (shouldCacheQueryResults()) {
            if (shouldConformResultsInUnitOfWork()) {
                throw QueryException.cannotConformAndCacheQueryResults(this);
            }
            if (isPrepared()) {// only prepared queries can have cached results.
                Object result = getQueryResults(session, row, true);
                // Bug6138532 - if result is "cached no results", return null immediately
                if (result == InvalidObject.instance) {
                    return null;
                }
                if (result != null) {
                    if (session.isUnitOfWork()) {
                        result = ((UnitOfWorkImpl)session).registerExistingObject(result);
                    }
                    return result;
                }
            }
        }
        return super.execute(session, row);
    }

    /**
     * INTERNAL:
     * Execute the query.
     * Do a cache lookup and build object from row if required.
     * @exception  DatabaseException - an error has occurred on the database
     * @return object - the first object found or null if none.
     */
    protected Object executeObjectLevelReadQuery() throws DatabaseException {
        if (this.descriptor.isDescriptorForInterface()  || this.descriptor.hasTablePerClassPolicy()) {
            Object returnValue = this.descriptor.getInterfacePolicy().selectOneObjectUsingMultipleTableSubclassRead(this);
            
            if (this.descriptor.hasTablePerClassPolicy() && (!this.descriptor.isAbstract()) && (returnValue == null)) {
                // let it fall through to query the root.
            } else {
                this.executionTime = System.currentTimeMillis();
                return returnValue;
            }
        }
        
        boolean shouldSetRowsForJoins = hasJoining() && this.joinedAttributeManager.isToManyJoin();
        AbstractSession session = getSession();
        Object result = null;
        AbstractRecord row = null;
        
        checkResultSetAccessOptimization();
        
        if (this.usesResultSetAccessOptimization) {
            DatabaseCall call = ((DatasourceCallQueryMechanism)this.queryMechanism).selectResultSet();
            this.executionTime = System.currentTimeMillis();
            boolean exceptionOccured = false;
            ResultSet resultSet = call.getResult();
            DatabaseAccessor dbAccessor = (DatabaseAccessor)getAccessor();
            try {
                if (resultSet.next()) {
                    ResultSetMetaData metaData = call.getResult().getMetaData();
                    boolean useSimple = this.descriptor.getObjectBuilder().isSimple();  
                    if (useSimple) {
                        row = new SimpleResultSetRecord(call.getFields(), call.getFieldsArray(), resultSet, metaData, dbAccessor, getExecutionSession());
                        if (this.descriptor.isDescriptorTypeAggregate()) {
                            // Aggregate Collection may have an unmapped primary key referencing the owner, the corresponding field will not be used when the object is populated and therefore may not be cleared.
                            ((SimpleResultSetRecord)row).setShouldKeepValues(true);
                        }
                    } else {
                        row = new ResultSetRecord(call.getFields(), call.getFieldsArray(), resultSet, metaData, dbAccessor, getExecutionSession());
                    }
                    if (session.isUnitOfWork()) {
                        result = registerResultInUnitOfWork(row, (UnitOfWorkImpl)session, this.translationRow, true);
                    } else {
                        result = buildObject(row);
                    }
                        
                    if (!useSimple && !((ResultSetRecord)row).hasResultSet() && this.descriptor.getObjectBuilder().shouldKeepRow()) {
                        ((ResultSetRecord)row).removeNonIndirectionValues();
                    }
                }
            } catch (SQLException exception) {
                exceptionOccured = true;
                DatabaseException commException = dbAccessor.processExceptionForCommError(session, exception, call);
                if (commException != null) {
                    throw commException;
                }
                throw DatabaseException.sqlException(exception, call, getAccessor(), session, false);
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (dbAccessor != null) {
                        if (call.getStatement() != null) {
                            dbAccessor.releaseStatement(call.getStatement(), call.getSQLString(), call, session);
                        }
                    }
                    if (call.hasAllocatedConnection()) {
                        getExecutionSession().releaseConnectionAfterCall(this);
                    }
                } catch (RuntimeException cleanupException) {
                    if (!exceptionOccured) {
                        throw cleanupException;
                    }
                } catch (SQLException cleanupSQLException) {
                    if (!exceptionOccured) {
                        throw DatabaseException.sqlException(cleanupSQLException, call, dbAccessor, session, false);
                    }
                }
            }
        } else {
            // If using 1-m joins, must select all rows.
            if (shouldSetRowsForJoins) {
                List rows = getQueryMechanism().selectAllRows();
                if (rows.size() > 0) {
                    row = (AbstractRecord)rows.get(0);
                }
                getJoinedAttributeManager().setDataResults(rows, session);
            } else {
                row = getQueryMechanism().selectOneRow();
            }
            
            this.executionTime = System.currentTimeMillis();
            if (row != null) {
                if (session.isUnitOfWork()) {
                    result = registerResultInUnitOfWork(row, (UnitOfWorkImpl)session, this.translationRow, true);
                } else {
                    result = buildObject(row);
                }
            }
        }
        if ((result == null) && shouldCacheQueryResults()) {
            cacheResult(null);
        }
        if ((result == null) && this.shouldRefreshIdentityMapResult) {
            // bug5955326, should invalidate the shared cached if refreshed object no longer exists.
            if (this.selectionId != null) {
                session.getParentIdentityMapSession(this.descriptor, true, true).getIdentityMapAccessor().invalidateObject(this.selectionId, this.referenceClass);
            } else if (this.selectionObject != null) {
                session.getParentIdentityMapSession(this.descriptor, true, true).getIdentityMapAccessor().invalidateObject(this.selectionObject);
            }
        }                

        if (this.shouldIncludeData) {
            ComplexQueryResult complexResult = new ComplexQueryResult();
            complexResult.setResult(result);
            complexResult.setData(row);
            return complexResult;
        }

        return result;
    }
    
    /**
     * INTERNAL:
     * Execute the query building the objects directly from the database result-set.
     * @exception  DatabaseException - an error has occurred on the database
     * @return object - the first object found or null if none.
     */
    protected Object executeObjectLevelReadQueryFromResultSet() throws DatabaseException {
        AbstractSession session = this.session;
        DatabasePlatform platform = session.getPlatform();
        DatabaseCall call = ((DatasourceCallQueryMechanism)this.queryMechanism).selectResultSet();
        Statement statement = call.getStatement();
        ResultSet resultSet = call.getResult();
        DatabaseAccessor accessor = (DatabaseAccessor)((List<Accessor>)this.accessors).get(0);
        boolean exceptionOccured = false;
        try {
            if (!resultSet.next()) {
                return null;
            }
            ResultSetMetaData metaData = resultSet.getMetaData();
            return this.descriptor.getObjectBuilder().buildObjectFromResultSet(this, null, resultSet, session, accessor, metaData, platform, call.getFields(), call.getFieldsArray());
        } catch (SQLException exception) {
            exceptionOccured = true;
            DatabaseException commException = accessor.processExceptionForCommError(session, exception, call);
            if (commException != null) {
                throw commException;
            }
            throw DatabaseException.sqlException(exception, call, accessor, session, false);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    accessor.releaseStatement(statement, call.getSQLString(), call, session);
                }
                if (accessor != null) {
                    session.releaseReadConnection(accessor);
                }
            } catch (SQLException exception) {
                if (!exceptionOccured) {
                    //in the case of an external connection pool the connection may be null after the statement release
                    // if it is null we will be unable to check the connection for a comm error and
                    //therefore must return as if it was not a comm error.
                    DatabaseException commException = accessor.processExceptionForCommError(session, exception, call);
                    if (commException != null) {
                        throw commException;
                    }
                    throw DatabaseException.sqlException(exception, call, accessor, session, false);
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Extract the correct query result from the transporter.
     */
    public Object extractRemoteResult(Transporter transporter) {
        return ((DistributedSession)getSession()).getObjectCorrespondingTo(transporter.getObject(), transporter.getObjectDescriptors(), new IdentityHashMap(), this);
    }

    /**
     * INTERNAL:
     * Returns the specific default redirector for this query type.  There are numerous default query redirectors.
     * See ClassDescriptor for their types.
     */
    protected QueryRedirector getDefaultRedirector(){
        return descriptor.getDefaultReadObjectQueryRedirector();
    }

    /**
     * PUBLIC:
     * The primary key can be specified if used instead of an expression or selection object.
     * If composite the primary must be in the same order as defined in the descriptor.
     * @Depreacted since EclipseLink 2.1, replaced by getSelectionId()
     * @see #getSelectionId()
     */
    @Deprecated
    public Vector getSelectionKey() {
        if (this.selectionId instanceof CacheId) {
            return new Vector(Arrays.asList(((CacheId)this.selectionId).getPrimaryKey()));
        }
        Vector primaryKey = new Vector(1);
        primaryKey.add(this.selectionId);
        return (Vector)selectionId;

    }

    /**
     * PUBLIC:
     * Return the selection object of the query.
     * This can be used instead of a where clause expression for single object primary key queries.
     * The selection object given should have a primary key defined,
     * this primary key will be used to query the database instance of the same object.
     * This is a basic form of query by example where only the primary key is required,
     * it can be used for simple query forms, or testing.
     */
    public Object getSelectionObject() {
        return selectionObject;
    }

    /**
     * PUBLIC:
     * Return if this is a read object query.
     */
    public boolean isReadObjectQuery() {
        return true;
    }

    /**
     * INTERNAL:
     * Return if the query is by primary key.
     */
    public boolean isPrimaryKeyQuery() {
        return (this.selectionId != null) || (this.selectionObject != null);
    }

    /**
     * PUBLIC:
     * Allow for the selection object of the query to be refreshed or put into the EclipseLink cache.
     * By default on a read or refresh the object in the cache is refreshed and returned or a new object is built from the database,
     * in some cases such as EJB BMP it is desirable to refresh or load into the object passed into the read object query.
     * <p>Note: This forces the selection object into the cache a replaces any existing object that may already be there,
     * this is a strict violation of object identity and other objects can still be referring to the old object.
     */
    public void loadResultIntoSelectionObject() {
        setShouldLoadResultIntoSelectionObject(true);
    }
    
    /**
     * INTERNAL:
     * Copy all setting from the query.
     * This is used to morph queries from one type to the other.
     * By default this calls prepareFromQuery, but additional properties may be required
     * to be copied as prepareFromQuery only copies properties that affect the SQL.
     */
    public void copyFromQuery(DatabaseQuery query) {
        super.copyFromQuery(query);
        if (query.isReadObjectQuery()) {
            ReadObjectQuery readQuery = (ReadObjectQuery)query;
            this.selectionId = readQuery.selectionId;
            this.selectionObject = readQuery.selectionObject;
            this.shouldLoadResultIntoSelectionObject = readQuery.shouldLoadResultIntoSelectionObject;
        }
    }
    
    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    protected void prepare() throws QueryException {
        if (prepareFromCachedQuery()) {
            return;
        }
        super.prepare();
        
        if ((this.selectionId != null) || (this.selectionObject != null)) {
            // The expression is set in the prepare as params.
            setSelectionCriteria(this.descriptor.getObjectBuilder().getPrimaryKeyExpression());
            setExpressionBuilder(getSelectionCriteria().getBuilder());
            extendPessimisticLockScope();
            // For bug 2989998 the translation row is required to be set at this point.
            if (!shouldPrepare()) {
                if (this.selectionId != null) {
                    // Row must come from the key.
                    setTranslationRow(this.descriptor.getObjectBuilder().buildRowFromPrimaryKeyValues(this.selectionId, this.session));
                } else {//(getSelectionObject() != null)
                    setTranslationRow(this.descriptor.getObjectBuilder().buildRowForTranslation(this.selectionObject, this.session));
                }
            }
        }

        if (this.descriptor.isDescriptorForInterface()) {
            return;
        }
        
        // PERF: Disable cache check if not a primary key query.
        if (isExpressionQuery()) {
            Expression selectionCriteria = getSelectionCriteria();
            if (selectionCriteria != null) {
                if (((this.cacheUsage == CheckCacheByPrimaryKey)
                                && (!this.descriptor.getObjectBuilder().isPrimaryKeyExpression(false, selectionCriteria, this.session)))
                        || ((this.cacheUsage == CheckCacheByExactPrimaryKey)
                                && (!this.descriptor.getObjectBuilder().isPrimaryKeyExpression(true, selectionCriteria, this.session)))) {
                    this.cacheUsage = DoNotCheckCache;
                }
            }
        }
        
        // If using 1-m joining select all rows.
        if ((this.joinedAttributeManager != null) && this.joinedAttributeManager.isToManyJoin()) {
            getQueryMechanism().prepareSelectAllRows();
        } else {
            getQueryMechanism().prepareSelectOneRow();
        }

        // should be called after prepareSelectRow so that the call knows whether it returns ResultSet
        prepareResultSetAccessOptimization();
    }
    
    /**
     * INTERNAL:
     * Set the properties needed to be cascaded into the custom query including the translation row.
     * This is used only for primary key queries, as the descriptor query manager
     * stores a predefined query for this query to avoid having to re-prepare and allow for customization.
     */
    protected void prepareCustomQuery(DatabaseQuery customQuery) {
        super.prepareCustomQuery(customQuery);
        ReadObjectQuery customReadQuery = (ReadObjectQuery)customQuery;
        customReadQuery.shouldRefreshIdentityMapResult = this.shouldRefreshIdentityMapResult;
        customReadQuery.cascadePolicy = this.cascadePolicy;
        customReadQuery.shouldMaintainCache = this.shouldMaintainCache;
        customReadQuery.shouldUseWrapperPolicy = this.shouldUseWrapperPolicy;
        // CR... was missing some values, execution could cause infinite loop.
        customReadQuery.queryId = this.queryId;
        customReadQuery.executionTime = this.executionTime;
        customReadQuery.shouldLoadResultIntoSelectionObject = this.shouldLoadResultIntoSelectionObject;
        AbstractRecord primaryKeyRow;
        if (this.selectionObject != null) {
            // CR#... Must also set the selection object as may be loading into the object (refresh)
            customReadQuery.selectionObject = this.selectionObject;
            // The translation/primary key row will be set in prepareForExecution.
        } else if (this.selectionId != null) {
            customReadQuery.selectionId = this.selectionId;
        } else {
            // The primary key row must be used.
            primaryKeyRow = customQuery.getDescriptor().getObjectBuilder().extractPrimaryKeyRowFromExpression(getSelectionCriteria(), customQuery.getTranslationRow(), customReadQuery.getSession());
            customReadQuery.setTranslationRow(primaryKeyRow);
        }
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    public void prepareForExecution() throws QueryException {
        super.prepareForExecution();

        // For bug 2989998 the translation row now sometimes set earlier in prepare.
        if (shouldPrepare()) {
            if (this.selectionId != null) {
                // Row must come from the key.
                this.translationRow = this.descriptor.getObjectBuilder().buildRowFromPrimaryKeyValues(this.selectionId, getSession());
            } else if (this.selectionObject != null) {
                // The expression is set in the prepare as params.
                this.translationRow = this.descriptor.getObjectBuilder().buildRowForTranslation(this.selectionObject, getSession());
            }
        }
        
        // If we have tenant discriminator fields we need to add them to the 
        // database row when doing a primary key query.
        // Modifying the translation row here will modify it on the original 
        // query which is not good (will append the tenant field to the sql call 
        // for subsequent queries) The translation row must be cloned to isolate 
        // this.
        if (getDescriptor().hasMultitenantPolicy()) {
            this.translationRow = this.translationRow.clone();
            getDescriptor().getMultitenantPolicy().addFieldsToRow(this.translationRow, getSession());            
        }
    }

    /**
     * INTERNAL:
     * Prepare the receiver for execution in a session.
     */
    protected void prePrepare() throws QueryException {
        super.prePrepare();
        //Bug#3947714  In case getSelectionObject() is proxy            
        if (getSelectionObject() != null && getSession().getProject().hasProxyIndirection()) {
            setSelectionObject(ProxyIndirectionPolicy.getValueFromProxy(getSelectionObject()));
        }
    }

    /**
     * INTERNAL:
     * All objects queried via a UnitOfWork get registered here.  If the query
     * went to the database.
     * <p>
     * Involves registering the query result individually and in totality, and
     * hence refreshing / conforming is done here.
     * @param result may be collection (read all) or an object (read one),
     * or even a cursor.  If in transaction the shared cache will
     * be bypassed, meaning the result may not be originals from the parent
     * but raw database rows.
     * @param unitOfWork the unitOfWork the result is being registered in.
     * @param arguments the original arguments/parameters passed to the query
     * execution.  Used by conforming
     * @param buildDirectlyFromRows If in transaction must construct
     * a registered result from raw database rows.
     * @return the final (conformed, refreshed, wrapped) UnitOfWork query result
     */
    public Object registerResultInUnitOfWork(Object result, UnitOfWorkImpl unitOfWork, AbstractRecord arguments, boolean buildDirectlyFromRows) {
        if (result == null) {
            return null;
        }
        if (unitOfWork.hasCloneMapping() // PERF: Avoid conforming empty uow.
                && (shouldConformResultsInUnitOfWork() || this.descriptor.shouldAlwaysConformResultsInUnitOfWork())) {
            return conformResult(result, unitOfWork, arguments, buildDirectlyFromRows);
        }
        Object clone = null;
        if (buildDirectlyFromRows) {
            clone = buildObject((AbstractRecord)result);
        } else {
            clone = registerIndividualResult(result, null, unitOfWork, null, null);
        }

        if (shouldUseWrapperPolicy()) {
            clone = this.descriptor.getObjectBuilder().wrapObject(clone, unitOfWork);
        }
        return clone;
    }

    protected Object remoteExecute() {
        // Do a cache lookup.
        checkDescriptor(session);
        // As the selection object is transient, compute the key.
        if (getSelectionObject() != null) {
            // Must be checked separately as the expression and row is not yet set.
            setSelectionId(getDescriptor().getObjectBuilder().extractPrimaryKeyFromObject(getSelectionObject(), session));
        }

        Object cacheHit = checkEarlyReturn(getSession(), getTranslationRow());
        if ((cacheHit != null) || shouldCheckCacheOnly()) {
            if (cacheHit == InvalidObject.instance) {
                return null;
            }
            return cacheHit;
        }

        return super.remoteExecute();
    }

    /**
     * INTERNAL:
     * replace the value holders in the specified result object(s)
     */
    public Map replaceValueHoldersIn(Object object, RemoteSessionController controller) {
        return controller.replaceValueHoldersIn(object);
    }

    /**
     * INTERNAL:
     * Return the primary key stored in this query 
     * 
     * @return the selection id of this ReadObjectQuery
     */
    @Override
    protected Object getQueryPrimaryKey(){
        return getSelectionId();
    }
    
    /**
     * PUBLIC:
     * Return Id of the object to be selected by the query.
     */
    public Object getSelectionId() {
        return this.selectionId;
    }

    /**
     * PUBLIC:
     * The Id of the object to be selected by the query.
     * This will generate a query by primary key.
     * This can be used instead of an Expression, SQL, or JPQL, or example object.
     * The Id is the Id value for a singleton primary key,
     * for a composite it is an instance of CacheId.
     * @see CacheId
     */
    public void setSelectionId(Object id) {
        this.selectionId = id;
    }

    /**
     * PUBLIC:
     * The primary key can be specified if used instead of an expression or selection object.
     * If composite the primary must be in the same order as defined in the descriptor.
     * @depreacted since EclipseLink 2.1, replaced by setSelectionId(Object)
     * @see #setSelectionId(Object)
     */
    @Deprecated
    public void setSelectionKey(List selectionKey) {
        if (selectionKey == null) {
            this.selectionId = null;
        } else if (selectionKey.size() == 1) {
            this.selectionId = selectionKey.get(0);            
        } else {
            this.selectionId = new CacheId(selectionKey.toArray());
        }
    }

    /**
     * PUBLIC:
     * Used to set the where clause of the query.
     * This can be used instead of a where clause expression for single object primary key queries.
     * The selection object given should have a primary key defined,
     * this primary key will be used to query the database instance of the same object.
     * This is a basic form of query by example where only the primary key is required,
     * it can be used for simple query forms, or testing.
     */
    public void setSelectionObject(Object selectionObject) {
        if (selectionObject == null) {
            throw QueryException.selectionObjectCannotBeNull(this);
        }
        setSelectionId(null);
        // Check if the query needs to be unprepared.
        if ((this.selectionObject == null) || (this.selectionObject.getClass() != selectionObject.getClass())) {
            setIsPrepared(false);
        }
        setReferenceClass(selectionObject.getClass());
        this.selectionObject = selectionObject;
    }

    /**
     * PUBLIC:
     * Allow for the selection object of the query to be refreshed or put into the EclipseLink cache.
     * By default on a read or refresh the object in the cache is refreshed and returned or a new object is built from the database,
     * in some cases such as EJB BMP it is desirable to refresh or load into the object passed into the read object query.
     * <p>Note: This forces the selection object into the cache a replaces any existing object that may already be there,
     * this is a strict violation of object identity and other objects can still be referring to the old object.
     */
    public void setShouldLoadResultIntoSelectionObject(boolean shouldLoadResultIntoSelectionObject) {
        this.shouldLoadResultIntoSelectionObject = shouldLoadResultIntoSelectionObject;
    }

    /**
     * PUBLIC:
     * The primary key can be specified if used instead of an expression or selection object.
     * @Depreacted since EclipseLink 2.1, replaced by setSelectionId(Object)
     * @see #setSelectionId(Object)
     */
    @Deprecated
    public void setSingletonSelectionKey(Object selectionKey) {
        Vector key = new NonSynchronizedVector();
        key.add(selectionKey);
        setSelectionKey(key);
    }

    /**
     * PUBLIC:
     * Return if cache should be checked.
     */
    public boolean shouldCheckCacheByExactPrimaryKey() {
        return this.cacheUsage == CheckCacheByExactPrimaryKey;
    }

    /**
     * PUBLIC:
     * Return if cache should be checked.
     */
    public boolean shouldCheckCacheByPrimaryKey() {
        return (this.cacheUsage == CheckCacheByPrimaryKey) || (this.cacheUsage == UseDescriptorSetting);
    }

    /**
     * PUBLIC:
     * Return if cache should be checked.
     */
    public boolean shouldCheckCacheThenDatabase() {
        return this.cacheUsage == CheckCacheThenDatabase;
    }

    /**
     * PUBLIC:
     * return true if the result should be loaded into the passed in selection Object
     */
    public boolean shouldLoadResultIntoSelectionObject() {
        return shouldLoadResultIntoSelectionObject;
    }

    /**
     * INTERNAL:
     * Return if the query has an non-default fetch group defined for itself.
     */
    protected boolean hasNonDefaultFetchGroup() {
        return this.descriptor.hasFetchGroupManager() && ((this.fetchGroup != null) || (this.fetchGroupName != null)
                || (!this.shouldUseDefaultFetchGroup && (this.descriptor.getFetchGroupManager().getDefaultFetchGroup() != null)));

    }
}
