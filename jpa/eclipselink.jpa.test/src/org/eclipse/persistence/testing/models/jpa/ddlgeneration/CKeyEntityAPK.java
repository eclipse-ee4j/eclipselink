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

package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

/**
 * @author Wonseok Kim
 */
public class CKeyEntityAPK {
    public int seq;
    public String firstName;
    public String lastName;


    public CKeyEntityAPK() {
    }

    public CKeyEntityAPK(int seq, String firstName, String lastName) {
        this.seq = seq;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CKeyEntityAPK that = (CKeyEntityAPK) o;

        return seq == that.seq && firstName.equals(that.firstName) && lastName.equals(that.lastName);
    }

    public int hashCode() {
        int result;
        result = seq;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }
}
