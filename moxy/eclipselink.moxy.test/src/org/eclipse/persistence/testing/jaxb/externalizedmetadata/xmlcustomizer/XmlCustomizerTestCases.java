/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - August 6/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlcustomizer;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.oxm.OXTestCase;

/**
 * Tests XmlCustomizer - this annotation facilitates descriptor customization.
 *
 */
public class XmlCustomizerTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlcustomizer";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlcustomizer/";

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlCustomizerTestCases(String name) {
        super(name);
    }
    
    /**
     * Test @XmlCustomizer annotation on the Java class.  Here, no XML override
     * is performed.  The instance doc will contain 'first-name' and 'last-name' 
     * tags which were changed by the customizer from 'firstName' and
     * 'lastName' respectively.
     * 
     * Positive test.
     */
    public void testXmlCustomizerNoOverride() {
        Class<?>[] classes = { 
                Employee.class
            };

        JAXBContext jaxbContext = null;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(classes, null);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();

        Object obj;
        try {
            // test unmarshal
            obj = unmarshaller.unmarshal(new StreamSource(new StringReader(getInstanceDocument())));
            assertTrue("Unmarshal operation failed - objects are not equal", obj.equals(getControlObject()));

            // test marshal
            StringWriter sw = new StringWriter();
            marshaller.marshal(obj, sw);

            String ctrlString = OXTestCase.removeWhiteSpaceFromString(getInstanceDocument());
            String testString = OXTestCase.removeWhiteSpaceFromString(sw.toString());
            assertTrue("Comparison failed - expected \n'" + ctrlString + "'\n but was \n'" + testString + "'\n", ctrlString.equals(testString));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }
    
    /**
     * Test @XmlCustomizer annotation on the Java class.  Here, no XML override
     * is performed.  The instance doc will contain 'firstName' and 'lastName' 
     * tags which will have been changed by the customizer to 'first-name' and
     * 'last-name' respectively.
     * 
     * Negative test.
     */
    public void testXmlCustomizerNoOverrideFail() {
        Class<?>[] classes = { 
                Employee.class
            };

        JAXBContext jaxbContext = null;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(classes, null);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();

        Object obj;
        try {
            // test unmarshal
            obj = unmarshaller.unmarshal(new StreamSource(new StringReader(getInstanceDocumentInvalid())));
            assertFalse("Unmarshal operation succeeded unexpectedly", obj.equals(getControlObject()));

            // test marshal
            StringWriter sw = new StringWriter();
            marshaller.marshal(getControlObject(), sw);

            String ctrlString = OXTestCase.removeWhiteSpaceFromString(getInstanceDocumentInvalid());
            String testString = OXTestCase.removeWhiteSpaceFromString(sw.toString());
            assertFalse("Comparison succeeded unexpectedly", ctrlString.equals(testString));
        } catch (JAXBException e) {
            fail("An unexpected exception occurred");
        }
    }
    
    /**
     * Test @XmlCustomizer via XML override.  The instance doc will contain 'my-first-name' 
     * and 'my-last-name' tags which are changed by the customizer from 'firstName' and
     * 'lastName' respectively.  Note that the customizer set via annotations 
     * (MyEmployeeCustomizer) will be overridden.
     * 
     * Positive test.
     */
    public void testXmlCustomizerOverride() {
        Class<?>[] classes = { 
                Employee.class
            };

        String metadataFile = PATH + "my-eclipselink-oxm.xml";
        
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(classes, properties);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();

        Object obj;
        try {
            // test unmarshal
            obj = unmarshaller.unmarshal(new StreamSource(new StringReader(getInstanceDocumentForXmlOverride())));
            assertTrue("Unmarshal operation failed - objects are not equal", obj.equals(getControlObject()));

            // test marshal
            StringWriter sw = new StringWriter();
            marshaller.marshal(obj, sw);

            String ctrlString = OXTestCase.removeWhiteSpaceFromString(getInstanceDocumentForXmlOverride());
            String testString = OXTestCase.removeWhiteSpaceFromString(sw.toString());
            assertTrue("Comparison failed - expected \n'" + ctrlString + "'\n but was \n'" + testString + "'\n", ctrlString.equals(testString));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }

    private String getInstanceDocument() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><employee><first-name>Joe</first-name><last-name>Oracle</last-name></employee>";
    }
    
    private String getInstanceDocumentInvalid() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><employee><firstName>Joe</firstName><lastName>Oracle</lastName></employee>";
    }
    
    private String getInstanceDocumentForXmlOverride() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><employee><my-first-name>Joe</my-first-name><my-last-name>Oracle</my-last-name></employee>";
    }
    
    private Employee getControlObject() {
        Employee emp = new Employee();
        emp.firstName = "Joe";
        emp.lastName = "Oracle";
        return emp;
    }
}
