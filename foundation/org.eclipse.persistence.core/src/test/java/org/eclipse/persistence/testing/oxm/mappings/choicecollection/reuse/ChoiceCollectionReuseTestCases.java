/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2009-10-06 13:06:00 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choicecollection.reuse;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

import java.net.URL;
import java.util.LinkedList;

public class ChoiceCollectionReuseTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choicecollection/ChoiceCollectionMixed.xml";

    public ChoiceCollectionReuseTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new EmployeeProject());
    }

    @Override
    public Object getReadControlObject() {
        Employee employee = new Employee();
        employee.name = "Jane Doe";

        employee.choice = new LinkedList<>();
        employee.choice.add("123 Fake Street");
        employee.choice.add(12);
        Address addr = new Address();
        addr.city = "Ottawa";
        addr.street = "45 O'Connor";
        employee.choice.add(addr);
        employee.choice.add(14);

        employee.choice.add("addressString");

        employee.phone = "123-4567";

        return employee;
    }

    @Override
    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.name = "Jane Doe";

        employee.choice = new LinkedList<>();
        employee.choice.add("123 Fake Street");
        employee.choice.add(12);
        Address addr = new Address();
        addr.city = "Ottawa";
        addr.street = "45 O'Connor";
        employee.choice.add(addr);
        employee.choice.add(14);

        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName("simpleAddress");
        xmlRoot.setObject("addressString");
        employee.choice.add(xmlRoot);

        employee.phone = "123-4567";

        return employee;
    }

    @Override
    public Project getNewProject(Project originalProject, ClassLoader classLoader) {
        Project project = super.getNewProject(originalProject, classLoader);
        project.getDatasourceLogin().setPlatform(new SAXPlatform());

        return project;
    }

    public void testContainerReused() throws Exception {
        URL url = ClassLoader.getSystemResource(resourceName);
        Employee testObject = (Employee) xmlUnmarshaller.unmarshal(url);

        assertEquals("This mapping's container was not reused.", LinkedList.class, testObject.choice.getClass());
    }

}
