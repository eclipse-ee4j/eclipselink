/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.w3c.dom.Document;

public class DynamicJAXBUsingXMLNamesTestCases extends TestCase {

    public DynamicJAXBUsingXMLNamesTestCases(String name) throws Exception {
        super(name);
    }

    public String getName() {
        return "Dynamic JAXB: Using XML Names: " + super.getName();
    }

    public void testCreateEntityByXMLName() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(EXAMPLE_OXM);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + EXAMPLE_OXM + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.dynamic", new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.eclipse.persistence.testing.jaxb.dynamic", classLoader, properties);
        DynamicEntity employee = (DynamicEntity) jaxbContext.createByQualifiedName(EMPLOYEE_NAMESPACE, EMPLOYEE_TYPE_NAME, false);
        assertNotNull("Could not create Dynamic Entity.", employee);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "mynamespace");

        jaxbContext.setValueByXPath(employee, "name/text()", nsResolver, "Larry King");
        jaxbContext.setValueByXPath(employee, "employee-id/text()", nsResolver, "CA34287");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(employee, marshalDoc);

        DynamicEntity employee2 = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);
        String newName = jaxbContext.getValueByXPath(employee2, "name/text()", nsResolver, String.class);
        String newId = jaxbContext.getValueByXPath(employee2, "employee-id/text()", nsResolver, String.class);

        assertEquals("Larry King", newName);
        assertEquals("CA34287", newId);
    }
    
    public void testCreateEntityByXMLNameJSON() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(EXAMPLE_OXM);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + EXAMPLE_OXM + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.dynamic", new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.eclipse.persistence.testing.jaxb.dynamic", classLoader, properties);
        DynamicEntity employee = (DynamicEntity) jaxbContext.createByQualifiedName(EMPLOYEE_NAMESPACE, EMPLOYEE_TYPE_NAME, false);
        assertNotNull("Could not create Dynamic Entity.", employee);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "mynamespace");

        jaxbContext.setValueByXPath(employee, "name/text()", nsResolver, "Larry King");
        jaxbContext.setValueByXPath(employee, "employee-id/text()", nsResolver, "CA34287");

        JAXBMarshaller m = jaxbContext.createMarshaller();
        m.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        m.marshal(employee, marshalDoc);

        JAXBUnmarshaller u = jaxbContext.createUnmarshaller();
        u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        DynamicEntity employee2 = (DynamicEntity) u.unmarshal(marshalDoc);
        String newName = jaxbContext.getValueByXPath(employee2, "name/text()", nsResolver, String.class);
        String newId = jaxbContext.getValueByXPath(employee2, "employee-id/text()", nsResolver, String.class);

        assertEquals("Larry King", newName);
        assertEquals("CA34287", newId);
        
    }

    public void testCreateEntityByXPathNameCollision1() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(EXAMPLE_XSD_2);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + EXAMPLE_XSD_2 + "]");
        }

        Map<String, InputStream> properties = new HashMap<String, InputStream>();
        properties.put(DynamicJAXBContextFactory.XML_SCHEMA_KEY, iStream);

        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.eclipse.persistence.testing.jaxb.dynamic", classLoader, properties);
        DynamicEntity person = (DynamicEntity) jaxbContext.createByQualifiedName("mynamespace", "person", true);
        assertNotNull("Could not create Dynamic Entity.", person);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "mynamespace");

        jaxbContext.setValueByXPath(person, "ns0:full_name/text()", nsResolver, "Larry King");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity person2 = ((JAXBElement<DynamicEntity>) jaxbContext.createUnmarshaller().unmarshal(marshalDoc)).getValue();
        String newName = jaxbContext.getValueByXPath(person2, "ns0:full_name/text()", nsResolver, String.class);

        assertEquals("Larry King", newName);
    }

    public void testCreateEntityByXPathNameCollision2() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream iStream = classLoader.getResourceAsStream(EXAMPLE_XSD_2);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + EXAMPLE_XSD_2 + "]");
        }

        Map<String, InputStream> properties = new HashMap<String, InputStream>();
        properties.put(DynamicJAXBContextFactory.XML_SCHEMA_KEY, iStream);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "mynamespace");

        DynamicJAXBContext jaxbContext = (DynamicJAXBContext) JAXBContext.newInstance("org.eclipse.persistence.testing.jaxb.dynamic", classLoader, properties);
        DynamicEntity person = (DynamicEntity) jaxbContext.createByQualifiedName("mynamespace", "person", false);
        assertNotNull("Could not create Dynamic Entity.", person);

        // Set name by XPath
        jaxbContext.setValueByXPath(person, "ns0:first-name/text()", nsResolver, "Larry");

        // Create address by XPath, set street by XPath
        DynamicEntity address = jaxbContext.createByXPath(person, "ns0:address", nsResolver, DynamicEntity.class);
        jaxbContext.setValueByXPath(address, "ns0:street/text()", nsResolver, "400 CNN Plaza");

        // Create person's address by XPath
        jaxbContext.setValueByXPath(person, "ns0:address", nsResolver, address);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity person2 = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);
        DynamicEntity address2 = jaxbContext.getValueByXPath(person2, "ns0:address", nsResolver, DynamicEntity.class);
        String newName = jaxbContext.getValueByXPath(person2, "ns0:first-name/text()", nsResolver, String.class);
        String newStreet = jaxbContext.getValueByXPath(address2, "ns0:street/text()", nsResolver, String.class);
        String newStreet2 = jaxbContext.getValueByXPath(person2, "ns0:address/ns0:street/text()", nsResolver, String.class);

        assertEquals("Larry", newName);
        assertEquals("400 CNN Plaza", newStreet);
        assertEquals("400 CNN Plaza", newStreet2);
    }

    private void print(Object o, JAXBContext jaxbContext) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(o, System.err);
    }

    private static final String SESSION_NAMES =
        "org.eclipse.persistence.testing.jaxb.dynamic:org.eclipse.persistence.testing.jaxb.dynamic.secondproject";

    private static final String EXAMPLE_XSD =
        "org/eclipse/persistence/testing/jaxb/dynamic/contextcreation.xsd";
    private static final String EXAMPLE_XSD_2 =
        "org/eclipse/persistence/testing/jaxb/dynamic/contextcreation2.xsd";
    private static final String EXAMPLE_OXM =
        "org/eclipse/persistence/testing/jaxb/dynamic/contextcreation-oxm.xml";

    private static final String EMPLOYEE_CLASS_NAME =
        "mynamespace.Employee";
    private static final String EMPLOYEE_NAMESPACE = "mynamespace";
    private static final String EMPLOYEE_TYPE_NAME = "employee-data";
    private static final String PERSON_CLASS_NAME =
        "org.eclipse.persistence.testing.jaxb.dynamic.Person";

}