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
 * dmccann - July 14/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattribute;

import java.io.File;

import javax.xml.namespace.QName;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XmlAttribute via eclipselink-oxm.xml
 *
 */
public class XmlAttributeTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattribute";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlattribute/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAttributeTestCases(String name) {
        super(name);
        outputResolver = new MySchemaOutputResolver();
    }
    
    private void doGenerateSchema() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            // validate schema
            String controlSchema = PATH + "schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
            shouldGenerateSchema = false;
        }
    }
    
    /**
     * Tests @XmlAttribute override via eclipselink-oxm.xml.  
     * 
     * Positive test.
     */
    public void testXmlAttributeOverride() {
        doGenerateSchema();
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests @XmlAttribute override via eclipselink-oxm.xml.  The id attribute
     * is set to 'required' in the xml file, but the instance document does
     * not have an id attribute.
     * 
     * Negative test.
     */
    public void testXmlAttributeOverrideInvalid() {
        doGenerateSchema();
        String src = PATH + "employee-invalid.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation passed unxepectedly", result != null);
    }

    /**
     * Test setting the container class via container-type attribute.
     * 
     * Positive test.
     */
    public void testContainerType() {
        doGenerateSchema();
        XMLDescriptor xDesc = getJAXBContext().getXMLContext().getDescriptor(new QName("employee"));
        assertNotNull("No descriptor was generated for Employee.", xDesc);
        DatabaseMapping mapping = xDesc.getMappingForAttributeName("things");
        assertNotNull("No mapping exists on Employee for attribute [things].", mapping);
        assertTrue("Expected an XMLCompositeDirectCollectionMapping for attribute [things], but was [" + mapping.toString() +"].", mapping instanceof XMLCompositeDirectCollectionMapping);
        assertTrue("Expected container class [java.util.LinkedList] but was ["+((XMLCompositeDirectCollectionMapping) mapping).getContainerPolicy().getContainerClassName()+"]", ((XMLCompositeDirectCollectionMapping) mapping).getContainerPolicy().getContainerClassName().equals("java.util.LinkedList"));
    }
}
