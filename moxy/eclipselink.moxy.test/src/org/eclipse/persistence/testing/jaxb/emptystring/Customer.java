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
//     bdoughan - July 6/2010 - Initial implementation
package org.eclipse.persistence.testing.jaxb.emptystring;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Customer {

    private String id;
    private String name;

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != Customer.class) {
            return false;
        }
        Customer testCustomer = (Customer) obj;
        return equals(id, testCustomer.getId()) && equals(name, testCustomer.getName());
    }

    private boolean equals(String control, String test) {
        if(null == control) {
            return null == test;
        } else {
            return control.equals(test);
        }
    }

}
