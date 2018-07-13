/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelement;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementNamespaceTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_namespace.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_namespace.json";
    private final static int CONTROL_ID = 10;

    public XmlElementNamespaceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = EmployeeNamespace.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeNamespace employee = new EmployeeNamespace();
        employee.id = CONTROL_ID;

        return employee;
    }
}
