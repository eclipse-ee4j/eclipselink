/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.MarshalException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.xml.sax.helpers.DefaultHandler;

public class MarshalSchemaValidationTestCases extends OXTestCase {

    static String SCHEMA = "org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd";

    private JAXBMarshaller marshaller;
    private Employee employee;

    public MarshalSchemaValidationTestCases(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        Class[] classes = {Employee.class};
        JAXBContext jc = JAXBContextFactory.createContext(classes, null);
        marshaller = (JAXBMarshaller) jc.createMarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream stream = ClassLoader.getSystemResourceAsStream(SCHEMA);
        Schema schema = sf.newSchema(new StreamSource(stream));
        stream.close();
        marshaller.setSchema(schema);

        employee = new Employee();
        employee.setID(1234567);
    }

    public void testFailOnSecondErrorContentHandler() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        marshaller.setEventHandler(eventHandler);
        try {
            marshaller.marshal(employee, new DefaultHandler());
        } catch (JAXBException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        } catch(Exception e) {
            throw e;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorNode() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        marshaller.setEventHandler(eventHandler);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.newDocument();
            marshaller.marshal(employee, document);
        } catch (JAXBException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        } catch(Exception e) {
            throw e;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorOutputStream() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        marshaller.setEventHandler(eventHandler);
        try {
            marshaller.marshal(employee, new ByteArrayOutputStream());
        } catch (JAXBException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        } catch(Exception e) {
            throw e;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorResult() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        marshaller.setEventHandler(eventHandler);
        try {
            marshaller.marshal(employee, new StreamResult(new ByteArrayOutputStream()));
        } catch (JAXBException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        } catch(Exception e) {
            throw e;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorWriter() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        marshaller.setEventHandler(eventHandler);
        try {
            marshaller.marshal(employee, new StringWriter());
        } catch (JAXBException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        } catch(Exception e) {
            throw e;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorXMLEventWriter() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        marshaller.setEventHandler(eventHandler);
        try {
            if(null == XML_OUTPUT_FACTORY) {
                return;
            }
            XMLEventWriter xmlEventWriter = XML_OUTPUT_FACTORY.createXMLEventWriter(new ByteArrayOutputStream());
            marshaller.marshal(employee, xmlEventWriter);
        } catch (JAXBException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        } catch(XMLStreamException e) {
            return;
        } catch(Exception e) {
            throw e;
        }
        fail("No Exceptions thrown.");
    }

    public void testFailOnSecondErrorXMLStreamWriter() throws Exception {
        CustomErrorValidationEventHandler eventHandler = new CustomErrorValidationEventHandler();
        marshaller.setEventHandler(eventHandler);
        try {
            if(null == XML_OUTPUT_FACTORY) {
                return;
            }
            XMLStreamWriter xmlStreamWriter = XML_OUTPUT_FACTORY.createXMLStreamWriter(new ByteArrayOutputStream());
            marshaller.marshal(employee, xmlStreamWriter);
        } catch (JAXBException ex) {
            assertEquals(2, eventHandler.getErrorCount());
            return;
        } catch(XMLStreamException e) {
            return;
        } catch(Exception e) {
            throw e;
        }
        fail("No Exceptions thrown.");
    }

}