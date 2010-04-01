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
import javax.persistence.Version;

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
@Table(name = "TMP_BASIC_TYPES_FA")
public class BasicTypesFieldAccess {

    @Transient
    private boolean postUpdateCalled;

    public BasicTypesFieldAccess() {
    }

    public BasicTypesFieldAccess(int anId) {
        id = anId;
    }

    @Version
    protected long version;

    @Id
    @Column(name = "ID")
    protected int id;

    // Java primitive types
    @Basic
    @Column(name = "P_BOOLEAN")
    protected boolean primitiveBoolean; // TODO ?

    @Basic
    @Column(name = "P_BYTE")
    protected byte primititveByte; // SMALLINT

    @Basic
    @Column(name = "P_CHAR")
    protected char primitiveChar = '0'; // INTEGER

    @Basic
    @Column(name = "P_SHORT")
    protected short primitiveShort; // SMALLINT

    @Basic
    @Column(name = "P_INT")
    protected int primitiveInt; // INTEGER

    @Basic
    @Column(name = "P_LONG")
    protected long primitiveLong; // BIGINT

    @Basic
    @Column(name = "P_FLOAT")
    protected float primitiveFloat; // REAL

    @Basic
    @Column(name = "P_DOUBLE")
    protected double primitiveDouble; // DOUBLE

    // wrappers of primitive types
    @Basic
    @Column(name = "W_BOOLEAN")
    protected Boolean wrapperBoolean; // TODO ?

    @Basic
    @Column(name = "W_BYTE")
    protected Byte wrapperByte; // SMALLINT

    @Basic
    @Column(name = "W_CHARACTER")
    protected Character wrapperCharacter; // INTEGER

    @Basic
    @Column(name = "W_SMALLINT")
    protected Short wrapperShort; // SMALLINT

    @Basic
    @Column(name = "W_INTEGER")
    protected Integer wrapperInteger; // INTEGER

    @Basic
    @Column(name = "W_LONG")
    protected Long wrapperLong; // BIGINT

    @Basic
    @Column(name = "W_FLOAT")
    protected Float wrapperFloat; // REAL

    @Basic
    @Column(name = "W_DOUBLE")
    protected Double wrapperDouble; // DOUBLE

    // immutable types
    @Basic
    @Column(name = "STRING_VC", length = 128)
    protected String string2Varchar; // VARCHAR

    @Basic
    @Column(name = "STRING_CLOB")
    @Lob
    protected String string2Clob; // CLOB

    @Basic
    @Column(name = "BIG_DECIMAL", precision = 12, scale = 2)
    protected BigDecimal bigDecimal; // DECIMAL

    @Basic
    @Column(name = "BIG_INTEGER", precision = 12)
    protected BigInteger bigInteger; // DECIMAL

    // mutable types
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UTIL_DATE")
    protected Date utilDate; // DATE

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UTIL_CALENDAR")
    protected Calendar utilCalendar; // TIMESTAMP

    @Basic
    @Column(name = "SQL_DATE")
    protected java.sql.Date sqlDate; // DATE

    @Basic
    @Column(name = "SQL_TIME")
    protected Time sqlTime; // TIME

    @Basic
    @Column(name = "SQL_TIMESTAMP")
    protected Timestamp sqlTimestamp; // TIMESTAMP

    // arrays
    @Basic
    @Column(name = "PBA_BINARY", length = 8)
    @Mutable
    protected byte[] primitiveByteArray2Binary; // BINARY

    @Basic
    @Column(name = "PBA_LONGVARBINARY", length = 1500)
    @Mutable
    protected byte[] primitiveByteArray2Longvarbinary; // LONGVARBINARY

    @Basic
    @Column(name = "PBA_BLOB", length = 65535) // FIXME: remove length after bugzilla 307774 is fixed
    @Lob
    @Mutable
    protected byte[] primitiveByteArray2Blob; // BLOB

    @Basic
    @Column(name = "WBA_BINARY", length = 8)
    @Mutable
    protected Byte[] wrapperByteArray2Binary; // BINARY

    @Basic
    @Column(name = "WBA_LONGVARBINARY", length = 1500)
    @Mutable
    protected Byte[] wrapperByteArray2Longvarbinary; // LONGVARBINARY

    @Basic
    @Column(name = "WBA_BLOB", length = 65535) // FIXME: remove length after bugzilla 307774 is fixed
    @Lob
    @Mutable
    protected Byte[] wrapperByteArray2Blob; // BLOB

