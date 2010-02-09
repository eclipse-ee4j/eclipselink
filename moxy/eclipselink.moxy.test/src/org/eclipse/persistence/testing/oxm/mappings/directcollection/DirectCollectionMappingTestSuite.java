/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directcollection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.arraylist.DirectCollectionArrayListTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.arraylist.xmlattribute.DirectCollectionArrayListXMLAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.converter.DirectCollectionObjectTypeConverterTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.errortests.DirectCollectionErrorTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement.DirectCollectionWithGroupingElementIdentifiedByNameEmptyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement.DirectCollectionWithGroupingElementIdentifiedByNameIntegerTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement.DirectCollectionWithGroupingElementIdentifiedByNameNullItemTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement.DirectCollectionWithGroupingElementIdentifiedByNameNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement.DirectCollectionWithGroupingElementIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement.DirectCollectionWithGroupingElementIntegerSingleNodeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement.DirectCollectionWithGroupingElementListWithUnionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement.DirectCollectionWithGroupingElementSchemaTypeSingleNodeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement.DirectCollectionWithGroupingElementSchemaTypeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement.DirectCollectionWithGroupingElementSingleElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement.DirectCollectionWithGroupingElementWithCommentTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement.DirectCollectionWithoutGroupingElementIdentifiedByNameEmptyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement.DirectCollectionWithoutGroupingElementIdentifiedByNameIntegerTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement.DirectCollectionWithoutGroupingElementIdentifiedByNameNullItemTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement.DirectCollectionWithoutGroupingElementIdentifiedByNameNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement.DirectCollectionWithoutGroupingElementIdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement.DirectCollectionWithoutGroupingElementIntegerWithCommentsTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement.DirectCollectionWithoutGroupingElementListWithUnionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement.DirectCollectionWithoutGroupingElementSchemaTypeSingleNodeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement.DirectCollectionWithoutGroupingElementSchemaTypeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement.DirectCollectionWithoutGroupingElementSingleElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement.DirectCollectionWithoutGroupingElementSingleNodeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyposition.withgroupingelement.DirectCollectionWithGroupingElementIdentifiedByPositionEmptyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyposition.withgroupingelement.DirectCollectionWithGroupingElementIdentifiedByPositionNullTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyposition.withgroupingelement.DirectCollectionWithGroupingElementIdentifiedByPositionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.listoflists.XMLDirectCollectionOfListsTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable.DirectCollectionIsSetNodeNullPolicyTrueTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable.DirectCollectionNillableNodeNullPolicyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable.DirectCollectionOptionalNodeNullPolicyAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.nillable.DirectCollectionOptionalNodeNullPolicyElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.reuse.DirectCollectionReuseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.singlenode.DirectCollectionSingleNodeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.singlenode.xmlattribute.DirectCollectionSingleNodeXMLAttributeMethodAccessingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.singlenode.xmlattribute.DirectCollectionSingleNodeXMLAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.typeattribute.DirectCollectionTypeAttributeTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.union.UnionTestCases;

public class DirectCollectionMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("DirectCollection Mapping Test Suite");

        suite.addTestSuite(DirectCollectionWithGroupingElementIdentifiedByNameTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementIdentifiedByNameIntegerTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementIdentifiedByNameEmptyTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementIdentifiedByNameNullTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementIdentifiedByNameNullItemTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementSingleElementTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementIntegerSingleNodeTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementListWithUnionTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementWithCommentTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementSchemaTypeTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementSchemaTypeSingleNodeTestCases.class);

        suite.addTestSuite(DirectCollectionWithoutGroupingElementIdentifiedByNameTestCases.class);
        suite.addTestSuite(DirectCollectionWithoutGroupingElementIdentifiedByNameIntegerTestCases.class);
        suite.addTestSuite(DirectCollectionWithoutGroupingElementIdentifiedByNameEmptyTestCases.class);
        suite.addTestSuite(DirectCollectionWithoutGroupingElementIdentifiedByNameNullTestCases.class);
        suite.addTestSuite(DirectCollectionWithoutGroupingElementIdentifiedByNameNullItemTestCases.class);
        suite.addTestSuite(DirectCollectionWithoutGroupingElementSingleElementTestCases.class);
        suite.addTestSuite(DirectCollectionWithoutGroupingElementSingleNodeTestCases.class);
        suite.addTestSuite(DirectCollectionWithoutGroupingElementListWithUnionTestCases.class);
        suite.addTestSuite(DirectCollectionWithoutGroupingElementIntegerWithCommentsTestCases.class);
        suite.addTestSuite(DirectCollectionWithoutGroupingElementSchemaTypeTestCases.class);
        suite.addTestSuite(DirectCollectionWithoutGroupingElementSchemaTypeSingleNodeTestCases.class);

        suite.addTestSuite(DirectCollectionWithGroupingElementIdentifiedByPositionTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementIdentifiedByPositionEmptyTestCases.class);
        suite.addTestSuite(DirectCollectionWithGroupingElementIdentifiedByPositionNullTestCases.class);

        suite.addTestSuite(DirectCollectionSingleNodeTestCases.class);
        suite.addTestSuite(DirectCollectionSingleNodeXMLAttributeTestCases.class);

        suite.addTestSuite(DirectCollectionArrayListTestCases.class);
        suite.addTestSuite(DirectCollectionArrayListXMLAttributeTestCases.class);
        suite.addTestSuite(DirectCollectionSingleNodeXMLAttributeMethodAccessingTestCases.class);

        suite.addTest(DirectCollectionTypeAttributeTestCases.suite());
        suite.addTestSuite(DirectCollectionObjectTypeConverterTestCases.class);

        suite.addTestSuite(DirectCollectionErrorTestCases.class);
        suite.addTest(UnionTestCases.suite());

        suite.addTestSuite(XMLDirectCollectionOfListsTestCases.class);

        suite.addTestSuite(DirectCollectionReuseTestCases.class);

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directcollection.DirectCollectionMappingTestSuite" };
        //junit.swingui.TestRunner.main(arguments);
        junit.textui.TestRunner.main(arguments);
    }

}
