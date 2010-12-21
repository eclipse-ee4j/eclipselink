/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - June 12/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.schemagen.deploymentxml.Employee;

import junit.framework.TestCase;

public class SchemaGenTestCases extends TestCase {
    protected static String tmpdir = (System.getenv("T_WORK") == null ? "" : (System.getenv("T_WORK") + "/"));
    protected static ClassLoader loader = Thread.currentThread().getContextClassLoader();

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public SchemaGenTestCases(String name) {
        super(name);
    }
    
    /**
     * Generate one or more schemas from deployment xml.
     * 
     * @param contextPath
     * @param outputResolver
     * @param additionalGlobalElements
     */
    protected void generateSchema(String contextPath, MySchemaOutputResolver outputResolver, Map<QName, Type> additionalGlobalElements) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(contextPath, loader);
            jaxbContext.generateSchema(outputResolver, additionalGlobalElements);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate one or more schemas from an array of Classes.
     * 
     * @param classesToBeBound
     * @param outputResolver
     * @param additionalGlobalElements
     */
    protected void generateSchema(Class[] classesToBeBound, MySchemaOutputResolver outputResolver, Map<QName, Type> additionalGlobalElements) throws Exception {
        JAXBContext jaxbContext;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(classesToBeBound, null, loader);
            jaxbContext.generateSchema(outputResolver, additionalGlobalElements);
        } catch (JAXBException e) {
            throw e;
        }
    }

    /**
     * Generate one or more schemas from an array of Types.
     * 
     * @param typesToBeBound
     * @param outputResolver
     * @param additionalGlobalElements
     */
    protected void generateSchema(Type[] typesToBeBound, MySchemaOutputResolver outputResolver, Map<QName, Type> additionalGlobalElements) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(typesToBeBound, null, loader);
            jaxbContext.generateSchema(outputResolver, additionalGlobalElements);
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
