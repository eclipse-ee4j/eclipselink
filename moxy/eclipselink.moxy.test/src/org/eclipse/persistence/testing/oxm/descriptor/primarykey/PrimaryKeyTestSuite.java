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
//     rbarkhouse - 2009-09-28 14:07:00 - initial implementation
package org.eclipse.persistence.testing.oxm.descriptor.primarykey;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.identifiedbyname.RootElementIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.identifiedbynamespace.RootElementIdentifiedByNamespaceTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsAlwaysWrapTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsTestCases;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsTestCases2;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsTestCases3;

public class PrimaryKeyTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Primary Key Test Suite");
        suite.addTestSuite(DeploymentXMLTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.descriptor.primarykey.PrimaryKeyTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

}