    @Basic
    @Column(name = "PCA_VARCHAR")
    @Mutable
    protected char[] primitiveCharArray2Varchar; // VARCHAR

    @Basic
    @Column(name = "PCA_CLOB")
    @Lob
    @Mutable
    protected char[] primitiveCharArray2Clob; // CLOB

    @Basic
    @Column(name = "WCA_VARCHAR")
    @Mutable
    protected Character[] wrapperCharacterArray2Varchar; // VARCHAR

    @Basic
    @Column(name = "WCA_CLOB")
    @Lob
    @Mutable
    protected Character[] wrapperCharacterArray2Clob; // CLOB

    // user-defined serializable
    @Basic
    @Column(name = "SERIALIZABLE", length = 65535) // FIXME: remove length after bugzilla 307774 is fixed
    @Lob
    protected Serializable serializable; // BLOB

    // enums
    protected @Enumerated(EnumType.STRING)
    @Column(name = "ENUM_STRING")
    UserDefinedEnum enumString;

    protected @Enumerated(EnumType.ORDINAL)
    @Column(name = "ENUM_ORDINAL")
    UserDefinedEnum enumOrdinal;

    protected transient String notPersistentByModifier;

    @Transient
    protected int notPersistentByAnnotation;

    public void fill() {
        primitiveBoolean = true;
        primititveByte = 1;
        primitiveChar = 'A';
        primitiveShort = 2;
        primitiveInt = 3;
        primitiveLong = 4;
        primitiveFloat = 1.5f;
        primitiveDouble = 2.5;
        wrapperBoolean = Boolean.TRUE;
        wrapperByte = new Byte((byte) 1);
        wrapperCharacter = new Character('A');
        wrapperShort = new Short((short) 2);
        wrapperInteger = new Integer(3);
        wrapperLong = new Long(4);
        wrapperFloat = new Float(1.5f);
        wrapperDouble = new Double(2.5);
        string2Varchar = "VARCHAR";
        string2Clob = "CLOB";
        bigDecimal = new BigDecimal("42.42");
        bigInteger = new BigInteger("77");
        utilDate = new Date(17000);
        utilCalendar = new GregorianCalendar(2005, 9, 8, 10, 49);
        sqlDate = java.sql.Date.valueOf("2005-09-08");
        sqlTime = Time.valueOf("10:49:00");
        sqlTimestamp = new Timestamp(17000);
        primitiveByteArray2Binary = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };

        primitiveByteArray2Longvarbinary = new byte[1111];
        for (int i = 0; i < primitiveByteArray2Longvarbinary.length; i++) {
            primitiveByteArray2Longvarbinary[i] = (byte) i;
        }
        primitiveByteArray2Blob = new byte[3333];
        for (int i = 0; i < primitiveByteArray2Blob.length; i++) {
            primitiveByteArray2Blob[i] = (byte) i;
        }

        wrapperByteArray2Binary = new Byte[] { new Byte((byte) 0), new Byte((byte) 1), new Byte((byte) 2), new Byte((byte) 3),
                new Byte((byte) 4), new Byte((byte) 5), new Byte((byte) 6), new Byte((byte) 7) };

        wrapperByteArray2Longvarbinary = new Byte[1111];
        for (int i = 0; i < wrapperByteArray2Longvarbinary.length; i++) {
            wrapperByteArray2Longvarbinary[i] = new Byte((byte) i);
        }

        wrapperByteArray2Blob = new Byte[3333];
        for (int i = 0; i < wrapperByteArray2Blob.length; i++) {
            wrapperByteArray2Blob[i] = new Byte((byte) i);
        }

        primitiveCharArray2Varchar = new char[] { 'V', 'A', 'R', 'C', 'A', 'R' };
        primitiveCharArray2Clob = new char[] { 'C', 'L', 'O', 'B' };

        wrapperCharacterArray2Varchar = new Character[] { new Character('V'), new Character('A'), new Character('R'),
                new Character('C'), new Character('H'), new Character('A'), new Character('R') };
        wrapperCharacterArray2Clob = new Character[] { new Character('C'), new Character('L'), new Character('O'),
                new Character('B') };
        serializable = new UserDefinedSerializable("REGEN"); // BLOB

