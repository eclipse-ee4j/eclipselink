/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.framework.jpa.server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Generic JEE server platform.
 */
public class JEEPlatform implements ServerPlatform {

    /** The entity manager for the test is injected and passed to the test server platform. */
    public static EntityManager entityManager;

    /** The entity manager factory for the test is injected and passed to the test server platform. */
    public static EntityManagerFactory entityManagerFactory;

    /** The variable for getting entity manager by jndi lookup, set the system property "ejb.lookup" to be true if you want jndi lookup */
    public static final String EJB_LOOKUP = "ejb.lookup";

    public static boolean ejbLookup = false;

    /**
     * Nothing required in JEE.
     */
    @Override
    public void initialize() {

    }

    /**
     * Return if the JTA transaction is active.
     */
    @Override
    public boolean isTransactionActive() {
        try {
            return getUserTransaction().getStatus() != Status.STATUS_NO_TRANSACTION;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Return if the JTA transaction is roll back only.
     */
    @Override
    public boolean getRollbackOnly() {
        try {
            return getUserTransaction().getStatus() != Status.STATUS_ACTIVE;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Start a new JTA transaction.
     */
    @Override
    public void beginTransaction() {
        try {
            getUserTransaction().begin();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Commit the existing JTA transaction.
     */
    @Override
    public void commitTransaction() {
        try {
            getUserTransaction().commit();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Roll back the existing JTA transaction.
     */
    @Override
    public void rollbackTransaction() {
        try {
            getUserTransaction().rollback();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Not required in JEE.
     */
    @Override
    public void closeEntityManager(EntityManager entityManager) {
    }

    public UserTransaction getUserTransaction() {
        try {
            return (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        } catch (NamingException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Mark the existing JTA transaction for rollback.
     */
    @Override
    public void setTransactionForRollback() {
        try {
            getUserTransaction().setRollbackOnly();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Is the platform Oracle?
     */
    @Override
    public boolean isOc4j() {
        return false;
    }

    /**
     * Is the platform Weblogic?
     */
    @Override
    public boolean isWeblogic() {
        return false;
    }

    /**
     * Is the platform JBoss?
     */
    @Override
    public boolean isJBoss() {
        return false;
    }

    /**
     * Is the platform Spring?
     */
    @Override
    public boolean isSpring() {
        return false;
    }

    /**
     * Is the platform clustered?
     */
    @Override
    public boolean isClustered() {
        return false;
    }

    /**
     * Return the managed EntityManager for the persistence unit.
     */
    @Override
    public EntityManager getEntityManager(String persistenceUnit) {
        boolean useLookup = ejbLookup || Boolean.getBoolean(EJB_LOOKUP);
        if (!useLookup) {
            return entityManager;
        } else {
            String contextName = "java:comp/env/persistence/" + persistenceUnit + "/entity-manager";
            try {
                return (EntityManager)new InitialContext().lookup(contextName);
            } catch (NamingException exception) {
                //most tests do not need the fallback lookup as the java:comp/env
                //is the only namespace they should worry about
                if (persistenceUnit.contains("member")) {
                    //retry within application space:
                    String appContextName = "java:app/persistence/" + persistenceUnit + "/entity-manager";
                    try {
                        return (EntityManager)new InitialContext().lookup(appContextName);
                    } catch (NamingException e) {
                        e.addSuppressed(exception);
                        throw new RuntimeException(e);
                    }
                }
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     * Return the managed EntityManagerFactory for the persistence unit.
     */
    @Override
    public EntityManagerFactory getEntityManagerFactory(String persistenceUnit) {
        boolean useLookup = ejbLookup || Boolean.getBoolean(EJB_LOOKUP);
        if (!useLookup) {
            return entityManagerFactory;
        } else{
            String contextName = "java:comp/env/persistence/" + persistenceUnit + "/factory";
            try {
                return (EntityManagerFactory)new InitialContext().lookup(contextName);
            } catch (NamingException exception) {
                //most tests do not need the fallback lookup as the java:comp/env
                //is the only namespace they should worry about
                if (persistenceUnit.contains("member")) {
                    //retry within application space:
                    String appContextName = "java:app/persistence/" + persistenceUnit + "/factory";
                    try {
                        return (EntityManagerFactory)new InitialContext().lookup(appContextName);
                    } catch (NamingException e) {
                        e.addSuppressed(exception);
                        throw new RuntimeException(e);
                    }
                }
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     * Join the transaction if required
     */
    @Override
    public void joinTransaction(EntityManager em) {
        em.joinTransaction();
    }

}
