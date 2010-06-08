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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XmlElement via eclipselink-oxm.xml
 *
 */
public class XmlElementTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelement/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlElementTestCases(String name) {
        super(name);
        outputResolver = new MySchemaOutputResolver();
    }
    
    /**
     * Tests @XmlElement override via eclipselink-oxm.xml.  
     * 
     * Positive test.
     */
    public void testXmlElementOverride() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 2);
            // validate schema
            String controlSchema = PATH + "schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
            shouldGenerateSchema = false;
        }
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests @XmlElement override via eclipselink-oxm.xml.  The myUtilDate 
     * element is set to 'required' in the xml file, but the instance 
     * document does not have a myUtilDate element.
     * 
     * Negative test.
     */
    public void testXmlElementOverrideInvalid() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 2);
            // validate schema
            String controlSchema = PATH + "schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
            shouldGenerateSchema = false;
        }
        String src = PATH + "employee-invalid.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation passed unxepectedly", result != null);
    }
    
    public void testTeamWithListOfPlayersSchemaGen() {
        String metadataFile = PATH + "team-oxm.xml";
        
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(CONTEXT_PATH, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        
        try {
            JAXBContext jContext = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Team.class }, properties, loader);
            MySchemaOutputResolver oResolver = new MySchemaOutputResolver(); 
            jContext.generateSchema(oResolver);
            // validate schema
            String controlSchema = PATH + "team-schema.xsd";
            compareSchemas(oResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        } catch (JAXBException e) {
            fail("An exception occurred creating the JAXBContext: " + e.getMessage());
        }
    }
}
