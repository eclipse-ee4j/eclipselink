/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.collections.CollectionsTestSuite;
import org.eclipse.persistence.testing.jaxb.cycle.CycleRecoverableTestCases;
import org.eclipse.persistence.testing.jaxb.defaultvalue.DefaultValueTestSuite;
import org.eclipse.persistence.testing.jaxb.employee.JAXBEmployeeNSTestCases;
import org.eclipse.persistence.testing.jaxb.employee.JAXBEmployeeNoWrapperTestCases;
import org.eclipse.persistence.testing.jaxb.employee.JAXBEmployeeTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex.JAXBContextByClassArrayWithIndexTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex.JAXBContextByClassArrayWithRefInBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex.JAXBContextByClassArrayWithRefTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex.JAXBContextByPackageWithIndexTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbintrospector.elementname.JAXBIntrospectorGetElementNameTestCases;
import org.eclipse.persistence.testing.jaxb.prefixmapper.PrefixMapperMapTestCases;
import org.eclipse.persistence.testing.jaxb.prefixmapper.PrefixMapperTestCases;
import org.eclipse.persistence.testing.jaxb.properties.PropertyTestCases;
import org.eclipse.persistence.testing.jaxb.readonly.ReadAndWriteOnlyTestCases;
import org.eclipse.persistence.testing.jaxb.refresh.RefreshTestSuite;
import org.eclipse.persistence.testing.jaxb.stax.XMLStreamWriterDefaultNamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.DefaultValueTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.RepeatedUnmarshalTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.space.UnmarshalWithSpaceEventTestCases;
import org.eclipse.persistence.testing.jaxb.uri.ChildURITestCases;
import org.eclipse.persistence.testing.jaxb.uri.URITestCases;
import org.eclipse.persistence.testing.jaxb.xmlbindings.XMLBindingsPopulatedTestCases;
import org.eclipse.persistence.testing.jaxb.xmlbindings.XMLBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlbindings.XMLBindingsWithExternalMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.xmldecriptor.LazyInitTestCases;
import org.eclipse.persistence.testing.jaxb.xmlmixed.XmlMixedTestCases;
import org.eclipse.persistence.testing.jaxb.xmlpath.XmlPathToAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.xmlpath.XmlPathToElementWithXmlAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.xmlpath.XmlPathWithXmlAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.xmlschema.XMLSchemaModelTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods.proporder.PropOrderTestCases;

public class JAXBTestSuite3 extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite3");
		
        suite.addTest(org.eclipse.persistence.testing.jaxb.singleobject.JAXBSingleObjectTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.classloader.DifferentClassLoaderTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.classloader.InnerClassTestCases.class);

        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.helper.JAXBHelperTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.any.AnyWithJAXBElementTestCases.class);
        
        suite.addTest(org.eclipse.persistence.testing.jaxb.substitution.SubstitutionTestSuite.suite());

        suite.addTest(org.eclipse.persistence.testing.jaxb.innerclasses.InnerClassTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.interfaces.InterfaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.MarshalSchemaValidationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.NoSchemaRefTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.UnmarshalSchemaValidationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.UnmarshallerNullTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.NoSchemaRefTestCases.class);
        suite.addTestSuite(XMLStreamWriterDefaultNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.qname.QNameTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.qname.defaultnamespace.QNameTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator.AnyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator.AnyCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator.ElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator.ElementCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.XmlTypeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.XmlTypeNameTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.proporder.NonTransientTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.proporder.TransientTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlaccessortype.none.NoneTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlaccessortype.FieldAndPropertyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods.XmlVirtualAccessMethodsTestCases.class);
        suite.addTest(RefreshTestSuite.suite());
        
        suite.addTestSuite(JAXBContextByClassArrayWithIndexTestCases.class);
        suite.addTestSuite(JAXBContextByClassArrayWithRefTestCases.class);
        suite.addTestSuite(JAXBContextByClassArrayWithRefInBindingsTestCases.class);
        suite.addTestSuite(JAXBContextByPackageWithIndexTestCases.class);
        
        suite.addTestSuite(PropOrderTestCases.class);
        suite.addTestSuite(LazyInitTestCases.class);
        suite.addTestSuite(XmlMixedTestCases.class);
        suite.addTestSuite(XmlPathToElementWithXmlAttributeTestCases.class);
        suite.addTestSuite(XmlPathToAttributeTestCases.class);
        suite.addTestSuite(XmlPathWithXmlAttributeTestCases.class);
        suite.addTestSuite(RepeatedUnmarshalTestCases.class);
        suite.addTestSuite(DefaultValueTestCases.class);
        suite.addTestSuite(ReadAndWriteOnlyTestCases.class);
        suite.addTestSuite(XMLBindingsTestCases.class);
        suite.addTestSuite(XMLBindingsPopulatedTestCases.class);
        suite.addTestSuite(XMLBindingsWithExternalMetadataTestCases.class);
        suite.addTestSuite(XMLSchemaModelTestCases.class);
        suite.addTestSuite(JAXBIntrospectorGetElementNameTestCases.class);
        suite.addTest(CollectionsTestSuite.suite());
        suite.addTest(DefaultValueTestSuite.suite());
        suite.addTestSuite(UnmarshalWithSpaceEventTestCases.class);
        suite.addTestSuite(PrefixMapperTestCases.class);
        suite.addTestSuite(PrefixMapperMapTestCases.class);
        suite.addTestSuite(ChildURITestCases.class);
        suite.addTestSuite(URITestCases.class);
        suite.addTestSuite(PropertyTestCases.class);        
        suite.addTest(SunCompatibilityTestSuite.suite());
        
        suite.addTestSuite(JAXBEmployeeTestCases.class);
        suite.addTestSuite(JAXBEmployeeNoWrapperTestCases.class);
        suite.addTestSuite(JAXBEmployeeNSTestCases.class);
        suite.addTest(org.eclipse.persistence.testing.jaxb.eventhandler.EventHandlerTestSuite.suite());
        
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite3" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}