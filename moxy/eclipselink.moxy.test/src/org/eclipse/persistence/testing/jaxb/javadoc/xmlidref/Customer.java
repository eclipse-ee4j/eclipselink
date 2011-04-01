/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
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
