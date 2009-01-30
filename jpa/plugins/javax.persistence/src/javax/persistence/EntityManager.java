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
 * The API for this class and its comments are derived from the JPA 2.0 specification
 * which is developed under the Java Community Process (JSR 317) and is copyright
 * Sun Microsystems, Inc.
 *
 * Contributors:
 *     dclarke - Java Persistence API 2.0 Public Draft
 *     			 Specification and licensing terms available from
 *     		   	 http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the
 * Java Community Process (JCP) and is made available for testing and evaluation
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/

package javax.persistence;

import java.util.Map;
import java.util.Set;

/**
 * Interface used to interact with the persistence context.
 *
 * <p>
 * An <code>EntityManager</code> instance is associated with a persistence
 * context. A persistence context is a set of entity instances in which for any
 * persistent entity identity there is a unique entity instance. Within the
 * persistence context, the entity instances and their life-cycle are managed.
 * This interface defines the methods that are used to interact with the
 * persistence context. The <code>EntityManager</code> API is used to create and
 * remove persistent entity instances, to find entities by their primary key,
 * and to query over entities.
 *
 * <p>
 * The set of entities that can be managed by a given <code>EntityManager</code>
 * instance is defined by a persistence unit. A persistence unit defines the set
 * of all classes that are related or grouped by the application, and which must
 * be co-located in their mapping to a single database.
 *
 * @since Java Persistence API 1.0
 */
public interface EntityManager {

	/**
	 * Make an entity instance managed and persistent.
	 *
	 * @param entity
	 * @throws EntityExistsException
	 *             if the entity already exists. (The EntityExistsException may
	 *             be thrown when the persist operation is invoked, or the
	 *             EntityExistsException or another PersistenceException may be
	 *             thrown at flush or commit time.)
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 * @throws IllegalArgumentException
	 *             if not an entity
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
	 * @return the instance that the state was merged to
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
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
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 * @throws IllegalArgumentException
	 *             if not an entity or if a detached entity
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
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 * @throws IllegalArgumentException
	 *             if the first argument does not denote an entity type or the
	 *             second argument is not a valid type for that entity's primary
	 *             key
	 */
	public <T> T find(Class<T> entityClass, Object primaryKey);

