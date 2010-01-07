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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.schemagen.imports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.address.Address;
import org.w3c.dom.Document;

public class SchemaGenImportTestCases extends TestCase {
    public SchemaGenImportTestCases(String name) throws Exception {
        super(name);
    }

    public void testSchemaGenerationWithImport() throws Exception {
        boolean exception = false;
        String msg = null;
        String src = "org/eclipse/persistence/testing/jaxb/schemagen/imports/imports.xml";
        String tmpdir = System.getenv("T_WORK");

        String sourceFile = "org/eclipse/persistence/testing/jaxb/schemagen/imports/someExistingSchema.xsd";
    	String targetFile = tmpdir + "/someExistingSchema.xsd";

    	File copiedFile = null;
        try {
        	copiedFile = copyFile(sourceFile, targetFile);
            Class[] jClasses = new Class[] { Address.class, Employee.class};
            Generator gen = new Generator(new JavaModelInputImpl(jClasses, new JavaModelImpl(Thread.currentThread().getContextClassLoader())));
            gen.generateSchemaFiles(tmpdir, null);
            SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
            Schema theSchema = sFact.newSchema(new File(tmpdir + "/schema0.xsd"));           
            
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src)); 
            validator.validate(ss);            
        } catch (Exception ex) {
        	ex.printStackTrace();
            exception = true;
            msg = ex.toString();
        } finally{
        	if(copiedFile != null){
        		copiedFile.delete();
        	}
        }
        assertTrue("Schema validation failed unexpectedly: " + msg, exception==false);
                        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        builderFactory.setNamespaceAware(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
            
        InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/imports/schema0.xsd");
        Document control = parser.parse(stream);
            
        stream = new FileInputStream(new File(tmpdir + "/schema0.xsd"));
        Document test = parser.parse(stream);
            
        JAXBXMLComparer xmlComparer = new JAXBXMLComparer();
        
        assertTrue("schema0.xsd did not match control document", xmlComparer.isSchemaEqual(control, test));
        	
    }
    
    private File copyFile(String sourceFile, String targetFile) throws Exception{
        File inputFile = new File(sourceFile);
    	File outputFile = new File(targetFile);

    	FileReader in = new FileReader(inputFile);
    	FileWriter out = new FileWriter(outputFile);
    	int c;

    	while ((c = in.read()) != -1){
    	    out.write(c);
        }

    	in.close();
    	out.close();
    	return outputFile;
    }
}
