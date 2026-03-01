/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.keybased;

import org.eclipse.persistence.oxm.XMLUnmarshallerHandler;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

public abstract class KeyBasedMappingTestCases extends XMLMappingTestCases {
    protected static final String CONTROL_ID = "222";
    protected static final String CONTROL_NAME = "Joe Smith";
    protected static final String CONTROL_ADD_ID_1 = "199";
    protected static final String CONTROL_ADD_STREET_1 = "Some Other St.";
    protected static final String CONTROL_ADD_CITY_1 = "Anyothertown";
    protected static final String CONTROL_ADD_COUNTRY_1 = "Canada";
    protected static final String CONTROL_ADD_ZIP_1 = "X0X0X0";
    protected static final String CONTROL_ADD_ID_2 = "99";
    protected static final String CONTROL_ADD_STREET_2 = "Some St.";
    protected static final String CONTROL_ADD_CITY_2 = "Anytown";
    protected static final String CONTROL_ADD_COUNTRY_2 = "Canada";
    protected static final String CONTROL_ADD_ZIP_2 = "X0X0X0";
    protected static final String CONTROL_ADD_ID_3 = "11199";
    protected static final String CONTROL_ADD_STREET_3 = "Another St.";
    protected static final String CONTROL_ADD_CITY_3 = "Anytown";
    protected static final String CONTROL_ADD_COUNTRY_3 = "Canada";
    protected static final String CONTROL_ADD_ZIP_3 = "Y0Y0Y0";
    protected static final String CONTROL_ADD_ID_4 = "1199";
    protected static final String CONTROL_ADD_STREET_4 = "Some St.";
    protected static final String CONTROL_ADD_CITY_4 = "Sometown";
    protected static final String CONTROL_ADD_COUNTRY_4 = "Canada";
    protected static final String CONTROL_ADD_ZIP_4 = "X0X0X0";

    protected static final int INT_CONTROL_ADD_ID_1 = 199;
    protected static final int INT_CONTROL_ADD_ID_2 = 99;
    protected static final int INT_CONTROL_ADD_ID_3 = 11199;
    protected static final int INT_CONTROL_ADD_ID_4 = 1199;

    public KeyBasedMappingTestCases(String name) throws Exception {
        super(name);
    }

    @Override
    public void testObjectToContentHandler() throws Exception {
        SAXDocumentBuilder builder = new SAXDocumentBuilder();
        xmlMarshaller.marshal(getWriteControlObject(), builder);
        Document controlDocument = getWriteControlDocument();
        Document testDocument = builder.getDocument();
        log("**testObjectToContentHandler**");
        log("Expected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);
        assertXMLIdentical(controlDocument, testDocument);
    }

    @Override
    public void testObjectToXMLDocument() throws Exception {
        Document testDocument = xmlMarshaller.objectToXML(getWriteControlObject());
        objectToXMLDocumentTest(testDocument);
    }

    @Override
    public void testObjectToXMLStringWriter() throws Exception {
        StringWriter writer = new StringWriter();
        xmlMarshaller.marshal(getWriteControlObject(), writer);
        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();
        objectToXMLDocumentTest(testDocument);
    }

    @Override
    public void testUnmarshallerHandler() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        SAXParser saxParser = saxParserFactory.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        XMLUnmarshallerHandler xmlUnmarshallerHandler = xmlUnmarshaller.getUnmarshallerHandler();
        xmlReader.setContentHandler(xmlUnmarshallerHandler);
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
        InputSource inputSource = new InputSource(inputStream);
        xmlReader.parse(inputSource);
        xmlToObjectTest(xmlUnmarshallerHandler.getResult());
    }

    @Override
    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        Object testObject = xmlUnmarshaller.unmarshal(instream);
        instream.close();
        xmlToObjectTest(testObject);
    }

    @Override
    public void testXMLToObjectFromURL() throws Exception {
        java.net.URL url = ClassLoader.getSystemResource(resourceName);
        Object testObject = xmlUnmarshaller.unmarshal(url);
        xmlToObjectTest(testObject);
    }

    @Override
    public void xmlToObjectTest(Object testObject) throws Exception {
        log("\n**xmlToObjectTest**");
        log("Expected:");
        log(getReadControlObject().toString());
        log("Actual:");
        log(testObject.toString());
        assertEquals(getReadControlObject(), testObject);
    }
}
