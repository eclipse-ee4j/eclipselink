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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.group.nestedGroup;

public class Employee {
    EmploymentInfo _G1;

    public Employee() {
        _G1 = new EmploymentInfo();
    }

    public EmploymentInfo getG1() {
        return _G1;
    }

    public void setG1(EmploymentInfo value) {
        _G1 = value;
    }
}
