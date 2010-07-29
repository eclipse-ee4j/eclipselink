package org.eclipse.persistence.testing.jaxb.annotations.xmlaccessmethods;

import java.awt.Desktop.Action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
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
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlAccessMethods;

@XmlRootElement(name="employee")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Employee {
    
    @XmlAccessMethods(getMethodName="xGetProperty1", setMethodName="xSetProperty1")
    public String property1;
    
    String property2;
    
    @XmlTransient
    public boolean wasProp1GetCalled = false;
    
    @XmlTransient
    public boolean wasProp2GetCalled = false;
    
    @XmlTransient
    public boolean wasProp1SetCalled = false;
    
    @XmlTransient
    public boolean wasProp2SetCalled = false;
    
    @XmlAccessMethods(getMethodName="xGetProperty2", setMethodName="xSetProperty2")
    public String getProperty2() {
        return property2;
    }
    
    public void setProperty2(String prop2) {
        this.property2 = prop2;
    }
    
    public String xGetProperty1() {
        wasProp1GetCalled = true;
        return property1;
    }
    
    public void xSetProperty1(String value) {
        wasProp1SetCalled = true;
        property1 = value;
    }
    
    public String xGetProperty2() {
        wasProp2GetCalled = true;
        return property2;
    }
    
    public void xSetProperty2(String property) {
        wasProp2SetCalled = true;
        property2 = property;
    }
    
    public boolean equals(Object obj) {
        Employee emp = (Employee)obj;
        if(!(emp.property1.equals(this.property1))) {
            return false;
        }
        if(!(emp.property2.equals(this.property2))) {
            return false;
        }
        return emp.wasProp1GetCalled == this.wasProp1GetCalled &&
            emp.wasProp1SetCalled == this.wasProp1SetCalled &&
            emp.wasProp2GetCalled == this.wasProp2GetCalled &&
            emp.wasProp2SetCalled == this.wasProp2SetCalled;
    }

}
