/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     03/09/2022: Tomas Kraus
//       - Issue 1442: Implement New Jakarta Persistence 3.1 Features
package org.eclipse.persistence.jpa.test.query.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;

/**
 * JPA Entity used in {@code LocalTime}/{@code LocalDate}/{@code LocalDateTime} tests.
 */
@Entity
@NamedQuery(name = "DateTimeQueryEntity.findByLocalDateTime", query = "SELECT e FROM DateTimeQueryEntity e WHERE e.datetime = :dateTime")
public class DateTimeQueryEntity {

    @Id
    private Integer id;

    private LocalTime time;

    private LocalDate date;

    private LocalDateTime datetime;

    public DateTimeQueryEntity() {
    }

    public DateTimeQueryEntity(final Integer id, final LocalTime time, final LocalDate date, final LocalDateTime datetime) {
        this.setId(id);
        this.setTime(time);
        this.setDate(date);
        this.setDatetime(datetime);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

}
