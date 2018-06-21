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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;

import org.eclipse.persistence.oxm.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.UnmarshalException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class UnmarshalSchemaValidationTestCases extends OXTestCase {

    static String SCHEMA = "org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd";
    static String DOUBLE_ERROR_XML = "org/eclipse/persistence/testing/oxm/jaxb/Employee_TwoError.xml";

    private JAXBUnmarshaller unmarshaller;

    private Schema schema;

    public UnmarshalSchemaValidationTestCases(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        Class[] classes = {Employee.class};
        JAXBContext jc = JAXBContextFactory.createContext(classes, null);
        unmarshaller = (JAXBUnmarshaller) jc.createUnmarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream stream = ClassLoader.getSystemResourceAsStream(SCHEMA);
        Schema schema = sf.newSchema(new StreamSource(stream));
        stream.close();
        this.schema = schema;
    }

    public void testFailOnSecondErrorFile() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(Thread.currentThread().getContextClassLoader().getResource(DOUBLE_ERROR_XML));
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorInputSource() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        InputSource inputSource = new InputSource(stream);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(inputSource);
        } catch (UnmarshalException ex) {
            assertEquals(2,eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorNode() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        XMLParser xmlParser = xmlPlatform.newXMLParser();
        xmlParser.setNamespaceAware(true);
        Node node = xmlParser.parse(stream);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(node);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorNodeWithClass() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        XMLParser xmlParser = xmlPlatform.newXMLParser();
        xmlParser.setNamespaceAware(true);
        Node node = xmlParser.parse(stream);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(node, Employee.class);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorReader() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        InputStreamReader reader = new InputStreamReader(stream);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(reader);
        } catch (UnmarshalException ex) {
            assertEquals(eventHandler.getErrorCount(), 2);
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorSource() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        Source source = new StreamSource(stream);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(source);
        } catch (UnmarshalException ex) {
            assertEquals(eventHandler.getErrorCount(), 2);
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorSAXSource() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        Source source = new SAXSource(xr, new InputSource(stream));
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(source);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorSAXSourceWithClass() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        Source source = new SAXSource(xr, new InputSource(stream));
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(source, Employee.class);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorSAXSourceWithType() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        Source source = new SAXSource(xr, new InputSource(stream));
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(source, (Type) Employee.class);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorURL() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        URL url = ClassLoader.getSystemResource(DOUBLE_ERROR_XML);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(url);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorSourceWithClass() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        Source source = new StreamSource(stream);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(source, Employee.class);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorSourceWithType() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        Source source = new StreamSource(stream);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(source, (Type) Employee.class);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorXMLEventReader() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        XMLEventReader xmlEventReader = createXMLEventReader(stream);
        if(null == xmlEventReader) {
            return;
        }
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(xmlEventReader);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorXMLEventReaderWithClass() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        XMLEventReader xmlEventReader = createXMLEventReader(stream);
        if(null == xmlEventReader) {
            return;
        }
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(xmlEventReader, Employee.class);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorXMLEventReaderWithType() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        XMLEventReader xmlEventReader = createXMLEventReader(stream);
        if(null == xmlEventReader) {
            return;
        }
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(xmlEventReader, (Type) Employee.class);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorXMLStreamReader() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        XMLStreamReader xmlStreamReader = createXMLStreamReader(stream);
        if(null == xmlStreamReader) {
            return;
        }
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(xmlStreamReader);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorXMLStreamReaderWithClass() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        XMLStreamReader xmlStreamReader = createXMLStreamReader(stream);
        if(null == xmlStreamReader) {
            return;
        }
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(xmlStreamReader, Employee.class);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorXMLStreamReaderWithType() throws Exception {
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        XMLStreamReader xmlStreamReader = createXMLStreamReader(stream);
        if(null == xmlStreamReader) {
            return;
        }
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(xmlStreamReader, (Type) Employee.class);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorInputStream() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        unmarshaller.setEventHandler(eventHandler);
        InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
        try {
            unmarshaller.setSchema(this.schema);
            unmarshaller.unmarshal(stream);
        } catch (UnmarshalException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        }
        fail("No Exceptions thrown.");
    }

    private XMLEventReader createXMLEventReader(InputStream stream) {
        try {
            if(null == XML_INPUT_FACTORY) {
                return null;
            }
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(stream);
            return xmlEventReader;
        } catch(XMLStreamException e) {
            return null;
        }
    }

    private XMLStreamReader createXMLStreamReader(InputStream stream) {
        try {
            if(null == XML_INPUT_FACTORY) {
                return null;
            }
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(stream);
            return xmlStreamReader;
        } catch(XMLStreamException e) {
            return null;
        }
    }

}
