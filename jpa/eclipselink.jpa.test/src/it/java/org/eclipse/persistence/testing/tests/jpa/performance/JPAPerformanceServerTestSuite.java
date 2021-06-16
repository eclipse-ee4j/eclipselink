/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.performance;

import junit.framework.TestSuite;
import junit.framework.Test;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import jakarta.persistence.*;

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
        executor.setSession(((JpaEntityManagerFactory)factory).getServerSession());
        executor.setEntityManagerFactory(factory);
        JPAPerformanceRegressionModel test = new JPAPerformanceRegressionModel();
        executor.runTest(test);
        executor.logResultForTestEntity(test);
        factory.close();
    }
}
