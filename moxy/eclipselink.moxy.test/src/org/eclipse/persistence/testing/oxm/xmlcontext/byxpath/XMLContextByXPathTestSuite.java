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
 *     rbarkhou - 21 Sept 2009 - 1.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.xmlcontext.byxpath;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class XMLContextByXPathTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("XML Context 'Get Value By XPath' Test Suite");
        suite.addTestSuite(ByXPathTestCases.class);
        suite.addTestSuite(ByXPathNSTestCases.class);
        return suite;
    }
}
