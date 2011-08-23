/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.framework.wdf;

import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;

public interface JPAEnvironment {
    
    
    public EntityManagerFactory createNewEntityManagerFactory(Map<String, Object> properties) throws NamingException; 


    public EntityManagerFactory getEntityManagerFactory();

    public EntityManagerFactory createNewEntityManagerFactory() throws NamingException;

    public EntityManager getEntityManager();

    public void beginTransaction(EntityManager em);

    public void commitTransaction(EntityManager em);

    public void rollbackTransaction(EntityManager em);

    public void markTransactionForRollback(EntityManager em);

    public void commitTransactionAndClear(EntityManager em);

    public void rollbackTransactionAndClear(EntityManager em);

    /**
     * Returns <code>true</code> if the transaction is active and not marked for rollback.
     * 
     * @param em
     *            The entity manager
     * @return <code>true</code> if the transaction is active and not marked for rollback
     */
    public boolean isTransactionActive(EntityManager em);

    /**
     * Returns <code>true</code> if the transaction marked for rollback but not rolled back yet.
     * 
     * @param em
     *            The entity manager
     * @return <code>true</code> if the transaction marked for rollback but not rolled back yet
     */
    public boolean isTransactionMarkedForRollback(EntityManager em);

    public boolean usesExtendedPC();

    public DataSource getDataSource();

    public Object getPropertyValue(EntityManager em, String key);

    public void evict(EntityManager em, Class<?> clazz);
    
    public void evictAll(EntityManager em);
}
