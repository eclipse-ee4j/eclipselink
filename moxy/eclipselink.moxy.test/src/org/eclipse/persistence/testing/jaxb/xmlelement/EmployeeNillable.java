/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee")
public class EmployeeNillable {

    private String string;
    private List<String> strings;
    private EmployeeNillable employee;
    private List<EmployeeNillable> employees;

    public EmployeeNillable() {
        strings = new ArrayList<String>(1);
        employees = new ArrayList<EmployeeNillable>(1);
    }

    @XmlElement(nillable=true)
    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @XmlElement(nillable=true)
    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    @XmlElement(nillable=true)
    public EmployeeNillable getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeNillable employee) {
        this.employee = employee;
    }

    @XmlElement(nillable=true)
    public List<EmployeeNillable> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeNillable> employees) {
        this.employees = employees;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != EmployeeNillable.class) {
            return false;
        }
        EmployeeNillable test = (EmployeeNillable) obj;
        if(!equals(string, test.getString())) {
            return false;
        }
        if(!equals(strings, test.getStrings())) {
            return false;
        }
        if(!equals(employee, test.getEmployee())) {
            return false;
        }
        if(!equals(employees, test.getEmployees())) {
            return false;
        }
        return true;
    }

    private boolean equals(Object control, Object test) {
        if(null == control) {
            return null == test;
        } else {
            return control.equals(test);
        }
    }

    private boolean equals(List control, List test) {
        if(null == control) {
            return null == test;
        }
        if(null == test) {
            return null == control;
        }
        int controlSize = control.size();
        if(controlSize != test.size()) {
            return false;
        }
        for(int x=0; x<controlSize; x++) {
            if(!equals(control.get(x), test.get(x))) {
                return false;
            }
        }
        return true;
    }

}