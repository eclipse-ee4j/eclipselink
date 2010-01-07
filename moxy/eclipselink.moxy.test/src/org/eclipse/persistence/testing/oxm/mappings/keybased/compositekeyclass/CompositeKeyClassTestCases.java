/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Oct 29/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.keybased.compositekeyclass;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeKeyClassTestCases extends XMLMappingTestCases {

    public CompositeKeyClassTestCases(String name) throws Exception {
        super(name);
    }

    @Override
    protected Object getControlObject() {
        Department department = new Department();

        Employee employee1 = new Employee();
        EmployeeID id1 = new EmployeeID();
        id1.setId1(1);
        id1.setId2("A");
        employee1.setId(id1);
        department.getEmployees().add(employee1);

        Employee employee2 = new Employee();
        EmployeeID id2 = new EmployeeID();
        id2.setId1(2);
        id2.setId2("B");
        employee2.setId(id2);
        employee2.setManager(employee1);
        employee1.getTeamMembers().add(employee2);
        department.getEmployees().add(employee2);

        Employee employee3 = new Employee();
        EmployeeID id3 = new EmployeeID();
        id3.setId1(3);
        id3.setId2("C");
        employee3.setId(id3);
        employee3.setManager(employee1);
        employee1.getTeamMembers().add(employee3);
        department.getEmployees().add(employee3);

        return department;
    }

}