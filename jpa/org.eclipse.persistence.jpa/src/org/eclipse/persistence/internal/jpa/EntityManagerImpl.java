/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     
 *     05/28/2008-1.0M8 Andrei Ilitchev 
 *        - 224964: Provide support for Proxy Authentication through JPA. 
 *        Added setProperties method to be used in case properties couldn't be passed to createEM method.
 *        The properties now set to the uow's parent - not to the uow itself.
 *        In case there's no active transaction, close method now releases uow. 
 *        UowImpl was amended to allow value holders instantiation even after it has been released,
 *        the parent ClientSession is released, too. 
 *
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;

import java.util.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import javax.sql.DataSource;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.DefaultConnector;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.factories.ReferenceMode;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.server.ConnectionPolicy;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.jpa.transaction.*;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.PropertiesHandler;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;

/**
* <p>
* <b>Purpose</b>: Contains the implementation of the EntityManager.
* <p>
* <b>Description</b>: This class provides the implementation for the combined EclipseLink
* and JPA EntityManager class.  
* <p>
* <b>Responsibilities</b>:It is responsible for tracking transaction state and the
* objects within that transaction.
* @see javax.persistence.EntityManager
* @see org.eclipse.persistence.jpa.JpaEntityManager
* 
* @author gyorke
* @since TopLink 10.1.3 EJB 3.0 Preview
*/
public class EntityManagerImpl implements org.eclipse.persistence.jpa.JpaEntityManager {
    // To avoid any dependencies on new functionalities from JPA 2.0 we'll
    // reference the new locking strategy by string name.
    private static final String NONE = "NONE";
    private static final String PESSIMISTIC = "PESSIMISTIC";
    private static final String OPTIMISTIC_FORCE_INCREMENT = "OPTIMISTIC_FORCE_INCREMENT";
    
    protected TransactionWrapperImpl transaction = null;
    protected boolean isOpen = true;
    protected RepeatableWriteUnitOfWork extendedPersistenceContext;
    // This attribute references the ServerSession that this deployment is using.
    // This is a simple mechanism to reduce the number of SessionManager accesses.
    protected ServerSession serverSession;
    // References the factory that has created this entity manager
    // to make sure that the factory is not garbage collected
    protected EntityManagerFactoryImpl factory;

    // We have not removed these flags because we may want to use them at a later date to provide transactional EntityManagers in JAVA SE
    protected boolean extended;
    protected boolean propagatePersistenceContext;
        
    //gf3334, force early transaction flag.
    protected boolean beginEarlyTransaction = false;
    
    //gf3334, this is place holder for properties that passed from createEntityManager 
    protected Map properties;

    protected FlushModeType flushMode;
    
    //Stores the reference mode for the UOW options WEAK or HARD
    protected ReferenceMode referenceMode;
    
    //Connection policy used to create the uow's parent ClientSession.
    protected ConnectionPolicy connectionPolicy;
    
    /** Property to avoid resuming unit of work if going to be closed on commit anyway. */
    protected boolean closeOnCommit;
    
    /** Property to avoid discover new objects in unit of work if application always uses persist. */
    protected boolean persistOnCommit;
    
    /**
     * Constructor returns an EntityManager assigned to the a particular ServerSession.
     * @param sessionName the ServerSession name that should be used.
     * This constructor can potentially throw EclipseLink exceptions regarding the existence, or
     * errors with the specified session.
     */
    public EntityManagerImpl(String sessionName, boolean propagatePersistenceContext, boolean extended) {
        this((ServerSession)SessionManager.getManager().getSession(sessionName), null, propagatePersistenceContext, extended);
    }

   /**
     * Constructor called from the EntityManagerFactory to create an EntityManager
     * @param serverSession the serverSession assigned to this deployment.
     */
    public EntityManagerImpl(ServerSession serverSession, boolean propagatePersistenceContext, boolean extended){
        this(serverSession, null, propagatePersistenceContext, extended);
    }
    
    /**
     * Constructor called from the EntityManagerFactory to create an EntityManager
     * @param serverSession the serverSession assigned to this deployment.
     * Note: The properties argument is provided to allow properties to be passed into this EntityManager,
     * but there are currently no such properties implemented
     */
    public EntityManagerImpl(ServerSession serverSession, Map properties, boolean propagatePersistenceContext, boolean extended) {
        this.serverSession = serverSession;
        initialize(properties);
    }
    
    /**
     * Initialize the state after construction.
     */
    protected void initialize(Map properties) {
        detectTransactionWrapper();
        this.extended = true;
        this.propagatePersistenceContext = false;
        // bug 236249: In JPA session.setProperty() throws UnsupportedOperationException.
        if (properties != null) {
            this.properties = new HashMap(properties);
        }
        this.flushMode = FlushModeType.AUTO;
        this.persistOnCommit = true;
        processProperties();
    }
    
    /**
     * Constructor called from the EntityManagerFactory to create an EntityManager
     * @param factory the EntityMangerFactoryImpl that created this entity manager.
     * Note: The properties argument is provided to allow properties to be passed into this EntityManager,
     * but there are currently no such properties implemented
     */
    public EntityManagerImpl(EntityManagerFactoryImpl factory, Map properties, boolean propagatePersistenceContext, boolean extended) {
        this.factory = factory;
        this.serverSession = factory.getServerSession();
        initialize(properties);
    }
    
    /**
     * Clear the persistence context, causing all managed
     * entities to become detached. Changes made to entities that
     * have not been flushed to the database will not be
     * persisted.
     */
    public void clear() {
        try {
            verifyOpen();
            if (this.isExtended()) {
                if (this.extendedPersistenceContext != null) {
                    if (checkForTransaction(false) == null) {
                        // clear all change sets and cache
                        this.extendedPersistenceContext.clearForClose(true);
                        this.extendedPersistenceContext = null;
                    } else {
                        // clear all change sets created after the last flush and cache
                        this.extendedPersistenceContext.clear(true);
                    }
                }
            } else {
                transaction.clear();
            }
        } catch (RuntimeException e) {
            this.setRollbackOnly();
            throw e;
        }
    }

    /**
     * Internal method called by EntityTransactionImpl class in case of transaction rollback.
     * The caller is responsible for releasing extendedPersistenceContext and it's parent.
     */
    public void removeExtendedPersistenceContext() {
        this.extendedPersistenceContext = null;
    }

