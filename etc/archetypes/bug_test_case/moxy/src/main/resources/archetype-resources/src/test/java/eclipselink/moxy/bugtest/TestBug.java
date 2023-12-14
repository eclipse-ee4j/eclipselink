/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package eclipselink.moxy.bugtest;

import eclipselink.moxy.bugtest.domain.Container;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.junit.Test;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestBug {

    private static final String JSON_ATTRIBUTE = "input_attribute_first.json";
    private static final String JSON_VALUE = "input_value_first.json";
    private static final String XML_READ_DOCUMENT = "input.xml";
    private static final String XMLSCHEMA_IMPORT = "input.xsd";
    private static final String DOMAIN_PACKAGE = "eclipselink.moxy.bugtest.domain";
    private static final Class<?>[] DOMAIN_CLASSES = new Class<?>[]{Container.class};

    private JAXBContext jaxbContext;


    @Test
    public void moxyXmlDocTest() throws Exception {
        System.setProperty(JAXBContext.JAXB_CONTEXT_FACTORY, "org.eclipse.persistence.jaxb.JAXBContextFactory");

//        System.setProperty(JAXBContext.JAXB_CONTEXT_FACTORY, "org.eclipse.persistence.jaxb.JAXBContextFactory");
//        System.setProperty(JAXBContext.JAXB_CONTEXT_FACTORY, "org.glassfish.jaxb.runtime.v2.ContextFactory");

//        System.setProperty("jakarta.xml.bind.JAXBContextFactory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
//        System.setProperty("jakarta.xml.bind.JAXBContextFactory", "org.glassfish.jaxb.runtime.v2.ContextFactory");

        final HashMap<String, Object> contextProperties = new HashMap<>();
        contextProperties.put(JAXBContextProperties.MEDIA_TYPE, "application/xml");

        jaxbContext = JAXBContext.newInstance(DOMAIN_CLASSES, contextProperties);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object unmarshallDoc = unmarshaller.unmarshal(Thread.currentThread().getContextClassLoader().getResource(XML_READ_DOCUMENT));

        verifyUnmarshalledObject(unmarshallDoc);

        StringWriter sw = marshallDoc(unmarshallDoc, contextProperties);
        System.out.println(sw);
        verifyMarshalledXmlDoc(sw);
        verifyMarshalledDocAgainstSchema(sw);
    }

    @Test
    public void moxyJsonDocAttributeFirstTest() throws Exception {
        System.setProperty(JAXBContext.JAXB_CONTEXT_FACTORY, "org.eclipse.persistence.jaxb.JAXBContextFactory");
        final HashMap<String, Object> contextProperties = new HashMap<>();
        contextProperties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        contextProperties.put(JAXBContextProperties.JSON_VALUE_WRAPPER, "value1");
//	    contextProperties.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "@");

        Object unmarshallDoc = unmarshallDoc(JSON_ATTRIBUTE, contextProperties);
        verifyUnmarshalledObject(unmarshallDoc);

        StringWriter sw = marshallDoc(unmarshallDoc, contextProperties);
        System.out.println(sw);
        verifyMarshalledJsonDoc(sw);
    }

    @Test
    public void moxyJsonDocValueFirstTest() throws Exception {
        System.setProperty(JAXBContext.JAXB_CONTEXT_FACTORY, "org.eclipse.persistence.jaxb.JAXBContextFactory");
        final HashMap<String, Object> contextProperties = new HashMap<>();
        contextProperties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        contextProperties.put(JAXBContextProperties.JSON_VALUE_WRAPPER, "value1");
//	    contextProperties.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "@");

        Object unmarshallDoc = unmarshallDoc(JSON_VALUE, contextProperties);
        verifyUnmarshalledObject(unmarshallDoc);

        StringWriter sw = marshallDoc(unmarshallDoc, contextProperties);
        System.out.println(sw);
        verifyMarshalledJsonDoc(sw);
    }

    private StringWriter marshallDoc(Object document , HashMap<String, Object> contextProperties) throws Exception {
        if (jaxbContext == null) {
            //Via package name. Needs ObjectFactory.
            //jaxbContext = JAXBContext.newInstance(DOMAIN_PACKAGE, this.getClass().getClassLoader(), contextProperties);
            //List of domain objects. ObjectFactory is not required.
            jaxbContext = JAXBContext.newInstance(DOMAIN_CLASSES, contextProperties);

        }

        final StringWriter sw = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(document, sw);

        return sw;
    }

    private Object unmarshallDoc(String documentFileName, HashMap<String, Object> contextProperties) throws Exception {
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance(DOMAIN_PACKAGE, this.getClass().getClassLoader(), contextProperties);
        }

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller.unmarshal(Thread.currentThread().getContextClassLoader().getResource(documentFileName));
    }

    private void verifyUnmarshalledObject(Object document) throws Exception {
        assertNotNull(document);
        assertEquals("some attribute", ((Container)document).getAttribute());
        assertEquals("some value", ((Container)document).getValue());
    }


    private void verifyMarshalledXmlDoc(StringWriter sw) throws Exception {
        assertTrue(sw.toString().indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\"?><C a=\"some attribute\">some value</C>") != -1);
    }

    private void verifyMarshalledJsonDoc(StringWriter sw) throws Exception {
        assertTrue(sw.toString().indexOf("{\"C\":{\"a\":\"some attribute\",\"value1\":\"some value\"}}") != -1);
    }


    private void verifyMarshalledDocAgainstSchema(StringWriter sw) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema testReqSchema = factory.newSchema(Thread.currentThread().getContextClassLoader().getResource(XMLSCHEMA_IMPORT));
        testReqSchema.newValidator().validate(new StreamSource(new StringReader(sw.getBuffer().toString())));

    }
}
