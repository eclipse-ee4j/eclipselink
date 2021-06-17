/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.jaxb.xmlelementwrapper;

import java.util.Arrays;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Department
{
    @XmlElement(namespace = "http://www.somenamespace.org/")
    public String name;

    @XmlElementWrapper(name = "employees", namespace = "http://www.somenamespace.org/")
    @XmlElement(name = "employee",  namespace = "http://www.somenamespace.org/")
    public Employee[] employees;

    /**
     * Assumes the contents of the array is relevant, but not ordering.
     *
     */
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Department)) {
            return false;
        }
        Department dept = (Department) obj;

        if (dept.employees == null && employees == null) {
            return true;
        }

        if (dept.employees == null && employees != null) {
            return false;
        }
        if (employees == null && dept.employees != null) {
            return false;
        }
        if (employees.length != dept.employees.length) {
            return false;
        }
        boolean foundMatch;
        for (int i = 0; i < employees.length; i++) {
            foundMatch = false;
            for (int j = 0; j < dept.employees.length; j++) {
                if (dept.employees[j].equals(employees[i])) {
                    foundMatch = true;
                }
            }
            if (!foundMatch) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Department [employees=" + Arrays.toString(employees) + ", name=" + name + "]";
    }
}

