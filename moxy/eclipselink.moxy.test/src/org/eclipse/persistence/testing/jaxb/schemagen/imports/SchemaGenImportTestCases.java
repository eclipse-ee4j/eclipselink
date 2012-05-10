/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.address.Address;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.relativeschemalocation.test.Foo;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.relativeschemalocation.test2.Bar;
import org.xml.sax.SAXException;

public class SchemaGenImportTestCases extends TestCase {
    static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/imports.xml";
    static String INVALID_XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/invalid_imports.xml";
    static String EMPLOYEE_XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/employee.xsd";
    static String ADDRESS_XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/address.xsd";
    static String EMPLOYEE_NS = "employeeNamespace";
    static String ADDRESS_NS = "addressNamespace";
    static String FILE = "file:///";
    static String FOO_URI = "http://test.org"; 
    static String FOO_SCHEMA = "org/eclipse/persistence/testing/jaxb/schemagen/imports/foo.xsd"; 
    static String BAR_URI = "http://test2.org"; 
    static String BAR_SCHEMA = "org/eclipse/persistence/testing/jaxb/schemagen/imports/bar.xsd"; 

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
        	e.printStackTrace();
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

    /**
     * Test's that the import schema location can be relativized given a 
     * source schema with no path info in the name, i.e. "employee.xsd".
     * No exception should occur.
     * 
     */
    public void testRelativeSchemaLocationWithNoSlash() {
        try {
            Class[] jClasses = new Class[] { Address.class, Employee.class};
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(".", null);
        } catch (Exception x) {
            x.printStackTrace();
            fail("An unexpected exception occurred during schema generation: " + x.getMessage());
        }
    }

    public void testRelativeSchemaLocation() throws Exception {
        JAXBContext jctx = JAXBContextFactory.createContext(new Class[] { Foo.class, Bar.class}, null );
        MyStreamSchemaOutputResolver resolver = new MyStreamSchemaOutputResolver();
        jctx.generateSchema(resolver);
        Map<String, File> map = resolver.schemaFiles;
        assertTrue("No schemas were generated", map.size() > 0);
        File fooFile = map.get(FOO_URI);
        File barFile = map.get(BAR_URI);
        assertTrue("No schema was generated for Foo", fooFile != null);
        assertTrue("No schema was generated for Bar", barFile != null);
        
        ExternalizedMetadataTestCases.compareSchemas(fooFile, new File(FOO_SCHEMA));
        ExternalizedMetadataTestCases.compareSchemas(barFile, new File(BAR_SCHEMA));
    }
    
    /**
     * SchemaOutputResolver for writing out the generated schema.  Returns a StreamResult
     * wrapping a StringWriter.
     *
     */
    public static class MyStreamSchemaOutputResolver extends SchemaOutputResolver {
        // keep a list of processed schemas for the validation phase of the test(s)
        public Map<String, File> schemaFiles;
        
        public MyStreamSchemaOutputResolver() {
            schemaFiles = new HashMap<String, File>();
        }
        @Override
        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            String filePath = this.modifyFileName(namespaceURI);
            File file = new File(filePath);
            StreamResult result = new StreamResult(file);
            result.setSystemId(file.toURI().toURL().toString());
            schemaFiles.put(namespaceURI, file);
            return result;
        }

        private String modifyFileName(String namespaceURI) throws IOException {
            String fileName = namespaceURI.substring(7);
            fileName = fileName.replaceAll("/", "_");
            return  fileName + ".xsd"; 
        }
    }
}