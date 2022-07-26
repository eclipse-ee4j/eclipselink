/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
 package org.eclipse.persistence.testing.tests.junit.failover.emulateddriver;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Vector;

import org.eclipse.persistence.sessions.DatabaseRecord;

public class EmulatedResultSet implements ResultSet {
    protected Vector rows;
    protected int index;

    public EmulatedResultSet(Vector rows) {
        this.rows = rows;
        this.index = 0;
    }

    public Vector getRows() {
        return rows;
    }

    @Override
    public boolean next() {
        this.index++;
        return this.index <= this.rows.size();
    }

    @Override
    public void close() {
    }

    @Override
    public boolean wasNull() {
        return false;
    }

    @Override
    public String getString(int columnIndex) {
        return (String) getObject(columnIndex);
    }

    @Override
    public boolean getBoolean(int columnIndex) {
        return (Boolean) getObject(columnIndex);
    }

    @Override
    public byte getByte(int columnIndex) {
        return ((Number) getObject(columnIndex)).byteValue();
    }

    @Override
    public short getShort(int columnIndex) {
        return ((Number) getObject(columnIndex)).shortValue();
    }

    @Override
    public int getInt(int columnIndex) {
        return ((Number) getObject(columnIndex)).intValue();
    }

    @Override
    public long getLong(int columnIndex) {
        Number value = (Number) getObject(columnIndex);
        if (value == null) {
            return 0;
        } else {
            return value.longValue();
        }
    }

    @Override
    public float getFloat(int columnIndex) {
        return ((Number) getObject(columnIndex)).floatValue();
    }

    @Override
    public double getDouble(int columnIndex) {
        return ((Number) getObject(columnIndex)).doubleValue();
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) {
        Number number = (Number) getObject(columnIndex);
        return convertNumber2BigDecimal(number);
    }

    @Override
    public byte[] getBytes(int columnIndex) {
        return (byte[]) getObject(columnIndex);
    }

    @Override
    public java.sql.Date getDate(int columnIndex) {
        return (java.sql.Date) getObject(columnIndex);
    }

    @Override
    public java.sql.Time getTime(int columnIndex) {
        return (java.sql.Time) getObject(columnIndex);
    }

    @Override
    public java.sql.Timestamp getTimestamp(int columnIndex) {
        return (java.sql.Timestamp) getObject(columnIndex);
    }

    @Override
    public java.io.InputStream getAsciiStream(int columnIndex) {
        return (java.io.InputStream) getObject(columnIndex);
    }

    @Override
    public java.io.InputStream getUnicodeStream(int columnIndex) {
        return (java.io.InputStream) getObject(columnIndex);
    }

    @Override
    public java.io.InputStream getBinaryStream(int columnIndex) {
        return (java.io.InputStream) getObject(columnIndex);
    }

    @Override
    public String getString(String columnName) {
        return (String) getObject(columnName);
    }

    @Override
    public boolean getBoolean(String columnName) {
        return (Boolean) getObject(columnName);
    }

    @Override
    public byte getByte(String columnName) {
        return ((Number) getObject(columnName)).byteValue();
    }

    @Override
    public short getShort(String columnName) {
        return ((Number) getObject(columnName)).shortValue();
    }

    @Override
    public int getInt(String columnName) {
        return ((Number) getObject(columnName)).intValue();
    }

    @Override
    public long getLong(String columnName) {
        return ((Number) getObject(columnName)).longValue();
    }

    @Override
    public float getFloat(String columnName) {
        return ((Number) getObject(columnName)).floatValue();
    }

    @Override
    public double getDouble(String columnName) {
        return ((Number) getObject(columnName)).doubleValue();
    }

    @Override
    public BigDecimal getBigDecimal(String columnName, int scale) {
        Number number = (Number) getObject(columnName);
        return convertNumber2BigDecimal(number);
    }

    private BigDecimal convertNumber2BigDecimal(Number number) {
         if (number == null) {
             return null;
         }
         if (number instanceof BigDecimal) {
             return (BigDecimal) number;
         }
         return new BigDecimal(number.longValue());
     }



    @Override
    public byte[] getBytes(String columnName) {
        return (byte[]) getObject(columnName);
    }

    @Override
    public java.sql.Date getDate(String columnName) {
        return (java.sql.Date) getObject(columnName);
    }

    @Override
    public java.sql.Time getTime(String columnName) {
        return (java.sql.Time) getObject(columnName);
    }

    @Override
    public java.sql.Timestamp getTimestamp(String columnName) {
        return (java.sql.Timestamp) getObject(columnName);
    }

    @Override
    public java.io.InputStream getAsciiStream(String columnName) {
        return (java.io.InputStream) getObject(columnName);
    }

