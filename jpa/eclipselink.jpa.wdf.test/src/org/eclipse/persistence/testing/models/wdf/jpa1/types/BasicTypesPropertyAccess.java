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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Mutable;

/*
 * This entity class contains all supported <i>basic </i> data types.
 *
 * The Basic annotation is the simplest type of mapping to a database column. It can optionally be applied to any persistent
 * property or instance variable of the following type: Java primitive types, wrappers of the primitive types, java.lang.String,
 * java.math.BigInteger, java.math.BigDecimal, java.util.Date, java.util.Calendar, java.sql.Date, java.sql.Time,
 * java.sql.Timestamp, byte[], Byte[], char[], Character[], enums, and any other type that implements Serializable.
 */

@Entity
@Table(name = "TMP_BASIC_TYPES_PA")
public class BasicTypesPropertyAccess {

    private boolean postUpdateCalled;

    public BasicTypesPropertyAccess() {
    }

    public BasicTypesPropertyAccess(int anId) {
        _id = anId;
        _primitiveChar = 'a';
    }

    private int _id;

    // Java primitive types
    private boolean _primitiveBoolean; // TODO ?

    private byte _primititveByte; // SMALLINT

    private char _primitiveChar; // INTEGER

    private short _primitiveShort; // SMALLINT

    private int _primitiveInt; // INTEGER

    private long _primitiveLong; // BIGINT

    private float _primitiveFloat; // REAL

    private double _primitiveDouble; // DOUBLE

    // wrappers of primitive types
    private Boolean _wrapperBoolean; // TODO ?

    private Byte _wrapperByte; // SMALLINT

    private Character _wrapperCharacter; // INTEGER

    private Short _wrapperShort; // SMALLINT

    private Integer _wrapperInteger; // INTEGER

    private Long _wrapperLong; // BIGINT

    private Float _wrapperFloat; // REAL

    private Double _wrapperDouble; // DOUBLE

    // immutable types
    private String _string2Varchar; // VARCHAR

    private String _string2Clob; // CLOB

    private BigDecimal _bigDecimal; // DECIMAL

    private BigInteger _bigInteger; // DECIMAL

    // mutable types
    private Date _utilDate; // DATE

    private Calendar _utilCalendar; // TIMESTAMP

    private java.sql.Date _sqlDate; // DATE

    private Time sqlTime; // TIME

    private Timestamp _sqlTimestamp; // TIMESTAMP

    // arrays
    private byte[] _primitiveByteArray2Binary; // BINARY

    private byte[] _primitiveByteArray2Longvarbinary; // LONGVARBINARY

    private byte[] _primitiveByteArray2Blob; // BLOB

    private Byte[] _wrapperByteArray2Binary; // BINARY

    private Byte[] _wrapperByteArray2Longvarbinary; // LONGVARBINARY

    private Byte[] _wrapperByteArray2Blob; // BLOB

    private char[] _primitiveCharArray2Varchar; // VARCHAR

    private char[] _primitiveCharArray2Clob; // CLOB

    private Character[] _wrapperCharacterArray2Varchar; // VARCHAR

    private Character[] _wrapperCharacterArray2Clob; // CLOB

    // user-defined serializable
    private Serializable _serializable; // BLOB

    // enums
    private UserDefinedEnum _enumString;

    private UserDefinedEnum _enumOrdinal;

    public transient String notPersistentByModifier;

    public int notPersistentByAnnotation;

