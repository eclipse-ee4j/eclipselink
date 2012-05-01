/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4 - Initial Implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory.ClassLevelAccessorTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory.PackageLevelAccessorTestCases;
import org.eclipse.persistence.testing.jaxb.cycle.CycleRecoverableTestCases;

public class SunCompatibilityTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Sun Compatability Test Suite");
        
        suite.addTestSuite(CycleRecoverableTestCases.class);

        suite.addTestSuite(ClassLevelAccessorTestCases.class);
        suite.addTestSuite(PackageLevelAccessorTestCases.class);
        
        return suite;
    }
}