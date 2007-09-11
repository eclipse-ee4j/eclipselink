/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jpa;

import java.util.Vector;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * <p>
 * <b>Purpose</b>: Defines the Interface for TopLink extensions to the EntityManager
 * <p>
 * @see javax.persistence.EntityManager
 */
/*
 * @author Gordon Yorke
 */

public interface EntityManager extends javax.persistence.EntityManager {

	/**
	 * This method returns the current session to the requestor.  The current session
	 * will be a the active UnitOfWork within a transaction and will be a 'scrap'
	 * UnitOfWork outside of a transaction.  The caller is conserned about the results
	 * then the getSession() or getUnitOfWork() API should be called.
	 */
    public Session getActiveSession();
    
    /**
     * Return the underlying server session
     */
    public ServerSession getServerSession();
    
    /**
     * This method will return the transactional UnitOfWork during the transaction and null
     * outside of the transaction.
     */
    public UnitOfWork getUnitOfWork();
    
    /**
     * This method will return a Session outside of a transaction and null within a transaction.
     */
    public Session getSession();
    
    /**
     * This method is used to create a query using a Toplink Expression and the return type.
     */
    public javax.persistence.Query createQuery(Expression expression, Class resultType);
    
    /**
     * This method will create a query object that wraps a TopLink Named Query.
     */
    public javax.persistence.Query createDescriptorNamedQuery(String queryName, Class descriptorClass);
    
    /**
     * This method will create a query object that wraps a TopLink Named Query.
     */
    public javax.persistence.Query createDescriptorNamedQuery(String queryName, Class descriptorClass, Vector argumentTypes);

}
