/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation.dom;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.6
 */
@XmlRootElement
public class Employee {

    @NotNull
    @XmlAttribute
    private Integer id;

    @Min(value=18, groups=Drivers.class)
    @XmlAttribute
    @NotNull
    private int age;

    @NotNull
    @Size(min = 3, max = 15)
    @XmlElement
    private String personalName;

    @Pattern(regexp="\\(\\d{3}\\)\\d{3}-\\d{4}")
    @XmlElement
    private String phoneNumber;

    @NotNull
    @XmlElement
    private Department department;

    @NotNull(groups=Drivers.class)
    @Valid
    @XmlElement
    private DrivingLicense drivingLicense;

    public Employee(){
    }

    public Employee withId(int id){
        this.id = id;
        return this;
    }

    public Employee withAge(int age){
        this.age = age;
        return this;
    }

    public Employee withPersonalName(String personalName){
        this.personalName = personalName;
        return this;
    }

    public Employee withPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Employee withDepartment(Department department){
        this.department = department;
        return this;
    }

    public Employee withDrivingLicense(DrivingLicense drivingLicense){
        this.drivingLicense = drivingLicense;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Employee employee = (Employee) o;

        if (age != employee.age) {
            return false;
        }
        if (department != employee.department) {
            return false;
        }
        if (drivingLicense != null ? !drivingLicense.equals(employee.drivingLicense) : employee.drivingLicense != null) {
            return false;
        }
        if (id != null ? !id.equals(employee.id) : employee.id != null) {
            return false;
        }
        if (personalName != null ? !personalName.equals(employee.personalName) : employee.personalName != null) {
            return false;
        }
        if (phoneNumber != null ? !phoneNumber.equals(employee.phoneNumber) : employee.phoneNumber != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = age;
        result = 31 * result + (id == null ? 0 : id);
        result = 31 * result + (personalName != null ? personalName.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (drivingLicense != null ? drivingLicense.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", age=" + age +
                ", personalName='" + personalName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", department=" + department +
                ", drivingLicense=" + drivingLicense +
                '}';
    }
}
