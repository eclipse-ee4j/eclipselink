/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - August 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.json;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.json.adapter.JsonMapAdapterTestCases;
import org.eclipse.persistence.testing.jaxb.json.any.AnyTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributeNoXmlRootElementIncludeRootFalseTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributeNoXmlRootElementInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributeNoXmlRootElementJAXBElementTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributeNoXmlRootElementTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributePrefixEmptyStringTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributePrefixOnContextTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributePrefixOnMarshallerTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.SimpleBeanAttrNullTestCases;
import org.eclipse.persistence.testing.jaxb.json.characters.EscapeCharactersTestCases;
import org.eclipse.persistence.testing.jaxb.json.characters.UTF8TestCases;
import org.eclipse.persistence.testing.jaxb.json.characters.UsAsciiTestCases;
import org.eclipse.persistence.testing.jaxb.json.emptyroot.EmptyNullMarshalUnmarshalTestCases;
import org.eclipse.persistence.testing.jaxb.json.multiline.MultiLineStringTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.DifferentNamespacesTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.NamespaceInheritanceSeparatorContextTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.NamespaceInheritanceSeparatorTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.NamespaceInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.NamespacesOnContextTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.NamespacesOnUnmarshalOnlyTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.SeparatorInNameTestCases;
import org.eclipse.persistence.testing.jaxb.json.nil.NilElementsUsageTestCases;
import org.eclipse.persistence.testing.jaxb.json.norootelement.IncludeRootFalseWithXMLRootElementTestCases;
import org.eclipse.persistence.testing.jaxb.json.norootelement.IncludeRootTrueWithXMLRootElementTestCases;
import org.eclipse.persistence.testing.jaxb.json.norootelement.InheritanceNoRootTestCases;
import org.eclipse.persistence.testing.jaxb.json.norootelement.NoRootElementNSTestCases;
import org.eclipse.persistence.testing.jaxb.json.norootelement.NoRootElementTestCases;
import org.eclipse.persistence.testing.jaxb.json.padding.JSONWithPaddingTestCases;
import org.eclipse.persistence.testing.jaxb.json.rootlevellist.RootLevelListTestCases;
import org.eclipse.persistence.testing.jaxb.json.type.TypeNameValueTestCases;
import org.eclipse.persistence.testing.jaxb.json.type.TypePrefixTestCases;
import org.eclipse.persistence.testing.jaxb.json.type.TypePropertyInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.json.type.TypePropertyTestCases;
import org.eclipse.persistence.testing.jaxb.json.unmapped.JsonUnmappedTestCases;
import org.eclipse.persistence.testing.jaxb.json.wrapper.AllWrapperTestCases;
import org.eclipse.persistence.testing.jaxb.json.xmlvalue.XMLValuePropDifferentTestCases;
import org.eclipse.persistence.testing.jaxb.json.xmlvalue.XMLValuePropTestCases;
import org.eclipse.persistence.testing.oxm.xmlconversionmanager.NumberTestCases;

public class JSONTestSuite extends TestSuite {
    public static Test suite() {
      TestSuite suite = new TestSuite("JSONTestSuite");
          suite.addTestSuite(JSONAttributePrefixOnContextTestCases.class);
          suite.addTestSuite(JSONAttributePrefixEmptyStringTestCases.class);
          suite.addTestSuite(JSONAttributePrefixOnMarshallerTestCases.class);
          suite.addTestSuite(JSONAttributeNoXmlRootElementTestCases.class);
          suite.addTestSuite(JSONAttributeNoXmlRootElementIncludeRootFalseTestCases.class);
          suite.addTestSuite(JsonMapAdapterTestCases.class);
          suite.addTestSuite(JSONAttributeNoXmlRootElementInheritanceTestCases.class);
          suite.addTestSuite(JSONAttributeNoXmlRootElementJAXBElementTestCases.class);
          suite.addTestSuite(SimpleBeanAttrNullTestCases.class);
          suite.addTestSuite(DifferentNamespacesTestCases.class);
          suite.addTestSuite(NamespacesOnContextTestCases.class);
          suite.addTestSuite(NamespacesOnUnmarshalOnlyTestCases.class);
          suite.addTestSuite(NoRootElementTestCases.class);
          suite.addTestSuite(NoRootElementNSTestCases.class);
          suite.addTestSuite(NamespaceInheritanceTestCases.class);
          suite.addTestSuite(NamespaceInheritanceSeparatorTestCases.class);
          suite.addTestSuite(NamespaceInheritanceSeparatorContextTestCases.class);
          suite.addTestSuite(NilElementsUsageTestCases.class);
          suite.addTestSuite(SeparatorInNameTestCases.class);
          suite.addTestSuite(IncludeRootFalseWithXMLRootElementTestCases.class);
          suite.addTestSuite(IncludeRootTrueWithXMLRootElementTestCases.class);
          suite.addTestSuite(XMLValuePropTestCases.class);
          suite.addTestSuite(XMLValuePropDifferentTestCases.class);
          suite.addTestSuite(NumberTestCases.class);
          suite.addTestSuite(EscapeCharactersTestCases.class);
          suite.addTestSuite(UsAsciiTestCases.class);
          suite.addTestSuite(UTF8TestCases.class);
          suite.addTest(RootLevelListTestCases.suite());
          suite.addTestSuite(EmptyNullMarshalUnmarshalTestCases.class);
          suite.addTestSuite(InheritanceNoRootTestCases.class);
          suite.addTest(JSONWithPaddingTestCases.suite());
          suite.addTest(AnyTestCases.suite());
          suite.addTest(AllWrapperTestCases.suite());
          suite.addTestSuite(MultiLineStringTestCases.class);
          suite.addTestSuite(TypeNameValueTestCases.class);
          suite.addTestSuite(TypePrefixTestCases.class);
          suite.addTestSuite(TypePropertyInheritanceTestCases.class);
          suite.addTestSuite(TypePropertyTestCases.class);
          suite.addTestSuite(JsonUnmappedTestCases.class);

          return suite;
    }
}

