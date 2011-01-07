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
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlseealso;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XmlSeeAlso via eclipselink-oxm.xml
 *
 */
public class XmlSeeAlsoTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlseealso";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlseealso/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlSeeAlsoTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests generation for Employee when xml-see-also is defined.  Overrides the
     * @XmlSeeAlso on Employee (XmlSeeAlsoTestCases.class) with (MySimpleClass, 
     * MyOtherClass)
     * 
     * Positive test.
     */
    public void testEmployeeGeneration() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 2);
            shouldGenerateSchema = false;
        }
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests generation of an xml-see-also class in the same package as Employee
     * 
     * Positive test.
     */
    public void testXmlSeeAlsoSamePackage() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 2);
            shouldGenerateSchema = false;
        }
        String src = PATH + "mysimpleclass.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests generation of an xml-see-also class from a package different than Employee's
     * 
     * Positive test.
     */
    public void testXmlSeeAlsoOtherPackage() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 2);
            shouldGenerateSchema = false;
        }
        String src = PATH + "myotherclass.xml";
        String result = validateAgainstSchema(src, "http://www.example.com/xsd", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
}
