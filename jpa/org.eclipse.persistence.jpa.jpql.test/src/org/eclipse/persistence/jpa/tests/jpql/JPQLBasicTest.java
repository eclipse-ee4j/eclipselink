/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

/**
 * The root class of all the Hermes unit-tests.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class JPQLBasicTest {

    /**
     * Creates a new <code>JPQLBasicTest</code>.
     */
    protected JPQLBasicTest() {
        super();
        initialize();
    }

    /**
     * Initializes this unit-tests.
     */
    protected void initialize() {
    }

    /**
     * Notifies this helper before a test is invoked.
     *
     * @throws Exception If an error was encountered during the initialization
     */
    protected void setUp() throws Exception {
    }

    /**
     * Notifies this helper before the tests of one class are invoked.
     *
     * @throws Exception If an error was encountered during the initialization
     */
    protected void setUpClass() throws Exception {
    }

    /**
     * Notifies this helper after a test was invoked.
     *
     * @throws Exception If an error was encountered during execution
     */
    protected void tearDown() throws Exception {
    }

    /**
     * Notifies this helper after the tests of one class were invoked.
     *
     * @throws Exception If an error was encountered during execution
     */
    protected void tearDownClass() throws Exception {
    }
}
