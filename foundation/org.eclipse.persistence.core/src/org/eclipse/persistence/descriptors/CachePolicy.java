/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheKeyType;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * <p><b>Purpose</b>:
 * CachePolicy defines the cache configuration.
 * EclipseLink supports an integrated shared (L2) object cache.
 * Caching is enabled by default.<br/>
 * To disable caching use:<br/>
 * <code>setCacheIsolation(CacheIsolationType.ISOLATED)</code>
 *
 * @see ClassDescriptor
 */
public class CachePolicy implements Cloneable, Serializable {
    protected Class identityMapClass;
    protected int identityMapSize;
    protected boolean shouldAlwaysRefreshCache;
    protected boolean shouldOnlyRefreshCacheIfNewerVersion;
    protected boolean shouldDisableCacheHits;
    
    protected Class remoteIdentityMapClass;
    protected int remoteIdentityMapSize;
    protected boolean shouldAlwaysRefreshCacheOnRemote;
    protected boolean shouldDisableCacheHitsOnRemote;

    // this attribute is used to determine what classes should be isolated from the shared cache
    // and the severity of their isolation.
    protected CacheIsolationType cacheIsolation;

    /** Configures how objects will be sent via cache synchronization, if synchronization is enabled. */
    protected int cacheSynchronizationType = UNDEFINED_OBJECT_CHANGE_BEHAVIOR;
    public static final int UNDEFINED_OBJECT_CHANGE_BEHAVIOR = 0;
    public static final int SEND_OBJECT_CHANGES = 1;
    public static final int INVALIDATE_CHANGED_OBJECTS = 2;
    public static final int SEND_NEW_OBJECTS_WITH_CHANGES = 3;
    public static final int DO_NOT_SEND_CHANGES = 4;

    /** Configures how the unit of work uses the session cache. */
    protected int unitOfWorkCacheIsolationLevel = UNDEFINED_ISOLATATION;
    /* Required to resolve initialization. */
    protected boolean wasDefaultUnitOfWorkCacheIsolationLevel;
    public static final int UNDEFINED_ISOLATATION = -1;
    public static final int USE_SESSION_CACHE_AFTER_TRANSACTION = 0;
    public static final int ISOLATE_NEW_DATA_AFTER_TRANSACTION = 1; // this is the default behaviour even when undefined.
    public static final int ISOLATE_CACHE_AFTER_TRANSACTION = 2;
    public static final int ISOLATE_FROM_CLIENT_SESSION = 3;  // Entity Instances only exist in UOW and shared cache.
    public static final int ISOLATE_CACHE_ALWAYS = 4;

    /** Allow cache key type to be configured. */
    protected CacheKeyType cacheKeyType;
        
    //Added for interceptor support.
    protected Class cacheInterceptorClass;
    //Added for interceptor support.
    protected String cacheInterceptorClassName;
        
    /** This flag controls how the MergeManager should merge an Entity when merging into the shared cache.*/
    protected boolean fullyMergeEntity;
    
    protected Map<List<DatabaseField>, CacheIndex> cacheIndexes;

    /** Allows configuration of database change event notification. */
    protected DatabaseChangeNotificationType databaseChangeNotificationType;

    /**
     * PUBLIC:
     * Return a new descriptor.
     */
    public CachePolicy() {
        this.identityMapSize = -1;
        this.remoteIdentityMapSize = -1;
    }
    
    /**
     * Returns what type of database change notification an entity/descriptor should use.
     * This is only relevant if the persistence unit/session has been configured with a DatabaseEventListener,
     * such as the OracleChangeNotificationListener that receives database change events.
     * This allows for the EclipseLink cache to be invalidated or updated from database changes.
     */
    public DatabaseChangeNotificationType getDatabaseChangeNotificationType() {
        return databaseChangeNotificationType;
    }
    
    /**
     * Configures what type of database change notification an entity/descriptor should use.
     * This is only relevant if the persistence unit/session has been configured with a DatabaseEventListener,
     * such as the OracleChangeNotificationListener that receives database change events.
     * This allows for the EclipseLink cache to be invalidated or updated from database changes.
     */
    public void setDatabaseChangeNotificationType(DatabaseChangeNotificationType databaseChangeNotificationType) {
        this.databaseChangeNotificationType = databaseChangeNotificationType;
    }

