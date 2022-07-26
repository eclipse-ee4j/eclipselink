/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedTypeProvider;

/**
 * This helper is responsible to create and initialize the ORM Configuration and the Persistence
 * Configuration required to execute the unit-tests.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public interface JPQLQueryTestHelper {

    /**
     * Retrieves the external form of the ORM configuration with the given name.
     *
     * @param ormXmlFileName The name of the orm.xml
     * @return The external form of the ORM configuration.
     */
    IORMConfiguration getORMConfiguration(String ormXmlFileName);

    /**
     * Returns the persistence unit that manages some entities.
     *
     * @return The external form of a persistence unit that will be used to retrieve the entities
     * used for testing
     */
    IManagedTypeProvider getPersistenceUnit();

    /**
     * Notifies this helper before a test is invoked.
     *
     */
    void setUp();

    /**
     * Notifies this helper before the tests are invoked, which is before the entire test suite is invoked.
     *
     */
    void setUpBefore();

    /**
     * Notifies this helper after a test was invoked.
     *
     */
    void tearDown();

    /**
     * Notifies this helper after the entire suite of tests have been invoked, which is done after
     * the very last test that was invoked.
     *
     */
    void tearDownAfter();
}
