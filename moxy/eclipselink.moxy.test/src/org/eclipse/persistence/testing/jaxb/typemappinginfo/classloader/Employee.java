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
//     bdoughan - February 5/2010 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo.classloader;

import java.util.ArrayList;
import java.util.List;

public class Employee{

    public Address address;
    public List<PhoneNumber> phoneNumbers;

    public Employee(){
        phoneNumbers = new ArrayList();
    }

    public boolean equals(Object theObject){
        if(theObject instanceof Employee){
            Employee emp = (Employee)theObject;
            if(address != null){
                if(emp.address == null){
                    return false;
                }
                else if(!address.equals(emp.address)){
                    return false;
                }
            }else if(emp.address != null){
                return false;
            }
            return true;
        }else{
           return false;
        }
    }

}
