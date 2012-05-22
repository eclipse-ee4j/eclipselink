/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - August 6/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.choice.converter;

import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.choice.Address;
import org.eclipse.persistence.testing.oxm.mappings.choice.Employee;

public class ConverterTestCases extends XMLWithJSONMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choice/ChoiceComplexValue.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choice/ChoiceComplexValue.json";

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
        employee.choice = addressWrapper;

        employee.phone = "123-4567"; 

        return employee;
    }

    public Project getNewProject(Project originalProject, ClassLoader classLoader) {
        Project project = new EmployeeProject();
        project.setDatasourceLogin(new XMLLogin());
        project.getDatasourceLogin().setPlatform(new SAXPlatform());

        return project;
    }

}