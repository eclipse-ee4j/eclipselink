/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

public class PhoneNumber {
    public String number;

    public Employee owningEmployee;

    public Employee getOwningEmployee() {
        return owningEmployee;
    }

    public void setOwningEmployee(Employee employee) {
        this.owningEmployee = employee;
    }

    public boolean equals(Object o) {
        if(!(o instanceof PhoneNumber)) {
            return false;
        }

        PhoneNumber obj = (PhoneNumber)o;
        return number.equals(obj.number) && owningEmployee.id == obj.owningEmployee.id;

    }
}
