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
package org.eclipse.persistence.testing.oxm.mappings.keybased;

import java.util.Collection;

public class Root {
    public Employee employee;
    public Collection addresses;

    /**
     * For the purpose of Key-based mapping tests, equality
     * will be performed on the Root's Employee - more specifically,
     * the address(es) attribute will be compared to ensure that the
     * correct target Address(es) was returned based on the key(s).
     *
     * @param obj a Root containing an Employee whose Address(es) will
     * be checked to verify correctness.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Root)) {
            return false;
        }

        Root tgtRoot = (Root) obj;
        return tgtRoot.employee.equals(this.employee);
    }

    public String toString() {
        String employeeString = "";

        if (employee != null) {
            employeeString = employee.toString();
        }
        return "Root: employee="+employeeString;
    }
}
