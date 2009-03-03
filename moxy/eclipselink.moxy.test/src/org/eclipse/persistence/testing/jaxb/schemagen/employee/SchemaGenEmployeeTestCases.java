/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.schemagen.employee;

import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import junit.framework.TestCase;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Schema generation tests - based on the JAXB 2.0 TCK: 
 *     java2schema/CustomizedMapping/classes/XMLRootElement 
 */
public class SchemaGenEmployeeTestCases extends TestCase {
    public SchemaGenEmployeeTestCases(String name) throws Exception {
        super(name);
    }

    public void testEmployeeSchemaGeneration() throws Exception {
        boolean exception = false;
        String msg = null;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/employee/employee.xml";
        String tmpdir = System.getenv("T_WORK");

        try {
            Class[] jClasses = new Class[] { Address.class, Employee.class, PhoneNumber.class, Department.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
            Schema theSchema = sFact.newSchema(new File(tmpdir + "/schema0.xsd"));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src)); 
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.toString();
        }
        assertTrue("Schema validation failed unexpectedly: " + msg, exception==false);
                        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        builderFactory.setNamespaceAware(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
            
        InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/employee/schema0.xsd");
        Document control = parser.parse(stream);
            
        stream = new FileInputStream(new File(tmpdir + "/schema0.xsd"));
        Document test = parser.parse(stream);
            
        JAXBXMLComparer xmlComparer = new JAXBXMLComparer();
        
        assertTrue("schema0.xsd did not match control document", xmlComparer.isSchemaEqual(control, test));
        	
        stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/employee/schema1.xsd");
        control = parser.parse(stream);
            
        stream = new FileInputStream(new File(tmpdir + "/schema1.xsd"));
        test = parser.parse(stream);
                        
        assertTrue("schema1.xsd did not match control document", xmlComparer.isNodeEqual(control, test));     
        
    }
    
    /**
     * The following test expects a schema validation exception to occur.
     * This is due to the fact that the supplied instance document does
     * not contain a 'firstName' element, which is required as the 
     * corresponding field in the Employee class contains the following
     * annotation:  @XmlElement(required = true)
     * @throws Exception
     */
    public void testEmployeeSchemaGenMissingRequiredElement() throws Exception {
        boolean exception = false;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/employee/employee_missing_required_element.xml";
        String tmpdir = System.getenv("T_WORK");
        String msg = "";

        try {
            Class[] jClasses = new Class[] { Address.class, Employee.class, PhoneNumber.class, Department.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
            Schema theSchema = sFact.newSchema(new File(tmpdir + "/schema0.xsd"));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src)); 
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.getLocalizedMessage();
        }
        assertTrue("Schema validation passed unexpectedly", exception);
        assertTrue("An unexpected exception occurred: " + msg, msg.contains("firstName"));
    }
    
    /**
     * The following test expects a schema validation exception to occur.
     * This is due to the fact that the supplied instance document does
     * not contain a 'firstName' element, which is required as the 
     * corresponding field in the Employee class contains the following
     * annotation:  @XmlElement(required = true)
     * @throws Exception
     */
    public void testEmployeeSchemaGenMissingRequiredAttribute() throws Exception {
        boolean exception = false;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/employee/employee_missing_required_attribute.xml";
        String tmpdir = System.getenv("T_WORK");
        String msg = "";

        try {
            Class[] jClasses = new Class[] { Address.class, Employee.class, PhoneNumber.class, Department.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
            Schema theSchema = sFact.newSchema(new File(tmpdir + "/schema0.xsd"));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src)); 
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.getLocalizedMessage();
        }
        assertTrue("Schema validation passed unexpectedly", exception);
        assertTrue("An unexpected exception occurred: " + msg, msg.contains("id"));
    }
}