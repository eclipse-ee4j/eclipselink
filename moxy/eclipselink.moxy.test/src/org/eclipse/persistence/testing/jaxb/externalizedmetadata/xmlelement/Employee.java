/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - June 17/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelement;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee")
public class Employee {
    @XmlElement(name="first-name")
    public String firstName;

    @XmlElement(nillable=true)
    public String lastName;

    @XmlElement(required=true)
    public int id;

    public void setMyInt(int newInt) {}
    public int getMyInt() {
        return 66;
    }

    public Object myUtilDate;

    public List myEmployees;

    public boolean equals(Object o ){
        if(o instanceof Employee){
            Employee emp = (Employee)o;
            if(!firstName.equals(emp.firstName) || !lastName.equals(emp.lastName) || id != emp.id || getMyInt() != emp.getMyInt()){
                return false;
            }
            if(myEmployees == null){
                if(emp.myEmployees != null){
                    return false;
                }
            }else{
                if(myEmployees.size() != emp.myEmployees.size()){
                    return false;
                }
                if(!myEmployees.equals(emp.myEmployees)){
                    return false;
                }

            }
            return true;
        }
        return false;
    }
}
