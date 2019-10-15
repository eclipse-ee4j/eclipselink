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
//     Oracle - initial implementation
package org.eclipse.persistence.testing.tests;

import org.eclipse.persistence.testing.tests.jpa.mongo.MongoDatabaseTestSuite;
import org.eclipse.persistence.testing.tests.jpa.mongo.MongoDatabaseXMLTestSuite;
import org.eclipse.persistence.testing.tests.jpa.mongo.MongoTestSuite;
import org.eclipse.persistence.testing.tests.jpa.mongo.MongoXMLTestSuite;

import junit.framework.TestSuite;
import junit.framework.Test;

public class NoSQLJPATestSuite extends TestSuite{

    public static Test suite() {
        TestSuite fullSuite = new TestSuite();
        fullSuite.setName("NoSQLJPATestSuite");

        try {
            Class.forName("com.mongodb.client.MongoDatabase");
            System.out.println("Testing MongoDatabaseTestSuite");
            fullSuite.addTest(MongoDatabaseTestSuite.suite());
            fullSuite.addTest(MongoDatabaseXMLTestSuite.suite());
        } catch (ClassNotFoundException e) {
            System.out.println("Testing MongoTestSuite");
            fullSuite.addTest(MongoXMLTestSuite.suite());
            fullSuite.addTest(MongoTestSuite.suite());
        }
        return fullSuite;
    }
}
