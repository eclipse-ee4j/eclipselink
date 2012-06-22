/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.xmlroot.simple;

import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;

import junit.textui.TestRunner;

import org.eclipse.persistence.internal.oxm.record.XMLEventReaderInputSource;
import org.eclipse.persistence.internal.oxm.record.XMLEventReaderReader;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderReader;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class XMLRootSimpleTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/simple/employee.xml";
    protected final static String CONTROL_OBJECT = "Joe Smith";
    protected final static String CONTROL_ELEMENT_NAME = "employee-name";
    protected final static String CONTROL_PREFIX = "oxm";
    protected final static String CONTROL_NAMESPACE_URI = "test";

    public XMLRootSimpleTestCases(String name) throws Exception {
        super(name);
        setControlDocument(getXMLResource());
        setProject(new XMLRootSimpleProject());
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootSimpleTestCases" };
        TestRunner.main(arguments);
    }

    public Object getControlObject() {
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_ELEMENT_NAME);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(CONTROL_OBJECT);
        return xmlRoot;
    }

    public Object getWriteControlObject() {
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_ELEMENT_NAME);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(CONTROL_OBJECT);
        return xmlRoot;
    }

    public String getXMLResource() {
        return XML_RESOURCE;
    }

    // Unmarshal tests
    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(getXMLResource());
        Object testObject = xmlUnmarshaller.unmarshal(instream, String.class);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromDocument() throws Exception {
        Object testObject = xmlUnmarshaller.unmarshal(getControlDocument(), String.class);
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromURL() throws Exception {
        java.net.URL url = ClassLoader.getSystemResource(getXMLResource());
        Object testObject = xmlUnmarshaller.unmarshal(url, String.class);
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
                InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
                XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);

                XMLStreamReaderReader staxReader = new XMLStreamReaderReader();
                staxReader.setErrorHandler(xmlUnmarshaller.getErrorHandler());
                XMLStreamReaderInputSource inputSource = new XMLStreamReaderInputSource(xmlStreamReader);
                Object testObject = xmlUnmarshaller.unmarshal(staxReader, inputSource, String.class);

                instream.close();
                xmlToObjectTest(testObject);
        }
    }
    
    public void testXMLToObjectFromXMLEventReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
                InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
                XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(instream);

                XMLEventReaderReader staxReader = new XMLEventReaderReader();
                staxReader.setErrorHandler(xmlUnmarshaller.getErrorHandler());
                XMLEventReaderInputSource inputSource = new XMLEventReaderInputSource(xmlEventReader);
                Object testObject = xmlUnmarshaller.unmarshal(staxReader, inputSource, String.class);

                instream.close();
                xmlToObjectTest(testObject);
        }
    }    

    public void xmlToObjectTest(Object testObject) throws Exception {
        log("\n**testXMLDocumentToObject**");
        log("Expected:");
        log(getReadControlObject().toString());
        log("Actual:");
        log(testObject.toString());

        XMLRoot controlObj = (XMLRoot)getReadControlObject();
        XMLRoot testObj = (XMLRoot)testObject;

        this.assertEquals(controlObj.getLocalName(), testObj.getLocalName());
        this.assertEquals(controlObj.getNamespaceURI(), testObj.getNamespaceURI());
        this.assertEquals(controlObj.getObject(), testObj.getObject());
    }

    // DOES NOT APPLY
    public void testUnmarshallerHandler() throws Exception {
    }

    public Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xmlroot/simple/employee-write.xml");
        Document writeDocument = parser.parse(inputStream);
        removeEmptyTextNodes(controlDocument);
        inputStream.close();
        return writeDocument;
    }

}
