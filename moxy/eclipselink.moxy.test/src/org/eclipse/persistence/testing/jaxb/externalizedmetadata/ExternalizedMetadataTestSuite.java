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
// dmccann - June 17/2009 - 2.0 - Initial implementation
// Martin Vojtek - November 14/2014 - Added test suites for XmlIDExtension and XmlValueExtension
package org.eclipse.persistence.testing.jaxb.externalizedmetadata;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.exceptions.ExceptionHandlingTestSuite;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.JAXBContextFactoryTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.MappingsTestSuite;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.MultipleBindingsFourFilesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.MultipleBindingsSimpleTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.superclassoverride.SuperClassOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder.XmlAccessorOrderTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder.packagelevel.XMLAccessorOrderPackageInfoTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder.packagelevel.classoverride.XMLAccessorOrderClassOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder.packagelevel.javaclassoverride.XMLAccessorOrderJavaClassOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.XmlAccessorTypeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.field.XmlAccessorTypeFieldTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.inheritance.XmlAccessorTypeInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.none.XmlAccessorTypeNoneTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.none.XmlAccessorTypeNoneWithPropOrderTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.packagelevel.XmlAccessorTypePackageTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.packagelevel.classoverride.XmlAccessorTypePackageClassOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.packagelevel.javaclassoverride.XmlAccessorTypePackageJavaClassOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.packagelevel.nooverride.XmlAccessorTypePackageNoOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.property.XmlAccessorTypePropertyTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.propertylevel.FieldAccessTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.propertylevel.PropertyAccessTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.propertylevel.UnspecifiedTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.publicmember.XmlAccessorTypePublicMemberTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.AdapterOnClassTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.hexbinary.AdapterHexBinaryTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.list.XmlAdapterListTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.list.XmlAdapterListsTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.negative.XmlAdapterNegativeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.AdapterOnPackageTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.unspecified.XmlAdapterUnspecifiedClassTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.property.AdapterOnPropertyTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.ClassLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.PackageLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.PropertyLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyattribute.XmlAnyAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.XmlAnyElementAdapterListTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.XmlAnyElementAdapterTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.XmlAnyElementBaseLAXFalseTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.XmlAnyElementBaseLAXTrueTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.XmlAnyElementBaseTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.XmlAnyElementDomHandlerTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.XmlAnyElementListTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs.XmlAnyElementWithEltRefsTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs2.XmlAnyElementWithEltRefsNonGlobalTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs3.XmlAnyElementWithEltRefsViaAnnotationTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattachmentref.XmlAttachmentRefCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattribute.XmlAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlclassextractor.XmlClassExtractorTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlcustomizer.XmlCustomizerTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlcustomizer.XmlCustomizerWithOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmldiscriminator.XmlDiscriminatorTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement.XmlElementTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.XmlElementNillablePackageLevelOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.XmlElementNillablePackageLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.XmlElementNillablePackageTypeOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.XmlElementNillableTypeLevelOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.XmlElementNillableTypeLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref.XmlElementRefTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref.XmlElementRefWithWrapperTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs.XmlElementRefsTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelements.XmlElementsTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper.XmlElemenetWrapperNilTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper.XmlElementWrapperDefaultNameTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper.XmlElementWrapperElementOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper.XmlElementWrapperTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum.XmlEnumInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum.XmlEnumQualifiedTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum.XmlEnumTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum.XmlEnumUnqualifiedTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlid.XmlIdExtensionOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref.XmlIdRefExceptionTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref.XmlIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref.XmlIdRefTwoPackagesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlinlinebinarydata.XmlInlineBinaryDataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode.AccessorTypeNoneTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode.XmlJoinNodeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist.XmlListNoStringTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist.XmlListOnXmlAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist.XmlListTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmetadatacomplete.XmlMetadataCompleteTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype.XmlMimeTypeCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmixed.XmlMixedTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.NameTransformerExceptionTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.NameTransformerSimpleTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.NameTransformerTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.XmlMappingDefaultNameTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.XmlMappingSpecifiedNameTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy.XmlNullPolicyPackageLevelOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy.XmlNullPolicyPackageLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy.XmlNullPolicyPackageTypeOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy.XmlNullPolicyTypeLevelOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy.XmlNullPolicyTypeLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlregistry.XmlRegistryNonLocalTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlregistry.XmlRegistryTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlrootelement.XmlRootElementTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.XmlSchemaTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.NamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.linked.LinkedNamespacesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematype.XmlSchemaTypeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematypes.XmlSchemaTypesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlseealso.XmlSeeAlsoTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.XmlTransientPropertyToTransientClassTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.XmlTransientTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.inheritance.XmlTransientInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.splitpackage.SplitPackageTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.unset.classlevel.XmlTransientUnsetClassTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.unset.prop.XmlTransientUnsetPropTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.EmployeeFactoryClassTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.XmlTypeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.factory.FactoryTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.proporder.PropOrderTestSuite;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueCdnPriceInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueCdnPriceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueCdnPricesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueInternationalPriceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueInternationalPricesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueTestCases;
import org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementArrayTestCases;

