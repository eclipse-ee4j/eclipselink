/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlaccessororder;

import jakarta.xml.bind.annotation.XmlAccessOrder;
import jakarta.xml.bind.annotation.XmlAccessorOrder;
import jakarta.xml.bind.annotation.XmlRootElement;

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
        if(!(obj instanceof Employee emp)) {
            return false;
        }
        return (emp.id == id) && (emp.lastName.equals(lastName)) && (emp.firstName.equals(firstName) && (emp.salary == salary));
    }
}
