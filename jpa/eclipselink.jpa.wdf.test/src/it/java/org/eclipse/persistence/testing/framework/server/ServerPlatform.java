/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     SAP - use in eclipselink.jpa.wdf.test package
package org.eclipse.persistence.testing.framework.server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

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
