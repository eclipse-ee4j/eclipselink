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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XmlAccessorOrder via eclipselink-oxm.xml
 *
 */
public class XmlAccessorOrderTestCases extends ExternalizedMetadataTestCases {
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlaccessororder/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlAccessorOrderTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests @XmlAccessorOrder override via eclipselink-oxm.xml.  The Employee object
     * has the order set to 'UNDEFINED', but this is overridden in the metadata xml
     * file - 'ALPHABETICAL'.
     * 
     * Positive test.
     */
    public void testXmlAccessorOrderOverride() {
        outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
        String src = PATH + "employee-ordered.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests @XmlAccessorOrder override via eclipselink-oxm.xml.  The Employee object
     * has the order set to 'UNDEFINED', but this is overridden in the metadata xml
     * file - 'ALPHABETICAL'.
     * 
     * Negative test.
     */
    public void testXmlAccessorOrderOverrideInvalidDoc() {
        outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
        String src = PATH + "employee-unordered.xml";
        String result = validateAgainstSchema(src, null, outputResolver);
        assertTrue("Schema validation passed unxepectedly", result != null);
    }

    /**
     * Tests overriding @XmlAccessorOrder set in package-info.java via
     * eclipselink-oxm.xml.  Here, the order is set to 'UNDEFINED' in
     * package-info.java, but this is overridden in the metadata xml
     * file - 'ALPHABETICAL'.
     * 
     * Positive test.
     */
    public void testXmlAccessorOrderPackageLevelOverride() {
        String contextPath = CONTEXT_PATH + ".packagelevel";
        String path = PATH + "packagelevel/";
        
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder.packagelevel.Employee.class }, contextPath, path, 1);
        
        String src = PATH + "employee-ordered.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests overriding @XmlAccessorOrder set at the package level in eclipselink-oxm.xml with
     * one set at the class level in eclipselink-oxm.xml.  Here, the order is set to 
     * 'ALPHABETICAL' at the package level, but overridden as 'UNDEFINED'.
     * 
     * Positive test.
     */
    public void testXmlAccessorOrderClassOverridesPackage() {
        String contextPath = CONTEXT_PATH + ".packagelevel.classoverride";
        String path = PATH + "packagelevel/classoverride/";
        
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder.packagelevel.classoverride.Employee.class }, contextPath, path, 1);
        
        String src = PATH + "employee-unordered.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests the @XmlAccessorOrder set in the java class will override one set in 
     * eclipselink-oxm.xml at the package level.  eclipselink-oxm.xml will have 
     * 'ALPHABETICAL' at the package level, but it will be set to 'UNDEFINED' in the
     * java class.
     * 
     * Positive test.
     */
    public void testXmlAccessorOrderJavaClassOverridesPackage() {
        String contextPath = CONTEXT_PATH + ".packagelevel.javaclassoverride";
        String path = PATH + "packagelevel/javaclassoverride/";
        
        outputResolver = generateSchema(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder.packagelevel.javaclassoverride.Employee.class }, contextPath, path, 1);
        
        String src = PATH + "employee-unordered.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
}