    /**
     * INTERNAL:
     * Allow the inheritance properties of the descriptor to be initialized.
     * The descriptor's parent must first be initialized.
     */
    public void initializeFromParent(CachePolicy parentPolicy, ClassDescriptor descriptor, ClassDescriptor descriptorDescriptor, AbstractSession session) throws DescriptorException {
        // If the parent is isolated, then the child must also be isolated.
        if (!parentPolicy.isSharedIsolation()) {
            if (!isIsolated() && (getCacheIsolation() != parentPolicy.getCacheIsolation())) {
                session.log(SessionLog.WARNING, SessionLog.METADATA, "overriding_cache_isolation",
                        new Object[]{descriptorDescriptor.getAlias(),
                                parentPolicy.getCacheIsolation(), descriptor.getAlias(),
                                getCacheIsolation()});
                setCacheIsolation(parentPolicy.getCacheIsolation());                    
            }
        }
        // Child must maintain the same indexes as the parent.
        for (CacheIndex index : parentPolicy.getCacheIndexes().values()) {
            addCacheIndex(index);
        }
        if ((getDatabaseChangeNotificationType() == null) && (parentPolicy.getDatabaseChangeNotificationType() != null)) {
            setDatabaseChangeNotificationType(parentPolicy.getDatabaseChangeNotificationType());
        }
        if ((getCacheSynchronizationType() == UNDEFINED_OBJECT_CHANGE_BEHAVIOR)
                && (parentPolicy.getCacheSynchronizationType() != UNDEFINED_OBJECT_CHANGE_BEHAVIOR)) {
            setCacheSynchronizationType(parentPolicy.getCacheSynchronizationType());
        }
    }

    /**
     * INTERNAL:
     * Initialize the cache isolation setting.
     * This may need to be called multiple times as notifyReferencingDescriptorsOfIsolation() can change the cache isolation.
     */
    public void postInitialize(ClassDescriptor descriptor, AbstractSession session) throws DescriptorException {
        if (!isSharedIsolation()) {
            descriptor.notifyReferencingDescriptorsOfIsolation(session);
        }
        
        // PERF: If using isolated cache, then default uow isolation to always (avoids merge/double build).
        if ((getUnitOfWorkCacheIsolationLevel() == UNDEFINED_ISOLATATION) || this.wasDefaultUnitOfWorkCacheIsolationLevel) {
            this.wasDefaultUnitOfWorkCacheIsolationLevel = true;
            if (isIsolated()) {
                setUnitOfWorkCacheIsolationLevel(ISOLATE_CACHE_ALWAYS);
            } else if (isProtectedIsolation()) {
                setUnitOfWorkCacheIsolationLevel(ISOLATE_FROM_CLIENT_SESSION);
            } else {
                setUnitOfWorkCacheIsolationLevel(ISOLATE_NEW_DATA_AFTER_TRANSACTION);
            }
        }

        // Record that there is an isolated class in the project.
        if (!isSharedIsolation()) {
            session.getProject().setHasIsolatedClasses(true);
        }
        if (!shouldIsolateObjectsInUnitOfWork() && !descriptor.shouldBeReadOnly()) {
            session.getProject().setHasNonIsolatedUOWClasses(true);
        }
    }

    /**
     * INTERNAL:
     * Allow the inheritance properties of the descriptor to be initialized.
     * The descriptor's parent must first be initialized.
     */
    public void initialize(ClassDescriptor descriptor, AbstractSession session) throws DescriptorException {        
        if (hasCacheIndexes()) {
            for (CacheIndex index : getCacheIndexes().values()) {
                for (int count = 0; count < index.getFields().size(); count++) {
                    index.getFields().set(count, descriptor.buildField(index.getFields().get(count)));
                }
            }
        }
        for (DatabaseMapping mapping : descriptor.getMappings()) {
            if (!mapping.isCacheable()) {
                if (isSharedIsolation()) {
                    setCacheIsolation(CacheIsolationType.PROTECTED);
                }
            }

            if (mapping.isForeignReferenceMapping()) {
                ClassDescriptor referencedDescriptor = ((ForeignReferenceMapping)mapping).getReferenceDescriptor();
                if (referencedDescriptor!= null) {
                    if (isSharedIsolation() && !referencedDescriptor.getCachePolicy().isSharedIsolation()) {
                        setCacheIsolation(CacheIsolationType.PROTECTED);
                    }
                }
            }

            if (mapping.isAggregateObjectMapping()) {
                ClassDescriptor referencedDescriptor = ((AggregateObjectMapping)mapping).getReferenceDescriptor();
                if (referencedDescriptor != null) {
                    if (isSharedIsolation() && !referencedDescriptor.getCachePolicy().isSharedIsolation()) {
                        setCacheIsolation(CacheIsolationType.PROTECTED);
                    }
                }
            }
        }

        if (this.databaseChangeNotificationType == null) {
            this.databaseChangeNotificationType = DatabaseChangeNotificationType.INVALIDATE;
        }
        if (this.cacheSynchronizationType == UNDEFINED_OBJECT_CHANGE_BEHAVIOR) {
            this.cacheSynchronizationType = SEND_OBJECT_CHANGES;
        }
        if ((this.databaseChangeNotificationType != DatabaseChangeNotificationType.NONE)
                && session.isDatabaseSession() && ((DatabaseSession)session).getDatabaseEventListener() != null) {
            ((DatabaseSession)session).getDatabaseEventListener().initialize(descriptor, session);
        }
    }
    
    /**
     * PUBLIC:
     * Return the cache index for the field names.
     */
    public CacheIndex getCacheIndex(List<DatabaseField> fields) {
        return getCacheIndexes().get(fields);
    }
    
