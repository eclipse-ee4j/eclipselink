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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder.packagelevel.javaclassoverride;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlRootElement(name="employee-type")
public class Employee {
    public String g;
    public String b;
    public String a;

    public boolean equals(Object obj){
        if(obj instanceof Employee){
            Employee empObj = (Employee)obj;
            if(!a.equals(empObj.a)){
                return false;
            }
            if(!b.equals(empObj.b)){
                return false;
            }
            if(!g.equals(empObj.g)){
                return false;
            }
            return true;
        }
        return false;
    }
}
