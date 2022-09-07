/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.mongo;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite to test Mongo database orm.xml support.
 * ** To run this test suite the Mongo database must be running.
 */
public class MongoDatabaseXMLTest extends MongoDatabaseTest {

    public MongoDatabaseXMLTest(){
    }

    public MongoDatabaseXMLTest(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        MongoDatabaseXMLTest testSetup = new MongoDatabaseXMLTest("testSetup");
        suite.setName("MongoXMLTestSuite");
        if (testSetup.isEnabled()) {
            suite.addTest(testSetup);
            suite.addTest(new MongoDatabaseXMLTest("testInsert"));
            suite.addTest(new MongoDatabaseXMLTest("testFind"));
            suite.addTest(new MongoDatabaseXMLTest("testUpdate"));
            suite.addTest(new MongoDatabaseXMLTest("testMerge"));
            suite.addTest(new MongoDatabaseXMLTest("testLockError"));
            suite.addTest(new MongoDatabaseXMLTest("testRefresh"));
            suite.addTest(new MongoDatabaseXMLTest("testDelete"));
            suite.addTest(new MongoDatabaseXMLTest("testSimpleJPQL"));
            suite.addTest(new MongoDatabaseXMLTest("testJPQLLike"));
            suite.addTest(new MongoDatabaseXMLTest("testComplexJPQL"));
            suite.addTest(new MongoDatabaseXMLTest("testNativeQuery"));
        }
        return suite;
    }

    @Override
    public String getPersistenceUnitName() {
        return "mongo-xml";
    }

}
