/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa;

import java.util.List;

import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * <p>
 * <b>Purpose</b>: Defines the Interface for EclipseLink extensions to the EntityManager
 * <p>
 * @see javax.persistence.EntityManager
 */
/*
 * @author Gordon Yorke
 */

public interface JpaEntityManager extends javax.persistence.EntityManager {

    /**
     * This method returns the current session to the requester.  The current session
     * will be the active UnitOfWork within a transaction and will be a 'scrap'
     * UnitOfWork outside of a transaction.  The caller is concerned about the results
     * then the getSession() or getUnitOfWork() API should be called.
     */
    Session getActiveSession();
    
    /**
     * Return the underlying server session
     */
    ServerSession getServerSession();
    
    /**
     * This method will return the transactional UnitOfWork during the transaction and null
     * outside of the transaction.
     */
    UnitOfWork getUnitOfWork();
    
    /**
     * This method will return a Session outside of a transaction and null within a transaction.
     */
    Session getSession();
    
    /**
     * This method is used to create a query using a EclipseLink Expression for the entity class.
     */
    javax.persistence.Query createQuery(Expression expression, Class entityClass);
    
    /**
     * This method is used to create a query using a EclipseLink DatabaseQuery.
     */
    javax.persistence.Query createQuery(DatabaseQuery query);
    
    /**
     * This method is used to create a query using a EclipseLink Call.
     */
    javax.persistence.Query createQuery(Call call);
    
    /**
     * This method is used to create a query using a EclipseLink Call for the entity class.
     */
    javax.persistence.Query createQuery(Call call, Class entityClass);
    
    /**
     * This method is used to create a query using query by example.
     */
    javax.persistence.Query createQueryByExample(Object exampleObject);
    
    /**
     * This method will create a query object that wraps a EclipseLink Named Query.
     */
    javax.persistence.Query createDescriptorNamedQuery(String queryName, Class descriptorClass);
    
    /**
     * This method will create a query object that wraps a EclipseLink Named Query.
     */
    javax.persistence.Query createDescriptorNamedQuery(String queryName, Class descriptorClass, List argumentTypes);

    /**
     * This method will load the passed entity or collection of entities using the passed AttributeGroup.
     * In case of collection all members should be either entities of the same type
     * or have a common inheritance hierarchy mapped root class.
     * The AttributeGroup should correspond to the entity type. 
     */
    public void load(Object entityOrEntities, AttributeGroup group);

    /**
     * This method will return copy the passed entity using the passed AttributeGroup.
     * In case of collection all members should be either entities of the same type
     * or have a common inheritance hierarchy mapped root class.
     * The AttributeGroup should correspond to the entity type. 
     */
    public Object copy(Object entityOrEntities, AttributeGroup group);
}
