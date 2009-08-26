/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     		     Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 * Java(TM) Persistence API, Version 2.0 - EARLY ACCESS
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP).  The code is untested and presumed not to be a  
 * compatible implementation of JSR 317: Java(TM) Persistence API, Version 2.0.   
 * We encourage you to migrate to an implementation of the Java(TM) Persistence 
 * API, Version 2.0 Specification that has been tested and verified to be compatible 
 * as soon as such an implementation is available, and we encourage you to retain 
 * this notice in any implementation of Java(TM) Persistence API, Version 2.0 
 * Specification that you distribute.
 ******************************************************************************/
package javax.persistence;

import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.QueryBuilder;
import javax.persistence.metamodel.Metamodel;

/**
 * Interface used to interact with the persistence context.
 * 
 * @since Java Persistence 1.0
 */
public interface EntityManager {
    /**
     * Make an instance managed and persistent.
     * 
     * @param entity
     * @throws EntityExistsException
     *             if the entity already exists. (If the entity already exists,
     *             the EntityExistsException may be thrown when the persist
     *             operation is invoked, or the EntityExistsException or another
     *             PersistenceException may be thrown at flush or commit time.)
     * @throws IllegalArgumentException
     *             if the instance is not an entity
     * @throws TransactionRequiredException
     *             if invoked on a container-managed entity manager of type
     *             PersistenceContextType.TRANSACTION and there is no
     *             transaction.
     */
    public void persist(Object entity);

    /**
     * Merge the state of the given entity into the current persistence context.
     * 
     * @param entity
     * @return the managed instance that the state was merged to
     * @throws IllegalArgumentException
     *             if instance is not an entity or is a removed entity
     * @throws TransactionRequiredException
     *             if invoked on a container-managed entity manager of type
     *             PersistenceContextType.TRANSACTION and there is no
     *             transaction.
     */
    public <T> T merge(T entity);

    /**
     * Remove the entity instance.
     * 
     * @param entity
     * @throws IllegalArgumentException
     *             if the instance is not an entity or is a detached entity
     * @throws TransactionRequiredException
     *             if invoked on a container-managed entity manager of type
     *             PersistenceContextType.TRANSACTION and there is no
     *             transaction.
     */
    public void remove(Object entity);

    /**
     * Find by primary key. Search for an entity of the specified class and
     * primary key. If the entity instance is contained in the persistence
     * context it is returned from there.
     * 
     * @param entityClass
     * @param primaryKey
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is is not a valid type for that entity's
     *             primary key or is null
     */
    public <T> T find(Class<T> entityClass, Object primaryKey);