    public void fill() {
        _primitiveBoolean = true;
        _primititveByte = 1;
        _primitiveChar = 'A';
        _primitiveShort = 2;
        _primitiveInt = 3;
        _primitiveLong = 4;
        _primitiveFloat = 1.5f;
        _primitiveDouble = 2.5;
        _wrapperBoolean = Boolean.TRUE;
        _wrapperByte = new Byte((byte) 1);
        _wrapperCharacter = new Character('A');
        _wrapperShort = new Short((short) 2);
        _wrapperInteger = new Integer(3);
        _wrapperLong = new Long(4);
        _wrapperFloat = new Float(1.5f);
        _wrapperDouble = new Double(2.5);
        _string2Varchar = "VARCHAR";
        _string2Clob = "CLOB";
        _bigDecimal = new BigDecimal("42.42");
        _bigInteger = new BigInteger("77");
        _utilDate = new Date(17000);
        _utilCalendar = new GregorianCalendar(2005, 9, 8, 10, 49);
        _sqlDate = java.sql.Date.valueOf("2005-09-08");
        sqlTime = Time.valueOf("10:49:00");
        _sqlTimestamp = new Timestamp(17000);
        _primitiveByteArray2Binary = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };

        _primitiveByteArray2Longvarbinary = new byte[1111];
        for (int i = 0; i < _primitiveByteArray2Longvarbinary.length; i++) {
            _primitiveByteArray2Longvarbinary[i] = (byte) i;
        }
        _primitiveByteArray2Blob = new byte[3333];
        for (int i = 0; i < _primitiveByteArray2Blob.length; i++) {
            _primitiveByteArray2Blob[i] = (byte) i;
        }

        _wrapperByteArray2Binary = new Byte[] { new Byte((byte) 0), new Byte((byte) 1), new Byte((byte) 2), new Byte((byte) 3),
                new Byte((byte) 4), new Byte((byte) 5), new Byte((byte) 6), new Byte((byte) 7) };

        _wrapperByteArray2Longvarbinary = new Byte[1111];
        for (int i = 0; i < _wrapperByteArray2Longvarbinary.length; i++) {
            _wrapperByteArray2Longvarbinary[i] = new Byte((byte) i);
        }

        _wrapperByteArray2Blob = new Byte[3333];
        for (int i = 0; i < _wrapperByteArray2Blob.length; i++) {
            _wrapperByteArray2Blob[i] = new Byte((byte) i);
        }

        _primitiveCharArray2Varchar = new char[] { 'V', 'A', 'R', 'C', 'A', 'R' };
        _primitiveCharArray2Clob = new char[] { 'C', 'L', 'O', 'B' };

        _wrapperCharacterArray2Varchar = new Character[] { new Character('V'), new Character('A'), new Character('R'),
                new Character('C'), new Character('H'), new Character('A'), new Character('R') };
        _wrapperCharacterArray2Clob = new Character[] { new Character('C'), new Character('L'), new Character('O'),
                new Character('B') };
        _serializable = new UserDefinedSerializable("REGEN"); // BLOB

