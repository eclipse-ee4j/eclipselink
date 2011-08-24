/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute.AnyAttributeReadOnlyMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyattribute.AnyAttributeWriteOnlyMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anycollection.AnyCollectionMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anycollection.AnyCollectionReadOnlyMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anycollection.AnyCollectionWriteOnlyMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyobject.AnyObjectMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyobject.AnyObjectReadOnlyMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.anyobject.AnyObjectWriteOnlyMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.binarydata.BinaryDataMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.binarydatacollection.BinaryDataCollectionMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.ChoiceMappingEmployeeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.ChoiceMappingWithJoinNodesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choicecollection.ChoiceCollectionMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.collectionreference.CollectionReferenceMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.collectionreference.CollectionReferenceReadOnlyTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.collectionreference.CollectionReferenceWriteOnlyTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.composite.CompositeMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.compositecollection.CompositeCollecitonMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct.DirectMappingTeamTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct.DirectMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct.DirectMappingVehicleTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct.DirectMappingXmlValueTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.directcollection.DirectCollectionMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.multiple.MultipleMappingPerFieldTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.objectreference.ObjectReferenceMappingReadOnlyTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.objectreference.ObjectReferenceMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.objectreference.ObjectReferenceMappingWriteOnlyTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmlinversereference.XmlInverseReferenceMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation.JAXBContextTransformationMappingTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation.MethodSetOnTypeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation.XmlTransformationTestCases;

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
        suite.addTestSuite(AnyAttributeReadOnlyMappingTestCases.class);
        suite.addTestSuite(AnyAttributeWriteOnlyMappingTestCases.class);
        suite.addTestSuite(AnyCollectionMappingTestCases.class);
        suite.addTestSuite(AnyCollectionReadOnlyMappingTestCases.class); 
        suite.addTestSuite(AnyCollectionWriteOnlyMappingTestCases.class);
        suite.addTestSuite(AnyObjectMappingTestCases.class);
        suite.addTestSuite(AnyObjectReadOnlyMappingTestCases.class);
        suite.addTestSuite(AnyObjectWriteOnlyMappingTestCases.class);
        suite.addTestSuite(BinaryDataMappingTestCases.class);
        suite.addTestSuite(BinaryDataCollectionMappingTestCases.class);        
        suite.addTestSuite(ChoiceMappingEmployeeTestCases.class);
        suite.addTestSuite(ChoiceMappingWithJoinNodesTestCases.class);
        suite.addTestSuite(ChoiceCollectionMappingTestCases.class);
        suite.addTestSuite(CollectionReferenceMappingTestCases.class);
        suite.addTestSuite(CollectionReferenceReadOnlyTestCases.class);
        suite.addTestSuite(CollectionReferenceWriteOnlyTestCases.class);
        suite.addTestSuite(CompositeCollecitonMappingTestCases.class);
        suite.addTestSuite(CompositeMappingTestCases.class);
        suite.addTestSuite(DirectCollectionMappingTestCases.class);
        suite.addTestSuite(DirectMappingTestCases.class);
        suite.addTestSuite(DirectMappingTeamTestCases.class);
        suite.addTestSuite(DirectMappingXmlValueTestCases.class);
        suite.addTestSuite(DirectMappingVehicleTestCases.class);
        suite.addTestSuite(MultipleMappingPerFieldTestCases.class);
        suite.addTestSuite(ObjectReferenceMappingTestCases.class);
        suite.addTestSuite(ObjectReferenceMappingReadOnlyTestCases.class);
        suite.addTestSuite(ObjectReferenceMappingWriteOnlyTestCases.class);
        suite.addTestSuite(XmlInverseReferenceMappingTestCases.class);
        suite.addTestSuite(XmlTransformationTestCases.class);
        suite.addTestSuite(MethodSetOnTypeTestCases.class);
        suite.addTestSuite(JAXBContextTransformationMappingTestCases.class);
        return suite;
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.MappingsTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
