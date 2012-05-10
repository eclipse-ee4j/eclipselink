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
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RootLevelListTestCases extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Root Level List Test Suite");
        suite.addTestSuite(WithoutXmlRootElementTestCases.class);
        suite.addTestSuite(WithXmlRootElementTestCases.class);
        suite.addTestSuite(WithXmlRootElementSetTestCases.class);
        suite.addTestSuite(WithoutXmlRootElementSetTestCases.class);
        
        suite.addTestSuite(WithXmlRootElementJAXBElementTestCases.class);
        suite.addTestSuite(WithXmlRootElementJAXBElementNoRootTestCases.class);
        suite.addTestSuite(WithoutXmlRootElementJAXBElementTestCases.class);        
        suite.addTestSuite(WithXmlRootElementJAXBElementSetTestCases.class);
        suite.addTestSuite(WithoutXmlRootElementJAXBElementSetTestCases.class);
        
        return suite;
    }

}