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
//     Denise Smith -  July 9, 2009 Initial test
package org.eclipse.persistence.testing.jaxb.singleobject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.persistence.jaxb.JAXBUnmarshallerHandler;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class JAXBSingleObjectIntegerXsiTestCases extends JAXBWithJSONTestCases {

    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObjectXsiType.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/singleobject/singleObjectXsiType.json";

    public JAXBSingleObjectIntegerXsiTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[1];
        classes[0] = Object.class;
        setClasses(classes);

        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi");
        namespaces.put("rootNamespace", "ns0");
        getJSONUnmarshaller().setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
        getJSONMarshaller().setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
    }

    public void testSchemaGen() throws Exception {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);

        assertEquals("A Schema was generated but should not have been", 0, outputResolver.getSchemaFiles().size());
    }

    public List<InputStream> getControlSchemaFiles() {
        //not applicable for this test since we override testSchemaGen
        return null;
    }
    public Object getWriteControlObject() {
        return getControlObject();
    }

    protected Object getControlObject() {
        Integer testInteger = 25;
        QName qname = new QName("rootNamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, testInteger);
        return jaxbElement;
    }

    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromURL() throws Exception {
        java.net.URL url = ClassLoader.getSystemResource(resourceName);
        Object testObject = jaxbUnmarshaller.unmarshal(url);
        xmlToObjectTest(testObject);
    }


    public void testUnmarshallerHandler() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        SAXParser saxParser = saxParserFactory.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();

        JAXBUnmarshallerHandler jaxbUnmarshallerHandler = (JAXBUnmarshallerHandler)jaxbUnmarshaller.getUnmarshallerHandler();
        xmlReader.setContentHandler(jaxbUnmarshallerHandler);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
        InputSource inputSource = new InputSource(inputStream);
        xmlReader.parse(inputSource);

        xmlToObjectTest(jaxbUnmarshallerHandler.getResult());
    }


    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        Object testObject = jaxbUnmarshaller.unmarshal(instream);
        instream.close();
        xmlToObjectTest(testObject);
    }


}
