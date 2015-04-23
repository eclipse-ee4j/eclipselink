/*******************************************************************************
 * Copyright (c) 2012, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.rs;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RESTfulTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("RESTful Test Suite");
        suite.addTestSuite(IsReadableTestCases.class);
        suite.addTestSuite(IsWriteableTestCases.class);
        suite.addTestSuite(SimpleListTestCases.class);
        suite.addTestSuite(ArrayTestCases.class);
        suite.addTestSuite(LinkedListTestCases.class);
        suite.addTestSuite(GenericListTestCases.class);
        suite.addTestSuite(ListAdapterTestCases.class);
        suite.addTestSuite(MapAdapterTestCases.class);
        suite.addTestSuite(JAXBElementsGenericListTestCases.class);
        suite.addTestSuite(MyArrayListTestCases.class);
        return suite;
    }

}