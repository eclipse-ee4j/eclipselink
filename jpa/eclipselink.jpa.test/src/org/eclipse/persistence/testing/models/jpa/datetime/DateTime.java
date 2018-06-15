/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     07/01/2014-2.5.3 Rick Curtis
//       - 375101: Date and Calendar should not require @Temporal.
package org.eclipse.persistence.testing.models.jpa.datetime;

import static javax.persistence.GenerationType.TABLE;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="CMP3_DATE_TIME")
public class DateTime implements Serializable {
    private Integer id;
    private java.sql.Date date;
    private Time time;
    private Timestamp timestamp;
    private Date utilDate;
    private Calendar calendar;

    private Map<Date, DateTime> uniSelfMap;

    public DateTime() {
    }

    public DateTime(java.sql.Date date, Time time, Timestamp timestamp, Date utilDate, Calendar calendar) {
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.utilDate = utilDate;
        this.calendar = calendar;

        uniSelfMap = new HashMap<Date, DateTime>();
        uniSelfMap.put(new Date(), this);
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="DATETIME_TABLE_GENERATOR")
    @TableGenerator(
        name="DATETIME_TABLE_GENERATOR",
        table="CMP3_DATETIME_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT"
    )
    @Column(name="DT_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="SQL_DATE")
    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    @Column(name="SQL_TIME")
    public Time getTime() {
        return time;
    }

    public void setTime(Time date) {
        this.time = date;
    }

    @Column(name="SQL_TS")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp date) {
        this.timestamp = date;
    }

    @Column(name="UTIL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUtilDate() {
        return utilDate;
    }

    public void setUtilDate(Date date) {
        this.utilDate = date;
    }

    @Column(name="CAL")
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
