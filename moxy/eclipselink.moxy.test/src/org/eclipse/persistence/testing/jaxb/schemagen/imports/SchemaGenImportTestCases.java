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
package org.eclipse.persistence.testing.jaxb.schemagen.imports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.address.Address;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.relativeschemalocation.test.Foo;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.relativeschemalocation.test2.Bar;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class SchemaGenImportTestCases extends TestCase {

    private static final String PACKAGE_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/";
    private static final String IMPORTS_XML = "imports.xml";
    private static final String XML_RESOURCE = PACKAGE_RESOURCE + IMPORTS_XML;
    private static final String INVALID_XML_RESOURCE = PACKAGE_RESOURCE + "invalid_imports.xml";
    private static final String EMPLOYEE_XSD_RESOURCE = PACKAGE_RESOURCE + "employee.xsd";
    private static final String ADDRESS_XSD_RESOURCE = PACKAGE_RESOURCE + "address.xsd";

    private static final String EMPLOYEE_NS = "employeeNamespace";
    private static final String ADDRESS_NS = "addressNamespace";
    private static final String FILE = "file:///";
    private static final String FOO_URI = "http://test.org";
    private static final String FOO_SCHEMA = PACKAGE_RESOURCE + "foo.xsd";
    private static final String BAR_URI = "http://test2.org";
    private static final String BAR_SCHEMA = PACKAGE_RESOURCE + "bar.xsd";

    private static final String tmpdir = System.getenv("T_WORK") == null
            ? System.getProperty("java.io.tmpdir") : System.getenv("T_WORK");

    public SchemaGenImportTestCases(String name) throws Exception {
        super(name);
    }

    public void testSchemaGenerationWithImport() {
        Class[] jClasses = new Class[] { Address.class, Employee.class};

        Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
        File outDir = setOutDir();
        try {
            Files.copy(getFile(PACKAGE_RESOURCE + "someExistingSchema.xsd").toPath(), new File(outDir, "someExistingSchema.xsd").toPath());
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        MySystemIDSchemaOutputResolver mysor = new MySystemIDSchemaOutputResolver(outDir);
        gen.generateSchemaFiles(mysor, null);

        // validate a valid instance doc against the generated employee schema
        SchemaFactory sFact = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
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
            ss = new StreamSource(getFile(XML_RESOURCE));
            validator.validate(ss);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("An unexpected exception occurred");
        }
        try {
            ss = new StreamSource(getFile(INVALID_XML_RESOURCE));
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
        private final File outputDir;

        public MySystemIDSchemaOutputResolver(File dir) {
            schemaFiles = new HashMap<String, File>();
            outputDir = dir;
        }

        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            File schemaFile = null;
            Result res = null;
            if (namespaceURI == null) {
                namespaceURI = "";
            } else if (namespaceURI.equals(EMPLOYEE_NS)) {
                schemaFile = new File(outputDir, "employee.xsd");
            } else if (namespaceURI.equals(ADDRESS_NS)) {
                schemaFile = new File(outputDir, "address.xsd");
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
        File outDir = setOutDir();
        try {
            Class[] jClasses = new Class[] { Address.class, Employee.class};
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(outDir.getAbsolutePath(), null);
        } catch (Exception x) {
            x.printStackTrace();
            fail("An unexpected exception occurred during schema generation: " + x.getMessage());
        }
    }

    public void testRelativeSchemaLocation() throws Exception {
        JAXBContext jctx = JAXBContextFactory.createContext(new Class[] { Foo.class, Bar.class}, null );
        File outDir = setOutDir();
        MyStreamSchemaOutputResolver resolver = new MyStreamSchemaOutputResolver(outDir);
        jctx.generateSchema(resolver);
        Map<String, File> map = resolver.schemaFiles;
        assertTrue("No schemas were generated", map.size() > 0);
        File fooFile = map.get(FOO_URI);
        File barFile = map.get(BAR_URI);
        assertTrue("No schema was generated for Foo", fooFile != null);
        assertTrue("No schema was generated for Bar", barFile != null);

        ExternalizedMetadataTestCases.compareSchemas(fooFile, getFile(FOO_SCHEMA));
        ExternalizedMetadataTestCases.compareSchemas(barFile, getFile(BAR_SCHEMA));
    }

    private File setOutDir() {
        File outDir = new File(tmpdir, getName());
        if (!outDir.mkdirs()) {
            for (File f : outDir.listFiles()) {
                f.delete();
            }
        }
        return outDir;
    }

    private static File getFile(String resourceName) {
        return new File(Thread.currentThread().getContextClassLoader().getResource(resourceName).getPath());
    }

    /**
     * SchemaOutputResolver for writing out the generated schema.  Returns a StreamResult
     * wrapping a StringWriter.
     *
     */
    public static class MyStreamSchemaOutputResolver extends SchemaOutputResolver {
        // keep a list of processed schemas for the validation phase of the test(s)
        public Map<String, File> schemaFiles;
        private final File outputDir;

        public MyStreamSchemaOutputResolver(File dir) {
            schemaFiles = new HashMap<String, File>();
            outputDir = dir;
        }
        @Override
        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            String filePath = this.modifyFileName(namespaceURI);
            File file = new File(outputDir, filePath);
            StreamResult result = new StreamResult(file);
            result.setSystemId(file.toURI().toURL().toString());
            schemaFiles.put(namespaceURI, file);
            return result;
        }

        private String modifyFileName(String namespaceURI) throws IOException {
            String fileName = namespaceURI.startsWith("http://") ? namespaceURI.substring(7) : namespaceURI;
            fileName = fileName.replaceAll("/", "_");
            return  fileName + ".xsd";
        }
    }
}
