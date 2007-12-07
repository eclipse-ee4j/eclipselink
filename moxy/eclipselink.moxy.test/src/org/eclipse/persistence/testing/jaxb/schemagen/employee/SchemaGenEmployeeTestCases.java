/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.schemagen.employee;

import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;

import java.io.File;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import junit.framework.TestCase;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

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
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl()));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(new File(tmpdir + "/schema0.xsd"));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src)); 
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.toString();
        }
        assertTrue("Schema validation failed unexpectedly: " + msg, exception==false);
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
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl()));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
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
}