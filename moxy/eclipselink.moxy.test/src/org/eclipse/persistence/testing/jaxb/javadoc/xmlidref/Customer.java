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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlidref;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="customer")
public class Customer {

    @XmlAttribute(name="customerID")
    public String id;

    @XmlElement(name="name")
    public String name;

    @XmlID
    public String getCustomerID(){
        return id;
    }
    public void setCustomerID(String id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Customer)) {
            return false;
        }
        Customer customer = (Customer) obj;

        return this.id.equals(customer.id) && this.name.equals(customer.name);
    }
}
