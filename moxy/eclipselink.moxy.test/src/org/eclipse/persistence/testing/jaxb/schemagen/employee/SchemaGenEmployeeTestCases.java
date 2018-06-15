/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.schemagen.employee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;

import junit.framework.TestCase;

/**
 * Schema generation tests - based on the JAXB 2.0 TCK:
 *     java2schema/CustomizedMapping/classes/XMLRootElement
 */
public class SchemaGenEmployeeTestCases extends TestCase {

    private static final String tmpdir = System.getenv("T_WORK") == null
            ? System.getProperty("java.io.tmpdir") : System.getenv("T_WORK");

    public SchemaGenEmployeeTestCases(String name) throws Exception {
        super(name);
    }

    public void testEmployeeSchemaGeneration() throws Exception {
        boolean exception = false;
        String msg = null;
        InputStream employeeXml = null;

        File newXsd = new File(tmpdir, "schema1.xsd");
        if (newXsd.exists() && newXsd.isFile() && newXsd.delete()) {
            System.err.println("removed existing: " + newXsd.getAbsolutePath());
        }

        try {
            employeeXml = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/employee/employee.xml");

            Class[] jClasses = new Class[] { Address.class, Employee.class, PhoneNumber.class, Department.class, MyTestType.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(newXsd);
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(employeeXml);
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.toString();
        } finally {
            if (null != employeeXml) {
                try {
                    employeeXml.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        assertTrue("Schema validation failed unexpectedly: " + msg, exception==false);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        builderFactory.setNamespaceAware(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();

        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/employee/schema1.xsd");
        Document control = parser.parse(stream);

        stream = new FileInputStream(newXsd);
        Document test = parser.parse(stream);

        JAXBXMLComparer xmlComparer = new JAXBXMLComparer();

        assertTrue("schema1.xsd did not match control document", xmlComparer.isSchemaEqual(control, test));


    }

    /**
     * The following test expects a schema validation exception to occur.
     * This is due to the fact that the supplied instance document does
     * not contain a 'firstName' element, which is required as the
     * corresponding field in the Employee class contains the following
     * annotation:  @XmlElement(required = true)
     * @throws Exception
     */
    public void xtestEmployeeSchemaGenMissingRequiredElement() throws Exception {
        boolean exception = false;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/employee/employee_missing_required_element.xml";
        String msg = "";

        File newXsd = new File(tmpdir, "/schema1.xsd");
        if (newXsd.exists() && newXsd.isFile() && newXsd.delete()) {
            System.err.println("removed existing: " + newXsd.getAbsolutePath());
        }

        try {
            Class[] jClasses = new Class[] { Address.class, Employee.class, PhoneNumber.class, Department.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(newXsd);
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
    public void xtestEmployeeSchemaGenMissingRequiredAttribute() throws Exception {
        boolean exception = false;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/employee/employee_missing_required_attribute.xml";
        String msg = "";

        File newXsd = new File(tmpdir, "schema1.xsd");
        if (newXsd.exists() && newXsd.isFile() && newXsd.delete()) {
            System.err.println("removed existing: " + newXsd.getAbsolutePath());
        }

        try {
            Class[] jClasses = new Class[] { Address.class, Employee.class, PhoneNumber.class, Department.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(newXsd);
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

    /**
     * In this test the xsi:type of the root element set to subclass, which is valid.
     */
    public void xtestAbstractSuperclassVaildDoc() throws Exception {
        boolean exception = false;
        String msg = null;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/employee/valid_inheritance.xml";

        File newXsd = new File(tmpdir, "schema1.xsd");
        if (newXsd.exists() && newXsd.isFile() && newXsd.delete()) {
            System.err.println("removed existing: " + newXsd.getAbsolutePath());
        }

        try {
            Class[] jClasses = new Class[] { MyAbstractTestType.class, MyTestSubType.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(newXsd);
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src));
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.toString();
        }
        assertFalse("Schema validation failed unexpectedly: " + msg, exception);
    }

    /**
     * In this test the xsi:type of the root element set to the abstract superclass,
     * which should cause a validation exception.
     */
    public void xtestAbstractSuperclassInvaildDoc() throws Exception {
        boolean exception = false;
        String msg = null;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/employee/invalid_inheritance.xml";

        File newXsd = new File(tmpdir, "schema1.xsd");
        if (newXsd.exists() && newXsd.isFile() && newXsd.delete()) {
            System.err.println("removed existing: " + newXsd.getAbsolutePath());
        }

        try {
            Class[] jClasses = new Class[] { MyAbstractTestType.class, MyTestSubType.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(newXsd);
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src));
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.toString();
        }
        assertTrue("Schema validation did not fail as expected: " + msg, exception);
    }

    /**
     * This test will validate the descriptor's configuration wrt inheritance for
     * an abstract superclass via marshal operation.
     */
    public void xtestAbstractSuperclassMarshal() throws Exception {
        boolean exception = false;
        String msg = null;
        String src = "output.xml";
        MyTestSubType testObj = new MyTestSubType();
        testObj.subTypeInt = 66;

        try {
            Class[] jClasses = new Class[] { MyAbstractTestType.class, MyTestSubType.class };
            JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(jClasses, null);
            Marshaller marshaller = jCtx.createMarshaller();
            FileWriter fw = new FileWriter(new File(tmpdir, src));
            marshaller.marshal(testObj, fw);
        } catch (Exception ex) {
            exception = true;
            ex.printStackTrace();
            msg = ex.toString();
        }
        assertFalse("Marshal operation failed unexpectedly: " + msg, exception);
    }

    /**
     * This test will validate the descriptor's configuration wrt inheritance for
     * an abstract superclass via unmarshal operation.
     */
    public void xtestAbstractSuperclassUnmarshal() throws Exception {
        boolean exception = false;
        String msg = null;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/employee/mytestsubtype.xml";
        Object obj = null;

        try {
            Class[] jClasses = new Class[] { MyAbstractTestType.class, MyTestSubType.class };
            JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(jClasses, null);
            Unmarshaller unmarshaller = jCtx.createUnmarshaller();
            obj = unmarshaller.unmarshal(new File(src));
        } catch (Exception ex) {
            exception = true;
            msg = ex.toString();
        }
        assertFalse("Unmarshal operation failed unexpectedly: " + msg, exception);
        assertNotNull("Unmarshal operation resulted in null", obj);

        assertTrue(obj instanceof JAXBElement);
        assertEquals(MyAbstractTestType.class, ((JAXBElement)obj).getDeclaredType());
    }
}
