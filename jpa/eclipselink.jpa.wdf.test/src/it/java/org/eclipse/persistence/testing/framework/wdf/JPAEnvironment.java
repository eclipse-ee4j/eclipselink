/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.framework.wdf;

import java.util.Map;

import javax.naming.NamingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
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
