/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.xmlroot.complex;

import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.persistence.internal.oxm.record.XMLEventReaderInputSource;
import org.eclipse.persistence.internal.oxm.record.XMLEventReaderReader;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderReader;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Node;

public class XMLRootComplexTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlroot/complex/employee.xml";
    protected final static String CONTROL_PERSON_NAME = "Joe Smith";
    protected final static String CONTROL_ELEMENT_NAME = "oxm:employee";
    protected final static String CONTROL_NAMESPACE_URI = "test";

    public XMLRootComplexTestCases(String name) throws Exception {
        super(name);
        setControlDocument(getXMLResource());
        setProject(getTopLinkProject());
    }

    public Project getTopLinkProject() {
        return new XMLRootComplexProject();
    }

    protected Object getControlObject() {
        Person peep = new Person();
        peep.setName(CONTROL_PERSON_NAME);

        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_ELEMENT_NAME);
        xmlRoot.setNamespaceURI(CONTROL_NAMESPACE_URI);
        xmlRoot.setObject(peep);
        return xmlRoot;
    }

    // Unmarshal tests
    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(getXMLResource());
        Object testObject = xmlUnmarshaller.unmarshal(instream, Person.class);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromNode() throws Exception {
        Object testObject = xmlUnmarshaller.unmarshal(getControlDocument(), Person.class);
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromURL() throws Exception {
        java.net.URL url = ClassLoader.getSystemResource(getXMLResource());
        Object testObject = xmlUnmarshaller.unmarshal(url, Person.class);
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
                InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
                XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);

                XMLStreamReaderReader staxReader = new XMLStreamReaderReader();
                staxReader.setErrorHandler(xmlUnmarshaller.getErrorHandler());
                XMLStreamReaderInputSource inputSource = new XMLStreamReaderInputSource(xmlStreamReader);
                Object testObject = xmlUnmarshaller.unmarshal(staxReader, inputSource, Person.class);

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
                Object testObject = xmlUnmarshaller.unmarshal(staxReader, inputSource, Person.class);

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

        if (getReadControlObject() instanceof XMLRoot) {
            XMLRoot controlObj = (XMLRoot)getReadControlObject();
            XMLRoot testObj = (XMLRoot)testObject;

            this.assertEquals(controlObj.getLocalName(), testObj.getLocalName());
            this.assertEquals(controlObj.getNamespaceURI(), testObj.getNamespaceURI());
            this.assertEquals(controlObj.getObject(), testObj.getObject());
        } else {
            this.assertEquals(getReadControlObject(), testObject);
        }
    }

    // DOES NOT APPLY
    public void testUnmarshallerHandler() throws Exception {
    }

    public String getXMLResource() {
        return XML_RESOURCE;
    }

}