    @Override
    public java.io.InputStream getUnicodeStream(String columnName) {
        return (java.io.InputStream) getObject(columnName);
    }

    @Override
    public java.io.InputStream getBinaryStream(String columnName) {
        return (java.io.InputStream) getObject(columnName);
    }

    @Override
    public SQLWarning getWarnings() {
        return null;
    }

    @Override
    public void clearWarnings() {
    }

    @Override
    public String getCursorName() {
        return null;
    }

    @Override
    public ResultSetMetaData getMetaData() {
        return new EmulatedResultSetMetaData(this);
    }

    @Override
    public Object getObject(int columnIndex) {
        return ((DatabaseRecord) this.rows.get(this.index - 1)).getValues().get(columnIndex - 1);
    }

    @Override
    public Object getObject(String columnName) {
        return ((DatabaseRecord) this.rows.get(this.index - 1)).get(columnName);
    }

    // ----------------------------------------------------------------

    @Override
    public int findColumn(String columnName) {
        return 0;
    }

    // --------------------------JDBC 2.0-----------------------------------
    // ---------------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------------

    @Override
    public java.io.Reader getCharacterStream(int columnIndex) {
        return (java.io.Reader) getObject(columnIndex);
    }

    @Override
    public java.io.Reader getCharacterStream(String columnName) {
        return (java.io.Reader) getObject(columnName);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) {
        Number number = (Number)  getObject(columnIndex);
         return convertNumber2BigDecimal(number);
    }

    @Override
    public BigDecimal getBigDecimal(String columnName) {
        Number number = (Number)  getObject(columnName);
        return convertNumber2BigDecimal(number);
    }

    // ---------------------------------------------------------------------
    // Traversal/Positioning
    // ---------------------------------------------------------------------

    @Override
    public boolean isBeforeFirst() {
        return this.index == 0;
    }

    @Override
    public boolean isAfterLast() {
        return (this.index - 1) == this.rows.size();
    }

    @Override
    public boolean isFirst() {
        return this.index == 1;
    }

    @Override
    public boolean isLast() {
        return this.index == this.rows.size();
    }

    @Override
    public void beforeFirst() {
        this.index = 0;
    }

    @Override
    public void afterLast() {
        this.index = this.rows.size() + 1;
    }

    @Override
    public boolean first() {
        this.index = 1;
        return true;
    }

    @Override
    public boolean last() {
        this.index = this.rows.size();
        return true;
    }

    @Override
    public int getRow() {
        return index;
    }

    @Override
    public boolean absolute(int row) {
        this.index = row;
        return true;
    }

    @Override
    public boolean relative(int rows) {
        this.index = index + rows;
        return true;
    }

    @Override
    public boolean previous() {
        this.index--;
        return true;
    }

    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    @Override
    public void setFetchDirection(int direction) {
    }

