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
package org.eclipse.persistence.internal.indirection;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadQuery;

/**
 * QueryBasedValueHolder wraps a database-stored object and implements behavior
 * to access it. The object is read from the database by invoking a
 * user-specified query.
 *
 * @see ObjectLevelReadQuery
 * @author Dorin Sandu
 */
public class QueryBasedValueHolder extends DatabaseValueHolder {

    /**
     * Stores the query to be executed.
     */
    protected transient ReadQuery query;
    protected transient Object sourceObject;
    protected Integer refreshCascade;

    protected QueryBasedValueHolder() {
    }

    /**
     * Initialize the query-based value holder.
     */
    public QueryBasedValueHolder(ReadQuery query, AbstractRecord row, AbstractSession session) {
        this(query, null, row, session);
    }

    /**

    /**
     * Store the uow identity so that it can be used to determine new
     * transaction logic
     */
    /**
     * Initialize the query-based value holder.
     */
    public QueryBasedValueHolder(ReadQuery query, Object sourceObject, AbstractRecord row, AbstractSession session) {
        this.row = row;
        this.session = session;

        // Make sure not to put a ClientSession or IsolatedClientSession in
        // the shared cache (indirectly).
        // Skip this if unitOfWork, for we use session.isUnitOfWork() to
        // implement
        // isTransactionalValueholder(), saving us from needing a boolean
        // instance variable.
        // If unitOfWork this safety measure is deferred until merge time with
        // releaseWrappedValuehHolder.
        // Note that if isolated session & query will return itself, which is
        // safe
        // for if isolated it will not go in the shared cache.
        if (!session.isUnitOfWork()) {
            this.session = session.getRootSession(query);
        }
        this.query = query;
        this.sourceObject = sourceObject;
    }

    /**
     * INTERNAL:
     * Returns the refresh cascade policy that was set on the query that was used to instantiate the valueholder
     * a null value means that a non refresh query was used.
     */
    public Integer getRefreshCascadePolicy(){
        return this.refreshCascade;
    }

    /**
     * Process against the UOW and attempt to load a local copy before going to the shared cache
     * If null is returned then the calling UOW will instantiate as normal.
     */
    @Override
    public Object getValue(UnitOfWorkImpl uow) {
        if (this.query.isReadObjectQuery()){
            return this.query.getQueryMechanism().checkCacheForObject(this.query.getTranslationRow(), uow);
        }
        //not able to shortcircuit cache lookup to UOW return null;
        return null;
    }

    /**
     * Return the query. The query for a QueryBasedValueHolder is constructed
     * dynamically based on the original query on the parent object and the
     * mapping configuration. If modifying a query the developer must be careful
     * not to change the results returned as that may cause the application to
     * see incorrect results.
     */
    public ReadQuery getQuery() {
        return query;
    }

    protected Object instantiate() throws DatabaseException {
        return instantiate(this.session);
    }

    /**
     * Instantiate the object by executing the query on the session.
     */
    protected Object instantiate(AbstractSession session) throws DatabaseException {
        if (session == null) {
            throw ValidationException.instantiatingValueholderWithNullSession();
        }
        if (this.query.isObjectBuildingQuery() && ((ObjectBuildingQuery)this.query).shouldRefreshIdentityMapResult()){
            this.refreshCascade = ((ObjectBuildingQuery)this.query).getCascadePolicy();
        }
        Object result = session.executeQuery(getQuery(), getRow());
        // Bug 489898 - ensure that the query's session is dereferenced, post-execution
        getQuery().setSession(null);
        return result;
    }

    /**
     * Triggers UnitOfWork valueholders directly without triggering the wrapped
     * valueholder (this).
     * <p>
     * When in transaction and/or for pessimistic locking the
     * UnitOfWorkValueHolder needs to be triggered directly without triggering
     * the wrapped valueholder. However only the wrapped valueholder knows how
     * to trigger the indirection, i.e. it may be a batchValueHolder, and it
     * stores all the info like the row and the query. Note: This method is not
     * thread-safe. It must be used in a synchronized manner
     */
    public Object instantiateForUnitOfWorkValueHolder(UnitOfWorkValueHolder unitOfWorkValueHolder) {
        return instantiate(unitOfWorkValueHolder.getUnitOfWork());
    }

