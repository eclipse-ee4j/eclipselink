/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.framework.server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public interface ServerPlatform {

    /**
     * Allow for any initialization.
     */
    void initialize();

    /**
     * Return if the JTA transaction is active.
     */
    boolean isTransactionActive();

    /**
     * Return if the JTA transaction is roll back only.
     */
    boolean getRollbackOnly();

    /**
     * Start a new JTS transaction.
     */
    void beginTransaction();

    /**
     * Commit the existing JTS transaction.
     */
    void commitTransaction();

    /**
     * Roll back the existing JTS transaction.
     */
    void rollbackTransaction();

    /**
     * Mark the existing JTS transaction for rollback.
     */
    void setTransactionForRollback();

    /**
     * Close the entity manager if required.
     */
    void closeEntityManager(EntityManager entityManager);

    /**
     * Is the platform Oracle?
     */
    boolean isOc4j();

    /**
     * Is the platform Weblogic?
     */
    boolean isWeblogic();

    /**
     * Is the platform JBoss?
     */
    boolean isJBoss();

    /**
     * Is the platform Spring?
     */
    boolean isSpring();

    /**
     * Is the platform clustered?
     */
    boolean isClustered();

    /**
     * Join the transaction if required
     */
    void joinTransaction(EntityManager em);

    /**
     * Return the managed EntityManager for the persistence unit.
     */
    EntityManager getEntityManager(String persistenceUnit);

    /**
     * Return the managed EntityManagerFactory for the persistence unit.
     */
    EntityManagerFactory getEntityManagerFactory(String persistenceUnit);

}
