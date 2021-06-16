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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpaths;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlChoiceObjectMappings via eclipselink-oxm.xml
 *
 */
public class XmlPathsTestCases extends JAXBWithJSONTestCases{
    private static final String INT_VAL = "66";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpaths/employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpaths/employee.json";

    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     * @throws Exception
     */
    public XmlPathsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Employee.class});
    }


    /**
     * Return the control Employee.
     *
     * @return
     */
    public Employee getControlObject() {
        Employee emp = new Employee();
        emp.thing = new Integer(INT_VAL);
        return emp;
    }



    public void testEmployeeSchemaGen() throws Exception {
        // validate the schema
        List controlSchemas = new ArrayList();
        controlSchemas.add(getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/annotations/xmlpaths/employee.xsd"));
        super.testSchemaGen(controlSchemas);
    }


    public void testInstanceDocValidation() {
        InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/annotations/xmlpaths/employee.xsd");
        StreamSource schemaSource = new StreamSource(schema);

        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        String result = validateAgainstSchema(instanceDocStream, schemaSource);
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }

    public void testInvalidXmlPaths() {
        try {
            JAXBContext jaxbCtx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { InvalidEmployee.class }, null);
        } catch (JAXBException e) {
            //e.printStackTrace();
            return;
        }
        fail("The expected exception was not thrown.");
    }
}
