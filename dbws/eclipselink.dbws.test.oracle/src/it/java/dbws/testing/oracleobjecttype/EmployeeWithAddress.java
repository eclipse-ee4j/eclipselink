/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - May 2008, created DBWS test package

package dbws.testing.oracleobjecttype;

public class EmployeeWithAddress {

    public EmployeeWithAddress() {
    }

    public Integer id;
    public String firstName;
    public String lastName;
    public Address address;

    @Override
    public String toString() {
        String sb = "[" +
                id +
                "] " +
                firstName +
                " " +
                lastName +
                " " +
                address;
        return sb;
    }
}
