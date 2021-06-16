/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// dmccann - June 17/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessortype;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="employee-type")
public class Employee {
    public String firstName;
    public String lastName;
    private int id;

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
