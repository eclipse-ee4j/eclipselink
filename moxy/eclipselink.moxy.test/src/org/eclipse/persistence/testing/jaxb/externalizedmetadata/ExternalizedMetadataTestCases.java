/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.Employee;

import junit.framework.TestCase;

/**
 * Tests Externalized Metadata functionality.
 *
 */
public class ExternalizedMetadataTestCases extends TestCase {
    protected static String tmpdir = (System.getenv("T_WORK") == null ? "" : (System.getenv("T_WORK") + "/"));
    protected static ClassLoader loader = Thread.currentThread().getContextClassLoader();

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public ExternalizedMetadataTestCases(String name) {
        super(name);
    }
    

    /**
     * Generate the schema(s) for a given set of classes.  Any eclipselink-oxm.xml file(s)
     * found in the package(s) will be applied by default.
     * 
     * @param classes
     * @param expectedSchemaCount
     */
    public MySchemaOutputResolver generateSchema(Class[] classes, int expectedSchemaCount) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(classes, null, outputResolver, classLoader); 
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
        return outputResolver;
    }

    /**
     * Generate the schema using the CONTEXT_PATH.
     *
     * @param contextPath
     * @param expectedSchemaCount
     */
    public MySchemaOutputResolver generateSchema(String contextPath, int expectedSchemaCount) {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(contextPath, outputResolver);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
        return outputResolver;
    }

    /**
     * Generate the schema(s) for a given context path, and apply the eclipselink-oxm.xml
     * file found on the path.  The eclipselink-oxm.xml will be stored in the property map
     * using the contextPath as a key (maps package name to xml metadata file).
     * 
     * @param contextPath
     * @param path
     * @param expectedSchemaCount
     */
    public MySchemaOutputResolver generateSchema(String contextPath, String path, int expectedSchemaCount) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String metadataFile = path + "eclipselink-oxm.xml";
        
        InputStream iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(contextPath, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(new Class[] { Employee.class }, properties, outputResolver, classLoader); 
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
        return outputResolver;
    }

    /**
     * Generate the schema(s) for a given context path, and apply the eclipselink-oxm.xml
     * file(s) in the properties map.
     * 
     * @param contextPath
     * @param properties
     * @param expectedSchemaCount
     */
    public MySchemaOutputResolver generateSchema(String contextPath, Map<String, Map<String, Source>> properties, int expectedSchemaCount) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(contextPath, outputResolver, properties);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
        return outputResolver;
    }

    /**
     * Generate the schema(s) for a given set of classes, and apply the eclipselink-oxm.xml 
     * file found on the path.  The eclipselink-oxm.xml will be stored in the property map 
     * using the contextPath as a key (maps package name to xml metadata file).
     * 
     * @param classes
     * @param contextPath used as key for storing eclipselink-oxm.xml file Source in properties map
     * @param path eclipselink-oxm.xml file will be searched for on this path
     * @param expectedSchemaCount
     */
    public MySchemaOutputResolver generateSchema(Class[] classes, String contextPath, String path, int expectedSchemaCount) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String metadataFile = path + "eclipselink-oxm.xml";
        
        InputStream iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(contextPath, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(classes, properties, outputResolver, classLoader); 
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
        return outputResolver;
    }

    /**
     * Generate one or more schemas from a context path.
     * 
     * @param contextPath
     * @param outputResolver
     */
    protected void generateSchema(String contextPath, MySchemaOutputResolver outputResolver, Map<String, Map<String, Source>> properties) throws Exception {
        JAXBContext jaxbContext;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(contextPath, loader, properties);
            jaxbContext.generateSchema(outputResolver);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate one or more schemas from a context path.
     * 
     * @param contextPath
     * @param outputResolver
     */
    protected void generateSchema(String contextPath, MySchemaOutputResolver outputResolver) throws Exception {
        JAXBContext jaxbContext;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(contextPath, loader);
            jaxbContext.generateSchema(outputResolver);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate one or more schemas from an array of classes and a Map containing zero or more
     * eclipselink-oxm.xml entries.
     * 
     * @param classesToBeBound
     * @param properties
     * @param outputResolver
     * @param classLoader
     */
    protected void generateSchema(Class[] classesToBeBound, java.util.Map properties, MySchemaOutputResolver outputResolver, ClassLoader classLoader) throws Exception {
        JAXBContext jaxbContext;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(classesToBeBound, properties, classLoader);
            jaxbContext.generateSchema(outputResolver);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
        
    /**
     * Validates a given instance doc against the generated schema.
     * 
     * @param src instance document to be validated
     * @param outputResolver contains one or more schemas to validate against
     */
    protected String validateAgainstSchema(String src, MySchemaOutputResolver outputResolver) {
        return validateAgainstSchema(src, 0, outputResolver);
    }
    
    /**
     * Validates a given instance doc against the generated schema.
     * 
     * @param src
     * @param schemaIndex index in output resolver's list of generated schemas
     * @param outputResolver contains one or more schemas to validate against
     */
    protected String validateAgainstSchema(String src, int schemaIndex, MySchemaOutputResolver outputResolver) {
        SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
        Schema theSchema;
        try {
            theSchema = sFact.newSchema(outputResolver.schemaFiles.get(schemaIndex));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src)); 
            validator.validate(ss);
        } catch (Exception e) {
            //e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }
    
    /**
     * SchemaOutputResolver for writing out the generated schema.
     *
     */
    protected class MySchemaOutputResolver extends SchemaOutputResolver {
        // keep a list of processed schemas for the validation phase of the test(s)
        public List<File> schemaFiles;
        
        public MySchemaOutputResolver() {
            schemaFiles = new ArrayList<File>();
        }
        
        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            //return new StreamResult(System.out);
            File schemaFile = new File(tmpdir + suggestedFileName);
            schemaFiles.add(schemaFile);
            return new StreamResult(schemaFile);
        }
    }
}
