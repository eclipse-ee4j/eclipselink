/*
 * Copyright (c) 2026 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.jpa.advanced.holders;

public class EmployeeSalaryDTO {

    private final long salary;
    private final long adjustedSalary;

    /**
     * Constructor with two primitive long parameters.
     * @param salary The employee's base salary
     * @param adjustedSalary The adjusted salary (e.g., from CASE expression)
     */
    public EmployeeSalaryDTO(long salary, long adjustedSalary) {
        this.salary = salary;
        this.adjustedSalary = adjustedSalary;
    }

    public long getSalary() {
        return salary;
    }

    public long getAdjustedSalary() {
        return adjustedSalary;
    }

    @Override
    public String toString() {
        return "EmployeeSalaryDTO{salary=" + salary + ", adjustedSalary=" + adjustedSalary + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeSalaryDTO that = (EmployeeSalaryDTO) o;
        return salary == that.salary && adjustedSalary == that.adjustedSalary;
    }

    @Override
    public int hashCode() {
        return (int)(31 * salary + adjustedSalary);
    }
}