    /**
     * PUBLIC:
     * Add the cache index to the descriptor's cache settings.
     * This allows for cache hits to be obtained on non-primary key fields.
     * The primary key is always indexed in the cache.
     * Cache indexes are defined by their database column names.
     */
    public void addCacheIndex(CacheIndex index) {
        getCacheIndexes().put(index.getFields(), index);
    }
    
    /**
     * PUBLIC:
     * Add the cache index to the descriptor's cache settings.
     * This allows for cache hits to be obtained on non-primary key fields.
     * The primary key is always indexed in the cache.
     * Cache indexes are defined by their database column names.
     */
    public void addCacheIndex(String... fields) {
        addCacheIndex(new CacheIndex(fields));
    }
    
    /**
     * PUBLIC:
     * Add the cache index to the descriptor's cache settings.
     * This allows for cache hits to be obtained on non-primary key fields.
     * The primary key is always indexed in the cache.
     * Cache indexes are defined by their database column names.
     */
    public void addCacheIndex(DatabaseField fields[]) {
        addCacheIndex(new CacheIndex(fields));
    }
    
    public boolean hasCacheIndexes() {
        return (this.cacheIndexes != null) && (!this.cacheIndexes.isEmpty());
    }
    
    public Map<List<DatabaseField>, CacheIndex> getCacheIndexes() {
        if (this.cacheIndexes == null) {
            this.cacheIndexes = new HashMap<List<DatabaseField>, CacheIndex>();
        }
        return this.cacheIndexes;
    }

    public void setCacheIndexes(Map<List<DatabaseField>, CacheIndex> cacheIndexes) {
        this.cacheIndexes = cacheIndexes;
    }
    
    public CachePolicy clone() {
        try {
            return (CachePolicy)super.clone();
        } catch (CloneNotSupportedException ignore) {
            throw new InternalError(ignore.getMessage());
        }
    }

