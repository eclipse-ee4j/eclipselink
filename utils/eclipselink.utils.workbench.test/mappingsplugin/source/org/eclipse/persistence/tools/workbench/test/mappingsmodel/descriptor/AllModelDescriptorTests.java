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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor.xml.MWEisDescriptorTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor.xml.MWOXDescriptorTests;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor.xml.MWXmlDescriptorTests;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * decentralize test creation code
 */
public class AllModelDescriptorTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(ClassTools.packageNameFor(AllModelDescriptorTests.class));

        suite.addTest(MWAggregateDescriptorTests.suite());
        suite.addTest(MWDescriptorTests.suite());
        suite.addTest(MWMappingDescriptorTests.suite());
        suite.addTest(MWTableDescriptorTests.suite());
        suite.addTest(MWXmlDescriptorTests.suite());
        suite.addTest(MWEisDescriptorTests.suite());
        suite.addTest(MWOXDescriptorTests.suite());
        suite.addTest(MWInterfaceDescriptorTests.suite());
        suite.addTest(MWDescriptorReturningPolicyTests.suite());

        return suite;
    }

    /**
     * suppress instantiation
     */
    private AllModelDescriptorTests() {
        super();
    }

}
