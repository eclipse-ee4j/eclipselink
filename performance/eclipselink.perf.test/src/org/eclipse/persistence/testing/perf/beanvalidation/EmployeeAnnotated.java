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
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.perf.beanvalidation;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Employee that will undergo bean validation callbacks from both JPA and MOXy and will trigger bean validation.
 * Designed to minimise performance effects of bean validation provider on our performance tests. Contains only the
 * simplest custom annotation, which is always valid.
 */
@Entity(name = "EmployeeAnnotated")
@XmlRootElement
public class EmployeeAnnotated implements Serializable {

    @Id
    @CustomAnnotation
    @XmlAttribute
    private Integer id;

    @XmlAttribute
    private int age;

    @XmlElement
    private String personalName;

    @XmlElement
    private String phoneNumber;

    public EmployeeAnnotated(){
    }

    public EmployeeAnnotated withId(int id){
        this.id = id;
        return this;
    }

    public EmployeeAnnotated withAge(int age){
        this.age = age;
        return this;
    }

    public EmployeeAnnotated withPersonalName(String personalName){
        this.personalName = personalName;
        return this;
    }

    public EmployeeAnnotated withPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
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

        EmployeeAnnotated employee = (EmployeeAnnotated) o;

        if (age != employee.age) {
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
        return result;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", age=" + age +
                ", personalName='" + personalName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