	/**
	 * Find by primary key and lock. Search for an entity of the specified class
	 * and primary key and lock it with respect to the specified lock type. If
	 * the entity instance is contained in the persistence context it is
	 * returned from there. If the entity is found within the persistence
	 * context and the lock mode type is pessimistic and the entity has a
	 * version attribute, the persistence provider must perform optimistic
	 * version checks when obtaining the database lock. If these checks fail,
	 * the OptimisticLockException will be thrown. If the lock mode type is
	 * pessimistic and the entity instance is found but cannot be locked: - the
	 * PessimisticLockException will be thrown if the database locking failure
	 * causes transaction-level rollback. - the LockTimeoutException will be
	 * thrown if the database locking failure causes only statement-level
	 * rollback
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
	 * @since Java Persistence API 2.0
	 */
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode);

	/**
	 * Find by primary key and lock. Search for an entity of the specified class
	 * and primary key and lock it with respect to the specified lock type. If
	 * the entity instance is contained in the persistence context it is
	 * returned from there. If the entity is found within the persistence
	 * context and the lock mode type is pessimistic and the entity has a
	 * version attribute, the persistence provider must perform optimistic
	 * version checks when obtaining the database lock. If these checks fail,
	 * the OptimisticLockException will be thrown. If the lock mode type is
	 * pessimistic and the entity instance is found but cannot be locked: - the
	 * PessimisticLockException will be thrown if the database locking failure
	 * causes transaction-level rollback. - the LockTimeoutException will be
	 * thrown if the database locking failure causes only statement-level
	 * rollback If a vendor-specific property or hint is not recognized, it is
	 * silently ignored. Portable applications should not rely on the standard
	 * timeout hint. Depending on the database in use and the locking mechanisms
	 * used by the provider, the hint may or may not be observed.
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
	 * @since Java Persistence API 2.0
	 */
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map properties);

	/**
	 * Get an instance, whose state may be lazily fetched. If the requested
	 * instance does not exist in the database, throws
	 * {@link EntityNotFoundException} when the instance state is first
	 * accessed. (The persistence provider runtime is permitted to throw
	 * {@link EntityNotFoundException} when {@link #getReference} is called.)
	 *
	 * The application should not expect that the instance state will be
	 * available upon detachment, unless it was accessed by the application
	 * while the entity manager was open.
	 *
	 * @param entityClass
	 * @param primaryKey
	 * @return the found entity instance
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 * @throws IllegalArgumentException
	 *             if the first argument does not denote an entity type or the
	 *             second argument is not a valid type for that entity's primary
	 *             key
	 * @throws EntityNotFoundException
	 *             if the entity state cannot be accessed
	 */
	public <T> T getReference(Class<T> entityClass, Object primaryKey);

	/**
	 * Synchronize the persistence context to the underlying database.
	 *
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
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
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 */
	public void setFlushMode(FlushModeType flushMode);

	/**
	 * Get the flush mode that applies to all objects contained in the
	 * persistence context.
	 *
	 * @return flush mode
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 */
	public FlushModeType getFlushMode();

	/**
	 * Set the lock mode for an entity object contained in the persistence
	 * context.
	 *
	 * @param entity
	 * @param lockMode
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 * @throws PersistenceException
	 *             if an unsupported lock call is made
	 * @throws IllegalArgumentException
	 *             if the instance is not an entity or is a detached entity
	 * @throws TransactionRequiredException
	 *             if there is no transaction
	 */
	public void lock(Object entity, LockModeType lockMode);

	/**
	 * Set the lock mode for an entity object contained in the persistence
	 * context.
	 *
	 * @param entity
	 * @param lockMode
	 * @throws PersistenceException
	 *             if an unsupported lock call is made
	 * @throws IllegalArgumentException
	 *             if the instance is not an entity or is a detached entity
	 * @throws javax.persistence.TransactionRequiredException
	 *             if there is no transaction
	 * @since Java Persistence API 2.0
	 */
	public void lock(Object entity, LockModeType lockMode, Map properties);

	/**
	 * Refresh the state of the instance from the database, overwriting changes
	 * made to the entity, if any.
	 *
	 * @param entity
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 * @throws IllegalArgumentException
	 *             if not an entity or entity is not managed
	 * @throws TransactionRequiredException
	 *             if invoked on a container-managed entity manager of type
	 *             PersistenceContextType.TRANSACTION and there is no
	 *             transaction.
	 * @throws EntityNotFoundException
	 *             if the entity no longer exists in the database.
	 */
	public void refresh(Object entity);

	/**
	 * Refresh the state of the instance from the database, overwriting changes
	 * made to the entity, if any, and lock it with respect to given lock mode
	 * type. If the lock mode type is pessimistic and the entity instance is
	 * found but cannot be locked:
	 * <ul>
	 * <li> the PessimisticLockException will be thrown if the database locking
	 * failure causes transaction-level rollback. <li> the LockTimeoutException
	 * will be thrown if the database locking failure causes only
	 * statement-level rollback.
	 * </ul>
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
	 * @since Java Persistence API 2.0
	 */
	public void refresh(Object entity, LockModeType lockMode);

	/**
	 * Refresh the state of the instance from the database, overwriting changes
	 * made to the entity, if any, and lock it with respect to given lock mode
	 * type. If the lock mode type is pessimistic and the entity instance is
	 * found but cannot be locked:
	 * <ul>
	 * <li> the PessimisticLockException will be thrown if the database locking
	 * failure causes transaction-level rollback. <li> the LockTimeoutException
	 * will be thrown if the database locking failure causes only
	 * statement-level rollback If a vendor-specific property or hint is not
	 * recognized, it is silently ignored.
	 * </ul>
	 * <p>
	 * Portable applications should not rely on the standard timeout hint.
	 * Depending on the database in use and the locking mechanisms used by the
	 * provider, the hint may or may not be observed.
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
	 * @since Java Persistence API 2.0
	 */
	public void refresh(Object entity, LockModeType lockMode, Map properties);

	/**
	 * Clear the persistence context, causing all managed entities to become
	 * detached. Changes made to entities that have not been flushed to the
	 * database will not be persisted.
	 *
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 */
	public void clear();

	/**
	 * Remove the given entity from the persistence context, causing a managed
	 * entity to become detached. Changes made to the entity that have not been
	 * flushed, if any (including removal of the entity), will not be
	 * synchronized to the database.
	 *
	 * @param entity
	 * @throws IllegalArgumentException
	 *             if the instance is not an entity
	 * @since Java Persistence API 2.0
	 */
	public void clear(Object entity);

	/**
	 * Check if the instance belongs to the current persistence context.
	 *
	 * @param entity
	 * @return <code>true</code> if the instance belongs to the current
	 *         persistence context.
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
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
	 * @since Java Persistence API 2.0
	 */
	public LockModeType getLockMode(Object entity);

	/**
	 * Get the properties and associated values that are in effect for the
	 * entity manager. Changing the contents of the map does not change the
	 * configuration in effect.
	 *
	 * @since Java Persistence API 2.0
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
	 * @since Java Persistence API 2.0
	 */
	public Set<String> getSupportedProperties();

	/**
	 * Create an instance of Query for executing a Java Persistence query
	 * language statement.
	 *
	 * @param qlString
	 *            a Java Persistence query language query string
	 * @return the new query instance
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 * @throws IllegalArgumentException
	 *             if query string is not valid
	 */
	public Query createQuery(String qlString);

	/**
	 * Create an instance of Query for executing a criteria query.
	 *
	 * @param qdef
	 *            a Criteria API query definition object
	 * @return the new query instance
	 * @throws IllegalArgumentException
	 *             if the query definition is found to be invalid
	 * @since Java Persistence API 2.0
	 */
	public Query createQuery(QueryDefinition qdef);

	/**
	 * Create an instance of Query for executing a named query (in the Java
	 * Persistence query language or in native SQL).
	 *
	 * @param name
	 *            the name of a query defined in metadata
	 * @return the new query instance
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 * @throws IllegalArgumentException
	 *             if a query has not been defined with the given name
	 */
	public Query createNamedQuery(String name);

	/**
	 * Create an instance of Query for executing a native SQL statement, e.g.,
	 * for update or delete.
	 *
	 * @param sqlString
	 *            a native SQL query string
	 * @return the new query instance
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
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
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
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
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 */
	public Query createNativeQuery(String sqlString, String resultSetMapping);

	/**
	 * Indicate to the EntityManager that a JTA transaction is active. This
	 * method should be called on a JTA application managed EntityManager that
	 * was created outside the scope of the active transaction to associate it
	 * with the current JTA transaction.
	 *
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
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
	 * @since Java Persistence API 2.0
	 */
	public <T> T unwrap(Class<T> cls);

	/**
	 * Return the underlying provider object for the EntityManager, if
	 * available. The result of this method is implementation specific.
	 *
	 * @throws IllegalStateException
	 *             if this EntityManager has been closed.
	 */
	public Object getDelegate();

	/**
	 * Close an application-managed EntityManager. After the close method has
	 * been invoked, all methods on the EntityManager instance and any Query
	 * objects obtained from it will throw the IllegalStateException except for
	 * getTransaction and isOpen (which will return false). If this method is
	 * called when the EntityManager is associated with an active transaction,
	 * the persistence context remains managed until the transaction completes.
	 *
	 * @throws IllegalStateException
	 *             if the EntityManager is container-managed or has been already
	 *             closed..
	 */
	public void close();

	/**
	 * Determine whether the EntityManager is open.
	 *
	 * @return true until the EntityManager has been closed.
	 */
	public boolean isOpen();

	/**
	 * Returns the resource-level transaction object. The EntityTransaction
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
	 * @since Java Persistence API 2.0
	 */
	public EntityManagerFactory getEntityManagerFactory();

	/**
	 * Return an instance of QueryBuilder for the creation of Criteria API
	 * QueryDefinition objects.
	 *
	 * @return QueryBuilder instance
	 * @throws IllegalStateException
	 *             if the entity manager has been closed.
	 * @since Java Persistence API 2.0
	 */
	public QueryBuilder getQueryBuilder();
}
