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
package org.eclipse.persistence.testing.oxm.mappings.anyobject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectWithGroupingWithXMLRootSimpleTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.AnyObjectKeepAllAsElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.AnyObjectWithoutGroupingWithXMLRootSimpleTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.AnyObjectXMLRootSimpleNonStringTestCases;

public class XMLAnyObjectMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("XMLAnyObjectMapping Test Suite");
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectComplexChildTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectNoChildTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectTextChildTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectComplexChildNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectNoDefaultRootComplexChildrenTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectWithGroupingWithXMLRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectWithGroupingWithXMLRootXSITypeTestCases.class);
        suite.addTestSuite(AnyObjectWithGroupingWithXMLRootSimpleTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement.AnyObjectXMLRootSimpleNonStringTestCases.class);

        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.AnyObjectComplexChildTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.AnyObjectNoChildTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.AnyObjectTextChildTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.AnyObjectNoDefaultRootComplexChildrenTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.AnyObjectWithoutGroupingWithXMLRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.AnyObjectKeepUnknownAsElementTestCases.class);
        suite.addTestSuite(AnyObjectWithoutGroupingWithXMLRootSimpleTestCases.class);
        suite.addTestSuite(AnyObjectXMLRootSimpleNonStringTestCases.class);

        suite.addTestSuite(AnyObjectKeepAllAsElementTestCases.class);

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anyobject.XMLAnyObjectMappingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
