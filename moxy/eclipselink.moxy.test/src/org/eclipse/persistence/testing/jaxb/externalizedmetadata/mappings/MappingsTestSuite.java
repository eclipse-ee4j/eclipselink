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
 * dmccann - March 25/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute.AnyAttributeMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anycollection.AnyCollectionMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyobject.AnyObjectMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.collectionreference.CollectionReferenceMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite.CompositeMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.compositecollection.CompositeCollecitonMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct.DirectMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.directcollection.DirectCollectionMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.objectreference.ObjectReferenceMappingTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite for testing MOXy mappings configured via external metadata file.
 *
 */
public class MappingsTestSuite extends TestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("Mappings Test Suite");
        suite.addTestSuite(AnyAttributeMappingTestCases.class);
        suite.addTestSuite(AnyCollectionMappingTestCases.class);
        suite.addTestSuite(AnyObjectMappingTestCases.class);
        suite.addTestSuite(CollectionReferenceMappingTestCases.class);
        suite.addTestSuite(CompositeCollecitonMappingTestCases.class);
        suite.addTestSuite(CompositeMappingTestCases.class);
        suite.addTestSuite(DirectCollectionMappingTestCases.class);
        suite.addTestSuite(DirectMappingTestCases.class);
        suite.addTestSuite(ObjectReferenceMappingTestCases.class);
        return suite;
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.MappingsTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
