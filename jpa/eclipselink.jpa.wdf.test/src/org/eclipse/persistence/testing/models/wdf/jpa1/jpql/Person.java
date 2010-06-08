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

package org.eclipse.persistence.testing.models.wdf.jpa1.jpql;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "TMP_PERSON")
public class Person {
    @Id
    protected long id;

    @ManyToOne
    @JoinColumn(name = "CITY")
    protected City city;

    @Basic
    @Column(name = "W_STRING")
    protected String string;

    @Basic
    @Column(name = "P_INT")
    protected int _int;

    @Basic
    @Column(name = "W_INT")
    protected Integer integer;

    @Basic
    @Column(name = "P_FLOAT")
    protected float _float;

    @Basic
    @Column(name = "W_FLOAT")
    protected Float _Float;

    @Basic
    @Column(name = "P_BOOL")
    protected boolean _boolean;

    @Basic
    @Column(name = "W_BOOL")
    protected Boolean _Boolean;

    @Basic
    @Column(name = "W_BIGINT")
    protected BigInteger bigInteger;

    @Basic
    @Column(name = "W_BIGDEC")
    protected BigDecimal bigDecimal;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UTIL_DATE")
    protected java.util.Date utilDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UTIL_CALENDAR")
    protected java.util.Calendar calendar;

    @Basic
    @Column(name = "SQL_DATE")
    protected java.sql.Date sqlDate;

    @Basic
    @Column(name = "SQL_TIME")
    protected java.sql.Time sqlTime;

    @Basic
    @Column(name = "SQL_TIMESTAMP")
    protected java.sql.Timestamp sqlTimestamp;

    @Basic
    @Column(name = "PBA_BINARY", length = 8)
    protected byte[] byteArray;

    @Basic
    @Column(name = "WBA_BINARY", length = 8)
    protected Byte[] byteArray_;

    @Basic
    @Column(name = "PCA_VARCHAR")
    protected char[] charArray;

    @Basic
    @Column(name = "WCA_VARCHAR")
    protected Character[] charArray_;

    public boolean is_boolean() {
        return _boolean;
    }

    public void set_boolean(boolean _boolean) {
        this._boolean = _boolean;
    }

    public Boolean get_Boolean() {
        return _Boolean;
    }

    public void set_Boolean(Boolean boolean1) {
        _Boolean = boolean1;
    }

    public float get_float() {
        return _float;
    }

    public void set_float(float _float) {
        this._float = _float;
    }

    public Float get_Float() {
        return _Float;
    }

    public void set_Float(Float float1) {
        _Float = float1;
    }

    public int get_int() {
        return _int;
    }

    public void set_int(int _int) {
        this._int = _int;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public BigInteger getBigInteger() {
        return bigInteger;
    }

    public void setBigInteger(BigInteger bigInteger) {
        this.bigInteger = bigInteger;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public Byte[] getByteArray_() {
        return byteArray_;
    }

    public void setByteArray_(Byte[] byteArray_) {
        this.byteArray_ = byteArray_;
    }

    public java.util.Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(java.util.Calendar calendar) {
        this.calendar = calendar;
    }

    public char[] getCharArray() {
        return charArray;
    }

    public void setCharArray(char[] charArray) {
        this.charArray = charArray;
    }

    public Character[] getCharArray_() {
        return charArray_;
    }

    public void setCharArray_(Character[] charArray_) {
        this.charArray_ = charArray_;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public java.sql.Date getSqlDate() {
        return sqlDate;
    }

    public void setSqlDate(java.sql.Date sqlDate) {
        this.sqlDate = sqlDate;
    }

    public java.sql.Time getSqlTime() {
        return sqlTime;
    }

    public void setSqlTime(java.sql.Time sqlTime) {
        this.sqlTime = sqlTime;
    }

    public java.sql.Timestamp getSqlTimestamp() {
        return sqlTimestamp;
    }

    public void setSqlTimestamp(java.sql.Timestamp sqlTimestamp) {
        this.sqlTimestamp = sqlTimestamp;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public java.util.Date getUtilDate() {
        return utilDate;
    }

    public void setUtilDate(java.util.Date utilDate) {
        this.utilDate = utilDate;
    }
}
