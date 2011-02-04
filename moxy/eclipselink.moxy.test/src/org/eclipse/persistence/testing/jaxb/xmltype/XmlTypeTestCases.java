/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmltype;

import java.io.File;
import java.io.Writer;
import java.util.List;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.xmltype.builtin.EmploymentPeriod;
import org.eclipse.persistence.testing.jaxb.xmltype.builtin.MyDate;

/**
 *
 */
public class XmlTypeTestCases extends JAXBTestCases {
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmltype/schema.xsd";
    private final static String XSD_RESOURCE_1 = "org/eclipse/persistence/testing/jaxb/xmltype/builtintype.xsd";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmltype/instance.xml";
    private static final String CONTROL_NAME = "John";

    public XmlTypeTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
    }

    protected Object getControlObject() {
        return Employee.buildEmployee();
    }
    
    public void testSchemaGen() throws Exception {
        ExternalizedMetadataTestCases.MySchemaOutputResolver myresolver = new ExternalizedMetadataTestCases.MySchemaOutputResolver();
        getJAXBContext().generateSchema(myresolver);
        ExternalizedMetadataTestCases.compareSchemas(new File(XSD_RESOURCE), myresolver.schemaFiles.get(""));
    }
    
    public void testBuiltInTypeSchemaGen() throws Exception {
        JAXBContext jctx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { EmploymentPeriod.class, MyDate.class}, null);
        MyStreamSchemaOutputResolver resolver = new MyStreamSchemaOutputResolver();
        jctx.generateSchema(resolver);
        List<Writer> schemas = resolver.getSchemaFiles();
        assertTrue("Expected generated schema count to be 1, but was ["+schemas.size()+"].", schemas.size() == 1);
        ExternalizedMetadataTestCases.compareSchemas(schemas.get(0).toString(), new File(XSD_RESOURCE_1));
    }
}
