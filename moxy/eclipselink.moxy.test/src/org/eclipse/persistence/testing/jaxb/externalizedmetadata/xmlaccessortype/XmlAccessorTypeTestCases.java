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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XmlAccessorType via eclipselink-oxm.xml
 *
 */
public class XmlAccessorTypeTestCases extends ExternalizedMetadataTestCases {
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessortype/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAccessorTypeTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests no @XmlAccessorType override via eclipselink-oxm.xml.
     * 
     * Positive test.
     */
    public void testNoXmlAccessorTypeOverride() {
        outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests class level @XmlAccessorType override via eclipselink-oxm.xml.
     * Here, the Employee object has the access set to 'NONE', but this is
     * overridden as 'FIELD'.
     * 
     * Positive test.
     */
    public void testXmlAccessorTypeFieldOverride() {
        String contextPath = CONTEXT_PATH + ".field";
        String path = PATH + "field/";
        
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.field.Employee.class }, contextPath, path, 1);
        
        String src = PATH + "employee-field.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests class level @XmlAccessorType override via eclipselink-oxm.xml.
     * Here, the Employee object has the access set to 'NONE', but this is
     * overridden as 'PROPERTY'.
     * 
     * Positive test.
     */
    public void testXmlAccessorTypePropertyOverride() {
        String contextPath = CONTEXT_PATH + ".property";
        String path = PATH + "property/";
        
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.property.Employee.class }, contextPath, path, 1);
        
        String src = PATH + "employee-property.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests class level @XmlAccessorType override via eclipselink-oxm.xml.
     * Here, the Employee object has the access set to 'NONE', but this is
     * overridden as 'PUBLIC_MEMBER'.
     * 
     * Positive test.
     */
    public void testXmlAccessorTypePublicMemberOverride() {
        String contextPath = CONTEXT_PATH + ".publicmember";
        String path = PATH + "publicmember/";
        
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.publicmember.Employee.class }, contextPath, path, 1);
        
        String src = PATH + "employee-publicmember.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests overriding @XmlAccessorType set in package-info.java via eclipselink-oxm.xml.
     * Here, package-info has the access set to 'PROPERTY', but this is
     * overridden as 'FIELD'.
     * 
     * Positive test.
     */
    public void testXmlAccessorTypePackageLevelOverride() {
        String contextPath = CONTEXT_PATH + ".packagelevel";
        String path = PATH + "packagelevel/";
        
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.packagelevel.Employee.class }, contextPath, path, 1);
        
        String src = PATH + "employee-field.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests overriding @XmlAccessorType set at the package level in eclipselink-oxm.xml
     * via class level override.  Here, the package level setting is 'PROPERTY', which
     * is overridden for Employee as 'PUBLIC_MEMBER'. 
     * 
     * Positive test.
     */
    public void testXmlAccessorTypeClassOverridesPackage() {
        String contextPath = CONTEXT_PATH + ".packagelevel.classoverride";
        String path = PATH + "packagelevel/classoverride/";
        
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.packagelevel.classoverride.Employee.class }, contextPath, path, 1);
        
        String src = PATH + "employee-publicmember.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests the @XmlAccessorType set in the java class will override one set in 
     * eclipselink-oxm.xml at the package level.  eclipselink-oxm.xml will have 
     * 'PUBLIC_MEMBER' at the package level, but it will be set to 'FIELD' in the
     * java class.
     * 
     * Positive test.
     */
    public void testXmlAccessorOrderJavaClassOverridesPackage() {
        String contextPath = CONTEXT_PATH + ".packagelevel.javaclassoverride";
        String path = PATH + "packagelevel/javaclassoverride/";
        
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.packagelevel.javaclassoverride.Employee.class }, contextPath, path, 1);
        
        String src = PATH + "employee-field.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests the @XmlAccessorType set in package-info.java.  No overrides will 
     * be performed.
     * 
     * Positive test.
     */
    public void testPkgXmlAccessorOrderNoOverride() {
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.packagelevel.nooverride.Employee.class }, 1);
        
        String src = PATH + "employee-none.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests the @XmlAccessorType set in package-info.java.  No overrides will 
     * be performed.
     * 
     * Negative test.
     */
    public void testPkgXmlAccessorOrderNoOverrideFail() {
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.packagelevel.nooverride.Employee.class }, 1);
        
        String src = PATH + "employee-property.xml";
        // since package-info sets to NONE, the following should fail
        String result = validateAgainstSchema(src, null, outputResolver);
        assertTrue("Schema validation passed unxepectedly", result != null);
    }
    
    public void testNoAccessTypePropertiesAdded() {
        String contextPath = CONTEXT_PATH + ".none";
        String path = PATH + "none/";

        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.none.Employee.class }, contextPath, path,  1);

        String src = PATH + "employee-no-access.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation passed unxepectedly " + result, result == null);
    }
}
