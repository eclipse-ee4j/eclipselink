/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlrootelement;

import java.io.File;
import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;

/**
 * Schema generation tests - based on the JAXB 2.0 TCK:
 *     java2schema/CustomizedMapping/classes/XMLRootElement
 */
public class SchemaGenXMLRootElementTestCases extends SchemaGenTestCases {

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public SchemaGenXMLRootElementTestCases(String name) throws Exception {
        super(name);
    }

    public void testName001Positive() throws Exception {
        boolean exception = false;
        String msg = null;
        InputStream src = null;
        try {
            src = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/customizedmapping/xmlrootelement/Name001p.xml");
            Class[] jClasses = new Class[] { Name001.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(new File(tmpdir, "schema1.xsd"));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(src);
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.toString();
        } finally {
            if (null != src) {
                try {
                    src.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        assertTrue("Schema validation failed unexpectedly: " + msg, exception==false);
    }

    public void testName001Negative() throws Exception {
        boolean exception = false;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/customizedmapping/xmlrootelement/Name001n.xml";
        try {
            Class[] jClasses = new Class[] { Name001.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(new File(tmpdir, "schema1.xsd"));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src));
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse("Schema validation did not fail as expected: ", exception==false);
    }

    public void testName002Positive() throws Exception {
        boolean exception = false;
        String msg = null;
        InputStream src = null;
        try {
            src = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/customizedmapping/xmlrootelement/Name002p.xml");
            Class[] jClasses = new Class[] { Name002.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(new File(tmpdir, "schema1.xsd"));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(src);
            validator.validate(ss);
        } catch (Exception ex) {
            msg = ex.getMessage();
            exception = true;
        } finally {
            if (null != src) {
                src.close();
            }
        }
        assertTrue("Schema validation failed unexpectedly: " + msg, exception==false);
    }

    public void testName002Negative() throws Exception {
        boolean exception = false;
        String msg = null;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/customizedmapping/xmlrootelement/Name002n.xml";
        try {
            Class[] jClasses = new Class[] { Name002.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(new File(tmpdir, "schema1.xsd"));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src));
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.getMessage();
        }
        assertFalse("Schema validation did not fail as expected: " + msg, exception==false);
    }

    public void testNamespace001Positive() throws Exception {
        boolean exception = false;
        String msg = null;
        InputStream src = null;
        try {
            src = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/customizedmapping/xmlrootelement/NameSpace001p.xml");
            Class[] jClasses = new Class[] { NameSpace001.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(new File(tmpdir, "schema2.xsd"));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(src);
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.getMessage();
        } finally {
            if (null != src) {
                src.close();
            }
        }
        assertTrue("Schema validation failed unexpectedly: " + msg, exception==false);
    }

    public void testNamespace001Negative() throws Exception {
        boolean exception = false;
        String msg = null;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/customizedmapping/xmlrootelement/NameSpace001n.xml";
        try {
            Class[] jClasses = new Class[] { NameSpace001.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(new File(tmpdir, "schema2.xsd"));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src));
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.getMessage();
        }
        assertFalse("Schema validation did not fail as expected: " + msg, exception==false);
    }
}