    /**
     * Find by primary key, using the specified properties. Search for an entity
     * of the specified class and primary key. If the entity instance is
     * contained in the persistence context it is returned from there. If a
     * vendor-specific property or hint is not recognized, it is silently
     * ignored.
     * 
     * @param entityClass
     * @param primaryKey
     * @param properties
     *            standard and vendor-specific properties
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is is not a valid type for that entity's
     *             primary key or is null
     */
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties);

    /**
     * Find by primary key and lock. Search for an entity of the specified class
     * and primary key and lock it with respect to the specified lock type. If
     * the entity instance is contained in the persistence context it is
     * returned from there, and the effect of this method is the same as if the
     * lock method had been called on the entity. If the entity is found within
     * the persistence context and the lock mode type is pessimistic and the
     * entity has a version attribute, the persistence provider must perform
     * optimistic version checks when obtaining the database lock. If these
     * checks fail, the OptimisticLockException will be thrown. If the lock mode
     * type is pessimistic and the entity instance is found but cannot be
     * locked: - the PessimisticLockException will be thrown if the database
     * locking failure causes transaction-level rollback. - the
     * LockTimeoutException will be thrown if the database locking failure
     * causes only statement-level rollback
     * 
     * @param entityClass
     * @param primaryKey
     * @param lockMode
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is not a valid type for that entity's primary
     *             key or is null
     * @throws TransactionRequiredException
     *             if there is no transaction and a lock mode other than NONE is
     *             set
     * @throws OptimisticLockException
     *             if the optimistic version check fails
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     * @throws PersistenceException
     *             if an unsupported lock call is made
     */
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode);

    /**
     * Find by primary key and lock, using the specified properties. Search for
     * an entity of the specified class and primary key and lock it with respect
     * to the specified lock type. If the entity instance is contained in the
     * persistence context it is returned from there. If the entity is found
     * within the persistence context and the lock mode type is pessimistic and
     * the entity has a version attribute, the persistence provider must perform
     * optimistic version checks when obtaining the database lock. If these
     * checks fail, the OptimisticLockException will be thrown. If the lock mode
     * type is pessimistic and the entity instance is found but cannot be
     * locked: - the PessimisticLockException will be thrown if the database
     * locking failure causes transaction-level rollback. - the
     * LockTimeoutException will be thrown if the database locking failure
     * causes only statement-level rollback If a vendor-specific property or
     * hint is not recognized, it is silently ignored. Portable applications
     * should not rely on the standard timeout hint. Depending on the database
     * in use and the locking mechanisms used by the provider, the hint may or
     * may not be observed.
     * 
     * @param entityClass
     * @param primaryKey
     * @param lockMode
     * @param properties
     *            standard and vendor-specific properties and hints
     * @return the found entity instance or null if the entity does not exist
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is not a valid type for that entity's primary
     *             key or is null
     * @throws TransactionRequiredException
     *             if there is no transaction and a lock mode other than NONE is
     *             set
     * @throws OptimisticLockException
     *             if the optimistic version check fails
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     * @throws PersistenceException
     *             if an unsupported lock call is made
     */
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties);

    /**
     * Get an instance, whose state may be lazily fetched. If the requested
     * instance does not exist in the database, the EntityNotFoundException is
     * thrown when the instance state is first accessed. (The persistence
     * provider runtime is permitted to throw the EntityNotFoundException when
     * getReference is called.) The application should not expect that the
     * instance state will be available upon detachment, unless it was accessed
     * by the application while the entity manager was open.
     * 
     * @param entityClass
     * @param primaryKey
     * @return the found entity instance
     * @throws IllegalArgumentException
     *             if the first argument does not denote an entity type or the
     *             second argument is not a valid type for that entity's primary
     *             key or is null
     * @throws EntityNotFoundException
     *             if the entity state cannot be accessed
     */
    public <T> T getReference(Class<T> entityClass, Object primaryKey);

    /**
     * Synchronize the persistence context to the underlying database.
     * 
     * @throws TransactionRequiredException
     *             if there is no transaction
     * @throws PersistenceException
     *             if the flush fails
     */
    public void flush();

    /**
     * Set the flush mode that applies to all objects contained in the
     * persistence context.
     * 
     * @param flushMode
     */
    public void setFlushMode(FlushModeType flushMode);

    /**
     * Get the flush mode that applies to all objects contained in the
     * persistence context.
     * 
     * @return flushMode
     */
    public FlushModeType getFlushMode();

    /**
     * Lock an entity instance that is contained in the persistence context with
     * the specified lock mode type. If a pessimistic lock mode type is
     * specified and the entity contains a version attribute, the persistence
     * provider must also perform optimistic version checks when obtaining the
     * database lock. If these checks fail, the OptimisticLockException will be
     * thrown. If the lock mode type is pessimistic and the entity instance is
     * found but cannot be locked: - the PessimisticLockException will be thrown
     * if the database locking failure causes transaction-level rollback. - the
     * LockTimeoutException will be thrown if the database locking failure
     * causes only statement-level rollback
     * 
     * @param entity
     * @param lockMode
     * @throws IllegalArgumentException
     *             if the instance is not an entity or is a detached entity
     * @throws TransactionRequiredException
     *             if there is no transaction
     * @throws EntityNotFoundException
     *             if the entity does not exist in the database when pessimistic
     *             locking is performed
     * @throws OptimisticLockException
     *             if the optimistic version check fails
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     * @throws PersistenceException
     *             if an unsupported lock call is made
     */
    public void lock(Object entity, LockModeType lockMode);

    /**
     * Lock an entity instance that is contained in the persistence context with
     * the specified lock mode type and with specified properties. If a
     * pessimistic lock mode type is specified and the entity contains a version
     * attribute, the persistence provider must also perform optimistic version
     * checks when obtaining the database lock. If these checks fail, the
     * OptimisticLockException will be thrown. If the lock mode type is
     * pessimistic and the entity instance is found but cannot be locked: - the
     * PessimisticLockException will be thrown if the database locking failure
     * causes transaction-level rollback. - the LockTimeoutException will be
     * thrown if the database locking failure causes only statement-level
     * rollback If a vendor-specific property or hint is not recognized, it is
     * silently ignored. Portable applications should not rely on the standard
     * timeout hint. Depending on the database in use and the locking mechanisms
     * used by the provider, the hint may or may not be observed.
     * 
     * @param entity
     * @param lockMode
     * @param properties
     *            standard and vendor-specific properties and hints
     * @throws IllegalArgumentException
     *             if the instance is not an entity or is a detached entity
     * @throws TransactionRequiredException
     *             if there is no transaction
     * @throws EntityNotFoundException
     *             if the entity does not exist in the database when pessimistic
     *             locking is performed
     * @throws OptimisticLockException
     *             if the optimistic version check fails
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     * @throws PersistenceException
     *             if an unsupported lock call is made
     */
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties);

    /**
     * Refresh the state of the instance from the database, overwriting changes
     * made to the entity, if any.
     * 
     * @param entity
     * @throws IllegalArgumentException
     *             if the instance is not an entity or the entity is not managed
     * @throws TransactionRequiredException
     *             if invoked on a container-managed entity manager of type
     *             PersistenceContextType.TRANSACTION and there is no
     *             transaction.
     * @throws EntityNotFoundException
     *             if the entity no longer exists in the database
     */
    public void refresh(Object entity);

    /**
     * Refresh the state of the instance from the database, using the specified
     * properties, and overwriting changes made to the entity, if any. If a
     * vendor-specific property or hint is not recognized, it is silently
     * ignored.
     * 
     * @param entity
     * @param properties
     *            standard and vendor-specific properties
     * @throws IllegalArgumentException
     *             if the instance is not an entity or the entity is not managed
     * @throws TransactionRequiredException
     *             if invoked on a container-managed entity manager of type
     *             PersistenceContextType.TRANSACTION and there is no
     *             transaction.
     * @throws EntityNotFoundException
     *             if the entity no longer exists in the database
     */
    public void refresh(Object entity, Map<String, Object> properties);

    /**
     * Refresh the state of the instance from the database, overwriting changes
     * made to the entity, if any, and lock it with respect to given lock mode
     * type. If the lock mode type is pessimistic and the entity instance is
     * found but cannot be locked: - the PessimisticLockException will be thrown
     * if the database locking failure causes transaction-level rollback. - the
     * LockTimeoutException will be thrown if the database locking failure
     * causes only statement-level rollback.
     * 
     * @param entity
     * @param lockMode
     * @throws IllegalArgumentException
     *             if the instance is not an entity or the entity is not managed
     * @throws TransactionRequiredException
     *             if there is no transaction
     * @throws EntityNotFoundException
     *             if the entity no longer exists in the database
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     * @throws PersistenceException
     *             if an unsupported lock call is made
     */
    public void refresh(Object entity, LockModeType lockMode);

    /**
     * Refresh the state of the instance from the database, overwriting changes
     * made to the entity, if any, and lock it with respect to given lock mode
     * type and with specified properties. If the lock mode type is pessimistic
     * and the entity instance is found but cannot be locked: - the
     * PessimisticLockException will be thrown if the database locking failure
     * causes transaction-level rollback. - the LockTimeoutException will be
     * thrown if the database locking failure causes only statement-level
     * rollback If a vendor-specific property or hint is not recognized, it is
     * silently ignored. Portable applications should not rely on the standard
     * timeout hint. Depending on the database in use and the locking mechanisms
     * used by the provider, the hint may or may not be observed.
     * 
     * @param entity
     * @param lockMode
     * @param properties
     *            standard and vendor-specific properties and hints
     * @throws IllegalArgumentException
     *             if the instance is not an entity or the entity is not managed
     * @throws TransactionRequiredException
     *             if there is no transaction
     * @throws EntityNotFoundException
     *             if the entity no longer exists in the database
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     * @throws PersistenceException
     *             if an unsupported lock call is made
     */
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties);

    /**
     * Clear the persistence context, causing all managed entities to become
     * detached. Changes made to entities that have not been flushed to the
     * database will not be persisted.
     */
    public void clear();

    /**
     * Remove the given entity from the persistence context, causing a managed
     * entity to become detached. Unflushed changes made to the entity if any
     * (including removal of the entity), will not be synchronized to the
     * database. Entities which previously referenced the detached entity will
     * continue to reference it.
     * 
     * @param entity
     * @throws IllegalArgumentException
     *             if the instance is not an entity
     */
    public void detach(Object entity);

    /**
     * Check if the instance is a managed entity instance belonging to the
     * current persistence context.
     * 
     * @param entity
     * @return
     * @throws IllegalArgumentException
     *             if not an entity
     */
    public boolean contains(Object entity);

    /**
     * Get the current lock mode for the entity instance.
     * 
     * @param entity
     * @return lock mode
     * @throws TransactionRequiredException
     *             if there is no transaction
     * @throws IllegalArgumentException
     *             if the instance is not a managed entity and a transaction is
     *             active
     */
    public LockModeType getLockMode(Object entity);

    /**
     * Set an entity manager property. If a vendor-specific property is not
     * recognized, it is silently ignored.
     * 
     * @param propertyName
     * @param value
     * @throws IllegalArgumentException
     *             if the second argument is not valid for the implementation
     */
    public void setProperty(String propertyName, Object value);

    /**
     * Get the properties and associated values that are in effect for the
     * entity manager. Changing the contents of the map does not change the
     * configuration in effect.
     */
    public Map<String, Object> getProperties();

    /**
     * Get the names of the properties that are supported for use with the
     * entity manager. These correspond to properties and hints that may be
     * passed to the methods of the EntityManager interface that take a
     * properties argument or used with the PersistenceContext annotation. These
     * properties include all standard entity manager hints and properties as
     * well as vendor-specific ones supported by the provider. These properties
     * may or may not currently be in effect.
     * 
     * @return property names
     */
    public Set<String> getSupportedProperties();

    /**
     * Create an instance of Query for executing a Java Persistence query
     * language statement.
     * 
     * @param qlString
     *            a Java Persistence query string
     * @return the new query instance
     * @throws IllegalArgumentException
     *             if the query string is found to be invalid
     */
    public Query createQuery(String qlString);

	/**
	 * Create an instance of TypedQuery for executing a
	 * Java Persistence query language statement.
	 * @param qlString a Java Persistence query string
	 * @param resultClass the type of the query result
	 * @return the new query instance
	 * @throws IllegalArgumentException if the query string is found
	 *         to be invalid
	 */
	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass);

	/**
	 * Create an instance of TypedQuery for executing a
	 * criteria query.
	 * @param criteriaQuery  a criteria query object
	 * @return the new query instance
	 * @throws IllegalArgumentException if the query definition is
	 *       found to be invalid
	 */
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery); 

    /**
     * Create an instance of Query for executing a named query (in the Java
     * Persistence query language or in native SQL).
     * 
     * @param name
     *            the name of a query defined in metadata
     * @return the new query instance
     * @throws IllegalArgumentException
     *             if a query has not been defined with the given name or if the
     *             query string is found to be invalid
     */
    public Query createNamedQuery(String name);

	/**
	 * Create an instance of TypedQuery for executing a
	 * named query (in the Java Persistence query language
	 * or in native SQL).
	 * @param name the name of a query defined in metadata
	 * @param resultClass the type of the query result
	 * @return the new query instance
	 * @throws IllegalArgumentException if a query has not been
	 * 		defined with the given name or if the query string is
	 *		found to be invalid
	 */
	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass);

    /**
     * Create an instance of Query for executing a native SQL statement, e.g.,
     * for update or delete.
     * 
     * @param sqlString
     *            a native SQL query string
     * @return the new query instance
     */
    public Query createNativeQuery(String sqlString);

    /**
     * Create an instance of Query for executing a native SQL query.
     * 
     * @param sqlString
     *            a native SQL query string
     * @param resultClass
     *            the class of the resulting instance(s)
     * @return the new query instance
     */
    public Query createNativeQuery(String sqlString, Class resultClass);

    /**
     * Create an instance of Query for executing a native SQL query.
     * 
     * @param sqlString
     *            a native SQL query string
     * @param resultSetMapping
     *            the name of the result set mapping
     * @return the new query instance
     */
    public Query createNativeQuery(String sqlString, String resultSetMapping);

    /**
     * Indicate to the EntityManager that a JTA transaction is active. This
     * method should be called on a JTA application managed EntityManager that
     * was created outside the scope of the active transaction to associate it
     * with the current JTA transaction.
     * 
     * @throws TransactionRequiredException
     *             if there is no transaction.
     */
    public void joinTransaction();

    /**
     * Return an object of the specified type to allow access to the
     * provider-specific API. If the provider's EntityManager implementation
     * does not support the specified class, the PersistenceException is thrown.
     * 
     * @param cls
     *            the class of the object to be returned. This is normally
     *            either the underlying EntityManager implementation class or an
     *            interface that it implements.
     * @return an instance of the specified class
     * @throws PersistenceException
     *             if the provider does not support the call.
     */
    public <T> T unwrap(Class<T> cls);

    /**
     * Return the underlying provider object for the EntityManager, if
     * available. The result of this method is implementation specific. The
     * unwrap method is to be preferred for new applications.
     */
    public Object getDelegate();

    /**
     * Close an application-managed EntityManager. After the close method has
     * been invoked, all methods on the EntityManager instance and any Query
     * objects obtained from it will throw the IllegalStateException except for
     * getProperties, getSupportedProperties, getTransaction, and isOpen (which
     * will return false). If this method is called when the EntityManager is
     * associated with an active transaction, the persistence context remains
     * managed until the transaction completes.
     * 
     * @throws IllegalStateException
     *             if the EntityManager is container-managed.
     */
    public void close();

    /**
     * Determine whether the EntityManager is open.
     * 
     * @return true until the EntityManager has been closed.
     */
    public boolean isOpen();

    /**
     * Return the resource-level transaction object. The EntityTransaction
     * instance may be used serially to begin and commit multiple transactions.
     * 
     * @return EntityTransaction instance
     * @throws IllegalStateException
     *             if invoked on a JTA EntityManager.
     */
    public EntityTransaction getTransaction();

    /**
     * Return the entity manager factory for the entity manager.
     * 
     * @return EntityManagerFactory instance
     * @throws IllegalStateException
     *             if the entity manager has been closed.
     */
    public EntityManagerFactory getEntityManagerFactory();

    /**
     * Return an instance of QueryBuilder for the creation of CriteriaQuery
     * objects.
     * 
     * @return QueryBuilder instance
     * @throws IllegalStateException
     *             if the entity manager has been closed.
     */
    public QueryBuilder getQueryBuilder();

    /**
     * Return an instance of Metamodel interface for access to the metamodel of
     * the persistence unit.
     * 
     * @return Metamodel instance
     * @throws IllegalStateException
     *             if the entity manager has been closed.
     */
    public Metamodel getMetamodel();
}