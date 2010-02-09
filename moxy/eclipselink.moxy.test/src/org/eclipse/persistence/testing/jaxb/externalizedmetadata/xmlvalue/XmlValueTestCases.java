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
 * dmccann - October 7/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XmlValue via eclipselink-oxm.xml
 *
 */
public class XmlValueTestCases extends ExternalizedMetadataTestCases {
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlvalue/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlValueTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests @XmlValue via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testCDNPriceXmlValue() {
        String metadataFile = PATH + "eclipselink-oxm-cdnprice.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        outputResolver = generateSchema(new Class[] { CDNPriceNoAnnotation.class }, CONTEXT_PATH, iStream, 1);
        
        // validate schema
        String controlSchema = PATH + "cdnprice_schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        
        String src = PATH + "cdnprice.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests @XmlValue via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testCDNPricesXmlValue() {
        String metadataFile = PATH + "eclipselink-oxm-cdnprices.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        outputResolver = generateSchema(new Class[] { CDNPricesNoAnnotation.class }, CONTEXT_PATH, iStream, 1);
        
        // validate schema
        String controlSchema = PATH + "cdnprices_schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        
        String src = PATH + "cdnprices.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests @XmlValue via eclipselink-oxm.xml.
     * 
     * Here we map the class to XML Schema complexType with with simpleContent.
     * 
     * Positive test.
     */
    public void testInternationalPriceXmlValue() {
        String metadataFile = PATH + "eclipselink-oxm-intprice.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        outputResolver = generateSchema(new Class[] { InternationalPriceNoAnnotation.class }, CONTEXT_PATH, iStream, 1);
        // validate schema
        String controlSchema = PATH + "intprice_schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        
        String src = PATH + "intprice.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests @XmlValue via eclipselink-oxm.xml.
     * 
     * Here we map the class to XML Schema complexType with with simpleContent.
     * 
     * Positive test.
     */
    public void testInternationalPricesXmlValue() {
        String metadataFile = PATH + "eclipselink-oxm-intprices.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        outputResolver = generateSchema(new Class[] { InternationalPricesNoAnnotation.class }, CONTEXT_PATH, iStream, 1);
        // validate schema
        String controlSchema = PATH + "intprices_schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        
        String src = PATH + "intprices.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests @XmlValue via eclipselink-oxm.xml.
     * 
     * An exception should occur since more than one field is set as XmlValue.
     * 
     * Negative test.
     */
    public void testTwoXmlValues() {
        String metadataFile = PATH + "eclipselink-oxm-intprice-invalid.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        boolean exceptionOccurred = false;
        
        try { 
            JAXBContextFactory.createContext(new Class[] { InternationalPriceNoAnnotation.class }, properties, loader);
        } catch (Exception x) {
            exceptionOccurred = true;
        }
        
        assertTrue("The expected exception was not thrown", exceptionOccurred);
    }

    /**
     * Tests @XmlValue via eclipselink-oxm.xml.
     * 
     * An exception should occur since only XmlAttributes are allowed when
     * there is an XmlValue.
     * 
     * Negative test.
     */
    public void testXmlValueWithElement() {
        String metadataFile = PATH + "eclipselink-oxm-intprice-invalid-2.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        boolean exceptionOccurred = false;
        
        try { 
            JAXBContextFactory.createContext(new Class[] { InternationalPriceNoAnnotation.class }, properties, loader);
        } catch (Exception x) {
            exceptionOccurred = true;
        }
        
        assertTrue("The expected exception was not thrown", exceptionOccurred);
    }
}
