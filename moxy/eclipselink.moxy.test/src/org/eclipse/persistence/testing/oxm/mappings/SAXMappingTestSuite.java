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
package org.eclipse.persistence.testing.oxm.mappings;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.anycollection.XMLAnyCollectionMappingTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.XMLAnyObjectMappingTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.CompositeCollectionMappingTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.CompositeObjectMappingTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.binarydata.XMLBinaryDataMappingTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.XMLBinaryDataCollectionMappingTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.DirectCollectionMappingTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.DirectToFieldMappingTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.namespaces.NamespaceTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.namespaces.identifiedbyname.IdentifiedByNameNamespaceTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.simpletypes.SimpleTypeMappingTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.transformation.TransformationMappingTestSuite;
import org.eclipse.persistence.testing.oxm.mappings.typeddirect.TypedDirectMappingTestSuite;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshallerTestSuite;

public class SAXMappingTestSuite extends TestCase {
    public SAXMappingTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.SAXMappingTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("SAX Mapping Test Suite");

                suite.addTest(DirectToFieldMappingTestSuite.suite());
                suite.addTest(CompositeObjectMappingTestSuite.suite());
                suite.addTest(CompositeCollectionMappingTestSuite.suite());
                suite.addTest(DirectCollectionMappingTestSuite.suite());
                suite.addTest(TransformationMappingTestSuite.suite());
                suite.addTest(TypedDirectMappingTestSuite.suite());
                suite.addTest(SimpleTypeMappingTestSuite.suite());
                suite.addTest(NamespaceTestSuite.suite());
                suite.addTest(IdentifiedByNameNamespaceTestSuite.suite());
                suite.addTest(XMLMarshallerTestSuite.suite());
                suite.addTest(XMLAnyObjectMappingTestSuite.suite());
                suite.addTest(XMLAnyCollectionMappingTestSuite.suite());

                // See B5259059: NPE on anyObject mapping XPath null, anyCollection mapping XPath=filled
                // The use cases are described in the doc b5259059_jaxb_factory_npe_DesignSpec_v2006nnnn.doc
                //suite.addTest(XMLAnyObjectAndAnyCollectionMappingTestSuite.suite());

                //suite.addTestSuite(XMLMarshalTestCases.class);
                //suite.addTestSuite(XMLUnmarshalTestCases.class);
                //suite.addTestSuite(XMLMarshallerEncodingTestCases.class);
                //suite.addTestSuite(XMLMarshallerFormattingTestCases.class);
                suite.addTestSuite(XMLBinaryDataMappingTestSuite.class);
                suite.addTestSuite(XMLBinaryDataCollectionMappingTestSuite.class);
        
        return suite;
    }
}
