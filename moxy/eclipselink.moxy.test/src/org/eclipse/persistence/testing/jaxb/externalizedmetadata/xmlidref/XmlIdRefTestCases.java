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
 * dmccann - July 14/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XmlID and XmlIDREF via eclipselink-oxm.xml
 *
 */
public class XmlIdRefTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlidref";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlidref/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlIdRefTestCases(String name) {
        super(name);
        outputResolver = new MySchemaOutputResolver();
    }
    
    /**
     * Tests @XmlID override via eclipselink-oxm.xml.  Overrides the @XmlID [city]
     * with xml-id [id], and @XmlIDREF [homeAddress] with xml-idref [workAddress].
     * 
     * Positive test.
     */
    public void testXmlIDAndXmlIDREFOverride() {
        outputResolver = generateSchema(new Class[] { Employee.class, Address.class }, CONTEXT_PATH, PATH, 1);
        
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }
    
    /**
     * Tests @XmlID override via eclipselink-oxm.xml.  Here there is an xml-idref [address] on 
     * Employee, but no corresponding xml-id set on Address.  An exception should occur.  
     * 
     * Negative test.
     */
    public void testXmlIDREFWithNOXmlID() {
        String metadataFile = PATH + "eclipselink-oxm-no-id.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        boolean ex = false;
        try {
            JAXBContextFactory.createContext(new Class[] { Employee2.class, Address2.class }, properties, loader);
        } catch (JAXBException e) {
            //e.printStackTrace();
            ex = true;
        }
        assertTrue("The expected exception was not thrown.", ex);
    }

    /**
     * Tests @XmlID override via eclipselink-oxm.xml.  Here there are two fields
     * ([city] and [id]) set as xml-id.  
     * 
     * Negative test.
     */
    public void testMultipleXmlIDs() {
        String metadataFile = PATH + "eclipselink-oxm-multi-id.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        boolean ex = false;
        try {
            JAXBContextFactory.createContext(new Class[] { Address2.class }, properties, loader);
        } catch (JAXBException e) {
            //e.printStackTrace();
            ex = true;
        }
        assertTrue("The expected exception was not thrown.", ex);
    }
    
    /**
     * Tests @XmlID override via eclipselink-oxm.xml.  Here there is one @XmlID 
     * annotation [city] and an xml-id [id]  
     * 
     * Negative test.
     */
    public void testMultipleXmlIDs2() {
        String metadataFile = PATH + "eclipselink-oxm-multi-id2.xml";
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        boolean ex = false;
        try {
            JAXBContextFactory.createContext(new Class[] { Address.class }, properties, loader);
        } catch (JAXBException e) {
            //e.printStackTrace();
            ex = true;
        }
        assertTrue("The expected exception was not thrown.", ex);
    }
}