/**
 * Suite for testing eclipselink-oxm.xml processing.
 *
 */
public class ExternalizedMetadataTestSuite extends TestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("Externalized Metadata Test Suite");
        suite.addTest(MappingsTestSuite.suite());
        suite.addTest(ExceptionHandlingTestSuite.suite());
        suite.addTestSuite(XmlTransientTestCases.class);
        suite.addTestSuite(XmlTransientUnsetClassTestCases.class);
        suite.addTestSuite(XmlTransientInheritanceTestCases.class);
        suite.addTestSuite(XmlTransientPropertyToTransientClassTestCases.class);
        suite.addTestSuite(XmlSeeAlsoTestCases.class);
        suite.addTestSuite(XmlSchemaTestCases.class);
        suite.addTestSuite(NamespaceTestCases.class);
        suite.addTestSuite(NameTransformerTestCases.class);
        suite.addTestSuite(NameTransformerExceptionTestCases.class);
        suite.addTestSuite(NameTransformerSimpleTestCases.class);
        suite.addTestSuite(XmlMappingDefaultNameTestCases.class);
        suite.addTestSuite(XmlMappingSpecifiedNameTestCases.class);
        suite.addTestSuite(XmlRootElementTestCases.class);
        suite.addTestSuite(XmlTypeTestCases.class);
        suite.addTestSuite(EmployeeFactoryClassTestCases.class);
        suite.addTestSuite(XmlAccessorTypeTestCases.class);
        suite.addTestSuite(XmlAccessorTypeFieldTestCases.class);
         suite.addTestSuite(XmlAccessorTypeNoneTestCases.class);
         suite.addTestSuite(XmlAccessorTypeNoneWithPropOrderTestCases.class);
         suite.addTestSuite(XmlAccessorTypeInheritanceTestCases.class);
         suite.addTestSuite(XmlAccessorTypePackageTestCases.class);
         suite.addTestSuite(XmlAccessorTypePackageClassOverrideTestCases.class);
         suite.addTestSuite(XmlAccessorTypePackageJavaClassOverrideTestCases.class);
         suite.addTestSuite(XmlAccessorTypePackageNoOverrideTestCases.class);
         suite.addTestSuite(XmlAccessorTypePropertyTestCases.class);
         suite.addTestSuite(XmlAccessorTypePublicMemberTestCases.class);
        suite.addTestSuite(XmlAccessorOrderTestCases.class);
        suite.addTestSuite(XMLAccessorOrderPackageInfoTestCases.class);
        suite.addTestSuite(XMLAccessorOrderClassOverrideTestCases.class);
        suite.addTestSuite(XMLAccessorOrderJavaClassOverrideTestCases.class);
        suite.addTestSuite(JAXBContextFactoryTestCases.class);
        suite.addTestSuite(XmlElementTestCases.class);
        suite.addTestSuite(AdapterOnClassTestCases.class);
        suite.addTestSuite(AdapterOnPropertyTestCases.class);
        suite.addTestSuite(AdapterHexBinaryTestCases.class);
        suite.addTestSuite(AdapterOnPackageTestCases.class);
        suite.addTestSuite(XmlAdapterNegativeTestCases.class);
        suite.addTestSuite(XmlAdapterUnspecifiedClassTestCases.class);
        suite.addTestSuite(ClassLevelTestCases.class);
        suite.addTestSuite(PackageLevelTestCases.class);
        suite.addTestSuite(PropertyLevelTestCases.class);
        suite.addTestSuite(XmlAttributeTestCases.class);
        suite.addTestSuite(XmlCustomizerTestCases.class);
        suite.addTestSuite(XmlCustomizerWithOverrideTestCases.class);
        suite.addTestSuite(XmlElementWrapperTestCases.class);
        suite.addTestSuite(XmlElemenetWrapperNilTestCases.class);
        suite.addTestSuite(XmlElementWrapperDefaultNameTestCases.class);
        suite.addTestSuite(XmlElementWrapperElementOverrideTestCases.class);
        suite.addTestSuite(XmlValueTestCases.class);
        suite.addTestSuite(XmlValueCdnPriceTestCases.class);
        suite.addTestSuite(XmlValueInternationalPriceTestCases.class);
        suite.addTestSuite(XmlValueInternationalPricesTestCases.class);
        suite.addTestSuite(XmlValueCdnPricesTestCases.class);
        suite.addTestSuite(XmlValueCdnPriceInheritanceTestCases.class);
        suite.addTestSuite(XmlListTestCases.class);
        suite.addTestSuite(XmlListNoStringTestCases.class);
        suite.addTestSuite(XmlListOnXmlAttributeTestCases.class);
        suite.addTestSuite(XmlAnyElementBaseTestCases.class);
        suite.addTestSuite(XmlAnyElementBaseLAXFalseTestCases.class);
        suite.addTestSuite(XmlAnyElementBaseLAXTrueTestCases.class);
        suite.addTestSuite(XmlAnyElementAdapterListTestCases.class);
        suite.addTestSuite(XmlAnyElementAdapterTestCases.class);
        suite.addTestSuite(XmlAnyElementArrayTestCases.class);
        suite.addTestSuite(XmlAnyElementDomHandlerTestCases.class);
        suite.addTestSuite(XmlAnyElementListTestCases.class);
        suite.addTestSuite(XmlAnyElementWithEltRefsTestCases.class);
        suite.addTestSuite(XmlAnyElementWithEltRefsNonGlobalTestCases.class);
        suite.addTestSuite(XmlAnyElementWithEltRefsViaAnnotationTestCases.class);
        suite.addTestSuite(XmlIdRefTestCases.class);
        suite.addTestSuite(XmlIdRefTwoPackagesTestCases.class);
        suite.addTestSuite(XmlIdRefExceptionTestCases.class);
        suite.addTestSuite(XmlMixedTestCases.class);
        suite.addTestSuite(XmlAnyAttributeTestCases.class);
        suite.addTestSuite(XmlMimeTypeCases.class);
        suite.addTestSuite(XmlAttachmentRefCases.class);
        suite.addTestSuite(XmlElementsTestCases.class);
        suite.addTestSuite(XmlElementRefTestCases.class);
        suite.addTestSuite(XmlElementRefWithWrapperTestCases.class);
        suite.addTestSuite(XmlElementRefsTestCases.class);
        suite.addTestSuite(XmlSchemaTypeTestCases.class);
        suite.addTestSuite(XmlSchemaTypesTestCases.class);
        suite.addTestSuite(XmlEnumTestCases.class);
        suite.addTestSuite(XmlEnumInheritanceTestCases.class);
        suite.addTestSuite(XmlEnumUnqualifiedTestCases.class);
        suite.addTestSuite(XmlEnumQualifiedTestCases.class);
        suite.addTestSuite(XmlInlineBinaryDataTestCases.class);
        suite.addTestSuite(XmlRegistryTestCases.class);
        suite.addTestSuite(XmlRegistryNonLocalTestCases.class);
        suite.addTestSuite(XmlClassExtractorTestCases.class);
        suite.addTestSuite(XmlDiscriminatorTestCases.class);
        suite.addTestSuite(XmlJoinNodeTestCases.class);
        suite.addTestSuite(AccessorTypeNoneTestCases.class);
        suite.addTestSuite(XmlMetadataCompleteTestCases.class);
        suite.addTestSuite(XmlAdapterListTestCases.class);
        suite.addTestSuite(XmlAdapterListsTestCases.class);
        suite.addTestSuite(MultipleBindingsSimpleTestCases.class);
        suite.addTestSuite(MultipleBindingsFourFilesTestCases.class);
        suite.addTestSuite(SplitPackageTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.externalizedmetadata.namespace.NamespaceTestCases.class);
        suite.addTestSuite(FactoryTestCases.class);
        suite.addTestSuite(SuperClassOverrideTestCases.class);
        suite.addTest(PropOrderTestSuite.suite());
        suite.addTestSuite(FieldAccessTestCases.class);
        suite.addTestSuite(PropertyAccessTestCases.class);
        suite.addTestSuite(UnspecifiedTestCases.class);
        suite.addTestSuite(XmlTransientUnsetPropTestCases.class);
        suite.addTestSuite(XmlElementNillablePackageLevelOverrideTestCases.class);
        suite.addTestSuite(XmlElementNillablePackageLevelTestCases.class);
        suite.addTestSuite(XmlElementNillablePackageTypeOverrideTestCases.class);
        suite.addTestSuite(XmlElementNillableTypeLevelOverrideTestCases.class);
        suite.addTestSuite(XmlElementNillableTypeLevelTestCases.class);
        suite.addTestSuite(XmlNullPolicyPackageLevelOverrideTestCases.class);
        suite.addTestSuite(XmlNullPolicyPackageLevelTestCases.class);
        suite.addTestSuite(XmlNullPolicyPackageTypeOverrideTestCases.class);
        suite.addTestSuite(XmlNullPolicyTypeLevelOverrideTestCases.class);
        suite.addTestSuite(XmlNullPolicyTypeLevelTestCases.class);
        suite.addTestSuite(XmlIdExtensionOverrideTestCases.class);
        suite.addTest(new JUnit4TestAdapter(LinkedNamespacesTestCases.class));
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }
}
