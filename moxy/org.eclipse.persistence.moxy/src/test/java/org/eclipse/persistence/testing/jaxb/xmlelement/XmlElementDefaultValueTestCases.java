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
//     rbarkhouse - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementDefaultValueTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_defaultvalue.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_defaultvalue.json";

    private final static String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_defaultvalue_w.xml";
    private final static String JSON_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_defaultvalue_w.json";

    public XmlElementDefaultValueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_RESOURCE_WRITE);
        setWriteControlJSON(JSON_RESOURCE_WRITE);
        Class[] classes = new Class[1];
        classes[0] = EmployeeDefaultValue.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeDefaultValue employee = new EmployeeDefaultValue();
        employee.name = EmployeeDefaultValue.DEFAULT_NAME;

        employee.ints = new ArrayList<Integer>();
        employee.ints.add(123);
        employee.ints.add(123);
        employee.ints.add(10);
        employee.ints.add(null);
        return employee;
    }

}
