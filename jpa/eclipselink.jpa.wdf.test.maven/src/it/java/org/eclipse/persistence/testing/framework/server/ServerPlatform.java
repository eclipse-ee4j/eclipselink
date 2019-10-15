/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     SAP - use in eclipselink.jpa.wdf.test package
package org.eclipse.persistence.testing.framework.server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public interface ServerPlatform {

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
     * Is the platform clustered?
     */
    boolean isClustered();

    /**
     * Return the managed EntityManager for the persistence unit.
     */
    EntityManager getEntityManager(String persistenceUnit);

    /**
     * Return the managed EntityManagerFactory for the persistence unit.
     */
    EntityManagerFactory getEntityManagerFactory(String persistenceUnit);

}
