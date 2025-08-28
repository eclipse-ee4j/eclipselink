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
//     Oracle - initial contribution
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Objects;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Cacheable(false)
@Table(name = "DDL_DATE_TIME")
// Query on exact value match.
@NamedQuery(name = "DateTimeEntity.findByLocalDate", query = "SELECT e FROM DateTimeEntity e WHERE e.localDate = :date")
@NamedQuery(name = "DateTimeEntity.findByLocalTime", query = "SELECT e FROM DateTimeEntity e WHERE e.localTimeAttr = :time")
@NamedQuery(name = "DateTimeEntity.findByLocalDateTime", query = "SELECT e FROM DateTimeEntity e WHERE e.localDateTime = :dateTime")
@NamedQuery(name = "DateTimeEntity.findByOffsetTime", query = "SELECT e FROM DateTimeEntity e WHERE e.offsetTime = :time")
@NamedQuery(name = "DateTimeEntity.findByOffsetDateTime", query = "SELECT e FROM DateTimeEntity e WHERE e.offsetDateTime = :dateTime")
public class DateTimeEntity implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    Long id;
    private LocalDate localDate;
    // LOCALTIME is MySQL keyword so using another name for this attribute.
    private LocalTime localTimeAttr;
    private LocalDateTime localDateTime;
    private OffsetTime offsetTime;
    private OffsetDateTime offsetDateTime;

    public DateTimeEntity() {
    }

    public DateTimeEntity(Long id, LocalDate localDate, LocalTime localTime,
                          LocalDateTime localDateTime, OffsetTime offsetTime,
                          OffsetDateTime offsetDateTime) {
        this.id = id;
        this.localDate = localDate;
        this.localTimeAttr = localTime;
        this.localDateTime = localDateTime;
        this.offsetTime = offsetTime;
        this.offsetDateTime = offsetDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalTime getLocalTime() {
        return localTimeAttr;
    }

    public void setLocalTime(java.time.LocalTime localTime) {
        this.localTimeAttr = localTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public OffsetTime getOffsetTime() {
        return offsetTime;
    }

    public void setOffsetTime(OffsetTime offsetTime) {
        this.offsetTime = offsetTime;
    }

    public OffsetDateTime getOffsetDateTime() {
        return offsetDateTime;
    }

    public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
        this.offsetDateTime = offsetDateTime;
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DateTimeEntity that = (DateTimeEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(localDate, that.localDate) &&
                Objects.equals(localTimeAttr, that.localTimeAttr) &&
                Objects.equals(localDateTime, that.localDateTime) &&
                Objects.equals(offsetTime, that.offsetTime) &&
                Objects.equals(offsetDateTime, that.offsetDateTime);
    }
}
