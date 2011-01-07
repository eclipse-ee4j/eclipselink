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
 * dmccann - September 25/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlElementWrapper via eclipselink-oxm.xml
 *
 */
public class XmlElementWrapperTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementwrapper";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlelementwrapper/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlElementWrapperTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  This tests validates
     * schema generation when an @XmlElementWrapper is present in code/xml.   
     * 
     * Positive test.
     */
    public void doTestSchemaGeneration() {
        outputResolver = generateSchema(CONTEXT_PATH, PATH, 1);
        shouldGenerateSchema = false;
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  No overrides are done,
     * so the class annotations should be used to generate the schema.
     * 
     * Positive test.
     */
    public void testXmlElementWrapperNoOverride() {
        outputResolver = generateSchema(new Class[] { Employee.class }, 1);
        // validate schema
        String controlSchema = PATH + "schema.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
        
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  Here, a number of
     * overrides are performed.
     * 
     * Here, @XmlElementWrapper.namespace() is not "##default" and different 
     * from the target namespace of the enclosing class.  An element declaration 
     * whose name is @XmlElementWrapper.name() and target namespace is
     * @XmlElementWrapper.namespace() should be generated.  Note: The element 
     * declaration is assumed to already exist and is not created.
     * 
     * Positive test.
     */
    public void testXmlElementWrapperNS() {
        String metadataFile = PATH + "eclipselink-oxm-ns.xml";
        
        outputResolver = generateSchemaWithFileName(CONTEXT_PATH, metadataFile, 1);

        // validate schema
        String controlSchema = PATH + "schema_ns.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));
    }

    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  Here, a number of
     * overrides are performed.
     * 
     * Positive test.
     */
    public void testXmlElementWrapper() {
        if (shouldGenerateSchema) {
            doTestSchemaGeneration();
        }
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  Here, a number of
     * overrides are performed.  In addition, an @XmlElement annotation is
     * used to override the name of the wrapped element from 'digits' to 
     * 'a-digit'.
     * 
     * Positive test.
     */
    public void testXmlElementWrapperWithXmlElementOverride() {
        String metadataFile = PATH + "eclipselink-oxm-xmlelement.xml";

        outputResolver = generateSchemaWithFileName(CONTEXT_PATH, metadataFile, 1);

        // validate schema
        String controlSchema = PATH + "schema_xmlelement.xsd";
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(controlSchema));

        String src = PATH + "employee-xmlelement.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  The instance document
     * does not contain the 'my-digits' wrapper, and is therefore invalid.  
     * 
     * Negative test.
     */
    public void testXmlElementWrapperNoWrapper() {
        if (shouldGenerateSchema) {
            doTestSchemaGeneration();
        }
        String src = PATH + "employee-invalid.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation passed unxepectedly", result != null);
    }

    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  The instance document
     * contains a nil 'my-digits' wrapper element.  
     * 
     * Positive test.
     */
    public void testXmlElementWrapperNil() {
        if (shouldGenerateSchema) {
            doTestSchemaGeneration();
        }
        String src = PATH + "employee-nil-wrapper.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  Here, a number of
     * overrides are performed. A marshal operation is performed to ensure
     * the wrapper is processed correctly.
     * 
     * Positive test.
     */
    public void testXmlElementWrapperMarshal() {
        if (shouldGenerateSchema) {
            doTestSchemaGeneration();
        }
        
        Employee emp = new Employee();
        int[] theDigits = new int[] { 666, 999 };
        emp.digits = theDigits;
        
        Document testDoc = parser.newDocument();
        
        Marshaller marshaller = getJAXBContext().createMarshaller();
        try {
            marshaller.marshal(emp, testDoc);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred marshalling the Employee.");
        }

        String src = PATH + "employee.xml";
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }

        assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
    }

    /**
     * Tests @XmlElementWrapper via eclipselink-oxm.xml.  Here, a number of
     * overrides are performed. An unmarshal operation is performed to ensure
     * the wrapper is processed correctly.
     * 
     * Positive test.
     */
    public void testXmlElementWrapperUnmarshal() {
        if (shouldGenerateSchema) {
            doTestSchemaGeneration();
        }
        
        Employee emp = null;
        Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
        try {
            String src = PATH + "employee.xml";
            emp = (Employee) unmarshaller.unmarshal(getControlDocument(src));
            assertNotNull("The Employee object is null after unmarshal.", emp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred unmarshalling the document.");
        }

        Employee ctrlEmp = new Employee();
        int[] theDigits = new int[] { 666, 999 };
        ctrlEmp.digits = theDigits;

        assertTrue("The unmarshalled Employee did not match the control Employee object.", ctrlEmp.equals(emp));
    }
}
