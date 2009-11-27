/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.types;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/*
 * This entity class contains all supported <i>basic </i> data types.
 * 
 * The Basic annotation is the simplest type of mapping to a database column. It can optionally be applied to any persistent
 * property or instance variable of the following type: Java primitive types, wrappers of the primitive types, java.lang.String,
 * java.math.BigInteger, java.math.BigDecimal, java.util.Date, java.util.Calendar, java.sql.Date, java.sql.Time,
 * java.sql.Timestamp, byte[], Byte[], char[], Character[], enums, and any other type that implements Serializable.
 */

@Entity
@Table(name = "TMP_TEMPORAL_FA")
public class TemporalTypesFieldAccess {

    @Transient
    private boolean postUpdateCalled;

    protected TemporalTypesFieldAccess() {
    }

    public TemporalTypesFieldAccess(BigInteger anId) {
        id = anId;
    }

    @Id
    @Column(name = "ID", length = 19)
    // issue id 8: was length = 20
    private BigInteger id;

    // (mutable) temporal types
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UTIL_DATE_TS")
    protected Date utilDateAsTimestamp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UTIL_CALENDAR_TS")
    protected Calendar utilCalendarAsTimestamp;

    @Temporal(TemporalType.DATE)
    @Column(name = "UTIL_DATE_D")
    protected Date utilDateAsDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "UTIL_CALENDAR_D")
    protected Calendar utilCalendarAsDate;

    @Basic
    @Temporal(TemporalType.TIME)
    @Column(name = "UTIL_DATE_T")
    protected Date utilDateAsTime;

    @Basic
    // intentionally combined @Basic and @Temporal
    @Temporal(TemporalType.TIME)
    @Column(name = "UTIL_CALENDAR_T")
    protected Calendar utilCalendarAsTime;

    @Basic
    @Column(name = "SQL_DATE")
    protected java.sql.Date sqlDate;

    @Basic
    @Column(name = "SQL_TIME")
    protected Time sqlTime;

    @Basic
    @Column(name = "SQL_TIMESTAMP")
    protected Timestamp sqlTimestamp;

    public void fill() {
        sqlDate = java.sql.Date.valueOf("2005-09-08");
        sqlTime = Time.valueOf("10:49:00");
        sqlTimestamp = new Timestamp(17000);
        utilDateAsTimestamp = new Date(sqlTimestamp.getTime());
        utilDateAsTime = new Date(sqlTime.getTime());
        utilDateAsDate = new Date(sqlDate.getTime());
        utilCalendarAsTimestamp = new GregorianCalendar(2005, 9, 8, 10, 49);
        utilCalendarAsDate = new GregorianCalendar(2005, 9, 8);
        utilCalendarAsTime = new GregorianCalendar(1970, 1, 1, 10, 49);

    }

    public BigInteger getId() {
        return id;
    }

    public java.sql.Date getSqlDate() {
        return sqlDate;
    }

    public void setSqlDate(java.sql.Date sqlDate) {
        this.sqlDate = sqlDate;
    }

    public Time getSqlTime() {
        return sqlTime;
    }

    public void setSqlTime(Time sqlTime) {
        this.sqlTime = sqlTime;
    }

    public Timestamp getSqlTimestamp() {
        return sqlTimestamp;
    }

    public void setSqlTimestamp(Timestamp sqlTimestamp) {
        this.sqlTimestamp = sqlTimestamp;
    }

    public Calendar getUtilCalendarAsDate() {
        return utilCalendarAsDate;
    }

    public void setUtilCalendarAsDate(Calendar utilCalendarAsDate) {
        this.utilCalendarAsDate = utilCalendarAsDate;
    }

    public Calendar getUtilCalendarAsTime() {
        return utilCalendarAsTime;
    }

    public void setUtilCalendarAsTime(Calendar utilCalendarAsTime) {
        this.utilCalendarAsTime = utilCalendarAsTime;
    }

    public Calendar getUtilCalendarAsTimestamp() {
        return utilCalendarAsTimestamp;
    }

    public void setUtilCalendarAsTimestamp(Calendar utilCalendarAsTimestamp) {
        this.utilCalendarAsTimestamp = utilCalendarAsTimestamp;
    }

    public Date getUtilDateAsDate() {
        return utilDateAsDate;
    }

    public void setUtilDateAsDate(Date utilDateAsDate) {
        this.utilDateAsDate = utilDateAsDate;
    }

    public Date getUtilDateAsTime() {
        return utilDateAsTime;
    }

    public void setUtilDateAsTime(Date utilDateAsTime) {
        this.utilDateAsTime = utilDateAsTime;
    }

    public Date getUtilDateAsTimestamp() {
        return utilDateAsTimestamp;
    }

    public void setUtilDateAsTimestamp(Date utilDateAsTimestamp) {
        this.utilDateAsTimestamp = utilDateAsTimestamp;
    }

    @PostUpdate
    public void postUpdate() {
        postUpdateCalled = true;

    }

    public boolean postUpdateWasCalled() {
        return postUpdateCalled;
    }

}
