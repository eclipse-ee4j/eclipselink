/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     06/08/2008-1.0M9 Andrei Ilitchev
//        - New file introduced for bug 235433: Can't customize ConnectionPolicy through JPA.
package org.eclipse.persistence.config;

/**
 *
 * This property could be specified while creating either EntityManagerFactory
 * (createEntityManagerFactory or persistence.xml)
 * or EntityManager (createEntityManager); the latter overrides the former.
 * <p>
 * The "isolated case" mentioned below is the case of some (or all) entities requiring isolated cache.
 * That could be achieved either by specifying the property for one or more entities:
 *   eclipselink.cache.shared.Employee {@literal ->} "false"
 * or for all entities:
 *   eclipselink.cache.shared.default {@literal ->} "false"
 * Note that this property(es) could be specified only on persistence unit level (cannot be passed to createEntityManager method).
 *
 * <p>JPA persistence property usage:
 * {@snippet :
 *  properties.add(PersistenceUnitProperties.EXCLUSIVE_CONNECTION_MODE, ExclusiveConnectionMode.Isolated);
 * }
 *
 * <p>Values are case-insensitive.
 * "" could be used instead of default value ExclusiveConnectionMode.DEFAULT.
 *
 * @see PersistenceUnitProperties
 */
public final class ExclusiveConnectionMode {
    /**
     * The value causes creation of
     * IsolatedClientSession in isolated case
     * and ClientSession otherwise.
     * Connection is kept exclusive for the duration of EclipseLink transaction.
     * Inside EclipseLink transaction all writes and reads performed through the exclusive connection.
     * Outside EclipseLink transaction a new connection is acquired from connection pool
     * for each read and released back immediately after the query is executed.
     * <p>
     * Note that EclipseLink transaction usually starts later than JTA transaction does,
     * or entityManager.beginTransaction method is called: EclipseLink usually waits until
     * it's about to perform a query that potentially alters database's state to start
     * EclipseLink transaction. That reflects in connection usage in Transactional mode.
     * Example:
     * {@snippet :
     *  // EclipseLink transaction is not started yet - readQuery1 acquires and immediately releases connection.
     *  readQuery1.getResultList();
     *  entityManager.getTransaction().begin();
     *  // EclipseLink transaction still not started yet - readQuery2 acquires and immediately releases connection.
     *  readQuery2.getResultList();
     *  entityManager.persist(new Employee());
     *  // EclipseLink transaction is not started yet - readQuery3 acquires and immediately releases connection.
     *  readQuery3.getResultList();
     *  entityManager.flush();
     *  // Before the flushing is performed EclipseLink transaction is started:
     *  // connection is acquired that will be kept until the end of transaction and used for all reads and writes.
     *  readQuery4.getResultList();
     *  entityManager.commit();
     *  // EclipseLink transaction is completed, the connection used by EclipseLink transaction is released;
     *  // readQuery5 acquires and immediately releases connection.
     *  readQuery5.getResultList();
     * }
     * <p>
     * There's a way to force beginning of EclipseLink transaction right before executing the first query after em.getTransaction().begin():
     *   "eclipselink.transaction.join-existing" {@literal ->} true.
     * In the example that setting would force EclipseLink transaction top begin right before readQuery2 is executed
     * and therefore cause execution of readQuery2 and readQuery3 through the exclusive connection
     * (of course the same connection would be used by readQuery4 and flush, too).
     * However, this approach could hardly be recommended:
     * none of the objects read after EclipseLink transaction begins
     * get into the shared cache unless altered therefore seriously reducing benefits of EclipseLink shared cache.
     */
    public static final String  Transactional = "Transactional";

    /**
     * The value causes creation of
     * ExclusiveIsolatedClientSession in isolated case
     * and throws exception otherwise.
     * Connection is kept exclusive for the whole lifetime of the owning EntityManager.
     * Inside EclipseLink transaction all writes and reads performed through the exclusive connection.
     * Outside EclipseLink transaction only isolated entities are read through the exclusive connection;
     * for non isolated entities a new connection is acquired from connection pool
     * for each read and released back immediately after the query is executed.
     * <p>
     * In the example presented above all five readQueries
     * would read isolated entity through the exclusive connection,
     * however non-isolated entity would be read the same as in Transactional mode.
     */
    public static final String  Isolated = "Isolated";

    /**
     * The value causes creation of ExclusiveIsolatedClientSession
     * in both isolated and non-isolated cases.
     * Connection is kept exclusive for the whole lifetime of the owning EntityManager.
     * All writes and reads performed through the exclusive connection.
     * <p>
     * In the example presented above all five readQueries
     * would read all entity through the exclusive connection.
     * <p>
     * Note that though all entities use the exclusive connection
     * only isolated entities use isolated cache; non-isolated entities use shared cache.
     */
    public static final String  Always = "Always";

    public static final String DEFAULT = Transactional;

    private ExclusiveConnectionMode() {
        // no instance please
    }
}