    /**
     * INTERNAL:
     * Run any extra code required after the valueholder instantiates
     * For query based VH, we notify the cache that the valueholder has been triggered
     */
    public void postInstantiate(){
        DatabaseMapping mapping = query.getSourceMapping();
        if (mapping != null && mapping.isForeignReferenceMapping()){
            // Fix for Bug#474232
            ClassDescriptor descriptor = mapping.getDescriptor();
            final IndirectionPolicy indirectionPolicy = ((ForeignReferenceMapping) mapping).getIndirectionPolicy();
            if (indirectionPolicy != null && indirectionPolicy.isWeavedObjectBasicIndirectionPolicy()) {
                if (!isCoordinatedWithProperty && mapping.isObjectReferenceMapping() && sourceObject != null && value != null) {
                    // Bug#487930
                    Object object = sourceObject;
                    if (descriptor != null && descriptor.isAggregateDescriptor()) {
                        // navigate to the leaf element in the accessor tree
                        for(AttributeAccessor accessor : descriptor.getAccessorTree()) {
                            object = accessor.getAttributeValueFromObject(object);
                        }
                    }
                    mapping.setAttributeValueInObject(object, this);
                    isCoordinatedWithProperty = true;
                }
            }

            if (descriptor == null || descriptor.isAggregateDescriptor()){
                descriptor = session.getDescriptor(sourceObject);
            }
            if (descriptor != null){
                session.getIdentityMapAccessorInstance().getIdentityMap(descriptor).lazyRelationshipLoaded(sourceObject, this, (ForeignReferenceMapping)query.getSourceMapping());
            }
        }
    }

    /**
     * Releases a wrapped valueholder privately owned by a particular unit of
     * work.
     * <p>
     * When unit of work clones are built directly from rows no object in the
     * shared cache points to this valueholder, so it can store the unit of work
     * as its session. However once that UnitOfWork commits and the valueholder
     * is merged into the shared cache, the session needs to be reset to the
     * root session, ie. the server session.
     */
    @Override
    public void releaseWrappedValueHolder(AbstractSession targetSession) {
        AbstractSession session = getSession();
        if ((session != null) && session.isUnitOfWork()) {
            setSession(targetSession);
        }
    }

    /**
     * Reset all the fields that are not needed after instantiation.
     */
    protected void resetFields() {
        super.resetFields();
        this.query = null;
    }

    /**
     * Set the query.
     */
    protected void setQuery(ReadQuery theQuery) {
        query = theQuery;
    }

    /**
     * INTERNAL: Answers if this valueholder is a pessimistic locking one. Such
     * valueholders are special in that they can be triggered multiple times by
     * different UnitsOfWork. Each time a lock query will be issued. Hence even
     * if instantiated it may have to be instantiated again, and once
     * instantiated all fields can not be reset.
     * <p>
     * Since locks will be issued each time this valueholder is triggered,
     * triggering this directly on the session in auto commit mode will generate
     * an exception. This only UnitOfWorkValueHolder's wrapping this can trigger
     * it. Note: This method is not thread-safe. It must be used in a
     * synchronized manner
     */
    public boolean isPessimisticLockingValueHolder() {
        // Get the easy checks out of the way first.
        if ((this.query == null) || !this.query.isObjectLevelReadQuery()) {
            return false;
        }
        ObjectLevelReadQuery query = (ObjectLevelReadQuery) this.query;

        // Note even if the reference class is not locked, but the valueholder
        // query
        // has joined attributes, then this may count as a lock query.
        // This means it is possible to trigger a valueholder to get an object
        // which
        // is not to be pess. locked and get an exception for triggering it on
        // the
        // session outside a transaction.
        return query.isLockQuery(this.session);
    }

    public void setSourceObject(Object sourceObject) {
        this.sourceObject = sourceObject;
    }

    /**
     * INTERNAL:
     * @param refreshCascadePolicy
     * @return
     */
    public void setRefreshCascadePolicy(Integer refreshCascadePolicy) {
        this.refreshCascade = refreshCascadePolicy;
    }

}
