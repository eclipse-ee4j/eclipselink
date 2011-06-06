/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlIsSetNullPolicy;
import org.eclipse.persistence.oxm.annotations.XmlMarshalNullRepresentation;
import org.eclipse.persistence.oxm.annotations.XmlNullPolicy;
import org.eclipse.persistence.oxm.annotations.XmlParameter;
import org.eclipse.persistence.oxm.annotations.XmlPath;

//@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"empId", "mgrId", "projectId", "firstName", "lastName", "projectName", "data1", "data2", "salary", "privateData", "characterData", "someString", "AString"})
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
    

    String someString;
    
    String aString;

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
    @XmlIsSetNullPolicy(xsiNilRepresentsNull = true, emptyNodeRepresentsNull= false, 
            nullRepresentationForXml=XmlMarshalNullRepresentation.XSI_NIL, 
            isSetMethodName="isSetSomeString",
            isSetParameters={@XmlParameter(value="false", type=Boolean.class)})
    @XmlElement(name="some-string")
    public String getSomeString() { 
        return someString; 
    }
    
    public void setSomeString(String str) {
        isSomeStringSet = true;
        someString = str; 
    }
    
    @XmlTransient
    public boolean isSetSomeString(Boolean ignoredParam) {
        return isSomeStringSet;
    }
    
    @XmlElement(name="a-string")
    @XmlNullPolicy(isSetPerformedForAbsentNode=true, xsiNilRepresentsNull = false, emptyNodeRepresentsNull = false, nullRepresentationForXml = XmlMarshalNullRepresentation.EMPTY_NODE)
    public String getAString() { 
        return aString; 
    }
    
    void setAString(String str) {
        isAStringSet = true;
        aString = str; 
    }
    
    @XmlTransient
    boolean isSetAString() {
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