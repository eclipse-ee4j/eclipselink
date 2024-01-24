/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.datafactory;

import java.io.IOException;
import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;

public class DataFactoryTestCases extends SDOTestCase {

    private JAXBHelperContext jaxbHelperContext;

    public DataFactoryTestCases(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        try {
            Class<?>[] classes = new Class<?>[1];
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

    @Override
    public void tearDown() {
    }

    private class JAXBSchemaOutputResolver extends SchemaOutputResolver {

        private StringWriter schemaWriter;

        public String getSchema() {
            return schemaWriter.toString();
        }

        @Override
        public Result createOutput(String arg0, String arg1) throws IOException {
            schemaWriter = new StringWriter();
            return new StreamResult(schemaWriter);
        }

    }

}
