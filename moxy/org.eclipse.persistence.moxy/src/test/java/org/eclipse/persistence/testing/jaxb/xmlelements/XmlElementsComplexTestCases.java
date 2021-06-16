/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementsComplexTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_complex.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_complex.json";
    private final static String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_complex_schema.json";
    private final static int CONTROL_ID = 10;

    public XmlElementsComplexTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = Employee.class;
        classes[1] = Address.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.id = CONTROL_ID;
        Address addr = new Address();
        addr.street = "123 Fake Street";
        addr.city = "Ottawa";
        employee.choice = addr;
        return employee;
    }

    public void testJSONSchemaGen() throws Exception{
        InputStream controlSchema = classLoader.getResourceAsStream(JSON_SCHEMA_RESOURCE);
        super.generateJSONSchema(controlSchema);

    }

}