    @Override
    public int getFetchDirection() {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) {
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getConcurrency() {
        return 0;
    }

    // ---------------------------------------------------------------------
    // Updates
    // ---------------------------------------------------------------------

    @Override
    public boolean rowUpdated() {
        return false;
    }

    @Override
    public boolean rowInserted() {
        return false;
    }

    @Override
    public boolean rowDeleted() {
        return false;
    }

    @Override
    public void updateNull(int columnIndex) {
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) {
    }

    @Override
    public void updateByte(int columnIndex, byte x) {
    }

    @Override
    public void updateShort(int columnIndex, short x) {
    }

    @Override
    public void updateInt(int columnIndex, int x) {
    }

    @Override
    public void updateLong(int columnIndex, long x) {
    }

    @Override
    public void updateFloat(int columnIndex, float x) {
    }

    @Override
    public void updateDouble(int columnIndex, double x) {
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) {
    }

    @Override
    public void updateString(int columnIndex, String x) {
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) {
    }

    @Override
    public void updateDate(int columnIndex, java.sql.Date x) {
    }

    @Override
    public void updateTime(int columnIndex, java.sql.Time x) {
    }

    @Override
    public void updateTimestamp(int columnIndex, java.sql.Timestamp x) {
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x,
                                  int length) {
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x,
                                   int length) {
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x,
                                      int length) {
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scale) {
    }

    @Override
    public void updateObject(int columnIndex, Object x) {
    }

    @Override
    public void updateNull(String columnName) {
    }

    @Override
    public void updateBoolean(String columnName, boolean x) {
    }

    @Override
    public void updateByte(String columnName, byte x) {
    }

    @Override
    public void updateShort(String columnName, short x) {
    }

    @Override
    public void updateInt(String columnName, int x) {
    }

    @Override
    public void updateLong(String columnName, long x) {
    }

    @Override
    public void updateFloat(String columnName, float x) {
    }

    @Override
    public void updateDouble(String columnName, double x) {
    }

    @Override
    public void updateBigDecimal(String columnName, BigDecimal x) {
    }

    @Override
    public void updateString(String columnName, String x) {
    }

    @Override
    public void updateBytes(String columnName, byte[] x) {
    }

    @Override
    public void updateDate(String columnName, java.sql.Date x) {
    }

    @Override
    public void updateTime(String columnName, java.sql.Time x) {
    }

    @Override
    public void updateTimestamp(String columnName, java.sql.Timestamp x) {
    }

    @Override
    public void updateAsciiStream(String columnName, java.io.InputStream x,
                                  int length) {
    }

    @Override
    public void updateBinaryStream(String columnName, java.io.InputStream x,
                                   int length) {
    }

    @Override
    public void updateCharacterStream(String columnName, java.io.Reader reader,
                                      int length) {
    }

    @Override
    public void updateObject(String columnName, Object x, int scale) {
    }

    @Override
    public void updateObject(String columnName, Object x) {
    }

    @Override
    public void insertRow() {
    }

    @Override
    public void updateRow() {
    }

    @Override
    public void deleteRow() {
    }

    @Override
    public void refreshRow() {
    }

    @Override
    public void cancelRowUpdates() {
    }

    @Override
    public void moveToInsertRow() {
    }

    @Override
    public void moveToCurrentRow() {
    }

    @Override
    public Statement getStatement() {
        return null;
    }

    @Override
    public Object getObject(int i, java.util.Map map) {
        return getObject(i);
    }

    @Override
    public Ref getRef(int i) {
        return null;
    }

    @Override
    public Blob getBlob(int i) {
        return (Blob) getObject(i);
    }

    @Override
    public Clob getClob(int i) {
        return (Clob) getObject(i);
    }

    @Override
    public Array getArray(int i) {
        return null;
    }

    @Override
    public Object getObject(String colName, java.util.Map map) {
        return getObject(colName);
    }

    @Override
    public Ref getRef(String colName) {
        return null;
    }

    @Override
    public Blob getBlob(String colName) {
        return (Blob) getObject(colName);
    }

    @Override
    public Clob getClob(String colName) {
        return (Clob) getObject(colName);
    }

    @Override
    public Array getArray(String colName) {
        return null;
    }

    @Override
    public java.sql.Date getDate(int columnIndex, Calendar cal) {
        return null;
    }

    @Override
    public java.sql.Date getDate(String columnName, Calendar cal) {
        return null;
    }

    @Override
    public java.sql.Time getTime(int columnIndex, Calendar cal) {
        return null;
    }

    @Override
    public java.sql.Time getTime(String columnName, Calendar cal) {
        return null;
    }

    @Override
    public java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal) {
        return null;
    }

    @Override
    public java.sql.Timestamp getTimestamp(String columnName, Calendar cal) {
        return null;
    }

    // -------------------------- JDBC 3.0
    // ----------------------------------------

    @Override
    public java.net.URL getURL(int columnIndex) {
        return null;
    }

    @Override
    public java.net.URL getURL(String columnName) {
        return null;
    }

    @Override
    public void updateRef(int columnIndex, java.sql.Ref x) {
    }

    @Override
    public void updateRef(String columnName, java.sql.Ref x) {
    }

    @Override
    public void updateBlob(int columnIndex, java.sql.Blob x) {
    }

    @Override
    public void updateBlob(String columnName, java.sql.Blob x) {
    }

    @Override
    public void updateClob(int columnIndex, java.sql.Clob x) {
    }

    @Override
    public void updateClob(String columnName, java.sql.Clob x) {
    }

    @Override
    public void updateArray(int columnIndex, java.sql.Array x) {
    }

    @Override
    public void updateArray(String columnName, java.sql.Array x) {
    }

    // 236070: Methods introduced in JDK 1.6
    @Override
    public int getHoldability()  throws SQLException {
        return 0;
    }

