/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 19 October 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmllocation;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XmlLocationTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("XmlLocation Test Suite");
        suite.addTestSuite(XmlLocationTestCases.class);
        suite.addTestSuite(XmlLocationNonTransientTestCases.class);
        suite.addTestSuite(XmlLocationErrorTestCases.class);
        suite.addTestSuite(XmlLocationSchemaGenTests.class);
        return suite;
    }

}