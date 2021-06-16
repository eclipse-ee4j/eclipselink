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
// dmccann - 1.0M9 - Initial implementation
package org.eclipse.persistence.testing.oxm.events.descriptor;

import java.util.ArrayList;

import org.eclipse.persistence.oxm.platform.XMLPlatform;
import org.eclipse.persistence.testing.oxm.OXTestCase.Platform;
import org.eclipse.persistence.testing.oxm.events.Address;
import org.eclipse.persistence.testing.oxm.events.Employee;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class PostBuildEventTestCases extends XMLMappingTestCases {
    static Integer EMPLOYEE_POST_BUILD = new Integer(0);
    static Integer ADDRESS_POST_BUILD  = new Integer(1);
    EmployeeProject project;

    public PostBuildEventTestCases(String name) throws Exception {
        super(name);
        project = new EmployeeProject();
        setProject(project);
        setControlDocument("org/eclipse/persistence/testing/oxm/events/composite_object.xml");
    }

    public void setUp() throws Exception {
        super.setUp();
        project.setup();
    }

    public void xmlToObjectTest(Object testObject) throws Exception {
        super.xmlToObjectTest(testObject);
        assertTrue("Employee post build event did not occur as expected", project.events.contains(EMPLOYEE_POST_BUILD));
        assertTrue("Address  post build event did not occur as expected", project.events.contains(ADDRESS_POST_BUILD));
    }

    public Object getControlObject() {
        Employee employee = new Employee();
        Address address = new Address();
        address.street = "2201 Riverside Drive";
        employee.address = address;
        if (platform == Platform.SAX) {
            employee.anyCollection = new ArrayList();  // initialize list for equality check
        }
        return employee;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.events.descriptor.PostBuildEventTestCases" });
    }
}