    /**
     * If in a transaction this method will check for existence and register the object if
     * it is new.  The instance of the entity provided will become managed.
     * @param entity
     * @throws IllegalArgumentException if the given Object is not an entity.
     */
    public void persist(Object entity) {
        try {
            verifyOpen();
            if (entity == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { entity }));
            }
            try {
                getActivePersistenceContext(checkForTransaction(!isExtended())).registerNewObjectForPersist(entity, new IdentityHashMap());
            } catch (RuntimeException e) {
                if (ValidationException.class.isAssignableFrom(e.getClass())) {
                    throw new EntityExistsException(e.getLocalizedMessage(), e);
                }
                throw e;
            }
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Merge the state of the given entity into the current persistence
     * context, using the unqualified class name as the entity name.
     * 
     * @param entity
     * @return the instance that the state was merged to
     */
    public <T> T merge(T entity) {
        try {
            verifyOpen();
            return (T) mergeInternal(entity);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Merge the state of the given entity into the
     * current persistence context, using the unqualified
     * class name as the entity name.
     * @param entity
     * @return the instance that the state was merged to
     * @throws IllegalArgumentException if given Object is not an entity or is a removed entity
     */
    protected Object mergeInternal(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { entity }));
        }
        Object merged = null;
        UnitOfWorkImpl context = getActivePersistenceContext(checkForTransaction(!isExtended()));
        try {
            merged = context.mergeCloneWithReferences(entity, MergeManager.CASCADE_BY_MAPPING, true);
        } catch (org.eclipse.persistence.exceptions.OptimisticLockException ole) {
            throw new javax.persistence.OptimisticLockException(ole);
        }
        return merged;
    }

    /**
     * Remove the instance.
     * @param entity
     * @throws IllegalArgumentException if Object passed in is not an entity
     */
    public void remove(Object entity) {
        try {
            verifyOpen();
            if (entity == null) { //gf732 - check for null
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { entity }));
            }
            try {
                getActivePersistenceContext(checkForTransaction(!isExtended())).performRemove(entity, new IdentityHashMap());
            } catch (RuntimeException e) {
                throw e;
            }
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Find by primary key.
     * @param entityClass - the entity class to find.
     * @param primaryKey - the entity primary key value, or primary key class, or a List of primary key values.
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException
     *   if the first argument does not denote an entity type or
     *   the second argument is not a valid type for that entity's
     *   primary key.
     */
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return find(entityClass, primaryKey, null, new HashMap());
    }
    
    /**
     * Find by primary key and lock.
     * Search for an entity of the specified class and primary key and lock it 
     * with respect to the specified lock type. If the entity instance is 
     * contained in the persistence context it is returned from there. If the 
     * entity is found within the persistence context and the lock mode type
     * is pessimistic and the entity has a version attribute, the persistence 
     * provider must perform optimistic version checks when obtaining the 
     * database lock. If these checks fail, the OptimisticLockException will be 
     * thrown.
     * If the lock mode type is pessimistic and the entity instance is found but 
     * cannot be locked:
     *  - the PessimisticLockException will be thrown if the database locking 
     *    failure causes transaction-level rollback.
     *  - the LockTimeoutException will be thrown if the database locking 
     *    failure causes only statement-level rollback
     *    
     * @param entityClass
     * @param primaryKey
     * @param lockMode
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException if the first argument does not denote an 
     *         entity type or the second argument is not a valid type for that 
     *         entity's primary key or is null
     * @throws TransactionRequiredException if there is no transaction and a 
     *         lock mode other than NONE is set
     * @throws OptimisticLockException if the optimistic version check fails
     * @throws PessimisticLockException if pessimistic locking fails and the 
     *         transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking fails and only the 
     *         statement is rolled back
     * @throws PersistenceException if an unsupported lock call is made
     */
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        HashMap queryHints = new HashMap();
        if (properties != null && properties.containsKey(QueryHints.PESSIMISTIC_LOCK_TIMEOUT)) {
            queryHints.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, properties.get(QueryHints.PESSIMISTIC_LOCK_TIMEOUT));
        }
        
        return find(entityClass, primaryKey, lockMode, queryHints);
    }
    
    /**
     * Find by primary key and lock.
     * Search for an entity of the specified class and primary key and lock it 
     * with respect to the specified lock type. If the entity instance is 
     * contained in the persistence context it is returned from there. If the 
     * entity is found within the persistence context and the lock mode type is 
     * pessimistic and the entity has a version attribute, the persistence 
     * provider must perform optimistic version checks when obtaining the 
     * database lock. If these checks fail, the OptimisticLockException will be 
     * thrown. 
     * If the lock mode type is pessimistic and the entity instance is found but 
     * cannot be locked:
     *  - the PessimisticLockException will be thrown if the database locking 
     *    failure causes transaction-level rollback.
     *  - the LockTimeoutException will be thrown if the database locking failure 
     *    causes only statement-level rollback
      * If a vendor-specific property or hint is not recognized, it is silently 
     * ignored. Portable applications should not rely on the standard timeout
     * hint. Depending on the database in use and the locking mechanisms used by 
     * the provider, the hint may or may not be observed.
     * 
     * @param entityClass
     * @param primaryKey
     * @param lockMode
     * @param properties standard and vendor-specific properties and hints
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException if the first argument does not denote an 
     *         entity type or the second argument is not a valid type for that 
     *         entity's primary key or is null
     * @throws TransactionRequiredException if there is no transaction and a lock 
     *         mode other than NONE is set
     * @throws OptimisticLockException if the optimistic version check fails
     * @throws PessimisticLockException if pessimistic locking fails and the 
     *         transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking fails and only the 
     *         statement is rolled back
     * @throws PersistenceException if an unsupported lock call is made
     */
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map properties) {
        try {
            verifyOpen();
            Session session = getActiveSession();
            ClassDescriptor descriptor = session.getDescriptor(entityClass);
            if (descriptor == null || descriptor.isAggregateDescriptor() || descriptor.isAggregateCollectionDescriptor()) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_bean_class", new Object[] { entityClass }));
            }
            return (T) findInternal(descriptor, (UnitOfWork) session, primaryKey, lockMode, properties);
        } catch (RuntimeException e) {
            // Only roll back if it is not a LockTimeoutException.
            if (! (e instanceof javax.persistence.LockTimeoutException)) { 
                setRollbackOnly();
            }
            
            throw e;
        }
    }
    
    /**
     * Find by primary key.
     * @param entityClass - the entity class to find.
     * @param primaryKey - the entity primary key value, or primary key class, or a List of primary key values.
     * @return the found entity instance or null, if the entity does not exist.
     * @throws IllegalArgumentException if the first argument does not indicate an entity or if the
     *   second argument is not a valid type for that entity's primaryKey.
     */
    public Object find(String entityName, Object primaryKey) {
        try {
            verifyOpen();
            Session session = getActiveSession();
            ClassDescriptor descriptor = session.getDescriptorForAlias(entityName);
            if (descriptor == null || descriptor.isAggregateDescriptor() || descriptor.isAggregateCollectionDescriptor()) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_entitybean_name", new Object[] { entityName }));
            }
            return findInternal(descriptor, session, primaryKey, null, null);
        } catch (RuntimeException e) {
            // Only roll back if it is not a LockTimeoutException.
            if (! (e instanceof javax.persistence.LockTimeoutException)) { 
                setRollbackOnly();
            }

            throw e;
        }
    }

    /**
     * Find by primary key.
     * @param entityClass - the entity class to find.
     * @param primaryKey - the entity primary key value, or primary key class, or a List of primary key values.
     * @return the found entity instance or null, if the entity does not exist.
     * @throws IllegalArgumentException if the first argument does
     *   not denote an entity type or the second argument is not a valid type for that
     *   entity's primary key.
     */
    protected Object findInternal(ClassDescriptor descriptor, Session session, Object primaryKey, LockModeType lockMode, Map properties) {
        if (primaryKey == null) { //gf721 - check for null PK
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_pk"));
        }
        
        List primaryKeyValues;
        if (primaryKey instanceof List) {
            primaryKeyValues = (List)primaryKey;
        } else {
            if (descriptor.getCMPPolicy().getPKClass() != null && !descriptor.getCMPPolicy().getPKClass().isAssignableFrom(primaryKey.getClass())) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("invalid_pk_class", new Object[] { descriptor.getCMPPolicy().getPKClass(), primaryKey.getClass() }));
            }
            primaryKeyValues = descriptor.getCMPPolicy().createPkVectorFromKey(primaryKey, (AbstractSession)session);
        }
        
        // Get the read object query and apply the properties to it.
        // PERF: use descriptor defined query to avoid extra query creation.
        ReadObjectQuery query = descriptor.getQueryManager().getReadObjectQuery();
        
        if (query == null) {
            query = getReadObjectQuery(descriptor.getJavaClass(), primaryKeyValues, properties);
        } else {
            query.checkPrepare((AbstractSession)session, null);
            query = (ReadObjectQuery)query.clone();
            
            // Apply the properties if there are some.
            QueryHintsHandler.apply(properties, query);
            
            query.setIsExecutionClone(true);
            query.setSelectionKey(primaryKeyValues);
        }
        
        // Apply any EclipseLink defaults if they haven't been set through
        // the properties.
        if (properties == null || ! properties.containsKey(QueryHints.CACHE_USAGE)) {
            query.conformResultsInUnitOfWork();
        }
        
        return executeQuery(query, lockMode, (UnitOfWork) session);
    }

    /**
     * Synchronize the persistence context with the
     * underlying database.
     */
    public void flush() {
        try {
            // Based on spec definition 3 possible exceptions are thrown
            //IllegalState by verifyOpen,
            //TransactionRequired by check for transaction
            //PersistenceException for all others.
             // but there is a tck test that checks for illegal state exception and the 
            // official statement is that the spec 'intended' for IllegalStateException to be raised.

            verifyOpen();
            try {
                getActivePersistenceContext(checkForTransaction(true)).writeChanges();
            } catch (RuntimeException e) {
                if (EclipseLinkException.class.isAssignableFrom(e.getClass())) {
                    throw new PersistenceException(e);
                }
                throw e; 
            }
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    protected void detectTransactionWrapper() {
        if (this.serverSession.hasExternalTransactionController()) {
            setJTATransactionWrapper();
        } else {
            setEntityTransactionWrapper();
        }
    }
    
    /**
     * Execute the locking query.
     */
    private Object executeQuery(ReadObjectQuery query, LockModeType lockMode, UnitOfWork uow) {
        try {
            ClassDescriptor descriptor;
            if (query.getSelectionObject() == null) {
                // No selection object, must be doing a find via primary key.
                // Use the reference class from the query.
                descriptor = uow.getDescriptor(query.getReferenceClass());
            } else {
                // Use the selection object to get the descriptor.
                descriptor = uow.getDescriptor(query.getSelectionObject());
            }
            
            OptimisticLockingPolicy lockingPolicy = descriptor.getOptimisticLockingPolicy();
            
            if (lockingPolicy == null || !(lockingPolicy instanceof VersionLockingPolicy)) {
                if (lockMode != null && ! lockMode.name().equals(PESSIMISTIC) && ! lockMode.name().equals(NONE)) {
                    // Any locking mode other than PESSIMISTIC and NONE needs a 
                    // version locking policy to be present, otherwise an 
                    // exception is thrown.
                    throw new PersistenceException(ExceptionLocalization.buildMessage("ejb30-wrong-lock_called_without_version_locking-index", null));
                }
            }
            
            if (lockMode == null || lockMode.name().equals(NONE)) {
                // If force refresh or no selection object has been specified 
                // (indicating a find by primary key is being executed), then 
                // set the lock and execute the query.
                if (query.getSelectionObject() == null || query.shouldRefreshIdentityMapResult()) {
                    query.setLockMode(ObjectBuildingQuery.NO_LOCK);
                    return uow.executeQuery(query);
                } else {
                    // Just return the selection object.
                    return query.getSelectionObject();
                }
            } else if (lockMode.name().contains(PESSIMISTIC)) {
                if (query.getQueryTimeout() > 0) {
                    query.setLockMode(ObjectBuildingQuery.LOCK);
                } else {
                    query.setLockMode(ObjectBuildingQuery.LOCK_NOWAIT);    
                }
            
                return uow.executeQuery(query);
            } else {
                Object obj;
                
                // If force refresh or no selection object has been specified 
                // (indicating a find by primary key is being executed), then 
                // execute the query to find the object we must lock.
                if (query.getSelectionObject() == null || query.shouldRefreshIdentityMapResult()) {
                    obj = uow.executeQuery(query);
                } else {
                    obj = query.getSelectionObject();
                }
                
                uow.forceUpdateToVersionField(obj, (lockMode == LockModeType.WRITE || lockMode.name().equals(OPTIMISTIC_FORCE_INCREMENT)));
                
                return obj;
            }
        } catch (RuntimeException e) {
            if (lockMode != null && lockMode.name().contains(PESSIMISTIC)) {
                if (query.getQueryTimeout() > 0) {
                    throw new javax.persistence.LockTimeoutException(e);    
                } else {
                    throw new javax.persistence.PessimisticLockException(e);    
                }
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Refresh the state of the instance from the database.
     * @param entity instance registered in the current persistence context.
     */
    public void refresh(Object entity) {
        refresh(entity, null);
    }

    /**
     * Refresh the state of the instance from the database, overwriting changes 
     * made to the entity, if any, and lock it with respect to given lock mode 
     * type. 
     * If the lock mode type is pessimistic and the entity instance is found but 
     * cannot be locked:
     *  - the PessimisticLockException will be thrown if the database locking 
     *    failure causes transaction-level rollback.
     *  - the LockTimeoutException will be thrown if the database locking failure 
     *    causes only statement-level rollback.
     *    
     * @param entity
     * @param lockMode
     * @throws IllegalArgumentException if the instance is not an entity or the 
     *         entity is not managed
     * @throws TransactionRequiredException if there is no transaction
     * @throws EntityNotFoundException if the entity no longer exists in the 
     *         database
     * @throws PessimisticLockException if pessimistic locking fails and the 
     *         transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking fails and only the 
     *         statement is rolled back
     * @throws PersistenceException if an unsupported lock call is made
     */
    public void refresh(Object entity, LockModeType lockMode) {
        HashMap queryHints = new HashMap();
        if (properties != null && properties.containsKey(QueryHints.PESSIMISTIC_LOCK_TIMEOUT)) {
            queryHints.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, properties.get(QueryHints.PESSIMISTIC_LOCK_TIMEOUT));
        }
        
        refresh(entity, lockMode, queryHints);
    }
    
    /**
     * Refresh the state of the instance from the database, overwriting changes 
     * made to the entity, if any, and lock it with respect to given lock mode 
     * type. 
     * If the lock mode type is pessimistic and the entity instance is found but 
     * cannot be locked:
     *  - the PessimisticLockException will be thrown if the database locking 
     *    failure causes transaction-level rollback.
     *  - the LockTimeoutException will be thrown if the database locking failure 
     *    causes only statement-level rollback
     * If a vendor-specific property or hint is not recognized, it is silently 
     * ignored. Portable applications should not rely on the standard timeout
     * hint. Depending on the database in use and the locking mechanisms used by 
     * the provider, the hint may or may not be observed.
     * 
     * @param entity
     * @param lockMode
     * @param properties standard and vendor-specific properties and hints
     * @throws IllegalArgumentException if the instance is not an entity or the 
     *         entity is not managed
     * @throws TransactionRequiredException if there is no transaction
     * @throws EntityNotFoundException if the entity no longer exists in the 
     *         database
     * @throws PessimisticLockException if pessimistic locking fails and the 
     *         transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking fails and only the 
     *         statement is rolled back
     * @throws PersistenceException if an unsupported lock call is made
     */
    public void refresh(Object entity, LockModeType lockMode, Map properties) {
        try {
            verifyOpen();
            UnitOfWork uow = getActivePersistenceContext(checkForTransaction(!isExtended()));
            if (! contains(entity, uow)) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("cant_refresh_not_managed_object", new Object[] { entity }));
            }

            // Get the read object query and apply the properties to it.        
            ReadObjectQuery query = getReadObjectQuery(entity, properties);
            
            // Apply any EclipseLink defaults if they haven't been set through
            // the properties.
            if (properties == null || ! properties.containsKey(QueryHints.REFRESH)) {
                query.refreshIdentityMapResult();
            }
            
            if (properties == null || ! properties.containsKey(QueryHints.REFRESH_CASCADE)) {
                query.cascadeByMapping();
            }
            
            Object refreshedEntity = executeQuery(query, lockMode, uow);
            if (refreshedEntity == null) {
                // bug5955326, ReadObjectQuery will now ensure the object is invalidated if refresh returns null.
                throw new EntityNotFoundException(ExceptionLocalization.buildMessage("entity_no_longer_exists_in_db", new Object[] { entity }));
            }
        } catch (RuntimeException exception) {
            // Only roll back if it is not a LockTimeoutException.
            if (! (exception instanceof javax.persistence.LockTimeoutException)) { 
                setRollbackOnly();
            }
            
            throw exception;
        }
    }
    
    /**
     * Check if the instance belongs to the current persistence
     * context.
     * @param entity
     * @return
     * @throws IllegalArgumentException if given Object is not an entity
     */
    public boolean contains(Object entity) {
        try {
            verifyOpen();
            if (entity == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { entity }));
            }
            ClassDescriptor descriptor = (ClassDescriptor)getServerSession().getDescriptors().get(entity.getClass());
            if (descriptor == null || descriptor.isAggregateDescriptor() || descriptor.isAggregateCollectionDescriptor()) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("not_an_entity", new Object[] { entity }));
            }

            if ((!hasActivePersistenceContext())) {
                return false;
            }

            return contains(entity, getActivePersistenceContext(checkForTransaction(false)));
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Check if the instance belongs to the current persistence
     * context.
     */
    protected boolean contains(Object entity, UnitOfWork uow) {
        return ((UnitOfWorkImpl)uow).isObjectRegistered(entity) && !((UnitOfWorkImpl)uow).isObjectDeleted(entity);
    }
    
    public javax.persistence.Query createDescriptorNamedQuery(String queryName, Class descriptorClass){
        return createDescriptorNamedQuery(queryName, descriptorClass, null);
    }
    
    public javax.persistence.Query createDescriptorNamedQuery(String queryName, Class descriptorClass, Vector argumentTypes){
        try {
            verifyOpen();
            ClassDescriptor descriptor = getServerSession().getDescriptor(descriptorClass);
            if (descriptor != null){
                DatabaseQuery query = descriptor.getQueryManager().getLocalQueryByArgumentTypes(queryName, argumentTypes);
                if (query != null){
                    return new EJBQueryImpl(query, this);
                }
            }
            return null;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }
    
    /**
     * Create an instance of Query for executing a named query (in EJBQL or
     * native SQL).
     * 
     * @param name the name of a query defined in metadata
     * @return the new query instance
     */
    public Query createNamedQuery(String name) {
        try {
            verifyOpen();
            return new EJBQueryImpl(name, this, true);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Create an instance of Query for executing a native SQL query.
     * 
     * @param sqlString a native SQL query string
     * @return the new query instance
     */
    public Query createNativeQuery(String sqlString) {
        try {
            verifyOpen();
            return new EJBQueryImpl(EJBQueryImpl.buildSQLDatabaseQuery(
                    sqlString, false), this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method is used to create a query using SQL.  The class, must be the expected
     * return type.
     */
    public Query createNativeQuery(String sqlString, Class resultType){
        try {
            verifyOpen();
            DatabaseQuery query = createNativeQueryInternal(sqlString, resultType);
            return new EJBQueryImpl(query, this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }
     
    /**
     * Create an instance of Query for executing
     * a native SQL query.
     * @param sqlString a native SQL query string
     * @param resultSetMapping the name of the result set mapping
     * @return the new query instance
     * @throws IllegalArgumentException if query string is not valid
     */
    public Query createNativeQuery(String sqlString, String resultSetMapping){
        try {
            verifyOpen();
            ResultSetMappingQuery query = new ResultSetMappingQuery();
            query.setSQLResultSetMappingName(resultSetMapping);
            query.setSQLString(sqlString);
            query.setIsUserDefined(true);
            return new EJBQueryImpl(query, this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method returns the current session to the requestor.  The current session
     * will be a the active UnitOfWork within a transaction and will be a 'scrap'
     * UnitOfWork outside of a transaction.  The caller is concerned about the results
     * then the getSession() or getUnitOfWork() API should be called.
     */
    public Session getActiveSession() {
        Object txn = checkForTransaction(false);
        if (txn == null && !this.isExtended()) {
            return this.serverSession.acquireNonSynchronizedUnitOfWork(this.referenceMode);
        } else {
            return getActivePersistenceContext(txn);
        }
    }
    /**
     * Return the underlying provider object for the EntityManager,
     * if available. The result of this method is implementation
     * specific.
     */
    public Object getDelegate() {
        try {
            verifyOpen();
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }
    
    /**
     * Get the flush mode that applies to all objects contained
     * in the persistence context.
     * @return flushMode
     */
    public FlushModeType getFlushMode() {
        try {
            verifyOpen();
            return flushMode;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method will return the active UnitOfWork 
     */
    public UnitOfWork getUnitOfWork() {
        return getActivePersistenceContext(checkForTransaction(false));
    }
    
    /**
     * This method will return a Session outside of a transaction and null within a transaction.
     */
    public Session getSession() {
        if (checkForTransaction(false) == null) {
            return this.serverSession.acquireNonSynchronizedUnitOfWork(this.referenceMode);
        }
        return null;
    }
    
    /**
     * Returns the resource-level transaction object.
     * The EntityTransaction instance may be used serially to
     * begin and commit multiple transactions.
     * @return EntityTransaction instance
     * @throws IllegalStateException if invoked on a JTA
     * EntityManager.
     */
    public javax.persistence.EntityTransaction getTransaction(){
        try {
            return ((TransactionWrapper)transaction).getTransaction();
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }
    
    /**
     * The method search for user defined property passed in from EntityManager, if it is not found then
     * search for it from EntityManagerFactory properties.
     * @param name
     * @return
     */
    public Object getProperty(String name) {
        Object propertyValue=null;
        if(name==null){
            return null;
        }
        if(this.properties!=null){
            propertyValue=this.properties.get(name);
        }
        if(propertyValue==null){
            propertyValue=this.factory.getServerSession().getProperty(name);
        }
        return propertyValue;
    }

    /**
     * Build a selection query for the primary key values.
     */
    protected ReadObjectQuery getReadObjectQuery(Class referenceClass, List primaryKeyValues, Map properties) {
        ReadObjectQuery query = getReadObjectQuery(properties);
        query.setReferenceClass(referenceClass);
        query.setSelectionKey(primaryKeyValues);
        return query;
    }
    
    /**
     * Build a selection query using the given properties.
     */
    protected ReadObjectQuery getReadObjectQuery(Map properties) {
        ReadObjectQuery query = new ReadObjectQuery();
        
        // Apply the properties if there are some.
        QueryHintsHandler.apply(properties, query);
        query.setIsExecutionClone(true);
        return query;
    }
    
    /**
     * Build a selection query for the given entity.
     */
    protected ReadObjectQuery getReadObjectQuery(Object entity, Map properties) {
        ReadObjectQuery query = getReadObjectQuery(properties);
        query.setSelectionObject(entity);
        return query;
    }
    
    /**
     * Get an instance, whose state may be lazily fetched.
     * If the requested instance does not exist in the database,
     * throws EntityNotFoundException when the instance state is first accessed. 
     * (The container is permitted to throw EntityNotFoundException when get is called.)
     * The application should not expect that the instance state will
     * be available upon detachment, unless it was accessed by the
     * application while the entity manager was open.
     * @param entityClass
     * @param primaryKey
     * @return the found entity instance.
     * @throws IllegalArgumentException if the first argument does
     *   not denote an entity type or the second argument is not a valid type for that
     *   entity's primary key.
     * @throws EntityNotFoundException if the entity state
     *   cannot be accessed.
     */
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        try {
            verifyOpen();
            UnitOfWork session = (UnitOfWork)getActiveSession();            
            Object reference = session.getReference(entityClass, primaryKey);
            if (reference == null) {
                Object[] args = {primaryKey};
                String message = ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_reference", args);
                throw new javax.persistence.EntityNotFoundException(message);
            }
            return (T)reference;
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw exception;
        }
    }

    /**
     * Return the underlying server session
     */
    public ServerSession getServerSession() {
        return this.serverSession;
    }
    
    /**
     * This method is used to create a query using SQL.  The class, must be the expected
     * return type.
     */
    protected DatabaseQuery createNativeQueryInternal(String sqlString, Class resultType) {
        ReadAllQuery query = new ReadAllQuery(resultType);
        query.setSQLString(sqlString);
        query.setIsUserDefined(true);
        return query;
    }
    
    
    /**
     * This method is used to create a query using a EclipseLink Expression and the return type.
     */
    public javax.persistence.Query createQuery(Expression expression, Class resultType){
        try {
            verifyOpen();
            DatabaseQuery query = createQueryInternal(expression, resultType);
            return new EJBQueryImpl(query, this);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }  
    
    /**
     * Create an instance of Query for executing an JPQL query.
     * 
     * @param jpqlString an JPQL query string
     * @return the new query instance
     */
    public Query createQuery(String jpqlString) {    
        try {
            verifyOpen();            
            EJBQueryImpl ejbqImpl;
            
            try {
                ejbqImpl = new EJBQueryImpl(jpqlString, this);    
            } catch(JPQLException exception) {            
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("wrap_ejbql_exception"), exception);            
            }
            
            return ejbqImpl;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * This method is used to create a query using a EclipseLink Expression and the return type.
     */
    protected DatabaseQuery createQueryInternal(Expression expression, Class resultType) {
        ReadAllQuery query = new ReadAllQuery(resultType);
        query.setSelectionCriteria(expression);
        return query;
    }


    /**
     * <p>Closes this EntityManager.
     * 
     * <p>After invoking this method, all methods on the instance will throw an
     * {@link IllegalStateException} except for {@link #isOpen}, which will return
     * <code>false</code>   .</p>
     *
     * <p>This should be called when a method is finished with the EntityManager in a
     * bean-managed transaction environment or when executed outside a container. Closing
     * of the EntityManager is handled by the container when using container-managed
     * transactions.</p>
     */
    public void close() {
        try {
            verifyOpen();
            isOpen = false;
            factory = null;
            serverSession = null;
            if (extendedPersistenceContext != null) {
                //bug210677, checkForTransactioin returns null in afterCompletion - in this case check for uow being synchronized.
                if (checkForTransaction(false) == null && !extendedPersistenceContext.isSynchronized()) {
                    // uow.release clears change sets but keeps the cache.
                    // uow still could be used for instantiating of ValueHolders after it's released.
                    extendedPersistenceContext.release();
                    extendedPersistenceContext.getParent().release();
                } else {
                    // when commit will be called uow will be released, all change sets will be cleared, but the cache will be kept.
                    // uow still could be used for instantiating of ValueHolders after it's released.
                    extendedPersistenceContext.setResumeUnitOfWorkOnTransactionCompletion(false);
                }
                extendedPersistenceContext = null;
            }
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }


    /**
     * Indicates if this EntityManager is an extended Persistence Context
     */
    public boolean isExtended() {
        return this.extended;
    }
    
    /**
     * Internal method. Indicates whether flushMode is AUTO.
     * @return boolean
     */
    public boolean isFlushModeAUTO() {
        return flushMode == FlushModeType.AUTO;    
    }
    
    /**
     * Indicates whether or not this entity manager is open. Returns <code>true</code> until
     * a call to {@link #close} is made.
     */
    public boolean isOpen() {
        return isOpen && factory.isOpen();
    }
    
    /**
     * Set the lock mode for an entity object contained in the persistence 
     * context.
     * 
     * @param entity
     * @param lockMode
     * @throws PersistenceException if an unsupported lock call is made
     * @throws IllegalArgumentException if the instance is not an entity or is a detached entity
     * @throws javax.persistence.TransactionRequiredException if there is no transaction
     */
    public void lock(Object entity, LockModeType lockMode) {
        HashMap queryHints = new HashMap();
        if (properties != null && properties.containsKey(QueryHints.PESSIMISTIC_LOCK_TIMEOUT)) {
            queryHints.put(QueryHints.PESSIMISTIC_LOCK_TIMEOUT, properties.get(QueryHints.PESSIMISTIC_LOCK_TIMEOUT));
        }
        
        lock(entity, lockMode, queryHints);
    }
    
    /**
     * Set the lock mode for an entity object contained in the persistence 
     * context.
     * 
     * @param entity
     * @param lockMode
     * @throws PersistenceException if an unsupported lock call is made
     * @throws IllegalArgumentException if the instance is not an entity or is 
     *         a detached entity
     * @throws javax.persistence.TransactionRequiredException if there is no 
     *         transaction
     */
    public void lock(Object entity, LockModeType lockMode, Map properties) {
        try {
            verifyOpen();
            
            // Get the read object query and apply the properties to it.        
            ReadObjectQuery query = getReadObjectQuery(entity, properties);
            
            // Apply any EclipseLink defaults if they haven't been set through
            // the properties.
            if (properties == null || ! properties.containsKey(QueryHints.REFRESH)) {
                query.refreshIdentityMapResult();
            }
            
            if (properties == null || ! properties.containsKey(QueryHints.REFRESH_CASCADE)) {
                query.cascadePrivateParts();
            }
            
            executeQuery(query, lockMode, getActivePersistenceContext(checkForTransaction(!isExtended())));
        } catch (RuntimeException e) {
            if (! (e instanceof javax.persistence.LockTimeoutException)) {
                setRollbackOnly();
            }
            
            throw e;
        }
    }
    
    public void verifyOpen() {
        if (!isOpen()) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("operation_on_closed_entity_manager"));
        }
    }
    public RepeatableWriteUnitOfWork getActivePersistenceContext(Object txn) {
        // use local uow as it will be local to this EM and not on the txn
        if (this.extendedPersistenceContext == null || !this.extendedPersistenceContext.isActive()) {
            this.extendedPersistenceContext = new RepeatableWriteUnitOfWork(this.serverSession.acquireClientSession(connectionPolicy, properties), this.referenceMode);
            this.extendedPersistenceContext.setResumeUnitOfWorkOnTransactionCompletion(!this.closeOnCommit);
            this.extendedPersistenceContext.setShouldDiscoverNewObjects(this.persistOnCommit);
            this.extendedPersistenceContext.setShouldCascadeCloneToJoinedRelationship(true);
            if (txn != null) {
                // if there is an active txn we must register with it on creation of PC
                transaction.registerUnitOfWorkWithTxn(this.extendedPersistenceContext);
            }
        }
        if (this.beginEarlyTransaction && txn != null && !this.extendedPersistenceContext.isInTransaction() ) {
            //gf3334, force persistencecontext early transaction
            this.extendedPersistenceContext.beginEarlyTransaction();
        }
        return this.extendedPersistenceContext;
    }

    /**
     * Use this method to set properties into existing EntityManager
     * that are normally passed to createEntityManager method.
     * Note that if the method called when active persistence context already exists
     * then properties used to create persistence context will be ignored
     * until the new persistence context is created (that happens either after transaction rolled back
     * or after clear method was called).
     */
    public void setProperties(Map properties) {
        if(hasActivePersistenceContext()) {
            this.extendedPersistenceContext.log(SessionLog.WARNING, SessionLog.PROPERTIES, "entity_manager_sets_properties_while_context_is_active");
        }
        this.properties = properties; 
        processProperties();
    }
    
    /**
     * This method is used in contains to check if we already have a persistence context.
     * If there is no active persistence context the method returns false
     */
    private boolean hasActivePersistenceContext() {
        if (isExtended() && (this.extendedPersistenceContext == null || !this.extendedPersistenceContext.isActive()))
            return false;
        else
            return true;
    }

    protected Object checkForTransaction(boolean validateExistence) {
        return transaction.checkForTransaction(validateExistence);
    }

    public boolean shouldFlushBeforeQuery() {
        Object foundTransaction = checkForTransaction(false);
        if ((foundTransaction != null) && transaction.shouldFlushBeforeQuery(getActivePersistenceContext(foundTransaction))) {
            return true;
        }
        return false;
    }

    /**
     * This is used to determine if the Persistence Contexts (in the form of UOWs)
     * should be propagated or not, which effectively means we will use the 
     * active unit of work.
     */
    public boolean shouldPropagatePersistenceContext() {
        return this.propagatePersistenceContext;
    }
    
    //Indicate the early transaction should be forced to start.
    public boolean shouldBeginEarlyTransaction(){
        return this.beginEarlyTransaction;
    }
    
    /**
     * Indicate to the EntityManager that a JTA transaction is
     * active. This method should be called on a JTA application
     * managed EntityManager that was created outside the scope
     * of the active transaction to associate it with the current
     * JTA transaction.
     * @throws javax.persistence.TransactionRequiredException if there is
     * no transaction.
     */
    public void joinTransaction() {
        try {
            verifyOpen();
            transaction.registerUnitOfWorkWithTxn(getActivePersistenceContext(checkForTransaction(true)));
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Internal method. Sets transaction to rollback only.
     */
    protected void setRollbackOnly() {
        this.transaction.setRollbackOnlyInternal();
    }
    
    /**
     * Internal method, set begin early transaction if property has been specified.
     */
    private void processProperties(){
        String beginEarlyTransactionProperty = getPropertiesHandlerProperty(EntityManagerProperties.JOIN_EXISTING_TRANSACTION);
        if (beginEarlyTransactionProperty != null) {
            this.beginEarlyTransaction="true".equalsIgnoreCase(beginEarlyTransactionProperty);
        }
        String referenceMode = getPropertiesHandlerProperty(EntityManagerProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE);
        if (referenceMode != null) {
            this.referenceMode = ReferenceMode.valueOf(referenceMode);
        }
        String flushMode = getPropertiesHandlerProperty(EntityManagerProperties.PERSISTENCE_CONTEXT_FLUSH_MODE);
        if (flushMode != null) {
            this.flushMode = FlushModeType.valueOf(flushMode);
        }
        String closeOnCommit = getPropertiesHandlerProperty(EntityManagerProperties.PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT);
        if (closeOnCommit != null) {
            this.closeOnCommit = "true".equalsIgnoreCase(closeOnCommit);
        }
        String persistOnCommit = getPropertiesHandlerProperty(EntityManagerProperties.PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT);
        if (persistOnCommit != null) {
            this.persistOnCommit = "true".equalsIgnoreCase(persistOnCommit);
        }
        
        connectionPolicy = processConnectionPolicyProperties();
    }
    
    /**
     * The method search for user defined property passed in from EntityManager that uses PropertiesHandler, if it is not found then
     * search for it from EntityManagerFactory properties.
     * @param name
     * @return
     */
    protected String getPropertiesHandlerProperty(String name) {
        return PropertiesHandler.getSessionPropertyValue(name, properties, getServerSession());
    }

    protected void setEntityTransactionWrapper() {   
        transaction = new EntityTransactionWrapper(this);
    }
    
    /**
     * Set the flush mode that applies to all objects contained
     * in the persistence context.
     * @param flushMode
     */
    public void setFlushMode(FlushModeType flushMode) {
        try {
            verifyOpen();
            this.flushMode = flushMode;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }
    
    protected void setJTATransactionWrapper() {
        transaction = new JTATransactionWrapper(this);
    }
    
    /**
     * Process properties that define connection policy.
     */
    protected ConnectionPolicy processConnectionPolicyProperties() {
        ConnectionPolicy policy = serverSession.getDefaultConnectionPolicy();
        
        if(properties == null || properties.isEmpty()) {
            return policy;
        }
        
        // Search only the properties map - serverSession's properties have been already processed.
        ConnectionPolicy policyFromProperties = (ConnectionPolicy)properties.get(EntityManagerProperties.CONNECTION_POLICY);
        if(policyFromProperties != null) {
            policy = policyFromProperties;
        }
        
        // Note that serverSession passed into the methods below only because it carries the SessionLog into which the debug info should be written.
        // The property is search for in the passed properties map only (not in serverSession, not in System.properties).        
        ConnectionPolicy newPolicy = null;
        String isLazyString = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(EntityManagerProperties.EXCLUSIVE_CONNECTION_IS_LAZY, properties, serverSession, false);
        if(isLazyString != null) {
            boolean isLazy = Boolean.parseBoolean(isLazyString);
            if(policy.isLazy() != isLazy) {
                if(newPolicy == null) {
                    newPolicy = (ConnectionPolicy)policy.clone();
                }
                newPolicy.setIsLazy(isLazy);
            }
        }
        ConnectionPolicy.ExclusiveMode exclusiveMode = EntityManagerSetupImpl.getConnectionPolicyExclusiveModeFromProperties(properties, serverSession, false);
        if(exclusiveMode != null) {
            if(!exclusiveMode.equals(policy.getExclusiveMode())) {
                if(newPolicy == null) {
                    newPolicy = (ConnectionPolicy)policy.clone();
                }
                newPolicy.setExclusiveMode(exclusiveMode);
            }
        }

        String user = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(EntityManagerProperties.JDBC_USER, properties, serverSession, false);
        String password = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(EntityManagerProperties.JDBC_PASSWORD, properties, serverSession, false);
        String driver = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(EntityManagerProperties.JDBC_DRIVER, properties, serverSession, false);
        String connectionString = EntityManagerFactoryProvider.getConfigPropertyAsStringLogDebug(EntityManagerProperties.JDBC_URL, properties, serverSession, false);

        //find the jta datasource
        Object jtaDataSourceObj = EntityManagerFactoryProvider.getConfigPropertyLogDebug(EntityManagerProperties.JTA_DATASOURCE, properties, serverSession, false);
        DataSource jtaDataSource = null;
        String jtaDataSourceName = null;
        if(jtaDataSourceObj != null) {
            if(jtaDataSourceObj instanceof DataSource) {
                jtaDataSource = (DataSource)jtaDataSourceObj;
            } else if(jtaDataSourceObj instanceof String) {
                jtaDataSourceName = (String)jtaDataSourceObj;
            }
        }

        //find the non jta datasource  
        Object nonjtaDataSourceObj = EntityManagerFactoryProvider.getConfigPropertyLogDebug(EntityManagerProperties.NON_JTA_DATASOURCE, properties, serverSession, false);
        DataSource nonjtaDataSource = null;
        String nonjtaDataSourceName = null;
        if(nonjtaDataSourceObj != null) {
            if(nonjtaDataSourceObj instanceof DataSource) {
                nonjtaDataSource = (DataSource)nonjtaDataSourceObj;
            } else if(nonjtaDataSourceObj instanceof String) {
                nonjtaDataSourceName = (String)nonjtaDataSourceObj;
            }
        }

        if(user!=null || password!=null || driver!=null || connectionString!= null || jtaDataSourceObj!=null || nonjtaDataSourceObj!=null) {        
            // Validation: Can't specify jdbcDriver, connectionString with a DataSource
            boolean isDefaultConnectorRequired = isPropertyToBeAdded(driver) || isPropertyToBeAdded(connectionString); 
            boolean isJNDIConnectorRequired = isPropertyToBeAdded(jtaDataSource, jtaDataSourceName) || isPropertyToBeAdded(nonjtaDataSource, nonjtaDataSourceName); 
            if(isDefaultConnectorRequired && isJNDIConnectorRequired) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("entity_manager_properties_conflict_default_connector_vs_jndi_connector", new Object[] {}));
            }
            
            DatasourceLogin login = (DatasourceLogin)policy.getLogin();
            if(login == null) {
                if(policy.getPoolName() != null) {
                    login = (DatasourceLogin)serverSession.getConnectionPool(policy.getPoolName()).getLogin();
                } else {
                    login = (DatasourceLogin)serverSession.getDatasourceLogin();
                }
            }
            
            // Validation: Can't specify jdbcDriver, connectionString if externalTransactionController is used - this requires externalConnectionPooling
            if(login.shouldUseExternalTransactionController() && isDefaultConnectorRequired) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("entity_manager_properties_conflict_default_connector_vs_external_transaction_controller", new Object[] {}));
            }
            
            javax.sql.DataSource dataSource = null;
            String dataSourceName = null;
            if(isJNDIConnectorRequired) {
                if(login.shouldUseExternalTransactionController()) {
                    if(isPropertyToBeAdded(jtaDataSource, jtaDataSourceName)) {
                        dataSource = jtaDataSource;                
                        dataSourceName = jtaDataSourceName;                
                    }
                    // validation: Can't change externalTransactionController state - will ignore data source that doesn't match the flag.
                    if(isPropertyToBeAdded(nonjtaDataSource, nonjtaDataSourceName)) {
                        serverSession.log(SessionLog.WARNING, SessionLog.PROPERTIES, "entity_manager_ignores_nonjta_data_source");
                    }
                } else {
                    if(isPropertyToBeAdded(nonjtaDataSource, nonjtaDataSourceName)) {
                        dataSource = nonjtaDataSource;                
                        dataSourceName = nonjtaDataSourceName;                
                    }
                    // validation: Can't change externalTransactionController state - will ignore data source that doesn't match the flag.
                    if(isPropertyToBeAdded(jtaDataSource, jtaDataSourceName)) {
                        serverSession.log(SessionLog.WARNING, SessionLog.PROPERTIES, "entity_manager_ignores_jta_data_source");
                    }
                }
            }
            
            // isNew...Required == null means no change required; TRUE - newValue substitute oldValue by newValue; FALSE - remove oldValue. 
            Boolean isNewUserRequired = isPropertyValueToBeUpdated(login.getUserName(), user);
            Boolean isNewPasswordRequired;
            // if user name should be removed from properties then password should be removed, too.
            if(isNewUserRequired != null && !isNewUserRequired) {
                isNewPasswordRequired = Boolean.FALSE;
            } else {
                isNewPasswordRequired = isPropertyValueToBeUpdated(login.getPassword(), password);
            }
            DefaultConnector oldDefaultConnector = null;
            if(login.getConnector() instanceof DefaultConnector) {
                oldDefaultConnector = (DefaultConnector)login.getConnector();
            }
            boolean isNewDefaultConnectorRequired = oldDefaultConnector==null && isDefaultConnectorRequired;
            JNDIConnector oldJNDIConnector = null;
            if(login.getConnector() instanceof JNDIConnector) {
                oldJNDIConnector = (JNDIConnector)login.getConnector();
            }
            boolean isNewJNDIConnectorRequired = oldJNDIConnector==null && isJNDIConnectorRequired;
            Boolean isNewDriverRequired = null;
            Boolean isNewConnectionStringRequired = null;
            if(isNewDefaultConnectorRequired) {
                isNewDriverRequired = isPropertyValueToBeUpdated(null, driver);
                isNewConnectionStringRequired = isPropertyValueToBeUpdated(null, connectionString);
            } else {
                if(oldDefaultConnector != null) {
                    isNewDriverRequired = isPropertyValueToBeUpdated(oldDefaultConnector.getDriverClassName(), driver);
                    isNewConnectionStringRequired = isPropertyValueToBeUpdated(oldDefaultConnector.getConnectionString(), connectionString);
                }
            }
            Boolean isNewDataSourceRequired = null;
            if(isNewJNDIConnectorRequired) {
                isNewDataSourceRequired = Boolean.TRUE;
            } else {
                if(oldJNDIConnector != null) {
                    if(dataSource != null) {
                        if(!dataSource.equals(oldJNDIConnector.getDataSource())) {
                            isNewDataSourceRequired = Boolean.TRUE;
                        } 
                    } else if(dataSourceName != null) {
                        if(!dataSourceName.equals(oldJNDIConnector.getName())) {
                            isNewDataSourceRequired = Boolean.TRUE;
                        }
                    }
                }
            }
            
            if(isNewUserRequired!=null || isNewPasswordRequired!=null || isNewDriverRequired!=null || isNewConnectionStringRequired!=null || isNewDataSourceRequired) {
                // a new login required - so a new policy required, too.
                if(newPolicy == null) {
                    newPolicy = (ConnectionPolicy)policy.clone();
                }
                // the new policy must have a new login - not to override the existing one in the original ConnectionPolicy that is likely shared.
                DatasourceLogin newLogin = (DatasourceLogin)newPolicy.getLogin();
                // sometimes ConnectionPolicy.clone clones the login , too - sometimes it doesn't.
                if(newPolicy.getLogin() == null || newPolicy.getLogin() == policy.getLogin()) {
                    newLogin = (DatasourceLogin)login.clone();
                    newPolicy.setLogin(newLogin);
                }
                // because it uses a new login the connection policy should not be pooled.
                newPolicy.setPoolName(null);
                
                if(isNewUserRequired!=null) {
                    if(isNewUserRequired) {
                        newLogin.setProperty("user", user);
                    } else {
                        newLogin.getProperties().remove("user");
                    }
                }
                if(isNewPasswordRequired!=null) {
                    if(isNewPasswordRequired) {
                        newLogin.setProperty("password", password);
                    } else {
                        newLogin.getProperties().remove("password");
                    }
                }
                if(isNewDefaultConnectorRequired) {
                    newLogin.setConnector(new DefaultConnector());
                    newLogin.setUsesExternalConnectionPooling(false);
                } else if(isNewJNDIConnectorRequired) {
                    newLogin.setConnector(new JNDIConnector());
                    newLogin.setUsesExternalConnectionPooling(true);
                }
                if(isDefaultConnectorRequired) {
                    DefaultConnector defaultConnector = (DefaultConnector)newLogin.getConnector();
                    if(isNewDriverRequired!=null) {
                        if(isNewDriverRequired) {
                            defaultConnector.setDriverClassName(driver);
                        } else {
                            defaultConnector.setDriverClassName(null);
                        }
                    }
                    if(isNewConnectionStringRequired!=null) {
                        if(isNewConnectionStringRequired) {
                            defaultConnector.setDatabaseURL(connectionString);
                        } else {
                            defaultConnector.setDatabaseURL(null);
                        }
                    }
                } else if(isNewDataSourceRequired != null) {
                    JNDIConnector jndiConnector = (JNDIConnector)newLogin.getConnector();
                    if(isNewDataSourceRequired) {
                        if(dataSource != null) {
                            jndiConnector.setDataSource(dataSource);
                        } else {
                            // dataSourceName != null
                            jndiConnector.setName(dataSourceName);
                        }
                    }
                }
            }
        }
        
        if(newPolicy != null) {
            return newPolicy;
        } else {
            return policy;
        }
    }
    
    /**
     * Property value is to be added if it's non null and not an empty string.
     */
    protected static boolean isPropertyToBeAdded(String value) {
        return value != null && value.length() > 0;
    }
    protected static boolean isPropertyToBeAdded(DataSource ds, String dsName) {
        return ds!=null || (dsName != null && dsName.length()>0);
    }
    /**
     * Property value of an empty string indicates that the existing property should be removed.
     */
    protected static boolean isPropertyToBeRemoved(String value) {
        return value != null && value.length() == 0;
    }
    /**
     * @return null: no change; TRUE: substitute oldValue by newValue; FALSE: remove oldValue 
     */
    protected Boolean isPropertyValueToBeUpdated(String oldValue, String newValue) {
        if(newValue == null) {
            // no new value - no change
            return null;
        } else {
            // new value is a non empty string
            if(newValue.length() > 0) {
                if(oldValue != null) {
                    if(newValue.equals(oldValue)) {
                        // new and old values are equal - no change.
                        return null;
                    } else {
                        // new and old values are different - change old value for new value.
                        return Boolean.TRUE;
                    }
                } else {
                    // no old value - change for new value.
                    return Boolean.TRUE;
                }
            } else {
                // new value is an empty string - if old value exists it should be substituted with new value..
                if(oldValue != null) {
                    return Boolean.FALSE;
                } else {
                    return null;
                }
            }
        }
    }
}
