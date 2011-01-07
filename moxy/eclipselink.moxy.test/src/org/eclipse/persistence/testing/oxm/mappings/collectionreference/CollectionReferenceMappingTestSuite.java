/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-10-06 14:57:58 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.collectionreference;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.mappings.collectionreference.reuse.CollectionReferenceReuseTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.errortests.CompositeCollectionErrorTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.IdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.IdentifiedByNamespaceTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyposition.IdentifiedByPositionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.keepaselement.CompositeCollectionKeepUnknownAsElementTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.map.CompositeCollectionMapNullChildTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.map.CompositeCollectionMapTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.mappingxpathcollision.MappingXpathCollisionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.nested.CompositeCollectionNestedTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.setmethod.SetMethodTestCases;

public class CollectionReferenceMappingTestSuite extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("Collection Reference Mapping Test Suite");

        suite.addTestSuite(CollectionReferenceReuseTestCases.class);

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.collectionreference.CollectionReferenceMappingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

}