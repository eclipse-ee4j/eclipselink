/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
public class MongoXMLTestSuite extends MongoTestSuite {
    
    public MongoXMLTestSuite(){
    }

    public MongoXMLTestSuite(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("MongoXMLTestSuite");
        suite.addTest(new MongoXMLTestSuite("testSetup"));
        suite.addTest(new MongoXMLTestSuite("testInsert"));
        suite.addTest(new MongoXMLTestSuite("testFind"));
        suite.addTest(new MongoXMLTestSuite("testUpdate"));
        suite.addTest(new MongoXMLTestSuite("testMerge"));
        suite.addTest(new MongoXMLTestSuite("testLockError"));
        suite.addTest(new MongoXMLTestSuite("testRefresh"));
        suite.addTest(new MongoXMLTestSuite("testDelete"));
        suite.addTest(new MongoXMLTestSuite("testSimpleJPQL"));
        suite.addTest(new MongoXMLTestSuite("testJPQLLike"));
        suite.addTest(new MongoXMLTestSuite("testComplexJPQL"));
        suite.addTest(new MongoXMLTestSuite("testNativeQuery"));
        return suite;
    }
    
    @Override
    public String getPersistenceUnitName() {
        return "mongo-xml";
    }
    
}
