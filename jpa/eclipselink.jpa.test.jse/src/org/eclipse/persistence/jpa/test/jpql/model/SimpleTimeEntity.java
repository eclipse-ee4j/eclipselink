/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     09/10/2019 - 2.7 Will Dazey
//       - 550951: Parameter binding for java.time types results in SQL syntax exception
package org.eclipse.persistence.jpa.test.jpql.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SimpleTimeEntity {

    @Id
    private int id;

    private Integer simpleInteger;
    private String simpleString;

    private java.time.LocalDate simpleLocalDate;
    private java.time.LocalTime simpleLocalTime;
    private java.time.LocalDateTime simpleLocalDateTime;
    private java.time.OffsetTime simpleOffsetTime;
    private java.time.OffsetDateTime simpleOffsetDateTime;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Integer getSimpleInteger() {
        return simpleInteger;
    }
    public void setSimpleInteger(Integer simpleInteger) {
        this.simpleInteger = simpleInteger;
    }

    public String getSimpleString() {
        return simpleString;
    }
    public void setSimpleString(String simpleString) {
        this.simpleString = simpleString;
    }

    public java.time.LocalDate getSimpleLocalDate() {
        return simpleLocalDate;
    }
    public void setSimpleLocalDate(java.time.LocalDate simpleLocalDate) {
        this.simpleLocalDate = simpleLocalDate;
    }

    public java.time.LocalTime getSimpleLocalTime() {
        return simpleLocalTime;
    }
    public void setSimpleLocalTime(java.time.LocalTime simpleLocalTime) {
        this.simpleLocalTime = simpleLocalTime;
    }

    public java.time.LocalDateTime getSimpleLocalDateTime() {
        return simpleLocalDateTime;
    }
    public void setSimpleLocalDateTime(java.time.LocalDateTime simpleLocalDateTime) {
        this.simpleLocalDateTime = simpleLocalDateTime;
    }

    public java.time.OffsetTime getSimpleOffsetTime() {
        return simpleOffsetTime;
    }
    public void setSimpleOffsetTime(java.time.OffsetTime simpleOffsetTime) {
        this.simpleOffsetTime = simpleOffsetTime;
    }

    public java.time.OffsetDateTime getSimpleOffsetDateTime() {
        return simpleOffsetDateTime;
    }
    public void setSimpleOffsetDateTime(java.time.OffsetDateTime simpleOffsetDateTime) {
        this.simpleOffsetDateTime = simpleOffsetDateTime;
    }
}