    @Override
    public Reader getNCharacterStream(int columnIndex)  throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel)  throws SQLException {
        return null;
    }

    @Override
    public NClob getNClob(int columnIndex)  throws SQLException {
        return null;
    }

   @Override
   public NClob getNClob(String columnLabel)  throws SQLException {
        return null;
    }

   @Override
   public String getNString(int columnIndex)  throws SQLException {
       return null;
   }

   @Override
   public String getNString(String columnLabel)  throws SQLException {
       return null;
   }

   @Override
   public RowId getRowId(int columnIndex)  throws SQLException {
       return null;
   }

   @Override
   public RowId getRowId(String columnLabel)  throws SQLException {
       return null;
   }

   @Override
   public SQLXML getSQLXML(int columnIndex)  throws SQLException {
       return null;
   }

   @Override
   public SQLXML getSQLXML(String columnLabel)  throws SQLException {
       return null;
   }

   @Override
   public boolean isClosed()  throws SQLException {
       return false;
   }

   @Override
   public void updateAsciiStream(int columnIndex, InputStream stream, long length)  throws SQLException {
   }

   @Override
   public void updateAsciiStream(int columnIndex, InputStream stream)  throws SQLException {
   }

   @Override
   public void updateAsciiStream(String columnLabel, InputStream stream, long length)  throws SQLException {
   }

   @Override
   public void updateAsciiStream(String columnLabel, InputStream stream)  throws SQLException {
   }

   @Override
   public void updateBlob(int columnIndex, InputStream stream, long length)  throws SQLException {
   }

   @Override
   public void updateBlob(int columnIndex, InputStream stream)  throws SQLException {
   }

   @Override
   public void updateBlob(String columnLabel, InputStream stream, long length)  throws SQLException {
   }

   @Override
   public void updateBlob(String columnLabel, InputStream stream)  throws SQLException {
   }

   @Override
   public void updateBinaryStream(int columnIndex, InputStream stream, long length)  throws SQLException {
   }

   @Override
   public void updateBinaryStream(int columnIndex, InputStream stream)  throws SQLException {
   }

   @Override
   public void updateBinaryStream(String columnLabel, InputStream stream, long length)  throws SQLException {
   }

   @Override
   public void updateBinaryStream(String columnLabel, InputStream stream)  throws SQLException {
   }

   @Override
   public void updateCharacterStream(int columnIndex, Reader reader, long length)  throws SQLException {
   }

   @Override
   public void updateCharacterStream(int columnIndex, Reader reader)  throws SQLException {
   }

   @Override
   public void updateCharacterStream(String columnLabel, Reader reader, long length)  throws SQLException {
   }

   @Override
   public void updateCharacterStream(String columnLabel, Reader reader)  throws SQLException {
   }

   @Override
   public void updateClob(int columnIndex, Reader reader, long length)  throws SQLException {
   }

   @Override
   public void updateClob(int columnIndex, Reader reader)  throws SQLException {
   }

   @Override
   public void updateClob(String columnLabel, Reader reader, long length)  throws SQLException {
   }

   @Override
   public void updateClob(String columnLabel, Reader reader)  throws SQLException {
   }

   @Override
   public void updateNCharacterStream(int columnIndex, Reader reader, long length)  throws SQLException {
   }

   @Override
   public void updateNCharacterStream(int columnIndex, Reader reader)  throws SQLException {
   }

   @Override
   public void updateNCharacterStream(String columnLabel, Reader reader, long length)  throws SQLException {
   }

   @Override
   public void updateNCharacterStream(String columnLabel, Reader reader)  throws SQLException {
   }

   @Override
   public void updateNClob(int columnIndex, Reader reader, long length)  throws SQLException {
   }

   @Override
   public void updateNClob(int columnIndex, Reader reader)  throws SQLException {
   }

   @Override
   public void updateNClob(String columnLabel, Reader reader, long length)  throws SQLException {
   }

   @Override
   public void updateNClob(String columnLabel, Reader reader)  throws SQLException {
   }

   @Override
   public void updateNClob(int columnIndex, NClob nclob)  throws SQLException {
   }

   @Override
   public void updateNClob(String columnLabel, NClob nclob)  throws SQLException {
   }

   @Override
   public void updateNString(int columnIndex, String nString)  throws SQLException {
   }

   @Override
   public void updateNString(String columnLabel, String nString)  throws SQLException {
   }

   @Override
   public void updateSQLXML(String columnLabel, SQLXML sqlxml)  throws SQLException {
   }

   @Override
   public void updateSQLXML(int columnIndex, SQLXML sqlxml)  throws SQLException {
   }

   @Override
   public void updateRowId(int columnIndex, RowId rowid)  throws SQLException {
   }

   @Override
   public void updateRowId(String columnLabel, RowId rowid)  throws SQLException {
   }

   @Override
   public boolean isWrapperFor(Class<?> iFace) throws SQLException{
        return false;
    }

   @Override
   public <T>T unwrap(Class<T> iFace)  throws SQLException {
        return iFace.cast(this);
    }

   @Override
   public <T> T getObject(String columnLabel, Class<T> type){return null;}

   @Override
   public <T> T getObject(int columnIndex, Class<T> type){return null;}
}
