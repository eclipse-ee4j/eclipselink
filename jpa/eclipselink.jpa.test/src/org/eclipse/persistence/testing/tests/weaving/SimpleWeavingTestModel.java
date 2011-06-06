/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.weaving;

// J2SE imports
import java.util.*;

// TopLink Testing Framework
import org.eclipse.persistence.testing.framework.*;

public class SimpleWeavingTestModel extends TestModel {
	
    public SimpleWeavingTestModel () {
        setDescription("This simple model tests TopLink's weaving functionality.");
    }
    
    public void addTests() {
		junit.framework.TestSuite testsuite = (junit.framework.TestSuite)SimpleWeaverTestSuite.suite();
		for (Enumeration e = testsuite.tests(); e.hasMoreElements();) {
			junit.framework.TestCase testcase =	(junit.framework.TestCase)e.nextElement();
			addTest(new JUnitTestCase(testcase));
		}
    }

}
