/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.annotations.xmlaccessmethods.XmlAccessMethodsTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlclassextractor.XmlClassExtractorTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlelementnillable.XmlElementNillableFieldLevelOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlelementnillable.XmlElementNillablePackageLevelTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlelementnillable.XmlElementNillableTypeLevelOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlelementnillable.XmlElementNillableTypeLevelTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlidref.XmlIdRefMissingIdEventHandlerTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlidref.XmlIdRefMissingIdTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlidref.self.XmlIdRefSelfTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata.InlineHexBinaryTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference.InverseRefChoiceAdapterTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference.InverseReferenceAdapterTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference.InverseReferenceWithRefTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmllocation.XmlLocationTestSuite;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer.JAXBDefaultNameTransformerTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer.classlevel.upper.JAXBClassLevelUpperNameTransformerTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer.upper.JAXBUpperNameTransformerTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy.DefaultNoNodeTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy.XmlNullPolicyNoXmlElementTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy.XmlNullPolicyPackageLevelTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy.XmlNullPolicyTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy.XmlNullPolicyTypeLevelOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy.XmlNullPolicyTypeLevelTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.PredicateTestSuite;
import org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.adapter.CustomerTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlpaths.XmlPathsTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlpaths.override.XmlPathsOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlproperty.XmlPropertyTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.SimpleRootTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.XMLTransformationNoArgCtorTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.XMLTransformationNoArgCtorXMLBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.XmlTransformationMethodTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmltransformation.XmlTransformationTestCases;



public class AnnotationsTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite");

        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.XmlPathTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.XmlPathOverrideTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.XmlPathUnmappedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.schematype.SchemaTypeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.self.SelfTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.self.SelfWithEventHandlerTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.attributecollection.XmlPathCollectionAttributeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlpath.attributecollection.XmlPathCollectionRefAttributeTestCases.class);
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
        suite.addTestSuite(DefaultNoNodeTestCases.class);
        suite.addTestSuite(XmlNullPolicyNoXmlElementTestCases.class);
        suite.addTestSuite(XmlNullPolicyPackageLevelTestCases.class);
        suite.addTestSuite(XmlNullPolicyTypeLevelTestCases.class);
        suite.addTestSuite(XmlNullPolicyTypeLevelOverrideTestCases.class);
        suite.addTestSuite(XmlElementNillablePackageLevelTestCases.class);
        suite.addTestSuite(XmlElementNillableTypeLevelTestCases.class);
        suite.addTestSuite(XmlElementNillableTypeLevelOverrideTestCases.class);
        suite.addTestSuite(XmlElementNillableFieldLevelOverrideTestCases.class);
        suite.addTestSuite(JAXBDefaultNameTransformerTestCases.class);
        suite.addTestSuite(JAXBUpperNameTransformerTestCases.class);
        suite.addTestSuite(JAXBClassLevelUpperNameTransformerTestCases.class);
        suite.addTestSuite(XmlAccessMethodsTestCases.class);
        suite.addTestSuite(XmlClassExtractorTestCases.class);
        suite.addTestSuite(XmlPropertyTestCases.class);
        suite.addTestSuite(XmlTransformationTestCases.class);
        suite.addTestSuite(XMLTransformationNoArgCtorTestCases.class);
        suite.addTestSuite(XMLTransformationNoArgCtorXMLBindingsTestCases.class);
        suite.addTestSuite(XmlTransformationMethodTestCases.class);
        suite.addTestSuite(SimpleRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlcontainerproperty.ContainerPropertyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.PropertyTypeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.required.RequiredAnnotationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmldiscriminator.XmlDiscriminatorTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmldiscriminator.ns.XmlDiscriminatorNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmldiscriminator.ns.XmlDiscriminatorRootNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode.XmlJoinNodeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode.xmlvalue.XmlJoinNodesWithValueTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.XmlElementsJoinNodeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.collection.XmlElementsJoinNodeTestCases.class);
        suite.addTest(PredicateTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlinlinebinarydata.XmlInlineBinaryDataTestCases.class);
        suite.addTestSuite(InlineHexBinaryTestCases.class);
        suite.addTestSuite(XmlIdRefMissingIdTestCases.class);
        suite.addTestSuite(XmlIdRefMissingIdEventHandlerTestCases.class);
        suite.addTestSuite(XmlIdRefSelfTestCases.class);
        suite.addTest(org.eclipse.persistence.testing.jaxb.annotations.xmltransient.XmlTransientTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.qualified.QualfiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.noxmlrootelement.NoRootElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.qname.XmlElementDeclQNameTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.qname.XmlElementDeclQNameNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.emptystringns.EmptyStringNSTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.unqualified.UnqualfiedTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.xsitype.XsiTypeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.xsitype2.FooBarXsiTypeTestCases.class);
        suite.addTest(XmlLocationTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.XmlValueTestSuite.suite());
        suite.addTestSuite(CustomerTestCases.class);
        suite.addTestSuite(InverseReferenceWithRefTestCases.class);
        suite.addTestSuite(InverseReferenceAdapterTestCases.class);
        suite.addTestSuite(InverseRefChoiceAdapterTestCases.class);
        suite.addTest(new JUnit4TestAdapter(org.eclipse.persistence.testing.jaxb.annotations.xmlschema.XmlSchemaTestCases.class));

        return suite;
    }

}
