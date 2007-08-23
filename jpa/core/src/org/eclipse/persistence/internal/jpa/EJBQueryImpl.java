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

import java.util.*;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.jpa.*;
import org.eclipse.persistence.internal.helper.*;

/**
* Concrete EJB 3.0 query class
*
* To do:
* Internationalize exceptions
* Change TopLink exceptions to exception types used by the spec
* Named Parameters
* Report Query
* firstResultIndex set in query framework
* temporal type parameters
* Change single result to use ReadObjectQuery
**/
public class EJBQueryImpl extends org.eclipse.persistence.internal.jpa.base.EJBQueryImpl implements EJBQuery {

    /**
     * Base constructor for EJBQueryImpl.  Initializes basic variables.
     * An EJBQLQueryImpl may only ever be tied to one entityManager.
     */
    protected EJBQueryImpl(EntityManagerImpl entityManager) {
        super(entityManager);
    }

    /**
     * Create an EJBQueryImpl with a TopLink query.
     * @param query
     * @param entityManager
     */
    public EJBQueryImpl(DatabaseQuery query, EntityManagerImpl entityManager) {
        super(query, entityManager);
    }

    /**
     * Build an EJBQueryImpl based on the given ejbql string
     * @param ejbql
     * @param entityManager
     */
    public EJBQueryImpl(String ejbql, EntityManagerImpl entityManager) {
        super(ejbql, entityManager);
    }

    /**
     * Create an EJBQueryImpl with either a query name or an ejbql string
     * @param queryDescription
     * @param entityManager
     * @param isNamedQuery determines whether to treat the query description as ejbql or a query name
     */
    public EJBQueryImpl(String queryDescription, EntityManagerImpl entityManager, boolean isNamedQuery) {
        super(queryDescription, entityManager, isNamedQuery);
    }

    /**
     *  Convert the given object to the class represented by the given temporal type.
     *  @return an object represting the given TemporalType
     */
    protected Object convertTemporalType(Object value, TemporalType type) {
        ConversionManager conversionManager = ((org.eclipse.persistence.internal.sessions.AbstractSession)getEntityManager().getActiveSession()).getDatasourcePlatform().getConversionManager();
        if (type == TemporalType.TIME) {
            return conversionManager.convertObject(value, ClassConstants.TIME);
        } else if (type == TemporalType.TIMESTAMP) {
            return conversionManager.convertObject(value, ClassConstants.TIMESTAMP);
        } else if (type == TemporalType.DATE) {
            return conversionManager.convertObject(value, ClassConstants.SQLDATE);
        }
        return value;
    }

    /**
     * Return the entityManager this query is tied to.
     */
    public EntityManager getEntityManager() {
        return (EntityManager)entityManager;
    }

    /**
    * Set the position of the first result to retrieve.
    * @param start position of the first result, numbered from 0
    * @return the same query instance
    */
    public Query setFirstResult(int startPosition) {
        try {
            entityManager.verifyOpen();
            setFirstResultInternal(startPosition);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
    * Set the flush mode type to be used for the query execution.
    * @param flushMode
    */
    public Query setFlushMode(FlushModeType flushMode) {
        try {
            entityManager.verifyOpen();
            if (flushMode == null) {
                getDatabaseQuery().setFlushOnExecute(null);
            } else {
                cloneIfParseCachedQuery();
                getDatabaseQuery().setFlushOnExecute(flushMode == FlushModeType.AUTO);
            }
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
    * Set an implementation-specific hint.
    * If the hint name is not recognized, it is silently ignored.
    * @param hintName
    * @param value
    * @return the same query instance
    * @throws IllegalArgumentException if the second argument is not
    * valid for the implementation
    */
    public Query setHint(String hintName, Object value) {
        try {
            entityManager.verifyOpen();
            setHintInternal(hintName, value);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
    * Set the maximum number of results to retrieve.
    * @param maxResult
    * @return the same query instance
    */
    public Query setMaxResults(int maxResult) {
        try {
            entityManager.verifyOpen();
            setMaxResultsInternal(maxResult);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
    * Bind an argument to a named parameter.
    * @param name the parameter name
    * @param value
    * @return the same query instance
    */
    public Query setParameter(String name, Object value) {
        try {
            entityManager.verifyOpen();
            setParameterInternal(name, value);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
    * Bind an instance of java.util.Date to a named parameter.
    * @param name
    * @param value
    * @param temporalType
    * @return the same query instance
    */
    public Query setParameter(String name, Date value, TemporalType temporalType) {
        return setParameter(name, convertTemporalType(value, temporalType));
    }

    /**
    * Bind an instance of java.util.Calendar to a named parameter.
    * @param name
    * @param value
    * @param temporalType
    * @return the same query instance
    */
    public Query setParameter(String name, Calendar value, TemporalType temporalType) {
        return setParameter(name, convertTemporalType(value, temporalType));
    }

    /**
    * Bind an argument to a positional parameter.
    * @param position
    * @param value
    * @return the same query instance
    */
    public Query setParameter(int position, Object value) {
        try {
            entityManager.verifyOpen();
            setParameterInternal(position, value);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
    * Bind an instance of java.util.Date to a positional parameter.
    * @param position
    * @param value
    * @param temporalType
    * @return the same query instance
    */
    public Query setParameter(int position, Date value, TemporalType temporalType) {
        return setParameter(position, convertTemporalType(value, temporalType));
    }

    /**
    * Bind an instance of java.util.Calendar to a positional parameter.
    * @param position
    * @param value
    * @param temporalType
    * @return the same query instance
    */
    public Query setParameter(int position, Calendar value, TemporalType temporalType) {
        return setParameter(position, convertTemporalType(value, temporalType));
    }

    protected void throwNoResultException(String message) {
        throw new NoResultException(message);
    }
    
    protected void throwNonUniqueResultException(String message) {
        throw new NonUniqueResultException(message);
    }
}
