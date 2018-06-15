/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 05 September 2012 - 2.4 - Initial implementation

package org.eclipse.persistence.testing.jaxb.typemappinginfo.parray;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PrimitiveArrayTestSuite extends TestCase {

    public PrimitiveArrayTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Primitive Array TypeMappingInfo Test Suite");
        suite.addTestSuite(StringArrayTestsCases.class);
        suite.addTestSuite(StringArrayNilTestsCases.class);
        suite.addTestSuite(StringArraySingleNodeTestsCases.class);
        suite.addTestSuite(PrimitiveIntArrayTestsCases.class);
        return suite;
    }

}
