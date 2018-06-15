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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.interfaces.xmltransient.InvalidTransientInterfaceTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.duplicateelem.ObjectFactoryTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex.JAXBContextByClassArrayWithIndexTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex.JAXBContextByClassArrayWithRefInBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex.JAXBContextByClassArrayWithRefTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex.JAXBContextByPackageWithIndexTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbintrospector.elementname.JAXBIntrospectorGetElementNameTestCases;
import org.eclipse.persistence.testing.jaxb.prefixmapper.DefaultNSPrefixMapperSimpleTestCases;
import org.eclipse.persistence.testing.jaxb.prefixmapper.PrefixMapperContextTestCases;
import org.eclipse.persistence.testing.jaxb.prefixmapper.PrefixMapperMapTestCases;
import org.eclipse.persistence.testing.jaxb.prefixmapper.PrefixMapperPackageInfoTestCases;
import org.eclipse.persistence.testing.jaxb.prefixmapper.PrefixMapperTestCases;
import org.eclipse.persistence.testing.jaxb.properties.PropertyTestCases;
import org.eclipse.persistence.testing.jaxb.readonly.ReadAndWriteOnlyTestCases;
import org.eclipse.persistence.testing.jaxb.stax.XMLStreamReaderEndEventTestCases;
import org.eclipse.persistence.testing.jaxb.stax.XMLStreamWriterDefaultNamespaceTestCases;
import org.eclipse.persistence.testing.jaxb.unmapped.UnmappedElementsWarningTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.DefaultValueTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.JSONUnmarshalAutoDetectTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.JSONUnmarshalTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.RepeatedUnmarshalTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.autodetect.AutoDetectFailsTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.autodetect.AutoDetectMediaTypeTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.autodetect.AutoDetectSmallDocTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.space.UnmarshalWithSpaceEventTestCases;
import org.eclipse.persistence.testing.jaxb.unmarshaller.validation.ValidationTestCases;
import org.eclipse.persistence.testing.jaxb.uri.ChildURITestCases;
import org.eclipse.persistence.testing.jaxb.uri.URITestCases;
import org.eclipse.persistence.testing.jaxb.xmlbindings.XMLBindingsPopulatedTestCases;
import org.eclipse.persistence.testing.jaxb.xmlbindings.XMLBindingsTestCases;
import org.eclipse.persistence.testing.jaxb.xmlbindings.XMLBindingsWithExternalMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.xmldecriptor.LazyInitTestCases;
import org.eclipse.persistence.testing.jaxb.xmlAnyMixed.XmlAnyMixedTestCases;
import org.eclipse.persistence.testing.jaxb.xmlmixed.XmlMixedTestCases;
import org.eclipse.persistence.testing.jaxb.xmlpath.XmlPathToAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.xmlpath.XmlPathToElementWithXmlAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.xmlpath.XmlPathWithMultipleEqualsCharactersTestCases;
import org.eclipse.persistence.testing.jaxb.xmlpath.XmlPathWithXmlAttributeTestCases;
import org.eclipse.persistence.testing.jaxb.xmlschema.XMLSchemaModelTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods.proporder.PropOrderTestCases;

public class JAXBTestSuite3 extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite3");

        suite.addTest(org.eclipse.persistence.testing.jaxb.singleobject.JAXBSingleObjectTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.classloader.DifferentClassLoaderTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.classloader.InnerClassTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.classloader.XmlElementsEnumTestCases.class);

        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.helper.JAXBHelperTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.any.AnyWithJAXBElementTestCases.class);

        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.interfaces.InterfaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.interfaces.choice.InterfaceChoiceTestCases.class);
        suite.addTestSuite(InvalidTransientInterfaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.inheritance.interfaces.InterfacesTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.MarshalSchemaValidationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.NoSchemaRefTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.UnmarshalSchemaValidationTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.UnmarshallerNullTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.NoSchemaRefTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlmarshaller.HandleListenerExceptionsTestCases.class);
        suite.addTestSuite(XMLStreamWriterDefaultNamespaceTestCases.class);
        suite.addTestSuite(XMLStreamReaderEndEventTestCases.class);
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
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.proporder.ExtraPropTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmltype.proporder.MissingPropTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlaccessortype.none.NoneTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlaccessortype.FieldAndPropertyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlaccessortype.IgnoreInvalidNonPublicFieldTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods.XmlVirtualAccessMethodsTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschematype.XmlSchemaTypeDateTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschematype.XmlSchemaTypeDateEmptyTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschematype.XmlSchemaTypeTwoDatesTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschematype.NonNegativeIntegerSchemaTypeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlschematype.CharTestCases.class);

        suite.addTestSuite(JAXBContextByClassArrayWithIndexTestCases.class);
        suite.addTestSuite(JAXBContextByClassArrayWithRefTestCases.class);
        suite.addTestSuite(JAXBContextByClassArrayWithRefInBindingsTestCases.class);
        suite.addTestSuite(JAXBContextByPackageWithIndexTestCases.class);
        suite.addTestSuite(ObjectFactoryTestCases.class);
        //suite.addTestSuite(MultipleXmlElementRefTestCases.class);

        suite.addTestSuite(PropOrderTestCases.class);
        suite.addTestSuite(LazyInitTestCases.class);
        suite.addTestSuite(XmlMixedTestCases.class);
        suite.addTestSuite(XmlAnyMixedTestCases.class);
        suite.addTestSuite(XmlPathWithMultipleEqualsCharactersTestCases.class);
        suite.addTestSuite(XmlPathToElementWithXmlAttributeTestCases.class);
        suite.addTestSuite(XmlPathToAttributeTestCases.class);
        suite.addTestSuite(XmlPathWithXmlAttributeTestCases.class);
        suite.addTestSuite(RepeatedUnmarshalTestCases.class);
        suite.addTestSuite(JSONUnmarshalTestCases.class);
        suite.addTestSuite(JSONUnmarshalAutoDetectTestCases.class);
        suite.addTestSuite(DefaultValueTestCases.class);
        suite.addTestSuite(ValidationTestCases.class);
        suite.addTestSuite(AutoDetectMediaTypeTestCases.class);
        suite.addTestSuite(AutoDetectFailsTestCases.class);
        suite.addTestSuite(AutoDetectSmallDocTestCases.class);
        suite.addTestSuite(ReadAndWriteOnlyTestCases.class);
        suite.addTestSuite(XMLBindingsTestCases.class);
        suite.addTestSuite(XMLBindingsPopulatedTestCases.class);
        suite.addTestSuite(XMLBindingsWithExternalMetadataTestCases.class);
        suite.addTestSuite(XMLSchemaModelTestCases.class);
        suite.addTestSuite(JAXBIntrospectorGetElementNameTestCases.class);
        suite.addTestSuite(UnmarshalWithSpaceEventTestCases.class);
        suite.addTestSuite(PrefixMapperTestCases.class);
        suite.addTestSuite(PrefixMapperMapTestCases.class);
        suite.addTestSuite(PrefixMapperPackageInfoTestCases.class);
        suite.addTestSuite(PrefixMapperContextTestCases.class);
        suite.addTestSuite(DefaultNSPrefixMapperSimpleTestCases.class);
        suite.addTestSuite(ChildURITestCases.class);
        suite.addTestSuite(URITestCases.class);
        suite.addTestSuite(PropertyTestCases.class);
        suite.addTestSuite(UnmappedElementsWarningTestCases.class);

        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite3" };
        // junit.swingui.TestRunner.main(arguments);
        // System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }

}
