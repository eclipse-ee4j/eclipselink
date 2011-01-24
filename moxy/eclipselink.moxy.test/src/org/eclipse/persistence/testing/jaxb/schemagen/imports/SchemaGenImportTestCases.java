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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.schemagen.imports;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.address.Address;
import org.xml.sax.SAXException;

public class SchemaGenImportTestCases extends TestCase {
    static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/imports.xml";
    static String INVALID_XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/invalid_imports.xml";
    static String EMPLOYEE_XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/employee.xsd";
    static String ADDRESS_XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/address.xsd";
    static String EMPLOYEE_NS = "employeeNamespace";
    static String ADDRESS_NS = "addressNamespace";
    static String FILE = "file:///";

    public SchemaGenImportTestCases(String name) throws Exception {
        super(name);
    }

    public void testSchemaGenerationWithImport() {
        Class[] jClasses = new Class[] { Address.class, Employee.class};

        Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
        MySystemIDSchemaOutputResolver mysor = new MySystemIDSchemaOutputResolver();
        gen.generateSchemaFiles(mysor, null);

        // validate a valid instance doc against the generated employee schema
        SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
        Schema employeeSchema = null;
        try {
            employeeSchema = sFact.newSchema(mysor.schemaFiles.get(EMPLOYEE_NS));
        } catch (SAXException e) {
            fail("SchemaFactory could not create Employee schema");
        }
        
        StreamSource ss;
        Validator validator = employeeSchema.newValidator();
        try {
            ss = new StreamSource(new File(XML_RESOURCE)); 
            validator.validate(ss);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("An unexpected exception occurred");
        }
        try {
            ss = new StreamSource(new File(INVALID_XML_RESOURCE)); 
            validator.validate(ss);
        } catch (Exception ex) {
            return;
        }
        fail("The expected exception never occurred");
    }
    
    /**
     * SchemaOutputResolver for writing out the generated schema.  Sets
     * the SystemID on the returned result.
     */
    public static class MySystemIDSchemaOutputResolver extends SchemaOutputResolver {
        // keep a list of processed schemas for the validation phase of the test(s)
        public Map<String, File> schemaFiles;
        
        public MySystemIDSchemaOutputResolver() {
            schemaFiles = new HashMap<String, File>();
        }
        
        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            File schemaFile = null;
            Result res = null;
            if (namespaceURI == null) {
                namespaceURI = "";
            } else if (namespaceURI.equals(EMPLOYEE_NS)) {
                schemaFile = new File(EMPLOYEE_XSD_RESOURCE);
            } else if (namespaceURI.equals(ADDRESS_NS)) {
                schemaFile = new File(ADDRESS_XSD_RESOURCE);
            }
            schemaFiles.put(namespaceURI, schemaFile);
            res = new StreamResult(schemaFile);
            return res;
        }
    }
}