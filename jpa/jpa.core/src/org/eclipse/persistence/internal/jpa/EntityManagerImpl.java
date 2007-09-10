/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;

import java.util.Map;
import java.util.Vector;

import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.internal.jpa.transaction.*;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
* <p>
* <b>Purpose</b>: Contains the implementation of the EntityManager.
* <p>
* <b>Description</b>: This class provides the implementation for the combined TopLink
* and EJB3.0 EntityManager class.  
* <p>
* <b>Responsibilities</b>:It is responcible for tracking transaction state and the
* objects within that transaction.
* @see javax.persistence.EntityManager
* @see org.eclipse.persistence.jpa.EntityManager
*/

/*  @author  gyorke  
 *  @since   TopLink 10.1.3 EJB 3.0 Preview
 */


public class EntityManagerImpl 
    extends org.eclipse.persistence.internal.jpa.base.EntityManagerImpl
    implements org.eclipse.persistence.jpa.EntityManager 
{
   
    private FlushModeType flushMode;
    
    /**
     * Constructor returns an EntityManager assigned to the a particular ServerSession.
     * @param sessionName the ServerSession name that should be used.
     * This constructor can potentially throw TopLink exceptions regarding the existence, or
     * errors with the specified session.
     */
    public EntityManagerImpl(String sessionName, boolean propagatePersistenceContext, boolean extended){
        super(sessionName, propagatePersistenceContext, extended);
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
    public EntityManagerImpl(ServerSession serverSession, Map properties, boolean propagePersistenceContext, boolean extended){
        super(serverSession, properties, propagePersistenceContext, extended);
        flushMode = FlushModeType.AUTO;
    }
	
    /**
     * Constructor called from the EntityManagerFactory to create an EntityManager
     * @param factory the EntityMangerFactoryImpl that created this entity manager.
     * Note: The properties argument is provided to allow properties to be passed into this EntityManager,
     * but there are currently no such properties implemented
     */
    public EntityManagerImpl(EntityManagerFactoryImpl factory, Map properties, boolean propagePersistenceContext, boolean extended){
        super(factory, properties, propagePersistenceContext, extended);
        flushMode = FlushModeType.AUTO;
    }
	
	/**
	 * Merge the state of the given entity into the
	 * current persistence context, using the unqualified
	 * class name as the entity name.
	 * @param entity
	 * @return the instance that the state was merged to
	 */
	public <T> T merge(T entity){
        try{
            verifyOpen();
            return (T) mergeInternal(entity);
        }catch (RuntimeException e){
            this.transaction.setRollbackOnlyInternal();
            throw e;
        }
	}
	
	/**
	* Find by primary key.
	* @param entityClass
	* @param primaryKey
	* @return the found entity instance
	* or null if the entity does not exist
	* @throws IllegalArgumentException if the first argument does
	* not denote an entity type or the second argument is not a valid type for that
	* entity's primary key
	*/
	public <T> T find(Class<T> entityClass, Object primaryKey){
        try {
            verifyOpen();
            return (T) findInternal(entityClass, primaryKey);
        } catch (RuntimeException e) {
            this.transaction.setRollbackOnlyInternal();
            throw e;
        }
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
     * @return the found entity instance
     * @throws IllegalArgumentException if the first argument does
     * not denote an entity type or the second
     * argument is not a valid type for that
     * entity's primary key
     * @throws EntityNotFoundException if the entity state
     * cannot be accessed
     */
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        try {
            verifyOpen();
            Object returnValue = findInternal(entityClass, primaryKey);
            if (returnValue ==null){
                Object[] o = {primaryKey};
                String message = ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_reference", o);
                throw new javax.persistence.EntityNotFoundException(message);
            }
            return (T)returnValue;
        } catch (RuntimeException e) {
            this.transaction.setRollbackOnlyInternal();
            throw e;
        }
    }
    
	/**
	* Create an instance of Query for executing an
	* EJBQL query.
	* @param ejbqlString an EJBQL query string
	* @return the new query instance
	*/
	public Query createQuery(String ejbqlString){
    
        try {
            verifyOpen();
            
            EJBQueryImpl ejbqImpl;
            
            try
            {
                ejbqImpl = new EJBQueryImpl(ejbqlString, this);    
            }
            
            catch(JPQLException ex)
            {            
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("wrap_ejbql_exception"), ex);            
            }
            
            return ejbqImpl;
        } catch (RuntimeException e) {
            this.transaction.setRollbackOnlyInternal();
            throw e;
        }
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
            this.transaction.setRollbackOnlyInternal();
            throw e;
        }
    }
    
	/**
	* Create an instance of Query for executing a
	* named query (in EJBQL or native SQL).
	* @param name the name of a query defined in metadata
	* @return the new query instance
	*/
	public Query createNamedQuery(String name){
        try {
            verifyOpen();
            return new EJBQueryImpl(name, this, true);
        } catch (RuntimeException e) {
            this.transaction.setRollbackOnlyInternal();
            throw e;
        }
	}
	/**
	* Create an instance of Query for executing
	* a native SQL query.
	* @param sqlString a native SQL query string
	* @return the new query instance
	*/
	public Query createNativeQuery(String sqlString){
        try {
            verifyOpen();
            return new EJBQueryImpl( EJBQueryImpl.buildSQLDatabaseQuery( sqlString, false), this );
        } catch (RuntimeException e) {
            this.transaction.setRollbackOnlyInternal();
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
            this.transaction.setRollbackOnlyInternal();
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
            this.transaction.setRollbackOnlyInternal();
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
            this.transaction.setRollbackOnlyInternal();
            throw e;
        }
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
            this.transaction.setRollbackOnlyInternal();
            throw e;
        }
    }
    
    /**
     * This method is used to create a query using a Toplink Expression and the return type.
     */
    public javax.persistence.Query createQuery(Expression expression, Class resultType){
        try {
            verifyOpen();
            DatabaseQuery query = createQueryInternal(expression, resultType);
            return new EJBQueryImpl(query, this);
        } catch (RuntimeException e) {
            this.transaction.setRollbackOnlyInternal();
            throw e;
        }
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
            this.transaction.setRollbackOnlyInternal();
            throw e;
        }
    }

    /**
    * Internal method. Indicates whether flushMode is AUTO.
    * @return boolean
    */
    public boolean isFlushModeAUTO() {
        return flushMode == FlushModeType.AUTO;    
    }
    
    protected void setJTATransactionWrapper() {
        transaction = new JTATransactionWrapper(this);
    }
    
    protected void setEntityTransactionWrapper() {   
        transaction = new EntityTransactionWrapper(this);
    }    
}
