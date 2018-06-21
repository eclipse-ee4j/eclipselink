/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

import java.util.ArrayList;

public class Employee {
    public int id;
    public String firstName;
    public String lastName;
    public Address address;
    public ArrayList<PhoneNumber> phoneNumbers;

    public boolean equals(Object e) {
        if(!(e instanceof Employee)) {
            return false;
        }
        Employee obj = (Employee)e;
        boolean equal = this.id == obj.id;
        equal = equal && this.firstName.equals(obj.firstName);
        equal = equal && this.lastName.equals(obj.lastName);
        equal = equal && this.address.equals(obj.address);
        if(this.phoneNumbers.size() != obj.phoneNumbers.size()) {
            return false;
        }
        for(int i = 0; i < phoneNumbers.size(); i++) {
            PhoneNumber num1 = phoneNumbers.get(i);
            PhoneNumber num2 = obj.phoneNumbers.get(i);
            equal = equal && num1.equals(num2);
        }

        return equal;
    }
}
