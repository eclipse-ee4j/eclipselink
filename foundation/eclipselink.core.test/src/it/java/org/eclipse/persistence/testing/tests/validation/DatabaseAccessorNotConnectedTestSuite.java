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
package org.eclipse.persistence.testing.tests.validation;

/**
 * This testsuite was added to allow test to be run to validate that the appropriate
 * DatabaseAccessorNotConnectedException is thrown
 * Expanded to also test databaseAccessorConnectionIsNull is thrown
 */
public class DatabaseAccessorNotConnectedTestSuite extends org.eclipse.persistence.testing.framework.TestSuite {
    public DatabaseAccessorNotConnectedTestSuite() {
        super();
        setDescription("This test suite monitors and verifies that the appropriate DatabaseException - DatabaseAccessor not connected is thrown");
        setName("DatabaseAccessorNotConnectedTestSuite");
    }

    public void addTests() {
        addTest(new NotConnectedDeleteObjectTest());
        addTest(new NotConnectedReadObjectTest());
        addTest(new NotConnectedUpdateObjectQueryTest());
        addTest(new NotConnectedWriteObjectTest());
        addTest(new ConnectionIsNullAccessorTest());
    }
}
