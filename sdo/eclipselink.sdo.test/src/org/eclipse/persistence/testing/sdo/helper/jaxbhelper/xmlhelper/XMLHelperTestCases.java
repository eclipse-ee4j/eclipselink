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
*     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.xmlhelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XSDHelper;

public class XMLHelperTestCases extends SDOTestCase {

    private static final String XML_SCHEMA = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/xmlhelper/GlobalElement.xsd";
    private static final String XML_INPUT = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/xmlhelper/GlobalElement.xml";
    private static final String XML_INPUT_UTF16 = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/xmlhelper/GlobalElement_UTF16.xml";

    private JAXBHelperContext jaxbHelperContext;

    public XMLHelperTestCases(String name) {
        super(name);
    }

    public void setUp() {
        XMLHelperProject project = new XMLHelperProject();
        XMLContext xmlContext = new XMLContext(project);
        JAXBContext jaxbContext = new JAXBContext(xmlContext);
        jaxbHelperContext = new JAXBHelperContext(jaxbContext);
        
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA);
        jaxbHelperContext.getXSDHelper().define(xsd, null);
    }

    public void testTypes() {
        Type rootTypeType = jaxbHelperContext.getTypeHelper().getType("urn:xml", "root-type");
        assertNotNull(rootTypeType);

        Type rootType = jaxbHelperContext.getTypeHelper().getType("urn:xml", "root");
        assertNull(rootType);
    }

    public void testCreateTypeFromGlobalComplexType() {
        try {
            InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_INPUT);
            XMLDocument xmlDocument = jaxbHelperContext.getXMLHelper().load(xml);
            assertNotNull(xmlDocument);
            assertNotNull(xmlDocument.getRootObject());
        } catch(IOException e) {
            fail();
        }
    }

    public void testLoadUTF16() {
        try {
            InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_INPUT_UTF16);
            XMLDocument xmlDocument = jaxbHelperContext.getXMLHelper().load(xml);
            boolean correctEncoding = "UTF-16".equals(xmlDocument.getEncoding()) || "UTF-16LE".equals(xmlDocument.getEncoding()); 
            assertTrue(correctEncoding);
            assertEquals("1.1", xmlDocument.getXMLVersion());
        } catch(IOException e) {
            fail();
        }
    }

    public void testLoadInputStream1IOException() {
        boolean fail = true;
        try {
            InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_INPUT_UTF16);
            xml.close();
            XMLDocument xmlDocument = jaxbHelperContext.getXMLHelper().load(xml);
        } catch(IOException e) {
            fail = false;
        } catch(Exception e) {
            fail("An IOException was expected, but another exception was thrown.");
        }
        if(fail) {
            fail("An IOException should have been thrown, but no exceptions were thrown.");
        }
    }

    public void testLoadInputStream2IOException() {
        boolean fail = true;
        try {
            InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_INPUT_UTF16);
            xml.close();
            XMLDocument xmlDocument = jaxbHelperContext.getXMLHelper().load(xml, null, null);
        } catch(IOException e) {
            fail = false;
        } catch(Exception e) {
            fail("An IOException was expected, but another exception was thrown.");
        }
        if(fail) {
            fail("An IOException should have been thrown, but no exceptions were thrown.");
        }
    }

    public void testLoadSourceIOException() {
        boolean fail = true;
        try {
            InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_INPUT_UTF16);
            StreamSource source = new StreamSource(xml);
            xml.close();
            XMLDocument xmlDocument = jaxbHelperContext.getXMLHelper().load(source, null, null);
        } catch(IOException e) {
            fail = false;
        } catch(Exception e) {
            fail("An IOException was expected, but another exception was thrown.");
        }
        if(fail) {
            fail("An IOException should have been thrown, but no exceptions were thrown.");
        }
    }

    public void testLoadReaderIOException() {
        boolean fail = true;
        try {
            InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_INPUT_UTF16);
            xml.close();
            InputStreamReader reader = new InputStreamReader(xml);
            XMLDocument xmlDocument = jaxbHelperContext.getXMLHelper().load(reader, null, null);
        } catch(IOException e) {
            fail = false;
        } catch(Exception e) {
            fail("An IOException was expected, but another exception was thrown.");
        }
        if(fail) {
            fail("An IOException should have been thrown, but no exceptions were thrown.");
        }
    }

    public void tearDown() {
    }

}
