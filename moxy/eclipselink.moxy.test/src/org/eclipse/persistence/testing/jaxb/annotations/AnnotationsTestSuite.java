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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations;

import org.eclipse.persistence.testing.jaxb.annotations.xmlaccessmethods.XmlAccessMethodsTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlclassextractor.XmlClassExtractorTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer.JAXBDefaultNameTransformerTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer.upper.JAXBUpperNameTransformerTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy.XmlNullPolicyTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.PredicateTestSuite;
import org.eclipse.persistence.testing.jaxb.annotations.xmlpaths.XmlPathsTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlpaths.override.XmlPathsOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlproperty.XmlPropertyTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.XMLTransformationNoArgCtorTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.XMLTransformationNoArgCtorXMLBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.XmlTransformationMethodTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.XmlTransformationTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AnnotationsTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite");

        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.XmlPathTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.XmlPathOverrideTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.XmlPathUnmappedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.schematype.SchemaTypeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.self.SelfTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.attributecollection.XmlPathCollectionAttributeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.cdata.XmlCDATATestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.cdata.XmlCDATAOverrideTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.cdata.UnmappedCDATATestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlwriteonly.XmlWriteOnlyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlwriteonly.XmlWriteOnlyOverrideTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlreadonly.XmlReadOnlyOverrideTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlreadonly.XmlReadOnlyOverrideTestCases.class);
        suite.addTestSuite(XmlPathsTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.enumtype.EnumTestCases.class);
        suite.addTestSuite(XmlPathsOverrideTestCases.class);
        suite.addTestSuite(XmlNullPolicyTestCases.class);
        suite.addTestSuite(JAXBDefaultNameTransformerTestCases.class);
        suite.addTestSuite(JAXBUpperNameTransformerTestCases.class);
        suite.addTestSuite(XmlAccessMethodsTestCases.class);
        suite.addTestSuite(XmlClassExtractorTestCases.class);
        suite.addTestSuite(XmlPropertyTestCases.class);
        suite.addTestSuite(XmlTransformationTestCases.class);
        suite.addTestSuite(XMLTransformationNoArgCtorTestCases.class);
        suite.addTestSuite(XMLTransformationNoArgCtorXMLBindingsTestCases.class);
        suite.addTestSuite(XmlTransformationMethodTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlcontainerproperty.ContainerPropertyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.PropertyTypeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.required.RequiredAnnotationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmldiscriminator.XmlDiscriminatorTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode.XmlJoinNodeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode.xmlvalue.XmlJoinNodesWithValueTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.XmlElementsJoinNodeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.collection.XmlElementsJoinNodeTestCases.class);
        suite.addTest(PredicateTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata.XmlInlineBinaryDataTestCases.class);
        suite.addTest(org.eclipse.persistence.testing.jaxb.annotations.xmltransient.XmlTransientTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.qualified.QualfiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.unqualified.UnqualfiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmllocation.XmlLocationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmllocation.XmlLocationNonTransientTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmllocation.XmlLocationErrorTestCases.class);

        return suite;
    }

}