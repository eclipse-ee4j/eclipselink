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
* dmccann - April 30/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.deploymentxml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.schemagen.employee.Address;
import org.eclipse.persistence.testing.jaxb.schemagen.employee.Department;
import org.eclipse.persistence.testing.jaxb.schemagen.employee.Employee;
import org.eclipse.persistence.testing.jaxb.schemagen.employee.PhoneNumber;

import junit.framework.TestCase;

public class DeploymentXMLSchemaGenTestCases extends TestCase {
    static String tmpdir;
    
    public DeploymentXMLSchemaGenTestCases(String name) throws Exception {
        super(name);
        tmpdir = (System.getenv("T_WORK") == null ? "" : (System.getenv("T_WORK") + "/"));
    }
    
    public void testSchemaGenFromProjectXml() throws Exception {
        String msg = "";
        boolean exception = false;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/deploymentxml/Employee.xml";

    	try {
        	MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
            JAXBContext jaxbContext = JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.schemagen.deploymentxml", null);
            jaxbContext.generateSchema(outputResolver);
            
            assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
            assertTrue("More than one schema was generated unxepectedly", outputResolver.schemaFiles.size() == 1);
            
            SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
            Schema theSchema = sFact.newSchema(outputResolver.schemaFiles.get(0));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src)); 
            validator.validate(ss);
        } catch (Exception ex) {
            exception = true;
            msg = ex.toString();
        }
        assertFalse("Schema validation failed unexpectedly: " + msg, exception);
    }

    class MySchemaOutputResolver extends SchemaOutputResolver {
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
