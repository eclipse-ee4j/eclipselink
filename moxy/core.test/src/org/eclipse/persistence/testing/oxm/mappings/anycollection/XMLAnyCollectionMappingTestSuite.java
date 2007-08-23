/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anycollection;

import org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionKeepAllAsElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionKeepUnknownAsElementTestCases;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class XMLAnyCollectionMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("XMLAnyCollectionMapping Test Suite");
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionOnlyMappedWithoutGroupingTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionComplexChildrenTestCases.class);

        /*
         * B5112171: 25 Apr 2006
         * During marshalling - XML AnyObject and AnyCollection
         * mappings throw a NullPointerException when the
         * "document root element" on child object descriptors are not
         * all defined.  These nodes will be ignored with a warning.
         */
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionNoDefaultRootComplexChildrenTestCases.class);

        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionNonRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionNoChildrenTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionTextChildTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionMixedChildrenTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionComplexChildrenTestCases.class);

        // B5112171
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionNoDefaultRootComplexChildrenTestCases.class);

        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionNoChildrenTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionTextChildTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionMixedChildrenTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionComplexChildrenNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionMixedChildrenArrayListTestCases.class);

        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement.AnyCollectionWithoutGroupingWithXMLRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionWithGroupingWithXMLRootTestCases.class);
        suite.addTestSuite(AnyCollectionKeepAllAsElementTestCases.class);
        suite.addTestSuite(AnyCollectionKeepUnknownAsElementTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anycollection.XMLAnyCollectionMappingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}