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
package org.eclipse.persistence.testing.oxm.mappings.sequenced;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.sequenced.Setting;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class SimpleAnyTestCases extends XMLMappingTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/sequenced/SimpleAny.xml";
    private static final String CONTROL_XML_ROOT_NAME = "STRING";
    private static final String CONTROL_XML_ROOT_VALUE = "Hello World";

    private static final EmployeeProject EMPLOYEE_PROJECT = new EmployeeProject();

    public SimpleAnyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(EMPLOYEE_PROJECT);
    }

    public Object getControlObject() {
        Employee controlEmployee = new Employee();

        Setting anySetting = new Setting();
        DatabaseMapping anyMapping = EMPLOYEE_PROJECT.getDescriptor(Employee.class).getMappingForAttributeName("any");
        anySetting.setMapping(anyMapping);
        anySetting.setObject(controlEmployee);
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(CONTROL_XML_ROOT_NAME);
        xmlRoot.setObject(CONTROL_XML_ROOT_VALUE);
        anySetting.setValue(xmlRoot);
        controlEmployee.getSettings().add(anySetting);

        return controlEmployee;
    }

    public void xmlToObjectTest(Object testObject) throws Exception {
        Employee testEmployee = (Employee) testObject;
        assertEquals(1, testEmployee.getSettings().size());
        super.xmlToObjectTest(testObject);
    }

}
