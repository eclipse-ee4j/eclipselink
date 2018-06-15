/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.testing.models.jpa21.advanced.xml;

public class EmployeeDetails {
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer responsibilityCount;

    public EmployeeDetails(Integer id, String firstName, String lastName, Integer responsibilityCount) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.responsibilityCount = responsibilityCount;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getResponsibilityCount() {
        return responsibilityCount;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setResponsibilityCount(Integer responsibilityCount) {
        this.responsibilityCount = responsibilityCount;
    }
}
