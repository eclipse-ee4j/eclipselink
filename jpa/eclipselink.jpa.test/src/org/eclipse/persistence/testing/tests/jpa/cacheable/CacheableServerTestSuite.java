/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.cacheable;

import org.eclipse.persistence.testing.tests.jpa.cacheable.CacheableModelJunitTestAll;
import org.eclipse.persistence.testing.tests.jpa.cacheable.CacheableModelJunitTestNone;
import org.eclipse.persistence.testing.tests.jpa.cacheable.CacheableModelJunitTestEnableSelective;
import org.eclipse.persistence.testing.tests.jpa.cacheable.CacheableModelJunitTestDisableSelective;
import org.eclipse.persistence.testing.tests.jpa.cacheable.CacheableModelJunitTestUnspecified;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * <p><b>Purpose</b>: To collect the tests that will run against Application Server only.
 */
public class CacheableServerTestSuite extends TestSuite {
    public static Test suite() {
        JUnitTestCase.initializePlatform();
        TestSuite suite = new TestSuite();
        suite.setName("Cacheable ServerTestSuite");
        suite.addTest(CacheableModelJunitTestAll.suite());
        suite.addTest(CacheableModelJunitTestNone.suite());
        suite.addTest(CacheableModelJunitTestEnableSelective.suite());
        suite.addTest(CacheableModelJunitTestDisableSelective.suite());
        suite.addTest(CacheableModelJunitTestUnspecified.suite());
        return suite;
    }
}
