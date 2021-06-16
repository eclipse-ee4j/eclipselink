/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// dmccann - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmltype;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.xmltype.builtin.EmploymentPeriod;
import org.eclipse.persistence.testing.jaxb.xmltype.builtin.MyDate;

/**
 *
 */
public class XmlTypeTestCases extends JAXBWithJSONTestCases {
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmltype/schema.xsd";
    private final static String XSD_RESOURCE_1 = "org/eclipse/persistence/testing/jaxb/xmltype/builtintype.xsd";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmltype/instance.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmltype/instance.json";

    public XmlTypeTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        return Employee.buildEmployee();
    }

    public void testSchemaGen() throws Exception {
        List controlSchemas = new ArrayList();
        controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE));
        super.testSchemaGen(controlSchemas);
    }

    public void testBuiltInTypeSchemaGen() throws Exception {

        JAXBContext jctx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { EmploymentPeriod.class, MyDate.class}, null);
        MyStreamSchemaOutputResolver outputResolver = new MyStreamSchemaOutputResolver();
        jctx.generateSchema(outputResolver);

        List<Writer> generatedSchemas = outputResolver.getSchemaFiles();
        List controlSchemas = new ArrayList();
        controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE_1));

        compareSchemas(controlSchemas, generatedSchemas);
     }
}
