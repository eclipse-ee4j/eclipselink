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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.interfaces;

import javax.xml.bind.annotation.XmlID;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

public class PhoneNumberInterfaces implements ContactInfo{

    @XmlID
    public String id;

    public String number;

    @XmlInverseReference(mappedBy = "addressOrPhone")
    public EmployeeWithElementsInterfaces emp;

    public boolean equals(Object obj) {
        PhoneNumberInterfaces phone = null;
        try {
            phone = (PhoneNumberInterfaces)obj;
        } catch(ClassCastException ex) {
            return false;
        }
        return id.equals(phone.id) && number.equals(phone.number) && (emp == phone.emp || (emp != null && phone.emp != null));
    }
}
