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
 *     Oracle - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests;

import org.eclipse.persistence.testing.tests.jpa.mongo.MongoTestSuite;
import org.eclipse.persistence.testing.tests.jpa.mongo.MongoXMLTestSuite;

import junit.framework.TestSuite;
import junit.framework.Test;

public class NoSQLJPATestSuite extends TestSuite{
    
    public static Test suite() {
        TestSuite fullSuite = new TestSuite();
        fullSuite.setName("NoSQLJPATestSuite");

        fullSuite.addTest(MongoTestSuite.suite());
        fullSuite.addTest(MongoXMLTestSuite.suite());
        return fullSuite;
    }
}
