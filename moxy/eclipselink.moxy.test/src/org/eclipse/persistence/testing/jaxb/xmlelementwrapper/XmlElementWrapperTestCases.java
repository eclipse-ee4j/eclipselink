/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Vikram Bhatia - December 24/2013 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementwrapper;

import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlElementWrapper
 *
 */
public class XmlElementWrapperTestCases extends JAXBWithJSONTestCases{

    private static final String PATH = "org/eclipse/persistence/testing/jaxb/xmlelementwrapper/";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementwrapper/department.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementwrapper/department.json";

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     * @throws Exception
     */
    public XmlElementWrapperTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Department.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public void testSchemaGen() throws Exception {
        List controlSchemas = new ArrayList();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlelementwrapper/schema.xsd");
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }

    @Override
    protected Object getControlObject() {
        // setup control objects
        Department dept = new Department();
        dept.name = "HR";

        Employee emp1 = new Employee();
        emp1.id = 1;
        emp1.name = "Bob";
        Employee emp2 = new Employee();
        emp2.id = 2;
        emp2.name = "Fred";

        dept.employees = new Employee[] {emp1, emp2};
        return dept;
    }
}
