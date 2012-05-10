/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
