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

import org.eclipse.persistence.indirection.*;

/**
 * <p><b>Purpose</b>: Describes an Employee's phone number.
 * <p><b>Description</b>: Used in a 1:M relationship from an employee. Since many people have various numbers
 * they can be contacted at the type describes where the phone number could reach the Employee.
 */
public class PhoneNumber extends org.eclipse.persistence.testing.models.performance.PhoneNumber {

    /** Owner maintains the 1:1 mapping to an Employee (required for 1:M relationship in Employee) */
    public ValueHolderInterface ownerHolder;

    public PhoneNumber() {
        this("home", "###", "#######");
    }

    public PhoneNumber(String type, String theAreaCode, String theNumber) {
        super(type, theAreaCode, theNumber);
        this.ownerHolder = new ValueHolder();
    }

    public org.eclipse.persistence.testing.models.performance.Employee getOwner() {
        return (Employee)ownerHolder.getValue();
    }

    public void setOwner(org.eclipse.persistence.testing.models.performance.Employee owner) {
        this.ownerHolder.setValue(owner);
    }
}
