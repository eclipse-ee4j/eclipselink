/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection;


/*
 * See B5259059: NPE on anyObject mapping XPath null, anyCollection mapping XPath=filled
 * The use cases are described in the doc b5259059_jaxb_factory_npe_DesignSpec_v2006nnnn.doc
 */
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class XMLAnyObjectAndAnyCollectionMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("XMLAnyObjectAndAnyCollectionMapping Test Suite");

        /*
         * UseCases with the pattern *0*0 = 1,3,9,11 where both mapping XPath entries are null
         */
        // F:0000 = first obj, xpath=null, sec obj, xpath=null
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC01NoDefaultRootComplexChildrenTestCases.class);
        // P:0001 previous to b5259059 received NPE on empty xpath (2nd arg)
        // with full xpath (4th arg)
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC02NoDefaultRootComplexChildrenTestCases.class);
        // F:0010 = first obj, xpath=null, sec coll, xpath=null
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC03NoDefaultRootComplexChildrenTestCases.class);
        // P:0011 previous to b5259059 received NPE on empty xpath (2nd arg)
        // with full xpath (4th arg)
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC04NoDefaultRootComplexChildrenTestCases.class);
        // P:0011
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC05NoDefaultRootComplexChildrenTestCases.class);
        // P:0101
        // DocPreservation failure		
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC06NoDefaultRootComplexChildrenTestCases.class);
        // P:0110
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC07NoDefaultRootComplexChildrenTestCases.class);
        // P:0111
        // DocPreservation failure		
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC08NoDefaultRootComplexChildrenTestCases.class);
        // F:1000 = first coll, xpath=null, sec obj, xpath=null
        //suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC09NoDefaultRootComplexChildrenTestCases.class);
        // P:1001 previous to b5259059 received NPE on empty xpath (2nd arg)
        // with full xpath (4th arg)
        //suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC10NoDefaultRootComplexChildrenTestCases.class);
        // F:1010 = first coll, xpath=null, sec coll, xpath=null
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC11NoDefaultRootComplexChildrenTestCases.class);
        // P:1011 previous to b5259059 received NPE on empty xpath (2nd arg)
        // with full xpath (4th arg)
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC12NoDefaultRootComplexChildrenTestCases.class);
        // P:1100
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC13NoDefaultRootComplexChildrenTestCases.class);
        // P:1101
        // DocPreservation failure		
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC14NoDefaultRootComplexChildrenTestCases.class);
        // P:1110
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC15NoDefaultRootComplexChildrenTestCases.class);
        // P:1111
        // DocPreservation failure		
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.AnyObjectAndAnyCollectionUC16NoDefaultRootComplexChildrenTestCases.class);

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.XMLAnyObjectAndAnyCollectionMappingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
