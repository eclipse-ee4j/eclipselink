/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     rbarkhouse - 2009-10-15 13:10:33  - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.converter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CompositeObjectSelfConverterTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Composite Object Self Mapping with Converter Test Suite");
        suite.addTestSuite(CompositeObjectSelfConverterTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositeobject.CompositeObjectSelfConverterTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

    public String getName() {
        String nameSoFar = super.getName();
        return nameSoFar + Boolean.getBoolean("useDeploymentXML");
    }

}
