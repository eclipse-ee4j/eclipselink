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
//     Denise Smith - September 3, 2009 Initial test
package org.eclipse.persistence.testing.jaxb.multiplepackage;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.xmlelement.EmployeeNamespace;

public class MultiplePackageTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/multiplepackage/dept.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/multiplepackage/dept.json";
    private final static int CONTROL_ID = 10;

    public MultiplePackageTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeNamespace.class;
        classes[1] = Department.class;

        jaxbContext = JAXBContextFactory.createContext(classes, null);
        xmlContext =((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext();
        setProject(xmlContext.getSession(0).getProject());
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    }

    protected Object getControlObject() {
        Department dept = new Department();
        EmployeeNamespace employee = new EmployeeNamespace();
        employee.id = CONTROL_ID;
        dept.emp = employee;
        dept.id = 1;
        return dept;
    }

}
