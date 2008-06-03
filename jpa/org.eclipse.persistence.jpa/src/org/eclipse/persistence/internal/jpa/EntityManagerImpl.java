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

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.factories.ReferenceMode;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.jpa.transaction.*;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.jpa.config.EntityManagerProperties;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
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
    
    protected TransactionWrapperImpl transaction = null;
    protected boolean isOpen = true;
    protected RepeatableWriteUnitOfWork extendedPersistenceContext;
    // This attribute references the ServerSession that this deployement is using.
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
    
    /**
     * Constructor returns an EntityManager assigned to the a particular ServerSession.
     * @param sessionName the ServerSession name that should be used.
     * This constructor can potentially throw EclipseLink exceptions regarding the existence, or
     * errors with the specified session.
     */
    public EntityManagerImpl(String sessionName, boolean propagatePersistenceContext, boolean extended) {
        this((ServerSession)SessionManager.getManager().getSession(sessionName), null, propagatePersistenceContext, extended);
        flushMode = FlushModeType.AUTO;
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
        detectTransactionWrapper();
        this.extended = true;
        this.propagatePersistenceContext = false;
        this.properties=properties;
        processProperties();
        flushMode = FlushModeType.AUTO;
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
        detectTransactionWrapper();
        this.extended = true;
        this.propagatePersistenceContext = false;
        this.properties=properties;
        processProperties();
        flushMode = FlushModeType.AUTO;
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
        try {
            verifyOpen();
            Session session = getActiveSession();
            ClassDescriptor descriptor = session.getDescriptor(entityClass);
            if (descriptor == null || descriptor.isAggregateDescriptor() || descriptor.isAggregateCollectionDescriptor()) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_bean_class", new Object[] { entityClass }));
            }
            return (T) findInternal(descriptor, session, primaryKey);
        } catch (RuntimeException e) {
            setRollbackOnly();
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
            return findInternal(descriptor, session, primaryKey);
        } catch (RuntimeException e) {
            setRollbackOnly();
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
    protected Object findInternal(ClassDescriptor descriptor, Session session, Object primaryKey) {
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
        ReadObjectQuery query = new ReadObjectQuery(descriptor.getJavaClass());
        query.setSelectionKey(primaryKeyValues);
        query.conformResultsInUnitOfWork();
        query.setIsExecutionClone(true);
        return session.executeQuery(query);
    }

    /**
     * Synchronize the persistence context with the
     * underlying database.
     */
    public void flush() {
        try {
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
     * Refresh the state of the instance from the database.
     * @param entity instance registered in the current persistence context.
     */
    public void refresh(Object entity) {
        try {
            verifyOpen();
            UnitOfWork uow = getActivePersistenceContext(checkForTransaction(!isExtended()));
            if (!contains(entity, uow)) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("cant_refresh_not_managed_object", new Object[] { entity }));
            }
            ReadObjectQuery query = new ReadObjectQuery();
            query.setSelectionObject(entity);
            query.refreshIdentityMapResult();
            query.cascadeByMapping();
            query.setLockMode(ObjectBuildingQuery.NO_LOCK);
            query.setIsExecutionClone(true);
            Object refreshedEntity = null;
            refreshedEntity = uow.executeQuery(query);
            if (refreshedEntity == null) {
                // bug5955326, ReadObjectQuery will now ensure the object is invalidated if refresh returns null.
                throw new EntityNotFoundException(ExceptionLocalization.buildMessage("entity_no_longer_exists_in_db", new Object[] { entity }));
            }
        } catch (RuntimeException exception) {
            setRollbackOnly();
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
     * Set the lock mode for an entity object contained in the persistence context.
     * @param entity
     * @param lockMode
     * @throws PersistenceException if an unsupported lock call is made
     * @throws IllegalArgumentException if the instance is not an entity or is a detached entity
     * @throws javax.persistence.TransactionRequiredException if there is no transaction
     */
    public void lock(Object entity, LockModeType lockMode) {
        try {
            verifyOpen();
            RepeatableWriteUnitOfWork context = getActivePersistenceContext(checkForTransaction(!isExtended()));
            ClassDescriptor descriptor = context.getDescriptor(entity);
            OptimisticLockingPolicy lockingPolicy = descriptor.getOptimisticLockingPolicy();
            if ((lockingPolicy == null) || !(lockingPolicy instanceof VersionLockingPolicy)) {
                throw new PersistenceException(ExceptionLocalization.buildMessage("ejb30-wrong-lock_called_without_version_locking-index", null));
            }
            context.forceUpdateToVersionField(entity, (lockMode == LockModeType.WRITE));
        } catch (RuntimeException e) {
            setRollbackOnly();
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
            this.extendedPersistenceContext = new RepeatableWriteUnitOfWork(this.serverSession.acquireClientSession(properties), this.referenceMode);
            this.extendedPersistenceContext.setResumeUnitOfWorkOnTransactionCompletion(true);
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
        if(beginEarlyTransactionProperty!=null){
            this.beginEarlyTransaction="true".equalsIgnoreCase(beginEarlyTransactionProperty);
        }
        String referenceMode = getPropertiesHandlerProperty(EntityManagerProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE);
        if (referenceMode != null){
            this.referenceMode = ReferenceMode.valueOf(referenceMode);
        }
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
}
