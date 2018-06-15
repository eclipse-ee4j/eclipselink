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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype.packagelevel.javaclassoverride;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="employee-type")
public class Employee {
    public String firstName;
    public String lastName;
    private int id;

    public Employee(){
    }

    public Employee(int id){
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public boolean getIsSet() {
        return true;
    }

    public boolean equals(Object obj){
        if(obj instanceof Employee){
            Employee empObj = (Employee)obj;
            if(firstName == null){
                if(empObj.firstName !=null){
                    return false;
                }
            }else if(!firstName.equals(empObj.firstName)){
                return false;
            }
            if(lastName == null){
                if(empObj.lastName !=null){
                    return false;
                }
            }else if(!lastName.equals(empObj.lastName)){
                return false;
            }
            if(id != empObj.id){
                return false;
            }
            return true;
        }
        return false;
    }
}
