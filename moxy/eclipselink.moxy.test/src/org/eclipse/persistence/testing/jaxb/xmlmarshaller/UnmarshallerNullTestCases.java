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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class UnmarshallerNullTestCases extends OXTestCase {

    static String DOUBLE_ERROR_XML = "org/eclipse/persistence/testing/oxm/jaxb/Employee_TwoError.xml";

    private JAXBUnmarshaller unmarshaller;

    public UnmarshallerNullTestCases(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        Class[] classes = {Employee.class};
        JAXBContext jc = JAXBContextFactory.createContext(classes, null);
        unmarshaller = (JAXBUnmarshaller) jc.createUnmarshaller();
    }

    public void testFailNullFile() throws Exception {
        try {
            unmarshaller.unmarshal((File) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullInputSource() throws Exception {
        try {
            unmarshaller.unmarshal((InputSource) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullNode() throws Exception {
        try {
            unmarshaller.unmarshal((Node) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullNodeWithClass() throws Exception {
        try {
            unmarshaller.unmarshal((Node) null, Employee.class);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNodeWithNullClass() throws Exception {
        try {
            InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
            XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            XMLParser xmlParser = xmlPlatform.newXMLParser();
            xmlParser.setNamespaceAware(true);
            Node node = xmlParser.parse(stream);
            unmarshaller.unmarshal(node, null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullReader() throws Exception {
        try {
            unmarshaller.unmarshal((Reader) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullSource() throws Exception {
        try {
            unmarshaller.unmarshal((Source) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullSourceWithClass() throws Exception {
        try {
            unmarshaller.unmarshal((Source) null, Employee.class);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailSourceWithNullClass() throws Exception {
        try {
            InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
            Source source = new StreamSource(stream);
            unmarshaller.unmarshal(source, (Class) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullSourceWithType() throws Exception {
        try {
            unmarshaller.unmarshal((Source) null, (Type) Employee.class);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailSourceWithNullType() throws Exception {
        try {
            InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
            Source source = new StreamSource(stream);
            unmarshaller.unmarshal(source, (Type) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullURL() throws Exception {
        try {
            unmarshaller.unmarshal((URL) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullXMLEventReader() throws Exception {
        try {
            unmarshaller.unmarshal((XMLEventReader) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullXMLEventReaderWithClass() throws Exception {
        try {
            unmarshaller.unmarshal((XMLEventReader) null, Employee.class);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailXMLEventReaderWithNullClass() throws Exception {
        try {
            if(null == XML_INPUT_FACTORY) {
                return;
            }
            InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(stream);
            unmarshaller.unmarshal(xmlEventReader, (Class) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullXMLEventReaderWithType() throws Exception {
        try {
            unmarshaller.unmarshal((XMLEventReader) null, (Type) Employee.class);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailXMLEventReaderWithNullType() throws Exception {
        try {
            if(null == XML_INPUT_FACTORY) {
                return;
            }
            InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(stream);
            unmarshaller.unmarshal(xmlEventReader, (Type) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullXMLStreamReader() throws Exception {
        try {
            unmarshaller.unmarshal((XMLStreamReader) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullXMLStreamReaderWithClass() throws Exception {
        try {
            unmarshaller.unmarshal((XMLStreamReader) null, Employee.class);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailXMLStreamReaderWithNullClass() throws Exception {
        try {
            if(null == XML_INPUT_FACTORY) {
                return;
            }
            InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(stream);
            unmarshaller.unmarshal(xmlStreamReader, (Class) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullXMLStreamReaderWithType() throws Exception {
        try {
            unmarshaller.unmarshal((XMLStreamReader) null, (Type) Employee.class);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailXMLStreamReaderWithNullType() throws Exception {
        try {
            if(null == XML_INPUT_FACTORY) {
                return;
            }
            InputStream stream = ClassLoader.getSystemResourceAsStream(DOUBLE_ERROR_XML);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(stream);
            unmarshaller.unmarshal(xmlStreamReader, (Type) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

    public void testFailNullInputStream() throws Exception {
        try {
            unmarshaller.unmarshal((InputStream) null);
        } catch(IllegalArgumentException e) {
            return;
        }
        fail("IllegalArgumentException not thrown.");
    }

}