        setEnumOrdinal(UserDefinedEnum.EMIL);
        setEnumString(UserDefinedEnum.HUGO);

    }

    /**
     * @return Returns the bigDecimal.
     */
    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    /**
     * @param bigDecimal
     *            The bigDecimal to set.
     */
    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    /**
     * @return Returns the bigInteger.
     */
    public BigInteger getBigInteger() {
        return bigInteger;
    }

    /**
     * @param bigInteger
     *            The bigInteger to set.
     */
    public void setBigInteger(BigInteger bigInteger) {
        this.bigInteger = bigInteger;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Returns the primititveByte.
     */
    public byte getPrimititveByte() {
        return primititveByte;
    }

    /**
     * @param primititveByte
     *            The primititveByte to set.
     */
    public void setPrimititveByte(byte primititveByte) {
        this.primititveByte = primititveByte;
    }

    /**
     * @return Returns the primitiveBoolean.
     */
    public boolean isPrimitiveBoolean() {
        return primitiveBoolean;
    }

    /**
     * @param primitiveBoolean
     *            The primitiveBoolean to set.
     */
    public void setPrimitiveBoolean(boolean primitiveBoolean) {
        this.primitiveBoolean = primitiveBoolean;
    }

    /**
     * @return Returns the primitiveByteArray2Binary.
     */
    public byte[] getPrimitiveByteArray2Binary() {
        return primitiveByteArray2Binary;
    }

    /**
     * @param primitiveByteArray2Binary
     *            The primitiveByteArray2Binary to set.
     */
    public void setPrimitiveByteArray2Binary(byte[] primitiveByteArray2Binary) {
        this.primitiveByteArray2Binary = primitiveByteArray2Binary;
    }

    /**
     * @return Returns the primitiveByteArray2Blob.
     */
    public byte[] getPrimitiveByteArray2Blob() {
        return primitiveByteArray2Blob;
    }

    /**
     * @param primitiveByteArray2Blob
     *            The primitiveByteArray2Blob to set.
     */
    public void setPrimitiveByteArray2Blob(byte[] primitiveByteArray2Blob) {
        this.primitiveByteArray2Blob = primitiveByteArray2Blob;
    }

    /**
     * @return Returns the primitiveByteArray2Longvarbinary.
     */
    public byte[] getPrimitiveByteArray2Longvarbinary() {
        return primitiveByteArray2Longvarbinary;
    }

    /**
     * @param primitiveByteArray2Longvarbinary
     *            The primitiveByteArray2Longvarbinary to set.
     */
    public void setPrimitiveByteArray2Longvarbinary(byte[] primitiveByteArray2Longvarbinary) {
        this.primitiveByteArray2Longvarbinary = primitiveByteArray2Longvarbinary;
    }

    /**
     * @return Returns the primitiveChar.
     */
    public char getPrimitiveChar() {
        return primitiveChar;
    }

    /**
     * @param primitiveChar
     *            The primitiveChar to set.
     */
    public void setPrimitiveChar(char primitiveChar) {
        this.primitiveChar = primitiveChar;
    }

    /**
     * @return Returns the primitiveCharArray2Clob.
     */
    public char[] getPrimitiveCharArray2Clob() {
        return primitiveCharArray2Clob;
    }

    /**
     * @param primitiveCharArray2Clob
     *            The primitiveCharArray2Clob to set.
     */
    public void setPrimitiveCharArray2Clob(char[] primitiveCharArray2Clob) {
        this.primitiveCharArray2Clob = primitiveCharArray2Clob;
    }

    /**
     * @return Returns the primitiveCharArray2Varchar.
     */
    public char[] getPrimitiveCharArray2Varchar() {
        return primitiveCharArray2Varchar;
    }

    /**
     * @param primitiveCharArray2Varchar
     *            The primitiveCharArray2Varchar to set.
     */
    public void setPrimitiveCharArray2Varchar(char[] primitiveCharArray2Varchar) {
        this.primitiveCharArray2Varchar = primitiveCharArray2Varchar;
    }

    /**
     * @return Returns the primitiveFloat.
     */
    public float getPrimitiveFloat() {
        return primitiveFloat;
    }

    /**
     * @param primitiveFloat
     *            The primitiveFloat to set.
     */
    public void setPrimitiveFloat(float primitiveFloat) {
        this.primitiveFloat = primitiveFloat;
    }

    /**
     * @return Returns the primitiveInt.
     */
    public int getPrimitiveInt() {
        return primitiveInt;
    }

    /**
     * @param primitiveInt
     *            The primitiveInt to set.
     */
    public void setPrimitiveInt(int primitiveInt) {
        this.primitiveInt = primitiveInt;
    }

    /**
     * @return Returns the primitiveLong.
     */
    public long getPrimitiveLong() {
        return primitiveLong;
    }

    /**
     * @param primitiveLong
     *            The primitiveLong to set.
     */
    public void setPrimitiveLong(long primitiveLong) {
        this.primitiveLong = primitiveLong;
    }

    /**
     * @return Returns the primitiveShort.
     */
    public short getPrimitiveShort() {
        return primitiveShort;
    }

    /**
     * @param primitiveShort
     *            The primitiveShort to set.
     */
    public void setPrimitiveShort(short primitiveShort) {
        this.primitiveShort = primitiveShort;
    }

    /**
     * @return Returns the prmitiveDouble.
     */
    public double getPrimitiveDouble() {
        return primitiveDouble;
    }

    /**
     * @param prmitiveDouble
     *            The prmitiveDouble to set.
     */
    public void setPrimitiveDouble(double prmitiveDouble) {
        this.primitiveDouble = prmitiveDouble;
    }

    /**
     * @return Returns the serializable.
     */
    public Serializable getSerializable() {
        return serializable;
    }

    /**
     * @param serializable
     *            The serializable to set.
     */
    public void setSerializable(Serializable serializable) {
        this.serializable = serializable;
    }

    /**
     * @return Returns the sqlDate.
     */
    public java.sql.Date getSqlDate() {
        return sqlDate;
    }

    /**
     * @param sqlDate
     *            The sqlDate to set.
     */
    public void setSqlDate(java.sql.Date sqlDate) {
        this.sqlDate = sqlDate;
    }

    /**
     * @return Returns the sqlTime.
     */
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
    public Timestamp getSqlTimestamp() {
        return sqlTimestamp;
    }

    /**
     * @param sqlTimestamp
     *            The sqlTimestamp to set.
     */
    public void setSqlTimestamp(Timestamp sqlTimestamp) {
        this.sqlTimestamp = sqlTimestamp;
    }

    /**
     * @return Returns the string2Clob.
     */
    public String getString2Clob() {
        return string2Clob;
    }

    /**
     * @param string2Clob
     *            The string2Clob to set.
     */
    public void setString2Clob(String string2Clob) {
        this.string2Clob = string2Clob;
    }

    /**
     * @return Returns the string2Varchar.
     */
    public String getString2Varchar() {
        return string2Varchar;
    }

    /**
     * @param string2Varchar
     *            The string2Varchar to set.
     */
    public void setString2Varchar(String string2Varchar) {
        this.string2Varchar = string2Varchar;
    }

    /**
     * @return Returns the utilCalendar.
     */
    public Calendar getUtilCalendar() {
        return utilCalendar;
    }

    /**
     * @param utilCalendar
     *            The utilCalendar to set.
     */
    public void setUtilCalendar(Calendar utilCalendar) {
        this.utilCalendar = utilCalendar;
    }

    /**
     * @return Returns the utilDate.
     */
    public Date getUtilDate() {
        return utilDate;
    }

    /**
     * @param utilDate
     *            The utilDate to set.
     */
    public void setUtilDate(Date utilDate) {
        this.utilDate = utilDate;
    }

    /**
     * @return Returns the wrapperBoolean.
     */
    public Boolean getWrapperBoolean() {
        return wrapperBoolean;
    }

    /**
     * @param wrapperBoolean
     *            The wrapperBoolean to set.
     */
    public void setWrapperBoolean(Boolean wrapperBoolean) {
        this.wrapperBoolean = wrapperBoolean;
    }

    /**
     * @return Returns the wrapperByte.
     */
    public Byte getWrapperByte() {
        return wrapperByte;
    }

    /**
     * @param wrapperByte
     *            The wrapperByte to set.
     */
    public void setWrapperByte(Byte wrapperByte) {
        this.wrapperByte = wrapperByte;
    }

    /**
     * @return Returns the wrapperByteArray2Binary.
     */
    public Byte[] getWrapperByteArray2Binary() {
        return wrapperByteArray2Binary;
    }

    /**
     * @param wrapperByteArray2Binary
     *            The wrapperByteArray2Binary to set.
     */
    public void setWrapperByteArray2Binary(Byte[] wrapperByteArray2Binary) {
        this.wrapperByteArray2Binary = wrapperByteArray2Binary;
    }

    /**
     * @return Returns the wrapperByteArray2Blob.
     */
    public Byte[] getWrapperByteArray2Blob() {
        return wrapperByteArray2Blob;
    }

    /**
     * @param wrapperByteArray2Blob
     *            The wrapperByteArray2Blob to set.
     */
    public void setWrapperByteArray2Blob(Byte[] wrapperByteArray2Blob) {
        this.wrapperByteArray2Blob = wrapperByteArray2Blob;
    }

    /**
     * @return Returns the wrapperByteArray2Longvarbinary.
     */
    public Byte[] getWrapperByteArray2Longvarbinary() {
        return wrapperByteArray2Longvarbinary;
    }

    /**
     * @param wrapperByteArray2Longvarbinary
     *            The wrapperByteArray2Longvarbinary to set.
     */
    public void setWrapperByteArray2Longvarbinary(Byte[] wrapperByteArray2Longvarbinary) {
        this.wrapperByteArray2Longvarbinary = wrapperByteArray2Longvarbinary;
    }

    /**
     * @return Returns the wrapperCharacter.
     */
    public Character getWrapperCharacter() {
        return wrapperCharacter;
    }

    /**
     * @param wrapperCharacter
     *            The wrapperCharacter to set.
     */
    public void setWrapperCharacter(Character wrapperCharacter) {
        this.wrapperCharacter = wrapperCharacter;
    }

    /**
     * @return Returns the wrapperCharacterArray2Clob.
     */
    public Character[] getWrapperCharacterArray2Clob() {
        return wrapperCharacterArray2Clob;
    }

    /**
     * @param wrapperCharacterArray2Clob
     *            The wrapperCharacterArray2Clob to set.
     */
    public void setWrapperCharacterArray2Clob(Character[] wrapperCharacterArray2Clob) {
        this.wrapperCharacterArray2Clob = wrapperCharacterArray2Clob;
    }

    /**
     * @return Returns the wrapperCharacterArray2Varchar.
     */
    public Character[] getWrapperCharacterArray2Varchar() {
        return wrapperCharacterArray2Varchar;
    }

    /**
     * @param wrapperCharacterArray2Varchar
     *            The wrapperCharacterArray2Varchar to set.
     */
    public void setWrapperCharacterArray2Varchar(Character[] wrapperCharacterArray2Varchar) {
        this.wrapperCharacterArray2Varchar = wrapperCharacterArray2Varchar;
    }

    /**
     * @return Returns the wrapperDouble.
     */
    public Double getWrapperDouble() {
        return wrapperDouble;
    }

    /**
     * @param wrapperDouble
     *            The wrapperDouble to set.
     */
    public void setWrapperDouble(Double wrapperDouble) {
        this.wrapperDouble = wrapperDouble;
    }

    /**
     * @return Returns the wrapperFloat.
     */
    public Float getWrapperFloat() {
        return wrapperFloat;
    }

    /**
     * @param wrapperFloat
     *            The wrapperFloat to set.
     */
    public void setWrapperFloat(Float wrapperFloat) {
        this.wrapperFloat = wrapperFloat;
    }

    /**
     * @return Returns the wrapperInteger.
     */
    public Integer getWrapperInteger() {
        return wrapperInteger;
    }

    /**
     * @param wrapperInteger
     *            The wrapperInteger to set.
     */
    public void setWrapperInteger(Integer wrapperInteger) {
        this.wrapperInteger = wrapperInteger;
    }

    /**
     * @return Returns the wrapperLong.
     */
    public Long getWrapperLong() {
        return wrapperLong;
    }

    /**
     * @param wrapperLong
     *            The wrapperLong to set.
     */
    public void setWrapperLong(Long wrapperLong) {
        this.wrapperLong = wrapperLong;
    }

    /**
     * @return Returns the wrapperShort.
     */
    public Short getWrapperShort() {
        return wrapperShort;
    }

    /**
     * @param wrapperShort
     *            The wrapperShort to set.
     */
    public void setWrapperShort(Short wrapperShort) {
        this.wrapperShort = wrapperShort;
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

    public void setEnumString(UserDefinedEnum enumString) {
        this.enumString = enumString;
    }

    public UserDefinedEnum getEnumString() {
        return enumString;
    }

    public void setEnumOrdinal(UserDefinedEnum enumOrdinal) {
        this.enumOrdinal = enumOrdinal;
    }

    public UserDefinedEnum getEnumOrdinal() {
        return enumOrdinal;
    }

    public synchronized long getVersion() {
        return version;
    }
}
