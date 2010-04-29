/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DynamicJAXBFromXSDTestCases extends TestCase {

    private static final String RESOURCE_DIR = "org/eclipse/persistence/testing/jaxb/dynamic/";
    private static final String CONTEXT_PATH = "mynamespace";

    // Schema files used to test each annotation
    private static final String XMLSCHEMA_QUALIFIED = RESOURCE_DIR + "xmlschema-qualified.xsd";
    private static final String XMLSCHEMA_UNQUALIFIED = RESOURCE_DIR +  "xmlschema-unqualified.xsd";
    private static final String XMLSCHEMA_DEFAULTS = RESOURCE_DIR + "xmlschema-defaults.xsd";
    private static final String XMLSCHEMA_IMPORT = RESOURCE_DIR + "xmlschema-import.xsd";
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
    private static final String XMLELEMENTDECL = RESOURCE_DIR + "xmlelementdecl.xsd";
    private static final String XMLELEMENTCOLLECTION = RESOURCE_DIR + "xmlelement-collection.xsd";

    // Names of types to instantiate
    private static final String PACKAGE = "mynamespace";
    private static final String PERSON = "Person";
    private static final String EMPLOYEE = "Employee";
    private static final String INDIVIDUO = "Individuo";
    private static final String CDN_CURRENCY = "CdnCurrency";
    private static final String DATA = "Data";
    private static final String COMPANY = "Company";
    private static final String COMPASS_DIRECTION = "CompassDirection";
    private static final String NORTH_CONSTANT = "NORTH";

    DynamicJAXBContext jaxbContext;

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

        Node attr = node.getAttributes().item(0);
        assertNull("Attribute should not have namespace prefix.", attr.getPrefix());

        Node childNode = node.getChildNodes().item(0);
        assertNull("Child node should not have namespace prefix.", childNode.getPrefix());
    }

    public void testXmlSchemaDefaults() throws Exception {
        // <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLSCHEMA_DEFAULTS);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PERSON);
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

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLSCHEMA_IMPORT);

        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, new NoExtensionEntityResolver(), null, null);

        DynamicEntity person = jaxbContext.newDynamicEntity(PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        DynamicEntity salary = jaxbContext.newDynamicEntity(CDN_CURRENCY);
        assertNotNull("Could not create Dynamic Entity.", salary);

        salary.set("value", "75425.75");

        person.set("name", "Bob Dobbs");
        person.set("salary", salary);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);
        
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
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

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

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

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
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

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
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

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
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

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
        Map<String, Map<String, Source>> props = new HashMap<String, Map<String, Source>>();
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

        salary.set("value", 75100.25);

        person.set("name", "Bob Dobbs");
        person.set("salary", salary);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

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

        assertTrue("Unexpected number of attributes.", node.getAttributes().getLength() == 3);
    }

    public void testXmlMixed() throws Exception {
        // Also tests XmlElementRef / XmlElementRefs

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(XMLMIXED);

        try {
            jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(inputStream, null, null, null);
        } catch (UndeclaredThrowableException ute) {
            // If running in a non-JAXB 2.2 environment, we will get this error because the required() method
            // on @XmlElementRef is missing.  Just ignore this and pass the test.
            return;
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
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getChildNodes().item(0);

        assertEquals("Root element was not 'individuo' as expected.", "individuo", node.getLocalName());
    }

    private void print(Object o) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(o, System.out);
    }

    private class NoExtensionEntityResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            // Grab only the filename part from the full path
            File f = new File(systemId);

            String correctedId = RESOURCE_DIR + f.getName() + ".xsd";

            InputSource is = new InputSource(ClassLoader.getSystemResourceAsStream(correctedId));
            is.setSystemId(correctedId);
            return is;
        }
    }

}