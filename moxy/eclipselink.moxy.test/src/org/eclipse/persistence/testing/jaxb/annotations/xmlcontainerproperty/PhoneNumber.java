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
// mmacivor - January 09, 2009 - 1.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlcontainerproperty;

import javax.xml.bind.annotation.*;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

public class PhoneNumber {

    @XmlValue
    public String number;

    @XmlInverseReference(mappedBy="phoneNumbers")
    public Employee owningEmployee;

    public boolean equals(Object o) {
        if(!(o instanceof PhoneNumber)) {
            return false;
        }

        PhoneNumber obj = (PhoneNumber)o;
        return number.equals(obj.number) && owningEmployee.id == obj.owningEmployee.id;

    }
}