    /**
     * INTERNAL:
     * Some attributes have default values defined in Project.
     * If such the value for the attribute hasn't been set then the default value is assigned.
     */
    public void assignDefaultValues(AbstractSession session) {
        if(this.identityMapSize == -1) {
            this.identityMapSize = session.getProject().getDefaultIdentityMapSize();
        }
        if(this.identityMapClass == null) {
            this.identityMapClass = session.getProject().getDefaultIdentityMapClass();
        }
        if(this.cacheIsolation == null) {
            this.cacheIsolation = session.getProject().getDefaultCacheIsolation();
        }
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this Descriptor to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * @param classLoader 
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        try {
            if (this.cacheInterceptorClass == null && this.cacheInterceptorClassName != null){
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        this.cacheInterceptorClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(this.cacheInterceptorClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(this.cacheInterceptorClassName, exception.getException());
                   }
                } else {
                    this.cacheInterceptorClass = PrivilegedAccessHelper.getClassForName(this.cacheInterceptorClassName, true, classLoader);
                }
            }
        } catch (ClassNotFoundException exc){
            throw ValidationException.classNotFoundWhileConvertingClassNames(this.cacheInterceptorClassName, exc);
        }
    }

    /**
     * @return the fullyMergeEntity
     */
    public boolean getFullyMergeEntity() {
        return fullyMergeEntity;
    }

    /**
     * ADVANCED:
     * Set what cache key type to use to store the object in the cache.
     */
    public void setCacheKeyType(CacheKeyType cacheKeyType) {
        this.cacheKeyType = cacheKeyType;
    }

    /**
     * ADVANCED:
     * Return what cache key type to use to store the object in the cache.
     */
    public CacheKeyType getCacheKeyType() {
        return cacheKeyType;
    }

    /**
     * A CacheInterceptor is an adaptor that when overridden and assigned to a Descriptor all interaction
     * between EclipseLink and the internal cache for that class will pass through the Interceptor.
     * Advanced users could use this interceptor to audit, profile or log cache access.  This Interceptor
     * could also be used to redirect or augment the TopLink cache with an alternate cache mechanism.
     * EclipseLink's configurated IdentityMaps will be passed to the Interceptor constructor.
     * 
     * As with IdentityMaps an entire class inheritance hierarchy will share the same interceptor.
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor
     */
    public Class getCacheInterceptorClass(){
        return this.cacheInterceptorClass;
    }
    
    /**
     * A CacheInterceptor is an adaptor that when overridden and assigned to a Descriptor all interaction
     * between EclipseLink and the internal cache for that class will pass through the Interceptor.
     * Advanced users could use this interceptor to audit, profile or log cache access.  This Interceptor
     * could also be used to redirect or augment the TopLink cache with an alternate cache mechanism.
     * EclipseLink's configurated IdentityMaps will be passed to the Interceptor constructor.
     * 
     * As with IdentityMaps an entire class inheritance hierarchy will share the same interceptor.
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor
     */
    public String getCacheInterceptorClassName(){
        return this.cacheInterceptorClassName;
    }

    /**
     * PUBLIC:
     * Get a value indicating the type of cache synchronization that will be used on objects of
     * this type. Possible values are:
     * SEND_OBJECT_CHANGES
     * INVALIDATE_CHANGED_OBJECTS
     * SEND_NEW_OBJECTS+WITH_CHANGES
     * DO_NOT_SEND_CHANGES
     * @return int
     *
     */
    public int getCacheSynchronizationType() {
        return cacheSynchronizationType;
    }

    /**
     * INTERNAL:
     * Return the class of identity map to be used by this descriptor.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public Class getIdentityMapClass() {
        return identityMapClass;
    }

    /**
     * PUBLIC:
     * Return the size of the identity map.
     */
    public int getIdentityMapSize() {
        return identityMapSize;
    }

    /**
     * INTERNAL:
     * Return the class of identity map to be used by this descriptor.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public Class getRemoteIdentityMapClass() {
        if (remoteIdentityMapClass == null) {
            remoteIdentityMapClass = getIdentityMapClass();
        }

        return remoteIdentityMapClass;
    }

    /**
     * PUBLIC:
     * Return the size of the remote identity map.
     */
    public int getRemoteIdentityMapSize() {
        if (remoteIdentityMapSize == -1) {
            remoteIdentityMapSize = getIdentityMapSize();
        }
        return remoteIdentityMapSize;
    }

    /**
     * PUBLIC:
     * Return if for cache hits on primary key read object queries to be disabled.
     *
     * @see #disableCacheHits()
     */
    public boolean shouldDisableCacheHits() {
        return shouldDisableCacheHits;
    }

    /**
     * PUBLIC:
     * Return if the remote server session cache hits on primary key read object queries is aloowed or not.
     *
     * @see #disableCacheHitsOnRemote()
     */
    public boolean shouldDisableCacheHitsOnRemote() {
        return shouldDisableCacheHitsOnRemote;
    }

    /**
     * PUBLIC:
     * This method returns <CODE>true</CODE> if the <CODE>ClassDescriptor</CODE> is configured to only refresh the cache
     * if the data received from the database by a query is newer than the data in the cache (as determined by the
     * optimistic locking field). Otherwise, it returns <CODE>false</CODE>.
     *
     * @see #setShouldOnlyRefreshCacheIfNewerVersion
     */
    public boolean shouldOnlyRefreshCacheIfNewerVersion() {
        return shouldOnlyRefreshCacheIfNewerVersion;
    }

    /**
     * PUBLIC:
     * This method returns <CODE>true</CODE> if the <CODE>ClassDescriptor</CODE> is configured to always refresh
     * the cache if data is received from the database by any query. Otherwise, it returns <CODE>false</CODE>.
     *
     * @see #setShouldAlwaysRefreshCache
     */
    public boolean shouldAlwaysRefreshCache() {
        return shouldAlwaysRefreshCache;
    }

    /**
     * PUBLIC:
     * This method returns <CODE>true</CODE> if the <CODE>ClassDescriptor</CODE> is configured to always remotely
     * refresh the cache if data is received from the database by any query in a {@link org.eclipse.persistence.sessions.remote.RemoteSession}.
     * Otherwise, it returns <CODE>false</CODE>.
     *
     * @see #setShouldAlwaysRefreshCacheOnRemote
     */
    public boolean shouldAlwaysRefreshCacheOnRemote() {
        return shouldAlwaysRefreshCacheOnRemote;
    }

    /**
     * PUBLIC:
     * Set if cache hits on primary key read object queries should be disabled.
     *
     * @see #alwaysRefreshCache()
     */
    public void setShouldDisableCacheHits(boolean shouldDisableCacheHits) {
        this.shouldDisableCacheHits = shouldDisableCacheHits;
    }

    /**
     * PUBLIC:
     * Set if the remote session cache hits on primary key read object queries is allowed or not.
     *
     * @see #disableCacheHitsOnRemote()
     */
    public void setShouldDisableCacheHitsOnRemote(boolean shouldDisableCacheHitsOnRemote) {
        this.shouldDisableCacheHitsOnRemote = shouldDisableCacheHitsOnRemote;
    }

    /**
     * PUBLIC:
     * When the <CODE>shouldOnlyRefreshCacheIfNewerVersion</CODE> argument passed into this method is <CODE>true</CODE>,
     * this method configures a <CODE>ClassDescriptor</CODE> to only refresh the cache if the data received from the database
     * by a query is newer than the data in the cache (as determined by the optimistic locking field) and as long as one of the following is true:
     *
     * <UL>
     * <LI>the <CODE>ClassDescriptor</CODE> was configured by calling {@link #alwaysRefreshCache} or {@link #alwaysRefreshCacheOnRemote},</LI>
     * <LI>the query was configured by calling {@link org.eclipse.persistence.queries.ObjectLevelReadQuery#refreshIdentityMapResult}, or</LI>
     * <LI>the query was a call to {@link org.eclipse.persistence.sessions.Session#refreshObject}</LI>
     * </UL>
     * <P>
     *
     * However, if a query hits the cache, data is not refreshed regardless of how this setting is configured. For example, by default,
     * when a query for a single object based on its primary key is executed, OracleAS TopLink will first look in the cache for the object.
     * If the object is in the cache, the cached object is returned and data is not refreshed. To avoid cache hits, use
     * the {@link #disableCacheHits} method.<P>
     *
     * Also note that the {@link org.eclipse.persistence.sessions.UnitOfWork} will not refresh its registered objects.<P>
     *
     * When the <CODE>shouldOnlyRefreshCacheIfNewerVersion</CODE> argument passed into this method is <CODE>false</CODE>, this method
     * ensures that a <CODE>ClassDescriptor</CODE> is not configured to only refresh the cache if the data received from the database by a
     * query is newer than the data in the cache (as determined by the optimistic locking field).
     *
     * @see #onlyRefreshCacheIfNewerVersion
     * @see #dontOnlyRefreshCacheIfNewerVersion
     */
    public void setShouldOnlyRefreshCacheIfNewerVersion(boolean shouldOnlyRefreshCacheIfNewerVersion) {
        this.shouldOnlyRefreshCacheIfNewerVersion = shouldOnlyRefreshCacheIfNewerVersion;
    }

    /**
     * PUBLIC:
     * When the <CODE>shouldAlwaysRefreshCache</CODE> argument passed into this method is <CODE>true</CODE>,
     * this method configures a <CODE>ClassDescriptor</CODE> to always refresh the cache if data is received from
     * the database by any query.<P>
     *
     * However, if a query hits the cache, data is not refreshed regardless of how this setting is configured.
     * For example, by default, when a query for a single object based on its primary key is executed, OracleAS TopLink
     * will first look in the cache for the object. If the object is in the cache, the cached object is returned and
     * data is not refreshed. To avoid cache hits, use the {@link #disableCacheHits} method.<P>
     *
     * Also note that the {@link org.eclipse.persistence.sessions.UnitOfWork} will not refresh its registered objects.<P>
     *
     * Use this property with caution because it can lead to poor performance and may refresh on queries when it is not desired.
     * Normally, if you require fresh data, it is better to configure a query with {@link org.eclipse.persistence.queries.ObjectLevelReadQuery#refreshIdentityMapResult}.
     * To ensure that refreshes are only done when required, use this method in conjunction with {@link #onlyRefreshCacheIfNewerVersion}.<P>
     *
     * When the <CODE>shouldAlwaysRefreshCache</CODE> argument passed into this method is <CODE>false</CODE>, this method
     * ensures that a <CODE>ClassDescriptor</CODE> is not configured to always refresh the cache if data is received from the database by any query.<P>
     *
     * @see #alwaysRefreshCache
     * @see #dontAlwaysRefreshCache
     */
    public void setShouldAlwaysRefreshCache(boolean shouldAlwaysRefreshCache) {
        this.shouldAlwaysRefreshCache = shouldAlwaysRefreshCache;
    }

    /**
     * PUBLIC:
     * When the <CODE>shouldAlwaysRefreshCacheOnRemote</CODE> argument passed into this method is <CODE>true</CODE>,
     * this method configures a <CODE>ClassDescriptor</CODE> to always remotely refresh the cache if data is received from
     * the database by any query in a {@link org.eclipse.persistence.sessions.remote.RemoteSession}.
     *
     * However, if a query hits the cache, data is not refreshed regardless of how this setting is configured. For
     * example, by default, when a query for a single object based on its primary key is executed, OracleAS TopLink
     * will first look in the cache for the object. If the object is in the cache, the cached object is returned and
     * data is not refreshed. To avoid cache hits, use the {@link #disableCacheHitsOnRemote} method.<P>
     *
     * Also note that the {@link org.eclipse.persistence.sessions.UnitOfWork} will not refresh its registered objects.<P>
     *
     * Use this property with caution because it can lead to poor performance and may refresh on queries when it is
     * not desired. Normally, if you require fresh data, it is better to configure a query with {@link org.eclipse.persistence.queries.ObjectLevelReadQuery#refreshIdentityMapResult}.
     * To ensure that refreshes are only done when required, use this method in conjunction with {@link #onlyRefreshCacheIfNewerVersion}.<P>
     *
     * When the <CODE>shouldAlwaysRefreshCacheOnRemote</CODE> argument passed into this method is <CODE>false</CODE>,
     * this method ensures that a <CODE>ClassDescriptor</CODE> is not configured to always remotely refresh the cache if data
     * is received from the database by any query in a {@link org.eclipse.persistence.sessions.remote.RemoteSession}.
     *
     * @see #alwaysRefreshCacheOnRemote
     * @see #dontAlwaysRefreshCacheOnRemote
     */
    public void setShouldAlwaysRefreshCacheOnRemote(boolean shouldAlwaysRefreshCacheOnRemote) {
        this.shouldAlwaysRefreshCacheOnRemote = shouldAlwaysRefreshCacheOnRemote;
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be used by this descriptor.
     * The default is the "FullIdentityMap".
     */
    public void setRemoteIdentityMapClass(Class theIdentityMapClass) {
        remoteIdentityMapClass = theIdentityMapClass;
    }

    /**
     * PUBLIC:
     * Set the size of the identity map to be used by this descriptor.
     * The default is the 100.
     */
    public void setRemoteIdentityMapSize(int identityMapSize) {
        remoteIdentityMapSize = identityMapSize;
    }

    /**
     * PUBLIC:
     * Controls how the Entity instances will be cached.  See the CacheIsolationType for details on the options.
     * @return the isolationType
     */
    public CacheIsolationType getCacheIsolation() {
        return cacheIsolation;
    }

    /**
     * PUBLIC:
     * Controls how the Entity instances and data will be cached.  See the CacheIsolationType for details on the options.
     * To disable all second level caching simply set CacheIsolationType.ISOLATED.  Note that setting the isolation
     * will automatically set the corresponding cacheSynchronizationType.   
     * ISOLATED = DO_NOT_SEND_CHANGES, PROTECTED and SHARED = SEND_OBJECT_CHANGES
     */
    public void setCacheIsolation(CacheIsolationType isolationType) {
        this.cacheIsolation = isolationType;
        if (CacheIsolationType.ISOLATED == isolationType) {
            // bug 3587273 - set the cache synchronization type so isolated objects are not sent
            // do not call the setter method because it does not allow changing the cache synchronization
            // type of isolated objects
            cacheSynchronizationType = DO_NOT_SEND_CHANGES;
        }
    }

    /**
     * INTERNAL:
     * Return if the unit of work should by-pass the session cache.
     * Objects will be built in the unit of work, and never merged into the session cache.
     */
    public boolean shouldIsolateObjectsInUnitOfWork() {
        return this.unitOfWorkCacheIsolationLevel == ISOLATE_CACHE_ALWAYS;
    }
          
    /**
     * INTERNAL:
     * Return if the unit of work should by-pass the IsolatedSession cache.
     * Objects will be built/merged into the unit of work and into the session cache.
     * but not built/merge into the IsolatedClientSession cache.
     */
    public boolean shouldIsolateProtectedObjectsInUnitOfWork() {
        return this.unitOfWorkCacheIsolationLevel == ISOLATE_FROM_CLIENT_SESSION;
    }
          
    /**
     * INTERNAL:
     * Return if the unit of work should by-pass the session cache after an early transaction.
     */
    public boolean shouldIsolateObjectsInUnitOfWorkEarlyTransaction() {
        return this.unitOfWorkCacheIsolationLevel == ISOLATE_CACHE_AFTER_TRANSACTION;
    }
              
    /**
     * INTERNAL:
     * Return if the unit of work should use the session cache after an early transaction.
     */
    public boolean shouldUseSessionCacheInUnitOfWorkEarlyTransaction() {
        return this.unitOfWorkCacheIsolationLevel == USE_SESSION_CACHE_AFTER_TRANSACTION;
    }
    
    /**
     * ADVANCED:
     * Return the unit of work cache isolation setting.
     * This setting configures how the session cache will be used in a unit of work.
     * @see #setUnitOfWorkCacheIsolationLevel(int)
     */
    public int getUnitOfWorkCacheIsolationLevel() {
        return unitOfWorkCacheIsolationLevel;
    }
    
    /**
     * ADVANCED:
     * This setting configures how the session cache will be used in a unit of work.
     * Most of the options only apply to a unit of work in an early transaction,
     * such as a unit of work that was flushed (writeChanges), issued a modify query, or acquired a pessimistic lock.
     * <p> USE_SESSION_CACHE_AFTER_TRANSACTION - Objects built from new data accessed after a unit of work early transaction are stored in the session cache.
     * This options is the most efficient as it allows the cache to be used after an early transaction. 
     * This should only be used if it is known that this class is not modified in the transaction,
     * otherwise this could cause uncommitted data to be loaded into the session cache.
     * ISOLATE_NEW_DATA_AFTER_TRANSACTION - Default (when using caching): Objects built from new data accessed after a unit of work early transaction are only stored in the unit of work.
     * This still allows previously cached objects to be accessed in the unit of work after an early transaction,
     * but ensures uncommitted data will never be put in the session cache by storing any object built from new data only in the unit of work.
     * ISOLATE_CACHE_AFTER_TRANSACTION - After a unit of work early transaction the session cache is no longer used for this class.
     * Objects will be directly built from the database data and only stored in the unit of work, even if previously cached.
     * Note that this may lead to poor performance as the session cache is bypassed after an early transaction.
     * ISOLATE_CACHE_ALWAYS - Default (when using isolated cache): The session cache will never be used for this class.
     * Objects will be directly built from the database data and only stored in the unit of work.
     * New objects and changes will also never be merged into the session cache.
     * Note that this may lead to poor performance as the session cache is bypassed,
     * however if this class is isolated or pessimistic locked and always accessed in a transaction, this can avoid having to build two copies of the object.
     */
    public void setUnitOfWorkCacheIsolationLevel(int unitOfWorkCacheIsolationLevel) {
        this.unitOfWorkCacheIsolationLevel = unitOfWorkCacheIsolationLevel;
    }

    /**
     * @param fullyMergeEntity the fullyMergeEntity to set
     */
    public void setFullyMergeEntity(boolean fullyMergeEntity) {
        this.fullyMergeEntity = fullyMergeEntity;
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be used by this descriptor.
     * The default is the "FullIdentityMap".
     */
    public void setIdentityMapClass(Class theIdentityMapClass) {
        identityMapClass = theIdentityMapClass;
    }

    /**
     * PUBLIC:
     * Set the size of the identity map to be used by this descriptor.
     * The default is the 100.
     */
    public void setIdentityMapSize(int identityMapSize) {
        this.identityMapSize = identityMapSize;
    }

    /**
     * OBSOLETE:
     * Set the type of cache coordination that will be used on objects of this type.  Possible values
     * are:<ul>
     * <li>SEND_OBJECT_CHANGES
     * <li>INVALIDATE_CHANGED_OBJECTS
     * <li>SEND_NEW_OBJECTS_WITH_CHANGES
     * <li>DO_NOT_SEND_CHANGES</ul>
     * Note: Cache Synchronization type cannot be altered for descriptors that are set as isolated using
     * the setIsIsolated method.<p>
     * This has been replaced by setCacheCoordinationType().
     * @see #setCacheCoordinationType(CacheCoordinationType)
     * @param type int  The synchronization type for this descriptor
     */
    public void setCacheSynchronizationType(int type) {
        // bug 3587273
        if (!isIsolated()) {
            cacheSynchronizationType = type;
        }
    }

    /**
     * PUBLIC:
     * Set the type of cache coordination that will be used on objects of this type.
     * Valid values defined in CacheCoordinationType.
     */
    public void setCacheCoordinationType(CacheCoordinationType type) {
        if (type == null) {
            setCacheSynchronizationType(UNDEFINED_OBJECT_CHANGE_BEHAVIOR);
        } else if (type == CacheCoordinationType.SEND_OBJECT_CHANGES) {
            setCacheSynchronizationType(SEND_OBJECT_CHANGES);
        } else if (type == CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS) {
            setCacheSynchronizationType(INVALIDATE_CHANGED_OBJECTS);
        } else if (type == CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES) {
            setCacheSynchronizationType(SEND_NEW_OBJECTS_WITH_CHANGES);
        } else if (type == CacheCoordinationType.NONE) {
            setCacheSynchronizationType(DO_NOT_SEND_CHANGES);
        }
    }

    /**
     * PUBLIC:
     * A CacheInterceptor is an adaptor that when overridden and assigned to a Descriptor all interaction
     * between EclipseLink and the internal cache for that class will pass through the Interceptor.
     * Advanced users could use this interceptor to audit, profile or log cache access.  This Interceptor
     * could also be used to redirect or augment the TopLink cache with an alternate cache mechanism.
     * EclipseLink's configurated IdentityMaps will be passed to the Interceptor constructor.

     * As with IdentityMaps an entire class inheritance hierarchy will share the same interceptor.
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor
     */
    public void setCacheInterceptorClass(Class cacheInterceptorClass){
        this.cacheInterceptorClass = cacheInterceptorClass;
    }

    /**
     * PUBLIC:
     * A CacheInterceptor is an adaptor that when overridden and assigned to a Descriptor all interaction
     * between EclipseLink and the internal cache for that class will pass through the Interceptor.
     * Advanced users could use this interceptor to audit, profile or log cache access.  This Interceptor
     * could also be used to redirect or augment the TopLink cache with an alternate cache mechanism.
     * EclipseLink's configurated IdentityMaps will be passed to the Interceptor constructor.

     * As with IdentityMaps an entire class inheritance hierarchy will share the same interceptor.
     * @see org.eclipse.persistence.sessions.interceptors.CacheInterceptor
     */
    public void setCacheInterceptorClassName(String cacheInterceptorClassName){
        this.cacheInterceptorClassName = cacheInterceptorClassName;
    }

    /**
     * PUBLIC:
     * Returns true if the descriptor represents an isolated class
     */
    public boolean isIsolated() {
        return CacheIsolationType.ISOLATED == this.cacheIsolation;
    }

    /**
     * PUBLIC:
     * Returns true if the descriptor represents a protected class.
     */
    public boolean isProtectedIsolation() {
        return CacheIsolationType.PROTECTED ==this.cacheIsolation;
    }

    /**
     * PUBLIC:
     * Returns true if the descriptor represents a shared class.
     */
    public boolean isSharedIsolation() {
        return this.cacheIsolation == null || CacheIsolationType.SHARED == this.cacheIsolation;
    }

    /**
     * INTERNAL:
     * Index the object by index in the cache using its row.
     */
    public void indexObjectInCache(CacheKey cacheKey, AbstractRecord databaseRow, Object domainObject, ClassDescriptor descriptor, AbstractSession session, boolean refresh) {
        if (!hasCacheIndexes()) {
            return;
        }
        for (CacheIndex index : this.cacheIndexes.values()) {
            if (!refresh || index.isUpdateable()) {
                List<DatabaseField> fields = index.getFields();
                int size = fields.size();
                Object[] values = new Object[size];
                for (int count = 0; count < size; count++) {
                    values[count] = databaseRow.get(fields.get(count));
                }
                CacheId indexValues = new CacheId(values);
                session.getIdentityMapAccessorInstance().putCacheKeyByIndex(index, indexValues, cacheKey, descriptor);
            }
        }
    }

    /**
     * INTERNAL:
     * Index the object by index in the cache using its changeSet.
     */
    public void indexObjectInCache(ObjectChangeSet changeSet, Object object, ClassDescriptor descriptor, AbstractSession session) {
        if (!hasCacheIndexes()) {
            return;
        }
        for (CacheIndex index : this.cacheIndexes.values()) {
            if ((changeSet == null) || (changeSet.isNew() && index.isInsertable()) || (!changeSet.isNew() && index.isUpdateable())) {
                List<DatabaseField> fields = index.getFields();
                int size = fields.size();
                Object[] values = new Object[size];
                for (int count = 0; count < size; count++) {
                    values[count] = descriptor.getObjectBuilder().extractValueFromObjectForField(object, fields.get(count), session);
                }
                CacheId indexValues = new CacheId(values);
                CacheKey cacheKey = null;
                Object id = null;
                if (changeSet != null) {
                    cacheKey = changeSet.getActiveCacheKey();
                    if (cacheKey == null) {
                        id = changeSet.getId();
                    }
                } else {
                    id = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(object, session);
                }
                if (cacheKey == null) {
                    cacheKey = session.getIdentityMapAccessorInstance().getCacheKeyForObject(id, descriptor.getJavaClass(), descriptor, true);
                }
                session.getIdentityMapAccessorInstance().putCacheKeyByIndex(index, indexValues, cacheKey, descriptor);
            }
        }
    }

    /**
     * INTERNAL:
     * Lookup the expression in the cache if it contains any indexes.
     */
    public CacheKey checkCacheByIndex(Expression expression, AbstractRecord translationRow, ClassDescriptor descriptor, AbstractSession session) {
        if (!hasCacheIndexes()) {
            return null;
        }
        AbstractRecord record = descriptor.getObjectBuilder().extractRowFromExpression(expression, translationRow, session);
        if (record == null) {
            return null;
        }
        for (CacheIndex index : this.cacheIndexes.values()) {
            List<DatabaseField> fields = index.getFields();
            int size = fields.size();
            Object[] values = new Object[size];
            for (int count = 0; count < size; count++) {
                Object value = record.get(fields.get(count));
                if (value == null) {
                    break;
                }
                values[count] = value;
            }
            CacheId indexValues = new CacheId(values);
            CacheKey cacheKey = session.getIdentityMapAccessorInstance().getCacheKeyByIndex(index, indexValues, true, descriptor);
            if (cacheKey != null) {
                return cacheKey;
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Lookup the expression in the cache if it contains any indexes.
     */
    public boolean isIndexableExpression(Expression expression, ClassDescriptor descriptor, AbstractSession session) {
        if (!hasCacheIndexes()) {
            return false;
        }
        for (CacheIndex index : this.cacheIndexes.values()) {
            List<DatabaseField> searchFields = index.getFields();
            int size = searchFields.size();
            Set<DatabaseField> foundFields = new HashSet(size);
            boolean isValid = expression.extractFields(true, false, descriptor, searchFields, foundFields);
            if (isValid && (foundFields.size() == size)) {
                return true;
            }
        }
        return false;
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the full identity map.
     * This map caches all instances read and grows to accomodate them.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useFullIdentityMap() {
        setIdentityMapClass(ClassConstants.FullIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the hard cache weak identity map.
     * This map uses weak references to only cache object in-memory.
     * It also includes a secondary fixed sized hard cache to improve caching performance.
     * This is provided because some Java VM's implement soft references differently.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useHardCacheWeakIdentityMap() {
        setIdentityMapClass(ClassConstants.HardCacheWeakIdentityMap_Class);
    }
    
    /**
     * PUBLIC:
     * Set the class of identity map to be the soft identity map.
     * This map uses soft references to only cache all object in-memory, until memory is low.
     * Note that "low" is interpreted differently by different JVM's.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useSoftIdentityMap() {
        setIdentityMapClass(ClassConstants.SoftIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the no identity map.
     * This map does no caching.
     * Note: This map does not maintain object identity.
     * In general if caching is not desired a WeakIdentityMap should be used with an isolated descriptor.
     * The default is the "SoftCacheWeakIdentityMap".
     * @see #setIsIsolated(boolean)
     */
    public void useNoIdentityMap() {
        setIdentityMapClass(ClassConstants.NoIdentityMap_Class);
    }

    /**
     * PUBLIC:
     * Set the class of identity map to be the weak identity map.
     * The default is the "SoftCacheWeakIdentityMap".
     */
    public void useWeakIdentityMap() {
        setIdentityMapClass(ClassConstants.WeakIdentityMap_Class);
    }
}
