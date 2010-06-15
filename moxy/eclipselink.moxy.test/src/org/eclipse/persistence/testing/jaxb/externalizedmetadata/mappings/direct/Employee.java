/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - January 28/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Employee {
    @XmlAttribute
    @XmlPath("@id")
    public int empId;
    
    public int mgrId;
    public int projectId;
    public String firstName;
    public String lastName;
    public String projectName;
    public String data1;
    public String data2;
    public double salary;
    public String privateData;
    public String characterData; 
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
                salary == empObj.salary &&
                mgrId == empObj.mgrId &&
                characterData.equals(empObj.characterData) &&
                projectId == empObj.projectId &&
                someString.equals(empObj.someString) &&
                projectName.equals(empObj.projectName));
    }
}