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
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XmlType via eclipselink-oxm.xml
 *
 */
public class XmlTypeTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlTypeTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests @XmlType override via eclipselink-oxm.xml.  Overrides type 
     * name (my-employee-type) with (employee-type) and propOrder 
     * ("id", "firstName", "lastName") with ("id", "lastName", "firstName")
     * 
     * Positive test.
     */
    public void testEmployeeXmlTypeOverride() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            shouldGenerateSchema = false;
        }
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests @XmlType override via eclipselink-oxm.xml.  Overrides type 
     * name (my-employee-type) with (employee-type) and propOrder 
     * ("id", "firstName", "lastName") with ("id", "lastName", "firstName")
     * 
     * Negative test.
     */
    public void testEmployeeXmlTypeOverrideInvalid() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            shouldGenerateSchema = false;
        }
        String src = PATH + "employee-invalid.xml";
        String result = validateAgainstSchema(src, null, outputResolver);
        assertTrue("Schema validation passed unxepectedly", result != null);
    }
}
