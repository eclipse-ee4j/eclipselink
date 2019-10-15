/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.performance.toplink;

import org.eclipse.persistence.testing.models.performance.Address;
import org.eclipse.persistence.indirection.*;

/**
 * <p><b>Purpose</b>: Represent a employee of an organization.
 * <p><b>Description</b>: An Employee is a root object in the Employee Demo.
 * It maintains relationships to all of the other objects in the system.
 */
public class Employee extends org.eclipse.persistence.testing.models.performance.Employee {

    public ValueHolderInterface addressHolder;

    public ValueHolderInterface managerHolder;

    public Employee() {
        super();
        addressHolder = new ValueHolder();
        managerHolder = new ValueHolder();
    }

    public Address getAddress() {
        return (Address)addressHolder.getValue();
    }

    public org.eclipse.persistence.testing.models.performance.Employee getManager() {
        return (Employee)managerHolder.getValue();
    }

    public void setAddress(Address address) {
        this.addressHolder.setValue(address);
    }

    protected ValueHolderInterface getAddressHolder() {
        return addressHolder;
    }

    protected void setAddressHolder(ValueHolderInterface addressHolder) {
        this.addressHolder = addressHolder;
    }

    protected ValueHolderInterface getManagerHolder() {
        return managerHolder;
    }

    protected void setManagerHolder(ValueHolderInterface managerHolder) {
        this.managerHolder = managerHolder;
    }

    public void setManager(org.eclipse.persistence.testing.models.performance.Employee manager) {
        this.managerHolder.setValue(manager);
    }
}
