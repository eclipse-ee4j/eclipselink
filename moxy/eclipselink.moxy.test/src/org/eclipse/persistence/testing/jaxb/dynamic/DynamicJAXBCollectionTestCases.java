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
 *     rbarkhouse - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.dynamic.util.MyList;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class DynamicJAXBCollectionTestCases extends TestCase {

    public DynamicJAXBCollectionTestCases(String name) throws Exception {
        super(name);
    }

    public String getName() {
        return "Dynamic JAXB: Collections: " + super.getName();
    }

    public void testXSDSingleListUnmarshal() throws Exception {
        InputStream schemaStream = classLoader.getSystemResourceAsStream(XSD_SINGLE);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(schemaStream, null, null, null);

        InputStream xmlStream = classLoader.getSystemResourceAsStream(XSD_SINGLE_INSTANCE);
        JAXBElement<DynamicEntity> jaxbElement = (JAXBElement<DynamicEntity>) jaxbContext.createUnmarshaller().unmarshal(xmlStream);

        DynamicEntity customer = jaxbElement.getValue();
        assertNotNull("Could not create Dynamic Entity.", customer);

        List<DynamicEntity> phoneNumbers = customer.<List<DynamicEntity>>get("phoneNumbers");
        assertEquals("Unexpected number of phoneNumbers returned.", 3, phoneNumbers.size());
        DynamicEntity firstPhoneNumber = phoneNumbers.get(0);
        assertEquals("Incorrect phoneNumber type.", "work", firstPhoneNumber.get("type"));
    }

    public void testXSDMultipleListUnmarshal() throws Exception {
        InputStream schemaStream = classLoader.getSystemResourceAsStream(XSD_MULTI);
        jaxbContext = DynamicJAXBContextFactory.createContextFromXSD(schemaStream, null, null, null);

        InputStream xmlStream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(XSD_MULTI_INSTANCE);
        JAXBElement<DynamicEntity> jaxbElement = (JAXBElement<DynamicEntity>) jaxbContext.createUnmarshaller().unmarshal(xmlStream);

        DynamicEntity customer = jaxbElement.getValue();
        assertNotNull("Could not create Dynamic Entity.", customer);

        List<DynamicEntity> addresses = customer.<List<DynamicEntity>>get("addresses");
        assertEquals("Unexpected number of addresses returned.", 2, addresses.size());
        DynamicEntity firstAddress = addresses.get(0);
        assertEquals("Incorrect address city.", "Any Town", firstAddress.get("city"));

        List<DynamicEntity> phoneNumbers = customer.<List<DynamicEntity>>get("phoneNumbers");
        assertEquals("Unexpected number of phoneNumbers returned.", 3, phoneNumbers.size());
        DynamicEntity firstPhoneNumber = phoneNumbers.get(0);
        assertEquals("Incorrect phoneNumber type.", "work", firstPhoneNumber.get("type"));

        List<DynamicEntity> addresses2 = customer.<List<DynamicEntity>>get("addresses2");
        assertEquals("Unexpected number of addresses returned.", 1, addresses2.size());
        DynamicEntity firstAddress2 = addresses2.get(0);
        assertEquals("Incorrect address city.", "Big City", firstAddress2.get("city"));
    }

    public void testOXMSingleListVector() throws Exception {
        InputStream iStream = classLoader.getResourceAsStream(OXM_SINGLE_VECTOR);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + OXM_SINGLE_VECTOR + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        DynamicEntity phone1 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone1.set("type", "HOME"); phone1.set("number", "1234567890");

        DynamicEntity phone2 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone2.set("type", "WORK"); phone2.set("number", "1235678904");

        Vector<DynamicEntity> phones = new Vector<DynamicEntity>(3);
        phones.add(phone1); phones.add(phone2);

        person.set("phones", phones);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);
        Vector<DynamicEntity> readPhones = readPerson.get("phones");
        assertEquals("Unexpected number of phoneNumbers returned.", 2, readPhones.size());
        DynamicEntity firstPhoneNumber = readPhones.get(0);
        assertEquals("Incorrect phoneNumber type.", "HOME", firstPhoneNumber.get("type"));
    }

    public void testOXMSingleListSet() throws Exception {
        InputStream iStream = classLoader.getResourceAsStream(OXM_SINGLE_SET);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + OXM_SINGLE_SET + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        DynamicEntity phone1 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone1.set("type", "HOME"); phone1.set("number", "1234567890");

        DynamicEntity phone2 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone2.set("type", "WORK"); phone2.set("number", "1235678904");

        HashSet<DynamicEntity> phones = new HashSet<DynamicEntity>(3);
        phones.add(phone1); phones.add(phone2);

        person.set("phones", phones);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);
        Set<DynamicEntity> readPhones = readPerson.get("phones");
        assertEquals("Unexpected number of phoneNumbers returned.", 2, readPhones.size());
    }

    public void testOXMSingleListList() throws Exception {
        InputStream iStream = classLoader.getResourceAsStream(OXM_SINGLE_LIST);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + OXM_SINGLE_LIST + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        DynamicEntity phone1 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone1.set("type", "HOME"); phone1.set("number", "1234567890");

        DynamicEntity phone2 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone2.set("type", "WORK"); phone2.set("number", "1235678904");

        LinkedList<DynamicEntity> phones = new LinkedList<DynamicEntity>();
        phones.add(phone1); phones.add(phone2);

        person.set("phones", phones);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);
        LinkedList<DynamicEntity> readPhones = readPerson.get("phones");
        assertEquals("Unexpected number of phoneNumbers returned.", 2, readPhones.size());
        DynamicEntity firstPhoneNumber = readPhones.get(0);
        assertEquals("Incorrect phoneNumber type.", "HOME", firstPhoneNumber.get("type"));
    }

    public void testOXMSingleListCustom() throws Exception {
        InputStream iStream = classLoader.getResourceAsStream(OXM_SINGLE_CUSTOM);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + OXM_SINGLE_CUSTOM + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(classLoader, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        DynamicEntity phone1 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone1.set("type", "HOME"); phone1.set("number", "1234567890");

        DynamicEntity phone2 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone2.set("type", "WORK"); phone2.set("number", "1235678904");

        MyList phones = new MyList();
        phones.add(phone1); phones.add(phone2);

        person.set("phones", phones);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);
        MyList readPhones = readPerson.get("phones");
        assertEquals("Unexpected number of phoneNumbers returned.", 2, readPhones.size());
        DynamicEntity firstPhoneNumber = (DynamicEntity) readPhones.get(0);
        assertEquals("Incorrect phoneNumber type.", "HOME", firstPhoneNumber.get("type"));
    }

    /*
    public void testOXMSingleListMap() throws Exception {
        InputStream iStream = classLoader.getResourceAsStream(OXM_SINGLE_MAP);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + OXM_SINGLE_MAP + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(PACKAGE, new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        JaxbClassLoader jcl = new JaxbClassLoader(classLoader);

        jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(jcl, properties);

        DynamicEntity person = jaxbContext.newDynamicEntity(PACKAGE + "." + PERSON);
        assertNotNull("Could not create Dynamic Entity.", person);

        DynamicEntity phone1 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone1.set("type", "HOME"); phone1.set("number", "1234567890");

        DynamicEntity phone2 = jaxbContext.newDynamicEntity(PACKAGE + "." + PHONE);
        phone2.set("type", "WORK"); phone2.set("number", "1235678904");

        HashMap<String, DynamicEntity> phones = new HashMap<String, DynamicEntity>();
        phones.put("phone1", phone1); phones.put("phone2", phone2);

        person.set("phones", phones);

        print(person);

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        jaxbContext.createMarshaller().marshal(person, marshalDoc);

        DynamicEntity readPerson = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(marshalDoc);
        MyList readPhones = readPerson.get("phones");
        assertEquals("Unexpected number of phoneNumbers returned.", 2, readPhones.size());
        DynamicEntity firstPhoneNumber = (DynamicEntity) readPhones.get(0);
        assertEquals("Incorrect phoneNumber type.", "HOME", firstPhoneNumber.get("type"));
    }
    */

    // Utility methods
    // ====================================================================

    private void print(Object o) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(o, System.err);
    }

    private static String XSD_SINGLE = "org/eclipse/persistence/testing/jaxb/dynamic/SingleList.xsd";
    private static String XSD_SINGLE_INSTANCE = "org/eclipse/persistence/testing/jaxb/dynamic/SingleList.xml";
    private static String XSD_MULTI = "org/eclipse/persistence/testing/jaxb/dynamic/MultipleList.xsd";
    private static String XSD_MULTI_INSTANCE = "org/eclipse/persistence/testing/jaxb/dynamic/MultipleList.xml";

    private static String OXM_SINGLE_VECTOR = "org/eclipse/persistence/testing/jaxb/dynamic/singlelistvector-oxm.xml";
    private static String OXM_SINGLE_SET = "org/eclipse/persistence/testing/jaxb/dynamic/singlelistset-oxm.xml";
    private static String OXM_SINGLE_LIST = "org/eclipse/persistence/testing/jaxb/dynamic/singlelistlist-oxm.xml";
    private static String OXM_SINGLE_CUSTOM = "org/eclipse/persistence/testing/jaxb/dynamic/singlelistcustom-oxm.xml";
    private static String OXM_SINGLE_MAP = "org/eclipse/persistence/testing/jaxb/dynamic/singlelistmap-oxm.xml";

    private static final String PACKAGE = "mynamespace";
    private static final String PERSON = "Person";
    private static final String PHONE = "Phone";


    private DynamicJAXBContext jaxbContext;

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

}