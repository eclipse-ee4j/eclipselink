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
// mmacivor - October 16th 2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidref.array;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@XmlType(name = "phoneNumber")
public class PhoneNumberArray {

    @XmlID
    public String id;

    public String number;

    @XmlInverseReference(mappedBy = "phones")
    public EmployeeArray emp;

    public boolean equals(Object obj) {
        PhoneNumberArray phone = null;
        try {
            phone = (PhoneNumberArray)obj;
        } catch(ClassCastException ex) {
            return false;
        }
        return id.equals(phone.id) && number.equals(phone.number) && (emp == phone.emp || (emp != null && phone.emp != null));
    }
}
