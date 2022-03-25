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

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.JAXBContextFactoryTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.AdapterOnClassTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.hexbinary.AdapterHexBinaryTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.negative.XmlAdapterNegativeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.AdapterOnPackageTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.unspecified.XmlAdapterUnspecifiedClassTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.property.AdapterOnPropertyTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.ClassLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.PackageLevelTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.PropertyLevelTestCases;
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
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattribute.XmlAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlcustomizer.XmlCustomizerTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlcustomizer.XmlCustomizerWithOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement.XmlElementTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper.XmlElemenetWrapperNilTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper.XmlElementWrapperDefaultNameTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper.XmlElementWrapperElementOverrideTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper.XmlElementWrapperTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref.XmlIdRefExceptionTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref.XmlIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref.XmlIdRefTwoPackagesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist.XmlListNoStringTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist.XmlListOnXmlAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmllist.XmlListTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueCdnPriceInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueCdnPriceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueCdnPricesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueInternationalPriceTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueInternationalPricesTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.XmlValueTestCases;
import org.eclipse.persistence.testing.jaxb.xmlanyelement.XmlAnyElementArrayTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite for testing eclipselink-oxm.xml processing.
 *
 */
public class ExternalizedMetadataTestSuite2 extends TestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("Externalized Metadata Test Suite 2");
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
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestSuite2" };
        junit.textui.TestRunner.main(arguments);
    }
}
