/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/22/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import javax.persistence.EntityManager;

import org.eclipse.persistence.sessions.server.ServerSession;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class DDLTestSuite extends JUnitTestCase {
    public DDLTestSuite() {}
    
    public DDLTestSuite(String name) {
        super(name);
    }
    
    @Override
    public void setUp () {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DDLTestSuite");
        
        suite.addTest(ForeignKeyTestSuite.suite());
        suite.addTest(IndexTestSuite.suite());
        
        return suite;
    }
}
