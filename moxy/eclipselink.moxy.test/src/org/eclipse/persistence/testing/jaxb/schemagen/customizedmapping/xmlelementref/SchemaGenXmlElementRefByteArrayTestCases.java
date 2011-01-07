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
 * mmacivor - June 10/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlelementref;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLConstants;

import junit.framework.TestCase;

public class SchemaGenXmlElementRefByteArrayTestCases  extends TestCase {
    static String tmpdir;
    MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
    boolean shouldGenerateSchema = true;

    public SchemaGenXmlElementRefByteArrayTestCases(String name) throws Exception {
        super(name);
        tmpdir = (System.getenv("T_WORK") == null ? "" : (System.getenv("T_WORK") + "/"));
    }

    /**
     * Generate the schema for these tests once only.  If generation fails, it will do so
     * for each test (meaning all tests will result in a generation failure).  If generation
     * is successful it is not performed again.
     */
    private void generateSchema() {
        if (shouldGenerateSchema) {
            outputResolver = new MySchemaOutputResolver();
            try {
                Class[] classes = new Class[]{ WrappedByteArray.class }; 
                JAXBContext context = (org.eclipse.persistence.jaxb.JAXBContext) org.eclipse.persistence.jaxb.JAXBContextFactory.createContext(classes, null);
                context.generateSchema(outputResolver);
            } catch (Exception ex) {
                fail("Schema generation failed unexpectedly: " + ex.toString());
            }
            assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
            assertTrue("More than one shcema was generated unxepectedly", outputResolver.schemaFiles.size() == 1);
            shouldGenerateSchema = false;
        }
    }
    
    public void testGenerateSchema() {
        generateSchema();

        try {
            SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
            Schema theSchema = sFact.newSchema(outputResolver.schemaFiles.get(0));
            Validator validator = theSchema.newValidator();
            String src = "org/eclipse/persistence/testing/jaxb/schemagen/customizedmapping/xmlelementref/bytearray.xml";
            StreamSource ss = new StreamSource(new File(src)); 
            validator.validate(ss);
        } catch (Exception ex) {
            fail("Schema validation failed unexpectedly: " + ex.toString());
        }
        
    }
    class MySchemaOutputResolver extends SchemaOutputResolver {
        // keep a list of processed schemas for the validation phase of the test(s)
        public List<File> schemaFiles;
        
        public MySchemaOutputResolver() {
            schemaFiles = new ArrayList<File>();
        }
        
        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            File schemaFile = new File(tmpdir + suggestedFileName);
            schemaFiles.add(schemaFile);
            return new StreamResult(schemaFile);
        }
    }
}