/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlaccessororder;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlRootElement
public class Employee {

    public double salary;
    private int id;
    public String lastName;
    public String firstName;


    public int getId(){
        return id;
    }

    public void setId(int n){
        id = n;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee)obj;
        return (emp.id == id) && (emp.lastName.equals(lastName)) && (emp.firstName.equals(firstName) && (emp.salary == salary));
    }
}
