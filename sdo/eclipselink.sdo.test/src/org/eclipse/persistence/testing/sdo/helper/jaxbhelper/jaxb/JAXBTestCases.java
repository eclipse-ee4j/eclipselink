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
*     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.jaxb;

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
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XSDHelper;

public class JAXBTestCases extends SDOTestCase {

    private static final String XML_SCHEMA = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/jaxb/JAXB.xsd";

    private JAXBHelperContext jaxbHelperContext;

    public JAXBTestCases(String name) {
        super(name);
    }

    public void setUp() {
        try {
            Class[] classes = new Class[3];
            classes[0] = Root.class;
            classes[1] = Child1.class;
            classes[2] = Child2.class;
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            jaxbHelperContext = new JAXBHelperContext(jaxbContext);

            /*
            JAXBSchemaOutputResolver sor = new JAXBSchemaOutputResolver();
            jaxbContext.generateSchema(sor);
            String xmlSchema = sor.getSchema();
            System.out.println(xmlSchema);
            jaxbHelperContext.getXSDHelper().define(xmlSchema);
            */
            InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA);
            jaxbHelperContext.getXSDHelper().define(xsd, null);            
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testCreateGlobalComplexType() {
       DataObject rootDO = jaxbHelperContext.getDataFactory().create("urn:jaxb", "root");
       assertNotNull(rootDO);

       Root root = (Root) jaxbHelperContext.unwrap(rootDO);
       assertNotNull(root);
       assertEquals(Root.class, root.getClass());
    }

    public void testCreateGlobalComplexElement() {
        DataObject child2DO = jaxbHelperContext.getDataFactory().create("urn:jaxb", "child2");
        assertNotNull(child2DO);

        Child2 child2 = (Child2) jaxbHelperContext.unwrap(child2DO);
        assertNotNull(child2);
        assertEquals(Child2.class, child2.getClass());
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
