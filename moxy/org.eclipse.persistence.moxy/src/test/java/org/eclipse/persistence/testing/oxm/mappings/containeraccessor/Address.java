/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

public class Address {
    public String street;
    public String city;
    public String state;
    public String country;

    public Employee owningEmployee;

    public Employee getOwningEmployee() {
        return owningEmployee;
    }

    public void setOwningEmployee(Employee employee) {
        owningEmployee = employee;
    }

    public boolean equals(Object o) {
        if(!(o instanceof Address)) {
            return false;
        }
        Address obj = (Address)o;
        boolean equal = true;

        equal = equal && street.equals(obj.street);
        equal = equal && city.equals(obj.city);
        equal = equal && state.equals(obj.state);
        equal = equal && country.equals(obj.country);

        equal = equal && owningEmployee.id == obj.owningEmployee.id;

        return equal;
    }
}
