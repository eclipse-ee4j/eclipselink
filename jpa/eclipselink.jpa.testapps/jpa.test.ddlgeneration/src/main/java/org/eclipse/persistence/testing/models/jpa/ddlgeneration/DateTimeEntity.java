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

package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DDL_DATE_TIME_ENTITY")
public class DateTimeEntity {

    @Id private Long id;

    @Column(name = "LOC_DT", secondPrecision = 5)
    private LocalDateTime localDateTime;

    @Column(name = "LOC_T", secondPrecision = 4)
    private LocalTime localTime;

    @Column(name = "OFF_DT", secondPrecision = 5)
    private OffsetDateTime offsetDateTime;

    @Column(name = "OFF_T", secondPrecision = 4)
    private OffsetTime offsetTime;

    public DateTimeEntity() {
        this(null, null, null, null, null);
    }

    public DateTimeEntity(Long id,
                          LocalDateTime timestamp,
                          LocalTime localTime,
                          OffsetDateTime offsetDateTime,
                          OffsetTime offsetTime) {
        this.id = id;
        this.localDateTime = timestamp;
        this.localTime = localTime;
        this.offsetDateTime = offsetDateTime;
        this.offsetTime = offsetTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public OffsetDateTime getOffsetDateTime() {
        return offsetDateTime;
    }

    public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
        this.offsetDateTime = offsetDateTime;
    }

    public OffsetTime getOffsetTime() {
        return offsetTime;
    }

    public void setOffsetTime(OffsetTime offsetTime) {
        this.offsetTime = offsetTime;
    }

}
