/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.any;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AnyTestCases extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("AnyTestCases");
        suite.addTestSuite(EmptyAnyCollectionNoRootTestCases.class);
        suite.addTestSuite(EmptyAnyCollectionWithRootTestCases.class);
        suite.addTestSuite(NullAnyCollectionNoRootTestCases.class);
        suite.addTestSuite(NullAnyCollectionWithRootTestCases.class);
        return suite;
    }

}
