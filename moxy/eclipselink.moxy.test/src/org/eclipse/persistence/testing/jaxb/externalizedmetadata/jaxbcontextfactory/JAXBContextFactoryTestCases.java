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
// dmccann - June 17/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.metadata.XMLMetadataSource;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.a.BeanA;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.b.BeanB;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Tests various JAXBContext creation methods.
 *
 */
public class JAXBContextFactoryTestCases extends ExternalizedMetadataTestCases {
    private MySchemaOutputResolver outputResolver;
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/";
    private static final String ARRAY_NAMESPACE = "http://jaxb.dev.java.net/array";
    private static final String BEAN_NAMESPACE = "defaultTns";
    private static final String FOO_XML = "foo.xml";
    private static final String FOO_OXM_XML = "foo-oxm.xml";
    private static final String FOO_OXM_JSON = "foo-oxm.json";
    private static final String NO_PKG_OXM_XML = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/bindingformat/eclipselink-oxm-no-package.xml";

    private static final String FILE_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/bindingformat/file/";
    private static final String INPUT_SRC_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/bindingformat/inputsource/";
    private static final String INPUT_STRM_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/bindingformat/inputstream/";
    private static final String READER_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/bindingformat/reader/";
    private static final String SOURCE_PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/bindingformat/source/";

    private static URI FILE_OXM_XML;
    private static URI FILE_OXM_JSON;
    private static URI FILE_OXM_JSON_INVALID;
    private static URL INPUT_SRC_OXM_XML;
    private static URL INPUT_SRC_OXM_JSON;
    private static URL INPUT_STRM_OXM_XML;
    private static URL INPUT_STRM_OXM_JSON;
    private static URL READER_OXM_XML;
    private static URL READER_EMPTY_OXM_XML;
    private static URL READER_EMPTY_OXM_JSON;
    private static URL READER_OXM_JSON;

    private static final String SOURCE_OXM_XML = SOURCE_PATH + FOO_OXM_XML;
    private static final String SOURCE_OXM_JSON = SOURCE_PATH + FOO_OXM_JSON;
    private static final String FILE_XML = FILE_PATH + FOO_XML;
    private static final String INPUT_SRC_XML = INPUT_SRC_PATH + FOO_XML;
    private static final String INPUT_STRM_XML = INPUT_STRM_PATH + FOO_XML;
    private static final String READER_XML = READER_PATH + FOO_XML;
    private static final String READER_NO_BINDINGS = READER_PATH + "foo_no_bindings.xml";
    private static final String SOURCE_XML = SOURCE_PATH + FOO_XML;

    private static final String FILE = "File";
    private static final String INPUT_SRC = "InputSource";
    private static final String INPUT_STRM = "InputStream";
    private static final String READER = "Reader";
    private static final String SOURCE = "Source";

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     */
    public JAXBContextFactoryTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();

        FILE_OXM_XML = Thread.currentThread().getContextClassLoader().getResource(FILE_PATH + FOO_OXM_XML).toURI();
        FILE_OXM_JSON = Thread.currentThread().getContextClassLoader().getResource(FILE_PATH + FOO_OXM_JSON).toURI();
        FILE_OXM_JSON_INVALID = Thread.currentThread().getContextClassLoader().getResource(FILE_PATH + "foo-oxm-invalid.json").toURI();

