/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
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

public class DynamicJAXBFromOXMTestCases extends TestCase {

    private static final String RESOURCE_DIR = "org/eclipse/persistence/testing/jaxb/dynamic/";

    // Schema files used to test each annotation
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

    private static final String DATATYPES = RESOURCE_DIR + "datatypes-oxm.xml";

    // Names of types to instantiate
    private static final String PACKAGE = "mynamespace";
    private static final String PERSON = "Person";
    private static final String CDN_CURRENCY = "CdnCurrency";

    DynamicJAXBContext jaxbContext;

    public DynamicJAXBFromOXMTestCases(String name) throws Exception {
        super(name);
    }

    public void testXmlSchemaQualified() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(XMLSCHEMA_QUALIFIED);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLSCHEMA_QUALIFIED + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 456);
        person.set("name", "Bob Dobbs");
        person.set("salary", 45000.00);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // Make sure "targetNamespace" was interpreted properly.
        Node node = marshalDoc.getChildNodes().item(0);
        assertEquals("Target Namespace was not set as expected.", "mynamespace", node.getNamespaceURI());

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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(XMLSCHEMA_UNQUALIFIED);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLSCHEMA_UNQUALIFIED + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 456);
        person.set("name", "Bob Dobbs");
        person.set("salary", 45000.00);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        // Make sure "targetNamespace" was interpreted properly.
        Node node = marshalDoc.getChildNodes().item(0);
        assertEquals("Target Namespace was not set as expected.", "mynamespace", node.getNamespaceURI());

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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(XMLSCHEMA_DEFAULTS);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLSCHEMA_DEFAULTS + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        // Testing defaults, so don't specify a package name (default package)
        metadataSourceMap.put("", new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
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

    public void testXmlSeeAlso() throws Exception {
        fail("Not Implemented - bug 322285");
    }

    public void testXmlRootElement() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(XMLROOTELEMENT);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLROOTELEMENT + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

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
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

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
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

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
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

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
        fail("Not implemented - bug 322284");
    }

    public void testXmlList() throws Exception {
        fail("Not implemented - bug 322284");
    }

    public void testXmlValue() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(XMLVALUE);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLVALUE + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

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
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

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
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

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
        fail("Not implemented - bug 322284");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(XMLMIXED);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + XMLMIXED + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("name", "Bob Dobbs");

        /*ArrayList list = new ArrayList();
        list.add("Hello");
        list.add(new JAXBElement<String>(new QName("myNamespace", "title"), String.class, person.getClass(), "MR"));
        list.add(new JAXBElement<String>(new QName("myNamespace", "name"), String.class, person.getClass(), "Bob Dobbs"));
        list.add(", your point balance is");
        list.add(new JAXBElement<BigInteger>(new QName("myNamespace", "rewardPoints"), BigInteger.class, person.getClass(), BigInteger.valueOf(175)));
        list.add("Visit www.rewards.com!");
        person.set("mixed", list);*/

        ArrayList list = new ArrayList();
        list.add("Hello");
        list.add("Visit www.rewards.com!");
        person.set("mixed", list);

        print(person);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        Node node = marshalDoc.getDocumentElement();

        assertTrue("Unexpected number of elements.", node.getChildNodes().getLength() == 6);
    }

    public void testXmlId() throws Exception {
        throw new Exception("Not Implemented");
    }

    public void testXmlElements() throws Exception {
        throw new Exception("Not Implemented");
    }

    public void testXmlElementRef() throws Exception {
        throw new Exception("Not Implemented");
    }

    public void testXmlSchemaType() throws Exception {
        throw new Exception("Not Implemented");
    }

    public void testXmlEnum() throws Exception {
        throw new Exception("Not Implemented");
    }

    public void testXmlEnumError() throws Exception {
        throw new Exception("Not Implemented");
    }

    public void testXmlElementDecl() throws Exception {
        throw new Exception("Not Implemented");
    }

    public void testSchemaWithJAXBBindings() throws Exception {
        throw new Exception("Not Implemented");
    }

    public void testSubstitutionGroupsUnmarshal() throws Exception {
        throw new Exception("Not Implemented");
    }

    public void testSubstitutionGroupsMarshal() throws Exception {
        throw new Exception("Not Implemented");
    }

    // ====================================================================

    public void testTypePreservation() throws Exception {
        fail("Not implemented - bug 322284");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(DATATYPES);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + DATATYPES + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        // Testing defaults, so don't specify a package name (default package)
        metadataSourceMap.put("", new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        person.set("id", 456);
        person.set("name", "Bob Dobbs");
        person.set("salary", 45000.00);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);

        assertEquals("Property type was not preserved during unmarshal.", Integer.class, readPerson.<Object>get("id").getClass());
        assertEquals("Property type was not preserved during unmarshal.", Double.class, readPerson.<Object>get("salary").getClass());
    }

    // ====================================================================

    private void print(Object o) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(o, System.err);
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