        _enumOrdinal = UserDefinedEnum.EMIL;
        _enumString = UserDefinedEnum.HUGO;
    }

    /**
     * @return Returns the bigDecimal.
     */
    @Basic
    @Column(name = "BIG_DECIMAL", precision = 12, scale = 2)
    public BigDecimal getBigDecimal() {
        return _bigDecimal;
    }

    /**
     * @param bigDecimal
     *            The bigDecimal to set.
     */
    public void setBigDecimal(BigDecimal bigDecimal) {
        this._bigDecimal = bigDecimal;
    }

    /**
     * @return Returns the bigInteger.
     */
    @Basic
    @Column(name = "BIG_INTEGER", precision = 12)
    public BigInteger getBigInteger() {
        return _bigInteger;
    }

    /**
     * @param bigInteger
     *            The bigInteger to set.
     */
    public void setBigInteger(BigInteger bigInteger) {
        this._bigInteger = bigInteger;
    }

    /**
     * @return Returns the id.
     */
    @Id
    public int getId() {
        return _id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id) {
        this._id = id;
    }

    /**
     * @return Returns the primititveByte.
     */
    @Basic
    @Column(name = "P_BYTE")
    public byte getPrimititveByte() {
        return _primititveByte;
    }

    /**
     * @param primititveByte
     *            The primititveByte to set.
     */
    public void setPrimititveByte(byte primititveByte) {
        this._primititveByte = primititveByte;
    }

    /**
     * @return Returns the primitiveBoolean.
     */
    @Basic
    @Column(name = "P_BOOLEAN")
    public boolean getPrimitiveBoolean() {
        return _primitiveBoolean;
    }

    /**
     * @param primitiveBoolean
     *            The primitiveBoolean to set.
     */
    public void setPrimitiveBoolean(boolean primitiveBoolean) {
        this._primitiveBoolean = primitiveBoolean;
    }

    /**
     * @return Returns the primitiveByteArray2Binary.
     */
    @Basic
    @Column(name = "PBA_BINARY", length = 8)
    @Mutable
    public byte[] getPrimitiveByteArray2Binary() {
        return _primitiveByteArray2Binary;
    }

    /**
     * @param primitiveByteArray2Binary
     *            The primitiveByteArray2Binary to set.
     */
    public void setPrimitiveByteArray2Binary(byte[] primitiveByteArray2Binary) {
        this._primitiveByteArray2Binary = primitiveByteArray2Binary;
    }

    /**
     * @return Returns the primitiveByteArray2Blob.
     */
    @Basic
    @Column(name = "PBA_BLOB")
    @Lob
    @Mutable
    public byte[] getPrimitiveByteArray2Blob() {
        return _primitiveByteArray2Blob;
    }

    /**
     * @param primitiveByteArray2Blob
     *            The primitiveByteArray2Blob to set.
     */
    public void setPrimitiveByteArray2Blob(byte[] primitiveByteArray2Blob) {
        this._primitiveByteArray2Blob = primitiveByteArray2Blob;
    }

    /**
     * @return Returns the primitiveByteArray2Longvarbinary.
     */
    @Basic
    @Column(name = "PBA_LONGVARBINARY", length = 1500)
    @Mutable
    public byte[] getPrimitiveByteArray2Longvarbinary() {
        return _primitiveByteArray2Longvarbinary;
    }

    /**
     * @param primitiveByteArray2Longvarbinary
     *            The primitiveByteArray2Longvarbinary to set.
     */
    public void setPrimitiveByteArray2Longvarbinary(byte[] primitiveByteArray2Longvarbinary) {
        this._primitiveByteArray2Longvarbinary = primitiveByteArray2Longvarbinary;
    }

    /**
     * @return Returns the primitiveChar.
     */
    @Basic
    @Column(name = "P_CHAR")
    public char getPrimitiveChar() {
        return _primitiveChar;
    }

    /**
     * @param primitiveChar
     *            The primitiveChar to set.
     */
    public void setPrimitiveChar(char primitiveChar) {
        this._primitiveChar = primitiveChar;
    }

    /**
     * @return Returns the primitiveCharArray2Clob.
     */
    @Basic
    @Column(name = "PCA_CLOB")
    @Lob
    @Mutable
    public char[] getPrimitiveCharArray2Clob() {
        return _primitiveCharArray2Clob;
    }

    /**
     * @param primitiveCharArray2Clob
     *            The primitiveCharArray2Clob to set.
     */
    public void setPrimitiveCharArray2Clob(char[] primitiveCharArray2Clob) {
        this._primitiveCharArray2Clob = primitiveCharArray2Clob;
    }

    /**
     * @return Returns the primitiveCharArray2Varchar.
     */
    @Basic
    @Column(name = "PCA_VARCHAR")
    @Mutable
    public char[] getPrimitiveCharArray2Varchar() {
        return _primitiveCharArray2Varchar;
    }

    /**
     * @param primitiveCharArray2Varchar
     *            The primitiveCharArray2Varchar to set.
     */
    public void setPrimitiveCharArray2Varchar(char[] primitiveCharArray2Varchar) {
        this._primitiveCharArray2Varchar = primitiveCharArray2Varchar;
    }

    /**
     * @return Returns the primitiveFloat.
     */
    @Basic
    @Column(name = "P_FLOAT")
    public float getPrimitiveFloat() {
        return _primitiveFloat;
    }

    /**
     * @param primitiveFloat
     *            The primitiveFloat to set.
     */
    public void setPrimitiveFloat(float primitiveFloat) {
        this._primitiveFloat = primitiveFloat;
    }

    /**
     * @return Returns the primitiveInt.
     */
    @Basic
    @Column(name = "P_INT")
    public int getPrimitiveInt() {
        return _primitiveInt;
    }

    /**
     * @param primitiveInt
     *            The primitiveInt to set.
     */
    public void setPrimitiveInt(int primitiveInt) {
        this._primitiveInt = primitiveInt;
    }

    /**
     * @return Returns the primitiveLong.
     */
    @Basic
    @Column(name = "P_LONG")
    public long getPrimitiveLong() {
        return _primitiveLong;
    }

    /**
     * @param primitiveLong
     *            The primitiveLong to set.
     */
    public void setPrimitiveLong(long primitiveLong) {
        this._primitiveLong = primitiveLong;
    }

    /**
     * @return Returns the primitiveShort.
     */
    @Basic
    @Column(name = "P_SHORT")
    public short getPrimitiveShort() {
        return _primitiveShort;
    }

    /**
     * @param primitiveShort
     *            The primitiveShort to set.
     */
    public void setPrimitiveShort(short primitiveShort) {
        this._primitiveShort = primitiveShort;
    }

    /**
     * @return Returns the prmitiveDouble.
     */
    @Basic
    @Column(name = "P_DOUBLE")
    public double getPrimitiveDouble() {
        return _primitiveDouble;
    }

    /**
     * @param prmitiveDouble
     *            The prmitiveDouble to set.
     */
    public void setPrimitiveDouble(double prmitiveDouble) {
        this._primitiveDouble = prmitiveDouble;
    }

    /**
     * @return Returns the serializable.
     */
    @Basic
    @Column(name = "SERIALIZABLE")
    @Lob
    public Serializable getSerializable() {
        return _serializable;
    }

    /**
     * @param serializable
     *            The serializable to set.
     */
    public void setSerializable(Serializable serializable) {
        this._serializable = serializable;
    }

    /**
     * @return Returns the sqlDate.
     */
    @Basic
    @Column(name = "SQL_DATE")
    public java.sql.Date getSqlDate() {
        return _sqlDate;
    }

    /**
     * @param sqlDate
     *            The sqlDate to set.
     */
    public void setSqlDate(java.sql.Date sqlDate) {
        this._sqlDate = sqlDate;
    }

    /**
     * @return Returns the sqlTime.
     */
    @Basic
    @Column(name = "SQL_TIME")
    public Time getSqlTime() {
        return sqlTime;
    }

    /**
     * @param sqlTime
     *            The sqlTime to set.
     */
    public void setSqlTime(Time sqlTime) {
        this.sqlTime = sqlTime;
    }

    /**
     * @return Returns the sqlTimestamp.
     */
    @Basic
    @Column(name = "SQL_TIMESTAMP")
    public Timestamp getSqlTimestamp() {
        return _sqlTimestamp;
    }

    /**
     * @param sqlTimestamp
     *            The sqlTimestamp to set.
     */
    public void setSqlTimestamp(Timestamp sqlTimestamp) {
        this._sqlTimestamp = sqlTimestamp;
    }

    /**
     * @return Returns the string2Clob.
     */
    @Basic
    @Column(name = "STRING_CLOB")
    @Lob
    public String getString2Clob() {
        return _string2Clob;
    }

    /**
     * @param string2Clob
     *            The string2Clob to set.
     */
    public void setString2Clob(String string2Clob) {
        this._string2Clob = string2Clob;
    }

    /**
     * @return Returns the string2Varchar.
     */
    @Basic
    @Column(name = "STRING_VC", length = 128)
    public String getString2Varchar() {
        return _string2Varchar;
    }

    /**
     * @param string2Varchar
     *            The string2Varchar to set.
     */
    public void setString2Varchar(String string2Varchar) {
        this._string2Varchar = string2Varchar;
    }

    /**
     * @return Returns the utilCalendar.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UTIL_CALENDAR")
    public Calendar getUtilCalendar() {
        return _utilCalendar;
    }

    /**
     * @param utilCalendar
     *            The utilCalendar to set.
     */
    public void setUtilCalendar(Calendar utilCalendar) {
        this._utilCalendar = utilCalendar;
    }

    /**
     * @return Returns the utilDate.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UTIL_DATE")
    public Date getUtilDate() {
        return _utilDate;
    }

    /**
     * @param utilDate
     *            The utilDate to set.
     */
    public void setUtilDate(Date utilDate) {
        this._utilDate = utilDate;
    }

    /**
     * @return Returns the wrapperBoolean.
     */
    @Basic
    @Column(name = "W_BOOLEAN")
    public Boolean isWrapperBoolean() {
        return _wrapperBoolean;
    }

    /**
     * @param wrapperBoolean
     *            The wrapperBoolean to set.
     */
    public void setWrapperBoolean(Boolean wrapperBoolean) {
        this._wrapperBoolean = wrapperBoolean;
    }

    /**
     * @return Returns the wrapperByte.
     */
    @Basic
    @Column(name = "W_BYTE")
    public Byte getWrapperByte() {
        return _wrapperByte;
    }

    /**
     * @param wrapperByte
     *            The wrapperByte to set.
     */
    public void setWrapperByte(Byte wrapperByte) {
        this._wrapperByte = wrapperByte;
    }

    /**
     * @return Returns the wrapperByteArray2Binary.
     */
    @Basic
    @Column(name = "WBA_BINARY", length = 8)
    @Mutable
    public Byte[] getWrapperByteArray2Binary() {
        return _wrapperByteArray2Binary;
    }

    /**
     * @param wrapperByteArray2Binary
     *            The wrapperByteArray2Binary to set.
     */
    public void setWrapperByteArray2Binary(Byte[] wrapperByteArray2Binary) {
        this._wrapperByteArray2Binary = wrapperByteArray2Binary;
    }

    /**
     * @return Returns the wrapperByteArray2Blob.
     */
    @Basic
    @Column(name = "WBA_BLOB")
    @Lob
    @Mutable
    public Byte[] getWrapperByteArray2Blob() {
        return _wrapperByteArray2Blob;
    }

    /**
     * @param wrapperByteArray2Blob
     *            The wrapperByteArray2Blob to set.
     */
    public void setWrapperByteArray2Blob(Byte[] wrapperByteArray2Blob) {
        this._wrapperByteArray2Blob = wrapperByteArray2Blob;
    }

    /**
     * @return Returns the wrapperByteArray2Longvarbinary.
     */
    @Basic
    @Column(name = "WBA_LONGVARBINARY", length = 1500)
    @Mutable
    public Byte[] getWrapperByteArray2Longvarbinary() {
        return _wrapperByteArray2Longvarbinary;
    }

    /**
     * @param wrapperByteArray2Longvarbinary
     *            The wrapperByteArray2Longvarbinary to set.
     */
    public void setWrapperByteArray2Longvarbinary(Byte[] wrapperByteArray2Longvarbinary) {
        this._wrapperByteArray2Longvarbinary = wrapperByteArray2Longvarbinary;
    }

    /**
     * @return Returns the wrapperCharacter.
     */
    @Basic
    @Column(name = "W_CHARACTER")
    public Character getWrapperCharacter() {
        return _wrapperCharacter;
    }

    /**
     * @param wrapperCharacter
     *            The wrapperCharacter to set.
     */
    public void setWrapperCharacter(Character wrapperCharacter) {
        this._wrapperCharacter = wrapperCharacter;
    }

    /**
     * @return Returns the wrapperCharacterArray2Clob.
     */
    @Basic
    @Column(name = "WCA_CLOB")
    @Lob
    @Mutable
    public Character[] getWrapperCharacterArray2Clob() {
        return _wrapperCharacterArray2Clob;
    }

    /**
     * @param wrapperCharacterArray2Clob
     *            The wrapperCharacterArray2Clob to set.
     */
    public void setWrapperCharacterArray2Clob(Character[] wrapperCharacterArray2Clob) {
        this._wrapperCharacterArray2Clob = wrapperCharacterArray2Clob;
    }

    /**
     * @return Returns the wrapperCharacterArray2Varchar.
     */
    @Basic
    @Column(name = "WCA_VARCHAR")
    @Mutable
    public Character[] getWrapperCharacterArray2Varchar() {
        return _wrapperCharacterArray2Varchar;
    }

    /**
     * @param wrapperCharacterArray2Varchar
     *            The wrapperCharacterArray2Varchar to set.
     */
    public void setWrapperCharacterArray2Varchar(Character[] wrapperCharacterArray2Varchar) {
        this._wrapperCharacterArray2Varchar = wrapperCharacterArray2Varchar;
    }

    /**
     * @return Returns the wrapperDouble.
     */
    @Basic
    @Column(name = "W_DOUBLE")
    public Double getWrapperDouble() {
        return _wrapperDouble;
    }

    /**
     * @param wrapperDouble
     *            The wrapperDouble to set.
     */
    public void setWrapperDouble(Double wrapperDouble) {
        this._wrapperDouble = wrapperDouble;
    }

    /**
     * @return Returns the wrapperFloat.
     */
    @Basic
    @Column(name = "W_FLOAT")
    public Float getWrapperFloat() {
        return _wrapperFloat;
    }

    /**
     * @param wrapperFloat
     *            The wrapperFloat to set.
     */
    public void setWrapperFloat(Float wrapperFloat) {
        this._wrapperFloat = wrapperFloat;
    }

    /**
     * @return Returns the wrapperInteger.
     */
    @Basic
    @Column(name = "W_INTEGER")
    public Integer getWrapperInteger() {
        return _wrapperInteger;
    }

    /**
     * @param wrapperInteger
     *            The wrapperInteger to set.
     */
    public void setWrapperInteger(Integer wrapperInteger) {
        this._wrapperInteger = wrapperInteger;
    }

    /**
     * @return Returns the wrapperLong.
     */
    @Basic
    @Column(name = "W_LONG")
    public Long getWrapperLong() {
        return _wrapperLong;
    }

    /**
     * @param wrapperLong
     *            The wrapperLong to set.
     */
    public void setWrapperLong(Long wrapperLong) {
        this._wrapperLong = wrapperLong;
    }

    /**
     * @return Returns the wrapperShort.
     */
    @Basic
    @Column(name = "W_SMALLINT")
    public Short getWrapperShort() {
        return _wrapperShort;
    }

    /**
     * @param wrapperShort
     *            The wrapperShort to set.
     */
    public void setWrapperShort(Short wrapperShort) {
        this._wrapperShort = wrapperShort;
    }

    public void clearPostUpdate() {
        postUpdateCalled = false;
    }

    @PostUpdate
    public void postUpdate() {
        postUpdateCalled = true;

    }

    public boolean postUpdateWasCalled() {
        return postUpdateCalled;
    }

    @Transient
    public int getNotPersistentByAnnotation() {
        return notPersistentByAnnotation;
    }

    public void setNotPersistentByAnnotation(int notPersistentByAnnotation) {
        this.notPersistentByAnnotation = notPersistentByAnnotation;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ENUM_STRING")
    public UserDefinedEnum getEnumString() {
        return _enumString;
    }

    public void setEnumString(UserDefinedEnum string) {
        _enumString = string;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "ENUM_ORDINAL")
    public UserDefinedEnum getEnumOrdinal() {
        return _enumOrdinal;
    }

    public void setEnumOrdinal(UserDefinedEnum ordinal) {
        _enumOrdinal = ordinal;
    }
}
