/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - January 28/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import org.eclipse.persistence.oxm.annotations.XmlAccessMethods;
import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Employee {
    public int empId;
    public int mgrId;
    public int projectId;
    public String firstName;
    public String lastName;
    public String projectName;
    public String data1;
    public String data2;
    public Double salary;
    public String privateData;
    public String characterData;
    @XmlAccessMethods(getMethodName="someFakeMethod", setMethodName="anotherFakeMethod")
    public String someString;
    public String aString;

    @javax.xml.bind.annotation.XmlTransient
    public boolean isSomeStringSet;
    @javax.xml.bind.annotation.XmlTransient
    public boolean isAStringSet;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    String getProject() {
        wasGetCalled = true;
        return projectName;
    }

    void setProject(String name) {
        wasSetCalled = true;
        projectName = name;
    }

    String getSomeString() {
        return someString;
    }

    void setSomeString(String str) {
        isSomeStringSet = true;
        someString = str;
    }

    public boolean isSetSomeString(Boolean ignoredParam) {
        return isSomeStringSet;
    }

    String getAString() {
        return aString;
    }

    void setAString(String str) {
        isAStringSet = true;
        aString = str;
    }

    public boolean isSetAString() {
        return isAStringSet;
    }

    public boolean equals(Object obj) {
        Employee empObj;
        try {
            empObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }

        if (someString == null && empObj.someString != null) {
            return false;
        }

        return (empId == empObj.empId &&
                firstName.equals(empObj.firstName) &&
                lastName.equals(empObj.lastName) &&
                data1.equals(empObj.data1) &&
                data2.equals(empObj.data2) &&
                salary.equals(empObj.salary) &&
                mgrId == empObj.mgrId &&
                characterData.equals(empObj.characterData) &&
                projectId == empObj.projectId &&
                someString.equals(empObj.someString) &&
                projectName.equals(empObj.projectName));
    }
}
