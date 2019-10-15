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
//     rbarkhouse - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.ClassExtractor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.mappings.transformers.AttributeTransformerAdapter;
import org.eclipse.persistence.mappings.transformers.FieldTransformerAdapter;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.jaxb.dynamic.util.Computer;
import org.eclipse.persistence.testing.jaxb.dynamic.util.ComputerAdapter;
import org.eclipse.persistence.testing.jaxb.dynamic.util.LinkAdapter;
import org.eclipse.persistence.testing.jaxb.dynamic.util.LinkAdapterString;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DynamicJAXBFromOXMTestCases extends TestCase {

    private DynamicJAXBContext jaxbContext;

    public DynamicJAXBFromOXMTestCases(String name) throws Exception {
        super(name);
    }

    public String getName() {
        return "Dynamic JAXB: OXM: " + super.getName();
    }

    // Standard OXM Annotation tests
    // ====================================================================

    public void testXmlSchemaQualified() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLSCHEMA_QUALIFIED);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLSCHEMA_QUALIFIED + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 456);
        person.set("name", "Bob Dobbs");
        person.set("salary", 45000.00);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // Make sure "targetNamespace" was interpreted properly.
        Element element = marshalDoc.getDocumentElement();
        assertEquals("Target Namespace was not set as expected.", "mynamespace", element.getNamespaceURI());

        // Make sure "elementFormDefault" was interpreted properly.
        // elementFormDefault=qualified, so the root node, the
        // root node's attribute, and the child node should all have a prefix.
        assertNotNull("Root node did not have namespace prefix as expected.", element.getPrefix());

        Node attr = element.getAttributeNodeNS("mynamespace", "id");
        assertNotNull("Attribute did not have namespace prefix as expected.", attr.getPrefix());

        Node childNode = element.getElementsByTagNameNS("mynamespace", "salary").item(0);
        assertNotNull("Child node did not have namespace prefix as expected.", childNode.getPrefix());
    }

    public void testXmlSchemaUnqualified() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLSCHEMA_UNQUALIFIED);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLSCHEMA_UNQUALIFIED + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 456);
        person.set("name", "Bob Dobbs");
        person.set("salary", 45000.00);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // Make sure "targetNamespace" was interpreted properly.
        Element element = marshalDoc.getDocumentElement();
        assertEquals("Target Namespace was not set as expected.", "mynamespace", element.getNamespaceURI());

        // Make sure "elementFormDefault" was interpreted properly.
        // elementFormDefault=unqualified, so the root node should have a prefix
        // but the root node's attribute and child node should not.
        assertNotNull("Root node did not have namespace prefix as expected.", element.getPrefix());

        Node attr = element.getAttributeNode("id");
        assertNull("Attribute should not have namespace prefix (" + attr.getPrefix() + ").", attr.getPrefix());

        Node childNode = element.getElementsByTagName("salary").item(0);
        assertNull("Child node should not have namespace prefix.", childNode.getPrefix());
    }

    public void testXmlSchemaDefaults() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLSCHEMA_DEFAULTS);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLSCHEMA_DEFAULTS + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        // Testing defaults, so don't specify a package name (default package)
        metadataSourceMap.put("", new StreamSource(iStream));

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 456);
        person.set("name", "Bob Dobbs");
        person.set("salary", 45000.00);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // "targetNamespace" should be null by default
        Element element = marshalDoc.getDocumentElement();
        assertNull("Target Namespace was not null as expected.", element.getNamespaceURI());

        // Make sure "elementFormDefault" was interpreted properly.
        // When unset, no namespace qualification is done.
        assertNull("Root node should not have namespace prefix.", element.getPrefix());

        Node attr = element.getAttributeNode("id");
        assertNull("Attribute should not have namespace prefix.", attr.getPrefix());

        Node childNode = element.getElementsByTagName("salary").item(0);
        assertNull("Child node should not have namespace prefix.", childNode.getPrefix());
    }

    public void testXmlRootElement() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLROOTELEMENT);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLROOTELEMENT + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Bob Dobbs");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getChildNodes().item(0);

        assertEquals("Root element was not 'individuo' as expected.", "individuo", node.getLocalName());
    }

    public void testXmlType() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLTYPE);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLTYPE + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("email", "bdobbs@subgenius.com");
        person.set("lastName", "Dobbs");
        person.set("id", 678);
        person.set("phoneNumber", "212-555-8282");
        person.set("firstName", "Bob");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // Test that XmlType.propOrder was interpreted properly
        Node node = marshalDoc.getDocumentElement().getChildNodes().item(0);
        assertNotNull("Node was null.", node);
        assertEquals("Unexpected node.", "id", node.getLocalName());

        node = marshalDoc.getDocumentElement().getChildNodes().item(1);
        assertNotNull("Node was null.", node);
        assertEquals("Unexpected node.", "firstName", node.getLocalName());

        node = marshalDoc.getDocumentElement().getChildNodes().item(2);
        assertNotNull("Node was null.", node);
        assertEquals("Unexpected node.", "lastName", node.getLocalName());

        node = marshalDoc.getDocumentElement().getChildNodes().item(3);
        assertNotNull("Node was null.", node);
        assertEquals("Unexpected node.", "phoneNumber", node.getLocalName());

        node = marshalDoc.getDocumentElement().getChildNodes().item(4);
        assertNotNull("Node was null.", node);
        assertEquals("Unexpected node.", "email", node.getLocalName());
    }

    public void testXmlAttribute() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLATTRIBUTE);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLATTRIBUTE + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 777);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getChildNodes().item(0);

        if (node.getAttributes() == null || node.getAttributes().getNamedItemNS("mynamespace", "id") == null) {
            fail("Attribute not present.");
        }
    }

    public void testXmlElement() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLELEMENT);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLELEMENT + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("type", "O+");

        ArrayList<String> nicknames;

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement();

        assertNotNull("Element not present.", node.getChildNodes());

        String elemName = node.getChildNodes().item(0).getNodeName();
        assertEquals("Element not present.", "type", elemName);
    }

    public void testXmlList() throws Exception {
        // Also tests collection of XmlElement
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLLIST);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLLIST + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Richard Nixon");

        ArrayList<String> nicks = new ArrayList<String>();
        nicks.add("Tricky Dick");
        nicks.add("Iron Butt");
        person.set("nicknames", nicks);

        ArrayList<String> emails = new ArrayList<String>();
        emails.add("nixon.richard@whitehouse.gov");
        emails.add("rnixon1913@aol.com");
        person.set("emails", emails);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        int numNicks = marshalDoc.getElementsByTagName("nicknames").getLength();
        assertEquals("Incorrect number of nicknames.", 2, numNicks);

        int numEmails = marshalDoc.getElementsByTagName("emails").getLength();
        assertEquals("Incorrect number of emails.", 1, numEmails);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);
        assertEquals("Incorrect number of nicknames.", 2, ((ArrayList) person.get("nicknames")).size());
        assertEquals("Incorrect number of emails.", 2, ((ArrayList) person.get("emails")).size());
    }

    public void testXmlValue() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLVALUE);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLVALUE + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        DynamicEntity salary = jaxbContext.newDynamicEntity(PACKAGE + "." + CDN_CURRENCY);
        assertNotNull("Could not create Dynamic Entity.", salary);

        salary.set("amount", new BigDecimal(75100));

        person.set("name", "Bob Dobbs");
        person.set("salary", salary);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // Nothing to really test, XmlValue isn't represented in an instance doc.
    }

    public void testXmlAnyElement() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLANYELEMENT);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLANYELEMENT + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLANYATTRIBUTE);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLANYATTRIBUTE + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLMIXED);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLMIXED + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Bob Dobbs");

        ArrayList list = new ArrayList();
        list.add("Hello ");
        list.add(new JAXBElement<String>(new QName("myNamespace", "title"), String.class, person.getClass(), "MR"));
        list.add(new JAXBElement<String>(new QName("myNamespace", "name"), String.class, person.getClass(), "Bob Dobbs"));
        list.add(", your point balance is ");
        list.add(new JAXBElement<BigInteger>(new QName("myNamespace", "rewardPoints"), BigInteger.class, person.getClass(), BigInteger.valueOf(175)));
        list.add("Visit www.rewards.com!");
        person.set("mixed", list);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement();

        assertEquals("Unexpected number of elements.", 7, node.getChildNodes().getLength());
    }

    public void testXmlId() throws Exception {
        // Tests both XmlId and XmlIdRef
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLID);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLID + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity data = jaxbContext.newDynamicEntity(PACKAGE + "." + DATA);
        assertNotNull("Could not create Dynamic Entity.", data);
        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);
        DynamicEntity company = jaxbContext.newDynamicEntity(PACKAGE + "." + COMPANY);
        assertNotNull("Could not create Dynamic Entity.", company);

        company.set("name", "ACME International");
        company.set("address", "165 Main St, Anytown US, 93012");
        company.set("id", Integer.valueOf(882));

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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLELEMENTS);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLELEMENTS + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        ArrayList list = new ArrayList(1);
        list.add("BOB");
        person.set("items", list);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement().getChildNodes().item(0);

        assertEquals("Unexpected element name.", "name", node.getNodeName());

        person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        list = new ArrayList(1);
        list.add(328763);
        person.set("items", list);
        marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);
        node = marshalDoc.getDocumentElement().getChildNodes().item(0);

        assertEquals("Unexpected element name.", "referenceNumber", node.getNodeName());
    }

    public void testXmlElementRef() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLELEMENTREF);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLELEMENTREF + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);
        person.set("name", "Jim Watson");

        DynamicEntity phone1 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone1.set("id", 111);
        phone1.set("number", "118-123-1124");

        DynamicEntity phone2 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone2.set("id", 222);
        phone2.set("number", "623-121-7425");

        ArrayList<DynamicEntity> phones = new ArrayList<DynamicEntity>();
        phones.add(phone1);
        phones.add(phone2);
        person.set("phones", phones);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement().getChildNodes().item(1);
        assertEquals("Element wrapper not written as expected.", "gsm_phones", node.getNodeName());

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);

        ArrayList<DynamicEntity> l = readPerson.get("phones");
        assertEquals("Phones not unmarshalled as expected.", 111, ((Number) (l.get(0).get("id"))).intValue());
    }

    public void testXmlSchemaType() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLSCHEMATYPE);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLSCHEMATYPE + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("dateOfBirth", DatatypeFactory.newInstance().newXMLGregorianCalendarDate(1976, 02, 17, DatatypeConstants.FIELD_UNDEFINED));

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement().getChildNodes().item(0);

        assertEquals("Unexpected date value.", "1976-02-17", node.getTextContent());
    }

    public void testXmlEnum() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLENUM);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLENUM + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        Object nConst = jaxbContext.getEnumConstant(PACKAGE + "." + COMPASS_DIRECTION, "N");
        assertNotNull("Could not find enum constant.", nConst);

        person.set("quadrant", nConst);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement().getChildNodes().item(0);
        assertEquals("Enum not written as expected.", "NORTH", node.getTextContent());
    }

    public void testXmlEnumError() throws Exception {
        // Tests XmlEnum and XmlEnumValue
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLENUM);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLENUM + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLELEMENTDECL);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLELEMENTDECL + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity individuo = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", individuo);
        individuo.set("name", "Bob Dobbs");

        QName individuoQName = new QName(PACKAGE, INDIVIDUO);

        JAXBElement<DynamicEntity> individuoElement = new JAXBElement<DynamicEntity>(individuoQName, DynamicEntity.class, individuo);
        individuoElement.setValue(individuo);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(individuoElement, marshalDoc);

        Node node = marshalDoc.getChildNodes().item(0);

        assertEquals("Root element was not 'individuo' as expected.", "individuo", node.getLocalName());
    }

    // Other tests
    // ====================================================================

    public void testSubstitutionGroupsMarshal() throws Exception {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream iStream = classLoader.getResourceAsStream(SUBSTITUTION);
            if (iStream == null) {
                fail("Couldn't load metadata file [" + SUBSTITUTION + "]");
            }

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

            jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

            QName personQName = new QName("myNamespace", "person");
            DynamicEntity person = jaxbContext.newDynamicEntity("myNamespace" + "." + PERSON);
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
            DynamicEntity personne = jaxbContext.newDynamicEntity("myNamespace" + "." + PERSON);
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
        } catch (UndeclaredThrowableException e) {
            if (e.getUndeclaredThrowable() instanceof NoSuchMethodException) {
                // We will get NoSuchMethod: XmlElementRef.required() if not running JAXB 2.2
                // or greater, so just pass in this case.
                assertTrue(true);
            }
        }
    }

    public void testSubstitutionGroupsUnmarshal() throws Exception {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream iStream = classLoader.getResourceAsStream(SUBSTITUTION);
            if (iStream == null) {
                fail("Couldn't load metadata file [" + SUBSTITUTION + "]");
            }

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

            jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

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
        } catch (UndeclaredThrowableException e) {
            if (e.getUndeclaredThrowable() instanceof NoSuchMethodException) {
                // We will get NoSuchMethod: XmlElementRef.required() if not running JAXB 2.2
                // or greater, so just pass in this case.
                assertTrue(true);
            }
        }
    }

    public void testTypePreservation() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(DATATYPES);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + DATATYPES + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        // Testing defaults, so don't specify a package name (default package)
        metadataSourceMap.put("", new StreamSource(iStream));

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 456);
        person.set("name", "Bob Dobbs");
        ArrayList payments = new ArrayList();
        payments.add(4150.00d);
        payments.add(4150.00d);
        payments.add(387.22d);
        person.set("payments", payments);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);

        assertEquals("Property type was not preserved during unmarshal.", Integer.class, readPerson.<Object>get("id").getClass());
        ArrayList readPayments = readPerson.get("payments");
        assertEquals("Property type was not preserved during unmarshal.", Double.class, readPayments.get(0).getClass());
    }

    // MOXy Extensions tests
    // ====================================================================

    public void testXmlReadOnly() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLREADONLY);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLREADONLY + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Lisa Nova");
        person.set("sin", "272762998");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // Ensure that the 'sin' element was not written
        int occurancesOfSin = marshalDoc.getElementsByTagName("sin").getLength();
        assertEquals("Read-only element 'sin' was written to XML.", 0, occurancesOfSin);
    }

    public void testXmlWriteOnly() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLWRITEONLY);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLWRITEONLY + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Lisa Nova");
        person.set("last-modified", System.currentTimeMillis());

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // Ensure that the 'last-modified' element was not read
        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);
        assertNull("Write-only element 'last-modified' was read from XML.", readPerson.get("last-modified"));
    }

    public void testXmlCDATA() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLCDATA);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLCDATA + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("characterData", "JKSGSIU&^@#bgr8736JGHYXC><#*&6JHGS");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // Ensure that a CDATA node was written out
        Node node = marshalDoc.getChildNodes().item(0).getFirstChild().getFirstChild();
        assertTrue("Element contents did not contain expected ![CDATA[]].", node instanceof CDATASection);
    }

    public void testXmlPath() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLPATH);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLPATH + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Lisa Nova");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        String name = jaxbContext.getValueByXPath(person, "contact-info/personal-info/name/text()", null, String.class);
        assertNotNull("'name' element was not found at the specified XPath.", name);
    }

    public void testXmlNullPolicy() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLNULLPOLICY);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLNULLPOLICY + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        // First test marshaling
        // No fields are set in the Person, so we should see
        //    info1 - written as empty element
        //    info2 - absent element (should not appear)
        //    info3 - written with xsi:nil="true"
        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node info1Node = marshalDoc.getElementsByTagName("write1").item(0);
        assertTrue("'write1' null policy was not applied during marshal.",
                info1Node.getAttributes().getLength() == 0 && info1Node.getChildNodes().getLength() == 0);

        Node info2Node = marshalDoc.getElementsByTagName("write2").item(0);
        assertNull("'write2' null policy was not applied during marshal.", info2Node);

        Node info3Node = marshalDoc.getElementsByTagName("write3").item(0);
        assertNotNull("'write3' null policy was not applied during marshal.",
                info3Node.getAttributes().getNamedItemNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, XMLConstants.SCHEMA_NIL_ATTRIBUTE));

        // Now test unmarshal
        InputStream is = ClassLoader.getSystemResourceAsStream(XMLNULLPOLICY_INSTANCE);
        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(is);

        assertNull("'read1' should have been null in the object.", readPerson.get("read1"));
        assertNull("'read2' should have been null in the object.", readPerson.get("read2"));
        assertNull("'read3' should have been null in the object.", readPerson.get("read3"));
        assertNull("'read4' should have been null in the object.", readPerson.get("read4"));

        assertNull("'read5' should have been null in the object.", readPerson.get("read5"));
        assertNotNull("'read6' should have been empty Address the object.", readPerson.get("read6"));
        assertNull("'read7' should have been null in the object.", readPerson.get("read7"));
        assertNull("'read8' should have been null in the object.", readPerson.get("read8"));

        assertEquals("'read9' should have been empty String in the object.", "", readPerson.get("read9"));
        assertEquals("'read10' should have been empty String in the object.", "", readPerson.get("read10"));
        assertNull("'read11' should have been null in the object.", readPerson.get("read11"));
        assertNull("'read12' should have been null in the object.", readPerson.get("read12"));
    }

    public void testXmlProperties() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLPROPERTIES);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLPROPERTIES + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        // Ensure that properties were set on the Descriptor and Mapping
        XMLDescriptor d = jaxbContext.getXMLContext().getDescriptor(new QName("mynamespace", "person"));
        assertEquals("Descriptor property not present.", Integer.valueOf(101), d.getProperty("identifier"));
        assertEquals("Descriptor property not present.", Boolean.FALSE, d.getProperty("active"));

        XMLDirectMapping m = (XMLDirectMapping) d.getMappingForAttributeName("name");
        assertEquals("Mapping property not present.", "ENGLISH", m.getProperty("language"));
        assertEquals("Mapping property not present.", "first and last name", m.getProperty("comment"));
    }

    public void testXmlClassExtractor() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLCLASSEXTRACTOR);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLCLASSEXTRACTOR + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        // Because the root element of the instance document, 'employee', is different than
        // the default root element for the Person descriptor (which is the class the
        // ClassExtractor is returning), we will get a JAXBElement from the unmarshal.

        InputStream is = ClassLoader.getSystemResourceAsStream(XMLCLASSEXTRACTOR_INSTANCE);
        DynamicEntity emp = (DynamicEntity )jaxbContext.createUnmarshaller().unmarshal(is);

        // Ensure that the JAXBElement's value's type is Person (as specified by the ClassExtractor)
        assertEquals("JAXBElement's declaredClass", "mynamespace.Employee", emp.getClass().getName());
    }

    public void testXmlCustomizer() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLCUSTOMIZER);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLCUSTOMIZER + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Rahm Emmanuel");

        // Ensure that the Descriptor customizer changed the XPath for name
        // to 'contact-info/name/text()'

        String name = jaxbContext.getValueByXPath(person, "contact-info/personal-info/name/text()", null, String.class);
        assertNotNull("'name' element was not found at the customized XPath.", name);
    }

    public void testXmlAdapter() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLADAPTER_PACKAGE);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLADAPTER_PACKAGE + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        Computer computer = new Computer();
        computer.ipCode = 121531298;
        computer.macCode = 48261593;
        computer.workgroup = 'C';

        String STREET = "33 Mason St.";
        String CITY = "Buffalo";
        String STATE = "NY";
        String ZIP = "33333";

        DynamicEntity address = jaxbContext.newDynamicEntity(PACKAGE + "." + ADDRESS);
        assertNotNull("Could not create Dynamic Entity.", address);
        address.set("street", STREET);
        address.set("city", CITY);
        address.set("state", STATE);
        address.set("zip", ZIP);

        person.set("name", "Jim Watson");
        person.set("computer", computer);
        person.set("address", address);

        LinkAdapter.jc = jaxbContext;

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);
        DynamicEntity readAddress = readPerson.get("address");

        assertEquals("Error adapting Link to Address", STREET, readAddress.get("street"));
        assertEquals("Error adapting Link to Address", CITY, readAddress.get("city"));
        assertEquals("Error adapting Link to Address", STATE, readAddress.get("state"));
        assertEquals("Error adapting Link to Address", ZIP, readAddress.get("zip"));
    }

    public void testXmlAdapterOnElement() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLADAPTER_ELEMENT);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLADAPTER_ELEMENT + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        Computer computer = new Computer();
        computer.ipCode = 121531298;
        computer.macCode = 48261593;
        computer.workgroup = 'C';

        String STREET = "33 Mason St.";
        String CITY = "Buffalo";
        String STATE = "NY";
        String ZIP = "33333";

        DynamicEntity address = jaxbContext.newDynamicEntity(PACKAGE + "." + ADDRESS);
        assertNotNull("Could not create Dynamic Entity.", address);
        address.set("street", STREET);
        address.set("city", CITY);
        address.set("state", STATE);
        address.set("zip", ZIP);

        person.set("name", "Jim Watson");
        person.set("computer", computer);
        person.set("address", address);

        LinkAdapterString.jc = jaxbContext;

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);
        DynamicEntity readAddress = readPerson.get("address");

        assertEquals("Error adapting Link to Address", STREET, readAddress.get("street"));
        assertEquals("Error adapting Link to Address", CITY, readAddress.get("city"));
        assertEquals("Error adapting Link to Address", STATE, readAddress.get("state"));
        assertEquals("Error adapting Link to Address", ZIP, readAddress.get("zip"));
    }

    public void testXmlDiscriminatorNode() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLDISCRIMINATORNODE);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLDISCRIMINATORNODE + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);
        person.set("name", "Tim Geithner");

        DynamicEntity customer = jaxbContext.newDynamicEntity(PACKAGE + "." + CUSTOMER);
        assertNotNull("Could not create Dynamic Entity.", customer);
        customer.set("name", "Jack Ruby");
        customer.set("custID", 3987);

        DynamicEntity vipCustomer = jaxbContext.newDynamicEntity(PACKAGE + "." + VIPCUSTOMER);
        assertNotNull("Could not create Dynamic Entity.", vipCustomer);
        vipCustomer.set("name", "Bob Dobbs");
        vipCustomer.set("vipCode", "CA2472");

        Document personDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, personDoc);
        Document customerDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(customer, customerDoc);
        Document vipCustomerDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(vipCustomer, vipCustomerDoc);

        Node classIndicatorNode = personDoc.getDocumentElement().getAttributes().getNamedItem("ptype");
        assertNotNull("Class indicator attribute not found.", classIndicatorNode);
        assertTrue("Person had incorrect or missing class indicator field.", classIndicatorNode.getNodeName() != "P");

        classIndicatorNode = customerDoc.getDocumentElement().getAttributes().getNamedItem("ptype");
        assertNotNull("Class indicator attribute not found.", classIndicatorNode);
        assertTrue("Customer had incorrect or missing class indicator field.", classIndicatorNode.getNodeName() != "C");

        classIndicatorNode = vipCustomerDoc.getDocumentElement().getAttributes().getNamedItem("ptype");
        assertNotNull("Class indicator attribute not found.", classIndicatorNode);
        assertTrue("VIPCustomer had incorrect or missing class indicator attribute.", classIndicatorNode.getNodeName() != "E");
    }

    public void testXmlJoinNode() throws Exception {
        // Tests xml-join-node, xml-join-nodes, xml-key
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLJOINNODE);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLJOINNODE + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity company = exampleCompany();
        assertNotNull("Could not create Dynamic Entity.", company);

        Document companyDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(company, companyDoc);

        DynamicEntity readCompany = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(companyDoc);

        ArrayList<DynamicEntity> readEmployees = readCompany.get("employees");
        for (Iterator iterator = readEmployees.iterator(); iterator.hasNext();) {
            DynamicEntity emp = (DynamicEntity) iterator.next();
            DynamicEntity add = emp.get("address");

            String idString = ((Integer)emp.get("id")).toString();
            // Employee # xxxx  ==> Address # xxxxx
            String expectedAddressIdString = idString + idString.charAt(0);

            assertEquals("Incorrect Employee/Address relationship.", ((Integer)add.get("id")).toString(), expectedAddressIdString);
        }
    }

    public void testXmlInverseReference() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(XMLINVERSEREFERENCE);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLINVERSEREFERENCE + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);
        person.set("name", "Tony Robbins");

        DynamicEntity address = jaxbContext.newDynamicEntity(PACKAGE + "." + ADDRESS);
        assertNotNull("Could not create Dynamic Entity.", address);
        address.set("value", "123 Main Street");
        address.set("person", person);
        person.set("address", address);

        DynamicEntity phone1 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        assertNotNull("Could not create Dynamic Entity.", phone1);
        phone1.set("value", "4167728728");
        phone1.set("person", person);
        DynamicEntity phone2 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        assertNotNull("Could not create Dynamic Entity.", phone2);
        phone2.set("value", "4162988971");
        phone2.set("person", person);

        Vector<DynamicEntity> phones =  new Vector<DynamicEntity>(2);
        phones.add(phone1);
        phones.add(phone2);
        person.set("phoneNumbers", phones);

        Document personDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, personDoc);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(personDoc);
        DynamicEntity readAddress = readPerson.get("address");
        assertEquals("Backpointer was not properly set.", readPerson, readAddress.get("person"));

        Vector<DynamicEntity> readPhones = (Vector<DynamicEntity>) readPerson.get("phoneNumbers");
        for (DynamicEntity readPhone : readPhones) {
            assertEquals("Backpointer was not properly set.", readPerson, readPhone.get("person"));
        }
    }

    public void testBinary() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(BINARY);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + BINARY + "]");
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        byte[] b = new byte[] {30,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4,1,2,3,4};

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);
        person.set("name", "Tony Robbins");
        person.set("abyte", (byte) 19882);
        person.set("base64", b);
        person.set("hex", b);

        Document personDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, personDoc);

        // Check Binary formats
        String base64Str =
            XMLConversionManager.getDefaultXMLManager().convertObject(b, String.class, XMLConstants.BASE_64_BINARY_QNAME).toString();
        String hexStr =
            XMLConversionManager.getDefaultXMLManager().convertObject(b, String.class, XMLConstants.HEX_BINARY_QNAME).toString();

        Element docElement = personDoc.getDocumentElement();
        Element base64Elem = (Element) docElement.getElementsByTagName("base64").item(0);
        Element hexElem = (Element) docElement.getElementsByTagName("hex").item(0);

        assertEquals(base64Str, base64Elem.getFirstChild().getNodeValue());
        assertEquals(hexStr, hexElem.getFirstChild().getNodeValue());
    }

    /**
     * Ensure that using unmarshal(XMLStreamReader, Class) works in a Dynamic context.
     * Create a dummy Descriptor with a concrete Java class, add it to the session, and then
     * try to unmarshal using that class.  An empty instance of that concrete class should be returned.
     */
    public void testUnmarshalWithClass() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(OXM_METADATA);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, iStream);

        DynamicJAXBContext jc = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        Unmarshaller unmarshaller = jc.createUnmarshaller();

        InputStream xmlStream = classLoader.getResourceAsStream(XML_RESOURCE_BEFORE);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        Document xsdDocument = docFactory.newDocumentBuilder().parse(xmlStream);
        Source domSource = new DOMSource(xsdDocument);

        XMLDescriptor dummy = new XMLDescriptor();
        dummy.setJavaClass(org.eclipse.persistence.testing.jaxb.employee.Employee.class);
        jc.getXMLContext().getSession(0).addDescriptor(dummy);

        Object o = unmarshaller.unmarshal(domSource, org.eclipse.persistence.testing.jaxb.employee.Employee.class);
        JAXBElement jelem = (JAXBElement) o;

        assertEquals(org.eclipse.persistence.testing.jaxb.employee.Employee.class, jelem.getValue().getClass());
    }

    // Utility methods
    // ====================================================================

    private DynamicEntity exampleCompany() {
        DynamicEntity address1 = jaxbContext.newDynamicEntity(PACKAGE + "." + ADDRESS);
        address1.set("id", 11111);
        address1.set("city", "Houston");
        DynamicEntity address2 = jaxbContext.newDynamicEntity(PACKAGE + "." + ADDRESS);
        address2.set("id", 22222);
        address2.set("city", "Toronto");
        DynamicEntity address3 = jaxbContext.newDynamicEntity(PACKAGE + "." + ADDRESS);
        address3.set("id", 33333);
        address3.set("city", "Kuopio");

        DynamicEntity employee1 = jaxbContext.newDynamicEntity(PACKAGE + "." + EMPLOYEE);
        employee1.set("id", 1111);
        employee1.set("name", "J.R. Ewing");
        employee1.set("address", address1);
        DynamicEntity employee2 = jaxbContext.newDynamicEntity(PACKAGE + "." + EMPLOYEE);
        employee2.set("id", 2222);
        employee2.set("name", "Bud Mackenzie");
        employee2.set("address", address2);
        DynamicEntity employee3 = jaxbContext.newDynamicEntity(PACKAGE + "." + EMPLOYEE);
        employee3.set("id", 3333);
        employee3.set("name", "Tarja Turunen");
        employee3.set("address", address3);

        ArrayList addresses = new ArrayList(3);
        addresses.add(address1);
        addresses.add(address2);
        addresses.add(address3);

        ArrayList employees = new ArrayList(3);
        employees.add(employee1);
        employees.add(employee2);
        employees.add(employee3);

        DynamicEntity company = jaxbContext.newDynamicEntity(PACKAGE + "." + COMPANY);
        company.set("name", "ACME Inc.");
        company.set("employees", employees);
        company.set("addresses", addresses);

        return company;
    }

    private void print(Object o) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(o, System.err);
    }

    // ====================================================================

    private static final String RESOURCE_DIR = "org/eclipse/persistence/testing/jaxb/dynamic/";

    // Schema files used to test each feature
    private static final String DATATYPES = RESOURCE_DIR + "datatypes-oxm.xml";
    private static final String XMLSCHEMA_QUALIFIED = RESOURCE_DIR + "xmlschema-qualified-oxm.xml";
    private static final String XMLSCHEMA_UNQUALIFIED = RESOURCE_DIR + "xmlschema-unqualified-oxm.xml";
    private static final String XMLSCHEMA_DEFAULTS = RESOURCE_DIR + "xmlschema-defaults-oxm.xml";
    private static final String XMLROOTELEMENT = RESOURCE_DIR + "xmlrootelement-oxm.xml";
    private static final String XMLTYPE = RESOURCE_DIR + "xmltype-oxm.xml";
    private static final String XMLATTRIBUTE = RESOURCE_DIR + "xmlattribute-oxm.xml";
    private static final String XMLELEMENT = RESOURCE_DIR + "xmlelement-oxm.xml";
    private static final String XMLVALUE = RESOURCE_DIR + "xmlvalue-oxm.xml";
    private static final String XMLANYELEMENT = RESOURCE_DIR + "xmlanyelement-oxm.xml";
    private static final String XMLANYATTRIBUTE = RESOURCE_DIR + "xmlanyattribute-oxm.xml";
    private static final String XMLMIXED = RESOURCE_DIR + "xmlmixed-oxm.xml";
    private static final String XMLID = RESOURCE_DIR + "xmlid-oxm.xml";
    private static final String XMLELEMENTS = RESOURCE_DIR + "xmlelements-oxm.xml";
    private static final String XMLSCHEMATYPE = RESOURCE_DIR + "xmlschematype-oxm.xml";
    private static final String XMLENUM = RESOURCE_DIR + "xmlenum-oxm.xml";
    private static final String XMLELEMENTDECL = RESOURCE_DIR + "xmlelementdecl-oxm.xml";
    private static final String XMLREADONLY = RESOURCE_DIR + "xmlreadonly-oxm.xml";
    private static final String XMLWRITEONLY = RESOURCE_DIR + "xmlwriteonly-oxm.xml";
    private static final String XMLCDATA = RESOURCE_DIR + "xmlCDATA-oxm.xml";
    private static final String XMLPATH = RESOURCE_DIR + "xmlpath-oxm.xml";
    private static final String XMLNULLPOLICY = RESOURCE_DIR + "xmlnullpolicy-oxm.xml";
    private static final String XMLNULLPOLICY_INSTANCE = RESOURCE_DIR + "xmlnullpolicy-instance.xml";
    private static final String XMLPROPERTIES = RESOURCE_DIR + "xmlproperties-oxm.xml";
    private static final String XMLCLASSEXTRACTOR = RESOURCE_DIR + "xmlclassextractor-oxm.xml";
    private static final String XMLCLASSEXTRACTOR_INSTANCE = RESOURCE_DIR + "xmlclassextractor-instance.xml";
    private static final String XMLCUSTOMIZER = RESOURCE_DIR + "xmlcustomizer-oxm.xml";
    private static final String XMLADAPTER_PACKAGE = RESOURCE_DIR + "xmladapter-package-oxm.xml";
    private static final String XMLADAPTER_ELEMENT = RESOURCE_DIR + "xmladapter-element-oxm.xml";
    private static final String XMLDISCRIMINATORNODE  = RESOURCE_DIR + "xmldiscriminatornode-oxm.xml";
    private static final String XMLJOINNODE = RESOURCE_DIR + "xmljoinnode-oxm.xml";
    private static final String XMLINVERSEREFERENCE = RESOURCE_DIR + "xmlinversereference-oxm.xml";
    private static final String XMLLIST = RESOURCE_DIR + "xmllist-oxm.xml";
    private static final String XMLELEMENTREF = RESOURCE_DIR + "xmlelementref-oxm.xml";
    private static final String SUBSTITUTION = RESOURCE_DIR + "substitution-oxm.xml";
    private static final String BINARY = RESOURCE_DIR + "binary-oxm.xml";

    private static final String OXM_METADATA = RESOURCE_DIR + "metadata.xml";


    // Test Instance Docs
    private static final String PERSON_XML = RESOURCE_DIR + "sub-person-en.xml";
    private static final String PERSONNE_XML = RESOURCE_DIR + "sub-personne-fr.xml";
    private static final String XML_RESOURCE_BEFORE = RESOURCE_DIR + "before.xml";

    // Names of types used in test cases
    private static final String PACKAGE = "mynamespace";
    private static final String PERSON = "Person";
    private static final String INDIVIDUO = "individuo";
    private static final String EMPLOYEE = "Employee";
    private static final String PHONE = "Phone";
    private static final String CUSTOMER = "Customer";
    private static final String VIPCUSTOMER = "VIPCustomer";
    private static final String CDN_CURRENCY = "CdnCurrency";
    private static final String COMPANY = "Company";
    private static final String ADDRESS = "Address";
    private static final String DATA = "Data";
    private static final String COMPASS_DIRECTION = "CompassDirection";

}
