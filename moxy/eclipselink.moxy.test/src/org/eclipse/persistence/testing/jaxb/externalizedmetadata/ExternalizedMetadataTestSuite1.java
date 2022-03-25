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

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.exceptions.ExceptionHandlingTestSuite;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.MappingsTestSuite;
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
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.publicmember.XmlAccessorTypePublicMemberTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.NameTransformerExceptionTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.NameTransformerSimpleTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.NameTransformerTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.XmlMappingDefaultNameTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer.XmlMappingSpecifiedNameTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlrootelement.XmlRootElementTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.XmlSchemaTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.NamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlseealso.XmlSeeAlsoTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.XmlTransientPropertyToTransientClassTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.XmlTransientTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.inheritance.XmlTransientInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.unset.classlevel.XmlTransientUnsetClassTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.EmployeeFactoryClassTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.XmlTypeTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite for testing eclipselink-oxm.xml processing.
 *
 */
public class ExternalizedMetadataTestSuite1 extends TestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("Externalized Metadata Test Suite 1");
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
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestSuite1" };
        junit.textui.TestRunner.main(arguments);
    }
}
