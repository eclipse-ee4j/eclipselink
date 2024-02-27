/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2022 IBM Corporation. All rights reserved.
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
//     07/01/2014-2.5.3 Rick Curtis
//       - 375101: Date and Calendar should not require @Temporal.
package org.eclipse.persistence.testing.models.jpa.datetime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static jakarta.persistence.GenerationType.TABLE;

@Entity
@Table(name = "CMP3_DATE_TIME")
public class DateTime implements Serializable {

    private Integer id;
    private java.sql.Date date;
    private Instant instant;
    private LocalDate localDate;
    private LocalTime localTime;
    private LocalDateTime localDateTime;
    private OffsetTime offsetTime;
    private OffsetDateTime offsetDateTime;
    private Year year;
    private Time time;
    private Timestamp timestamp;
    private Date utilDate;
    private Calendar calendar;

    private Map<Date, DateTime> uniSelfMap;

    public DateTime() {
        LocalTime localTime = LocalTime.of(0, 0);
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);

        this.date = new java.sql.Date(0);
        this.instant = Instant.ofEpochSecond(0);
        this.localDate = LocalDate.ofEpochDay(0);
        this.localTime = localTime;
        this.localDateTime = localDateTime;
        this.offsetTime = OffsetTime.of(localTime, ZoneOffset.UTC);
        this.offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
        this.year = Year.of(0);
        this.time = new Time(0);
        this.timestamp = new Timestamp(0);
        this.utilDate = new Date(0);
        this.calendar = Calendar.getInstance();

        uniSelfMap = new HashMap<>();
        uniSelfMap.put(new Date(), this);
    }

    @Id
    @GeneratedValue(strategy = TABLE, generator = "DATETIME_TABLE_GENERATOR")
    @TableGenerator(name = "DATETIME_TABLE_GENERATOR", table = "CMP3_DATETIME_SEQ", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT")
    @Column(name = "DT_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "SQL_DATE")
    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    @Column(name = "TIME_INSTANT")
    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    @Column(name = "LOCAL_DATE")
    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    @Column(name = "LOCAL_TIME")
    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    @Column(name = "OFFSET_DATE_TIME")
    public OffsetDateTime getOffsetDateTime() {
        return offsetDateTime;
    }

    public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
        this.offsetDateTime = offsetDateTime;
    }

    @Column(name = "OFFSET_TIME")
    public OffsetTime getOffsetTime() {
        return offsetTime;
    }

    public void setOffsetTime(OffsetTime offsetTime) {
        this.offsetTime = offsetTime;
    }

    @Column(name = "LOCAL_DATE_TIME")
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Column(name = "TIME_YEAR")
    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    @Column(name = "SQL_TIME")
    public Time getTime() {
        return time;
    }

    public void setTime(Time date) {
        this.time = date;
    }

    @Column(name = "SQL_TS")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp date) {
        this.timestamp = date;
    }

    @Column(name = "UTIL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUtilDate() {
        return utilDate;
    }

    public void setUtilDate(Date date) {
        this.utilDate = date;
    }

    @Column(name = "CAL")
    // No @Temporal to test defaulting
    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar date) {
        this.calendar = date;
    }

    @OneToMany(cascade = CascadeType.ALL)
    // No @MapKeyTemporal to test defaulting
    public Map<Date, DateTime> getUniSelfMap() {
        return uniSelfMap;
    }

    public void setUniSelfMap(Map<Date, DateTime> u) {
        uniSelfMap = u;
    }
}
