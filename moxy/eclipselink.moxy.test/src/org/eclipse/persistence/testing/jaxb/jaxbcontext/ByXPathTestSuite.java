/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 27 November 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ByXPathTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Get/Set/CreateByXPath Test Suite");

        suite.addTestSuite(GetByXPathTests.class);
        suite.addTestSuite(SetByXPathTests.class);
        suite.addTestSuite(CreateByXPathTests.class);

        return suite;
    }

}