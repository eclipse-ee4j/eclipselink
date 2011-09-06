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
 * dmccann - November 04/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmixed;

import java.io.File;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Element;

/**
 * Tests XmlMixed via eclipselink-oxm.xml
 *
 */
public class XmlMixedTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmixed";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlmixed/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlMixedTestCases(String name) {
        super(name);
        outputResolver = new MySchemaOutputResolver();
    }

    private void doSchemaGeneration() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            // validate schema
            String controlSchema = PATH + "schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
            shouldGenerateSchema = false;
        }
    }    
    
    /**
     * Tests @XmlMixed override via eclipselink-oxm.xml.  
     * 
     * Positive test.
     */
    public void testXmlMixed() {
        doSchemaGeneration();
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * 
     * Positive test.
     * @throws JAXBException 
     */
    public void testXmlMixedUnmarshal() throws JAXBException {
    	
    	Class[] classes = new Class[] { Employee.class };
    	MySchemaOutputResolver outputResolver = generateSchema(classes, CONTEXT_PATH, PATH, 1);

        // test unmarshal
        Employee emp = null;
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try {
            String src = PATH + "employee.xml";
            emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", emp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }

        assertNotNull("The Employee did not umnmarshal correctly: 'stuff' is null.", emp.stuff);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff' size of [3] but was [" + emp.stuff.size() + "]", emp.stuff.size() == 3);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.0' to be instanceof [String] but was [" + emp.stuff.get(0) + "]", emp.stuff.get(0) instanceof String);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.1' to be instanceof [Element] but was [" + emp.stuff.get(1) + "]", emp.stuff.get(1) instanceof Element);
        assertTrue("The Employee did not umnmarshal correctly: expected 'stuff.2' to be instanceof [String] but was [" + emp.stuff.get(2) + "]", emp.stuff.get(2) instanceof String);
    }
}
