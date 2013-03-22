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
package org.eclipse.persistence.internal.sessions;

import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Provides isolation support by allowing a client session
 * to have a local cache of the subset of the classes.
 * This can be used to avoid caching frequently changing data,
 * or for security or VPD purposes.
 */
public class IsolatedClientSession extends ClientSession {
    public IsolatedClientSession(ServerSession parent, ConnectionPolicy connectionPolicy) {
        super(parent, connectionPolicy);
        // PERF: Cache the write-lock check to avoid cost of checking in every register/clone.
        this.shouldCheckWriteLock = getDatasourceLogin().shouldSynchronizedReadOnWrite() || getDatasourceLogin().shouldSynchronizeWrites();
    }

    public IsolatedClientSession(ServerSession parent, ConnectionPolicy connectionPolicy, Map properties) {
        super(parent, connectionPolicy, properties);
        // PERF: Cache the write-lock check to avoid cost of checking in every register/clone.
        this.shouldCheckWriteLock = getDatasourceLogin().shouldSynchronizedReadOnWrite() || getDatasourceLogin().shouldSynchronizeWrites();
   }

    /**
    * INTERNAL:
    * Set up the IdentityMapManager.  This method allows subclasses of Session to override
    * the default IdentityMapManager functionality.
    */
    @Override
    public void initializeIdentityMapAccessor() {
        this.identityMapAccessor = new IsolatedClientSessionIdentityMapAccessor(this);
    }

    /**
    * INTERNAL:
    * Helper method to calculate whether to execute this query locally or send
    * it to the server session.
    */
    protected boolean shouldExecuteLocally(DatabaseQuery query) {
        if (isIsolatedQuery(query)) {
            return true;
        }
        return isInTransaction();
    }

    /**
    * INTERNAL: Answers if this query is an isolated query and must be
    * executed locally.
    */
    protected boolean isIsolatedQuery(DatabaseQuery query) {
        query.checkDescriptor(this);
        ClassDescriptor descriptor = query.getDescriptor();
        if (query.isDataModifyQuery() || query.isDataReadQuery() || (descriptor != null && descriptor.getCachePolicy().isIsolated())
                || (query.isObjectBuildingQuery() && ((ObjectBuildingQuery)query).shouldUseExclusiveConnection())) {
            // For CR#4334 if in transaction stay on client session.
            // That way client's write accessor will be used for all queries.
            // This is to preserve transaction isolation levels.
            // also if this is an isolated class and we are in an isolated session
            //load locally.
            return true;
        }
        return false;
    }

    /**
     * INTERNAL:
     * Returns the appropriate IdentityMap session for this descriptor.  Sessions can be 
     * chained and each session can have its own Cache/IdentityMap.  Entities can be stored
     * at different levels based on Cache Isolation.  This method will return the correct Session
     * for a particular Entity class based on the Isolation Level and the attributes provided.
     * <p>
     * @param canReturnSelf true when method calls itself.  If the path
     * starting at <code>this</code> is acceptable.  Sometimes true if want to
     * move to the first valid session, i.e. executing on ClientSession when really
     * should be on ServerSession.
     * @param terminalOnly return the last session in the chain where the Enitity is stored.
     * @return Session with the required IdentityMap
     */
    @Override
    public AbstractSession getParentIdentityMapSession(ClassDescriptor descriptor, boolean canReturnSelf, boolean terminalOnly) {
        if (canReturnSelf && (descriptor == null || descriptor.getCachePolicy().isIsolated()
                || (descriptor.getCachePolicy().isProtectedIsolation() && !descriptor.shouldIsolateProtectedObjectsInUnitOfWork()
                        && !terminalOnly))){
            return this;
        }
        return getParent().getParentIdentityMapSession(descriptor, canReturnSelf, terminalOnly);
    }

    /**
     * INTERNAL:
     * For use within the merge process this method will get an object from the shared 
     * cache using a readlock.  If a readlock is unavailable then the merge manager will be 
     * transitioned to deferred locks and a deferred lock will be used.
     */
    @Override
    protected CacheKey getCacheKeyFromTargetSessionForMerge(Object implementation, ObjectBuilder builder, ClassDescriptor descriptor, MergeManager mergeManager){
        Object primaryKey = builder.extractPrimaryKeyFromObject(implementation, this, true);
        CacheKey cacheKey = getIdentityMapAccessorInstance().getCacheKeyForObject(primaryKey, implementation.getClass(), descriptor, true);
        return cacheKey;
    }

    /**
    * INTERNAL:
    * Gets the session which this query will be executed on.
    * Generally will be called immediately before the call is translated,
    * which is immediately before session.executeCall.
    * <p>
    * Since the execution session also knows the correct datasource platform
    * to execute on, it is often used in the mappings where the platform is
    * needed for type conversion, or where calls are translated.
    * <p>
    * Is also the session with the accessor.  Will return a ClientSession if
    * it is in transaction and has a write connection.
    * @return a session with a live accessor
    * @param query may store session name or reference class for brokers case
    */
    @Override
    public AbstractSession getExecutionSession(DatabaseQuery query) {
        if (shouldExecuteLocally(query)) {
            return this;
        } else {
            return this.parent.getExecutionSession(query);
        }
    }

    /**
     * PUBLIC:
     * Return if this session is an isolated client session.
     */
    @Override
    public boolean isIsolatedClientSession() {
        return true;
    }    

    /**
     * PUBLIC:
     * Returns true if Protected Entities should be built within this session
     */
    public boolean isProtectedSession(){
        return true;
    }

}
