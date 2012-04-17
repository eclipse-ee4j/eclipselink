/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - August 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json;

import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributeNoXmlRootElementIncludeRootFalseTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributeNoXmlRootElementInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributeNoXmlRootElementJAXBElementTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributeNoXmlRootElementTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributePrefixEmptyStringTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributePrefixOnContextTestCases;
import org.eclipse.persistence.testing.jaxb.json.attribute.JSONAttributePrefixOnMarshallerTestCases;
import org.eclipse.persistence.testing.jaxb.json.characters.EscapeCharactersTestCases;
import org.eclipse.persistence.testing.jaxb.json.characters.UsAsciiTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.DifferentNamespacesTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.NamespaceInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.NamespacesOnContextTestCases;
import org.eclipse.persistence.testing.jaxb.json.namespaces.NamespacesOnUnmarshalOnlyTestCases;
import org.eclipse.persistence.testing.jaxb.json.norootelement.IncludeRootFalseWithXMLRootElementTestCases;
import org.eclipse.persistence.testing.jaxb.json.norootelement.IncludeRootTrueWithXMLRootElementTestCases;
import org.eclipse.persistence.testing.jaxb.json.norootelement.NoRootElementNSTestCases;
import org.eclipse.persistence.testing.jaxb.json.norootelement.NoRootElementTestCases;
import org.eclipse.persistence.testing.jaxb.json.rootlevellist.RootLevelListTestCases;
import org.eclipse.persistence.testing.jaxb.json.xmlvalue.XMLValuePropDifferentTestCases;
import org.eclipse.persistence.testing.jaxb.json.xmlvalue.XMLValuePropTestCases;
import org.eclipse.persistence.testing.oxm.xmlconversionmanager.NumberTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JSONTestSuite extends TestSuite {
    public static Test suite() {
    	  TestSuite suite = new TestSuite("JSONTestSuite");
          suite.addTestSuite(JSONAttributePrefixOnContextTestCases.class);
          suite.addTestSuite(JSONAttributePrefixEmptyStringTestCases.class);
          suite.addTestSuite(JSONAttributePrefixOnMarshallerTestCases.class);
          suite.addTestSuite(JSONAttributeNoXmlRootElementTestCases.class);
          suite.addTestSuite(JSONAttributeNoXmlRootElementIncludeRootFalseTestCases.class);
          suite.addTestSuite(JSONAttributeNoXmlRootElementInheritanceTestCases.class);
          suite.addTestSuite(JSONAttributeNoXmlRootElementJAXBElementTestCases.class);
          suite.addTestSuite(DifferentNamespacesTestCases.class);
          suite.addTestSuite(NamespacesOnContextTestCases.class);
          suite.addTestSuite(NamespacesOnUnmarshalOnlyTestCases.class);
          suite.addTestSuite(NoRootElementTestCases.class);
          suite.addTestSuite(NoRootElementNSTestCases.class);
          suite.addTestSuite(NamespaceInheritanceTestCases.class);
          suite.addTestSuite(IncludeRootFalseWithXMLRootElementTestCases.class);
          suite.addTestSuite(IncludeRootTrueWithXMLRootElementTestCases.class);
          suite.addTestSuite(XMLValuePropTestCases.class);
          suite.addTestSuite(XMLValuePropDifferentTestCases.class);
          suite.addTestSuite(NumberTestCases.class);
          suite.addTestSuite(EscapeCharactersTestCases.class);
          suite.addTestSuite(UsAsciiTestCases.class);
          suite.addTest(RootLevelListTestCases.suite());
          return suite;
	}
}
	