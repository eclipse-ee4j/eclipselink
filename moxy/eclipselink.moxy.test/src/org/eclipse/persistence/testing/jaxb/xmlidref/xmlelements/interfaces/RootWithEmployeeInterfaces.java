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
//     Denise Smith - February 19, 2013
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.interfaces;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class RootWithEmployeeInterfaces {
     @XmlElement(name="employee")
        public EmployeeWithElementsInterfaces employee;

        @XmlElement(name="address")
        public Collection<AddressInterfaces> addresses;

        @XmlElement(name="phone-number")
        public Collection<PhoneNumberInterfaces> phoneNumbers;

        /**
         * For the purpose of ID/IDREF  tests, equality will be performed
         * on the Root's Employee - more specifically, the address(es)
         * attribute will be compared to ensure that the correct target
         * Address(es) was returned based on the key(s).
         *
         * @param obj a Root containing an Employee whose Address(es) will
         * be checked to verify correctness.
         */
        public boolean equals(Object obj) {
            if (!(obj instanceof RootWithEmployeeInterfaces)) {
                return false;
            }

            RootWithEmployeeInterfaces tgtRoot = (RootWithEmployeeInterfaces) obj;
            return tgtRoot.employee.equals(this.employee);
        }
}
