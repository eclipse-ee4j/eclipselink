/*
 * Copyright (c) 2011, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial implementation as part of extensibility feature
//     05/26/2016-2.7 Tomas Kraus
//       - 494610: Session Properties map should be Map<String, Object>
package org.eclipse.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * <p>
 * <b>Purpose</b>: Defines the Interface for EclipseLink extensions to the EntityManagerFactory
 * </p>
 * @see jakarta.persistence.EntityManagerFactory
 */
public interface JpaEntityManagerFactory extends EntityManagerFactory, AutoCloseable {

    /**
     * Returns the DatabaseSession that the Factory will be using and
     * initializes it if it is not available.
     */
    DatabaseSessionImpl getDatabaseSession();

    /**
     * Returns the ServerSession that the Factory will be using and
     * initializes it if it is not available.
     */
    ServerSession getServerSession();

    /**
     * Returns the SessionBroker that the Factory will be using and
     * initializes it if it is not available.
     *
     * Calls to this method should only be made on entity managers
     * representing composite persistence units.
     */
    SessionBroker getSessionBroker();

    /**
     * Gets the underlying implementation of the EntityManagerFactory.
     * This method will return a version of EntityManagerFactory that is
     * based on the available metadata at the time it is called.  Future calls
     * to refresh will not affect that metadata on this EntityManagerFactory.
     */
    EntityManagerFactoryDelegate unwrap();

    /**
     * As this EntityManagerFactory to refresh itself.  This will cause the
     * underlying EntityManagerFactory implementation to be dropped and a new one
     * to be bootstrapped.  Existing EntityManagers will continue to use the old implementation
     */
    void refreshMetadata(Map<String, Object> properties);

    /**
     * Create a new application-managed {@code EntityManager}, start a resource-local
     * transaction, and call the given function, passing both the {@code EntityManager}
     * and the {@code EntityTransaction}.
     * The given function is responsible for finishing the transaction by calling {@code commit}
     * or {@code rollback} method.
     *
     * @param work a function to be called in the scope of the transaction
     */
    default void withTransaction(BiConsumer<EntityManager, EntityTransaction> work) {
        try (EntityManager em = this.createEntityManager()) {
            EntityTransaction t = em.getTransaction();
            t.begin();
            work.accept(em, t);
        }
    }

    /**
     * Create a new application-managed {@code EntityManager}, start a resource-local
     * transaction, and call the given function, passing both the {@code EntityManager}
     * and the {@code EntityTransaction}.
     * The given function is responsible for finishing the transaction by calling {@code commit}
     * or {@code rollback} method.
     *
     * @param work a function to be called in the scope of the transaction
     * @return the value returned by the given function
     */
    default <R> R withTransaction(BiFunction<EntityManager, EntityTransaction, R> work) {
        try (EntityManager em = this.createEntityManager()) {
            EntityTransaction t = em.getTransaction();
            t.begin();
            return work.apply(em, t);
        }
    }

    /**
     * Create a new application-managed {@code EntityManager}, start a resource-local
     * transaction, and call the given function, passing the {@code EntityManager}.
     * If the given function does not throw an exception, commit the transaction and
     * return the result of the function. If the function does throw an exception,
     * roll back the transaction and rethrow the exception. Finally, close the
     * {@code EntityManager}.
     *
     * @param work a function to be called in the scope of the transaction
     */
    default void withTransaction(TransactionVoidWork work) {
        try (EntityManager em = this.createEntityManager()) {
            EntityTransaction t = em.getTransaction();
            try {
                t.begin();
                work.work(em);
                t.commit();
            } catch (Exception e) {
                t.rollback();
                throw new PersistenceException("Application-managed transaction failed", e);
            }
        }
    }

    /**
     * Create a new application-managed {@code EntityManager}, start a resource-local
     * transaction, and call the given function, passing the {@code EntityManager}.
     * If the given function does not throw an exception, commit the transaction and
     * return the result of the function. If the function does throw an exception,
     * roll back the transaction and rethrow the exception. Finally, close the
     * {@code EntityManager}.
     *
     * @param work a function to be called in the scope of the transaction
     * @return the value returned by the given function
     */
    default <R> R withTransaction(TransactionWork<R> work) {
        try (EntityManager em = this.createEntityManager()) {
            EntityTransaction t = em.getTransaction();
            try {
                t.begin();
                R result = work.work(em);
                t.commit();
                return result;
            } catch (Exception e) {
                t.rollback();
                throw new PersistenceException("Application-managed transaction failed", e);
            }
        }
    }

    /**
     * A task that runs in a transaction, returns no result and may throw an exception.
     * Implementors define a single method with {@link EntityManager} argument called {@code work}.
     */
    @FunctionalInterface
    interface TransactionVoidWork {
        /**
         * Executes the function. Throws an exception if unable to do so.
         *
         * @param em the application-managed {@link EntityManager} instance
         * @throws Exception when unable to compute a result
         */
        void work(EntityManager em) throws Exception;
    }

    /**
     * A task that runs in a transaction, returns result and may throw an exception.
     * Implementors define a single method with {@link EntityManager} argument called {@code work}.
     *
     * @param <R> the result type of method {@code work}
     */
    @FunctionalInterface
    interface TransactionWork<R> {
        /**
         * Executes the function. Throws an exception if unable to do so.
         *
         * @param em the application-managed {@link EntityManager} instance
         * @return the computed result
         * @throws Exception when unable to compute a result
         */
        R work(EntityManager em) throws Exception;
    }

}

