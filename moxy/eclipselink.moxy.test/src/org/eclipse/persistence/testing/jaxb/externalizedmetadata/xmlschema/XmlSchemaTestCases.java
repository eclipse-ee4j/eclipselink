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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema;

import java.io.File;

import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XmlSchema via eclipselink-oxm.xml
 *
 */
public class XmlSchemaTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/";
    static String PREFIX = "nsx";
    static String NSX_OVERRIDE_VALUE = "http://www.example.com/xsds/real";
    static boolean FORM_DEFAULT_VALUE = false;
    static String NAMESPACE = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlSchemaTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests package-info override via eclipselink-oxm.xml.  
     * 
     * The value in package-info.java for prefix 'nsx' is "http://www.example.com/xsds/fake", but 
     * due to the override in the oxm.xml file it should be "http://www.example.com/xsds/real".
     * 
     * The value in package-info.java for target namespace is 
     * "http://www.eclipse.org/eclipselink/xsds/persistence/oxm/junk", but due to the override in
     * the oxm.xml file it should be "http://www.eclipse.org/eclipselink/xsds/persistence/oxm"
     * 
     * Also, elementForm and attributeForm are both QUALIFIED in package-info, but set to 
     * UNQUALIFIED in the oxm.xml file. 
     * 
     * Positive test.
     */
    public void testXmlSchemaOverride() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            shouldGenerateSchema = false;
        }
        File schemaFile = outputResolver.schemaFiles.get("http://www.eclipse.org/eclipselink/xsds/persistence/oxm");
        Project proj = new SchemaModelProject();
        XMLContext context = new XMLContext(proj);
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        try {
            Schema schema = (Schema) unmarshaller.unmarshal(schemaFile, Schema.class);
            NamespaceResolver nsr = schema.getNamespaceResolver();
            String uri = nsr.resolveNamespacePrefix("nsx");
            assertTrue("Expected uri [" + NSX_OVERRIDE_VALUE + "] for prefix [" + PREFIX + "] but was null", uri != null);
            assertTrue("Expected uri [" + NSX_OVERRIDE_VALUE + "] for prefix [" + PREFIX + "] but was [ " + uri + "]", uri.equals(NSX_OVERRIDE_VALUE));
            
            boolean elementQualified = schema.isElementFormDefault();
            assertTrue("Expected elementFormDefault [" + FORM_DEFAULT_VALUE + "] but was [" + elementQualified + "]", elementQualified == FORM_DEFAULT_VALUE);

            boolean attributeQualified = schema.isAttributeFormDefault();
            assertTrue("Expected attributeFormDefault [" + FORM_DEFAULT_VALUE + "] but was [" + attributeQualified + "]", attributeQualified == FORM_DEFAULT_VALUE);

            String targetNamespace = schema.getTargetNamespace();
            assertTrue("Expected target namespace [" + NAMESPACE + "] but was null", targetNamespace != null);
            assertTrue("Expected target namespace [" + NAMESPACE + "] but was [ " + targetNamespace + "]", targetNamespace.equals(NAMESPACE));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
    
    /**
     * Tests XmlSchema namespace declaration via eclipselink-oxm.xml.  The namespace
     * information in package-info.java should be overridden.
     * 
     * Positive test.
     */
    public void testEmployeeValidation() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            shouldGenerateSchema = false;
        }
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests XmlSchema namespace declaration via eclipselink-oxm.xml.  The namespace
     * information in package-info.java should be overridden. This test case should
     * fail due to an invalid namespace.
     * 
     * Negative test.
     */
    public void testInvalidEmployeeValidation() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            shouldGenerateSchema = false;
        }
        String src = PATH + "employee-invalidnamespace.xml";
        String result = validateAgainstSchema(src, "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", outputResolver);
        assertTrue("Schema validation passed unxepectedly", result != null);
    }

    /**
     * Tests XmlSchema namespace declaration via eclipselink-oxm.xml.  The namespace
     * information in package-info.java should be overridden.
     *  
     * Positive test.
     */
    public void testAddressValidation() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            shouldGenerateSchema = false;
        }
        String src = PATH + "address.xml";
        String result = validateAgainstSchema(src, "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests XmlSchema namespace declaration via eclipselink-oxm.xml.  The namespace
     * information in package-info.java should be overridden. This test case should
     * fail due to an invalid namespace.
     * 
     * Negative test.
     */
    public void testInvalidAddressValidation() {
        if (shouldGenerateSchema) {
            outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
            shouldGenerateSchema = false;
        }
        String src = PATH + "address-invalidnamespace.xml";
        String result = validateAgainstSchema(src, "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", outputResolver);
        assertTrue("Schema validation passed unxepectedly", result != null);
    }
}
