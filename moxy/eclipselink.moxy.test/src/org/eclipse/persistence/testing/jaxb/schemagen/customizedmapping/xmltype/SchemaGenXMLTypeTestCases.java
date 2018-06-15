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
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmltype;

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
public class SchemaGenXMLTypeTestCases extends SchemaGenTestCases {

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public SchemaGenXMLTypeTestCases(String name) throws Exception {
        super(name);
    }

    public void testBaseType001cPositive() throws Exception {
        boolean exception = false;
        String msg = null;
        InputStream src = null;
        File newXsd = new File(tmpdir, "schema1.xsd");
        if (newXsd.exists() && newXsd.isFile() && newXsd.delete()) {
            System.err.println("removed existing: " + newXsd.getAbsolutePath());
        }

        try {
            src = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/customizedmapping/xmltype/BaseType001p.xml");
            Class[] jClasses = new Class[] { BaseType001c.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(newXsd);
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

    public void testBaseType001cNegative() throws Exception {
        boolean exception = false;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/customizedmapping/xmltype/BaseType001n.xml";
        File newXsd = new File(tmpdir, "schema0.xsd");
        if (newXsd.exists() && newXsd.isFile() && newXsd.delete()) {
            System.err.println("removed existing: " + newXsd.getAbsolutePath());
        }

        try {
            Class[] jClasses = new Class[] { BaseType001c.class };
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema theSchema = sFact.newSchema(newXsd);
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src));
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
        }
        assertFalse("Schema validation did not fail as expected: ", exception==false);
    }
}
