/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4.1 - initial implementation
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
