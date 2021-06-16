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
package org.eclipse.persistence.testing.jaxb.xmlenum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlEnumAttributeCollectionTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_attribute_list.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_attribute_list.json";
    private final static String CONTROL_NAME = "John Doe";

    public XmlEnumAttributeCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[2];
        classes[0] = EmployeeDepartmentAttributeList.class;
        classes[1] = Department.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeDepartmentAttributeList emp = new EmployeeDepartmentAttributeList();
        emp.name = CONTROL_NAME;
        ArrayList deps = new ArrayList();
        deps.add(Department.J2EE);
        deps.add(Department.SUPPORT);
        emp.deps = deps;
        return emp;
    }

    protected Map getProperties(){
        Map<String, String> props = new HashMap<String, String>();
        props.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "@");
        return props;
    }
}
