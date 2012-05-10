/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa21.advanced;

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
