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
//     bdoughan - August 7/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choicecollection.converter;

import java.util.Vector;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.choicecollection.Address;
import org.eclipse.persistence.testing.oxm.mappings.choicecollection.Employee;

public class ConverterTestCases extends XMLWithJSONMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choicecollection/ChoiceCollectionComplexValue.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choicecollection/ChoiceCollectionComplexValue.json";

    public ConverterTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setProject(new EmployeeProject());
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.name = "Jane Doe";

        Address addr = new Address();
        addr.street = "45 O'Connor";
        addr.city = "Ottawa";
        Wrapper addressWrapper = new Wrapper();
        addressWrapper.setValue(addr);
        employee.choice = new Vector<Object>();
        employee.choice.add(addressWrapper);

        employee.phone = "123-4567";

        return employee;
    }

    public Project getNewProject(Project originalProject, ClassLoader classLoader) {
        Project project = super.getNewProject(originalProject, classLoader);
        //project.setDatasourceLogin(new XMLLogin());
        //project.getDatasourceLogin().setPlatform(new SAXPlatform());

        return project;
    }

}
