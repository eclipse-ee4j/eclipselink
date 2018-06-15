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
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;
import org.eclipse.persistence.testing.oxm.mappings.choice.Address;

@XmlRootElement(name="employee")
public class EmployeeWithAddress {

    public AddressNoCtor address;
    public String name;

    public boolean equals(Object obj) {
        EmployeeWithAddress emp = (EmployeeWithAddress)obj;
        if(!name.equals(emp.name)){
            return false;
        }
        if(!address.equals(emp.address)){
            return false;
        }
        return true;
    }
}
