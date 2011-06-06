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
* dmccann - May 21/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlelementwrapper;

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
import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestCases;

import junit.framework.TestCase;

/**
 * Tests @XmlElementWrapper annotation processing.
 *
 */
public class SchemaGenXmlElementWrapperTestCases extends SchemaGenTestCases {
    static String PATH = "org/eclipse/persistence/testing/jaxb/schemagen/customizedmapping/xmlelementwrapper/";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public SchemaGenXmlElementWrapperTestCases(String name) throws Exception {
        super(name);
    }
    
    public void testElementWrapper() {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(new Class[]{ MyClassThree.class }, outputResolver, null);
        } catch (Exception ex) {
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("More than one shcema was generated unxepectedly", outputResolver.schemaFiles.size() == 1);
        
        String result = validateAgainstSchema(PATH + "root3.xml", outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * If the element wrapper has a namespace that is not ##default and not the target
     * namespace an element reference should generated 
     */
    public void testElementWrapperRef() {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(new Class[]{ MyClassOne.class, MyClassTwo.class }, outputResolver, null);
        } catch (Exception ex) {
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }

        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("More than two schemas were generated unxepectedly", outputResolver.schemaFiles.size() == 2);

        String src = PATH + "root.xml";
        String result = validateAgainstSchema(src, 1, outputResolver);
        if (result != null) {
            // account for map ordering differences between VMs
            if (validateAgainstSchema(src, 0, outputResolver) == null) {
                // success
                return;
            }
            fail(result);
        }
    }
}
