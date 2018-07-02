/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - June 17/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlattribute;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee")
public class Employee {
    @XmlAttribute(name="firstname")
    public Object firstName;

    @XmlAttribute(name="lastname")
    public String lastName;

    @XmlAttribute(required=false)
    public int id;

    public Object things;

    public boolean equals(Object obj) {
        Employee eObj;
        try {
            eObj = (Employee) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        if(!(firstName.equals(eObj.firstName))){
            return false;
        }
        if(!(lastName.equals(eObj.lastName))){
            return false;
        }
        if(id != eObj.id){
            return false;
        }
        if(things instanceof List){
            if(!(eObj.things instanceof List)){
                return false;
            }
            if(((List)things).size() != ((List)eObj.things).size() ){
                return false;
            }
            for(int i=0;i<((List)things).size(); i++){
                Object next = ((List)things).get(i);
                Object nextCompare =((List)eObj.things).get(i);
                if(!(next.equals(nextCompare))){
                    return false;
                }
            }
        }else{
            if(!(things.equals(eObj.things))){
                return false;
            }
        }

        return true;
    }
}
