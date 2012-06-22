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
*     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.datafactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.helper.SDODataFactory;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

public class DataFactoryTestCases extends SDOTestCase {

    private JAXBHelperContext jaxbHelperContext;

    public DataFactoryTestCases(String name) {
        super(name);
    }

    public void setUp() {
        try {
            Class[] classes = new Class[1];
            classes[0] = Root.class;
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            jaxbHelperContext = new JAXBHelperContext(jaxbContext);
            JAXBSchemaOutputResolver jsor = new JAXBSchemaOutputResolver();
            jaxbContext.generateSchema(jsor);
            String xsd = jsor.getSchema();
            jaxbHelperContext.getXSDHelper().define(xsd);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testCreatePojoType() {
        DataObject rootDO = jaxbHelperContext.getDataFactory().create("urn:datafactory", "root");
        assertNotNull(rootDO);
        
        Root root = (Root) jaxbHelperContext.unwrap(rootDO);
        assertNotNull(root);
    }

    public void testCreateNonPojoType() {
        DataObject typeDO = jaxbHelperContext.getDataFactory().create("commonj.sdo", "Type");
        assertNotNull(typeDO);
        
        Object object = jaxbHelperContext.unwrap(typeDO);
        assertNull(object);
    }

    public void testInvalidType() {
        boolean fail = true;
        try {
            jaxbHelperContext.getDataFactory().create("INVALID_URI", "INVALID_NAME");
        } catch(IllegalArgumentException e) {
            fail = false;
        }
        if(fail) {
            fail("An IllegalArgumentException should have been thrown.");
        }
    }

    public void tearDown() {
    }

    private class JAXBSchemaOutputResolver extends SchemaOutputResolver {

        private StringWriter schemaWriter;
        
        public String getSchema() {
            return schemaWriter.toString();
        }

        public Result createOutput(String arg0, String arg1) throws IOException {
            schemaWriter = new StringWriter();
            return new StreamResult(schemaWriter);
        }

    }

}
