/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.performance;

import junit.framework.TestSuite;
import junit.framework.Test;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import javax.persistence.*;

/**
 * <p><b>Purpose</b>: Run performance tests inside JEE server.
 */
public class JPAPerformanceServerTestSuite extends JUnitTestCase {
    
    public JPAPerformanceServerTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPAPerformanceServerTestSuite");
        suite.addTest(new JPAPerformanceServerTestSuite("testPerformance"));
        return suite;
    }

    public void testPerformance() throws Throwable {
        TestExecutor executor = new TestExecutor();
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("performance");
        executor.setSession(((EntityManagerFactoryImpl)factory).getServerSession());
        executor.setEntityManagerFactory(factory);
        JPAPerformanceRegressionModel test = new JPAPerformanceRegressionModel();
        executor.runTest(test);
        executor.logResultForTestEntity(test);
        factory.close();
    }
}
