/*******************************************************************************
 * Copyright (c) 2011, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
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
        suite.setName("MongoXMLTestSuite");
        suite.addTest(new MongoDatabaseXMLTestSuite("testSetup"));
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
        return suite;
    }

    @Override
    public String getPersistenceUnitName() {
        return "mongo-xml";
    }

}
