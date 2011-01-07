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
 * dmccann - October 7/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.adapter.MyValueAdapter;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.adapter.MyValueClass;
import org.w3c.dom.Document;

/**
 * Tests XmlValue via eclipselink-oxm.xml
 *
 */
public class XmlValueTestCases extends ExternalizedMetadataTestCases {
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlvalue/";
    private static final String ADAPTER_CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.adapter";
    private static final String ADAPTER_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlvalue/adapter/";
    private static final String ADAPTER_OXM_DOC = ADAPTER_PATH + "oxm.xml";
    private static final String ADAPTER_INSTANCE_DOC = ADAPTER_PATH + "boolean.xml";
    private JAXBContext jCtx;
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlValueTestCases(String name) {
        super(name);
    }

    private JAXBContext getValueClassContext() {
        if (jCtx == null) {
            InputStream iStream = loader.getResourceAsStream(ADAPTER_OXM_DOC);
            if (iStream == null) {
                fail("Couldn't load metadata file [" + ADAPTER_OXM_DOC + "]");
            }
            HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
            metadataSourceMap.put(ADAPTER_CONTEXT_PATH, new StreamSource(iStream));
            Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
            try {
                jCtx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { MyValueClass.class }, properties);
            } catch (JAXBException e) {
                e.printStackTrace();
                fail("JAXBContext creation failed.");
            }
        }
        return jCtx;
    }

    private MyValueClass getControlValueClass() {
        MyValueClass mvc = new MyValueClass();
        mvc.blah = new Boolean("true");
        return mvc;        
    }
    
    private Document getControlDocument() {
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(ADAPTER_INSTANCE_DOC);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + ADAPTER_INSTANCE_DOC + "].");
        }
        return ctrlDoc;
    }

    /**
     * Tests @XmlValue via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testCDNPriceXmlValue() {
        String metadataFile = PATH + "eclipselink-oxm-cdnprice.xml";

        outputResolver = generateSchemaWithFileName(new Class[] { CDNPriceNoAnnotation.class }, CONTEXT_PATH, metadataFile, 1);
        
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
        outputResolver = generateSchemaWithFileName(new Class[] { CDNPricesNoAnnotation.class }, CONTEXT_PATH, metadataFile, 1);
        
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
        outputResolver = generateSchemaWithFileName(new Class[] { InternationalPriceNoAnnotation.class }, CONTEXT_PATH, metadataFile, 1);
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
        outputResolver = generateSchemaWithFileName(new Class[] { InternationalPricesNoAnnotation.class }, CONTEXT_PATH, metadataFile, 1);
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
    
    /**
     * Test setting the container class via container-type attribute.
     * 
     * Positive test.
     */
    public void testContainerType() {
        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { CDNPricesNoAnnotation.class }, CONTEXT_PATH, PATH + "eclipselink-oxm-cdnprices.xml");
        } catch (JAXBException e) {
            fail("JAXBContext creation failed.");
        }
        XMLDescriptor xDesc = jCtx.getXMLContext().getDescriptor(new QName("canadian-price"));
        assertNotNull("No descriptor was generated for CDNPricesNoAnnotation.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("prices");
        assertNotNull("No mapping exists on CDNPricesNoAnnotation for attribute [prices].", mapping);
        assertTrue("Expected an XMLCompositeDirectCollectionMapping for attribute [prices], but was [" + mapping.toString() +"].", mapping instanceof XMLCompositeDirectCollectionMapping);
        assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLCompositeDirectCollectionMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLCompositeDirectCollectionMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
    }
    
    /**
     * Tests unmarshal XmlValue with an adapter set.
     * 
     * Positive test.
     */
    public void testXmlAdapterUnmarshal() throws Exception {
        JAXBContext jaxbCtx = getValueClassContext();
        Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
        Object o = unmarshaller.unmarshal(new File(ADAPTER_INSTANCE_DOC));
        assertNotNull("Unmarshalled object is null", o);
        assertTrue("Expected [MyValueClass] but was [" + o.getClass().getName() + "]", o instanceof MyValueClass);
        assertNotNull("'blah' property is null", ((MyValueClass) o).blah);
        assertEquals(((MyValueClass) o).blah, new Boolean("True"));
    }
    
    /**
     * Tests marshal XmlValue with an adapter set.
     * 
     * Positive test.
     */
    public void testXmlAdapterMarshal() throws Exception {
        JAXBContext jaxbCtx = getValueClassContext();
        // setup control document
        Document testDoc = parser.newDocument();
        Marshaller marshaller = jaxbCtx.createMarshaller();
        marshaller.marshal(getControlValueClass(), testDoc);
        //marshaller.marshal(mvc, System.out);
        assertTrue("Document comparison failed unxepectedly: ", compareDocuments(getControlDocument(), testDoc));
    }
}
