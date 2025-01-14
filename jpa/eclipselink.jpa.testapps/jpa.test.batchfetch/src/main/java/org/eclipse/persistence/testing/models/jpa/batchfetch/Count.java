/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa.batchfetch;

import java.util.Objects;

public final class Count {
    private final long value;
    private final Employee employee;

    public Count(long value, Employee employee) {
        this.value = value;
        this.employee = employee;
    }

    public long value() {return value;}

    public Employee employee() {return employee;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Count count = (Count) o;
        return value == count.value && Objects.equals(employee, count.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, employee);
    }

    @Override
    public String toString() {
        return "Count[" +
               "value=" + value + ", " +
               "employee=" + employee + ']';
    }
}