        INPUT_SRC_OXM_XML = Thread.currentThread().getContextClassLoader().getResource(INPUT_SRC_PATH + FOO_OXM_XML);
        INPUT_SRC_OXM_JSON = Thread.currentThread().getContextClassLoader().getResource(INPUT_SRC_PATH + FOO_OXM_JSON);
        INPUT_STRM_OXM_XML = Thread.currentThread().getContextClassLoader().getResource(INPUT_STRM_PATH + FOO_OXM_XML);
        INPUT_STRM_OXM_JSON = Thread.currentThread().getContextClassLoader().getResource(INPUT_STRM_PATH + FOO_OXM_JSON);
        READER_OXM_XML = Thread.currentThread().getContextClassLoader().getResource(READER_PATH + FOO_OXM_XML);
        READER_EMPTY_OXM_XML = Thread.currentThread().getContextClassLoader().getResource(READER_PATH + "foo-oxm_empty.xml");
        READER_EMPTY_OXM_JSON = Thread.currentThread().getContextClassLoader().getResource(READER_PATH + "foo-oxm_empty.json");
        READER_OXM_JSON = Thread.currentThread().getContextClassLoader().getResource(READER_PATH + FOO_OXM_JSON);
    }

    /**
     * Tests override via eclipselink-oxm.xml.  Here, the metadata file is not
     * handed in via properties or context path, but looked up by package in
     * the context factory.  An @XmlTransient override will be performed
     * on Employee.lastName to ensure the xml file was picked up properly.
     *
     * Positive test.
     */
    public void testLoadXmlFileViaPackage() throws IOException {
        outputResolver = generateSchema(new Class[] { Employee.class }, CONTEXT_PATH, PATH, 1);
        InputStream src = Thread.currentThread().getContextClassLoader().getResource(PATH + "employee.xml").openStream();
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests override via eclipselink-oxm.xml.  Here, the metadata file is not
     * handed in via properties or context path, but looked up by package in
     * the context factory.  An @XmlTransient override will be performed
     * on Address to ensure the xml file was picked up properly.
     *
     * 1 x Positive test, 1x Negative test
     */
    public void testLoadMultipleXmlFilesViaSamePackage() throws IOException {
        outputResolver = generateSchema(new Class[] { Employee.class, Address.class }, CONTEXT_PATH, PATH, 1);
        InputStream src = Thread.currentThread().getContextClassLoader().getResource(PATH + "address.xml").openStream();
        String result = validateAgainstSchema(src, null, outputResolver);
        // address is set to transient in Xml, should fail
        assertTrue("Schema validation passed unxepectedly", result != null);
        src = Thread.currentThread().getContextClassLoader().getResource(PATH + "employee.xml").openStream();
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests override via eclipselink-oxm.xml.  Here, the metadata files are not
     * handed in via properties or context path, but looked up by package in
     * the context factory.  Various overrides will be performed to ensure
     * the xml files were picked up properly.
     *
     * 1 x Positive tests, 1x Negative test
     */
    public void testLoadMultipleXmlFilesViaDifferentPackage() throws IOException {
        outputResolver = generateSchema(new Class[] { Employee.class, Address.class, }, CONTEXT_PATH, PATH, 1);

        InputStream src = Thread.currentThread().getContextClassLoader().getResource(PATH + "employee.xml").openStream();
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
        src = Thread.currentThread().getContextClassLoader().getResource(PATH + "address.xml").openStream();
        result = validateAgainstSchema(src, null, outputResolver);
        // address is set to transient in Xml, should fail
        assertTrue("Schema validation passed unxepectedly", result != null);
    }

    /**
     * Test loading metadata via properties map.
     *
     * Positive test.
     */
    public void testLoadXmlFilesViaProperties() throws IOException, URISyntaxException {
        String contextPath = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.properties.foo:org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.properties.bar";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String metadataFile = PATH + "properties/bar/eclipselink-oxm.xml";

        InputStream iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.properties.bar", new StreamSource(iStream));

        metadataFile = PATH + "properties/foo/eclipselink-oxm.xml";

        iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.properties.foo", new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        outputResolver = generateSchema(contextPath, properties, 1);

        // validate schema against control schema
        compareSchemas(new File(classLoader.getResource(PATH + "properties/schema.xsd").toURI()), outputResolver.schemaFiles.get(EMPTY_NAMESPACE));

        // validate instance docs against schema
        InputStream src = classLoader.getResource(PATH + "properties/bar/employee.xml").openStream();
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
        src = classLoader.getResource(PATH + "properties/foo/home-address.xml").openStream();
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    public void testArrayOfTypes() {
        try {
            Field addressesField = org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.arrayoftypes.Employee.class.getDeclaredField("addresses");
            Type[] types = new Type[2];
            types[0] = addressesField.getGenericType();
            types[1] = org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.arrayoftypes.Employee.class;
            String contextPath = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.arrayoftypes";
            String path = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/arrayoftypes/";
            outputResolver = generateSchema(types, contextPath, path, 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Test processing an eclipselink-oxm.xml file with no JavaTypes.
     *
     */
    public void testBindingsFileWithNoTypes() {
        String metadataFile = PATH + "eclipselink-oxm-no-types.xml";
        generateSchemaWithFileName(new Class[] {}, "org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory", metadataFile, 0);
    }

    /**
     * Test passing a String[] into the context factory via Type[].
     *
     */
    public void testStringArrayInTypesToBeBound() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String metadataFile = PATH + "stringarray/a/eclipselink-oxm.xml";
        InputStream iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        HashMap<String, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.a", new StreamSource(iStream));

        metadataFile = PATH + "stringarray/b/eclipselink-oxm.xml";
        iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.b", new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        JAXBContext jaxbContext;
        try {
            Type[] types = {
                    BeanA.class,
                    BeanB.class,
                    String[].class
                };

            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(types, properties, loader);
            jaxbContext.generateSchema(outputResolver);
            URI controlSchema = Thread.currentThread().getContextClassLoader().getResource(PATH + "stringarray/bean_schema.xsd").toURI();
            compareSchemas(outputResolver.schemaFiles.get(BEAN_NAMESPACE), new File(controlSchema));
            controlSchema = Thread.currentThread().getContextClassLoader().getResource(PATH + "stringarray/string_array_schema.xsd").toURI();
            compareSchemas(outputResolver.schemaFiles.get(ARRAY_NAMESPACE), new File(controlSchema));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Test passing a String[] into the context factory via Class[].
     *
     */
    public void testStringArrayInClassesToBeBound() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String metadataFile = PATH + "stringarray/a/eclipselink-oxm.xml";
        InputStream iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }

        HashMap<String, Source> metadataSourceMap = new HashMap<>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.a", new StreamSource(iStream));

        metadataFile = PATH + "stringarray/b/eclipselink-oxm.xml";
        iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.b", new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        JAXBContext jaxbContext;
        try {
            Class<?>[] types = {
                    BeanA.class,
                    BeanB.class,
                    String[].class
                };

            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(types, properties, loader);
            jaxbContext.generateSchema(outputResolver);
            String controlSchema = PATH + "stringarray/bean_schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(BEAN_NAMESPACE), new File(Thread.currentThread().getContextClassLoader().getResource(controlSchema).toURI()));
            controlSchema = PATH + "stringarray/string_array_schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(ARRAY_NAMESPACE),  new File(Thread.currentThread().getContextClassLoader().getResource(controlSchema).toURI()));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testBindingFormatFile() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new File(FILE_OXM_XML));
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, properties, loader);
        doTestFile(jCtx);
    }

    public void testBindingFormatXMLMetadataSource() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new XMLMetadataSource(new File(FILE_OXM_XML)));
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, properties, loader);
        doTestFile(jCtx);
    }

    public void testBindingFormatInputSource() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new InputSource(INPUT_SRC_OXM_XML.openStream()));
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, properties, loader);
        doTestInputSrc(jCtx);
    }

    public void testBindingFormatString() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, INPUT_SRC_OXM_XML);
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, properties, loader);
        doTestInputSrc(jCtx);
    }

    public void testBindingFormatStringClasspath() throws Exception {
        URL[] urls = new URL[1];
        File f = new File(Thread.currentThread().getContextClassLoader().
                getResource("org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/bindingformat/testfolder").toURI());
        urls[0] = f.toURL();
        URLClassLoader testLoader = new URLClassLoader(urls);


        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, "subfolder/foo-oxm.xml");

        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, properties, testLoader);
        doTestInputSrc(jCtx);
    }

    public void testBindingFormatStringInvalid() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, "foobar");
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class };
        boolean exceptionThrown = false;
        try {
            JAXBContextFactory.createContext(classes, properties, loader);
        } catch(Exception ex) {
            exceptionThrown = true;
            assertTrue("Incorrect exception caught", ((org.eclipse.persistence.exceptions.JAXBException)ex).getErrorCode() == 50027);
        }
        if(!exceptionThrown) {
            fail("Expected exception now thrown.");
        }
    }

    public void testBindingFormatStringURL() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        File file = new File(INPUT_SRC_OXM_XML.toURI());
        URL url = file.toURI().toURL();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, url.toExternalForm());
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, properties, loader);
        doTestInputSrc(jCtx);

    }
    public void testBindingFormatMetadataSourceString() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new XMLMetadataSource(INPUT_SRC_OXM_XML));
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, properties, loader);
        doTestInputSrc(jCtx);
    }

    public void testBindingFormatMetadatSourceNull() throws Exception {
        boolean exception = false;
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new XMLMetadataSource((File)null));
        } catch(IllegalArgumentException ex) {
            exception = true;
        }
        assertTrue("Expected exception not thrown", exception);
    }
    public void testBindingFormatMetadataSourceStringInvalid() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new XMLMetadataSource("foobar"));
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class };
        boolean exceptionThrown = false;
        try {
            JAXBContextFactory.createContext(classes, properties, loader);
        } catch(Exception ex) {
            exceptionThrown = true;
            assertTrue("Incorrect exception caught", ((org.eclipse.persistence.exceptions.JAXBException)ex).getErrorCode() == 50076);
        }
        if(!exceptionThrown) {
            fail("Expected exception now thrown.");
        }
    }

    public void testBindingFormatMetadataSourceURLString() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        File file = new File(INPUT_SRC_OXM_XML.toURI());
        URL url = file.toURI().toURL();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new XMLMetadataSource(url.toExternalForm()));
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, properties, loader);
        doTestInputSrc(jCtx);
    }

    public void testBindingFormatInputStream() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, INPUT_STRM_OXM_XML.openStream());
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputstream.Foo.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, properties, loader);
        doTestInputStrm(jCtx);
    }

    public void testBindingsEmptyXML() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new InputStreamReader(READER_EMPTY_OXM_XML.openStream()));
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.FooWithRootElement.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, null, loader);

        org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.FooWithRootElement fooWithRoot = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.FooWithRootElement("a123");
        marshal(jCtx, READER, fooWithRoot, READER_NO_BINDINGS);
        unmarshal(jCtx, READER, fooWithRoot, READER_NO_BINDINGS);
    }

    public void testBindingsEmptyJSON() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new InputStreamReader(READER_EMPTY_OXM_JSON.openStream()));
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.FooWithRootElement.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, null, loader);

        org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.FooWithRootElement fooWithRoot = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.FooWithRootElement("a123");
        marshal(jCtx, READER, fooWithRoot, READER_NO_BINDINGS);
        unmarshal(jCtx, READER, fooWithRoot, READER_NO_BINDINGS);
    }

    public void testBindingFormatReader() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new InputStreamReader(READER_OXM_XML.openStream()));
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.Foo.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, properties, loader);
        doTestReader(jCtx);
    }

    public void testBindingFormatSource() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        InputStream is = ClassLoader.getSystemResourceAsStream(SOURCE_OXM_XML);
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, new StreamSource(is));
        Class[] classes = new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.source.Foo.class };
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(classes, properties, loader);
        doTestSource(jCtx);
    }

    public void testBindingFormatList() throws Exception {
        List<Object> inputFiles = new ArrayList<>();
        inputFiles.add(new File(FILE_OXM_XML));
        inputFiles.add(new InputSource(INPUT_SRC_OXM_XML.openStream()));
        inputFiles.add(INPUT_STRM_OXM_XML.openStream());
        inputFiles.add(new InputStreamReader(READER_OXM_XML.openStream()));
        inputFiles.add(new StreamSource(ClassLoader.getSystemResourceAsStream(SOURCE_OXM_XML)));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputFiles);
        Class[] listClasses = new Class[] {
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputstream.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.source.Foo.class};
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(listClasses, properties, loader);
        doTestFile(jCtx);
        doTestInputSrc(jCtx);
        doTestInputStrm(jCtx);
        doTestReader(jCtx);
        doTestSource(jCtx);
    }

    public void testBindingFormatList2() throws Exception {
        List<Object> inputFiles = new ArrayList<>();
        inputFiles.add(new File(FILE_OXM_XML));
        inputFiles.add(new InputSource(INPUT_SRC_OXM_XML.openStream()));
        inputFiles.add(INPUT_STRM_OXM_XML.openStream());
        inputFiles.add(new InputStreamReader(READER_OXM_XML.openStream()));
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        Document n = builderFactory.newDocumentBuilder().parse(ClassLoader.getSystemResourceAsStream(SOURCE_OXM_XML));

        DOMSource ds = new DOMSource(n);
        inputFiles.add(ds);

        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputFiles);
        Class[] listClasses = new Class[] {
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputstream.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.source.Foo.class};
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(listClasses, properties, loader);
        doTestFile(jCtx);
        doTestInputSrc(jCtx);
        doTestInputStrm(jCtx);
        doTestReader(jCtx);
        doTestSource(jCtx);
    }

    public void testBindingFormatXMLInvalid() throws Exception {
        List<Object> inputFiles = new ArrayList<>();
        inputFiles.add(new File(FILE_PATH + "foo-oxm-invalid.xml"));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputFiles);
        Class[] listClasses = new Class[] {
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class};

        try{
            JAXBContextFactory.createContext(listClasses, properties, loader);
        }catch(org.eclipse.persistence.exceptions.JAXBException ex){
            assertEquals(org.eclipse.persistence.exceptions.JAXBException.COULD_NOT_UNMARSHAL_METADATA, ex.getErrorCode());
            return;
        }
        fail("A JAXBException should have occured with error code " + org.eclipse.persistence.exceptions.JAXBException.COULD_NOT_UNMARSHAL_METADATA);
    }

    public void testBindingFormatJSONInvalid() throws Exception {
        List<Object> inputFiles = new ArrayList<>();
        inputFiles.add(new File(FILE_OXM_JSON_INVALID));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputFiles);
        Class[] listClasses = new Class[] {
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class};

        try{
            JAXBContextFactory.createContext(listClasses, properties, loader);
        }catch(org.eclipse.persistence.exceptions.JAXBException ex){
            assertEquals(org.eclipse.persistence.exceptions.JAXBException.COULD_NOT_UNMARSHAL_METADATA, ex.getErrorCode());
            return;
        }
        fail("A JAXBException should have occured with error code " + org.eclipse.persistence.exceptions.JAXBException.COULD_NOT_UNMARSHAL_METADATA);
    }

    public void testBindingFormatJSONInvalid2() throws Exception {
        List<Object> inputFiles = new ArrayList<>();
        inputFiles.add(new File(FILE_PATH + "foo-oxm-invalid2.json"));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputFiles);
        Class[] listClasses = new Class[] {
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class};
        try{
            JAXBContextFactory.createContext(listClasses, properties, loader);
        }catch(org.eclipse.persistence.exceptions.JAXBException ex){
            assertEquals(org.eclipse.persistence.exceptions.JAXBException.COULD_NOT_UNMARSHAL_METADATA, ex.getErrorCode());
            return;
        }
        fail("A JAXBException should have occured with error code " + org.eclipse.persistence.exceptions.JAXBException.COULD_NOT_UNMARSHAL_METADATA);

    }

    //see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=463573
    public void IGNORED_testBindingFormatListJSONWhitespace() throws Exception {
         List<Object> inputFiles = new ArrayList<>();
         inputFiles.add(new File(FILE_PATH + "foo-oxm-whitespace.json"));
         inputFiles.add(new InputSource(INPUT_SRC_OXM_JSON.openStream()));
         inputFiles.add(INPUT_STRM_OXM_JSON.openStream());
         inputFiles.add(new InputStreamReader(READER_OXM_JSON.openStream()));
         inputFiles.add(new StreamSource(ClassLoader.getSystemResourceAsStream(SOURCE_OXM_JSON)));
         Map<String, Object> properties = new HashMap<>();
         properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputFiles);
         Class[] listClasses = new Class[] {
                 org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class,
                 org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class,
                 org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputstream.Foo.class,
                 org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.Foo.class,
                 org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.source.Foo.class};
        try {
            JAXBContextFactory.createContext(listClasses, properties, loader);
        } catch (org.eclipse.persistence.exceptions.JAXBException ex) {
            assertEquals(org.eclipse.persistence.exceptions.JAXBException.COULD_NOT_UNMARSHAL_METADATA, ex.getErrorCode());
            return;
        }
        fail("A JAXBException should have occured with error code " + org.eclipse.persistence.exceptions.JAXBException.COULD_NOT_UNMARSHAL_METADATA);

    }

    public void testBindingFormatListJSON() throws Exception {
        List<Object> inputFiles = new ArrayList<>();
        inputFiles.add(new File(FILE_OXM_JSON));
        inputFiles.add(new InputSource(INPUT_SRC_OXM_JSON.openStream()));
        inputFiles.add(INPUT_STRM_OXM_JSON.openStream());
        inputFiles.add(new InputStreamReader(READER_OXM_JSON.openStream()));
        inputFiles.add(new StreamSource(ClassLoader.getSystemResourceAsStream(SOURCE_OXM_JSON)));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputFiles);
        Class[] listClasses = new Class[] {
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputstream.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.source.Foo.class};
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(listClasses, properties, loader);
        doTestFile(jCtx);
        doTestInputSrc(jCtx);
        doTestInputStrm(jCtx);
        doTestReader(jCtx);
        doTestSource(jCtx);
    }
    public void testBindingFormatListJSON2() throws Exception {
        List<Object> inputFiles = new ArrayList<>();
        inputFiles.add(new File(FILE_OXM_JSON));
        inputFiles.add(new InputSource(INPUT_SRC_OXM_JSON.openStream()));
        inputFiles.add(INPUT_STRM_OXM_JSON.openStream());
        inputFiles.add(new InputStreamReader(READER_OXM_JSON.openStream()));

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        Document n = builderFactory.newDocumentBuilder().parse(ClassLoader.getSystemResourceAsStream(SOURCE_OXM_XML));

        DOMSource ds = new DOMSource(n);
        inputFiles.add(ds);

        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputFiles);
        Class[] listClasses = new Class[] {
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputstream.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.source.Foo.class};
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(listClasses, properties, loader);
        doTestFile(jCtx);
        doTestInputSrc(jCtx);
        doTestInputStrm(jCtx);
        doTestReader(jCtx);
        doTestSource(jCtx);
    }

    public void testBindingFormatListXMLandJSON() throws Exception {
        List<Object> inputFiles = new ArrayList<>();
        inputFiles.add(new File(FILE_OXM_XML));
        inputFiles.add(new InputSource(INPUT_SRC_OXM_JSON.openStream()));
        inputFiles.add(INPUT_STRM_OXM_XML.openStream());
        inputFiles.add(new InputStreamReader(READER_OXM_JSON.openStream()));
        inputFiles.add(new StreamSource(ClassLoader.getSystemResourceAsStream(SOURCE_OXM_XML)));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputFiles);
        Class[] listClasses = new Class[] {
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputstream.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.source.Foo.class};
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(listClasses, properties, loader);
        doTestFile(jCtx);
        doTestInputSrc(jCtx);
        doTestInputStrm(jCtx);
        doTestReader(jCtx);
        doTestSource(jCtx);
    }

    public void testBindingFormatListJSONandXML() throws Exception {
        List<Object> inputFiles = new ArrayList<>();
        inputFiles.add(new File(FILE_OXM_JSON));
        inputFiles.add(new InputSource(INPUT_SRC_OXM_XML.openStream()));
        inputFiles.add(INPUT_STRM_OXM_JSON.openStream());
        inputFiles.add(new InputStreamReader(READER_OXM_XML.openStream()));
        inputFiles.add(new StreamSource(ClassLoader.getSystemResourceAsStream(SOURCE_OXM_JSON)));
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputFiles);
        Class[] listClasses = new Class[] {
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputstream.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.Foo.class,
                org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.source.Foo.class};
        JAXBContext jCtx = (JAXBContext) JAXBContextFactory.createContext(listClasses, properties, loader);
        doTestFile(jCtx);
        doTestInputSrc(jCtx);
        doTestInputStrm(jCtx);
        doTestReader(jCtx);
        doTestSource(jCtx);
    }

    public void testBindingFormatNoPackageSet() {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, Thread.currentThread().getContextClassLoader().getResource((NO_PKG_OXM_XML)).openStream());
            JAXBContextFactory.createContext(new Class[] {}, properties, loader);
        } catch (org.eclipse.persistence.exceptions.JAXBException jaxbe) {
            return;
        } catch (Exception e) {
            fail("The expected exception was not thrown, received " + e.getMessage());
        }
        fail("The expected exception was not thrown.");
    }

    // ------------------- CONVENIENCE METHODS ------------------- //

    private void doTestFile(JAXBContext jCtx) {
        org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo foo = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.file.Foo("a123");
        marshal(jCtx, FILE, foo, FILE_XML);
        unmarshal(jCtx, FILE, foo, FILE_XML);
    }

    private void doTestInputSrc(JAXBContext jCtx) {
        org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo foo = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputsource.Foo("a123");
        marshal(jCtx, INPUT_SRC, foo, INPUT_SRC_XML);
        unmarshal(jCtx, INPUT_SRC, foo, INPUT_SRC_XML);
    }

    private void doTestInputStrm(JAXBContext jCtx) {
        org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputstream.Foo foo = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.inputstream.Foo("a123");
        marshal(jCtx, INPUT_STRM, foo, INPUT_STRM_XML);
        unmarshal(jCtx, INPUT_STRM, foo, INPUT_STRM_XML);
    }

    private void doTestReader(JAXBContext jCtx) {
        org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.Foo foo = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.reader.Foo("a123");
        marshal(jCtx, READER, foo, READER_XML);
        unmarshal(jCtx, READER, foo, READER_XML);
    }

    private void doTestSource(JAXBContext jCtx) {
        org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.source.Foo foo = new org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.bindingformat.source.Foo("a123");
        marshal(jCtx, SOURCE, foo, SOURCE_XML);
        unmarshal(jCtx, SOURCE, foo, SOURCE_XML);
    }

    private void marshal(JAXBContext jCtx, String inputType, Object foo, String instanceDoc) {
        // setup control document
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(instanceDoc);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + instanceDoc + "].");
        }
        // marshal
        try {
            jCtx.createMarshaller().marshal(foo, testDoc);
            //jCtx.createMarshaller().marshal(foo, System.out);
            //System.out.println("\n");
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred during marshal [" + inputType + "]");
        }
        assertTrue("Marshal [" + inputType + "] failed - documents are not equal: ", compareDocuments(ctrlDoc, testDoc));
    }

    private void unmarshal (JAXBContext jCtx, String inputType, Object foo, String instanceDoc) {
        // setup control document
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(instanceDoc);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + instanceDoc + "].");
        }
        // unmarshal
        Object obj = null;
        try {
            obj = jCtx.createUnmarshaller().unmarshal(ctrlDoc);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred during unmarshal [" + inputType + "]");
        }
        assertNotNull("Unmarshal [" + inputType + "] failed - returned object is null", obj);
        assertEquals("Unmarshal [" + inputType + "] failed - objects are not equal", obj, foo);
    }
}
