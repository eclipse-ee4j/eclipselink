/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.MultipleBindingsFourFilesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.multiplebindings.MultipleBindingsSimpleTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.superclassoverride.SuperClassOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.propertylevel.FieldAccessTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.propertylevel.PropertyAccessTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.propertylevel.UnspecifiedTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.list.XmlAdapterListTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.list.XmlAdapterListsTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyattribute.XmlAnyAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattachmentref.XmlAttachmentRefCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlclassextractor.XmlClassExtractorTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmldiscriminator.XmlDiscriminatorTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.XmlElementNillablePackageLevelOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.XmlElementNillablePackageLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.XmlElementNillablePackageTypeOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.XmlElementNillableTypeLevelOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementnillable.XmlElementNillableTypeLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref.XmlElementRefTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref.XmlElementRefWithWrapperTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs.XmlElementRefsTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelements.XmlElementsTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum.XmlEnumInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum.XmlEnumQualifiedTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum.XmlEnumTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum.XmlEnumUnqualifiedTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlid.XmlIdExtensionOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlinlinebinarydata.XmlInlineBinaryDataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode.AccessorTypeNoneTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode.XmlJoinNodeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmetadatacomplete.XmlMetadataCompleteTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmimetype.XmlMimeTypeCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmixed.XmlMixedTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy.XmlNullPolicyPackageLevelOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy.XmlNullPolicyPackageLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy.XmlNullPolicyPackageTypeOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy.XmlNullPolicyTypeLevelOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnullpolicy.XmlNullPolicyTypeLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlregistry.XmlRegistryNonLocalTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlregistry.XmlRegistryTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.linked.LinkedNamespacesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematype.XmlSchemaTypeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematypes.XmlSchemaTypesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.splitpackage.SplitPackageTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.unset.prop.XmlTransientUnsetPropTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.factory.FactoryTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.proporder.PropOrderTestSuite;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite for testing eclipselink-oxm.xml processing.
 *
 */
public class ExternalizedMetadataTestSuite3 extends TestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("Externalized Metadata Test Suite 3");
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
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestSuite3" };
        junit.textui.TestRunner.main(arguments);
    }
}
