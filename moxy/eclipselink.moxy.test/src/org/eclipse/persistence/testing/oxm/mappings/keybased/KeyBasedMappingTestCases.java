/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.keybased;

import java.io.*;
import javax.xml.parsers.*;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
//import org.custommonkey.xmlunit.Diff;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

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

    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        log("**objectToXMLDocumentTest**");
        log("Expected:");
        log(getWriteControlDocument());
        log("\nActual:");
        log(testDocument);
        assertXMLIdentical(getWriteControlDocument(), testDocument);
    }

    protected void setProject(Project project) {
        this.project = project;
    }

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

    public void testObjectToXMLDocument() throws Exception {
        Document testDocument = xmlMarshaller.objectToXML(getWriteControlObject());
        objectToXMLDocumentTest(testDocument);
    }

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

    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        Object testObject = xmlUnmarshaller.unmarshal(instream);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromURL() throws Exception {
        java.net.URL url = ClassLoader.getSystemResource(resourceName);
        Object testObject = xmlUnmarshaller.unmarshal(url);
        xmlToObjectTest(testObject);
    }

    public void xmlToObjectTest(Object testObject) throws Exception {
        log("\n**xmlToObjectTest**");
        log("Expected:");
        log(getReadControlObject().toString());
        log("Actual:");
        log(testObject.toString());
        assertEquals(getReadControlObject(), testObject);
    }
}
