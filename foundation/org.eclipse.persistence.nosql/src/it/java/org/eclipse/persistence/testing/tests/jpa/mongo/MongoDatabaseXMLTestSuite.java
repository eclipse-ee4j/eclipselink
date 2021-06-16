/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.mongo;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite to test Mongo database orm.xml support.
 * ** To run this test suite the Mongo database must be running.
 */
public class MongoDatabaseXMLTestSuite extends MongoDatabaseTestSuite {

    public MongoDatabaseXMLTestSuite(){
    }

    public MongoDatabaseXMLTestSuite(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        MongoDatabaseXMLTestSuite testSetup = new MongoDatabaseXMLTestSuite("testSetup");
        suite.setName("MongoXMLTestSuite");
        if (testSetup.isEnabled()) {
            suite.addTest(testSetup);
            suite.addTest(new MongoDatabaseXMLTestSuite("testInsert"));
            suite.addTest(new MongoDatabaseXMLTestSuite("testFind"));
            suite.addTest(new MongoDatabaseXMLTestSuite("testUpdate"));
            suite.addTest(new MongoDatabaseXMLTestSuite("testMerge"));
            suite.addTest(new MongoDatabaseXMLTestSuite("testLockError"));
            suite.addTest(new MongoDatabaseXMLTestSuite("testRefresh"));
            suite.addTest(new MongoDatabaseXMLTestSuite("testDelete"));
            suite.addTest(new MongoDatabaseXMLTestSuite("testSimpleJPQL"));
            suite.addTest(new MongoDatabaseXMLTestSuite("testJPQLLike"));
            suite.addTest(new MongoDatabaseXMLTestSuite("testComplexJPQL"));
            suite.addTest(new MongoDatabaseXMLTestSuite("testNativeQuery"));
        }
        return suite;
    }

    @Override
    public String getPersistenceUnitName() {
        return "mongo-xml";
    }

}
