/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
// mmacivor - October 16th 2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidref.array;

import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlType;

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
