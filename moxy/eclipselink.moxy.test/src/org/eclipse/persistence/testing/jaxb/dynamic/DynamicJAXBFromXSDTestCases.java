/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.io.*;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.dynamic.util.CustomEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import junit.framework.TestCase;


public class DynamicJAXBFromXSDTestCases extends TestCase {

    DynamicJAXBContext jaxbContext;
    protected static final String tmpdir = System.getenv("T_WORK") == null
            ? System.getProperty("java.io.tmpdir") : System.getenv("T_WORK");

    static {
        try {
            // Disable XJC's schema correctness check.  XSD imports do not seem to work if this is left on.
            System.setProperty("com.sun.tools.xjc.api.impl.s2j.SchemaCompilerImpl.noCorrectnessCheck", "true");
        } catch (Exception e) {
            // Ignore
        }
    }

    public DynamicJAXBFromXSDTestCases(String name) throws Exception {
        super(name);
    }

    public String getName() {
        return "Dynamic JAXB: XSD: " + super.getName();
    }

    // EclipseLink requires JDK >= 1.7
    public void testEclipseLinkSchema() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(ECLIPSELINK_SCHEMA);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);
    }

    // ====================================================================

    public void testXmlSchemaQualified() throws Exception {
        // <xs:schema targetNamespace="myNamespace" xmlns:xs="http://www.w3.org/2001/XMLSchema"
        //      attributeFormDefault="qualified" elementFormDefault="qualified">

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLSCHEMA_QUALIFIED);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 456);
        person.set("name", "Bob Dobbs");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // Make sure "targetNamespace" was interpreted properly.
        Node node = marshalDoc.getChildNodes().item(0);
        assertEquals("Target Namespace was not set as expected.", "myNamespace", node.getNamespaceURI());

        // Make sure "elementFormDefault" was interpreted properly.
        // elementFormDefault=qualified, so the root node, the
        // root node's attribute, and the child node should all have a prefix.
        assertNotNull("Root node did not have namespace prefix as expected.", node.getPrefix());

        Node attr = node.getAttributes().item(0);
        assertNotNull("Attribute did not have namespace prefix as expected.", attr.getPrefix());

        Node childNode = node.getChildNodes().item(0);
        assertNotNull("Child node did not have namespace prefix as expected.", childNode.getPrefix());
    }

    public void testXmlSchemaUnqualified() throws Exception {
        // <xs:schema targetNamespace="myNamespace" xmlns:xs="http://www.w3.org/2001/XMLSchema"
        //      attributeFormDefault="unqualified" elementFormDefault="unqualified">

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLSCHEMA_UNQUALIFIED);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 456);
        person.set("name", "Bob Dobbs");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // Make sure "targetNamespace" was interpreted properly.
        Node node = marshalDoc.getChildNodes().item(0);
        assertEquals("Target Namespace was not set as expected.", "myNamespace", node.getNamespaceURI());

        // Make sure "elementFormDefault" was interpreted properly.
        // elementFormDefault=unqualified, so the root node should have a prefix
        // but the root node's attribute and child node should not.
        assertNotNull("Root node did not have namespace prefix as expected.", node.getPrefix());

        // Do not test attribute prefix with the Oracle xmlparserv2, it qualifies attributes by default.
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        if (builderFactory.getClass().getPackage().getName().contains("oracle")) {
            return;
        } else {
            Node attr = node.getAttributes().item(0);
            assertNull("Attribute should not have namespace prefix (" + attr.getPrefix() + ").", attr.getPrefix());
        }

        Node childNode = node.getChildNodes().item(0);
        assertNull("Child node should not have namespace prefix.", childNode.getPrefix());
    }

    public void testXmlSchemaDefaults() throws Exception {
        // <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLSCHEMA_DEFAULTS);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(DEF_PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 456);
        person.set("name", "Bob Dobbs");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // "targetNamespace" should be null by default
        Node node = marshalDoc.getChildNodes().item(0);
        assertNull("Target Namespace was not null as expected.", node.getNamespaceURI());

        // Make sure "elementFormDefault" was interpreted properly.
        // When unset, no namespace qualification is done.
        assertNull("Root node should not have namespace prefix.", node.getPrefix());

        Node attr = node.getAttributes().item(0);
        assertNull("Attribute should not have namespace prefix.", attr.getPrefix());

        Node childNode = node.getChildNodes().item(0);
        assertNull("Child node should not have namespace prefix.", childNode.getPrefix());
    }

    public void testXmlSchemaImport() throws Exception {
        // <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
        //       <xs:import schemaLocation="xmlschema-currency" namespace="bankNamespace"/>

        // Do not run this test with the Oracle xmlparserv2, it will not properly hit the EntityResolver
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        if (builderFactory.getClass().getPackage().getName().contains("oracle")) {
            return;
        }

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLSCHEMA_IMPORT);

        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, new CustomEntityResolver(false), null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        DynamicEntity salary = jaxbContext.newDynamicEntity(BANK_PACKAGE + "." + CDN_CURRENCY);
        assertNotNull("Could not create Dynamic Entity.", salary);

        salary.set("value", new BigDecimal(75425.75));

        person.set("name", "Bob Dobbs");
        person.set("salary", salary);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        JAXBElement jbe = new JAXBElement(new QName("person"), DynamicEntity.class, person);

        jaxbContext.createMarshaller().marshal(jbe, marshalDoc);

        // Nothing to really test, if the import failed we couldn't have created the salary.
    }

    public void testXmlSeeAlso() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLSEEALSO);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + EMPLOYEE);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Bob Dobbs");
        person.set("employeeId", "CA2472");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        JAXBElement<DynamicEntity> jbe = new JAXBElement<DynamicEntity>(new QName("root"), DynamicEntity.class, person);

        jaxbContext.createMarshaller().marshal(jbe, marshalDoc);

        // Nothing to really test, XmlSeeAlso isn't represented in an instance doc.
    }

    public void testXmlRootElement() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLROOTELEMENT);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + INDIVIDUO);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Bob Dobbs");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getChildNodes().item(0);

        assertEquals("Root element was not 'individuo' as expected.", "individuo", node.getLocalName());
    }

    public void testXmlType() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLTYPE);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);


        person.set("email", "bdobbs@subgenius.com");
        person.set("lastName", "Dobbs");
        person.set("id", 678);
        person.set("phoneNumber", "212-555-8282");
        person.set("firstName", "Bob");

        JAXBElement jbe = new JAXBElement(new QName("person"), DynamicEntity.class, person);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(jbe, marshalDoc);

        // Test that XmlType.propOrder was interpreted properly
        Node node = marshalDoc.getDocumentElement().getChildNodes().item(0);
        assertNotNull("Node was null.", node);
        assertEquals("Unexpected node.", "id", node.getLocalName());

        node = marshalDoc.getDocumentElement().getChildNodes().item(1);
        assertNotNull("Node was null.", node);
        assertEquals("Unexpected node.", "first-name", node.getLocalName());

        node = marshalDoc.getDocumentElement().getChildNodes().item(2);
        assertNotNull("Node was null.", node);
        assertEquals("Unexpected node.", "last-name", node.getLocalName());

        node = marshalDoc.getDocumentElement().getChildNodes().item(3);
        assertNotNull("Node was null.", node);
        assertEquals("Unexpected node.", "phone-number", node.getLocalName());

        node = marshalDoc.getDocumentElement().getChildNodes().item(4);
        assertNotNull("Node was null.", node);
        assertEquals("Unexpected node.", "email", node.getLocalName());
    }

    public void testXmlAttribute() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLATTRIBUTE);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 777);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        JAXBElement jbe = new JAXBElement(new QName("person"), DynamicEntity.class, person);

        jaxbContext.createMarshaller().marshal(jbe, marshalDoc);

        Node node = marshalDoc.getChildNodes().item(0);

        if (node.getAttributes() == null || node.getAttributes().getNamedItem("id") == null) {
            fail("Attribute not present.");
        }
    }

    public void testXmlElement() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLELEMENT);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("type", "O+");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        JAXBElement jbe = new JAXBElement(new QName("person"), DynamicEntity.class, person);

        jaxbContext.createMarshaller().marshal(jbe, marshalDoc);

        Node node = marshalDoc.getDocumentElement();

        assertNotNull("Element not present.", node.getChildNodes());

        String elemName = node.getChildNodes().item(0).getNodeName();
        assertEquals("Element not present.", "type", elemName);
    }

    public void testXmlElementCollection() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLELEMENTCOLLECTION);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        ArrayList nicknames = new ArrayList();
        nicknames.add("Bobby");
        nicknames.add("Dobsy");
        nicknames.add("Big Kahuna");

        person.set("nickname", nicknames);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        JAXBElement jbe = new JAXBElement(new QName("person"), DynamicEntity.class, person);

        jaxbContext.createMarshaller().marshal(jbe, marshalDoc);

        Node node = marshalDoc.getDocumentElement();

        assertNotNull("Element not present.", node.getChildNodes());
        assertEquals("Unexpected number of child nodes present.", 3, node.getChildNodes().getLength());
    }

    public void testXmlElementCustomized() throws Exception {
        // Customize the EclipseLink mapping generation by providing an eclipselink-oxm.xml

        String metadataFile = RESOURCE_DIR + "eclipselink-oxm.xml";
        InputStream iStream = ClassLoader.getSystemResourceAsStream(metadataFile);
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLELEMENT);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, props);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("type", "O+");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement();

        assertNotNull("Element not present.", node.getChildNodes());

        String elemName = node.getChildNodes().item(0).getNodeName();

        // 'type' was renamed to 'bloodType' in the OXM bindings file
        assertEquals("Element not present.", "bloodType", elemName);
    }

    public void testXmlList() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLLIST);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        ArrayList<String> codes = new ArrayList<String>(3);
        codes.add("D1182");
        codes.add("D1716");
        codes.add("G7212");

        person.set("name", "Bob Dobbs");
        person.set("codes", codes);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement();

        assertEquals("Unexpected number of nodes in document.", 2, node.getChildNodes().getLength());

        String value = node.getChildNodes().item(1).getTextContent();

        assertEquals("Unexpected element contents.", "D1182 D1716 G7212", value);
    }

    public void testXmlValue() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLVALUE);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        DynamicEntity salary = jaxbContext.newDynamicEntity(PACKAGE + "." + CDN_CURRENCY);
        assertNotNull("Could not create Dynamic Entity.", salary);

        salary.set("value", new BigDecimal(75100));

        person.set("name", "Bob Dobbs");
        person.set("salary", salary);
        JAXBElement jbe = new JAXBElement(new QName("person"), DynamicEntity.class, person);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(jbe, marshalDoc);

        // Nothing to really test, XmlValue isn't represented in an instance doc.
    }

    public void testXmlAnyElement() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLANYELEMENT);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Bob Dobbs");
        person.set("any", "StringValue");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement();

        assertTrue("Any element not found.", node.getChildNodes().item(1).getNodeType() == Node.TEXT_NODE);
    }

    public void testXmlAnyAttribute() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLANYATTRIBUTE);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        Map<QName, Object> otherAttributes = new HashMap<QName, Object>();
        otherAttributes.put(new QName("foo"), new BigDecimal(1234));
        otherAttributes.put(new QName("bar"), Boolean.FALSE);

        person.set("name", "Bob Dobbs");
        person.set("otherAttributes", otherAttributes);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement();
        Node otherAttributesNode = node.getAttributes().getNamedItem("foo");
        assertNotNull("'foo' attribute not found.", otherAttributesNode);
        otherAttributesNode = node.getAttributes().getNamedItem("bar");
        assertNotNull("'bar' attribute not found.", otherAttributesNode);
    }

    public void testXmlMixed() throws Exception {
        // Also tests XmlElementRef / XmlElementRefs

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLMIXED);

        try {
            jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);
        } catch (JAXBException e) {
            // If running in a non-JAXB 2.2 environment, we will get this error because the required() method
            // on @XmlElementRef is missing.  Just ignore this and pass the test.
            if (e.getLinkedException() instanceof UndeclaredThrowableException) {
                return;
            } else {
                throw e;
            }
        } catch (Exception e) {
            if (e instanceof UndeclaredThrowableException) {
                return;
            } else {
                throw e;
            }
        }

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        ArrayList list = new ArrayList();
        list.add("Hello");
        list.add(new JAXBElement<String>(new QName("myNamespace", "title"), String.class, person.getClass(), "MR"));
        list.add(new JAXBElement<String>(new QName("myNamespace", "name"), String.class, person.getClass(), "Bob Dobbs"));
        list.add(", your point balance is");
        list.add(new JAXBElement<BigInteger>(new QName("myNamespace", "rewardPoints"), BigInteger.class, person.getClass(), BigInteger.valueOf(175)));
        list.add("Visit www.rewards.com!");
        person.set("content", list);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement();

        assertTrue("Unexpected number of elements.", node.getChildNodes().getLength() == 6);
    }

    public void testXmlId() throws Exception {
        // Tests both XmlId and XmlIdRef

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLID);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity data = jaxbContext.newDynamicEntity(PACKAGE + "." + DATA);
        assertNotNull("Could not create Dynamic Entity.", data);
        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);
        DynamicEntity company = jaxbContext.newDynamicEntity(PACKAGE + "." + COMPANY);
        assertNotNull("Could not create Dynamic Entity.", company);

        company.set("name", "ACME International");
        company.set("address", "165 Main St, Anytown US, 93012");
        company.set("id", "882");

        person.set("name", "Bob Dobbs");
        person.set("company", company);

        data.set("person", person);
        data.set("company", company);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(data, marshalDoc);

        // 'person' node
        Node pNode = marshalDoc.getDocumentElement().getChildNodes().item(0);
        // 'company' node
        Node cNode = pNode.getChildNodes().item(1);

        // If IDREF worked properly, the company element should only contain the id of the company object
        assertEquals("'company' has unexpected number of child nodes.", 1, cNode.getChildNodes().getLength());
    }

    public void testXmlElements() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLELEMENTS);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        ArrayList list = new ArrayList(1);
        list.add("BOB");
        person.set("nameOrReferenceNumber", list);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement().getChildNodes().item(0);

        assertEquals("Unexpected element name.", "name", node.getNodeName());

        person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        list = new ArrayList(1);
        list.add(328763);
        person.set("nameOrReferenceNumber", list);
        marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);
        node = marshalDoc.getDocumentElement().getChildNodes().item(0);

        assertEquals("Unexpected element name.", "reference-number", node.getNodeName());
    }

    public void testXmlElementRef() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLELEMENTREF);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        ArrayList list = new ArrayList(1);
        list.add("BOB");
        person.set("nameOrReferenceNumber", list);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement().getChildNodes().item(0);

        assertEquals("Unexpected element name.", "name", node.getNodeName());

        person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        list = new ArrayList(1);
        list.add(328763);
        person.set("nameOrReferenceNumber", list);
        marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);
        node = marshalDoc.getDocumentElement().getChildNodes().item(0);

        assertEquals("Unexpected element name.", "reference-number", node.getNodeName());
    }

    public void testXmlSchemaType() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLSCHEMATYPE);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("dateOfBirth", DatatypeFactory.newInstance().newXMLGregorianCalendarDate(1976, 02, 17, DatatypeConstants.FIELD_UNDEFINED));

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement().getChildNodes().item(0);

        assertEquals("Unexpected date value.", "1976-02-17", node.getTextContent());
    }

    public void testXmlEnum() throws Exception {
        // Tests XmlEnum and XmlEnumValue

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLENUM);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        Object NORTH = jaxbContext.getEnumConstant(PACKAGE + "." + COMPASS_DIRECTION, NORTH_CONSTANT);
        assertNotNull("Could not find enum constant.", NORTH);

        person.set("quadrant", NORTH);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);
    }

    public void testXmlEnumBig() throws Exception {
        // Tests XmlEnum and XmlEnumValue
        // This test schema contains >128 enum values to test an ASM boundary case

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLENUM_BIG);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        Object EST = jaxbContext.getEnumConstant(DEF_PACKAGE + "." + TIME_ZONE, EST_CONSTANT);
        assertNotNull("Could not find enum constant.", EST);
    }

    public void testXmlEnumError() throws Exception {
        // Tests XmlEnum and XmlEnumValue

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLENUM);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        Exception caughtException = null;
        try {
            Object NORTHWEST = jaxbContext.getEnumConstant(PACKAGE + "." + COMPASS_DIRECTION, "NORTHWEST");
        } catch (Exception e) {
            caughtException = e;
        }

        assertNotNull("Expected exception was not thrown.", caughtException);
    }

    public void testXmlElementDecl() throws Exception {
        // Also tests XmlRegistry

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLELEMENTDECL);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Bob Dobbs");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        JAXBElement jbe = new JAXBElement(new QName("individuo"), DynamicEntity.class, person);
        jaxbContext.createMarshaller().marshal(jbe, marshalDoc);


        Node node = marshalDoc.getChildNodes().item(0);

        assertEquals("Root element was not 'individuo' as expected.", "individuo", node.getLocalName());
    }

    public void testSchemaWithJAXBBindings() throws Exception {
        // jaxbcustom.xsd specifies that the generated package name should be "foo.bar" and
        // the person type will be named MyPersonType in Java

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(JAXBCUSTOM);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity("foo.bar.MyPersonType");
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Bob Dobbs");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);
    }

    public void testSubstitutionGroupsUnmarshal() throws Exception {
        try {
            InputStream xsdStream = ClassLoader.getSystemResourceAsStream(SUBSTITUTION);
            jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(xsdStream, null, null, null);

            InputStream xmlStream = ClassLoader.getSystemResourceAsStream(PERSON_XML);
            JAXBElement person = (JAXBElement) jaxbContext.createUnmarshaller().unmarshal(xmlStream);
            assertEquals("Element was not substituted properly: ", new QName("myNamespace", "person"), person.getName());
            JAXBElement name = (JAXBElement) ((DynamicEntity) person.getValue()).get("name");
            assertEquals("Element was not substituted properly: ", new QName("myNamespace", "name"), name.getName());

            // ====================================================================

            InputStream xmlStream2 = ClassLoader.getSystemResourceAsStream(PERSONNE_XML);
            JAXBElement person2 = (JAXBElement) jaxbContext.createUnmarshaller().unmarshal(xmlStream2);
            assertEquals("Element was not substituted properly: ", new QName("myNamespace", "personne"), person2.getName());
            JAXBElement name2 = (JAXBElement) ((DynamicEntity) person2.getValue()).get("name");
            assertEquals("Element was not substituted properly: ", new QName("myNamespace", "nom"), name2.getName());
        } catch (JAXBException e) {
            // If running in a non-JAXB 2.2 environment, we will get this error because the required() method
            // on @XmlElementRef is missing.  Just ignore this and pass the test.
            if (e.getLinkedException() instanceof UndeclaredThrowableException) {
                return;
            } else {
                throw e;
            }
        } catch (Exception e) {
            if (e instanceof UndeclaredThrowableException) {
                return;
            } else {
                throw e;
            }
        }
    }

    public void testSubstitutionGroupsMarshal() throws Exception {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(SUBSTITUTION);
            jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

            QName personQName = new QName("myNamespace", "person");
            DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
            JAXBElement<DynamicEntity> personElement = new JAXBElement<DynamicEntity>(personQName, DynamicEntity.class, person);
            personElement.setValue(person);

            QName nameQName = new QName("myNamespace", "name");
            JAXBElement<String> nameElement = new JAXBElement<String>(nameQName, String.class, "Marty Friedman");

            person.set("name", nameElement);

            Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            jaxbContext.createMarshaller().marshal(personElement, marshalDoc);

            Node node1 = marshalDoc.getDocumentElement();
            assertEquals("Incorrect element name: ", "person", node1.getLocalName());

            Node node2 = node1.getFirstChild();
            assertEquals("Incorrect element name: ", "name", node2.getLocalName());

            // ====================================================================

            QName personneQName = new QName("myNamespace", "personne");
            DynamicEntity personne = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
            JAXBElement<DynamicEntity> personneElement = new JAXBElement<DynamicEntity>(personneQName, DynamicEntity.class, personne);
            personneElement.setValue(personne);

            QName nomQName = new QName("myNamespace", "nom");
            JAXBElement<String> nomElement = new JAXBElement<String>(nomQName, String.class, "Marty Friedman");

            personne.set("name", nomElement);

            Document marshalDoc2 = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            jaxbContext.createMarshaller().marshal(personneElement, marshalDoc2);

            Node node3 = marshalDoc2.getDocumentElement();
            assertEquals("Incorrect element name: ", "personne", node3.getLocalName());

            Node node4 = node3.getFirstChild();
            assertEquals("Incorrect element name: ", "nom", node4.getLocalName());
        } catch (JAXBException e) {
            // If running in a non-JAXB 2.2 environment, we will get this error because the required() method
            // on @XmlElementRef is missing.  Just ignore this and pass the test.
            if (e.getLinkedException() instanceof UndeclaredThrowableException) {
                return;
            } else {
                throw e;
            }
        } catch (Exception e) {
            if (e instanceof UndeclaredThrowableException) {
                return;
            } else {
                throw e;
            }
        }
    }

    // ====================================================================

    public void testTypePreservation() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLSCHEMA_DEFAULTS);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(DEF_PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 456);
        person.set("name", "Bob Dobbs");
        person.set("salary", 45000.00);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);

        assertEquals("Property type was not preserved during unmarshal.", Double.class, readPerson.<Object>get("salary").getClass());
        assertEquals("Property type was not preserved during unmarshal.", Integer.class, readPerson.<Object>get("id").getClass());
    }

    public void testNestedInnerClasses() throws Exception {
        // Tests multiple levels of inner classes, eg. mynamespace.Person.RelatedResource.Link
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(NESTEDINNERCLASSES);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity("mynamespace.Person");
        DynamicEntity resource = jaxbContext.newDynamicEntity("mynamespace.Person.RelatedResource");
        DynamicEntity link = jaxbContext.newDynamicEntity("mynamespace.Person.RelatedResource.Link");
        DynamicEntity link2 = jaxbContext.newDynamicEntity("mynamespace.Person.RelatedResource.Link");

        link.set("linkName", "LINKFOO");
        link2.set("linkName", "LINKFOO2");

        resource.set("resourceName", "RESBAR");

        ArrayList<DynamicEntity> links = new ArrayList<DynamicEntity>();
        links.add(link);
        links.add(link2);

        resource.set("link", links);
        person.set("name", "Bob Smith");
        person.set("relatedResource", resource);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        JAXBElement jbe = new JAXBElement(new QName("person"), DynamicEntity.class, person);
        jaxbContext.createMarshaller().marshal(jbe, marshalDoc);
    }

    public void testBinary() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(BINARY);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        byte[] byteArray = new byte[] {30,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4};

        DynamicEntity person = jaxbContext.newDynamicEntity("mynamespace.Person");
        person.set("name", "B. Nary");
        person.set("abyte", (byte) 30);
        person.set("base64", byteArray);
        person.set("hex", byteArray);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);
    }

    public void testBinaryGlobalType() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(BINARY2);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        byte[] byteArray = new byte[] {30,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4};

        DynamicEntity person = jaxbContext.newDynamicEntity("mynamespace.Person");
        person.set("name", "B. Nary");
        person.set("abyte", (byte) 30);
        person.set("base64", byteArray);
        person.set("hex", byteArray);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        JAXBElement jbe = new JAXBElement(new QName("person"), DynamicEntity.class, person);
        jaxbContext.createMarshaller().marshal(jbe, marshalDoc);
    }

    public void testXMLSchemaSchema() throws Exception {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLSCHEMASCHEMA);
            jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

            DynamicEntity schema = jaxbContext.newDynamicEntity("org.w3._2001.xmlschema.Schema");
            schema.set("targetNamespace", "myNamespace");
            Object QUALIFIED = jaxbContext.getEnumConstant("org.w3._2001.xmlschema.FormChoice", "QUALIFIED");
            schema.set("attributeFormDefault", QUALIFIED);

            DynamicEntity complexType = jaxbContext.newDynamicEntity("org.w3._2001.xmlschema.TopLevelComplexType");
            complexType.set("name", "myComplexType");

            List<DynamicEntity> complexTypes = new ArrayList<DynamicEntity>(1);
            complexTypes.add(complexType);

            schema.set("simpleTypeOrComplexTypeOrGroup", complexTypes);

            List<Object> blocks = new ArrayList<Object>();
            blocks.add("FOOBAR");
            schema.set("blockDefault", blocks);

            File f = new File(tmpdir, "myschema.xsd");
            jaxbContext.createMarshaller().marshal(schema, f);
        } catch (JAXBException e) {
            // If running in a non-JAXB 2.2 environment, we will get this error because the required() method
            // on @XmlElementRef is missing.  Just ignore this and pass the test.
            if (e.getLinkedException() instanceof UndeclaredThrowableException) {
                return;
            } else {
                throw e;
            }
        } catch (Exception e) {
            if (e instanceof UndeclaredThrowableException) {
                return;
            } else {
                throw e;
            }
        }
    }


    /**
     * Test for element type reference across multiple XML schemas with different namespaces.
     * Validates result after marshalling against XML Schema.
     * @throws Exception
     */
    public void testXmlSchemaCrossSchema() throws Exception {
        String backupProperty = null;
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLSCHEMA_IMPORT_CROSS_SCHEMA);
            DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, new CustomEntityResolver(true), null, null);
            backupProperty = (System.getProperty(PROP_CORRECTNESS_CHECK_SCHEMA) != null) ? System.getProperty(PROP_CORRECTNESS_CHECK_SCHEMA) : null;
            System.setProperty(PROP_CORRECTNESS_CHECK_SCHEMA, "true");

            DynamicEntity testReq = jaxbContext.newDynamicEntity("com.temp.first.TestReq");

            DynamicEntity fault = jaxbContext.newDynamicEntity("com.temp.third.FaultType");

            DynamicEntity dataReference = jaxbContext.newDynamicEntity("com.temp.second.InheritedFaultType");
            dataReference.set("referenceId", "123456");

            DynamicEntity userKey = jaxbContext.newDynamicEntity("com.temp.fourth.UserType");
            userKey.set("userId", "TestUserID");

            dataReference.set("userKey", userKey);

            fault.set("dataReference", dataReference);

            testReq.set("fault", fault);
            testReq.set("companyId", "TestCompanyID");

            final StringWriter sw = new StringWriter();
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(testReq, sw);

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema testReqSchema = factory.newSchema(Thread.currentThread().getContextClassLoader().getResource(XMLSCHEMA_IMPORT_CROSS_SCHEMA));
            testReqSchema.newValidator().validate(
                    new StreamSource(new StringReader(sw.getBuffer().toString())));
        } catch (Exception e) {
            throw e;
        } finally {
            if (backupProperty != null) {
                System.setProperty(PROP_CORRECTNESS_CHECK_SCHEMA, backupProperty);
            } else {
                System.clearProperty(PROP_CORRECTNESS_CHECK_SCHEMA);
            }
        }
    }


    public void testGetByXPathPosition() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XPATH_POSITION);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        InputStream xmlStream = ClassLoader.getSystemResourceAsStream(XPATH_POSITION_XML);
        JAXBElement<DynamicEntity> jelem = (JAXBElement<DynamicEntity>) jaxbContext.createUnmarshaller().unmarshal(xmlStream);
        DynamicEntity testBean = jelem.getValue();

        Object o = jaxbContext.getValueByXPath(testBean, "array[1]/text()", null, Object.class);
        assertNotNull("XPath: 'array[1]/text()' returned null.", o);
        o = jaxbContext.getValueByXPath(testBean, "array[2]/text()", null, Object.class);
        assertNull("XPath: 'array[2]/text()' did not return null.", o);
        o = jaxbContext.getValueByXPath(testBean, "map/entry[1]/value/text()", null, Object.class);
        assertEquals("foo", o);
        o = jaxbContext.getValueByXPath(testBean, "sub-bean[1]/map/entry[1]/value/text()", null, Object.class);
        assertEquals("foo2", o);
    }

    public void testDateTimeArray() throws Exception {
        // Tests to ensure that XSD dateTime is always unmarshalled as XMLGregorianCalendar, and never
        // as GregorianCalendar.  This can fail intermittently so is tested in a loop.

        HashSet<Class> elemClasses = new HashSet<Class>();

        for (int i = 0; i < 50; i++) {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(DATETIME_ARRAY);
            jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

            InputStream xmlStream = ClassLoader.getSystemResourceAsStream(DATETIME_ARRAY_XML);
            JAXBElement<DynamicEntity> jelem = (JAXBElement<DynamicEntity>) jaxbContext.createUnmarshaller().unmarshal(xmlStream);
            DynamicEntity testBean = jelem.getValue();

            ArrayList array = testBean.get("array");
            elemClasses.add(array.get(0).getClass());
        }

        assertEquals("dateTime was not consistently unmarshalled as XMLGregorianCalendar " + elemClasses, 1, elemClasses.size());
        Class elemClass = (Class) elemClasses.toArray()[0];
        boolean isXmlGregorianCalendar = ClassConstants.XML_GREGORIAN_CALENDAR.isAssignableFrom(elemClass);
        assertTrue("dateTime was not unmarshalled as XMLGregorianCalendar", isXmlGregorianCalendar);
    }

    // ====================================================================

    private void print(Object o) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(o, System.err);
    }

    // ====================================================================

    private static final String RESOURCE_DIR = "org/eclipse/persistence/testing/jaxb/dynamic/";
    private static final String CONTEXT_PATH = "mynamespace";

    // System property name to restrict access to external schemas
    private static final String PROP_ACCESS_EXTERNAL_SCHEMA = "javax.xml.accessExternalSchema";
    // System property name to disable XJC's schema correctness check.  XSD imports do not seem to work if this is left on.
    private static final String PROP_CORRECTNESS_CHECK_SCHEMA = "com.sun.tools.xjc.api.impl.s2j.SchemaCompilerImpl.noCorrectnessCheck";


    // Schema files used to test each annotation
    private static final String XMLSCHEMA_QUALIFIED = RESOURCE_DIR + "xmlschema-qualified.xsd";
    private static final String XMLSCHEMA_UNQUALIFIED = RESOURCE_DIR +  "xmlschema-unqualified.xsd";
    private static final String XMLSCHEMA_DEFAULTS = RESOURCE_DIR + "xmlschema-defaults.xsd";
    private static final String XMLSCHEMA_IMPORT = RESOURCE_DIR + "xmlschema-import.xsd";
    private static final String XMLSCHEMA_IMPORT_CROSS_SCHEMA = RESOURCE_DIR + "xmlschema-import-cross-first.xsd";
    private static final String XMLSCHEMA_CURRENCY = RESOURCE_DIR + "xmlschema-currency.xsd";
    private static final String XMLSEEALSO = RESOURCE_DIR + "xmlseealso.xsd";
    private static final String XMLROOTELEMENT = RESOURCE_DIR + "xmlrootelement.xsd";
    private static final String XMLTYPE = RESOURCE_DIR + "xmltype.xsd";
    private static final String XMLATTRIBUTE = RESOURCE_DIR + "xmlattribute.xsd";
    private static final String XMLELEMENT = RESOURCE_DIR + "xmlelement.xsd";
    private static final String XMLLIST = RESOURCE_DIR + "xmllist.xsd";
    private static final String XMLVALUE = RESOURCE_DIR + "xmlvalue.xsd";
    private static final String XMLANYELEMENT = RESOURCE_DIR + "xmlanyelement.xsd";
    private static final String XMLANYATTRIBUTE = RESOURCE_DIR + "xmlanyattribute.xsd";
    private static final String XMLMIXED = RESOURCE_DIR + "xmlmixed.xsd";
    private static final String XMLID = RESOURCE_DIR + "xmlid.xsd";
    private static final String XMLELEMENTS = RESOURCE_DIR + "xmlelements.xsd";
    private static final String XMLELEMENTREF = RESOURCE_DIR + "xmlelementref.xsd";
    private static final String XMLSCHEMATYPE = RESOURCE_DIR + "xmlschematype.xsd";
    private static final String XMLENUM = RESOURCE_DIR + "xmlenum.xsd";
    private static final String XMLENUM_BIG = RESOURCE_DIR + "xmlenum-big.xsd";
    private static final String XMLELEMENTDECL = RESOURCE_DIR + "xmlelementdecl.xsd";
    private static final String XMLELEMENTCOLLECTION = RESOURCE_DIR + "xmlelement-collection.xsd";
    private static final String JAXBCUSTOM = RESOURCE_DIR + "jaxbcustom.xsd";
    private static final String SUBSTITUTION = RESOURCE_DIR + "substitution.xsd";
    private static final String NESTEDINNERCLASSES = RESOURCE_DIR + "nestedinnerclasses.xsd";
    private static final String BINARY = RESOURCE_DIR + "binary.xsd";
    private static final String BINARY2 = RESOURCE_DIR + "binary2.xsd";
    private static final String XMLSCHEMASCHEMA = RESOURCE_DIR + "XMLSchema.xsd";
    private static final String XPATH_POSITION = RESOURCE_DIR + "xpathposition.xsd";
    private static final String DATETIME_ARRAY = RESOURCE_DIR + "dateTimeArray.xsd";

    private static final String ECLIPSELINK_SCHEMA = "org/eclipse/persistence/jaxb/eclipselink_oxm_2_6.xsd";

    // Test Instance Docs
    private static final String PERSON_XML = RESOURCE_DIR + "sub-person-en.xml";
    private static final String PERSONNE_XML = RESOURCE_DIR + "sub-personne-fr.xml";
    private static final String XPATH_POSITION_XML = RESOURCE_DIR + "xpathposition.xml";
    private static final String DATETIME_ARRAY_XML = RESOURCE_DIR + "dateTimeArray.xml";

    // Names of types to instantiate
    private static final String PACKAGE = "mynamespace";
    private static final String DEF_PACKAGE = "generated";
    private static final String BANK_PACKAGE = "banknamespace";
    private static final String PERSON = "Person";
    private static final String EMPLOYEE = "Employee";
    private static final String INDIVIDUO = "Individuo";
    private static final String CDN_CURRENCY = "CdnCurrency";
    private static final String DATA = "Data";
    private static final String COMPANY = "Company";
    private static final String COMPASS_DIRECTION = "CompassDirection";
    private static final String TIME_ZONE = "Timezone";
    private static final String NORTH_CONSTANT = "NORTH";
    private static final String EST_CONSTANT = "EST";

}
